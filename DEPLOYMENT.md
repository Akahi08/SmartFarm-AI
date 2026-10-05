# SmartFarm AI: Deployment Guide & Go-Live Checklist

This guide outlines deployment procedures for transitioning SmartFarm AI from development to production.

---

## 1. Environment Configuration

Populate `.env` with production credentials:
```bash
NODE_ENV=production
APP_BASE_URL=https://your-domain.ng
FIREBASE_PROJECT_ID=smartfarm-production
PAYSTACK_MODE=live
PAYSTACK_SECRET_KEY=sk_live_...
PAYSTACK_PUBLIC_KEY=pk_live_...
PAYSTACK_WEBHOOK_BASE_URL=https://api.your-domain.ng/webhooks/paystack
DISEASE_ADAPTER=multimodal # or custom
DISEASE_CONFIDENCE_THRESHOLD=0.70
WAIVER_WINDOW_DAYS=14
PRICE_STALE_DAYS=7
WHATSAPP_NUMBER_PLACEHOLDER=+234803...
```

---

## 2. Pre-Launch Compliance & Go-Live Checklist

Before accepting real payments or onboarding farmers in Kogi State:

1. **Escrow & Financial Regulations**:
   - Confirm with Nigerian banking/CBN counsel that holding buyer payments for 50/50 milestone release complies with NDIC/CBN escrow licensing rules, or route settlements directly through a licensed PSP/bank partner.
2. **Paystack Split Accounts**:
   - Provision verified Paystack subaccounts for approved input suppliers to enable direct automated settlement splits.
3. **Pesticide / NAFDAC Verification**:
   - Require agro-dealers to provide certified NAFDAC registration certificates for every chemical formulation listed.
4. **Real Agricultural Expert Onboarding**:
   - Update consultant profile with verified names, certifications, and operational contact channels.
5. **Disease Model Calibration**:
   - Train and validate an on-device TensorFlow Lite or cloud vision model using Nigerian field-collected cassava images under varied daylight conditions.
6. **Demo Data Purge**:
   - Execute the "Reset Demo Data" action in the Admin Console to purge sample cases before onboarding real farmers.
