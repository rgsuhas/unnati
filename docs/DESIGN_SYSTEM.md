# Mahila-Shakti Unnati — Design System Reference

Extracted from 4 HTML reference screens. These tokens drive the glassmorphism overhaul.

## Color Palette (Material 3 — Light)

| Token                   | Hex       | Usage                                    |
|-------------------------|-----------|------------------------------------------|
| `primary`               | `#630ED4` | Brand purple — FAB, active nav, headings |
| `primary-container`     | `#7C3AED` | Gradient hero card end, filled buttons   |
| `on-primary`            | `#FFFFFF` |                                          |
| `secondary-container`   | `#FEA619` | Accent/badge — "Due" status, chips       |
| `on-secondary-container`| `#684000` |                                          |
| `surface`               | `#FBF8FF` | App background, card base                |
| `surface-container`     | `#EEECF8` | Slightly elevated surfaces, preview card |
| `surface-container-high`| `#E9E7F3` | Higher elevation cards                   |
| `surface-variant`       | `#E3E1ED` | Dividers, borders                        |
| `on-surface`            | `#1A1B23` | Primary text                             |
| `on-surface-variant`    | `#4A4455` | Secondary text, placeholders             |
| `outline`               | `#4A4455` (variant) | Input borders, icons           |
| `error`                 | `#BA1A1A` | Error states                             |
| `error-container`       | `#FFDAD6` | Error card background                    |
| `background`            | `#FBF8FF` | Page background                          |
| `inverse-surface`       | `#2F3039` | Dark snackbars                           |

**Body background:** `#F5F3FF` (Level 1 Lavender Mist — 1 level above surface)

## Typography

Two font families, all defined as Tailwind custom text scales:

| Token        | Family    | Size | Line-H | Weight | Android Equiv |
|--------------|-----------|------|--------|--------|---------------|
| `display-lg` | Poppins   | 32sp | 40sp   | 700    | `displayLarge`  |
| `headline-md`| Poppins   | 24sp | 32sp   | 600    | `headlineMedium`|
| `title-sm`   | Poppins   | 20sp | 28sp   | 600    | `titleSmall`    |
| `label-xl`   | Noto Sans | 18sp | 20sp   | 600    | `labelLarge`    |
| `body-lg`    | Noto Sans | 18sp | 26sp   | 400    | `bodyLarge`     |
| `body-md`    | Noto Sans | 16sp | 24sp   | 400    | `bodyMedium`    |
| `label-md`   | Noto Sans | 14sp | 18sp   | 500    | `labelMedium`   |

Letter spacing: `display-lg` -0.02em, `headline-md` -0.01em, `label-md` +0.02em

## Spacing Scale

| Token         | Value | Use case                          |
|---------------|-------|-----------------------------------|
| `unit`        | 8dp   | Base unit — icon padding, gaps    |
| `stack-sm`    | 12dp  | Tight vertical stacks             |
| `gutter`      | 16dp  | Horizontal padding inside cards   |
| `stack-md`    | 24dp  | Section vertical spacing          |
| `margin-page` | 24dp  | Page horizontal padding           |
| `stack-lg`    | 40dp  | Large section gaps                |
| `touch-target-min` | 56dp | FAB / circular icon buttons |

## Glass Morphism

The core visual language of the app:

```css
/* glass-card class */
background-color: rgba(255, 255, 255, 0.70);
backdrop-filter: blur(12px);
border: 1.5px solid rgba(255, 255, 255, 0.40);
```

**In Compose:**
```kotlin
Modifier
  .background(Color.White.copy(alpha = 0.70f), RoundedCornerShape(24.dp))
  // backdrop-filter not natively supported — use .blur() or solid background with low alpha
  // Approximate with: surface.copy(alpha = 0.85f) + elevation shadow
```

**Practical approach for Android Compose (no WebKit blur):**  
Use `Color.White.copy(alpha = 0.92f)` as card background on a colored page background — the contrast gives the glass illusion without true backdrop-filter.

## Shadows / Elevation

| Usage              | Shadow value                                    |
|--------------------|-------------------------------------------------|
| Cards (default)    | `0px 12px 32px rgba(124, 58, 237, 0.10)`        |
| Bottom nav         | `0px -4px 24px rgba(124, 58, 237, 0.05)`        |
| Top app bar        | subtle bottom border `rgba(255,255,255,0.40)`   |

**In Compose:** `CardDefaults.cardElevation(defaultElevation = 2.dp)` approximates this.

## Border Radii

| Component          | Radius  |
|--------------------|---------|
| Cards, list items  | 24dp    |
| Buttons (pill)     | 50dp (full) |
| Chips, badges      | 50dp (full) |
| Input fields       | 16dp    |
| Bottom nav corners | 24dp (top-only) |
| FAB                | 50dp    |

## Component Patterns

### Top App Bar
```
bg: rgba(255,255,255,0.70) backdrop-blur
border-bottom: 1.5px rgba(255,255,255,0.40)
height: 64dp
title: Poppins headline-md, color=primary
nav icon: 40dp circle, tint=primary
actions: 40dp circle, tint=on-surface-variant
```

### Member List Row
```
glass-card, rounded-24dp, padding 16dp
[Avatar 48dp circle] [Name(title-sm) + Phone(body-md,on-surface-variant)] [Status badge]
Status "Paid": bg=primary/10, text=primary, pill
Status "Due": bg=secondary-container/20, text=secondary, pill
Status badge shows ₹amount
```

### Dashboard Hero Card
```
bg: gradient surface → primary-container/90 (vertical)
rounded-24dp, padding 24dp
Decorative blur orb: 128dp circle bg=secondary-container/30 blur-xl, position -top-10 -right-10
Title: display-lg Poppins, white
Subtitle: body-md Noto, white/70
```

### Stat Card (2-column grid)
```
glass-card, rounded-24dp, padding 16dp
Icon: material-symbols, 24dp, primary
Label: label-md, on-surface-variant
Value: headline-md Poppins, on-surface
```

### Quick Action Button (Dashboard)
```
56dp circle, bg=primary-container/10 (10% alpha)
Icon: material-symbols, 28dp, primary
Label below: label-md, on-surface-variant
```

### Bottom Navigation
```
bg: rgba(255,255,255,0.70) backdrop-blur
border-top: 1.5px rgba(255,255,255,0.40)
shadow: 0px -4px 24px rgba(124,58,237,0.05)
height: 80dp
border-radius top-start=24dp top-end=24dp
Active item: filled icon, text=primary, indicator pill
Inactive item: outlined icon, text=on-surface-variant
```

### Savings Member Row
```
glass-card, rounded-24dp, padding 16dp
Avatar initials circle (48dp, primary/10 bg, primary text)
Name + "₹150/week" subtitle
Toggle buttons: PAID (green bordered pill) / PENDING (amber bordered pill)
Selected: filled with bg color; unselected: bordered outline
```

### Input Fields (Glass)
```
OutlinedTextField
shape: RoundedCornerShape(16.dp)
border color: primary when focused, outline when idle
bg: surface-container-lowest (white)
label: label-md, on-surface-variant
leading icon: tint=primary
```

### Primary CTA Button
```
full-width, height 56dp, rounded-[28dp] (pill)
bg: primary (#630ED4)
text: label-xl Noto Sans 18sp/600, white
```

### Status Badges
```
Paid / Active: bg=primary/10, text=primary, rounded-full
Due / Pending: bg=secondary-container/20, text=secondary, rounded-full  
Blocked: bg=error/10, text=error, rounded-full
```

## Implementation Checklist

| Screen | Status |
|--------|--------|
| S-03 Dashboard | Needs overhaul |
| S-04 Member List | Needs overhaul |
| S-05 Add/Edit Member | Needs overhaul |
| S-06 Member Profile | Needs overhaul |
| S-08 Weekly Savings Entry | Needs overhaul |
| S-09 Loan List | Needs overhaul |
| S-10 New Loan | Needs overhaul |
| S-11 Loan Detail | Needs overhaul |
| S-12 Export Preview | Minor touch |
| S-14 Settings | Minor touch |
| PinScreen | Needs overhaul |
| SplashScreen | Already good |
