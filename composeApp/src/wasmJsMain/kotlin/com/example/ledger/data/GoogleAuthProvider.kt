package com.example.ledger.data

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.js.JsAny

@JsFun("(clientId) => { if (window.googleAuthBridge) window.googleAuthBridge.init(clientId); }")
private external fun initAuthBridge(clientId: String)

@JsFun("(callback) => { if (window.googleAuthBridge) window.googleAuthBridge.requestToken(callback); }")
private external fun requestTokenFromBridge(callback: (JsAny?) -> Unit)

/**
 * WasmJS implementation of GoogleAuthProvider using Google Identity Services.
 */
actual class GoogleAuthProvider actual constructor() {
    private var accessToken: String? = null
    private var email: String? = null
    private var displayName: String? = null
    
    init {
        // Initialize bridge with Client ID. 
        initAuthBridge("YOUR_WEB_CLIENT_ID.apps.googleusercontent.com")
    }
    
    actual fun isSignedIn(): Boolean = accessToken != null
    
    actual suspend fun getAccessToken(): String? = accessToken
    
    actual fun getUserEmail(): String? = email
    
    actual fun getUserDisplayName(): String? = displayName
    
    actual fun signOut() {
        accessToken = null
        email = null
        displayName = null
    }

    actual suspend fun signIn(): Boolean = suspendCancellableCoroutine { continuation ->
        try {
            requestTokenFromBridge { token ->
                val tokenStr = token?.toString()
                if (tokenStr != null) {
                    accessToken = tokenStr
                    continuation.resume(true)
                } else {
                    continuation.resume(false)
                }
            }
        } catch (e: Exception) {
            continuation.resume(false)
        }
    }
}
