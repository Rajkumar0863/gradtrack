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


const applicationForm =
    document.getElementById("applicationForm");

const applicationIdInput =
    document.getElementById("applicationId");

const companyInput =
    document.getElementById("company");

const roleInput =
    document.getElementById("role");

const statusInput =
    document.getElementById("status");

const priorityInput =
    document.getElementById("priority");

const applicationDateInput =
    document.getElementById("applicationDate");

const deadlineInput =
    document.getElementById("deadline");

const submitButton =
    document.getElementById("submitButton");

const cancelEditButton =
    document.getElementById("cancelEditButton");

const refreshButton =
    document.getElementById("refreshButton");

const formTitle =
    document.getElementById("formTitle");

const messageBox =
    document.getElementById("messageBox");

const applicationTableBody =
    document.getElementById("applicationTableBody");

const totalApplications =
    document.getElementById("totalApplications");

const activeApplications =
    document.getElementById("activeApplications");

const offerApplications =
    document.getElementById("offerApplications");

const rejectedApplications =
    document.getElementById("rejectedApplications");


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


async function loadApplications() {

    try {

        const response =
            await fetch(API_URL);

        if (!response.ok) {

            throw new Error(
                "Unable to load applications"
            );
        }

        const applications =
            await response.json();

        renderApplications(
            applications
        );

        updateSummary(
            applications
        );

    } catch (error) {

        applicationTableBody.innerHTML = `
            <tr>
                <td colspan="7"
                    class="empty-state">
                    ${escapeHtml(error.message)}
                </td>
            </tr>
        `;
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
                        JSON.stringify(application)
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

        resetForm(false);

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
                        JSON.stringify(application)
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

        resetForm(false);

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


async function deleteApplication(id) {

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

        resetForm(false);

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


function renderApplications(
    applications
) {

    if (applications.length === 0) {

        applicationTableBody.innerHTML = `
            <tr>
                <td colspan="7"
                    class="empty-state">
                    No applications found.
                </td>
            </tr>
        `;

        return;
    }

    applicationTableBody.innerHTML =
        applications
            .map(application => {

                const encodedApplication =
                    encodeURIComponent(
                        JSON.stringify(
                            application
                        )
                    );

                return `
                    <tr>

                        <td>
                            ${escapeHtml(
                                application.company
                            )}
                        </td>

                        <td>
                            ${escapeHtml(
                                application.role
                            )}
                        </td>

                        <td>
                            <span class="badge
                                ${getStatusClass(
                                    application.status
                                )}">
                                ${formatStatus(
                                    application.status
                                )}
                            </span>
                        </td>

                        <td>
                            <span class="badge
                                ${getPriorityClass(
                                    application.priority
                                )}">
                                ${formatStatus(
                                    application.priority
                                )}
                            </span>
                        </td>

                        <td>
                            ${formatDate(
                                application.applicationDate
                            )}
                        </td>

                        <td>
                            ${formatDate(
                                application.deadline
                            )}
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
            })
            .join("");
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
        ] || [application.status];

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

    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}


function populateStatusOptions(
    statuses,
    selectedStatus
) {

    statusInput.innerHTML =
        statuses
            .map(status => {

                const selected =
                    status === selectedStatus
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
            })
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

    if (clearMessage) {

        hideMessage();
    }
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
                application.status
                === "OFFER"
        );

    const rejected =
        applications.filter(
            application =>
                application.status
                === "REJECTED"
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

    if (priority === "HIGH") {

        return "priority-high";
    }

    if (priority === "MEDIUM") {

        return "priority-medium";
    }

    return "priority-low";
}


function getStatusClass(
    status
) {

    if (status === "OFFER") {

        return "status-offer";
    }

    if (status === "REJECTED") {

        return "status-rejected";
    }

    if (status === "WITHDRAWN") {

        return "status-withdrawn";
    }

    return "status-active";
}


function formatStatus(value) {

    return value
        .toLowerCase()
        .split("_")
        .map(
            word =>
                word.charAt(0)
                    .toUpperCase()
                + word.slice(1)
        )
        .join(" ");
}


function formatDate(date) {

    if (!date) {

        return "-";
    }

    const parts =
        date.split("-");

    if (parts.length !== 3) {

        return date;
    }

    return `${parts[2]}/${parts[1]}/${parts[0]}`;
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

    if (type === "success") {

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

        return JSON.parse(text);

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
        typeof body === "object"
        && body.error
    ) {

        return body.error;
    }

    if (
        typeof body === "object"
    ) {

        return Object
            .values(body)
            .join(", ");
    }

    return body;
}


function escapeHtml(value) {

    if (
        value === null
        || value === undefined
    ) {

        return "";
    }

    return String(value)
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