package com.investkaro.loan

import com.investkaro.domain.loan.CalculateEmiUseCase
import com.investkaro.domain.loan.EmiCalculationInput
import com.investkaro.domain.loan.LoanType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class CalculateEmiUseCaseTest {

    private lateinit var useCase: CalculateEmiUseCase

    @Before
    fun setUp() {
        useCase = CalculateEmiUseCase()
    }

    /**
     * Reference Case 1:
     * Home loan: Principal = 25,00,000, Annual Rate = 8.5%, Tenure = 240 months (20 years).
     * Monthly rate i = 0.085 / 12 = 0.007083333333333333
     * (1 + i)^240 = 5.4412433...
     * Monthly EMI = 2500000 * (0.007083333333 * 5.4412433 / 4.4412433) = 21695.59... -> ₹21,696
     */
    @Test
    fun `reference case 1 - home loan 25 lakhs at 8 point 5 percent for 20 years`() {
        val input = EmiCalculationInput(
            loanType = LoanType.HOME_LOAN,
            principal = BigDecimal("2500000"),
            annualRatePercent = BigDecimal("8.5"),
            tenureMonths = 240,
            processingFeeFlat = BigDecimal("5000")
        )

        val result = useCase.calculate(input)

        assertEquals(BigDecimal("21696"), result.monthlyEmi)
        assertEquals(BigDecimal("2500000"), result.principal)
        assertEquals(240, result.amortizationSchedule.size)
        assertEquals(BigDecimal("5000"), result.processingFee)

        // Reconciliation: totalRepayment == principal + totalInterest
        assertEquals(result.principal.add(result.totalInterest), result.totalRepayment)

        // Final month balance must reach strictly zero
        assertEquals(BigDecimal.ZERO, result.amortizationSchedule.last().closingBalance)
    }

    /**
     * Reference Case 2:
     * Zero-interest loan: Principal = 1,20,000, Annual Rate = 0%, Tenure = 12 months.
     * EMI = 120000 / 12 = 10,000.
     * Total interest = 0.
     * Total repayment = 1,20,000.
     */
    @Test
    fun `zero interest loan produces exact linear monthly payment and zero interest`() {
        val input = EmiCalculationInput(
            loanType = LoanType.PERSONAL_LOAN,
            principal = BigDecimal("120000"),
            annualRatePercent = BigDecimal("0"),
            tenureMonths = 12
        )

        val result = useCase.calculate(input)

        assertEquals(BigDecimal("10000"), result.monthlyEmi)
        assertEquals(BigDecimal("0"), result.totalInterest)
        assertEquals(BigDecimal("120000"), result.totalRepayment)
        assertEquals(BigDecimal.ZERO, result.amortizationSchedule.last().closingBalance)
    }

    /**
     * Test Amortization Consistency:
     * The sum of all principal repayments across all months must equal the initial loan principal!
     */
    @Test
    fun `amortization schedule principal components sum exactly to original loan principal`() {
        val input = EmiCalculationInput(
            loanType = LoanType.CAR_LOAN,
            principal = BigDecimal("800000"),
            annualRatePercent = BigDecimal("9.25"),
            tenureMonths = 60
        )

        val result = useCase.calculate(input)

        var totalPrincipalRepaid = BigDecimal.ZERO
        for (month in result.amortizationSchedule) {
            totalPrincipalRepaid = totalPrincipalRepaid.add(month.principalComponent)
            assertTrue("Principal component cannot be negative", month.principalComponent >= BigDecimal.ZERO)
            assertTrue("Interest component cannot be negative", month.interestComponent >= BigDecimal.ZERO)
        }

        assertEquals(BigDecimal("800000"), totalPrincipalRepaid)
        assertEquals(BigDecimal.ZERO, result.amortizationSchedule.last().closingBalance)
    }

    /**
     * Short and long tenure extremes: 1 month and 360 months (30 years).
     */
    @Test
    fun `short and long tenures calculate without crash or infinity`() {
        val oneMonth = useCase.calculate(
            EmiCalculationInput(
                loanType = LoanType.GOLD_LOAN,
                principal = BigDecimal("50000"),
                annualRatePercent = BigDecimal("10.0"),
                tenureMonths = 1
            )
        )
        assertEquals(1, oneMonth.amortizationSchedule.size)
        assertEquals(BigDecimal.ZERO, oneMonth.amortizationSchedule.last().closingBalance)

        val thirtyYears = useCase.calculate(
            EmiCalculationInput(
                loanType = LoanType.HOME_LOAN,
                principal = BigDecimal("5000000"),
                annualRatePercent = BigDecimal("8.75"),
                tenureMonths = 360
            )
        )
        assertEquals(360, thirtyYears.amortizationSchedule.size)
        assertEquals(BigDecimal.ZERO, thirtyYears.amortizationSchedule.last().closingBalance)
        assertTrue(thirtyYears.totalInterest > thirtyYears.principal) // Over 30 years interest exceeds principal
    }

    /**
     * Processing fee is kept separate and not added to principal.
     */
    @Test
    fun `processing fee remains separate and does not inflate loan principal`() {
        val input = EmiCalculationInput(
            loanType = LoanType.BUSINESS_LOAN,
            principal = BigDecimal("1000000"),
            annualRatePercent = BigDecimal("14.0"),
            tenureMonths = 36,
            processingFeeFlat = BigDecimal("20000")
        )

        val result = useCase.calculate(input)

        assertEquals(BigDecimal("1000000"), result.principal)
        assertEquals(BigDecimal("20000"), result.processingFee)
        assertEquals(result.principal.add(result.totalInterest), result.totalRepayment)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `zero principal throws IllegalArgumentException`() {
        useCase.calculate(
            EmiCalculationInput(
                loanType = LoanType.HOME_LOAN,
                principal = BigDecimal.ZERO,
                annualRatePercent = BigDecimal("8.5"),
                tenureMonths = 12
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative principal throws IllegalArgumentException`() {
        useCase.calculate(
            EmiCalculationInput(
                loanType = LoanType.HOME_LOAN,
                principal = BigDecimal("-50000"),
                annualRatePercent = BigDecimal("8.5"),
                tenureMonths = 12
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `zero tenure months throws IllegalArgumentException`() {
        useCase.calculate(
            EmiCalculationInput(
                loanType = LoanType.HOME_LOAN,
                principal = BigDecimal("100000"),
                annualRatePercent = BigDecimal("8.5"),
                tenureMonths = 0
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `negative rate throws IllegalArgumentException`() {
        useCase.calculate(
            EmiCalculationInput(
                loanType = LoanType.HOME_LOAN,
                principal = BigDecimal("100000"),
                annualRatePercent = BigDecimal("-2.5"),
                tenureMonths = 12
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rate above 100 percent throws IllegalArgumentException`() {
        useCase.calculate(
            EmiCalculationInput(
                loanType = LoanType.HOME_LOAN,
                principal = BigDecimal("100000"),
                annualRatePercent = BigDecimal("105"),
                tenureMonths = 12
            )
        )
    }
}
