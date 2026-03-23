# Testing Patterns

**Analysis Date:** 2025-05-22

## Test Framework

**Runner:**
- JUnit 4
- `androidx.test.runner.AndroidJUnitRunner` for instrumented tests.

**Assertion Library:**
- `org.junit.Assert`

**Run Commands:**
```bash
./gradlew test         # Run local unit tests
./gradlew connectedAndroidTest # Run instrumented tests on device
```

## Test File Organization

**Location:**
- Local unit tests: `app/src/test/java/hr/fipu/organizationtool/`
- Instrumented tests: `app/src/androidTest/java/hr/fipu/organizationtool/`

**Naming:**
- `*Test.kt` (e.g., `ExampleUnitTest.kt`, `ExampleInstrumentedTest.kt`).

**Structure:**
```
app/src/test/java/hr/fipu/organizationtool/
└── ExampleUnitTest.kt
app/src/androidTest/java/hr/fipu/organizationtool/
└── ExampleInstrumentedTest.kt
```

## Test Structure

**Suite Organization:**
```kotlin
// From ExampleUnitTest.kt
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}
```

**Patterns:**
- Standard JUnit 4 annotations (`@Test`, `@Before`, `@After`).
- `@RunWith(AndroidJUnit4::class)` for instrumented tests.

## Mocking

**Framework:**
- None detected (e.g., no Mockito or MockK in `libs.versions.toml` or `build.gradle`).

**Patterns:**
- No mocking patterns observed yet.

## Fixtures and Factories

**Test Data:**
- No test data factories or fixtures observed.

**Location:**
- Not established.

## Coverage

**Requirements:**
- None enforced in current configuration.

**View Coverage:**
- Use Android Studio's built-in coverage runner.

## Test Types

**Unit Tests:**
- Local JVM tests for business logic (e.g., `TaskViewModel`).

**Integration Tests:**
- Room database tests (using `androidTest` with in-memory database) are not yet implemented but are the expected pattern for this stack.

**E2E Tests:**
- Compose UI tests using `ComposeTestRule`.
- Configured via `androidx-compose-ui-test-junit4` dependency.

## Common Patterns

**Async Testing:**
- Coroutines testing would likely use `runTest` from `kotlinx-coroutines-test` (not yet in dependencies).

**Error Testing:**
- Standard `@Test(expected = Exception::class)` or `try-catch` with `fail()`.

---

*Testing analysis: 2025-05-22*
