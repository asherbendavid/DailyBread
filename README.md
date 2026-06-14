# Daily Bread

A simple, elegant Android app that sends you daily Bible verse notifications—one each morning and evening—with quick access to your preferred Bible app (YouVersion, MySword, or both).

Built as a personal devotional tool and learning project in Kotlin and Jetpack Compose.

## Features

- 📬 **Daily Notifications** - Receive morning and evening Bible verses at customizable times
- 📖 **Verse Cards** - View both today's verses in a clean, card-based layout on the main screen
- 📅 **Browse by Date** - Navigate to any date to see that day's assigned verses (with previous/next buttons and a "Today" quick-jump)
- ⚙️ **Flexible Settings**
  - Enable/disable morning and/or evening notifications
  - Set custom notification times
  - Choose which Bible app opens when you tap a notification (YouVersion, MySword, or both)
  - Check battery optimization status with one-click system configuration
- 🔔 **Smart Notifications**
  - Persists daily across device reboots
  - Respects your chosen Bible app settings
  - Automatically reschedules for the next day
- 📚 **Complete Reading Plan** - Follows the Gideons International Bible reading plan (covers the entire Bible in a year)
- ✨ **Material Design 3** - Modern, clean UI with adaptive layouts for portrait and landscape
- 📱 **Leap Year Handling** - JSON data includes separate entries for leap years to ensure correct verse splits

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with ViewModel and Repository pattern
- **Persistence**: DataStore (for user preferences)
- **Background Tasks**: WorkManager (for reliable notification scheduling)
- **Local Data**: JSON-based verse library
- **Notifications**: Android NotificationCompat with custom channels

## Building & Installation

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 29+ (MinSDK 29, TargetSDK 36)
- Gradle 8.0+

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/asherbendavid/daily-bread.git
   cd daily-bread
   ```

2. **Open in Android Studio**
   - File → Open → select the project folder

3. **Sync Gradle**
   - Android Studio will prompt you to sync. Do it.

4. **Build and Run**
   - Select your device/emulator
   - Run (Shift+F10) or click the green play button

5. **Grant Permissions**
   - When prompted, allow notification and alarm permissions
   - (On Android 13+, you'll see a permission dialog; on Android 10-12, it's granted by default)

### Testing on Your Device

For reliable notification testing:
1. Add the app to your device's battery optimization whitelist (Settings → Apps → Daily Bread)
2. Enable all notifications in your OS notification settings
3. Set a test time 2-3 minutes in the future in the app settings
4. Check your notification drawer when the time arrives

**Note on Manufacturer-Specific Issues**: Some manufacturers (Huawei, Xiaomi, OnePlus, Samsung) have aggressive battery optimization that can kill background tasks. The app includes a battery status checker that links to [dontkillmyapp.com](https://dontkillmyapp.com) for manufacturer-specific setup guides.

## Usage

### Main Screen
- **Verse Cards** - Displays today's morning and evening verses with their Bible references
- **Navigation Arrows** - Move to previous/next dates
- **"Today" Button** - Jump back to today (only appears when viewing a different date)
- **Open Buttons** - Tap to open the verse in your chosen Bible app
- **Settings Icon** - Access app settings (gear icon in top-right)

### Settings Screen
- **Battery Optimization** - Check status and configure if needed
- **Notifications** - Toggle morning/evening notifications and set times
- **Bible App Integration** - Choose which app opens when you tap a verse
- **About Daily Bread** - View app info, acknowledgements, and license

## Design Decisions

### Compose over XML Layouts
Jetpack Compose provides a modern, declarative way to build UI. This makes the code more readable and the layout more maintainable than traditional XML.

### Material Design 3
A clean, contemporary design language that feels native to modern Android while remaining simple and focused on the core task.

### Local JSON Data
The Bible reading plan is stored locally as JSON rather than fetched from an API. This ensures the app works offline and doesn't require network requests for core functionality.

### WorkManager for Notifications
WorkManager is more reliable than raw AlarmManager, especially on devices with aggressive battery optimization. It respects the device's doze state and reboots.

### DataStore over SharedPreferences
DataStore is the modern replacement for SharedPreferences, offering better coroutine support and type safety.

### Separate Leap Year Handling
The JSON includes separate entries for Feb 28/Mar 1 in leap years vs. non-leap years. This ensures verse assignments align perfectly with the actual calendar structure and intended reading splits.

### Lifecycle-Aware Battery Status Refresh
When users return from the system battery optimization dialog, the app automatically rechecks the status. This provides instant visual feedback without requiring them to restart the app.

## Future Ideas

- 🎨 **Widget** - A home screen widget showing today's verse
- 🌙 **Dark Mode** - Full dark mode support (Material Design 3 foundation is ready)
- 📱 **Additional Bible Apps** - Support for other Bible apps beyond YouVersion and MySword
- 🔤 **Verse Text in App** - Display the full verse text in the card (currently just shows the reference)
- 📤 **Verse Sharing** - Built-in sharing to social media or messaging apps
- 🔖 **Bookmarks** - Save favorite verses (synced with your chosen Bible app when available)
- 🎯 **Different Reading Plans** - Support for other Bible reading plans (New Testament only, Psalms & Proverbs, etc.)

## Contributing & Feature Requests

### Report Issues
If you find a bug or have a question, please open a [GitHub Issue](https://github.com/asherbendavid/daily-bread/issues):

1. Go to the **Issues** tab
2. Click **New Issue**
3. Provide a clear title and description of the problem
4. Include your Android version and device model if relevant

### Suggest Features
Feature requests and suggestions are welcome! Please use GitHub Discussions:

1. Go to the **Discussions** tab
2. Click **New Discussion**
3. Select the **Ideas** category
4. Describe what you'd like to see and why

**Bible App Integrations**: If you'd like support for a different Bible app (e.g., BibleGateway, Olive Tree, YouVersion for a specific region), please open a discussion in the **Ideas** category with:
- The app name
- Where it can be found (Play Store link)
- How the app handles deep linking to specific verses

### Alpha Testing
This app is currently in alpha testing. If you use it and notice any issues (especially with notification timing or verse references), please report them as issues. Your feedback helps ensure quality before the eventual Play Store release.

## Acknowledgements

- **God** - For the Word and the gift of Scripture
- **Gideons International** - For the comprehensive Bible reading plan this app follows
- **Android & Kotlin Communities** - For excellent documentation and support
- **Material Design** - For the design framework and guidelines

## License

This project is personal use and currently not licensed for redistribution. If you're interested in using, modifying, or distributing this app, please reach out.

## Author

**Asher Ben David**

Built as a personal devotional tool and Kotlin learning project. Available for friends, family, and anyone else who finds value in daily Bible reading.

---

**Status**: Alpha (in active personal use and testing)  
**Last Updated**: June 2026  
**Minimum Android Version**: 10 (API 29)  
**Target Android Version**: 15 (API 36)
