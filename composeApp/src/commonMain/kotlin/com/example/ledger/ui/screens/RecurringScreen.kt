@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ledger.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ledger.data.Transaction
import com.example.ledger.data.TransactionAccount
import com.example.ledger.data.isPaymentDueCheck
import com.example.ledger.data.toPriceString
import com.example.ledger.ui.components.ExpressiveMotion
import com.example.ledger.ui.components.icon
import com.example.ledger.ui.theme.LedgerTheme

@Composable
fun RecurringScreen(
    recurringTemplates: List<Transaction>,
    onBack: () -> Unit,
    onAddRecurring: () -> Unit,
    onProcessTransaction: (Transaction) -> Unit,
    onSkipTransaction: (Transaction) -> Unit,
    onDeleteTemplate: (Transaction) -> Unit,
    onEditTemplate: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeTransactions by remember {
        derivedStateOf {
            recurringTemplates.filter { !isPaymentDueCheck(it.nextPaymentDate ?: it.date) }
        }
    }
    val dueTransactions by remember {
        derivedStateOf {
            recurringTemplates.filter { isPaymentDueCheck(it.nextPaymentDate ?: it.date) }
        }
    }
    
    val monthlyTotal by remember {
        derivedStateOf {
            recurringTemplates
                .filter { it.recurringFrequency?.displayName == "Monthly" }
                .sumOf { if (it.isIncome) it.amount else -it.amount }
        }
    }
    
    val dueAmount by remember {
        derivedStateOf { dueTransactions.sumOf { it.amount } }
    }
    
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            RecurringHeader(onBack = onBack)
        },
        floatingActionButton = {
            RecurringFAB(onClick = onAddRecurring)
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        if (recurringTemplates.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyRecurringState()
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Stats Row
                item(key = "stats", contentType = "stats") {
                    RecurringStatsRow(
                        monthlyTotal = monthlyTotal,
                        dueAmount = dueAmount,
                        totalCount = recurringTemplates.size
                    )
                }
                
                // Tab Selector
                item(key = "tabs", contentType = "tabs") {
                    RecurringTabSelector(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        activeCount = activeTransactions.size,
                        dueCount = dueTransactions.size
                    )
                }
                
                // Transaction List
                if (selectedTab == 0) {
                    if (activeTransactions.isEmpty()) {
                        item(key = "empty-active", contentType = "empty") {
                            EmptyTabState(
                                icon = Icons.Outlined.CheckCircle,
                                message = "All payments are up to date"
                            )
                        }
                    } else {
                        items(activeTransactions, key = { it.id }) { transaction ->
                            RecurringTransactionItem(
                                transaction = transaction,
                                isActive = true,
                                onClick = { onEditTemplate(transaction) }
                            )
                        }
                    }
                } else {
                    if (dueTransactions.isEmpty()) {
                        item(key = "empty-due", contentType = "empty") {
                            EmptyTabState(
                                icon = Icons.Outlined.Celebration,
                                message = "No payments due"
                            )
                        }
                    } else {
                        items(dueTransactions, key = { it.id }) { transaction ->
                            DueTransactionItem(
                                transaction = transaction,
                                onProcess = { onProcessTransaction(transaction) },
                                onSkip = { onSkipTransaction(transaction) },
                                onEdit = { onEditTemplate(transaction) }
                            )
                        }
                    }
                }
                
                item(key = "spacer", contentType = "spacer") {
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun RecurringHeader(onBack: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back"
                )
            }
            
            Spacer(Modifier.width(4.dp))
            
            Column {
                Text(
                    text = "Recurring Payments",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Manage subscriptions and bills",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RecurringFAB(onClick: () -> Unit) {
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
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add recurring payment",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun RecurringStatsRow(
    monthlyTotal: Double,
    dueAmount: Double,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val extendedColors = LedgerTheme.extendedColors
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            icon = Icons.Outlined.CalendarMonth,
            label = "Monthly",
            value = "$${monthlyTotal.toPriceString()}",
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f)
        )
        
        StatCard(
            icon = Icons.Outlined.Warning,
            label = "Due Now",
            value = "$${dueAmount.toPriceString()}",
            containerColor = if (dueAmount > 0) extendedColors.expenseContainer else extendedColors.incomeContainer,
            contentColor = if (dueAmount > 0) extendedColors.expense else extendedColors.income,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = contentColor
            )
            
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun RecurringTabSelector(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    activeCount: Int,
    dueCount: Int,
    modifier: Modifier = Modifier
) {
    val animatedOffset by animateFloatAsState(
        targetValue = selectedTab.toFloat(),
        animationSpec = ExpressiveMotion.Bouncy
    )
    
    val extendedColors = LedgerTheme.extendedColors
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        BoxWithConstraints(
            modifier = Modifier.padding(4.dp)
        ) {
            val halfWidth = (maxWidth - 8.dp) / 2
            
            // Sliding indicator
            Box(
                modifier = Modifier
                    .offset(x = halfWidth * animatedOffset + 4.dp * animatedOffset)
                    .width(halfWidth)
                    .fillMaxHeight()
                    .background(
                        color = if (selectedTab == 0) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            extendedColors.expenseContainer,
                        shape = RoundedCornerShape(10.dp)
                    )
            )
            
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabButton(
                    text = "Active",
                    count = activeCount,
                    isSelected = selectedTab == 0,
                    showBadge = false,
                    onClick = { onTabSelected(0) },
                    modifier = Modifier.weight(1f)
                )
                
                TabButton(
                    text = "Due",
                    count = dueCount,
                    isSelected = selectedTab == 1,
                    showBadge = dueCount > 0 && selectedTab != 1,
                    onClick = { onTabSelected(1) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    count: Int,
    isSelected: Boolean,
    showBadge: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LedgerTheme.extendedColors
    
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (showBadge) 
                    extendedColors.expense 
                else if (isSelected) 
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else 
                    MaterialTheme.colorScheme.surfaceContainerHighest
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (showBadge) 
                        extendedColors.onExpense
                    else if (isSelected) 
                        MaterialTheme.colorScheme.primary
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun RecurringTransactionItem(
    transaction: Transaction,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val extendedColors = LedgerTheme.extendedColors
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = ExpressiveMotion.Snappy
    )
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category icon
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (transaction.isIncome) 
                    extendedColors.incomeContainer 
                else 
                    extendedColors.expenseContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = transaction.category.icon(),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = if (transaction.isIncome) 
                            extendedColors.income 
                        else 
                            extendedColors.expense
                    )
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = transaction.recurringFrequency?.displayName ?: "Monthly",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Icon(
                        imageVector = if (transaction.account == TransactionAccount.MAIN) 
                            Icons.Outlined.AccountBalance else Icons.Outlined.Wallet,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Next: ${transaction.nextPaymentDate ?: "N/A"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = (if (transaction.isIncome) "+" else "-") + "$${transaction.amount.toPriceString()}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (transaction.isIncome) extendedColors.income else extendedColors.expense
            )
        }
    }
}

@Composable
fun DueTransactionItem(
    transaction: Transaction,
    onProcess: () -> Unit,
    onSkip: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = ExpressiveMotion.Snappy
    )
    
    val extendedColors = LedgerTheme.extendedColors
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onEdit
            ),
        shape = RoundedCornerShape(16.dp),
        color = extendedColors.expenseContainer
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = extendedColors.expense.copy(alpha = 0.2f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = transaction.category.icon(),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = extendedColors.expense
                        )
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = transaction.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = extendedColors.expense
                        ) {
                            Text(
                                text = "DUE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = extendedColors.onExpense,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (transaction.account == TransactionAccount.MAIN) 
                                Icons.Outlined.AccountBalance else Icons.Outlined.Wallet,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "${transaction.recurringFrequency?.displayName ?: "Monthly"} payment",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Text(
                    text = "$${transaction.amount.toPriceString()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = extendedColors.expense
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onSkip,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SkipNext,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Skip")
                }
                
                Button(
                    onClick = onProcess,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Process")
                }
            }
        }
    }
}

@Composable
private fun EmptyRecurringState(modifier: Modifier = Modifier) {
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
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Outlined.Repeat,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Text(
                text = "No recurring payments",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Add subscriptions and bills to track them",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyTabState(
    icon: ImageVector,
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Keep backward compatibility
@Composable
fun RecurringDashboard(
    monthlyAverage: Double,
    dueThisMonth: Double,
    yearlyTotal: Double,
    totalIncome: Double,
    totalExpense: Double,
    modifier: Modifier = Modifier
) {
    RecurringStatsRow(
        monthlyTotal = monthlyAverage,
        dueAmount = dueThisMonth,
        totalCount = 0,
        modifier = modifier
    )
}
