package com.investkaro.ui.invest

import com.investkaro.domain.model.InvestmentDuration
import com.investkaro.domain.usecase.CalculateInvestmentUseCase
import com.investkaro.domain.usecase.CalculateLumpSumUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class InvestKaroViewModelTest {

    private lateinit var viewModel: InvestKaroViewModel

    @Before
    fun setUp() {
        viewModel = InvestKaroViewModel(
            sipUseCase = CalculateInvestmentUseCase(),
            lumpSumUseCase = CalculateLumpSumUseCase()
        )
    }

    @Test
    fun `initial state has valid default SIP calculation`() {
        val state = viewModel.uiState.value
        assertEquals("5000", state.amountInput)
        assertEquals("12", state.rateInput)
        assertEquals(5, state.selectedDuration.years)
        assertEquals(CalculationMode.MONTHLY_SIP, state.calculationMode)
        assertNotNull(state.sipResult)
        assertEquals(BigDecimal("300000"), state.sipResult?.totalInvested)
        assertTrue((state.sipResult?.estimatedFutureValue ?: BigDecimal.ZERO) > BigDecimal("300000"))
    }

    @Test
    fun `mode switch to Lump Sum calculates lump sum projection`() {
        viewModel.onModeChange(CalculationMode.LUMP_SUM)
        val state = viewModel.uiState.value
        assertEquals(CalculationMode.LUMP_SUM, state.calculationMode)
        assertNotNull(state.lumpSumResult)
        assertEquals(BigDecimal("5000"), state.lumpSumResult?.totalInvested)
    }

    @Test
    fun `invalid amount sets error and clears result`() {
        viewModel.onAmountChange("0")
        var state = viewModel.uiState.value
        assertNotNull(state.amountError)
        assertNull(state.sipResult)

        viewModel.onAmountChange("-100")
        state = viewModel.uiState.value
        assertNotNull(state.amountError)
        assertNull(state.sipResult)
    }

    @Test
    fun `invalid rate sets error and clears result`() {
        viewModel.onRateChange("150")
        var state = viewModel.uiState.value
        assertNotNull(state.rateError)
        assertNull(state.sipResult)

        viewModel.onRateChange("-150")
        state = viewModel.uiState.value
        assertNotNull(state.rateError)
        assertNull(state.sipResult)
    }

    @Test
    fun `reset clears inputs and calculation results`() {
        viewModel.onAmountChange("25000")
        viewModel.onRateChange("15")
        viewModel.resetInputs()
        val state = viewModel.uiState.value
        assertEquals("", state.amountInput)
        assertEquals("", state.rateInput)
        assertNull(state.sipResult)
    }
}
