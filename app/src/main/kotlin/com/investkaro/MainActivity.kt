package com.investkaro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.investkaro.settings.ThemePreferencesRepository
import com.investkaro.ui.emi.EmiCalculateScreen
import com.investkaro.ui.emi.EmiViewModel
import com.investkaro.ui.invest.InvestKaroScreen
import com.investkaro.ui.invest.InvestKaroViewModel
import com.investkaro.ui.navigation.AppDestination
import com.investkaro.ui.navigation.InvestKaroBottomBar
import com.investkaro.ui.theme.InvestKaroTheme
import com.investkaro.ui.theme.MotionTokens
import com.investkaro.ui.theme.isReducedMotion

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val themeRepository = ThemePreferencesRepository(applicationContext)

        setContent {
            val systemDark = isSystemInDarkTheme()

            val investViewModel: InvestKaroViewModel = viewModel(
                factory = InvestKaroViewModel.Factory(
                    themeRepository = themeRepository,
                    systemDark = systemDark
                )
            )

            val emiViewModel: EmiViewModel = viewModel()

            val investUiState by investViewModel.uiState.collectAsState()
            val emiUiState by emiViewModel.uiState.collectAsState()

            var currentDestination by remember { mutableStateOf(AppDestination.INVEST_KARO) }

            InvestKaroTheme(darkTheme = investUiState.isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        InvestKaroBottomBar(
                            currentDestination = currentDestination,
                            onDestinationSelected = { currentDestination = it }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        val reducedMotion = isReducedMotion()
                        AnimatedContent(
                            targetState = currentDestination,
                            transitionSpec = {
                                if (reducedMotion) {
                                    fadeIn(animationSpec = tween(MotionTokens.Fast)) togetherWith
                                        fadeOut(animationSpec = tween(MotionTokens.Fast))
                                } else {
                                    (fadeIn(animationSpec = tween(MotionTokens.Standard, easing = MotionTokens.EasingStandard)) +
                                        slideInVertically(animationSpec = tween(MotionTokens.Standard, easing = MotionTokens.EasingStandard)) { 12 }) togetherWith
                                        (fadeOut(animationSpec = tween(MotionTokens.Fast, easing = MotionTokens.EasingStandard)) +
                                            slideOutVertically(animationSpec = tween(MotionTokens.Fast, easing = MotionTokens.EasingStandard)) { -12 })
                                }
                            },
                            label = "destination_transition"
                        ) { destination ->
                            when (destination) {
                                AppDestination.INVEST_KARO -> {
                                    InvestKaroScreen(
                                        uiState = investUiState,
                                        onModeChange = investViewModel::onModeChange,
                                        onOpenCatalogue = investViewModel::openCatalogueSheet,
                                        onCloseCatalogue = investViewModel::closeCatalogueSheet,
                                        onCatalogueItemSelected = investViewModel::onCatalogueItemSelected,
                                        onAmountChange = investViewModel::onAmountChange,
                                        onDurationChange = investViewModel::onDurationChange,
                                        onLumpSumDurationChange = investViewModel::onLumpSumDurationChange,
                                        onRateChange = investViewModel::onRateChange,
                                        onToggleTheme = investViewModel::toggleTheme,
                                        onToggleAssumptions = investViewModel::toggleAssumptions,
                                        onReset = investViewModel::resetInputs
                                    )
                                }
                                AppDestination.EMI_CALCULATE -> {
                                    EmiCalculateScreen(
                                        uiState = emiUiState,
                                        isDarkTheme = investUiState.isDarkTheme,
                                        onToggleTheme = investViewModel::toggleTheme,
                                        onLoanTypeChange = emiViewModel::onLoanTypeChange,
                                        onPrincipalChange = emiViewModel::onPrincipalChange,
                                        onRateChange = emiViewModel::onRateChange,
                                        onTenureValueChange = emiViewModel::onTenureValueChange,
                                        onTenureUnitChange = emiViewModel::onTenureUnitChange,
                                        onOpenSchedule = emiViewModel::openScheduleSheet,
                                        onCloseSchedule = emiViewModel::closeScheduleSheet,
                                        onReset = emiViewModel::resetInputs
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}