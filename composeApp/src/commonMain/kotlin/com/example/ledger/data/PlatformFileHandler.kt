package com.example.ledger.data

/**
 * Platform-independent file handling for backups.
 */
expect class PlatformFileHandler() {
    /**
     * Pick a JSON file from storage and return its content.
     */
    suspend fun pickJsonFile(): String?

    /**
     * Save a JSON string to a file in storage.
     */
    suspend fun saveJsonFile(content: String, fileName: String): Boolean
}
