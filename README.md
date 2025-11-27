# Spring Boot Practice Project

## 🎯 Project Overview

This is a personal practice project designed to explore and master various Spring Boot features, Java capabilities, and software development best practices. The project serves as a hands-on learning environment where different technologies and patterns can be experimented with in isolated branches.

## 📋 Project Goals

### Primary Objectives
1. **Master Spring Boot Features** - Deep dive into Spring Boot ecosystem including Security, Data Access, Microservices, and more
2. **Enhance Java Skills** - Practice Java 21 features, file manipulation, regex, streams, and other core Java capabilities
3. **Learn by Doing** - Implement real-world scenarios and solve practical problems
4. **Build a Reference Library** - Create reusable code snippets and patterns for future projects
5. **Adopt Best Practices** - Follow industry-standard conventions for code quality, testing, and documentation

### Specific Learning Areas
- **Database Integration** - JPA, Hibernate, JDBC, PostgreSQL, transactions
- **Security** - Spring Security, authentication, authorization, JWT, OAuth2
- **REST APIs** - RESTful design, validation, error handling, API documentation
- **Microservices** - Service discovery, API gateway, distributed systems
- **Testing** - Unit tests, integration tests, test-driven development
- **Monitoring** - Actuator, metrics, health checks, application monitoring
- **Java Core** - File I/O, regex patterns, collections, streams, concurrency

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 3.5.8 |
| Language | Java | 21 |
| Build Tool | Maven | Latest |
| Server Port | Custom | 9590 |
| Configuration | Properties | application.properties |

### Base Dependencies
- **Spring Web** - HTTP endpoints and embedded Tomcat server
- **Spring Boot DevTools** - Hot reload for rapid development
- **Spring Boot Actuator** - Production-ready monitoring features

## 🌳 Branch Strategy

This project uses a structured branching strategy to maintain code quality and stability across different environments.

### Permanent Branches

#### 1. `main` Branch
- **Purpose:** Production-ready, stable code only
- **Protection:** Highest level of protection
- **Merges From:** `staging` branch only (after thorough testing)
- **Direct Commits:** ❌ Never commit directly to main
- **Status:** Always deployable and stable

#### 2. `staging` Branch
- **Purpose:** Pre-production testing and integration
- **Protection:** Medium level of protection
- **Merges From:** `dev` branch (after feature integration)
- **Testing:** Comprehensive testing before merging to main
- **Status:** Should be stable, minor issues acceptable

#### 3. `dev` Branch
- **Purpose:** Active development and feature integration
- **Protection:** Basic protection
- **Merges From:** Feature branches (after local testing)
- **Testing:** Integration testing, may have known issues
- **Status:** Active development, expect changes

### Feature Branches

Feature branches are created from `dev` and merged back to `dev` after completion.

#### Naming Convention
```
feature/<topic-name>
```

#### Examples

**Database Practice**
```bash
feature/database-practice
feature/jpa-hibernate
feature/postgresql-integration
```

**Security Practice**
```bash
feature/security-practice
feature/spring-security-jwt
feature/oauth2-implementation
```

**REST API Practice**
```bash
feature/rest-api-practice
feature/api-validation
feature/error-handling
```

**Microservices Practice**
```bash
feature/microservices-practice
feature/service-discovery
feature/api-gateway
```

**Java Core Practice**
```bash
feature/java-core-practice
feature/file-manipulation
feature/regex-patterns
feature/streams-api
```

**Testing Practice**
```bash
feature/testing-practice
feature/unit-testing
feature/integration-testing
```

## 🔄 Development Workflow

### Creating a New Feature

1. **Start from dev branch**
   ```bash
   git checkout dev
   git pull origin dev
   ```

2. **Create feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Develop and commit frequently**
   ```bash
   git add .
   git commit -m "Add specific feature implementation"
   ```

4. **Keep branch updated with dev**
   ```bash
   git checkout dev
   git pull origin dev
   git checkout feature/your-feature-name
   git merge dev
   ```

### Merging Feature to Dev

1. **Ensure all tests pass**
   ```bash
   mvn clean test
   ```

2. **Switch to dev and merge**
   ```bash
   git checkout dev
   git merge feature/your-feature-name
   ```

3. **Run integration tests**
   ```bash
   mvn clean verify
   ```

4. **Push to remote**
   ```bash
   git push origin dev
   ```

### Promoting from Dev to Staging

1. **Verify dev is stable**
   ```bash
   git checkout dev
   mvn clean verify
   ```

2. **Merge to staging**
   ```bash
   git checkout staging
   git merge dev
   ```

3. **Perform thorough testing on staging**
    - Manual testing
    - Integration testing
    - Performance testing

4. **Push to remote**
   ```bash
   git push origin staging
   ```

### Promoting from Staging to Main

1. **Verify staging is production-ready**
   ```bash
   git checkout staging
   mvn clean package
   # Run comprehensive tests
   ```

2. **Merge to main**
   ```bash
   git checkout main
   git merge staging
   ```

3. **Tag the release (optional)**
   ```bash
   git tag -a v1.0.0 -m "Release version 1.0.0"
   git push origin v1.0.0
   ```

4. **Push to remote**
   ```bash
   git push origin main
   ```

## 📝 Best Practices

### Code Quality

1. **Follow Java Naming Conventions**
    - Classes: PascalCase (e.g., `UserService`)
    - Methods: camelCase (e.g., `getUserById`)
    - Constants: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_ATTEMPTS`)
    - Packages: lowercase (e.g., `com.sirbuni.practice`)

2. **Write Clean, Readable Code**
    - Keep methods small and focused (single responsibility)
    - Use meaningful variable and method names
    - Add comments only when necessary to explain "why", not "what"
    - Avoid deep nesting (max 3 levels recommended)

3. **Handle Exceptions Properly**
    - Never swallow exceptions silently
    - Use specific exception types
    - Provide meaningful error messages
    - Log exceptions with appropriate context

4. **Use Spring Boot Best Practices**
    - Leverage dependency injection
    - Use constructor injection over field injection
    - Keep controllers thin (business logic in services)
    - Use DTOs for API requests/responses
    - Implement proper validation

### Testing

1. **Write Tests First (TDD)**
    - Write failing test
    - Implement feature
    - Refactor if needed

2. **Test Coverage**
    - Aim for meaningful test coverage (quality over quantity)
    - Test business logic thoroughly
    - Test edge cases and error scenarios

3. **Test Naming**
    - Use descriptive test method names
    - Format: `methodName_condition_expectedResult`
    - Example: `getUserById_whenUserExists_returnsUser`

### Git Commit Messages

Follow conventional commit format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, no logic change)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Examples:**
```bash
feat(auth): implement JWT authentication

Add JWT token generation and validation for user authentication.
Includes token refresh mechanism and expiration handling.

Closes #123
```

```bash
fix(database): resolve connection pool exhaustion

Increase connection pool size and add proper connection
timeout configuration to prevent pool exhaustion under load.
```

```bash
test(user-service): add unit tests for user creation

Add comprehensive unit tests covering success cases,
validation errors, and duplicate user scenarios.
```

### Configuration Management

1. **Use Profiles**
    - `application.properties` - Base configuration
    - `application-dev.properties` - Development settings
    - `application-staging.properties` - Staging settings
    - `application-prod.properties` - Production settings

2. **Never Commit Secrets**
    - Use environment variables for sensitive data
    - Add sensitive files to `.gitignore`
    - Use Spring Boot's encrypted properties if needed

3. **Document Configuration**
    - Add comments to explain non-obvious properties
    - Document required environment variables
    - Maintain configuration change log

### Code Review

1. **Self-Review First**
    - Review your own changes before requesting review
    - Ensure tests pass
    - Check for console errors or warnings

2. **Keep PRs Small**
    - Focus on single feature or fix
    - Easier to review and test
    - Faster feedback cycle

3. **Provide Context**
    - Explain what and why in PR description
    - Link related issues or documentation
    - Highlight areas needing special attention

## 🚀 Getting Started

### Prerequisites
- Java 21 installed
- Maven installed
- Git installed
- IDE (IntelliJ IDEA, Eclipse, or VS Code recommended)

### Initial Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd spring-practice
   ```

2. **Verify Java version**
   ```bash
   java -version
   # Should show Java 21
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Verify application is running**
    - Open browser: http://localhost:9590/actuator/health
    - Should return: `{"status":"UP"}`

### Creating Your First Feature Branch

1. **Ensure you're on dev branch**
   ```bash
   git checkout dev
   ```

2. **Create and switch to feature branch**
   ```bash
   git checkout -b feature/my-first-feature
   ```

3. **Make your changes and commit**
   ```bash
   git add .
   git commit -m "feat: implement my first feature"
   ```

4. **When ready, merge back to dev**
   ```bash
   git checkout dev
   git merge feature/my-first-feature
   ```

## 📚 Useful Commands

### Maven Commands
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Run tests and integration tests
mvn verify

# Package application
mvn package

# Run application
mvn spring-boot:run

# Skip tests during build
mvn clean install -DskipTests
```

### Git Commands
```bash
# View all branches
git branch -a

# View commit history
git log --oneline --graph --all

# View changes
git status
git diff

# Stash changes temporarily
git stash
git stash pop

# Update branch with latest changes
git pull origin <branch-name>

# Delete local branch
git branch -d feature/old-branch

# Delete remote branch
git push origin --delete feature/old-branch
```

### Spring Boot Actuator Endpoints
```bash
# Health check
curl http://localhost:9590/actuator/health

# Application info
curl http://localhost:9590/actuator/info

# All available endpoints
curl http://localhost:9590/actuator

# Metrics
curl http://localhost:9590/actuator/metrics

# Environment properties
curl http://localhost:9590/actuator/env

# Application beans
curl http://localhost:9590/actuator/beans
```

## 📖 Learning Resources

### Official Documentation
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Framework Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/)
- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)

### Recommended Reading
- Effective Java by Joshua Bloch
- Spring in Action by Craig Walls
- Clean Code by Robert C. Martin
- Test Driven Development by Kent Beck

## 🎓 Practice Topics Checklist

### Spring Boot Features
- [ ] REST API Development
- [ ] Spring Data JPA
- [ ] Spring Security
- [ ] Actuator and Monitoring
- [ ] Exception Handling
- [ ] Validation
- [ ] Caching
- [ ] Scheduling
- [ ] Async Processing
- [ ] Event Handling
- [ ] Profiles and Configuration
- [ ] Logging
- [ ] Microservices Architecture

### Java Skills
- [ ] Streams API
- [ ] Lambda Expressions
- [ ] File I/O Operations
- [ ] Regular Expressions
- [ ] Collections Framework
- [ ] Concurrency and Multithreading
- [ ] Generics
- [ ] Reflection
- [ ] Annotations
- [ ] Date and Time API

### Testing
- [ ] JUnit 5
- [ ] Mockito
- [ ] Integration Testing
- [ ] Test Containers
- [ ] REST API Testing
- [ ] Performance Testing

### DevOps
- [ ] Docker Containerization
- [ ] CI/CD Pipeline
- [ ] Database Migrations (Flyway/Liquibase)
- [ ] Application Monitoring
- [ ] Log Aggregation

## 📞 Contact & Contributions

This is a personal practice project, but suggestions and improvements are welcome!

---

**Remember:** The goal is learning and experimentation. Don't be afraid to make mistakes - they're the best teachers!

*Last Updated: November 2024*
