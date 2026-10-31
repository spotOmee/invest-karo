package com.investkaro.domain.usecase

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class LumpSumCalculationInput(
    val initialAmount: BigDecimal,
    val durationYears: Int,
    val annualRatePercent: BigDecimal,
    val compoundingFrequencyPerYear: Int = 1 // Annual compounding standard for lump-sum illustration
)

data class LumpSumCalculationResult(
    val initialAmount: BigDecimal,
    val durationYears: Int,
    val annualRatePercent: BigDecimal,
    val totalInvested: BigDecimal,
    val estimatedFutureValue: BigDecimal,
    val estimatedGrowth: BigDecimal,
    val assumptions: List<String>
) {
    init {
        require(estimatedGrowth == estimatedFutureValue.subtract(totalInvested)) {
            "Reconciliation invariant failed: estimatedGrowth must equal FV minus totalInvested"
        }
    }
}

class CalculateLumpSumUseCase {

    private val mathContext = MathContext(34, RoundingMode.HALF_UP)
    private val hundred = BigDecimal(100)

    fun calculate(input: LumpSumCalculationInput): LumpSumCalculationResult {
        val a = input.initialAmount
        val t = input.durationYears
        val ratePercent = input.annualRatePercent
        val m = input.compoundingFrequencyPerYear

        val totalInvested = a.setScale(0, RoundingMode.HALF_UP)

        val fvExact: BigDecimal = if (ratePercent.compareTo(BigDecimal.ZERO) == 0) {
            a
        } else {
            val r = ratePercent.divide(hundred, mathContext)
            val periodRate = r.divide(BigDecimal(m), mathContext)
            val onePlusR = BigDecimal.ONE.add(periodRate, mathContext)
            val totalPeriods = m * t
            val compoundFactor = onePlusR.pow(totalPeriods, mathContext)
            a.multiply(compoundFactor, mathContext)
        }

        val displayFV = fvExact.setScale(0, RoundingMode.HALF_UP)
        val displayGrowth = displayFV.subtract(totalInvested)

        val assumptions = listOf(
            "Lump-sum investment: One-time upfront deposit of ₹${a.toPlainString()}.",
            "Duration: $t years with compounding $m time(s) per year.",
            "Assumed nominal return: ${ratePercent.toPlainString()}% p.a. Constant rate illustration.",
            "Unmodelled factors: Taxes, exit loads, expense ratios, STT, and inflation are not factored in."
        )

        return LumpSumCalculationResult(
            initialAmount = a,
            durationYears = t,
            annualRatePercent = ratePercent,
            totalInvested = totalInvested,
            estimatedFutureValue = displayFV,
            estimatedGrowth = displayGrowth,
            assumptions = assumptions
        )
    }
}
