package com.example.ledger.data

import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.w3c.files.FileReader
import org.w3c.files.get
import kotlin.coroutines.resume

/**
 * JS implementation of PlatformFileHandler.
 */
actual class PlatformFileHandler actual constructor() {

    actual suspend fun pickJsonFile(): String? = suspendCancellableCoroutine { continuation ->
        val input = document.createElement("input") as HTMLInputElement
        input.type = "file"
        input.accept = "application/json"
        
        input.onchange = {
            val file = input.files?.get(0)
            if (file != null) {
                val reader = FileReader()
                reader.onload = { event ->
                    // event.target is dynamic in JS
                    val target: dynamic = event.target
                    val content = target.result as String
                    continuation.resume(content)
                }
                reader.onerror = {
                    continuation.resume(null)
                }
                reader.readAsText(file)
            } else {
                continuation.resume(null)
            }
        }
        
        input.click()
        
        continuation.invokeOnCancellation { 
            // Cleanup if needed
        }
    }

    actual suspend fun saveJsonFile(content: String, fileName: String): Boolean {
        try {
            // JS specific: arrayOf works for Blob parts
            val blob = Blob(arrayOf(content), BlobPropertyBag(type = "application/json"))
            val url = URL.createObjectURL(blob)
            val a = document.createElement("a") as HTMLAnchorElement
            a.href = url
            a.download = fileName
            document.body?.appendChild(a)
            a.click()
            document.body?.removeChild(a)
            URL.revokeObjectURL(url)
            return true
        } catch (e: Exception) {
            println("Error saving file: ${e.message}")
            return false
        }
    }
}
