# Quick Reference Guide

Quick reference for common development tasks in FinancialTrack.

## Table of Contents
- [Adding a New Module](#adding-a-new-module)
- [Creating a New Screen](#creating-a-new-screen)
- [Database Operations](#database-operations)
- [Working with ViewModels](#working-with-viewmodels)
- [Common Patterns](#common-patterns)
- [Debugging Tips](#debugging-tips)

## Adding a New Module

### 1. Create Data Model
```kotlin
// app/src/main/java/com/example/financialtrack/data/model/YourModel.kt
@Entity(tableName = "your_table")
data class YourModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val field1: String,
    val field2: Double
)
```

### 2. Create DAO
```kotlin
// app/src/main/java/com/example/financialtrack/data/database/YourDao.kt
@Dao
interface YourDao {
    @Query("SELECT * FROM your_table WHERE userId = :userId")
    fun getAll(userId: String): LiveData<List<YourModel>>
    
    @Insert
    suspend fun insert(item: YourModel): Long
    
    @Update
    suspend fun update(item: YourModel)
    
    @Delete
    suspend fun delete(item: YourModel)
}
```

### 3. Update Database
```kotlin
// Add to AppDatabase.kt
@Database(
    entities = [
        // ... existing entities
        YourModel::class
    ],
    version = 2  // Increment version
)
abstract class AppDatabase : RoomDatabase() {
    // ... existing DAOs
    abstract fun yourDao(): YourDao
}
```

### 4. Create Repository
```kotlin
// app/src/main/java/com/example/financialtrack/data/repository/YourRepository.kt
class YourRepository(private val dao: YourDao) {
    fun getAll(userId: String) = dao.getAll(userId)
    suspend fun insert(item: YourModel) = dao.insert(item)
}
```

### 5. Create ViewModel
```kotlin
// app/src/main/java/com/example/financialtrack/ui/yourmodule/YourViewModel.kt
class YourViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: YourRepository
    
    init {
        val dao = AppDatabase.getDatabase(application).yourDao()
        repository = YourRepository(dao)
    }
    
    fun getAll(userId: String) = repository.getAll(userId)
    
    fun insert(item: YourModel) = viewModelScope.launch {
        repository.insert(item)
    }
}
```

## Creating a New Screen

### 1. Create Layout
```xml
<!-- app/src/main/res/layout/fragment_your_screen.xml -->
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout>
    <!-- Your UI elements -->
</androidx.constraintlayout.widget.ConstraintLayout>
```

### 2. Create Fragment/Activity
```kotlin
class YourFragment : Fragment() {
    private lateinit var binding: FragmentYourScreenBinding
    private val viewModel: YourViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYourScreenBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }
    
    private fun setupObservers() {
        viewModel.data.observe(viewLifecycleOwner) { data ->
            // Update UI
        }
    }
}
```

## Database Operations

### Query with LiveData
```kotlin
// DAO
@Query("SELECT * FROM transactions WHERE userId = :userId")
fun getAllTransactions(userId: String): LiveData<List<Transaction>>

// ViewModel
val transactions = repository.getAllTransactions(userId)

// Fragment
viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
    updateUI(transactions)
}
```

### Insert Data
```kotlin
// ViewModel
fun addTransaction(transaction: Transaction) = viewModelScope.launch {
    repository.insert(transaction)
}

// Fragment
viewModel.addTransaction(newTransaction)
```

### Update Data
```kotlin
// ViewModel
fun updateTransaction(transaction: Transaction) = viewModelScope.launch {
    repository.update(transaction)
}
```

### Delete Data
```kotlin
// ViewModel
fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
    repository.delete(transaction)
}
```

### Complex Queries
```kotlin
@Query("""
    SELECT * FROM transactions 
    WHERE userId = :userId 
    AND date BETWEEN :startDate AND :endDate
    AND type = :type
    ORDER BY date DESC
""")
fun getFilteredTransactions(
    userId: String,
    startDate: Long,
    endDate: Long,
    type: TransactionType
): LiveData<List<Transaction>>
```

## Working with ViewModels

### Basic ViewModel
```kotlin
class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val _data = MutableLiveData<List<Item>>()
    val data: LiveData<List<Item>> = _data
    
    fun loadData() = viewModelScope.launch {
        // Load data
    }
}
```

### ViewModel with State
```kotlin
sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<Item>) : UiState()
    data class Error(val message: String) : UiState()
}

class MyViewModel : AndroidViewModel() {
    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState
    
    fun loadData() = viewModelScope.launch {
        _uiState.value = UiState.Loading
        try {
            val data = repository.getData()
            _uiState.value = UiState.Success(data)
        } catch (e: Exception) {
            _uiState.value = UiState.Error(e.message ?: "Unknown error")
        }
    }
}
```

### Using in Fragment
```kotlin
class MyFragment : Fragment() {
    private val viewModel: MyViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> showLoading()
                is UiState.Success -> showData(state.data)
                is UiState.Error -> showError(state.message)
            }
        }
        
        viewModel.loadData()
    }
}
```

## Common Patterns

### Format Currency
```kotlin
import com.example.financialtrack.utils.FormatUtils

val formattedAmount = FormatUtils.formatCurrency(transaction.amount)
```

### Format Date
```kotlin
val formattedDate = FormatUtils.formatDate(transaction.date)
```

### Get Current User
```kotlin
val currentUser = FirebaseAuth.getInstance().currentUser
val userId = currentUser?.uid ?: ""
```

### RecyclerView Adapter
```kotlin
class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    
    private var transactions = emptyList<Transaction>()
    
    fun setData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(transactions[position])
    }
    
    override fun getItemCount() = transactions.size
    
    class ViewHolder(private val binding: ItemTransactionBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(transaction: Transaction) {
            binding.apply {
                amountText.text = FormatUtils.formatCurrency(transaction.amount)
                categoryText.text = transaction.category
                dateText.text = FormatUtils.formatDate(transaction.date)
            }
        }
    }
}

// In Fragment
val adapter = TransactionAdapter()
binding.recyclerView.adapter = adapter
viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
    adapter.setData(transactions)
}
```

### Show Dialog
```kotlin
fun showDeleteConfirmation(transaction: Transaction) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle("Delete Transaction")
        .setMessage("Are you sure you want to delete this transaction?")
        .setPositiveButton("Delete") { _, _ ->
            viewModel.deleteTransaction(transaction)
        }
        .setNegativeButton("Cancel", null)
        .show()
}
```

### Show Snackbar
```kotlin
Snackbar.make(binding.root, "Transaction saved", Snackbar.LENGTH_SHORT).show()
```

## Debugging Tips

### View Database Contents
```bash
# Using ADB
adb shell
run-as com.example.financialtrack
cd databases
sqlite3 financial_track_database

# View tables
.tables

# View data
SELECT * FROM transactions;
```

### Enable Room Query Logging
```kotlin
// In AppDatabase
Room.databaseBuilder(context, AppDatabase::class.java, "database")
    .setQueryCallback({ sqlQuery, bindArgs ->
        Log.d("RoomQuery", "SQL Query: $sqlQuery Args: $bindArgs")
    }, Executors.newSingleThreadExecutor())
    .build()
```

### Check LiveData Value
```kotlin
viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
    Log.d("TAG", "Transactions count: ${transactions.size}")
    transactions.forEach { transaction ->
        Log.d("TAG", "Transaction: ${transaction.description} - ${transaction.amount}")
    }
}
```

### Profile Database Performance
```bash
adb shell setprop log.tag.SQLiteTime VERBOSE
adb shell setprop log.tag.SQLiteStatements VERBOSE
```

### Clear App Data
```bash
# Clear data without uninstalling
adb shell pm clear com.example.financialtrack
```

### Common Gradle Commands
```bash
# Clean build
./gradlew clean

# Assemble debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run specific test
./gradlew test --tests TransactionModelTest

# Check dependencies
./gradlew dependencies

# Build with logs
./gradlew build --info
```

## String Resources

### Add to strings.xml
```xml
<string name="transaction_deleted">Transaction deleted</string>
<string name="error_loading_data">Error loading data</string>
```

### Use in Code
```kotlin
val message = getString(R.string.transaction_deleted)
```

## Navigation

### Navigate to Fragment
```kotlin
findNavController().navigate(R.id.action_dashboard_to_transaction)
```

### Navigate with Arguments
```kotlin
val bundle = bundleOf("transactionId" to transactionId)
findNavController().navigate(R.id.action_list_to_detail, bundle)
```

### Get Arguments
```kotlin
val transactionId = arguments?.getLong("transactionId") ?: 0L
```

## Error Handling

### Try-Catch Pattern
```kotlin
fun saveTransaction(transaction: Transaction) = viewModelScope.launch {
    try {
        repository.insert(transaction)
        _saveStatus.value = Success("Transaction saved")
    } catch (e: Exception) {
        _saveStatus.value = Error(e.message ?: "Failed to save")
        Log.e("TransactionVM", "Error saving transaction", e)
    }
}
```

## Resource IDs

### Colors
```xml
@color/primary
@color/success
@color/error
@color/income_green
@color/expense_red
```

### Dimensions (add to dimens.xml if needed)
```xml
<dimen name="padding_small">8dp</dimen>
<dimen name="padding_medium">16dp</dimen>
<dimen name="padding_large">24dp</dimen>
```

## Quick Fixes

### "Cannot resolve symbol R"
1. Build → Clean Project
2. Build → Rebuild Project
3. File → Invalidate Caches and Restart

### "Room schema export directory not set"
Add to build.gradle.kts:
```kotlin
android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }
}
```

### "Unresolved reference: databinding"
Enable in build.gradle.kts:
```kotlin
android {
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}
```

---

For more details, see:
- [ARCHITECTURE.md](ARCHITECTURE.md) - Architecture details
- [DOCUMENTATION.md](DOCUMENTATION.md) - Full documentation
- [CONTRIBUTING.md](CONTRIBUTING.md) - Contribution guidelines
