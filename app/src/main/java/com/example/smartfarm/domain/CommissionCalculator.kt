package com.example.smartfarm.domain

/**
 * Calculates marketplace commissions and service fees according to SmartFarm AI rules.
 * All computations use integer kobo.
 */
object CommissionCalculator {

    // ₦500,000 in kobo = 50,000,000 kobo
    const val THRESHOLD_KOBO: Long = 500_000 * 100L

    // Input orders commission (paid by supplier)
    const val INPUT_COMMISSION_LOW_RATE = 0.05   // 5% for < ₦500,000
    const val INPUT_COMMISSION_HIGH_RATE = 0.025 // 2.5% for >= ₦500,000

    // Produce orders buyer service charge (paid by buyer)
    const val PRODUCE_BUYER_CHARGE_LOW_RATE = 0.03   // 3% for < ₦500,000
    const val PRODUCE_BUYER_CHARGE_HIGH_RATE = 0.025 // 2.5% for >= ₦500,000

    // Estimated Paystack processing fee rate (1.5% capped at ₦2,000) borne by seller
    const val PROCESSING_FEE_RATE = 0.015
    val PROCESSING_FEE_CAP = Money.fromNaira(2000L) // 200,000 kobo

    data class InputCommissionResult(
        val grossKobo: Long,
        val ratePercent: Double,
        val commissionKobo: Long,
        val supplierNetKobo: Long
    )

    data class ProduceChargeResult(
        val producePriceKobo: Long,
        val buyerRatePercent: Double,
        val buyerServiceChargeKobo: Long,
        val sellerProcessingFeeKobo: Long,
        val logisticsChargeKobo: Long,
        val totalBuyerPayableKobo: Long,
        val farmerNetPayoutKobo: Long,
        val milestone1Kobo: Long,
        val milestone2Kobo: Long
    )

    /**
     * Calculates supplier commission on farm input orders.
     * Evaluated strictly on gross goods value (excluding delivery).
     */
    fun calculateInputCommission(grossGoodsKobo: Long): InputCommissionResult {
        val rate = if (grossGoodsKobo >= THRESHOLD_KOBO) {
            INPUT_COMMISSION_HIGH_RATE
        } else {
            INPUT_COMMISSION_LOW_RATE
        }
        val commissionKobo = (grossGoodsKobo * rate).toLong()
        val supplierNetKobo = grossGoodsKobo - commissionKobo

        return InputCommissionResult(
            grossKobo = grossGoodsKobo,
            ratePercent = rate * 100.0,
            commissionKobo = commissionKobo,
            supplierNetKobo = supplierNetKobo
        )
    }

    /**
     * Calculates produce order fees and milestone splits for cassava marketplace.
     */
    fun calculateProduceCharges(
        producePriceKobo: Long,
        logisticsChargeKobo: Long = 0L
    ): ProduceChargeResult {
        val buyerRate = if (producePriceKobo >= THRESHOLD_KOBO) {
            PRODUCE_BUYER_CHARGE_HIGH_RATE
        } else {
            PRODUCE_BUYER_CHARGE_LOW_RATE
        }
        val buyerServiceChargeKobo = (producePriceKobo * buyerRate).toLong()

        // Paystack processing fee borne by farmer (seller)
        val rawProcessingFee = (producePriceKobo * PROCESSING_FEE_RATE).toLong()
        val sellerProcessingFeeKobo = minOf(rawProcessingFee, PROCESSING_FEE_CAP.kobo)

        val totalBuyerPayableKobo = producePriceKobo + buyerServiceChargeKobo + logisticsChargeKobo
        val farmerNetPayoutKobo = producePriceKobo - sellerProcessingFeeKobo

        // 50% milestone split
        val milestone1Kobo = farmerNetPayoutKobo / 2
        val milestone2Kobo = farmerNetPayoutKobo - milestone1Kobo // preserves odd kobo

        return ProduceChargeResult(
            producePriceKobo = producePriceKobo,
            buyerRatePercent = buyerRate * 100.0,
            buyerServiceChargeKobo = buyerServiceChargeKobo,
            sellerProcessingFeeKobo = sellerProcessingFeeKobo,
            logisticsChargeKobo = logisticsChargeKobo,
            totalBuyerPayableKobo = totalBuyerPayableKobo,
            farmerNetPayoutKobo = farmerNetPayoutKobo,
            milestone1Kobo = milestone1Kobo,
            milestone2Kobo = milestone2Kobo
        )
    }
}
