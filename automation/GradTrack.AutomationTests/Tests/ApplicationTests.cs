using GradTrack.AutomationTests.Pages;
using Microsoft.Playwright;
using Microsoft.Playwright.NUnit;
using NUnit.Framework;

namespace GradTrack.AutomationTests.Tests;

public class ApplicationTests : PageTest
{
    [Test]
    public async Task ShouldOpenGradTrackHomePage()
    {
        var gradTrackPage =
            new GradTrackPage(Page);

        await gradTrackPage.OpenAsync();

        await Expect(Page)
            .ToHaveTitleAsync("GradTrack");

        await Expect(
            Page.GetByRole(
                AriaRole.Heading,
                new()
                {
                    Name = "GradTrack"
                }
            )
        ).ToBeVisibleAsync();

        await Expect(
            Page.GetByRole(
                AriaRole.Heading,
                new()
                {
                    Name = "Applications"
                }
            )
        ).ToBeVisibleAsync();
    }

    [Test]
    public async Task ShouldCreateApplicationFromBrowser()
    {
        var gradTrackPage =
            new GradTrackPage(Page);

        await gradTrackPage.OpenAsync();

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
            await gradTrackPage
                .FillApplicationFormAsync(
                    uniqueCompany,
                    "Graduate Software Engineer",
                    "SAVED",
                    "HIGH",
                    applicationDate,
                    deadline
                );

            await gradTrackPage.SubmitAsync();

            await Expect(
                gradTrackPage.MessageBox
            ).ToContainTextAsync(
                "Application added successfully."
            );

            var applicationRow =
                gradTrackPage
                    .GetApplicationRow(
                        uniqueCompany
                    );

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
            await gradTrackPage
                .DeleteApplicationIfPresentAsync(
                    uniqueCompany
                );
        }
    }

    [Test]
    public async Task ShouldEditApplicationAndMoveFromSavedToApplied()
    {
        var gradTrackPage =
            new GradTrackPage(Page);

        await gradTrackPage.OpenAsync();

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
            await gradTrackPage
                .FillApplicationFormAsync(
                    uniqueCompany,
                    originalRole,
                    "SAVED",
                    "HIGH",
                    applicationDate,
                    deadline
                );

            await gradTrackPage.SubmitAsync();

            await Expect(
                gradTrackPage.MessageBox
            ).ToContainTextAsync(
                "Application added successfully."
            );

            var applicationRow =
                gradTrackPage
                    .GetApplicationRow(
                        uniqueCompany
                    );

            await Expect(
                applicationRow
            ).ToBeVisibleAsync();

            await Expect(
                applicationRow
            ).ToContainTextAsync("Saved");

            await gradTrackPage
                .ClickEditAsync(uniqueCompany);

            await Expect(
                gradTrackPage.FormTitle
            ).ToHaveTextAsync(
                "Edit Application"
            );

            await Expect(
                gradTrackPage.SubmitButton
            ).ToHaveTextAsync(
                "Update Application"
            );

            await Expect(
                gradTrackPage.CompanyInput
            ).ToHaveValueAsync(
                uniqueCompany
            );

            await Expect(
                gradTrackPage.RoleInput
            ).ToHaveValueAsync(
                originalRole
            );

            await Expect(
                gradTrackPage.StatusSelect
            ).ToHaveValueAsync(
                "SAVED"
            );

            await Expect(
                gradTrackPage.StatusOptions
            ).ToHaveCountAsync(3);

            await Expect(
                gradTrackPage
                    .GetStatusOption("SAVED")
            ).ToHaveCountAsync(1);

            await Expect(
                gradTrackPage
                    .GetStatusOption("APPLIED")
            ).ToHaveCountAsync(1);

            await Expect(
                gradTrackPage
                    .GetStatusOption("WITHDRAWN")
            ).ToHaveCountAsync(1);

            await Expect(
                gradTrackPage
                    .GetStatusOption("OFFER")
            ).ToHaveCountAsync(0);

            await gradTrackPage
                .RoleInput
                .FillAsync(updatedRole);

            await gradTrackPage
                .StatusSelect
                .SelectOptionAsync("APPLIED");

            await gradTrackPage.SubmitAsync();

            await Expect(
                gradTrackPage.MessageBox
            ).ToContainTextAsync(
                "Application updated successfully."
            );

            await Expect(
                gradTrackPage.FormTitle
            ).ToHaveTextAsync(
                "Add Application"
            );

            await Expect(
                gradTrackPage.SubmitButton
            ).ToHaveTextAsync(
                "Add Application"
            );

            var updatedRow =
                gradTrackPage
                    .GetApplicationRow(
                        uniqueCompany
                    );

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
            await gradTrackPage
                .DeleteApplicationIfPresentAsync(
                    uniqueCompany
                );
        }
    }
}