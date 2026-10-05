# SmartFarm AI: Implementation Report

## 1. Implemented and Tested

- **Architecture & Domain Rules**:
  - `Money.kt`: Full integer-kobo money representation protecting against floating-point errors.
  - `CommissionCalculator.kt`: Input order supplier commission (5% < ₦500,000; 2.5% ≥ ₦500,000), Produce order buyer service charge (3% < ₦500,000; 2.5% ≥ ₦500,000), seller payment-processing fee estimate (1.5% capped at ₦2,000), and 50/50 milestone splits.
  - `TreatmentFeeCalculator.kt`: ₦500 plan logic, free waiver when buying recommended inputs within 14 days, and automatic waiver reversion upon order cancellation.
  - `ProduceOrderStateMachine.kt`: Enforced state transitions (`ORDER_CONFIRMED` -> `PAYMENT_RECEIVED_TEST` -> `LOGISTICS_ASSIGNED` -> `PICKED_UP` -> `MILESTONE_1_ELIGIBLE` -> `IN_TRANSIT` -> `DELIVERED` -> `BUYER_CONFIRMED` -> `FINAL_MILESTONE_ELIGIBLE` -> `COMPLETED`, with `DISPUTED` and `CANCELLED`).
  - Unit tests covering boundary values for commission rates, money operations, waiver rules, and state machine transitions.

- **Farmer User Interface (Jetpack Compose, M3)**:
  - Time-of-day greeting ("Good morning, Musa"), community location display, trust strip, and "TEST MODE" badge.
  - Six primary action tiles (Check My Cassava, Get Treatment Help, Buy Farm Inputs, Sell My Cassava, Check Cassava Market, Talk to an Agricultural Expert).
  - Quick action links (My Orders, My Farm, Terms & Help).
  - Expert profile card with "Verified SmartFarm AI Consultant" badge and popover explaining internal verification.

- **Disease Screening Flow**:
  - 4 photo tips with numbered visual guide.
  - Mandatory image consent checkbox and optional location sharing toggle.
  - `DemoDiseaseAdapter` with labeled diagnoses (Cassava Mosaic Disease, Bacterial Blight, Brown Streak, Green Mite, Healthy Leaf, and Blurry/Uncertain).
  - Low confidence handling (< 0.70) without naming false diseases.
  - Clear next steps and direct CTA to expert treatment plan or live consultation.

- **Treatment Plan (₦500 or Free)**:
  - Localized plan with immediate farm sanitation steps, organic/biological option, and chemical option.
  - Strict safety warnings: dosages only populated from registered product labels.
  - Automatic waiver when input order is placed and confirmed.

- **Farm Inputs & Suppliers**:
  - Certified cassava stem varieties (TME 419, TMS 98/0505), NPK fertilizers, and bio-pesticides.
  - "Verified Supplier" badge with non-government disclaimer.
  - Chemical vs Organic/Biological side-by-side comparison matrix.
  - Shopping cart with "Check and Pay" modal displaying clear subtotal, delivery quote, and total (supplier commission hidden from farmer).

- **Marketplace & 50/50 Milestone Workflow**:
  - Cassava selling form with quantities, harvest dates, and logistics requests.
  - Farmer payout preview showing net amount and 50% Milestone 1 / 50% Milestone 2 splits.
  - Buyer marketplace with filters and "Check and Pay" breakdown.
  - Delivery receipt confirmation and 48-hour dispute window.

- **Admin & Role Portals**:
  - One-click top-bar role switcher.
  - Supplier dashboard: catalogue, stock toggle, incoming order progression, and settlement statements.
  - Consultant workspace: disease cases, treatment plan reviewer, and consultations with privacy-protected farmer identifiers.
  - Admin console: revenue metrics, listing approvals, milestone release console, commission ledger, audit logs, and demo data reset.

---

## 2. Mocked / Simulated (and How Each Is Labelled)

- **Disease Screening Model**:
  - Handled by `DemoDiseaseAdapter`. Prominently labelled in the UI with a persistent red banner: `DEMO RESULT: not a real diagnosis. Sample cassava model active.`
- **Payments (Paystack)**:
  - Running in simulated test mode. Labelled with an amber banner across the app: `TEST MODE: Paystack & settlements simulated. No real money charged.`
- **Milestone Settlements (50% / 50%)**:
  - Released via simulated admin ledger in the Admin Console. Labelled as `TEST SETTLEMENT: no real money moved.`
- **Market Prices**:
  - Benchmark sample prices tagged with a `DEMO DATA` badge and source attribution (`Entered by SmartFarm AI team`).
- **Communication Channels**:
  - WhatsApp button triggers deep link (`https://wa.me/2348000000000`) and call button triggers system dialer (`tel:+2348000000000`).

---

## 3. Not Implemented / Needs Human Input Prior to Launch

- **Real Agricultural Consultant Identity**: Placeholder used (`[Expert name to be added]`). Requires actual consultant name, credentials, and business phone number.
- **NAFDAC / Pesticide Regulatory Database Integration**: Product dosages are strictly hardcoded from sample registered labels. Real API sync with NAFDAC/NASC registry needed.
- **Paystack Live Keys & Subaccount Split**: Requires live Nigerian Paystack merchant account credentials and subaccount verification for registered agro-dealers.
- **Legal Review**: Legal text and privacy policies are labeled `Draft: needs legal review` and must be vetted by a Nigerian corporate/agricultural attorney.
- **Custom Trained Vision AI Model**: Real cassava CNN model (e.g. MobileNet trained on Cassava leaf dataset) to replace the `DemoDiseaseAdapter`.
- **Translations (i18n)**: English is currently implemented; translations for Nigerian Pidgin, Hausa, Yoruba, and Nupe designed for next phase.
