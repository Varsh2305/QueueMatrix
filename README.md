# QueueMatrix (QM) - Module 1: Public Homepage & Authentication

Welcome to **QueueMatrix**, an enterprise-grade service queue, booking, and customer journey orchestration platform.

This directory contains **Module 1**:
- Public Homepage with logo, tagline, and supported organization sectors.
- Public View Board (no authentication required) with clean empty states.
- Direct User Registration (no OTP): Full name, email address, password, phone number, organization category, and role (permitted public roles).
- Secure Email & Password Login with BCrypt hashing and JWT stateless tokens.
- Strict Role Isolation: Customer accounts activate immediately; Organization Admin and Super Admin accounts are restricted from public self-registration; Staff accounts enter `PENDING_INVITE_APPROVAL`.
- Role-Based Routing & Dashboard Placeholder.

---

## Architecture Overview

```
QM/
├── backend/                  # Java 25 / Spring Boot 4.1.2 Backend
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/queuematrix/backend/
│       │   ├── config/       # SecurityConfig (BCrypt PasswordEncoder, JWT Stateless, CORS)
│       │   ├── controller/   # AuthController, PublicController
│       │   ├── dto/          # Requests (RegisterRequest, LoginRequest) & Responses
│       │   ├── entity/       # User, Role, Organization, OrganizationType, UserStatus
│       │   ├── repository/   # Spring Data JPA Repositories (UserRepository, OrganizationRepository)
│       │   ├── security/     # JwtTokenProvider, UserPrincipal, CustomUserDetailsService
│       │   ├── service/      # AuthService, PublicService
│       │   └── exception/    # GlobalExceptionHandler
│       └── main/resources/
│           └── application.properties
└── frontend/                 # React 18 + Vite + TypeScript Frontend
    ├── package.json
    ├── vite.config.ts
    ├── index.html
    └── src/
        ├── components/       # Navbar, Footer
        ├── pages/            # HomePage, PublicDisplayPage, SignUpPage, LoginPage, DashboardPlaceholderPage
        ├── services/         # Typed API Client (register, login, getPublicHome, getPublicDisplay)
        └── types/            # TypeScript Interfaces
```

---

## Security & Credentials

- **Password Hashing:** Passwords are cryptographically salted and hashed using **BCrypt** with automatic salt generation before persistence. No plain text passwords are ever stored, returned, or logged.
- **JWT Authorization:** Stateless signed JWT access tokens are issued upon successful login, carrying user identity and role claims.
- **Role Isolation:** Privileged roles (`SUPER_ADMIN` and `ORGANIZATION_ADMIN`) cannot be self-registered by public users; they require administrative provisioning.

---

## Running the Application

### 1. Database (MySQL 8.0)
The backend is pre-configured to connect to local MySQL on port 3306:
- Database: `queuematrix_db` (created automatically if it does not exist)
- Default credentials: `root` / `root`
- Override credentials if needed via PowerShell:
  ```powershell
  $env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/queuematrix_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
  $env:SPRING_DATASOURCE_USERNAME="root"
  $env:SPRING_DATASOURCE_PASSWORD="your_mysql_password"
  ```

*(Zero-setup H2 fallback for quick testing without MySQL: pass `-Dspring-boot.run.arguments="--spring.datasource.url=jdbc:h2:mem:queuematrix_db;DB_CLOSE_DELAY=-1;MODE=MySQL --spring.datasource.driver-class-name=org.h2.Driver --spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"`)*

### 2. Start Backend (Spring Boot 4.1.2 / Java 25)
```powershell
cd backend
mvn spring-boot:run
```
The backend API starts on `http://localhost:8080`.

### 3. Start Frontend (React + Vite)
```powershell
cd frontend
npm run dev
```
The frontend web application opens on `http://localhost:5173`.
