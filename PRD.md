# Product Requirements Document
## Mahila-Shakti Unnati
### Micro-Finance Digital Ledger for Women's Self-Help Groups

**Project:** MindMatrix VTU Internship Program — Project #83  
**Domain:** Fintech / Micro-Finance (SHG)  
**Platform:** Android (Kotlin)  
**Author:** Suhas R Gudadar, 1hk22cs165  
**Version:** 1.0  
**Date:** 2026-05-07

---

## Table of Contents

1. [Problem Statement](#1-problem-statement)
2. [Vision & Product Description](#2-vision--product-description)
3. [Scope](#3-scope)
4. [User Roles](#4-user-roles)
5. [Functional Requirements](#5-functional-requirements)
6. [Non-Functional Requirements](#6-non-functional-requirements)
7. [Feature Catalogue](#7-feature-catalogue)
8. [App Usage & User Flow](#8-app-usage--user-flow)
9. [Screen Inventory](#9-screen-inventory)
10. [Data Models](#10-data-models)
11. [Business Rules](#11-business-rules)
12. [Technical Architecture](#12-technical-architecture)
13. [GenAI Integration](#13-genai-integration)
14. [Impact Goals](#14-impact-goals)
15. [Success Criteria](#15-success-criteria)

---

## 1. Problem Statement

Self-Help Groups (SHGs) are the backbone of rural micro-finance in India, empowering millions of women to pool savings and access credit. However, nearly all SHGs still rely on **physical registers and manual bookkeeping** — a process riddled with risk.

| Pain Point | Description |
|---|---|
| **Error-Prone Ledgers** | Manual arithmetic mistakes in weekly savings and interest calculations lead to incorrect balances. |
| **Dispute Triggers** | Discrepancies in handwritten records create mistrust and community conflict. |
| **No Audit Trail** | There is no reliable way to verify historical transactions. |
| **Loan Mismanagement** | Double loans and unpaid dues go undetected without a centralized system. |
| **Zero Digital Footprint** | Members cannot build credit histories for formal banking access. |

---

## 2. Vision & Product Description

**Mahila-Shakti Unnati** is a fully offline-capable Android application that serves as a *"Digital Accountant"* for women's SHGs. Built with Kotlin and Room DB, it replaces paper registers with a tamper-resistant digital ledger accessible even without internet.

The app is designed to be operated by **semi-literate rural women** using a simple, icon-driven interface in regional languages. It automatically calculates savings totals, simple interest, and loan eligibility — removing human error entirely.

**Tagline:** *Empowering Rural Women Through Digital Financial Literacy*

---

## 3. Scope

### In Scope

- Member registration with photo & details
- Weekly savings entry (Paid / Pending)
- Loan issuance, tracking & interest computation
- WhatsApp / text export of financial summaries
- Group capital and loan eligibility calculation
- Offline-first local Room DB storage
- Admin + Member role differentiation

### Out of Scope

- Real-time bank integration or UPI payments
- Multi-group federated management dashboard
- Cloud sync or remote server backend
- Biometric authentication
- Regulatory / RBI compliance reporting
- Automated SMS or notifications to members
- Formal credit score bureau reporting

---

## 4. User Roles

| Role | Access Level | Description |
|---|---|---|
| **Admin** | Full access | SHG group leader / secretary. Can add/edit members, record savings, issue loans, enter repayments, and export data. PIN-protected. |
| **Member (View)** | Read-only, own data | Can view their own savings history, loan status, and balance. Cannot see other members' data. |
| **Guest / Unauth** | None | No access. App opens to PIN login screen. |

---

## 5. Functional Requirements

| ID | Feature | Requirement Description |
|---|---|---|
| FR-01 | **Member Management** | The system shall allow an admin to add, edit, and deactivate SHG members with name, photo, phone, and joining date. |
| FR-02 | **Savings Entry** | The system shall record each member's weekly savings as 'Paid' or 'Pending' with a timestamp. |
| FR-03 | **Savings Aggregation** | The system shall automatically recalculate total group savings and individual balances upon each new entry. |
| FR-04 | **Loan Issuance** | The system shall allow a loan to be issued only if the member has no existing unpaid loan. |
| FR-05 | **Interest Calculation** | The system shall compute simple interest on active loans using configurable rate and duration. |
| FR-06 | **Repayment Tracking** | The system shall record partial and full loan repayments and update outstanding balance in real time. |
| FR-07 | **Loan Eligibility Check** | The system shall calculate and display each member's loan eligibility based on their savings contribution ratio. |
| FR-08 | **Data Export** | The system shall export a formatted text summary of group financials for sharing via WhatsApp Intent. |
| FR-09 | **Contribution History** | The system shall display a chronological payment history for each member. |
| FR-10 | **Role Access** | Admin users shall have full access; member-view users shall see only their own data. |

---

## 6. Non-Functional Requirements

| Category | Requirement | Target / Metric |
|---|---|---|
| **Performance** | Savings total must update on new entry | < 500 ms response time |
| **Offline Capability** | Full functionality without internet | 100% offline (Room DB) |
| **Reliability** | No data loss on app crash or restart | ACID transactions via Room |
| **Usability** | Operable by semi-literate users | Icon-driven UI, regional language support |
| **Scalability** | Handle up to 50 members per group | No performance degradation |
| **Security** | PIN-protected admin access | Local encrypted storage |
| **Maintainability** | DB schema changes must not corrupt data | Room migration scripts required |
| **Portability** | Run on low-end Android devices | Min SDK: Android 6.0 (API 23) |

---

## 7. Feature Catalogue

### Must-Have Features (MVP)

| Feature | Description |
|---|---|
| **Member Directory** | Add members with name, photo, phone, and role. Edit or soft-delete members without losing history. |
| **Weekly Savings Entry** | Mark each member's weekly contribution as Paid or Pending. Auto-timestamp every record. |
| **Real-Time Savings Total** | Group total savings and individual balances recalculate instantly on every new savings entry. |
| **Loan Issuance with Guard** | Issue loans to members. System blocks new loan if an unpaid loan already exists for that member. |
| **Simple Interest Engine** | Compute simple interest (P × R × T / 100) on active loans. Configurable interest rate per group. |
| **Repayment Tracker** | Record partial/full repayments. Show outstanding principal + accrued interest at any time. |
| **Loan Eligibility Display** | Show each member's eligible loan amount based on their savings ratio vs. group capital. |
| **WhatsApp Export** | Generate a plain-text financial summary and share via Android Intent (WhatsApp, email, SMS). |
| **Room DB with Migrations** | All data stored in Room DB with proper foreign-key relations. Migration scripts prevent data loss on schema updates. |
| **Contribution History View** | Chronological list of all savings payments per member with date and status. |

### Good-to-Have Features (Bonus)

| Feature | Description |
|---|---|
| **GenAI Financial Advisor** | Integrate a lightweight on-device LLM or Gemini API call to answer members' finance questions in local language. |
| **PIN / Fingerprint Lock** | Protect admin access with a PIN or Android BiometricPrompt for data security. |
| **Multilingual UI** | Support Hindi, Tamil, Telugu, Kannada using Android string resources and locale switching. |
| **PDF Report Generation** | Export monthly statements as a formatted PDF using iTextG or similar library. |
| **Dark Mode** | Full Material You dark-theme support for low-light rural environments. |
| **Notification Reminders** | Weekly WorkManager reminders to the admin to collect and enter savings dues. |
| **Analytics Dashboard** | Bar/pie charts showing savings trends, loan utilization, and repayment rates using MPAndroidChart. |
| **Backup to Google Drive** | Optional encrypted JSON backup to the group's shared Google Drive folder. |
| **Credit Score Indicator** | Simple in-app credit score (0–100) derived from repayment consistency to teach financial literacy. |
| **Aadhaar / Member ID Linking** | Optional field to store masked Aadhaar or member ID for identity verification. |

---

## 8. App Usage & User Flow

```
[App Launch]
     │
     ▼
[PIN Login Screen]
     │ Admin PIN correct
     ▼
[Dashboard]
 ├── Group Capital
 ├── Pending Dues Count
 └── Active Loans Count
     │
     ├──────────────────────────────────────┐
     │                                      │
     ▼                                      ▼
[Members Tab]                        [Savings Tab]
  ├── Member List                      ├── Select Week
  ├── Add Member (+)                   ├── Member List
  │     ├── Name                       └── Tap Paid / Pending
  │     ├── Photo (Camera/Gallery)           │ (totals update instantly)
  │     ├── Phone
  │     └── Join Date
  └── Tap Member → Member Profile
        ├── Contribution History
        ├── Loan Status
        └── Credit Score (optional)
     │
     ▼
[Loans Tab]
  ├── Active Loans List
  ├── New Loan
  │     ├── Select Member
  │     ├── System checks unpaid loan → BLOCK if exists
  │     ├── Show Eligibility Amount
  │     ├── Enter Principal + Rate
  │     └── Confirm → Loan Created
  └── Tap Loan → Loan Detail
        ├── Outstanding Principal
        ├── Accrued Interest
        ├── Repayment History
        └── Enter Repayment
     │
     ▼
[Export / Share]
  └── Tap Export on Dashboard
        └── Generates plain-text summary
              └── Android Intent → WhatsApp / SMS / Email
```

### Step-by-Step User Journey

| Step | Action | Details |
|---|---|---|
| 1 | **Admin Login** | Admin unlocks the app with PIN. Dashboard shows group capital, pending dues, and active loans at a glance. |
| 2 | **Member Directory** | Navigate to Members tab. Tap '+' to add a new member — fill name, photo (camera/gallery), phone, and join date. |
| 3 | **Weekly Savings Round** | Go to Savings. Select the week. For each member, tap Paid or Pending. Totals update instantly. |
| 4 | **Loan Application** | Member requests loan. Admin opens Loans > New Loan, selects member — system checks for unpaid loans and shows eligibility. Enter amount and rate. |
| 5 | **Repayment Entry** | On repayment day, Admin selects active loan, enters amount received. Interest auto-accrues. Balance updates. |
| 6 | **Export & Share** | Tap Export on the Dashboard. A clean summary (members, savings, loans, balances) is formatted and shared via WhatsApp Intent. |

---

## 9. Screen Inventory

| # | Screen Name | Role | Description |
|---|---|---|---|
| S-01 | **Splash / Launch** | All | App logo, version, brief load |
| S-02 | **PIN Login** | Admin | 4-digit PIN entry; wrong PIN shows error |
| S-03 | **Dashboard (Home)** | Admin | Group capital card, pending dues count, active loans count, quick-action buttons, export button |
| S-04 | **Member List** | Admin | RecyclerView of all members with photo, name, balance chip; FAB to add |
| S-05 | **Add / Edit Member** | Admin | Form: name, phone, join date, photo picker (camera or gallery), role selector |
| S-06 | **Member Profile** | Admin / Member | Member photo, total savings, credit score chip, tabs: [Contribution History \| Loan Status] |
| S-07 | **Contribution History** | Admin / Member | Chronological list of savings entries with date, week, status (Paid/Pending) |
| S-08 | **Weekly Savings Entry** | Admin | Week selector (date picker), member list with Paid/Pending toggle per row, running total footer |
| S-09 | **Loan List** | Admin | Active and closed loans; filter tabs; FAB for new loan |
| S-10 | **New Loan Form** | Admin | Member picker, eligibility display, principal input, interest rate input, duration, confirm button (blocked if unpaid loan exists) |
| S-11 | **Loan Detail** | Admin | Outstanding principal, accrued interest, repayment history list, "Enter Repayment" bottom sheet |
| S-12 | **Repayment Entry** | Admin | Amount input, date, note; auto-calculates remaining balance and interest |
| S-13 | **Export Preview** | Admin | Formatted text summary of group finances; share button triggers Android Intent |
| S-14 | **Settings** | Admin | Interest rate config, language selector, PIN change, dark mode toggle |
| S-15 | **GenAI Advisor** *(optional)* | Admin / Member | Chat UI for natural language finance Q&A via Gemini API |
| S-16 | **Analytics Dashboard** *(optional)* | Admin | Bar/pie charts: savings trends, loan utilization, repayment rates |

---

## 10. Data Models

### Entity: Member
```
Member {
  id            : Int (PK, autoGenerate)
  name          : String
  phone         : String
  photoUri      : String?
  joinDate      : Long (epoch ms)
  role          : Enum(ADMIN, MEMBER)
  isActive      : Boolean (soft delete)
}
```

### Entity: SavingsEntry
```
SavingsEntry {
  id            : Int (PK, autoGenerate)
  memberId      : Int (FK → Member.id)
  weekStartDate : Long (epoch ms)
  amount        : Double
  status        : Enum(PAID, PENDING)
  recordedAt    : Long (epoch ms)
}
```

### Entity: Loan
```
Loan {
  id            : Int (PK, autoGenerate)
  memberId      : Int (FK → Member.id)
  principal     : Double
  interestRate  : Double  // annual %
  startDate     : Long (epoch ms)
  durationMonths: Int
  status        : Enum(ACTIVE, CLOSED)
}
```

### Entity: Repayment
```
Repayment {
  id            : Int (PK, autoGenerate)
  loanId        : Int (FK → Loan.id)
  amount        : Double
  paidDate      : Long (epoch ms)
  note          : String?
}
```

### Relationships
```
Member  ──(1:N)──▶  SavingsEntry
Member  ──(1:N)──▶  Loan
Loan    ──(1:N)──▶  Repayment
```

---

## 11. Business Rules

| Rule ID | Rule | Enforcement |
|---|---|---|
| BR-01 | A member cannot receive a new loan if they have an existing loan with status = ACTIVE. | Checked in ViewModel before Loan insert; UI shows error and blocks form submission. |
| BR-02 | Total group savings must recalculate within 500ms whenever a SavingsEntry is inserted or updated. | Room + LiveData / StateFlow reactive query. |
| BR-03 | Simple interest = (Principal × Rate × Time) / 100. Time is in years (months / 12). | Computed in ViewModel; displayed on Loan Detail screen. |
| BR-04 | Loan eligibility = member's total savings / group total savings × group capital (configurable multiplier). | Displayed as max eligible amount on New Loan form. |
| BR-05 | Soft-delete only: deactivated members retain all historical records. | `isActive = false`; queries filter display but preserve data. |
| BR-06 | Savings entry date must not be in the future. | Validated at input layer in ViewModel. |
| BR-07 | Admin PIN is required to access any write operation. Session persists until app is backgrounded for > 5 minutes. | PIN checked on launch and on session timeout. |
| BR-08 | Export generates human-readable plain text only — no binary formats for core export. | String-formatted summary; PDF is a good-to-have. |

---

## 12. Technical Architecture

### Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Reactive** | LiveData / StateFlow + Coroutines |
| **Database** | Room DB (SQLite) |
| **UI** | Material Design 3, RecyclerView with DiffUtil |
| **Camera** | CameraX |
| **Export** | Android Implicit Intent (ACTION_SEND, MIME: text/plain) |
| **Min SDK** | Android 6.0 (API 23) |
| **Target SDK** | Android 14+ |

### Architecture Diagram

```
┌─────────────────────────────────────────┐
│              UI Layer                   │
│  Activities / Fragments / Composables   │
│  Material Design 3 Components           │
└──────────────┬──────────────────────────┘
               │ observes
┌──────────────▼──────────────────────────┐
│           ViewModel Layer               │
│  Business Logic, Validation, SI Calc    │
│  StateFlow / LiveData                   │
└──────────────┬──────────────────────────┘
               │ calls
┌──────────────▼──────────────────────────┐
│          Repository Layer               │
│  Abstracts DB access                    │
└──────────────┬──────────────────────────┘
               │ queries
┌──────────────▼──────────────────────────┐
│           Room DB (Local)               │
│  Entities: Member, SavingsEntry,        │
│            Loan, Repayment              │
│  DAOs + Migration Scripts               │
└─────────────────────────────────────────┘
```

### Database Migration Strategy
- Room `@Migration` classes handle schema version upgrades.
- All migrations must be tested against seed data before release.
- Migrations must never drop existing data columns — only ADD or RENAME.

---

## 13. GenAI Integration

| Aspect | Details |
|---|---|
| **Feature** | In-app Financial Advisor chatbot |
| **Trigger** | User taps "Ask Unnati" button on Dashboard or Member Profile |
| **Backend** | Gemini API (cloud) OR on-device ML Kit (offline fallback) |
| **Scope** | Natural language Q&A about group finances, interest, loan eligibility |
| **Language** | Supports query and response in Hindi, Kannada, Tamil, Telugu, English |
| **Data sent** | Anonymized aggregate figures only — no PII to external APIs |
| **Fallback** | If offline and no on-device model, show static FAQ cards |

---

## 14. Impact Goals

| Impact Area | How the App Delivers It |
|---|---|
| **Women's Empowerment** | Gives rural women digital tools to independently manage their collective capital, reducing dependency on male intermediaries. |
| **Financial Literacy** | The app's real-time interest display and credit-score indicator teach members the basics of credit and money management. |
| **Transparency** | An immutable digital ledger eliminates the ambiguity of handwritten registers, building trust within the group. |
| **Scalability of Impact** | Each SHG using the app can potentially link to formal banking, scaling micro-finance reach to thousands of women. |

---

## 15. Success Criteria

| ID | Success Criterion | Verification Method |
|---|---|---|
| SC-01 | Savings totals must update instantly (< 500 ms) when a new savings entry is added — no manual refresh. | Instrumented test with timestamp measurement |
| SC-02 | The app must display an error and block loan creation if the selected member has an existing unpaid loan. | Unit test on ViewModel loan guard logic |
| SC-03 | Data must be exportable as a clean, human-readable text string shareable via WhatsApp without extra formatting steps. | Manual QA on export flow |
| SC-04 | Room DB migrations must be correctly implemented — upgrading the app must not crash or corrupt existing data. | Migration test with pre-seeded v1 DB |
| SC-05 | Simple interest calculation must be accurate to 2 decimal places for all standard inputs. | Unit test with known P, R, T values |
| SC-06 | Member photos must persist across app restarts and be displayed in the member list without reloading. | Manual QA after force-stop and relaunch |

---

## Appendix: Quick Reference

### Interest Formula
```
Simple Interest = (Principal × Rate × Time) / 100
where Time = duration in months / 12
```

### Loan Eligibility Formula
```
Max Eligible Loan = (Member Total Savings / Group Total Savings) × Group Capital × Multiplier
Multiplier: configurable in Settings (default: 3x)
```

### Export Text Format (WhatsApp Summary)
```
📊 Mahila-Shakti Unnati — Group Report
Date: DD/MM/YYYY

👥 Total Members: X (Active: Y)
💰 Group Capital: ₹XX,XXX
📅 This Week Savings: ₹X,XXX (Paid: N | Pending: N)

🏦 Active Loans: N
   Outstanding Principal: ₹XX,XXX
   Accrued Interest: ₹X,XXX

⚠️  Pending Dues: N members
```

---

*Document prepared from MindMatrix VTU Internship Program — Project #83*  
*Mahila-Shakti Unnati | Android App Development using GenAI*
