package com.investkaro.domain

import com.investkaro.domain.usecase.CalculateLumpSumUseCase
import com.investkaro.domain.usecase.LumpSumCalculationInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class CalculateLumpSumUseCaseTest {

    private lateinit var useCase: CalculateLumpSumUseCase

    @Before
    fun setUp() {
        useCase = CalculateLumpSumUseCase()
    }

    @Test
    fun `lump sum reference case - 1 lakh for 5 years at 12 percent annual compounding`() {
        // A * (1.12)^5 = 100000 * 1.76234168 = 176234.168... -> 176234
        val input = LumpSumCalculationInput(
            initialAmount = BigDecimal("100000"),
            durationYears = 5,
            annualRatePercent = BigDecimal("12"),
            compoundingFrequencyPerYear = 1
        )

        val result = useCase.calculate(input)

        assertEquals(BigDecimal("100000"), result.totalInvested)
        assertEquals(BigDecimal("176234"), result.estimatedFutureValue)
        assertEquals(BigDecimal("76234"), result.estimatedGrowth)
        assertEquals(result.estimatedFutureValue.subtract(result.totalInvested), result.estimatedGrowth)
    }

    @Test
    fun `zero return lump sum yields zero growth and FV equals principal`() {
        val input = LumpSumCalculationInput(
            initialAmount = BigDecimal("500000"),
            durationYears = 10,
            annualRatePercent = BigDecimal("0")
        )

        val result = useCase.calculate(input)

        assertEquals(BigDecimal("500000"), result.totalInvested)
        assertEquals(BigDecimal("500000"), result.estimatedFutureValue)
        assertEquals(BigDecimal("0"), result.estimatedGrowth)
        assertEquals(result.estimatedFutureValue.subtract(result.totalInvested), result.estimatedGrowth)
    }

    @Test
    fun `reconciliation invariant holds across varied tenures`() {
        for (years in 1..25) {
            val input = LumpSumCalculationInput(
                initialAmount = BigDecimal("25000"),
                durationYears = years,
                annualRatePercent = BigDecimal("11.5")
            )
            val result = useCase.calculate(input)
            assertTrue(result.estimatedFutureValue >= result.totalInvested)
            assertEquals(result.estimatedFutureValue.subtract(result.totalInvested), result.estimatedGrowth)
        }
    }
}
