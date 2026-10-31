package com.investkaro.catalogue

import com.investkaro.data.catalogue.InvestmentCatalogueRepository
import com.investkaro.domain.catalogue.CalculationModelIds
import com.investkaro.domain.catalogue.CatalogueCategory
import com.investkaro.domain.catalogue.CatalogueStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InvestmentCatalogueTest {

    @Test
    fun `catalogue contains exactly 72 seeded items across all 6 categories`() {
        val all = InvestmentCatalogueRepository.items
        assertEquals(72, all.size)

        val byCategory = all.groupBy { it.category }
        assertEquals(22, byCategory[CatalogueCategory.MUTUAL_FUNDS]?.size)
        assertEquals(10, byCategory[CatalogueCategory.SHARES_ETFS]?.size)
        assertEquals(20, byCategory[CatalogueCategory.FIXED_INCOME]?.size)
        assertEquals(8, byCategory[CatalogueCategory.RETIREMENT]?.size)
        assertEquals(7, byCategory[CatalogueCategory.PRECIOUS_METALS]?.size)
        assertEquals(5, byCategory[CatalogueCategory.OTHER_ADVANCED]?.size)
    }

    @Test
    fun `search by acronyms finds expected instruments`() {
        val ppf = InvestmentCatalogueRepository.filter("PPF")
        assertTrue(ppf.any { it.name.contains("Public Provident Fund") })

        val nps = InvestmentCatalogueRepository.filter("NPS")
        assertTrue(nps.any { it.name.contains("National Pension System") })

        val fd = InvestmentCatalogueRepository.filter("FD")
        assertTrue(fd.any { it.name.contains("Bank fixed deposit") })

        val rd = InvestmentCatalogueRepository.filter("RD")
        assertTrue(rd.any { it.name.contains("Bank recurring deposit") })

        val etf = InvestmentCatalogueRepository.filter("ETF")
        assertTrue(etf.any { it.name.contains("Index ETF") })

        val reit = InvestmentCatalogueRepository.filter("REIT")
        assertTrue(reit.any { it.name.contains("REIT") })

        val sgb = InvestmentCatalogueRepository.filter("SGB")
        assertTrue(sgb.any { it.name.contains("Sovereign Gold Bond") })
    }

    @Test
    fun `search by partial name is case insensitive`() {
        val results = InvestmentCatalogueRepository.filter("sukanya")
        assertEquals(1, results.size)
        assertEquals("Sukanya Samriddhi Yojana (SSY)", results.first().name)
    }

    @Test
    fun `category filtering returns only items belonging to category`() {
        val mutualFunds = InvestmentCatalogueRepository.filter("", CatalogueCategory.MUTUAL_FUNDS)
        assertEquals(22, mutualFunds.size)
        assertTrue(mutualFunds.all { it.category == CatalogueCategory.MUTUAL_FUNDS })

        val fixedIncome = InvestmentCatalogueRepository.filter("", CatalogueCategory.FIXED_INCOME)
        assertEquals(20, fixedIncome.size)
        assertTrue(fixedIncome.all { it.category == CatalogueCategory.FIXED_INCOME })
    }

    @Test
    fun `planning only items have no assigned calculation model ID`() {
        val bankFd = InvestmentCatalogueRepository.findById("bank_fd")
        assertNotNull(bankFd)
        assertEquals(CatalogueStatus.PLANNING_ONLY, bankFd?.status)
        assertNull(bankFd?.modelId)
        assertFalse(bankFd?.isCalculatorSupported ?: true)

        val ppf = InvestmentCatalogueRepository.findById("ppf")
        assertNotNull(ppf)
        assertEquals(CatalogueStatus.PLANNING_ONLY, ppf?.status)
        assertNull(ppf?.modelId)
    }

    @Test
    fun `calculator supported items have valid model ID`() {
        val mfSip = InvestmentCatalogueRepository.findById("mf_sip")
        assertNotNull(mfSip)
        assertEquals(CatalogueStatus.CALCULATOR_SUPPORTED, mfSip?.status)
        assertEquals(CalculationModelIds.MONTHLY_CONTRIBUTION, mfSip?.modelId)
        assertTrue(mfSip?.isCalculatorSupported ?: false)

        val mfLumpSum = InvestmentCatalogueRepository.findById("mf_lump_sum")
        assertNotNull(mfLumpSum)
        assertEquals(CatalogueStatus.CALCULATOR_SUPPORTED, mfLumpSum?.status)
        assertEquals(CalculationModelIds.LUMP_SUM_COMPOUND, mfLumpSum?.modelId)
        assertTrue(mfLumpSum?.isCalculatorSupported ?: false)
    }
}
