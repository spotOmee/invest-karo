package com.investkaro.ui.calculator

import com.investkaro.domain.model.InvestmentDuration
import com.investkaro.domain.model.InvestmentType
import com.investkaro.domain.usecase.CalculateInvestmentUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class CalculatorViewModelTest {

    private lateinit var viewModel: CalculatorViewModel

    @Before
    fun setUp() {
        viewModel = CalculatorViewModel(
            calculateUseCase = CalculateInvestmentUseCase(),
            themeRepository = null,
            initialIsDark = false
        )
    }

    @Test
    fun `initial state has default values and valid calculation`() {
        val state = viewModel.uiState.value
        assertEquals("5000", state.amountInput)
        assertEquals("12", state.rateInput)
        assertEquals(5, state.selectedDuration.years)
        assertEquals(InvestmentType.MUTUAL_FUND_SIP, state.selectedInvestmentType)
        assertNotNull(state.calculationResult)
        assertEquals(BigDecimal("408348"), state.calculationResult?.estimatedFutureValue)
        assertEquals(BigDecimal("300000"), state.calculationResult?.totalInvested)
        assertEquals(BigDecimal("108348"), state.calculationResult?.estimatedGrowth)
    }

    @Test
    fun `changing amount updates result immediately`() {
        viewModel.onAmountChange("10000")
        val state = viewModel.uiState.value
        assertEquals("10000", state.amountInput)
        assertNotNull(state.calculationResult)
        // 10000 * ((1.01^60 - 1)/0.01) = 816696.69... -> 816697
        assertEquals(BigDecimal("816697"), state.calculationResult?.estimatedFutureValue)
        assertEquals(BigDecimal("600000"), state.calculationResult?.totalInvested)
        assertEquals(BigDecimal("216697"), state.calculationResult?.estimatedGrowth)
    }

    @Test
    fun `changing duration updates result immediately`() {
        viewModel.onDurationChange(InvestmentDuration(10))
        val state = viewModel.uiState.value
        assertEquals(10, state.selectedDuration.years)
        assertEquals(120, state.calculationResult?.totalMonths)
        // 5000 * ((1.01^120 - 1)/0.01) = 5000 * 230.038689... = 1150193.44... -> 1150193
        assertEquals(BigDecimal("1150193"), state.calculationResult?.estimatedFutureValue)
        assertEquals(BigDecimal("600000"), state.calculationResult?.totalInvested)
        assertEquals(BigDecimal("550193"), state.calculationResult?.estimatedGrowth)
    }

    @Test
    fun `changing rate updates result immediately`() {
        viewModel.onRateChange("15")
        val state = viewModel.uiState.value
        assertEquals("15", state.rateInput)
        assertNotNull(state.calculationResult)
        assertEquals(BigDecimal("15"), state.calculationResult?.annualRatePercent)
    }

    @Test
    fun `invalid amount sets error and clears calculation result`() {
        viewModel.onAmountChange("0")
        val state = viewModel.uiState.value
        assertNotNull(state.amountError)
        assertNull(state.calculationResult)

        viewModel.onAmountChange("-100")
        val state2 = viewModel.uiState.value
        assertNotNull(state2.amountError)
        assertNull(state2.calculationResult)
    }

    @Test
    fun `invalid rate sets error and clears calculation result`() {
        viewModel.onRateChange("150")
        val state = viewModel.uiState.value
        assertNotNull(state.rateError)
        assertNull(state.calculationResult)
    }

    @Test
    fun `reset clears inputs to empty without crashing`() {
        viewModel.resetInputs()
        val state = viewModel.uiState.value
        assertEquals("", state.amountInput)
        assertEquals("", state.rateInput)
        assertNull(state.amountError)
        assertNull(state.rateError)
        assertNull(state.calculationResult)
        assertEquals(InvestmentDuration(5), state.selectedDuration)
        assertEquals(InvestmentType.DEFAULT, state.selectedInvestmentType)
    }

    @Test
    fun `toggle assumptions updates expanded flag`() {
        val initial = viewModel.uiState.value.isAssumptionsExpanded
        viewModel.toggleAssumptions()
        assertEquals(!initial, viewModel.uiState.value.isAssumptionsExpanded)
        viewModel.toggleAssumptions()
        assertEquals(initial, viewModel.uiState.value.isAssumptionsExpanded)
    }

    @Test
    fun `changing investment type updates state and recalculates`() {
        viewModel.onInvestmentTypeChange(InvestmentType.INDEX_FUND_SIP)
        val state = viewModel.uiState.value
        assertEquals(InvestmentType.INDEX_FUND_SIP, state.selectedInvestmentType)
        assertEquals(InvestmentType.INDEX_FUND_SIP, state.calculationResult?.investmentType)
    }
}
