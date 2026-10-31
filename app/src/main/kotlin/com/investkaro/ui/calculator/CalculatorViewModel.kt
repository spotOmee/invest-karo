package com.investkaro.ui.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.investkaro.domain.model.InputValidationState
import com.investkaro.domain.model.InvestmentDuration
import com.investkaro.domain.model.InvestmentType
import com.investkaro.domain.usecase.CalculateInvestmentUseCase
import com.investkaro.settings.ThemePreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel(
    private val calculateUseCase: CalculateInvestmentUseCase = CalculateInvestmentUseCase(),
    private val themeRepository: ThemePreferencesRepository? = null,
    initialIsDark: Boolean = false
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CalculatorUiState(
            amountInput = "5000",
            rateInput = "12",
            isDarkTheme = initialIsDark
        )
    )
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        // Run initial calculation for default values
        recalculate(_uiState.value)

        // Observe theme preferences if repository is available
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

    fun onAmountChange(rawAmount: String) {
        _uiState.update { it.copy(amountInput = rawAmount) }
        recalculate(_uiState.value)
    }

    fun onInvestmentTypeChange(newType: InvestmentType) {
        _uiState.update { it.copy(selectedInvestmentType = newType) }
        recalculate(_uiState.value)
    }

    fun onDurationChange(duration: InvestmentDuration) {
        _uiState.update { it.copy(selectedDuration = duration) }
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
                selectedInvestmentType = InvestmentType.DEFAULT,
                selectedDuration = InvestmentDuration(InvestmentDuration.DEFAULT_YEARS),
                rateInput = "",
                rateError = null,
                generalError = null,
                calculationResult = null,
                isAssumptionsExpanded = false
            )
        }
    }

    private fun recalculate(state: CalculatorUiState) {
        val validation = calculateUseCase.validateInputs(
            rawAmount = state.amountInput,
            rawRate = state.rateInput,
            duration = state.selectedDuration,
            type = state.selectedInvestmentType
        )

        when (validation) {
            is InputValidationState.Idle -> {
                _uiState.update {
                    it.copy(
                        amountError = null,
                        rateError = null,
                        generalError = null,
                        calculationResult = null
                    )
                }
            }
            is InputValidationState.Invalid -> {
                _uiState.update {
                    it.copy(
                        amountError = validation.amountError,
                        rateError = validation.rateError,
                        generalError = validation.generalError,
                        calculationResult = null
                    )
                }
            }
            is InputValidationState.Valid -> {
                val result = calculateUseCase.calculate(validation.input)
                _uiState.update {
                    it.copy(
                        amountError = null,
                        rateError = null,
                        generalError = null,
                        calculationResult = result
                    )
                }
            }
        }
    }

    private fun sanitizeNumericInput(input: String, allowNegative: Boolean): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""

        val sb = StringBuilder()
        var hasDecimal = false
        var hasNegative = false

        for (i in trimmed.indices) {
            val c = trimmed[i]
            when {
                c.isDigit() -> sb.append(c)
                c == '.' && !hasDecimal -> {
                    hasDecimal = true
                    sb.append(c)
                }
                c == '-' && allowNegative && i == 0 && !hasNegative -> {
                    hasNegative = true
                    sb.append(c)
                }
            }
        }
        return sb.toString()
    }

    class Factory(
        private val calculateUseCase: CalculateInvestmentUseCase,
        private val themeRepository: ThemePreferencesRepository,
        private val systemDark: Boolean
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CalculatorViewModel(
                calculateUseCase = calculateUseCase,
                themeRepository = themeRepository,
                initialIsDark = systemDark
            ) as T
        }
    }
}
