\# GradTrack Defect Log



\## 1. Document Information



\*\*Project:\*\* GradTrack  

\*\*Document:\*\* Defect Log  

\*\*Author:\*\* Rajkumar Vijayan  

\*\*Application Type:\*\* Full-Stack Graduate Application Tracking Platform  

\*\*Version:\*\* 1.0  



\---



\## 2. Purpose



This document records defects and technical issues identified during the development and testing of GradTrack.



The purpose of the defect log is to document:



\- The issue observed

\- The affected component

\- The impact of the issue

\- How the issue was investigated

\- The root cause

\- The corrective action

\- The verification performed after the fix



This log focuses on issues encountered during the actual development and testing process.



\---



\## 3. Defect Status Definitions



| Status | Meaning |

|---|---|

| Open | Defect has been identified but not resolved |

| In Progress | Investigation or correction is underway |

| Fixed | A correction has been implemented |

| Verified | The correction has been tested successfully |

| Closed | Defect has been resolved and verification completed |



\---



\## 4. Severity Definitions



| Severity | Description |

|---|---|

| Critical | Application or major workflow cannot operate |

| High | Important functionality is unavailable or significantly incorrect |

| Medium | Functionality works partially but behaviour requires correction |

| Low | Minor issue with limited functional impact |



\---



\# DEF-001 — PostgreSQL Command Not Available from Command Prompt



\## Summary



The PostgreSQL `psql` command could not initially be executed directly from Windows Command Prompt.



\## Component



Development Environment / PostgreSQL



\## Severity



Medium



\## Status



Closed



\## Observed Behaviour



Attempting to use:



&#x20;   psql



from Command Prompt resulted in a command-not-found error.



\## Expected Behaviour



The PostgreSQL command-line client should be accessible when required for database administration and testing.



\## Impact



Database setup and manual database interaction could not initially be performed directly from the terminal.



\## Root Cause



The PostgreSQL command-line tools were installed, but the directory containing `psql.exe` was not available through the active command environment.



\## Resolution



PostgreSQL installation and command-line tooling were identified and the development workflow was adjusted so that the database could be managed correctly.



The application itself connects to PostgreSQL through JDBC and does not depend on manually invoking `psql` during normal execution.



\## Verification



GradTrack successfully connected to the PostgreSQL `gradtrack` database and persisted application records.



\## Final Status



\*\*Closed\*\*



\---



\# DEF-002 — Database Password Should Not Be Stored in Source Control



\## Summary



Database configuration required a safer method for providing PostgreSQL credentials.



\## Component



Spring Boot Configuration / Security



\## Severity



High



\## Status



Closed



\## Risk



Hardcoding a database password inside:



&#x20;   application.properties



would risk committing credentials to Git and exposing environment-specific secrets.



\## Expected Behaviour



Database credentials should be supplied externally rather than stored directly in source-controlled configuration.



\## Root Cause



Local development requires PostgreSQL authentication, but the credential is specific to the developer's environment.



\## Resolution



GradTrack was configured to use environment variables.



Configuration:



&#x20;   spring.datasource.url=${DB\_URL:jdbc:postgresql://localhost:5432/gradtrack}

&#x20;   spring.datasource.username=${DB\_USERNAME:postgres}

&#x20;   spring.datasource.password=${DB\_PASSWORD}



The password can be provided from Windows CMD using:



&#x20;   set "DB\_PASSWORD=YOUR\_POSTGRES\_PASSWORD"



The environment variable applies to the active command session.



\## Verification



GradTrack successfully starts and connects to PostgreSQL when the `DB\_PASSWORD` environment variable is supplied.



No PostgreSQL password is required in the committed application configuration.



\## Final Status



\*\*Closed\*\*



\---



\# DEF-003 — Invalid Deadline Could Produce Logically Incorrect Application Data



\## Summary



GradTrack required a business rule preventing an application deadline from occurring before the application date.



\## Component



Service Layer / Business Validation



\## Severity



High



\## Status



Closed



\## Example Invalid Data



&#x20;   Application Date: 2026-10-10

&#x20;   Deadline:         2026-10-05



\## Expected Behaviour



The system should reject an application when:



&#x20;   deadline < applicationDate



\## Required Rule



&#x20;   deadline >= applicationDate



\## Risk



Without service-layer validation, logically invalid application records could be stored even when all individual fields were syntactically valid.



\## Root Cause



Field-level validation such as `@NotNull` verifies whether dates are present, but it does not compare two fields to determine whether their relationship is logically valid.



\## Resolution



Date validation was implemented in the service layer.



When the deadline occurs before the application date, GradTrack throws an `IllegalArgumentException`.



The global exception handler converts this into an HTTP `400 Bad Request` response.



Example response:



&#x20;   {

&#x20;     "error": "Deadline cannot be before application date"

&#x20;   }



\## Verification



The rule is verified at multiple levels.



Service tests verify the business rule directly.



A Playwright end-to-end test submits:



&#x20;   Application Date = Today + 10 days

&#x20;   Deadline         = Today + 5 days



The browser receives and displays:



&#x20;   Deadline cannot be before application date



The test then searches for the attempted application and verifies that it was not persisted.



\## Final Status



\*\*Verified and Closed\*\*



\---



\# DEF-004 — Recruitment Status Could Require Invalid State Transitions



\## Summary



Application updates required controlled recruitment-stage transitions instead of allowing unrestricted movement between statuses.



\## Component



Service Layer / Application Workflow



\## Severity



High



\## Status



Closed



\## Example Invalid Transition



&#x20;   SAVED -> OFFER



\## Expected Behaviour



Applications should progress only through permitted recruitment stages.



For example:



&#x20;   SAVED -> APPLIED



is valid.



However:



&#x20;   SAVED -> OFFER



should be rejected.



\## Risk



Without transition validation, GradTrack could contain recruitment records that do not represent a valid application lifecycle.



\## Root Cause



An enum defines the available status values but does not automatically define which transitions between those values are valid.



\## Resolution



Status-transition validation was implemented in the service layer.



The service determines the set of allowed next states based on the application's current state.



Invalid transitions produce an error similar to:



&#x20;   Invalid status transition from SAVED to OFFER



The same status is allowed so that other application information can be edited without forcing a recruitment-stage change.



\## Verification



Service tests verify valid and invalid transition behaviour.



The browser edit workflow also verifies that an application in `SAVED` status exposes only:



&#x20;   SAVED

&#x20;   APPLIED

&#x20;   WITHDRAWN



The Playwright suite verifies that `OFFER` is not available when editing a `SAVED` application.



\## Final Status



\*\*Verified and Closed\*\*



\---



\# DEF-005 — Browser Validation Could Prevent Backend Validation from Being Tested



\## Summary



Native HTML validation prevented an invalid company value from reaching the backend during end-to-end testing.



\## Component



Frontend / Playwright / Backend Validation Testing



\## Severity



Medium



\## Status



Closed



\## Observed Behaviour



The company input uses the HTML:



&#x20;   required



attribute.



When the field is empty, the browser can stop form submission before the REST request reaches Spring Boot.



\## Expected Test Behaviour



The end-to-end test needed to verify that backend validation independently rejects a missing company.



The intended path was:



&#x20;   Browser

&#x20;      |

&#x20;      v

&#x20;   REST API

&#x20;      |

&#x20;      v

&#x20;   Jakarta Validation

&#x20;      |

&#x20;      v

&#x20;   HTTP 400

&#x20;      |

&#x20;      v

&#x20;   Browser Error Message



\## Root Cause



Browser-native validation was correctly protecting the user, but this meant a normal empty-field browser test would only prove frontend/browser validation.



It would not prove that the server independently enforces the same rule.



\## Resolution



During the Playwright validation test, the test removes the HTML `required` attribute from the company field.



Conceptually:



&#x20;   element.removeAttribute('required')



The rest of the form is completed normally and submitted.



This intentionally allows the invalid request to reach the backend.



\## Verification



Spring Boot rejects the request.



The frontend displays:



&#x20;   Company name is required



The test then searches for the unique attempted application and confirms:



&#x20;   0 results



This verifies that the invalid application was not persisted.



\## Final Status



\*\*Verified and Closed\*\*



\---



\# DEF-006 — Browser Tests Failed When GradTrack Was Not Running



\## Summary



The Playwright browser suite initially encountered a connection failure when the Spring Boot application was not running.



\## Component



End-to-End Test Environment



\## Severity



Medium



\## Status



Closed



\## Observed Behaviour



Playwright attempted to navigate to:



&#x20;   http://localhost:8080



and received a connection-refused error.



\## Expected Behaviour



The end-to-end suite requires a running GradTrack application before browser tests execute.



\## Root Cause



Unlike isolated unit tests, Playwright tests interact with the real running application.



Therefore they depend on:



\- Spring Boot

\- PostgreSQL

\- Application availability on port 8080



\## Resolution



The local test workflow was defined as two processes.



Terminal 1:



&#x20;   set "DB\_PASSWORD=YOUR\_POSTGRES\_PASSWORD"

&#x20;   mvn spring-boot:run



Terminal 2:



&#x20;   cd automation\\GradTrack.AutomationTests

&#x20;   dotnet test



The CI pipeline was also configured to start GradTrack automatically before executing Playwright.



\## Verification



The Playwright suite executes successfully when the application is available.



The current suite result is:



&#x20;   Passed: 6

&#x20;   Failed: 0



\## Final Status



\*\*Closed\*\*



\---



\# DEF-007 — CI Required Application Readiness Check Before Browser Tests



\## Summary



Starting Spring Boot in CI does not guarantee that the application is immediately ready to accept browser requests.



\## Component



GitHub Actions / Continuous Integration



\## Severity



High



\## Status



Closed



\## Risk



If Playwright starts immediately after launching Spring Boot, tests may attempt to access:



&#x20;   http://localhost:8080



before the application has completed startup.



This can create timing-related CI failures.



\## Expected Behaviour



Browser automation should start only after GradTrack is available.



\## Resolution



A readiness check was added to the GitHub Actions workflow.



The pipeline repeatedly checks:



&#x20;   http://localhost:8080



before starting the Playwright suite.



If GradTrack becomes available, CI continues.



If the application does not become available within the allowed attempts, the workflow prints the application log and fails.



\## Verification



The CI pipeline successfully:



1\. Provisions PostgreSQL

2\. Starts GradTrack

3\. Waits for GradTrack

4\. Executes Playwright



The Java and browser automation stages have successfully executed through GitHub Actions.



\## Final Status



\*\*Verified and Closed\*\*



\---



\# DEF-008 — Playwright Test Code Became Concentrated in a Single Test File



\## Summary



The initial Playwright automation suite contained browser locators, reusable interactions, cleanup behaviour, and multiple test scenarios in a single `UnitTest1.cs` file.



\## Component



C# Test Automation / Maintainability



\## Severity



Medium



\## Status



Closed



\## Observed Structure



Initially:



&#x20;   GradTrack.AutomationTests/

&#x20;       |

&#x20;       └── UnitTest1.cs



As additional scenarios were introduced, the file contained responsibilities for:



\- Browser selectors

\- Form interactions

\- Search interactions

\- Filtering

\- Editing

\- Cleanup

\- Assertions

\- Multiple test scenarios



\## Risk



Keeping all browser automation in one file would make the suite increasingly difficult to:



\- Read

\- Maintain

\- Extend

\- Debug

\- Update when frontend selectors change



\## Root Cause



The automation project began with a small number of tests and expanded incrementally.



The original structure was sufficient for initial testing but became less maintainable as coverage increased.



\## Resolution



The automation suite was refactored using the Page Object Model.



Current structure:



&#x20;   GradTrack.AutomationTests/

&#x20;       |

&#x20;       ├── Pages/

&#x20;       │   └── GradTrackPage.cs

&#x20;       |

&#x20;       └── Tests/

&#x20;           ├── ApplicationTests.cs

&#x20;           ├── SearchFilterTests.cs

&#x20;           └── ValidationTests.cs



`GradTrackPage.cs` centralises reusable browser interactions and selectors.



The test files retain scenario logic and assertions.



\## Verification



After removing the original `UnitTest1.cs`, the refactored suite was executed.



Result:



&#x20;   Failed: 0

&#x20;   Passed: 6

&#x20;   Skipped: 0

&#x20;   Total: 6



This confirmed that the refactor preserved existing automated behaviour.



\## Final Status



\*\*Verified and Closed\*\*



\---



\# DEF-009 — Search and Filtering Required Backend Support



\## Summary



As GradTrack's dashboard functionality expanded, search and filtering required a scalable separation between the browser and backend.



\## Component



REST API / Service Layer / Repository / Frontend



\## Severity



Medium



\## Status



Closed



\## Requirement



Users should be able to search and filter applications by:



\- Company or role

\- Status

\- Priority



and apply supported sorting.



\## Risk



Performing all filtering exclusively in JavaScript would require the browser to retrieve the complete application collection before processing it.



That would tightly couple data retrieval and presentation behaviour.



\## Resolution



Backend query parameters were introduced.



Examples:



&#x20;   GET /api/applications?search=accenture



&#x20;   GET /api/applications?status=APPLIED



&#x20;   GET /api/applications?priority=HIGH



Combined:



&#x20;   GET /api/applications?search=accenture\&status=APPLIED\&priority=HIGH\&sort=deadline



The repository was extended with JPA specification support, while the service dynamically builds the required query criteria.



The frontend constructs the required query string from the selected controls.



\## Verification



Search, filtering, sorting, and combined query behaviour were tested through the backend and browser workflow.



The Playwright suite includes a search-and-filter end-to-end scenario.



\## Final Status



\*\*Verified and Closed\*\*



\---



\# DEF-010 — Playwright Tests Required Reliable Test Data Cleanup



\## Summary



Browser automation creates real records in PostgreSQL and therefore required a cleanup strategy.



\## Component



Playwright / Test Data Management



\## Severity



Medium



\## Status



Closed



\## Risk



Without cleanup, repeated automated runs could leave test applications in the database.



This could:



\- Pollute development data

\- Produce duplicate test records

\- Make search assertions less predictable

\- Reduce test isolation



\## Resolution



Reusable cleanup behaviour was implemented in the Page Object Model.



Tests that create persistent records use unique names and remove their test data after execution.



Cleanup is executed in a way that does not hide the original test failure.



\## Verification



Repeated browser test runs can create and remove temporary application records while preserving the expected test results.



Current Playwright result:



&#x20;   Passed: 6

&#x20;   Failed: 0



\## Final Status



\*\*Verified and Closed\*\*



\---



\## 5. Defect Summary



| ID | Area | Severity | Status |

|---|---|---|---|

| DEF-001 | PostgreSQL CLI Environment | Medium | Closed |

| DEF-002 | Database Credential Configuration | High | Closed |

| DEF-003 | Deadline Business Rule | High | Verified / Closed |

| DEF-004 | Status Transition Rules | High | Verified / Closed |

| DEF-005 | Backend Validation E2E Testing | Medium | Verified / Closed |

| DEF-006 | E2E Application Availability | Medium | Closed |

| DEF-007 | CI Application Readiness | High | Verified / Closed |

| DEF-008 | Playwright Test Maintainability | Medium | Verified / Closed |

| DEF-009 | Server-Side Search and Filtering | Medium | Verified / Closed |

| DEF-010 | Automated Test Data Cleanup | Medium | Verified / Closed |



\---



\## 6. Defect Distribution



\### By Severity



&#x20;   Critical: 0

&#x20;   High:     4

&#x20;   Medium:   6

&#x20;   Low:      0



\### By Current Status



&#x20;   Open:              0

&#x20;   In Progress:       0

&#x20;   Fixed:             0

&#x20;   Verified / Closed: 7

&#x20;   Closed:            3



No known critical or high-severity defect remains open in the documented GradTrack v1.0 baseline.



\---



\## 7. Lessons from Defect Resolution



The issues identified during GradTrack development demonstrate several software engineering principles.



\### Validation Must Exist on the Server



HTML validation improves user experience but cannot replace backend validation.



A client can bypass browser validation and call the REST API directly.



Therefore GradTrack validates important input on the backend.



\### Business Rules Belong in the Business Layer



Rules such as deadline ordering and recruitment-stage transitions are enforced in the service layer rather than relying only on the UI.



\### End-to-End Tests Have Infrastructure Dependencies



Browser tests require the application and its dependent services to be available.



This dependency must be managed both locally and in CI.



\### CI Must Account for Startup Time



Starting a process does not mean the process is immediately ready.



The readiness check prevents browser tests from racing against application startup.



\### Test Automation Requires Maintainable Architecture



As browser coverage increased, moving from a single test file to the Page Object Model reduced duplication and separated reusable interactions from test assertions.



\### Test Data Must Be Controlled



End-to-end automation interacting with a real database requires unique test data and cleanup to preserve repeatability.



\---



\## 8. Current Quality Baseline



The current GradTrack baseline contains:



&#x20;   Java Service Tests:      15

&#x20;   Java Controller Tests:   14

&#x20;   Playwright E2E Tests:     6

&#x20;   ---------------------------

&#x20;   Total Automated Tests:   35



Current expected regression result:



&#x20;   35 Passed

&#x20;    0 Failed



The GitHub Actions pipeline provides automated verification of both the Java regression suite and browser-level end-to-end tests.



\---



\## 9. Document Status



\*\*GradTrack v1.0 Defect Log\*\*



All defects and technical issues currently documented in this log have been resolved or verified.



Future defects should be added using the same structure:



&#x20;   Defect ID

&#x20;   Summary

&#x20;   Component

&#x20;   Severity

&#x20;   Status

&#x20;   Observed Behaviour

&#x20;   Expected Behaviour

&#x20;   Root Cause

&#x20;   Resolution

&#x20;   Verification

&#x20;   Final Status

