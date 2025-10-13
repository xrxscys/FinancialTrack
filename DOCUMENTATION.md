# FinancialTrack - Project Documentation

## Module Overview

### Data Layer (`data/`)

#### Models (`data/model/`)
- **User.kt** - User authentication data
- **Transaction.kt** - Financial transaction records with type (INCOME/EXPENSE)
- **Budget.kt** - Budget tracking with periods (DAILY/WEEKLY/MONTHLY/YEARLY)
- **Debt.kt** - Debt and loan tracking with interest rates
- **Notification.kt** - In-app notification system

#### Database (`data/database/`)
- **AppDatabase.kt** - Room database configuration
- **UserDao.kt** - User data access operations
- **TransactionDao.kt** - Transaction CRUD operations
- **BudgetDao.kt** - Budget management operations
- **DebtDao.kt** - Debt tracking operations
- **NotificationDao.kt** - Notification operations

#### Repositories (`data/repository/`)
Repository pattern implementation for each data type, providing a clean API for ViewModels.

### UI Layer (`ui/`)

Each module follows MVVM pattern:
- ViewModel handles business logic
- Future Activities/Fragments will handle UI
- LiveData for reactive updates

#### Modules:
1. **auth/** - Authentication with Firebase & Google Sign-In
2. **dashboard/** - Main overview screen
3. **transaction/** - Transaction management
4. **budget/** - Budget setting and tracking
5. **reports/** - Analytics and charts (MPAndroidChart)
6. **profile/** - User profile management
7. **notification/** - Notification center
8. **debt/** - Debt & loan tracker

### Utilities (`utils/`)
- **Converters.kt** - Room type converters for enums
- **Constants.kt** - App-wide constants and categories
- **FormatUtils.kt** - Date and currency formatting

## Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select existing
3. Add Android app with package: `com.example.financialtrack`
4. Download `google-services.json`
5. Copy template: `cp app/google-services.json.template app/google-services.json`
6. Update with your actual Firebase configuration
7. Enable Authentication → Google Sign-In

## Database Schema

### Users Table
- id (PrimaryKey)
- email
- displayName
- photoUrl
- createdAt

### Transactions Table
- id (AutoGenerate)
- userId
- amount
- type (INCOME/EXPENSE)
- category
- description
- date

### Budgets Table
- id (AutoGenerate)
- userId
- category
- amount
- period (DAILY/WEEKLY/MONTHLY/YEARLY)
- startDate
- endDate

### Debts Table
- id (AutoGenerate)
- userId
- creditorName
- amount
- amountPaid
- dueDate
- interestRate
- type (LOAN/DEBT/CREDIT_CARD)
- description

### Notifications Table
- id (AutoGenerate)
- userId
- title
- message
- type (BUDGET_ALERT/DEBT_REMINDER/TRANSACTION_ALERT/GENERAL)
- isRead
- createdAt

## Adding New Features

### Adding a new transaction category:
1. Update `Constants.kt` - Add to INCOME_CATEGORIES or EXPENSE_CATEGORIES
2. No database changes needed - categories are stored as strings

### Adding a new budget period:
1. Update `BudgetPeriod` enum in `Budget.kt`
2. Update `Converters.kt` if needed
3. Rebuild database schema

### Creating a new screen:
1. Create Fragment in appropriate module folder
2. Add to navigation graph
3. Use existing ViewModel or create new one
4. Follow MVVM pattern

## Development Guidelines

### Code Style
- Follow Kotlin conventions
- Use meaningful variable names
- Add comments for complex logic
- Keep functions small and focused

### Testing
- Write unit tests for ViewModels
- Test Repository logic
- UI tests for critical flows

### Git Workflow
1. Pull latest from `dev`
2. Create feature branch: `feature/your-feature`
3. Make changes and commit
4. Push and create PR to `dev`
5. After review and testing, merge to `main`

## Dependencies Management

All dependencies are managed in `app/build.gradle.kts`:
- Update versions carefully
- Test after dependency updates
- Check for breaking changes

## Troubleshooting

### Build Errors
- Clean project: `./gradlew clean`
- Rebuild: `./gradlew build`
- Invalidate caches in Android Studio

### Firebase Issues
- Verify `google-services.json` is in `app/` directory
- Check package name matches Firebase console
- Ensure Firebase Authentication is enabled

### Database Issues
- Uninstall and reinstall app
- Check Room schema version
- Verify migration strategy

## Next Steps

Priority implementations:
1. Complete UI for all modules (Activities/Fragments)
2. Implement Firebase Authentication flow
3. Add data validation and error handling
4. Implement chart visualization in Reports module
5. Add notification scheduling
6. Implement data backup/restore
7. Add dark theme support
8. Localization for multiple languages
