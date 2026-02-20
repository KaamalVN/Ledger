package com.example.ledger.data

import com.russhwolf.settings.Settings
import kotlinx.serialization.json.Json

/**
 * Repository handling all local data persistence using multiplatform-settings.
 * Stores transactions, recurring templates, and app settings as JSON in key-value store.
 */
class LedgerRepository {
    private val settings: Settings = Settings()
    
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
        encodeDefaults = true
    }
    
    companion object {
        private const val KEY_TRANSACTIONS = "ledger_transactions"
        private const val KEY_RECURRING = "ledger_recurring_templates"
        private const val KEY_SETTINGS = "ledger_app_settings"
        private const val KEY_THEME = "ledger_theme_mode"
    }
    
    // ============ TRANSACTIONS ============
    
    fun saveTransactions(transactions: List<Transaction>) {
        val jsonString = json.encodeToString(transactions)
        settings.putString(KEY_TRANSACTIONS, jsonString)
    }
    
    fun loadTransactions(): List<Transaction> {
        val jsonString = settings.getStringOrNull(KEY_TRANSACTIONS) ?: return emptyList()
        return try {
            json.decodeFromString<List<Transaction>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addTransaction(transaction: Transaction) {
        val current = loadTransactions().toMutableList()
        current.add(transaction)
        saveTransactions(current)
    }
    
    fun deleteTransaction(transactionId: String) {
        val current = loadTransactions().toMutableList()
        current.removeAll { it.id == transactionId }
        saveTransactions(current)
    }
    
    fun updateTransaction(transaction: Transaction) {
        val current = loadTransactions().toMutableList()
        val index = current.indexOfFirst { it.id == transaction.id }
        if (index != -1) {
            current[index] = transaction
            saveTransactions(current)
        }
    }
    
    // ============ RECURRING TEMPLATES ============
    
    fun saveRecurringTemplates(templates: List<Transaction>) {
        val jsonString = json.encodeToString(templates)
        settings.putString(KEY_RECURRING, jsonString)
    }
    
    fun loadRecurringTemplates(): List<Transaction> {
        val jsonString = settings.getStringOrNull(KEY_RECURRING) ?: return emptyList()
        return try {
            json.decodeFromString<List<Transaction>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addRecurringTemplate(template: Transaction) {
        val current = loadRecurringTemplates().toMutableList()
        current.add(template)
        saveRecurringTemplates(current)
    }
    
    fun deleteRecurringTemplate(templateId: String) {
        val current = loadRecurringTemplates().toMutableList()
        current.removeAll { it.id == templateId }
        saveRecurringTemplates(current)
    }
    
    fun updateRecurringTemplate(template: Transaction) {
        val current = loadRecurringTemplates().toMutableList()
        val index = current.indexOfFirst { it.id == template.id }
        if (index != -1) {
            current[index] = template
            saveRecurringTemplates(current)
        }
    }
    
    // ============ APP SETTINGS ============
    
    fun saveAppSettings(appSettings: AppSettings) {
        val jsonString = json.encodeToString(appSettings)
        settings.putString(KEY_SETTINGS, jsonString)
    }
    
    fun loadAppSettings(): AppSettings {
        val jsonString = settings.getStringOrNull(KEY_SETTINGS) ?: return AppSettings()
        return try {
            json.decodeFromString<AppSettings>(jsonString)
        } catch (e: Exception) {
            AppSettings()
        }
    }
    
    // ============ THEME ============
    
    fun saveTheme(themeMode: String) {
        settings.putString(KEY_THEME, themeMode)
    }
    
    fun loadTheme(): String {
        return settings.getStringOrNull(KEY_THEME) ?: "DEFAULT"
    }
    
    // ============ BACKUP / RESTORE ============
    
    fun createBackup(): LedgerBackup {
        return LedgerBackup(
            transactions = loadTransactions(),
            recurringTemplates = loadRecurringTemplates(),
            settings = loadAppSettings()
        )
    }
    
    fun restoreFromBackup(backup: LedgerBackup) {
        saveTransactions(backup.transactions)
        saveRecurringTemplates(backup.recurringTemplates)
        saveAppSettings(backup.settings)
    }
    
    fun mergeWithBackup(remote: LedgerBackup): Boolean {
        return try {
            // Merge transactions
            val localTx = loadTransactions().associateBy { it.id }.toMutableMap()
            remote.transactions.forEach { rtx ->
                val ltx = localTx[rtx.id]
                if (ltx == null || rtx.lastModified > ltx.lastModified) {
                    localTx[rtx.id] = rtx
                }
            }
            saveTransactions(localTx.values.toList())
            
            // Merge recurring templates
            val localRec = loadRecurringTemplates().associateBy { it.id }.toMutableMap()
            remote.recurringTemplates.forEach { rr ->
                val lr = localRec[rr.id]
                if (lr == null || rr.lastModified > lr.lastModified) {
                    localRec[rr.id] = rr
                }
            }
            saveRecurringTemplates(localRec.values.toList())
            
            // For settings, we usually take the latest modification or keep local?
            // Let's just keep remote if it's "newer" in a real app, but here we don't have settings timestamp.
            // For now, keep local settings.
            
            true
        } catch (e: Exception) {
            false
        }
    }

    
    fun exportBackupJson(): String {
        val backup = createBackup()
        return json.encodeToString(backup)
    }
    
    fun importBackupJson(jsonString: String): Boolean {
        return try {
            val backup = json.decodeFromString<LedgerBackup>(jsonString)
            restoreFromBackup(backup)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // ============ CLEAR ============
    
    fun clearAll() {
        settings.clear()
    }
}
