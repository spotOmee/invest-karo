package com.investkaro.ui.invest

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.investkaro.ui.components.AnimatedMoneyText
import com.investkaro.ui.components.MoneySummaryHeader
import com.investkaro.ui.components.SmoothSegmentedControl
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.investkaro.domain.catalogue.CatalogueStatus
import com.investkaro.domain.model.InvestmentDuration
import com.investkaro.formatting.IndianCurrencyFormatter
import com.investkaro.ui.theme.MotionTokens
import com.investkaro.ui.theme.Spacing
import com.investkaro.ui.theme.pressScaleEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestKaroScreen(
    uiState: InvestKaroUiState,
    onModeChange: (CalculationMode) -> Unit,
    onOpenCatalogue: () -> Unit,
    onCloseCatalogue: () -> Unit,
    onCatalogueItemSelected: (com.investkaro.domain.catalogue.CatalogueItem) -> Unit,
    onAmountChange: (String) -> Unit,
    onDurationChange: (InvestmentDuration) -> Unit,
    onLumpSumDurationChange: (Int) -> Unit,
    onRateChange: (String) -> Unit,
    onToggleTheme: () -> Unit,
    onToggleAssumptions: () -> Unit,
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
                        text = "Invest Karo",
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
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Reset",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .pressScaleEffect()
                            .semantics {
                                contentDescription = if (uiState.isDarkTheme) "Switch to light theme" else "Switch to dark theme"
                            }
                    ) {
                        val rotation by animateFloatAsState(
                            targetValue = if (uiState.isDarkTheme) 180f else 0f,
                            animationSpec = tween(durationMillis = MotionTokens.Emphasis, easing = MotionTokens.EasingEmphasized),
                            label = "theme_icon_rot"
                        )
                        Icon(
                            imageVector = if (uiState.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (uiState.isDarkTheme) "Light theme" else "Dark theme",
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

            // SINGLE SOURCE OF TRUTH: ESTIMATED FUTURE VALUE
            val rawFutureValue = when {
                uiState.calculationMode == CalculationMode.MONTHLY_SIP && uiState.sipResult != null ->
                    uiState.sipResult.estimatedFutureValue
                uiState.calculationMode == CalculationMode.LUMP_SUM && uiState.lumpSumResult != null ->
                    uiState.lumpSumResult.estimatedFutureValue
                else -> null
            }

            MoneySummaryHeader(
                label = "Estimated future value",
                rawAmount = rawFutureValue,
                emptyPrompt = if (uiState.selectedCatalogueItem.status == CatalogueStatus.CALCULATOR_SUPPORTED) {
                    "Enter contribution amount, duration & return rate"
                } else {
                    "Planning-only instrument (regulatory fixed rate)"
                },
                secondaryChipText = if (rawFutureValue != null) {
                    "${uiState.selectedDuration.years} yr • ${uiState.calculationMode.title}"
                } else {
                    uiState.selectedCatalogueItem.category.displayName
                }
            )

            // Mode Selector: Monthly SIP vs Lump Sum (Smooth Segmented Pill Control)
            if (uiState.selectedCatalogueItem.status == CatalogueStatus.CALCULATOR_SUPPORTED) {
                SmoothSegmentedControl(
                    items = CalculationMode.entries,
                    selectedItem = uiState.calculationMode,
                    onItemSelected = onModeChange,
                    itemLabel = { it.title },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // INPUT 1: Investment Type Picker Button
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Investment type",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = Spacing.xs)
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Spacing.inputCornerRadius))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(Spacing.inputCornerRadius))
                        .clickable { onOpenCatalogue() },
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(Spacing.inputCornerRadius)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.selectedCatalogueItem.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select investment type",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // IF CALCULATOR SUPPORTED: RENDER INPUTS AND CALCULATOR RESULT
            if (uiState.selectedCatalogueItem.status == CatalogueStatus.CALCULATOR_SUPPORTED) {

                // Amount Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (uiState.calculationMode == CalculationMode.MONTHLY_SIP) "Monthly investment" else "Lump-sum investment",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = Spacing.xs)
                    )

                    OutlinedTextField(
                        value = uiState.amountInput,
                        onValueChange = onAmountChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Spacing.inputCornerRadius),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            errorContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        prefix = {
                            Text(
                                text = "₹ ",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        },
                        placeholder = {
                            Text(
                                text = if (uiState.calculationMode == CalculationMode.MONTHLY_SIP) "e.g. 5,000" else "e.g. 1,00,000",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        singleLine = true,
                        isError = uiState.amountError != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        supportingText = if (uiState.amountError != null) {
                            {
                                Text(
                                    text = uiState.amountError,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        } else null
                    )
                }

                // Duration Dropdown
                if (uiState.calculationMode == CalculationMode.MONTHLY_SIP) {
                    DurationDropdown(
                        selectedDuration = uiState.selectedDuration,
                        onDurationSelected = onDurationChange
                    )
                } else {
                    LumpSumDurationDropdown(
                        selectedYears = uiState.lumpSumDurationYears,
                        onYearsSelected = onLumpSumDurationChange
                    )
                }

                // Expected Return Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Expected annual return",
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
                            errorContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        suffix = {
                            Text(
                                text = "% p.a.",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        },
                        placeholder = {
                            Text(
                                text = "e.g. 12.0",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        singleLine = true,
                        isError = uiState.rateError != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        supportingText = if (uiState.rateError != null) {
                            {
                                Text(
                                    text = uiState.rateError,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        } else null
                    )
                }

                // 8. Compact Breakdown Rows (Total invested + Estimated growth, NO repeated final amount)
                val hasSipResult = uiState.calculationMode == CalculationMode.MONTHLY_SIP && uiState.sipResult != null
                val hasLumpSumResult = uiState.calculationMode == CalculationMode.LUMP_SUM && uiState.lumpSumResult != null

                if (hasSipResult || hasLumpSumResult) {
                    val total = if (hasSipResult) uiState.sipResult!!.totalInvested else uiState.lumpSumResult!!.totalInvested
                    val growth = if (hasSipResult) uiState.sipResult!!.estimatedGrowth else uiState.lumpSumResult!!.estimatedGrowth
                    val assumptions = if (hasSipResult) uiState.sipResult!!.assumptions else uiState.lumpSumResult!!.assumptions

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
                                text = "Breakdown",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )

                            // Breakdown Rows: Total invested & Estimated growth
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total invested",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                AnimatedMoneyText(
                                    amount = total,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Estimated growth",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                AnimatedMoneyText(
                                    amount = growth,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    isDynamicColorBySign = true,
                                    positiveColor = MaterialTheme.colorScheme.primary,
                                    negativeColor = MaterialTheme.colorScheme.error
                                )
                            }

                            Spacer(modifier = Modifier.height(Spacing.xs))

                            // Expandable assumptions & calculation details (with compact disclaimer inside)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { onToggleAssumptions() }
                                    .padding(vertical = Spacing.xxs),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (uiState.isAssumptionsExpanded) "Hide assumptions & formula" else "Assumptions & calculation details",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = if (uiState.isAssumptionsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            AnimatedVisibility(
                                visible = uiState.isAssumptionsExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = Spacing.xs),
                                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                                ) {
                                    assumptions.forEach { assumption ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                                        ) {
                                            Text("•", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                            Text(assumption, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(Spacing.xxs))
                                    Text(
                                        text = "Disclaimer: Estimates are illustrative and not financial advice. Actual returns, fees, taxes, and product rules can affect outcomes. Market-linked investments can lose value.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

            } else {
                // PLANNING ONLY OR COMING SOON CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Spacing.cardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Planning Only Instrument",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.xs))

                        Text(
                            text = "Calculation model not available for this instrument. Scheme rates vary by gazette notification or quarterly compounding rules (e.g. RBI/Post Office guidelines). No speculative or fake returns are generated.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(Spacing.sm))

                        // Risk and limitations specific to this instrument
                        Text(
                            text = "Product Notes & Limitations:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = uiState.selectedCatalogueItem.riskAndLimitations,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(72.dp)) // Clearance for floating bottom bar
        }
    }

    // Modal Bottom Sheet for Catalogue
    if (uiState.isCatalogueSheetOpen) {
        InvestmentCatalogueBottomSheet(
            selectedItemId = uiState.selectedCatalogueItem.id,
            onItemSelected = onCatalogueItemSelected,
            onDismissRequest = onCloseCatalogue,
            sheetState = sheetState
        )
    }
}

@Composable
private fun DurationDropdown(
    selectedDuration: InvestmentDuration,
    onDurationSelected: (InvestmentDuration) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Investment duration",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = Spacing.xs)
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = Spacing.minTouchTarget)
                    .clip(RoundedCornerShape(Spacing.inputCornerRadius))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.dp,
                        color = if (expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(Spacing.inputCornerRadius)
                    )
                    .clickable { expanded = !expanded }
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${selectedDuration.displayText} (${selectedDuration.totalMonths} months)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select duration",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                InvestmentDuration.ALL_OPTIONS.forEach { duration ->
                    val isSelected = duration.years == selectedDuration.years
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${duration.displayText} (${duration.totalMonths} contributions)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        onClick = {
                            onDurationSelected(duration)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LumpSumDurationDropdown(
    selectedYears: Int,
    onYearsSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(1, 2, 3, 5, 7, 10, 15, 20, 25, 30)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Holding period (duration)",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = Spacing.xs)
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = Spacing.minTouchTarget)
                    .clip(RoundedCornerShape(Spacing.inputCornerRadius))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.dp,
                        color = if (expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(Spacing.inputCornerRadius)
                    )
                    .clickable { expanded = !expanded }
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$selectedYears years",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select years",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                options.forEach { years ->
                    val isSelected = years == selectedYears
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$years years",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        onClick = {
                            onYearsSelected(years)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
