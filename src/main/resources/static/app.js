const API_URL = "/api/applications";

const ALL_STATUSES = [
    "SAVED",
    "APPLIED",
    "ONLINE_ASSESSMENT",
    "VIDEO_INTERVIEW",
    "ASSESSMENT_CENTRE",
    "FINAL_INTERVIEW",
    "OFFER",
    "REJECTED",
    "WITHDRAWN"
];

const STATUS_TRANSITIONS = {
    SAVED: [
        "SAVED",
        "APPLIED",
        "WITHDRAWN"
    ],

    APPLIED: [
        "APPLIED",
        "ONLINE_ASSESSMENT",
        "VIDEO_INTERVIEW",
        "REJECTED",
        "WITHDRAWN"
    ],

    ONLINE_ASSESSMENT: [
        "ONLINE_ASSESSMENT",
        "VIDEO_INTERVIEW",
        "ASSESSMENT_CENTRE",
        "REJECTED",
        "WITHDRAWN"
    ],

    VIDEO_INTERVIEW: [
        "VIDEO_INTERVIEW",
        "ASSESSMENT_CENTRE",
        "FINAL_INTERVIEW",
        "REJECTED",
        "WITHDRAWN"
    ],

    ASSESSMENT_CENTRE: [
        "ASSESSMENT_CENTRE",
        "FINAL_INTERVIEW",
        "OFFER",
        "REJECTED",
        "WITHDRAWN"
    ],

    FINAL_INTERVIEW: [
        "FINAL_INTERVIEW",
        "OFFER",
        "REJECTED",
        "WITHDRAWN"
    ],

    OFFER: [
        "OFFER",
        "WITHDRAWN"
    ],

    REJECTED: [
        "REJECTED"
    ],

    WITHDRAWN: [
        "WITHDRAWN"
    ]
};

const PRIORITY_ORDER = {
    HIGH: 1,
    MEDIUM: 2,
    LOW: 3
};

let allApplications = [];

const applicationForm =
    document.getElementById(
        "applicationForm"
    );

const applicationIdInput =
    document.getElementById(
        "applicationId"
    );

const companyInput =
    document.getElementById(
        "company"
    );

const roleInput =
    document.getElementById(
        "role"
    );

const statusInput =
    document.getElementById(
        "status"
    );

const priorityInput =
    document.getElementById(
        "priority"
    );

const applicationDateInput =
    document.getElementById(
        "applicationDate"
    );

const deadlineInput =
    document.getElementById(
        "deadline"
    );

const submitButton =
    document.getElementById(
        "submitButton"
    );

const cancelEditButton =
    document.getElementById(
        "cancelEditButton"
    );

const refreshButton =
    document.getElementById(
        "refreshButton"
    );

const clearFiltersButton =
    document.getElementById(
        "clearFiltersButton"
    );

const formTitle =
    document.getElementById(
        "formTitle"
    );

const messageBox =
    document.getElementById(
        "messageBox"
    );

const applicationTableBody =
    document.getElementById(
        "applicationTableBody"
    );

const totalApplications =
    document.getElementById(
        "totalApplications"
    );

const activeApplications =
    document.getElementById(
        "activeApplications"
    );

const offerApplications =
    document.getElementById(
        "offerApplications"
    );

const rejectedApplications =
    document.getElementById(
        "rejectedApplications"
    );

const searchInput =
    document.getElementById(
        "searchInput"
    );

const statusFilter =
    document.getElementById(
        "statusFilter"
    );

const priorityFilter =
    document.getElementById(
        "priorityFilter"
    );

const sortSelect =
    document.getElementById(
        "sortSelect"
    );

const resultCount =
    document.getElementById(
        "resultCount"
    );

document.addEventListener(
    "DOMContentLoaded",
    () => {

        populateStatusOptions(
            ALL_STATUSES,
            "SAVED"
        );

        setDefaultApplicationDate();

        loadApplications();
    }
);

applicationForm.addEventListener(
    "submit",
    async event => {

        event.preventDefault();

        hideMessage();

        const application = {
            company:
                companyInput.value.trim(),

            role:
                roleInput.value.trim(),

            status:
                statusInput.value,

            priority:
                priorityInput.value,

            applicationDate:
                applicationDateInput.value,

            deadline:
                deadlineInput.value
        };

        const applicationId =
            applicationIdInput.value;

        if (applicationId) {

            await updateApplication(
                applicationId,
                application
            );

        } else {

            await createApplication(
                application
            );
        }
    }
);

refreshButton.addEventListener(
    "click",
    loadApplications
);

cancelEditButton.addEventListener(
    "click",
    resetForm
);

clearFiltersButton.addEventListener(
    "click",
    clearFilters
);

searchInput.addEventListener(
    "input",
    applyFiltersAndRender
);

statusFilter.addEventListener(
    "change",
    applyFiltersAndRender
);

priorityFilter.addEventListener(
    "change",
    applyFiltersAndRender
);

sortSelect.addEventListener(
    "change",
    applyFiltersAndRender
);

async function loadApplications() {

    try {

        const response =
            await fetch(
                API_URL
            );

        if (!response.ok) {

            throw new Error(
                "Unable to load applications"
            );
        }

        allApplications =
            await response.json();

        updateSummary(
            allApplications
        );

        applyFiltersAndRender();

    } catch (error) {

        applicationTableBody.innerHTML = `
            <tr>
                <td
                    colspan="8"
                    class="empty-state"
                >
                    ${escapeHtml(error.message)}
                </td>
            </tr>
        `;

        resultCount.textContent =
            "0 results";
    }
}

async function createApplication(
    application
) {

    try {

        const response =
            await fetch(
                API_URL,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(
                            application
                        )
                }
            );

        const responseBody =
            await readResponseBody(
                response
            );

        if (!response.ok) {

            throw new Error(
                extractErrorMessage(
                    responseBody
                )
            );
        }

        resetForm(
            false
        );

        showMessage(
            "Application added successfully.",
            "success"
        );

        await loadApplications();

    } catch (error) {

        showMessage(
            error.message,
            "error"
        );
    }
}

async function updateApplication(
    id,
    application
) {

    try {

        const response =
            await fetch(
                `${API_URL}/${id}`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(
                            application
                        )
                }
            );

        const responseBody =
            await readResponseBody(
                response
            );

        if (!response.ok) {

            throw new Error(
                extractErrorMessage(
                    responseBody
                )
            );
        }

        resetForm(
            false
        );

        showMessage(
            "Application updated successfully.",
            "success"
        );

        await loadApplications();

    } catch (error) {

        showMessage(
            error.message,
            "error"
        );
    }
}

async function deleteApplication(
    id
) {

    const confirmed =
        window.confirm(
            "Are you sure you want to delete this application?"
        );

    if (!confirmed) {
        return;
    }

    try {

        const response =
            await fetch(
                `${API_URL}/${id}`,
                {
                    method: "DELETE"
                }
            );

        if (!response.ok) {

            throw new Error(
                "Unable to delete application"
            );
        }

        resetForm(
            false
        );

        showMessage(
            "Application deleted successfully.",
            "success"
        );

        await loadApplications();

    } catch (error) {

        showMessage(
            error.message,
            "error"
        );
    }
}

function applyFiltersAndRender() {

    const searchTerm =
        searchInput.value
            .trim()
            .toLowerCase();

    const selectedStatus =
        statusFilter.value;

    const selectedPriority =
        priorityFilter.value;

    const selectedSort =
        sortSelect.value;

    let filteredApplications =
        [...allApplications];

    if (searchTerm) {

        filteredApplications =
            filteredApplications.filter(
                application => {

                    const company =
                        application.company
                            .toLowerCase();

                    const role =
                        application.role
                            .toLowerCase();

                    return (
                        company.includes(
                            searchTerm
                        )
                        ||
                        role.includes(
                            searchTerm
                        )
                    );
                }
            );
    }

    if (
        selectedStatus !== "ALL"
    ) {

        filteredApplications =
            filteredApplications.filter(
                application =>
                    application.status ===
                    selectedStatus
            );
    }

    if (
        selectedPriority !== "ALL"
    ) {

        filteredApplications =
            filteredApplications.filter(
                application =>
                    application.priority ===
                    selectedPriority
            );
    }

    filteredApplications =
        sortApplications(
            filteredApplications,
            selectedSort
        );

    renderApplications(
        filteredApplications
    );

    resultCount.textContent =
        `${filteredApplications.length} result${filteredApplications.length === 1 ? "" : "s"}`;
}

function sortApplications(
    applications,
    sortType
) {

    const sorted =
        [...applications];

    switch (sortType) {

        case "DEADLINE_ASC":

            sorted.sort(
                (first, second) =>
                    new Date(
                        first.deadline
                    )
                    -
                    new Date(
                        second.deadline
                    )
            );

            break;

        case "DEADLINE_DESC":

            sorted.sort(
                (first, second) =>
                    new Date(
                        second.deadline
                    )
                    -
                    new Date(
                        first.deadline
                    )
            );

            break;

        case "COMPANY_ASC":

            sorted.sort(
                (first, second) =>
                    first.company.localeCompare(
                        second.company
                    )
            );

            break;

        case "COMPANY_DESC":

            sorted.sort(
                (first, second) =>
                    second.company.localeCompare(
                        first.company
                    )
            );

            break;

        case "PRIORITY":

            sorted.sort(
                (first, second) =>
                    PRIORITY_ORDER[
                        first.priority
                    ]
                    -
                    PRIORITY_ORDER[
                        second.priority
                    ]
            );

            break;

        default:
            break;
    }

    return sorted;
}

function renderApplications(
    applications
) {

    if (
        applications.length === 0
    ) {

        applicationTableBody.innerHTML = `
            <tr>
                <td
                    colspan="8"
                    class="empty-state"
                >
                    No applications match the current filters.
                </td>
            </tr>
        `;

        return;
    }

    applicationTableBody.innerHTML =
        applications
            .map(
                application => {

                    const encodedApplication =
                        encodeURIComponent(
                            JSON.stringify(
                                application
                            )
                        );

                    const deadlineHealth =
                        getDeadlineHealth(
                            application
                        );

                    return `
                        <tr>

                            <td>
                                ${escapeHtml(application.company)}
                            </td>

                            <td>
                                ${escapeHtml(application.role)}
                            </td>

                            <td>

                                <span
                                    class="badge ${getStatusClass(application.status)}"
                                >
                                    ${formatStatus(application.status)}
                                </span>

                            </td>

                            <td>

                                <span
                                    class="badge ${getPriorityClass(application.priority)}"
                                >
                                    ${formatStatus(application.priority)}
                                </span>

                            </td>

                            <td>
                                ${formatDate(application.applicationDate)}
                            </td>

                            <td>
                                ${formatDate(application.deadline)}
                            </td>

                            <td>

                                <span
                                    class="badge ${deadlineHealth.className}"
                                >
                                    ${deadlineHealth.label}
                                </span>

                            </td>

                            <td>

                                <div class="action-buttons">

                                    <button
                                        class="edit-button"
                                        data-application="${encodedApplication}"
                                        onclick="editApplicationFromButton(this)"
                                    >
                                        Edit
                                    </button>

                                    <button
                                        class="delete-button"
                                        onclick="deleteApplication(${application.id})"
                                    >
                                        Delete
                                    </button>

                                </div>

                            </td>

                        </tr>
                    `;
                }
            )
            .join("");
}

function getDeadlineHealth(
    application
) {

    const terminalStatuses = [
        "OFFER",
        "REJECTED",
        "WITHDRAWN"
    ];

    if (
        terminalStatuses.includes(
            application.status
        )
    ) {

        return {
            label:
                "Closed",

            className:
                "deadline-complete"
        };
    }

    const today =
        startOfDay(
            new Date()
        );

    const deadline =
        startOfDay(
            parseLocalDate(
                application.deadline
            )
        );

    const millisecondsPerDay =
        1000 * 60 * 60 * 24;

    const daysRemaining =
        Math.ceil(
            (
                deadline - today
            )
            /
            millisecondsPerDay
        );

    if (
        daysRemaining < 0
    ) {

        const overdueDays =
            Math.abs(
                daysRemaining
            );

        return {
            label:
                overdueDays === 1
                    ? "Overdue by 1 day"
                    : `Overdue by ${overdueDays} days`,

            className:
                "deadline-overdue"
        };
    }

    if (
        daysRemaining === 0
    ) {

        return {
            label:
                "Due today",

            className:
                "deadline-urgent"
        };
    }

    if (
        daysRemaining <= 3
    ) {

        return {
            label:
                `${daysRemaining} day${daysRemaining === 1 ? "" : "s"} left`,

            className:
                "deadline-urgent"
        };
    }

    if (
        daysRemaining <= 7
    ) {

        return {
            label:
                `${daysRemaining} days left`,

            className:
                "deadline-warning"
        };
    }

    return {
        label:
            `${daysRemaining} days left`,

        className:
            "deadline-safe"
    };
}

function editApplicationFromButton(
    button
) {

    const encodedApplication =
        button.dataset.application;

    const application =
        JSON.parse(
            decodeURIComponent(
                encodedApplication
            )
        );

    editApplication(
        application
    );
}

function editApplication(
    application
) {

    applicationIdInput.value =
        application.id;

    companyInput.value =
        application.company;

    roleInput.value =
        application.role;

    priorityInput.value =
        application.priority;

    applicationDateInput.value =
        application.applicationDate;

    deadlineInput.value =
        application.deadline;

    const allowedStatuses =
        STATUS_TRANSITIONS[
            application.status
        ]
        ||
        [
            application.status
        ];

    populateStatusOptions(
        allowedStatuses,
        application.status
    );

    formTitle.textContent =
        "Edit Application";

    submitButton.textContent =
        "Update Application";

    cancelEditButton.classList.remove(
        "hidden"
    );

    hideMessage();

    window.scrollTo(
        {
            top: 0,
            behavior: "smooth"
        }
    );
}

function populateStatusOptions(
    statuses,
    selectedStatus
) {

    statusInput.innerHTML =
        statuses
            .map(
                status => {

                    const selected =
                        status ===
                        selectedStatus
                            ? "selected"
                            : "";

                    return `
                        <option
                            value="${status}"
                            ${selected}
                        >
                            ${formatStatus(status)}
                        </option>
                    `;
                }
            )
            .join("");
}

function resetForm(
    clearMessage = true
) {

    applicationForm.reset();

    applicationIdInput.value =
        "";

    formTitle.textContent =
        "Add Application";

    submitButton.textContent =
        "Add Application";

    cancelEditButton.classList.add(
        "hidden"
    );

    populateStatusOptions(
        ALL_STATUSES,
        "SAVED"
    );

    priorityInput.value =
        "HIGH";

    setDefaultApplicationDate();

    if (
        clearMessage
    ) {

        hideMessage();
    }
}

function clearFilters() {

    searchInput.value =
        "";

    statusFilter.value =
        "ALL";

    priorityFilter.value =
        "ALL";

    sortSelect.value =
        "DEFAULT";

    applyFiltersAndRender();
}

function updateSummary(
    applications
) {

    totalApplications.textContent =
        applications.length;

    const active =
        applications.filter(
            application =>
                ![
                    "OFFER",
                    "REJECTED",
                    "WITHDRAWN"
                ].includes(
                    application.status
                )
        );

    const offers =
        applications.filter(
            application =>
                application.status ===
                "OFFER"
        );

    const rejected =
        applications.filter(
            application =>
                application.status ===
                "REJECTED"
        );

    activeApplications.textContent =
        active.length;

    offerApplications.textContent =
        offers.length;

    rejectedApplications.textContent =
        rejected.length;
}

function getPriorityClass(
    priority
) {

    if (
        priority === "HIGH"
    ) {

        return "priority-high";
    }

    if (
        priority === "MEDIUM"
    ) {

        return "priority-medium";
    }

    return "priority-low";
}

function getStatusClass(
    status
) {

    if (
        status === "OFFER"
    ) {

        return "status-offer";
    }

    if (
        status === "REJECTED"
    ) {

        return "status-rejected";
    }

    if (
        status === "WITHDRAWN"
    ) {

        return "status-withdrawn";
    }

    return "status-active";
}

function formatStatus(
    value
) {

    return value
        .toLowerCase()
        .split("_")
        .map(
            word =>
                word
                    .charAt(0)
                    .toUpperCase()
                +
                word.slice(1)
        )
        .join(" ");
}

function formatDate(
    date
) {

    if (!date) {
        return "-";
    }

    const parts =
        date.split("-");

    if (
        parts.length !== 3
    ) {

        return date;
    }

    return `${parts[2]}/${parts[1]}/${parts[0]}`;
}

function parseLocalDate(
    date
) {

    const [
        year,
        month,
        day
    ] =
        date
            .split("-")
            .map(Number);

    return new Date(
        year,
        month - 1,
        day
    );
}

function startOfDay(
    date
) {

    return new Date(
        date.getFullYear(),
        date.getMonth(),
        date.getDate()
    );
}

function setDefaultApplicationDate() {

    const today =
        new Date();

    const year =
        today.getFullYear();

    const month =
        String(
            today.getMonth() + 1
        ).padStart(
            2,
            "0"
        );

    const day =
        String(
            today.getDate()
        ).padStart(
            2,
            "0"
        );

    applicationDateInput.value =
        `${year}-${month}-${day}`;
}

function showMessage(
    message,
    type
) {

    messageBox.textContent =
        message;

    messageBox.className =
        "message";

    if (
        type === "success"
    ) {

        messageBox.classList.add(
            "message-success"
        );

    } else {

        messageBox.classList.add(
            "message-error"
        );
    }
}

function hideMessage() {

    messageBox.textContent =
        "";

    messageBox.className =
        "message hidden";
}

async function readResponseBody(
    response
) {

    const text =
        await response.text();

    if (!text) {
        return null;
    }

    try {

        return JSON.parse(
            text
        );

    } catch {

        return text;
    }
}

function extractErrorMessage(
    body
) {

    if (!body) {

        return "Request failed";
    }

    if (
        typeof body ===
        "object"
        &&
        body.error
    ) {

        return body.error;
    }

    if (
        typeof body ===
        "object"
    ) {

        return Object
            .values(
                body
            )
            .join(
                ", "
            );
    }

    return body;
}

function escapeHtml(
    value
) {

    if (
        value === null
        ||
        value === undefined
    ) {

        return "";
    }

    return String(
        value
    )
        .replaceAll(
            "&",
            "&amp;"
        )
        .replaceAll(
            "<",
            "&lt;"
        )
        .replaceAll(
            ">",
            "&gt;"
        )
        .replaceAll(
            '"',
            "&quot;"
        )
        .replaceAll(
            "'",
            "&#039;"
        );
}