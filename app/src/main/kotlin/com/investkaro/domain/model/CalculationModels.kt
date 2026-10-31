package com.investkaro.domain.model

import java.math.BigDecimal

/**
 * Validated inputs ready for the calculation engine.
 */
data class CalculationInput(
    val monthlyAmount: BigDecimal,
    val duration: InvestmentDuration,
    val annualRatePercent: BigDecimal,
    val investmentType: InvestmentType
)

/**
 * Result of the investment calculation with mathematical reconciliation guaranteed:
 * estimatedGrowth = estimatedFutureValue - totalInvested
 */
data class CalculationResult(
    val monthlyAmount: BigDecimal,
    val durationYears: Int,
    val totalMonths: Int,
    val annualRatePercent: BigDecimal,
    val totalInvested: BigDecimal,
    val estimatedFutureValue: BigDecimal,
    val estimatedGrowth: BigDecimal,
    val investmentType: InvestmentType,
    val assumptions: List<String>
) {
    init {
        // Enforce breakdown reconciliation invariant
        require(estimatedGrowth == estimatedFutureValue.subtract(totalInvested)) {
            "Estimated growth ($estimatedGrowth) must equal estimated future value ($estimatedFutureValue) minus total invested ($totalInvested)"
        }
    }
}

/**
 * Domain validation states for inputs.
 */
sealed interface InputValidationState {
    data object Idle : InputValidationState

    data class Invalid(
        val amountError: String? = null,
        val rateError: String? = null,
        val generalError: String? = null
    ) : InputValidationState {
        val hasErrors: Boolean
            get() = amountError != null || rateError != null || generalError != null
    }

    data class Valid(
        val input: CalculationInput
    ) : InputValidationState
}
