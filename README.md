# Mahila-Shakti Unnati

> **Empowering Rural Women Through Digital Financial Literacy**

[![Platform](https://img.shields.io/badge/Platform-Android-green)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-blue)](https://kotlinlang.org)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26%20(Android%208.0)-orange)](https://developer.android.com/about/versions/oreo)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM-purple)](https://developer.android.com/topic/architecture)
[![License](https://img.shields.io/badge/License-Academic-lightgrey)](./docs/PRD.md)

**MindMatrix VTU Internship Program — Project #83**
**Author:** Suhas R Gudadar · 1HK22CS165 · rgsuhas07@gmail.com

---

## What is Unnati?

**Mahila-Shakti Unnati** is a fully offline Android app that acts as a *Digital Accountant* for women's Self-Help Groups (SHGs) in rural India.

SHGs are community savings groups where women pool weekly savings, accumulate capital, and issue small loans to members. Nearly all of them still use **handwritten paper registers** — leading to arithmetic errors, disputes, and loan mismanagement.

Unnati replaces those registers with a tamper-resistant Room DB ledger that works 100% without internet, is usable by semi-literate users, and automatically computes savings totals, interest, and loan eligibility.

---

## Problem Statement

| Pain Point | Real-World Impact |
|---|---|
| Manual arithmetic errors | Incorrect member balances, disputes within the group |
| No audit trail | Cannot verify historical transactions |
| Loan mismanagement | Double loans and unpaid dues go undetected |
| Zero digital footprint | Members can't build credit histories for formal banking |

---

## Key Features

| Feature | Description |
|---|---|
| **Offline-First** | 100% functional with no internet. All data in local Room DB. |
| **Weekly Savings** | Batch entry for all members. Duplicate-week prevention via DB unique index. |
| **Group Capital** | Reactive real-time total — updates in < 500ms on every new savings entry. |
| **Loan Engine** | Issue, track, and close loans. Simple interest `(P × R × T) / 100`. |
| **Loan Guard** | Blocks new loan if member already has an ACTIVE loan (ViewModel + DB level). |
| **Repayment Tracker** | Per-instalment records. Outstanding principal + accrued interest always visible. |
| **Member Profiles** | Photo (stored in `filesDir`), full savings & loan history, soft-delete only. |
| **Admin PIN Gate** | All write ops require 4-digit PIN. Session timeout: 5 minutes. |
| **WhatsApp Export** | Plain-text ledger summary shared via Android `ACTION_SEND` intent. |
| **Dark / Light Theme** | Multi-palette theme system with system-default fallback. |
| **GenAI Advisor** | Gemini-powered Q&A chatbot — anonymized data only, multilingual. |

---

## Screenshots / Design Prototypes

Interactive HTML prototypes are in [`design/`](design/):

| Screen | Light | Dark |
|---|---|---|
| Dashboard | [s03_dashboard.html](design/s03_dashboard.html) | [dark/s09_dashboard_dark.html](design/dark/s09_dashboard_dark.html) |
| Member List | [s04_member_list.html](design/s04_member_list.html) | [dark/s04_member_list_dark.html](design/dark/s04_member_list_dark.html) |
| Savings Entry | [s08_savings_entry.html](design/s08_savings_entry.html) | [dark/s08_savings_entry_dark.html](design/dark/s08_savings_entry_dark.html) |
| New Loan | [s10_new_loan.html](design/s10_new_loan.html) | [dark/s10_new_loan_dark.html](design/dark/s10_new_loan_dark.html) |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material Design 3 |
| Architecture | MVVM (ViewModel + StateFlow + Coroutines) |
| Database | Room DB · KSP annotation processing |
| Navigation | Navigation Compose |
| Preferences | Jetpack DataStore |
| Image Loading | Coil |
| AI | Google Generative AI SDK (Gemini 1.5 Flash) |
| Min SDK | 26 (Android 8.0 Oreo) |
| Target SDK | 35 |
| Build Tool | Gradle (Kotlin DSL) |

---

## Architecture

```
┌─────────────────────────────────────┐
│         Composable Screens          │
│  (pure UI, zero business logic)     │
│  collectAsState() ← StateFlow       │
└─────────────┬───────────────────────┘
              │ call ViewModel fns
┌─────────────▼───────────────────────┐
│           ViewModels                │
│  validation · calculations · state  │
│  viewModelScope + Dispatchers.IO    │
└─────────────┬───────────────────────┘
              │ suspend / Flow
┌─────────────▼───────────────────────┐
│        UnnatiRepository             │
│  single gateway — wraps all DAOs    │
└──────┬──────┬──────┬────────────────┘
       │      │      │      │
  MemberDao  SavingsDao  LoanDao  RepaymentDao
       │      │      │      │
┌──────▼──────▼──────▼──────▼────────┐
│         Room DB (SQLite)            │
│  Member · SavingsEntry · Loan       │
│  Repayment · FK constraints         │
└─────────────────────────────────────┘
```

---

## Project Structure

```
Unnati/
├── app/
│   └── src/main/java/com/example/unnati/
│       ├── UnnatiApp.kt              ← Application class (repo DI root)
│       ├── MainActivity.kt           ← NavHost, bottom nav, dark mode toggle
│       ├── data/
│       │   ├── AppDatabase.kt        ← Room DB singleton, v1
│       │   ├── AppDataStore.kt       ← DataStore keys (PIN, interest rate, etc.)
│       │   ├── dao/
│       │   │   ├── MemberDao.kt
│       │   │   ├── SavingsDao.kt
│       │   │   ├── LoanDao.kt
│       │   │   └── RepaymentDao.kt
│       │   ├── entity/
│       │   │   ├── Member.kt
│       │   │   ├── SavingsEntry.kt   ← UNIQUE(memberId, weekStartDate)
│       │   │   ├── Loan.kt
│       │   │   └── Repayment.kt
│       │   └── repository/
│       │       └── UnnatiRepository.kt
│       └── ui/
│           ├── navigation/Screen.kt  ← Sealed route definitions
│           ├── components/
│           │   └── GlassCard.kt
│           ├── screens/
│           │   ├── splash/           SplashScreen.kt
│           │   ├── auth/             PinScreen.kt
│           │   ├── dashboard/        DashboardScreen.kt
│           │   ├── members/          MemberListScreen · AddEditMemberScreen · MemberProfileScreen
│           │   ├── savings/          SavingsEntryScreen.kt
│           │   ├── loans/            LoanListScreen · NewLoanScreen · LoanDetailScreen
│           │   ├── export/           ExportPreviewScreen.kt
│           │   └── settings/         SettingsScreen.kt
│           ├── viewmodel/
│           │   ├── DashboardViewModel.kt
│           │   ├── MembersViewModel.kt
│           │   ├── MemberProfileViewModel.kt
│           │   ├── SavingsEntryViewModel.kt
│           │   ├── LoanViewModel.kt
│           │   ├── ExportViewModel.kt
│           │   ├── PinViewModel.kt
│           │   └── ViewModelFactory.kt
│           └── theme/
│               ├── Color.kt          ← DeepViolet · Saffron · SuccessGreen · ErrorRed
│               ├── Type.kt
│               └── Theme.kt
├── design/                           ← HTML prototypes (light + dark)
├── docs/                             ← All project documentation
│   ├── PRD.md                        ← Product Requirements Document
│   ├── APP_FLOWS_AND_VIEWS.md        ← All 16 screens with ASCII layouts
│   ├── GEMINI.md                     ← Gemini API integration guide
│   └── DESIGN_SYSTEM.md             ← Colors, typography, spacing tokens
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
    └── libs.versions.toml            ← Version catalog
```

---

## Installation & Setup

**Prerequisites**
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11+
- Android device or emulator running API 26+

**Clone and open**

```bash
git clone https://github.com/rgsuhas/unnati.git
cd unnati
# Open in Android Studio → File → Open → select this folder
```

**Gemini AI key (optional — only needed for GenAI Advisor screen)**

```bash
# Create local.properties if it doesn't exist, then add:
echo "GEMINI_API_KEY=your_key_here" >> local.properties
# Get a free key at: https://aistudio.google.com/app/apikey
# This file is in .gitignore — never committed
```

**Build and run**

```bash
# Build debug APK
./gradlew assembleDebug

# Install directly to connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Compile check only (fast, no APK)
./gradlew compileDebugKotlin

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

---

## Data Models

### Relationships
```
Member (1) ──▶ (N) SavingsEntry
Member (1) ──▶ (N) Loan
Loan   (1) ──▶ (N) Repayment
```

### Member
| Field | Type | Notes |
|---|---|---|
| `id` | Int PK | Auto-generated |
| `name` | String | |
| `phone` | String | |
| `photoUri` | String? | Path inside `context.filesDir` |
| `joinDate` | Long | Epoch ms |
| `role` | String | `ADMIN` or `MEMBER` |
| `isActive` | Boolean | Soft-delete only — rows are never deleted |

### SavingsEntry
| Field | Type | Notes |
|---|---|---|
| `memberId` | Int FK | → Member, CASCADE |
| `weekStartDate` | Long | Monday of week, epoch ms |
| `amount` | Double | |
| `status` | String | `PAID` or `PENDING` |

Unique DB constraint on `(memberId, weekStartDate)` prevents duplicate weekly entries.

### Loan
| Field | Type | Notes |
|---|---|---|
| `memberId` | Int FK | → Member, CASCADE |
| `principal` | Double | |
| `interestRate` | Double | Monthly % — read from DataStore |
| `durationMonths` | Int | |
| `status` | String | `ACTIVE` or `CLOSED` |

### Repayment
| Field | Type | Notes |
|---|---|---|
| `loanId` | Int FK | → Loan, CASCADE |
| `amount` | Double | |
| `paidDate` | Long | Epoch ms |
| `note` | String? | Optional remark |

---

## Business Rules

| ID | Rule | Where Enforced |
|---|---|---|
| BR-01 | Block new loan if member has an ACTIVE loan | `NewLoanViewModel` — disables button **and** rejects in `issueLoan()` |
| BR-02 | Group capital updates in < 500ms on new savings | Room reactive `Flow` → `SavingsDao.getTotalGroupCapital()` |
| BR-03 | Simple Interest = `(P × R × T) / 100`, T in months | `LoanDetailViewModel.accruedInterest` |
| BR-04 | Loan eligibility = `(memberSavings / groupTotal) × capital × multiplier` | `NewLoanViewModel.eligibilityAmount` |
| BR-05 | Deactivated members keep all history | `softDeleteMember()` sets `isActive = false` only |
| BR-06 | Savings date cannot be future | Validated in `SavingsEntryViewModel` before insert |
| BR-07 | Admin PIN required for all writes | PIN session, 5-minute timeout |
| BR-08 | Export is plain text only | `ExportViewModel` returns `String` — no binary formats |

---

## Screens

| Screen | Route | Access |
|---|---|---|
| Splash | `splash` | All |
| PIN Login | `pin_login` | All |
| Dashboard | `dashboard` | Admin |
| Member List | `member_list` | Admin |
| Add / Edit Member | `add_member/{memberId}` | Admin |
| Member Profile | `member_profile/{memberId}` | Admin + Member (own) |
| Weekly Savings Entry | `savings_entry` | Admin |
| Loan List | `loan_list` | Admin |
| New Loan | `new_loan` | Admin |
| Loan Detail | `loan_detail/{loanId}` | Admin |
| Export Preview | `export` | Admin |
| Settings | `settings` | Admin |

---

## Testing Plan

| ID | Type | What is tested |
|---|---|---|
| SC-01 | Instrumented | `SavingsEntry` insert → `totalGroupCapital` Flow updates in < 500ms |
| SC-02 | Unit | `NewLoanViewModel` with mock ACTIVE-loan member → `isBlocked = true`, `issueLoan()` returns error |
| SC-03 | Unit | `ExportViewModel` → output is plain text, contains all required fields |
| SC-04 | Migration | `MigrationTestHelper` against pre-seeded v1 DB — no data loss |
| SC-05 | Unit | `simpleInterest(principal=2000.0, rate=2.0, months=6)` == `240.00` |
| SC-06 | Instrumented | Insert member with photo → force-stop → relaunch → photo still displayed |

---

## Formulas Reference

```
Simple Interest   = (P × R × T) / 100          T = duration in months
Loan Eligibility  = (memberSavings / groupTotal) × groupCapital × multiplier
                    multiplier configurable in Settings (default: 3×)

Export summary format:
  📊 Mahila-Shakti Unnati — Group Report
  Date: DD/MM/YYYY
  👥 Total Members: X  (Active: Y)
  💰 Group Capital: ₹XX,XXX
  🏦 Active Loans: N  |  Outstanding: ₹XX,XXX
  ⚠️  Pending Dues: N members
```

---

## GenAI Advisor (Gemini Integration)

- Triggered from Dashboard → "Ask Unnati" button
- Uses **Gemini 1.5 Flash** (fast, free-tier friendly, multilingual)
- Responds in the same language the user writes in (Hindi / Kannada / Tamil / Telugu / English)
- Sends **only anonymized aggregate figures** — no names, phone numbers, or transaction IDs
- Falls back to static FAQ cards when offline
- Full implementation guide: [`docs/GEMINI.md`](docs/GEMINI.md)

---

## Documentation

| Document | Description |
|---|---|
| [`docs/PRD.md`](docs/PRD.md) | Full Product Requirements Document — problem, features, business rules, success criteria |
| [`docs/APP_FLOWS_AND_VIEWS.md`](docs/APP_FLOWS_AND_VIEWS.md) | All 16 screens with ASCII layouts, navigation flows, and Kotlin implementation prompts |
| [`docs/GEMINI.md`](docs/GEMINI.md) | Gemini API integration — system prompt templates, context builder, safety settings, offline fallback |
| [`docs/DESIGN_SYSTEM.md`](docs/DESIGN_SYSTEM.md) | Design tokens, color palette, typography, spacing, component guidelines |
| [`design/`](design/) | Interactive HTML prototypes for 4 key screens (light + dark variants) |

---

## Impact

| Area | How Unnati Delivers |
|---|---|
| Women's Empowerment | Gives rural women digital tools to manage collective capital independently |
| Financial Literacy | Real-time interest display and credit score teach money management basics |
| Transparency | Immutable digital ledger replaces ambiguous handwritten registers |
| Banking Access | Consistent digital records help members build credit history for formal banking |

---

*MindMatrix VTU Internship Program — Project #83 · Android App Development using Gen AI*
*Suhas R Gudadar · 1HK22CS165*
