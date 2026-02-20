package com.example.ledger.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Material Expressive Shapes - Varied and Dynamic
val LedgerShapes = Shapes(
    // Extra Small - For small elements like chips, badges
    extraSmall = RoundedCornerShape(8.dp),
    
    // Small - For buttons, small cards
    small = RoundedCornerShape(12.dp),
    
    // Medium - For standard cards and containers
    medium = RoundedCornerShape(16.dp),
    
    // Large - For prominent cards
    large = RoundedCornerShape(24.dp),
    
    // Extra Large - For hero elements and major surfaces
    extraLarge = RoundedCornerShape(32.dp)
)
