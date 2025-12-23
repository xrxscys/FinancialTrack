# FinancialTrack

A comprehensive personal finance management Android application built with Kotlin and MVVM architecture.

## 🏗️ Architecture

This project follows the **MVVM (Model-View-ViewModel)** architecture pattern with clean separation of concerns:

- **Model**: Data layer containing Room database entities, DAOs, and repositories
- **View**: UI layer with Activities and Fragments
- **ViewModel**: Business logic layer that connects the View and Model

## 📋 Team Workflow

We follow a GitFlow-inspired branching strategy:

- **`main`** branch → Stable production builds
- **`dev`** branch → Integration branch for features
- **`feature/<name>`** → Feature development branches (per module)

### Development Process

1. Create a feature branch from `dev`: `git checkout -b feature/transaction-module`
2. Develop and test your changes
3. Create a Pull Request to merge into `dev`
4. After testing in `dev`, merge to `main` for release

## 🛠️ Tech Stack

### Core Technologies
- **Kotlin** - Primary programming language
- **Android SDK** - Android app development
- **MVVM Pattern** - Architecture pattern

### Android Jetpack Components
- **Room Database** - Local data persistence
- **LiveData** - Observable data holder
- **ViewModel** - UI-related data holder
- **Lifecycle** - Lifecycle-aware components
- **ViewBinding & DataBinding** - Type-safe view access

### Authentication
- **Firebase Authentication** - User authentication
- **Google Sign-In** - Social authentication

### Data Visualization
- **MPAndroidChart** - Charts and graphs for reports

### Concurrency
- **Kotlin Coroutines** - Asynchronous programming

### Navigation
- **Navigation Component** - In-app navigation

## 📦 Modules

The application is organized into 8 functional modules:

### 1. 🔐 User Authentication
- Firebase Authentication integration
- Google Sign-In support
- User session management
- **Package**: `ui.auth`

### 2. 📊 Dashboard
- Overview of financial status
- Total balance display
- Income vs Expense summary
- Recent transactions
- **Package**: `ui.dashboard`

### 3. 💰 Transaction
- Add, edit, delete transactions
- Income and expense tracking
- Transaction categorization
- Transaction history
- **Package**: `ui.transaction`

### 4. 💵 Budget Management
- Set budgets by category
- Track budget usage
- Budget alerts and notifications
- Period-based budgets (daily, weekly, monthly, yearly)
- **Package**: `ui.budget`

### 5. 📈 Reports and Analytics
- Visual charts and graphs
- Category-wise breakdown
- Monthly/yearly reports
- Spending trends analysis
- **Package**: `ui.reports`

### 6. 👤 User Profile
- User information management
- Profile editing
- Settings and preferences
- **Package**: `ui.profile`

### 7. 🔔 Notification
- Budget alerts
- Debt reminders
- Transaction notifications
- **Package**: `ui.notification`

### 8. 💳 Debt & Loan Tracker
- Track debts and loans
- Due date reminders
- Payment tracking
- Interest rate calculations
- **Package**: `ui.debt`

## 🗂️ Project Structure

```
app/
├── src/main/
│   ├── java/com/example/financialtrack/
│   │   ├── data/
│   │   │   ├── database/         # Room Database, DAOs
│   │   │   ├── model/            # Data entities
│   │   │   └── repository/       # Repository pattern
│   │   ├── ui/
│   │   │   ├── auth/             # Authentication module
│   │   │   ├── dashboard/        # Dashboard module
│   │   │   ├── transaction/      # Transaction module
│   │   │   ├── budget/           # Budget module
│   │   │   ├── reports/          # Reports module
│   │   │   ├── profile/          # Profile module
│   │   │   ├── notification/     # Notification module
│   │   │   └── debt/             # Debt & Loan module
│   │   └── utils/                # Utility classes
│   └── res/                      # Resources (layouts, values, etc.)
└── build.gradle.kts
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or newer
- JDK 8 or higher
- Android SDK (API 24+)
- Firebase account (for authentication)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/xrxscys/FinancialTrack.git
   cd FinancialTrack
   ```

2. **Configure Firebase**
   - Create a new Firebase project
   - Add an Android app to your Firebase project
   - Download `google-services.json`
   - Place it in the `app/` directory
   - Enable Firebase Authentication and Google Sign-In

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run the app**
   - Open the project in Android Studio
   - Connect an Android device or start an emulator
   - Click "Run" or press Shift+F10

## 📱 Features

- ✅ User authentication with Google Sign-In
- ✅ Transaction management (CRUD operations)
- ✅ Budget tracking and alerts
- ✅ Debt and loan tracking
- ✅ Reports with visual charts
- ✅ Real-time notifications
- ✅ Offline data persistence with Room
- ✅ Material Design UI

## 🧪 Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

## 👥 Contributors

- Team FinancialTrack

## 📞 Support

For support, please open an issue in the GitHub repository.

---

Built with ❤️ using Kotlin and Android Jetpack