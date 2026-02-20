@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.example.ledger.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ledger.data.*
import com.example.ledger.ui.components.*

// ============ RECURRING TRANSACTION FORM ============

@Composable
fun RecurringTransactionFormScreen(
    onBack: () -> Unit,
    onSave: (Transaction) -> Unit,
    onDelete: ((Transaction) -> Unit)? = null,
    editingTemplate: Transaction? = null,
    modifier: Modifier = Modifier
) {
    var formType by remember {
        mutableStateOf(
            when {
                editingTemplate?.transferToAccount != null -> TransactionFormType.TRANSFER
                editingTemplate?.isIncome == true -> TransactionFormType.INCOME
                else -> TransactionFormType.EXPENSE
            }
        )
    }
    var name by remember { mutableStateOf(editingTemplate?.title ?: "") }
    var amount by remember { mutableStateOf(editingTemplate?.amount?.toString() ?: "") }
    var frequency by remember { mutableStateOf(editingTemplate?.recurringFrequency ?: RecurringFrequency.MONTHLY) }
    var dateState by remember { mutableStateOf(if (editingTemplate != null) DateState.fromFormattedString(editingTemplate.date) else DateState()) }
    var timeState by remember { mutableStateOf(if (editingTemplate != null) TimeState.fromDisplayString(editingTemplate.time) else TimeState()) }
    var category by remember { mutableStateOf(editingTemplate?.category ?: TransactionCategory.BILLS) }
    var account by remember { mutableStateOf(editingTemplate?.account ?: TransactionAccount.MAIN) }

    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    // Date/Time Dialogs
    if (showDatePicker) {
        DatePickerDialog(
            selectedDate = dateState,
            onDismiss = { showDatePicker = false },
            onConfirm = { newDate ->
                dateState = newDate
                showDatePicker = false
            }
        )
    }
    
    if (showTimePicker) {
        TimePickerDialog(
            selectedTime = timeState,
            onDismiss = { showTimePicker = false },
            onConfirm = { newTime ->
                timeState = newTime
                showTimePicker = false
            }
        )
    }
    
    Scaffold(
        topBar = {
            FormTopBar(
                title = if (editingTemplate == null) "New Recurring Payment" else "Edit Recurring Payment",
                onBack = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Type Selector
            SlidingPillSelector(
                selectedType = formType,
                onTypeChange = { formType = it }
            )
            
            // Account Selector
            val accountLabel = if (formType == TransactionFormType.TRANSFER) "From Account" else "Account"
            Text(
                text = accountLabel,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            AccountSelector(
                selectedAccount = account,
                onAccountChange = { account = it }
            )
            
            // Name Field
            FormTextField(
                value = name,
                onValueChange = { name = it },
                label = "Payment Name",
                placeholder = "Netflix, Rent, Salary...",
                leadingIcon = Icons.Outlined.Description
            )
            
            // Amount Field
            FormTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                label = "Amount",
                placeholder = "0.00",
                leadingIcon = Icons.Outlined.AttachMoney,
                keyboardType = KeyboardType.Decimal
            )
            
            // Frequency Selector
            FrequencySelector(
                selected = frequency,
                onSelect = { frequency = it }
            )
            
            // Date and Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DateTimeField(
                    label = "Start Date",
                    value = dateState.toDisplayString(),
                    icon = Icons.Outlined.CalendarToday,
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f)
                )
                
                DateTimeField(
                    label = "Time",
                    value = timeState.toDisplayString(),
                    icon = Icons.Outlined.Schedule,
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Category Selector (Hide for transfer)
            if (formType != TransactionFormType.TRANSFER) {
                CategorySelector(
                    selected = category,
                    isIncome = formType == TransactionFormType.INCOME,
                    onSelect = { category = it }
                )
            }
            
            Spacer(Modifier.height(20.dp))
            
            // Save Button
            FormSaveButton(
                text = "Add Recurring Payment",
                enabled = name.isNotBlank() && amount.isNotBlank(),
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    onSave(
                        Transaction(
                            title = name,
                            amount = amountValue,
                            date = dateState.toFormattedString(),
                            time = timeState.toDisplayString(),
                            category = if (formType == TransactionFormType.TRANSFER) TransactionCategory.POCKET_TRANSFER else category,
                            isIncome = formType == TransactionFormType.INCOME,
                            isRecurring = true,
                            recurringFrequency = frequency,
                            nextPaymentDate = dateState.toFormattedString(),
                            account = account,
                            transferToAccount = if (formType == TransactionFormType.TRANSFER) {
                                if (account == TransactionAccount.MAIN) TransactionAccount.POCKET else TransactionAccount.MAIN
                            } else null,
                            id = editingTemplate?.id ?: generateId()
                        )
                    )
                }
            )
            
            if (editingTemplate != null && onDelete != null) {
                OutlinedButton(
                    onClick = { onDelete(editingTemplate) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Delete Template", style = MaterialTheme.typography.titleSmall)
                }
            }


        }
    }
}

// ============ REGULAR TRANSACTION FORM ============

@Composable
fun TransactionFormScreen(
    onBack: () -> Unit,
    onSave: (Transaction) -> Unit,
    onDelete: ((Transaction) -> Unit)? = null,
    editingTransaction: Transaction? = null,
    modifier: Modifier = Modifier
) {
    var formType by remember {
        mutableStateOf(
            when {
                editingTransaction?.transferToAccount != null -> TransactionFormType.TRANSFER
                editingTransaction?.isIncome == true -> TransactionFormType.INCOME
                else -> TransactionFormType.EXPENSE
            }
        )
    }
    var name by remember { mutableStateOf(editingTransaction?.title ?: "") }
    var amount by remember { mutableStateOf(editingTransaction?.amount?.toString() ?: "") }
    var dateState by remember { mutableStateOf(if (editingTransaction != null) DateState.fromFormattedString(editingTransaction.date) else DateState()) }
    var timeState by remember { mutableStateOf(if (editingTransaction != null) TimeState.fromDisplayString(editingTransaction.time) else TimeState()) }
    var category by remember { mutableStateOf(editingTransaction?.category ?: TransactionCategory.OTHER) }
    var account by remember { mutableStateOf(editingTransaction?.account ?: TransactionAccount.MAIN) }

    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    // Date/Time Dialogs
    if (showDatePicker) {
        DatePickerDialog(
            selectedDate = dateState,
            onDismiss = { showDatePicker = false },
            onConfirm = { newDate ->
                dateState = newDate
                showDatePicker = false
            }
        )
    }
    
    if (showTimePicker) {
        TimePickerDialog(
            selectedTime = timeState,
            onDismiss = { showTimePicker = false },
            onConfirm = { newTime ->
                timeState = newTime
                showTimePicker = false
            }
        )
    }
    
    Scaffold(
        topBar = {
            FormTopBar(
                title = if (editingTransaction == null) "New Transaction" else "Edit Transaction",
                onBack = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Type Selector
            SlidingPillSelector(
                selectedType = formType,
                onTypeChange = { formType = it }
            )
            
            // Account Selector
            val accountLabel = if (formType == TransactionFormType.TRANSFER) "From Account" else "Account"
            Text(
                text = accountLabel,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            AccountSelector(
                selectedAccount = account,
                onAccountChange = { account = it }
            )
            
            // Name Field
            FormTextField(
                value = name,
                onValueChange = { name = it },
                label = "Description",
                placeholder = "What was this transaction for?",
                leadingIcon = Icons.Outlined.Description
            )
            
            // Amount Field
            FormTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                label = "Amount",
                placeholder = "0.00",
                leadingIcon = Icons.Outlined.AttachMoney,
                keyboardType = KeyboardType.Decimal
            )
            
            // Date and Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DateTimeField(
                    label = "Date",
                    value = dateState.toDisplayString(),
                    icon = Icons.Outlined.CalendarToday,
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f)
                )
                
                DateTimeField(
                    label = "Time",
                    value = timeState.toDisplayString(),
                    icon = Icons.Outlined.Schedule,
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Category Selector (Hide for transfer)
            if (formType != TransactionFormType.TRANSFER) {
                CategorySelector(
                    selected = category,
                    isIncome = formType == TransactionFormType.INCOME,
                    onSelect = { category = it }
                )
            }
            
            Spacer(Modifier.height(20.dp))
            
            // Save Button
            FormSaveButton(
                text = "Add Transaction",
                enabled = name.isNotBlank() && amount.isNotBlank(),
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    onSave(
                        Transaction(
                            title = name,
                            amount = amountValue,
                            date = dateState.toFormattedString(),
                            time = timeState.toDisplayString(),
                            category = if (formType == TransactionFormType.TRANSFER) TransactionCategory.POCKET_TRANSFER else category,
                            isIncome = formType == TransactionFormType.INCOME,
                            account = account,
                            transferToAccount = if (formType == TransactionFormType.TRANSFER) {
                                if (account == TransactionAccount.MAIN) TransactionAccount.POCKET else TransactionAccount.MAIN
                            } else null,
                            id = editingTransaction?.id ?: generateId()
                        )
                    )
                }
            )
            
            if (editingTransaction != null && onDelete != null) {
                OutlinedButton(
                    onClick = { onDelete(editingTransaction) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Delete Transaction", style = MaterialTheme.typography.titleSmall)
                }
            }

        }
    }
}

// ============ SHARED FORM COMPONENTS ============

@Composable
private fun FormTopBar(
    title: String,
    onBack: () -> Unit
) {
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
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun DateTimeField(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun FrequencySelector(
    selected: RecurringFrequency,
    onSelect: (RecurringFrequency) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Frequency",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RecurringFrequency.entries.forEach { frequency ->
                FilterChip(
                    selected = selected == frequency,
                    onClick = { onSelect(frequency) },
                    label = { Text(frequency.displayName) },
                    leadingIcon = if (selected == frequency) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun CategorySelector(
    selected: TransactionCategory,
    isIncome: Boolean,
    onSelect: (TransactionCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = if (isIncome) {
        listOf(
            TransactionCategory.SALARY,
            TransactionCategory.FREELANCE,
            TransactionCategory.INVESTMENT,
            TransactionCategory.GIFT,
            TransactionCategory.OTHER_INCOME
        )
    } else {
        listOf(
            TransactionCategory.FOOD,
            TransactionCategory.TRANSPORT,
            TransactionCategory.SHOPPING,
            TransactionCategory.BILLS,
            TransactionCategory.ENTERTAINMENT,
            TransactionCategory.HEALTH,
            TransactionCategory.EDUCATION,
            TransactionCategory.OTHER
        )
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Category",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                FilterChip(
                    selected = selected == cat,
                    onClick = { onSelect(cat) },
                    label = { Text(cat.displayName) },
                    leadingIcon = {
                        Icon(
                            imageVector = cat.icon(),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun FormSaveButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = ExpressiveMotion.Bouncy
    )
    
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(16.dp),
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}
