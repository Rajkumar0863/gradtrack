using GradTrack.AutomationTests.Pages;
using Microsoft.Playwright.NUnit;
using NUnit.Framework;

namespace GradTrack.AutomationTests.Tests;

public class ValidationTests : PageTest
{
    [Test]
    public async Task ShouldShowBackendValidationErrorWhenCompanyIsMissing()
    {
        var gradTrackPage =
            new GradTrackPage(Page);

        await gradTrackPage.OpenAsync();

        string uniqueRole =
            $"Validation Test Role {DateTime.Now:HHmmssfff}";

        string applicationDate =
            DateTime.Today.ToString("yyyy-MM-dd");

        string deadline =
            DateTime.Today
                .AddDays(30)
                .ToString("yyyy-MM-dd");

        await gradTrackPage
            .RemoveCompanyRequiredAttributeAsync();

        await gradTrackPage
            .FillApplicationFormAsync(
                "",
                uniqueRole,
                "SAVED",
                "HIGH",
                applicationDate,
                deadline
            );

        await gradTrackPage.SubmitAsync();

        await Expect(
            gradTrackPage.MessageBox
        ).ToContainTextAsync(
            "Company name is required"
        );

        await gradTrackPage
            .SearchAsync(
                uniqueRole
            );

        await Expect(
            gradTrackPage.ResultCount
        ).ToHaveTextAsync(
            "0 results"
        );
    }

    [Test]
    public async Task ShouldRejectDeadlineBeforeApplicationDate()
    {
        var gradTrackPage =
            new GradTrackPage(Page);

        await gradTrackPage.OpenAsync();

        string uniqueCompany =
            $"Invalid Date Test {DateTime.Now:HHmmssfff}";

        string applicationDate =
            DateTime.Today
                .AddDays(10)
                .ToString("yyyy-MM-dd");

        string deadline =
            DateTime.Today
                .AddDays(5)
                .ToString("yyyy-MM-dd");

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
            "Deadline cannot be before application date"
        );

        await gradTrackPage
            .SearchAsync(
                uniqueCompany
            );

        await Expect(
            gradTrackPage.ResultCount
        ).ToHaveTextAsync(
            "0 results"
        );
    }
}