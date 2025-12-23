# Debt & Loan Module - Testing Guide

## 🧪 Comprehensive Testing Plan

This guide provides detailed test cases to verify all functionality of the Debt & Loan Reminder Module.

---

## 📋 Pre-Testing Setup

### Prerequisites
1. App successfully builds with no errors
2. Emulator or device running API 24+
3. Fresh app install (or clear app data for clean state)
4. System time accessible for notifications

### Environment Setup
```bash
# Clear app data
adb shell pm clear com.example.financialtrack

# Kill and restart app
adb shell am force-stop com.example.financialtrack
adb shell am start -n com.example.financialtrack/.MainActivity
```

---

## ✅ Feature Test Cases

### Test Suite 1: Add Loan

#### Test 1.1: Add Loan with All Fields
**Objective**: Verify complete loan creation with all fields

**Steps**:
1. Navigate to Debt & Loans tab
2. Tap "+ Add Loan" button
3. Enter:
   - Title: "Car Loan"
   - Amount: "50000.00"
   - Description: "Monthly car payment"
   - Date: 30 days from today
   - Time: 14:30
4. Tap "Save"

**Expected Results**:
- ✅ Dialog closes
- ✅ Loan appears in active loans list
- ✅ Loan shows correct title, amount, date
- ✅ No error messages

**Actual Result**: _______________

---

#### Test 1.2: Add Loan with Minimal Fields
**Objective**: Verify required field validation

**Steps**:
1. Tap "+ Add Loan"
2. Enter:
   - Title: "Laptop"
   - Amount: "25000"
   - Skip description
   - Select date (future)
   - Select time
3. Tap "Save"

**Expected Results**:
- ✅ Dialog closes
- ✅ Loan saved without description
- ✅ Description shows "No description provided" when expanded

**Actual Result**: _______________

---

#### Test 1.3: Validation - Missing Title
**Objective**: Verify title is required

**Steps**:
1. Tap "+ Add Loan"
2. Leave title empty
3. Fill amount, date, time
4. Tap "Save"

**Expected Results**:
- ✅ Toast message: "Please enter loan title"
- ✅ Dialog remains open
- ✅ Can correct and save

**Actual Result**: _______________

---

#### Test 1.4: Validation - Invalid Amount
**Objective**: Verify numeric validation

**Steps**:
1. Tap "+ Add Loan"
2. Enter amount: "abc"
3. Tap "Save"

**Expected Results**:
- ✅ Toast message: "Please enter valid amount in Philippine Pesos"
- ✅ Dialog remains open

**Actual Result**: _______________

---

#### Test 1.5: Validation - Zero Amount
**Objective**: Verify zero amount is rejected

**Steps**:
1. Tap "+ Add Loan"
2. Enter amount: "0"
3. Tap "Save"

**Expected Results**:
- ✅ Toast message: "Please enter valid amount in Philippine Pesos"

**Actual Result**: _______________

---

#### Test 1.6: Validation - Negative Amount
**Objective**: Verify negative amounts rejected

**Steps**:
1. Tap "+ Add Loan"
2. Enter amount: "-5000"
3. Tap "Save"

**Expected Results**:
- ✅ Toast message: "Please enter valid amount in Philippine Pesos"

**Actual Result**: _______________

---

#### Test 1.7: Cancel Add Loan
**Objective**: Verify cancel button closes dialog

**Steps**:
1. Tap "+ Add Loan"
2. Fill some fields
3. Tap "Cancel"

**Expected Results**:
- ✅ Dialog closes
- ✅ No loan created
- ✅ Fields not saved

**Actual Result**: _______________

---

### Test Suite 2: Display & Expand Loans

#### Test 2.1: View Loan Details
**Objective**: Verify expandable card shows all details

**Steps**:
1. Create loan: "House Rent" - ₱20,000 - 30 days out
2. Tap on loan card
3. Observe expanded view
4. Tap again to collapse

**Expected Results**:
- ✅ Card expands smoothly
- ✅ Shows description
- ✅ Shows amount, due date, created date
- ✅ Shows "Mark as Paid" checkbox
- ✅ Card collapses on second tap
- ✅ Icon rotates 180°

**Actual Result**: _______________

---

#### Test 2.2: Long Description Display
**Objective**: Verify long text displays properly

**Steps**:
1. Create loan with description: "Lorem ipsum dolor sit amet..." (500+ characters)
2. Expand loan card
3. Scroll description

**Expected Results**:
- ✅ Full description visible
- ✅ Text wraps properly
- ✅ No overflow or truncation
- ✅ Scrollable if needed

**Actual Result**: _______________

---

#### Test 2.3: Large Amount Display
**Objective**: Verify large amounts format correctly

**Steps**:
1. Create loan: "₱999,999,999.99"
2. View in card

**Expected Results**:
- ✅ Amount displays: "₱999,999,999.99"
- ✅ No truncation
- ✅ Proper alignment

**Actual Result**: _______________

---

### Test Suite 3: Sorting & Filtering

#### Test 3.1: Sort by Nearest Deadline
**Objective**: Verify deadline sorting works

**Steps**:
1. Create 3 loans:
   - "Loan A" - Due in 30 days
   - "Loan B" - Due in 5 days
   - "Loan C" - Due in 15 days
2. Tap "Sort/Filter"
3. Select "Nearest deadline"

**Expected Results**:
- ✅ List reorders: B (5d), C (15d), A (30d)
- ✅ Dialog closes
- ✅ Sort persists until changed

**Actual Result**: _______________

---

#### Test 3.2: Sort by Title A-Z
**Objective**: Verify alphabetical sorting

**Steps**:
1. With same 3 loans
2. Tap "Sort/Filter"
3. Select "Title A-Z"

**Expected Results**:
- ✅ List reorders: A, B, C
- ✅ Alphabetical order

**Actual Result**: _______________

---

#### Test 3.3: Sort by Title Z-A
**Objective**: Verify reverse alphabetical sorting

**Steps**:
1. Tap "Sort/Filter"
2. Select "Title Z-A"

**Expected Results**:
- ✅ List reorders: C, B, A
- ✅ Reverse alphabetical order

**Actual Result**: _______________

---

#### Test 3.4: Sort by Amount (Low to High)
**Objective**: Verify ascending amount sort

**Steps**:
1. Create 3 loans with different amounts:
   - "Expensive" - ₱50,000
   - "Cheap" - ₱5,000
   - "Medium" - ₱25,000
2. Sort "Amount (lowest → highest)"

**Expected Results**:
- ✅ List reorders: Cheap (₱5,000), Medium (₱25,000), Expensive (₱50,000)

**Actual Result**: _______________

---

#### Test 3.5: Sort by Amount (High to Low)
**Objective**: Verify descending amount sort

**Steps**:
1. Sort "Amount (highest → lowest)"

**Expected Results**:
- ✅ List reorders: Expensive (₱50,000), Medium (₱25,000), Cheap (₱5,000)

**Actual Result**: _______________

---

#### Test 3.6: Sort by Newest First (Default)
**Objective**: Verify newest loans appear first

**Steps**:
1. Create 3 loans in this order:
   - 09:00 - "First Loan"
   - 09:05 - "Second Loan"
   - 09:10 - "Third Loan"
2. Sort "Newest first"

**Expected Results**:
- ✅ List reorders: Third, Second, First
- ✅ Newest appears first

**Actual Result**: _______________

---

### Test Suite 4: Mark as Paid

#### Test 4.1: Mark Single Loan as Paid
**Objective**: Verify loan moves to history

**Steps**:
1. Create loan: "Test Loan"
2. Expand the loan
3. Check "Mark as Paid"
4. Confirm dialog

**Expected Results**:
- ✅ Confirmation dialog appears
- ✅ After confirming, loan disappears from active list
- ✅ Loan appears in "Loan History" section
- ✅ Paid date shows in history

**Actual Result**: _______________

---

#### Test 4.2: Cancel Mark as Paid
**Objective**: Verify canceling prevents payment

**Steps**:
1. Expand loan
2. Check "Mark as Paid"
3. Tap "Cancel" in confirmation dialog

**Expected Results**:
- ✅ Dialog closes
- ✅ Loan remains in active list
- ✅ Checkbox remains unchecked

**Actual Result**: _______________

---

#### Test 4.3: View Paid Loan Details
**Objective**: Verify history loans show details

**Steps**:
1. Mark loan as paid (from Test 4.1)
2. Scroll to "Loan History" section
3. Tap on paid loan
4. View details

**Expected Results**:
- ✅ History section visible
- ✅ Loan shows in history
- ✅ Paid date displays
- ✅ Details expand and collapse
- ✅ No "Mark as Paid" checkbox

**Actual Result**: _______________

---

#### Test 4.4: Mark Multiple Loans as Paid
**Objective**: Verify multiple loans can be paid

**Steps**:
1. Create 3 loans
2. Mark first as paid
3. Mark second as paid
4. Verify third remains active

**Expected Results**:
- ✅ Only 1 loan in active list
- ✅ 2 loans in history
- ✅ All data correct

**Actual Result**: _______________

---

### Test Suite 5: Empty State

#### Test 5.1: Empty State Message
**Objective**: Verify friendly message displays

**Steps**:
1. Create a loan
2. Mark it as paid
3. Empty message should show

**Expected Results**:
- ✅ Active loans section hidden
- ✅ Message displays: "Great! You don't have any loans 🎉"
- ✅ Add button still visible

**Actual Result**: _______________

---

#### Test 5.2: Empty State Disappears
**Objective**: Verify message hides when loan added

**Steps**:
1. With empty state showing
2. Tap "+ Add Loan"
3. Add a loan

**Expected Results**:
- ✅ Message disappears
- ✅ New loan appears
- ✅ Active loans section visible

**Actual Result**: _______________

---

#### Test 5.3: Fresh App Launch (Empty State)
**Objective**: Verify empty state on first launch

**Steps**:
1. Clear app data
2. Relaunch app
3. Navigate to Debt & Loans

**Expected Results**:
- ✅ Empty message displays
- ✅ No loans in list
- ✅ Add button functional

**Actual Result**: _______________

---

### Test Suite 6: Visual Indicators

#### Test 6.1: Urgency Color - Low (7+ Days)
**Objective**: Verify green color for safe deadlines

**Steps**:
1. Create loan due 30 days from today
2. View loan in list

**Expected Results**:
- ✅ Deadline text displays in green (#4CAF50)
- ✅ Card not highlighted

**Actual Result**: _______________

---

#### Test 6.2: Urgency Color - Medium (1-7 Days)
**Objective**: Verify gold color for moderate urgency

**Steps**:
1. Create loan due 5 days from today
2. View in list

**Expected Results**:
- ✅ Deadline text displays in gold (#FFD700)

**Actual Result**: _______________

---

#### Test 6.3: Urgency Color - High (1-3 Days)
**Objective**: Verify orange color for high urgency

**Steps**:
1. Create loan due 2 days from today
2. View in list

**Expected Results**:
- ✅ Deadline text displays in orange (#FF9800)

**Actual Result**: _______________

---

#### Test 6.4: Urgency Color - Critical (Today/Overdue)
**Objective**: Verify red color for overdue

**Steps**:
1. Manually insert loan with past due date (via database)
2. Restart app
3. View in list

**Expected Results**:
- ✅ Deadline text displays in red (#F44336)

**Actual Result**: _______________

---

#### Test 6.5: Icon Rotation on Expand
**Objective**: Verify expand icon rotates

**Steps**:
1. View loan card
2. Tap to expand (observe icon)
3. Tap to collapse (observe icon)

**Expected Results**:
- ✅ Icon rotates 0° → 180° on expand
- ✅ Icon rotates 180° → 0° on collapse
- ✅ Smooth rotation animation

**Actual Result**: _______________

---

### Test Suite 7: Notifications

#### Test 7.1: Notification on Loan Creation
**Objective**: Verify notification created on loan add

**Steps**:
1. Open Notifications tab
2. Note current notification count
3. Create loan due in 5 days
4. Wait 5 seconds
5. Refresh/reopen Notifications tab

**Expected Results**:
- ✅ New notification appears
- ✅ Title: "Loan Reminder"
- ✅ Message: "Reminder: Your loan '...' is due in 5 days."
- ✅ Notification type: DEBT_REMINDER

**Actual Result**: _______________

---

#### Test 7.2: Notification Message Format
**Objective**: Verify correct message format

**Steps**:
1. Create loan: "Car Payment" due in 3 days
2. Check notification message

**Expected Results**:
- ✅ Message format: "Reminder: Your loan 'Car Payment' is due in 3 days."

**Actual Result**: _______________

---

#### Test 7.3: No Duplicate Notifications
**Objective**: Verify duplicate prevention works

**Steps**:
1. Create loan due in 3 days
2. Check notifications (should be 1)
3. Force app refresh
4. Check notifications again

**Expected Results**:
- ✅ Still only 1 notification
- ✅ No duplicates created
- ✅ 10-minute window prevents duplicates

**Actual Result**: _______________

---

#### Test 7.4: Notification Stops After Payment
**Objective**: Verify notifications stop when marked paid

**Steps**:
1. Create loan due in 5 days
2. Verify notification appears
3. Mark loan as paid
4. Trigger notification check (app restart)

**Expected Results**:
- ✅ Notification created initially
- ✅ No new notifications after marked paid
- ✅ Old notification still visible in history

**Actual Result**: _______________

---

#### Test 7.5: Multiple Notification Thresholds
**Objective**: Verify notifications at all thresholds

**Steps**: (Simulated time test)
1. Create loan
2. Monitor notifications as deadlines approach
3. Observe at each threshold:
   - 5 days before
   - 3 days before
   - 1 day before
   - 5 hours before
   - 3 hours before
   - 1 hour before

**Expected Results**:
- ✅ Notification at each threshold
- ✅ Correct time remaining in message
- ✅ No duplicates within 10-minute window

**Actual Result**: _______________

---

### Test Suite 8: Data Persistence

#### Test 8.1: Loans Persist After App Restart
**Objective**: Verify data survives app restart

**Steps**:
1. Create loan: "Persistent Test"
2. Close app completely
3. Reopen app
4. Navigate to Debt & Loans

**Expected Results**:
- ✅ Loan still exists
- ✅ All details preserved
- ✅ Correct sorting applied

**Actual Result**: _______________

---

#### Test 8.2: History Persists After App Restart
**Objective**: Verify paid loans persist

**Steps**:
1. Create loan
2. Mark as paid
3. Close app
4. Reopen

**Expected Results**:
- ✅ Loan still in history
- ✅ Paid date preserved
- ✅ All details correct

**Actual Result**: _______________

---

#### Test 8.3: Sort State Persists
**Objective**: Verify sort selection persists

**Steps**:
1. Sort by "Nearest deadline"
2. Close app
3. Reopen

**Expected Results**:
- ✅ Loans still sorted by deadline
- ✅ Sort state maintained

**Actual Result**: _______________

---

#### Test 8.4: Large Database Test
**Objective**: Verify performance with many loans

**Steps**:
1. Create 50 loans (programmatically or manually)
2. Observe app performance
3. Sort loans
4. Expand cards

**Expected Results**:
- ✅ App responsive
- ✅ No lag
- ✅ All loans visible
- ✅ RecyclerView efficient

**Actual Result**: _______________

---

### Test Suite 9: Device Compatibility

#### Test 9.1: Portrait Orientation
**Objective**: Verify UI in portrait

**Steps**:
1. Keep device in portrait
2. Create loan
3. Expand card
4. Sort loans

**Expected Results**:
- ✅ All elements visible
- ✅ No truncation
- ✅ Buttons clickable
- ✅ Text readable

**Actual Result**: _______________

---

#### Test 9.2: Landscape Orientation
**Objective**: Verify UI in landscape

**Steps**:
1. Rotate device to landscape
2. Create loan
3. Expand card
4. View form

**Expected Results**:
- ✅ Layout adapts
- ✅ All elements visible
- ✅ Form scrollable
- ✅ Buttons accessible

**Actual Result**: _______________

---

#### Test 9.3: Tablet (Large Screen)
**Objective**: Verify UI on tablet

**Steps**:
1. Run on tablet or large screen
2. Create/manage loans

**Expected Results**:
- ✅ UI scales properly
- ✅ Efficient use of space
- ✅ Cards appropriately sized
- ✅ Text readable

**Actual Result**: _______________

---

#### Test 9.4: Small Phone (API 24+)
**Objective**: Verify minimum API support

**Steps**:
1. Run on API 24 emulator or old device
2. Test all features

**Expected Results**:
- ✅ App launches
- ✅ All features work
- ✅ No crashes
- ✅ Performance acceptable

**Actual Result**: _______________

---

### Test Suite 10: Offline Functionality

#### Test 10.1: Works Without Internet
**Objective**: Verify offline operation

**Steps**:
1. Enable airplane mode
2. Create loan
3. Mark as paid
4. Sort loans

**Expected Results**:
- ✅ All features work offline
- ✅ No network requests
- ✅ Data saves locally
- ✅ No error dialogs

**Actual Result**: _______________

---

#### Test 10.2: Offline Data Persistence
**Objective**: Verify data survives offline

**Steps**:
1. In airplane mode, create loan
2. Restart app (still offline)
3. Loan visible

**Expected Results**:
- ✅ Loan persists offline
- ✅ Complete data available
- ✅ Notifications work offline

**Actual Result**: _______________

---

## 📊 Test Summary

### Pass/Fail Count
| Category | Total | Passed | Failed | %  |
|----------|-------|--------|--------|-----|
| Add Loan | 7 | ___ | ___ | __% |
| Display | 3 | ___ | ___ | __% |
| Sorting | 6 | ___ | ___ | __% |
| Payment | 4 | ___ | ___ | __% |
| Empty State | 3 | ___ | ___ | __% |
| Visuals | 5 | ___ | ___ | __% |
| Notifications | 5 | ___ | ___ | __% |
| Persistence | 4 | ___ | ___ | __% |
| Compatibility | 4 | ___ | ___ | __% |
| Offline | 2 | ___ | ___ | __% |
| **TOTAL** | **43** | **___** | **___** | **___%** |

---

## 🐛 Bug Report Format

For any failures, document using this format:

```
Test Case: [Test Number]
Title: [Brief Description]
Severity: [Critical / High / Medium / Low]

Steps to Reproduce:
1. ...
2. ...
3. ...

Expected Result:
- [What should happen]

Actual Result:
- [What actually happened]

Screenshots:
[Attach screenshots]

Device:
- Model: [Device model]
- Android: [API level]
- App Version: [Version]

Additional Notes:
[Any other relevant info]
```

---

## ✅ Sign-Off

**Testing Date**: _______________  
**Tester Name**: _______________  
**Total Tests Run**: _______________  
**Pass Rate**: _______________  
**Ready for Production**: ☐ Yes ☐ No  
**Comments**: _______________

---

## 📝 Notes

- Test cases should be executed in order
- Document results as you go
- Screenshots help identify UI issues
- Check device logs for any warnings/errors
- Test on multiple devices if possible
- Repeat critical tests after any code changes

