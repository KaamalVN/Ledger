package com.example.ledger.data

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Google Drive service for backing up and restoring Ledger data.
 * Uses the Google Drive REST API v3 with OAuth2 Bearer tokens.
 *
 * The actual OAuth2 flow (sign-in, token management) is handled
 * by platform-specific implementations via expect/actual.
 */
class GoogleDriveService {
    
    companion object {
        private const val DRIVE_BASE_URL = "https://www.googleapis.com/drive/v3"
        private const val DRIVE_UPLOAD_URL = "https://www.googleapis.com/upload/drive/v3"
        private const val APP_FOLDER = "appDataFolder"
        private const val BACKUP_FILE_NAME = "ledger_backup.json"
        private const val BACKUP_MIME_TYPE = "application/json"
    }
    
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
        encodeDefaults = true
    }
    
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 10000
        }
        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) {
                    println("GoogleDriveService: $message")
                }
            }
        }
    }
    
    /**
     * Upload a backup to Google Drive appData folder.
     * If a backup already exists, it will be updated.
     */
    suspend fun uploadBackup(accessToken: String, backupJson: String): Result<String> {
        return try {
            // First, check if a backup file already exists
            val existingFileId = findBackupFile(accessToken)
            
            if (existingFileId != null) {
                // Update existing file
                updateFile(accessToken, existingFileId, backupJson)
            } else {
                // Create new file
                createFile(accessToken, backupJson)
            }
        } catch (e: Exception) {
            println("GoogleDriveService: Upload error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Download the latest backup from Google Drive.
     */
    suspend fun downloadBackup(accessToken: String): Result<String> {
        return try {
            val fileId = findBackupFile(accessToken)
                ?: return Result.failure(Exception("No backup found on Google Drive"))
            
            val response = httpClient.get("$DRIVE_BASE_URL/files/$fileId") {
                parameter("alt", "media")
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.bodyAsText())
            } else {
                Result.failure(Exception("Failed to download backup: ${response.status}"))
            }
        } catch (e: Exception) {
            println("GoogleDriveService: Download error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Find existing backup file in appData folder.
     */
    private suspend fun findBackupFile(accessToken: String): String? {
        return try {
            val response = httpClient.get("$DRIVE_BASE_URL/files") {
                parameter("spaces", APP_FOLDER)
                parameter("q", "name='$BACKUP_FILE_NAME'")
                parameter("fields", "files(id, name, modifiedTime)")
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            
            if (response.status == HttpStatusCode.OK) {
                val fileList = json.decodeFromString<DriveFileList>(response.bodyAsText())
                fileList.files.firstOrNull()?.id
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Create a new backup file in Google Drive appData folder.
     */
    private suspend fun createFile(accessToken: String, content: String): Result<String> {
        val metadata = """{"name":"$BACKUP_FILE_NAME","parents":["$APP_FOLDER"]}"""
        
        val response = httpClient.post("$DRIVE_UPLOAD_URL/files") {
            parameter("uploadType", "multipart")
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("metadata", metadata, Headers.build {
                            append(HttpHeaders.ContentType, "application/json; charset=UTF-8")
                        })
                        append("file", content, Headers.build {
                            append(HttpHeaders.ContentType, BACKUP_MIME_TYPE)
                        })
                    }
                )
            )
        }
        
        return if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
            Result.success("Backup uploaded successfully")
        } else {
            Result.failure(Exception("Failed to create backup: ${response.status}"))
        }
    }
    
    /**
     * Update an existing backup file on Google Drive.
     */
    private suspend fun updateFile(accessToken: String, fileId: String, content: String): Result<String> {
        val response = httpClient.patch("$DRIVE_UPLOAD_URL/files/$fileId") {
            parameter("uploadType", "media")
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            contentType(ContentType.Application.Json)
            setBody(content)
        }
        
        return if (response.status == HttpStatusCode.OK) {
            Result.success("Backup updated successfully")
        } else {
            Result.failure(Exception("Failed to update backup: ${response.status}"))
        }
    }
    
    /**
     * Delete the backup from Google Drive.
     */
    suspend fun deleteBackup(accessToken: String): Result<String> {
        return try {
            val fileId = findBackupFile(accessToken)
                ?: return Result.failure(Exception("No backup to delete"))
            
            val response = httpClient.delete("$DRIVE_BASE_URL/files/$fileId") {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            
            if (response.status == HttpStatusCode.NoContent || response.status == HttpStatusCode.OK) {
                Result.success("Backup deleted")
            } else {
                Result.failure(Exception("Failed to delete backup: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get info about the existing backup (last modified time, etc.)
     */
    suspend fun getBackupInfo(accessToken: String): DriveFileMetadata? {
        return try {
            val response = httpClient.get("$DRIVE_BASE_URL/files") {
                parameter("spaces", APP_FOLDER)
                parameter("q", "name='$BACKUP_FILE_NAME'")
                parameter("fields", "files(id, name, mimeType, modifiedTime)")
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
            
            if (response.status == HttpStatusCode.OK) {
                val fileList = json.decodeFromString<DriveFileList>(response.bodyAsText())
                fileList.files.firstOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            println("GoogleDriveService: getBackupInfo error: ${e.message}")
            e.printStackTrace()
            null
        }
    }
    
    fun close() {
        httpClient.close()
    }
}
