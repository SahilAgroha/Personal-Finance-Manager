# Personal Finance Manager

A secure RESTful Personal Finance Manager built with **Java 21** and **Spring Boot 4.1.1**. The application allows authenticated users to manage income and expenses, organize transactions with default and custom categories, track savings goals, and generate monthly and yearly financial reports.

The project was implemented as a backend-focused assignment with an emphasis on clean architecture, validation, user-level data isolation, session-based authentication, business-rule enforcement, automated testing, and API-level end-to-end validation.

---

## ✨ Features

### 🔐 Authentication & User Management
- User registration with:
  - Email/username validation
  - Password validation
  - Full name
  - Phone number
- Login with username/email and password
- Session-based authentication using secure cookies
- Logout and session invalidation
- Protected API endpoints
- User-level data isolation
- Duplicate registration prevention
- Authentication and authorization error handling

### 💰 Transaction Management
- Create income and expense transactions
- Retrieve all transactions
- Retrieve a transaction by ID
- Update transaction details
- Delete transactions
- Filter transactions by:
  - Category
  - Date range
  - Category + date range
- Validation for:
  - Positive transaction amounts
  - Valid dates
  - Existing categories
  - Required request fields
- Transaction dates are immutable during update
- Deleted transactions no longer affect reports or goal progress

### 🏷️ Category Management
- Built-in/default categories
- User-specific custom categories
- Create custom income and expense categories
- Prevent duplicate category names
- Prevent deletion of default categories
- Prevent deletion of categories currently used by transactions
- Custom categories are isolated between users

### 🎯 Savings Goals
- Create savings goals
- Optional start date with automatic default to the current date
- Target amount and target date validation
- Retrieve all goals
- Retrieve a goal by ID
- Update goal target amount/date
- Delete goals
- Dynamic progress calculation based on transactions
- Progress percentage calculation
- Remaining amount calculation
- Progress is automatically updated when transactions are added, modified, or deleted
- Goals are isolated between users

### 📊 Reports & Analytics
- Monthly financial reports
- Yearly financial reports
- Income grouped by category
- Expenses grouped by category
- Net savings calculation
- Reports reflect transaction deletions and updates
- Reports are restricted to the authenticated user's data

### 🧪 Testing
- JUnit 5 unit tests
- Mockito-based dependency mocking
- Service-layer testing
- Validation and exception-path testing
- Security-related testing
- JaCoCo code coverage reporting
- Comprehensive E2E API test script covering authentication, transactions, categories, goals, reports, data isolation, and complete user journeys

---

## 🏗️ Architecture

The application follows a layered Spring Boot architecture:

```text
                    ┌──────────────────────┐
                    │       REST API       │
                    │     Controllers      │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │       Services       │
                    │ Business Logic /     │
                    │ Validation / Rules   │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │     Repositories     │
                    │    Spring Data JPA   │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │      PostgreSQL      │
                    └──────────────────────┘
```

### Main design principles

- **Controller layer** — handles HTTP requests/responses and request validation.
- **Service layer** — contains business rules and application logic.
- **Repository layer** — handles persistence through Spring Data JPA.
- **DTOs** — keep API contracts separate from database entities.
- **Global exception handling** — provides consistent API error responses.
- **Security configuration** — controls authentication and protected endpoints.
- **Configuration externalization** — database and environment-specific settings are kept outside application logic.

This structure keeps responsibilities separated and makes the application easier to test and maintain.

---

## 🛠️ Technology Stack

| Area | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 |
| Web | Spring Web / REST |
| Security | Spring Security |
| Persistence | Spring Data JPA |
| ORM | Hibernate ORM |
| Database | PostgreSQL |
| Database Driver | PostgreSQL JDBC |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| Testing | JUnit 5 |
| Mocking | Mockito |
| Coverage | JaCoCo |
| API Testing | cURL / provided Bash E2E test suite |
| Application Server | Apache Tomcat 11 |

---

## 📁 Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── sahil/
│   │           └── personalfinancemanager/
│   │               ├── config/
│   │               ├── controller/
│   │               ├── dto/
│   │               │   ├── auth/
│   │               │   ├── category/
│   │               │   ├── goal/
│   │               │   ├── report/
│   │               │   └── transaction/
│   │               ├── entity/
│   │               ├── exception/
│   │               ├── repository/
│   │               ├── security/
│   │               ├── service/
│   │               └── PersonalFinanceManagerApplication.java
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/
        └── com/
            └── sahil/
                └── personalfinancemanager/
                    └── ...
```

---

## 🔒 Security Model

The application uses **session-based authentication** rather than JWT authentication, matching the assignment requirements.

### Authentication flow

```text
Client
  │
  │ POST /api/auth/login
  ▼
Authentication Controller
  │
  ▼
Authentication Service
  │
  ├── Validate credentials
  ├── Verify password
  └── Create authenticated session
  │
  ▼
Secure Session Cookie
  │
  ▼
Protected API Requests
```

Registration and login are publicly accessible. Other application endpoints require an authenticated session.

When a user logs out, the session is invalidated. Attempts to access protected endpoints without a valid session are rejected.

### Data isolation

Every user-owned resource is resolved using the currently authenticated user.

For example:

```text
User A
 ├── Transactions
 ├── Custom Categories
 └── Goals

User B
 ├── Transactions
 ├── Custom Categories
 └── Goals
```

User A cannot read, update, or delete User B's transactions, goals, or custom categories.

---

# 📚 API Documentation

Base URL for local development:

```text
http://localhost:8080/api
```

All endpoints below require authentication unless explicitly marked **Public**.

---

## 1. Authentication

### Register User — Public

```http
POST /api/auth/register
Content-Type: application/json
```

Request:

```json
{
  "username": "user@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phoneNumber": "+1234567890"
}
```

Successful response:

```json
{
  "message": "User registered successfully",
  "userId": 1
}
```

Typical statuses:

- `201 Created`
- `400 Bad Request`
- `409 Conflict`

---

### Login — Public

```http
POST /api/auth/login
Content-Type: application/json
```

Request:

```json
{
  "username": "user@example.com",
  "password": "password123"
}
```

Successful response:

```json
{
  "message": "Login successful"
}
```

The authenticated session is maintained using a cookie.

Typical statuses:

- `200 OK`
- `400 Bad Request`
- `401 Unauthorized`

---

### Logout

```http
POST /api/auth/logout
```

Successful response:

```json
{
  "message": "Logout successful"
}
```

The session is invalidated and subsequent protected requests are rejected.

---

# 2. Transactions

### Create Transaction

```http
POST /api/transactions
Content-Type: application/json
```

Request:

```json
{
  "amount": 5000.00,
  "date": "2024-01-15",
  "category": "Salary",
  "description": "January Salary"
}
```

Response:

```json
{
  "id": 1,
  "amount": 5000.00,
  "date": "2024-01-15",
  "category": "Salary",
  "description": "January Salary",
  "type": "INCOME"
}
```

The transaction type is determined from the category.

---

### Get All Transactions

```http
GET /api/transactions
```

Response:

```json
{
  "transactions": [
    {
      "id": 1,
      "amount": 5000.00,
      "date": "2024-01-15",
      "category": "Salary",
      "description": "January Salary",
      "type": "INCOME"
    }
  ]
}
```

---

### Filter by Category

```http
GET /api/transactions?category=Salary
```

---

### Filter by Date Range

```http
GET /api/transactions?startDate=2024-01-01&endDate=2024-01-31
```

---

### Filter by Category and Date Range

```http
GET /api/transactions?startDate=2024-01-01&endDate=2024-01-31&category=Food
```

Date filtering requires both `startDate` and `endDate`.

---

### Get Transaction by ID

```http
GET /api/transactions/{id}
```

Example:

```http
GET /api/transactions/1
```

---

### Update Transaction

```http
PUT /api/transactions/{id}
Content-Type: application/json
```

Request:

```json
{
  "amount": 5500.00,
  "description": "Updated January Salary"
}
```

The transaction date is intentionally not changed by an update request.

---

### Delete Transaction

```http
DELETE /api/transactions/{id}
```

Response:

```json
{
  "message": "Transaction deleted successfully"
}
```

---

# 3. Categories

### Get Categories

```http
GET /api/categories
```

Response:

```json
{
  "categories": [
    {
      "name": "Salary",
      "type": "INCOME",
      "isCustom": false
    },
    {
      "name": "Food",
      "type": "EXPENSE",
      "isCustom": false
    }
  ]
}
```

The response contains the user's available default and custom categories.

---

### Create Custom Category

```http
POST /api/categories
Content-Type: application/json
```

Request:

```json
{
  "name": "Freelance",
  "type": "INCOME"
}
```

A custom category belongs only to the authenticated user.

---

### Update Custom Category

```http
PUT /api/categories/{name}
Content-Type: application/json
```

The category update operation is restricted to the authenticated user's custom categories.

---

### Delete Custom Category

```http
DELETE /api/categories/{name}
```

A custom category cannot be deleted while it is being used by an existing transaction.

Default categories cannot be deleted.

---

# 4. Savings Goals

### Create Goal

```http
POST /api/goals
Content-Type: application/json
```

Request:

```json
{
  "goalName": "Emergency Fund",
  "targetAmount": 10000.00,
  "targetDate": "2027-01-01",
  "startDate": "2024-01-01"
}
```

`startDate` is optional. When omitted, it defaults to the goal creation date.

Response:

```json
{
  "id": 1,
  "goalName": "Emergency Fund",
  "targetAmount": 10000.00,
  "targetDate": "2027-01-01",
  "startDate": "2024-01-01",
  "currentProgress": 6550.00,
  "progressPercentage": 65.5,
  "remainingAmount": 3450.00
}
```

---

### Get All Goals

```http
GET /api/goals
```

Returns all goals belonging to the authenticated user with dynamically calculated progress.

---

### Get Goal by ID

```http
GET /api/goals/{id}
```

---

### Update Goal

```http
PUT /api/goals/{id}
Content-Type: application/json
```

Example:

```json
{
  "targetAmount": 15000.00
}
```

Only supported goal fields are updated.

---

### Delete Goal

```http
DELETE /api/goals/{id}
```

Response:

```json
{
  "message": "Goal deleted successfully"
}
```

---

## Goal Progress Calculation

Goal progress is based on the user's financial activity from the goal start date through the applicable end date.

```text
Current Progress
    =
Total Income
    -
Total Expenses
```

Progress percentage:

```text
Progress Percentage
    =
(Current Progress / Target Amount) × 100
```

The calculated percentage is capped at `100%`, and negative progress does not produce a negative percentage.

Remaining amount:

```text
Remaining Amount
    =
Target Amount - Current Progress
```

The remaining amount cannot be negative.

Because progress is calculated from transactions rather than stored as a fixed value, adding or deleting transactions automatically changes the goal's progress.

---

# 5. Reports

## Monthly Report

```http
GET /api/reports/monthly/{year}/{month}
```

Example:

```http
GET /api/reports/monthly/2024/1
```

Response:

```json
{
  "month": 1,
  "year": 2024,
  "totalIncome": {
    "Salary": 3000.00,
    "Freelance": 500.00
  },
  "totalExpenses": {
    "Food": 400.00,
    "Rent": 1200.00
  },
  "netSavings": 1900.00
}
```

---

## Yearly Report

```http
GET /api/reports/yearly/{year}
```

Example:

```http
GET /api/reports/yearly/2024
```

Response:

```json
{
  "year": 2024,
  "totalIncome": {
    "Salary": 36000.00,
    "Freelance": 6000.00
  },
  "totalExpenses": {
    "Food": 4800.00,
    "Rent": 14400.00
  },
  "netSavings": 22800.00
}
```

Reports are generated from the authenticated user's transactions only.

---

# 🗄️ Database

The application uses PostgreSQL.

Local database configuration used during development:

```text
Database: personal_finance_manager
Host: localhost
Port: 5432
Schema: public
```

Create the database using PostgreSQL:

```sql
CREATE DATABASE personal_finance_manager;
```

The application uses Spring Data JPA and Hibernate for database access.

Do not commit database passwords, API keys, or other credentials to Git.

---

# 🚀 Running the Application

## Prerequisites

Install:

- Java 21 or newer
- Maven
- PostgreSQL
- Git

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

Verify PostgreSQL is running and the `personal_finance_manager` database exists.

---

## 1. Clone the Repository

```bash
git clone <repository-url>
cd personal-finance-manager
```

If the project has already been downloaded, simply open the project directory in IntelliJ IDEA or another Java IDE.

---

## 2. Configure PostgreSQL

Create the database:

```sql
CREATE DATABASE personal_finance_manager;
```

Configure the PostgreSQL connection in the application's external configuration with your local PostgreSQL username and password.

Keep credentials outside version control.

---

## 3. Build the Project

```bash
mvn clean install
```

---

## 4. Run the Application

```bash
mvn spring-boot:run
```

The application starts on:

```text
http://localhost:8080
```

The API base URL is:

```text
http://localhost:8080/api
```

---

# 🧪 Testing

The project contains unit tests using **JUnit 5** and **Mockito**.

Run the complete test suite:

```bash
mvn clean test
```

Generate JaCoCo coverage:

```bash
mvn clean test jacoco:report
```

The HTML coverage report is generated under:

```text
target/site/jacoco/index.html
```

Open that file in a browser to inspect class, method, line, and branch coverage.

### Testing strategy

The tests cover:

- Authentication success and failure
- User-not-found scenarios
- Registration validation
- Transaction CRUD
- Transaction filtering
- Date validation
- Category validation
- Custom category management
- Category usage/deletion rules
- Savings goal validation
- Goal progress calculations
- Goal updates and deletion
- Report calculations
- User authentication edge cases
- Data access restrictions
- Exception handling

The assignment requires a minimum of **80% code coverage**. The test suite is designed around the business logic and error paths rather than only testing successful requests.

---

# 🔬 End-to-End API Testing

The repository includes the provided:

```text
financial_manager_tests.sh
```

The script performs comprehensive API-level validation, including:

1. User registration
2. Duplicate registration prevention
3. Login and failed login attempts
4. Session-based authorization
5. Logout/session invalidation
6. Transaction creation
7. Transaction validation
8. Transaction filtering
9. Transaction updates
10. Transaction deletion
11. Category management
12. Custom category validation
13. Category usage/deletion rules
14. Savings goal creation
15. Goal validation
16. Goal progress calculation
17. Goal updates/deletion
18. Goal/report consistency after transaction deletion
19. Monthly reports
20. Yearly reports
21. User data isolation
22. Cross-user access prevention
23. Complete end-to-end user journey

The provided test suite contains **86 test/validation checks** across these scenarios.

Run it against the local API:

```bash
bash financial_manager_tests.sh http://localhost:8080/api
```

On Linux/macOS, it can also be made executable:

```bash
chmod +x financial_manager_tests.sh
./financial_manager_tests.sh http://localhost:8080/api
```

The script uses cookie files to maintain independent sessions for multiple users and verifies that data created by one user is not exposed to another user.

---

# 🧩 Validation & Error Handling

The application validates incoming requests before business logic is executed.

Examples include:

- Invalid email addresses
- Missing required fields
- Invalid passwords
- Invalid phone numbers
- Negative transaction amounts
- Zero transaction amounts
- Future transaction dates
- Invalid date formats
- Missing date-range boundaries
- Invalid date ranges
- Invalid category types
- Duplicate categories
- Non-existent categories
- Invalid goal amounts
- Past goal target dates
- Invalid goal date ranges
- Access to non-existent resources
- Cross-user resource access
- Unauthorized requests

A global exception-handling mechanism provides consistent API responses for validation and application errors.

---

# 🧠 Important Design Decisions

## Session Authentication Instead of JWT

The assignment specifically requires session-based authentication with secure cookies. Therefore, authentication state is maintained through the server-side session rather than a stateless JWT-only design.

## DTOs Instead of Exposing Entities

Request and response DTOs are used to keep the API contract separate from persistence entities. This avoids coupling external clients directly to the database model.

## Business Logic in Services

Controllers remain thin. Calculations such as goal progress, remaining amounts, report aggregation, category rules, and ownership checks are handled by service classes.

## User Isolation at the Service/Repository Boundary

User-owned records are always resolved in the context of the authenticated user. This prevents a user from accessing another user's resources even when they know a resource ID.

## Dynamic Goal Progress

Goal progress is calculated from transactions instead of being permanently stored. This keeps goals consistent when transactions are created, updated, or deleted.

## Category Protection

Default categories are system-managed and cannot be deleted. Custom categories are user-owned, and categories currently referenced by transactions cannot be removed.

---

# 📈 Data Consistency

A key part of the implementation is maintaining consistency between transactions, goals, and reports.

For example:

```text
Create Transaction
       │
       ├──────────────► Transaction list changes
       │
       ├──────────────► Goal progress changes
       │
       └──────────────► Monthly/Yearly reports change


Delete Transaction
       │
       ├──────────────► Transaction removed
       │
       ├──────────────► Goal progress recalculated
       │
       └──────────────► Reports recalculated
```

This behavior is explicitly validated by the E2E test suite.

---

# 🛡️ Security Considerations

- Passwords are not stored in plain text.
- Authentication is required for protected endpoints.
- Sessions are invalidated on logout.
- User-owned resources are scoped to the authenticated user.
- Cross-user update/delete attempts are rejected.
- Input validation is applied to API requests.
- Database credentials should be supplied through environment/external configuration.
- Secrets and credentials must not be committed to Git.
- Error handling avoids exposing unnecessary internal implementation details.

---

# 📋 Assignment Requirements Coverage

| Requirement | Implementation |
|---|---|
| User registration | ✅ |
| Login | ✅ |
| Session-based authentication | ✅ |
| Secure cookies/session management | ✅ |
| Logout | ✅ |
| User data isolation | ✅ |
| Transaction CRUD | ✅ |
| Transaction filtering | ✅ |
| Default categories | ✅ |
| Custom categories | ✅ |
| Category validation | ✅ |
| Category usage protection | ✅ |
| Savings goals | ✅ |
| Goal progress calculation | ✅ |
| Monthly reports | ✅ |
| Yearly reports | ✅ |
| Layered architecture | ✅ |
| DTOs | ✅ |
| Global exception handling | ✅ |
| Externalized configuration | ✅ |
| JUnit 5 tests | ✅ |
| Mockito mocking | ✅ |
| JaCoCo coverage | ✅ |
| E2E API test suite | ✅ |
| Comprehensive README | ✅ |

---

# 🔄 Typical User Workflow

```text
Register
   │
   ▼
Login
   │
   ▼
Receive authenticated session
   │
   ├──────────────► View default categories
   │
   ├──────────────► Create custom categories
   │
   ├──────────────► Add income/expenses
   │
   ├──────────────► Filter transactions
   │
   ├──────────────► Create savings goals
   │
   ├──────────────► Track goal progress
   │
   ├──────────────► Generate monthly reports
   │
   ├──────────────► Generate yearly reports
   │
   └──────────────► Logout
                         │
                         ▼
                  Session invalidated
```

---

# 📝 Example Financial Flow

Suppose a user has:

```text
Income:
Salary       = ₹5,500
Freelance    = ₹1,500

Expenses:
Food         = ₹450
```

Then:

```text
Total Income  = ₹7,000
Total Expense = ₹450

Net Savings
= ₹7,000 - ₹450
= ₹6,550
```

For a goal with a target of ₹10,000:

```text
Progress
= ₹6,550 / ₹10,000 × 100
= 65.5%

Remaining
= ₹10,000 - ₹6,550
= ₹3,450
```

The same underlying transaction data is used by both the goal calculation and financial reports, keeping the results consistent.

---

# 🏁 Submission Checklist

Before submitting the repository:

- [ ] Source code committed
- [ ] Unit tests committed
- [ ] JaCoCo coverage checked
- [ ] `financial_manager_tests.sh` included
- [ ] README included
- [ ] No passwords or secrets committed
- [ ] `target/` excluded from Git
- [ ] `.env`/secret configuration excluded from Git
- [ ] PostgreSQL connection verified
- [ ] Application starts successfully
- [ ] API endpoints verified
- [ ] E2E tests executed against the final build
- [ ] Deployment URL tested if the application is deployed

---

# 👨‍💻 Author

**Sahil**

B.Tech — Information Technology  
IIIT Bhopal

---

## License

This project was developed as an academic/system-design assignment for demonstrating REST API development, backend architecture, authentication, database persistence, business logic, testing, and API validation.
