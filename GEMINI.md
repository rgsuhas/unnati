# GEMINI.md — Gemini AI Integration Context
## Mahila-Shakti Unnati — GenAI Financial Advisor

This file is the complete context document for the AI (Claude or Gemini) to understand the app, its domain, and how to generate correct responses when implementing or operating the GenAI feature.

---

## What This App Is

**Mahila-Shakti Unnati** is an offline-first Android app that acts as a digital ledger ("Digital Accountant") for Indian rural women's **Self-Help Groups (SHGs)**.

SHGs are community savings groups where women:
- Pool weekly savings (typically ₹50–₹200 per week per member)
- Accumulate group capital over time
- Issue small loans to members from the group capital
- Charge simple interest on loans (typically 2%/month)
- Track repayments manually (this app digitizes that)

The app replaces handwritten registers with a tamper-resistant Room DB on Android.

**Users are semi-literate rural women in India.** The interface must be simple, icon-driven, and available in Hindi, Kannada, Tamil, Telugu, and English.

---

## Gemini Feature: "Ask Unnati" In-App Financial Advisor

### Purpose
A conversational chatbot that answers finance questions from the group leader (admin) or members in natural language — including regional Indian languages.

### Persona
- **Name:** Unnati (उन्नति — meaning "progress/growth" in Hindi)
- **Tone:** Warm, encouraging, simple. Like a helpful village bank elder.
- **Language:** Responds in the same language the user asks in (Hindi/Kannada/Tamil/Telugu/English).
- **Never:** use jargon, complex financial terms, or condescending tone.
- **Always:** Give rupee amounts in Indian format (₹X,XX,XXX), use simple arithmetic explanations.

---

## System Prompt Template

Use this as the system instruction when calling the Gemini API. Populate `{variables}` from Room DB at runtime.

```
You are Unnati, a friendly and simple financial advisor for a rural Indian women's Self-Help Group (SHG) called "{groupName}".

Your role is to help the group leader and members understand their savings, loans, and finances in very simple language. 
Always be warm, encouraging, and respectful. Use simple words. Avoid complex financial jargon.
Always respond in the same language the user is writing in (Hindi, Kannada, Tamil, Telugu, or English).

Current group financial context (use this to answer questions):
- Group Name: {groupName}
- Total Group Capital (savings): ₹{groupCapital}
- Total Active Members: {totalMembers}
- Weekly savings amount per member: ₹{weeklyAmount}
- Default loan interest rate: {defaultRate}% per month
- Active loans: {activeLoanCount} loans, ₹{totalOutstanding} outstanding

If the user is asking about their own account, here is their data:
- Member name: [Member] (do not reveal actual name to others)
- Their total savings: ₹{memberSavings}
- Their credit score: {creditScore}/100
- Active loan: {loanStatus} (principal ₹{loanPrincipal}, outstanding ₹{loanOutstanding}, rate {loanRate}%/month)

Rules you must follow:
1. Never reveal one member's financial data to another member.
2. Never promise specific loan approvals — only show eligibility numbers.
3. If you don't know something, say "I don't have that information right now."
4. Keep answers under 3 short paragraphs unless asked for detail.
5. For interest calculations, always show the formula step-by-step.
6. Never ask for Aadhaar numbers, bank account numbers, or passwords.
7. If asked about government schemes (PM Jan Dhan, MUDRA loans, etc.), give a brief explanation and suggest they contact their local bank or NABARD office.
```

---

## Context Data Builder (Kotlin)

Build this data class from Room before each Gemini API call:

```kotlin
data class GeminiContext(
    val groupName: String,
    val groupCapital: Double,
    val totalMembers: Int,
    val weeklyAmount: Double,
    val defaultRate: Double,
    val activeLoanCount: Int,
    val totalOutstanding: Double,
    // member-specific (null if admin querying group-level)
    val memberSavings: Double? = null,
    val creditScore: Int? = null,
    val loanStatus: String? = null,
    val loanPrincipal: Double? = null,
    val loanOutstanding: Double? = null,
    val loanRate: Double? = null
)

fun buildSystemPrompt(ctx: GeminiContext): String = """
    You are Unnati, a friendly financial advisor for the SHG "${ctx.groupName}".
    Be warm, simple, and always respond in the user's language.
    
    Group Capital: ₹${ctx.groupCapital.formatINR()}
    Members: ${ctx.totalMembers}
    Weekly savings: ₹${ctx.weeklyAmount.formatINR()}
    Active loans: ${ctx.activeLoanCount}, Outstanding: ₹${ctx.totalOutstanding.formatINR()}
    ${if (ctx.memberSavings != null) "Member savings: ₹${ctx.memberSavings.formatINR()}" else ""}
    ${if (ctx.creditScore != null) "Credit score: ${ctx.creditScore}/100" else ""}
    ${if (ctx.loanOutstanding != null) "Loan outstanding: ₹${ctx.loanOutstanding.formatINR()}" else ""}
    
    Never reveal PII. Never promise loan approvals. Keep answers simple and brief.
""".trimIndent()
```

---

## API Call Implementation (Kotlin)

```kotlin
// In GenAIViewModel.kt
private val generativeModel = GenerativeModel(
    modelName = "gemini-1.5-flash",   // fast + free tier friendly
    apiKey = BuildConfig.GEMINI_API_KEY,
    generationConfig = generationConfig {
        temperature = 0.4f         // low = more factual, less creative
        topK = 32
        topP = 0.95f
        maxOutputTokens = 512      // keep responses short for mobile
    },
    safetySettings = listOf(
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.LOW_AND_ABOVE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
    )
)

fun sendMessage(userQuery: String, context: GeminiContext) {
    viewModelScope.launch {
        _isLoading.value = true
        
        // Build conversation with system context prepended
        val systemPrompt = buildSystemPrompt(context)
        val fullPrompt = "$systemPrompt\n\nUser: $userQuery"
        
        try {
            // Streaming response — typewriter effect in UI
            generativeModel.generateContentStream(fullPrompt)
                .collect { chunk ->
                    val token = chunk.text ?: ""
                    appendBotToken(token)  // updates last bot message in _messages
                }
        } catch (e: Exception) {
            showOfflineFallback()
        } finally {
            _isLoading.value = false
        }
    }
}
```

---

## API Key Setup

1. Get API key from [Google AI Studio](https://aistudio.google.com/app/apikey) — free tier available.
2. Add to `local.properties` (never commit this file):
   ```
   GEMINI_API_KEY=your_key_here
   ```
3. Expose via `BuildConfig` in `build.gradle`:
   ```kotlin
   buildConfigField("String", "GEMINI_API_KEY", "\"${properties["GEMINI_API_KEY"]}\"")
   ```
4. Add `local.properties` to `.gitignore`.

---

## Offline Fallback (when no internet)

When Gemini API call fails (no network), show these static FAQ cards instead of an error:

```kotlin
val offlineFAQs = listOf(
    FAQ(
        question = "ब्याज कैसे calculate होता है? / How is interest calculated?",
        answer = "Simple Interest = (Principal × Rate × Time) / 100\n" +
                 "Example: ₹2,000 loan at 2%/month for 6 months\n" +
                 "= (2000 × 2 × 6) / 100 = ₹240 interest\n" +
                 "Total to repay = ₹2,240"
    ),
    FAQ(
        question = "मैं कितना loan ले सकती हूँ? / How much loan am I eligible for?",
        answer = "Your loan eligibility = (Your savings ÷ Group total savings) × Group capital × 3\n" +
                 "The more you save regularly, the more you can borrow!"
    ),
    FAQ(
        question = "Credit score क्या है? / What is a credit score?",
        answer = "Your credit score (0–100) shows how consistently you've paid your weekly savings.\n" +
                 "100 = perfect record. Higher score = more loan eligibility in the future.\n" +
                 "Pay on time every week to improve your score!"
    ),
    FAQ(
        question = "Group capital क्या है? / What is group capital?",
        answer = "Group capital is the total amount all members have saved together (Paid entries only).\n" +
                 "This money is used to give loans to members. The more everyone saves, the more loans are possible!"
    )
)
```

---

## Privacy Rules (Mandatory)

| Rule | Implementation |
|---|---|
| No PII to Gemini API | Build anonymized context — use labels like "the member" not actual names or phone numbers |
| No transaction IDs | Never send raw loan IDs or entry IDs in prompts |
| Member isolation | Admin context includes group data. Member context includes only their own data. Enforced in `buildSystemPrompt()` based on user role. |
| API key never in source | Always via `local.properties` + `BuildConfig`, never hardcoded |
| Offline-first | Gemini feature degrades gracefully to static FAQ — app never breaks if API unavailable |

---

## Suggested Questions (Shown in Chat UI)

Pre-populate as chips below the bot's first message:

**English:**
- "How much can I borrow?"
- "What's my credit score?"
- "How is interest calculated?"
- "When should I repay my loan?"
- "What is group capital?"

**Hindi (हिन्दी):**
- "मैं कितना loan ले सकती हूँ?"
- "मेरा credit score क्या है?"
- "ब्याज कैसे calculate होता है?"
- "Group capital कितना है?"

**Kannada (ಕನ್ನಡ):**
- "ನಾನು ಎಷ್ಟು ಸಾಲ ತೆಗೆದುಕೊಳ್ಳಬಹುದು?"
- "ಬಡ್ಡಿ ಹೇಗೆ ಲೆಕ್ಕ ಹಾಕುತ್ತಾರೆ?"

---

## Model Choice Guide

| Scenario | Model | Reason |
|---|---|---|
| Default / production | `gemini-1.5-flash` | Fast, cheap, good multilingual support |
| Higher quality answers | `gemini-1.5-pro` | More accurate, higher cost |
| On-device (offline AI) | ML Kit (on-device) | No internet needed, limited capability |
| Testing / dev | `gemini-1.0-pro` | Stable, well-documented |

Use `gemini-1.5-flash` as the default. Allow override via Settings for advanced users.

---

## Integration Checklist

- [ ] `GEMINI_API_KEY` added to `local.properties`
- [ ] `local.properties` in `.gitignore`
- [ ] `BuildConfig.GEMINI_API_KEY` wired in `build.gradle`
- [ ] `generativeai` SDK dependency added
- [ ] `GeminiContext` data class built from Room queries (anonymized)
- [ ] System prompt uses `buildSystemPrompt(ctx)` — no hardcoded facts
- [ ] Streaming response implemented (typewriter effect)
- [ ] Offline fallback FAQ cards implemented
- [ ] Safety settings configured (block harmful content)
- [ ] Member role check: admin sees group data, member sees own data only
- [ ] API errors caught and handled gracefully (no crash)

---

## Domain Glossary (for Gemini context)

| Term | Meaning |
|---|---|
| SHG | Self-Help Group — a community savings collective, usually 10–20 women |
| Group Capital | Total accumulated savings from all members (paid entries only) |
| Weekly Savings | Fixed amount each member contributes every week (e.g., ₹100) |
| Loan Eligibility | Max loan amount a member can receive, based on their savings ratio |
| Simple Interest | Interest = (P × R × T) / 100 — no compounding |
| Credit Score | 0–100 score based on savings payment consistency |
| Pending Due | A member who has not paid their weekly savings for the current week |
| Repayment | Partial or full payment towards an active loan |
| NABARD | National Bank for Agriculture and Rural Development — supports SHGs in India |
| MUDRA | Govt. loan scheme for micro-enterprises — often relevant to SHG members |
