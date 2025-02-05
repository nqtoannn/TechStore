#  Tech Store E-commerce 🍃☁️

An e-commerce application for selling electronic devices, including web and mobile platforms, with separate sections for administrators and customers. And integrating AI into customer review classification.

## Table of Contents
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [Dependencies](#dependencies)
- [Troubleshooting](#troubleshooting)

## Architecture

The architecture consists of the following components:

- **Java Spring Boot**: Backend server for TechStore, handling business logic and API services.
- **Flask**: AI-powered customer review classification service.
- **MySQL Database**: Relational database for data storage and management.
- **ReactJS**: Web application frontend for customer interaction.
- **Java** (Android): Mobile application for Android users.

## Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- Python 3.10
- Android Studio
- Postman (optional, for API testing)

## Getting Started

Clone the repository:

```bash
git clone [https://github.com/nqtoannn/TechStore](https://github.com/nqtoannn/TechStore)
```

## Running the Application
1. **Start MySQL Database**: 
  - Ensure MySQL Server is running.
  - Create a new database schema "Techstore".

2. **Configure Microservices with MySQL**:
   - Edit the `application.properties` file to configure MySQL connection details. Example configuration:
     ```yaml
      server.port=8080
      spring.mvc.pathmatch.matching-strategy=ant-path-matcher
      spring.datasource.url=jdbc:mysql://localhost:3306/techstore
      spring.datasource.username=root
      spring.datasource.password=1234
     ```

3.  **Start Server**:
  - cd techstore
  - mvn clean install
  - mvn spring-boot:run

4. **Setup AI Server Environment**:
  - cd vihsd
  - python app.py

5. **Start Webserver**:
  - cd web
  - npm i
  - npm run start

6. **Start Application**:
  Open app folder in Android Studio and start the app.

## Dependencies
The project uses the following dependencies:
### Backend (Java Spring Boot)
- Spring Boot  
- Spring Cloud  
- Spring Data JPA  
- Spring Security 
- MySQL Connector  
- Lombok  
- JWT  

### AI Service (Flask)
- Flask  
- TensorFlow/Keras 

### Frontend (ReactJS)
- React  
- React Router  
- Axios  
- TailwindCSS

### Mobile App (Android)
- Retrofit
- Volley
  
## Troubleshooting
If you encounter any issues, please check the following:
  - Ensure MySQL is running and the database schema is created.
  - Check the logs for any error messages.
