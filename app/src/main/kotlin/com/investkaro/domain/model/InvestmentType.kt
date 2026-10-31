package com.investkaro.domain.model

/**
 * Represents the investment types supported by Invest Karo.
 *
 * NOTE: For this version, only types that strictly map to the disclosed
 * end-of-month recurring contribution model are included.
 *
 * Products such as FD, RD, PPF, EPF/VPF, NPS, SSY, NSC, KVP, MIS, SCSS,
 * bonds, stocks, gold, ETFs, and REITs are deliberately excluded because
 * they have distinct compounding frequencies (e.g., quarterly in bank RDs/FDs),
 * annual lock-ins, tax deductions, government interest rate revisions, or lump-sum dynamics.
 */
enum class InvestmentType(
    val id: String,
    val title: String,
    val subtitle: String,
    val disclosureNote: String
) {
    MUTUAL_FUND_SIP(
        id = "mf_sip",
        title = "Mutual fund SIP (market-linked estimate)",
        subtitle = "Equity / Hybrid / Debt SIP illustrative projection",
        disclosureNote = "Market-linked projection. Assumes a constant monthly rate derived from the annual assumption. Actual mutual fund returns fluctuate daily based on NAV and are neither fixed nor guaranteed."
    ),
    INDEX_FUND_SIP(
        id = "index_sip",
        title = "Index fund SIP (market-linked estimate)",
        subtitle = "Passive index tracking illustrative projection",
        disclosureNote = "Market-linked passive fund projection. Assumes constant monthly compounding. Actual index returns are volatile and subject to tracking error, dividends reinvestment, and market cycles."
    ),
    RECURRING_INVESTMENT(
        id = "recurring_generic",
        title = "Recurring investment — generic projection",
        subtitle = "Generic monthly contribution model",
        disclosureNote = "Illustrative mathematical projection for periodic end-of-month investments at the user-specified rate. Not an official bank Recurring Deposit (RD) quote, as bank RDs compound quarterly under RBI rules."
    );

    companion object {
        val DEFAULT = MUTUAL_FUND_SIP

        fun fromId(id: String): InvestmentType =
            entries.find { it.id == id } ?: DEFAULT
    }
}
