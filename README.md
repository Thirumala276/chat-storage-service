# 💬 Chat Storage Service

A **Java Spring Boot microservice** for storing and managing **chat sessions and messages**.
Built with **Spring Boot, Gradle, PostgreSQL, Liquibase, and Docker Compose**, this service supports **environment-specific configurations** for **dev, uat, and prod**.

Designed for **RAG (Retrieval-Augmented Generation) chatbots**, it securely stores conversations along with any **retrieved context**.

For **LLM integration**, we use **Gemini Flash 2.0**, and for **knowledge retrieval**, we leverage **pgvector** in PostgreSQL.

---

## 🚀 Features

* RESTful API with **Spring Boot**
* **Spring Data JPA** for database persistence
* **Liquibase** for versioned DB migrations
* **PostgreSQL + pgvector** support for vector embeddings(cohere)
* **RAG-ready chat history storage**
* **Rate limiting** for API requests using **Bucket4j**
* **API key authentication** for secure access (**X-API-KEY mandatory**)
* **CORS configuration** to allow cross-origin requests
* **Docker Compose** for local multi-container deployment
* API documentation with **Swagger UI**
* Health monitoring via **Spring Actuator**
* Database management via **pgAdmin**
* **Unit and Integration Testing** using **JUnit** and **Mockito** for test coverage

---

## 🛠️ Technologies

* ☕ **Java 17+**
* 🌱 **Spring Boot**
* 🐘 **PostgreSQL + pgvector**
* 🔄 **Liquibase**
* 🐳 **Docker & Docker Compose**
* 🤖 **Gemini Flash 2.0** (LLM for RAG)
* ⛓ **Bucket4j** (Rate Limiting)
* 🧪 **JUnit & Mockito** (Testing & Coverage)

---

## 📂 Project Structure

```
chat-storage-service/
├── src/main/java/com/raga/chat/   # Source code
├── src/main/resources/
│   ├── application.yaml           # Default configuration
│   └── db/changelog/              # Liquibase changelogs
├── src/test/java/com/raga/chat/   # Unit & Integration tests
├── config/env/                    # Environment-specific .env files
├── docker-compose.yml             # Multi-container orchestration
├── Dockerfile                     # Container build instructions
└── build.gradle                   # Gradle build file
```

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

### 3️⃣ Run with Docker Compose (Environment-Specific)

Docker automatically picks the correct **application YAML** for the environment via Spring profiles:

* **Development**

```bash
docker-compose --env-file ./config/env/dev.env up --build
```

* **UAT**

```bash
docker-compose --env-file ./config/env/uat.env up --build
```

* **Production**

```bash
docker-compose --env-file ./config/env/prod.env up --build
```

✅ **Tip:** The `.env` file contains all environment-specific variables such as ports, database credentials, and Spring profiles. The corresponding `application-{profile}.yaml` is automatically loaded by Spring Boot.

---

## 🔒 Security & API Usage

* **X-API-KEY:** Mandatory for every API request.

    * The **value of X-API-KEY** is **environment-specific** and is defined in the corresponding `.env` file as `API_SECRET_KEY`.

        * Example:

          ```env
          # dev.env
          API_SECRET_KEY=dev-secret-key
    
          # uat.env
          API_SECRET_KEY=uat-secret-key
    
          # prod.env
          API_SECRET_KEY=prod-secret-key
          ```
    * Each request to the API must include this key in the header:

      ```
      X-API-KEY: <value from API_SECRET_KEY for the current environment>
      ```
* **Rate Limiting:** Requests are limited per API key using **Bucket4j** to prevent abuse.

    * Can be **customized per minute capacity** in the Spring Boot configuration.
    * If the rate limit is **exceeded within the configured time**, the API responds with **HTTP 429 Too Many Requests**.
* **CORS:** Configured to allow requests from trusted origins. Can be customized in the Spring Boot configuration.

---

## 🌐 URLs

| Service         | URL                                                                                                  | Credentials                                     |
| --------------- | ---------------------------------------------------------------------------------------------------- | ----------------------------------------------- |
| Swagger UI      | [http://localhost:8080/chat/swagger-ui/index.html](http://localhost:8080/chat/swagger-ui/index.html) | N/A                                             |
| pgAdmin         | [http://localhost:9090/login](http://localhost:9090/login)                                           | Email: `admin@company.com`<br>Password: `admin` |
| Actuator Health | [http://localhost:8080/chat/actuator/health](http://localhost:8080/chat/actuator/health)             | N/A                                             |

---

## 🗄️ Connect pgAdmin to PostgreSQL

1. Open pgAdmin: [http://localhost:9090/login](http://localhost:9090/login)

    * Email: `admin@company.com`
    * Password: `admin`

2. Create a new server:

    * **Name:** `Chat Storage DB`
    * **Host:** `db` (Docker service name)
    * **Port:** `5432`
    * **Username:** `postgres`
    * **Password:** `postgres`

✅ You will now see the `chat_storage_dev` (or respective environment) database with schema `chat_storage_schema`.

---

## 🛠️ Troubleshooting

* **Port already in use (8080, 5432, 9090):**

```bash
lsof -i :8080
kill -9 <PID>
```

Or update ports in the respective `.env` file.

* **Containers not starting:**

```bash
docker-compose down -v
docker-compose --env-file ./config/env/dev.env up --build
```

* **pgAdmin cannot connect to DB:** Use `db` as host (not `localhost`).

---

## 📖 References

* [Gradle Docs](https://docs.gradle.org)
* [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/)
* [Liquibase Docs](https://www.liquibase.org/documentation/index.html)
* [pgvector Extension](https://github.com/pgvector/pgvector)
* [Gemini Flash 2.0](https://ai.google.dev/gemini-api)
* [Docker Compose](https://docs.docker.com/compose/)
* [Bucket4j](https://bucket4j.com/)
* [JUnit & Mockito](https://junit.org/junit5/docs/current/user-guide/) (Unit and Integration Test Coverage)

---

