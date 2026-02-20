package com.example.ledger.data

/**
 * iOS implementation of GoogleAuthProvider.
 * 
 * NOTE: Full Google Sign-In on iOS requires the GoogleSignIn SDK 
 * configured via CocoaPods/SPM. This is a stub implementation.
 */
actual class GoogleAuthProvider actual constructor() {
    private var accessToken: String? = null
    private var email: String? = null
    private var displayName: String? = null
    
    actual fun isSignedIn(): Boolean = accessToken != null
    
    actual suspend fun getAccessToken(): String? = accessToken
    
    actual fun getUserEmail(): String? = email
    
    actual fun getUserDisplayName(): String? = displayName
    
    fun setAuthState(token: String?, userEmail: String?, name: String?) {
        accessToken = token
        email = userEmail
        displayName = name
    }
    
    fun signOut() {
        accessToken = null
        email = null
        displayName = null
    }

    actual suspend fun signIn(): Boolean {
        // iOS implementation would use GoogleSignIn SDK
        return false
    }
}
