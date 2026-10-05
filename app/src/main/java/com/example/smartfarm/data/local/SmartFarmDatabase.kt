package com.example.smartfarm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.smartfarm.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        DiseaseCaseEntity::class,
        TreatmentPlanEntity::class,
        ConsultationEntity::class,
        ProductEntity::class,
        InputOrderEntity::class,
        MarketPriceEntity::class,
        CassavaListingEntity::class,
        ProduceOrderEntity::class,
        CommissionRecordEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SmartFarmDatabase : RoomDatabase() {

    abstract fun smartFarmDao(): SmartFarmDao

    companion object {
        @Volatile
        private var INSTANCE: SmartFarmDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SmartFarmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmartFarmDatabase::class.java,
                    "smartfarm_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialDemoData(database.smartFarmDao())
                    }
                }
            }
        }

        suspend fun populateInitialDemoData(dao: SmartFarmDao) {
            // Seed Users
            val demoUsers = listOf(
                UserEntity(
                    id = "farmer_musa",
                    role = UserRole.FARMER,
                    name = "Musa Ibrahim",
                    phone = "+234 803 000 1122",
                    email = "musa.farmer@smartfarm.demo",
                    state = "Kogi",
                    lga = "Dekina",
                    community = "Anyigba",
                    consentGiven = true,
                    locationConsent = false,
                    isDemo = true
                ),
                UserEntity(
                    id = "buyer_garba",
                    role = UserRole.BUYER,
                    name = "Alhaji Garba Garri Processors",
                    phone = "+234 802 000 3344",
                    email = "garba.buyer@smartfarm.demo",
                    state = "Kogi",
                    lga = "Lokoja",
                    community = "Felele",
                    isDemo = true
                ),
                UserEntity(
                    id = "supplier_kogi",
                    role = UserRole.SUPPLIER,
                    name = "Kogi Agro-Allied Inputs (Verified)",
                    phone = "+234 805 000 5566",
                    email = "agro.supplier@smartfarm.demo",
                    state = "Kogi",
                    lga = "Lokoja",
                    community = "Ganaja",
                    isDemo = true
                ),
                UserEntity(
                    id = "consultant_01",
                    role = UserRole.CONSULTANT,
                    name = "Dr. [Expert name to be added]",
                    phone = "+234 800 000 7788",
                    email = "consultant@smartfarm.demo",
                    state = "Kogi",
                    lga = "Lokoja",
                    community = "Secretariat",
                    isDemo = true
                ),
                UserEntity(
                    id = "admin_01",
                    role = UserRole.ADMIN,
                    name = "SmartFarm Administrator",
                    phone = "+234 809 000 9900",
                    email = "admin@smartfarm.demo",
                    state = "Kogi",
                    lga = "Lokoja",
                    community = "Central",
                    isDemo = true
                )
            )
            demoUsers.forEach { dao.insertUser(it) }

            // Seed Products
            val demoProducts = listOf(
                ProductEntity(
                    id = "PROD-001",
                    supplierId = "supplier_kogi",
                    supplierName = "Kogi Agro-Allied Inputs",
                    isSupplierVerified = true,
                    simpleName = "Certified Cassava Stems (TME 419)",
                    officialName = "TME 419 Certified Foundation Stem Cuttings",
                    purpose = "High-starch, CMD disease-resistant cassava stems.",
                    category = "Stem Cuttings",
                    type = "NEITHER",
                    priceKobo = 120000L, // ₦1,200 / bundle
                    unit = "Bundle (50 stems)",
                    quantityPerUnit = "50 stems",
                    inStock = true,
                    state = "Kogi",
                    lga = "Lokoja",
                    safetyInfo = "Store under shade before planting. Plant within 5 days of cutting.",
                    labelInfo = "Recommended spacing: 1m x 1m (10,000 stems/ha). Yield potential: 25-35 tonnes/ha.",
                    registrationNumber = "NASC/CAS/2024/091",
                    approvalStatus = "APPROVED",
                    isDemo = true
                ),
                ProductEntity(
                    id = "PROD-002",
                    supplierId = "supplier_kogi",
                    supplierName = "Kogi Agro-Allied Inputs",
                    isSupplierVerified = true,
                    simpleName = "Cassava NPK 15:15:15 Fertilizer",
                    officialName = "Balanced Mineral NPK Blend for Root Bulking",
                    purpose = "Essential root nutrition to increase root yield and starch quality.",
                    category = "Fertilizer",
                    type = "CHEMICAL",
                    priceKobo = 3800000L, // ₦38,000 / bag
                    unit = "50kg Bag",
                    quantityPerUnit = "50kg",
                    inStock = true,
                    state = "Kogi",
                    lga = "Lokoja",
                    safetyInfo = "Wear gloves during application. Keep away from water streams and children.",
                    labelInfo = "Apply 200kg/ha at 4 to 8 weeks after planting as ring placement 15cm from stem base.",
                    registrationNumber = "FMA-FERT-2023-882",
                    approvalStatus = "APPROVED",
                    isDemo = true
                ),
                ProductEntity(
                    id = "PROD-003",
                    supplierId = "supplier_kogi",
                    supplierName = "Kogi Agro-Allied Inputs",
                    isSupplierVerified = true,
                    simpleName = "Bio-Neem Organic Pest Repellent",
                    officialName = "Cold-Pressed Azadirachtin Botanical Bio-Pesticide",
                    purpose = "Natural organic spray to control whiteflies and green mites.",
                    category = "Biological",
                    type = "ORGANIC_BIOLOGICAL",
                    priceKobo = 450000L, // ₦4,500
                    unit = "1 Litre Bottle",
                    quantityPerUnit = "1 Litre",
                    inStock = true,
                    state = "Kogi",
                    lga = "Lokoja",
                    safetyInfo = "Natural botanical extract. Low toxicity to non-target beneficial insects.",
                    labelInfo = "Dilute 50ml per 15L knapsack sprayer. Apply early morning or late evening. Pre-harvest interval: 0 days.",
                    registrationNumber = "NAFDAC Reg No: 04-8912P",
                    approvalStatus = "APPROVED",
                    isDemo = true
                ),
                ProductEntity(
                    id = "PROD-004",
                    supplierId = "supplier_kogi",
                    supplierName = "Kogi Agro-Allied Inputs",
                    isSupplierVerified = true,
                    simpleName = "Copper Hydroxide Bactericide Spray",
                    officialName = "Copper Hydroxide 77% WP Contact Bactericide",
                    purpose = "Targeted treatment for severe Cassava Bacterial Blight outbreaks.",
                    category = "Crop Protection",
                    type = "CHEMICAL",
                    priceKobo = 680000L, // ₦6,800
                    unit = "500g Sachet",
                    quantityPerUnit = "500g",
                    inStock = true,
                    state = "Kogi",
                    lga = "Lokoja",
                    safetyInfo = "CAUTION: Corrosive to eyes. Wear safety goggles, protective gloves, and boots.",
                    labelInfo = "Apply 30g per 15L water upon first leaf symptom. Repeat every 14 days if needed. Harvest interval: 14 days.",
                    registrationNumber = "NAFDAC Reg No: 04-3291P",
                    approvalStatus = "APPROVED",
                    isDemo = true
                ),
                ProductEntity(
                    id = "PROD-005",
                    supplierId = "supplier_kogi",
                    supplierName = "Kogi Agro-Allied Inputs",
                    isSupplierVerified = true,
                    simpleName = "Organic Trichoderma Soil Inoculant",
                    officialName = "Trichoderma harzianum Bio-Fungicide Powder",
                    purpose = "Bio-agent protecting stem cuttings and root zones from fungal root rot.",
                    category = "Biological",
                    type = "ORGANIC_BIOLOGICAL",
                    priceKobo = 520000L, // ₦5,200
                    unit = "1kg Pack",
                    quantityPerUnit = "1kg",
                    inStock = true,
                    state = "Kogi",
                    lga = "Lokoja",
                    safetyInfo = "Beneficial living microorganism. Avoid mixing directly with chemical fungicides.",
                    labelInfo = "Dip cuttings in slurry (50g per 10L water) before planting, or drench around planting ridge.",
                    registrationNumber = "NAFDAC Reg No: BIO-2022-114",
                    approvalStatus = "APPROVED",
                    isDemo = true
                )
            )
            demoProducts.forEach { dao.insertProduct(it) }

            // Seed Market Prices
            val now = System.currentTimeMillis()
            val nineDaysAgo = now - (9 * 24 * 3600 * 1000L) // Stale test case (> 7 days)
            val demoPrices = listOf(
                MarketPriceEntity(
                    id = "PRICE-01",
                    location = "Anyigba Market (Dekina LGA, Kogi)",
                    cassavaForm = "Fresh Roots",
                    priceKobo = 8500000L, // ₦85,000 / tonne
                    unit = "Tonne",
                    updatedAt = now,
                    sourceLabel = "Entered by SmartFarm AI team",
                    isDemo = true,
                    buyersLookingCount = 4
                ),
                MarketPriceEntity(
                    id = "PRICE-02",
                    location = "Lokoja Central Market (Kogi)",
                    cassavaForm = "Garri (White)",
                    priceKobo = 4800000L, // ₦48,000 / 100kg bag
                    unit = "100kg Bag",
                    updatedAt = now,
                    sourceLabel = "Entered by SmartFarm AI team",
                    isDemo = true,
                    buyersLookingCount = 6
                ),
                MarketPriceEntity(
                    id = "PRICE-03",
                    location = "Kabba Farmers Market (Kogi)",
                    cassavaForm = "Dried Cassava Chips",
                    priceKobo = 6200000L, // ₦62,000 / tonne
                    unit = "Tonne",
                    updatedAt = nineDaysAgo, // Past 7 days -> Shows Stale Warning!
                    sourceLabel = "Reported by buyer: unverified",
                    isDemo = true,
                    buyersLookingCount = 2
                ),
                MarketPriceEntity(
                    id = "PRICE-04",
                    location = "Ankpa Main Market (Kogi)",
                    cassavaForm = "Fresh Roots",
                    priceKobo = 8200000L, // ₦82,000 / tonne
                    unit = "Tonne",
                    updatedAt = now,
                    sourceLabel = "Entered by SmartFarm AI team",
                    isDemo = true,
                    buyersLookingCount = 3
                )
            )
            demoPrices.forEach { dao.insertMarketPrice(it) }

            // Seed Cassava Listing
            val demoListing = CassavaListingEntity(
                id = "LIST-001",
                farmerId = "farmer_musa",
                farmerFirstName = "Musa",
                lga = "Dekina",
                community = "Anyigba",
                cassavaForm = "Fresh Roots (TME 419)",
                quantity = 10.0,
                unit = "Tonne",
                pricePerUnitKobo = 8500000L, // ₦85,000/tonne
                totalKobo = 85000000L, // ₦850,000 total
                harvestDate = "Next 5 Days",
                notes = "Matured high-starch cassava ready for harvest. Transport accessible road.",
                status = "ACTIVE",
                logisticsNeeded = true,
                createdAt = now
            )
            dao.insertListing(demoListing)

            // Seed Audit Log
            dao.insertAuditLog(
                AuditLogEntity(
                    id = "AUDIT-001",
                    actor = "System Init",
                    role = "System",
                    action = "INITIAL_SEED",
                    entityType = "System",
                    entityId = "ALL",
                    details = "SmartFarm AI initialized with demo datasets for Kogi State cassava ecosystem.",
                    timestamp = now
                )
            )
        }
    }
}
