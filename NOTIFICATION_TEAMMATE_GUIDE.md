# ⚡ Notification Quick Reference Card

## For Your Teammates

---

## 🚀 Integration in 3 Steps

### Step 1: Initialize (in onCreate)
```kotlin
private lateinit var notificationService: NotificationService
private val notificationViewModel: NotificationViewModel by viewModels()

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    notificationService = NotificationService(this)
}
```

### Step 2: Create Notification
```kotlin
val notification = notificationService.createBillReminderNotification(
    billName = "Electric Bill",
    dueDate = "December 15, 2025"
)
```

### Step 3: Save to Database
```kotlin
if (notification != null) {
    notificationViewModel.insertNotification(notification)
}
```

**That's it! System notification appears with sound + vibration automatically!** ✨

---

## 📋 All Available Templates

| Template | Method | Parameters | Where to Use |
|----------|--------|------------|--------------|
| 💰 Bill Reminder | `createBillReminderNotification()` | `billName`, `dueDate` | Debt/Bill Creation |
| 🏦 Large Transaction | `createLargeTransactionNotification()` | `amount`, `description` | Transaction Creation |
| 📊 Budget Alert | `createBudgetAlertNotification()` | `category`, `percentageUsed` | Budget Checking |
| 🎯 Goal Progress | `createGoalUpdateNotification()` | `goalName`, `progress` | Goal Updates |
| 🎨 Custom | `createGenericNotification()` | `title`, `message` | Any Scenario |

---

## 💻 Copy-Paste Code Snippets

### Trigger on Bill Creation
```kotlin
// In DebtActivity or wherever bills are created:
val notification = notificationService.createBillReminderNotification(
    billName = bill.name,
    dueDate = bill.dueDate
)
if (notification != null) {
    notificationViewModel.insertNotification(notification)
}
```

### Trigger on Large Transaction
```kotlin
// In TransactionActivity, when saving transaction:
if (amount > 1000) {
    val notification = notificationService.createLargeTransactionNotification(
        amount = amount,
        description = description
    )
    if (notification != null) {
        notificationViewModel.insertNotification(notification)
    }
}
```

### Trigger on Budget Exceeded
```kotlin
// In BudgetActivity, when checking limits:
if (spent > (limit * 0.8)) {  // 80% threshold
    val notification = notificationService.createBudgetAlertNotification(
        category = category,
        percentageUsed = percentageUsed
    )
    if (notification != null) {
        notificationViewModel.insertNotification(notification)
    }
}
```

### Trigger on Goal Milestone
```kotlin
// In ProfileActivity, when updating goal:
if (newProgress % 25 == 0) {  // Every 25% milestone
    val notification = notificationService.createGoalUpdateNotification(
        goalName = goal.name,
        progress = newProgress
    )
    if (notification != null) {
        notificationViewModel.insertNotification(notification)
    }
}
```

### Custom Notification
```kotlin
val notification = notificationService.createGenericNotification(
    title = "Your Title",
    message = "Your message"
)
if (notification != null) {
    notificationViewModel.insertNotification(notification)
}
```

---

## 🎯 Navigation Options

When notification is tapped, where should it go?

```kotlin
FinancialTrackNotificationManager.NavigationType.TRANSACTIONS   // Transactions page
FinancialTrackNotificationManager.NavigationType.DEBTS          // Debts page
FinancialTrackNotificationManager.NavigationType.PROFILE        // Profile/Goals page
FinancialTrackNotificationManager.NavigationType.REPORTS        // Reports page
FinancialTrackNotificationManager.NavigationType.NONE           // No navigation
```

Each template already has correct navigation! 
- Bill Reminder → DEBTS
- Large Transaction → TRANSACTIONS
- Budget Alert → NONE (home)
- Goal Progress → PROFILE
- Custom → You decide

---

## 🎨 Automatic Features (No Code Needed!)

When you call any notification method:

- 🔊 Sound plays automatically
- 📳 Vibration triggered automatically
- 💡 LED light shows automatically
- 📱 Shows in notification panel (even inside app!)
- 💾 Saved to database automatically
- 📄 Appears in Notifications page automatically
- 🎯 Navigation works automatically
- 🗑️ Users can delete it automatically

---

## ✅ Checklist Before Using

- [ ] User is logged in (FirebaseAuth)
- [ ] NotificationService initialized in onCreate()
- [ ] NotificationViewModel used with by viewModels()
- [ ] Device notification volume not muted
- [ ] POST_NOTIFICATIONS permission granted (should be automatic)

---

## 🆘 Troubleshooting

**Q: Notification not showing?**
A: Check user is logged in & POST_NOTIFICATIONS permission granted

**Q: Sound not playing?**
A: Check device notification volume, not on silent

**Q: Import not working?**
A: Run `./gradlew clean assembleDebug` to regenerate files

**Q: Can't find notificationViewModel?**
A: Make sure to use `by viewModels()` not `lateinit var`

---

## 📁 File Locations

| File | Purpose | Location |
|------|---------|----------|
| NotificationService.kt | All templates | utils/ |
| NotificationViewModel.kt | DB operations | ui/notification/ |
| NotificationManager.kt | System notifications | utils/ |
| Notification.kt | Database model | data/model/ |

---

## 🎓 Learn More

- **Integration Guide**: `NOTIFICATION_INTEGRATION_GUIDE.md`
- **Add Templates**: `HOW_TO_ADD_NOTIFICATION_TEMPLATES.md`
- **System Features**: `DEBUG_NOTIFICATIONS.md`

---

## 📞 Need Help?

1. Check integration guide for examples
2. Look at MainActivity.kt test buttons for reference code
3. Check if user is logged in (FirebaseAuth)
4. Verify permission is granted in app settings
5. Check device notification settings

---

**Ready to add notifications to your features? Let's go! 🚀**
