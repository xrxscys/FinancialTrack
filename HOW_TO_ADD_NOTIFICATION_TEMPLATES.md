# 📝 Adding New Notification Templates

## Where to Add New Notification Types

**File**: `app/src/main/java/com/example/financialtrack/utils/NotificationService.kt`

---

## 🔧 How to Add a New Template

### Step 1: Open NotificationService.kt

Located at: `app/src/main/java/com/example/financialtrack/utils/NotificationService.kt`

### Step 2: Add Your Method (Copy & Paste Template)

Add this at the end of the class, before the closing brace `}`:

```kotlin
/**
 * Template notification type: [Your Notification Name]
 */
fun create[YourNotificationName](
    param1: String,
    param2: Int
): Notification? {
    return createNotification(
        title = "Your Title",
        message = "Your message with $param1 and $param2",
        type = NotificationType.YOUR_TYPE,
        navigationType = FinancialTrackNotificationManager.NavigationType.YOUR_PAGE
    )
}
```

---

## 📋 Example: Adding "Savings Milestone" Notification

### Original Code (in NotificationService.kt):

```kotlin
    /**
     * Template for custom/generic notifications
     * Override this for specific notification types
     */
    fun createGenericNotification(
        title: String,
        message: String
    ): Notification? {
        return createNotification(
            title = title,
            message = message,
            type = NotificationType.GENERAL,
            navigationType = FinancialTrackNotificationManager.NavigationType.NONE
        )
    }
}  // <-- This is the class closing brace
```

### New Code (Add before the closing brace):

```kotlin
    /**
     * Template for custom/generic notifications
     * Override this for specific notification types
     */
    fun createGenericNotification(
        title: String,
        message: String
    ): Notification? {
        return createNotification(
            title = title,
            message = message,
            type = NotificationType.GENERAL,
            navigationType = FinancialTrackNotificationManager.NavigationType.NONE
        )
    }

    /**
     * Template notification type: Savings Milestone Reached
     */
    fun createSavingsMilestoneNotification(
        amount: Double,
        percentage: Int
    ): Notification? {
        return createNotification(
            title = "Savings Goal Milestone!",
            message = "You've saved \$$amount ($percentage% of your goal)!",
            type = NotificationType.GENERAL,
            navigationType = FinancialTrackNotificationManager.NavigationType.PROFILE
        )
    }
}  // <-- Class closing brace
```

---

## 🎯 Available Navigation Types

```kotlin
FinancialTrackNotificationManager.NavigationType.TRANSACTIONS   // Transactions page
FinancialTrackNotificationManager.NavigationType.DEBTS          // Debts/Loans page
FinancialTrackNotificationManager.NavigationType.PROFILE        // Profile/Goals page
FinancialTrackNotificationManager.NavigationType.REPORTS        // Reports page
FinancialTrackNotificationManager.NavigationType.NONE           // No navigation
```

---

## 📊 Available Notification Types

```kotlin
NotificationType.BUDGET_ALERT       // For budget-related alerts
NotificationType.DEBT_REMINDER      // For debt/bill reminders
NotificationType.TRANSACTION_ALERT  // For transaction alerts
NotificationType.GENERAL            // For general/custom notifications
```

---

## 🚀 Using Your New Template

Once added to `NotificationService.kt`, teammates can use it anywhere:

```kotlin
// In any Activity/Fragment:
val notification = notificationService.createSavingsMilestoneNotification(
    amount = 5000.00,
    percentage = 50
)
if (notification != null) {
    notificationViewModel.insertNotification(notification)
    // System notification automatically appears with sound + vibration!
}
```

---

## ✨ Template Creation Checklist

When adding new templates, make sure:

- [ ] Method name starts with `create` (e.g., `createSavingsMilestoneNotification`)
- [ ] Add documentation comment with `/**` ... `*/`
- [ ] Method returns `Notification?` (nullable Notification)
- [ ] Call `createNotification()` with appropriate parameters
- [ ] Set proper `title` (what users see in notification)
- [ ] Set proper `message` (details of what happened)
- [ ] Choose correct `type` from NotificationType enum
- [ ] Choose correct `navigationType` for where to go when tapped
- [ ] `showSystemNotification = true` (default, enables sound + vibration)

---

## 💡 Pro Tips

### Tip 1: Use Template Parameters
Always use template parameters for dynamic content:

```kotlin
// ✅ Good - uses parameters
title = "Bill Reminder",
message = "Your $billName is due on $dueDate"

// ❌ Bad - hardcoded values
title = "Bill Reminder",
message = "Your Electric Bill is due on December 15, 2025"
```

### Tip 2: Keep Messages Concise
Notifications should be scannable:

```kotlin
// ✅ Good - clear and concise
"You've saved \$$amount ($percentage% of your goal)!"

// ❌ Bad - too wordy
"Congratulations! You have successfully saved \$$amount which represents $percentage% of your total savings goal that you set for yourself."
```

### Tip 3: Use Meaningful Navigation
Route users to relevant pages:

```kotlin
// ✅ Good - relevant page
if (notification.type == NotificationType.BUDGET_ALERT) {
    navigationType = NavigationType.NONE  // User needs to take action on home page
}

// Good example:
if (notification.type == NotificationType.DEBT_REMINDER) {
    navigationType = NavigationType.DEBTS  // Route to Debts page
}
```

### Tip 4: Document Your Template
Help teammates understand what it does:

```kotlin
/**
 * Notification triggered when savings milestone is reached.
 * Congratulates user and shows progress percentage.
 * 
 * @param amount Current savings amount in dollars
 * @param percentage Percentage of goal completed (0-100)
 * @return Notification object or null if user not logged in
 */
fun createSavingsMilestoneNotification(
    amount: Double,
    percentage: Int
): Notification? {
    // ...
}
```

---

## 🧪 Testing Your New Template

After adding a new template:

1. **Open your Activity** where you want to use it
2. **Initialize NotificationService**: `notificationService = NotificationService(this)`
3. **Call your method**:
   ```kotlin
   val notification = notificationService.createYourNewNotification(
       param1 = "value1",
       param2 = 50
   )
   if (notification != null) {
       notificationViewModel.insertNotification(notification)
   }
   ```
4. **Check results**:
   - ✅ System notification appears with sound
   - ✅ Notification saves to database
   - ✅ Appears in Notifications page
   - ✅ Tapping navigates correctly

---

## 📚 Real Template Examples

### Example 1: Spending Alert
```kotlin
fun createSpendingAlertNotification(
    amount: Double,
    reason: String
): Notification? {
    return createNotification(
        title = "Spending Alert",
        message = "You spent \$$amount on $reason",
        type = NotificationType.TRANSACTION_ALERT,
        navigationType = FinancialTrackNotificationManager.NavigationType.TRANSACTIONS
    )
}
```

### Example 2: Income Received
```kotlin
fun createIncomeReceivedNotification(
    amount: Double,
    source: String
): Notification? {
    return createNotification(
        title = "Income Received",
        message = "You received \$$amount from $source",
        type = NotificationType.GENERAL,
        navigationType = FinancialTrackNotificationManager.NavigationType.TRANSACTIONS
    )
}
```

### Example 3: Debt Paid Off
```kotlin
fun createDebtPaidOffNotification(
    debtName: String
): Notification? {
    return createNotification(
        title = "Debt Paid Off! 🎉",
        message = "$debtName has been fully paid",
        type = NotificationType.DEBT_REMINDER,
        navigationType = FinancialTrackNotificationManager.NavigationType.DEBTS
    )
}
```

### Example 4: Report Generated
```kotlin
fun createReportGeneratedNotification(
    reportType: String,
    month: String
): Notification? {
    return createNotification(
        title = "Report Ready",
        message = "$reportType report for $month is ready",
        type = NotificationType.GENERAL,
        navigationType = FinancialTrackNotificationManager.NavigationType.REPORTS
    )
}
```

---

## 🔗 Related Files

- **NotificationService.kt** - Where to add templates
  - `app/src/main/java/com/example/financialtrack/utils/NotificationService.kt`

- **Notification.kt** - Database model
  - `app/src/main/java/com/example/financialtrack/data/model/Notification.kt`

- **NotificationViewModel.kt** - Database operations
  - `app/src/main/java/com/example/financialtrack/ui/notification/NotificationViewModel.kt`

- **NotificationManager.kt** - System notification handling
  - `app/src/main/java/com/example/financialtrack/utils/NotificationManager.kt`

---

**That's it! Add your templates to `NotificationService.kt` and they're ready to use everywhere in your app!** 🚀
