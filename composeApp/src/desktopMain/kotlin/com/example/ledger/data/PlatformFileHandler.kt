package com.example.ledger.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

actual class PlatformFileHandler actual constructor() {
    actual suspend fun pickJsonFile(): String? = withContext(Dispatchers.IO) {
        val chooser = JFileChooser().apply {
            fileFilter = FileNameExtensionFilter("JSON files", "json")
            dialogTitle = "Select Backup File"
        }
        val result = chooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            chooser.selectedFile.readText()
        } else {
            null
        }
    }

    actual suspend fun saveJsonFile(content: String, fileName: String): Boolean = withContext(Dispatchers.IO) {
        val chooser = JFileChooser().apply {
            selectedFile = File(fileName)
            dialogTitle = "Save Backup File"
        }
        val result = chooser.showSaveDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val file = chooser.selectedFile
            file.writeText(content)
            true
        } else {
            false
        }
    }
}
