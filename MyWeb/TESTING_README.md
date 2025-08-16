# Testing Guide - MyWeb Spring Boot Application

## Overview

This document provides comprehensive guidance for running and understanding the integration tests for the MyWeb Spring Boot application. The testing suite covers all layers of the application stack and ensures proper integration between components.

## Test Architecture

### Test Structure
```
src/test/java/com/shivam/MyWeb/
├── Service/
│   └── UserServiceIntegrationTest.java      # Service layer tests
├── Controller/
│   └── UserControllerIntegrationTest.java   # Controller layer tests
├── EndToEndIntegrationTest.java             # End-to-end tests
├── MyWebApplicationTests.java               # Basic application tests
└── TestUtils.java                           # Test utilities
```

### Test Configuration
```
src/test/resources/
└── application-test.properties              # Test-specific configuration
```

## Running Tests

### Option 1: Using Maven Commands

#### Run All Tests
```bash
mvn test
```

#### Run Specific Test Classes
```bash
# Service layer tests only
mvn test -Dtest=UserServiceIntegrationTest

# Controller layer tests only
mvn test -Dtest=UserControllerIntegrationTest

# End-to-end tests only
mvn test -Dtest=EndToEndIntegrationTest
```

#### Clean Build and Test
```bash
mvn clean test
```

#### Run Tests with Debug Output
```bash
mvn test -X
```

### Option 2: Using the Test Runner Script (Windows)

#### Run All Tests
```bash
run-tests.bat
```

#### Run Specific Test Types
```bash
run-tests.bat service      # Service layer tests
run-tests.bat controller   # Controller layer tests
run-tests.bat e2e         # End-to-end tests
run-tests.bat clean       # Clean build and test
run-tests.bat debug       # Debug output
```

### Option 3: Using IDE

1. **IntelliJ IDEA**: Right-click on test files or test methods and select "Run"
2. **Eclipse**: Right-click on test files and select "Run As" → "JUnit Test"
3. **VS Code**: Use the Testing extension to run individual tests

## Test Categories

### 1. Service Layer Tests (`UserServiceIntegrationTest`)
**Purpose**: Test business logic and data operations
**Coverage**: CRUD operations, data consistency, edge cases
**Test Count**: 7 tests

**Key Tests**:
- `testGetAllUsers()` - Verify user retrieval
- `testGetUserById()` - Test user lookup by ID
- `testAddUser()` - Test user creation
- `testUpdateUser()` - Test user updates
- `testDeleteUser()` - Test user deletion
- `testGetIndex()` - Test index functionality
- `testMultipleOperations()` - Test complex workflows

### 2. Controller Layer Tests (`UserControllerIntegrationTest`)
**Purpose**: Test REST API endpoints and HTTP handling
**Coverage**: All HTTP methods, request/response validation
**Test Count**: 8 tests

**Key Tests**:
- `testGetAllUsersEndpoint()` - GET /users
- `testGetUserByIdEndpoint()` - GET /users/{id}
- `testAddUserEndpoint()` - POST /users
- `testUpdateUserEndpoint()` - PUT /users
- `testDeleteUserEndpoint()` - DELETE /users/{id}
- `testCompleteCrudWorkflow()` - Full CRUD workflow
- `testGetNonExistentUser()` - Error handling
- `testMalformedJsonHandling()` - Input validation

### 3. End-to-End Tests (`EndToEndIntegrationTest`)
**Purpose**: Test complete application workflows
**Coverage**: Full application lifecycle, data consistency
**Test Count**: 8 tests

**Key Tests**:
- `testContextLoads()` - Spring context validation
- `testInitialDataExists()` - Data initialization
- `testServiceLayerCrudOperations()` - Service layer workflows
- `testRestEndpointsCrudOperations()` - REST API workflows
- `testConcurrentOperations()` - Concurrent access testing
- `testDataConsistency()` - Cross-layer data validation
- `testEdgeCases()` - Boundary condition testing
- `testFullApplicationLifecycle()` - Complete session simulation

## Test Data Management

### Initial Data
The application starts with 3 default users:
- ID: 1, Name: "Shivam", Email: "shivam@gmail.com"
- ID: 2, Name: "Raj", Email: "raj@gmail.com"
- ID: 3, Name: "Rajesh", Email: "rajesh@gmail.com"

### Test Data Isolation
- Tests use unique IDs (100+) to avoid conflicts
- Data state is restored after tests that modify shared data
- Each test class runs in isolation

### Test Utilities
The `TestUtils` class provides:
- Test user generation
- HTTP entity creation
- Property validation helpers
- Random data generation

## Test Configuration

### `application-test.properties`
```properties
# Test-specific configuration
spring.main.allow-bean-definition-overriding=true
spring.main.allow-circular-references=true

# Logging for tests
logging.level.com.shivam.MyWeb=DEBUG
logging.level.org.springframework.web=DEBUG

# Test server configuration
server.port=0

# Disable security for testing
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```

### Test Annotations
- `@SpringBootTest` - Full application context
- `@TestPropertySource` - Test-specific properties
- `@DisplayName` - Descriptive test names
- `@Order` - Test execution order (for E2E tests)

## Troubleshooting

### Common Issues

#### 1. Port Already in Use
**Error**: `Port 8080 is already in use`
**Solution**: Tests use random ports (`server.port=0`), but ensure no other Spring Boot apps are running

#### 2. Test Failures Due to Data State
**Error**: Tests failing because of modified data
**Solution**: Tests now restore data state after modifications

#### 3. Context Loading Issues
**Error**: Spring context fails to load
**Solution**: Check that all required beans are properly configured

#### 4. Maven Dependency Issues
**Error**: Class not found or compilation errors
**Solution**: Run `mvn clean compile` to ensure proper compilation

### Debug Mode
Enable debug output to see detailed test execution:
```bash
mvn test -X
```

### Test Reports
Test results are available in:
- Console output during execution
- `target/surefire-reports/` directory
- IDE test runners

## Performance Considerations

### Test Execution Times
- **Service Tests**: ~5 seconds
- **Controller Tests**: ~15 seconds
- **E2E Tests**: ~0.4 seconds
- **Total Time**: ~34 seconds

### Optimization Tips
1. Run specific test categories during development
2. Use `@DirtiesContext` sparingly
3. Avoid unnecessary database operations in tests
4. Use test profiles for different environments

## Best Practices

### Writing Tests
1. **Arrange-Act-Assert**: Follow the AAA pattern
2. **Descriptive Names**: Use `@DisplayName` for clarity
3. **Isolation**: Ensure tests don't interfere with each other
4. **Data Cleanup**: Restore state after tests that modify data

### Test Organization
1. **Group Related Tests**: Use descriptive class names
2. **Consistent Naming**: Follow naming conventions
3. **Proper Annotations**: Use appropriate Spring Boot test annotations
4. **Error Handling**: Test both success and failure scenarios

## Continuous Integration

### Maven Integration
The tests are configured to run automatically with:
- `mvn compile` - Compilation
- `mvn test` - Test execution
- `mvn package` - Build and test
- `mvn verify` - Full verification

### CI/CD Pipeline
Tests should be integrated into your CI/CD pipeline:
1. **Build Stage**: Compile and run unit tests
2. **Test Stage**: Run integration tests
3. **Deploy Stage**: Deploy if all tests pass

## Monitoring and Reporting

### Test Metrics
- **Success Rate**: Currently 100% (24/24 tests)
- **Execution Time**: Total ~34 seconds
- **Coverage**: All application layers tested

### Test Reports
Generate detailed reports with:
```bash
mvn surefire-report:report
```

## Support and Maintenance

### Updating Tests
When modifying the application:
1. Update existing tests to match new functionality
2. Add new tests for new features
3. Ensure all tests pass before committing

### Test Maintenance
- Review test failures regularly
- Update tests when API changes
- Maintain test data consistency
- Monitor test execution times

---

**Last Updated**: August 16, 2025  
**Test Version**: 1.0  
**Spring Boot Version**: 3.4.8  
**Java Version**: 21
