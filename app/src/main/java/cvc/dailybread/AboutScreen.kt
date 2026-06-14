package cvc.dailybread

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // App Icon
            Image(
                painter = painterResource(id = R.drawable.about_app_icon),
                contentDescription = "Daily Bread Icon",
                modifier = Modifier.size(120.dp)
            )

            // App Name and Version
            Text(
                text = "Daily Bread",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Version 1.0.0",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            HorizontalDivider()

            // About the App Section
            _root_ide_package_.cvc.dailybread.AboutSection(
                title = "About the App",
                content = """
                    Daily Bread is a simple Bible reading companion app that helps you stay consistent with daily scripture reading.
                    
                    The app provides morning and evening Bible verse notifications based on the Gideons International reading plan, guiding you through the entire Bible in one year.

                    The Bible reading plan used in this app guides readers through the entire bible in one year.
                    
                    Features:
                    • Morning and evening verse notifications
                    • Customizable notification times
                    • Integration with YouVersion and MySword Bible apps
                    • Previous/next day navigation
                    • Reliable notification delivery with battery optimization guidance
                    
                    Exo 16:15 ...This is the bread which the LORD hath given you to eat.
                    Exo 16:21 And they gathered it every morning...
                    
                """.trimIndent()
            )

            HorizontalDivider()

            // About the Author Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "About the Developer",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Image(
                    painter = painterResource(id = R.drawable.about_author_logo),
                    contentDescription = "Developer Logo",
                    modifier = Modifier.size(80.dp)
                )

                Text(
                    text = """
                        Dashing Dog
                        
                        A hobbyist programmer passionate about creating tools that help people grow in their service for GOD.
                        
                        2Ti 2:15: Study to shew thyself approved unto GOD, a workman that needeth not to be ashamed, rightly dividing the word of truth.
                    """.trimIndent(),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            HorizontalDivider()

            // Acknowledgements Section
            _root_ide_package_.cvc.dailybread.AboutSection(
                title = "Acknowledgements",
                content = """
                    Special thanks to:
                    
                    • The Most High GOD, for His Word and guidance: Psa 35:18 (KJV) I will give THEE thanks in the great congregation: I will praise THEE among much people. (MSG) I will give THEE full credit when everyone gathers for worship; When the people turn out in force I will say my Hallelujahs.
                    • The First Born Son of the Most High GOD, for each soft whisper in my ear: Isa 30:21 ...This is the way, walk ye in it...
                    • All my Brothers and Sisters in GOD's El Familia, that love and support me: Psa 133:1,3 ...Behold, how good and pleasant it is for brethren to dwell together in unity!...for there the LORD commanded the blessing...
                    • The Gideons International, for their dedication to sharing Scripture and creating the annual Bible reading plan that this app follows
                    
                """.trimIndent()
            )

            HorizontalDivider()

            // Copyright/Legal
            Text(
                text = "© 2026 Dashing Dog Designs\nAll rights reserved.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AboutSection(
    title: String,
    content: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = content,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Start
        )
    }
}