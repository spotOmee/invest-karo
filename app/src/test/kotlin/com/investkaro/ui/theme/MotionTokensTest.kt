package com.investkaro.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MotionTokensTest {

    @Test
    fun `motion tokens provide expected spring counter specs`() {
        assertNotNull(MotionTokens.SpringCounter)
        assertEquals(0.95f, MotionTokens.SpringCounter.dampingRatio, 0.001f)
        assertEquals(220f, MotionTokens.SpringCounter.stiffness, 0.001f)
    }

    @Test
    fun `motion tokens provide expected spring pill specs`() {
        assertNotNull(MotionTokens.SpringPill)
        assertEquals(0.85f, MotionTokens.SpringPill.dampingRatio, 0.001f)
        assertEquals(220f, MotionTokens.SpringPill.stiffness, 0.001f)
    }
}
