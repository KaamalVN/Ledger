package com.example.ledger

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform