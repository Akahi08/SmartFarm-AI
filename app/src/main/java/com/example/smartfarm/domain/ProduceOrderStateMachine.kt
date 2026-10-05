package com.example.smartfarm.domain

/**
 * State machine enforcing the produce milestone payment workflow.
 * IMPORTANT: NEVER call this escrow. Uses 'milestone payment workflow'.
 */
object ProduceOrderStateMachine {

    enum class OrderState(val displayName: String) {
        ORDER_CONFIRMED("Order Confirmed"),
        PAYMENT_RECEIVED_TEST("Payment Received"),
        LOGISTICS_ASSIGNED("Logistics Assigned"),
        PICKED_UP("Picked Up"),
        MILESTONE_1_ELIGIBLE("50% Milestone Eligible"),
        IN_TRANSIT("In Transit"),
        DELIVERED("Delivered"),
        BUYER_CONFIRMED("Buyer Confirmed"),
        FINAL_MILESTONE_ELIGIBLE("Final Milestone Eligible"),
        COMPLETED("Completed"),
        DISPUTED("Disputed"),
        CANCELLED("Cancelled")
    }

    enum class MilestoneSettlementStatus(val displayName: String) {
        PENDING("Pending"),
        ELIGIBLE("Eligible for Settlement"),
        RELEASED_TEST("Settlement Released"),
        HELD("Held in Dispute"),
        REVERSED("Reversed")
    }

    private val allowedTransitions: Map<OrderState, Set<OrderState>> = mapOf(
        OrderState.ORDER_CONFIRMED to setOf(
            OrderState.PAYMENT_RECEIVED_TEST,
            OrderState.CANCELLED
        ),
        OrderState.PAYMENT_RECEIVED_TEST to setOf(
            OrderState.LOGISTICS_ASSIGNED,
            OrderState.CANCELLED,
            OrderState.DISPUTED
        ),
        OrderState.LOGISTICS_ASSIGNED to setOf(
            OrderState.PICKED_UP,
            OrderState.CANCELLED,
            OrderState.DISPUTED
        ),
        OrderState.PICKED_UP to setOf(
            OrderState.MILESTONE_1_ELIGIBLE,
            OrderState.IN_TRANSIT,
            OrderState.DISPUTED
        ),
        OrderState.MILESTONE_1_ELIGIBLE to setOf(
            OrderState.IN_TRANSIT,
            OrderState.DISPUTED
        ),
        OrderState.IN_TRANSIT to setOf(
            OrderState.DELIVERED,
            OrderState.DISPUTED
        ),
        OrderState.DELIVERED to setOf(
            OrderState.BUYER_CONFIRMED,
            OrderState.FINAL_MILESTONE_ELIGIBLE,
            OrderState.DISPUTED
        ),
        OrderState.BUYER_CONFIRMED to setOf(
            OrderState.FINAL_MILESTONE_ELIGIBLE,
            OrderState.DISPUTED
        ),
        OrderState.FINAL_MILESTONE_ELIGIBLE to setOf(
            OrderState.COMPLETED
        ),
        OrderState.DISPUTED to setOf(
            OrderState.ORDER_CONFIRMED,
            OrderState.PAYMENT_RECEIVED_TEST,
            OrderState.DELIVERED,
            OrderState.COMPLETED,
            OrderState.CANCELLED
        ),
        OrderState.COMPLETED to emptySet(),
        OrderState.CANCELLED to emptySet()
    )

    fun canTransition(from: OrderState, to: OrderState): Boolean {
        return allowedTransitions[from]?.contains(to) == true
    }

    /**
     * Determines whether Milestone 1 (50%) can be flagged as eligible or released.
     */
    fun isMilestone1Eligible(state: OrderState, hasProofOfPickup: Boolean): Boolean {
        if (!hasProofOfPickup) return false
        return state in listOf(
            OrderState.PICKED_UP,
            OrderState.MILESTONE_1_ELIGIBLE,
            OrderState.IN_TRANSIT,
            OrderState.DELIVERED,
            OrderState.BUYER_CONFIRMED,
            OrderState.FINAL_MILESTONE_ELIGIBLE,
            OrderState.COMPLETED
        )
    }

    /**
     * Determines whether Final Milestone (Milestone 2, 50%) can be released.
     */
    fun isFinalMilestoneEligible(
        state: OrderState,
        buyerConfirmed: Boolean,
        disputeWindowExpired: Boolean,
        isDisputed: Boolean
    ): Boolean {
        if (isDisputed) return false
        if (!buyerConfirmed && !disputeWindowExpired) return false
        return state in listOf(
            OrderState.BUYER_CONFIRMED,
            OrderState.FINAL_MILESTONE_ELIGIBLE,
            OrderState.COMPLETED
        )
    }
}
