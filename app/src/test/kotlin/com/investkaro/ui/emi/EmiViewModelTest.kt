package com.investkaro.ui.emi

import com.investkaro.domain.loan.CalculateEmiUseCase
import com.investkaro.domain.loan.TenureUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class EmiViewModelTest {

    private lateinit var viewModel: EmiViewModel

    @Before
    fun setUp() {
        viewModel = EmiViewModel(CalculateEmiUseCase())
    }

    @Test
    fun `initial state has valid default loan calculation`() {
        val state = viewModel.uiState.value
        assertEquals("2500000", state.principalInput)
        assertEquals("8.5", state.rateInput)
        assertEquals("20", state.tenureValueInput)
        assertEquals(TenureUnit.YEARS, state.tenureUnit)
        assertNotNull(state.emiResult)
        assertEquals(BigDecimal("21696"), state.emiResult?.monthlyEmi)
        assertTrue(state.isInputValid)
    }

    @Test
    fun `zero interest reference case produces exact linear repayment`() {
        viewModel.onPrincipalChange("120000")
        viewModel.onRateChange("0")
        viewModel.onTenureUnitChange(TenureUnit.MONTHS)
        viewModel.onTenureValueChange("12")

        val state = viewModel.uiState.value
        assertNotNull(state.emiResult)
        val res = state.emiResult!!
        assertEquals(BigDecimal("10000"), res.monthlyEmi)
        assertEquals(BigDecimal("0"), res.totalInterest)
        assertEquals(BigDecimal("120000"), res.totalRepayment)
        assertEquals(res.principal.add(res.totalInterest), res.totalRepayment)
    }

    @Test
    fun `positive interest reference case 25 lakhs at 8 point 5 percent for 20 years`() {
        viewModel.onPrincipalChange("2500000")
        viewModel.onRateChange("8.5")
        viewModel.onTenureValueChange("20")
        viewModel.onTenureUnitChange(TenureUnit.YEARS)

        val state = viewModel.uiState.value
        assertNotNull(state.emiResult)
        val res = state.emiResult!!
        assertEquals(BigDecimal("21696"), res.monthlyEmi)
        assertEquals(240, res.tenureMonths)
        assertEquals(res.principal.add(res.totalInterest), res.totalRepayment)
    }

    @Test
    fun `years to months toggle correctly converts tenure`() {
        viewModel.onTenureValueChange("5")
        viewModel.onTenureUnitChange(TenureUnit.YEARS)
        assertEquals(60, viewModel.uiState.value.totalTenureMonths)

        viewModel.onTenureUnitChange(TenureUnit.MONTHS)
        assertEquals("60", viewModel.uiState.value.tenureValueInput)
        assertEquals(60, viewModel.uiState.value.totalTenureMonths)

        viewModel.onTenureUnitChange(TenureUnit.YEARS)
        assertEquals("5", viewModel.uiState.value.tenureValueInput)
        assertEquals(60, viewModel.uiState.value.totalTenureMonths)
    }

    @Test
    fun `decimal years tenure gracefully computes total months without crashing`() {
        viewModel.onPrincipalChange("100000")
        viewModel.onRateChange("10")
        viewModel.onTenureUnitChange(TenureUnit.YEARS)
        viewModel.onTenureValueChange("2.5")

        val state = viewModel.uiState.value
        assertEquals(30, state.totalTenureMonths)
        assertNull(state.tenureError)
        assertNotNull(state.emiResult)
        assertEquals(30, state.emiResult?.tenureMonths)
    }

    @Test
    fun `zero or negative principal produces inline error and null result`() {
        viewModel.onPrincipalChange("0")
        var state = viewModel.uiState.value
        assertNotNull(state.principalError)
        assertNull(state.emiResult)
        assertFalse(state.isInputValid)

        viewModel.onPrincipalChange("-5000")
        state = viewModel.uiState.value
        assertNotNull(state.principalError)
        assertNull(state.emiResult)
    }

    @Test
    fun `zero or negative tenure produces inline error`() {
        viewModel.onTenureValueChange("0")
        var state = viewModel.uiState.value
        assertNotNull(state.tenureError)
        assertNull(state.emiResult)

        viewModel.onTenureValueChange("-12")
        state = viewModel.uiState.value
        assertNotNull(state.tenureError)
        assertNull(state.emiResult)
    }

    @Test
    fun `rate boundary checks - rejects negative and greater than 100 percent`() {
        viewModel.onRateChange("-5")
        var state = viewModel.uiState.value
        assertNotNull(state.rateError)
        assertNull(state.emiResult)

        viewModel.onRateChange("150")
        state = viewModel.uiState.value
        assertNotNull(state.rateError)
        assertNull(state.emiResult)

        viewModel.onRateChange("100")
        state = viewModel.uiState.value
        assertNull(state.rateError)
        assertNotNull(state.emiResult)
    }

    @Test
    fun `empty input clears calculation without showing misleading zero`() {
        viewModel.resetInputs()
        val state = viewModel.uiState.value
        assertEquals("", state.principalInput)
        assertEquals("", state.rateInput)
        assertEquals("", state.tenureValueInput)
        assertNull(state.emiResult)
        assertFalse(state.isInputValid)
    }

    @Test
    fun `reconciliation invariant holds for fractional monthly emi across various parameters`() {
        val testCases = listOf(
            Triple("500000", "7.25", "7"),
            Triple("1500000", "11.5", "15"),
            Triple("350000", "14.0", "3"),
            Triple("8000000", "9.0", "25")
        )

        for ((p, r, t) in testCases) {
            viewModel.onPrincipalChange(p)
            viewModel.onRateChange(r)
            viewModel.onTenureValueChange(t)
            viewModel.onTenureUnitChange(TenureUnit.YEARS)

            val state = viewModel.uiState.value
            assertNotNull("Expected valid result for P=$p, r=$r, t=$t", state.emiResult)
            val res = state.emiResult!!
            assertEquals("Total repayment must equal principal + total interest", res.principal.add(res.totalInterest), res.totalRepayment)
            assertTrue("Total interest cannot be negative", res.totalInterest >= BigDecimal.ZERO)
            assertTrue("Monthly EMI must be positive", res.monthlyEmi > BigDecimal.ZERO)
        }
    }
}
