package com.example.smartfarm.data.local

import androidx.room.*
import com.example.smartfarm.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SmartFarmDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // --- Disease Cases ---
    @Query("SELECT * FROM disease_cases WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getDiseaseCasesForFarmer(farmerId: String): Flow<List<DiseaseCaseEntity>>

    @Query("SELECT * FROM disease_cases ORDER BY createdAt DESC")
    fun getAllDiseaseCases(): Flow<List<DiseaseCaseEntity>>

    @Query("SELECT * FROM disease_cases WHERE id = :id LIMIT 1")
    suspend fun getDiseaseCaseById(id: String): DiseaseCaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiseaseCase(case: DiseaseCaseEntity)

    // --- Treatment Plans ---
    @Query("SELECT * FROM treatment_plans WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getTreatmentPlansForFarmer(farmerId: String): Flow<List<TreatmentPlanEntity>>

    @Query("SELECT * FROM treatment_plans ORDER BY createdAt DESC")
    fun getAllTreatmentPlans(): Flow<List<TreatmentPlanEntity>>

    @Query("SELECT * FROM treatment_plans WHERE id = :id LIMIT 1")
    suspend fun getTreatmentPlanById(id: String): TreatmentPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTreatmentPlan(plan: TreatmentPlanEntity)

    @Update
    suspend fun updateTreatmentPlan(plan: TreatmentPlanEntity)

    // --- Consultations ---
    @Query("SELECT * FROM consultations WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getConsultationsForFarmer(farmerId: String): Flow<List<ConsultationEntity>>

    @Query("SELECT * FROM consultations ORDER BY createdAt DESC")
    fun getAllConsultations(): Flow<List<ConsultationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsultation(consultation: ConsultationEntity)

    @Update
    suspend fun updateConsultation(consultation: ConsultationEntity)

    // --- Products ---
    @Query("SELECT * FROM products WHERE approvalStatus = 'APPROVED' AND inStock = 1")
    fun getApprovedProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE supplierId = :supplierId")
    fun getProductsForSupplier(supplierId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    // --- Input Orders ---
    @Query("SELECT * FROM input_orders WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getInputOrdersForFarmer(farmerId: String): Flow<List<InputOrderEntity>>

    @Query("SELECT * FROM input_orders ORDER BY createdAt DESC")
    fun getAllInputOrders(): Flow<List<InputOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInputOrder(order: InputOrderEntity)

    @Update
    suspend fun updateInputOrder(order: InputOrderEntity)

    // --- Market Prices ---
    @Query("SELECT * FROM market_prices ORDER BY updatedAt DESC")
    fun getAllMarketPrices(): Flow<List<MarketPriceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketPrice(price: MarketPriceEntity)

    @Delete
    suspend fun deleteMarketPrice(price: MarketPriceEntity)

    // --- Cassava Listings ---
    @Query("SELECT * FROM cassava_listings WHERE status = 'ACTIVE' ORDER BY createdAt DESC")
    fun getActiveListings(): Flow<List<CassavaListingEntity>>

    @Query("SELECT * FROM cassava_listings WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getListingsForFarmer(farmerId: String): Flow<List<CassavaListingEntity>>

    @Query("SELECT * FROM cassava_listings ORDER BY createdAt DESC")
    fun getAllListings(): Flow<List<CassavaListingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: CassavaListingEntity)

    @Update
    suspend fun updateListing(listing: CassavaListingEntity)

    // --- Produce Orders ---
    @Query("SELECT * FROM produce_orders WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun getProduceOrdersForFarmer(farmerId: String): Flow<List<ProduceOrderEntity>>

    @Query("SELECT * FROM produce_orders WHERE buyerId = :buyerId ORDER BY createdAt DESC")
    fun getProduceOrdersForBuyer(buyerId: String): Flow<List<ProduceOrderEntity>>

    @Query("SELECT * FROM produce_orders ORDER BY createdAt DESC")
    fun getAllProduceOrders(): Flow<List<ProduceOrderEntity>>

    @Query("SELECT * FROM produce_orders WHERE id = :id LIMIT 1")
    suspend fun getProduceOrderById(id: String): ProduceOrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduceOrder(order: ProduceOrderEntity)

    @Update
    suspend fun updateProduceOrder(order: ProduceOrderEntity)

    // --- Commissions ---
    @Query("SELECT * FROM commissions ORDER BY createdAt DESC")
    fun getAllCommissions(): Flow<List<CommissionRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommission(record: CommissionRecordEntity)

    // --- Audit Logs ---
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    // --- Demo Data Reset ---
    @Query("DELETE FROM disease_cases WHERE isDemo = 1")
    suspend fun deleteDemoDiseaseCases()

    @Query("DELETE FROM products WHERE isDemo = 1")
    suspend fun deleteDemoProducts()

    @Query("DELETE FROM market_prices WHERE isDemo = 1")
    suspend fun deleteDemoMarketPrices()

    @Query("DELETE FROM cassava_listings WHERE farmerId LIKE 'demo%'")
    suspend fun deleteDemoListings()
}
