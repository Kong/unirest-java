# Copilot Instructions for Unirest-Java

## Project Overview
Unirest is a simplified, lightweight HTTP client library for Java. This is a multi-module Maven project requiring Java 11+.

### Module Structure
- **unirest** (unirest-java-core): Core HTTP client library
- **unirest-modules-gson**: GSON JSON object mapper integration
- **unirest-modules-jackson**: Jackson 3.x JSON object mapper integration  
- **unirest-modules-jackson-legacy**: Jackson 2.x JSON object mapper integration
- **unirest-modules-mocks**: Mock client for testing Unirest-based code
- **unirest-bdd-tests**: Behavioral/integration tests
- **unirest-java-bom**: Bill of Materials for dependency management

## Code Style & Standards

### What Copilot Should NOT Comment On (Already Enforced by Checkstyle)
The following are automatically checked by checkstyle during the build. Do not flag these in PR reviews:
- **Naming conventions**: Constants, local variables, member names, method names, package names, parameters, static variables, and type names
- **Import issues**: Illegal imports (sun.* packages), redundant imports, unused imports
- **Line length**: Max 186 characters
- **File length**: Max 1100 lines
- **Method length**: Max 35 lines
- **Method count**: Max 50 public methods per class
- **Parameter count**: Max 5 parameters per method
- **Nested if depth**: Max 3 levels
- **Braces requirement**: All control structures require braces
- **Hidden fields**: Except in setters and constructor parameters
- **Long literals**: Must use uppercase 'L' suffix
- **Forbidden patterns**:
  - `Calendar.getInstance` (use Java 8+ DateTime API)
  - `System.out.println`
  - `.printStackTrace()`

### License Headers
All Java source files must include the MIT license header. This is enforced by maven-license-plugin.

### Coding Conventions
- Use Java 8+ DateTime API instead of `Calendar`
- Proper exception handling (no printStackTrace)
- No direct console output (System.out)
- Prefer fluent/builder patterns (see `Config`, `HttpRequest` classes)
- Use `Optional` for nullable return values
- Follow immutable patterns where practical

## Testing Requirements

### Test Structure
All contributions must include tests:

1. **Unit Tests** (`unirest/src/test/java/`)
   - Test individual components in isolation
   - Use JUnit 5 with Mockito for mocking
   - Use AssertJ for assertions

2. **Behavioral Tests** (`unirest-bdd-tests/src/test/java/BehaviorTests/`)
   - Integration tests against a mock server
   - Tests extend `BddTest` base class
   - Mock server auto-resets between tests
   - Test real HTTP scenarios end-to-end

### Testing Frameworks Used
- JUnit 5 (junit-jupiter)
- Mockito for mocking
- AssertJ for fluent assertions
- JSONAssert for JSON comparisons
- Custom `MockClient` from unirest-modules-mocks

### Example Test Pattern (Unit Test)
```java
@ExtendWith(MockitoExtension.class)
class MyFeatureTest {
    @InjectMocks
    private MyClass underTest;
    
    @Test
    void shouldDoSomething() {
        // Given / When / Then pattern
    }
}
```

### Example Test Pattern (Behavioral Test)
```java
class MyFeatureTest extends BddTest {
    @Test
    void canDoSomethingOverHttp() {
        Unirest.config().someConfiguration();
        
        var response = Unirest.get(MockServer.GET)
            .asObject(RequestCapture.class);
            
        assertEquals(200, response.getStatus());
    }
}
```

## Architecture Guidelines

### Key Classes
- `Unirest`: Static entry point with primary instance
- `UnirestInstance`: Configurable instance (supports multiple instances)
- `Config`: Configuration holder with fluent API
- `HttpRequest`/`BaseRequest`: Request builder interface and implementation
- `HttpResponse`: Response wrapper with various body types
- `Client`: HTTP client abstraction (JavaClient implementation)
- `ObjectMapper`: JSON serialization abstraction

### Adding New Features
1. Add configuration option to `Config` class if needed
2. Implement feature in appropriate module
3. Add unit tests in same module
4. Add behavioral tests in `unirest-bdd-tests`
5. Update documentation in `mkdocs/docs/` if user-facing

## Build & Verification

### Running Tests
```bash
mvn verify
```

### Build Commands
```bash
mvn clean install       # Full build with tests
mvn package -DskipTests # Build without tests
```

### Code Coverage
JaCoCo enforces minimum 60% code coverage on complexity.

## Pull Request Guidelines
- Maintain backwards compatibility - this is critical for the project
- All tests must pass (`mvn verify`)
- Checkstyle must pass (runs automatically during compile)
- Include both unit and behavioral tests for new features
- Update CHANGELOG.md for user-facing changes
- Update documentation in mkdocs/docs/ for new features

## Dependencies
- Dependencies are managed in the parent pom.xml
- Test dependencies are inherited: JUnit 5, Mockito, AssertJ, Guava (test scope)
- Prefer existing dependencies over adding new ones
- All production dependencies should be carefully considered for size impact

## Documentation
- API documentation via Javadoc
- User documentation in `mkdocs/docs/`
- Example code should be tested (reference behavioral tests)
