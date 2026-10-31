package com.investkaro.domain

import com.investkaro.domain.model.CalculationInput
import com.investkaro.domain.model.InputValidationState
import com.investkaro.domain.model.InvestmentDuration
import com.investkaro.domain.model.InvestmentType
import com.investkaro.domain.usecase.CalculateInvestmentUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class CalculateInvestmentUseCaseTest {

    private lateinit var useCase: CalculateInvestmentUseCase

    @Before
    fun setUp() {
        useCase = CalculateInvestmentUseCase()
    }

    /**
     * Reference Case 1:
     * P = 5,000, n = 5 years (60 months), r = 12% (0.12).
     * Monthly rate i = 0.12 / 12 = 0.01.
     * Compound factor (1.01)^60 = 1.8166966986...
     * FV = 5000 * ((1.01^60 - 1) / 0.01) = 5000 * 81.66966986 = 408348.349...
     * Display rounded: ₹4,08,348
     * Total invested = 5000 * 60 = ₹3,00,000
     * Estimated growth = 408348 - 300000 = ₹1,08,348
     */
    @Test
    fun `reference case 1 - 5000 per month for 5 years at 12 percent`() {
        val input = CalculationInput(
            monthlyAmount = BigDecimal("5000"),
            duration = InvestmentDuration(5),
            annualRatePercent = BigDecimal("12"),
            investmentType = InvestmentType.MUTUAL_FUND_SIP
        )

        val result = useCase.calculate(input)

        assertEquals(BigDecimal("300000"), result.totalInvested)
        assertEquals(BigDecimal("408348"), result.estimatedFutureValue)
        assertEquals(BigDecimal("108348"), result.estimatedGrowth)
        assertEquals(60, result.totalMonths)
        // Reconciliation invariant: growth == FV - total
        assertEquals(result.estimatedFutureValue.subtract(result.totalInvested), result.estimatedGrowth)
    }

    /**
     * Reference Case 2:
     * P = 10,000, n = 10 years (120 months), r = 15% (0.15).
     * i = 0.15 / 12 = 0.0125.
     * Compound factor (1.0125)^120 = 4.440213231...
     * FV = 10000 * (3.440213231 / 0.0125) = 10000 * 275.2170585 = 2752170.58...
     * Display rounded: ₹27,52,171
     * Total invested = 10000 * 120 = ₹12,00,000
     * Estimated growth = 2752171 - 1200000 = ₹15,52,171
     */
    @Test
    fun `reference case 2 - 10000 per month for 10 years at 15 percent`() {
        val input = CalculationInput(
            monthlyAmount = BigDecimal("10000"),
            duration = InvestmentDuration(10),
            annualRatePercent = BigDecimal("15"),
            investmentType = InvestmentType.INDEX_FUND_SIP
        )

        val result = useCase.calculate(input)

        assertEquals(BigDecimal("1200000"), result.totalInvested)
        assertEquals(BigDecimal("2752171"), result.estimatedFutureValue)
        assertEquals(BigDecimal("1552171"), result.estimatedGrowth)
        assertEquals(120, result.totalMonths)
        assertEquals(result.estimatedFutureValue.subtract(result.totalInvested), result.estimatedGrowth)
    }

    /**
     * Reference Case 3:
     * Zero annual return (r = 0%).
     * P = 5,000, n = 3 years (36 months).
     * FV = P * n = 5000 * 36 = 180,000.
     * Total invested = 180,000.
     * Estimated growth = 0.
     */
    @Test
    fun `zero annual return yields zero growth and FV equals total invested`() {
        val input = CalculationInput(
            monthlyAmount = BigDecimal("5000"),
            duration = InvestmentDuration(3),
            annualRatePercent = BigDecimal("0"),
            investmentType = InvestmentType.RECURRING_INVESTMENT
        )

        val result = useCase.calculate(input)

        assertEquals(BigDecimal("180000"), result.totalInvested)
        assertEquals(BigDecimal("180000"), result.estimatedFutureValue)
        assertEquals(BigDecimal("0"), result.estimatedGrowth)
        assertEquals(36, result.totalMonths)
        assertEquals(result.estimatedFutureValue.subtract(result.totalInvested), result.estimatedGrowth)
    }

    /**
     * Reference Case 4:
     * Negative annual return within supported range (-10%).
     * P = 5,000, n = 1 year (12 months), r = -10%.
     * i = -0.10 / 12 = -0.008333333333...
     * 1 + i = 0.991666666667...
     * (1 + i)^12 = 0.904382...
     * FV = 5000 * ((0.904382 - 1) / -0.00833333) ≈ 57370.76 -> ₹57,371.
     * Total invested = 5000 * 12 = 60,000.
     * Estimated growth = 57371 - 60000 = -₹2,629.
     */
    @Test
    fun `negative annual return within supported range calculates capital erosion properly`() {
        val input = CalculationInput(
            monthlyAmount = BigDecimal("5000"),
            duration = InvestmentDuration(1),
            annualRatePercent = BigDecimal("-10"),
            investmentType = InvestmentType.MUTUAL_FUND_SIP
        )

        val result = useCase.calculate(input)

        assertEquals(BigDecimal("60000"), result.totalInvested)
        assertEquals(BigDecimal("57325"), result.estimatedFutureValue)
        assertEquals(BigDecimal("-2675"), result.estimatedGrowth)
        assertEquals(12, result.totalMonths)
        assertEquals(result.estimatedFutureValue.subtract(result.totalInvested), result.estimatedGrowth)
    }

    /**
     * Decimal return percentages e.g. 10.5%, 12.75%, 8.25%.
     */
    @Test
    fun `decimal return percentages calculate accurately`() {
        val input = CalculationInput(
            monthlyAmount = BigDecimal("2500"),
            duration = InvestmentDuration(5),
            annualRatePercent = BigDecimal("10.5"),
            investmentType = InvestmentType.MUTUAL_FUND_SIP
        )

        val result = useCase.calculate(input)

        // 60 months, Total invested = 2500 * 60 = 150000
        assertEquals(BigDecimal("150000"), result.totalInvested)
        assertTrue(result.estimatedFutureValue > result.totalInvested)
        assertEquals(result.estimatedFutureValue.subtract(result.totalInvested), result.estimatedGrowth)
    }

    /**
     * Test all durations from 1 to 10 years and exact contribution count (years * 12).
     */
    @Test
    fun `each duration from 1 to 10 years has exact contribution count`() {
        for (years in 1..10) {
            val duration = InvestmentDuration(years)
            assertEquals(years * 12, duration.totalMonths)

            val input = CalculationInput(
                monthlyAmount = BigDecimal("1000"),
                duration = duration,
                annualRatePercent = BigDecimal("12"),
                investmentType = InvestmentType.MUTUAL_FUND_SIP
            )

            val result = useCase.calculate(input)
            assertEquals(years * 12, result.totalMonths)
            assertEquals(BigDecimal(1000 * years * 12), result.totalInvested)
            assertTrue("FV must exceed total invested for positive return", result.estimatedFutureValue > result.totalInvested)
            assertEquals(result.estimatedFutureValue.subtract(result.totalInvested), result.estimatedGrowth)
        }
    }

    /**
     * Test zero monthly contribution validation.
     */
    @Test
    fun `zero monthly contribution produces validation error`() {
        val state = useCase.validateInputs(
            rawAmount = "0",
            rawRate = "12",
            duration = InvestmentDuration(5),
            type = InvestmentType.MUTUAL_FUND_SIP
        )

        assertTrue(state is InputValidationState.Invalid)
        val invalid = state as InputValidationState.Invalid
        assertNotNull(invalid.amountError)
        assertTrue(invalid.amountError!!.contains("greater than ₹0"))
    }

    /**
     * Test negative monthly contribution validation.
     */
    @Test
    fun `negative monthly contribution produces validation error`() {
        val state = useCase.validateInputs(
            rawAmount = "-500",
            rawRate = "12",
            duration = InvestmentDuration(5),
            type = InvestmentType.MUTUAL_FUND_SIP
        )

        assertTrue(state is InputValidationState.Invalid)
        val invalid = state as InputValidationState.Invalid
        assertNotNull(invalid.amountError)
    }

    /**
     * Test empty and malformed amount and percentage input.
     */
    @Test
    fun `empty inputs produce idle or validation errors`() {
        val emptyBoth = useCase.validateInputs(
            rawAmount = "",
            rawRate = "",
            duration = InvestmentDuration(5),
            type = InvestmentType.MUTUAL_FUND_SIP
        )
        assertTrue(emptyBoth is InputValidationState.Idle)

        val malformedAmount = useCase.validateInputs(
            rawAmount = "abc",
            rawRate = "12",
            duration = InvestmentDuration(5),
            type = InvestmentType.MUTUAL_FUND_SIP
        )
        assertTrue(malformedAmount is InputValidationState.Invalid)
        assertNotNull((malformedAmount as InputValidationState.Invalid).amountError)

        val malformedRate = useCase.validateInputs(
            rawAmount = "5000",
            rawRate = "xyz",
            duration = InvestmentDuration(5),
            type = InvestmentType.MUTUAL_FUND_SIP
        )
        assertTrue(malformedRate is InputValidationState.Invalid)
        assertNotNull((malformedRate as InputValidationState.Invalid).rateError)
    }

    /**
     * Test invalid annual return boundaries (< -100% or > 100%).
     */
    @Test
    fun `annual rate outside supported range is rejected with clear error`() {
        val rateTooLow = useCase.validateInputs(
            rawAmount = "5000",
            rawRate = "-101",
            duration = InvestmentDuration(5),
            type = InvestmentType.MUTUAL_FUND_SIP
        )
        assertTrue(rateTooLow is InputValidationState.Invalid)
        assertTrue((rateTooLow as InputValidationState.Invalid).rateError!!.contains("between -100% and 100%"))

        val rateTooHigh = useCase.validateInputs(
            rawAmount = "5000",
            rawRate = "105",
            duration = InvestmentDuration(5),
            type = InvestmentType.MUTUAL_FUND_SIP
        )
        assertTrue(rateTooHigh is InputValidationState.Invalid)
        assertTrue((rateTooHigh as InputValidationState.Invalid).rateError!!.contains("between -100% and 100%"))

        // Boundary -100% is valid
        val minBoundary = useCase.validateInputs(
            rawAmount = "5000",
            rawRate = "-100",
            duration = InvestmentDuration(5),
            type = InvestmentType.MUTUAL_FUND_SIP
        )
        assertTrue(minBoundary is InputValidationState.Valid)

        // Boundary 100% is valid
        val maxBoundary = useCase.validateInputs(
            rawAmount = "5000",
            rawRate = "100",
            duration = InvestmentDuration(5),
            type = InvestmentType.MUTUAL_FUND_SIP
        )
        assertTrue(maxBoundary is InputValidationState.Valid)
    }

    /**
     * Test comma-formatted amounts and percentage signs are cleaned and parsed properly.
     */
    @Test
    fun `inputs with commas and symbols parse cleanly`() {
        val state = useCase.validateInputs(
            rawAmount = "₹ 50,000",
            rawRate = "12.5%",
            duration = InvestmentDuration(5),
            type = InvestmentType.MUTUAL_FUND_SIP
        )
        assertTrue(state is InputValidationState.Valid)
        val valid = state as InputValidationState.Valid
        assertEquals(BigDecimal("50000"), valid.input.monthlyAmount)
        assertEquals(BigDecimal("12.5"), valid.input.annualRatePercent)
    }

    /**
     * Test large but valid values do not crash or produce NaN/Infinity.
     */
    @Test
    fun `large values execute with high precision without overflowing`() {
        val input = CalculationInput(
            monthlyAmount = BigDecimal("10000000"), // 1 Crore / month
            duration = InvestmentDuration(10),
            annualRatePercent = BigDecimal("25"),
            investmentType = InvestmentType.MUTUAL_FUND_SIP
        )
        val result = useCase.calculate(input)
        assertTrue(result.estimatedFutureValue > BigDecimal.ZERO)
        assertEquals(result.estimatedFutureValue.subtract(result.totalInvested), result.estimatedGrowth)
    }
}
