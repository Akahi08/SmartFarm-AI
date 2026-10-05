package com.example

import com.example.smartfarm.domain.CommissionCalculator
import com.example.smartfarm.domain.Money
import com.example.smartfarm.domain.ProduceOrderStateMachine
import com.example.smartfarm.domain.TreatmentFeeCalculator
import org.junit.Assert.*
import org.junit.Test

class SmartFarmDomainUnitTest {

    @Test
    fun testMoneyFormattingAndArithmetic() {
        val m1 = Money.fromNaira(500L) // 50,000 kobo
        assertEquals("₦500", m1.toFormattedNaira())

        val m2 = Money.fromKobo(450050L) // ₦4,500.50
        assertEquals("₦4,500.50", m2.toFormattedNaira())

        val sum = m1 + Money.fromNaira(1500L)
        assertEquals(200000L, sum.kobo)
        assertEquals("₦2,000", sum.toFormattedNaira())
    }

    @Test
    fun testInputCommissionBoundaries() {
        // Below ₦500,000 -> 5%
        // Test ₦499,999 (49,999,900 kobo)
        val belowKobo = 49_999_900L
        val resBelow = CommissionCalculator.calculateInputCommission(belowKobo)
        assertEquals(5.0, resBelow.ratePercent, 0.001)
        val expectedCommBelow = (belowKobo * 0.05).toLong()
        assertEquals(expectedCommBelow, resBelow.commissionKobo)
        assertEquals(belowKobo - expectedCommBelow, resBelow.supplierNetKobo)

        // Exactly ₦500,000 (50,000,000 kobo) -> 2.5%
        val exactKobo = 50_000_000L
        val resExact = CommissionCalculator.calculateInputCommission(exactKobo)
        assertEquals(2.5, resExact.ratePercent, 0.001)
        val expectedCommExact = (exactKobo * 0.025).toLong() // 1,250,000 kobo = ₦12,500
        assertEquals(expectedCommExact, resExact.commissionKobo)
        assertEquals(exactKobo - expectedCommExact, resExact.supplierNetKobo)

        // Above ₦500,000 (e.g. ₦1,000,000 = 100,000,000 kobo) -> 2.5%
        val aboveKobo = 100_000_000L
        val resAbove = CommissionCalculator.calculateInputCommission(aboveKobo)
        assertEquals(2.5, resAbove.ratePercent, 0.001)
        assertEquals(2_500_000L, resAbove.commissionKobo) // ₦25,000
    }

    @Test
    fun testProduceChargesAndMilestoneSplit() {
        // 10 tonnes at ₦85,000/tonne = ₦850,000 (85,000,000 kobo) -> >= ₦500,000 -> 2.5% buyer service charge
        val produceKobo = 85_000_000L
        val logisticsKobo = 2_500_000L // ₦25,000

        val charges = CommissionCalculator.calculateProduceCharges(produceKobo, logisticsKobo)

        // Buyer service charge: 2.5% of ₦850,000 = ₦21,250 (2,125,000 kobo)
        assertEquals(2.5, charges.buyerRatePercent, 0.001)
        assertEquals(2_125_000L, charges.buyerServiceChargeKobo)

        // Total buyer payable: ₦850,000 + ₦21,250 + ₦25,000 = ₦896,250
        assertEquals(89_625_000L, charges.totalBuyerPayableKobo)

        // Seller processing fee: 1.5% capped at ₦2,000 (200,000 kobo)
        assertEquals(200_000L, charges.sellerProcessingFeeKobo)

        // Farmer net payout: ₦850,000 - ₦2,000 = ₦848,000 (84,800,000 kobo)
        assertEquals(84_800_000L, charges.farmerNetPayoutKobo)

        // 50% milestone split: ₦424,000 each
        assertEquals(42_400_000L, charges.milestone1Kobo)
        assertEquals(42_400_000L, charges.milestone2Kobo)
        assertEquals(charges.farmerNetPayoutKobo, charges.milestone1Kobo + charges.milestone2Kobo)
    }

    @Test
    fun testTreatmentFeeWaiverLogic() {
        val now = System.currentTimeMillis()
        val recommended = listOf("PROD-001", "PROD-004")

        // Case 1: No matching products -> Unpaid
        val nonMatchingOrders = listOf(
            TreatmentFeeCalculator.EligibleInputOrder(
                orderId = "ORD-001",
                isPaid = true,
                createdAt = now,
                productIds = listOf("PROD-999")
            )
        )
        val res1 = TreatmentFeeCalculator.checkWaiverEligibility(now, recommended, nonMatchingOrders)
        assertFalse(res1.isWaived)
        assertEquals(TreatmentFeeCalculator.FeeStatus.UNPAID, res1.feeStatus)

        // Case 2: Matching product within 14 days -> Waived
        val matchingOrders = listOf(
            TreatmentFeeCalculator.EligibleInputOrder(
                orderId = "ORD-002",
                isPaid = true,
                createdAt = now,
                productIds = listOf("PROD-001")
            )
        )
        val res2 = TreatmentFeeCalculator.checkWaiverEligibility(now, recommended, matchingOrders)
        assertTrue(res2.isWaived)
        assertEquals(TreatmentFeeCalculator.FeeStatus.WAIVED_INPUT_PURCHASE, res2.feeStatus)
        assertEquals("ORD-002", res2.matchingOrderId)

        // Case 3: Cancellation reversion
        val shouldRevert = TreatmentFeeCalculator.shouldRevertWaiver(
            currentStatus = TreatmentFeeCalculator.FeeStatus.WAIVED_INPUT_PURCHASE,
            waivedByOrderId = "ORD-002",
            cancelledOrderId = "ORD-002"
        )
        assertTrue(shouldRevert)
    }

    @Test
    fun testProduceOrderStateMachineTransitions() {
        // Valid transitions
        assertTrue(
            ProduceOrderStateMachine.canTransition(
                ProduceOrderStateMachine.OrderState.ORDER_CONFIRMED,
                ProduceOrderStateMachine.OrderState.PAYMENT_RECEIVED_TEST
            )
        )
        assertTrue(
            ProduceOrderStateMachine.canTransition(
                ProduceOrderStateMachine.OrderState.PAYMENT_RECEIVED_TEST,
                ProduceOrderStateMachine.OrderState.LOGISTICS_ASSIGNED
            )
        )
        assertTrue(
            ProduceOrderStateMachine.canTransition(
                ProduceOrderStateMachine.OrderState.LOGISTICS_ASSIGNED,
                ProduceOrderStateMachine.OrderState.PICKED_UP
            )
        )
        assertTrue(
            ProduceOrderStateMachine.canTransition(
                ProduceOrderStateMachine.OrderState.PICKED_UP,
                ProduceOrderStateMachine.OrderState.MILESTONE_1_ELIGIBLE
            )
        )

        // Invalid direct transition (skip payment directly to delivered is rejected)
        assertFalse(
            ProduceOrderStateMachine.canTransition(
                ProduceOrderStateMachine.OrderState.ORDER_CONFIRMED,
                ProduceOrderStateMachine.OrderState.DELIVERED
            )
        )
    }
}
