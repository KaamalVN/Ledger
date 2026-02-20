package com.example.ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        com.example.ledger.data.GoogleAuthProvider.setActivity(this)
        com.example.ledger.data.PlatformFileHandler.setActivity(this)
        
        // Register Google Sign-In launcher
        val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
                com.example.ledger.data.GoogleAuthProvider.handleSignInResult(task)
            } else {
                com.example.ledger.data.GoogleAuthProvider.handleSignInResult(null)
            }
        }
        com.example.ledger.data.GoogleAuthProvider.setLauncher(signInLauncher)

        setContent {
            App()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                val content = contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                com.example.ledger.data.PlatformFileHandler.handlePickResult(content)
            } else {
                com.example.ledger.data.PlatformFileHandler.handlePickResult(null)
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}