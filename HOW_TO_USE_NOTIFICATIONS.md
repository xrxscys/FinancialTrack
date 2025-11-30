# How to Use the Notification System - Complete Guide

## Quick Start (30 seconds)

1. **Initialize in your activity:**
   ```kotlin
   private lateinit var notificationService: NotificationService
   
   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       notificationService = NotificationService(this)
   }
   ```

2. **Create and send a notification:**
   ```kotlin
   notificationService.createBillReminderNotification(
       billName = "Electricity Bill",
       dueDate = "December 15, 2025"
   )
   ```

3. **That's it!** Notification will appear in both:
   - Device notification panel (with sound)
   - In-app Notifications list

## Available Notification Templates

### 1. Bill/Debt Reminder
```kotlin
notificationService.createBillReminderNotification(
    billName = "Rent Payment",
    dueDate = "December 1, 2025"
)
```
**Navigates to:** Debts page

### 2. Large Transaction Alert
```kotlin
notificationService.createLargeTransactionNotification(
    amount = 1500.00,
    description = "Monthly Rent"
)
```
**Navigates to:** Transactions page

### 3. Budget Alert
```kotlin
notificationService.createBudgetAlertNotification(
    category = "Groceries",
    percentageUsed = 90
)
```
**Navigates to:** Main page (no specific navigation)

### 4. Goal Progress Update
```kotlin
notificationService.createGoalUpdateNotification(
    goalName = "Emergency Fund",
    progress = 75
)
```
**Navigates to:** Profile page

### 5. Generic Notification
```kotlin
notificationService.createGenericNotification(
    title = "Payment Successful",
    message = "Your payment has been processed successfully"
)
```
**Navigates to:** Main page (no specific navigation)

## Saving to Database (Optional)

If you want to save notifications to the database for persistent storage:

```kotlin
val viewModel: NotificationViewModel by viewModels()

// After creating a notification
val notification = notificationService.createBillReminderNotification(...)
if (notification != null) {
    viewModel.insertNotification(notification)  // Save to database
}
```

## Viewing All Notifications

Users can view all notifications by:
1. Clicking "Notifications" button on MainActivity
2. See all notifications sorted by newest first
3. Click any notification to navigate to the relevant page

## Integration Points in Your App

### In DebtActivity
```kotlin
// When a debt is created or updated with a due date
val daysUntilDue = calculateDaysUntilDue(debt)
if (daysUntilDue == 7) {  // One week before due date
    notificationService.createBillReminderNotification(
        billName = debt.creditorName,
        dueDate = debt.dueDate.toString()
    )
}
```

### In TransactionActivity
```kotlin
// When a large transaction is recorded
if (transaction.amount > 500) {
    notificationService.createLargeTransactionNotification(
        amount = transaction.amount,
        description = transaction.description
    )
}
```

### In BudgetActivity
```kotlin
// When budget is exceeded
val percentageUsed = (spentAmount / budgetAmount * 100).toInt()
if (percentageUsed >= 80) {
    notificationService.createBudgetAlertNotification(
        category = budgetCategory,
        percentageUsed = percentageUsed
    )
}
```

### In ProfileActivity (Financial Goals)
```kotlin
// When goal progress is updated
if (goalProgress >= 50 && goalProgress < 75) {
    notificationService.createGoalUpdateNotification(
        goalName = goal.name,
        progress = goalProgress
    )
}
```

## Testing the System

### Method 1: Using Test Buttons (Easiest)
1. Run the app
2. Scroll to "Test Notifications" section
3. Click any of the 5 test buttons
4. See notifications appear in:
   - Device notification panel
   - Toast message on screen
   - Notifications list (click Notifications button)

### Method 2: Manual Testing
1. Create a notification programmatically
2. Check notification panel
3. Click notification to verify navigation

### Method 3: Database Testing
1. Save a notification to database
2. Close and reopen app
3. Check that notification still exists

## Notification Behavior

### System Notification (In Notification Panel)
- ✅ Appears immediately when created
- ✅ Shows title and message
- ✅ Includes default notification sound
- ✅ Clicking opens the app and navigates to relevant page
- ✅ Can be swiped away by user

### In-App Notification (In Notifications List)
- ✅ Persists in database
- ✅ Shows formatted date/time
- ✅ Sorted newest first
- ✅ Clickable to navigate to relevant page
- ✅ Can be marked as read (optional)

## Advanced Usage

### Disable System Notification for a Notification
```kotlin
val notification = notificationService.createNotification(
    title = "Custom Title",
    message = "Custom Message",
    type = NotificationType.GENERAL,
    navigationType = FinancialTrackNotificationManager.NavigationType.NONE,
    showSystemNotification = false  // Disable system notification
)
```

### Create Custom Notification Type

1. Add to NotificationType enum in `Notification.kt`:
```kotlin
enum class NotificationType {
    BUDGET_ALERT,
    DEBT_REMINDER,
    TRANSACTION_ALERT,
    GENERAL,
    CUSTOM_TYPE  // Add here
}
```

2. Add template to NotificationService:
```kotlin
fun createCustomTypeNotification(param: String): Notification? {
    return createNotification(
        title = "Custom Type Title",
        message = "Custom message: $param",
        type = NotificationType.CUSTOM_TYPE,
        navigationType = FinancialTrackNotificationManager.NavigationType.TRANSACTIONS
    )
}
```

## Troubleshooting

### Notification Not Appearing in Notification Panel
- ✅ Check if POST_NOTIFICATIONS permission is granted (Android 13+)
- ✅ Verify notification service is initialized
- ✅ Check that `showSystemNotification` is not set to `false`

### Notification Not Appearing in Notifications List
- ✅ Make sure you saved it to database with `viewModel.insertNotification()`
- ✅ Check that userId is correct
- ✅ Open NotificationActivity to see all notifications

### Navigation Not Working
- ✅ Verify navigationType is set correctly
- ✅ Check that target activity exists in AndroidManifest.xml
- ✅ Ensure activity is exported in manifest if needed

### Missing Toast Message
- ✅ This is optional feedback; not part of the core system
- ✅ Check if you're on the main thread

## Best Practices

1. **Always pass current userId** - Required for database
2. **Use appropriate notification types** - Helps organize notifications
3. **Set navigation type correctly** - Improves user experience
4. **Save to database** - For persistence and history
5. **Test on real device** - Notifications may behave differently on emulator
6. **Respect user preferences** - They may disable notifications in settings

## Performance Considerations

- Notifications are lightweight and won't impact performance
- Database queries are optimized with proper indexing
- LiveData updates are efficient
- No memory leaks (using proper lifecycle management)

## Privacy & Security

- ✅ No sensitive data in notification text
- ✅ No PII (Personally Identifiable Information) exposed
- ✅ Proper user ID scoping
- ✅ Database stored locally on device
- ✅ No network requests for notifications

## Summary

The notification system is designed to be:
- **Easy to use** - Simple methods with clear parameters
- **Flexible** - Works with or without database persistence
- **Professional** - Material Design UI, proper sounds
- **Integrated** - Works seamlessly with your app
- **Extensible** - Easy to add new notification types
- **Reliable** - Tested and production-ready

**You're all set to use the notification system! Happy coding!** 🎉

---

For more details, see:
- `NOTIFICATION_SYSTEM.md` - Technical documentation
- `NOTIFICATION_INTEGRATION.md` - Integration guide
- Test buttons in MainActivity - Live examples
