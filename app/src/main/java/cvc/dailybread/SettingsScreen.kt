package cvc.dailybread

import android.app.TimePickerDialog
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    onAboutClick: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Track battery optimization status
    var isBatteryOptimized by remember {
        mutableStateOf(!BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context))
    }

    // Refresh battery status when screen resumes (user comes back from settings dialog)
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                // Small delay to let system settings take effect
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    isBatteryOptimized = !BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context)
                }, 500) // 500ms delay
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            // Reschedule notifications when leaving settings
            scheduleVerseNotifications(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Battery optimization card
            BatteryOptimizationCard(
                isOptimized = isBatteryOptimized,
                onConfigureClick = {
                    BatteryOptimizationHelper.openBatteryOptimizationSettings(context)
                }
            )

            // Notifications card
            NotificationsCard(
                morningEnabled = settings.morningEnabled,
                morningTime = settings.morningTime,
                eveningEnabled = settings.eveningEnabled,
                eveningTime = settings.eveningTime,
                onMorningToggle = { viewModel.updateSettings(settings.copy(morningEnabled = it)) },
                onMorningTimeChange = { viewModel.updateSettings(settings.copy(morningTime = it)) },
                onEveningToggle = { viewModel.updateSettings(settings.copy(eveningEnabled = it)) },
                onEveningTimeChange = { viewModel.updateSettings(settings.copy(eveningTime = it)) }
            )

            // Bible App Integration card
            BibleAppCard(
                selectedAction = settings.actionOption,
                onActionChanged = { viewModel.updateSettings(settings.copy(actionOption = it)) }
            )

            // About button
            Button(
                onClick = onAboutClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("About Daily Bread")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    morningEnabled: Boolean,
    morningTime: LocalTime,
    eveningEnabled: Boolean,
    eveningTime: LocalTime,
    selectedAction: NotificationActionOption,
    isBatteryOptimized: Boolean,
    onMorningToggle: (Boolean) -> Unit,
    onMorningTimeChange: (LocalTime) -> Unit,
    onEveningToggle: (Boolean) -> Unit,
    onEveningTimeChange: (LocalTime) -> Unit,
    onActionChanged: (NotificationActionOption) -> Unit,
    onBatteryConfigureClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val context = LocalContext.current
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Battery optimization section
        BatteryOptimizationCard(
            isOptimized = isBatteryOptimized,
            onConfigureClick = onBatteryConfigureClick
        )

        HorizontalDivider()

        // Morning notification section
        Text("Morning Notification", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Morning", modifier = Modifier.weight(1f))
            Switch(checked = morningEnabled, onCheckedChange = onMorningToggle)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = morningEnabled) {
                    TimePickerDialog(
                        context,
                        { _, hour, minute -> onMorningTimeChange(LocalTime.of(hour, minute)) },
                        morningTime.hour,
                        morningTime.minute,
                        true
                    ).show()
                }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Morning Time", modifier = Modifier.weight(1f))
            Text(
                morningTime.format(timeFormatter),
                color = if (morningEnabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }

        HorizontalDivider()

        // Evening notification section
        Text("Evening Notification", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Evening", modifier = Modifier.weight(1f))
            Switch(checked = eveningEnabled, onCheckedChange = onEveningToggle)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = eveningEnabled) {
                    TimePickerDialog(
                        context,
                        { _, hour, minute -> onEveningTimeChange(LocalTime.of(hour, minute)) },
                        eveningTime.hour,
                        eveningTime.minute,
                        true
                    ).show()
                }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Evening Time", modifier = Modifier.weight(1f))
            Text(
                eveningTime.format(timeFormatter),
                color = if (eveningEnabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }

        HorizontalDivider()

        // Notification action section
        Text("When Notification is Tapped", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)

        NotificationActionOption.entries.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onActionChanged(option) }
                    .padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = selectedAction == option,
                    onClick = { onActionChanged(option) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(option.label)
            }
        }

        HorizontalDivider()

        // About Button at the bottom
        Button(
            onClick = onAboutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text ("About Daily Bread")
        }

        Spacer (modifier = Modifier.height(24.dp))
    }
}

@Composable
fun NotificationsCard(
    morningEnabled: Boolean,
    morningTime: LocalTime,
    eveningEnabled: Boolean,
    eveningTime: LocalTime,
    onMorningToggle: (Boolean) -> Unit,
    onMorningTimeChange: (LocalTime) -> Unit,
    onEveningToggle: (Boolean) -> Unit,
    onEveningTimeChange: (LocalTime) -> Unit
) {
    val context = LocalContext.current
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🔔",
                    fontSize = 24.sp
                )
                Text(
                    text = "Notifications",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()

            // Morning Section
            Text(
                text = "Morning Notification",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enable", modifier = Modifier.weight(1f))
                Switch(checked = morningEnabled, onCheckedChange = onMorningToggle)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = morningEnabled) {
                        TimePickerDialog(
                            context,
                            { _, hour, minute -> onMorningTimeChange(LocalTime.of(hour, minute)) },
                            morningTime.hour,
                            morningTime.minute,
                            true
                        ).show()
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Time", modifier = Modifier.weight(1f))
                Text(
                    morningTime.format(timeFormatter),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (morningEnabled)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Evening Section
            Text(
                text = "Evening Notification",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enable", modifier = Modifier.weight(1f))
                Switch(checked = eveningEnabled, onCheckedChange = onEveningToggle)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = eveningEnabled) {
                        TimePickerDialog(
                            context,
                            { _, hour, minute -> onEveningTimeChange(LocalTime.of(hour, minute)) },
                            eveningTime.hour,
                            eveningTime.minute,
                            true
                        ).show()
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Time", modifier = Modifier.weight(1f))
                Text(
                    eveningTime.format(timeFormatter),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (eveningEnabled)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
    }
}

@Composable
fun BibleAppCard(
    selectedAction: NotificationActionOption,
    onActionChanged: (NotificationActionOption) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "📱",
                    fontSize = 24.sp
                )
                Text(
                    text = "Bible App Integration",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()

            Text(
                text = "When tapping notification or verse card:",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            // Radio buttons
            NotificationActionOption.entries.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onActionChanged(option) }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedAction == option,
                        onClick = { onActionChanged(option) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option.label,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BatteryOptimizationCard(
    isOptimized: Boolean,
    onConfigureClick: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    val isAggressiveDevice = BatteryOptimizationHelper.isAggressiveManufacturer()
    val manufacturerUrl = BatteryOptimizationHelper.getManufacturerAdviceUrl(context)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isOptimized)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (isOptimized) "🚫" else "✅",
                    fontSize = 24.sp
                )
                Text(
                    text = "Battery Optimization",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isOptimized) {
                    "App IS being optimized (Not Good)"
                } else {
                    "App is NOT being optimized (Good!)"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isOptimized)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "For reliable notifications, this app must be excluded from battery optimization.",
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodyMedium
            )

            if (isOptimized) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onConfigureClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CONFIGURE NOW")
                }
            }

            if (isAggressiveDevice) {
                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "⚠️ Additional Settings Required",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your ${Build.MANUFACTURER} device has additional battery-saving features that may prevent notifications from working.",
                    fontSize = 13.sp,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Please review the manufacturer-specific settings:",
                    fontSize = 13.sp,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { uriHandler.openUri(manufacturerUrl) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View ${Build.MANUFACTURER} Settings Guide")
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    onClick = { uriHandler.openUri("https://dontkillmyapp.com") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Visit dontkillmyapp.com")
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    onClick = {
                        try {
                            uriHandler.openUri("https://play.google.com/store/apps/details?id=com.urbandroid.dontkillmyapp")
                        } catch (_: Exception) {
                            // Fallback if Play Store link doesn't work
                            uriHandler.openUri("https://dontkillmyapp.com")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Install 'Don't Kill My App' Helper")
                }
            }
        }
    }
}