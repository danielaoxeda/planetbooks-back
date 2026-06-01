# 🌍📖 Planet Books — Backend

> Backend API service for managing the virtual library platform with Java & MySQL.

![Java](https://img.shields.io/badge/Java-17-007396?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql)
![Docploy](https://img.shields.io/badge/Orchestrator-Docploy-FF6F00)
![Lombok](https://img.shields.io/badge/Lombok-Enabled-BC4E9C)
![JPA](https://img.shields.io/badge/JPA-Hibernate-59666C)
![Spring Devtools](https://img.shields.io/badge/Spring%20Devtools-HotReload-FF69B4)


## 📌 About the Project

**Planet Books Backend** is the core service of the virtual library system.
It provides RESTful APIs for user authentication, book catalog management, and administrative operations.

This repository contains the backend logic, database models, and deployment configuration for running the service in production.

## ✨ Features

### 👤 User Endpoints
- 🔐 **Authentication** — login, register, JWT-based sessions
- 📚 **Books** — browse and query catalog
- 👥 **Profiles** — manage user accounts

### 🛠️ Admin Endpoints
- 📖 Book Management (CRUD) — create, update, delete, list
- 👥 User Management — roles and permissions
- ⚙️ System Configuration — environment and deployment settings

## 🛠️ Tech Stack

| Technology | Purpose |
| --- | --- |
| Java 17 | Main programming language |
| Spring Boot | Framework for REST APIs |
| JPA / Hibernate | Persistence and ORM |
| Lombok | Reduce boilerplate code |
| Devtools | Hot reload during development |
| MySQL | Relational database |
| Docploy | Orchestration and deployment on VPS |
| JWT | Authentication and authorization |

---

## 🚀 Getting Started

### 📋 Prerequisites

- Java 17
- Maven
- MySQL >= 8
- VPS with Docploy installed

### 📦 Installation

```bash
# Clone the repository
git clone https://github.com/danielaoxeda/planetbooks-back.git

# Enter the project folder
cd planetbooks-back

# Build and run
./mvnw spring-boot:run

```

### 🗄️ Database Setup
Configure database connection in application.properties:
```bash
spring.datasource.url=jdbc:mysql://localhost:3306/planetbooks
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

```

### 🌐 Deployment with Docploy
1. Configure your VPS with Docploy.
2. Create a docploy.yml file:
```bash
services:
  planetbooks-back:
    image: openjdk:17
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/planetbooks
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=your_password

```
3. Deploy with:
```bash
docploy up -d

```

## 👥 Team & Collaboration
This project is developed as part of an academic initiative.
Collaboration practices include:
- Feature branches
- Pull Requests
- Code reviews
- Reparto de responsabilidades: ver `REPARTO_EQUIPO.md`


## 🔗 Related Repository
🔙 Backend: https://github.com/danielaoxeda/planetbooks-back


## 📌 Project Status
🚧 In active development
Core endpoints and database configuration are already implemented.

## 📄 License

This project is for academic purposes.
