package com.investkaro.ui.emi

import androidx.lifecycle.ViewModel
import com.investkaro.domain.loan.CalculateEmiUseCase
import com.investkaro.domain.loan.EmiCalculationInput
import com.investkaro.domain.loan.EmiCalculationResult
import com.investkaro.domain.loan.LoanType
import com.investkaro.domain.loan.TenureUnit
import com.investkaro.formatting.IndianCurrencyFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

data class EmiUiState(
    val selectedLoanType: LoanType = LoanType.HOME_LOAN,
    val principalInput: String = "2500000",
    val principalError: String? = null,
    val rateInput: String = "8.5",
    val rateError: String? = null,
    val tenureValueInput: String = "20",
    val tenureUnit: TenureUnit = TenureUnit.YEARS,
    val tenureError: String? = null,
    val emiResult: EmiCalculationResult? = null,
    val isScheduleSheetOpen: Boolean = false
) {
    val totalTenureMonths: Int
        get() {
            val clean = tenureValueInput.trim()
            val v = clean.toDoubleOrNull() ?: return 0
            val m = if (tenureUnit == TenureUnit.YEARS) {
                kotlin.math.round(v * 12).toInt()
            } else {
                kotlin.math.round(v).toInt()
            }
            return m.coerceAtLeast(0)
        }

    val formattedPrincipalPreview: String?
        get() {
            if (principalInput.isBlank() || principalError != null) return null
            val clean = IndianCurrencyFormatter.cleanNumericInput(principalInput)
            val parsed = clean.toBigDecimalOrNull() ?: return null
            return IndianCurrencyFormatter.formatRupees(parsed)
        }

    val isInputValid: Boolean
        get() = emiResult != null && principalError == null && rateError == null && tenureError == null
}

class EmiViewModel(
    private val emiUseCase: CalculateEmiUseCase = CalculateEmiUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmiUiState())
    val uiState: StateFlow<EmiUiState> = _uiState.asStateFlow()

    init {
        recalculate(_uiState.value)
    }

    fun onLoanTypeChange(type: LoanType) {
        _uiState.update { it.copy(selectedLoanType = type) }
        recalculate(_uiState.value)
    }

    fun onPrincipalChange(raw: String) {
        _uiState.update { it.copy(principalInput = raw) }
        recalculate(_uiState.value)
    }

    fun onRateChange(raw: String) {
        _uiState.update { it.copy(rateInput = raw) }
        recalculate(_uiState.value)
    }

    fun onTenureValueChange(raw: String) {
        _uiState.update { it.copy(tenureValueInput = raw) }
        recalculate(_uiState.value)
    }

    fun onTenureUnitChange(unit: TenureUnit) {
        val currentTenure = _uiState.value.tenureValueInput.trim().toDoubleOrNull()
        val converted = if (currentTenure != null && currentTenure > 0) {
            if (unit == TenureUnit.MONTHS && _uiState.value.tenureUnit == TenureUnit.YEARS) {
                kotlin.math.round(currentTenure * 12).toInt().toString()
            } else if (unit == TenureUnit.YEARS && _uiState.value.tenureUnit == TenureUnit.MONTHS) {
                val years = currentTenure / 12.0
                if (years == kotlin.math.floor(years)) {
                    years.toInt().toString()
                } else {
                    String.format(java.util.Locale.US, "%.1f", years)
                }
            } else {
                _uiState.value.tenureValueInput
            }
        } else {
            _uiState.value.tenureValueInput
        }

        _uiState.update {
            it.copy(
                tenureUnit = unit,
                tenureValueInput = converted
            )
        }
        recalculate(_uiState.value)
    }

    fun openScheduleSheet() {
        _uiState.update { it.copy(isScheduleSheetOpen = true) }
    }

    fun closeScheduleSheet() {
        _uiState.update { it.copy(isScheduleSheetOpen = false) }
    }

    fun resetInputs() {
        _uiState.update {
            it.copy(
                principalInput = "",
                principalError = null,
                rateInput = "",
                rateError = null,
                tenureValueInput = "",
                tenureError = null,
                emiResult = null,
                isScheduleSheetOpen = false
            )
        }
    }

    private fun recalculate(state: EmiUiState) {
        val cleanPrincipal = IndianCurrencyFormatter.cleanNumericInput(state.principalInput)
        val cleanRate = state.rateInput.trim().replace("%", "").trim()
        val cleanTenure = state.tenureValueInput.trim()

        if (cleanPrincipal.isEmpty() && cleanRate.isEmpty() && cleanTenure.isEmpty()) {
            _uiState.update {
                it.copy(
                    principalError = null,
                    rateError = null,
                    tenureError = null,
                    emiResult = null
                )
            }
            return
        }

        var pError: String? = null
        var parsedPrincipal: BigDecimal? = null
        if (cleanPrincipal.isEmpty()) {
            pError = "Enter loan amount."
        } else {
            try {
                val p = BigDecimal(cleanPrincipal)
                if (p <= BigDecimal.ZERO) {
                    pError = "Loan amount must be greater than ₹0."
                } else if (p > BigDecimal("1000000000")) {
                    pError = "Amount exceeds supported limit (₹100 Cr)."
                } else {
                    parsedPrincipal = p
                }
            } catch (_: NumberFormatException) {
                pError = "Invalid loan amount format."
            }
        }

        var rError: String? = null
        var parsedRate: BigDecimal? = null
        if (cleanRate.isEmpty()) {
            rError = "Enter annual interest rate."
        } else {
            try {
                val r = BigDecimal(cleanRate)
                if (r < BigDecimal.ZERO || r > BigDecimal("100")) {
                    rError = "Interest rate must be between 0% and 100%."
                } else {
                    parsedRate = r
                }
            } catch (_: NumberFormatException) {
                rError = "Invalid rate format."
            }
        }

        var tError: String? = null
        var parsedMonths: Int? = null
        if (cleanTenure.isEmpty()) {
            tError = "Enter tenure."
        } else {
            val tenureDouble = cleanTenure.toDoubleOrNull()
            if (tenureDouble == null || tenureDouble <= 0.0) {
                tError = "Tenure must be greater than 0."
            } else {
                val months = if (state.tenureUnit == TenureUnit.YEARS) {
                    kotlin.math.round(tenureDouble * 12).toInt()
                } else {
                    kotlin.math.round(tenureDouble).toInt()
                }
                if (months <= 0) {
                    tError = "Tenure must be greater than 0."
                } else if (months > 600) { // Max 50 years
                    tError = "Tenure cannot exceed 50 years (600 months)."
                } else {
                    parsedMonths = months
                }
            }
        }

        if (pError != null || rError != null || tError != null || parsedPrincipal == null || parsedRate == null || parsedMonths == null) {
            _uiState.update {
                it.copy(
                    principalError = pError,
                    rateError = rError,
                    tenureError = tError,
                    emiResult = null
                )
            }
            return
        }

        try {
            val input = EmiCalculationInput(
                loanType = state.selectedLoanType,
                principal = parsedPrincipal,
                annualRatePercent = parsedRate,
                tenureMonths = parsedMonths,
                processingFeeFlat = BigDecimal.ZERO
            )

            val result = emiUseCase.calculate(input)
            _uiState.update {
                it.copy(
                    principalError = null,
                    rateError = null,
                    tenureError = null,
                    emiResult = result
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    emiResult = null
                )
            }
        }
    }
}
