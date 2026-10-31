package com.investkaro.formatting

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Utility for formatting currency and numbers according to the Indian numbering system.
 *
 * Pattern:
 * - Up to 3 digits: no comma (e.g. 500)
 * - 4-5 digits: comma before last 3 (e.g. 5,000, 50,000)
 * - 6-7 digits: commas in 2-digit groups after first comma (e.g. 1,00,000, 10,00,000)
 * - 8+ digits: e.g. 1,00,00,000 (1 Crore)
 */
object IndianCurrencyFormatter {

    const val RUPEE_SYMBOL = "₹"

    /**
     * Formats a BigDecimal to Indian currency format with ₹ prefix.
     * Negative values are formatted as "-₹1,234".
     */
    fun formatRupees(amount: BigDecimal, includeSymbol: Boolean = true): String {
        val rounded = amount.setScale(0, RoundingMode.HALF_UP)
        val formattedNumber = formatIndianNumber(rounded.toPlainString())
        val isNegative = formattedNumber.startsWith("-")

        val absoluteNumber = if (isNegative) formattedNumber.substring(1) else formattedNumber
        return when {
            !includeSymbol -> formattedNumber
            isNegative -> "-$RUPEE_SYMBOL$absoluteNumber"
            else -> "$RUPEE_SYMBOL$absoluteNumber"
        }
    }

    /**
     * Formats a Long to Indian currency format with ₹ prefix.
     */
    fun formatRupees(amount: Long, includeSymbol: Boolean = true): String {
        return formatRupees(BigDecimal(amount), includeSymbol)
    }

    /**
     * Formats an integer number string into Indian digit grouping (e.g., "12,34,567").
     */
    fun formatIndianNumber(numberStr: String): String {
        val trimmed = numberStr.trim()
        if (trimmed.isEmpty()) return "0"

        val isNegative = trimmed.startsWith("-")
        val cleanDigits = (if (isNegative) trimmed.substring(1) else trimmed)
            .substringBefore(".") // Only group integer part

        if (cleanDigits.length <= 3) {
            return if (isNegative) "-$cleanDigits" else cleanDigits
        }

        val last3 = cleanDigits.takeLast(3)
        val remaining = cleanDigits.dropLast(3)

        val sb = StringBuilder()
        var count = 0
        for (i in remaining.length - 1 downTo 0) {
            sb.append(remaining[i])
            count++
            if (count % 2 == 0 && i > 0) {
                sb.append(',')
            }
        }

        val result = sb.reverse().toString() + "," + last3
        return if (isNegative) "-$result" else result
    }

    /**
     * Formats an annual return percentage (e.g., "12%", "10.5%").
     */
    fun formatPercentage(ratePercent: BigDecimal): String {
        val stripped = ratePercent.stripTrailingZeros()
        val formatted = if (stripped.scale() < 0) stripped.setScale(0).toPlainString() else stripped.toPlainString()
        return "$formatted%"
    }

    /**
     * Cleans an amount string by removing commas, rupee symbols, and spaces while keeping decimals.
     */
    fun cleanNumericInput(raw: String): String {
        return raw.replace("₹", "")
            .replace(",", "")
            .replace(" ", "")
            .trim()
    }
}
