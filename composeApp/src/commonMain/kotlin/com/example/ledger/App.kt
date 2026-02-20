@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ledger

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import com.example.ledger.data.*
import com.example.ledger.ui.screens.*
import com.example.ledger.ui.theme.AppThemeMode
import com.example.ledger.ui.theme.LedgerTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Clock

enum class Screen { 
    Dashboard, 
    AddSingle, EditSingle, 
    AddRecurring, EditRecurring, 
    ViewRecurring, 
    Settings,
    AllTransactions 
}

@Composable
fun App() {
    // Repository for local persistence
    val repository = remember { LedgerRepository() }
    val googleAuthProvider = remember { GoogleAuthProvider() }
    val googleDriveService = remember { GoogleDriveService() }
    val fileHandler = remember { PlatformFileHandler() }
    val scope = rememberCoroutineScope()
    
    // Load persisted data on first composition
    val recurringTemplates = remember { mutableStateListOf<Transaction>() }
    val actualTransactions = remember { mutableStateListOf<Transaction>() }
    var currentScreen by remember { mutableStateOf(Screen.Dashboard) }
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }
    var editingTemplate by remember { mutableStateOf<Transaction?>(null) }
    var currentTheme by remember { mutableStateOf(AppThemeMode.DEFAULT) }
    var lastSyncTime by remember { mutableStateOf<String?>(null) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var isGoogleConnected by remember { mutableStateOf(googleAuthProvider.isSignedIn()) }
    var userEmail by remember { mutableStateOf(googleAuthProvider.getUserEmail()) }
    
    var isBackingUpCloud by remember { mutableStateOf(false) }
    var isRestoringCloud by remember { mutableStateOf(false) }
    var isBackingUpLocal by remember { mutableStateOf(false) }
    var appSettings by remember { mutableStateOf(AppSettings()) }
    
    // Load data from persistence on first launch
    LaunchedEffect(Unit) {
        // Load saved theme
        val savedTheme = repository.loadTheme()
        currentTheme = try {
            AppThemeMode.valueOf(savedTheme)
        } catch (_: Exception) {
            AppThemeMode.DEFAULT
        }
        
        // Load saved transactions
        val savedTransactions = repository.loadTransactions()
        actualTransactions.addAll(savedTransactions)
        
        // Load saved recurring templates
        val savedTemplates = repository.loadRecurringTemplates()
        recurringTemplates.addAll(savedTemplates)
        
        // Load settings
        appSettings = repository.loadAppSettings()
        isGoogleConnected = googleAuthProvider.isSignedIn()
        userEmail = googleAuthProvider.getUserEmail()
        
        // Auto-sync on launch
        if (isGoogleConnected && appSettings.googleDriveConnected) {
            scope.launch {
                isBackingUpCloud = true
                syncWithCloud(repository, googleAuthProvider, googleDriveService, actualTransactions, recurringTemplates) { msg ->
                    snackbarMessage = msg
                    if (msg.contains("success", ignoreCase = true)) {
                        val now = Clock.System.now().toEpochMilliseconds()
                        appSettings = appSettings.copy(lastSyncTimestamp = now)
                        repository.saveAppSettings(appSettings)
                    }
                    isBackingUpCloud = false
                }
            }
        }
    }
    
    // Auto-save: persist whenever transactions or templates change
    // We use a derived state + launched effect to watch changes
    val transactionCount = actualTransactions.size
    val templateCount = recurringTemplates.size
    LaunchedEffect(transactionCount, templateCount) {
        if (transactionCount > 0 || templateCount > 0) {
            repository.saveTransactions(actualTransactions.toList())
            repository.saveRecurringTemplates(recurringTemplates.toList())
            
            // Check for Auto-Backup Cloud
            if (isGoogleConnected && appSettings.autoBackupIntervalCloud != "OFF") {
                val lastSync = appSettings.lastSyncTimestamp
                val intervalMs = when(appSettings.autoBackupIntervalCloud) {
                    "DAILY" -> 24 * 60 * 60 * 1000L
                    "WEEKLY" -> 7 * 24 * 60 * 60 * 1000L
                    else -> Long.MAX_VALUE
                }
                
                if (Clock.System.now().toEpochMilliseconds() - lastSync > intervalMs) {
                    scope.launch {
                        isBackingUpCloud = true
                        val token = googleAuthProvider.getAccessToken() ?: return@launch
                        val backupJson = repository.exportBackupJson()
                        val result = googleDriveService.uploadBackup(token, backupJson)
                        result.onSuccess {
                            val now = Clock.System.now().toEpochMilliseconds()
                            appSettings = appSettings.copy(lastSyncTimestamp = now)
                            repository.saveAppSettings(appSettings)
                            println("App: Auto Cloud Backup successful")
                        }
                        isBackingUpCloud = false
                    }
                }
            }

            // Check for Auto-Backup Local
            if (appSettings.autoBackupIntervalLocal != "OFF") {
                val lastLocal = appSettings.lastLocalBackupTimestamp
                val intervalMs = when(appSettings.autoBackupIntervalLocal) {
                    "DAILY" -> 24 * 60 * 60 * 1000L
                    "WEEKLY" -> 7 * 24 * 60 * 60 * 1000L
                    else -> Long.MAX_VALUE
                }

                if (Clock.System.now().toEpochMilliseconds() - lastLocal > intervalMs) {
                    val now = Clock.System.now().toEpochMilliseconds()
                    appSettings = appSettings.copy(lastLocalBackupTimestamp = now)
                    repository.saveAppSettings(appSettings)
                    println("App: Auto Local Backup triggered (represented by timestamp update)")
                }
            }
        }
    }
    
    LedgerTheme(themeMode = currentTheme) {
        val snackbarHostState = remember { SnackbarHostState() }
        
        // Show snackbar messages
        LaunchedEffect(snackbarMessage) {
            snackbarMessage?.let {
                snackbarHostState.showSnackbar(it)
                snackbarMessage = null
            }
        }
        
        when (currentScreen) {
            Screen.Dashboard -> DashboardScreen(
                transactions = actualTransactions,
                recurringTemplates = recurringTemplates,
                onAddSingle = { 
                    editingTransaction = null
                    currentScreen = Screen.AddSingle 
                },
                onAddRecurring = { 
                    editingTemplate = null
                    currentScreen = Screen.AddRecurring 
                },
                onViewRecurring = { currentScreen = Screen.ViewRecurring },
                onViewAllTransactions = { currentScreen = Screen.AllTransactions },
                onEditTransaction = { tx ->
                    editingTransaction = tx
                    currentScreen = Screen.EditSingle
                },
                onSettings = { currentScreen = Screen.Settings },
                onDeleteTransaction = { transaction ->
                    actualTransactions.remove(transaction)
                    repository.deleteTransaction(transaction.id)
                    snackbarMessage = "Transaction deleted"
                }
            )
            
            Screen.AllTransactions -> TransactionsScreen(
                transactions = actualTransactions,
                onBack = { currentScreen = Screen.Dashboard },
                onEditTransaction = { tx ->
                    editingTransaction = tx
                    currentScreen = Screen.EditSingle
                },
                onDeleteTransaction = { transaction ->
                    actualTransactions.remove(transaction)
                    repository.deleteTransaction(transaction.id)
                    snackbarMessage = "Transaction deleted"
                }
            )
            
            Screen.AddSingle, Screen.EditSingle -> TransactionFormScreen(
                editingTransaction = editingTransaction,
                onBack = { 
                    currentScreen = if (editingTransaction != null && !actualTransactions.contains(editingTransaction)) {
                        // If we were editing and deleted, or just finished
                        Screen.Dashboard 
                    } else {
                        if (currentScreen == Screen.EditSingle && editingTransaction != null) Screen.Dashboard else Screen.Dashboard
                    }
                    // Actually, simpler:
                    currentScreen = Screen.Dashboard 
                },
                onSave = { transaction ->
                    val updatedTx = transaction.copy(lastModified = Clock.System.now().toEpochMilliseconds())
                    if (editingTransaction != null) {
                        val index = actualTransactions.indexOfFirst { it.id == updatedTx.id }
                        if (index != -1) {
                            actualTransactions[index] = updatedTx
                            repository.updateTransaction(updatedTx)
                        }
                    } else {
                        actualTransactions.add(updatedTx)
                        repository.addTransaction(updatedTx)
                    }
                    currentScreen = Screen.Dashboard
                },
                onDelete = { transaction ->
                    actualTransactions.removeAll { it.id == transaction.id }
                    repository.deleteTransaction(transaction.id)
                    snackbarMessage = "Transaction deleted"
                    currentScreen = Screen.Dashboard
                }
            )
            
            Screen.AddRecurring, Screen.EditRecurring -> RecurringTransactionFormScreen(
                editingTemplate = editingTemplate,
                onBack = { 
                    currentScreen = if (recurringTemplates.isNotEmpty()) {
                        Screen.ViewRecurring
                    } else {
                        Screen.Dashboard
                    }
                },
                onSave = { transaction ->
                    val updatedTemplate = transaction.copy(lastModified = Clock.System.now().toEpochMilliseconds())
                    if (editingTemplate != null) {
                        val index = recurringTemplates.indexOfFirst { it.id == updatedTemplate.id }
                        if (index != -1) {
                            recurringTemplates[index] = updatedTemplate
                            repository.updateRecurringTemplate(updatedTemplate)
                        }
                    } else {
                        recurringTemplates.add(updatedTemplate)
                        repository.addRecurringTemplate(updatedTemplate)
                    }
                    currentScreen = Screen.ViewRecurring
                },
                onDelete = { transaction ->
                    recurringTemplates.removeAll { it.id == transaction.id }
                    repository.deleteRecurringTemplate(transaction.id)
                    snackbarMessage = "Recurring template deleted"
                    currentScreen = Screen.ViewRecurring
                }
            )
            
            Screen.ViewRecurring -> RecurringScreen(
                recurringTemplates = recurringTemplates,
                onBack = { currentScreen = Screen.Dashboard },
                onAddRecurring = { 
                    editingTemplate = null
                    currentScreen = Screen.AddRecurring 
                },
                onEditTemplate = { template ->
                    editingTemplate = template
                    currentScreen = Screen.EditRecurring
                },
                onProcessTransaction = { template ->
                    // Create an actual transaction from the template
                    val actualTransaction = template.copy(
                        id = generateNewId(),
                        isRecurring = false,
                        date = getCurrentDateFormatted(),
                        time = getCurrentTimeFormatted()
                    )
                    actualTransactions.add(actualTransaction)
                    repository.addTransaction(actualTransaction)
                    
                    // Update the template's next payment date
                    val index = recurringTemplates.indexOf(template)
                    if (index != -1) {
                        val updatedTemplate = template.copy(
                            nextPaymentDate = calculateNextPaymentDateFromTemplate(template)
                        )
                        recurringTemplates[index] = updatedTemplate
                        repository.updateRecurringTemplate(updatedTemplate)
                    }
                    snackbarMessage = "Payment processed"
                },
                onSkipTransaction = { template ->
                    val index = recurringTemplates.indexOf(template)
                    if (index != -1) {
                        val updatedTemplate = template.copy(
                            nextPaymentDate = calculateNextPaymentDateFromTemplate(template)
                        )
                        recurringTemplates[index] = updatedTemplate
                        repository.updateRecurringTemplate(updatedTemplate)
                    }
                    snackbarMessage = "Payment skipped"
                },
                onDeleteTemplate = { template ->
                    recurringTemplates.remove(template)
                    repository.deleteRecurringTemplate(template.id)
                    snackbarMessage = "Recurring template deleted"
                }
            )

            Screen.Settings -> SettingsScreen(
                currentTheme = currentTheme,
                onBack = { currentScreen = Screen.Dashboard },
                transactionCount = actualTransactions.size,
                recurringCount = recurringTemplates.size,
                isGoogleConnected = isGoogleConnected,
                userEmail = userEmail,
                onThemeChange = { mode ->
                    currentTheme = mode
                    repository.saveTheme(mode.name)
                },
                lastCloudBackupTime = formatTimestampFull(appSettings.lastSyncTimestamp),
                lastLocalBackupTime = formatTimestampFull(appSettings.lastLocalBackupTimestamp),
                autoBackupIntervalCloud = appSettings.autoBackupIntervalCloud,
                autoBackupIntervalLocal = appSettings.autoBackupIntervalLocal,
                isBackingUpCloud = isBackingUpCloud,
                isRestoringCloud = isRestoringCloud,
                onUpdateAutoBackupCloud = { interval ->
                    appSettings = appSettings.copy(autoBackupIntervalCloud = interval)
                    repository.saveAppSettings(appSettings)
                },
                onUpdateAutoBackupLocal = { interval ->
                    appSettings = appSettings.copy(autoBackupIntervalLocal = interval)
                    repository.saveAppSettings(appSettings)
                },
                onBackupToCloud = {
                    scope.launch {
                        isBackingUpCloud = true
                        snackbarMessage = "Backup started..."
                        val token = googleAuthProvider.getAccessToken()
                        if (token != null) {
                            val jsonString = repository.exportBackupJson()
                            val result = googleDriveService.uploadBackup(token, jsonString)
                            result.onSuccess {
                                snackbarMessage = "Backup successful!"
                                val now = Clock.System.now().toEpochMilliseconds()
                                appSettings = appSettings.copy(lastSyncTimestamp = now)
                                repository.saveAppSettings(appSettings)
                            }.onFailure { e ->
                                val errorMsg = e.message ?: e.toString()
                                println("App: Backup failed: $errorMsg")
                                e.printStackTrace()
                                snackbarMessage = "Backup failed: $errorMsg"
                            }
                        } else {
                            snackbarMessage = "Please sign in to Google first"
                        }
                        isBackingUpCloud = false
                    }
                },
                onRestoreFromCloud = {
                    scope.launch {
                        isRestoringCloud = true
                        snackbarMessage = "Downloading backup..."
                        val token = googleAuthProvider.getAccessToken()
                        if (token != null) {
                            val result = googleDriveService.downloadBackup(token)
                            result.onSuccess { jsonString ->
                                if (repository.importBackupJson(jsonString)) {
                                    // Reload data
                                    actualTransactions.clear()
                                    actualTransactions.addAll(repository.loadTransactions())
                                    recurringTemplates.clear()
                                    recurringTemplates.addAll(repository.loadRecurringTemplates())
                                    
                                    val settings = repository.loadAppSettings()
                                    currentTheme = try {
                                        AppThemeMode.valueOf(settings.themeMode)
                                    } catch (_: Exception) {
                                        AppThemeMode.DEFAULT
                                    }
                                    snackbarMessage = "Data restored successfully!"
                                } else {
                                    snackbarMessage = "Failed to parse backup data"
                                }
                            }.onFailure { e ->
                                val errorMsg = e.message ?: e.toString()
                                println("App: Restore failed: $errorMsg")
                                e.printStackTrace()
                                snackbarMessage = "Restore failed: $errorMsg"
                            }
                        } else {
                            snackbarMessage = "Please sign in to Google first"
                        }
                        isRestoringCloud = false
                    }
                },
                onExportData = {
                    val json = repository.exportBackupJson()
                    scope.launch {
                        isBackingUpLocal = true
                        val fileName = "ledger_backup_${Clock.System.now().toEpochMilliseconds()}.json"
                        val success = fileHandler.saveJsonFile(json, fileName)
                        if (!success) {
                            println("App: Export failed")
                            snackbarMessage = "Export failed"
                        } else {
                            snackbarMessage = "Export started"
                            val now = Clock.System.now().toEpochMilliseconds()
                            appSettings = appSettings.copy(lastLocalBackupTimestamp = now)
                            repository.saveAppSettings(appSettings)
                        }
                        isBackingUpLocal = false
                    }
                },
                onImportData = {
                    scope.launch {
                        val json = fileHandler.pickJsonFile()
                        if (json != null) {
                            val success = repository.importBackupJson(json)
                            if (success) {
                                // Reload everything
                                actualTransactions.clear()
                                actualTransactions.addAll(repository.loadTransactions())
                                recurringTemplates.clear()
                                recurringTemplates.addAll(repository.loadRecurringTemplates())
                                
                                val settings = repository.loadAppSettings()
                                try {
                                    currentTheme = AppThemeMode.valueOf(settings.themeMode)
                                } catch (_: Exception) {}
                                
                                snackbarMessage = "Data imported successfully!"
                            } else {
                                snackbarMessage = "Invalid backup file"
                            }
                        }
                    }
                },
                onConnectGoogle = {
                    scope.launch {
                        val success = googleAuthProvider.signIn()
                        if (success) {
                            isGoogleConnected = true
                            userEmail = googleAuthProvider.getUserEmail()
                            appSettings = appSettings.copy(googleDriveConnected = true)
                            repository.saveAppSettings(appSettings)
                            
                            // Auto-sync after connection
                            isBackingUpCloud = true
                            syncWithCloud(repository, googleAuthProvider, googleDriveService, actualTransactions, recurringTemplates) { msg ->
                                snackbarMessage = msg
                                if (msg.contains("success", ignoreCase = true)) {
                                    val now = Clock.System.now().toEpochMilliseconds()
                                    appSettings = appSettings.copy(lastSyncTimestamp = now)
                                    repository.saveAppSettings(appSettings)
                                }
                                isBackingUpCloud = false
                            }
                        } else {
                            snackbarMessage = "Google connection failed"
                        }
                    }
                },
                onSignOutGoogle = {
                    scope.launch {
                        googleAuthProvider.signOut()
                        isGoogleConnected = false
                        userEmail = null
                        appSettings = appSettings.copy(googleDriveConnected = false)
                        repository.saveAppSettings(appSettings)
                        snackbarMessage = "Cloud backup disconnected"
                    }
                },
                onClearAllData = {
                    actualTransactions.clear()
                    recurringTemplates.clear()
                    repository.clearAll()
                    currentTheme = AppThemeMode.DEFAULT
                    lastSyncTime = null
                    snackbarMessage = "All data cleared"
                }
            )
        }
    }
}

// Helper functions
private suspend fun syncWithCloud(
    repository: LedgerRepository,
    googleAuthProvider: GoogleAuthProvider,
    googleDriveService: GoogleDriveService,
    actualTransactions: MutableList<Transaction>,
    recurringTemplates: MutableList<Transaction>,
    onMessage: (String) -> Unit
) {
    val token = googleAuthProvider.getAccessToken() ?: return
    
    // 1. Download remote backup
    val result = googleDriveService.downloadBackup(token)
    result.onSuccess { backupJson ->
        val remoteJson = Json { ignoreUnknownKeys = true }
        val remoteBackup = remoteJson.decodeFromString<LedgerBackup>(backupJson)
        
        // 2. Merge with local
        val changed = repository.mergeWithBackup(remoteBackup)
        if (changed) {
            // Update UI list
            actualTransactions.clear()
            actualTransactions.addAll(repository.loadTransactions())
            recurringTemplates.clear()
            recurringTemplates.addAll(repository.loadRecurringTemplates())
            
            onMessage("Cloud data synced successfully")
        }
    }.onFailure { e ->
        onMessage("Sync failed: ${e.message}")
    }
}

private fun generateNewId(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    val timestamp = Clock.System.now().toEpochMilliseconds().toString().takeLast(6)
    val random = (1..10).map { chars[kotlin.random.Random.nextInt(chars.length)] }.joinToString("")
    return "$timestamp$random"
}