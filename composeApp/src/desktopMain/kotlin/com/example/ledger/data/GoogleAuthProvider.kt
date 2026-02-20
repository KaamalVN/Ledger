package com.example.ledger.data

/**
 * Desktop implementation of GoogleAuthProvider.
 * Stub implementation.
 */
actual class GoogleAuthProvider actual constructor() {
    private var accessToken: String? = null
    private var email: String? = null
    private var displayName: String? = null
    
    actual fun isSignedIn(): Boolean = accessToken != null
    
    actual suspend fun getAccessToken(): String? = accessToken
    
    actual fun getUserEmail(): String? = email
    
    actual fun getUserDisplayName(): String? = displayName
    
    actual fun signOut() {
        accessToken = null
        email = null
        displayName = null
    }

    actual suspend fun signIn(): Boolean {
        // Desktop login often requires an embedded browser or localhost listener
        return false
    }
}
