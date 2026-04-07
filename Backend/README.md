# Spring Boot RESTful API Backend

Backend RESTful API cho ứng dụng quản lý việc làm, được xây dựng với Spring Boot 3.5.12.

## 🎯 Tính Năng

- ✅ RESTful API Endpoints
- 🔐 Spring Security + OAuth2 Authentication
- 🗄️ JPA/Hibernate ORM
- 🔍 Advanced Filtering with Spring Filter
- 📨 Email Integration
- 📚 API Documentation (Swagger/OpenAPI)
- 🧪 Unit Testing with JUnit 5
- 🛠️ Lombok for code generation
- 📊 Actuator for monitoring

## 🛠️ Technology Stack

| Technology        | Version | Purpose                        |
| ----------------- | ------- | ------------------------------ |
| Spring Boot       | 3.5.12  | Framework                      |
| Spring Security   | Latest  | Authentication & Authorization |
| Spring Data JPA   | Latest  | ORM & Database                 |
| Spring Mail       | Latest  | Email sending                  |
| MySQL             | 8.0+    | Database                       |
| Lombok            | 9.2.0   | Code generation                |
| SpringDoc OpenAPI | 2.5.0   | API Documentation              |
| JUnit 5           | Latest  | Testing                        |
| Gradle            | Latest  | Build tool                     |

## 📋 Requirements

- Java 17 or higher
- MySQL 8.0 or higher
- Gradle 7.0 or higher
- Git

## 🚀 Quick Start

### 1. Clone Repository

```bash
git clone <repository-url>
cd Backend
```

### 2. Configure Database

Create MySQL database:

```sql
CREATE DATABASE job_database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/job_database
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Security
spring.security.oauth2.resourceserver.jwt.issuer-uri=your_issuer_uri
```

### 3. Build Project

```bash
# Clean and build
./gradlew clean build

# Build without running tests
./gradlew build -x test
```

### 4. Run Application

```bash
# Using Gradle
./gradlew bootRun

# Or using JAR file
java -jar build/libs/job-0.0.1-SNAPSHOT.jar
```

Application will start at: `http://localhost:8080`

## 📚 API Documentation

### Swagger UI

Access interactive API documentation at:

```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON

```
http://localhost:8080/v3/api-docs
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/vn/hunter/job/
│   │   ├── config/              # Spring configurations
│   │   ├── controller/          # REST endpoints
│   │   ├── service/             # Business logic
│   │   ├── repository/          # Data access
│   │   ├── entity/              # JPA entities
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── security/            # Security configurations
│   │   ├── exception/           # Custom exceptions
│   │   ├── util/                # Utility classes
│   │   └── Application.java     # Main class
│   └── resources/
│       ├── application.properties
│       └── templates/
└── test/
    └── java/vn/hunter/job/     # Unit tests
```

## 🔑 Key Endpoints

### Authentication

- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh token

### Admin Resources

- `GET /api/admin/users` - List users
- `POST /api/admin/users` - Create user
- `PUT /api/admin/users/{id}` - Update user
- `DELETE /api/admin/users/{id}` - Delete user

- `GET /api/admin/companies` - List companies
- `POST /api/admin/companies` - Create company
- `PUT /api/admin/companies/{id}` - Update company
- `DELETE /api/admin/companies/{id}` - Delete company

- `GET /api/admin/jobs` - List jobs
- `POST /api/admin/jobs` - Create job
- `PUT /api/admin/jobs/{id}` - Update job
- `DELETE /api/admin/jobs/{id}` - Delete job

### Public Resources

- `GET /api/jobs` - List all jobs
- `GET /api/jobs/{id}` - Get job details
- `GET /api/companies` - List all companies
- `GET /api/companies/{id}` - Get company details

## 🔐 Security Configuration

### Authentication & Authorization

- OAuth2 Resource Server configuration
- JWT Token validation
- Role-Based Access Control (RBAC)
- CORS configuration for frontend integration

### Password Security

- BCrypt password encoding
- Password validation rules

## 📧 Email Configuration

Configure email in `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## 🧪 Testing

### Run All Tests

```bash
./gradlew test
```

### Run Specific Test

```bash
./gradlew test --tests ClassName
```

### Code Coverage

```bash
./gradlew test jacocoTestReport
```

## 🔧 Gradle Commands

```bash
# Build
./gradlew build

# Run
./gradlew bootRun

# Test
./gradlew test

# Clean
./gradlew clean

# Dependencies
./gradlew dependencies

# Gradle wrapper update
./gradlew wrapper
```

## 📊 Actuator Endpoints

Access monitoring endpoints (if enabled):

```
http://localhost:8080/actuator
http://localhost:8080/actuator/health
http://localhost:8080/actuator/metrics
http://localhost:8080/actuator/env
```

## 🐛 Troubleshooting

### Issue: Port 8080 already in use

**Solution**: Change port in `application.properties`

```properties
server.port=8081
```

### Issue: MySQL connection failed

**Solution**:

- Verify MySQL is running
- Check database name, username, and password
- Ensure MySQL driver is in classpath

### Issue: Build fails with dependency errors

**Solution**:

```bash
./gradlew clean build --refresh-dependencies
```

## 📝 Environment Variables

Create `.env` or use system environment variables:

```
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/job_database
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password
SPRING_PROFILES_ACTIVE=dev
```

## 🚀 Deployment

### Build JAR

```bash
./gradlew build -x test
```

### Run JAR

```bash
java -jar build/libs/job-0.0.1-SNAPSHOT.jar
```

### Docker Deployment

```dockerfile
FROM openjdk:17-slim
COPY build/libs/job-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

```bash
docker build -t job-api:latest .
docker run -p 8080:8080 job-api:latest
```

## 🤝 Contributing

1. Create a feature branch
2. Make your changes
3. Write/update tests
4. Submit a pull request

## 📄 License

MIT License

## 📞 Support

For issues and questions, please open an issue on the repository.

---

**Created**: April 7, 2026  
**Java Version**: 17  
**Spring Boot Version**: 3.5.12
