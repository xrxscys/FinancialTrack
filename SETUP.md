# FinancialTrack - Development Setup Guide

This guide will help you set up the FinancialTrack Android application for development.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio**: Arctic Fox (2020.3.1) or newer
  - Download from [developer.android.com](https://developer.android.com/studio)
- **JDK**: Java Development Kit 8 or higher
- **Git**: Version control system
- **Firebase Account**: For authentication services

## Initial Setup

### 1. Clone the Repository

```bash
git clone https://github.com/xrxscys/FinancialTrack.git
cd FinancialTrack
```

### 2. Open in Android Studio

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the cloned `FinancialTrack` directory
4. Click "OK"

Android Studio will:
- Download Gradle wrapper
- Sync project dependencies
- Index project files

### 3. Firebase Configuration

#### Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Follow the setup wizard
4. Once created, click "Add app" → "Android"

#### Configure Firebase

1. Enter package name: `com.example.financialtrack`
2. Download `google-services.json`
3. Place it in the `app/` directory

```bash
# Copy the template and replace with your actual config
cp app/google-services.json.template app/google-services.json
# Then replace the values in google-services.json with your actual Firebase config
```

#### Enable Authentication

1. In Firebase Console, go to "Authentication"
2. Click "Get Started"
3. Enable "Google" sign-in method
4. Add your SHA-1 fingerprint:

```bash
# For debug builds
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

5. Copy the SHA-1 and add it in Firebase → Project Settings → Add fingerprint

### 4. Sync Project

In Android Studio:
1. Click "Sync Project with Gradle Files" (🐘 icon in toolbar)
2. Wait for sync to complete
3. Resolve any dependency issues if they appear

## Building the Project

### Debug Build

```bash
# Command line
./gradlew assembleDebug

# Or in Android Studio: Build → Build Bundle(s) / APK(s) → Build APK(s)
```

### Release Build

```bash
./gradlew assembleRelease
```

## Running the App

### On Emulator

1. Create an AVD (Android Virtual Device):
   - Tools → Device Manager
   - Click "Create Device"
   - Choose device definition (e.g., Pixel 6)
   - Select API level (minimum 24, recommended 33+)
   - Click "Finish"

2. Run the app:
   - Click ▶️ (Run) button
   - Select your emulator
   - Wait for app to launch

### On Physical Device

1. Enable Developer Options on your device:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
   
2. Enable USB Debugging:
   - Settings → Developer Options → USB Debugging

3. Connect device via USB

4. Run the app:
   - Click ▶️ (Run) button
   - Select your device
   - Click "OK"

## Testing

### Run Unit Tests

```bash
# Command line
./gradlew test

# Or in Android Studio: Run → Run 'All Tests'
```

### Run Instrumented Tests

```bash
# Requires connected device or emulator
./gradlew connectedAndroidTest
```

### Test Coverage

```bash
./gradlew jacocoTestReport
```

## Common Issues and Solutions

### Issue: google-services.json not found

**Solution**: Ensure `google-services.json` is in the `app/` directory, not in `app/src/`

### Issue: Build fails with "Manifest merger failed"

**Solution**: 
- Clean project: Build → Clean Project
- Rebuild: Build → Rebuild Project

### Issue: Firebase authentication fails

**Solution**:
1. Verify `google-services.json` is correct
2. Check SHA-1 fingerprint is added in Firebase
3. Ensure Google Sign-In is enabled in Firebase Console

### Issue: Room database errors

**Solution**:
- Uninstall app from device/emulator
- Reinstall to create fresh database

### Issue: Gradle sync fails

**Solution**:
```bash
# Clear Gradle cache
./gradlew --stop
rm -rf ~/.gradle/caches/
./gradlew clean build
```

## Project Structure Overview

```
FinancialTrack/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/financialtrack/
│   │   │   │   ├── data/           # Data layer
│   │   │   │   ├── ui/             # UI layer
│   │   │   │   └── utils/          # Utilities
│   │   │   ├── res/                # Resources
│   │   │   └── AndroidManifest.xml
│   │   └── test/                   # Unit tests
│   ├── build.gradle.kts
│   └── google-services.json        # Your Firebase config
├── build.gradle.kts                # Root build file
├── settings.gradle.kts
└── gradle.properties
```

## Development Workflow

1. **Create Feature Branch**
   ```bash
   git checkout dev
   git pull origin dev
   git checkout -b feature/your-feature-name
   ```

2. **Make Changes**
   - Write code following MVVM pattern
   - Add tests for new features
   - Update documentation if needed

3. **Test Your Changes**
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

4. **Commit and Push**
   ```bash
   git add .
   git commit -m "[Module] Brief description"
   git push origin feature/your-feature-name
   ```

5. **Create Pull Request**
   - Go to GitHub
   - Create PR from your branch to `dev`
   - Fill in PR template
   - Request review

## Useful Commands

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew test

# Check for dependency updates
./gradlew dependencyUpdates

# List all tasks
./gradlew tasks

# Build with stack trace
./gradlew build --stacktrace
```

## IDE Setup Recommendations

### Android Studio Plugins

- **Kotlin**: Included by default
- **.ignore**: Better .gitignore support
- **Rainbow Brackets**: Color-coded brackets
- **Key Promoter X**: Learn shortcuts

### Code Style

1. Settings → Editor → Code Style → Kotlin
2. Set from → Kotlin style guide
3. Apply

### Live Templates

Create shortcuts for common patterns:
- `viewmodel`: Create ViewModel template
- `dao`: Create DAO template
- `repo`: Create Repository template

## Additional Resources

- [Android Developer Documentation](https://developer.android.com/)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Firebase Documentation](https://firebase.google.com/docs)
- [MVVM Architecture Guide](https://developer.android.com/jetpack/guide)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)

## Getting Help

1. Check [DOCUMENTATION.md](DOCUMENTATION.md) for architecture details
2. Review [CONTRIBUTING.md](CONTRIBUTING.md) for workflow guidelines
3. Search existing [GitHub Issues](https://github.com/xrxscys/FinancialTrack/issues)
4. Open a new issue if needed

## Next Steps

After setup:
1. Explore the codebase
2. Run the app and familiarize yourself with the structure
3. Pick an issue from the backlog
4. Start contributing!

Happy coding! 🚀
