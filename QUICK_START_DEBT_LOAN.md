# 🚀 Debt & Loan Module - Quick Start Guide

## Get Started in 5 Minutes

This is your fastest route to getting the Debt & Loan module running.

---

## ✅ Verification Checklist (2 minutes)

Run through these quickly:

- [ ] All `.kt` files in `ui/debt/` compile without errors
- [ ] `LoanNotificationManager.kt` exists in `service/`
- [ ] Layout files exist:
  - [ ] `activity_debt.xml`
  - [ ] `dialog_add_edit_debt.xml`
  - [ ] `item_debt.xml`
- [ ] Drawable files exist:
  - [ ] `ic_expand_more.xml`
  - [ ] `ic_calendar.xml`
  - [ ] `ic_time.xml`
  - [ ] `ic_delete.xml`
  - [ ] `edit_text_background.xml`
- [ ] `strings.xml` has debt module entries
- [ ] `colors.xml` has urgency colors

If all checked, proceed. If any missing, refer to **COMPLETE_CHANGELIST.md**.

---

## 🏗️ Build & Run (2 minutes)

```bash
# Clean and build
./gradlew clean build

# Install on device/emulator
./gradlew installDebug

# Run
./gradlew installDebug

# App will start - navigate to Debt & Loans tab
```

---

## 🧪 Quick Test (1 minute)

1. **Open Debt & Loans tab** - Should show empty state message
2. **Tap "+ Add Loan"** - Dialog opens
3. **Fill form**:
   - Title: "Test Loan"
   - Amount: "10000"
   - Date: Today + 7 days
   - Time: Any time
4. **Tap Save** - Loan appears in list
5. **Tap loan card** - Expands to show details
6. **Tap "Sort/Filter"** - Shows 6 sort options
7. **Expand and check "Mark as Paid"** - Confirms and moves to history

✅ **All working?** You're done!

---

## 📋 Key Files at a Glance

### Main Activity
- **Class**: `DebtActivity.kt`
- **Purpose**: Main screen with active loans + history
- **Located**: `ui/debt/DebtActivity.kt`

### Dialog (Add/Edit)
- **Class**: `AddEditDebtDialogFragment.kt`
- **Purpose**: Form to add or edit loans
- **Located**: `ui/debt/AddEditDebtDialogFragment.kt`

### Data List
- **Class**: `DebtAdapter.kt`
- **Purpose**: Renders loans in cards
- **Located**: `ui/debt/DebtAdapter.kt`

### Business Logic
- **Class**: `DebtViewModel.kt`
- **Purpose**: Sorting, filtering, state management
- **Located**: `ui/debt/DebtViewModel.kt`

### Notifications
- **Class**: `LoanNotificationManager.kt`
- **Purpose**: Automatic deadline reminders
- **Located**: `service/LoanNotificationManager.kt`

---

## 🎯 Core Features - One Liner Each

| Feature | How |
|---------|-----|
| **Add Loan** | Tap "+ Add Loan" → Fill form → Save |
| **View Details** | Tap loan card to expand |
| **Sort** | Tap "Sort/Filter" → Pick option |
| **Mark Paid** | Expand → Check "Mark as Paid" → Confirm |
| **History** | Scroll down to "Loan History" section |
| **Notifications** | Check Notifications tab (auto-generated) |

---

## 🎨 Visual Indicators

```
Deadline Color Code:
🟢 Green  = 7+ days away (safe)
🟡 Yellow = 1-7 days away (caution)
🟠 Orange = 1-3 days away (urgent)
🔴 Red    = Today/Overdue (critical)
```

---

## 🔧 Common First Steps

### Issue: App won't build
**Solution**: 
```bash
./gradlew clean
./gradlew build --stacktrace
# Check error message and refer to DEBT_LOAN_SETUP_GUIDE.md
```

### Issue: Empty state doesn't show
**Solution**: 
Clear app data and restart:
```bash
adb shell pm clear com.example.financialtrack
adb shell am start -n com.example.financialtrack/.MainActivity
```

### Issue: Notifications not appearing
**Solution**: 
Add to app startup (MainActivity or FinancialTrackApp):
```kotlin
import com.example.financialtrack.service.DebtReminderWorker
DebtReminderWorker.scheduleDebtReminders(this)
```

---

## 📚 Documentation Files

Read in this order:

1. **This file** (Quick Start) - ✅ You're reading it
2. **DEBT_LOAN_IMPLEMENTATION_SUMMARY.md** - What was built
3. **DEBT_AND_LOAN_QUICK_REFERENCE.md** - Feature reference
4. **DEBT_AND_LOAN_MODULE_GUIDE.md** - Deep dive
5. **DEBT_LOAN_SETUP_GUIDE.md** - Integration details
6. **DEBT_LOAN_TESTING_GUIDE.md** - Test cases

---

## ✨ Pro Tips

### Tip 1: Update User ID
Replace `"user123"` in `DebtActivity.kt` with real user:
```kotlin
private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "user123"
```

### Tip 2: Customize Urgency Colors
Edit `colors.xml`:
```xml
<color name="urgency_low">#4CAF50</color>    <!-- Change to your color -->
<color name="urgency_medium">#FFD700</color>
<color name="urgency_high">#FF9800</color>
<color name="urgency_critical">#F44336</color>
```

### Tip 3: Adjust Notification Frequency
Edit `DebtReminderWorker.kt` - change from 30 to X minutes:
```kotlin
PeriodicWorkRequestBuilder<DebtReminderWorker>(
    X,  // Change this (minutes)
    TimeUnit.MINUTES
)
```

### Tip 4: Test Empty State
Mark all loans as paid to see empty message:
```
"Great! You don't have any loans 🎉"
```

---

## 🧪 5-Minute Test Plan

Run this to verify everything works:

```
Minute 1:
□ Create loan: "House Rent" - ₱20,000 - 30 days out

Minute 2:
□ Expand loan and verify details show
□ Check notification appears in Notifications tab

Minute 3:
□ Create loan: "Car Payment" - ₱50,000 - 5 days out
□ Verify it appears before "House Rent" (deadline sort)

Minute 4:
□ Sort by "Title A-Z"
□ Verify "Car Payment" appears before "House Rent"

Minute 5:
□ Mark "Car Payment" as paid
□ Verify it moves to history
□ Verify "House Rent" remains active
```

✅ All checks pass = Everything working!

---

## 🚀 You're All Set!

The module is ready to use. For questions:

- **Integration Issues?** → DEBT_LOAN_SETUP_GUIDE.md
- **Feature Questions?** → DEBT_AND_LOAN_QUICK_REFERENCE.md  
- **Testing?** → DEBT_LOAN_TESTING_GUIDE.md
- **Deep Technical?** → DEBT_AND_LOAN_MODULE_GUIDE.md

---

## 📞 Quick Reference

```
Main Files:
• DebtActivity.kt ........................ ui/debt/
• DebtViewModel.kt ....................... ui/debt/
• DebtAdapter.kt ......................... ui/debt/
• AddEditDebtDialogFragment.kt ........... ui/debt/
• LoanNotificationManager.kt ............. service/
• Debt.kt ............................... data/model/

Layouts:
• activity_debt.xml ..................... res/layout/
• dialog_add_edit_debt.xml .............. res/layout/
• item_debt.xml ......................... res/layout/

Resources:
• strings.xml ........................... res/values/
• colors.xml ............................ res/values/
• Drawables (5 files) ................... res/drawable/
```

---

**Ready to build?** Run `./gradlew build` and start exploring! 🎉

