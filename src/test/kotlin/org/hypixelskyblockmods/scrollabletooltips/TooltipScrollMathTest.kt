package org.hypixelskyblockmods.scrollabletooltips

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TooltipScrollMathTest {
    @Test
    fun `short in-bounds tooltips do not scroll`() {
        val range = TooltipScrollMath.range(baseY = 30, tooltipHeight = 80, screenHeight = 240)

        assertFalse(range.scrollable)
        assertEquals(0, range.initialOffset)
    }

    @Test
    fun `oversized tooltips start at the top viewport margin`() {
        val range = TooltipScrollMath.range(baseY = -80, tooltipHeight = 400, screenHeight = 240)

        assertTrue(range.scrollable)
        assertEquals(86, range.initialOffset)
        assertEquals(6, -80 + range.initialOffset)
    }

    @Test
    fun `wheel movement uses ten pixel steps and clamps`() {
        val range = TooltipScrollMath.range(baseY = -80, tooltipHeight = 400, screenHeight = 240)
        var offset = range.initialOffset

        offset = TooltipScrollMath.scroll(offset, -1.0, range)
        assertEquals(76, offset)

        repeat(100) {
            offset = TooltipScrollMath.scroll(offset, -1.0, range)
        }
        assertEquals(range.minimumOffset, offset)

        repeat(100) {
            offset = TooltipScrollMath.scroll(offset, 1.0, range)
        }
        assertEquals(range.maximumOffset, offset)
    }

    @Test
    fun `off-screen fitting tooltips are corrected into view`() {
        val range = TooltipScrollMath.range(baseY = 210, tooltipHeight = 50, screenHeight = 240)

        assertTrue(range.scrollable)
        assertEquals(-26, range.initialOffset)
        assertEquals(184, 210 + range.initialOffset)
    }

    @Test
    fun `changing targets resets the session to the top`() {
        val range = TooltipScrollMath.range(baseY = -80, tooltipHeight = 400, screenHeight = 240)
        val session = TooltipScrollSession<String>()

        assertEquals(range.initialOffset, session.position("first", range))
        assertTrue(session.scroll(-1.0))
        assertEquals(range.initialOffset - TooltipScrollMath.SCROLL_STEP, session.offset)
        assertEquals(range.initialOffset, session.position("second", range))
    }

    @Test
    fun `scroll events remain consumed at a boundary while a tooltip is active`() {
        val range = TooltipScrollMath.range(baseY = -80, tooltipHeight = 400, screenHeight = 240)
        val session = TooltipScrollSession<String>()
        session.position("item", range)

        repeat(100) { assertTrue(session.scroll(1.0)) }
        assertEquals(range.maximumOffset, session.offset)
    }
}
