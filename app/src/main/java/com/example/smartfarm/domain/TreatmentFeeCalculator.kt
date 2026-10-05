package com.example.smartfarm.domain

import java.util.concurrent.TimeUnit

/**
 * Handles localized treatment plan pricing (₦500 default) and waiver logic.
 */
object TreatmentFeeCalculator {

    val DEFAULT_PLAN_FEE = Money.fromNaira(500L) // 50,000 kobo
    const val DEFAULT_WAIVER_WINDOW_DAYS = 14L

    enum class FeeStatus {
        UNPAID,
        PAID,
        WAIVED_INPUT_PURCHASE
    }

    data class WaiverCheckResult(
        val isWaived: Boolean,
        val feeStatus: FeeStatus,
        val matchingOrderId: String?,
        val reason: String
    )

    /**
     * Determines if the ₦500 fee is waived based on eligible paid input orders.
     */
    fun checkWaiverEligibility(
        treatmentPlanRequestedAt: Long,
        recommendedProductIds: List<String>,
        paidInputOrders: List<EligibleInputOrder>,
        waiverWindowDays: Long = DEFAULT_WAIVER_WINDOW_DAYS
    ): WaiverCheckResult {
        if (recommendedProductIds.isEmpty()) {
            return WaiverCheckResult(
                isWaived = false,
                feeStatus = FeeStatus.UNPAID,
                matchingOrderId = null,
                reason = "No recommended products specified yet."
            )
        }

        val windowMillis = TimeUnit.DAYS.toMillis(waiverWindowDays)
        val minTime = treatmentPlanRequestedAt - windowMillis
        val maxTime = treatmentPlanRequestedAt + windowMillis

        for (order in paidInputOrders) {
            // Must be paid and within window
            if (order.isPaid && order.createdAt in minTime..maxTime) {
                // Must contain at least one recommended product
                val hasMatchingProduct = order.productIds.any { it in recommendedProductIds }
                if (hasMatchingProduct) {
                    return WaiverCheckResult(
                        isWaived = true,
                        feeStatus = FeeStatus.WAIVED_INPUT_PURCHASE,
                        matchingOrderId = order.orderId,
                        reason = "Waived because recommended farm inputs were ordered (Order #${order.orderId})."
                    )
                }
            }
        }

        return WaiverCheckResult(
            isWaived = false,
            feeStatus = FeeStatus.UNPAID,
            matchingOrderId = null,
            reason = "₦500 fee applies. Waived if you buy recommended inputs through SmartFarm AI."
        )
    }

    /**
     * Checks if a waiver must revert due to an order cancellation or refund.
     */
    fun shouldRevertWaiver(
        currentStatus: FeeStatus,
        waivedByOrderId: String?,
        cancelledOrderId: String
    ): Boolean {
        return currentStatus == FeeStatus.WAIVED_INPUT_PURCHASE &&
                waivedByOrderId != null &&
                waivedByOrderId == cancelledOrderId
    }

    data class EligibleInputOrder(
        val orderId: String,
        val isPaid: Boolean,
        val createdAt: Long,
        val productIds: List<String>
    )
}
