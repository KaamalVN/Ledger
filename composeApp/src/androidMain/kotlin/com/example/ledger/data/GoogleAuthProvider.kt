package com.example.ledger.data

import android.accounts.Account
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

/**
 * Android implementation of GoogleAuthProvider using Google Sign-In API.
 */
actual class GoogleAuthProvider actual constructor() {
    companion object {
        private var activityRef: WeakReference<ComponentActivity>? = null
        private var signInLauncher: ActivityResultLauncher<android.content.Intent>? = null
        private var pendingSignIn: CompletableDeferred<GoogleSignInAccount?>? = null
        
        fun setActivity(activity: ComponentActivity) {
            activityRef = WeakReference(activity)
        }

        fun setLauncher(launcher: ActivityResultLauncher<android.content.Intent>) {
            signInLauncher = launcher
        }

        fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
            if (task == null) {
                pendingSignIn?.complete(null)
                return
            }
            try {
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                pendingSignIn?.complete(account)
            } catch (e: Exception) {
                e.printStackTrace()
                pendingSignIn?.complete(null)
            } finally {
                pendingSignIn = null
            }
        }
    }

    private var accessToken: String? = null
    private var email: String? = null
    private var displayName: String? = null
    
    actual fun isSignedIn(): Boolean {
        val activity = activityRef?.get() ?: return accessToken != null
        return accessToken != null || GoogleSignIn.getLastSignedInAccount(activity) != null
    }
    
    actual suspend fun getAccessToken(): String? {
        if (accessToken != null) {
            println("GoogleAuthProvider: Using cached access token")
            return accessToken
        }
        
        // Try to refresh token silently if we have an account
        val activity = activityRef?.get() ?: return null
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if (account != null) {
            println("GoogleAuthProvider: Attempting silent token refresh for ${account.email}")
            val driveScope = "https://www.googleapis.com/auth/drive.appdata"
            val emailStr = account.email ?: return null
            try {
                accessToken = withContext(Dispatchers.IO) {
                    val androidAccount = Account(emailStr, "com.google")
                    GoogleAuthUtil.getToken(activity, androidAccount, "oauth2:$driveScope")
                }
                email = account.email
                displayName = account.displayName
                println("GoogleAuthProvider: Token refresh successful")
            } catch (e: Exception) {
                println("GoogleAuthProvider: Token refresh failed: ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("GoogleAuthProvider: No account found for token refresh")
        }
        return accessToken
    }
    
    actual fun getUserEmail(): String? {
        if (email != null) return email
        val activity = activityRef?.get() ?: return null
        return GoogleSignIn.getLastSignedInAccount(activity)?.email
    }
    
    actual fun getUserDisplayName(): String? {
        if (displayName != null) return displayName
        val activity = activityRef?.get() ?: return null
        return GoogleSignIn.getLastSignedInAccount(activity)?.displayName
    }
    
    actual suspend fun signIn(): Boolean = withContext(Dispatchers.Main) {
        val activity = activityRef?.get() ?: return@withContext false
        
        val driveScope = "https://www.googleapis.com/auth/drive.appdata"
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(driveScope))
            .build()
            
        val signInClient = GoogleSignIn.getClient(activity, gso)
        
        try {
            // 1. Try last signed in account
            val lastAccount = GoogleSignIn.getLastSignedInAccount(activity)
            if (lastAccount != null) {
                return@withContext processSuccessfulSignIn(activity, lastAccount)
            }
            
            // 2. Try silent sign in
            val silentResult = signInClient.silentSignIn()
            if (silentResult.isSuccessful) {
                val res = silentResult.result
                return@withContext processSuccessfulSignIn(activity, res)
            }
            
            // 3. Trigger full UI sign-in
            pendingSignIn = CompletableDeferred()
            signInLauncher?.launch(signInClient.signInIntent)
            val res = pendingSignIn?.await()
            if (res != null) {
                return@withContext processSuccessfulSignIn(activity, res)
            }
            
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun processSuccessfulSignIn(activity: ComponentActivity, account: GoogleSignInAccount): Boolean {
        val driveScope = "https://www.googleapis.com/auth/drive.appdata"
        val emailStr = account.email ?: return false
        email = account.email
        displayName = account.displayName
        
        return try {
            accessToken = withContext(Dispatchers.IO) {
                val androidAccount = Account(emailStr, "com.google")
                GoogleAuthUtil.getToken(activity, androidAccount, "oauth2:$driveScope")
            }
            true
        } catch (recoverable: com.google.android.gms.auth.UserRecoverableAuthException) {
            // In a real app, you'd trigger the intent: activity.startActivityForResult(recoverable.intent, ...)
            // For now, we print and return false.
            recoverable.printStackTrace()
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    actual fun signOut() {
        val activity = activityRef?.get() ?: return
        
        accessToken = null
        email = null
        displayName = null
        
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(activity, gso).signOut().addOnCompleteListener {
            // Signed out locally
        }
    }
}
