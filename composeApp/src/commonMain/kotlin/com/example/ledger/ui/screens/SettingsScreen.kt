@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ledger.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ledger.ui.components.ExpressiveMotion
import com.example.ledger.ui.theme.AppThemeMode
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun SettingsScreen(
    currentTheme: AppThemeMode,
    onThemeChange: (AppThemeMode) -> Unit,
    onBack: () -> Unit,
    onBackupToCloud: () -> Unit = {},
    onRestoreFromCloud: () -> Unit = {},
    onExportData: () -> Unit = {},
    onImportData: () -> Unit = {},
    onClearAllData: () -> Unit = {},
    transactionCount: Int = 0,
    recurringCount: Int = 0,
    lastSyncTime: String? = null,
    isGoogleConnected: Boolean = false,
    userEmail: String? = null,
    onConnectGoogle: () -> Unit = {},
    onSignOutGoogle: () -> Unit = {},
    isBackingUpCloud: Boolean = false,
    isRestoringCloud: Boolean = false,
    lastCloudBackupTime: String? = null,
    lastLocalBackupTime: String? = null,
    autoBackupIntervalCloud: String = "OFF",
    autoBackupIntervalLocal: String = "OFF",
    onUpdateAutoBackupCloud: (String) -> Unit = {},
    onUpdateAutoBackupLocal: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showClearDataDialog by remember { mutableStateOf(false) }
    
    // Clear data confirm dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("Clear All Data") },
            text = { 
                Text("This will permanently delete all transactions, recurring templates, and saved settings. This action cannot be undone.") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllData()
                        showClearDataDialog = false
                    }
                ) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            SettingsTopBar(onBack = onBack)
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Theme Section ──
            SettingsSectionHeader(
                icon = Icons.Outlined.Palette,
                title = "Appearance",
                subtitle = "Choose your visual style"
            )

            ThemeSelector(
                currentTheme = currentTheme,
                onThemeChange = onThemeChange
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            // ── Google Drive Section ──
            SettingsSectionHeader(
                icon = Icons.Outlined.Cloud,
                title = "Cloud Synchronization",
                subtitle = "Keep your data safe in Google Drive"
            )
            
            GoogleDriveSection(
                isConnected = isGoogleConnected,
                userEmail = userEmail,
                lastSyncTime = formatTimestamp(lastCloudBackupTime),
                isProgress = isBackingUpCloud || isRestoringCloud,
                onBackup = onBackupToCloud,
                onRestore = onRestoreFromCloud,
                onConnect = onConnectGoogle,
                onSignOut = onSignOutGoogle
            )

            if (isGoogleConnected) {
                AutoBackupOption(
                    title = "Auto Cloud Backup",
                    interval = autoBackupIntervalCloud,
                    onUpdate = onUpdateAutoBackupCloud
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // ── Data Management Section ──
            SettingsSectionHeader(
                icon = Icons.Outlined.Storage,
                title = "Data Management",
                subtitle = "Manage your local records"
            )

            DataManagementSection(
                transactionCount = transactionCount,
                recurringCount = recurringCount,
                lastLocalBackupTime = formatTimestamp(lastLocalBackupTime),
                onExportData = onExportData,
                onImportData = onImportData,
                onClearAllData = { showClearDataDialog = true }
            )
            
            AutoBackupOption(
                title = "Auto Local Backup",
                interval = autoBackupIntervalLocal,
                onUpdate = onUpdateAutoBackupLocal
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
            
            // ── About Section ──
            SettingsSectionHeader(
                icon = Icons.Outlined.Info,
                title = "About",
                subtitle = "App information"
            )
            
            AboutSection()

            Spacer(Modifier.height(32.dp))
        }
    }
}


// ============ TOP BAR ============

@Composable
private fun SettingsTopBar(onBack: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Column {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


// ============ SECTION HEADER ============

@Composable
private fun SettingsSectionHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// ============ THEME SELECTOR ============

@Composable
private fun ThemeSelector(
    currentTheme: AppThemeMode,
    onThemeChange: (AppThemeMode) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppThemeMode.entries.forEach { theme ->
            ThemeOptionCard(
                theme = theme,
                isSelected = currentTheme == theme,
                onClick = { onThemeChange(theme) }
            )
        }
    }
}

@Composable
private fun ThemeOptionCard(
    theme: AppThemeMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = ExpressiveMotion.Bouncy
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        animationSpec = tween(300)
    )

    val containerColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else
            MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = tween(300)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme preview swatch
            ThemePreviewSwatch(theme = theme)

            // Text info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = theme.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = theme.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Selected indicator
            if (isSelected) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            } else {
                // Unselected radio outline
                Surface(
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.size(28.dp)
                ) {}
            }
        }
    }
}




// ============ GOOGLE DRIVE SECTION ============

@Composable
private fun GoogleDriveSection(
    isConnected: Boolean,
    userEmail: String?,
    lastSyncTime: String?,
    isProgress: Boolean,
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onConnect: () -> Unit,
    onSignOut: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Connection status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isConnected) 
                        Color(0xFF22C55E).copy(alpha = 0.15f)
                    else 
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = if (isConnected) Icons.Outlined.CloudDone else Icons.Outlined.CloudOff,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (isConnected) 
                                Color(0xFF22C55E)
                            else 
                                MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        )
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isConnected) (userEmail ?: "Connected") else "Not Connected",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isConnected && lastSyncTime != null) 
                            "Last backup: $lastSyncTime"
                        else if (isConnected)
                            "Cloud backup enabled"
                        else 
                            "Connect to enable cloud backup",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (isProgress) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
            
            if (isConnected) {
                // Backup & Restore buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onRestore,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CloudDownload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Restore")
                    }
                    
                    Button(
                        onClick = onBackup,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Backup")
                    }
                }
            } else {
                // Setup prompt and Connect button
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = "Google Sign-In is required for cloud backup. This will sync your data securely to your Drive.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    Button(
                        onClick = onConnect,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Login,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Connect Google Drive")
                    }
                }
            }
            
            if (isConnected) {
                TextButton(
                    onClick = onSignOut,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sign Out and Disconnect")
                }
            }
        }
    }
}


// ============ DATA MANAGEMENT SECTION ============

@Composable
private fun DataManagementSection(
    transactionCount: Int,
    recurringCount: Int,
    lastLocalBackupTime: String?,
    onExportData: () -> Unit,
    onImportData: () -> Unit,
    onClearAllData: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Data stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DataStatItem(
                    label = "Transactions",
                    value = transactionCount.toString(),
                    icon = Icons.Outlined.Receipt,
                    modifier = Modifier.weight(1f)
                )
                DataStatItem(
                    label = "Recurring",
                    value = recurringCount.toString(),
                    icon = Icons.Outlined.Repeat,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Export button
            OutlinedButton(
                onClick = onExportData,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FileDownload,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Export Data as JSON")
            }
            
            // Import button
            OutlinedButton(
                onClick = onImportData,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.FileUpload,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Import Data from JSON")
            }
            
            // Clear all data button
            OutlinedButton(
                onClick = onClearAllData,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteForever,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Clear All Data")
            }
        }
    }
}

@Composable
private fun DataStatItem(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


// ============ ABOUT SECTION ============

@Composable
private fun AboutSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AboutRow(label = "App Name", value = "Ledger")
            AboutRow(label = "Version", value = "1.0.0")
            AboutRow(label = "Platform", value = "Kotlin Multiplatform")
            AboutRow(label = "UI Framework", value = "Compose Multiplatform")
            
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
            
            Text(
                text = "A beautiful personal finance tracker built with Material Design 3 Expressive.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AboutRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ThemePreviewSwatch(theme: AppThemeMode) {
    val colors = when (theme) {
        AppThemeMode.DEFAULT -> listOf(
            Color(0xFF1565C0),   // Primary blue
            Color(0xFFD1E4FF),   // Primary container
            Color(0xFFFDFCFF),   // Surface
            Color(0xFF1A1C1E)    // On surface
        )
        AppThemeMode.MONOLITH -> listOf(
            Color(0xFF3B82F6),   // Accent blue
            Color(0xFF161616),   // Slab
            Color(0xFF0A0A0A),   // Background
            Color(0xFFE5E5E5)    // Text main
        )
        AppThemeMode.PULP -> listOf(
            Color(0xFF4A6741),   // Earthy green
            Color(0xFFDADAD2),   // Paper grain
            Color(0xFFE5E5DF),   // Paper bg
            Color(0xFF3A3A35)    // Text main
        )
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.size(52.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        // 2x2 color grid
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = 14.dp))
                        .background(colors[0])
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topEnd = 14.dp))
                        .background(colors[1])
                )
            }
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(bottomStart = 14.dp))
                        .background(colors[2])
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(bottomEnd = 14.dp))
                        .background(colors[3])
                )
            }
        }
    }
}

@Composable
private fun AutoBackupOption(
    title: String,
    interval: String,
    onUpdate: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("OFF", "DAILY", "WEEKLY")
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        onClick = { expanded = true }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Outlined.AutoMode, null, tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text("Schedule frequency: $interval", style = MaterialTheme.typography.bodySmall)
            }
            Box {
                Icon(Icons.Outlined.ExpandMore, null)
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onUpdate(option)
                                expanded = false
                            },
                            trailingIcon = {
                                if (interval == option) {
                                    Icon(Icons.Outlined.Check, null)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DataMetricItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isDestructive) 
            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        else 
            MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
private fun formatTimestamp(timestamp: String?): String? {
    if (timestamp == null || timestamp == "null" || timestamp.isBlank()) return null

    return try {
        val cleanTimestamp = timestamp.filter { it.isDigit() }
        if (cleanTimestamp.isEmpty()) return null

        val millis = cleanTimestamp.toLong()
        val instant = kotlin.time.Instant.fromEpochMilliseconds(millis)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        // Date Formatting
        val month = localDateTime.month.name.lowercase()
            .replaceFirstChar { it.uppercase() }.take(3)
        val day = localDateTime.dayOfMonth
        val year = localDateTime.year

        // 12-Hour Time Logic
        val hour24 = localDateTime.hour
        val amPm = if (hour24 >= 12) "PM" else "AM"
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        val minute = localDateTime.minute.toString().padStart(2, '0')

        // Result: "Feb 18, 2026 • 5:10 PM"
        "$month $day, $year  •  $hour12:$minute $amPm"
    } catch (_: Exception) {
        timestamp
    }
}