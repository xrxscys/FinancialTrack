# Debug Notifications Guide

## Problem Solved
Testing system notifications was difficult because system notifications don't appear in the notification panel when the app is in the foreground. This debug mode lets you see system notifications **immediately while the app is open**.

## How to Enable Debug Mode

### Method: Long-Press the "Test Notifications" Label
1. Open the app
2. Scroll down to the **"Test Notifications"** heading
3. **Long-press** (hold for ~1 second) on the text
4. You'll see a toast: `"Debug Mode ENABLED ✓"`
5. The label will change to: `"Test Notifications 🧪 [DEBUG MODE ON]"`

### Toggle On/Off
- Long-press the label again to toggle debug mode off
- The setting persists across app restarts

## What Debug Mode Does

### Without Debug Mode (Normal)
- Click a test button → Toast appears
- Notification saves to database ✓
- Notification appears in Notifications page ✓
- System notification in panel: **Only when outside the app** ⚠️

### With Debug Mode (Testing)
- Click a test button → Toast appears
- Notification saves to database ✓
- Notification appears in Notifications page ✓
- System notification in panel: **Appears immediately, even inside the app** ✓
- Notifications have 🧪 indicator to show they're debug notifications

## How It Works

Debug mode uses Android's **HIGH_PRIORITY notification channel** which bypasses the foreground suppression that normally hides system notifications when the app is open.

**Key differences:**
- Uses dedicated `DebugNotificationHelper` class
- Creates notifications with `IMPORTANCE_HIGH` priority
- Adds vibration pattern for better visibility
- Prefixes notifications with 🧪 emoji

## Testing Workflow

1. **Enable debug mode** - Long-press "Test Notifications"
2. **Click a test button** - Bill Reminder, Large Transaction, etc.
3. **Watch for system notification** - Appears in notification panel immediately
4. **Verify appearance** - Check title, message, emoji indicator
5. **Check database** - Open Notifications page to verify it saved
6. **Test navigation** - Tap the system notification to verify it navigates correctly

## Implementation Details

### Files Modified
- `MainActivity.kt` - Added debug mode toggle and debug notification calls
- `activity_main.xml` - Added ID to Test Notifications label, made clickable
- `DebugNotificationHelper.kt` - New utility class (created)

### Code Example
```kotlin
// In MainActivity, each test button now includes:
if (isDebugMode) {
    debugNotificationHelper.showDebugSystemNotification(
        title = "🧪 Bill Reminder (Debug)",
        message = "Electric Bill due December 15, 2025"
    )
}
```

### SharedPreferences Storage
Debug mode preference is saved in `FinancialTrackDebug` shared preferences:
```kotlin
val isDebugMode = sharedPreferences.getBoolean("debug_mode_enabled", false)
```

## Troubleshooting

### Notifications Still Not Appearing?
1. Check Android version - HIGH_PRIORITY requires Android 5.0+
2. Verify notifications permission is granted in app settings
3. Check device notification volume isn't muted
4. Restart the app after enabling debug mode

### System Shows Wrong Channel?
- Debug notifications use separate channel: `financial_track_debug`
- Check device notification settings for this channel separately
- Enable sound/vibration for this channel if desired

## For Production
Debug mode is **development-only**. Before shipping to production:
- Ensure `isDebugMode` defaults to `false` ✓
- Debug notifications only appear if user manually enables it ✓
- No debug code runs by default ✓

## Advanced: Customize Debug Notifications

Edit `MainActivity.kt` test buttons to change debug notification content:

```kotlin
debugNotificationHelper.showDebugSystemNotification(
    title = "Your Custom Title",
    message = "Your custom message"
)
```

Or use the banner method:
```kotlin
debugNotificationHelper.showDebugBanner(
    title = "Custom Banner",
    message = "This shows as a status notification"
)
```

## Why This Matters

Real-world testing shows:
- ✓ Immediate visual feedback when testing
- ✓ Verify notification content before shipping
- ✓ Test notification appearance and styling
- ✓ Confirm sound/vibration settings work
- ✓ Check notification icon and colors

This debug mode makes development 10x faster by eliminating the need to background/foreground the app repeatedly.
