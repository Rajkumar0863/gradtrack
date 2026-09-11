using Microsoft.Playwright;

namespace GradTrack.AutomationTests.Pages;

public class GradTrackPage
{
    private readonly IPage _page;

    private const string BaseUrl = "http://localhost:8080";

    public GradTrackPage(IPage page)
    {
        _page = page;
    }

    public IPage Page => _page;

    public ILocator CompanyInput =>
        _page.Locator("#company");

    public ILocator RoleInput =>
        _page.Locator("#role");

    public ILocator StatusSelect =>
        _page.Locator("#status");

    public ILocator PrioritySelect =>
        _page.Locator("#priority");

    public ILocator ApplicationDateInput =>
        _page.Locator("#applicationDate");

    public ILocator DeadlineInput =>
        _page.Locator("#deadline");

    public ILocator SubmitButton =>
        _page.Locator("#submitButton");

    public ILocator MessageBox =>
        _page.Locator("#messageBox");

    public ILocator FormTitle =>
        _page.Locator("#formTitle");

    public ILocator SearchInput =>
        _page.Locator("#searchInput");

    public ILocator StatusFilter =>
        _page.Locator("#statusFilter");

    public ILocator PriorityFilter =>
        _page.Locator("#priorityFilter");

    public ILocator SortSelect =>
        _page.Locator("#sortSelect");

    public ILocator ClearFiltersButton =>
        _page.Locator("#clearFiltersButton");

    public ILocator ApplicationTableBody =>
        _page.Locator("#applicationTableBody");

    public ILocator ApplicationRows =>
        _page.Locator("#applicationTableBody tr");

    public ILocator ResultCount =>
        _page.Locator("#resultCount");

    public ILocator StatusOptions =>
        _page.Locator("#status option");

    public async Task OpenAsync()
    {
        await _page.GotoAsync(BaseUrl);
    }

    public async Task FillApplicationFormAsync(
        string company,
        string role,
        string status,
        string priority,
        string applicationDate,
        string deadline)
    {
        await CompanyInput.FillAsync(company);

        await RoleInput.FillAsync(role);

        await StatusSelect.SelectOptionAsync(status);

        await PrioritySelect.SelectOptionAsync(priority);

        await ApplicationDateInput.FillAsync(applicationDate);

        await DeadlineInput.FillAsync(deadline);
    }

    public async Task SubmitAsync()
    {
        await SubmitButton.ClickAsync();
    }

    public ILocator GetApplicationRow(
        string companyName)
    {
        return ApplicationRows.Filter(
            new LocatorFilterOptions
            {
                HasText = companyName
            }
        );
    }

    public async Task SearchAsync(
        string searchText)
    {
        await SearchInput.FillAsync(searchText);

        await _page.WaitForTimeoutAsync(500);
    }

    public async Task FilterByStatusAsync(
        string status)
    {
        await StatusFilter.SelectOptionAsync(status);

        await _page.WaitForTimeoutAsync(500);
    }

    public async Task FilterByPriorityAsync(
        string priority)
    {
        await PriorityFilter.SelectOptionAsync(priority);

        await _page.WaitForTimeoutAsync(500);
    }

    public async Task ClearFiltersAsync()
    {
        await ClearFiltersButton.ClickAsync();

        await _page.WaitForTimeoutAsync(500);
    }

    public async Task ClickEditAsync(
        string companyName)
    {
        var row =
            GetApplicationRow(companyName);

        await row
            .GetByRole(
                AriaRole.Button,
                new()
                {
                    Name = "Edit"
                }
            )
            .ClickAsync();
    }

    public async Task DeleteApplicationIfPresentAsync(
        string companyName)
    {
        try
        {
            await OpenAsync();

            await _page.WaitForTimeoutAsync(500);

            await ClearFiltersAsync();

            var row =
                GetApplicationRow(companyName);

            int rowCount =
                await row.CountAsync();

            if (rowCount == 0)
            {
                return;
            }

            _page.Dialog += async (_, dialog) =>
            {
                await dialog.AcceptAsync();
            };

            await row
                .GetByRole(
                    AriaRole.Button,
                    new()
                    {
                        Name = "Delete"
                    }
                )
                .ClickAsync();

            await row.WaitForAsync(
                new LocatorWaitForOptions
                {
                    State =
                        WaitForSelectorState.Detached
                }
            );
        }
        catch
        {
            /*
             * Cleanup must not hide
             * the original test failure.
             */
        }
    }

    public async Task RemoveCompanyRequiredAttributeAsync()
    {
        await CompanyInput.EvaluateAsync(
            "element => element.removeAttribute('required')"
        );
    }

    public ILocator GetStatusOption(
        string status)
    {
        return _page.Locator(
            $"#status option[value='{status}']"
        );
    }
}