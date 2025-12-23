# Contributing to FinancialTrack

Thank you for contributing to FinancialTrack! This document provides guidelines for contributing to the project.

## Development Workflow

### Branch Strategy

We use a GitFlow-inspired workflow:

```
main (stable releases)
  └── dev (integration)
       ├── feature/auth-module
       ├── feature/dashboard
       ├── feature/transaction-module
       ├── feature/budget-management
       ├── feature/reports-analytics
       ├── feature/user-profile
       ├── feature/notifications
       └── feature/debt-tracker
```

### Branches

- **`main`** - Production-ready code only
  - Always stable
  - Tagged releases
  - Protected branch (requires PR review)

- **`dev`** - Integration branch
  - Features are merged here first
  - Testing happens here
  - Merge to `main` after thorough testing

- **`feature/<name>`** - Feature development
  - Created from `dev`
  - One feature per branch
  - Merged back to `dev` via Pull Request

### Getting Started

1. **Fork or Clone the Repository**
   ```bash
   git clone https://github.com/xrxscys/FinancialTrack.git
   cd FinancialTrack
   ```

2. **Checkout dev branch**
   ```bash
   git checkout dev
   ```

3. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

### Making Changes

1. **Follow the MVVM Architecture**
   - Models in `data/model/`
   - Database operations in `data/database/`
   - Repositories in `data/repository/`
   - UI logic in ViewModels
   - UI components in respective module folders

2. **Code Style**
   - Follow Kotlin coding conventions
   - Use meaningful names for variables and functions
   - Add KDoc comments for public APIs
   - Keep functions focused and small
   - Use dependency injection where appropriate

3. **Commit Messages**
   - Use clear, descriptive commit messages
   - Format: `[Module] Brief description`
   - Examples:
     ```
     [Auth] Add Google Sign-In integration
     [Transaction] Implement transaction list view
     [Budget] Add budget alert notifications
     ```

4. **Testing**
   - Write unit tests for ViewModels
   - Test repository logic
   - Add UI tests for critical user flows
   - Run tests before pushing: `./gradlew test`

### Pull Request Process

1. **Before Creating PR**
   - Update from `dev`: `git pull origin dev`
   - Resolve any conflicts
   - Run tests: `./gradlew test`
   - Build successfully: `./gradlew build`
   - Lint your code

2. **Create Pull Request**
   - Push your branch: `git push origin feature/your-feature-name`
   - Go to GitHub and create PR to `dev` branch
   - Fill in the PR template:
     - Description of changes
     - Related issues
     - Screenshots (for UI changes)
     - Testing done

3. **PR Review**
   - Address review comments
   - Keep commits organized
   - Update PR description if scope changes

4. **After Merge**
   - Delete feature branch
   - Pull latest `dev`
   - Start next feature

### Module Development Guidelines

#### Authentication Module
- Use Firebase Authentication
- Implement Google Sign-In
- Handle auth state changes
- Store user data locally

#### Dashboard Module
- Display financial summary
- Show recent transactions
- Calculate balance from Room database
- Use LiveData for reactive updates

#### Transaction Module
- CRUD operations for transactions
- Category selection
- Date picker for transaction date
- Income/Expense toggle

#### Budget Module
- Set budgets by category
- Track spending against budget
- Alert when approaching limit
- Support different time periods

#### Reports Module
- Integrate MPAndroidChart
- Category-wise breakdown
- Monthly/Yearly reports
- Export functionality (future)

#### Profile Module
- Display user information
- Edit profile
- App settings
- Logout functionality

#### Notification Module
- Budget alerts
- Debt reminders
- Transaction notifications
- Notification preferences

#### Debt & Loan Tracker
- Add/Edit/Delete debts
- Track payment progress
- Calculate interest
- Due date reminders

### Code Review Checklist

- [ ] Code follows MVVM architecture
- [ ] No hardcoded strings (use strings.xml)
- [ ] Proper error handling
- [ ] No memory leaks (ViewModels, coroutines)
- [ ] Database operations on background thread
- [ ] LiveData used for UI updates
- [ ] Comments for complex logic
- [ ] Tests included for new features
- [ ] UI is responsive and user-friendly
- [ ] No unused imports or code

### Firebase Configuration

- Never commit `google-services.json` (it's in .gitignore)
- Use `google-services.json.template` as reference
- Document any Firebase changes in PR

### Database Migrations

- Plan schema changes carefully
- Create migration strategy
- Test migration thoroughly
- Document in PR description

### Issue Reporting

1. **Bug Reports**
   - Clear title describing the issue
   - Steps to reproduce
   - Expected vs actual behavior
   - Screenshots if applicable
   - Device and Android version

2. **Feature Requests**
   - Clear description of the feature
   - Use cases
   - Mockups if available
   - Priority level

### Getting Help

- Check DOCUMENTATION.md
- Review existing code for patterns
- Ask in PR comments
- Open a discussion issue

## Release Process

1. **Prepare Release**
   - Merge `dev` into `main`
   - Update version in `build.gradle.kts`
   - Update CHANGELOG.md

2. **Tag Release**
   ```bash
   git tag -a v1.0.0 -m "Release version 1.0.0"
   git push origin v1.0.0
   ```

3. **Build Release APK**
   ```bash
   ./gradlew assembleRelease
   ```

## Questions?

If you have questions about contributing, please:
1. Check existing documentation
2. Review similar PRs
3. Open a discussion issue
4. Contact the maintainers

Thank you for contributing to FinancialTrack! 🎉
