# SmartFarm AI (Kogi State Cassava MVP)

SmartFarm AI is a mobile-first agricultural application built for Nigerian cassava farmers, starting in Kogi State (Dekina, Anyigba, Ankpa, Lokoja, and Kabba). It provides free cassava leaf disease screening, expert-authored localized treatment plans with fee waiver logic, verified farm inputs with Chemical vs Organic comparisons, local market prices with staleness tracking, and a secure 50/50 milestone produce marketplace (strictly NOT escrow).

---

## 🌾 Core Features

1. **Farmer Home & 6 Large Action Tiles**:
   - Time-of-day greeting ("Good morning, Musa"), local community location, verified trust strip, and meeting the agricultural expert.
   - Six prominent actions: Check My Cassava, Get Treatment Help, Buy Farm Inputs, Sell My Cassava, Check Cassava Market, Talk to an Agricultural Expert.

2. **Cassava Disease Screening**:
   - 4-step photo guidance: Daylight, flat leaf, fill frame, close/far shots.
   - Consent management: Mandatory image storage consent and optional farm location toggle.
   - `DemoDiseaseAdapter`: Evaluates sample leaf symptoms (Cassava Mosaic Disease, Bacterial Blight, Brown Streak, Green Mite, Healthy Leaf, and Low Confidence < 0.70).
   - Low confidence / blurry image handling without guessing disease names.
   - Prominent `DEMO RESULT: not a real diagnosis` banner.

3. **Localized Treatment Plans (₦500 or FREE)**:
   - Authored/approved by agricultural extension consultant.
   - **Waiver Logic**: Waived when farmer orders recommended inputs through SmartFarm AI within 14 days. Reversion on order cancellation.
   - Cultural sanitation, organic bio-pesticides, and chemical treatments with strictly verified label dosages (no LLM-invented pesticide dosages).

4. **Verified Farm Inputs & Chemical vs Organic Comparison**:
   - Certified cassava stems (TME 419, TMS 98/0505), NPK fertilizers, and biologicals.
   - "Verified Supplier" badge with non-government disclaimer.
   - Side-by-side Chemical vs Organic comparison matrix (benefits, safety, harvest waiting intervals).
   - "Check and Pay" checkout with clear line items (no hidden supplier commission shown to farmers).

5. **Cassava Market Prices**:
   - Weekly survey data across Anyigba, Lokoja, Kabba, and Ankpa markets.
   - Form, price per unit, date, source label, and amber "Old price: check before you sell" badge for prices older than 7 days.

6. **Produce Marketplace & Milestone Payment Workflow**:
   - Never called "escrow"; uses a transparent 50/50 milestone payment workflow.
   - Buyer service charge: 3% (< ₦500,000) or 2.5% (≥ ₦500,000).
   - Farmer bears payment-processing fee (capped at ₦2,000), shown to buyers as an info note and previewed in farmer net payout.
   - 50% Milestone 1 released on verified physical pickup; 50% Milestone 2 released on buyer delivery confirmation or 48hr dispute expiry.

7. **Consultation Booking (₦3,000)**:
   - Paystack test checkout with WhatsApp deep link and direct phone call handoff.

8. **Role Switcher & Role Dashboards**:
   - One-click top-bar role switcher between **Farmer (Musa Ibrahim)**, **Buyer (Alhaji Garba Garri Mill)**, **Supplier (Kogi Agro-Allied)**, **Consultant (Extension Specialist)**, and **Admin Console**.
   - Admin console: Revenue metrics, listing & product approvals, test milestone release console, commission ledger, and audit log.

9. **Direct Web Link & Android App Links (Zero-Storage for Farmers)**:
   - Recognizes that smallholder farmers often hesitate to download apps due to phone storage, data cost, or older Android devices.
   - **Direct Web Access URL**: `https://ais-pre-ogle2dewyqnmnqjsmoznzh-43248366371.europe-west2.run.app` (or custom domain `https://smartfarm.ng`).
   - Runs directly in Chrome, Opera Mini, or any mobile browser with zero installation needed.
   - **Android App Links & Deep Linking**: Configured in `AndroidManifest.xml` with `autoVerify="true"` so clicking the web link automatically opens the native app if installed, or seamlessly loads in the web browser if not installed.
   - In-app **"Share Link on WhatsApp"** and **"Copy Web Link"** hub on the Farmer Home screen and Top Bar.

---

## 🚀 Running the Project

- **Platform**: Android (Kotlin, Jetpack Compose, Material Design 3, Room Database with KSP).
- **Compilation**: Run `compile_applet` tool or `./gradlew assembleDebug`.
- **Unit Tests**: Run `./gradlew testDebugUnitTest` to verify money arithmetic, commission boundaries, waiver logic, and order state machine.

---

## 🔑 Demo Logins & Test Accounts

Switch roles anytime from the **"Switch Role"** button in the top navigation bar:
- **Farmer**: Musa Ibrahim, Dekina LGA, Kogi State (`farmer_musa`)
- **Buyer**: Alhaji Garba Garri Processors, Lokoja, Kogi State (`buyer_garba`)
- **Supplier**: Kogi Agro-Allied Inputs (Verified), Lokoja (`supplier_kogi`)
- **Consultant**: Agricultural Extension Specialist (`consultant_01`)
- **Admin**: SmartFarm Administrator (`admin_01`)
