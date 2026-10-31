package com.investkaro.domain.catalogue

/**
 * Status of an investment type in Invest Karo.
 * Reflects genuine calculation readiness to prevent fabricated outcomes.
 */
enum class CatalogueStatus(val label: String) {
    CALCULATOR_SUPPORTED("Calculator Supported"),
    PLANNING_ONLY("Planning Only"),
    COMING_SOON("Coming Soon")
}

/**
 * High-level investment categories defined in the product specification.
 */
enum class CatalogueCategory(val displayName: String, val shortLabel: String) {
    MUTUAL_FUNDS("Mutual Funds", "MF"),
    SHARES_ETFS("Shares, ETFs & Listed", "Equity & ETF"),
    FIXED_INCOME("Fixed Income & Savings", "Fixed Income"),
    RETIREMENT("Retirement & Long-Term", "Retirement"),
    PRECIOUS_METALS("Precious Metals & Physical", "Metals & Real Estate"),
    OTHER_ADVANCED("Other & Advanced", "Alternatives")
}

/**
 * Calculation model identifiers.
 */
object CalculationModelIds {
    const val MONTHLY_CONTRIBUTION = "monthly_contribution"
    const val LUMP_SUM_COMPOUND = "lump_sum_compound"
}

/**
 * Represents a single investment item in the comprehensive catalogue.
 */
data class CatalogueItem(
    val id: String,
    val name: String,
    val category: CatalogueCategory,
    val description: String,
    val modelId: String?,
    val riskAndLimitations: String,
    val status: CatalogueStatus,
    val searchKeywords: List<String> = emptyList(),
    val isCustom: Boolean = false
) {
    val isCalculatorSupported: Boolean
        get() = status == CatalogueStatus.CALCULATOR_SUPPORTED && modelId != null

    /**
     * Determines whether this item matches a search query against its name,
     * category, acronyms, or description.
     */
    fun matchesQuery(query: String): Boolean {
        val clean = query.trim().lowercase()
        if (clean.isEmpty()) return true

        if (name.lowercase().contains(clean)) return true
        if (category.displayName.lowercase().contains(clean)) return true
        if (category.shortLabel.lowercase().contains(clean)) return true
        if (description.lowercase().contains(clean)) return true
        return searchKeywords.any { it.lowercase().contains(clean) || clean.contains(it.lowercase()) }
    }
}
