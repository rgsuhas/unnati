# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

**Project:** Mahila-Shakti Unnati — Micro-Finance Digital Ledger for Women's SHGs  
**Stack:** Kotlin · MVVM · Jetpack Compose · Room DB · Material Design 3  
**MindMatrix VTU Internship Program — Project #83**

---

## Project Map

```
PRD.md                  — Full product requirements (source of truth)
APP_FLOWS_AND_VIEWS.md  — All 16 screens with ASCII layouts + Kotlin dev prompts
GEMINI.md               — Gemini API integration context and prompts
CLAUDE.md               — This file
```

---

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run a single unit test class
./gradlew test --tests "com.example.unnati.ExampleUnitTest"

# Check for compilation errors without building APK
./gradlew compileDebugKotlin
```

---

## Architecture

### Pattern: MVVM with Jetpack Compose

The app uses **Jetpack Compose** (not XML layouts or Fragments). The layering is:

```
Composable Screen  →  ViewModel  →  UnnatiRepository  →  Room DAO
```

- Composables observe `StateFlow` via `collectAsState()` and call ViewModel functions. Zero business logic in composables.
- ViewModels hold ALL business logic: validation, calculations, state management.
- All DB access goes through `UnnatiRepository` — ViewModels never call DAOs directly.
- No `runBlocking` in UI layer. All DB calls via `viewModelScope.launch { }` on `Dispatchers.IO`.

### Navigation

Navigation Compose is used with a sealed `Screen` class in `ui/navigation/Screen.kt`. Routes are registered in `MainActivity.kt`'s `NavHost`. To add a new screen:

1. Add an `object` to `Screen` with a route string.
2. Add a `composable(Screen.Foo.route) { ... }` block in `MainActivity.kt`.
3. For screens with arguments (e.g., `memberId`), follow the `AddMember` pattern with `navArgument`.

### Repository

There is **one combined repository**: `UnnatiRepository` (not separate per-entity repositories as shown in PRD). It aggregates `MemberDao`, `SavingsDao`, `LoanDao`, and `RepaymentDao` and is the single point of DB access for all ViewModels.

### StateFlow Pattern

```kotlin
// GOOD — reactive, single source of truth
val groupCapital: StateFlow<Double> = repository.totalGroupCapital
    .map { it ?: 0.0 }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

// BAD — manual fetch, not reactive
fun loadCapital() { viewModelScope.launch { _capital.value = dao.getSum() } }
```

---

## Key Implementation Facts

- **Package name:** `com.example.unnati` (not `com.mahilashakti.unnati`)
- **Min SDK:** 26 (Android 8.0), not 23 as documented in PRD
- **Build tool for Room:** KSP (not KAPT) — `ksp(libs.androidx.room.compiler)`
- **Theme colors** are defined in `ui/theme/Color.kt`: `DeepViolet`, `DeepVioletDark`, `Saffron`, `SaffronLight`, `SuccessGreen`, `ErrorRed`, `WarningAmber`
- **Current state (as of init commit):** Splash screen is complete. PinLogin and Dashboard are placeholders in `MainActivity.kt`. All DAOs and `UnnatiRepository` are scaffolded.

---

## File Structure (actual)

```
app/src/main/java/com/example/unnati/
├── data/
│   ├── AppDatabase.kt              ← Room DB singleton, version 1, exportSchema=false
│   ├── dao/
│   │   ├── MemberDao.kt
│   │   ├── SavingsDao.kt
│   │   ├── LoanDao.kt
│   │   └── RepaymentDao.kt
│   ├── entity/
│   │   ├── Member.kt
│   │   ├── SavingsEntry.kt         ← UNIQUE index on (memberId, weekStartDate)
│   │   ├── Loan.kt
│   │   └── Repayment.kt
│   └── repository/
│       └── UnnatiRepository.kt     ← single repo, wraps all four DAOs
├── ui/
│   ├── navigation/Screen.kt        ← sealed class with Compose routes
│   ├── screens/
│   │   └── splash/SplashScreen.kt  ← complete, fade-in animation, 1.5s delay
│   └── theme/
│       ├── Color.kt
│       ├── Type.kt
│       └── Theme.kt
└── MainActivity.kt                  ← ComponentActivity, NavHost, edge-to-edge
```

---

## Business Rules — Enforce in ViewModel

| Rule | Where enforced |
|---|---|
| BR-01: Block new loan if member has ACTIVE loan | `NewLoanViewModel.isBlocked` StateFlow — disable button AND reject in `issueLoan()` |
| BR-02: Group capital recalculates < 500ms on new entry | Room reactive Flow via `SavingsDao.getTotalGroupCapital()` — no manual refresh |
| BR-03: Simple Interest = (P × R × T) / 100, T in months | `LoanDetailViewModel.accruedInterest` |
| BR-04: Loan eligibility = (memberSavings/groupTotal) × capital × multiplier | `NewLoanViewModel.eligibilityAmount` |
| BR-05: Deactivated members keep all history | `isActive = false` only — never DELETE Member rows |
| BR-06: Savings date cannot be future | Validate in savings ViewModel before insert |
| BR-07: Admin PIN required for all write ops | PIN session check — timeout 5 min |
| BR-08: Export is plain text only | Export ViewModel returns `String` — no HTML/Markdown |

---

## Room DB Rules

- Every insert/update touching multiple tables must use `@Transaction`.
- Always write a `@Migration` class when changing schema — **never** use `fallbackToDestructiveMigration()`.
- `SavingsEntry` already has a `UNIQUE` index on `(memberId, weekStartDate)` — enforced at DB level.
- Foreign keys are declared on `SavingsEntry`, `Loan`, and `Repayment` with `onDelete = CASCADE`.

---

## Kotlin Code Style

- `data class` for UI state per screen; `sealed class Result<T>` for async ops (`Loading`, `Success`, `Error`).
- Currency: `NumberFormat.getCurrencyInstance(Locale("en", "IN"))`
- Dates: `SimpleDateFormat("dd MMM yyyy", Locale.getDefault())`
- Amounts stored as `Double`, displayed to 2 decimal places.

---

## Common Mistakes to Avoid

1. **Do not DELETE Member rows.** Always `softDeleteMember(id)` which sets `isActive = false`.
2. **Do not add `fallbackToDestructiveMigration()`** — user data will be wiped on schema changes.
3. **Do not check BR-01 only in the UI.** The ViewModel must also reject the loan programmatically.
4. **Do not send member PII (name, phone) to Gemini API.** Use anonymized aggregate figures only.
5. **Do not store photos in external storage.** Use `context.filesDir`.
6. **Do not hardcode interest rate or weekly savings amount.** Read from DataStore so admin can configure them.
7. **Do not mix XML layouts with Compose.** This project is Compose-only.

---

## How to Build a Screen

Reference screens by ID from `APP_FLOWS_AND_VIEWS.md`:

> "Build S-08 (Weekly Savings Entry) as described in APP_FLOWS_AND_VIEWS.md"

Steps:
1. Create `ui/screens/<feature>/<Name>Screen.kt` as a `@Composable`.
2. Create a paired `<Name>ViewModel.kt` using `viewModelScope` + `UnnatiRepository`.
3. Add the route to `Screen.kt` and a `composable()` block in `MainActivity.kt`.
4. Inject `UnnatiRepository` into the ViewModel via `ViewModelProvider.Factory`.

For the Gemini feature:
> "Implement S-15 (GenAI Advisor) using the context in GEMINI.md"

---

## Testing Requirements

| SC-ID | Test Type | What to test |
|---|---|---|
| SC-01 | Instrumented | Insert SavingsEntry → assert `totalGroupCapital` Flow updates in < 500ms |
| SC-02 | Unit | `NewLoanViewModel` with mock ACTIVE loan member → `isBlocked = true`, `issueLoan()` returns error |
| SC-03 | Unit | Export ViewModel → plain text output contains all required fields |
| SC-04 | Migration | `MigrationTestHelper` against pre-seeded v1 DB |
| SC-05 | Unit | `InterestCalculator.simpleInterest(2000.0, 2.0, 6)` == `240.00` |
| SC-06 | Instrumented | Insert member with photo URI → force-stop → relaunch → photo displayed |
