package com.investkaro.ui.calculator

import com.investkaro.domain.model.CalculationResult
import com.investkaro.domain.model.InvestmentDuration
import com.investkaro.domain.model.InvestmentType

data class CalculatorUiState(
    val amountInput: String = "",
    val amountError: String? = null,
    val selectedInvestmentType: InvestmentType = InvestmentType.DEFAULT,
    val selectedDuration: InvestmentDuration = InvestmentDuration(InvestmentDuration.DEFAULT_YEARS),
    val rateInput: String = "",
    val rateError: String? = null,
    val generalError: String? = null,
    val calculationResult: CalculationResult? = null,
    val isDarkTheme: Boolean = false,
    val isAssumptionsExpanded: Boolean = false
) {
    val isFormComplete: Boolean
        get() = amountInput.isNotBlank() && rateInput.isNotBlank() && amountError == null && rateError == null

    val formattedAmountPreview: String?
        get() {
            if (amountInput.isBlank() || amountError != null) return null
            val clean = amountInput.replace(",", "").replace("₹", "").trim()
            val parsed = clean.toBigDecimalOrNull() ?: return null
            return com.investkaro.formatting.IndianCurrencyFormatter.formatRupees(parsed)
        }
}
