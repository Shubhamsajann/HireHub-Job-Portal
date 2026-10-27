
const API_URL = "/api/jobs";

const jobsContainer = document.getElementById("jobsContainer");
const jobMessage = document.getElementById("jobMessage");
const searchForm = document.getElementById("searchForm");
const searchInput = document.getElementById("searchInput");
const refreshJobs = document.getElementById("refreshJobs");

const authSection = document.getElementById("authSection");
const authForm = document.getElementById("authForm");
const authTitle = document.getElementById("authTitle");
const authMessage = document.getElementById("authMessage");
const authFeedback = document.getElementById("authFeedback");
const authSubmit = document.getElementById("authSubmit");
const authSwitch = document.getElementById("authSwitch");
const nameField = document.getElementById("nameField");
const roleField = document.getElementById("roleField");

const myApplicationsButton = document.getElementById("myApplicationsButton");

const recruiterSection = document.getElementById("recruiterSection");
const postJobForm = document.getElementById("postJobForm");
const postJobMessage = document.getElementById("postJobMessage");

const applicantsSection = document.getElementById("applicantsSection");
const applicantsMessage = document.getElementById("applicantsMessage");
const applicantsContainer = document.getElementById("applicantsContainer");
const refreshApplicants = document.getElementById("refreshApplicants");


myApplicationsButton.addEventListener("click", loadMyApplications);


let allJobs = [];
let authMode = "login";

function getToken() {
    return localStorage.getItem("token");
}

// Show login or registration form
function showAuth(mode) {
    authMode = mode;
    authSection.hidden = false;
    authFeedback.textContent = "";
    authForm.reset();

    const registering = mode === "register";

    authTitle.textContent = registering
        ? "Create your account"
        : "Login to HireHub";

    authMessage.textContent = registering
        ? "Join HireHub to explore opportunities."
        : "Enter your email and password.";

    authSubmit.textContent = registering ? "Register" : "Login";
    nameField.hidden = !registering;
    roleField.hidden = !registering;

    document.getElementById("name").required = registering;

    authSwitch.textContent = registering
        ? "Already have an account? Login"
        : "Need an account? Register";

    authSection.scrollIntoView({ behavior: "smooth" });
}

// Login button
document.getElementById("loginButton").addEventListener("click", function () {
    showAuth("login");
});

// Register button
document.getElementById("registerButton").addEventListener("click", function () {
    showAuth("register");
});

// Close authentication form
document.getElementById("closeAuth").addEventListener("click", function () {
    authSection.hidden = true;
});

// Switch between login and registration
authSwitch.addEventListener("click", function () {
    showAuth(authMode === "login" ? "register" : "login");
});

// Login and registration
authForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    authFeedback.textContent = "Please wait...";
    authSubmit.disabled = true;

    const registering = authMode === "register";

    const body = {
        email: document.getElementById("email").value.trim(),
        password: document.getElementById("password").value
    };

    let endpoint = "/api/auth/login";

    if (registering) {
        body.name = document.getElementById("name").value.trim();
        body.role = document.getElementById("role").value;
        endpoint = "/api/auth/register";
    }

    try {
        const response = await fetch(endpoint, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        });

        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(
                data.message || data.error ||
                "Request failed: " + response.status
            );
        }

        if (registering) {
            showAuth("login");
            authFeedback.textContent =
                "Registration successful. Please log in.";
        } else {
            if (!data.token) {
                throw new Error("Login response did not contain a token.");
            }

            localStorage.setItem("token", data.token);
            localStorage.setItem("userName", data.name || "");
            localStorage.setItem("userRole", data.role || "");
            localStorage.setItem("userId", data.id || "");

            authSection.hidden = true;
            updateAuthButtons();
            await loadJobs();
        }
    } catch (error) {
        authFeedback.textContent =
            (registering ? "Registration failed: " : "Login failed: ") +
            error.message;

        console.error("HireHub authentication error:", error);
    } finally {
        authSubmit.disabled = false;
    }
});

// Load jobs from Spring Boot

postJobForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    postJobMessage.textContent = "Posting job...";

    const job = {
        title: document.getElementById("jobTitle").value.trim(),
        companyName: document.getElementById("companyName").value.trim(),
        location: document.getElementById("jobLocation").value.trim(),
        employmentType: document.getElementById("employmentType").value,
        salary: document.getElementById("jobSalary").value
            ? Number(document.getElementById("jobSalary").value)
            : null,
        description: document.getElementById("jobDescription").value.trim()
    };

    try {
        const response = await fetch("/api/jobs", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + getToken()
            },
            body: JSON.stringify(job)
        });

        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(
                data.message || data.error ||
                "Could not post job: " + response.status
            );
        }

        postJobMessage.textContent = "Job posted successfully!";
        postJobForm.reset();

        await loadJobs();

    } catch (error) {
        postJobMessage.textContent = error.message;
        console.error("Post job error:", error);
    }
});


async function loadApplicants() {
    applicantsMessage.textContent = "Loading applicants...";
    applicantsContainer.replaceChildren();

    try {
        const response = await fetch("/api/applications", {
            headers: {
                "Authorization": "Bearer " + getToken()
            }
        });

        if (!response.ok) {
            throw new Error("Could not load applicants: " + response.status);
        }

        const applications = await response.json();

        if (applications.length === 0) {
            applicantsMessage.textContent = "No applications found.";
            return;
        }

        applicantsMessage.textContent =
            applications.length + " application(s) found";

        applications.forEach(function (application) {
            const card = document.createElement("article");
            card.className = "job-card";

            const candidate = document.createElement("h3");
            candidate.textContent =
                application.candidateName || "Candidate";

            const job = document.createElement("p");
            job.textContent = "Job: " + (application.jobTitle || "Unknown");

            const status = document.createElement("p");
            status.textContent = "Status: " + application.status;

            const actions = document.createElement("div");

            ["ACCEPTED", "REJECTED"].forEach(function (newStatus) {
                const button = document.createElement("button");
                button.className = "btn btn-outline";
                button.textContent =
                    newStatus === "ACCEPTED" ? "Accept" : "Reject";

                button.addEventListener("click", async function () {
                    try {
                        const updateResponse = await fetch(
                            `/api/applications/${application.id}/status?status=${newStatus}`,
                            {
                                method: "PUT",
                                headers: {
                                    "Authorization": "Bearer " + getToken()
                                }
                            }
                        );

                        if (!updateResponse.ok) {
                            throw new Error(
                                "Could not update status: " +
                                updateResponse.status
                            );
                        }

                        await loadApplicants();
                    } catch (error) {
                        alert(error.message);
                    }
                });

                actions.appendChild(button);
            });

            card.append(candidate, job, status, actions);
            applicantsContainer.appendChild(card);
        });
    } catch (error) {
        applicantsMessage.textContent = "Unable to load applicants.";
        console.error("Load applicants error:", error);
    }
}

refreshApplicants.addEventListener("click", loadApplicants);


async function loadJobs() {
    jobMessage.textContent = "Loading jobs...";
    jobsContainer.replaceChildren();

    try {
        const token = getToken();

        const headers = token
            ? { "Authorization": "Bearer " + token }
            : {};

        const response = await fetch(API_URL, { headers });

        if (!response.ok) {
            throw new Error("Could not load jobs: " + response.status);
        }

        allJobs = await response.json();
        displayJobs(allJobs);
    } catch (error) {
        jobMessage.textContent =
            "Unable to load jobs. Check that Spring Boot is running.";

        console.error("Load jobs error:", error);
    }
}

async function loadMyApplications() {
    const candidateId = localStorage.getItem("userId");

    if (!getToken() || !candidateId) {
        showAuth("login");
        return;
    }

    jobMessage.textContent = "Loading your applications...";
    jobsContainer.replaceChildren();

    try {
        const response = await fetch(
            `/api/applications/candidate/${candidateId}`,
            {
                headers: {
                    "Authorization": "Bearer " + getToken()
                }
            }
        );

        if (!response.ok) {
            throw new Error("Could not load applications: " + response.status);
        }

        const applications = await response.json();

        jobsContainer.replaceChildren();

        if (applications.length === 0) {
            jobMessage.textContent = "You haven't applied for any jobs yet.";
            return;
        }

        jobMessage.textContent = "My Applications (" + applications.length + ")";

        applications.forEach(function (application) {
            const card = document.createElement("article");
            card.className = "job-card";

            const title = document.createElement("h3");
            title.textContent = application.jobTitle || "Job Application";

            const status = document.createElement("p");
            status.textContent = "Status: " + application.status;

            const appliedDate = document.createElement("p");
            appliedDate.textContent = application.appliedAt
                ? "Applied on: " + new Date(application.appliedAt).toLocaleDateString()
                : "";

            card.append(title, status, appliedDate);
            jobsContainer.appendChild(card);
        });

    } catch (error) {
        jobMessage.textContent = "Unable to load your applications.";
        console.error("My applications error:", error);
    }
}


// Display job cards
function displayJobs(jobs) {
    jobsContainer.replaceChildren();

    if (jobs.length === 0) {
        jobMessage.textContent = "No jobs found.";
        return;
    }

    jobMessage.textContent = jobs.length + " job(s) found";

    jobs.forEach(function (job) {
        const card = document.createElement("article");
        card.className = "job-card";

        const title = document.createElement("h3");
        title.textContent = job.title || "Untitled job";

        const company = document.createElement("p");
        company.className = "company";
        company.textContent = job.companyName || "Company not specified";

        const details = document.createElement("p");
        details.className = "job-details";
        details.textContent =
            (job.location || "Location not specified") + " · " +
            (job.employmentType || "Employment type not specified") + " · " +
            formatSalary(job.salary);

        const description = document.createElement("p");
        description.className = "job-description";
        description.textContent =
            job.description || "No description available.";

        // Apply button
        const applyButton = document.createElement("button");
        applyButton.className = "btn btn-primary";
        applyButton.textContent = "Apply for Job";

        applyButton.addEventListener("click", function () {
            if (!getToken()) {
                showAuth("login");
                authFeedback.textContent =
                    "Please log in as a candidate to apply.";
                return;
            }

            const role = localStorage.getItem("userRole");

            if (role !== "CANDIDATE") {
                showAuth("login");
                authFeedback.textContent =
                    "Only candidates can apply for jobs.";
                return;
            }

            applyForJob(job.id, applyButton);
        });

        card.append(
            title,
            company,
            details,
            description,
            applyButton
        );

        jobsContainer.appendChild(card);
    });
}

// Apply for a job

async function applyForJob(jobId, button) {
    const originalText = button.textContent;

    button.disabled = true;
    button.textContent = "Applying...";

    try {
        const candidateId = localStorage.getItem("userId");

        const response = await fetch(
            `/api/applications/candidate/${candidateId}/job/${jobId}`,
            {
                method: "POST",
                headers: {
                    "Authorization": "Bearer " + getToken()
                }
            }
        );

        const data = await response.json().catch(() => ({}));

        if (response.status === 409) {
            button.textContent = "Already Applied";
            button.disabled = true;
            alert("You have already applied for this job.");
            return;
        }

        if (!response.ok) {
            throw new Error(
                data.message || data.error ||
                "Application failed: " + response.status
            );
        }

        button.textContent = "Applied!";
        button.disabled = true;
        alert("Application submitted successfully!");

    } catch (error) {
        alert(error.message);
        button.textContent = originalText;
        button.disabled = false;
    }
}

// Format salary
function formatSalary(salary) {
    if (salary === null || salary === undefined || salary === "") {
        return "Salary not specified";
    }

    const amount = Number(salary);

    return Number.isFinite(amount)
        ? "₹" + amount.toLocaleString("en-IN")
        : "Salary not specified";
}

// Update login/logout buttons

function updateAuthButtons() {
    const loggedIn = Boolean(getToken());

    const loginButton = document.getElementById("loginButton");
    const registerButton = document.getElementById("registerButton");
    const myApplicationsButton = document.getElementById("myApplicationsButton");

    const role = localStorage.getItem("userRole");

    loginButton.textContent = loggedIn ? "Logout" : "Login";
    registerButton.hidden = loggedIn;

    recruiterSection.hidden = !(loggedIn && role === "RECRUITER");

    myApplicationsButton.hidden = !(loggedIn && role === "CANDIDATE");
    applicantsSection.hidden = !(loggedIn && role === "RECRUITER");

    loginButton.onclick = function () {
        if (getToken()) {
            localStorage.removeItem("token");
            localStorage.removeItem("userName");
            localStorage.removeItem("userRole");
            localStorage.removeItem("userId");

            updateAuthButtons();
            loadJobs();
        } else {
            showAuth("login");
        }
    };
}


// Search jobs
searchForm.addEventListener("submit", function (event) {
    event.preventDefault();

    const query = searchInput.value.trim().toLowerCase();

    displayJobs(
        allJobs.filter(function (job) {
            const text = [
                job.title,
                job.companyName,
                job.location,
                job.description,
                job.employmentType
            ].join(" ").toLowerCase();

            return text.includes(query);
        })
    );
});

// Refresh jobs
refreshJobs.addEventListener("click", loadJobs);

// Initialize the website
updateAuthButtons();
loadJobs();
