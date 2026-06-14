package cvc.dailybread

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cvc.dailybread.ui.theme.DailyBreadTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // CRITICAL: Create notification channel first!
        NotificationHelper.createChannel(this)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        // Schedule notifications on app start
        scheduleVerseNotifications(this)

        setContent {
            DailyBreadTheme {
                AppNavigation()
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(onSettingsClick = { navController.navigate("settings") })
        }
        composable("settings") {
            SettingScreen(
                onBackClick = { navController.popBackStack()},
                onAboutClick = { navController.navigate("about")
                }
            )
        }
        composable("about"){
            AboutScreen(onBackClick = {navController.popBackStack() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onSettingsClick: () -> Unit) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Track current date being viewed
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    val isToday = currentDate == LocalDate.now()

    // Get settings for button visibility
    val settings = remember {
        runBlocking {
            SettingsRepository(context).settingsFlow.first()
        }
    }

    // Get verses for current date
    val morningVerse = remember(currentDate) {
        VerseRepository.getMorningVerse(context, currentDate)
    }
    val eveningVerse = remember(currentDate) {
        VerseRepository.getEveningVerse(context, currentDate)
    }

    // Format date with leap year indicator
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
    val formattedDate = currentDate.format(dateFormatter)

    val leapYearIndicator = when {
        (currentDate.monthValue == 2 && currentDate.dayOfMonth == 28) ||
                (currentDate.monthValue == 3 && currentDate.dayOfMonth == 1) -> {
            if (currentDate.isLeapYear) " (L)" else " (N)"
        }
        else -> ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Daily Bread")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formattedDate,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                            if (leapYearIndicator.isNotEmpty()) {
                                Text(
                                    text = leapYearIndicator,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Light,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Previous day button
                    IconButton(onClick = { currentDate = currentDate.minusDays(1) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Day"
                        )
                    }

                    // Today button (only show if not already today)
                    if (!isToday) {
                        IconButton(onClick = { currentDate = LocalDate.now() }) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = "Today"
                            )
                        }
                    }

                    // Next day button
                    IconButton(onClick = { currentDate = currentDate.plusDays(1) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Day"
                        )
                    }

                    // Settings button
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLandscape) {
            // Horizontal layout for landscape
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                VerseCard(
                    title = "Morning Inspiration",
                    verseReference = morningVerse.info,
                    verseText = "Tap a button below to read the full passage",
                    youVersionRef = morningVerse.youVersion,
                    mySwordRef = morningVerse.mySword,
                    actionOption = settings.actionOption,
                    modifier = Modifier.weight(1f)
                )
                VerseCard(
                    title = "Evening Reflection",
                    verseReference = eveningVerse.info,
                    verseText = "Tap a button below to read the full passage",
                    youVersionRef = eveningVerse.youVersion,
                    mySwordRef = eveningVerse.mySword,
                    actionOption = settings.actionOption,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            // Vertical layout for portrait
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                VerseCard(
                    title = "Morning Inspiration",
                    verseReference = morningVerse.info,
                    verseText = "Tap a button below to read the full passage",
                    youVersionRef = morningVerse.youVersion,
                    mySwordRef = morningVerse.mySword,
                    actionOption = settings.actionOption
                )
                VerseCard(
                    title = "Evening Reflection",
                    verseReference = eveningVerse.info,
                    verseText = "Tap a button below to read the full passage",
                    youVersionRef = eveningVerse.youVersion,
                    mySwordRef = eveningVerse.mySword,
                    actionOption = settings.actionOption
                )
            }
        }
    }
}

@Composable
fun VerseCard(
    title: String,
    verseReference: String,
    verseText: String,
    youVersionRef: String,
    mySwordRef: String,
    actionOption: NotificationActionOption,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = verseReference,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = verseText,
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodyMedium
            )

            // Show buttons based on action option
            if (actionOption != NotificationActionOption.NONE) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (actionOption) {
                        NotificationActionOption.YOUVERSION -> {
                            Button(
                                onClick = { openYouVersion(context, youVersionRef) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Open in YouVersion")
                            }
                        }
                        NotificationActionOption.MYSWORD -> {
                            Button(
                                onClick = { openMySword(context, mySwordRef) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Open in MySword")
                            }
                        }
                        NotificationActionOption.BOTH -> {
                            Button(
                                onClick = { openYouVersion(context, youVersionRef) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("YouVersion")
                            }
                            Button(
                                onClick = { openMySword(context, mySwordRef) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("MySword")
                            }
                        }
                        NotificationActionOption.NONE -> { /* No buttons */ }
                    }
                }
            }
        }
    }
}

private fun openYouVersion(context: android.content.Context, verseRef: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("youversion://bible?reference=$verseRef"))
    context.startActivity(intent)
}

private fun openMySword(context: android.content.Context, verseRef: String) {
    val intent = Intent().apply {
        component = android.content.ComponentName(
            "com.riversoft.android.mysword",
            "com.riversoft.android.mysword.MySwordLink"
        )
        data = Uri.parse("https://mysword.info/b?r=$verseRef")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}