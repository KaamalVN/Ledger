package com.example.ledger.data

/**
 * Platform-specific Google Sign-In handler.
 * Each platform implements this to handle OAuth2 authentication.
 */
expect class GoogleAuthProvider() {
    /**
     * Check if user is currently signed in.
     */
    fun isSignedIn(): Boolean
    
    /**
     * Get the current OAuth2 access token.
     * Returns null if not signed in or token expired.
     */
    suspend fun getAccessToken(): String?
    
    /**
     * Get the signed-in user's email address.
     */
    fun getUserEmail(): String?
    
    /**
     * Get the signed-in user's display name.
     */
    fun getUserDisplayName(): String?

    /**
     * Trigger the sign-in flow.
     * Returns true if successful.
     */
    suspend fun signIn(): Boolean

    /**
     * Sign out the user.
     */
    fun signOut()
}
