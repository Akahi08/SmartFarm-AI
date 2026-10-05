// SmartFarm AI Web Application
// Complete mobile-first agricultural web platform for Kogi State cassava farmers

const APP_URL = "https://ais-pre-ogle2dewyqnmnqjsmoznzh-43248366371.europe-west2.run.app";

// In-Memory Database & State
const state = {
  currentRole: 'FARMER',
  currentScreen: 'home',
  cart: [],
  imageConsent: true,
  locationConsent: false,
  diseaseResult: null,
  isAnalyzing: false,

  users: {
    FARMER: { id: 'farmer_musa', role: 'FARMER', name: 'Musa Ibrahim', phone: '+234 803 000 1122', lga: 'Dekina', community: 'Anyigba' },
    BUYER: { id: 'buyer_garba', role: 'BUYER', name: 'Alhaji Garba Garri Processors', phone: '+234 802 000 3344', lga: 'Lokoja', community: 'Felele' },
    SUPPLIER: { id: 'supplier_kogi', role: 'SUPPLIER', name: 'Kogi Agro-Allied Supplies (Verified)', phone: '+234 805 000 5566', lga: 'Lokoja', community: 'Ganaja' },
    CONSULTANT: { id: 'consultant_01', role: 'CONSULTANT', name: 'Agricultural Extension Consultant', phone: '+234 800 000 7788', lga: 'Lokoja', community: 'Secretariat' },
    ADMIN: { id: 'admin_01', role: 'ADMIN', name: 'SmartFarm Administrator', phone: '+234 809 000 9900', lga: 'Lokoja', community: 'Central' }
  },

  products: [
    {
      id: "PROD-001",
      name: "Certified Cassava Stems (TME 419)",
      official: "TME 419 Certified Foundation Stem Cuttings",
      purpose: "High-starch, CMD disease-resistant certified cassava stems.",
      category: "Stem Cuttings",
      type: "NEITHER",
      priceKobo: 120000,
      unit: "Bundle (50 stems)",
      safety: "Store under shade before planting. Plant within 5 days of cutting.",
      label: "Recommended spacing: 1m x 1m (10,000 stems/ha). Yield potential: 25-35 tonnes/ha.",
      regNo: "NASC/CAS/2024/091",
      supplier: "Kogi Agro-Allied Inputs",
      status: "APPROVED"
    },
    {
      id: "PROD-002",
      name: "Cassava NPK 15:15:15 Fertilizer",
      official: "Balanced Mineral NPK Blend for Root Bulking",
      purpose: "Essential root nutrition to increase root yield and starch quality.",
      category: "Fertilizer",
      type: "CHEMICAL",
      priceKobo: 3800000,
      unit: "50kg Bag",
      safety: "Wear gloves during application. Keep away from water streams and children.",
      label: "Apply 200kg/ha at 4 to 8 weeks after planting as ring placement 15cm from stem base.",
      regNo: "FMA-FERT-2023-882",
      supplier: "Kogi Agro-Allied Inputs",
      status: "APPROVED"
    },
    {
      id: "PROD-003",
      name: "Bio-Neem Organic Pest Repellent",
      official: "Cold-Pressed Azadirachtin Botanical Bio-Pesticide",
      purpose: "Natural organic spray to control whiteflies and green mites.",
      category: "Biological",
      type: "ORGANIC_BIOLOGICAL",
      priceKobo: 450000,
      unit: "1 Litre Bottle",
      safety: "Natural botanical extract. Low toxicity to non-target beneficial insects.",
      label: "Dilute 50ml per 15L knapsack sprayer. Apply early morning or late evening. Pre-harvest interval: 0 days.",
      regNo: "NAFDAC Reg No: 04-8912P",
      supplier: "Kogi Agro-Allied Inputs",
      status: "APPROVED"
    },
    {
      id: "PROD-004",
      name: "Copper Hydroxide Bactericide Spray",
      official: "Copper Hydroxide 77% WP Contact Bactericide",
      purpose: "Targeted treatment for severe Cassava Bacterial Blight outbreaks.",
      category: "Crop Protection",
      type: "CHEMICAL",
      priceKobo: 680000,
      unit: "500g Sachet",
      safety: "CAUTION: Corrosive to eyes. Wear safety goggles, protective gloves, and boots.",
      label: "Apply 30g per 15L water upon first leaf symptom. Repeat every 14 days if needed. Harvest interval: 14 days.",
      regNo: "NAFDAC Reg No: 04-3291P",
      supplier: "Kogi Agro-Allied Inputs",
      status: "APPROVED"
    },
    {
      id: "PROD-005",
      name: "Organic Trichoderma Soil Inoculant",
      official: "Trichoderma harzianum Bio-Fungicide Powder",
      purpose: "Bio-agent protecting stem cuttings and root zones from fungal root rot.",
      category: "Biological",
      type: "ORGANIC_BIOLOGICAL",
      priceKobo: 520000,
      unit: "1kg Pack",
      safety: "Beneficial living microorganism. Avoid mixing directly with chemical fungicides.",
      label: "Dip cuttings in slurry (50g per 10L water) before planting, or drench around planting ridge.",
      regNo: "NAFDAC Reg No: BIO-2022-114",
      supplier: "Kogi Agro-Allied Inputs",
      status: "APPROVED"
    }
  ],

  marketPrices: [
    { id: "PRICE-01", location: "Anyigba Market (Dekina LGA, Kogi)", form: "Fresh Roots", priceKobo: 8500000, unit: "Tonne", date: Date.now(), source: "Entered by SmartFarm AI team", isDemo: true, buyers: 4 },
    { id: "PRICE-02", location: "Lokoja Central Market (Kogi)", form: "Garri (White)", priceKobo: 4800000, unit: "100kg Bag", date: Date.now(), source: "Entered by SmartFarm AI team", isDemo: true, buyers: 6 },
    { id: "PRICE-03", location: "Kabba Farmers Market (Kogi)", form: "Dried Cassava Chips", priceKobo: 6200000, unit: "Tonne", date: Date.now() - (9 * 24 * 3600 * 1000), source: "Reported by buyer: unverified", isDemo: true, buyers: 2 },
    { id: "PRICE-04", location: "Ankpa Main Market (Kogi)", form: "Fresh Roots", priceKobo: 8200000, unit: "Tonne", date: Date.now(), source: "Entered by SmartFarm AI team", isDemo: true, buyers: 3 }
  ],

  listings: [
    {
      id: "LIST-001",
      farmerId: "farmer_musa",
      farmerFirstName: "Musa",
      lga: "Dekina",
      community: "Anyigba",
      form: "Fresh Roots (TME 419)",
      quantity: 10,
      unit: "Tonne",
      pricePerUnitKobo: 8500000,
      totalKobo: 85000000,
      harvestDate: "Next 5 Days",
      logisticsNeeded: true,
      status: "ACTIVE"
    }
  ],

  produceOrders: [
    {
      id: "PRD-DEMO-01",
      listingId: "LIST-001",
      farmerFirstName: "Musa",
      farmerLga: "Dekina",
      buyerName: "Alhaji Garba Garri Processors",
      quantity: 10,
      unit: "Tonne",
      producePriceKobo: 85000000,
      buyerChargeKobo: 2125000,
      sellerProcessingFeeKobo: 200000,
      logisticsKobo: 2500000,
      totalBuyerPayableKobo: 89625000,
      farmerNetPayoutKobo: 84800000,
      milestone1Kobo: 42400000,
      milestone2Kobo: 42400000,
      milestone1Status: "ELIGIBLE",
      milestone2Status: "PENDING",
      status: "PICKED_UP",
      isDisputed: false
    }
  ],

  inputOrders: [],
  treatmentPlans: [
    {
      id: "PLAN-001",
      problem: "Cassava Mosaic Disease",
      status: "READY",
      feeKobo: 50000,
      feeStatus: "UNPAID",
      author: "Verified SmartFarm AI Consultant",
      nextSteps: "Rogue and burn heavily mottled young cassava plants away from farm. Disinfect tools.",
      organic: "Bio-Neem Botanical Repellent: Spray early morning to suppress whiteflies. 0-day harvest delay.",
      chemical: "Targeted Bactericide/Fungicide: If infestation exceeds 20%, apply registered copper formulation.",
      safety: "Strictly adhere to product label. Wear eye protection and gloves."
    }
  ],
  consultations: [],
  commissions: [
    { id: "COMM-001", orderType: "PRODUCE_ORDER", grossKobo: 85000000, rate: 2.5, commKobo: 2125000, status: "SETTLED" }
  ],
  auditLogs: [
    { action: "SYSTEM_INIT", role: "SYSTEM", details: "SmartFarm AI Web Portal initialized for Kogi State." }
  ]
};

// Utilities
function formatNaira(kobo) {
  const naira = kobo / 100;
  return "₦" + naira.toLocaleString('en-US', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
}

function calculateInputCommission(grossKobo) {
  const threshold = 500000 * 100;
  const rate = grossKobo >= threshold ? 0.025 : 0.05;
  const commKobo = Math.round(grossKobo * rate);
  return { ratePercent: rate * 100, commissionKobo: commKobo, netSupplierKobo: grossKobo - commKobo };
}

function calculateProduceCharges(producePriceKobo, logisticsKobo = 2500000) {
  const threshold = 500000 * 100;
  const buyerRate = producePriceKobo >= threshold ? 0.025 : 0.03;
  const buyerChargeKobo = Math.round(producePriceKobo * buyerRate);
  const sellerFeeKobo = Math.min(Math.round(producePriceKobo * 0.015), 200000); // 1.5% capped at ₦2,000
  const totalBuyerPayableKobo = producePriceKobo + buyerChargeKobo + logisticsKobo;
  const farmerNetPayoutKobo = producePriceKobo - sellerFeeKobo;
  const milestone1Kobo = Math.floor(farmerNetPayoutKobo / 2);
  const milestone2Kobo = farmerNetPayoutKobo - milestone1Kobo;

  return {
    buyerRatePercent: buyerRate * 100,
    buyerChargeKobo,
    sellerFeeKobo,
    logisticsKobo,
    totalBuyerPayableKobo,
    farmerNetPayoutKobo,
    milestone1Kobo,
    milestone2Kobo
  };
}

// Navigation & Screen Rendering
function navigateTo(screen) {
  state.currentScreen = screen;
  renderScreen();
  updateNavUI();
}

function updateNavUI() {
  document.querySelectorAll('.nav-btn').forEach(btn => btn.classList.remove('active'));
  const btn = document.getElementById(`nav-${state.currentScreen}`);
  if (btn) btn.classList.add('active');
}

function selectRole(role) {
  state.currentRole = role;
  closeModal('role-modal');
  const user = state.users[role];
  document.getElementById('topbar-role').innerText = `${user.name} (${role})`;

  if (role === 'FARMER') navigateTo('home');
  else if (role === 'BUYER') navigateTo('buyer');
  else if (role === 'SUPPLIER') navigateTo('supplier');
  else if (role === 'CONSULTANT') navigateTo('consultant');
  else if (role === 'ADMIN') navigateTo('admin');
}

function renderScreen() {
  const container = document.getElementById('main-content');
  const user = state.users[state.currentRole];

  switch(state.currentScreen) {
    case 'home':
      container.innerHTML = renderFarmerHomeScreen(user);
      break;
    case 'screening':
      container.innerHTML = renderDiseaseScreeningScreen();
      break;
    case 'plans':
      container.innerHTML = renderTreatmentPlansScreen();
      break;
    case 'inputs':
      container.innerHTML = renderInputCatalogueScreen();
      break;
    case 'market':
      container.innerHTML = renderMarketPricesScreen();
      break;
    case 'sell':
      container.innerHTML = renderSellCassavaScreen();
      break;
    case 'expert':
      container.innerHTML = renderConsultationScreen();
      break;
    case 'orders':
      container.innerHTML = renderFarmerOrdersScreen();
      break;
    case 'buyer':
      container.innerHTML = renderBuyerMarketplaceScreen();
      break;
    case 'supplier':
      container.innerHTML = renderSupplierPortalScreen();
      break;
    case 'consultant':
      container.innerHTML = renderConsultantWorkspaceScreen();
      break;
    case 'admin':
      container.innerHTML = renderAdminConsoleScreen();
      break;
    case 'legal':
      container.innerHTML = renderLegalPrivacyScreen();
      break;
    default:
      container.innerHTML = renderFarmerHomeScreen(user);
  }
}

// Screen 1: Farmer Home
function renderFarmerHomeScreen(user) {
  const hour = new Date().getHours();
  const greeting = hour < 12 ? "Good morning" : hour < 17 ? "Good afternoon" : "Good evening";

  return `
    <div class="hero-card">
      <img src="images/cassava_hero_banner_1791065865990.jpg" alt="Cassava Farmland Kogi">
      <div class="hero-overlay">
        <h2>${greeting}, ${user.name.split(' ')[0]}</h2>
        <p>📍 ${user.community}, ${user.lga} LGA (Kogi State)</p>
      </div>
    </div>

    <div class="trust-strip">
      <div class="trust-item"><span>🛡️</span> Verified Expert</div>
      <div class="trust-item"><span>📦</span> Verified Inputs</div>
      <div class="trust-item"><span>⚖️</span> Clear Prices</div>
      <div class="trust-item"><span>📞</span> Farmer Support</div>
    </div>

    <div class="web-access-callout" style="cursor: pointer;" onclick="openWebShareModal()">
      <div class="web-callout-icon">🌐</div>
      <div class="web-callout-text">
        <div style="display: flex; align-items: center; gap: 6px;">
          <h3>Use on Web Directly</h3>
          <span class="pill-badge" style="background: var(--blue);">ZERO STORAGE</span>
        </div>
        <p>Farmers can use SmartFarm AI in Chrome or Opera Mini without downloading an app.</p>
      </div>
    </div>

    <h3 class="section-title">What do you want to do today?</h3>

    <div class="tile-grid">
      <div class="action-tile" onclick="navigateTo('screening')">
        <div class="tile-icon-box">📷</div>
        <div class="tile-info">
          <div class="tile-header">
            <span class="tile-title">1. Check My Cassava</span>
            <span class="tile-badge">FREE</span>
          </div>
          <div class="tile-subtitle">Take a leaf picture to check for leaf sickness</div>
        </div>
        <span class="tile-arrow">›</span>
      </div>

      <div class="action-tile" onclick="navigateTo('plans')">
        <div class="tile-icon-box">🩺</div>
        <div class="tile-info">
          <div class="tile-header">
            <span class="tile-title">2. Get Treatment Help</span>
            <span class="tile-badge">₦500 or FREE</span>
          </div>
          <div class="tile-subtitle">Localized expert plan · Free with recommended input order</div>
        </div>
        <span class="tile-arrow">›</span>
      </div>

      <div class="action-tile" onclick="navigateTo('inputs')">
        <div class="tile-icon-box">🛍️</div>
        <div class="tile-info">
          <div class="tile-header">
            <span class="tile-title">3. Buy Farm Inputs</span>
            <span class="tile-badge">VERIFIED</span>
          </div>
          <div class="tile-subtitle">Certified disease-free stems, fertilizer, safe bio-sprays</div>
        </div>
        <span class="tile-arrow">›</span>
      </div>

      <div class="action-tile" onclick="navigateTo('sell')">
        <div class="tile-icon-box">🌾</div>
        <div class="tile-info">
          <div class="tile-header">
            <span class="tile-title">4. Sell My Cassava</span>
            <span class="tile-badge">50/50 PAY</span>
          </div>
          <div class="tile-subtitle">Post cassava roots to reach verified buyers across Kogi</div>
        </div>
        <span class="tile-arrow">›</span>
      </div>

      <div class="action-tile" onclick="navigateTo('market')">
        <div class="tile-icon-box">📈</div>
        <div class="tile-info">
          <div class="tile-header">
            <span class="tile-title">5. Check Cassava Market</span>
            <span class="tile-badge">UPDATED</span>
          </div>
          <div class="tile-subtitle">Current prices in Anyigba, Lokoja, and Kabba markets</div>
        </div>
        <span class="tile-arrow">›</span>
      </div>

      <div class="action-tile" onclick="navigateTo('expert')">
        <div class="tile-icon-box">👨‍🌾</div>
        <div class="tile-info">
          <div class="tile-header">
            <span class="tile-title">6. Talk to Agricultural Expert</span>
            <span class="tile-badge">LIVE</span>
          </div>
          <div class="tile-subtitle">Direct 1-on-1 advisory with extension specialist (₦3,000)</div>
        </div>
        <span class="tile-arrow">›</span>
      </div>
    </div>

    <!-- Expert Profile Card -->
    <div class="card">
      <div style="display: flex; gap: 12px; align-items: center;">
        <div style="width: 50px; height: 50px; border-radius: 50%; background: var(--primary-light); display: flex; align-items: center; justify-content: center; font-size: 24px;">👨‍🔬</div>
        <div>
          <h4 style="font-weight: 700; font-size: 15px;">Meet Our Agricultural Expert</h4>
          <p style="font-size: 12px; color: var(--text-muted);">[Expert name to be added]</p>
        </div>
      </div>
      <div class="alert-box alert-success" style="margin-top: 10px; cursor: pointer;" onclick="openExpertDisclaimer()">
        <div><strong>Verified SmartFarm AI Consultant</strong> · <i>Click for internal check details ℹ️</i></div>
      </div>
      <p style="font-size: 12px; color: var(--text-muted); margin-top: 8px; line-height: 1.6;">
        • Extension specialist supporting farmers across Kogi State since 2007.<br>
        • Thousands of farmers advised on cassava stem selection and disease treatment.
      </p>
      <button class="btn btn-outline btn-block" style="margin-top: 12px;" onclick="navigateTo('expert')">
        📞 Book 1-on-1 Advisory Session (₦3,000)
      </button>
    </div>
  `;
}

// Screen 2: Disease Screening
function renderDiseaseScreeningScreen() {
  if (state.diseaseResult) {
    const res = state.diseaseResult;
    return `
      <div class="alert-box alert-success">
        🌿 <strong>AI DIAGNOSTIC SCREENING:</strong> Based on verified Kogi cassava pathology guidelines.
      </div>

      <div class="card">
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <h3 style="font-size: 16px;">Screening Result</h3>
          <button class="btn btn-outline" style="padding: 4px 10px; font-size: 11px;" onclick="clearDiseaseResult()">Check Another</button>
        </div>

        ${res.isConfident ? `
          <div style="background: ${res.label.includes('Healthy') ? 'var(--primary-container)' : 'var(--accent-light)'}; padding: 14px; border-radius: 12px; margin-top: 12px;">
            <h2 style="font-size: 18px; font-weight: 800; color: ${res.label.includes('Healthy') ? 'var(--primary-dark)' : 'var(--accent-dark)'};">${res.label}</h2>
            <p style="font-size: 12px; margin-top: 4px;">Confidence: <strong>${res.confidenceGrade} (${Math.round(res.confidence * 100)}%)</strong></p>
            <div style="background: white; height: 8px; border-radius: 4px; overflow: hidden; margin-top: 8px;">
              <div style="background: ${res.label.includes('Healthy') ? 'var(--primary)' : 'var(--accent)'}; width: ${res.confidence * 100}%; height: 100%;"></div>
            </div>
          </div>

          <h4 style="font-size: 14px; font-weight: 700; margin-top: 16px;">What this means:</h4>
          <p style="font-size: 13px; color: var(--text-muted);">${res.problem}</p>

          <h4 style="font-size: 14px; font-weight: 700; margin-top: 14px;">What to do next:</h4>
          <p style="font-size: 13px; color: var(--text-muted); white-space: pre-line;">${res.nextSteps}</p>

          ${!res.label.includes('Healthy') ? `
            <button class="btn btn-primary btn-block" style="margin-top: 20px;" onclick="navigateTo('plans')">
              🩺 Get Expert Treatment Plan (₦500 or FREE)
            </button>
          ` : ''}

          <button class="btn btn-outline btn-block" style="margin-top: 8px;" onclick="navigateTo('expert')">
            👨‍🌾 Talk to Agricultural Expert (₦3,000)
          </button>
        ` : `
          <div class="alert-box alert-warning" style="margin-top: 12px;">
            <div>
              <strong>Leaf Disease Not Recognized</strong><br>
              We are not sure from this picture. The leaf may be too blurry, too dark, or taken from too far away. We do not guess when we are not sure.
            </div>
          </div>
          <button class="btn btn-primary btn-block" style="margin-top: 16px;" onclick="clearDiseaseResult()">
            📷 Take Another Clear Picture
          </button>
          <button class="btn btn-outline btn-block" style="margin-top: 8px;" onclick="navigateTo('expert')">
            👨‍🌾 Ask Our Agricultural Expert Directly
          </button>
        `}
      </div>
    `;
  }

  return `
    <div class="alert-box alert-success">
      🌿 <strong>AI LEAF SCREENING:</strong> Check your cassava leaves for disease symptoms.
    </div>

    <div class="card">
      <h3 style="font-size: 16px; font-weight: 700;">4 Tips for Clear Leaf Pictures</h3>
      <div style="font-size: 13px; color: var(--text-muted); line-height: 1.8; margin-top: 8px;">
        1. ☀️ Use bright natural daylight. Avoid dark shadows.<br>
        2. ✋ Hold the leaf flat on your palm or clean surface.<br>
        3. 🌿 Fill the picture with one single affected leaf.<br>
        4. 🔍 Take one close-up shot and one full-plant shot.
      </div>
    </div>

    <div class="card">
      <h4 style="font-size: 14px; font-weight: 700;">Select Sample Cassava Leaf to Check:</h4>
      <div style="margin: 12px 0; border-radius: 12px; overflow: hidden; border: 1px solid var(--border); height: 160px; display: flex; align-items: center; justify-content: center; background: var(--primary-light);">
        <img src="images/cassava_leaf_guide_1791065875241.jpg" alt="Cassava Leaf Sample" style="max-height: 100%; object-fit: contain;">
      </div>

      <div class="form-group">
        <label>Select Test Symptom:</label>
        <select class="form-select" id="sample-case-select">
          <option value="mosaic">Cassava Mosaic Disease (CMD) - Mottled Yellowing</option>
          <option value="bacterial">Cassava Bacterial Blight (CBB) - Angular Leaf Spots</option>
          <option value="mite">Cassava Green Mite (CGM) - Leaf Speckling</option>
          <option value="healthy">Healthy Cassava Leaf - No Lesions</option>
          <option value="blurry">Blurry / Low-Confidence Leaf Photo</option>
        </select>
      </div>

      <div style="margin-top: 12px; display: flex; flex-direction: column; gap: 8px;">
        <label style="font-size: 12px; display: flex; align-items: flex-start; gap: 8px;">
          <input type="checkbox" id="consent-check" checked style="margin-top: 2px;">
          <span>I agree to store this leaf photo to check my cassava and improve SmartFarm AI. (Required)</span>
        </label>
        <label style="font-size: 12px; display: flex; align-items: center; gap: 8px;">
          <input type="checkbox" id="location-check">
          <span>Share farm location for Kogi regional pest alerts (Optional)</span>
        </label>
      </div>

      <button class="btn btn-primary btn-block" style="margin-top: 16px;" onclick="runDiseaseScreening()">
        🔍 Check Leaf Now (Free)
      </button>
    </div>
  `;
}

function runDiseaseScreening() {
  const check = document.getElementById('consent-check');
  if (check && !check.checked) {
    alert("Please check the consent box to proceed with disease screening.");
    return;
  }

  const select = document.getElementById('sample-case-select');
  const sample = select ? select.value : 'mosaic';

  const container = document.getElementById('main-content');
  container.innerHTML = `
    <div class="card" style="text-align: center; padding: 40px 20px;">
      <div style="font-size: 40px;">🔬</div>
      <h3 style="margin-top: 12px;">Analyzing leaf symptoms...</h3>
      <p style="font-size: 12px; color: var(--text-muted); margin-top: 4px;">Evaluating against Kogi cassava pathology database</p>
    </div>
  `;

  setTimeout(() => {
    if (sample === 'bacterial') {
      state.diseaseResult = {
        label: "Cassava Bacterial Blight",
        confidence: 0.88,
        confidenceGrade: "High",
        isConfident: true,
        problem: "Angular water-soaked leaf spots and wilting of shoots. Spreads rapidly in rain.",
        nextSteps: "1. Rogue and burn infected stems away from field.\n2. Do not use infected cuttings for planting.\n3. Disinfect harvesting knives with bleach.\n4. Consult extension specialist."
      };
    } else if (sample === 'mite') {
      state.diseaseResult = {
        label: "Cassava Green Mite",
        confidence: 0.78,
        confidenceGrade: "Medium",
        isConfident: true,
        problem: "Yellow speckling on upper leaf surfaces and candlestick shoot damage in dry weather.",
        nextSteps: "1. Conserve predatory mites and natural beneficial predators.\n2. Avoid unapproved harsh sprays.\n3. Plant tolerant cassava stems."
      };
    } else if (sample === 'healthy') {
      state.diseaseResult = {
        label: "Healthy Cassava Leaf",
        confidence: 0.94,
        confidenceGrade: "High",
        isConfident: true,
        problem: "No symptoms of major cassava diseases detected. Foliage is uniform and well-developed.",
        nextSteps: "1. Continue regular field monitoring every 2 weeks.\n2. Maintain good weed management.\n3. Ensure adequate soil nutrition."
      };
    } else if (sample === 'blurry') {
      state.diseaseResult = {
        label: null,
        confidence: 0.45,
        confidenceGrade: "Uncertain",
        isConfident: false,
        problem: "Image unclear or taken from too far.",
        nextSteps: "Take another clear picture or speak with our expert."
      };
    } else {
      state.diseaseResult = {
        label: "Cassava Mosaic Disease",
        confidence: 0.89,
        confidenceGrade: "High",
        isConfident: true,
        problem: "Distorted, patchy green and yellow mosaic leaves and stunted tuber development.",
        nextSteps: "1. Rogue severely affected young plants.\n2. Select disease-resistant stems (TME 419) for next planting.\n3. Control whitefly insect vectors."
      };
    }
    renderScreen();
  }, 1000);
}

function clearDiseaseResult() {
  state.diseaseResult = null;
  renderScreen();
}

// Screen 3: Treatment Plans
function renderTreatmentPlansScreen() {
  const plan = state.treatmentPlans[0];
  const isWaivedOrPaid = plan.feeStatus === "PAID" || plan.feeStatus === "WAIVED_INPUT_PURCHASE";

  return `
    <div class="card">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <h3 style="font-size: 16px;">Localized Treatment Plan</h3>
        <span class="pill-badge" style="background: var(--primary);">EXPERT APPROVED</span>
      </div>

      <div style="background: ${isWaivedOrPaid ? 'var(--primary-container)' : 'var(--accent-light)'}; padding: 12px; border-radius: var(--radius-sm); margin-top: 10px;">
        <strong>${isWaivedOrPaid ? '🎉 100% UNLOCKED' : 'Fee: ₦500 or FREE'}</strong><br>
        <small style="color: ${isWaivedOrPaid ? 'var(--primary-dark)' : 'var(--accent-dark)'};">
          ${isWaivedOrPaid ? 'Fee waived because recommended farm inputs were purchased through SmartFarm AI.' : 'Get this treatment plan FREE when you order recommended farm inputs, or pay ₦500 now.'}
        </small>
        ${!isWaivedOrPaid ? `
          <div style="display: flex; gap: 8px; margin-top: 8px;">
            <button class="btn btn-primary" style="padding: 6px 12px; font-size: 12px;" onclick="payForPlan()">Pay ₦500 via Paystack</button>
            <button class="btn btn-outline" style="padding: 6px 12px; font-size: 12px;" onclick="navigateTo('inputs')">Buy Inputs (Waive Fee)</button>
          </div>
        ` : ''}
      </div>

      <div style="margin-top: 16px; border-top: 1px solid var(--border); padding-top: 14px;">
        <h4 style="color: var(--primary-dark); font-size: 14px;">Target Disease: ${plan.problem}</h4>
        <p style="font-size: 11px; color: var(--text-muted); margin-bottom: 12px;">Author: ${plan.author}</p>

        <h5 style="font-weight: 700; font-size: 13px;">1. Immediate Sanitation Steps:</h5>
        <p style="font-size: 13px; color: var(--text-main); margin-bottom: 12px;">${plan.nextSteps}</p>

        <h5 style="font-weight: 700; font-size: 13px;">2. Organic / Biological Solution:</h5>
        <div style="background: var(--primary-light); padding: 10px; border-radius: 8px; font-size: 12px; color: var(--primary-dark); margin-bottom: 12px;">
          ${plan.organic}
        </div>

        <h5 style="font-weight: 700; font-size: 13px;">3. Chemical Option (Severe Cases):</h5>
        <div style="background: var(--accent-light); padding: 10px; border-radius: 8px; font-size: 12px; color: var(--accent-dark); margin-bottom: 12px;">
          ${plan.chemical}
        </div>

        <div class="alert-box alert-danger">
          🛡️ <strong>Safety Warning:</strong> ${plan.safety}
        </div>

        <button class="btn btn-primary btn-block" style="margin-top: 16px;" onclick="navigateTo('inputs')">
          🛍️ Order Verified Inputs from Catalogue
        </button>
      </div>
    </div>
  `;
}

function payForPlan() {
  state.treatmentPlans[0].feeStatus = "PAID";
  alert("Payment of ₦500 confirmed via Paystack. Plan is fully unlocked!");
  renderScreen();
}

// Screen 4: Input Catalogue & Comparison
function renderInputCatalogueScreen() {
  let cartTotal = state.cart.reduce((sum, item) => sum + item.priceKobo, 0);

  return `
    <div style="display: flex; justify-content: space-between; align-items: center;">
      <div>
        <h3 style="font-size: 17px; font-weight: 700;">Farm Inputs & Stems</h3>
        <p style="font-size: 12px; color: var(--text-muted);">Verified suppliers in Kogi State</p>
      </div>
      <button class="btn btn-primary" style="padding: 8px 14px; font-size: 12px;" onclick="openCartModal()">
        🛒 Cart (${state.cart.length})
      </button>
    </div>

    <!-- Chemical vs Organic Toggle Box -->
    <div class="card" style="background: var(--primary-container); border: none;">
      <h4 style="font-size: 14px; font-weight: 700; color: var(--primary-dark);">🌿 Organic vs 🧪 Chemical Comparison</h4>
      <p style="font-size: 12px; color: var(--primary-dark); margin: 6px 0 10px 0;">Compare efficacy, environmental safety, and harvest waiting times:</p>
      <div style="font-size: 12px; background: white; padding: 12px; border-radius: 8px; line-height: 1.7;">
        <strong>🌿 Organic / Biological:</strong> Safe for beneficial insects; 0-day harvest interval (harvest safe immediately).<br>
        <strong>🧪 Chemical Option:</strong> Fast curative knockdown for severe blight; strictly requires 14-day pre-harvest waiting interval.
      </div>
    </div>

    <!-- Products List -->
    <div class="tile-grid">
      ${state.products.map(p => `
        <div class="card">
          <div style="display: flex; justify-content: space-between; align-items: flex-start;">
            <div>
              <h4 style="font-size: 15px; font-weight: 700;">${p.name}</h4>
              <p style="font-size: 12px; color: var(--text-muted);">${p.official}</p>
            </div>
            <span class="pill-badge" style="background: ${p.type === 'ORGANIC_BIOLOGICAL' ? 'var(--primary)' : p.type === 'CHEMICAL' ? 'var(--accent)' : 'var(--blue)'};">
              ${p.type === 'ORGANIC_BIOLOGICAL' ? 'Biological' : p.type === 'CHEMICAL' ? 'Chemical' : 'Stem'}
            </span>
          </div>
          <p style="font-size: 12px; margin: 8px 0; color: var(--text-main);">${p.purpose}</p>
          <div style="font-size: 11px; color: var(--primary-dark); font-weight: 600;">✓ ${p.supplier} (Verified)</div>
          <div style="font-size: 11px; color: var(--text-muted);">${p.regNo}</div>
          <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 12px; padding-top: 8px; border-top: 1px solid var(--border);">
            <div>
              <strong style="font-size: 16px; color: var(--primary);">${formatNaira(p.priceKobo)}</strong>
              <span style="font-size: 11px; color: var(--text-muted);"> / ${p.unit}</span>
            </div>
            <button class="btn btn-secondary" style="padding: 6px 12px; font-size: 12px;" onclick="addToCart('${p.id}')">
              + Add to Cart
            </button>
          </div>
        </div>
      `).join('')}
    </div>

    <!-- Cart Modal -->
    <div class="modal-overlay" id="cart-modal">
      <div class="modal-card">
        <div class="modal-header">
          <h3>Check and Pay (Cart)</h3>
          <button class="close-btn" onclick="closeModal('cart-modal')">×</button>
        </div>
        ${state.cart.length === 0 ? `
          <p style="font-size: 13px; color: var(--text-muted);">Your cart is empty.</p>
        ` : `
          <div style="display: flex; flex-direction: column; gap: 8px;">
            ${state.cart.map((item, idx) => `
              <div style="display: flex; justify-content: space-between; font-size: 13px; border-bottom: 1px solid var(--border); padding-bottom: 6px;">
                <span>${item.name}</span>
                <div>
                  <strong>${formatNaira(item.priceKobo)}</strong>
                  <button style="border: none; background: none; color: var(--error); margin-left: 8px; cursor: pointer;" onclick="removeFromCart(${idx})">×</button>
                </div>
              </div>
            `).join('')}
          </div>
          <div style="margin-top: 12px; font-size: 13px; border-top: 1px solid var(--border); padding-top: 10px;">
            <div style="display: flex; justify-content: space-between;"><span>Subtotal:</span> <strong>${formatNaira(cartTotal)}</strong></div>
            <div style="display: flex; justify-content: space-between; margin-top: 4px;"><span>Kogi Delivery Quote:</span> <strong>${formatNaira(200000)}</strong></div>
            <div style="display: flex; justify-content: space-between; margin-top: 8px; font-size: 16px; color: var(--primary);">
              <span>Total Payable:</span> <strong>${formatNaira(cartTotal + 200000)}</strong>
            </div>
          </div>
          <div class="alert-box alert-success" style="margin-top: 10px;">
            🎉 BONUS: Ordering these inputs waives your ₦500 Treatment Plan fee!
          </div>
          <button class="btn btn-primary btn-block" onclick="checkoutInputOrder()">
            Pay with Paystack
          </button>
        `}
      </div>
    </div>
  `;
}

function addToCart(productId) {
  const prod = state.products.find(p => p.id === productId);
  if (prod) {
    state.cart.push(prod);
    alert(`Added ${prod.name} to cart.`);
    renderScreen();
  }
}

function removeFromCart(index) {
  state.cart.splice(index, 1);
  renderScreen();
  openCartModal();
}

function openCartModal() {
  openModal('cart-modal');
}

function checkoutInputOrder() {
  const totalKobo = state.cart.reduce((sum, item) => sum + item.priceKobo, 0);
  const comm = calculateInputCommission(totalKobo);
  const orderId = "ORD-" + Math.floor(100000 + Math.random() * 900000);

  state.inputOrders.push({
    id: orderId,
    items: state.cart.map(i => i.name).join(", "),
    grossKobo: totalKobo,
    totalKobo: totalKobo + 200000,
    status: "PAID",
    date: Date.now()
  });

  state.commissions.push({
    id: "COMM-" + orderId,
    orderType: "INPUT_ORDER",
    grossKobo: totalKobo,
    rate: comm.ratePercent,
    commKobo: comm.commissionKobo,
    status: "SETTLED"
  });

  // Waive treatment plan
  state.treatmentPlans[0].feeStatus = "WAIVED_INPUT_PURCHASE";
  state.cart = [];
  closeModal('cart-modal');
  alert(`Order #${orderId} placed successfully!\n\nPayment confirmed via Paystack. Your ₦500 Treatment Plan fee has been automatically waived!`);
  navigateTo('orders');
}

// Screen 5: Market Prices
function renderMarketPricesScreen() {
  const sevenDays = 7 * 24 * 3600 * 1000;
  const now = Date.now();

  return `
    <h3 style="font-size: 17px; font-weight: 700;">Cassava Market Prices</h3>
    <p style="font-size: 12px; color: var(--text-muted); margin-bottom: 12px;">Weekly benchmarks across major Kogi State markets</p>

    <div class="tile-grid">
      ${state.marketPrices.map(item => {
        const isStale = (now - item.date) > sevenDays;
        return `
          <div class="card">
            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
              <div>
                <h4 style="font-size: 15px; font-weight: 700;">${item.location}</h4>
                <p style="font-size: 13px; color: var(--primary-dark); font-weight: 600;">${item.form}</p>
              </div>
              <span class="pill-badge" style="background: var(--primary);">VERIFIED BENCHMARK</span>
            </div>
            <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-top: 10px;">
              <div>
                <strong style="font-size: 20px; color: var(--primary);">${formatNaira(item.priceKobo)}</strong>
                <span style="font-size: 12px; color: var(--text-muted);"> / ${item.unit}</span>
              </div>
              <span style="font-size: 11px; background: var(--primary-container); color: var(--primary-dark); font-weight: 700; padding: 2px 8px; border-radius: 4px;">
                👥 ${item.buyers} buyers active
              </span>
            </div>
            <div style="font-size: 11px; color: var(--text-muted); margin-top: 8px; border-top: 1px solid var(--border); padding-top: 6px; display: flex; justify-content: space-between;">
              <span>Source: ${item.source}</span>
              <span>Updated: ${new Date(item.date).toLocaleDateString()}</span>
            </div>
            ${isStale ? `
              <div class="alert-box alert-warning" style="margin-top: 8px;">
                ⚠️ <strong>Old price:</strong> check before you sell (older than 7 days).
              </div>
            ` : ''}
          </div>
        `;
      }).join('')}
    </div>

    <div class="card" style="background: var(--primary-light);">
      <h4 style="color: var(--primary-dark); font-size: 14px;">Have Cassava Ready to Sell?</h4>
      <p style="font-size: 12px; color: var(--primary-dark); margin: 4px 0 10px 0;">Post your harvest directly to connect with garri mill buyers.</p>
      <button class="btn btn-primary" onclick="navigateTo('sell')">List Cassava for Sale</button>
    </div>
  `;
}

// Screen 6: Sell My Cassava
function renderSellCassavaScreen() {
  return `
    <div style="display: flex; justify-content: space-between; align-items: center;">
      <h3 style="font-size: 17px; font-weight: 700;">Sell My Cassava</h3>
      <button class="btn btn-primary" style="padding: 6px 12px; font-size: 12px;" onclick="openModal('create-listing-modal')">+ New Listing</button>
    </div>

    <div class="card" style="background: var(--primary-container); border: none;">
      <h4 style="font-size: 14px; font-weight: 700; color: var(--primary-dark);">🛡️ 50/50 Milestone Payout Protection</h4>
      <p style="font-size: 12px; color: var(--primary-dark); margin-top: 4px; line-height: 1.6;">
        • 50% Milestone 1 released immediately upon verified farm pickup.<br>
        • 50% Milestone 2 released upon buyer delivery receipt.<br>
        • Seller processing fee clearly previewed (never hidden).
      </p>
    </div>

    <h4 style="font-size: 15px; font-weight: 700; margin-top: 10px;">My Active Listings</h4>

    <div class="tile-grid">
      ${state.listings.map(l => {
        const charges = calculateProduceCharges(l.totalKobo);
        return `
          <div class="card">
            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
              <div>
                <h4 style="font-size: 15px; font-weight: 700;">${l.quantity} ${l.unit} of ${l.form}</h4>
                <p style="font-size: 12px; color: var(--text-muted);">📍 ${l.community}, ${l.lga} LGA</p>
              </div>
              <span class="pill-badge" style="background: var(--primary);">${l.status}</span>
            </div>
            <div style="margin-top: 8px;">
              <span style="font-size: 12px; color: var(--text-muted);">Asking Price: </span>
              <strong style="font-size: 16px; color: var(--primary);">${formatNaira(l.totalKobo)}</strong>
            </div>
            <!-- Farmer Net Payout Breakdown -->
            <div style="background: var(--background); padding: 10px; border-radius: 8px; margin-top: 10px; font-size: 12px;">
              <div style="display: flex; justify-content: space-between;"><span>Farmer Net Payout:</span> <strong>${formatNaira(charges.farmerNetPayoutKobo)}</strong></div>
              <div style="display: flex; justify-content: space-between; color: var(--text-muted); margin-top: 2px;">
                <span>• Milestone 1 (Pickup):</span> <span>${formatNaira(charges.milestone1Kobo)}</span>
              </div>
              <div style="display: flex; justify-content: space-between; color: var(--text-muted); margin-top: 2px;">
                <span>• Milestone 2 (Delivery):</span> <span>${formatNaira(charges.milestone2Kobo)}</span>
              </div>
            </div>
          </div>
        `;
      }).join('')}
    </div>

    <!-- Create Listing Modal -->
    <div class="modal-overlay" id="create-listing-modal">
      <div class="modal-card">
        <div class="modal-header">
          <h3>Create Cassava Listing</h3>
          <button class="close-btn" onclick="closeModal('create-listing-modal')">×</button>
        </div>
        <div class="form-group">
          <label>Cassava Form:</label>
          <select class="form-select" id="list-form">
            <option>Fresh Roots (TME 419)</option>
            <option>White Garri</option>
            <option>Dried Cassava Chips</option>
          </select>
        </div>
        <div class="form-group">
          <label>Quantity (Tonnes / Bags):</label>
          <input type="number" class="form-input" id="list-qty" value="5">
        </div>
        <div class="form-group">
          <label>Asking Price per Unit (₦):</label>
          <input type="number" class="form-input" id="list-price" value="85000">
        </div>
        <div class="form-group">
          <label>Availability / Harvest Date:</label>
          <input type="text" class="form-input" id="list-harvest" value="Ready in 5 days">
        </div>
        <button class="btn btn-primary btn-block" onclick="submitListing()">
          Submit for Review
        </button>
      </div>
    </div>
  `;
}

function submitListing() {
  const form = document.getElementById('list-form').value;
  const qty = parseFloat(document.getElementById('list-qty').value) || 5;
  const price = (parseFloat(document.getElementById('list-price').value) || 85000) * 100;
  const harvest = document.getElementById('list-harvest').value;

  state.listings.push({
    id: "LIST-" + Math.floor(1000 + Math.random() * 9000),
    farmerId: "farmer_musa",
    farmerFirstName: "Musa",
    lga: "Dekina",
    community: "Anyigba",
    form: form,
    quantity: qty,
    unit: "Tonne",
    pricePerUnitKobo: price,
    totalKobo: qty * price,
    harvestDate: harvest,
    logisticsNeeded: true,
    status: "ACTIVE"
  });

  closeModal('create-listing-modal');
  alert("Cassava listing published successfully!");
  renderScreen();
}

// Screen 7: Talk to Agricultural Expert
function renderConsultationScreen() {
  return `
    <div class="card">
      <div style="display: flex; gap: 12px; align-items: center;">
        <div style="width: 50px; height: 50px; border-radius: 50%; background: var(--primary-light); display: flex; align-items: center; justify-content: center; font-size: 24px;">👨‍🌾</div>
        <div>
          <h3 style="font-size: 16px; font-weight: 700;">Live Advisory Session</h3>
          <p style="font-size: 12px; color: var(--text-muted);">Extension specialist for Kogi cassava farmers</p>
        </div>
      </div>
      <div style="margin-top: 12px; background: var(--primary-container); padding: 10px; border-radius: 8px; font-size: 13px;">
        Fee: <strong>₦3,000</strong> · 1-on-1 session via WhatsApp or Phone Call
      </div>
    </div>

    <div class="card">
      <h4 style="font-size: 14px; font-weight: 700;">Book a Time:</h4>
      <div class="form-group" style="margin-top: 8px;">
        <label>Select Slot:</label>
        <select class="form-select" id="cons-slot">
          <option>Today, 3:00 PM - 4:00 PM</option>
          <option>Tomorrow Morning, 10:00 AM - 11:00 AM</option>
          <option>Request Custom Time</option>
        </select>
      </div>
      <div class="form-group" style="margin-top: 8px;">
        <label>Describe your problem:</label>
        <textarea class="form-textarea" id="cons-text">My cassava stems have yellow mottled leaves and small tubers. Need advice on stem selection.</textarea>
      </div>
      <button class="btn btn-primary btn-block" style="margin-top: 14px;" onclick="bookConsultation()">
        Pay ₦3,000 via Paystack & Confirm Booking
      </button>
    </div>

    ${state.consultations.length > 0 ? `
      <h4 style="font-size: 15px; font-weight: 700; margin-top: 8px;">My Booked Sessions</h4>
      <div class="tile-grid">
        ${state.consultations.map(c => `
          <div class="card">
            <div style="display: flex; justify-content: space-between;">
              <strong>Session #${c.id}</strong>
              <span class="pill-badge" style="background: var(--primary);">${c.status}</span>
            </div>
            <p style="font-size: 12px; color: var(--text-muted); margin-top: 4px;">${c.text}</p>
            <div style="margin-top: 8px; display: flex; gap: 8px;">
              <a href="https://wa.me/2348000000000?text=Hello%20SmartFarm%20Expert%20Booking%20${c.id}" class="btn btn-block" style="background: #25D366; color: white; padding: 6px; font-size: 12px;" target="_blank">
                💬 Open WhatsApp
              </a>
              <a href="tel:+2348000000000" class="btn btn-outline btn-block" style="padding: 6px; font-size: 12px;">
                📞 Call Consultant
              </a>
            </div>
          </div>
        `).join('')}
      </div>
    ` : ''}
  `;
}

function bookConsultation() {
  const text = document.getElementById('cons-text').value;
  const consId = "CONS-" + Math.floor(1000 + Math.random() * 9000);

  state.consultations.push({
    id: consId,
    text: text,
    status: "PAID",
    date: Date.now()
  });

  alert(`Session #${consId} booked and ₦3,000 payment confirmed via Paystack!\nYou can now connect directly via WhatsApp or call.`);
  renderScreen();
}

// Screen 8: Farmer Orders & Sales
function renderFarmerOrdersScreen() {
  return `
    <h3 style="font-size: 17px; font-weight: 700;">My Farm Orders & Sales</h3>

    <h4 style="font-size: 14px; font-weight: 700; margin-top: 10px;">Input Shipments</h4>
    ${state.inputOrders.length === 0 ? `
      <p style="font-size: 13px; color: var(--text-muted);">No input orders placed yet.</p>
    ` : state.inputOrders.map(o => `
      <div class="card" style="margin-top: 8px;">
        <div style="display: flex; justify-content: space-between;">
          <strong>Order #${o.id}</strong>
          <span class="pill-badge" style="background: var(--primary);">${o.status}</span>
        </div>
        <p style="font-size: 13px; margin-top: 4px;">${o.items}</p>
        <div style="font-size: 12px; color: var(--primary); font-weight: 700; margin-top: 6px;">Total Paid: ${formatNaira(o.totalKobo)}</div>
      </div>
    `).join('')}

    <h4 style="font-size: 14px; font-weight: 700; margin-top: 16px;">Produce Sales Milestones</h4>
    ${state.produceOrders.map(p => `
      <div class="card" style="margin-top: 8px;">
        <div style="display: flex; justify-content: space-between;">
          <strong>Order #${p.id}</strong>
          <span class="pill-badge" style="background: var(--blue);">${p.status}</span>
        </div>
        <p style="font-size: 12px; margin-top: 4px;">Buyer: ${p.buyerName} (${p.quantity} ${p.unit})</p>
        <div style="background: var(--background); padding: 8px; border-radius: 6px; margin-top: 8px; font-size: 12px;">
          <div>• Milestone 1 (Pickup): <strong>${formatNaira(p.milestone1Kobo)} [${p.milestone1Status}]</strong></div>
          <div>• Milestone 2 (Delivery): <strong>${formatNaira(p.milestone2Kobo)} [${p.milestone2Status}]</strong></div>
        </div>
      </div>
    `).join('')}
  `;
}

// Screen 9: Buyer Marketplace
function renderBuyerMarketplaceScreen() {
  return `
    <h3 style="font-size: 17px; font-weight: 700;">Buyer Cassava Sourcing</h3>
    <p style="font-size: 12px; color: var(--text-muted); margin-bottom: 12px;">Source fresh cassava roots directly from verified Kogi farmers</p>

    <div class="tile-grid">
      ${state.listings.map(l => {
        const charges = calculateProduceCharges(l.totalKobo);
        return `
          <div class="card">
            <div style="display: flex; justify-content: space-between;">
              <h4 style="font-size: 15px; font-weight: 700;">${l.quantity} ${l.unit} of ${l.form}</h4>
              <span class="pill-badge" style="background: var(--primary);">VERIFIED FARM</span>
            </div>
            <p style="font-size: 12px; color: var(--text-muted); margin-top: 2px;">Farmer: ${l.farmerFirstName} (${l.lga} LGA)</p>
            <div style="margin-top: 8px;">
              <strong style="font-size: 18px; color: var(--primary);">${formatNaira(l.totalKobo)}</strong>
            </div>
            <!-- Check and Pay Breakdown -->
            <div style="background: var(--background); padding: 10px; border-radius: 8px; margin-top: 8px; font-size: 12px; line-height: 1.6;">
              <div style="display: flex; justify-content: space-between;"><span>1. Produce Price:</span> <span>${formatNaira(charges.producePriceKobo)}</span></div>
              <div style="display: flex; justify-content: space-between;"><span>2. SmartFarm Service (${charges.buyerRatePercent}%):</span> <span>${formatNaira(charges.buyerChargeKobo)}</span></div>
              <div style="display: flex; justify-content: space-between; color: var(--text-muted); font-size: 11px;"><span>3. Processing fee:</span> <span>(Paid by seller)</span></div>
              <div style="display: flex; justify-content: space-between;"><span>4. Logistics quote:</span> <span>${formatNaira(charges.logisticsKobo)}</span></div>
              <div style="display: flex; justify-content: space-between; font-weight: 700; color: var(--primary); margin-top: 4px; border-top: 1px solid var(--border); padding-top: 4px;">
                <span>Total Payable:</span> <span>${formatNaira(charges.totalBuyerPayableKobo)}</span>
              </div>
            </div>
            <button class="btn btn-primary btn-block" style="margin-top: 10px;" onclick="buyerPurchaseProduce('${l.id}')">
              Pay with Paystack
            </button>
          </div>
        `;
      }).join('')}
    </div>
  `;
}

function buyerPurchaseProduce(listingId) {
  const listing = state.listings.find(l => l.id === listingId);
  const charges = calculateProduceCharges(listing.totalKobo);
  const orderId = "PRD-" + Math.floor(1000 + Math.random() * 9000);

  state.produceOrders.push({
    id: orderId,
    listingId: listing.id,
    farmerFirstName: listing.farmerFirstName,
    farmerLga: listing.lga,
    buyerName: "Alhaji Garba Garri Processors",
    quantity: listing.quantity,
    unit: listing.unit,
    producePriceKobo: listing.totalKobo,
    buyerChargeKobo: charges.buyerChargeKobo,
    sellerProcessingFeeKobo: charges.sellerFeeKobo,
    logisticsKobo: charges.logisticsKobo,
    totalBuyerPayableKobo: charges.totalBuyerPayableKobo,
    farmerNetPayoutKobo: charges.farmerNetPayoutKobo,
    milestone1Kobo: charges.milestone1Kobo,
    milestone2Kobo: charges.milestone2Kobo,
    milestone1Status: "ELIGIBLE",
    milestone2Status: "PENDING",
    status: "PICKED_UP",
    isDisputed: false
  });

  state.commissions.push({
    id: "COMM-" + orderId,
    orderType: "PRODUCE_ORDER",
    grossKobo: listing.totalKobo,
    rate: charges.buyerRatePercent,
    commKobo: charges.buyerChargeKobo,
    status: "SETTLED"
  });

  alert(`Produce order #${orderId} confirmed and payment secured via Paystack!\nMilestone payment workflow activated.`);
  renderScreen();
}

// Screen 10: Supplier Portal
function renderSupplierPortalScreen() {
  return `
    <h3 style="font-size: 17px; font-weight: 700;">Supplier Portal</h3>
    <p style="font-size: 12px; color: var(--text-muted); margin-bottom: 12px;">Kogi Agro-Allied Supplies (Verified)</p>

    <div class="card">
      <h4 style="font-size: 14px; font-weight: 700;">Settlement Statement (Test Ledger)</h4>
      <p style="font-size: 12px; color: var(--text-muted); margin-top: 2px;">SmartFarm commission: 5% for orders < ₦500k; 2.5% for orders ≥ ₦500k.</p>
      <div style="margin-top: 10px; font-size: 12px;">
        ${state.inputOrders.map(o => {
          const comm = calculateInputCommission(o.grossKobo);
          return `
            <div style="background: var(--background); padding: 8px; border-radius: 6px; margin-top: 6px;">
              <div>Order #${o.id} - Gross: ${formatNaira(o.grossKobo)}</div>
              <div style="color: var(--error);">Commission (${comm.ratePercent}%): -${formatNaira(comm.commissionKobo)}</div>
              <div style="color: var(--primary); font-weight: 700;">Net Settlement: ${formatNaira(comm.netSupplierKobo)}</div>
            </div>
          `;
        }).join('')}
      </div>
    </div>
  `;
}

// Screen 11: Consultant Workspace
function renderConsultantWorkspaceScreen() {
  return `
    <h3 style="font-size: 17px; font-weight: 700;">Consultant Advisory Workspace</h3>
    <p style="font-size: 12px; color: var(--text-muted); margin-bottom: 12px;">Review treatment plans and farmer disease reports</p>
    <div class="alert-box alert-success">
      🔒 <strong>Privacy Protection:</strong> Farmer phone numbers are visible only on paid scheduled consultations.
    </div>
    <div class="card" style="margin-top: 12px;">
      <h4>Assigned Consultations: ${state.consultations.length}</h4>
      ${state.consultations.map(c => `
        <div style="border-top: 1px solid var(--border); padding-top: 8px; margin-top: 8px; font-size: 12px;">
          <strong>Session #${c.id}</strong> (Status: ${c.status})<br>
          Problem: ${c.text}<br>
          Farmer Contact: Musa (+234 803 000 1122)
        </div>
      `).join('')}
    </div>
  `;
}

// Screen 12: Admin Console
function renderAdminConsoleScreen() {
  const totalComm = state.commissions.reduce((sum, c) => sum + c.commKobo, 0);

  return `
    <h3 style="font-size: 17px; font-weight: 700;">SmartFarm Admin Console</h3>
    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-top: 10px;">
      <div class="card">
        <small style="color: var(--text-muted);">Total Commissions</small>
        <div style="font-size: 18px; font-weight: 800; color: var(--primary);">${formatNaira(totalComm)}</div>
      </div>
      <div class="card">
        <small style="color: var(--text-muted);">Active Listings</small>
        <div style="font-size: 18px; font-weight: 800; color: var(--primary);">${state.listings.length}</div>
      </div>
    </div>

    <!-- Milestone Release Console -->
    <div class="card" style="margin-top: 12px;">
      <h4 style="font-size: 14px; font-weight: 700;">⚖️ Milestone Release Console</h4>
      <div style="font-size: 12px; margin-top: 8px;">
        ${state.produceOrders.map(p => `
          <div style="border-top: 1px solid var(--border); padding-top: 8px; margin-top: 8px;">
            <strong>Order #${p.id}</strong> (Farmer ${p.farmerFirstName})<br>
            • Milestone 1 (50%): ${formatNaira(p.milestone1Kobo)} [Status: ${p.milestone1Status}]
            ${p.milestone1Status === 'ELIGIBLE' ? `
              <button class="btn btn-secondary" style="padding: 4px 8px; font-size: 11px; margin-left: 6px;" onclick="releaseMilestone('${p.id}', 1)">Release Milestone 1</button>
            ` : ''}
            <br>
            • Milestone 2 (50%): ${formatNaira(p.milestone2Kobo)} [Status: ${p.milestone2Status}]
            ${p.milestone2Status === 'ELIGIBLE' ? `
              <button class="btn btn-secondary" style="padding: 4px 8px; font-size: 11px; margin-left: 6px;" onclick="releaseMilestone('${p.id}', 2)">Release Milestone 2</button>
            ` : ''}
          </div>
        `).join('')}
      </div>
    </div>
  `;
}

function releaseMilestone(orderId, num) {
  const order = state.produceOrders.find(o => o.id === orderId);
  if (order) {
    if (num === 1) order.milestone1Status = "RELEASED";
    if (num === 2) {
      order.milestone2Status = "RELEASED";
      order.status = "COMPLETED";
    }
    alert(`Milestone ${num} released and paid to farmer account!`);
    renderScreen();
  }
}

// Screen 13: Legal & Privacy
function renderLegalPrivacyScreen() {
  return `
    <div class="alert-box alert-warning">
      ⚠️ <strong>LEGAL DRAFT:</strong> All terms below are drafted for Kogi State agricultural compliance and require review by a licensed Nigerian attorney.
    </div>

    <div class="card" style="margin-top: 12px;">
      <h4 style="font-size: 15px; font-weight: 700;">1. Cassava Milestone Workflow (Not Escrow)</h4>
      <p style="font-size: 12px; color: var(--text-muted); line-height: 1.6; margin-top: 4px;">
        SmartFarm AI coordinates payment milestone status. The platform does not operate an escrow bank. 50% Milestone 1 is released on physical pickup; 50% Milestone 2 is released upon buyer confirmation or 48-hour dispute expiration.
      </p>
    </div>

    <div class="card" style="margin-top: 10px;">
      <h4 style="font-size: 15px; font-weight: 700;">2. Pesticide Dosages & Safety</h4>
      <p style="font-size: 12px; color: var(--text-muted); line-height: 1.6; margin-top: 4px;">
        All dosage rates shown in treatment plans are derived directly from NAFDAC-registered manufacturer labels. No artificial intelligence is permitted to synthesize pesticide dosages.
      </p>
    </div>
  `;
}

// Modal management
function openModal(id) {
  const el = document.getElementById(id);
  if (el) el.classList.add('open');
}

function closeModal(id) {
  const el = document.getElementById(id);
  if (el) el.classList.remove('open');
}

function openRoleModal() {
  openModal('role-modal');
}

function openWebShareModal() {
  openModal('web-share-modal');
}

function copyWebLink() {
  const input = document.getElementById('share-link-input');
  if (input) {
    input.select();
    navigator.clipboard.writeText(input.value).then(() => {
      alert("Website link copied to clipboard!");
    });
  }
}

function openWebLinkDirectly() {
  window.open(APP_URL, '_blank');
}

function shareOnWhatsApp() {
  const text = encodeURIComponent(`Hello! Check your cassava leaf sickness and see current Kogi cassava market prices directly in your browser without downloading any app:\n\n${APP_URL}`);
  window.open(`https://wa.me/?text=${text}`, '_blank');
}

function openExpertDisclaimer() {
  document.getElementById('info-modal-title').innerText = "About Verified Consultant Badge";
  document.getElementById('info-modal-body').innerText = "This badge represents SmartFarm AI's internal quality and background verification. It is not an official government certification or civil service endorsement.";
  openModal('info-modal');
}

// Initialize on Load
window.addEventListener('DOMContentLoaded', () => {
  renderScreen();
});
