# eDAST

eDAST is a server-rendered web application for managing archival requests, archive staff workflows, document responses, and reading room reservations.

The application was originally developed as an academic project and has since been modernized with current Spring Boot and Spring Security versions, explicit authorization rules, automated tests, containerized local infrastructure, database migrations, and safer document storage.

## Features

### Users

- Register and sign in to a personal account.
- Submit registry, work (employment), education, cadastral, and special archival requests.
- Track personal requests and open completed responses.
- Download response documents through authorized endpoints.
- Create, view, edit, and cancel reading room reservations.

### Archive employees

- View assigned and unread requests.
- Search assigned requests by category.
- Open only requests assigned to their account.
- Write responses and attach supporting documents.
- View all and today's reading room reservations.

### Administrators

- View and search all archival requests.
- Reassign requests to archive employees.
- Create administrator and employee accounts.
- Manage user accounts and roles.
- View request and user statistics.

## Security

- Role-based authorization for users, employees, and administrators.
- Ownership and assignment checks prevent access to another user's records by changing URL identifiers.
- CSRF protection on all state-changing forms.
- Destructive and administrative mutations use `POST`; searches and views use `GET`.
- Passwords are hashed with BCrypt and validated using a shared password policy.
- Uploaded files are stored outside publicly served static resources.
- Document downloads require authorization and use generated, normalized filenames.
- Registration includes CAPTCHA validation.
- No default application credentials are included in the repository.
- Password recovery is intentionally unavailable until a token-based reset workflow is implemented.

## Technology stack

| Area                 | Technology                                            |
| -------------------- | ----------------------------------------------------- |
| Backend              | Java 21, Spring Boot 3.5                              |
| Security             | Spring Security 6                                     |
| Web UI               | Thymeleaf, HTML, CSS, JavaScript                      |
| Persistence          | Spring Data JPA, Hibernate, PostgreSQL 15             |
| Database migrations  | Flyway                                                |
| Local infrastructure | Docker Compose, Mailpit                               |
| Build and tests      | Maven Wrapper, JUnit 5, Mockito, Spring Security Test |

## Running locally

### Requirements

- JDK 21 or newer
- Docker with Docker Compose
- Git

### 1. Clone and configure the project

```bash
git clone https://github.com/ID993/edast.git
cd edast

cp src/main/resources/application.properties.example \
  src/main/resources/application.properties
```

`application.properties` is intentionally ignored by Git. Keep passwords and other local configuration out of version control.

### 2. Start PostgreSQL and Mailpit

Choose a non-empty local PostgreSQL password and export it in the current terminal:

```bash
read -rsp 'PostgreSQL password: ' DB_PASSWORD
echo
export DB_PASSWORD

docker compose up -d
docker compose ps
```

The same `DB_PASSWORD` value is used by Docker Compose and the Spring application. Environment variables are not retained automatically in a new terminal, and an existing PostgreSQL volume must be restarted with the password used when that volume was created.

### 3. Start the application

```bash
./mvnw spring-boot:run
```

Open:

- Application: <http://localhost:8080>
- Mailpit inbox: <http://localhost:8025>

Flyway applies the database migrations automatically during startup. Uploaded documents are stored in the ignored `storage/` runtime directory by default.

## Local administrator bootstrap

A fresh database intentionally contains no users or default credentials. Public registration always creates a regular user.

For local development only:

1. Register an account through <http://localhost:8080/register>.
2. Replace the example email below with the registered email and promote that account:

```bash
docker compose exec -T postgres \
  psql -U postgres -d edastdb \
  -c "UPDATE users SET role = 0 WHERE email = 'admin@example.com';"
```

In the current persisted enum mapping, role `0` is `ROLE_ADMIN`. Log out and sign in again so that Spring Security loads the updated authorities. The administrator can then create employee and additional administrator accounts through the user-management interface.

This database command is a local bootstrap procedure, not an application endpoint, and should not be used as an account-provisioning mechanism in production.

## Tests

PostgreSQL must be running and `DB_PASSWORD` must be exported before running the complete suite:

```bash
docker compose up -d
./mvnw test
```

The current suite contains 129 automated tests covering security rules, request ownership and assignment, account creation, reservations, responses, document storage, downloads, and core service behavior.

To build the executable JAR:

```bash
./mvnw clean package
java -jar target/edast-0.0.1-SNAPSHOT.jar
```

## Stopping and restarting

Stop the foreground application with `Ctrl+C`, then stop the local services while preserving database data:

```bash
docker compose stop
```

To continue in a new terminal later:

```bash
cd /path/to/edast

read -rsp 'PostgreSQL password: ' DB_PASSWORD
echo
export DB_PASSWORD

docker compose up -d
./mvnw spring-boot:run
```

To remove the containers and network while retaining the database volume:

```bash
docker compose down
```

The following command also permanently deletes the local PostgreSQL data volume:

```bash
docker compose down -v
```

## Configuration

The public configuration template is located at `src/main/resources/application.properties.example`.

| Variable                  | Purpose                   | Default                                    |
| ------------------------- | ------------------------- | ------------------------------------------ |
| `DB_PASSWORD`             | PostgreSQL password       | Required by the application                |
| `DB_URL`                  | JDBC connection URL       | `jdbc:postgresql://localhost:5432/edastdb` |
| `DB_USERNAME`             | PostgreSQL username       | `postgres`                                 |
| `DB_PORT`                 | Published PostgreSQL port | `5432`                                     |
| `MAIL_HOST`               | SMTP server               | `localhost`                                |
| `MAIL_PORT`               | SMTP port                 | `1025`                                     |
| `MAIL_FROM`               | Sender address            | `no-reply@example.com`                     |
| `STORAGE_LOCATION`        | Runtime upload directory  | `storage`                                  |
| `MAX_UPLOAD_FILE_SIZE`    | Maximum individual upload | `10MB`                                     |
| `MAX_UPLOAD_REQUEST_SIZE` | Maximum multipart request | `25MB`                                     |

## Project structure

```text
src/main/java/com/ivodam/finalpaper/edast/
├── config/       Application configuration
├── controller/   MVC and download endpoints
├── entity/       JPA entities
├── repository/   Spring Data repositories
├── security/     Authentication and authorization configuration
├── service/      Business logic and access checks
└── utility/      Supporting utilities

src/main/resources/
├── db/migration/ Flyway database migrations
├── static/       CSS and JavaScript assets
└── templates/    Thymeleaf views
```

## Author

[Ivo Damjanović](https://github.com/ID993)
