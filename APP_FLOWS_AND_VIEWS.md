# Mahila-Shakti Unnati — App Flows & Screen Design Spec
## Complete UI/UX Blueprint with Dev Prompts

**Platform:** Android (Kotlin + Material Design 3)  
**Architecture:** MVVM + Room DB + StateFlow  
**Design Language:** Material You — Warm Saffron/Violet palette, icon-first, accessible  

---

## Global Design Tokens

```
Primary:        #7C3AED  (Deep Violet)
Primary Light:  #A78BFA
Accent:         #F59E0B  (Saffron/Amber)
Accent Light:   #FDE68A
Surface:        #FAFAF9
Background:     #F5F3FF
Error:          #DC2626
Success:        #16A34A
Warning:        #D97706
Text Primary:   #1C1917
Text Secondary: #78716C
Card BG:        #FFFFFF
Divider:        #E7E5E4

Typography:
  Display:  Poppins Bold
  Heading:  Poppins SemiBold
  Body:     Noto Sans (supports Devanagari/Kannada/Tamil/Telugu)
  Mono:     JetBrains Mono (for amounts/numbers)

Corner Radius:  16dp (cards), 12dp (buttons), 8dp (chips)
Elevation:      2dp (cards), 4dp (FAB), 8dp (bottom sheet)
```

---

## Navigation Structure

```
Bottom Navigation Bar (Admin view):
  [🏠 Home]  [👥 Members]  [💰 Savings]  [🏦 Loans]

Top App Bar:
  [← Back]  [Screen Title]  [⋮ Overflow / Share]

Member View (read-only):
  [🏠 Home]  [📋 My History]  [🏦 My Loan]
```

---

## Master Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          APP LAUNCH                                     │
│                        S-01: Splash                                     │
└─────────────────────────────┬───────────────────────────────────────────┘
                              │ 1.5s auto
                              ▼
                    ┌─────────────────┐
                    │  S-02: PIN Login │
                    └────────┬────────┘
              ┌──────────────┴──────────────┐
              │ Admin PIN correct            │ Member tap
              ▼                             ▼
    ┌──────────────────┐        ┌───────────────────────┐
    │ S-03: Dashboard  │        │ S-06: Member Profile   │
    │ (Admin Home)     │        │ (Member's own view)    │
    └──────┬───────────┘        └───────────────────────┘
           │
    ┌──────┼──────────────────────────┐
    │      │                          │
    ▼      ▼                          ▼
S-04    S-08                       S-09
Members  Savings                   Loans
List     Entry                     List
  │                                   │
  ▼                                   ├──► S-10: New Loan Form
S-05                                  │
Add/Edit                              └──► S-11: Loan Detail
Member                                          │
  │                                             ▼
  └──► S-06: Member Profile              S-12: Repayment Entry
             │
             └──► S-07: Contribution History

Dashboard ──► S-13: Export Preview ──► Android Share Intent
Dashboard ──► S-14: Settings
Dashboard ──► S-15: GenAI Advisor (optional)
Settings  ──► S-16: Analytics Dashboard (optional)
```

---

---

# S-01: Splash Screen

## Layout

```
┌─────────────────────────────────┐
│                                 │
│                                 │
│                                 │
│         [App Logo — 96dp]       │
│      🌸 (lotus/woman icon)      │
│                                 │
│    Mahila-Shakti Unnati         │
│    [Poppins Bold, 28sp]         │
│                                 │
│  Digital Accountant for SHGs   │
│  [Noto Sans, 14sp, secondary]  │
│                                 │
│                                 │
│                                 │
│       ════════════              │
│       [progress bar, accent]    │
│                                 │
│   Empowering Women Through      │
│   Digital Financial Literacy    │
│   [12sp, tertiary]              │
│                                 │
│         v1.0.0                  │
└─────────────────────────────────┘
Background: vertical gradient #7C3AED → #4C1D95
All text: white
```

## Dev Prompt
```
Create an Android SplashActivity in Kotlin.
- Use a full-screen gradient background: #7C3AED (top) to #4C1D95 (bottom) via GradientDrawable.
- Center a vertical LinearLayout containing:
    1. ImageView: app logo (lotus/woman SVG, 96dp × 96dp, white tint)
    2. TextView: "Mahila-Shakti Unnati" — Poppins Bold, 28sp, white
    3. TextView: "Digital Accountant for SHGs" — Noto Sans, 14sp, white 70% alpha
    4. ProgressBar (horizontal, indeterminate, accent color #F59E0B, width 120dp)
    5. TextView: "Empowering Women Through Digital Financial Literacy" — 12sp, white 60% alpha
- After 1500ms delay (Handler/coroutine), navigate to PinLoginActivity.
- Use WindowCompat.setDecorFitsSystemWindows(window, false) for edge-to-edge.
- No back stack entry (finish this activity after navigation).
- Apply fade-in animation to the logo and title using ObjectAnimator (alpha 0→1, 600ms).
```

---

# S-02: PIN Login Screen

## Layout

```
┌─────────────────────────────────┐
│  [Status bar — transparent]     │
│                                 │
│         [Logo 56dp]             │
│   Mahila-Shakti Unnati          │
│   [Poppins SemiBold 22sp]       │
│                                 │
│   ┌─────────────────────────┐   │
│   │  Enter PIN to continue  │   │
│   │  [14sp, secondary]      │   │
│   └─────────────────────────┘   │
│                                 │
│   ● ● ● ●   [PIN dots row]      │
│   [4 circles, 16dp each]        │
│                                 │
│  ┌───┐ ┌───┐ ┌───┐             │
│  │ 1 │ │ 2 │ │ 3 │             │
│  └───┘ └───┘ └───┘             │
│  ┌───┐ ┌───┐ ┌───┐             │
│  │ 4 │ │ 5 │ │ 6 │             │
│  └───┘ └───┘ └───┘             │
│  ┌───┐ ┌───┐ ┌───┐             │
│  │ 7 │ │ 8 │ │ 9 │             │
│  └───┘ └───┘ └───┘             │
│       ┌───┐ ┌───┐              │
│       │ 0 │ │ ⌫ │              │
│       └───┘ └───┘              │
│                                 │
│  [ERROR: Wrong PIN — red text]  │
│                                 │
│   ─────── OR ───────            │
│   [Member View (no PIN)]        │
│   [text button, 12sp]           │
└─────────────────────────────────┘
Background: #F5F3FF (light lavender)
Numpad keys: white cards, 64dp × 64dp, radius 12dp, shadow 2dp
PIN dots: unfilled=grey border, filled=#7C3AED
```

## Dev Prompt
```
Create PinLoginActivity in Kotlin.
- ViewModel: PinViewModel with StateFlow<PinState> (Idle, Checking, Error, Success).
- UI: 
    * Display 4 PIN indicator dots (filled = #7C3AED, empty = grey border circle, 16dp each, 12dp gap).
    * Custom numpad: 3×4 grid of MaterialCardView buttons (1–9, 0, backspace).
    * Each tap: add digit to pinBuffer (max 4), animate dot fill with scale spring animation.
    * On 4th digit: auto-submit, call PinViewModel.verifyPin(pin: String).
    * PinViewModel reads stored PIN from EncryptedSharedPreferences (key = "admin_pin", default "1234").
    * On match → navigate to DashboardActivity, clear back stack.
    * On fail → shake animation on dots row (TranslateAnimation, 3 cycles, 8dp), show "Incorrect PIN" in red, clear buffer.
    * Backspace: remove last digit, unfill dot.
- "Member View" TextButton at bottom → navigate to MemberDashboardActivity (no PIN required).
- First launch (no PIN set) → navigate to SetPinActivity instead.
- Use BiometricPrompt optionally if fingerprint enrolled (good-to-have).
```

---

# S-03: Dashboard (Admin Home)

## Layout

```
┌─────────────────────────────────┐
│ [Top bar] Mahila-Shakti Unnati  ⋮│
│           [subtitle: group name] │
├─────────────────────────────────┤
│                                 │
│  ┌─────────────────────────┐   │
│  │  💰 Group Capital        │   │
│  │  ₹ 48,500               │   │
│  │  [Poppins Bold 36sp]    │   │
│  │  +₹500 this week ↑      │   │
│  └─────────────────────────┘   │
│  [gradient card: violet→purple] │
│                                 │
│  ┌───────────┐ ┌───────────┐   │
│  │ ⚠️ Pending│ │🏦 Loans  │   │
│  │  Dues     │ │  Active   │   │
│  │  3        │ │  5        │   │
│  │ members   │ │ ₹12,000   │   │
│  └───────────┘ └───────────┘   │
│  [white cards, accent border]   │
│                                 │
│  Quick Actions                  │
│  [section header, 12sp caps]    │
│                                 │
│  ┌──────┐ ┌──────┐ ┌──────┐   │
│  │  👥  │ │  💵  │ │  🏦  │   │
│  │ Add  │ │ Log  │ │ New  │   │
│  │Member│ │Saving│ │ Loan │   │
│  └──────┘ └──────┘ └──────┘   │
│                                 │
│  ┌─────────────────────────┐   │
│  │ 📤 Export & Share       │   │
│  │ Generate group summary  │   │
│  │ [→ arrow right]         │   │
│  └─────────────────────────┘   │
│                                 │
│  Recent Activity                │
│  [RecyclerView, last 5 events]  │
│  • Priya paid ₹100 — today     │
│  • Loan issued Meena ₹2000     │
│  • Savita pending this week    │
│                                 │
├─────────────────────────────────┤
│  🏠    👥    💰    🏦           │
│ Home Members Savings Loans      │
└─────────────────────────────────┘
```

## Dev Prompt
```
Create DashboardFragment in Kotlin (part of MainActivity with BottomNavigationView).
- ViewModel: DashboardViewModel observing:
    * groupCapital: StateFlow<Double> — SUM of all SavingsEntry where status=PAID
    * pendingDuesCount: StateFlow<Int> — members with status=PENDING in current week
    * activeLoansCount: StateFlow<Int> — Loan where status=ACTIVE
    * totalLoanOutstanding: StateFlow<Double> — SUM of principal remaining on active loans
    * recentActivity: StateFlow<List<ActivityEvent>> — last 5 events across all tables

- UI:
    * Hero card (MaterialCardView, gradient background drawable #7C3AED→#5B21B6, cornerRadius 20dp):
        - "Group Capital" label, 12sp, white 70%
        - Amount in Poppins Bold 36sp white
        - Weekly delta with up arrow in Accent color
    * Two stat cards side-by-side (pending dues, active loans) — white, 1dp accent border.
    * Quick Action row: 3 icon-button cards leading to AddMemberFragment, SavingsEntryFragment, NewLoanFragment.
    * Export row card: tapping navigates to ExportPreviewActivity.
    * Recent Activity RecyclerView: simple rows with icon, description, time-ago string.
    * Use SwipeRefreshLayout (pull-to-refresh triggers re-query).
    * All money values formatted as: NumberFormat.getCurrencyInstance(Locale("en","IN")).
- BottomNavigationView: Home, Members, Savings, Loans — each switching fragments.
```

---

# S-04: Member List Screen

## Layout

```
┌─────────────────────────────────┐
│ ← Members          🔍  [+FAB]  │
├─────────────────────────────────┤
│ [Search bar — rounded, 48dp]    │
│ 🔍 Search members...            │
├─────────────────────────────────┤
│ 12 Members  [chip: All|Active]  │
├─────────────────────────────────┤
│ ┌───────────────────────────┐  │
│ │ [Photo 48dp] Priya Sharma │  │
│ │  📞 98765XXXXX            │  │
│ │  Joined: Jan 2024         │  │
│ │  Savings: ₹4,500  ✅ Paid │  │
│ └───────────────────────────┘  │
│ ┌───────────────────────────┐  │
│ │ [Photo 48dp] Meena Patil  │  │
│ │  📞 97654XXXXX            │  │
│ │  Joined: Mar 2024         │  │
│ │  Savings: ₹3,200  ⚠️ Due  │  │
│ └───────────────────────────┘  │
│  ... (RecyclerView)             │
│                                 │
│              [+ FAB]            │
│         violet, bottom right    │
└─────────────────────────────────┘
```

## Dev Prompt
```
Create MembersFragment in Kotlin.
- ViewModel: MembersViewModel with:
    * members: StateFlow<List<MemberWithStats>> — join Member + latest savings status + total savings
    * searchQuery: MutableStateFlow<String>
    * Filtered list = members.combine(searchQuery) { list, query → list.filter name contains query }

- RecyclerView with MemberAdapter (DiffUtil.ItemCallback):
    * Each item: ShapeableImageView (48dp circle, loaded with Coil/Glide from photoUri, fallback = initials avatar)
    * Member name: Poppins SemiBold 16sp
    * Phone: Noto Sans 13sp, secondary color
    * Joined date: formatted "MMM yyyy"
    * Savings amount: JetBrains Mono 14sp, primary color
    * Status chip: MaterialChip — green "Paid" or amber "Due" based on current week's SavingsEntry

- SearchView in Toolbar: filters list reactively via searchQuery StateFlow.
- Filter chips row: "All" / "Active" / "Inactive" — filters isActive flag.
- FAB (+): navigate to AddEditMemberFragment (mode = ADD).
- Item tap → navigate to MemberProfileFragment(memberId).
- Item long-press → bottom sheet: [Edit] [Deactivate] [View History].
- Empty state: illustration + "No members yet. Tap + to add." centered.
```

---

# S-05: Add / Edit Member Screen

## Layout

```
┌─────────────────────────────────┐
│ ←  Add Member           [Save] │
├─────────────────────────────────┤
│                                 │
│      ┌──────────────┐          │
│      │              │          │
│      │  [Photo area]│          │
│      │   96dp circle│          │
│      │  tap to pick │          │
│      │  📷 camera   │          │
│      └──────────────┘          │
│       [Change Photo]            │
│                                 │
│  Full Name *                    │
│  ┌─────────────────────────┐   │
│  │ Enter full name          │   │
│  └─────────────────────────┘   │
│                                 │
│  Phone Number *                 │
│  ┌─────────────────────────┐   │
│  │ 📞 +91 __________        │   │
│  └─────────────────────────┘   │
│                                 │
│  Date Joined *                  │
│  ┌─────────────────────────┐   │
│  │ 📅 DD / MM / YYYY        │   │
│  └─────────────────────────┘   │
│                                 │
│  Role                           │
│  ┌──────────┐ ┌─────────────┐  │
│  │ ● Member │ │ ○ Admin     │  │
│  └──────────┘ └─────────────┘  │
│  [segmented button pair]        │
│                                 │
│  ┌─────────────────────────┐   │
│  │   SAVE MEMBER           │   │
│  │   [filled button]       │   │
│  └─────────────────────────┘   │
│                                 │
│  [Edit mode only]               │
│  ┌─────────────────────────┐   │
│  │ ⚠️ Deactivate Member    │   │
│  │ [outlined danger button]│   │
│  └─────────────────────────┘   │
└─────────────────────────────────┘
```

## Dev Prompt
```
Create AddEditMemberFragment in Kotlin.
- Accepts argument: memberId (Int, -1 = ADD mode, >0 = EDIT mode).
- ViewModel: AddEditMemberViewModel with:
    * uiState: StateFlow<MemberFormState> — holds all field values + validation errors
    * loadMember(id) — populates fields in edit mode
    * saveOrUpdate() — validates then inserts/updates via Repository

- Photo picker:
    * ShapeableImageView 96dp circle, tappable.
    * Bottom sheet on tap: [Take Photo (CameraX Intent)] [Choose from Gallery (GetContent)] [Remove Photo].
    * Store photo to internal app storage (Context.filesDir/photos/), save URI string to Member.photoUri.
    * Display using Coil: load(uri) with circular transform.

- Form fields (TextInputLayout + TextInputEditText, Material outlined style):
    * Name: required, minLength 2.
    * Phone: required, InputType phone, validate 10-digit Indian number.
    * Join Date: not a keyboard field — tap opens MaterialDatePicker (DatePicker dialog), display formatted date.
    * Role: MaterialButtonToggleGroup with 2 buttons: MEMBER / ADMIN.

- Validation: show inline errors via TextInputLayout.error on Save tap.
- Save flow: show CircularProgressIndicator, on success → popBackStack() + show Snackbar "Member saved".
- Edit mode: pre-populate all fields, show "Deactivate Member" button at bottom (sets isActive = false after confirmation AlertDialog).
- Navigation: Top bar "Save" menu item triggers same as Save button.
```

---

# S-06: Member Profile Screen

## Layout

```
┌─────────────────────────────────┐
│ ←  Member Profile        [⋮]   │
├─────────────────────────────────┤
│                                 │
│  ┌──────────────────────────┐  │
│  │ [Photo 72dp]  Priya S.   │  │
│  │               📞 987...  │  │
│  │               Jan 2024   │  │
│  │ ┌────────┐ ┌──────────┐ │  │
│  │ │₹ 4,500 │ │  Score   │ │  │
│  │ │Total   │ │  82/100  │ │  │
│  │ │Savings │ │  🟢 Good  │ │  │
│  │ └────────┘ └──────────┘ │  │
│  └──────────────────────────┘  │
│  [header card, violet gradient] │
│                                 │
│  Current Week Status            │
│  ┌─────────────────────────┐   │
│  │  ✅ Paid — This Week    │   │
│  │  ₹ 100 on 05 May 2026  │   │
│  └─────────────────────────┘   │
│                                 │
│  Active Loan                    │
│  ┌─────────────────────────┐   │
│  │  🏦 Loan: ₹ 2,000      │   │
│  │  Outstanding: ₹ 1,500  │   │
│  │  Due: 15 Jun 2026      │   │
│  │  [View Loan →]          │   │
│  └─────────────────────────┘   │
│                                 │
│  [TAB ROW]                      │
│  ┌──────────────────────────┐  │
│  │ Contribution  │   Loan   │  │
│  │   History     │  History │  │
│  └──────────────────────────┘  │
│                                 │
│  [Tab content — see S-07]       │
└─────────────────────────────────┘
```

## Dev Prompt
```
Create MemberProfileFragment in Kotlin.
- Argument: memberId: Int
- ViewModel: MemberProfileViewModel(memberId) observing:
    * member: StateFlow<Member>
    * totalSavings: StateFlow<Double> — SUM of paid entries for this member
    * creditScore: StateFlow<Int> — computed as: (paidCount / totalExpectedWeeks * 100).coerceIn(0,100)
    * currentWeekEntry: StateFlow<SavingsEntry?> — entry for current ISO week
    * activeLoan: StateFlow<Loan?> — Loan where memberId=this and status=ACTIVE

- Header card (violet gradient):
    * Circular photo (Coil), name, phone, join date.
    * Two stat chips: Total Savings (JetBrains Mono, amber), Credit Score (colored: red<40, amber<70, green>=70).
    
- Current week status card:
    * Green card if Paid, amber card if Pending, grey if no entry yet.
    * Admin: tapping this card navigates to SavingsEntryFragment filtered to this member.

- Active loan card (shown only if activeLoan != null):
    * Outstanding amount, due date.
    * "View Loan" button → LoanDetailFragment(loanId).
    * If no active loan → show "No active loan" + "Apply for Loan" button (Admin only).

- TabLayout + ViewPager2 with 2 tabs:
    * Tab 1: ContributionHistoryFragment (memberId)
    * Tab 2: LoanHistoryFragment (memberId) — list of all past loans

- Overflow menu (⋮): [Edit Member] [Export Member Summary] [Deactivate].
```

---

# S-07: Contribution History Screen

## Layout

```
┌─────────────────────────────────┐
│ ←  Contribution History         │
│    Priya Sharma                 │
├─────────────────────────────────┤
│ Filter: [All ▼] [2026 ▼]       │
├─────────────────────────────────┤
│ MAY 2026                        │
│ ┌───────────────────────────┐  │
│ │ Week 19 — 05 May 2026     │  │
│ │ ₹ 100          ✅ PAID    │  │
│ └───────────────────────────┘  │
│ ┌───────────────────────────┐  │
│ │ Week 18 — 28 Apr 2026     │  │
│ │ ₹ 100          ✅ PAID    │  │
│ └───────────────────────────┘  │
│                                 │
│ APR 2026                        │
│ ┌───────────────────────────┐  │
│ │ Week 17 — 21 Apr 2026     │  │
│ │ ₹ 100         ⚠️ PENDING  │  │
│ └───────────────────────────┘  │
│  ...                            │
│                                 │
│ ─────────────────────────────  │
│ Total Paid:     ₹ 4,200         │
│ Total Pending:  ₹   300         │
│ Entries:        45              │
└─────────────────────────────────┘
```

## Dev Prompt
```
Create ContributionHistoryFragment in Kotlin.
- Argument: memberId: Int
- ViewModel: ContributionHistoryViewModel(memberId) with:
    * entries: StateFlow<List<SavingsEntry>> ordered by weekStartDate DESC
    * totalPaid: StateFlow<Double>
    * totalPending: StateFlow<Double>
    * selectedYear: MutableStateFlow<Int> (default = current year)

- RecyclerView with grouping by month:
    * Use ConcatAdapter or a single adapter with VIEW_TYPE_HEADER and VIEW_TYPE_ENTRY.
    * Month headers: "MAY 2026" — Poppins SemiBold 13sp, secondary color, uppercase.
    * Entry rows: week number, formatted date, amount (JetBrains Mono), status chip.
    * Chip: green "PAID" or amber "PENDING".
    
- Sticky header month labels using StickyHeaderDecoration or SnapHelper.

- Filter row:
    * Status dropdown: All / Paid / Pending.
    * Year spinner: current year and past 2 years.

- Footer summary bar (fixed at bottom):
    * "Total Paid: ₹X,XXX" | "Pending: ₹XXX" | "Entries: N"
    * Divider above, white background.

- Empty state: "No contributions recorded yet." with savings icon.
- Admin only: long-press on entry → [Edit Entry] [Delete Entry] bottom sheet.
```

---

# S-08: Weekly Savings Entry Screen

## Layout

```
┌─────────────────────────────────┐
│ ←  Weekly Savings          [✓] │
├─────────────────────────────────┤
│ Week of:                        │
│ ┌─────────────────────────┐    │
│ │ 📅 05 May – 11 May 2026 │    │
│ └─────────────────────────┘    │
│ [tap to change week — picker]   │
├─────────────────────────────────┤
│ Amount per member: ₹ 100        │
│ [editable TextInputEditText]    │
├─────────────────────────────────┤
│ ┌───────────────────────────┐  │
│ │[Photo] Priya Sharma   PAID│  │
│ │        ₹ 100   [toggle]  │  │
│ └───────────────────────────┘  │
│ ┌───────────────────────────┐  │
│ │[Photo] Meena Patil  PEND. │  │
│ │        ₹ 100   [toggle]  │  │
│ └───────────────────────────┘  │
│ ┌───────────────────────────┐  │
│ │[Photo] Savita Devi    PAID│  │
│ │        ₹ 100   [toggle]  │  │
│ └───────────────────────────┘  │
│  ... (all active members)       │
├─────────────────────────────────┤
│ Paid: 9/12    Total: ₹ 900      │
│ [progress bar, green fill]      │
│ [SAVE ALL ENTRIES]              │
│ [filled button, full width]     │
└─────────────────────────────────┘
```

## Dev Prompt
```
Create SavingsEntryFragment in Kotlin.
- ViewModel: SavingsEntryViewModel with:
    * selectedWeek: MutableStateFlow<LocalDate> — start of ISO week (default = current week Monday)
    * weeklyAmount: MutableStateFlow<Double> — default from Settings (e.g., ₹100)
    * memberEntries: StateFlow<List<MemberSavingsRow>> — all active members with their entry for selectedWeek
    * paidCount: StateFlow<Int>, totalCollected: StateFlow<Double>
    * saveAllEntries() — batch upsert all rows to Room

- Week picker:
    * Tapping the week chip opens MaterialDatePicker (SELECTION_MODE_SINGLE).
    * Selected date snapped to Monday of that week.
    * On week change, reload memberEntries from DB (existing entries pre-fill status).

- Amount field: TextInputEditText, numeric. Changing it updates all row amounts in the list.

- RecyclerView (MemberSavingsAdapter):
    * Each row: circular photo (Coil), member name, amount field (pre-filled), toggle button.
    * Toggle button: MaterialButton with two states — "PAID" (green, filled) / "PENDING" (amber, outlined).
    * Toggling updates the in-memory list item (does NOT save immediately).
    * Amount is per-row editable (override for one member if needed).

- Footer sticky bar:
    * LinearProgressIndicator: paidCount / totalMembers.
    * "Paid: X/Y" label, total amount collected.

- "Save All Entries" button:
    * Calls saveAllEntries() — inserts/updates SavingsEntry for each row.
    * On success: Snackbar "Savings recorded for week of [date]", dashboard LiveData auto-updates.
    * Uses Room transaction for atomicity.
    * Success criteria: group capital recalculates in < 500ms (verified via StateFlow update time).
```

---

# S-09: Loan List Screen

## Layout

```
┌─────────────────────────────────┐
│ ←  Loans                 [+]   │
├─────────────────────────────────┤
│ [ACTIVE] [CLOSED]               │
│ [filter tabs]                   │
├─────────────────────────────────┤
│ Active Loans — ₹ 12,000 total   │
│                                 │
│ ┌───────────────────────────┐  │
│ │ [Photo] Meena Patil       │  │
│ │  Principal: ₹ 2,000      │  │
│ │  Outstanding: ₹ 1,650    │  │
│ │  Rate: 2%/month          │  │
│ │  Due: 15 Jun 2026  🔴 3d │  │
│ └───────────────────────────┘  │
│ ┌───────────────────────────┐  │
│ │ [Photo] Savita Devi       │  │
│ │  Principal: ₹ 5,000      │  │
│ │  Outstanding: ₹ 4,200    │  │
│ │  Rate: 2%/month          │  │
│ │  Due: 30 Jun 2026  🟡 28d│  │
│ └───────────────────────────┘  │
│  ...                            │
│                                 │
│              [+ FAB]            │
└─────────────────────────────────┘
```

## Dev Prompt
```
Create LoansFragment in Kotlin.
- ViewModel: LoansViewModel with:
    * activeLoans: StateFlow<List<LoanWithMember>> — Loan JOIN Member where status=ACTIVE
    * closedLoans: StateFlow<List<LoanWithMember>> — status=CLOSED
    * totalOutstanding: StateFlow<Double> — SUM outstanding active loans
    * selectedTab: MutableStateFlow<LoanTab> (ACTIVE | CLOSED)

- TabLayout with ViewPager2 OR simple show/hide based on selectedTab chip.

- RecyclerView (LoanAdapter with DiffUtil):
    * Each card: member photo + name, principal, outstanding amount, interest rate, due date.
    * Due date badge: red if due in ≤7 days, amber if ≤30 days, green otherwise.
    * Outstanding amount in JetBrains Mono, color-coded (red if overdue).
    * Mini progress bar showing repayment progress (repaid / principal).

- Header row: "Active Loans — ₹XX,XXX total" or "Closed Loans — X loans"

- FAB (+): navigate to NewLoanFragment.
- Item tap: navigate to LoanDetailFragment(loanId).

- Empty state (active tab): "No active loans." with illustration.
- Sort: by due date ascending (soonest first).
```

---

# S-10: New Loan Form Screen

## Layout

```
┌─────────────────────────────────┐
│ ←  New Loan                     │
├─────────────────────────────────┤
│                                 │
│  Select Member *                │
│  ┌─────────────────────────┐   │
│  │ 👤 Choose member...  ▼  │   │
│  └─────────────────────────┘   │
│                                 │
│  [After member selected:]       │
│  ┌─────────────────────────┐   │
│  │ ✅ Eligible for loan     │   │
│  │ Max amount: ₹ 6,750     │   │
│  │ Based on 45% savings    │   │
│  │ contribution ratio      │   │
│  └─────────────────────────┘   │
│  [green info card]              │
│                                 │
│  Loan Amount *                  │
│  ┌─────────────────────────┐   │
│  │ ₹ __________________    │   │
│  └─────────────────────────┘   │
│                                 │
│  Interest Rate (% / month) *   │
│  ┌─────────────────────────┐   │
│  │ 2                        │   │
│  └─────────────────────────┘   │
│                                 │
│  Duration (months) *            │
│  ┌─────────────────────────┐   │
│  │ 6                        │   │
│  └─────────────────────────┘   │
│                                 │
│  ┌─────────────────────────┐   │
│  │  Estimated Interest:    │   │
│  │  ₹ 720                  │   │
│  │  Total Repayable: ₹4,720│   │
│  └─────────────────────────┘   │
│  [auto-calculated preview card] │
│                                 │
│  ┌─────────────────────────┐   │
│  │   ISSUE LOAN            │   │
│  └─────────────────────────┘   │
│                                 │
│  [BLOCKED STATE — red card:]    │
│  ┌─────────────────────────┐   │
│  │ ❌ Cannot Issue Loan    │   │
│  │ Meena has an unpaid     │   │
│  │ loan of ₹ 1,650         │   │
│  │ [View Existing Loan →]  │   │
│  └─────────────────────────┘   │
└─────────────────────────────────┘
```

## Dev Prompt
```
Create NewLoanFragment in Kotlin.
- ViewModel: NewLoanViewModel with:
    * eligibleMembers: StateFlow<List<Member>> — all active members
    * selectedMember: MutableStateFlow<Member?>
    * existingLoan: StateFlow<Loan?> — active loan for selectedMember (null = eligible)
    * loanEligibilityAmount: StateFlow<Double> — (memberSavings / groupTotal) * groupCapital * multiplier
    * principal: MutableStateFlow<String>
    * rate: MutableStateFlow<String> (default from Settings)
    * duration: MutableStateFlow<String>
    * estimatedInterest: StateFlow<Double> — (P * R * T) / 100 reactive calculation
    * isBlocked: StateFlow<Boolean> — existingLoan != null
    * issueLoan() — validates, inserts Loan entity

- Member dropdown: MaterialAutoCompleteTextView showing all active members.
  On selection → trigger loadMemberLoanStatus(memberId).

- Eligibility card (animated slide-in):
    * If isBlocked = false: green card showing max eligible amount and savings ratio %.
    * If isBlocked = true: red card showing existing loan amount with "View Existing Loan" button.
    * Issue Loan button: DISABLED and grey when isBlocked = true.

- Real-time interest preview: recalculates on every keystroke in principal/rate/duration fields.
  Show: "Estimated Interest: ₹XXX | Total Repayable: ₹X,XXX" in a neutral info card.

- Validation:
    * Principal must not exceed loanEligibilityAmount.
    * Rate must be > 0 and < 100.
    * Duration must be > 0.
    * Member must be selected.

- On success: navigate back to LoansFragment, show Snackbar "Loan issued to [name]".
- SUCCESS CRITERIA: Loan insert is blocked at ViewModel level if existingLoan != null — not just UI.
```

---

# S-11: Loan Detail Screen

## Layout

```
┌─────────────────────────────────┐
│ ←  Loan Detail                  │
├─────────────────────────────────┤
│  ┌──────────────────────────┐  │
│  │ [Photo] Meena Patil      │  │
│  │  Loan Issued: 01 Dec 2025│  │
│  │  ──────────────────────  │  │
│  │  Principal:    ₹ 2,000   │  │
│  │  Rate:         2%/month  │  │
│  │  Duration:     6 months  │  │
│  │  ──────────────────────  │  │
│  │  Accrued Interest: ₹ 120 │  │
│  │  Total Repaid:     ₹ 470 │  │
│  │  OUTSTANDING:   ₹ 1,650  │  │
│  │  [large, red, bold]      │  │
│  └──────────────────────────┘  │
│                                 │
│  ██████░░░░░░░░░░  23%         │
│  [repayment progress bar]       │
│                                 │
│  [ENTER REPAYMENT — button]     │
│                                 │
│  Repayment History              │
│  ┌───────────────────────────┐ │
│  │ 05 May 2026  ₹ 300  Full │ │
│  │ 01 Apr 2026  ₹ 170  Part │ │
│  │ 01 Mar 2026  ₹  -    -   │ │
│  └───────────────────────────┘ │
│                                 │
│  [Mark as Closed — if balance=0]│
└─────────────────────────────────┘
```

## Dev Prompt
```
Create LoanDetailFragment in Kotlin.
- Argument: loanId: Int
- ViewModel: LoanDetailViewModel(loanId) observing:
    * loan: StateFlow<Loan>
    * member: StateFlow<Member>
    * repayments: StateFlow<List<Repayment>> ordered by paidDate DESC
    * totalRepaid: StateFlow<Double> — SUM of repayment amounts
    * accruedInterest: StateFlow<Double> — Simple Interest up to today
    * outstanding: StateFlow<Double> — principal + accruedInterest - totalRepaid

- Header card:
    * Member photo + name.
    * Issued date, principal, rate, duration.
    * Divider.
    * Accrued interest (computed in ViewModel).
    * Total repaid.
    * Outstanding balance: large text, red color if > 0, green if = 0.

- LinearProgressIndicator: value = totalRepaid / (principal + accruedInterest).
  Animated update when repayments change.

- "Enter Repayment" button: opens RepaymentEntryBottomSheet(loanId).
  Hidden if loan.status = CLOSED.

- Repayment history RecyclerView:
    * Date, amount, note.
    * "Full" badge if repayment closes the loan, "Partial" otherwise.
    * Empty state: "No repayments yet."

- "Mark as Closed" button: shown only when outstanding ≤ 0 and status = ACTIVE.
  Tapping → AlertDialog confirm → sets loan.status = CLOSED.

- Outstanding calculation must update in real-time via StateFlow whenever a repayment is added.
```

---

# S-12: Repayment Entry (Bottom Sheet)

## Layout

```
┌─────────────────────────────────┐
│ ▬▬▬  [drag handle]              │
│  Enter Repayment                │
│  Meena Patil — Loan #7          │
├─────────────────────────────────┤
│  Outstanding: ₹ 1,650           │
│  [amber info chip]              │
│                                 │
│  Amount Received *              │
│  ┌─────────────────────────┐   │
│  │ ₹ __________________    │   │
│  └─────────────────────────┘   │
│  [Pay Full — ₹1,650 chip]      │
│                                 │
│  Date *                         │
│  ┌─────────────────────────┐   │
│  │ 📅 07 May 2026           │   │
│  └─────────────────────────┘   │
│                                 │
│  Note (optional)                │
│  ┌─────────────────────────┐   │
│  │ e.g. "Paid in cash"     │   │
│  └─────────────────────────┘   │
│                                 │
│  After this payment:            │
│  Remaining: ₹ 1,350            │
│  [live preview, secondary text] │
│                                 │
│  [RECORD REPAYMENT — button]    │
└─────────────────────────────────┘
```

## Dev Prompt
```
Create RepaymentEntryBottomSheet (BottomSheetDialogFragment) in Kotlin.
- Argument: loanId: Int
- ViewModel: shared with LoanDetailFragment (same LoanDetailViewModel).

- Shows current outstanding amount in an amber chip at top.
- Amount field: numeric TextInputEditText. 
    * "Pay Full" suggestion chip below: tapping fills field with outstanding amount.
    * Live preview: "After this payment: Remaining ₹X,XXX" — recalculates as user types.
    * Validate: amount must be > 0 and ≤ outstanding.
    
- Date field: defaults to today. Tapping opens MaterialDatePicker. Cannot be future date.
- Note field: optional, single line.

- "Record Repayment" button:
    * Inserts Repayment entity with loanId, amount, paidDate, note.
    * Uses Room coroutine transaction.
    * If amount >= outstanding → automatically updates Loan.status = CLOSED in same transaction.
    * On success: dismiss bottom sheet. LoanDetailFragment StateFlow updates reactively → outstanding updates in < 500ms.
    * On success: Snackbar on parent fragment "Repayment recorded. Outstanding: ₹X,XXX"

- Peek height: 60% of screen. Expandable to full.
```

---

# S-13: Export Preview Screen

## Layout

```
┌─────────────────────────────────┐
│ ←  Export Report          [📤] │
├─────────────────────────────────┤
│ ┌─────────────────────────┐    │
│ │ 📊 Mahila-Shakti Unnati │    │
│ │    Group Report          │    │
│ │    07 May 2026           │    │
│ │                          │    │
│ │ 👥 Total Members: 12    │    │
│ │    Active: 11            │    │
│ │                          │    │
│ │ 💰 Group Capital: ₹48,500│   │
│ │ 📅 Week Savings:  ₹1,100 │   │
│ │    Paid: 9 | Pending: 3  │   │
│ │                          │    │
│ │ 🏦 Active Loans: 5      │    │
│ │    Outstanding: ₹12,000  │    │
│ │    Interest Due: ₹  840  │    │
│ │                          │    │
│ │ ⚠️  Pending Dues: 3     │    │
│ │    Meena, Kavita, Rani   │    │
│ └─────────────────────────┘    │
│ [monospace font, paper-like BG] │
│                                 │
│ Share via:                      │
│ ┌──────┐ ┌──────┐ ┌──────┐   │
│ │ 📱   │ │ 📧   │ │ 💬   │   │
│ │WhatsApp│Email│  SMS  │   │
│ └──────┘ └──────┘ └──────┘   │
│                                 │
│ [SHARE — opens Android chooser] │
└─────────────────────────────────┘
```

## Dev Prompt
```
Create ExportPreviewFragment in Kotlin.
- ViewModel: ExportViewModel with:
    * reportText: StateFlow<String> — generated summary string
    * generateReport() — queries all required data and assembles formatted string

- reportText generation (all in ViewModel, no business logic in Fragment):
    ```kotlin
    fun buildReportString(data: GroupReportData): String {
        return buildString {
            appendLine("📊 Mahila-Shakti Unnati — Group Report")
            appendLine("Date: ${formatDate(LocalDate.now())}")
            appendLine()
            appendLine("👥 Total Members: ${data.totalMembers} (Active: ${data.activeMembers})")
            appendLine("💰 Group Capital: ₹${formatAmount(data.groupCapital)}")
            appendLine("📅 This Week Savings: ₹${formatAmount(data.weekSavings)}")
            appendLine("   Paid: ${data.paidCount} | Pending: ${data.pendingCount}")
            appendLine()
            appendLine("🏦 Active Loans: ${data.activeLoanCount}")
            appendLine("   Outstanding Principal: ₹${formatAmount(data.totalOutstanding)}")
            appendLine("   Accrued Interest: ₹${formatAmount(data.totalInterest)}")
            appendLine()
            if (data.pendingMembers.isNotEmpty()) {
                appendLine("⚠️  Pending Dues: ${data.pendingMembers.joinToString(", ")}")
            }
            appendLine()
            appendLine("Generated by Mahila-Shakti Unnati App")
        }
    }
    ```

- Preview card: ScrollView with TextView displaying reportText in monospace font (JetBrains Mono 13sp), cream background (#FEFCE8), padding 16dp, rounded card.

- Share button row: 3 icon+label buttons for WhatsApp, Email, SMS. Tapping any:
    ```kotlin
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, reportText)
    }
    startActivity(Intent.createChooser(intent, "Share via"))
    ```
- Top bar share icon (📤) does the same.
- SUCCESS CRITERIA: exported text must be human-readable plain string — no markdown symbols, no HTML.
```

---

# S-14: Settings Screen

## Layout

```
┌─────────────────────────────────┐
│ ←  Settings                     │
├─────────────────────────────────┤
│ GROUP SETTINGS                  │
│ ┌───────────────────────────┐  │
│ │ Group Name                │  │
│ │ [Mahila Shakti SHG]       │  │
│ ├───────────────────────────┤  │
│ │ Weekly Savings Amount     │  │
│ │ ₹ 100                     │  │
│ ├───────────────────────────┤  │
│ │ Default Interest Rate     │  │
│ │ 2 % / month               │  │
│ ├───────────────────────────┤  │
│ │ Loan Eligibility Multiplier│  │
│ │ 3x savings                │  │
│ └───────────────────────────┘  │
│                                 │
│ SECURITY                        │
│ ┌───────────────────────────┐  │
│ │ Change Admin PIN          │  │
│ │ [chevron →]               │  │
│ └───────────────────────────┘  │
│                                 │
│ APPEARANCE                      │
│ ┌───────────────────────────┐  │
│ │ Dark Mode        [toggle] │  │
│ ├───────────────────────────┤  │
│ │ Language         [Hindi ▼]│  │
│ └───────────────────────────┘  │
│                                 │
│ DATA                            │
│ ┌───────────────────────────┐  │
│ │ Backup to Google Drive    │  │
│ │ [chevron →]               │  │
│ ├───────────────────────────┤  │
│ │ Export Full Report        │  │
│ └───────────────────────────┘  │
│                                 │
│ App Version: 1.0.0              │
└─────────────────────────────────┘
```

## Dev Prompt
```
Create SettingsFragment in Kotlin using PreferenceFragmentCompat OR custom RecyclerView layout.

- Use DataStore (Preferences DataStore) for all settings persistence.
  Keys: GROUP_NAME, WEEKLY_AMOUNT, DEFAULT_RATE, LOAN_MULTIPLIER, ADMIN_PIN, DARK_MODE, LANGUAGE.

- Group Settings section:
    * Group Name: EditTextPreference, shown in Dashboard top bar subtitle.
    * Weekly Savings Amount: EditTextPreference (numeric), default ₹100. Pre-fills SavingsEntryFragment.
    * Default Interest Rate: EditTextPreference (decimal), default 2.0. Pre-fills NewLoanFragment.
    * Loan Eligibility Multiplier: ListPreference — [1x, 2x, 3x, 4x, 5x], default 3x.

- Security section:
    * "Change Admin PIN" → navigate to ChangePinFragment (enter old PIN, then set new 4-digit PIN twice for confirmation).

- Appearance section:
    * Dark Mode: SwitchPreference → AppCompatDelegate.setDefaultNightMode().
    * Language: ListPreference [English, हिन्दी, ಕನ್ನಡ, தமிழ், తెలుగు].
      On change → recreate() Activity with new locale context.

- Data section:
    * "Backup to Google Drive" (good-to-have): navigate to GoogleDriveBackupFragment.
    * "Export Full Report" → navigate to ExportPreviewFragment.

- All changes take effect immediately (DataStore flow → ViewModels observe and react).
```

---

# S-15: GenAI Financial Advisor (Good-to-Have)

## Layout

```
┌─────────────────────────────────┐
│ ←  Ask Unnati 🤖                │
├─────────────────────────────────┤
│ ┌─────────────────────────┐    │
│ │ 🌸 Unnati               │    │
│ │ Hi! Ask me anything     │    │
│ │ about your group's      │    │
│ │ finances.               │    │
│ └─────────────────────────┘    │
│ [bot bubble — violet, left]     │
│                                 │
│        ┌─────────────────────┐ │
│        │ मेरा loan कब भरेगा? │ │
│        └─────────────────────┘ │
│        [user bubble — right]    │
│                                 │
│ ┌─────────────────────────┐    │
│ │ 🌸 Unnati               │    │
│ │ Meena ji, your loan of  │    │
│ │ ₹2,000 was issued on    │    │
│ │ Dec 1. At 2%/month for  │    │
│ │ 6 months, total         │    │
│ │ repayable is ₹2,240.    │    │
│ │ ₹1,650 is still due.    │    │
│ └─────────────────────────┘    │
│                                 │
│ Suggested questions:            │
│ [How much interest did I pay?]  │
│ [When is my next payment?]      │
│ [What is my credit score?]      │
│                                 │
├─────────────────────────────────┤
│ ┌─────────────────────┐ [Send] │
│ │ Ask something...    │        │
└─────────────────────────────────┘
```

## Dev Prompt
```
Create GenAIAdvisorFragment in Kotlin.
- ViewModel: GenAIViewModel with:
    * messages: MutableStateFlow<List<ChatMessage>> — list of user + bot messages
    * isLoading: StateFlow<Boolean>
    * sendMessage(query: String) — builds context + calls Gemini API

- Context building (before API call):
    * Query Room for: group capital, member's savings total, active loan details.
    * Build a system prompt string:
      "You are Unnati, a friendly financial advisor for an Indian rural women's SHG.
       Group Capital: ₹X. Member savings: ₹Y. Active loan: principal ₹Z, rate R%, outstanding ₹W.
       Answer in the language the user asks. Be simple and encouraging."

- Gemini API call (generative-ai-android SDK):
    * Model: gemini-pro or gemini-1.5-flash.
    * Stream response tokens as they arrive (use generateContentStream).
    * Append bot message token-by-token for typewriter effect.

- Offline fallback: if no network, show static FAQ chips:
    * "How is interest calculated?"
    * "What is my loan eligibility?"
    * "What is a credit score?"
    Tapping shows pre-written local answers.

- Chat RecyclerView:
    * User bubbles: right-aligned, accent color background.
    * Bot bubbles: left-aligned, white card with violet bot icon.
    * Loading indicator: animated 3-dot bubble while waiting for response.

- Suggested question chips below the last bot message.
- PII note: never send member names or phone numbers to Gemini API — use anonymized labels.
```

---

# S-16: Analytics Dashboard (Good-to-Have)

## Layout

```
┌─────────────────────────────────┐
│ ←  Analytics                    │
├─────────────────────────────────┤
│ Savings Trend (last 8 weeks)    │
│ ┌─────────────────────────┐    │
│ │  ▄ ▄ █ █ ▄ █ █ █       │    │
│ │  [BarChart, green bars] │    │
│ │  W1 W2 W3 W4 W5 W6 W7 W8│   │
│ └─────────────────────────┘    │
│                                 │
│ Loan Utilization                │
│ ┌─────────────────────────┐    │
│ │      [PieChart]         │    │
│ │   Repaid 23% |          │    │
│ │   Outstanding 77%       │    │
│ └─────────────────────────┘    │
│                                 │
│ Member Compliance Rate          │
│ ┌─────────────────────────┐    │
│ │ Priya    ████████ 90%   │    │
│ │ Meena    ██████░░ 70%   │    │
│ │ Savita   █████░░░ 60%   │    │
│ └─────────────────────────┘    │
│ [horizontal bar chart per member│
└─────────────────────────────────┘
```

## Dev Prompt
```
Create AnalyticsFragment in Kotlin using MPAndroidChart library.

Add to build.gradle: implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'

- ViewModel: AnalyticsViewModel with:
    * weeklySavingsData: StateFlow<List<BarEntry>> — last 8 weeks, SUM of paid savings per week
    * loanUtilizationData: StateFlow<List<PieEntry>> — [repaid%, outstanding%]
    * memberComplianceData: StateFlow<List<Pair<String, Float>>> — member name, compliance %

- Savings Trend BarChart:
    * BarChart from MPAndroidChart.
    * Green bars (#16A34A), axis labels = week numbers.
    * No legend, no grid lines, animate bar growth on entry (animateY 500ms).
    * X-axis: "W1", "W2", ... "W8".
    * Y-axis: rupee amounts.

- Loan Utilization PieChart:
    * PieChart with hole (donut style, 50% hole radius).
    * Colors: [#16A34A repaid, #DC2626 outstanding].
    * Center text: total loan amount.
    * Entry label: percentage.

- Member Compliance HorizontalBarChart:
    * One bar per active member.
    * Color: green if ≥70%, amber if ≥50%, red if <50%.
    * Y-axis: member names.
    * X-axis: 0–100%.

- Each chart in a MaterialCardView (elevation 2dp, radius 16dp).
- Section headers: "Savings Trend (Last 8 Weeks)" etc., Poppins SemiBold 15sp.
- All data loaded from Room via ViewModel — no mock data.
```

---

## Cross-Screen Navigation Map

```
S-01 Splash
  └─auto──► S-02 PIN Login
              ├─admin──► S-03 Dashboard
              │             ├──► S-04 Member List ──► S-05 Add/Edit Member
              │             │         └──► S-06 Member Profile
              │             │                   ├──► S-07 Contribution History
              │             │                   └──► S-11 Loan Detail
              │             ├──► S-08 Savings Entry (week selection + per-member toggle)
              │             ├──► S-09 Loan List
              │             │         ├──► S-10 New Loan Form
              │             │         └──► S-11 Loan Detail
              │             │                   └──► S-12 Repayment Entry (bottom sheet)
              │             ├──► S-13 Export Preview ──► Android Share Intent
              │             ├──► S-14 Settings
              │             ├──► S-15 GenAI Advisor (optional)
              │             └──► S-16 Analytics (optional)
              └─member──► S-06 Member Profile (own only, read-only)
                              └──► S-07 Contribution History (own)
```

---

## State Management Summary

```
ViewModel / StateFlow Map:

DashboardViewModel
  ├── groupCapital: Double        ← SUM SavingsEntry(PAID)
  ├── pendingDuesCount: Int       ← COUNT SavingsEntry(PENDING, currentWeek)
  ├── activeLoansCount: Int       ← COUNT Loan(ACTIVE)
  └── recentActivity: List        ← UNION last 5 events

SavingsEntryViewModel
  ├── memberEntries: List         ← active members + their entry for selectedWeek
  ├── weeklyAmount: Double        ← from Settings DataStore
  └── saveAllEntries()            ← Room transaction (ACID)

NewLoanViewModel
  ├── isBlocked: Boolean          ← existingLoan != null (BR-01)
  ├── eligibilityAmount: Double   ← (memberSavings/groupTotal)*capital*multiplier
  └── estimatedInterest: Double   ← (P*R*T)/100 reactive

LoanDetailViewModel
  ├── accruedInterest: Double     ← (P*R*daysElapsed/365)
  ├── outstanding: Double         ← principal + interest - totalRepaid
  └── repayments: List            ← ordered by date DESC
```

---

## Room DB Schema

```sql
CREATE TABLE Member (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  phone TEXT NOT NULL,
  photoUri TEXT,
  joinDate INTEGER NOT NULL,
  role TEXT NOT NULL DEFAULT 'MEMBER',
  isActive INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE SavingsEntry (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  memberId INTEGER NOT NULL REFERENCES Member(id),
  weekStartDate INTEGER NOT NULL,
  amount REAL NOT NULL,
  status TEXT NOT NULL,       -- 'PAID' | 'PENDING'
  recordedAt INTEGER NOT NULL,
  UNIQUE(memberId, weekStartDate)  -- one entry per member per week
);

CREATE TABLE Loan (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  memberId INTEGER NOT NULL REFERENCES Member(id),
  principal REAL NOT NULL,
  interestRate REAL NOT NULL,
  startDate INTEGER NOT NULL,
  durationMonths INTEGER NOT NULL,
  status TEXT NOT NULL DEFAULT 'ACTIVE'  -- 'ACTIVE' | 'CLOSED'
);

CREATE TABLE Repayment (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  loanId INTEGER NOT NULL REFERENCES Loan(id),
  amount REAL NOT NULL,
  paidDate INTEGER NOT NULL,
  note TEXT
);
```

---

## Key Formulas (Kotlin)

```kotlin
// Simple Interest
fun simpleInterest(principal: Double, ratePerMonth: Double, months: Int): Double =
    (principal * ratePerMonth * months) / 100.0

// Loan Eligibility
fun loanEligibility(
    memberSavings: Double,
    groupTotalSavings: Double,
    groupCapital: Double,
    multiplier: Double = 3.0
): Double = if (groupTotalSavings == 0.0) 0.0
            else (memberSavings / groupTotalSavings) * groupCapital * multiplier

// Credit Score (0–100)
fun creditScore(paidWeeks: Int, totalWeeks: Int): Int =
    if (totalWeeks == 0) 0
    else ((paidWeeks.toDouble() / totalWeeks) * 100).toInt().coerceIn(0, 100)

// Outstanding Balance
fun outstandingBalance(
    principal: Double,
    ratePerMonth: Double,
    monthsElapsed: Int,
    totalRepaid: Double
): Double = principal + simpleInterest(principal, ratePerMonth, monthsElapsed) - totalRepaid
```

---

*App Flows & Views Spec — Mahila-Shakti Unnati v1.0*  
*MindMatrix VTU Internship Program — Project #83*
