# Bank Management System

A comprehensive Java-based Bank Management System with ATM functionality, built using Swing GUI and SQLite database.

## Features

- **User Account Management**
  - Create new accounts with multi-step signup process
  - Personal and financial information collection
  - Unique card number and PIN generation

- **ATM Operations**
  - Secure login with card number and PIN
  - Cash deposits
  - Cash withdrawals
  - Fast cash with preset amounts
  - Balance enquiry
  - PIN change functionality
  - Mini statement with transaction history

- **Database Management**
  - SQLite database for data persistence
  - Secure storage of user information and transactions
  - Database viewer utility for debugging
  - Database initializer for setup

## Technology Stack

- **Language:** Java
- **GUI Framework:** Swing
- **Database:** SQLite
- **JDBC Driver:** sqlite-jdbc
- **Logging:** SLF4J

## Project Structure

```
Bank Management System/
├── src/bank/management/system/
│   ├── Login.java              # Login screen
│   ├── Signup.java             # Sign up - Personal details
│   ├── Signup2.java            # Sign up - Additional details
│   ├── Signup3.java            # Sign up - Account type & services
│   ├── main_Class.java         # Main ATM menu
│   ├── Deposit.java            # Deposit money
│   ├── Withdrawl.java          # Withdraw money
│   ├── FastCash.java           # Quick withdrawal
│   ├── BalanceEnquriy.java     # Check balance
│   ├── Pin.java                # Change PIN
│   ├── mini.java               # Mini statement
│   ├── Connn.java              # Database connection
│   ├── DatabaseInitializer.java # Database setup utility
│   └── DatabaseViewer.java     # Database viewer utility
├── lib/                        # External libraries
│   ├── sqlite-jdbc.jar
│   ├── slf4j-api.jar
│   └── slf4j-simple.jar
├── db/                         # Database files
│   ├── schema.sql              # Database schema
│   └── bank.db                 # SQLite database
└── icon/                       # UI icons and images

```

## Database Schema

The system uses four main tables:

1. **signup** - Personal information
2. **signup_two** - Additional details (religion, category, income, etc.)
3. **account** - Account details with card number and PIN
4. **transactions** - Transaction history (deposits, withdrawals)

## Setup Instructions

1. **Prerequisites**
   - Java JDK 11 or higher
   - SQLite JDBC driver (included in `lib/`)

2. **Database Setup**
   ```bash
   # Run the database initializer
   javac -d . -cp "lib/*" src/bank/management/system/DatabaseInitializer.java
   java -cp ".;lib/*" bank.management.system.DatabaseInitializer
   ```

3. **Compile the Project**
   ```bash
   javac -d . -cp "lib/*" src/bank/management/system/*.java
   ```

4. **Run the Application**
   ```bash
   java -cp ".;lib/*;src" bank.management.system.Login
   ```

## Demo Account

A demo account is automatically created for testing:
- **Card Number:** 1234567890123456
- **PIN:** 1234
- **Initial Balance:** ₹10,000

## Code Quality Improvements

This project has been refactored to fix:
- ✅ Deprecated `JPasswordField.getText()` replaced with `getPassword()`
- ✅ Raw `JComboBox` types parameterized with `JComboBox<String>`
- ✅ Resource leaks fixed with try-with-resources
- ✅ Improved exception handling
- ✅ Removed deprecated printStackTrace() calls
- ✅ Fixed unused imports
- ✅ Proper instance management

## Utilities

### Database Viewer
View all database contents with formatted output:
```bash
java -cp ".;lib/*" bank.management.system.DatabaseViewer
```

### Database Initializer
Reinitialize or verify database setup:
```bash
java -cp ".;lib/*" bank.management.system.DatabaseInitializer
```

## Screenshots

The application features:
- Professional ATM-style interface
- Card-based authentication
- Transaction confirmations
- Real-time balance updates

## License

This project is available under the MIT License.

## Author

**RUSHIKESH CHAMLE**
- GitHub: [@Rushi22007](https://github.com/Rushi22007)
- Location: Pune, Maharashtra
- Email: officialrushikesh22@gmail.com

## Contributing

Contributions, issues, and feature requests are welcome!

## Acknowledgments

- SQLite for the embedded database
- Java Swing for the GUI framework
