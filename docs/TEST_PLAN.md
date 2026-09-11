\# GradTrack Test Plan



\## 1. Document Information



\*\*Project:\*\* GradTrack  

\*\*Document:\*\* Software Test Plan  

\*\*Author:\*\* Rajkumar Vijayan  

\*\*Application Type:\*\* Full-Stack Graduate Application Tracking Platform  

\*\*Backend:\*\* Java, Spring Boot, PostgreSQL  

\*\*Frontend:\*\* HTML, CSS, JavaScript  

\*\*Automation:\*\* JUnit, Mockito, MockMvc, C#, NUnit, Playwright  

\*\*CI:\*\* GitHub Actions  



\---



\## 2. Purpose



This document defines the testing approach for GradTrack.



The objective of testing is to verify that the application correctly supports graduate application management while enforcing validation rules, recruitment-stage transitions, search and filtering behaviour, and reliable frontend-to-backend integration.



The test strategy combines:



\- Service-level automated testing

\- REST controller testing

\- Browser-based end-to-end testing

\- Continuous integration



GradTrack currently contains:



&#x20;   15 Service Tests

&#x20;   14 Controller Tests

&#x20;    6 End-to-End Tests

&#x20;   -------------------

&#x20;   35 Automated Tests



\---



\## 3. Test Objectives



The main objectives are to verify that:



1\. Applications can be created successfully.

2\. Applications can be retrieved successfully.

3\. Existing applications can be updated.

4\. Applications can be deleted.

5\. Invalid requests are rejected.

6\. Application deadlines follow business rules.

7\. Recruitment status transitions follow the defined workflow.

8\. Search returns appropriate applications.

9\. Status filtering works correctly.

10\. Priority filtering works correctly.

11\. Sorting produces the expected order.

12\. The browser correctly communicates with the REST API.

13\. Backend validation errors are displayed in the browser.

14\. Invalid applications are not persisted.

15\. Critical workflows continue to work after code changes.



\---



\## 4. Scope



\### 4.1 In Scope



The following functionality is included in the current test scope:



\- Application creation

\- Application retrieval

\- Application updates

\- Application deletion

\- Request validation

\- Date validation

\- Recruitment status validation

\- Search by company

\- Search by role

\- Status filtering

\- Priority filtering

\- Sorting

\- REST response handling

\- Browser form submission

\- Browser search and filtering

\- Browser editing

\- Frontend error display

\- Frontend-to-backend integration

\- Database-backed end-to-end workflows

\- Continuous integration execution



\---



\## 5. Out of Scope



The following areas are outside the current GradTrack version:



\- Authentication

\- Authorisation

\- Multiple user accounts

\- Performance testing

\- Load testing

\- Penetration testing

\- Mobile-native testing

\- Accessibility certification

\- Cross-browser coverage beyond the current automated Chromium suite

\- Cloud deployment testing

\- Email notification testing

\- Interview calendar integration



These may be considered in future versions.



\---



\## 6. Test Levels



GradTrack uses three main automated test levels.



\### 6.1 Service Tests



\*\*Technology:\*\*



\- JUnit

\- Mockito



\*\*Number of tests:\*\*



&#x20;   15



Service tests verify application business logic independently from the real database and browser.



Primary areas include:



\- CRUD service behaviour

\- Date validation

\- Status-transition validation

\- Search behaviour

\- Filtering

\- Sorting

\- Repository interactions



Dependencies are mocked using Mockito.



This allows business logic to be tested quickly and independently.



\---



\### 6.2 Controller Tests



\*\*Technology:\*\*



\- Spring WebMvcTest

\- MockMvc

\- Mockito



\*\*Number of tests:\*\*



&#x20;   14



Controller tests verify the HTTP layer without requiring the real PostgreSQL database.



Primary areas include:



\- GET endpoints

\- POST endpoints

\- PUT endpoints

\- DELETE endpoints

\- Request validation

\- HTTP status codes

\- Error responses

\- Query parameters

\- Search

\- Filtering

\- Sorting



The service dependency is mocked so that the controller can be tested independently.



\---



\### 6.3 End-to-End Tests



\*\*Technology:\*\*



\- C#

\- .NET 8

\- NUnit

\- Microsoft Playwright

\- Chromium



\*\*Number of tests:\*\*



&#x20;   6



The end-to-end tests operate through a real browser against a running GradTrack application.



These tests exercise multiple layers together:



&#x20;   Browser

&#x20;      |

&#x20;      v

&#x20;   Frontend

&#x20;      |

&#x20;      v

&#x20;   REST API

&#x20;      |

&#x20;      v

&#x20;   Service Layer

&#x20;      |

&#x20;      v

&#x20;   Repository

&#x20;      |

&#x20;      v

&#x20;   PostgreSQL



They are used for critical user journeys rather than every possible business rule.



\---



\## 7. Test Environment



\### Local Environment



GradTrack development and testing use:



\- Windows

\- Java 25

\- Spring Boot 4.1.1

\- Maven

\- PostgreSQL 18

\- .NET 8

\- NUnit

\- Microsoft Playwright

\- Chromium



Application URL:



&#x20;   http://localhost:8080



Database:



&#x20;   gradtrack



\---



\## 8. Continuous Integration Environment



GitHub Actions provides the CI environment.



The pipeline performs:



1\. Repository checkout

2\. Java 25 setup

3\. Maven build

4\. Java automated test execution

5\. PostgreSQL service provisioning

6\. .NET 8 setup

7\. C# dependency restoration

8\. C# automation build

9\. Chromium installation

10\. Spring Boot startup

11\. Application availability check

12\. Playwright end-to-end execution



The CI workflow runs on pushes and pull requests targeting the `main` branch.



\---



\## 9. Test Data Strategy



Automated end-to-end tests generate unique application data where required.



For example, test company names may include a timestamp.



Conceptually:



&#x20;   Playwright Create Test <timestamp>



This reduces the risk of one test conflicting with data created by another test.



Where persistent test records are created, cleanup logic removes the test application after the scenario.



Cleanup is designed so that cleanup failure does not hide the original test failure.



\---



\## 10. Functional Test Scenarios



\### TC-001 — Open GradTrack Dashboard



\*\*Objective:\*\*  

Verify that the GradTrack application can be opened through the browser.



\*\*Precondition:\*\*  

GradTrack is running.



\*\*Steps:\*\*



1\. Navigate to the GradTrack application.

2\. Wait for the page to load.

3\. Verify the page title.

4\. Verify key dashboard content is visible.



\*\*Expected Result:\*\*  

The GradTrack dashboard loads successfully.



\*\*Automation Level:\*\*  

Playwright End-to-End



\---



\### TC-002 — Create Application



\*\*Objective:\*\*  

Verify that a user can create a valid graduate application through the browser.



\*\*Precondition:\*\*  

GradTrack and PostgreSQL are running.



\*\*Steps:\*\*



1\. Open GradTrack.

2\. Enter a company.

3\. Enter a role.

4\. Select a status.

5\. Select a priority.

6\. Enter an application date.

7\. Enter a valid deadline.

8\. Submit the form.

9\. Verify the success response.

10\. Verify the new application appears in the table.



\*\*Expected Result:\*\*  

The application is created and displayed successfully.



\*\*Automation Level:\*\*  

Playwright End-to-End



\---



\### TC-003 — Search and Filter Applications



\*\*Objective:\*\*  

Verify that applications can be located using search and filtering.



\*\*Precondition:\*\*  

A suitable application exists.



\*\*Steps:\*\*



1\. Open GradTrack.

2\. Create or identify an application.

3\. Search using the company name.

4\. Verify the expected result.

5\. Apply a status filter.

6\. Verify the application remains visible.

7\. Apply a priority filter.

8\. Verify the expected application remains visible.



\*\*Expected Result:\*\*  

Only applications matching the requested criteria are displayed.



\*\*Automation Level:\*\*  

Playwright End-to-End



\---



\### TC-004 — Edit Application Status



\*\*Objective:\*\*  

Verify that an application can be edited and moved through a valid recruitment-stage transition.



\*\*Example Transition:\*\*



&#x20;   SAVED -> APPLIED



\*\*Steps:\*\*



1\. Create an application with status `SAVED`.

2\. Locate the application.

3\. Select Edit.

4\. Verify the edit form is displayed.

5\. Verify permitted status options.

6\. Verify an invalid status such as `OFFER` is unavailable.

7\. Change the role if required.

8\. Change the status to `APPLIED`.

9\. Submit the update.

10\. Verify the success message.

11\. Verify the updated application in the table.



\*\*Expected Result:\*\*  

The application is updated and the valid status transition succeeds.



\*\*Automation Level:\*\*  

Playwright End-to-End



\---



\### TC-005 — Missing Company Backend Validation



\*\*Objective:\*\*  

Verify that backend validation rejects an application without a company.



\*\*Special Test Technique:\*\*  

The browser's HTML `required` attribute is removed from the company field during the test.



This allows the invalid request to reach the backend instead of being stopped by native browser validation.



\*\*Steps:\*\*



1\. Open GradTrack.

2\. Remove the company field's HTML `required` attribute.

3\. Leave the company blank.

4\. Complete the remaining required fields.

5\. Submit the application.

6\. Verify the backend validation message.

7\. Search for the attempted application.



\*\*Expected Result:\*\*



The frontend displays:



&#x20;   Company name is required



The application is not persisted.



\*\*Automation Level:\*\*  

Playwright End-to-End



\---



\### TC-006 — Deadline Before Application Date



\*\*Objective:\*\*  

Verify that GradTrack rejects an application whose deadline is earlier than its application date.



\*\*Example:\*\*



&#x20;   Application Date = Today + 10 days

&#x20;   Deadline         = Today + 5 days



\*\*Steps:\*\*



1\. Open GradTrack.

2\. Enter a valid company.

3\. Enter a valid role.

4\. Select valid status and priority values.

5\. Enter an application date.

6\. Enter a deadline earlier than the application date.

7\. Submit the form.

8\. Verify the error message.

9\. Search for the attempted application.



\*\*Expected Result:\*\*



The frontend displays:



&#x20;   Deadline cannot be before application date



The application is not persisted.



\*\*Automation Level:\*\*  

Playwright End-to-End



\---



\## 11. Status Transition Testing



The service layer controls valid recruitment status transitions.



\### SAVED



Allowed transitions:



\- APPLIED

\- WITHDRAWN



\### APPLIED



Allowed transitions:



\- ONLINE\_ASSESSMENT

\- VIDEO\_INTERVIEW

\- REJECTED

\- WITHDRAWN



\### ONLINE\_ASSESSMENT



Allowed transitions:



\- VIDEO\_INTERVIEW

\- ASSESSMENT\_CENTRE

\- REJECTED

\- WITHDRAWN



\### VIDEO\_INTERVIEW



Allowed transitions:



\- ASSESSMENT\_CENTRE

\- FINAL\_INTERVIEW

\- REJECTED

\- WITHDRAWN



\### ASSESSMENT\_CENTRE



Allowed transitions:



\- FINAL\_INTERVIEW

\- OFFER

\- REJECTED

\- WITHDRAWN



\### FINAL\_INTERVIEW



Allowed transitions:



\- OFFER

\- REJECTED

\- WITHDRAWN



\### OFFER



Allowed transition:



\- WITHDRAWN



\### REJECTED



Terminal state.



\### WITHDRAWN



Terminal state.



The same status may be retained during an update.



\---



\## 12. Validation Testing



The following required fields are validated:



| Field | Validation |

|---|---|

| Company | Must not be blank |

| Role | Must not be blank |

| Status | Must not be null |

| Priority | Must not be null |

| Application Date | Must not be null |

| Deadline | Must not be null |



Invalid requests should produce:



&#x20;   HTTP 400 Bad Request



\---



\## 13. Business Rule Testing



Business rules include:



\### BR-001



A deadline cannot occur before the application date.



&#x20;   deadline >= applicationDate



\### BR-002



Recruitment status transitions must follow the permitted workflow.



\### BR-003



Terminal recruitment states cannot progress to normal recruitment stages.



\### BR-004



Updating non-status application information may retain the existing status.



\---



\## 14. Search and Filter Testing



Search behaviour includes matching:



\- Company

\- Role



Search is case-insensitive.



Filtering includes:



\- Status

\- Priority



Parameters can be combined.



Example:



&#x20;   GET /api/applications?search=software\&status=APPLIED\&priority=HIGH\&sort=deadline



\---



\## 15. Sorting Testing



Supported backend sorting includes:



\- Deadline ascending

\- Deadline descending

\- Company ascending

\- Company descending

\- Application date ascending

\- Application date descending



Priority sorting is performed in the frontend according to:



&#x20;   HIGH

&#x20;   MEDIUM

&#x20;   LOW



\---



\## 16. API Response Testing



Expected HTTP responses include:



| Operation | Expected Response |

|---|---|

| Successful GET | 200 OK |

| Successful POST | 201 Created |

| Successful PUT | 200 OK |

| Successful DELETE | 204 No Content |

| Missing resource | 404 Not Found |

| Validation failure | 400 Bad Request |

| Business rule failure | 400 Bad Request |



\---



\## 17. Regression Testing



The automated test suite acts as the GradTrack regression suite.



Before a change is considered verified:



&#x20;   mvn clean verify



should complete successfully.



The Playwright suite should also complete successfully:



&#x20;   dotnet test



Current expected results:



&#x20;   Java Tests

&#x20;   -----------

&#x20;   Passed: 29

&#x20;   Failed: 0



&#x20;   Playwright Tests

&#x20;   ----------------

&#x20;   Passed: 6

&#x20;   Failed: 0



&#x20;   Total Automated Tests

&#x20;   ---------------------

&#x20;   Passed: 35

&#x20;   Failed: 0



\---



\## 18. Entry Criteria



Testing can begin when:



\- Required functionality has been implemented.

\- The project compiles successfully.

\- Required dependencies are available.

\- The database can be started when integration testing requires it.

\- The application can start successfully.

\- Test data requirements are understood.



For browser end-to-end testing:



\- PostgreSQL must be available.

\- GradTrack must be running.

\- Chromium must be installed through Playwright.



\---



\## 19. Exit Criteria



A change is considered ready when:



\- The application builds successfully.

\- Relevant automated tests pass.

\- No known critical defect blocks the tested functionality.

\- Existing regression tests remain successful.

\- CI completes successfully before the change is considered verified on the main branch.



For the current GradTrack baseline:



&#x20;   Java:       29 / 29

&#x20;   Playwright:  6 / 6

&#x20;   Total:      35 / 35



\---



\## 20. Test Automation Structure



The browser automation suite follows the Page Object Model.



&#x20;   GradTrack.AutomationTests/

&#x20;   |

&#x20;   ├── Pages/

&#x20;   │   └── GradTrackPage.cs

&#x20;   |

&#x20;   └── Tests/

&#x20;       ├── ApplicationTests.cs

&#x20;       ├── SearchFilterTests.cs

&#x20;       └── ValidationTests.cs



\### GradTrackPage



Contains:



\- Locators

\- Form interactions

\- Search interactions

\- Filter interactions

\- Edit interactions

\- Test-data cleanup behaviour



\### ApplicationTests



Contains scenarios related to:



\- Opening GradTrack

\- Creating applications

\- Editing applications



\### SearchFilterTests



Contains scenarios related to:



\- Searching

\- Status filtering

\- Priority filtering



\### ValidationTests



Contains scenarios related to:



\- Request validation

\- Business-rule validation



\---



\## 21. Requirements Traceability



| Requirement | Test Level |

|---|---|

| Create application | Service, Controller, E2E |

| Retrieve application | Service, Controller |

| Update application | Service, Controller, E2E |

| Delete application | Service, Controller, E2E cleanup |

| Required-field validation | Controller, E2E |

| Deadline validation | Service, E2E |

| Status workflow | Service, E2E |

| Search | Service, Controller, E2E |

| Status filter | Service, Controller, E2E |

| Priority filter | Service, Controller, E2E |

| Sorting | Service, Controller |

| Browser/API integration | E2E |

| PostgreSQL integration in user workflow | E2E |

| Automated regression | CI |



\---



\## 22. Risk-Based Testing Approach



Not every behaviour requires an end-to-end browser test.



GradTrack uses more service and controller tests because they are faster and isolate failures more precisely.



Playwright is reserved for important user journeys such as:



\- Application creation

\- Search and filtering

\- Application editing

\- Validation propagation from backend to frontend



This provides broader coverage while keeping the browser suite relatively small.



\---



\## 23. Test Pyramid



GradTrack follows a practical automated testing structure:



&#x20;                   /\\

&#x20;                  /  \\

&#x20;                 / E2E\\

&#x20;                /  6   \\

&#x20;               /--------\\

&#x20;              /Controller\\

&#x20;             /    14      \\

&#x20;            /--------------\\

&#x20;           / Service Tests  \\

&#x20;          /       15         \\

&#x20;         /--------------------\\



Total:



&#x20;   35 automated tests



\---



\## 24. CI Test Flow



The automated verification process is:



&#x20;   Developer Change

&#x20;         |

&#x20;         v

&#x20;      Git Push

&#x20;         |

&#x20;         v

&#x20;   GitHub Actions

&#x20;         |

&#x20;         v

&#x20;   Java Build + Tests

&#x20;         |

&#x20;         v

&#x20;      29 Tests

&#x20;         |

&#x20;         v

&#x20;      PostgreSQL

&#x20;         |

&#x20;         v

&#x20;   Start Spring Boot

&#x20;         |

&#x20;         v

&#x20;   Install Chromium

&#x20;         |

&#x20;         v

&#x20;   Playwright Tests

&#x20;         |

&#x20;         v

&#x20;       6 Tests

&#x20;         |

&#x20;         v

&#x20;    CI Verification



The Playwright stage depends on the Java stage succeeding first.



\---



\## 25. Test Maintenance



When application behaviour changes:



1\. Review the affected requirement.

2\. Update implementation where required.

3\. Update relevant service tests.

4\. Update controller tests when the API contract changes.

5\. Update Page Object locators if the frontend structure changes.

6\. Update Playwright scenarios when user behaviour changes.

7\. Run the full regression suite.

8\. Verify the CI pipeline.



Centralising browser selectors in the Page Object Model reduces the amount of test code that must change when the UI changes.



\---



\## 26. Test Summary



GradTrack uses automated testing at multiple layers to verify both isolated business logic and complete user workflows.



Current automated test coverage consists of:



| Layer | Framework | Count |

|---|---|---:|

| Service | JUnit + Mockito | 15 |

| Controller | WebMvcTest + MockMvc | 14 |

| End-to-End | NUnit + Playwright | 6 |

| \*\*Total\*\* | | \*\*35\*\* |



The testing approach provides:



\- Fast business-logic feedback

\- REST API verification

\- Validation testing

\- Workflow testing

\- Browser-level verification

\- Database-backed end-to-end testing

\- Automated regression through CI



\---



\## 27. Approval Status



\*\*Current Status:\*\* Active test plan for GradTrack v1.0 development.



The document should be updated whenever significant application functionality, business rules, or testing strategy changes.

