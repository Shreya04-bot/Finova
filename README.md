# 🏦 Finova Backend

### Digital Banking & Payment Platform — Spring Boot REST API

Finova Backend is the secure, modular REST API powering the **Finova Digital Banking & Payment Platform**.

It provides authentication, account management, money transfers, simulated payments, beneficiary management, transaction history, notifications, admin controls, audit logging, caching, asynchronous event processing, and AI-powered spending analysis.

> ⚠️ **Important:** Finova uses simulated financial data. It does not connect to real banks, payment gateways, or process real money.

---

## ✨ Features

### 🔐 Authentication & Security

- JWT-based authentication
- Stateless Spring Security configuration
- BCrypt password hashing
- User registration and login
- Role-based authorization
- Customer, Admin and Support roles
- Protected REST APIs
- Ownership validation for financial operations
- Blocked-user protection
- Global exception handling

### 💳 Account Management

- Create bank accounts
- Savings accounts
- Current accounts
- Salary accounts
- Automatically generated account numbers
- Account balance management
- Account status management

### 💸 Money Transfers

- Account-to-account transfers
- Sender ownership validation
- Receiver account validation
- Active-account verification
- Sufficient-balance validation
- Atomic debit/credit operation
- Transaction record generation
- Transaction reference generation
- Asynchronous notification events

### 🧾 Payments

Supported simulated payment categories:

- Electricity
- Mobile Recharge
- Internet
- Water
- Education
- Shopping
- Other

Payments include:

- Atomic balance deduction
- Payment history
- Transaction creation
- Payment status tracking
- Asynchronous notifications

### 👥 Beneficiary Management

- Add beneficiary
- View personal beneficiaries
- Delete beneficiary
- Beneficiary ownership validation
- Bank name and account number storage

### 📊 Transaction History

- Account-based transaction history
- Transfer records
- Payment records
- Sender/receiver account information
- Transaction status
- Transaction type
- Transaction reference
- Creation timestamp

### 🔔 Notifications

- Transfer notifications
- Payment notifications
- Read/unread status
- Asynchronous notification creation
- Caffeine caching

### 👨‍💼 Admin Module

Administrators can:

- View customers
- Block accounts
- Unblock accounts
- View audit logs

All important administrative actions are audit logged.

### 🤖 AI Financial Assistant

Finova includes a rule-based financial assistant that analyzes payment history and provides:

- Total spending
- Spending by category
- Category percentages

The assistant is intentionally rule-based rather than dependent on an external LLM.

### ⚡ Caching

Finova uses **Caffeine** for in-memory caching.

Currently:

```
GET /api/notifications
```

is cached for improved performance.

Account balances are intentionally not cached because financial balances need to remain accurate.

---

## 🔄 Event-Driven Architecture

Instead of directly creating notifications inside money-moving services, Finova uses:

```
ApplicationEventPublisher
        ↓
   Spring Event
        ↓
@Async @EventListener
        ↓
 NotificationService
```

Because the money operation is transactional, the debit and credit are handled atomically.

---

## 🔐 Authentication Architecture

```
                    User
                     │
                     ▼
              Login Request
                     │
                     ▼
             AuthController
                     │
                     ▼
               AuthService
                     │
                     ├── Find User
                     ├── Verify BCrypt Password
                     │
                     ▼
                JwtService
                     │
                     ▼
                 JWT Token
                     │
                     ▼
                  Client
                     │
                     │ Authorization:
                     │ Bearer <token>
                     ▼
           JwtAuthenticationFilter
                     │
                     ▼
             SecurityContext
                     │
                     ▼
            Protected Controller
```

---

## ⚡ Event-Driven Notification Architecture

```
TransferService
      │
      │ publishEvent()
      ▼
MoneyTransferredEvent
      │
      ▼
NotificationEventListener
      │
      │ @Async
      ▼
NotificationService
      │
      ▼
PostgreSQL
```

For payments:

```
PaymentService
      │
      ▼
PaymentSuccessEvent
      │
      ▼
NotificationEventListener
      │
      ▼
NotificationService
```

This avoids coupling the primary financial operation directly to notification creation.

---

## 🗄️ Database Architecture

```
┌──────────────┐
│    users     │
└──────┬───────┘
       │
       │ 1
       │
       │ N
┌──────▼───────┐
│   accounts   │
└──────┬───────┘
       │
       ├──────────────────┐
       │                  │
       │                  │
       ▼                  ▼
┌───────────────┐   ┌───────────────┐
│ transactions  │   │    payments   │
└───────────────┘   └───────────────┘

┌───────────────┐
│ beneficiaries │
└───────┬───────┘
        │
        ▼
      users

┌───────────────┐
│ notifications │
└───────┬───────┘
        │
        ▼
      users

┌───────────────┐
│   audit_logs  │
└───────────────┘
```

---

## 📦 Project Structure

```
finova-backend/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── finova/
│   │   │           │
│   │   │           ├── config/
│   │   │           │   ├── SecurityConfig.java
│   │   │           │   ├── CacheConfig.java
│   │   │           │   └── OpenApiConfig.java
│   │   │           │
│   │   │           ├── controller/
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── AccountController.java
│   │   │           │   ├── TransferController.java
│   │   │           │   ├── PaymentController.java
│   │   │           │   ├── NotificationController.java
│   │   │           │   ├── BeneficiaryController.java
│   │   │           │   ├── TransactionController.java
│   │   │           │   ├── AdminController.java
│   │   │           │   └── AiAssistantController.java
│   │   │           │
│   │   │           ├── dto/
│   │   │           │   ├── request/
│   │   │           │   └── response/
│   │   │           │
│   │   │           ├── entity/
│   │   │           │   ├── User.java
│   │   │           │   ├── Account.java
│   │   │           │   ├── Transaction.java
│   │   │           │   ├── Payment.java
│   │   │           │   ├── Beneficiary.java
│   │   │           │   ├── Notification.java
│   │   │           │   └── AuditLog.java
│   │   │           │
│   │   │           ├── repository/
│   │   │           │
│   │   │           ├── service/
│   │   │           │
│   │   │           ├── security/
│   │   │           │   ├── JwtService.java
│   │   │           │   ├── JwtAuthenticationFilter.java
│   │   │           │   ├── UserPrincipal.java
│   │   │           │   ├── CurrentUserUtil.java
│   │   │           │   └── CustomUserDetailsService.java
│   │   │           │
│   │   │           ├── event/
│   │   │           │   ├── MoneyTransferredEvent.java
│   │   │           │   ├── PaymentSuccessEvent.java
│   │   │           │   └── NotificationEventListener.java
│   │   │           │
│   │   │           └── exception/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   ├── test/
│   │   └── java/
│   │
│   └── ...
│
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── pom.xml
└── README.md
```

---

## 🛠️ Technology Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1 |
| Security | Spring Security |
| Authentication | JWT |
| Password Hashing | BCrypt |
| Database | PostgreSQL |
| ORM | Spring Data JPA |
| Persistence | Hibernate |
| Validation | Jakarta Bean Validation |
| Caching | Caffeine |
| Async Processing | Spring @Async |
| Events | Spring Application Events |
| API Documentation | Springdoc OpenAPI |
| Testing | JUnit 5 |
| Mocking | Mockito |
| Web Testing | MockMvc |
| Build Tool | Maven |
| CI/CD | GitHub Actions |
| Containerization | None |

---

## 🌐 REST API

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a customer |
| POST | `/api/auth/login` | Authenticate and receive JWT |

### Accounts

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/accounts` | Get logged-in user's accounts |
| POST | `/api/accounts?type=SAVINGS` | Create account |

### Transfers

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/transfers` | Transfer money |

### Payments

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/payments` | Make simulated payment |
| GET | `/api/payments?accountId=X` | Get payment history |

### Beneficiaries

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/beneficiaries` | Get own beneficiaries |
| POST | `/api/beneficiaries` | Add beneficiary |
| DELETE | `/api/beneficiaries/{id}` | Delete beneficiary |

### Transactions

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/transactions?accountId=X` | Get transaction history |

### Notifications

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/notifications` | Get own notifications |
| PATCH | `/api/notifications/{id}/read` | Mark notification as read |

### Admin

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/users` | List customers |
| PATCH | `/api/admin/accounts/{id}/block` | Block account |
| PATCH | `/api/admin/accounts/{id}/unblock` | Unblock account |
| GET | `/api/admin/audit-logs` | View audit logs |

### Financial Assistant

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/assistant/spending-analysis?accountId=X` | Spending analysis |

---

## 📖 API Documentation

Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI specification:

```
http://localhost:8080/v3/api-docs
```

---

## 🚀 Getting Started

### Prerequisites

Make sure you have:

- Java 21+
- Maven
- PostgreSQL
- IntelliJ IDEA or another Java IDE
- Git

### 1. Clone the repository

```bash
git clone YOUR_BACKEND_REPOSITORY_URL
cd finova-backend
```

### 2. Configure PostgreSQL

Create a database:

```sql
CREATE DATABASE finova_db;
```

Configure your database credentials in:

```
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finova_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> ⚠️ Never commit real database credentials or JWT secrets to GitHub.

### 3. Run the application

Windows:

```bash
.\mvnw.cmd spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

Backend will run at:

```
http://localhost:8080
```

---

## 🧪 Running Tests

Run all tests:

```bash
mvn test
```

Run a clean build:

```bash
mvn clean package
```

The project contains:

- Unit tests
- Mockito tests
- Spring Boot integration tests
- MockMvc tests

---

## 🔄 CI/CD

GitHub Actions automatically:

```
Git Push / Pull Request
          │
          ▼
   GitHub Actions
          │
          ▼
   Start PostgreSQL
          │
          ▼
      Run Tests
          │
          ▼
    Build Backend
          │
          ▼
       Success
```

Workflow:

```
.github/workflows/ci.yml
```

---

## 🔐 Security Principles

Finova follows several important security principles:

**JWT Authentication**

All protected endpoints require:

```
Authorization: Bearer <JWT>
```

**Ownership Validation**

Money-moving operations verify that:

```
authenticatedUser == accountOwner
```

A client cannot simply provide another user's account ID and access their funds.

**Transactional Money Operations**

Money-changing service methods use:

```
@Transactional
```

to maintain atomicity.

**Password Security**

Passwords are stored using BCrypt hashing rather than plaintext.

**Role-Based Access Control**

Administrative endpoints require:

```
ROLE_ADMIN
```

---

## ⚡ Why Caffeine Instead of Redis?

The original architecture considered Redis, but Finova deliberately uses Caffeine.

Reasons:

- No external service required
- Zero additional infrastructure
- Easy local development
- Appropriate for a single-service portfolio project
- Demonstrates caching concepts

Financial balances are intentionally not cached because stale balances would be dangerous.

---

## 🔄 Why Spring Events Instead of Kafka?

The original architecture considered Kafka, but Finova uses:

```
ApplicationEventPublisher + @EventListener + @Async
```

This provides asynchronous decoupling without requiring a Kafka broker.

For a larger distributed system, this architecture could later evolve into Kafka-based messaging.

---

## 🐳 Why Docker Is Not Used

Docker was intentionally excluded from local development.

The application runs natively using:

```
Spring Boot + PostgreSQL + Maven
```

GitHub Actions may still use containerized infrastructure internally for CI.

---

## 📈 Future Improvements

Potential future enhancements include:

- PDF account statements
- Refresh token mechanism
- Password reset
- Support staff dashboard
- Advanced transaction filtering
- Pagination
- Production deployment
- Distributed caching
- Kafka-based messaging
- Email/SMS notifications
- Multi-factor authentication
- Advanced fraud detection

---

## 👩‍💻 Author

**Aarohi**

B.Tech Computer Science Engineering

Finova was developed as a full-stack portfolio project demonstrating practical Java, Spring Boot, database, security, frontend integration, testing, caching, asynchronous processing, and CI/CD concepts.

---

## ⭐ Project Highlights

Secure Authentication + Account Management + Money Transfers + Payments + Beneficiaries + Transactions + Notifications + Admin Controls + Caching + Event-Driven Processing + Financial Analytics + Testing + CI/CD

---

💡 *Finova is a simulated banking platform built for software engineering demonstration and portfolio purposes.*