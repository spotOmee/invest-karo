package com.investkaro.domain.model

/**
 * Valid investment duration options: strictly 1 to 10 years.
 */
data class InvestmentDuration(
    val years: Int
) {
    init {
        require(years in MIN_YEARS..MAX_YEARS) {
            "Investment duration must be between $MIN_YEARS and $MAX_YEARS years, was $years"
        }
    }

    val totalMonths: Int get() = years * 12

    val displayText: String
        get() = if (years == 1) "1 year" else "$years years"

    companion object {
        const val MIN_YEARS = 1
        const val MAX_YEARS = 10
        const val DEFAULT_YEARS = 5

        val ALL_OPTIONS: List<InvestmentDuration> = (MIN_YEARS..MAX_YEARS).map {
            InvestmentDuration(it)
        }

        fun fromYears(years: Int): InvestmentDuration {
            val clamped = years.coerceIn(MIN_YEARS, MAX_YEARS)
            return InvestmentDuration(clamped)
        }
    }
}
