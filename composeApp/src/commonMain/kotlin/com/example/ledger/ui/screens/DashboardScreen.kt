@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ledger.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ledger.data.Transaction
import com.example.ledger.data.isPaymentDueCheck
import com.example.ledger.ui.components.AnimatedBalanceCard
import com.example.ledger.ui.components.ExpressiveMotion
import com.example.ledger.ui.components.TransactionListItem
import com.example.ledger.ui.theme.LedgerTheme

@Composable
fun DashboardScreen(
    transactions: List<Transaction>,
    recurringTemplates: List<Transaction>,
    onAddSingle: () -> Unit,
    onAddRecurring: () -> Unit,
    onViewRecurring: () -> Unit,
    onViewAllTransactions: () -> Unit,
    onEditTransaction: (Transaction) -> Unit,
    onSettings: () -> Unit,
    onDeleteTransaction: (Transaction) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }
    val totalBalance by remember {
        derivedStateOf {
            transactions.sumOf { tx ->
                when {
                    tx.transferToAccount != null -> 0.0 // Transfers don't change total balance
                    tx.isIncome -> tx.amount
                    else -> -tx.amount
                }
            }
        }
    }
    
    val pocketBalance by remember {
        derivedStateOf {
            transactions.sumOf { tx ->
                when {
                    // Transfer TO pocket
                    tx.transferToAccount == com.example.ledger.data.TransactionAccount.POCKET -> tx.amount
                    // Transfer FROM pocket
                    tx.account == com.example.ledger.data.TransactionAccount.POCKET && tx.transferToAccount != null -> -tx.amount
                    // Regular pocket income
                    tx.account == com.example.ledger.data.TransactionAccount.POCKET && tx.isIncome -> tx.amount
                    // Regular pocket expense
                    tx.account == com.example.ledger.data.TransactionAccount.POCKET && !tx.isIncome -> -tx.amount
                    else -> 0.0
                }
            }
        }
    }

    
    val activeCount by remember {
        derivedStateOf {
            recurringTemplates.count { !isPaymentDueCheck(it.nextPaymentDate ?: it.date) }
        }
    }
    val dueCount by remember {
        derivedStateOf {
            recurringTemplates.count { isPaymentDueCheck(it.nextPaymentDate ?: it.date) }
        }
    }
    
    // Delete confirmation dialog
    transactionToDelete?.let { tx ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Delete Transaction") },
            text = { 
                Text("Are you sure you want to delete \"${tx.title}\"? This action cannot be undone.") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTransaction(tx)
                        transactionToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            DashboardHeader(onSettings = onSettings)
        },
        floatingActionButton = {
            DashboardFAB(onClick = onAddSingle)
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Balance Card
            item(key = "balance", contentType = "balance") {
                AnimatedBalanceCard(
                    totalBalance = totalBalance,
                    pocketBalance = pocketBalance
                )
            }
            
            // Quick Actions
            item(key = "actions", contentType = "actions") {
                QuickActionsGrid(
                    onAddTransaction = onAddSingle,
                    onViewRecurring = onViewRecurring,
                    activeRecurring = activeCount,
                    dueRecurring = dueCount,
                    totalTransactions = transactions.size
                )
            }
            
            // Recent Transactions Header
            item(key = "header", contentType = "header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    if (transactions.size > 5) {
                        TextButton(onClick = onViewAllTransactions) {
                            Text("See All", style = MaterialTheme.typography.labelLarge)
                        }
                    } else if (transactions.isNotEmpty()) {
                        Text(
                            text = "${transactions.size} total",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Transactions List
            if (transactions.isEmpty()) {
                item(key = "empty", contentType = "empty") {
                    EmptyStateCard(
                        message = "No transactions yet",
                        subtitle = "Tap the + button to add your first transaction"
                    )
                }
            } else {
                itemsIndexed(
                    items = transactions.asReversed().take(5),
                    key = { _, tx -> tx.id },
                    contentType = { _, _ -> "transaction" }
                ) { _, transaction ->
                    TransactionListItem(
                        transaction = transaction,
                        onClick = { onEditTransaction(transaction) },
                        onLongClick = { transactionToDelete = transaction }
                    )
                }
            }
            
            // Bottom spacing for FAB
            item(key = "spacer", contentType = "spacer") {
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun DashboardHeader(onSettings: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Ledger",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Settings icon
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                IconButton(
                    onClick = onSettings,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardFAB(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = ExpressiveMotion.Bouncy
    )
    
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .size(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add transaction",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun QuickActionsGrid(
    onAddTransaction: () -> Unit,
    onViewRecurring: () -> Unit,
    activeRecurring: Int,
    dueRecurring: Int,
    totalTransactions: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            icon = Icons.Outlined.Add,
            title = "Add New",
            detail = "$totalTransactions total",
            onClick = onAddTransaction,
            modifier = Modifier.weight(1f)
        )
        
        QuickActionCard(
            icon = Icons.Outlined.Repeat,
            title = "Recurring",
            detail = if (dueRecurring > 0) "$dueRecurring due" else "$activeRecurring active",
            onClick = onViewRecurring,
            badgeCount = if (dueRecurring > 0) dueRecurring else null,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    title: String,
    detail: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val extendedColors = LedgerTheme.extendedColors
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = ExpressiveMotion.Bouncy
    )
    
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Badge
        if (badgeCount != null && badgeCount > 0) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = extendedColors.expense,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
            ) {
                Text(
                    text = badgeCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = extendedColors.onExpense,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    message: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Outlined.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

