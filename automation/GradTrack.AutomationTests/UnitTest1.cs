using Microsoft.Playwright;
using Microsoft.Playwright.NUnit;
using NUnit.Framework;

namespace GradTrack.AutomationTests;

public class GradTrackHomePageTests : PageTest
{
    private const string BaseUrl = "http://localhost:8080";

    [Test]
    public async Task ShouldOpenGradTrackHomePage()
    {
        await Page.GotoAsync(BaseUrl);

        await Expect(Page)
            .ToHaveTitleAsync("GradTrack");

        await Expect(
            Page.GetByRole(
                AriaRole.Heading,
                new() { Name = "GradTrack" }
            )
        ).ToBeVisibleAsync();

        await Expect(
            Page.GetByRole(
                AriaRole.Heading,
                new() { Name = "Applications" }
            )
        ).ToBeVisibleAsync();
    }

    [Test]
    public async Task ShouldCreateApplicationFromBrowser()
    {
        await Page.GotoAsync(BaseUrl);

        string uniqueCompany =
            $"Playwright Create Test {DateTime.Now:HHmmssfff}";

        string applicationDate =
            DateTime.Today.ToString("yyyy-MM-dd");

        string deadline =
            DateTime.Today
                .AddDays(30)
                .ToString("yyyy-MM-dd");

        try
        {
            await FillApplicationForm(
                uniqueCompany,
                "Graduate Software Engineer",
                "SAVED",
                "HIGH",
                applicationDate,
                deadline
            );

            await Page
                .Locator("#submitButton")
                .ClickAsync();

            await Expect(
                Page.Locator("#messageBox")
            ).ToContainTextAsync(
                "Application added successfully."
            );

            var applicationRow =
                GetApplicationRow(uniqueCompany);

            await Expect(
                applicationRow
            ).ToBeVisibleAsync();

            await Expect(
                applicationRow
            ).ToContainTextAsync(
                "Graduate Software Engineer"
            );

            await Expect(
                applicationRow
            ).ToContainTextAsync("Saved");

            await Expect(
                applicationRow
            ).ToContainTextAsync("High");
        }
        finally
        {
            await DeleteApplicationIfPresent(
                uniqueCompany
            );
        }
    }

    [Test]
    public async Task ShouldSearchAndFilterApplications()
    {
        await Page.GotoAsync(BaseUrl);

        string uniqueCompany =
            $"Playwright Filter Test {DateTime.Now:HHmmssfff}";

        string applicationDate =
            DateTime.Today.ToString("yyyy-MM-dd");

        string deadline =
            DateTime.Today
                .AddDays(45)
                .ToString("yyyy-MM-dd");

        try
        {
            await FillApplicationForm(
                uniqueCompany,
                "Technology Graduate",
                "SAVED",
                "HIGH",
                applicationDate,
                deadline
            );

            await Page
                .Locator("#submitButton")
                .ClickAsync();

            await Expect(
                Page.Locator("#messageBox")
            ).ToContainTextAsync(
                "Application added successfully."
            );

            var createdRow =
                GetApplicationRow(uniqueCompany);

            await Expect(
                createdRow
            ).ToBeVisibleAsync();

            await Page
                .Locator("#searchInput")
                .FillAsync(uniqueCompany);

            await Page.WaitForTimeoutAsync(500);

            var searchedRow =
                GetApplicationRow(uniqueCompany);

            await Expect(
                searchedRow
            ).ToBeVisibleAsync();

            await Expect(
                Page.Locator(
                    "#applicationTableBody tr"
                )
            ).ToHaveCountAsync(1);

            await Page
                .Locator("#statusFilter")
                .SelectOptionAsync("SAVED");

            await Page.WaitForTimeoutAsync(500);

            var statusFilteredRow =
                GetApplicationRow(uniqueCompany);

            await Expect(
                statusFilteredRow
            ).ToBeVisibleAsync();

            await Expect(
                statusFilteredRow
            ).ToContainTextAsync("Saved");

            await Page
                .Locator("#priorityFilter")
                .SelectOptionAsync("HIGH");

            await Page.WaitForTimeoutAsync(500);

            var priorityFilteredRow =
                GetApplicationRow(uniqueCompany);

            await Expect(
                priorityFilteredRow
            ).ToBeVisibleAsync();

            await Expect(
                priorityFilteredRow
            ).ToContainTextAsync("High");

            await Expect(
                Page.Locator(
                    "#applicationTableBody tr"
                )
            ).ToHaveCountAsync(1);

            await Page
                .Locator("#clearFiltersButton")
                .ClickAsync();

            await Page.WaitForTimeoutAsync(500);

            await Expect(
                Page.Locator("#searchInput")
            ).ToHaveValueAsync("");

            await Expect(
                Page.Locator("#statusFilter")
            ).ToHaveValueAsync("ALL");

            await Expect(
                Page.Locator("#priorityFilter")
            ).ToHaveValueAsync("ALL");

            var clearedFilterRow =
                GetApplicationRow(uniqueCompany);

            await Expect(
                clearedFilterRow
            ).ToBeVisibleAsync();
        }
        finally
        {
            await DeleteApplicationIfPresent(
                uniqueCompany
            );
        }
    }

    [Test]
    public async Task ShouldEditApplicationAndMoveFromSavedToApplied()
    {
        await Page.GotoAsync(BaseUrl);

        string uniqueCompany =
            $"Playwright Edit Test {DateTime.Now:HHmmssfff}";

        string originalRole =
            "Graduate Technology Analyst";

        string updatedRole =
            "Graduate Software Engineer";

        string applicationDate =
            DateTime.Today.ToString("yyyy-MM-dd");

        string deadline =
            DateTime.Today
                .AddDays(60)
                .ToString("yyyy-MM-dd");

        try
        {
            await FillApplicationForm(
                uniqueCompany,
                originalRole,
                "SAVED",
                "HIGH",
                applicationDate,
                deadline
            );

            await Page
                .Locator("#submitButton")
                .ClickAsync();

            await Expect(
                Page.Locator("#messageBox")
            ).ToContainTextAsync(
                "Application added successfully."
            );

            var applicationRow =
                GetApplicationRow(uniqueCompany);

            await Expect(
                applicationRow
            ).ToBeVisibleAsync();

            await Expect(
                applicationRow
            ).ToContainTextAsync("Saved");

            await applicationRow
                .GetByRole(
                    AriaRole.Button,
                    new() { Name = "Edit" }
                )
                .ClickAsync();

            await Expect(
                Page.Locator("#formTitle")
            ).ToHaveTextAsync(
                "Edit Application"
            );

            await Expect(
                Page.Locator("#submitButton")
            ).ToHaveTextAsync(
                "Update Application"
            );

            await Expect(
                Page.Locator("#company")
            ).ToHaveValueAsync(
                uniqueCompany
            );

            await Expect(
                Page.Locator("#role")
            ).ToHaveValueAsync(
                originalRole
            );

            await Expect(
                Page.Locator("#status")
            ).ToHaveValueAsync(
                "SAVED"
            );

            var statusOptions =
                Page.Locator("#status option");

            await Expect(
                statusOptions
            ).ToHaveCountAsync(3);

            await Expect(
                Page.Locator(
                    "#status option[value='SAVED']"
                )
            ).ToHaveCountAsync(1);

            await Expect(
                Page.Locator(
                    "#status option[value='APPLIED']"
                )
            ).ToHaveCountAsync(1);

            await Expect(
                Page.Locator(
                    "#status option[value='WITHDRAWN']"
                )
            ).ToHaveCountAsync(1);

            await Expect(
                Page.Locator(
                    "#status option[value='OFFER']"
                )
            ).ToHaveCountAsync(0);

            await Page
                .Locator("#role")
                .FillAsync(updatedRole);

            await Page
                .Locator("#status")
                .SelectOptionAsync("APPLIED");

            await Page
                .Locator("#submitButton")
                .ClickAsync();

            await Expect(
                Page.Locator("#messageBox")
            ).ToContainTextAsync(
                "Application updated successfully."
            );

            await Expect(
                Page.Locator("#formTitle")
            ).ToHaveTextAsync(
                "Add Application"
            );

            await Expect(
                Page.Locator("#submitButton")
            ).ToHaveTextAsync(
                "Add Application"
            );

            var updatedRow =
                GetApplicationRow(uniqueCompany);

            await Expect(
                updatedRow
            ).ToBeVisibleAsync();

            await Expect(
                updatedRow
            ).ToContainTextAsync(
                updatedRole
            );

            await Expect(
                updatedRow
            ).ToContainTextAsync(
                "Applied"
            );

            await Expect(
                updatedRow
            ).ToContainTextAsync(
                "High"
            );
        }
        finally
        {
            await DeleteApplicationIfPresent(
                uniqueCompany
            );
        }
    }

    [Test]
    public async Task ShouldShowBackendValidationErrorWhenCompanyIsMissing()
    {
        await Page.GotoAsync(BaseUrl);

        string uniqueRole =
            $"Validation Test {DateTime.Now:HHmmssfff}";

        string applicationDate =
            DateTime.Today.ToString("yyyy-MM-dd");

        string deadline =
            DateTime.Today
                .AddDays(30)
                .ToString("yyyy-MM-dd");

        await Page
            .Locator("#company")
            .EvaluateAsync(
                "element => element.removeAttribute('required')"
            );

        await Page
            .Locator("#company")
            .FillAsync("");

        await Page
            .Locator("#role")
            .FillAsync(uniqueRole);

        await Page
            .Locator("#status")
            .SelectOptionAsync("SAVED");

        await Page
            .Locator("#priority")
            .SelectOptionAsync("HIGH");

        await Page
            .Locator("#applicationDate")
            .FillAsync(applicationDate);

        await Page
            .Locator("#deadline")
            .FillAsync(deadline);

        await Page
            .Locator("#submitButton")
            .ClickAsync();

        await Expect(
            Page.Locator("#messageBox")
        ).ToContainTextAsync(
            "Company name is required"
        );

        await Page
            .Locator("#searchInput")
            .FillAsync(uniqueRole);

        await Page.WaitForTimeoutAsync(500);

        await Expect(
            Page.Locator("#resultCount")
        ).ToHaveTextAsync("0 results");
    }

    [Test]
    public async Task ShouldRejectDeadlineBeforeApplicationDate()
    {
        await Page.GotoAsync(BaseUrl);

        string uniqueCompany =
            $"Playwright Date Rule Test {DateTime.Now:HHmmssfff}";

        string role =
            "Graduate Software Engineer";

        string applicationDate =
            DateTime.Today
                .AddDays(10)
                .ToString("yyyy-MM-dd");

        string invalidDeadline =
            DateTime.Today
                .AddDays(5)
                .ToString("yyyy-MM-dd");

        /*
         * Application date is deliberately later
         * than the deadline.
         */

        await FillApplicationForm(
            uniqueCompany,
            role,
            "SAVED",
            "HIGH",
            applicationDate,
            invalidDeadline
        );

        await Page
            .Locator("#submitButton")
            .ClickAsync();

        /*
         * The request reaches the Java service layer.
         * JobApplicationService should reject it.
         */

        await Expect(
            Page.Locator("#messageBox")
        ).ToContainTextAsync(
            "Deadline cannot be before application date"
        );

        /*
         * Verify the invalid record was never persisted.
         */

        await Page
            .Locator("#searchInput")
            .FillAsync(uniqueCompany);

        await Page.WaitForTimeoutAsync(500);

        await Expect(
            Page.Locator("#resultCount")
        ).ToHaveTextAsync("0 results");
    }

    private async Task FillApplicationForm(
        string company,
        string role,
        string status,
        string priority,
        string applicationDate,
        string deadline)
    {
        await Page
            .Locator("#company")
            .FillAsync(company);

        await Page
            .Locator("#role")
            .FillAsync(role);

        await Page
            .Locator("#status")
            .SelectOptionAsync(status);

        await Page
            .Locator("#priority")
            .SelectOptionAsync(priority);

        await Page
            .Locator("#applicationDate")
            .FillAsync(applicationDate);

        await Page
            .Locator("#deadline")
            .FillAsync(deadline);
    }

    private ILocator GetApplicationRow(
        string companyName)
    {
        return Page
            .Locator("#applicationTableBody tr")
            .Filter(
                new LocatorFilterOptions
                {
                    HasText = companyName
                }
            );
    }

    private async Task DeleteApplicationIfPresent(
        string companyName)
    {
        try
        {
            await Page.GotoAsync(BaseUrl);

            await Page.WaitForTimeoutAsync(500);

            await Page
                .Locator("#clearFiltersButton")
                .ClickAsync();

            await Page.WaitForTimeoutAsync(500);

            var applicationRow =
                GetApplicationRow(companyName);

            int rowCount =
                await applicationRow.CountAsync();

            if (rowCount == 0)
            {
                return;
            }

            Page.Dialog += async (_, dialog) =>
            {
                await dialog.AcceptAsync();
            };

            await applicationRow
                .GetByRole(
                    AriaRole.Button,
                    new() { Name = "Delete" }
                )
                .ClickAsync();

            await Expect(
                applicationRow
            ).ToHaveCountAsync(0);
        }
        catch
        {
            /*
             * Cleanup must not hide
             * the original test failure.
             */
        }
    }
}