# 🎓 Notification System - Complete Team Guide

## For Your Development Team

This guide shows your teammates exactly where to add notifications **WITHOUT changing ANY layouts** and ensure system notifications always appear with sound.

---

## 📍 Three Key Files Your Team Needs to Know

### 1️⃣ **NotificationService.kt** ← MAIN FILE FOR TEMPLATES
**Location**: `app/src/main/java/com/example/financialtrack/utils/NotificationService.kt`

This file has all notification templates. Teammates can:
- ✅ Use existing templates (Bill, Transaction, Budget, Goal, Generic)
- ✅ Add new templates here
- ✅ Call any method from their Activities/Fragments

**What it looks like:**
```kotlin
// Already exists - just use it!
fun createBillReminderNotification(billName: String, dueDate: String): Notification? { ... }
fun createLargeTransactionNotification(amount: Double, description: String): Notification? { ... }
fun createBudgetAlertNotification(category: String, percentageUsed: Int): Notification? { ... }
fun createGoalUpdateNotification(goalName: String, progress: Int): Notification? { ... }
fun createGenericNotification(title: String, message: String): Notification? { ... }
```

### 2️⃣ **NotificationViewModel.kt** ← FOR DATABASE OPERATIONS
**Location**: `app/src/main/java/com/example/financialtrack/ui/notification/NotificationViewModel.kt`

This handles saving to database. Single line to save:
```kotlin
notificationViewModel.insertNotification(notification)
```

### 3️⃣ **NotificationManager.kt** ← FOR SYSTEM NOTIFICATIONS
**Location**: `app/src/main/java/com/example/financialtrack/utils/NotificationManager.kt`

Already configured with:
- ✅ HIGH priority (shows even when app is open)
- ✅ Sound
- ✅ Vibration
- ✅ LED lights
- **Teammates don't touch this - it's automatic!**

---

## 🎯 Where Teammates Add Notification Calls

### In ANY Activity or Fragment, add 3 lines:

**1. Import (at top of file)**
```kotlin
import com.example.financialtrack.utils.NotificationService
import com.example.financialtrack.ui.notification.NotificationViewModel
```

**2. Initialize (in onCreate)**
```kotlin
private lateinit var notificationService: NotificationService
private val notificationViewModel: NotificationViewModel by viewModels()

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    notificationService = NotificationService(this)
}
```

**3. Trigger (wherever makes sense in their code)**
```kotlin
val notification = notificationService.createBillReminderNotification(
    billName = "Electric Bill",
    dueDate = "December 15, 2025"
)
if (notification != null) {
    notificationViewModel.insertNotification(notification)
    // DONE! System notification shows automatically with sound!
}
```

---

## 📊 Real-World Examples by Feature

### DebtActivity (Bill Reminders)
```kotlin
class DebtActivity : AppCompatActivity() {
    private lateinit var notificationService: NotificationService
    private val notificationViewModel: NotificationViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationService = NotificationService(this)
    }
    
    private fun saveBill(bill: Bill) {
        // ... save bill to database ...
        
        // Notify user about new bill
        val notification = notificationService.createBillReminderNotification(
            billName = bill.name,
            dueDate = bill.dueDate
        )
        if (notification != null) {
            notificationViewModel.insertNotification(notification)
            // 🔔 System notification appears with sound + vibration!
        }
    }
}
```

### TransactionActivity (Large Transactions)
```kotlin
class TransactionActivity : AppCompatActivity() {
    private lateinit var notificationService: NotificationService
    private val notificationViewModel: NotificationViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationService = NotificationService(this)
    }
    
    private fun saveTransaction(amount: Double, description: String) {
        // ... save transaction ...
        
        // Alert if large transaction
        if (amount > 500) {
            val notification = notificationService.createLargeTransactionNotification(
                amount = amount,
                description = description
            )
            if (notification != null) {
                notificationViewModel.insertNotification(notification)
                // 🔔 Alert notification with sound!
            }
        }
    }
}
```

### BudgetActivity (Budget Alerts)
```kotlin
class BudgetActivity : AppCompatActivity() {
    private lateinit var notificationService: NotificationService
    private val notificationViewModel: NotificationViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationService = NotificationService(this)
    }
    
    private fun checkBudgetLimit(category: String, spent: Double, limit: Double) {
        val percentageUsed = ((spent / limit) * 100).toInt()
        
        if (percentageUsed >= 80) {
            val notification = notificationService.createBudgetAlertNotification(
                category = category,
                percentageUsed = percentageUsed
            )
            if (notification != null) {
                notificationViewModel.insertNotification(notification)
                // 🔔 Budget alert with sound + vibration!
            }
        }
    }
}
```

### ProfileActivity (Goal Progress)
```kotlin
class ProfileActivity : AppCompatActivity() {
    private lateinit var notificationService: NotificationService
    private val notificationViewModel: NotificationViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationService = NotificationService(this)
    }
    
    private fun updateGoal(goalId: String, newAmount: Double) {
        val goal = getGoal(goalId)
        val newProgress = ((newAmount / goal.target) * 100).toInt()
        
        // Celebrate milestones
        if (newProgress == 25 || newProgress == 50 || newProgress == 75 || newProgress == 100) {
            val notification = notificationService.createGoalUpdateNotification(
                goalName = goal.name,
                progress = newProgress
            )
            if (notification != null) {
                notificationViewModel.insertNotification(notification)
                // 🔔 Celebration notification with sound!
            }
        }
    }
}
```

---

## ✨ What Happens Automatically

When teammates call `notificationViewModel.insertNotification(notification)`:

- 🔊 **Sound** - Default notification sound plays
- 📳 **Vibration** - Haptic feedback pattern (500ms + 250ms)
- 💡 **LED Light** - Blue indicator light on device
- 📱 **System Panel** - Notification appears in system tray (even inside app!)
- 💾 **Database** - Automatically saved to local database
- 📄 **Notifications Page** - Shows in app's Notifications page
- 🎯 **Navigation** - Tapping navigates to correct page
- 🗑️ **Delete** - Users can delete with trash icon animation

**ZERO additional code needed!** All features work automatically.

---

## 🆕 Adding Custom Notification Templates

If teammates need a new template type:

**File to edit**: `app/src/main/java/com/example/financialtrack/utils/NotificationService.kt`

**Template to add (at end of class, before closing brace)**:
```kotlin
/**
 * Custom notification for [your feature]
 */
fun createYourCustomNotification(
    param1: String,
    param2: Int
): Notification? {
    return createNotification(
        title = "Your Title",
        message = "Your message with $param1 and $param2",
        type = NotificationType.GENERAL,
        navigationType = FinancialTrackNotificationManager.NavigationType.YOUR_PAGE
    )
}
```

**Then use it**:
```kotlin
val notification = notificationService.createYourCustomNotification(
    param1 = "value",
    param2 = 50
)
if (notification != null) {
    notificationViewModel.insertNotification(notification)
}
```

---

## 🎯 Navigation Destinations

When notification is tapped, where should it go?

```kotlin
NavigationType.TRANSACTIONS   // Go to Transactions page
NavigationType.DEBTS          // Go to Debts page
NavigationType.PROFILE        // Go to Profile/Goals page
NavigationType.REPORTS        // Go to Reports page
NavigationType.NONE           // Stay on current page
```

**Each template already has correct navigation!**
- Bill Reminder → DEBTS
- Large Transaction → TRANSACTIONS
- Budget Alert → NONE
- Goal Progress → PROFILE
- Custom → Configure as needed

---

## ✅ Complete Integration Checklist

When adding notifications to a feature:

- [ ] Import `NotificationService` and `NotificationViewModel`
- [ ] Add `private lateinit var notificationService: NotificationService`
- [ ] Add `private val notificationViewModel: NotificationViewModel by viewModels()`
- [ ] Initialize in `onCreate()`: `notificationService = NotificationService(this)`
- [ ] Call appropriate template method when event happens
- [ ] Save with `notificationViewModel.insertNotification(notification)`
- [ ] Test - verify system notification appears with sound
- [ ] Check Notifications page - verify it appears there
- [ ] No layout changes needed ✓

---

## 📱 Testing

**For Development/Testing:**
1. Long-press "Test Notifications" label in MainActivity
2. Label changes to "🧪 [DEBUG MODE ON]"
3. Click test buttons - notifications show immediately (even inside app!)
4. Disable by long-pressing again

**For Production Testing:**
1. Click any test button
2. Background the app
3. System notification appears in panel
4. Sound + vibration triggers
5. Tap notification to navigate

---

## 🆘 Troubleshooting

| Problem | Solution |
|---------|----------|
| Notification not showing | Check user logged in, permission granted |
| Sound not playing | Check device volume, not on silent |
| Import errors | Run `./gradlew clean assembleDebug` |
| ViewModel not found | Use `by viewModels()` not `lateinit var` |
| Can't save to DB | Check user has Internet, Firebase connected |

---

## 📚 Documentation Files

- **NOTIFICATION_INTEGRATION_GUIDE.md** - Detailed examples
- **HOW_TO_ADD_NOTIFICATION_TEMPLATES.md** - Creating custom templates
- **DEBUG_NOTIFICATIONS.md** - Debug mode testing
- **NOTIFICATION_TEAMMATE_GUIDE.md** - Quick reference

---

## 🚀 Ready to Integrate!

Your teammates can now:
1. ✅ Use 5 pre-built notification templates
2. ✅ Add custom templates in NotificationService.kt
3. ✅ Trigger notifications from ANY Activity/Fragment
4. ✅ Get automatic sound, vibration, LED, system panel display
5. ✅ Save to database with one line
6. ✅ View in Notifications page
7. ✅ Navigate correctly on tap
8. ✅ NO layout changes needed!

**The notification system is production-ready and team-friendly!** 🎉

---

**Last Updated**: November 30, 2025  
**Status**: ✅ Team Ready  
**Difficulty**: ⭐⭐ (Super Easy)
