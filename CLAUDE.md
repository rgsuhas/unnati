# Mahila-Shakti Unnati — Developer Guidelines
## For Claude Code & Human Developers

**Project:** Android App — Micro-Finance Digital Ledger for Women's SHGs  
**Stack:** Kotlin · MVVM · Room DB · Material Design 3 · Jetpack  
**MindMatrix VTU Internship Program — Project #83**

---

## Project Map

```
PRD.md                  — Full product requirements (source of truth)
APP_FLOWS_AND_VIEWS.md  — All 16 screens with ASCII layouts + Kotlin dev prompts
GEMINI.md               — Gemini API integration context and prompts
CLAUDE.md               — This file: dev guidelines for AI + humans
```

---

## Architecture Rules (Non-Negotiable)

### Pattern: MVVM — strictly layered, no exceptions
```
Fragment/Activity  →  ViewModel  →  Repository  →  Room DAO
```
- Fragments hold ZERO business logic. They only observe StateFlow and call ViewModel functions.
- ViewModels hold ALL business logic: validation, calculations, state.
- Repositories abstract all DB access. ViewModels never call DAOs directly.
- No `runBlocking` anywhere in UI layer. All DB calls via coroutines (`viewModelScope.launch`).

### StateFlow over LiveData
- Use `StateFlow<T>` for UI state in ViewModels.
- Use `collectAsState()` (Compose) or `repeatOnLifecycle(STARTED)` (Views) in Fragments.
- Never use `.observe()` on LiveData from the main thread outside lifecycle-aware components.

### Room DB rules
- Every insert/update that touches multiple tables must be in a `@Transaction`.
- Always write a `@Migration` class when changing schema — never use `fallbackToDestructiveMigration()` in production builds.
- Use `UNIQUE` constraints in SQL (e.g., one SavingsEntry per member per week) rather than checking in code.
- Foreign keys must be declared and `onDelete = CASCADE` or `RESTRICT` set explicitly.

---

## Business Rules — Enforce in ViewModel, Not Just UI

These are hard rules. Breaking them = app failure per success criteria.

| Rule | Where enforced |
|---|---|
| BR-01: Block new loan if member has ACTIVE loan | `NewLoanViewModel.isBlocked` StateFlow — disable button AND reject in `issueLoan()` |
| BR-02: Group capital recalculates < 500ms on new entry | Room reactive query via Flow — no manual refresh |
| BR-03: Simple Interest = (P × R × T) / 100 | `LoanDetailViewModel.accruedInterest` — T in months |
| BR-04: Loan eligibility = (memberSavings/groupTotal) × capital × multiplier | `NewLoanViewModel.eligibilityAmount` |
| BR-05: Deactivated members keep all history | `isActive = false` only — never DELETE Member rows |
| BR-06: Savings date cannot be future | Validate in `SavingsEntryViewModel.saveAllEntries()` |
| BR-07: Admin PIN required for all write ops | PIN session check in `PinViewModel` — timeout 5 min |
| BR-08: Export is plain text only | `ExportViewModel.buildReportString()` returns `String` — no HTML/Markdown |

---

## Kotlin Code Style

```kotlin
// GOOD — reactive, single source of truth
val groupCapital: StateFlow<Double> = savingsRepository
    .getSumOfPaidEntries()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

// BAD — manual fetch, not reactive
fun loadCapital() { viewModelScope.launch { _capital.value = dao.getSum() } }
```

- Use `data class` for UI state objects (one sealed class per screen).
- Use `sealed class Result<T>` for async operations: `Loading`, `Success(data)`, `Error(msg)`.
- Format all currency: `NumberFormat.getCurrencyInstance(Locale("en", "IN"))`.
- Format all dates: `SimpleDateFormat("dd MMM yyyy", Locale.getDefault())`.
- All amounts stored as `Double` in DB, displayed rounded to 2 decimal places.

### Naming conventions
```
Entities:       Member, SavingsEntry, Loan, Repayment          (PascalCase)
DAOs:           MemberDao, SavingsDao, LoanDao, RepaymentDao
Repositories:   MemberRepository, SavingsRepository, LoanRepository
ViewModels:     DashboardViewModel, NewLoanViewModel, etc.
Fragments:      DashboardFragment, MemberListFragment, etc.
Screens:        match screen IDs from APP_FLOWS_AND_VIEWS.md (S-01 to S-16)
```

---

## UI / Design Rules

- **Design system:** Material Design 3 (Material You). Use `MaterialCardView`, `TextInputLayout`, `MaterialButton` — not custom views unless necessary.
- **Colors:** defined in `res/values/colors.xml` using tokens from APP_FLOWS_AND_VIEWS.md. Never hardcode hex values in layouts.
- **Typography:** Poppins (display/headers) + Noto Sans (body, supports Indian scripts). Add via Google Fonts in `res/font/`.
- **Amounts:** always use JetBrains Mono font for all rupee values.
- **Icons:** Material Symbols (outlined style). No third-party icon packs.
- **No hardcoded strings in layouts.** All user-visible strings go in `res/values/strings.xml`. Regional language strings go in `res/values-hi/`, `res/values-kn/`, etc.
- **Minimum touch target:** 48dp × 48dp for all interactive elements (accessibility requirement).
- **Test on API 23** (Android 6.0) as minimum target — avoid APIs above 23 without version guards.

---

## File / Module Structure

```
app/src/main/java/com/mahilashakti/unnati/
├── data/
│   ├── db/
│   │   ├── AppDatabase.kt          ← Room DB singleton + migrations
│   │   ├── dao/
│   │   │   ├── MemberDao.kt
│   │   │   ├── SavingsDao.kt
│   │   │   ├── LoanDao.kt
│   │   │   └── RepaymentDao.kt
│   │   └── entity/
│   │       ├── Member.kt
│   │       ├── SavingsEntry.kt
│   │       ├── Loan.kt
│   │       └── Repayment.kt
│   └── repository/
│       ├── MemberRepository.kt
│       ├── SavingsRepository.kt
│       ├── LoanRepository.kt
│       └── RepaymentRepository.kt
├── ui/
│   ├── splash/        SplashActivity.kt
│   ├── auth/          PinLoginActivity.kt, PinViewModel.kt
│   ├── dashboard/     DashboardFragment.kt, DashboardViewModel.kt
│   ├── members/       MemberListFragment.kt, AddEditMemberFragment.kt,
│   │                  MemberProfileFragment.kt, MembersViewModel.kt
│   ├── savings/       SavingsEntryFragment.kt, SavingsEntryViewModel.kt,
│   │                  ContributionHistoryFragment.kt
│   ├── loans/         LoanListFragment.kt, NewLoanFragment.kt,
│   │                  LoanDetailFragment.kt, RepaymentBottomSheet.kt,
│   │                  LoanViewModel.kt, NewLoanViewModel.kt
│   ├── export/        ExportPreviewFragment.kt, ExportViewModel.kt
│   ├── settings/      SettingsFragment.kt
│   ├── advisor/       GenAIAdvisorFragment.kt, GenAIViewModel.kt    [optional]
│   └── analytics/     AnalyticsFragment.kt, AnalyticsViewModel.kt  [optional]
├── util/
│   ├── CurrencyFormatter.kt
│   ├── DateFormatter.kt
│   └── InterestCalculator.kt
└── MainActivity.kt    ← hosts BottomNav + NavGraph
```

---

## Dependencies (build.gradle — app)

```kotlin
// Core Jetpack
implementation "androidx.core:core-ktx:1.12.0"
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0"
implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.7.0"
implementation "androidx.fragment:fragment-ktx:1.6.2"
implementation "androidx.navigation:navigation-fragment-ktx:2.7.6"
implementation "androidx.navigation:navigation-ui-ktx:2.7.6"

// Room DB
implementation "androidx.room:room-runtime:2.6.1"
implementation "androidx.room:room-ktx:2.6.1"
kapt "androidx.room:room-compiler:2.6.1"

// DataStore (Settings)
implementation "androidx.datastore:datastore-preferences:1.0.0"

// Material Design 3
implementation "com.google.android.material:material:1.11.0"

// Image loading
implementation "io.coil-kt:coil:2.5.0"

// Security (EncryptedSharedPreferences for PIN)
implementation "androidx.security:security-crypto:1.1.0-alpha06"

// CameraX
implementation "androidx.camera:camera-camera2:1.3.1"
implementation "androidx.camera:camera-lifecycle:1.3.1"
implementation "androidx.camera:camera-view:1.3.1"

// WorkManager (notification reminders — good-to-have)
implementation "androidx.work:work-runtime-ktx:2.9.0"

// Gemini AI (good-to-have — GenAI advisor)
implementation "com.google.ai.client.generativeai:generativeai:0.3.0"

// Charts (good-to-have — analytics)
implementation "com.github.PhilJay:MPAndroidChart:v3.1.0"

// Coroutines
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
```

---

## Testing Requirements (Success Criteria mapping)

| SC-ID | Test Type | What to test |
|---|---|---|
| SC-01 | Instrumented | Insert SavingsEntry → assert groupCapital StateFlow updates in < 500ms |
| SC-02 | Unit | `NewLoanViewModel` with mock member having ACTIVE loan → `isBlocked = true`, `issueLoan()` returns error |
| SC-03 | Unit | `ExportViewModel.buildReportString()` → assert output is plain text, contains all required fields |
| SC-04 | Migration | Use `MigrationTestHelper` to test each `@Migration` against pre-seeded v1 DB |
| SC-05 | Unit | `InterestCalculator.simpleInterest(2000.0, 2.0, 6)` == `240.00` |
| SC-06 | Instrumented | Insert member with photo URI → force-stop → relaunch → assert photo displayed in list |

Run unit tests: `./gradlew test`  
Run instrumented tests: `./gradlew connectedAndroidTest`

---

## Common Mistakes to Avoid

1. **Do not DELETE Member rows.** Always set `isActive = false`. Deleting breaks FK constraints and destroys savings/loan history.
2. **Do not allow fallbackToDestructiveMigration().** This wipes user data on schema change. Always write `@Migration`.
3. **Do not issue a loan without checking BR-01 in the ViewModel.** UI-only guards can be bypassed.
4. **Do not send raw member PII (name, phone) to Gemini API.** Build anonymized context strings only.
5. **Do not store photos in external storage.** Use `context.filesDir` — external storage requires runtime permissions and can be cleared.
6. **Do not use `runBlocking` on the main thread.** Use `viewModelScope.launch` + `Dispatchers.IO` for all DB ops.
7. **Do not hardcode the default interest rate or weekly savings amount.** Read from DataStore Settings so the admin can change them.

---

## How to Use This Repo with Claude Code

When asking Claude to build a screen, reference it by screen ID:

> "Build S-08 (Weekly Savings Entry) as described in APP_FLOWS_AND_VIEWS.md"

Claude will:
1. Read `APP_FLOWS_AND_VIEWS.md` for the layout and dev prompt for that screen.
2. Read `PRD.md` for functional requirements and business rules.
3. Read `CLAUDE.md` (this file) for architecture patterns and naming conventions.
4. Follow the MVVM structure defined in the file structure section above.

When asking Claude to add the Gemini feature:

> "Implement S-15 (GenAI Advisor) using the context in GEMINI.md"
