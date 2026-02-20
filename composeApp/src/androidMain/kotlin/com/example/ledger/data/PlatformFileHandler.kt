package com.example.ledger.data

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.core.content.FileProvider
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference

actual class PlatformFileHandler {
    companion object {
        private var activityRef: WeakReference<ComponentActivity>? = null
        private var pendingPick: CompletableDeferred<String?>? = null
        
        fun setActivity(activity: ComponentActivity) {
            activityRef = WeakReference(activity)
        }

        fun handlePickResult(content: String?) {
            pendingPick?.complete(content)
            pendingPick = null
        }
    }

    actual suspend fun pickJsonFile(): String? = withContext(Dispatchers.Main) {
        val activity = activityRef?.get() ?: return@withContext null
        
        pendingPick = CompletableDeferred()
        
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/json"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        
        activity.startActivityForResult(intent, 1001)
        
        pendingPick?.await()
    }

    actual suspend fun saveJsonFile(content: String, fileName: String): Boolean = withContext(Dispatchers.IO) {
        val activity = activityRef?.get() ?: return@withContext false
        
        try {
            println("PlatformFileHandler: Saving JSON to temp file: $fileName")
            val cacheFile = File(activity.cacheDir, fileName)
            cacheFile.writeText(content)
            
            val contentUri: Uri = FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                cacheFile
            )

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "application/json"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            withContext(Dispatchers.Main) {
                val shareIntent = Intent.createChooser(sendIntent, "Export Ledger Data")
                activity.startActivity(shareIntent)
            }
            true
        } catch (e: Exception) {
            println("PlatformFileHandler: Export error: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}
