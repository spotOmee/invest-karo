package com.investkaro.domain.loan

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

enum class LoanType(val displayName: String, val typicalRateHint: String) {
    HOME_LOAN("Home loan", "e.g. 8.5%"),
    CAR_LOAN("Car loan", "e.g. 9.0%"),
    PERSONAL_LOAN("Personal loan", "e.g. 13.5%"),
    EDUCATION_LOAN("Education loan", "e.g. 10.0%"),
    GOLD_LOAN("Gold loan", "e.g. 9.5%"),
    BUSINESS_LOAN("Business loan", "e.g. 14.0%"),
    CUSTOM_LOAN("Custom loan", "e.g. 12.0%")
}

enum class TenureUnit {
    YEARS,
    MONTHS
}

data class EmiCalculationInput(
    val loanType: LoanType,
    val principal: BigDecimal,
    val annualRatePercent: BigDecimal,
    val tenureMonths: Int,
    val processingFeeFlat: BigDecimal = BigDecimal.ZERO
)

data class AmortizationMonth(
    val monthNumber: Int,
    val openingBalance: BigDecimal,
    val emi: BigDecimal,
    val principalComponent: BigDecimal,
    val interestComponent: BigDecimal,
    val closingBalance: BigDecimal
)

data class EmiCalculationResult(
    val loanType: LoanType,
    val principal: BigDecimal,
    val annualRatePercent: BigDecimal,
    val tenureMonths: Int,
    val monthlyEmi: BigDecimal,
    val totalInterest: BigDecimal,
    val totalRepayment: BigDecimal,
    val processingFee: BigDecimal,
    val amortizationSchedule: List<AmortizationMonth>,
    val assumptions: List<String>
) {
    init {
        // Enforce totalRepayment == principal + totalInterest invariant
        require(totalRepayment == principal.add(totalInterest)) {
            "Total repayment ($totalRepayment) must equal principal ($principal) + total interest ($totalInterest)"
        }
    }
}

class CalculateEmiUseCase {

    private val mathContext = MathContext(34, RoundingMode.HALF_UP)
    private val twelve = BigDecimal(12)
    private val hundred = BigDecimal(100)

    fun calculate(input: EmiCalculationInput): EmiCalculationResult {
        val l = input.principal
        val n = input.tenureMonths
        val ratePercent = input.annualRatePercent
        val fee = input.processingFeeFlat

        require(n > 0) { "Tenure months must be positive, was $n" }
        require(l > BigDecimal.ZERO) { "Loan amount must be positive, was $l" }
        require(ratePercent >= BigDecimal.ZERO) { "Interest rate cannot be negative, was $ratePercent" }
        require(ratePercent <= BigDecimal("100")) { "Interest rate cannot exceed 100%, was $ratePercent" }

        val monthlyEmiExact: BigDecimal
        val monthlyRate: BigDecimal

        if (ratePercent.compareTo(BigDecimal.ZERO) == 0) {
            monthlyRate = BigDecimal.ZERO
            monthlyEmiExact = l.divide(BigDecimal(n), mathContext)
        } else {
            val r = ratePercent.divide(hundred, mathContext)
            monthlyRate = r.divide(twelve, mathContext)
            val onePlusI = BigDecimal.ONE.add(monthlyRate, mathContext)
            val factor = onePlusI.pow(n, mathContext) // (1 + i)^n
            val numerator = l.multiply(monthlyRate, mathContext).multiply(factor, mathContext)
            val denominator = factor.subtract(BigDecimal.ONE, mathContext)
            monthlyEmiExact = numerator.divide(denominator, mathContext)
        }

        val displayEmi = monthlyEmiExact.setScale(0, RoundingMode.HALF_UP)
        val displayPrincipal = l.setScale(0, RoundingMode.HALF_UP)

        // Generate Amortization Schedule
        val schedule = mutableListOf<AmortizationMonth>()
        var balance = displayPrincipal
        var accumulatedInterest = BigDecimal.ZERO

        for (m in 1..n) {
            val opening = balance
            val interestComponent: BigDecimal
            val principalComponent: BigDecimal
            val payment: BigDecimal
            val closing: BigDecimal

            if (m == n) {
                // Final month: balance reaches exactly 0
                interestComponent = if (monthlyRate > BigDecimal.ZERO) {
                    opening.multiply(monthlyRate, mathContext).setScale(0, RoundingMode.HALF_UP)
                } else {
                    BigDecimal.ZERO
                }
                principalComponent = opening
                payment = principalComponent.add(interestComponent)
                closing = BigDecimal.ZERO
            } else {
                interestComponent = if (monthlyRate > BigDecimal.ZERO) {
                    opening.multiply(monthlyRate, mathContext).setScale(0, RoundingMode.HALF_UP)
                } else {
                    BigDecimal.ZERO
                }
                principalComponent = displayEmi.subtract(interestComponent).coerceAtMost(opening)
                payment = displayEmi
                closing = opening.subtract(principalComponent).coerceAtLeast(BigDecimal.ZERO)
            }

            accumulatedInterest = accumulatedInterest.add(interestComponent)
            balance = closing

            schedule.add(
                AmortizationMonth(
                    monthNumber = m,
                    openingBalance = opening,
                    emi = payment,
                    principalComponent = principalComponent,
                    interestComponent = interestComponent,
                    closingBalance = closing
                )
            )
        }

        val totalInterest = accumulatedInterest.setScale(0, RoundingMode.HALF_UP)
        val totalRepayment = displayPrincipal.add(totalInterest)

        val assumptions = listOf(
            "Standard reducing balance monthly compounding (i = annual rate / 12).",
            "Fixed interest rate assumption over the entire $n months.",
            "Timely regular monthly payments with no prepayment or default penalties.",
            "Processing fee is shown separately and is not capitalized into principal."
        )

        return EmiCalculationResult(
            loanType = input.loanType,
            principal = displayPrincipal,
            annualRatePercent = ratePercent,
            tenureMonths = n,
            monthlyEmi = displayEmi,
            totalInterest = totalInterest,
            totalRepayment = totalRepayment,
            processingFee = fee.setScale(0, RoundingMode.HALF_UP),
            amortizationSchedule = schedule,
            assumptions = assumptions
        )
    }
}
