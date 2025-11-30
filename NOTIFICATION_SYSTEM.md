# Notification System Documentation

## Overview
This notification system provides a complete template for generating both in-app and system notifications in the FinancialTrack app. The system is designed to be easily customizable and extensible by the team.

## Architecture

### Components

#### 1. **NotificationManager** (`utils/NotificationManager.kt`)
Handles system/push notifications displayed in the device's notification panel.

**Key Features:**
- Creates notification channels (Android 8.0+)
- Shows notifications with default sound
- Handles click navigation to app pages
- Manages notification IDs

**Usage:**
```kotlin
val notificationManager = FinancialTrackNotificationManager(context)
notificationManager.showNotification(
    title = "Bill Reminder",
    message = "Your Electric Bill is due on December 15",
    navigationType = FinancialTrackNotificationManager.NavigationType.DEBTS
)
```

#### 2. **NotificationService** (`utils/NotificationService.kt`)
Template service for creating in-app notifications with predefined templates.

**Template Methods:**
- `createBillReminderNotification()` - For debt/bill reminders
- `createLargeTransactionNotification()` - For unusual transactions
- `createBudgetAlertNotification()` - For budget warnings
- `createGoalUpdateNotification()` - For financial goal progress
- `createGenericNotification()` - For custom notifications

**Usage:**
```kotlin
val notificationService = NotificationService(context)
val notification = notificationService.createBillReminderNotification(
    billName = "Electric Bill",
    dueDate = "December 15, 2025"
)
```

#### 3. **Notification Model** (`data/model/Notification.kt`)
Represents a notification with all necessary data.

**Fields:**
- `id` - Unique identifier
- `userId` - Associated user
- `title` - Notification title
- `message` - Notification message
- `type` - NotificationType enum (BUDGET_ALERT, DEBT_REMINDER, TRANSACTION_ALERT, GENERAL)
- `isRead` - Read status
- `navigationType` - Target page ("transactions", "debts", "profile", "reports", "none")
- `createdAt` - Timestamp

#### 4. **NotificationAdapter** (`ui/notification/NotificationAdapter.kt`)
RecyclerView adapter for displaying notifications in a list.

**Features:**
- Displays notifications sorted by newest first
- Click handling with automatic navigation
- Formatted date/time display
- Beautiful card-based UI

#### 5. **NotificationActivity** (`ui/notification/NotificationActivity.kt`)
Activity for displaying all notifications.

**Features:**
- Displays all user notifications
- Shows "No notifications" message when empty
- Observes notification changes in real-time
- One-click navigation to relevant pages

## Database Integration

### NotificationDao (`data/database/NotificationDao.kt`)
```kotlin
@Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
fun getAllNotifications(userId: String): LiveData<List<Notification>>

@Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY createdAt DESC")
fun getUnreadNotifications(userId: String): LiveData<List<Notification>>

@Insert
suspend fun insert(notification: Notification): Long

@Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
suspend fun markAsRead(id: Long)
```

### NotificationRepository (`data/repository/NotificationRepository.kt`)
Provides clean API for notification database operations.

## Testing Notifications

The app includes test buttons on the MainActivity to verify the notification system:

1. **Test: Bill Reminder** - Creates a bill reminder notification
2. **Test: Large Transaction** - Creates a transaction alert notification
3. **Test: Budget Alert** - Creates a budget warning notification
4. **Test: Goal Progress** - Creates a goal update notification
5. **Test: Generic Notification** - Creates a custom generic notification

All test buttons will:
- Display a toast confirmation
- Show a system notification in the device's notification panel
- Create an in-app notification entry

## How to Use

### 1. Generate a Notification
```kotlin
val service = NotificationService(context)
val notification = service.createBillReminderNotification(
    billName = "Credit Card",
    dueDate = "December 20, 2025"
)
```

### 2. Save to Database
```kotlin
val viewModel: NotificationViewModel by viewModels()
if (notification != null) {
    viewModel.insertNotification(notification)
}
```

### 3. Display System Notification
The notification is automatically shown in the system notification panel when created via NotificationService.

### 4. View All Notifications
User can tap the "Notifications" button in the MainActivity to view all notifications.

### 5. Click to Navigate
When a notification is clicked:
- It navigates to the appropriate page (Transactions, Debts, Profile, or Reports)
- It marks the notification as read (optional)

## Customization Guide

### Adding a New Notification Type

1. **Add to NotificationType enum:**
```kotlin
enum class NotificationType {
    BUDGET_ALERT, 
    DEBT_REMINDER, 
    TRANSACTION_ALERT, 
    GENERAL,
    YOUR_NEW_TYPE  // Add here
}
```

2. **Add NavigationType if needed:**
```kotlin
enum class NavigationType(val destination: String) {
    TRANSACTIONS("transactions"),
    DEBTS("debts"),
    PROFILE("profile"),
    REPORTS("reports"),
    YOUR_PAGE("your_page"),  // Add here
    NONE("none")
}
```

3. **Create a template method in NotificationService:**
```kotlin
fun createYourNotificationType(
    param1: String,
    param2: String
): Notification? {
    return createNotification(
        title = "Your Notification Title",
        message = "Your message: $param1 - $param2",
        type = NotificationType.YOUR_NEW_TYPE,
        navigationType = FinancialTrackNotificationManager.NavigationType.YOUR_PAGE
    )
}
```

4. **Use it throughout the app:**
```kotlin
notificationService.createYourNotificationType("value1", "value2")
```

### Changing Notification Appearance

**Sound:** Edit `NotificationManager.kt`:
```kotlin
setSound(
    android.provider.Settings.System.DEFAULT_NOTIFICATION_URI,
    // Change to custom sound URI
)
```

**Vibration:** In `createNotificationChannel()`:
```kotlin
enableVibration(false)  // Disable vibration
```

**Visual Style:** Edit `item_notification.xml` for the card appearance.

## Permissions

The system requires the following permission (added to AndroidManifest.xml):
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

For Android 12 and below, the permission is automatically granted. For Android 13+, the user may need to grant notification permission when first using the feature.

## Key Points

✅ **Newest notifications appear at the top** - Automatically sorted by timestamp
✅ **Clickable notifications** - Each notification navigates to the appropriate page
✅ **System notifications** - Appear in device notification panel with default sound
✅ **Template system** - Easy to add new notification types
✅ **Database persistence** - All notifications are saved to local database
✅ **Real-time updates** - Uses LiveData for automatic UI updates
✅ **Empty state** - Displays message when no notifications exist

## Future Enhancements

Team can extend this system by:
1. Adding notification categories with icons
2. Implementing notification scheduling
3. Adding notification grouping by type
4. Creating rich notification content with actions
5. Implementing notification dismissal
6. Adding notification history/archiving

---

**Note:** This is a template system. The team should update the notification types, messages, and logic according to specific business requirements.
