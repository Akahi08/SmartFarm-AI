package com.example.smartfarm.domain

import java.text.NumberFormat
import java.util.Locale

/**
 * Money representation storing all values as integer kobo to prevent floating-point rounding errors.
 * 1 Naira (₦) = 100 kobo.
 */
data class Money(val kobo: Long) : Comparable<Money> {

    operator fun plus(other: Money): Money = Money(this.kobo + other.kobo)
    operator fun minus(other: Money): Money = Money(this.kobo - other.kobo)
    operator fun times(multiplier: Double): Money = Money((this.kobo * multiplier).toLong())
    operator fun times(multiplier: Long): Money = Money(this.kobo * multiplier)

    override fun compareTo(other: Money): Int = this.kobo.compareTo(other.kobo)

    fun toNaira(): Double = kobo / 100.0

    /**
     * Formats integer kobo to Nigerian Naira (₦).
     * Example: 50000 kobo -> "₦500"
     * Example: 450050 kobo -> "₦4,500.50"
     */
    fun toFormattedNaira(showKoboIfZero: Boolean = false): String {
        val nairaValue = kobo / 100.0
        val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = if (showKoboIfZero || (kobo % 100 != 0L)) 2 else 0
            maximumFractionDigits = 2
        }
        return "₦${formatter.format(nairaValue)}"
    }

    companion object {
        val ZERO = Money(0L)

        fun fromNaira(naira: Long): Money = Money(naira * 100L)
        fun fromNaira(naira: Double): Money = Money((naira * 100L).toLong())
        fun fromKobo(kobo: Long): Money = Money(kobo)
    }
}
