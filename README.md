# GradTrack

![GradTrack CI](https://github.com/Rajkumar0863/gradtrack/actions/workflows/ci.yml/badge.svg)

A full-stack graduate job application tracking platform built with **Java, Spring Boot, PostgreSQL, HTML, CSS, JavaScript, C#, NUnit, Playwright, and GitHub Actions**.

GradTrack helps users manage graduate job applications across the recruitment lifecycle — from saving an opportunity through assessments and interviews to an offer, rejection, or withdrawal.

The project combines backend development, frontend development, database persistence, business-rule validation, automated testing, browser automation, and continuous integration in one end-to-end software engineering workflow.

---

## Features

### Application Management

GradTrack supports:

- Create applications
- View applications
- Edit applications
- Delete applications
- Track recruitment status
- Assign priority
- Record application dates
- Track deadlines
- Search by company or role
- Filter by status
- Filter by priority
- Sort applications
- Display deadline health
- Display validation and business-rule errors

---

## Recruitment Workflow

Each application has one of the following statuses:

- `SAVED`
- `APPLIED`
- `ONLINE_ASSESSMENT`
- `VIDEO_INTERVIEW`
- `ASSESSMENT_CENTRE`
- `FINAL_INTERVIEW`
- `OFFER`
- `REJECTED`
- `WITHDRAWN`

A typical progression is:

```text
SAVED
  |
  v
APPLIED
  |
  v
ONLINE_ASSESSMENT
  |
  v
VIDEO_INTERVIEW
  |
  v
ASSESSMENT_CENTRE
  |
  v
FINAL_INTERVIEW
  |
  v
OFFER
```

Applications can also move to `REJECTED` or `WITHDRAWN` where permitted.

`REJECTED` and `WITHDRAWN` are terminal states.

GradTrack prevents invalid transitions such as:

```text
SAVED -> OFFER
```

while allowing valid transitions such as:

```text
SAVED -> APPLIED
```

The same status can also be retained while editing other application information.

---

## Search, Filtering and Sorting

GradTrack supports server-side search and filtering.

Applications can be searched by:

- Company
- Role

Applications can be filtered by:

- Status
- Priority

Backend sorting supports:

- Deadline ascending
- Deadline descending
- Company ascending
- Company descending
- Application date ascending
- Application date descending

Priority sorting is handled in the browser using:

```text
HIGH -> MEDIUM -> LOW
```

Example API request:

```http
GET /api/applications?search=accenture&status=APPLIED&priority=HIGH&sort=deadline
```

This allows the browser to request only records matching the selected criteria rather than retrieving every application and performing all filtering locally.

---

## Deadline Intelligence

GradTrack provides deadline indicators to highlight applications requiring attention.

Applications are classified as:

| Deadline State | Meaning |
|---|---|
| Overdue | Deadline has passed |
| Due Today | Deadline is today |
| Due Soon | 1–3 days remaining |
| Upcoming | 4–7 days remaining |
| Safe | More than 7 days remaining |
| Closed | Application is no longer active |

The following statuses are treated as closed:

- `OFFER`
- `REJECTED`
- `WITHDRAWN`

---

## Technology Stack

| Area | Technology |
|---|---|
| Backend | Java 25 |
| Framework | Spring Boot 4.1.1 |
| Web | Spring MVC |
| Persistence | Spring Data JPA |
| ORM | Hibernate |
| Validation | Jakarta Validation |
| Database | PostgreSQL 18 |
| Build | Maven |
| Frontend | HTML5, CSS3, JavaScript |
| Java Testing | JUnit, Mockito, MockMvc, WebMvcTest |
| E2E Testing | C#, .NET 8, NUnit, Microsoft Playwright |
| Browser | Chromium |
| Version Control | Git, GitHub |
| CI | GitHub Actions |

---

## System Architecture

GradTrack follows a layered architecture.

```text
+-----------------------------+
|           Browser           |
|   HTML / CSS / JavaScript   |
+--------------+--------------+
               |
               | HTTP / JSON
               v
+-----------------------------+
|     Spring REST Controller  |
+--------------+--------------+
               |
               v
+-----------------------------+
|        Service Layer        |
| Validation + Business Rules |
+--------------+--------------+
               |
               v
+-----------------------------+
|    Spring Data Repository   |
+--------------+--------------+
               |
               v
+-----------------------------+
|         PostgreSQL          |
+-----------------------------+
```

The backend separates responsibilities into:

```text
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
PostgreSQL
```

### Controller Layer

Responsible for:

- HTTP requests
- Request parameters
- Request validation
- HTTP status codes
- REST responses

### Service Layer

Responsible for:

- Application business logic
- Date validation
- Recruitment status transitions
- Search and filtering coordination
- Sorting logic

### Repository Layer

Responsible for:

- Database access
- CRUD persistence
- JPA specifications
- Status counts
- Priority counts

---

## REST API

Base endpoint:

```text
/api/applications
```

### Get All Applications

```http
GET /api/applications
```

Returns all applications when no query parameters are supplied.

---

### Get Application by ID

```http
GET /api/applications/{id}
```

Example:

```http
GET /api/applications/1
```

Responses:

| Result | HTTP Status |
|---|---|
| Application found | `200 OK` |
| Application not found | `404 Not Found` |

---

### Create Application

```http
POST /api/applications
```

Example request:

```json
{
  "company": "Accenture",
  "role": "Software Engineering Graduate Programme",
  "status": "APPLIED",
  "priority": "HIGH",
  "applicationDate": "2026-09-11",
  "deadline": "2026-10-20"
}
```

Successful response:

```text
201 Created
```

---

### Update Application

```http
PUT /api/applications/{id}
```

The update operation enforces both request validation and business rules.

Responses:

| Result | HTTP Status |
|---|---|
| Application updated | `200 OK` |
| Validation or business-rule failure | `400 Bad Request` |
| Application not found | `404 Not Found` |

---

### Delete Application

```http
DELETE /api/applications/{id}
```

Responses:

| Result | HTTP Status |
|---|---|
| Application deleted | `204 No Content` |
| Application not found | `404 Not Found` |

---

## Search API

Search by company or role:

```http
GET /api/applications?search=software
```

Filter by status:

```http
GET /api/applications?status=APPLIED
```

Filter by priority:

```http
GET /api/applications?priority=HIGH
```

Sort by deadline:

```http
GET /api/applications?sort=deadline
```

Combine parameters:

```http
GET /api/applications?search=software&status=APPLIED&priority=HIGH&sort=deadline
```

The backend uses Spring Data JPA specifications to dynamically construct database queries.

---

## Request Validation

GradTrack validates incoming application data using Jakarta Validation.

Required fields are:

| Field | Rule |
|---|---|
| Company | Must not be blank |
| Role | Must not be blank |
| Status | Must not be null |
| Priority | Must not be null |
| Application Date | Must not be null |
| Deadline | Must not be null |

Example invalid response:

```json
{
  "company": "Company name is required"
}
```

Validation errors are handled centrally by:

```text
GlobalExceptionHandler
```

This keeps REST error handling consistent across the application.

---

## Business Rule Validation

GradTrack also performs service-layer validation for rules that cannot be handled using simple field validation.

### Deadline Rule

A deadline cannot occur before the application date.

```text
deadline >= applicationDate
```

Invalid example:

```text
Application Date: 2026-10-10
Deadline:         2026-10-05
```

The API returns:

```json
{
  "error": "Deadline cannot be before application date"
}
```

---

## Status Transition Validation

Recruitment status is modelled as a controlled workflow.

Examples of valid transitions:

```text
SAVED             -> APPLIED
APPLIED           -> ONLINE_ASSESSMENT
ONLINE_ASSESSMENT -> VIDEO_INTERVIEW
VIDEO_INTERVIEW   -> ASSESSMENT_CENTRE
ASSESSMENT_CENTRE -> FINAL_INTERVIEW
FINAL_INTERVIEW   -> OFFER
```

Example invalid transition:

```text
SAVED -> OFFER
```

Response:

```json
{
  "error": "Invalid status transition from SAVED to OFFER"
}
```

Keeping these rules in the service layer ensures they remain enforced even when the REST API is called directly without using the browser UI.

---

## Frontend Dashboard

GradTrack includes a browser-based dashboard served directly by Spring Boot.

The dashboard supports:

- Creating applications
- Editing applications
- Deleting applications
- Searching applications
- Filtering by status
- Filtering by priority
- Sorting
- Result counts
- Deadline health indicators
- Summary information
- Validation feedback

The frontend communicates with the Spring Boot backend through the REST API using JavaScript.

---

## Automated Testing

GradTrack currently contains:

```text
Java Service Tests       15
Java Controller Tests    14
Playwright E2E Tests      6
---------------------------
Total Automated Tests    35
```

### Test Summary

| Test Layer | Technology | Tests |
|---|---|---:|
| Service | JUnit + Mockito | 15 |
| Controller | MockMvc + WebMvcTest | 14 |
| End-to-End | C# + NUnit + Playwright | 6 |
| **Total** | | **35** |

---

## Service Tests

Service tests use:

- JUnit
- Mockito

They test business logic independently from the real database.

Coverage includes:

- Application creation
- Application retrieval
- Application updates
- Application deletion
- Deadline validation
- Status-transition validation
- Search behaviour
- Filtering
- Sorting
- Repository interaction

Mockito isolates the service layer from persistence dependencies.

---

## Controller Tests

Controller tests use:

- Spring `WebMvcTest`
- MockMvc
- Mockito

They verify HTTP behaviour without requiring the real PostgreSQL database.

Coverage includes:

- GET requests
- POST requests
- PUT requests
- DELETE requests
- HTTP status codes
- Request validation
- Error responses
- Search parameters
- Filter parameters
- Sort parameters

---

## Run Java Tests

From the project root:

```bash
mvn clean verify
```

Expected result:

```text
Tests run: 29
Failures: 0
Errors: 0
Skipped: 0
```

---

## End-to-End Browser Automation

GradTrack contains a separate C# automation project using:

- .NET 8
- NUnit
- Microsoft Playwright
- Chromium

The current end-to-end scenarios are:

1. Open the GradTrack dashboard
2. Create an application through the browser
3. Search and filter applications
4. Edit an application from `SAVED` to `APPLIED`
5. Verify backend validation when company is missing
6. Verify rejection of a deadline earlier than the application date

---

## Full-Stack Validation Testing

The Playwright suite verifies more than browser behaviour.

For the missing-company scenario, the test deliberately removes the browser's HTML `required` attribute.

This allows an invalid request to reach the backend.

```text
Browser
   |
   v
JavaScript
   |
   v
REST API
   |
   v
Jakarta Validation
   |
   v
GlobalExceptionHandler
   |
   v
HTTP 400
   |
   v
Frontend Error Message
```

The test then confirms that the invalid application was not persisted.

The deadline test follows another complete path:

```text
Browser
   |
   v
JavaScript
   |
   v
REST API
   |
   v
Service Layer
   |
   v
Business Rule Validation
   |
   v
HTTP 400
   |
   v
Frontend Error Message
```

This verifies both user-facing behaviour and backend enforcement.

---

## Page Object Model

The Playwright automation suite uses the **Page Object Model**.

```text
automation/
└── GradTrack.AutomationTests/
    ├── Pages/
    │   └── GradTrackPage.cs
    │
    ├── Tests/
    │   ├── ApplicationTests.cs
    │   ├── SearchFilterTests.cs
    │   └── ValidationTests.cs
    │
    └── GradTrack.AutomationTests.csproj
```

`GradTrackPage.cs` centralises:

- Page locators
- Form interactions
- Search interactions
- Filter interactions
- Edit interactions
- Test-data cleanup

This separates:

```text
HOW the browser is controlled
```

from:

```text
WHAT behaviour is being tested
```

---

## Run Playwright Tests

First start GradTrack from the repository root.

### Terminal 1

```cmd
set "DB_PASSWORD=YOUR_POSTGRES_PASSWORD"
mvn spring-boot:run
```

Keep the terminal running.

### Terminal 2

```cmd
cd E:\Projects\gradtrack\automation\GradTrack.AutomationTests
dotnet test
```

Expected result:

```text
Failed:  0
Passed:  6
Skipped: 0
Total:   6
```

---

## Headed Browser Mode

To watch Chromium execute the tests:

```cmd
set HEADED=1
dotnet test
```

For headless execution:

```cmd
set HEADED=0
dotnet test
```

CI uses headless mode.

---

## Continuous Integration

GradTrack uses **GitHub Actions** for automated CI.

The workflow runs on:

- Pushes to `main`
- Pull requests targeting `main`

The pipeline contains two stages.

### Java Build and Tests

```text
Checkout Repository
        |
        v
Configure Java 25
        |
        v
Restore Maven Dependencies
        |
        v
Compile GradTrack
        |
        v
Run 29 Java Tests
        |
        v
Build Verification
```

Command:

```bash
mvn --batch-mode clean verify
```

### Playwright End-to-End Tests

After the Java stage succeeds:

```text
Provision PostgreSQL
        |
        v
Configure Java 25
        |
        v
Configure .NET 8
        |
        v
Restore C# Dependencies
        |
        v
Build Automation Project
        |
        v
Install Chromium
        |
        v
Start Spring Boot
        |
        v
Wait for Application
        |
        v
Run 6 Playwright Tests
```

The CI environment verifies the complete stack:

```text
Browser
   +
Frontend
   +
REST API
   +
Business Logic
   +
Persistence
   +
PostgreSQL
```

---

## Local Development

### Prerequisites

Install:

- Java 25
- Maven
- PostgreSQL
- .NET 8 SDK
- Git

---

## Database Setup

Create a PostgreSQL database named:

```text
gradtrack
```

Default connection:

```text
jdbc:postgresql://localhost:5432/gradtrack
```

Default database username:

```text
postgres
```

Database credentials are not committed to source control.

---

## Environment Variables

GradTrack supports:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

The application configuration uses:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/gradtrack}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}
```

On Windows CMD:

```cmd
set "DB_PASSWORD=YOUR_POSTGRES_PASSWORD"
```

Then run:

```cmd
mvn spring-boot:run
```

---

## Open GradTrack

After Spring Boot starts, open:

```text
http://localhost:8080
```

---

## Repository Structure

```text
gradtrack/
│
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── automation/
│   └── GradTrack.AutomationTests/
│       ├── Pages/
│       │   └── GradTrackPage.cs
│       │
│       ├── Tests/
│       │   ├── ApplicationTests.cs
│       │   ├── SearchFilterTests.cs
│       │   └── ValidationTests.cs
│       │
│       └── GradTrack.AutomationTests.csproj
│
├── docs/
│   ├── DEFECT_LOG.md
│   └── TEST_PLAN.md
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── rajkumar/
│   │   │           └── gradtrack/
│   │   │               ├── controller/
│   │   │               ├── exception/
│   │   │               ├── model/
│   │   │               ├── repository/
│   │   │               ├── service/
│   │   │               └── GradTrackApplication.java
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── index.html
│   │       │   ├── style.css
│   │       │   └── app.js
│   │       │
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── rajkumar/
│                   └── gradtrack/
│                       ├── controller/
│                       └── service/
│
├── .gitignore
├── pom.xml
└── README.md
```

---

## Test Strategy

GradTrack uses a practical testing pyramid.

```text
                 /\
                /  \
               / E2E\
              /  6   \
             /--------\
            /Controller\
           /    14      \
          /--------------\
         / Service Tests  \
        /       15         \
       /--------------------\
```

Service tests provide fast feedback on business logic.

Controller tests verify REST behaviour.

Playwright tests verify complete user workflows through the real application.

---

## Key Engineering Decisions

### Layered Architecture

```text
Controller -> Service -> Repository -> PostgreSQL
```

HTTP handling, business logic, and persistence remain separate.

### Server-Side Business Rules

Rules such as:

```text
Deadline cannot be before application date
```

and:

```text
SAVED cannot transition directly to OFFER
```

are enforced by the service layer rather than relying on the frontend.

### Server-Side Validation

Frontend validation improves user experience, but the server independently validates requests using Jakarta Validation and service-layer rules.

### Server-Side Search

Search and filtering use REST query parameters and Spring Data JPA specifications.

### Page Object Model

Browser selectors and reusable UI operations are centralised inside `GradTrackPage.cs`.

### Environment-Based Credentials

Database credentials are supplied through environment variables rather than being committed to Git.

### Continuous Integration

GitHub Actions automatically executes regression and end-to-end tests instead of relying only on local testing.

---

## Documentation

Additional project documentation is available in:

- [`docs/TEST_PLAN.md`](docs/TEST_PLAN.md)
- [`docs/DEFECT_LOG.md`](docs/DEFECT_LOG.md)

---

## Development Workflow

```text
Requirement
    |
    v
Implementation
    |
    v
Local Testing
    |
    v
Automated Regression
    |
    v
Git Commit
    |
    v
GitHub Push
    |
    v
GitHub Actions CI
    |
    v
Verified Build
```

---

## What I Learned

GradTrack provided practical experience across the software development and testing lifecycle, including:

- REST API design
- Java and Spring Boot
- PostgreSQL integration
- Spring Data JPA
- Layered architecture
- Jakarta request validation
- Service-layer business rules
- State-transition workflows
- HTTP error handling
- HTML, CSS and JavaScript
- Server-side search and filtering
- JUnit testing
- Mockito
- MockMvc
- C# and NUnit
- Playwright browser automation
- Page Object Model
- Test-data cleanup
- Full-stack validation testing
- GitHub Actions
- PostgreSQL services in CI
- Automated regression testing
- Debugging across application layers

---

## Future Improvements

Potential future extensions include:

- User authentication and authorisation
- Multiple user accounts
- Pagination
- Application notes
- Interview scheduling
- Reminder notifications
- Application analytics
- Docker support
- Cloud deployment

These are intentionally outside the current v1.0 scope.

---

## Project Status

**GradTrack v1.0**

Current implementation includes:

- Full CRUD REST API
- PostgreSQL persistence
- Browser dashboard
- Jakarta request validation
- Service-layer business rules
- Recruitment status workflow
- Search
- Filtering
- Sorting
- Deadline intelligence
- 15 service tests
- 14 controller tests
- 6 Playwright end-to-end tests
- Page Object Model
- GitHub Actions CI
- Test plan
- Defect log

### Automated Test Baseline

```text
Service Tests       15 / 15
Controller Tests    14 / 14
Playwright Tests     6 / 6
---------------------------
Total               35 / 35
```

---

## Author

**Rajkumar Vijayan**

MSc Software Development (International Systems)  
University of Limerick, Ireland

GitHub: [Rajkumar0863](https://github.com/Rajkumar0863)

LinkedIn: [Rajkumar Vijayan](https://www.linkedin.com/in/rajkumar-vijayan-0135a8338/)
