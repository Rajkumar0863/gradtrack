using GradTrack.AutomationTests.Pages;
using Microsoft.Playwright.NUnit;
using NUnit.Framework;

namespace GradTrack.AutomationTests.Tests;

public class SearchFilterTests : PageTest
{
    [Test]
    public async Task ShouldSearchAndFilterApplications()
    {
        var gradTrackPage =
            new GradTrackPage(Page);

        await gradTrackPage.OpenAsync();

        string uniqueCompany =
            $"Playwright Search Test {DateTime.Now:HHmmssfff}";

        string role =
            "Technology Consultant";

        string applicationDate =
            DateTime.Today.ToString("yyyy-MM-dd");

        string deadline =
            DateTime.Today
                .AddDays(45)
                .ToString("yyyy-MM-dd");

        try
        {
            await gradTrackPage
                .FillApplicationFormAsync(
                    uniqueCompany,
                    role,
                    "APPLIED",
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

            await gradTrackPage
                .SearchAsync(
                    uniqueCompany
                );

            await Expect(
                gradTrackPage.ResultCount
            ).ToHaveTextAsync(
                "1 result"
            );

            await Expect(
                applicationRow
            ).ToBeVisibleAsync();

            await gradTrackPage
                .FilterByStatusAsync(
                    "APPLIED"
                );

            await Expect(
                applicationRow
            ).ToBeVisibleAsync();

            await gradTrackPage
                .FilterByPriorityAsync(
                    "HIGH"
                );

            await Expect(
                applicationRow
            ).ToBeVisibleAsync();

            await Expect(
                applicationRow
            ).ToContainTextAsync(
                uniqueCompany
            );

            await Expect(
                applicationRow
            ).ToContainTextAsync(
                role
            );

            await Expect(
                applicationRow
            ).ToContainTextAsync(
                "Applied"
            );

            await Expect(
                applicationRow
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