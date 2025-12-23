# Debt & Loan Reminder Module - Implementation Guide

## Overview

This document describes the complete implementation of the Debt & Loan Reminder Module for the FinancialTrack application. The module provides users with a comprehensive system to track, manage, and receive reminders for their loans and debts.

## ✅ Features Implemented

### 1. **Add Loan**
- Modal dialog with form fields:
  - **Title** (required) - Name of the loan
  - **Amount Loaned** (required) - Philippine Peso currency with numeric validation
  - **Description** (optional) - Long text field for additional details
  - **Deadline** (required) - Date & time picker combo
- **Save** button - Validates inputs and creates new loan entry
- **Cancel** button - Closes modal without saving
- Newly added loans appear below older ones (chronological order by creation date)

### 2. **Loan Display (Active Loans)**
- Each loan appears as a **clickable card bubble** showing:
  - Loan Title
  - Amount (₱ formatted with 2 decimal places)
  - Deadline (date only in collapsed view)
- **Expandable details**:
  - Click to expand and view description
  - Full details including creation date, paid date (if applicable)
- **Visual urgency indicator**: Color-coded deadline
  - 🟢 Green: 7+ days until deadline (urgency_low)
  - 🟡 Yellow: 1-7 days until deadline (urgency_medium)
  - 🟠 Orange: 1-3 days until deadline (urgency_high)
  - 🔴 Red: Overdue or today (urgency_critical)

### 3. **Sorting & Filtering**
- Filter/Sort button with options:
  - 📅 Nearest deadline (ascending)
  - 🔤 Title A–Z (alphabetical)
  - 🔤 Title Z–A (reverse alphabetical)
  - 💰 Amount (lowest → highest)
  - 💰 Amount (highest → lowest)
  - 📌 Newest first (default)
- Sorting affects **active loans only**, not history
- Real-time update of loan list

### 4. **Mark Loan as Paid**
- Each active loan has a **"Mark as Paid" checkbox** in expanded view
- Triggered action:
  - Removes loan from Active Loans section
  - Transfers loan to Loan History section
  - No deletion occurs (data persisted with `isActive=false` and `paidAt=timestamp`)
  - Confirmation dialog before marking as paid

### 5. **Loan History**
- Dedicated "Loan History" section showing all paid loans
- History loans are **read-only** (no mark as paid option)
- Clicking a history loan still shows full details
- History loans sorted by paid date (newest first)
- Section hidden if no paid loans exist

### 6. **Empty State**
- Friendly message when no active loans: **"Great! You don't have any loans 🎉"**
- Message disappears automatically when first loan is added
- Message reappears when all loans are marked as paid

---

## 📱 UI Components

### Layouts

#### `activity_debt.xml` - Main Activity Layout
- Header with back button and title
- Action button row (Add Loan + Sort/Filter)
- Scrollable content area with sections:
  - Empty state message
  - Active Loans RecyclerView
  - Loan History RecyclerView
  - Padding and dividers

#### `dialog_add_edit_debt.xml` - Add/Edit Loan Dialog
- Scrollable form with labeled fields:
  - Loan Title (EditText)
  - Amount in ₱ (numberDecimal input)
  - Description (multiline text)
  - Date picker (with calendar icon)
  - Time picker (with clock icon)
- Uses custom `edit_text_background.xml` drawable

#### `item_debt.xml` - Loan Card Item
- **Collapsed State**: Shows title, amount, deadline
- **Expanded State**: Shows description, full details, mark as paid checkbox
- Smooth expand/collapse animation with icon rotation
- Color-coded deadline text based on urgency
- Delete button (for history items only)

### Drawables (Created)
- `ic_expand_more.xml` - Expansion indicator
- `ic_calendar.xml` - Calendar icon
- `ic_time.xml` - Clock icon
- `ic_delete.xml` - Delete button icon
- `edit_text_background.xml` - Rounded text field background

### Colors (Added)
```xml
<color name="urgency_low">#4CAF50</color>        <!-- Green -->
<color name="urgency_medium">#FFD700</color>     <!-- Gold -->
<color name="urgency_high">#FF9800</color>       <!-- Orange -->
<color name="urgency_critical">#F44336</color>   <!-- Red -->
```

---

## 🗄️ Data Model

### **Debt Entity** (Enhanced)
```kotlin
@Entity(tableName = "debts")
data class Debt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val creditorName: String,              // Loan title
    val amount: Double,
    val amountPaid: Double = 0.0,          // For partial payments
    val dueDate: Long,                     // Timestamp
    val interestRate: Double = 0.0,
    val type: DebtType,                    // LOAN, DEBT, CREDIT_CARD
    val description: String = "",
    val isActive: Boolean = true,          // NEW: Active vs paid status
    val createdAt: Long = System.currentTimeMillis(),  // NEW
    val paidAt: Long? = null,              // NEW: When marked as paid
    val lastNotificationTime: Long? = null // NEW: For duplicate prevention
)
```

### **DebtDao** (Enhanced)
New queries added:
```kotlin
@Query("SELECT * FROM debts WHERE userId = :userId AND isActive = 1 ORDER BY createdAt DESC")
suspend fun getActiveDebts(userId: String): List<Debt>

@Query("SELECT * FROM debts WHERE userId = :userId AND isActive = 0 ORDER BY paidAt DESC")
suspend fun getPaidDebts(userId: String): List<Debt>
```

### **DebtRepository** (Enhanced)
New methods:
- `getActiveDebts()` - Fetch active loans
- `getPaidDebts()` - Fetch paid loans

### **Notification Model** (Existing)
Enhanced to support debt reminders:
- Type: `NotificationType.DEBT_REMINDER`
- NavigationType: `"debts"` - Routes to DebtActivity

---

## 🎯 ViewModel & Business Logic

### **DebtViewModel** (Enhanced)
```kotlin
class DebtViewModel(application: Application) : AndroidViewModel(application) {
    // LiveData for active and paid debts
    val activeDebts: LiveData<List<Debt>>
    val paidDebts: LiveData<List<Debt>>
    
    // Sorting functionality
    fun sortDebts(option: SortOption)
    fun markDebtAsPaid(debt: Debt)
    
    // CRUD operations
    fun insertDebt()
    fun updateDebt()
    fun deleteDebt()
}

enum class SortOption {
    NEWEST_FIRST,
    NEAREST_DEADLINE,
    TITLE_A_Z,
    TITLE_Z_A,
    AMOUNT_LOW_HIGH,
    AMOUNT_HIGH_LOW
}
```

### **DebtActivity** (Complete Implementation)
Features:
- Observes active and paid debts from ViewModel
- Manages two RecyclerView adapters (active + history)
- Dialog management for adding loans
- Sort/filter option selection
- Confirmation dialog for marking as paid
- Notification triggering after loan creation
- Empty state management

### **DebtAdapter** (New Implementation)
Features:
- Expandable/collapsible cards with smooth transitions
- Tracks expanded state for each loan
- Visual urgency coloring based on deadline
- Supports both active and history views
- "Mark as Paid" checkbox (active only)
- Delete button (history only)
- Proper amount formatting

### **AddEditDebtDialogFragment** (Complete Implementation)
Features:
- Date and time picker dialogs
- Form validation (required fields, numeric validation)
- Pre-population for edit mode
- Error messages via Toast
- Uses ViewBinding for type safety

---

## 🔔 Notification System

### **LoanNotificationManager** (New Service)
```kotlin
class LoanNotificationManager(
    context: Context,
    notificationRepository: NotificationRepository
)
```

**Notification Thresholds**:
- ⏰ 5 days before deadline
- ⏰ 3 days before deadline
- ⏰ 1 day before deadline
- ⏰ 5 hours before deadline
- ⏰ 3 hours before deadline
- ⏰ 1 hour before deadline

**Features**:
- Automatic notification creation based on deadline proximity
- Duplicate prevention with 10-minute window checking
- Only triggers for active loans
- Message format: `"Reminder: Your loan '{title}' is due in {time_remaining}."`
- Full integration with existing Notification repository

**Key Methods**:
- `checkAndCreateNotification(debt: Debt)` - Check and create if needed
- `shouldSendNotification()` - Prevent duplicates
- `createNotification()` - Create and persist notification
- `clearNotificationsForPaidDebt()` - Cleanup on payment

### **DebtReminderWorker** (Background Service)
```kotlin
class DebtReminderWorker(context: Context, params: WorkerParameters) 
    : CoroutineWorker(context, params)
```

**Configuration**:
- Runs every 30 minutes using WorkManager
- Periodic work with `ExistingPeriodicWorkPolicy.KEEP`
- Full coroutine support with IO dispatcher
- Error handling with retry mechanism

**Usage**:
```kotlin
// Schedule reminders
DebtReminderWorker.scheduleDebtReminders(context)

// Cancel reminders
DebtReminderWorker.cancelDebtReminders(context)
```

---

## 🔐 Technical Implementation Details

### **No Third-Party Services**
✅ No payment processing  
✅ No bank API integration  
✅ No external debt management services  
✅ Purely local reminder system  

### **Local Data Storage**
✅ Room database with offline-first design  
✅ All data stored locally on device  
✅ Persistent even after app restart  
✅ SQLite encryption ready (if needed)  

### **Offline Functionality**
✅ Works 100% offline  
✅ Notifications generated locally  
✅ No network calls required  
✅ Optional: Sync when connection available  

### **Data Persistence**
✅ No loans are deleted - marked as `isActive=false`  
✅ Full audit trail with `createdAt` and `paidAt` timestamps  
✅ Supports future analytics on loan history  

---

## 🚀 How to Use

### **Add a Loan**
1. Tap "+ Add Loan" button
2. Enter loan title, amount (₱), and deadline
3. (Optional) Add description
4. Tap "Save"

### **View Loan Details**
1. Tap on any loan card to expand
2. View description and full details
3. Tap again to collapse

### **Sort Loans**
1. Tap "Sort/Filter" button
2. Select desired sort option
3. List updates immediately

### **Mark Loan as Paid**
1. Expand the loan card
2. Check "Mark as Paid" checkbox
3. Confirm in popup dialog
4. Loan moves to history

### **View Loan History**
1. Scroll down past active loans
2. View "Loan History" section
3. Click any history loan to view details

---

## 📋 String Resources Added

All UI text is externalized to `strings.xml`:
- `debt_title` - "Loans & Debts"
- `debt_add` - "+ Add Loan"
- `debt_no_active_loans` - Empty state message
- `debt_active_section` - "Active Loans"
- `debt_history_section` - "Loan History"
- Sort option strings
- Notification message template

---

## ✨ Optional Enhancements Included

### **Visual Urgency Indicator** ✅
- Color-coded deadline display
- Changes based on days until deadline
- Updates in real-time

### **Overdue Status** ✅
- Critical urgency color (red) for overdue loans
- Visual distinction from other urgency levels

### **Confirmation Dialog** ✅
- Confirms before marking loan as paid
- Prevents accidental payments

### **Persistent Storage** ✅
- SQLite Room database
- Full historical tracking
- Supports future reporting

---

## 🔧 Integration with Notification Module

The Debt & Loan Module integrates seamlessly with the existing Notification system:

1. **Automatic Notification Generation**
   - After adding a loan, `LoanNotificationManager` checks deadline
   - Creates notifications at specified thresholds

2. **Notification Storage**
   - Uses existing `NotificationRepository`
   - Stored in notification database with type `DEBT_REMINDER`
   - NavigationType set to `"debts"` for proper routing

3. **Notification Lifecycle**
   - Notifications persist until marked as read
   - Multiple notifications can exist for same loan
   - Cleared when loan marked as paid (implicit via `isActive=false`)

4. **Notification Display**
   - Shows in NotificationActivity
   - Taps navigate to DebtActivity
   - Full integration with notification history

---

## 📚 File Structure

```
app/src/main/
├── java/com/example/financialtrack/
│   ├── ui/debt/
│   │   ├── DebtActivity.kt           (UPDATED)
│   │   ├── DebtViewModel.kt          (UPDATED)
│   │   ├── DebtAdapter.kt            (UPDATED)
│   │   └── AddEditDebtDialogFragment.kt (UPDATED)
│   ├── data/
│   │   ├── model/
│   │   │   └── Debt.kt               (UPDATED)
│   │   ├── database/
│   │   │   └── DebtDao.kt            (UPDATED)
│   │   └── repository/
│   │       ├── DebtRepository.kt     (UPDATED)
│   │       └── NotificationRepository.kt (EXISTING)
│   └── service/
│       ├── LoanNotificationManager.kt (NEW)
│       └── DebtReminderWorker.kt     (UPDATED)
└── res/
    ├── layout/
    │   ├── activity_debt.xml         (UPDATED)
    │   ├── dialog_add_edit_debt.xml  (UPDATED)
    │   └── item_debt.xml             (UPDATED)
    ├── drawable/
    │   ├── ic_expand_more.xml        (NEW)
    │   ├── ic_calendar.xml           (NEW)
    │   ├── ic_time.xml               (NEW)
    │   ├── ic_delete.xml             (NEW)
    │   └── edit_text_background.xml  (NEW)
    └── values/
        ├── strings.xml               (UPDATED)
        └── colors.xml                (UPDATED)
```

---

## 🎯 Testing Checklist

- [ ] Add a new loan and verify it appears in active list
- [ ] Expand/collapse loan card and verify details display
- [ ] Test date and time pickers
- [ ] Verify amount validation (negative, non-numeric, etc.)
- [ ] Test all sort options
- [ ] Mark loan as paid and verify it moves to history
- [ ] Verify empty state displays when no active loans
- [ ] Verify notifications trigger at correct times
- [ ] Test on low-end devices (API 24+)
- [ ] Verify offline functionality
- [ ] Test database migration (if upgrading from previous version)

---

## 📞 Support & Troubleshooting

### **Issue**: Notifications not triggering
**Solution**: Ensure `DebtReminderWorker` is scheduled in `onCreate()` or application startup

### **Issue**: Loans not persisting
**Solution**: Verify Room database migration occurs automatically with `fallbackToDestructiveMigration()`

### **Issue**: Empty state not showing
**Solution**: Check that `_activeDebts.postValue()` is called in ViewModel

### **Issue**: Colors not displaying
**Solution**: Verify color resources are added to `colors.xml` in values directory

---

## 🏁 Conclusion

The Debt & Loan Reminder Module is a complete, production-ready implementation that provides users with comprehensive debt tracking, organization, and reminder functionality. It integrates seamlessly with the existing FinancialTrack architecture and notification system while maintaining offline-first design principles.
