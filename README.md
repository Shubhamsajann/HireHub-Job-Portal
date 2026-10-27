# HireHub — Job Portal & Recruitment Management System

HireHub is a web-based job portal developed using Java and Spring Boot. It enables candidates to search and apply for jobs while allowing recruiters to post job openings and manage applications.

## Features

### Candidate
- Register and log in.
- Browse and search available jobs.
- Apply for jobs.
- Prevent duplicate applications.
- View application status.

### Recruiter
- Register and log in.
- Post job openings.
- View applicants and their application statuses.
- Accept or reject applications.

## Technology Stack

- **Backend:** Java, Spring Boot, Spring Web
- **Security:** Spring Security, JWT
- **Database:** MySQL
- **Persistence:** Spring Data JPA, Hibernate
- **Frontend:** HTML, CSS, JavaScript
- **Build Tool:** Maven

## Project Architecture

The project follows a layered architecture:

1. **Controller:** Handles HTTP requests and responses.
2. **Service:** Implements business logic.
3. **Repository:** Performs database operations through Spring Data JPA.
4. **Entity:** Represents database records.
5. **Frontend:** Communicates with the backend through REST APIs.

## Main API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Register a user |
| POST | `/api/auth/login` | Authenticate a user |
| GET | `/api/jobs` | Retrieve available jobs |
| GET | `/api/jobs/{id}` | Retrieve a job by ID |
| POST | `/api/jobs` | Create a job |
| PUT | `/api/jobs/{id}` | Update a job |
| DELETE | `/api/jobs/{id}` | Delete a job |
| POST | `/api/applications/candidate/{candidateId}/job/{jobId}` | Apply for a job |
| GET | `/api/applications/candidate/{candidateId}` | View candidate applications |
| GET | `/api/applications` | Retrieve applications |
| PUT | `/api/applications/{applicationId}/status?status=ACCEPTED` | Update application status |

Authentication and authorization requirements apply to protected endpoints.

## Prerequisites

- Java JDK compatible with the project configuration
- Maven
- MySQL Server
- IntelliJ IDEA or another Java IDE

## Setup and Run

1. Clone this repository:

   ```bash
   git clone YOUR_GITHUB_REPOSITORY_URL
   cd hirehub
   ```

2. Create a MySQL database named `hirehub`, or use the database configured in your application.

3. Configure the database connection and JWT secret using local configuration or environment variables.

4. Build the application:

   ```bash
   mvn clean package
   ```

5. Run the application:

   ```bash
   mvn spring-boot:run
   ```

6. Open the application in your browser:

   `http://localhost:8080/`

## Configuration and Security

Do not commit real database passwords, JWT signing secrets, API keys, or other sensitive configuration. Use local environment variables or an untracked configuration file for private values.

## Future Improvements

- Automated tests for REST APIs
- Pagination and advanced job filtering
- Email notifications
- Deployment to a cloud platform

## Author

**Shubham Sajjan**

GitHub: https://github.com/Shubhamsajann
