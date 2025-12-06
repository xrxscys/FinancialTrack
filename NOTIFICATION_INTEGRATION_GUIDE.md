# 🔔 Notification Integration Guide for Teammates

## Quick Start: How to Trigger Notifications in Your Code

Your teammates don't need to touch ANY layouts. They just need to:

1. **Create a NotificationService instance** (usually in your Activity/Fragment)
2. **Call the appropriate template method**
3. **Save to database**
4. Done! System notification automatically appears with sound + vibration ✅

---

## 📋 Integration Steps

### Step 1: Add Dependencies (In your Activity/Fragment)

```kotlin
import com.example.financialtrack.utils.NotificationService
import com.example.financialtrack.ui.notification.NotificationViewModel

class YourActivity : AppCompatActivity() {
    private lateinit var notificationService: NotificationService
    private val notificationViewModel: NotificationViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize notification service (just ONE line!)
        notificationService = NotificationService(this)
    }
}
```

---

## 🎯 Using Predefined Notification Templates

### Template 1: Bill Reminder
**When**: User adds a new bill or debt reminder is due

```kotlin
// In your Debt/Bill creation code:
val notification = notificationService.createBillReminderNotification(
    billName = "Electric Bill",
    dueDate = "December 15, 2025"
)
if (notification != null) {
    notificationViewModel.insertNotification(notification)
    // That's it! System notification shows with sound + vibration
}
```

### Template 2: Large Transaction Alert
**When**: User makes a large transaction

```kotlin
// In your Transaction creation code:
val notification = notificationService.createLargeTransactionNotification(
    amount = 500.00,
    description = "Electronics Purchase"
)
if (notification != null) {
    notificationViewModel.insertNotification(notification)
    // System notification appears automatically!
}
```

### Template 3: Budget Alert
**When**: Budget usage reaches threshold

```kotlin
// In your Budget checking code:
val notification = notificationService.createBudgetAlertNotification(
    category = "Food & Dining",
    percentageUsed = 85
)
if (notification != null) {
    notificationViewModel.insertNotification(notification)
    // Budget alert notification with sound + vibration!
}
```

### Template 4: Goal Progress Update
**When**: User reaches milestone in financial goal

```kotlin
// In your Goal tracking code:
val notification = notificationService.createGoalUpdateNotification(
    goalName = "Emergency Fund",
    progress = 50  // 50% complete
)
if (notification != null) {
    notificationViewModel.insertNotification(notification)
    // Goal progress notification shows up!
}
```

### Template 5: Generic Custom Notification
**When**: You need a custom notification

```kotlin
// For any other scenario:
val notification = notificationService.createGenericNotification(
    title = "Your Custom Title",
    message = "Your custom message here"
)
if (notification != null) {
    notificationViewModel.insertNotification(notification)
    // Custom notification triggered!
}
```

---

## 🔗 Real-World Integration Examples

### Example 1: Transaction Activity - Notify on Large Spend

```kotlin
class TransactionActivity : AppCompatActivity() {
    private lateinit var notificationService: NotificationService
    private val notificationViewModel: NotificationViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationService = NotificationService(this)
    }
    
    private fun saveTransaction(amount: Double, description: String) {
        // ... your transaction saving code ...
        
        // Trigger notification if amount is large
        if (amount > 1000) {
            val notification = notificationService.createLargeTransactionNotification(
                amount = amount,
                description = description
            )
            if (notification != null) {
                notificationViewModel.insertNotification(notification)
                // User gets system notification with sound!
            }
        }
        
        // ... rest of your code ...
    }
}
```

### Example 2: Debt Activity - Notify on Bill Due

```kotlin
class DebtActivity : AppCompatActivity() {
    private lateinit var notificationService: NotificationService
    private val notificationViewModel: NotificationViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationService = NotificationService(this)
    }
    
    private fun checkBillsDue() {
        val billsDue = getBillsDueToday()
        
        billsDue.forEach { bill ->
            val notification = notificationService.createBillReminderNotification(
                billName = bill.name,
                dueDate = bill.dueDate
            )
            if (notification != null) {
                notificationViewModel.insertNotification(notification)
                // System notification with sound + vibration appears!
            }
        }
    }
}
```

### Example 3: Budget Activity - Notify on Overspend

```kotlin
class BudgetActivity : AppCompatActivity() {
    private lateinit var notificationService: NotificationService
    private val notificationViewModel: NotificationViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationService = NotificationService(this)
    }
    
    private fun checkBudgetUsage(category: String, spent: Double, limit: Double) {
        val percentageUsed = ((spent / limit) * 100).toInt()
        
        // Notify when usage exceeds 80%
        if (percentageUsed >= 80) {
            val notification = notificationService.createBudgetAlertNotification(
                category = category,
                percentageUsed = percentageUsed
            )
            if (notification != null) {
                notificationViewModel.insertNotification(notification)
                // Alert notification with sound!
            }
        }
    }
}
```

### Example 4: Profile Activity - Goal Milestone

```kotlin
class ProfileActivity : AppCompatActivity() {
    private lateinit var notificationService: NotificationService
    private val notificationViewModel: NotificationViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationService = NotificationService(this)
    }
    
    private fun updateGoalProgress(goalId: String, newProgress: Int) {
        // ... update goal in database ...
        
        val goal = getGoal(goalId)
        if (goal != null) {
            val notification = notificationService.createGoalUpdateNotification(
                goalName = goal.name,
                progress = newProgress
            )
            if (notification != null) {
                notificationViewModel.insertNotification(notification)
                // Celebration notification with sound + vibration!
            }
        }
    }
}
```

---

## 🎨 System Notification Features (Automatic!)

When your teammates call any notification method, they automatically get:

- 🔊 **Sound** - Default notification sound plays
- 📳 **Vibration** - Haptic feedback pattern (500ms + 250ms)
- 💡 **LED Light** - Blue indicator light on device
- 📱 **Panel Notification** - Shows in system notification panel (even inside app!)
- 💾 **Database** - Saved to local database automatically
- 📄 **Notification Page** - Appears in app's Notifications page
- 🎯 **Navigation** - Tap notification to navigate to relevant page
- 🗑️ **Delete** - Users can delete with trash icon + animation

**No additional code needed!** All these features work automatically when calling the notification methods.

---

## 🔄 Navigation Types

When creating notifications, you can specify where to navigate:

```kotlin
// Available navigation types:
FinancialTrackNotificationManager.NavigationType.TRANSACTIONS   // Go to Transactions page
FinancialTrackNotificationManager.NavigationType.DEBTS          // Go to Debts page
FinancialTrackNotificationManager.NavigationType.PROFILE        // Go to Profile/Goals page
FinancialTrackNotificationManager.NavigationType.REPORTS        // Go to Reports page
FinancialTrackNotificationManager.NavigationType.NONE           // No navigation
```

Each template already has the correct navigation set:
- Bill Reminder → DEBTS page
- Large Transaction → TRANSACTIONS page
- Budget Alert → HOME (NONE)
- Goal Progress → PROFILE page
- Generic → Custom (you decide)

---

## ⚡ Quick Copy-Paste Template

For teammates to use anywhere in their code:

```kotlin
// ===== NOTIFICATION INTEGRATION =====
private fun triggerMyNotification() {
    val notification = notificationService.createGenericNotification(
        title = "My Notification Title",
        message = "My notification message"
    )
    if (notification != null) {
        notificationViewModel.insertNotification(notification)
    }
}
// ===== END NOTIFICATION INTEGRATION =====
```

---

## 📝 File Locations for Reference

- **NotificationService.kt** - All template methods
  - Path: `app/src/main/java/com/example/financialtrack/utils/NotificationService.kt`
  - **This is where templates are defined**

- **NotificationViewModel.kt** - Database operations
  - Path: `app/src/main/java/com/example/financialtrack/ui/notification/NotificationViewModel.kt`

- **FinancialTrackNotificationManager.kt** - System notification handling
  - Path: `app/src/main/java/com/example/financialtrack/utils/NotificationManager.kt`

---

## ✅ Checklist for Teammates

When integrating notifications:

- [ ] Import `NotificationService` and `NotificationViewModel`
- [ ] Initialize in `onCreate()`: `notificationService = NotificationService(this)`
- [ ] Use `by viewModels()` for `NotificationViewModel`
- [ ] Call appropriate template method
- [ ] Save with `notificationViewModel.insertNotification(notification)`
- [ ] Test - should see system notification immediately
- [ ] Verify in Notifications page - notification saves to database

---

## 🚀 No Layout Changes Needed!

- ✅ Notification page already exists
- ✅ System notification channels already configured
- ✅ Permissions already added to manifest
- ✅ Sounds, vibration, LED all automatic
- ✅ Database schema ready
- ✅ UI fully styled

**Your teammates just need to call 1 method and add 1 line to save it. Done!**

---

## 🆘 Troubleshooting

### Notification not appearing?
- Check: User is logged in (FirebaseAuth)
- Check: POST_NOTIFICATIONS permission granted
- Check: Device notification volume not muted
- Check: NotificationViewModel.insertNotification() was called

### Sound not playing?
- Check: Device notification volume
- Check: Not in silent mode
- Check: Notification channel settings in device

### Can't find NotificationService?
- Make sure you imported it: `import com.example.financialtrack.utils.NotificationService`
- Clean and rebuild project if import not working

---

## 💡 Pro Tips

1. **Add notifications during success operations**
   - After transaction saved ✅
   - After bill added ✅
   - After goal updated ✅

2. **Use meaningful titles and messages**
   - Include specific amounts, names, dates
   - Make it actionable for users

3. **Don't spam notifications**
   - Only notify on important events
   - Consider notification frequency

4. **Test with debug mode** (for development)
   - Long-press "Test Notifications" to enable
   - System notifications show while app is open

---

**Last Updated**: November 30, 2025  
**Status**: ✅ Ready for Team Integration  
**Ease Level**: ⭐⭐⭐⭐⭐ (Super Easy!)
