package com.example.ledger.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ledger.data.Transaction
import com.example.ledger.data.TransactionAccount
import com.example.ledger.data.TransactionCategory
import com.example.ledger.data.toPriceString
import com.example.ledger.ui.theme.LedgerTheme

// ============ MOTION CONSTANTS ============

object ExpressiveMotion {
    val Bouncy = spring<Float>(
        dampingRatio = 0.7f,
        stiffness = 400f
    )
    val Snappy = spring<Float>(
        dampingRatio = 0.8f,
        stiffness = 500f
    )
    val Smooth = spring<Float>(
        dampingRatio = 0.9f,
        stiffness = 300f
    )
}

// ============ CATEGORY ICONS ============

fun TransactionCategory.icon(): ImageVector = when (this) {
    TransactionCategory.SALARY -> Icons.Outlined.Work
    TransactionCategory.FREELANCE -> Icons.Outlined.Laptop
    TransactionCategory.INVESTMENT -> Icons.Outlined.TrendingUp
    TransactionCategory.GIFT -> Icons.Outlined.CardGiftcard
    TransactionCategory.OTHER_INCOME -> Icons.Outlined.AttachMoney
    TransactionCategory.FOOD -> Icons.Outlined.Restaurant
    TransactionCategory.TRANSPORT -> Icons.Outlined.DirectionsCar
    TransactionCategory.SHOPPING -> Icons.Outlined.ShoppingBag
    TransactionCategory.BILLS -> Icons.Outlined.Receipt
    TransactionCategory.ENTERTAINMENT -> Icons.Outlined.Movie
    TransactionCategory.HEALTH -> Icons.Outlined.LocalHospital
    TransactionCategory.EDUCATION -> Icons.Outlined.School
    TransactionCategory.POCKET_TRANSFER -> Icons.Outlined.Sync
    TransactionCategory.OTHER -> Icons.Outlined.MoreHoriz
}

// ============ BALANCE CARD ============

@Composable
fun AnimatedBalanceCard(
    totalBalance: Double,
    pocketBalance: Double,
    modifier: Modifier = Modifier
) {
    val mainBalance = totalBalance - pocketBalance
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                AnimatedCounter(
                    value = totalBalance,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    prefix = if (totalBalance >= 0) "$" else "-$"
                )
            }
            
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                thickness = 1.dp
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BalanceSmallItem(
                    label = "Main Account",
                    amount = mainBalance,
                    icon = Icons.Outlined.AccountBalance,
                    modifier = Modifier.weight(1f)
                )
                
                VerticalDivider(
                    modifier = Modifier.height(32.dp).padding(horizontal = 12.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
                )
                
                BalanceSmallItem(
                    label = "Mini Pocket",
                    amount = pocketBalance,
                    icon = Icons.Outlined.Wallet,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BalanceSmallItem(
    label: String,
    amount: Double,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
            )
            Text(
                text = (if (amount >= 0) "$" else "-$") + amount.let { if (it < 0) -it else it }.toPriceString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun AnimatedCounter(
    value: Double,
    style: androidx.compose.ui.text.TextStyle,
    color: androidx.compose.ui.graphics.Color,
    prefix: String = ""
) {
    val displayValue = if (value < 0) -value else value
    val animatedValue by animateFloatAsState(
        targetValue = displayValue.toFloat(),
        animationSpec = tween(600, easing = FastOutSlowInEasing)
    )
    
    Text(
        text = prefix + animatedValue.toDouble().toPriceString(),
        style = style,
        color = color,
        fontWeight = FontWeight.W600
    )
}

// ============ ACCOUNT SELECTOR ============

@Composable
fun AccountSelector(
    selectedAccount: TransactionAccount,
    onAccountChange: (TransactionAccount) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AccountOption(
            account = TransactionAccount.MAIN,
            isSelected = selectedAccount == TransactionAccount.MAIN,
            icon = Icons.Outlined.AccountBalance,
            onClick = { onAccountChange(TransactionAccount.MAIN) },
            modifier = Modifier.weight(1f)
        )
        
        AccountOption(
            account = TransactionAccount.POCKET,
            isSelected = selectedAccount == TransactionAccount.POCKET,
            icon = Icons.Outlined.Wallet,
            onClick = { onAccountChange(TransactionAccount.POCKET) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AccountOption(
    account: TransactionAccount,
    isSelected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = ExpressiveMotion.Bouncy
    )
    
    Surface(
        modifier = modifier
            .height(64.dp)
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
        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.secondary) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = account.displayName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============ FORM TYPES ============

enum class TransactionFormType {
    EXPENSE, INCOME, TRANSFER
}

// ============ PILL SELECTOR ============

@Composable
fun SlidingPillSelector(
    selectedType: TransactionFormType,
    onTypeChange: (TransactionFormType) -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedOffset by animateFloatAsState(
        targetValue = when (selectedType) {
            TransactionFormType.EXPENSE -> 0f
            TransactionFormType.TRANSFER -> 1f
            TransactionFormType.INCOME -> 2f
        },
        animationSpec = ExpressiveMotion.Bouncy
    )
    
    val extendedColors = LedgerTheme.extendedColors
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        BoxWithConstraints(
            modifier = Modifier.padding(4.dp)
        ) {
            val itemWidth = (maxWidth - 8.dp) / 3
            
            // Sliding indicator
            Box(
                modifier = Modifier
                    .offset(x = itemWidth * animatedOffset + 4.dp * animatedOffset)
                    .width(itemWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (selectedType) {
                            TransactionFormType.EXPENSE -> extendedColors.expenseContainer
                            TransactionFormType.INCOME -> extendedColors.incomeContainer
                            TransactionFormType.TRANSFER -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    )
            )
            
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PillOption(
                    text = "Expense",
                    icon = Icons.Outlined.ArrowUpward,
                    isSelected = selectedType == TransactionFormType.EXPENSE,
                    selectedColor = extendedColors.expense,
                    onClick = { onTypeChange(TransactionFormType.EXPENSE) },
                    modifier = Modifier.weight(1f)
                )
                
                PillOption(
                    text = "Transfer",
                    icon = Icons.Outlined.Sync,
                    isSelected = selectedType == TransactionFormType.TRANSFER,
                    selectedColor = MaterialTheme.colorScheme.secondary,
                    onClick = { onTypeChange(TransactionFormType.TRANSFER) },
                    modifier = Modifier.weight(1f)
                )
                
                PillOption(
                    text = "Income",
                    icon = Icons.Outlined.ArrowDownward,
                    isSelected = selectedType == TransactionFormType.INCOME,
                    selectedColor = extendedColors.income,
                    onClick = { onTypeChange(TransactionFormType.INCOME) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PillOption(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    selectedColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============ TRANSACTION LIST ITEM ============

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionListItem(
    transaction: Transaction,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
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
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
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
            
            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
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
                        text = "${transaction.category.displayName} · ${transaction.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            
            // Amount
            val amountColor = when {
                transaction.transferToAccount != null -> MaterialTheme.colorScheme.secondary
                transaction.isIncome -> extendedColors.income
                else -> extendedColors.expense
            }
            val amountPrefix = when {
                transaction.transferToAccount != null -> ""
                transaction.isIncome -> "+"
                else -> "-"
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amountPrefix + "$${transaction.amount.toPriceString()}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
                if (transaction.transferToAccount != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (transaction.account == TransactionAccount.MAIN) 
                                Icons.Outlined.AccountBalance else Icons.Outlined.Wallet,
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Icon(
                            imageVector = if (transaction.transferToAccount == TransactionAccount.MAIN) 
                                Icons.Outlined.AccountBalance else Icons.Outlined.Wallet,
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}


// ============ INFO CARD ============

@Composable
fun InfoCard(
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            content()
        }
    }
}

// ============ STAT ROW ============

@Composable
fun StatRow(
    stats: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        stats.forEach { (label, value) ->
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
