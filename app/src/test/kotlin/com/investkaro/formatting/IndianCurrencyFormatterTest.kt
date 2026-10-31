package com.investkaro.formatting

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class IndianCurrencyFormatterTest {

    @Test
    fun `formatRupees groups digits correctly according to Indian numbering system`() {
        assertEquals("₹0", IndianCurrencyFormatter.formatRupees(BigDecimal("0")))
        assertEquals("₹5", IndianCurrencyFormatter.formatRupees(BigDecimal("5")))
        assertEquals("₹50", IndianCurrencyFormatter.formatRupees(BigDecimal("50")))
        assertEquals("₹500", IndianCurrencyFormatter.formatRupees(BigDecimal("500")))
        assertEquals("₹1,000", IndianCurrencyFormatter.formatRupees(BigDecimal("1000")))
        assertEquals("₹5,000", IndianCurrencyFormatter.formatRupees(BigDecimal("5000")))
        assertEquals("₹50,000", IndianCurrencyFormatter.formatRupees(BigDecimal("50000")))
        assertEquals("₹1,00,000", IndianCurrencyFormatter.formatRupees(BigDecimal("100000")))
        assertEquals("₹1,23,456", IndianCurrencyFormatter.formatRupees(BigDecimal("123456")))
        assertEquals("₹12,34,567", IndianCurrencyFormatter.formatRupees(BigDecimal("1234567")))
        assertEquals("₹1,00,00,000", IndianCurrencyFormatter.formatRupees(BigDecimal("10000000")))
        assertEquals("₹12,34,56,789", IndianCurrencyFormatter.formatRupees(BigDecimal("123456789")))
    }

    @Test
    fun `formatRupees handles negative amounts correctly`() {
        assertEquals("-₹2,629", IndianCurrencyFormatter.formatRupees(BigDecimal("-2629")))
        assertEquals("-₹50,000", IndianCurrencyFormatter.formatRupees(BigDecimal("-50000")))
        assertEquals("-₹1,23,456", IndianCurrencyFormatter.formatRupees(BigDecimal("-123456")))
    }

    @Test
    fun `formatRupees without currency symbol produces clean grouped numbers`() {
        assertEquals("1,23,456", IndianCurrencyFormatter.formatRupees(BigDecimal("123456"), includeSymbol = false))
        assertEquals("-2,629", IndianCurrencyFormatter.formatRupees(BigDecimal("-2629"), includeSymbol = false))
    }

    @Test
    fun `formatPercentage formats decimals and whole numbers nicely`() {
        assertEquals("12%", IndianCurrencyFormatter.formatPercentage(BigDecimal("12")))
        assertEquals("12%", IndianCurrencyFormatter.formatPercentage(BigDecimal("12.0")))
        assertEquals("10.5%", IndianCurrencyFormatter.formatPercentage(BigDecimal("10.5")))
        assertEquals("8.25%", IndianCurrencyFormatter.formatPercentage(BigDecimal("8.25")))
    }

    @Test
    fun `cleanNumericInput strips currency symbols and commas`() {
        assertEquals("5000", IndianCurrencyFormatter.cleanNumericInput("₹ 5,000"))
        assertEquals("123456.78", IndianCurrencyFormatter.cleanNumericInput(" ₹ 1,23,456.78 "))
        assertEquals("1000", IndianCurrencyFormatter.cleanNumericInput("1,000"))
    }
}
