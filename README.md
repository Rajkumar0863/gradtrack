\# GradTrack



A full-stack graduate job application tracking platform built with \*\*Java, Spring Boot, PostgreSQL, HTML, CSS, JavaScript, C#, NUnit, Playwright, and GitHub Actions\*\*.



GradTrack helps users manage graduate job applications across the complete recruitment lifecycle — from saving an opportunity through assessments and interviews to an offer, rejection, or withdrawal.



The project was built as an end-to-end software engineering and testing project, combining REST API development, database persistence, business-rule validation, frontend development, automated testing, browser automation, and continuous integration.



\---



\## Features



\### Application Management



GradTrack allows users to:



\- Create graduate job applications

\- View existing applications

\- Edit applications

\- Delete applications

\- Track recruitment status

\- Assign application priority

\- Record application dates

\- Track application deadlines



\---



\## Recruitment Status Workflow



Each application has one of the following statuses:



\- `SAVED`

\- `APPLIED`

\- `ONLINE\_ASSESSMENT`

\- `VIDEO\_INTERVIEW`

\- `ASSESSMENT\_CENTRE`

\- `FINAL\_INTERVIEW`

\- `OFFER`

\- `REJECTED`

\- `WITHDRAWN`



GradTrack enforces valid recruitment-stage transitions in the service layer.



A typical progression is:



&#x20;   SAVED

&#x20;     |

&#x20;     v

&#x20;   APPLIED

&#x20;     |

&#x20;     v

&#x20;   ONLINE\_ASSESSMENT

&#x20;     |

&#x20;     v

&#x20;   VIDEO\_INTERVIEW

&#x20;     |

&#x20;     v

&#x20;   ASSESSMENT\_CENTRE

&#x20;     |

&#x20;     v

&#x20;   FINAL\_INTERVIEW

&#x20;     |

&#x20;     v

&#x20;   OFFER



Applications may also move to `REJECTED` or `WITHDRAWN` where permitted.



`REJECTED` and `WITHDRAWN` are terminal states.



The application prevents invalid transitions such as:



&#x20;   SAVED -> OFFER



while allowing valid transitions such as:



&#x20;   SAVED -> APPLIED



The same status can also be retained when other application details are edited.



\---



\## Search, Filtering and Sorting



GradTrack supports server-side application search and filtering.



Applications can be searched by:



\- Company

\- Role



Applications can be filtered by:



\- Status

\- Priority



Supported sorting includes:



\- Deadline ascending

\- Deadline descending

\- Company ascending

\- Company descending

\- Application date ascending

\- Application date descending



Priority sorting is also supported in the browser using the business-friendly order:



&#x20;   HIGH -> MEDIUM -> LOW



Example API request:



&#x20;   GET /api/applications?search=accenture\&status=APPLIED\&priority=HIGH\&sort=deadline



This allows the browser to request only applications matching the selected criteria rather than relying entirely on client-side filtering.



\---



\## Deadline Intelligence



GradTrack provides visual deadline information to help identify applications requiring attention.



Applications are classified as:



\- Overdue

\- Due today

\- Due within 1–3 days

\- Due within 4–7 days

\- More than 7 days remaining

\- Closed



Applications with the following statuses are treated as closed:



\- `OFFER`

\- `REJECTED`

\- `WITHDRAWN`



\---



\## Technology Stack



\### Backend



\- Java 25

\- Spring Boot 4.1.1

\- Spring MVC

\- Spring Data JPA

\- Hibernate

\- Jakarta Validation

\- Maven



\### Database



\- PostgreSQL 18



\### Frontend



\- HTML5

\- CSS3

\- JavaScript



\### Java Testing



\- JUnit

\- Mockito

\- Spring MockMvc

\- Spring WebMvcTest



\### End-to-End Automation



\- C#

\- .NET 8

\- NUnit

\- Microsoft Playwright

\- Chromium



\### DevOps



\- Git

\- GitHub

\- GitHub Actions

\- Automated CI pipeline

\- PostgreSQL service container



\---



\## System Architecture



GradTrack follows a layered application architecture.



&#x20;   Browser

&#x20;      |

&#x20;      | HTTP / JSON

&#x20;      v

&#x20;   HTML / CSS / JavaScript

&#x20;      |

&#x20;      v

&#x20;   Spring Boot REST Controller

&#x20;      |

&#x20;      v

&#x20;   Service Layer

&#x20;      |

&#x20;      | Business Rules

&#x20;      v

&#x20;   Spring Data JPA Repository

&#x20;      |

&#x20;      v

&#x20;   PostgreSQL



The backend separates responsibilities across:



&#x20;   Controller

&#x20;       |

&#x20;       v

&#x20;   Service

&#x20;       |

&#x20;       v

&#x20;   Repository

&#x20;       |

&#x20;       v

&#x20;   Database



\### Controller Layer



Responsible for:



\- HTTP requests

\- Request parameters

\- Request validation

\- HTTP status codes

\- REST responses



\### Service Layer



Responsible for:



\- Application business logic

\- Date validation

\- Recruitment status transitions

\- Search and filtering coordination

\- Sorting logic



\### Repository Layer



Responsible for:



\- Database access

\- CRUD persistence

\- JPA specifications

\- Status and priority counts



\### Database Layer



PostgreSQL stores persistent graduate application data.



\---



\## REST API



Base endpoint:



&#x20;   /api/applications



\### Get All Applications



&#x20;   GET /api/applications



Returns all applications when no query parameters are supplied.



\---



\### Get Application by ID



&#x20;   GET /api/applications/{id}



Example:



&#x20;   GET /api/applications/1



Returns:



\- `200 OK` when the application exists

\- `404 Not Found` when the application does not exist



\---



\### Create Application



&#x20;   POST /api/applications



Example request body:



&#x20;   {

&#x20;     "company": "Accenture",

&#x20;     "role": "Software Engineering Graduate Programme",

&#x20;     "status": "APPLIED",

&#x20;     "priority": "HIGH",

&#x20;     "applicationDate": "2026-09-11",

&#x20;     "deadline": "2026-10-20"

&#x20;   }



A successful creation returns:



&#x20;   201 Created



\---



\### Update Application



&#x20;   PUT /api/applications/{id}



Updates an existing application while enforcing validation and status-transition rules.



Returns:



\- `200 OK` when successfully updated

\- `400 Bad Request` when a business rule is violated

\- `404 Not Found` when the application does not exist



\---



\### Delete Application



&#x20;   DELETE /api/applications/{id}



Returns:



\- `204 No Content` when successfully deleted

\- `404 Not Found` when the application does not exist



\---



\## Search API



Search by company or role:



&#x20;   GET /api/applications?search=software



Filter by status:



&#x20;   GET /api/applications?status=APPLIED



Filter by priority:



&#x20;   GET /api/applications?priority=HIGH



Sort by deadline:



&#x20;   GET /api/applications?sort=deadline



Combine multiple parameters:



&#x20;   GET /api/applications?search=software\&status=APPLIED\&priority=HIGH\&sort=deadline



\---



\## Request Validation



GradTrack validates incoming application data using Jakarta Validation.



Required fields include:



\- Company

\- Role

\- Status

\- Priority

\- Application date

\- Deadline



For example, submitting an application without a company produces a `400 Bad Request` response containing:



&#x20;   {

&#x20;     "company": "Company name is required"

&#x20;   }



Validation errors are handled centrally by:



&#x20;   GlobalExceptionHandler



This keeps error handling consistent across REST endpoints.



\---



\## Business Rule Validation



Request validation alone is not enough for application-specific rules.



GradTrack therefore performs additional validation in the service layer.



\### Deadline Rule



The deadline cannot occur before the application date.



Business rule:



&#x20;   deadline >= applicationDate



Invalid example:



&#x20;   Application Date: 2026-10-10

&#x20;   Deadline:         2026-10-05



The API rejects the request with:



&#x20;   {

&#x20;     "error": "Deadline cannot be before application date"

&#x20;   }



\---



\## Status Transition Validation



GradTrack models recruitment status as a controlled state workflow.



Examples of valid transitions include:



&#x20;   SAVED -> APPLIED



&#x20;   APPLIED -> ONLINE\_ASSESSMENT



&#x20;   ONLINE\_ASSESSMENT -> VIDEO\_INTERVIEW



&#x20;   VIDEO\_INTERVIEW -> ASSESSMENT\_CENTRE



&#x20;   ASSESSMENT\_CENTRE -> FINAL\_INTERVIEW



&#x20;   FINAL\_INTERVIEW -> OFFER



Invalid transitions are rejected by the service layer.



For example:



&#x20;   SAVED -> OFFER



produces an error similar to:



&#x20;   {

&#x20;     "error": "Invalid status transition from SAVED to OFFER"

&#x20;   }



This prevents the application from entering logically inconsistent recruitment states.



\---



\## Frontend Dashboard



GradTrack includes a browser-based dashboard served directly by Spring Boot.



The dashboard supports:



\- Application creation

\- Application editing

\- Application deletion

\- Application search

\- Status filtering

\- Priority filtering

\- Sorting

\- Application result counts

\- Deadline health indicators

\- Summary information

\- Validation feedback



The frontend communicates with the Spring Boot backend using JavaScript and the REST API.



\---



\## Server-Side Search



Search, status filtering, priority filtering, and supported sorting options are processed by the backend.



The frontend constructs query parameters and calls the REST API.



For example:



&#x20;   /api/applications?search=microsoft\&status=APPLIED\&priority=HIGH\&sort=deadline



The backend uses Spring Data JPA specifications to dynamically construct the database query.



This design keeps filtering logic reusable and avoids requiring the browser to perform all data filtering itself.



\---



\## Automated Testing



GradTrack currently contains:



&#x20;   29 Java automated tests

&#x20;    6 C# Playwright end-to-end tests

&#x20;   ---------------------------------

&#x20;   35 automated tests



The project uses multiple testing levels rather than relying on a single type of test.



\---



\## Java Service Tests



Service-layer tests use:



\- JUnit

\- Mockito



They test business logic independently from the real database.



Coverage includes:



\- Application creation

\- Application retrieval

\- Application updates

\- Application deletion

\- Date validation

\- Status-transition validation

\- Search behaviour

\- Filtering

\- Sorting

\- Repository interaction



Mockito is used to isolate the service layer from persistence.



\---



\## Controller Tests



Controller tests use:



\- Spring WebMvcTest

\- MockMvc

\- Mockito



They verify HTTP-level behaviour without requiring the real PostgreSQL database.



Coverage includes:



\- GET requests

\- POST requests

\- PUT requests

\- DELETE requests

\- HTTP status codes

\- Request validation

\- Error responses

\- Search parameters

\- Filter parameters

\- Sort parameters



\---



\## Running Java Tests



From the project root:



&#x20;   mvn clean verify



The Java regression suite currently contains:



&#x20;   29 tests



Expected result:



&#x20;   Tests run: 29

&#x20;   Failures: 0

&#x20;   Errors: 0

&#x20;   Skipped: 0



\---



\## End-to-End Browser Automation



GradTrack includes a separate C# automation project using:



\- .NET 8

\- NUnit

\- Microsoft Playwright



The tests interact with the real application through Chromium.



The current end-to-end scenarios are:



1\. Open the GradTrack dashboard

2\. Create an application through the browser

3\. Search and filter applications

4\. Edit an application and move it from `SAVED` to `APPLIED`

5\. Verify backend validation when the company is missing

6\. Verify rejection of a deadline earlier than the application date



\---



\## Full-Stack Validation Testing



The Playwright validation tests intentionally exercise more than the browser.



For example, the missing-company test removes the browser's HTML `required` attribute before submitting the form.



This allows the invalid request to reach the backend.



The test therefore verifies the complete flow:



&#x20;   Browser

&#x20;      |

&#x20;      v

&#x20;   JavaScript

&#x20;      |

&#x20;      v

&#x20;   REST API

&#x20;      |

&#x20;      v

&#x20;   Jakarta Validation

&#x20;      |

&#x20;      v

&#x20;   Global Exception Handler

&#x20;      |

&#x20;      v

&#x20;   HTTP 400 Response

&#x20;      |

&#x20;      v

&#x20;   Frontend Error Message



The test then searches for the attempted application and confirms that it was not persisted.



The deadline validation test similarly exercises:



&#x20;   Browser

&#x20;      |

&#x20;      v

&#x20;   JavaScript

&#x20;      |

&#x20;      v

&#x20;   REST API

&#x20;      |

&#x20;      v

&#x20;   Service Layer

&#x20;      |

&#x20;      v

&#x20;   Business Rule Validation

&#x20;      |

&#x20;      v

&#x20;   HTTP 400 Response

&#x20;      |

&#x20;      v

&#x20;   Frontend Error Message



This verifies both the user-facing behaviour and backend enforcement.



\---



\## Page Object Model



The Playwright automation suite uses the Page Object Model design pattern.



Project structure:



&#x20;   automation/

&#x20;   └── GradTrack.AutomationTests/

&#x20;       ├── Pages/

&#x20;       │   └── GradTrackPage.cs

&#x20;       │

&#x20;       ├── Tests/

&#x20;       │   ├── ApplicationTests.cs

&#x20;       │   ├── SearchFilterTests.cs

&#x20;       │   └── ValidationTests.cs

&#x20;       │

&#x20;       └── GradTrack.AutomationTests.csproj



`GradTrackPage.cs` contains reusable:



\- Page locators

\- Form interactions

\- Search interactions

\- Filter interactions

\- Edit interactions

\- Cleanup behaviour



The test classes contain the test scenarios and assertions.



This separates:



&#x20;   HOW the browser is controlled



from:



&#x20;   WHAT behaviour is being tested



The result is a cleaner and more maintainable automation suite.



\---



\## Running Playwright Tests



First start GradTrack from the project root.



On Windows CMD:



&#x20;   set "DB\_PASSWORD=YOUR\_POSTGRES\_PASSWORD"

&#x20;   mvn spring-boot:run



Keep that terminal running.



Open another terminal:



&#x20;   cd E:\\Projects\\gradtrack\\automation\\GradTrack.AutomationTests

&#x20;   dotnet test



Expected result:



&#x20;   Failed: 0

&#x20;   Passed: 6

&#x20;   Skipped: 0

&#x20;   Total: 6



\---



\## Running Tests in Headed Mode



To watch Chromium execute the tests:



&#x20;   set HEADED=1

&#x20;   dotnet test



For headless execution:



&#x20;   set HEADED=0

&#x20;   dotnet test



Headless mode is used by the CI pipeline.



\---



\## Continuous Integration



GradTrack uses GitHub Actions for continuous integration.



The CI pipeline runs automatically on:



\- Pushes to `main`

\- Pull requests targeting `main`



The pipeline contains separate Java and browser-automation stages.



\### Java CI Stage



The Java stage:



1\. Checks out the repository

2\. Configures Java 25

3\. Restores Maven dependencies

4\. Compiles the project

5\. Executes the Java regression suite

6\. Verifies the build



Command:



&#x20;   mvn --batch-mode clean verify



\---



\## Playwright CI Stage



After the Java stage succeeds, the end-to-end stage:



1\. Checks out the repository

2\. Configures Java 25

3\. Configures .NET 8

4\. Provisions PostgreSQL

5\. Restores C# dependencies

6\. Builds the Playwright project

7\. Installs Chromium

8\. Starts the Spring Boot application

9\. Waits for the application to become available

10\. Executes the C# Playwright suite



This allows GradTrack to be tested automatically across:



&#x20;   Browser

&#x20;      +

&#x20;   Frontend

&#x20;      +

&#x20;   REST API

&#x20;      +

&#x20;   Business Logic

&#x20;      +

&#x20;   Persistence

&#x20;      +

&#x20;   PostgreSQL



\---



\## Local Development



\### Prerequisites



Install:



\- Java 25

\- Maven

\- PostgreSQL

\- .NET 8 SDK

\- Git



\---



\## Database Setup



Create a PostgreSQL database named:



&#x20;   gradtrack



Default development connection:



&#x20;   jdbc:postgresql://localhost:5432/gradtrack



The database username defaults to:



&#x20;   postgres



The database password is not committed to source control.



\---



\## Environment-Based Database Configuration



GradTrack uses environment variables for database credentials.



The application configuration supports:



&#x20;   DB\_URL

&#x20;   DB\_USERNAME

&#x20;   DB\_PASSWORD



On Windows CMD:



&#x20;   set "DB\_PASSWORD=YOUR\_POSTGRES\_PASSWORD"



Then start GradTrack:



&#x20;   mvn spring-boot:run



This keeps database passwords out of the Git repository.



\---



\## Open GradTrack



Once Spring Boot has started, open:



&#x20;   http://localhost:8080



\---



\## Repository Structure



&#x20;   gradtrack/

&#x20;   │

&#x20;   ├── .github/

&#x20;   │   └── workflows/

&#x20;   │       └── ci.yml

&#x20;   │

&#x20;   ├── automation/

&#x20;   │   └── GradTrack.AutomationTests/

&#x20;   │       ├── Pages/

&#x20;   │       │   └── GradTrackPage.cs

&#x20;   │       │

&#x20;   │       ├── Tests/

&#x20;   │       │   ├── ApplicationTests.cs

&#x20;   │       │   ├── SearchFilterTests.cs

&#x20;   │       │   └── ValidationTests.cs

&#x20;   │       │

&#x20;   │       └── GradTrack.AutomationTests.csproj

&#x20;   │

&#x20;   ├── src/

&#x20;   │   ├── main/

&#x20;   │   │   ├── java/

&#x20;   │   │   │   └── com/

&#x20;   │   │   │       └── rajkumar/

&#x20;   │   │   │           └── gradtrack/

&#x20;   │   │   │               ├── controller/

&#x20;   │   │   │               ├── exception/

&#x20;   │   │   │               ├── model/

&#x20;   │   │   │               ├── repository/

&#x20;   │   │   │               ├── service/

&#x20;   │   │   │               └── GradTrackApplication.java

&#x20;   │   │   │

&#x20;   │   │   └── resources/

&#x20;   │   │       ├── static/

&#x20;   │   │       │   ├── index.html

&#x20;   │   │       │   ├── style.css

&#x20;   │   │       │   └── app.js

&#x20;   │   │       │

&#x20;   │   │       └── application.properties

&#x20;   │   │

&#x20;   │   └── test/

&#x20;   │       └── java/

&#x20;   │           └── com/

&#x20;   │               └── rajkumar/

&#x20;   │                   └── gradtrack/

&#x20;   │                       ├── controller/

&#x20;   │                       └── service/

&#x20;   │

&#x20;   ├── .gitignore

&#x20;   ├── pom.xml

&#x20;   └── README.md



\---



\## Key Engineering Decisions



\### Layered Backend Architecture



The REST controller does not directly perform database operations.



Instead:



&#x20;   Controller -> Service -> Repository -> PostgreSQL



This separates HTTP handling, business logic, and persistence responsibilities.



\---



\### Business Logic in the Service Layer



Rules such as:



&#x20;   Deadline cannot be before application date



and:



&#x20;   SAVED cannot transition directly to OFFER



are implemented in the service layer rather than the frontend.



This means the rules remain enforced even if a client calls the REST API directly.



\---



\### Backend Validation



Frontend validation improves usability, but it cannot be trusted as the only validation layer.



GradTrack therefore validates requests again on the server using Jakarta Validation and service-layer business rules.



\---



\### Server-Side Filtering



Search and filtering are implemented through backend query parameters and Spring Data JPA specifications.



This provides a cleaner separation between UI behaviour and data retrieval.



\---



\### Page Object Model



Browser selectors and reusable UI actions are centralised in `GradTrackPage.cs`.



This reduces duplication and makes Playwright tests easier to maintain if the frontend changes.



\---



\### Environment Variables for Credentials



Database passwords are supplied through environment variables rather than being committed to Git.



This keeps environment-specific secrets outside source control.



\---



\### Multi-Layer Automated Testing



GradTrack deliberately combines:



&#x20;   Unit / Service Tests

&#x20;           +

&#x20;   Controller Tests

&#x20;           +

&#x20;   Browser End-to-End Tests



Each layer serves a different purpose.



Service tests provide fast feedback on business logic.



Controller tests verify REST behaviour.



Playwright tests verify complete user workflows.



\---



\### Continuous Integration



Automated tests are executed by GitHub Actions rather than relying only on local testing.



The CI environment provisions the required database and browser dependencies before executing the full test workflow.



\---



\## Test Strategy



The GradTrack testing strategy can be represented as:



&#x20;                   /\\

&#x20;                  /  \\

&#x20;                 / E2E\\

&#x20;                /------\\

&#x20;               /Controller\\

&#x20;              /------------\\

&#x20;             / Service Tests\\

&#x20;            /----------------\\



The lower layers contain more focused and faster tests.



The Playwright layer contains fewer tests focused on critical end-to-end user journeys.



\---



\## Current Automated Test Summary



| Test Layer | Technology | Tests |

|---|---|---:|

| Service | JUnit + Mockito | 15 |

| Controller | MockMvc + WebMvcTest | 14 |

| End-to-End | C# + NUnit + Playwright | 6 |

| \*\*Total\*\* | | \*\*35\*\* |



All current automated tests are expected to pass before changes are merged into the main branch.



\---



\## Development Workflow



A typical GradTrack development cycle is:



&#x20;   Requirement

&#x20;       |

&#x20;       v

&#x20;   Implementation

&#x20;       |

&#x20;       v

&#x20;   Local Testing

&#x20;       |

&#x20;       v

&#x20;   Automated Regression Tests

&#x20;       |

&#x20;       v

&#x20;   Git Commit

&#x20;       |

&#x20;       v

&#x20;   GitHub Push

&#x20;       |

&#x20;       v

&#x20;   GitHub Actions CI

&#x20;       |

&#x20;       v

&#x20;   Verified Build



This workflow helps detect regressions before new changes become part of the main codebase.



\---



\## What I Learned



Building GradTrack involved working across the full software development and testing lifecycle.



Key areas included:



\- Designing REST APIs

\- Building Java applications with Spring Boot

\- Connecting applications to PostgreSQL

\- Using Spring Data JPA

\- Designing layered application architecture

\- Implementing request validation

\- Implementing service-layer business rules

\- Designing recruitment status workflows

\- Handling HTTP errors consistently

\- Building a browser-based frontend

\- Integrating JavaScript with REST APIs

\- Implementing server-side search and filtering

\- Writing JUnit tests

\- Using Mockito for dependency isolation

\- Writing MockMvc controller tests

\- Designing C# NUnit tests

\- Automating Chromium with Playwright

\- Applying the Page Object Model

\- Managing test data and cleanup

\- Testing frontend-to-backend validation

\- Configuring GitHub Actions

\- Provisioning PostgreSQL in CI

\- Running browser automation in CI

\- Debugging failures across multiple application layers



GradTrack was built to practise not just feature development, but also maintainability, automated testing, debugging, and continuous integration as parts of one software engineering workflow.



\---



\## Future Improvements



Possible future extensions include:



\- User authentication and authorisation

\- Multiple user accounts

\- Pagination for large application datasets

\- Application notes

\- Interview scheduling

\- Email reminders

\- Application analytics

\- Docker-based local deployment

\- Cloud deployment



These are intentionally outside the current core scope so that the existing application can remain focused and well-tested.



\---



\## Project Status



GradTrack currently includes:



\- Full CRUD functionality

\- PostgreSQL persistence

\- REST API

\- Request validation

\- Business-rule validation

\- Recruitment status workflow

\- Search

\- Filtering

\- Sorting

\- Deadline intelligence

\- Browser dashboard

\- 29 Java automated tests

\- 6 Playwright end-to-end tests

\- Page Object Model automation structure

\- GitHub Actions continuous integration



\*\*Total automated tests: 35\*\*



\---



\## Author



\*\*Rajkumar Vijayan\*\*



MSc Software Development (International Systems)  

University of Limerick, Ireland



GitHub: \[Rajkumar0863](https://github.com/Rajkumar0863)



LinkedIn: \[Rajkumar Vijayan](https://www.linkedin.com/in/rajkumar-vijayan-0135a8338/)

