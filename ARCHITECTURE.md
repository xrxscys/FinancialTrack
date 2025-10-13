# FinancialTrack Architecture

## MVVM Architecture Pattern

```
┌─────────────────────────────────────────────────────────┐
│                         VIEW                            │
│  (Activities, Fragments, XML Layouts)                   │
│  - Displays data                                        │
│  - Handles user input                                   │
│  - No business logic                                    │
└─────────────────────────────────────────────────────────┘
                            │
                            │ observes LiveData
                            ▼
┌─────────────────────────────────────────────────────────┐
│                      VIEWMODEL                          │
│  - Holds UI state                                       │
│  - Survives configuration changes                       │
│  - Exposes data via LiveData                           │
│  - Calls Repository methods                             │
└─────────────────────────────────────────────────────────┘
                            │
                            │ requests data
                            ▼
┌─────────────────────────────────────────────────────────┐
│                     REPOSITORY                          │
│  - Single source of truth                              │
│  - Abstracts data sources                              │
│  - Handles data operations                              │
└─────────────────────────────────────────────────────────┘
                            │
                            │ uses
                            ▼
┌─────────────────────────────────────────────────────────┐
│                    DATA SOURCES                         │
│  ┌──────────────┐  ┌─────────────┐  ┌────────────────┐ │
│  │ Room Database│  │   Firebase  │  │  Remote API    │ │
│  │   (Local)    │  │    (Auth)   │  │   (Future)     │ │
│  └──────────────┘  └─────────────┘  └────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

## Data Flow

### 1. Read Operation
```
User Action → View → ViewModel → Repository → DAO → Database
           ← LiveData ← LiveData ← LiveData ← Observer ← 
```

### 2. Write Operation
```
User Action → View → ViewModel → Coroutine → Repository → DAO → Database
```

## Module Structure

### Authentication Flow
```
LoginActivity → AuthViewModel → UserRepository → UserDao → Room DB
                      ↓
              Firebase Auth (Google Sign-In)
```

### Transaction Flow
```
TransactionFragment → TransactionViewModel → TransactionRepository
                                                      ↓
                                              TransactionDao
                                                      ↓
                                                  Room DB
```

### Budget Flow
```
BudgetFragment → BudgetViewModel → BudgetRepository → BudgetDao → Room DB
                                           ↓
                                 TransactionRepository (for spent calculation)
```

## Technology Stack Details

### 1. Kotlin (100%)
- **Coroutines**: Async operations
- **Flow**: Reactive streams (future)
- **Extension Functions**: Utility methods
- **Data Classes**: Model definitions

### 2. Android Jetpack

#### Room Database
```kotlin
@Database(entities = [...], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    // ...
}
```

#### LiveData
```kotlin
// Repository
fun getAllTransactions(userId: String): LiveData<List<Transaction>>

// ViewModel
val transactions: LiveData<List<Transaction>> = repository.getAllTransactions(userId)

// View
viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
    // Update UI
}
```

#### ViewModel
```kotlin
class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository
    
    fun insertTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.insert(transaction)
    }
}
```

### 3. Firebase

#### Authentication
```kotlin
// Google Sign-In configuration
val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestIdToken(getString(R.string.default_web_client_id))
    .requestEmail()
    .build()

// Firebase Auth
val credential = GoogleAuthProvider.getCredential(idToken, null)
FirebaseAuth.getInstance().signInWithCredential(credential)
```

### 4. MPAndroidChart (Reports)
```kotlin
// Line Chart for income/expense trends
val lineChart: LineChart = findViewById(R.id.lineChart)
val entries = ArrayList<Entry>()
// Add data points
val dataSet = LineDataSet(entries, "Monthly Expenses")
lineChart.data = LineData(dataSet)
lineChart.invalidate()
```

## Database Schema

### Relationships

```
User (1) ────── (M) Transaction
  │
  └────── (M) Budget
  │
  └────── (M) Debt
  │
  └────── (M) Notification
```

### Queries

#### Get User Balance
```kotlin
// Calculate from transactions
SELECT 
    SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) as totalIncome,
    SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) as totalExpense
FROM transactions
WHERE userId = :userId
```

#### Get Budget Status
```kotlin
// Compare budget with spent amount
SELECT 
    b.*,
    COALESCE(SUM(t.amount), 0) as spent
FROM budgets b
LEFT JOIN transactions t ON b.category = t.category 
    AND t.type = 'EXPENSE'
    AND t.userId = b.userId
WHERE b.userId = :userId
GROUP BY b.id
```

## Dependency Injection (Future Enhancement)

Currently using manual dependency injection:
```kotlin
class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository
    
    init {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)
    }
}
```

Future: Migrate to Hilt/Dagger for automatic DI

## Coroutines Usage

### Repository Layer
```kotlin
class TransactionRepository(private val dao: TransactionDao) {
    
    // Suspend function for one-shot operations
    suspend fun insert(transaction: Transaction): Long {
        return dao.insert(transaction)
    }
    
    // LiveData for observable data
    fun getAllTransactions(userId: String): LiveData<List<Transaction>> {
        return dao.getAllTransactions(userId)
    }
}
```

### ViewModel Layer
```kotlin
class TransactionViewModel : AndroidViewModel() {
    
    // Launch coroutine in viewModelScope
    fun insertTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.insert(transaction)
    }
    
    // Handle errors
    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        try {
            repository.delete(transaction)
            _deleteStatus.value = Success
        } catch (e: Exception) {
            _deleteStatus.value = Error(e.message)
        }
    }
}
```

## Navigation (Future Implementation)

Using Navigation Component:
```xml
<navigation>
    <fragment id="@+id/dashboardFragment" />
    <fragment id="@+id/transactionFragment" />
    <fragment id="@+id/budgetFragment" />
    <action 
        android:id="@+id/action_dashboard_to_transaction"
        app:destination="@id/transactionFragment" />
</navigation>
```

## Testing Strategy

### Unit Tests
- ViewModel logic testing
- Repository testing with fake DAOs
- Utility function testing

### Integration Tests
- Database operations
- Repository with real Room database

### UI Tests (Espresso)
- User flows
- Navigation testing
- UI component interaction

## Security Considerations

1. **Firebase**: Secure authentication tokens
2. **Room**: Encrypted database (future: SQLCipher)
3. **ProGuard**: Code obfuscation for release builds
4. **API Keys**: Not hardcoded, stored securely

## Performance Optimizations

1. **Lazy Loading**: Paginate large transaction lists
2. **Background Threads**: All DB operations on IO dispatcher
3. **Caching**: Repository pattern for data caching
4. **Image Loading**: Coil/Glide for efficient image handling (future)

## Future Enhancements

1. **Dependency Injection**: Migrate to Hilt
2. **Compose UI**: Modern declarative UI
3. **WorkManager**: Background sync and notifications
4. **DataStore**: Replace SharedPreferences
5. **Paging 3**: Efficient list handling
6. **Remote Config**: Feature flags
7. **Analytics**: Firebase Analytics integration
8. **Crashlytics**: Error reporting
