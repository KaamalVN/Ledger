package com.example.ledger.ui.theme

import androidx.compose.ui.graphics.Color

// Material Design 3 Color System - Professional Finance App
// Based on trust-inducing blue primary with accessible contrast ratios
// Following M3 Expressive guidelines for finance applications

// ============ LIGHT THEME (Default) ============

// Primary - Professional blue for trust and reliability
val Primary = Color(0xFF1565C0)          // Blue 800
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFD1E4FF)
val OnPrimaryContainer = Color(0xFF001D36)

// Secondary - Warm purple accent
val Secondary = Color(0xFF5C5B7D)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFE2DFFF)
val OnSecondaryContainer = Color(0xFF191836)

// Tertiary - Complementary teal
val Tertiary = Color(0xFF006A6A)
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFF9CF1F0)
val OnTertiaryContainer = Color(0xFF002020)

// Error - Refined red for expenses/alerts
val Error = Color(0xFFBA1A1A)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF410002)

// Surface colors - Clean with depth
val Surface = Color(0xFFFDFCFF)
val OnSurface = Color(0xFF1A1C1E)
val SurfaceVariant = Color(0xFFDFE2EB)
val OnSurfaceVariant = Color(0xFF43474E)

val SurfaceContainer = Color(0xFFEEF0F4)
val SurfaceContainerHigh = Color(0xFFE8EAEE)
val SurfaceContainerHighest = Color(0xFFE2E4E8)
val SurfaceContainerLow = Color(0xFFF4F6FA)
val SurfaceContainerLowest = Color(0xFFFFFFFF)

val SurfaceBright = Color(0xFFFDFCFF)
val SurfaceDim = Color(0xFFDADCE0)

// Outline
val Outline = Color(0xFF73777F)
val OutlineVariant = Color(0xFFC3C6CF)

// Background
val Background = Color(0xFFFDFCFF)
val OnBackground = Color(0xFF1A1C1E)

// Scrim
val Scrim = Color(0xFF000000)

// Inverse
val InverseSurface = Color(0xFF2F3033)
val InverseOnSurface = Color(0xFFF1F0F4)
val InversePrimary = Color(0xFFA0CAFF)

// ============ DARK THEME (Default) ============

val PrimaryDark = Color(0xFFA0CAFF)
val OnPrimaryDark = Color(0xFF003258)
val PrimaryContainerDark = Color(0xFF00497D)
val OnPrimaryContainerDark = Color(0xFFD1E4FF)

val SecondaryDark = Color(0xFFC5C3EA)
val OnSecondaryDark = Color(0xFF2D2D4C)
val SecondaryContainerDark = Color(0xFF444364)
val OnSecondaryContainerDark = Color(0xFFE2DFFF)

val TertiaryDark = Color(0xFF80D5D4)
val OnTertiaryDark = Color(0xFF003737)
val TertiaryContainerDark = Color(0xFF004F4F)
val OnTertiaryContainerDark = Color(0xFF9CF1F0)

val ErrorDark = Color(0xFFFFB4AB)
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)

val SurfaceDark = Color(0xFF121316)
val OnSurfaceDark = Color(0xFFE2E4E8)
val SurfaceVariantDark = Color(0xFF43474E)
val OnSurfaceVariantDark = Color(0xFFC3C6CF)

val SurfaceContainerDark = Color(0xFF1E2022)
val SurfaceContainerHighDark = Color(0xFF292A2D)
val SurfaceContainerHighestDark = Color(0xFF343538)
val SurfaceContainerLowDark = Color(0xFF1A1C1E)
val SurfaceContainerLowestDark = Color(0xFF0D0E11)

val SurfaceBrightDark = Color(0xFF38393C)
val SurfaceDimDark = Color(0xFF121316)

val OutlineDark = Color(0xFF8D9199)
val OutlineVariantDark = Color(0xFF43474E)

val BackgroundDark = Color(0xFF121316)
val OnBackgroundDark = Color(0xFFE2E4E8)

val ScrimDark = Color(0xFF000000)

val InverseSurfaceDark = Color(0xFFE2E4E8)
val InverseOnSurfaceDark = Color(0xFF2F3033)
val InversePrimaryDark = Color(0xFF1565C0)

// ============ SEMANTIC COLORS ============

// Income - Professional green
val IncomeColor = Color(0xFF2E7D32)          // Green 800
val IncomeColorDark = Color(0xFF81C784)       // Green 300
val IncomeContainer = Color(0xFFDCEDC8)       // Green 100
val IncomeContainerDark = Color(0xFF1B5E20)   // Green 900

// Expense - Professional red
val ExpenseColor = Color(0xFFC62828)          // Red 800
val ExpenseColorDark = Color(0xFFEF9A9A)      // Red 200
val ExpenseContainer = Color(0xFFFFCDD2)      // Red 100
val ExpenseContainerDark = Color(0xFFB71C1C)  // Red 900

// Warning/Due - Amber
val WarningColor = Color(0xFFF57C00)          // Orange 800
val WarningColorDark = Color(0xFFFFB74D)      // Orange 300
val WarningContainer = Color(0xFFFFE0B2)      // Orange 100
val WarningContainerDark = Color(0xFFE65100)  // Orange 900


// ============================================================
//  THEME: Monolithic Anodized Slabwork (Dark Industrial)
//  Derived from DESIGN.md Design 1
// ============================================================

object MonolithColors {
    // Core palette
    val Background = Color(0xFF0A0A0A)
    val Surface = Color(0xFF161616)
    val SurfaceLight = Color(0xFF1E1E1E)
    val Accent = Color(0xFF3B82F6)          // Vibrant blue
    val TextMain = Color(0xFFE5E5E5)
    val TextDim = Color(0xFF737373)
    val Border = Color(0x14FFFFFF)           // 8% white
    val Chamfer = Color(0x1FFFFFFF)          // 12% white

    // M3 mapping
    val Primary = Color(0xFF3B82F6)
    val OnPrimary = Color(0xFF0A0A0A)
    val PrimaryContainer = Color(0xFF1E3A5F)
    val OnPrimaryContainer = Color(0xFFBDD7FF)

    val Secondary = Color(0xFF737373)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFF2A2A2A)
    val OnSecondaryContainer = Color(0xFFB0B0B0)

    val TertiaryColor = Color(0xFF10B981)      // Emerald green
    val OnTertiary = Color(0xFF0A0A0A)
    val TertiaryContainer = Color(0xFF0A2E1C)
    val OnTertiaryContainer = Color(0xFF6EE7B7)

    val ErrorColor = Color(0xFFF43F5E)         // Rose-500
    val OnError = Color(0xFF0A0A0A)
    val ErrorContainerColor = Color(0xFF3D0E16)
    val OnErrorContainer = Color(0xFFFDA4AF)

    val SurfaceColor = Color(0xFF0A0A0A)
    val OnSurface = Color(0xFFE5E5E5)
    val SurfaceVariant = Color(0xFF1E1E1E)
    val OnSurfaceVariant = Color(0xFF737373)

    val SurfaceContainer = Color(0xFF161616)
    val SurfaceContainerHigh = Color(0xFF1E1E1E)
    val SurfaceContainerHighest = Color(0xFF262626)
    val SurfaceContainerLow = Color(0xFF121212)
    val SurfaceContainerLowest = Color(0xFF090909)

    val SurfaceBright = Color(0xFF2A2A2A)
    val SurfaceDim = Color(0xFF0A0A0A)

    val Outline = Color(0xFF404040)
    val OutlineVariant = Color(0xFF262626)

    val OnBackground = Color(0xFFE5E5E5)

    val Scrim = Color(0xFF000000)

    val InverseSurface = Color(0xFFE5E5E5)
    val InverseOnSurface = Color(0xFF161616)
    val InversePrimary = Color(0xFF1D4ED8)

    // Semantic
    val IncomeColor = Color(0xFF10B981)
    val IncomeContainer = Color(0xFF0A2E1C)
    val ExpenseColor = Color(0xFFF43F5E)
    val ExpenseContainer = Color(0xFF3D0E16)
    val WarningColor = Color(0xFFF59E0B)
    val WarningContainer = Color(0xFF3D2800)
}


// ============================================================
//  THEME: Debossed Heavy-Pulp Relief (Light Neumorphic Paper)
//  Derived from DESIGN.md Design 2
// ============================================================

object PulpColors {
    // Core palette
    val PaperBg = Color(0xFFE5E5DF)
    val PaperGrain = Color(0xFFDADAD2)
    val Accent = Color(0xFF2D312E)
    val TextMain = Color(0xFF3A3A35)
    val TextMuted = Color(0xFF7A7A70)
    val Positive = Color(0xFF4A6741)
    val Negative = Color(0xFF914D4D)

    // M3 mapping
    val Primary = Color(0xFF4A6741)            // Earthy green
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFFC8D8C0)
    val OnPrimaryContainer = Color(0xFF1B3313)

    val Secondary = Color(0xFF6B6B60)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFFD4D4CA)
    val OnSecondaryContainer = Color(0xFF3A3A35)

    val TertiaryColor = Color(0xFF7A6B5D)       // Warm brown
    val OnTertiary = Color(0xFFFFFFFF)
    val TertiaryContainer = Color(0xFFD9CCBD)
    val OnTertiaryContainer = Color(0xFF3A3020)

    val ErrorColor = Color(0xFF914D4D)
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainerColor = Color(0xFFE5C5C5)
    val OnErrorContainer = Color(0xFF4A1F1F)

    val Surface = Color(0xFFE5E5DF)
    val OnSurface = Color(0xFF3A3A35)
    val SurfaceVariant = Color(0xFFDADAD2)
    val OnSurfaceVariant = Color(0xFF7A7A70)

    val SurfaceContainer = Color(0xFFDDDDD7)
    val SurfaceContainerHigh = Color(0xFFD5D5CF)
    val SurfaceContainerHighest = Color(0xFFCFCFC9)
    val SurfaceContainerLow = Color(0xFFE0E0DA)
    val SurfaceContainerLowest = Color(0xFFEAEAE4)

    val SurfaceBright = Color(0xFFEBEBE5)
    val SurfaceDim = Color(0xFFD6D6D0)

    val Outline = Color(0xFF9A9A90)
    val OutlineVariant = Color(0xFFC0C0B8)

    val Background = Color(0xFFE5E5DF)
    val OnBackground = Color(0xFF3A3A35)

    val Scrim = Color(0xFF000000)

    val InverseSurface = Color(0xFF3A3A35)
    val InverseOnSurface = Color(0xFFE5E5DF)
    val InversePrimary = Color(0xFF8FBF80)

    // Semantic
    val IncomeColor = Color(0xFF4A6741)
    val IncomeContainer = Color(0xFFC8D8C0)
    val ExpenseColor = Color(0xFF914D4D)
    val ExpenseContainer = Color(0xFFE5C5C5)
    val WarningColor = Color(0xFFA67C40)
    val WarningContainer = Color(0xFFE5D4B8)
}
