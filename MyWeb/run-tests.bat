@echo off
echo ========================================
echo MyWeb Integration Test Runner
echo ========================================
echo.

if "%1"=="service" (
    echo Running Service Layer Tests...
    mvn test -Dtest=UserServiceIntegrationTest
) else if "%1"=="controller" (
    echo Running Controller Layer Tests...
    mvn test -Dtest=UserControllerIntegrationTest
) else if "%1"=="e2e" (
    echo Running End-to-End Tests...
    mvn test -Dtest=EndToEndIntegrationTest
) else if "%1"=="all" (
    echo Running All Integration Tests...
    mvn test
) else if "%1"=="clean" (
    echo Running Clean Build and All Tests...
    mvn clean test
) else if "%1"=="debug" (
    echo Running Tests with Debug Output...
    mvn test -X
) else (
    echo Usage: run-tests.bat [option]
    echo.
    echo Options:
    echo   service   - Run service layer tests only
    echo   controller- Run controller layer tests only
    echo   e2e       - Run end-to-end tests only
    echo   all       - Run all tests (default)
    echo   clean     - Clean build and run all tests
    echo   debug     - Run tests with debug output
    echo.
    echo Examples:
    echo   run-tests.bat service
    echo   run-tests.bat controller
    echo   run-tests.bat e2e
    echo   run-tests.bat all
    echo   run-tests.bat clean
    echo   run-tests.bat debug
    echo.
    echo Running all tests by default...
    mvn test
)

echo.
echo ========================================
echo Test execution completed!
echo ========================================
pause
