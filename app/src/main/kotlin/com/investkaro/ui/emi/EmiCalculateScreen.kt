package com.investkaro.ui.emi

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.investkaro.ui.theme.MotionTokens
import com.investkaro.ui.theme.pressScaleEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.investkaro.domain.loan.AmortizationMonth
import com.investkaro.domain.loan.LoanType
import com.investkaro.domain.loan.TenureUnit
import com.investkaro.formatting.IndianCurrencyFormatter
import com.investkaro.ui.components.AnimatedMoneyText
import com.investkaro.ui.components.MoneySummaryHeader
import com.investkaro.ui.components.SmoothSegmentedControl
import com.investkaro.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmiCalculateScreen(
    uiState: EmiUiState,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onLoanTypeChange: (LoanType) -> Unit,
    onPrincipalChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onTenureValueChange: (String) -> Unit,
    onTenureUnitChange: (TenureUnit) -> Unit,
    onOpenSchedule: () -> Unit,
    onCloseSchedule: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "EMI Calculate",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.2).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    TextButton(
                        onClick = onReset,
                        modifier = Modifier.pressScaleEffect()
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .pressScaleEffect()
                            .semantics {
                                contentDescription = if (isDarkTheme) "Switch to light theme" else "Switch to dark theme"
                            }
                    ) {
                        val rotation by animateFloatAsState(
                            targetValue = if (isDarkTheme) 180f else 0f,
                            animationSpec = tween(durationMillis = MotionTokens.Emphasis, easing = MotionTokens.EasingEmphasized),
                            label = "emi_theme_rot"
                        )
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.graphicsLayer { rotationZ = rotation }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .imePadding()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {

            // SHARED MONEY SUMMARY HEADER
            MoneySummaryHeader(
                label = "Monthly EMI",
                rawAmount = uiState.emiResult?.monthlyEmi,
                emptyPrompt = "Enter loan amount, tenure & rate to calculate",
                secondaryChipText = if (uiState.totalTenureMonths > 0) "${uiState.totalTenureMonths} months" else null
            )

            // PRIMARY INPUT 1: Loan Amount
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Loan amount",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = Spacing.xs)
                )

                OutlinedTextField(
                    value = uiState.principalInput,
                    onValueChange = onPrincipalChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Spacing.inputCornerRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    prefix = { Text("₹ ", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)) },
                    placeholder = { Text("e.g. 25,00,000") },
                    singleLine = true,
                    isError = uiState.principalError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    supportingText = if (uiState.principalError != null) {
                        { Text(uiState.principalError, color = MaterialTheme.colorScheme.error) }
                    } else null
                )
            }

            // PRIMARY INPUT 2: Loan Tenure with Years / Months Toggle
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Loan tenure",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )

                    SmoothSegmentedControl(
                        items = listOf(TenureUnit.YEARS, TenureUnit.MONTHS),
                        selectedItem = uiState.tenureUnit,
                        onItemSelected = onTenureUnitChange,
                        height = 32.dp,
                        contentPadding = 2.dp,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        borderColor = Color.Transparent,
                        pillColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        pillShape = RoundedCornerShape(6.dp),
                        itemLabel = { if (it == TenureUnit.YEARS) "Years" else "Months" },
                        modifier = Modifier.width(140.dp)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                OutlinedTextField(
                    value = uiState.tenureValueInput,
                    onValueChange = onTenureValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Spacing.inputCornerRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    suffix = { Text(if (uiState.tenureUnit == TenureUnit.YEARS) "Years" else "Months", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary) },
                    placeholder = { Text(if (uiState.tenureUnit == TenureUnit.YEARS) "e.g. 20" else "e.g. 240") },
                    singleLine = true,
                    isError = uiState.tenureError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    supportingText = if (uiState.tenureError != null) {
                        { Text(uiState.tenureError, color = MaterialTheme.colorScheme.error) }
                    } else null
                )
            }

            // PRIMARY INPUT 3: Annual Interest Rate
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Annual interest rate",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = Spacing.xs)
                )

                OutlinedTextField(
                    value = uiState.rateInput,
                    onValueChange = onRateChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Spacing.inputCornerRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    suffix = { Text("% p.a.", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)) },
                    placeholder = { Text("e.g. 8.5") },
                    singleLine = true,
                    isError = uiState.rateError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    supportingText = if (uiState.rateError != null) {
                        { Text(uiState.rateError, color = MaterialTheme.colorScheme.error) }
                    } else null
                )
            }

            // RESULTS DETAILS CARD
            if (uiState.emiResult != null) {
                val res = uiState.emiResult
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Spacing.cardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Text(
                            text = "Loan Summary Breakdown",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                        // Row 1: Principal & Total Interest
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Principal loan", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(2.dp))
                                AnimatedMoneyText(
                                    amount = res.principal,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                Text("Total interest", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(2.dp))
                                AnimatedMoneyText(
                                    amount = res.totalInterest,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        // Row 2: Total Repayment (Principal + Interest)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total repayment", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(2.dp))
                                AnimatedMoneyText(
                                    amount = res.totalRepayment,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Button(
                                onClick = onOpenSchedule,
                                modifier = Modifier.pressScaleEffect(),
                                shape = RoundedCornerShape(Spacing.inputCornerRadius),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(imageVector = Icons.Default.FormatListNumbered, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(Spacing.xs))
                                Text("View breakdown", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(72.dp)) // Clearance for floating bottom bar
        }
    }

    // Amortization Schedule Sheet
    if (uiState.isScheduleSheetOpen && uiState.emiResult != null) {
        AmortizationScheduleSheet(
            schedule = uiState.emiResult.amortizationSchedule,
            onDismissRequest = onCloseSchedule,
            sheetState = sheetState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AmortizationScheduleSheet(
    schedule: List<AmortizationMonth>,
    onDismissRequest: () -> Unit,
    sheetState: androidx.compose.material3.SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Amortization Schedule",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${schedule.size} installments",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            Text(
                text = "Month-by-month repayment breakdown with closing balance reaching ₹0.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mo", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(32.dp))
                Text("EMI", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(64.dp))
                Text("Principal", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(64.dp))
                Text("Interest", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(64.dp))
                Text("Balance", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(64.dp))
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                items(schedule) { month ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.sm, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${month.monthNumber}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(32.dp))
                        Text(IndianCurrencyFormatter.formatRupees(month.emi, includeSymbol = false), style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(64.dp))
                        Text(IndianCurrencyFormatter.formatRupees(month.principalComponent, includeSymbol = false), style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(64.dp))
                        Text(IndianCurrencyFormatter.formatRupees(month.interestComponent, includeSymbol = false), style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(64.dp))
                        Text(IndianCurrencyFormatter.formatRupees(month.closingBalance, includeSymbol = false), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(64.dp))
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))
        }
    }
}
