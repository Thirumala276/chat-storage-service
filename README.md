# 💬 Chat Storage Service

A **Java Spring Boot microservice** for storing and managing **chat sessions and messages**.
Built with **Spring Boot, Gradle, PostgreSQL, Liquibase, and Docker Compose**, this service supports **environment-specific configurations** for **dev, uat, and prod**.

This microservice is designed for **RAG (Retrieval-Augmented Generation) chatbots**, storing conversations securely along with any **retrieved context**.

For **LLM integration**, we use **Gemini Flash 2.0**, and for **knowledge retrieval**, we leverage **pgvector** in PostgreSQL.

---

## 🚀 Features

* RESTful API with **Spring Boot**
* **Spring Data JPA** for database persistence
* **Liquibase** for versioned DB migrations
* **PostgreSQL + pgvector** support for vector embeddings
* **RAG-ready chat history storage**
* **Docker Compose** for local multi-container deployment
* API documentation with **Swagger UI**
* Health monitoring via **Spring Actuator**
* Database management via **pgAdmin**

---

## 🛠️ Technologies

* ☕ **Java 17+**
* 🌱 **Spring Boot**
* 🐘 **PostgreSQL + pgvector**
* 🔄 **Liquibase**
* 🐳 **Docker & Docker Compose**
* 🤖 **Gemini Flash 2.0** (LLM for RAG)

---

## 📂 Project Structure

```
chat-storage-service/
├── src/main/java/com/raga/chat/   # Source code
├── src/main/resources/
│   ├── application.yaml           # Default configuration
│   └── db/changelog/              # Liquibase changelogs
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

You can run the service for different environments by selecting the `.env` file:

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

✅ **Tip:** The `.env` file contains all environment-specific variables such as ports, database credentials, and Spring profiles.

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

* **pgAdmin cannot connect to DB:**
  Use `db` as host (not `localhost`).

---

## 📖 References

* [Gradle Docs](https://docs.gradle.org)
* [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/)
* [Liquibase Docs](https://www.liquibase.org/documentation/index.html)
* [pgvector Extension](https://github.com/pgvector/pgvector)
* [Gemini Flash 2.0](https://ai.google.dev/gemini-api)
* [Docker Compose](https://docs.docker.com/compose/)

---

## 📝 License

Licensed under the **MIT License**.
Feel free to use, modify, and distribute for your own purposes.
