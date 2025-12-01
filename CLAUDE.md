# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a personal practice project for learning Spring Boot and Java 21 features. The project uses a structured branching strategy and serves as a hands-on learning environment where different technologies and patterns are experimented with in isolated feature branches.

**Key Details:**
- **Framework**: Spring Boot 3.5.8
- **Java Version**: 21
- **Build Tool**: Maven
- **Server Port**: 9590
- **Package Structure**: `dev.sirbuni.practice.spring`

## Build and Run Commands

### Building
```bash
# Clean and compile
mvn clean compile

# Package application
mvn clean package

# Skip tests during build
mvn clean install -DskipTests
```

### Running
```bash
# Run application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Testing
```bash
# Run all tests
mvn test

# Run tests and integration tests
mvn verify

# Run a specific test class
mvn test -Dtest=ClassName

# Run a specific test method
mvn test -Dtest=ClassName#methodName
```

### Verification
```bash
# Check application health
curl http://localhost:9590/actuator/health

# View all actuator endpoints
curl http://localhost:9590/actuator

# View application metrics
curl http://localhost:9590/actuator/metrics
```

## Architecture and Code Organization

### Branch Strategy (CRITICAL)

This project uses a strict three-tier branching model:

1. **`main`** - Production-ready code only
   - Never commit directly to main
   - Only accepts merges from `staging`
   - Always deployable and stable

2. **`staging`** - Pre-production testing
   - Accepts merges from `dev`
   - Comprehensive testing required before merging to main
   - Medium level of protection

3. **`dev`** - Active development integration
   - Accepts merges from feature branches
   - Integration testing environment
   - May have known issues

4. **Feature branches** - Isolated development
   - Naming: `feature/<topic-name>` (e.g., `feature/regex-patterns`)
   - Created from and merged back to `dev`
   - Examples: `feature/database-practice`, `feature/security-practice`, `feature/regex-patterns`

**Current Branch**: `feature/regex-patterns` - Learning Java regex patterns with M-Koba SACCOS message parsing

### Application Structure

**Entry Point:**
- `SpringPracticeApplication.java` - Standard Spring Boot main class
- `Initializer.java` - ApplicationRunner that logs startup timing (2-second delay for demo purposes)

**Configuration:**
- Custom server port: `9590` (not default 8080)
- Actuator fully exposed with detailed health checks
- DevTools enabled for hot reload during development

**Package Convention:**
- Base package: `dev.sirbuni.practice.spring`
- Utilities: `dev.sirbuni.practice.spring.utils`
- Feature-specific code should follow: `dev.sirbuni.practice.spring.<feature>`

### Sample Data for Practice

Located in `src/main/resources/sample-data/`:

1. **M-Koba Messages** (SACCOS transaction notifications):
   - `m-koba-sample-texts.txt` - Plain text messages
   - `sms-20251128101700.xml` - SMS Backup & Restore XML format

2. **M-Pesa Messages** (Mobile money transactions):
   - `sms-m-pesa-20251128102028.xml` - SMS Backup & Restore XML format

These are real-world SMS messages used for regex pattern learning and text parsing practice.

### Documentation Structure

Comprehensive learning documentation in `.claude/docs/java/`:

**Regex Patterns Guide** (`.claude/docs/java/regex-patterns/`):
- 9 sequential guides covering regex from fundamentals to advanced techniques
- Includes XML file parsing guide (guide 09)
- All examples are complete and runnable
- Focused on M-Koba message parsing as practical use case
- See `README.md` in that directory for the complete learning path

**Key Documentation Files:**
- Each guide builds on previous concepts (01-09 sequence)
- Guide 06 focuses specifically on M-Koba message parsing patterns
- Guide 09 covers XML reading with Java DOM, SAX, StAX, JAXB, and Spring Boot

### Project Goals and Learning Areas

This project is specifically designed for practicing:

1. **Spring Boot Features**: Security, Data Access, Microservices, Actuator
2. **Java 21 Skills**: File I/O, regex, streams, collections
3. **Text Processing**: Parsing SMS messages, extracting transaction data, regex patterns
4. **XML Processing**: Multiple parsing approaches (DOM, SAX, StAX, JAXB)
5. **Best Practices**: Code quality, testing, documentation

### Important Conventions

**Commit Messages:**
Follow conventional commit format:
```
<type>(<scope>): <subject>

<body>

<footer>
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

Examples:
- `feat(regex): add M-Koba transaction parser`
- `docs(readme): update branch strategy documentation`
- `test(parser): add unit tests for date extraction`

**Development Workflow:**
1. Always start from `dev` branch when creating new features
2. Create feature branch with descriptive name
3. Develop with frequent commits
4. Merge to `dev` after local testing passes
5. Never push directly to `main` or `staging`

**Testing Requirements:**
- Run `mvn test` before merging to `dev`
- Run `mvn verify` before merging to `staging`
- Ensure actuator health check passes before considering code stable

## Feature-Specific Notes

### Current Feature: Regex Patterns (`feature/regex-patterns`)

**Purpose**: Learn Java regex patterns by creating text parsing utilities for M-Koba SACCOS messages

**Scope**: Documentation-only (no implementation code)
- Comprehensive guides in `.claude/docs/java/regex-patterns/`
- Sample data for practice in `src/main/resources/sample-data/`
- Learning resource for future implementation work

**Message Formats to Parse:**
1. Member transactions: `{phone}({name}) has {action} TZS.{amount}...`
2. Loan disbursements: `{ref} Confirmed.{loan_type} loan of TZS.{amount}...`
3. Personal confirmations: `{ref} Confirmed.You successfully {action}...`

## Claude Code Workflow Notes

When working in this repository:

1. **Check current branch first** - Never work directly on `main` or `staging`
2. **Read README.md** for complete project context and goals
3. **Feature branches are isolated** - Each branch practices different concepts
4. **Documentation over implementation** - Current branch focuses on learning guides
5. **Sample data is real** - M-Koba and M-Pesa messages are actual SMS data
6. **Port 9590 is standard** - Don't change without good reason
7. **Initializer adds 2-second delay** - This is intentional for logging demonstration

## References

- **README.md**: Complete project overview, goals, and workflows
- **pom.xml**: Maven dependencies and build configuration
- **application.properties**: Server and actuator configuration
- **.claude/docs/java/regex-patterns/README.md**: Complete regex learning path
- **.claude/prompts/**: Task-specific prompts for guided development
