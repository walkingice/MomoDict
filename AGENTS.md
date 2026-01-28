# AGENTS.md

This file provides guidelines for agentic coding agents operating in this Android Kotlin repository.

## Build, Lint, and Test Commands

### Build Commands
```bash
# Build the project
./gradlew build

# Build debug APK
./gradlew assembleDebug
# Build release APK
./gradlew assembleRelease

# Run format checks
./gradlew ktlintCheck

# Apply ktlint formatting
./gradlew ktlintFormat

# Run lint checks
./gradlew lint

# Run checkstyle
./gradlew checkstyle
```

### Test Commands
```bash
# Run all unit tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest

# Run a specific test class
./gradlew test --tests "org.zeroxlab.momodict.reader.DictReaderTest"

# Run a specific test method
./gradlew test --tests "org.zeroxlab.momodict.reader.DictReaderTest.testParseAlphabet"

# Run a specific package's tests
./gradlew test --tests "org.zeroxlab.momodict.reader"

# Run tests with code coverage
./gradlew test jacocoTestReport
```

## Code Style Guidelines

### Imports
- Sort imports alphabetically by package
- Group by category: java, javax, kotlin, android, androidx, org, com
- Use full package names; avoid wildcard imports
- Remove unused imports
- Example order: `java.*`, `javax.*`, `kotlin.*`, `android.*`, androidx.*, org.*, com.*

### Formatting
- Use 4-space indentation
- Line length limit: 100 characters
- No trailing whitespace
- One statement per line
- Blank lines between logical sections/classes
- Brace style: opening brace on same line as declaration

### Naming Conventions
- Class names: PascalCase (`MainActivity`, `DictReaderTest`)
- Variable names: camelCase (`context`, `controller`, `debounceJob`)
- Constants: UPPER_SNAKE_CASE (`INPUT_DELAY`, `MAX_RETRY_COUNT`)
- Method names: camelCase (`initView`, `debounceQuery`, `onOptionsItemSelected`)
- Package names: lowercase (`org.zeroxlab.momodict.db.room`)

### Types and Kotlin Features
- Prefer `val` over `var` when possible
- Use `lateinit` for non-null properties that need initialization after constructor
- Use `private const val` for constant declarations
- Use type inference where clear: `val binding = ActivityWithOneViewPagerBinding.inflate(layoutInflater)`
- Use Kotlin's null safety: `text?.let { ... }`, `input?.trim() ?: ""`
- Prefer `data class` for simple data objects: `class IdxEntry(val wordStr: String, ...)`
- Use `sealed class` for related types when appropriate

### Error Handling and Logging
- Use Kotlin's nullable types and safe calls (`?.`) for potentially null values
- Use `lateinit` with non-null assertion (`!`) when property is guaranteed to be initialized
- Log errors with meaningful messages using `android.util.Log`:
  ```kotlin
  Log.e(TAG, "Failed to load dictionary", e)
  ```
- Use `runCatching` for safe execution with default values
- Handle `IOException` and other expected exceptions appropriately
- Use `when` expressions for exception handling

### Coroutines and Lifecycle
- Use `viewModelScope` for ViewModel coroutine scopes
- Use `lifecycle.coroutineScope` for Activity/Fragment coroutine scopes
- Properly cancel coroutines on cleanup using `Job?.cancel()`
- Keep coroutines scoped to their lifecycle to prevent memory leaks
- Example:
  ```kotlin
  context.lifecycle.coroutineScope.launch {
      val items = controller.getData()
      view.updateItems(items)
  }
  ```

### UI Patterns
- Use ViewBinding instead of findViewById
- Follow MVVM pattern with Presenter/ViewModel
- Use Fragments for tab content (`InputFragment`, `HistoryFragment`, etc.)
- Implement interfaces for Presenter communication (`InputContract.View`)

### Testing
- Write unit tests using Robolectric and JUnit
- Mock dependencies with Mockito when appropriate
- Use `@Before` for setup and `@After` for teardown
- Test both positive and negative cases
- Test edge cases and error conditions
- Use `@Config(constants = BuildConfig::class)` for unit tests

```kotlin
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class DictReaderTest {
    @Before
    fun setUp() { /* initialization */ }

    @After
    fun tearDown() { /* cleanup */ }

    @Test
    fun testParseAlphabet() { /* test logic */ }
}
```

## Cursor/Copilot Rules

No specific Cursor rules found in this repository.
No Copilot instructions in `.github/copilot-instructions.md`.

## Additional Notes

- This is a Kotlin-first Android project with Room database
- Uses ViewBinding for view access
- Follows MVVM architecture pattern
- Implements dictionary file parsing for .ifo/.dict/.idx formats
- Project uses Android SDK 21+ with compile SDK 35
- Dictionary formats: Babylon, StarDict, iFonoDict
- Test resources: `app/src/test/resources/`

Follow existing code structure, patterns, and conventions throughout the codebase.