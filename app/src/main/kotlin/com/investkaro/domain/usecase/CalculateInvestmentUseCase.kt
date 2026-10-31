package com.investkaro.domain.usecase

import com.investkaro.domain.model.CalculationInput
import com.investkaro.domain.model.CalculationResult
import com.investkaro.domain.model.InputValidationState
import com.investkaro.domain.model.InvestmentDuration
import com.investkaro.domain.model.InvestmentType
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Pure Kotlin calculation engine for Invest Karo.
 *
 * Implements the standard ordinary annuity formula (contributions made at the end of every month):
 *
 *   When i != 0:
 *     FV = P * [ ((1 + i)^n - 1) / i ]
 *
 *   When i == 0:
 *     FV = P * n
 *
 * Where:
 *   P = monthly contribution (BigDecimal)
 *   r = nominal annual rate as a decimal (annualRatePercent / 100)
 *   i = monthly rate (r / 12)
 *   n = total months (years * 12)
 */
class CalculateInvestmentUseCase {

    private val mathContext = MathContext(34, RoundingMode.HALF_UP)
    private val twelve = BigDecimal(12)
    private val hundred = BigDecimal(100)

    companion object {
        val MIN_ANNUAL_RATE = BigDecimal("-100.00")
        val MAX_ANNUAL_RATE = BigDecimal("100.00")
        val MAX_MONTHLY_AMOUNT = BigDecimal("100000000") // ₹10 Crore/month limit
    }

    /**
     * Validates raw user input strings before calculation.
     */
    fun validateInputs(
        rawAmount: String,
        rawRate: String,
        duration: InvestmentDuration,
        type: InvestmentType
    ): InputValidationState {
        val cleanAmount = rawAmount.trim().replace(",", "").replace("₹", "").trim()
        val cleanRate = rawRate.trim().replace("%", "").trim()

        if (cleanAmount.isEmpty() && cleanRate.isEmpty()) {
            return InputValidationState.Idle
        }

        var amountError: String? = null
        var parsedAmount: BigDecimal? = null

        if (cleanAmount.isEmpty()) {
            amountError = "Enter a monthly investment amount."
        } else {
            try {
                val amount = BigDecimal(cleanAmount)
                when {
                    amount <= BigDecimal.ZERO -> {
                        amountError = "Monthly investment must be greater than ₹0."
                    }
                    amount > MAX_MONTHLY_AMOUNT -> {
                        amountError = "Monthly amount cannot exceed ₹10,00,00,000."
                    }
                    else -> parsedAmount = amount
                }
            } catch (_: NumberFormatException) {
                amountError = "Invalid amount format. Enter a valid number."
            }
        }

        var rateError: String? = null
        var parsedRate: BigDecimal? = null

        if (cleanRate.isEmpty()) {
            rateError = "Enter expected annual return percentage."
        } else {
            try {
                val rate = BigDecimal(cleanRate)
                when {
                    rate < MIN_ANNUAL_RATE || rate > MAX_ANNUAL_RATE -> {
                        rateError = "Expected annual return must be between -100% and 100%."
                    }
                    else -> {
                        // Check if monthly growth factor (1 + i) would be <= 0
                        val r = rate.divide(hundred, mathContext)
                        val i = r.divide(twelve, mathContext)
                        val onePlusI = BigDecimal.ONE.add(i, mathContext)
                        if (onePlusI <= BigDecimal.ZERO) {
                            rateError = "Assumed rate makes monthly factor non-positive."
                        } else {
                            parsedRate = rate
                        }
                    }
                }
            } catch (_: NumberFormatException) {
                rateError = "Invalid percentage format. Enter a valid number (e.g. 12 or 10.5)."
            }
        }

        if (amountError != null || rateError != null || parsedAmount == null || parsedRate == null) {
            return InputValidationState.Invalid(
                amountError = amountError,
                rateError = rateError
            )
        }

        return InputValidationState.Valid(
            CalculationInput(
                monthlyAmount = parsedAmount,
                duration = duration,
                annualRatePercent = parsedRate,
                investmentType = type
            )
        )
    }

    /**
     * Executes the calculation on validated inputs.
     */
    fun calculate(input: CalculationInput): CalculationResult {
        val p = input.monthlyAmount
        val n = input.duration.totalMonths
        val annualRatePercent = input.annualRatePercent
        val r = annualRatePercent.divide(hundred, mathContext)
        val i = r.divide(twelve, mathContext)

        val totalInvestedExact = p.multiply(BigDecimal(n))

        val fvExact: BigDecimal = if (annualRatePercent.compareTo(BigDecimal.ZERO) == 0) {
            totalInvestedExact
        } else {
            val onePlusI = BigDecimal.ONE.add(i, mathContext)
            // (1 + i)^n
            val compoundFactor = onePlusI.pow(n, mathContext)
            // ((1 + i)^n - 1)
            val numerator = compoundFactor.subtract(BigDecimal.ONE, mathContext)
            // ((1 + i)^n - 1) / i
            val annuityFactor = numerator.divide(i, mathContext)
            // P * [ ((1 + i)^n - 1) / i ]
            p.multiply(annuityFactor, mathContext)
        }

        // Consistent rounding convention for display
        val displayFV = fvExact.setScale(0, RoundingMode.HALF_UP)
        val displayTotalInvested = totalInvestedExact.setScale(0, RoundingMode.HALF_UP)
        // Invariant: estimatedGrowth = displayFV - displayTotalInvested
        val displayGrowth = displayFV.subtract(displayTotalInvested)

        val assumptions = listOf(
            "End-of-month contribution: ₹${input.monthlyAmount.toPlainString()} invested at the end of each month.",
            "Monthly rate: Annual rate of ${annualRatePercent.toPlainString()}% divided by 12 (${i.multiply(hundred).setScale(4, RoundingMode.HALF_UP).toPlainString()}% per month).",
            "Duration: ${input.duration.displayText} ($n monthly contributions).",
            "Investment context: ${input.investmentType.disclosureNote}",
            "Unmodelled factors: Taxes, expense ratios, STT, exit loads, inflation, and volatility are not factored in."
        )

        return CalculationResult(
            monthlyAmount = p,
            durationYears = input.duration.years,
            totalMonths = n,
            annualRatePercent = annualRatePercent,
            totalInvested = displayTotalInvested,
            estimatedFutureValue = displayFV,
            estimatedGrowth = displayGrowth,
            investmentType = input.investmentType,
            assumptions = assumptions
        )
    }
}
