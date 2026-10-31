package com.investkaro.ui.invest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.investkaro.data.catalogue.InvestmentCatalogueRepository
import com.investkaro.domain.catalogue.CalculationModelIds
import com.investkaro.domain.catalogue.CatalogueItem
import com.investkaro.domain.catalogue.CatalogueStatus
import com.investkaro.domain.model.CalculationInput
import com.investkaro.domain.model.CalculationResult
import com.investkaro.domain.model.InvestmentDuration
import com.investkaro.domain.model.InvestmentType
import com.investkaro.domain.usecase.CalculateInvestmentUseCase
import com.investkaro.domain.usecase.CalculateLumpSumUseCase
import com.investkaro.domain.usecase.LumpSumCalculationInput
import com.investkaro.domain.usecase.LumpSumCalculationResult
import com.investkaro.formatting.IndianCurrencyFormatter
import com.investkaro.settings.ThemePreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

enum class CalculationMode(val title: String) {
    MONTHLY_SIP("Monthly SIP"),
    LUMP_SUM("Lump sum")
}

data class InvestKaroUiState(
    val calculationMode: CalculationMode = CalculationMode.MONTHLY_SIP,
    val selectedCatalogueItem: CatalogueItem = InvestmentCatalogueRepository.items.first(),
    val amountInput: String = "5000",
    val amountError: String? = null,
    val selectedDuration: InvestmentDuration = InvestmentDuration(InvestmentDuration.DEFAULT_YEARS),
    val lumpSumDurationYears: Int = 5,
    val rateInput: String = "12",
    val rateError: String? = null,
    val sipResult: CalculationResult? = null,
    val lumpSumResult: LumpSumCalculationResult? = null,
    val isDarkTheme: Boolean = false,
    val isAssumptionsExpanded: Boolean = false,
    val isCatalogueSheetOpen: Boolean = false
) {
    val formattedAmountPreview: String?
        get() {
            if (amountInput.isBlank() || amountError != null) return null
            val clean = IndianCurrencyFormatter.cleanNumericInput(amountInput)
            val parsed = clean.toBigDecimalOrNull() ?: return null
            return IndianCurrencyFormatter.formatRupees(parsed)
        }
}

class InvestKaroViewModel(
    private val sipUseCase: CalculateInvestmentUseCase = CalculateInvestmentUseCase(),
    private val lumpSumUseCase: CalculateLumpSumUseCase = CalculateLumpSumUseCase(),
    private val themeRepository: ThemePreferencesRepository? = null,
    initialIsDark: Boolean = false
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        InvestKaroUiState(
            isDarkTheme = initialIsDark
        )
    )
    val uiState: StateFlow<InvestKaroUiState> = _uiState.asStateFlow()

    init {
        recalculate(_uiState.value)

        themeRepository?.let { repo ->
            viewModelScope.launch {
                repo.isDarkThemeFlow.collect { persistedDark ->
                    if (persistedDark != null) {
                        _uiState.update { it.copy(isDarkTheme = persistedDark) }
                    }
                }
            }
        }
    }

    fun onModeChange(mode: CalculationMode) {
        _uiState.update { it.copy(calculationMode = mode) }
        recalculate(_uiState.value)
    }

    fun onCatalogueItemSelected(item: CatalogueItem) {
        _uiState.update {
            it.copy(
                selectedCatalogueItem = item,
                isCatalogueSheetOpen = false,
                calculationMode = if (item.modelId == CalculationModelIds.LUMP_SUM_COMPOUND) {
                    CalculationMode.LUMP_SUM
                } else {
                    CalculationMode.MONTHLY_SIP
                }
            )
        }
        recalculate(_uiState.value)
    }

    fun openCatalogueSheet() {
        _uiState.update { it.copy(isCatalogueSheetOpen = true) }
    }

    fun closeCatalogueSheet() {
        _uiState.update { it.copy(isCatalogueSheetOpen = false) }
    }

    fun onAmountChange(rawAmount: String) {
        _uiState.update { it.copy(amountInput = rawAmount) }
        recalculate(_uiState.value)
    }

    fun onDurationChange(duration: InvestmentDuration) {
        _uiState.update { it.copy(selectedDuration = duration) }
        recalculate(_uiState.value)
    }

    fun onLumpSumDurationChange(years: Int) {
        _uiState.update { it.copy(lumpSumDurationYears = years) }
        recalculate(_uiState.value)
    }

    fun onRateChange(rawRate: String) {
        _uiState.update { it.copy(rateInput = rawRate) }
        recalculate(_uiState.value)
    }

    fun toggleTheme() {
        val newDark = !_uiState.value.isDarkTheme
        _uiState.update { it.copy(isDarkTheme = newDark) }
        themeRepository?.let { repo ->
            viewModelScope.launch {
                repo.setDarkTheme(newDark)
            }
        }
    }

    fun toggleAssumptions() {
        _uiState.update { it.copy(isAssumptionsExpanded = !it.isAssumptionsExpanded) }
    }

    fun resetInputs() {
        _uiState.update {
            it.copy(
                amountInput = "",
                amountError = null,
                rateInput = "",
                rateError = null,
                selectedDuration = InvestmentDuration(InvestmentDuration.DEFAULT_YEARS),
                lumpSumDurationYears = 5,
                sipResult = null,
                lumpSumResult = null,
                isAssumptionsExpanded = false
            )
        }
    }

    private fun recalculate(state: InvestKaroUiState) {
        val item = state.selectedCatalogueItem
        if (item.status != CatalogueStatus.CALCULATOR_SUPPORTED) {
            _uiState.update {
                it.copy(
                    amountError = null,
                    rateError = null,
                    sipResult = null,
                    lumpSumResult = null
                )
            }
            return
        }

        val cleanAmount = IndianCurrencyFormatter.cleanNumericInput(state.amountInput)
        val cleanRate = state.rateInput.trim().replace("%", "").trim()

        if (cleanAmount.isEmpty() && cleanRate.isEmpty()) {
            _uiState.update {
                it.copy(amountError = null, rateError = null, sipResult = null, lumpSumResult = null)
            }
            return
        }

        var amountError: String? = null
        var parsedAmount: BigDecimal? = null

        if (cleanAmount.isEmpty()) {
            amountError = "Enter an investment amount."
        } else {
            try {
                val amount = BigDecimal(cleanAmount)
                if (amount <= BigDecimal.ZERO) {
                    amountError = "Amount must be greater than ₹0."
                } else if (amount > BigDecimal("1000000000")) {
                    amountError = "Amount exceeds supported limit."
                } else {
                    parsedAmount = amount
                }
            } catch (_: NumberFormatException) {
                amountError = "Invalid number format."
            }
        }

        var rateError: String? = null
        var parsedRate: BigDecimal? = null

        if (cleanRate.isEmpty()) {
            rateError = "Enter expected return percentage."
        } else {
            try {
                val rate = BigDecimal(cleanRate)
                if (rate < BigDecimal("-100") || rate > BigDecimal("100")) {
                    rateError = "Expected return must be between -100% and 100%."
                } else {
                    parsedRate = rate
                }
            } catch (_: NumberFormatException) {
                rateError = "Invalid percentage format."
            }
        }

        if (amountError != null || rateError != null || parsedAmount == null || parsedRate == null) {
            _uiState.update {
                it.copy(
                    amountError = amountError,
                    rateError = rateError,
                    sipResult = null,
                    lumpSumResult = null
                )
            }
            return
        }

        if (state.calculationMode == CalculationMode.MONTHLY_SIP) {
            val input = CalculationInput(
                monthlyAmount = parsedAmount,
                duration = state.selectedDuration,
                annualRatePercent = parsedRate,
                investmentType = InvestmentType.MUTUAL_FUND_SIP
            )
            val result = sipUseCase.calculate(input)
            _uiState.update {
                it.copy(
                    amountError = null,
                    rateError = null,
                    sipResult = result,
                    lumpSumResult = null
                )
            }
        } else {
            val input = LumpSumCalculationInput(
                initialAmount = parsedAmount,
                durationYears = state.lumpSumDurationYears,
                annualRatePercent = parsedRate
            )
            val result = lumpSumUseCase.calculate(input)
            _uiState.update {
                it.copy(
                    amountError = null,
                    rateError = null,
                    sipResult = null,
                    lumpSumResult = result
                )
            }
        }
    }

    class Factory(
        private val themeRepository: ThemePreferencesRepository,
        private val systemDark: Boolean
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InvestKaroViewModel(
                themeRepository = themeRepository,
                initialIsDark = systemDark
            ) as T
        }
    }
}
