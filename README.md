<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=6,11,20&height=220&section=header&text=Personal%20Finance%20Manager&fontSize=42&fontColor=ffffff&animation=fadeIn&fontAlignY=35&desc=Secure%20%C2%B7%20Session-Authenticated%20%C2%B7%20Fully%20Tested%20REST%20API&descAlignY=55&descSize=18" width="100%"/>

<img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=600&size=22&duration=2800&pause=900&color=6DB33F&center=true&vCenter=true&width=780&lines=Track+income%2C+expenses+%26+savings+goals;Session-based+auth+with+secure+cookies;99%25+JUnit+%2B+Mockito+test+coverage;86%2F86+E2E+checks+passing+on+Render+%F0%9F%9A%80" alt="Typing SVG" />

<br/>

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white&style=for-the-badge)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white&style=for-the-badge)](#)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?logo=postgresql&logoColor=white&style=for-the-badge)](#)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white&style=for-the-badge)](#)

[![JUnit5](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5&logoColor=white&style=for-the-badge)](#)
[![Mockito](https://img.shields.io/badge/Mockito-Mocking-78A641?logo=mockito&logoColor=white&style=for-the-badge)](#)
[![Coverage](https://img.shields.io/badge/Coverage-99%25-brightgreen?logo=codecov&logoColor=white&style=for-the-badge)](#)
[![Tests](https://img.shields.io/badge/E2E%20Tests-86%2F86%20Passing-success?logo=checkmarx&logoColor=white&style=for-the-badge)](#)

[![Live Demo](https://img.shields.io/badge/🚀%20LIVE%20DEMO-pfm--83i3.onrender.com-46E3B7?style=for-the-badge)](https://pfm-83i3.onrender.com/api)

<br/>

<img src="https://skillicons.dev/icons?i=java,spring,postgres,maven,git,idea&theme=dark" />

<br/><br/>

**🔗 Live API Base URL** → [`https://pfm-83i3.onrender.com/api`](https://pfm-83i3.onrender.com/api)

</div>

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

## 📖 Table of Contents

<details open>
<summary>Click to expand</summary>

- [🌐 Live Deployment](#-live-deployment)
- [✨ Features](#-features)
- [🏗️ Architecture](#️-architecture)
- [🛠️ Technology Stack](#️-technology-stack)
- [📁 Project Structure](#-project-structure)
- [🔒 Security Model](#-security-model)
- [📚 API Documentation](#-api-documentation)
- [🗄️ Database](#️-database)
- [🚀 Running Locally](#-running-the-application)
- [🧪 Testing — 99% Coverage](#-testing--junit-5--mockito--99-coverage)
- [🔬 End-to-End Testing](#-end-to-end-api-testing)
- [🧩 Validation & Error Handling](#-validation--error-handling)
- [🧠 Design Decisions](#-important-design-decisions)
- [📈 Data Consistency](#-data-consistency)
- [🛡️ Security Considerations](#️-security-considerations)
- [📋 Requirements Coverage](#-assignment-requirements-coverage)
- [📝 Example Financial Flow](#-example-financial-flow)
- [🏁 Submission Checklist](#-submission-checklist)

</details>

<br/>

## 🌐 Live Deployment

<div align="center">

| 🌍 Environment | URL | Status |
|---|---|:---:|
| **Production (Render)** | [`https://pfm-83i3.onrender.com/api`](https://pfm-83i3.onrender.com/api) | ![Status](https://img.shields.io/badge/status-online-brightgreen?style=flat-square) |
| **Local Development** | `http://localhost:8080/api` | ![Local](https://img.shields.io/badge/status-manual-lightgrey?style=flat-square) |

</div>

All endpoints documented below work identically against either host — just swap the base URL.

```bash
# 🔥 Try it live right now
curl -X POST https://pfm-83i3.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user@example.com",
    "password": "password123",
    "fullName": "John Doe",
    "phoneNumber": "+1234567890"
  }'
```

Run the full E2E suite straight against production:

```bash
bash financial_manager_tests.sh https://pfm-83i3.onrender.com/api
```

```text
================================================
TEST EXECUTION SUMMARY
================================================
Base URL: https://pfm-83i3.onrender.com/api
Total Tests Executed: 86
Tests Passed: 86
Tests Failed: 0
Success Rate: 100%
🎉 ALL TESTS PASSED! 🎉
```

> ⚠️ **Cold start note:** Render's free tier spins down idle instances. The first request after inactivity may take 30–60 seconds while the dyno wakes up — subsequent requests are fast.

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

## ✨ Features

<table>
<tr>
<td width="50%" valign="top">

### 🔐 Authentication & User Management
- Email/username, password, full name & phone validation
- Login with username/email + password
- 🍪 Session-based authentication with secure cookies
- Logout with full session invalidation
- Protected API endpoints (all except register/login)
- User-level data isolation
- Duplicate registration prevention
- Robust auth/authz error handling

### 🏷️ Category Management
- 7 built-in default categories (see below)
- User-specific custom categories
- Duplicate category-name prevention
- Default categories can't be deleted or modified
- In-use categories can't be deleted
- Categories fully isolated per user

</td>
<td width="50%" valign="top">

### 💰 Transaction Management
- Full CRUD on income/expense transactions
- Sorted newest-first by default
- Filter by category, date range, and type
- Positive-amount & no-future-date validation
- 🔒 Transaction date is immutable on update
- Deleted transactions excluded from goals & reports

### 🎯 Savings Goals
- Create goals with optional start date (defaults to today)
- Target amount & future-date validation
- 📊 Dynamic progress + percentage calculation
- Remaining-amount calculation (never negative)
- Auto-recalculates on transaction changes
- Goals fully isolated per user

</td>
</tr>
</table>

### 📊 Reports & Analytics
📅 Monthly reports · 📆 Yearly reports · Income & expenses grouped by category · Net savings calculation · Live-reflects deletions/updates · Strictly scoped to the authenticated user

### 🧪 Testing Excellence

<div align="center">

| Metric | Result |
|---|:---:|
| **Unit Test Coverage (JaCoCo)** | ![99%](https://img.shields.io/badge/-99%25-brightgreen?style=for-the-badge) |
| **Framework** | JUnit 5 |
| **Mocking** | Mockito |
| **E2E API Checks** | ![86/86](https://img.shields.io/badge/-86%2F86%20passing-success?style=for-the-badge) |

</div>

JUnit 5 unit tests + Mockito-based mocking cover the service layer, validation logic, exception paths, and security-sensitive flows, backed by a **99% JaCoCo line/branch coverage** — well above the 80% assignment minimum — plus a comprehensive 86-check Bash E2E suite validating the live, deployed API end to end.

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

## 🏗️ Architecture

```text
                    ┌──────────────────────┐
                    │       REST API        │
                    │      Controllers       │
                    └──────────┬─────────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │        Services        │
                    │  Business Logic /      │
                    │  Validation / Rules    │
                    └──────────┬─────────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │     Repositories       │
                    │    Spring Data JPA     │
                    └──────────┬─────────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │      PostgreSQL         │
                    └──────────────────────┘
```

| Layer | Responsibility |
|---|---|
| **Controller** | HTTP request/response handling, request validation |
| **Service** | Business rules and application logic |
| **Repository** | Persistence via Spring Data JPA |
| **DTOs** | Keep API contracts separate from database entities |
| **Exception Handling** | Global `@ControllerAdvice`-based consistent error responses |
| **Security Config** | Authentication and endpoint protection |
| **Configuration** | Environment/DB settings externalized from code |

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

## 🛠️ Technology Stack

<div align="center">

| Area | Technology |
|---|---|
| 🧩 Language | Java 21 |
| 🍃 Framework | Spring Boot 4.1.1 |
| 🌐 Web | Spring Web / REST |
| 🔐 Security | Spring Security |
| 🗃️ Persistence | Spring Data JPA |
| 🔧 ORM | Hibernate ORM |
| 🐘 Database | PostgreSQL |
| 🔌 Database Driver | PostgreSQL JDBC |
| ✅ Validation | Jakarta Bean Validation |
| 📦 Build Tool | Maven |
| 🧪 Testing | JUnit 5 |
| 🎭 Mocking | Mockito |
| 📊 Coverage | JaCoCo (99%) |
| 🧾 API Testing | cURL / Bash E2E test suite |
| 🖥️ App Server | Apache Tomcat 11 |
| ☁️ Deployment | Render |

</div>

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

## 📁 Project Structure

```text
src/
├── main/
│   ├── java/com/sahil/personalfinancemanager/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   │   ├── auth/
│   │   │   ├── category/
│   │   │   ├── goal/
│   │   │   ├── report/
│   │   │   └── transaction/
│   │   ├── entity/
│   │   ├── exception/
│   │   ├── repository/
│   │   ├── security/
│   │   ├── service/
│   │   └── PersonalFinanceManagerApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/sahil/personalfinancemanager/...
```

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

## 🔒 Security Model

Session-based authentication (not JWT), matching assignment requirements.

```text
Client
  │  POST /api/auth/login
  ▼
Auth Controller → Auth Service
  │  ├── Validate credentials
  │  ├── Verify password
  │  └── Create authenticated session
  ▼
Secure Session Cookie 🍪
  ▼
Protected API Requests
```

Registration and login are public; every other endpoint requires an authenticated session. Logout invalidates the session immediately, and subsequent requests without a valid session are rejected with `401 Unauthorized`.

### 🧱 Data isolation

```text
User A                      User B
 ├── Transactions            ├── Transactions
 ├── Custom Categories       ├── Custom Categories
 └── Goals                   └── Goals
```

User A cannot read, update, or delete User B's transactions, goals, or custom categories — attempts return `403 Forbidden`.

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

# 📚 API Documentation

<div align="center">

**Live base URL:** `https://pfm-83i3.onrender.com/api` &nbsp;·&nbsp; **Local base URL:** `http://localhost:8080/api`

</div>

All endpoints require authentication unless marked **Public**.

<details>
<summary><h3>🔑 1. User Management & Authentication</h3></summary>

#### Register User — Public
```http
POST /api/auth/register
Content-Type: application/json
```
```json
{
  "username": "user@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phoneNumber": "+1234567890"
}
```
**Response:**
```json
{ "message": "User registered successfully", "userId": 1 }
```
`201 Created` · `400 Bad Request` · `409 Conflict`

#### Login — Public
```http
POST /api/auth/login
Content-Type: application/json
```
```json
{ "username": "user@example.com", "password": "password123" }
```
**Response:**
```json
{ "message": "Login successful" }
```
Session maintained via cookie. `200 OK` · `401 Unauthorized`

#### Logout
```http
POST /api/auth/logout
```
```json
{ "message": "Logout successful" }
```
`200 OK` · `401 Unauthorized`

</details>

<details>
<summary><h3>💰 2. Transaction Management</h3></summary>

#### Create Transaction
```http
POST /api/transactions
Content-Type: application/json
```
```json
{
  "amount": 50000.00,
  "date": "2024-01-15",
  "category": "Salary",
  "description": "January Salary"
}
```
**Response:**
```json
{
  "id": 1,
  "amount": 50000.00,
  "date": "2024-01-15",
  "category": "Salary",
  "description": "January Salary",
  "type": "INCOME"
}
```
`201 Created` · `400 Bad Request` · `401 Unauthorized`

#### Get / Filter Transactions
```http
GET /api/transactions
GET /api/transactions?category=Salary
GET /api/transactions?startDate=2024-01-01&endDate=2024-01-31
GET /api/transactions?startDate=2024-01-01&endDate=2024-01-31&category=Food
```
Results are sorted newest-first. Date filtering requires both `startDate` and `endDate`. `200 OK` · `401 Unauthorized`

#### Update Transaction
```http
PUT /api/transactions/{id}
```
```json
{ "amount": 60000.00, "description": "Updated January Salary" }
```
> 🔒 The transaction date is intentionally immutable on update.

`200 OK` · `400 Bad Request` · `401 Unauthorized` · `404 Not Found`

#### Delete Transaction
```http
DELETE /api/transactions/{id}
```
```json
{ "message": "Transaction deleted successfully" }
```
`200 OK` · `401 Unauthorized` · `404 Not Found`

</details>

<details>
<summary><h3>🏷️ 3. Category Management</h3></summary>

#### Default Categories

| Type | Categories |
|---|---|
| 💵 INCOME | Salary |
| 💸 EXPENSE | Food, Rent, Transportation, Entertainment, Healthcare, Utilities |

Default categories can never be modified or deleted.

#### Get All Categories
```http
GET /api/categories
```
```json
{
  "categories": [
    { "name": "Salary", "type": "INCOME", "isCustom": false },
    { "name": "Food", "type": "EXPENSE", "isCustom": false },
    { "name": "SideBusinessIncome", "type": "INCOME", "isCustom": true }
  ]
}
```
`200 OK` · `401 Unauthorized`

#### Create Custom Category
```http
POST /api/categories
```
```json
{ "name": "SideBusinessIncome", "type": "INCOME" }
```
**Response:**
```json
{ "name": "SideBusinessIncome", "type": "INCOME", "isCustom": true }
```
`201 Created` · `400 Bad Request` · `401 Unauthorized` · `409 Conflict`

#### Delete Custom Category
```http
DELETE /api/categories/{name}
```
```json
{ "message": "Category deleted successfully" }
```
> A category currently referenced by a transaction cannot be deleted.

`200 OK` · `400 Bad Request` · `401 Unauthorized` · `403 Forbidden` · `404 Not Found`

</details>

<details>
<summary><h3>🎯 4. Savings Goals</h3></summary>

#### Create Goal
```http
POST /api/goals
```
```json
{
  "goalName": "Emergency Fund",
  "targetAmount": 5000.00,
  "targetDate": "2026-01-01",
  "startDate": "2025-01-01"
}
```
`startDate` is optional — defaults to the goal creation date. `targetDate` must be in the future.

**Response:**
```json
{
  "id": 1,
  "goalName": "Emergency Fund",
  "targetAmount": 5000.00,
  "targetDate": "2026-01-01",
  "startDate": "2025-01-01",
  "currentProgress": 1000.00,
  "progressPercentage": 20.0,
  "remainingAmount": 4000.00
}
```
`201 Created` · `400 Bad Request` · `401 Unauthorized`

#### Get All Goals
```http
GET /api/goals
```
`200 OK` · `401 Unauthorized`

#### Get Goal by ID
```http
GET /api/goals/{id}
```
`200 OK` · `400 Bad Request` · `401 Unauthorized` · `403 Forbidden` · `404 Not Found`

#### Update Goal
```http
PUT /api/goals/{id}
```
```json
{ "targetAmount": 6000.00, "targetDate": "2026-02-01" }
```
`200 OK` · `400 Bad Request` · `401 Unauthorized` · `403 Forbidden` · `404 Not Found`

#### Delete Goal
```http
DELETE /api/goals/{id}
```
```json
{ "message": "Goal deleted successfully" }
```
`200 OK` · `401 Unauthorized` · `403 Forbidden` · `404 Not Found`

#### 📐 Goal Progress Calculation

```text
Current Progress    = Total Income − Total Expenses   (since goal start date)
Progress Percentage = (Current Progress / Target Amount) × 100   (capped 0–100%)
Remaining Amount    = Target Amount − Current Progress            (never negative)
```

Because progress is derived from live transaction data, it updates automatically whenever transactions change.

</details>

<details>
<summary><h3>📊 5. Reports & Analytics</h3></summary>

#### Monthly Report
```http
GET /api/reports/monthly/{year}/{month}
```
```json
{
  "month": 1,
  "year": 2024,
  "totalIncome": { "Salary": 3000.00, "Freelance": 500.00 },
  "totalExpenses": { "Food": 400.00, "Rent": 1200.00, "Transportation": 200.00 },
  "netSavings": 1700.00
}
```
`200 OK` · `401 Unauthorized`

#### Yearly Report
```http
GET /api/reports/yearly/{year}
```
```json
{
  "year": 2024,
  "totalIncome": { "Salary": 36000.00, "Freelance": 6000.00 },
  "totalExpenses": { "Food": 4800.00, "Rent": 14400.00, "Transportation": 2400.00 },
  "netSavings": 20400.00
}
```
`200 OK` · `401 Unauthorized`

Reports are generated exclusively from the authenticated user's transactions.

</details>

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

# 🗄️ Database

PostgreSQL, configured locally as:

```text
Database: personal_finance_manager
Host: localhost
Port: 5432
Schema: public
```

```sql
CREATE DATABASE personal_finance_manager;
```

Managed via Spring Data JPA + Hibernate. **Never commit credentials to Git.** In the Render deployment, the database connection is supplied through environment variables rather than `application.properties`.

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

# 🚀 Running the Application

<table>
<tr><td>

**Prerequisites:** Java 21+ · Maven · PostgreSQL · Git

```bash
java -version
mvn -version
```

**1️⃣ Clone**
```bash
git clone <repository-url>
cd personal-finance-manager
```

**2️⃣ Configure PostgreSQL**
```sql
CREATE DATABASE personal_finance_manager;
```
Set your local PostgreSQL credentials in the external configuration — keep them out of version control.

**3️⃣ Build**
```bash
mvn clean install
```

**4️⃣ Run**
```bash
mvn spring-boot:run
```
App starts at `http://localhost:8080`, API base at `http://localhost:8080/api` 🎉

</td></tr>
</table>

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

# 🧪 Testing — JUnit 5 · Mockito · 99% Coverage

<div align="center">

![JUnit5](https://img.shields.io/badge/JUnit%205-Unit%20Testing-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-Dependency%20Mocking-78A641?style=for-the-badge&logo=mockito&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-99%25%20Coverage-brightgreen?style=for-the-badge)

</div>

```bash
mvn clean test
mvn clean test jacoco:report
```

Coverage report: `target/site/jacoco/index.html`

<div align="center">

| Coverage Type | Result |
|---|:---:|
| Line Coverage | 🟩 99% |
| Branch Coverage | 🟩 99% |
| Required Minimum | 80% |

</div>

**Built with JUnit 5** for test structure/assertions and **Mockito** for mocking repositories and external dependencies, ensuring the service layer is tested in complete isolation. Coverage was pushed to **99%** by testing not just the happy paths but nearly every validation rule and exception branch:

- ✅ Authentication success & failure paths
- ✅ User-not-found scenarios
- ✅ Registration validation (email, password, phone)
- ✅ Transaction CRUD, filtering & date-immutability rules
- ✅ Category validation, duplicate & in-use protections
- ✅ Savings goal validation & progress-calculation edge cases
- ✅ Goal updates, deletion & percentage capping
- ✅ Report aggregation correctness
- ✅ Cross-user data-access restrictions
- ✅ Global exception-handler coverage

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

# 🔬 End-to-End API Testing

`financial_manager_tests.sh` runs **86 automated checks** against a live, running instance of the API:

<div align="center">

![Tests](https://img.shields.io/badge/86%2F86-Tests%20Passing-success?style=for-the-badge&logo=checkmarx&logoColor=white)

</div>

1. Registration & duplicate prevention
2. Login / failed login / session authorization
3. Logout & session invalidation
4. Transaction CRUD, validation & filtering
5. Category management & usage rules
6. Goal creation, validation, progress & updates
7. Goal/report consistency after deletion
8. Monthly & yearly reports
9. User data isolation & cross-user access prevention
10. Full end-to-end user journey

**Run locally:**
```bash
bash financial_manager_tests.sh http://localhost:8080/api
```

**Run against the live Render deployment:**
```bash
bash financial_manager_tests.sh https://pfm-83i3.onrender.com/api
```

```bash
chmod +x financial_manager_tests.sh
./financial_manager_tests.sh http://localhost:8080/api
```

Uses cookie files to maintain independent sessions per user and verify data isolation.

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

# 🧩 Validation & Error Handling

<div align="center">

| Status | Meaning |
|:---:|---|
| `400` | Bad Request — validation errors, malformed input |
| `401` | Unauthorized — invalid credentials, expired session |
| `403` | Forbidden — accessing another user's data |
| `404` | Resource Not Found |
| `409` | Conflict — duplicate category names, etc. |

</div>

> ✅ No 5xx errors occur for known/expected scenarios — every anticipated failure mode returns a clean, descriptive 4xx response.

Validated conditions include: invalid emails/passwords/phone numbers · missing fields · negative/zero amounts · future transaction dates · invalid/missing date ranges · invalid category types or duplicates · non-existent categories · invalid goal amounts/past goal dates · access to non-existent or cross-user resources · unauthorized requests.

A global `@ControllerAdvice` exception handler returns consistent error responses across the entire API.

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

# 🧠 Important Design Decisions

| Decision | Rationale |
|---|---|
| 🍪 **Session auth over JWT** | Assignment requires session-based auth with secure cookies |
| 📦 **DTOs over exposing entities** | Decouples API contract from persistence model |
| ⚙️ **Business logic in services** | Keeps controllers thin; calculations centralized |
| 🔐 **User isolation at service/repo boundary** | Prevents ID-based cross-user access |
| 📊 **Dynamic goal progress** | Derived from transactions, always consistent |
| 🏷️ **Category protection** | Default categories are system-managed; in-use categories can't be deleted |

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

# 📈 Data Consistency

```text
Create Transaction ──► Transaction list changes
                   ──► Goal progress changes
                   ──► Monthly/Yearly reports change

Delete Transaction ──► Transaction removed
                   ──► Goal progress recalculated
                   ──► Reports recalculated
```

Explicitly validated by the 86-check E2E test suite.

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

# 🛡️ Security Considerations

- 🔐 Passwords never stored in plain text
- 🔑 Authentication required on protected endpoints
- 🚪 Sessions invalidated on logout
- 🧱 Resources scoped to the authenticated user
- ⛔ Cross-user update/delete attempts rejected (`403`)
- ✅ Input validation on all requests
- 🌱 DB credentials via environment/external configuration
- 🙅 No secrets committed to Git
- 🛡️ Errors avoid leaking internal implementation details

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

# 📋 Assignment Requirements Coverage

<div align="center">

| Requirement | Status |
|---|:---:|
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
| **99% JaCoCo coverage** (80% required) | ✅ |
| E2E API test suite (86/86 passing) | ✅ |
| Comprehensive README | ✅ |
| **Live deployment on Render** | ✅ |

</div>

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

# 🔄 Typical User Workflow

```text
Register → Login → Authenticated Session
  ├── View default categories
  ├── Create custom categories
  ├── Add income/expenses
  ├── Filter transactions
  ├── Create savings goals
  ├── Track goal progress
  ├── Generate monthly reports
  ├── Generate yearly reports
  └── Logout → Session invalidated
```

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

# 📝 Example Financial Flow

```text
Income:   Salary ₹5,500 + Freelance ₹1,500 = ₹7,000
Expenses: Food ₹450

Net Savings = ₹7,000 − ₹450 = ₹6,550
```

For a ₹10,000 goal:
```text
Progress  = ₹6,550 / ₹10,000 × 100 = 65.5%
Remaining = ₹10,000 − ₹6,550 = ₹3,450
```

The same transaction data drives both goal calculations and financial reports, keeping results perfectly consistent.

<img src="https://capsule-render.vercel.app/api?type=rect&color=gradient&customColorList=6,11,20&height=3&width=100%"/>

# 🏁 Submission Checklist

- [x] Source code committed
- [x] Unit tests committed (JUnit 5 + Mockito)
- [x] JaCoCo coverage checked — **99%**
- [x] `financial_manager_tests.sh` included
- [x] README included
- [x] No passwords or secrets committed
- [x] `target/` excluded from Git
- [x] `.env`/secret configuration excluded from Git
- [x] PostgreSQL connection verified
- [x] Application starts successfully
- [x] API endpoints verified
- [x] E2E tests executed against the final build — **86/86 passing**
- [x] **Deployment URL tested:** [`https://pfm-83i3.onrender.com/api`](https://pfm-83i3.onrender.com/api)

<br/>

<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=6,11,20&height=150&section=footer"/>

### 👨‍💻 Author

**Sahil**
B.Tech — Information Technology · IIIT Bhopal

**Live API:** [pfm-83i3.onrender.com/api](https://pfm-83i3.onrender.com/api)

---

#### License

Developed as an academic/system-design assignment demonstrating REST API development, backend architecture, authentication, database persistence, business logic, testing, and API validation.

⭐ If this project helped you, consider giving it a star!

</div>
