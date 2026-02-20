package com.example.ledger.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ============ THEME MODE ============

enum class AppThemeMode(val displayName: String, val description: String) {
    DEFAULT("Material Blue", "Clean Material Design 3 with professional blue"),
    MONOLITH("Anodized Slabwork", "Dark industrial, precision-machined aesthetic"),
    PULP("Heavy-Pulp Relief", "Warm paper-like neumorphic light theme")
}

// Composition local to track current theme mode
val LocalAppThemeMode = staticCompositionLocalOf { AppThemeMode.DEFAULT }

// ============ EXTENDED COLORS ============

data class ExtendedColors(
    val income: Color,
    val onIncome: Color,
    val incomeContainer: Color,
    val onIncomeContainer: Color,
    val expense: Color,
    val onExpense: Color,
    val expenseContainer: Color,
    val onExpenseContainer: Color,
    val warning: Color,
    val warningContainer: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        income = IncomeColor,
        onIncome = Color.White,
        incomeContainer = IncomeContainer,
        onIncomeContainer = Color(0xFF1B5E20),
        expense = ExpenseColor,
        onExpense = Color.White,
        expenseContainer = ExpenseContainer,
        onExpenseContainer = Color(0xFFB71C1C),
        warning = WarningColor,
        warningContainer = WarningContainer
    )
}


// ============ DEFAULT THEME SCHEMES ============

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainerLowest = SurfaceContainerLowest,
    
    surfaceBright = SurfaceBright,
    surfaceDim = SurfaceDim,
    
    outline = Outline,
    outlineVariant = OutlineVariant,
    
    background = Background,
    onBackground = OnBackground,
    
    scrim = Scrim,
    
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    inversePrimary = InversePrimary,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    
    surfaceBright = SurfaceBrightDark,
    surfaceDim = SurfaceDimDark,
    
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    
    scrim = ScrimDark,
    
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    inversePrimary = InversePrimaryDark,
)

private val LightExtendedColors = ExtendedColors(
    income = IncomeColor,
    onIncome = Color.White,
    incomeContainer = IncomeContainer,
    onIncomeContainer = Color(0xFF1B5E20),
    expense = ExpenseColor,
    onExpense = Color.White,
    expenseContainer = ExpenseContainer,
    onExpenseContainer = Color(0xFFB71C1C),
    warning = WarningColor,
    warningContainer = WarningContainer
)

private val DarkExtendedColors = ExtendedColors(
    income = IncomeColorDark,
    onIncome = Color(0xFF1B5E20),
    incomeContainer = IncomeContainerDark,
    onIncomeContainer = Color(0xFFDCEDC8),
    expense = ExpenseColorDark,
    onExpense = Color(0xFFB71C1C),
    expenseContainer = ExpenseContainerDark,
    onExpenseContainer = Color(0xFFFFCDD2),
    warning = WarningColorDark,
    warningContainer = WarningContainerDark
)


// ============ MONOLITH (ANODIZED SLABWORK) SCHEME ============

private val MonolithColorScheme = darkColorScheme(
    primary = MonolithColors.Primary,
    onPrimary = MonolithColors.OnPrimary,
    primaryContainer = MonolithColors.PrimaryContainer,
    onPrimaryContainer = MonolithColors.OnPrimaryContainer,
    
    secondary = MonolithColors.Secondary,
    onSecondary = MonolithColors.OnSecondary,
    secondaryContainer = MonolithColors.SecondaryContainer,
    onSecondaryContainer = MonolithColors.OnSecondaryContainer,
    
    tertiary = MonolithColors.TertiaryColor,
    onTertiary = MonolithColors.OnTertiary,
    tertiaryContainer = MonolithColors.TertiaryContainer,
    onTertiaryContainer = MonolithColors.OnTertiaryContainer,
    
    error = MonolithColors.ErrorColor,
    onError = MonolithColors.OnError,
    errorContainer = MonolithColors.ErrorContainerColor,
    onErrorContainer = MonolithColors.OnErrorContainer,
    
    surface = MonolithColors.SurfaceColor,
    onSurface = MonolithColors.OnSurface,
    surfaceVariant = MonolithColors.SurfaceVariant,
    onSurfaceVariant = MonolithColors.OnSurfaceVariant,
    
    surfaceContainer = MonolithColors.SurfaceContainer,
    surfaceContainerHigh = MonolithColors.SurfaceContainerHigh,
    surfaceContainerHighest = MonolithColors.SurfaceContainerHighest,
    surfaceContainerLow = MonolithColors.SurfaceContainerLow,
    surfaceContainerLowest = MonolithColors.SurfaceContainerLowest,
    
    surfaceBright = MonolithColors.SurfaceBright,
    surfaceDim = MonolithColors.SurfaceDim,
    
    outline = MonolithColors.Outline,
    outlineVariant = MonolithColors.OutlineVariant,
    
    background = MonolithColors.Background,
    onBackground = MonolithColors.OnBackground,
    
    scrim = MonolithColors.Scrim,
    
    inverseSurface = MonolithColors.InverseSurface,
    inverseOnSurface = MonolithColors.InverseOnSurface,
    inversePrimary = MonolithColors.InversePrimary,
)

private val MonolithExtendedColors = ExtendedColors(
    income = MonolithColors.IncomeColor,
    onIncome = MonolithColors.Background,
    incomeContainer = MonolithColors.IncomeContainer,
    onIncomeContainer = Color(0xFF6EE7B7),
    expense = MonolithColors.ExpenseColor,
    onExpense = MonolithColors.Background,
    expenseContainer = MonolithColors.ExpenseContainer,
    onExpenseContainer = Color(0xFFFDA4AF),
    warning = MonolithColors.WarningColor,
    warningContainer = MonolithColors.WarningContainer
)


// ============ PULP (DEBOSSED HEAVY-PULP RELIEF) SCHEME ============

private val PulpColorScheme = lightColorScheme(
    primary = PulpColors.Primary,
    onPrimary = PulpColors.OnPrimary,
    primaryContainer = PulpColors.PrimaryContainer,
    onPrimaryContainer = PulpColors.OnPrimaryContainer,
    
    secondary = PulpColors.Secondary,
    onSecondary = PulpColors.OnSecondary,
    secondaryContainer = PulpColors.SecondaryContainer,
    onSecondaryContainer = PulpColors.OnSecondaryContainer,
    
    tertiary = PulpColors.TertiaryColor,
    onTertiary = PulpColors.OnTertiary,
    tertiaryContainer = PulpColors.TertiaryContainer,
    onTertiaryContainer = PulpColors.OnTertiaryContainer,
    
    error = PulpColors.ErrorColor,
    onError = PulpColors.OnError,
    errorContainer = PulpColors.ErrorContainerColor,
    onErrorContainer = PulpColors.OnErrorContainer,
    
    surface = PulpColors.Surface,
    onSurface = PulpColors.OnSurface,
    surfaceVariant = PulpColors.SurfaceVariant,
    onSurfaceVariant = PulpColors.OnSurfaceVariant,
    
    surfaceContainer = PulpColors.SurfaceContainer,
    surfaceContainerHigh = PulpColors.SurfaceContainerHigh,
    surfaceContainerHighest = PulpColors.SurfaceContainerHighest,
    surfaceContainerLow = PulpColors.SurfaceContainerLow,
    surfaceContainerLowest = PulpColors.SurfaceContainerLowest,
    
    surfaceBright = PulpColors.SurfaceBright,
    surfaceDim = PulpColors.SurfaceDim,
    
    outline = PulpColors.Outline,
    outlineVariant = PulpColors.OutlineVariant,
    
    background = PulpColors.Background,
    onBackground = PulpColors.OnBackground,
    
    scrim = PulpColors.Scrim,
    
    inverseSurface = PulpColors.InverseSurface,
    inverseOnSurface = PulpColors.InverseOnSurface,
    inversePrimary = PulpColors.InversePrimary,
)

private val PulpExtendedColors = ExtendedColors(
    income = PulpColors.IncomeColor,
    onIncome = Color.White,
    incomeContainer = PulpColors.IncomeContainer,
    onIncomeContainer = Color(0xFF1B3313),
    expense = PulpColors.ExpenseColor,
    onExpense = Color.White,
    expenseContainer = PulpColors.ExpenseContainer,
    onExpenseContainer = Color(0xFF4A1F1F),
    warning = PulpColors.WarningColor,
    warningContainer = PulpColors.WarningContainer
)


// ============ THEME COMPOSABLE ============

@Composable
fun LedgerTheme(
    themeMode: AppThemeMode = AppThemeMode.DEFAULT,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme
    val extendedColors: ExtendedColors

    when (themeMode) {
        AppThemeMode.DEFAULT -> {
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
            extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
        }
        AppThemeMode.MONOLITH -> {
            colorScheme = MonolithColorScheme
            extendedColors = MonolithExtendedColors
        }
        AppThemeMode.PULP -> {
            colorScheme = PulpColorScheme
            extendedColors = PulpExtendedColors
        }
    }

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors,
        LocalAppThemeMode provides themeMode
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LedgerTypography,
            shapes = LedgerShapes,
            content = content
        )
    }
}

// Extension to access extended colors
object LedgerTheme {
    val extendedColors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}
