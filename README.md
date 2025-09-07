# Chat Storage Service

A Java Spring Boot application for storing and managing chat sessions and messages.  
Built with **Spring Boot, Gradle, PostgreSQL, Liquibase, and Docker Compose**.  
Supports environment-based configuration for **dev/uat/prod**.

---

## 🚀 Features

- RESTful API with **Spring Boot**
- **Spring Data JPA** for persistence
- **Liquibase** for database migrations
- **PostgreSQL with pgvector** support
- **Docker Compose** for local deployment
- API documentation with **Swagger UI**
- Health monitoring via **Spring Actuator**
- Database management via **pgAdmin**

---

## 🛠️ Technologies

- Java 17+
- Spring Boot
- Gradle
- PostgreSQL + pgvector
- Liquibase
- Docker & Docker Compose

---

## 📂 Project Structure

- `src/main/java/com/raga/chat/` — Source code
- `src/main/resources/application.yaml` — Default configuration
- `src/main/resources/db/changelog/` — Liquibase changelogs
- `docker-compose.yml` — Multi-container orchestration
- `Dockerfile` — Container build instructions
- `config/env/` — Environment-specific `.env` files

---

## ⚙️ Setup & Run

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/Thirumala276/chat-storage-service.git
cd chat-storage-service
```

### 2️⃣ Build the Project
```bash
./gradlew clean build
```

### 3️⃣ Run with Docker Compose
```bash
docker-compose --env-file ./config/env/dev.env up --build
```

This will start:
- **chat-storage-service** (Spring Boot app)
- **PostgreSQL** (with pgvector)
- **pgAdmin**

---

## 🌐 URLs

- **Swagger UI** → [http://localhost:8080/chat/swagger-ui/index.html](http://localhost:8080/chat/swagger-ui/index.html)
- **pgAdmin** → [http://localhost:9090/login](http://localhost:9090/login)
    - Email: `admin@company.com`
    - Password: `admin`
- **Actuator Health** → [http://localhost:8080/chat/actuator/health](http://localhost:8080/chat/actuator/health)

---

## 🗄️ Connect pgAdmin to PostgreSQL

1. Open pgAdmin in your browser:  
   👉 [http://localhost:9090/login](http://localhost:9090/login)

    - **Email:** `admin@company.com`
    - **Password:** `admin`

2. After login, right-click on **Servers** → `Create` → `Server`.

3. In the **General** tab:
    - Name: `Chat Storage DB`

4. In the **Connection** tab:
    - Host name/address: `db`  
      *(this is the Docker service name, not `localhost`)*
    - Port: `5432`
    - Username: `postgres`
    - Password: `postgres`

5. Save and connect ✅

You should now see the `chat_storage_dev` database and the schema `chat_storage_schema` (if created via Liquibase).

---

## 🛠️ Troubleshooting

- **Port already in use (8080, 5432, or 9090):**  
  Stop any process using those ports or update `SERVER_PORT`, `DB_PORT`, or `PGADMIN_PORT` in your `.env` file.

- **Containers not starting:**  
  Run:
  ```bash
  docker-compose down -v
  docker-compose --env-file ./config/env/dev.env up --build
  ```

- **pgAdmin cannot connect to DB:**  
  Use `db` as the host (Docker service name), **not `localhost`**.

---

## 📖 Reference Documentation

- [Gradle Documentation](https://docs.gradle.org)
- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Liquibase Docs](https://www.liquibase.org/documentation/index.html)
- [Docker Compose](https://docs.docker.com/compose/)

---

## 📝 License
This project is licensed under the MIT License.  
Feel free to use and modify for your own purposes.
