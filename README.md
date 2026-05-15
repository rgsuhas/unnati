# Mahila-Shakti Unnati

> **Empowering Rural Women Through Digital Financial Literacy**

A fully offline-capable Android digital ledger for Women's Self-Help Groups (SHGs), replacing paper registers with an accurate, tamper-resistant system for savings, loans, and repayment tracking.

**MindMatrix VTU Internship Program — Project #83**  
**Author:** Suhas R Gudadar (1HK22CS165)

---

## Problem Statement

Nearly all rural SHGs in India still rely on handwritten registers. This leads to:

| Pain Point | Impact |
|---|---|
| Manual arithmetic errors | Incorrect balances and member disputes |
| No audit trail | Impossible to verify historical transactions |
| Loan mismanagement | Double loans and unpaid dues go undetected |
| Zero digital footprint | Members cannot build credit histories for formal banking |

---

## Features

- **Offline-first** — 100% functional without internet; all data stored locally in Room DB
- **Weekly savings tracking** — batch entry for all members with duplicate prevention
- **Loan lifecycle management** — issue, track, and close loans with simple-interest calculation
- **Repayment logging** — per-installment records with outstanding balance display
- **Group capital dashboard** — real-time reactive total updated on every savings entry
- **Member profiles** — photo support, full savings + loan history, soft-delete (history preserved)
- **Admin PIN gate** — all write operations require a 4-digit PIN with 5-minute session timeout
- **Plain-text export** — shareable ledger summary (no HTML, no Markdown — WhatsApp-friendly)
- **Dark / Light theme** — multi-palette theme system with system-default fallback
- **GenAI Advisor** — Gemini-powered financial health insights (anonymized data only)

---

## Screenshots

> Design mockups are in [`design/`](design/) — HTML prototypes for Dashboard, Member List, Savings Entry, and New Loan screens.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material Design 3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| Database | Room DB (KSP annotation processing) |
| Navigation | Navigation Compose |
| Image loading | Coil |
| Preferences | Jetpack DataStore |
| AI | Google Generative AI SDK (Gemini) |
| Min SDK | 26 (Android 8.0 Oreo) |
| Target SDK | 35 |

---

## Architecture

```
Composable Screen
      │  collectAsState()
      ▼
  ViewModel  (StateFlow, viewModelScope)
      │  suspend / Flow
      ▼
UnnatiRepository  (single aggregated repo)
      │
      ├── MemberDao
      ├── SavingsDao
      ├── LoanDao
      └── RepaymentDao
              │
              ▼
         Room Database
```

- **Composables** are pure UI — zero business logic.
- **ViewModels** own all validation, calculations, and state.
- **`UnnatiRepository`** is the single DB gateway; ViewModels never call DAOs directly.
- All DB access runs on `Dispatchers.IO` via `viewModelScope.launch {}` — no `runBlocking`.

---

## Project Structure

```
app/src/main/java/com/example/unnati/
├── UnnatiApp.kt                        ← Application class, DI root
├── MainActivity.kt                     ← NavHost, bottom nav, theme toggle
├── data/
│   ├── AppDatabase.kt                  ← Room DB singleton (v1)
│   ├── AppDataStore.kt                 ← DataStore keys & extension
│   ├── dao/
│   │   ├── MemberDao.kt
│   │   ├── SavingsDao.kt
│   │   ├── LoanDao.kt
│   │   └── RepaymentDao.kt
│   ├── entity/
│   │   ├── Member.kt
│   │   ├── SavingsEntry.kt             ← UNIQUE index (memberId, weekStartDate)
│   │   ├── Loan.kt
│   │   └── Repayment.kt
│   └── repository/
│       └── UnnatiRepository.kt
├── ui/
│   ├── navigation/Screen.kt            ← Sealed route definitions
│   ├── components/GlassCard.kt         ← Shared composable component
│   ├── screens/
│   │   ├── splash/SplashScreen.kt
│   │   ├── auth/PinScreen.kt
│   │   ├── dashboard/DashboardScreen.kt
│   │   ├── members/
│   │   │   ├── MemberListScreen.kt
│   │   │   ├── AddEditMemberScreen.kt
│   │   │   └── MemberProfileScreen.kt
│   │   ├── savings/SavingsEntryScreen.kt
│   │   ├── loans/
│   │   │   ├── LoanListScreen.kt
│   │   │   ├── NewLoanScreen.kt
│   │   │   └── LoanDetailScreen.kt
│   │   ├── export/ExportPreviewScreen.kt
│   │   └── settings/SettingsScreen.kt
│   ├── viewmodel/
│   │   ├── DashboardViewModel.kt
│   │   ├── MembersViewModel.kt
│   │   ├── MemberProfileViewModel.kt
│   │   ├── SavingsEntryViewModel.kt
│   │   ├── LoanViewModel.kt
│   │   ├── ExportViewModel.kt
│   │   ├── PinViewModel.kt
│   │   └── ViewModelFactory.kt
│   └── theme/
│       ├── Color.kt
│       ├── Type.kt
│       └── Theme.kt
```

---

## Data Models

### Member
| Field | Type | Notes |
|---|---|---|
| `id` | Int (PK) | Auto-generated |
| `name` | String | |
| `phone` | String | |
| `photoUri` | String? | Path inside `filesDir` |
| `joinDate` | Long | Epoch ms |
| `role` | String | `ADMIN` or `MEMBER` |
| `isActive` | Boolean | Soft-delete flag — never hard-delete |

### SavingsEntry
| Field | Type | Notes |
|---|---|---|
| `id` | Int (PK) | |
| `memberId` | Int (FK → Member) | CASCADE delete |
| `weekStartDate` | Long | Monday of week, epoch ms |
| `amount` | Double | |
| `status` | String | `PAID` or `PENDING` |
| `recordedAt` | Long | Epoch ms |

Unique constraint on `(memberId, weekStartDate)` prevents duplicate weekly entries.

### Loan
| Field | Type | Notes |
|---|---|---|
| `id` | Int (PK) | |
| `memberId` | Int (FK → Member) | CASCADE delete |
| `principal` | Double | |
| `interestRate` | Double | Monthly % (typically 2%) |
| `startDate` | Long | Epoch ms |
| `durationMonths` | Int | |
| `status` | String | `ACTIVE` or `CLOSED` |

### Repayment
| Field | Type | Notes |
|---|---|---|
| `id` | Int (PK) | |
| `loanId` | Int (FK → Loan) | CASCADE delete |
| `amount` | Double | |
| `paidDate` | Long | Epoch ms |
| `note` | String? | Optional remark |

---

## Business Rules

| Rule | Description | Enforced In |
|---|---|---|
| BR-01 | Block new loan if member already has an ACTIVE loan | `NewLoanViewModel` — disables button AND rejects in `issueLoan()` |
| BR-02 | Group capital recalculates in < 500ms on new savings entry | Room reactive `Flow` via `SavingsDao.getTotalGroupCapital()` |
| BR-03 | Simple Interest = (P × R × T) / 100, T in months | `LoanDetailViewModel.accruedInterest` |
| BR-04 | Loan eligibility = (memberSavings / groupTotal) × capital × multiplier | `NewLoanViewModel.eligibilityAmount` |
| BR-05 | Deactivated members retain full history | `softDeleteMember()` sets `isActive = false` — no row deletion |
| BR-06 | Savings date cannot be a future date | Validated in savings ViewModel before insert |
| BR-07 | Admin PIN required for all write operations | PIN session with 5-minute timeout |
| BR-08 | Export output is plain text only | `ExportViewModel` returns `String` — no HTML or Markdown |

---

## Screens (Navigation Routes)

| Screen | Route | Description |
|---|---|---|
| Splash | `splash` | Fade-in logo, 1.5s delay |
| PIN Login | `pin_login` | 4-digit admin PIN entry |
| Dashboard | `dashboard` | Group capital, quick actions, active loans count |
| Member List | `member_list` | Searchable list of active members |
| Add / Edit Member | `add_member/{memberId}` | Create new or edit existing member |
| Member Profile | `member_profile/{memberId}` | Savings history, loan list, photo |
| Savings Entry | `savings_entry` | Batch weekly savings for all members |
| Loan List | `loan_list` | All active and closed loans |
| New Loan | `new_loan` | Issue loan with eligibility check |
| Loan Detail | `loan_detail/{loanId}` | Interest calc, repayment log, close loan |
| Export Preview | `export` | Plain-text ledger preview + share |
| Settings | `settings` | Interest rate, savings amount, PIN change |

---

## Build & Run

**Prerequisites:** Android Studio Hedgehog or later, JDK 11+

```bash
# Clone
git clone https://github.com/rgsuhas/unnati.git
cd unnati

# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Check compilation only (fast)
./gradlew compileDebugKotlin

# Run instrumented tests (device/emulator required)
./gradlew connectedAndroidTest
```

The APK is output to `app/build/outputs/apk/debug/app-debug.apk`.

---

## Gemini AI Integration

The GenAI Advisor screen sends **anonymized aggregate figures only** — no member names, phone numbers, or individual balances leave the device. See [`GEMINI.md`](GEMINI.md) for prompt templates and integration context.

---

## Testing Requirements

| ID | Type | Scenario |
|---|---|---|
| SC-01 | Instrumented | Insert `SavingsEntry` → assert `totalGroupCapital` Flow updates in < 500ms |
| SC-02 | Unit | `NewLoanViewModel` with mock ACTIVE-loan member → `isBlocked = true`, `issueLoan()` returns error |
| SC-03 | Unit | `ExportViewModel` → plain-text output contains all required fields |
| SC-04 | Migration | `MigrationTestHelper` against pre-seeded v1 DB |
| SC-05 | Unit | `InterestCalculator.simpleInterest(2000.0, 2.0, 6) == 240.00` |
| SC-06 | Instrumented | Insert member with photo URI → force-stop → relaunch → photo displayed |

---

## Key Conventions

- **Currency:** `NumberFormat.getCurrencyInstance(Locale("en", "IN"))`
- **Dates:** `SimpleDateFormat("dd MMM yyyy", Locale.getDefault())`
- **Amounts:** stored as `Double`, displayed to 2 decimal places
- **Photos:** stored in `context.filesDir` — never external storage
- **Configurable values** (interest rate, weekly savings amount) read from DataStore — never hardcoded
- **Schema migrations:** always write a `@Migration` class — `fallbackToDestructiveMigration()` is forbidden

---

## Documentation

| File | Purpose |
|---|---|
| [`PRD.md`](PRD.md) | Full product requirements — source of truth |
| [`APP_FLOWS_AND_VIEWS.md`](APP_FLOWS_AND_VIEWS.md) | All 16 screens with ASCII layouts and Kotlin dev prompts |
| [`GEMINI.md`](GEMINI.md) | Gemini API integration context and prompt templates |
| [`CLAUDE.md`](CLAUDE.md) | AI assistant guidance for this codebase |
| [`design/`](design/) | HTML prototypes and design system reference |

---

## License

This project is developed as part of the MindMatrix VTU Internship Program (Project #83) by Suhas R Gudadar.
