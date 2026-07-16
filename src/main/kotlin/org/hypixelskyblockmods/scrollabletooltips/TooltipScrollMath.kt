package org.hypixelskyblockmods.scrollabletooltips

internal data class TooltipScrollRange(
    val minimumOffset: Int,
    val maximumOffset: Int,
    val initialOffset: Int,
    val scrollable: Boolean,
) {
    fun clamp(offset: Int): Int = offset.coerceIn(minimumOffset, maximumOffset)
}

internal object TooltipScrollMath {
    const val VIEWPORT_MARGIN = 6
    const val SCROLL_STEP = 10

    fun range(baseY: Int, tooltipHeight: Int, screenHeight: Int): TooltipScrollRange {
        val topOffset = VIEWPORT_MARGIN - baseY
        val bottomOffset = screenHeight - VIEWPORT_MARGIN - tooltipHeight - baseY
        val oversized = tooltipHeight + VIEWPORT_MARGIN * 2 > screenHeight
        val outsideViewport =
            baseY < VIEWPORT_MARGIN || baseY + tooltipHeight > screenHeight - VIEWPORT_MARGIN

        if (!oversized && !outsideViewport) {
            return TooltipScrollRange(0, 0, 0, scrollable = false)
        }

        if (!oversized) {
            val minimum = minOf(topOffset, bottomOffset)
            val maximum = maxOf(topOffset, bottomOffset)
            val correction = 0.coerceIn(minimum, maximum)
            return TooltipScrollRange(correction, correction, correction, scrollable = true)
        }

        val minimum = minOf(topOffset, bottomOffset)
        val maximum = maxOf(topOffset, bottomOffset)
        return TooltipScrollRange(
            minimumOffset = minimum,
            maximumOffset = maximum,
            initialOffset = topOffset.coerceIn(minimum, maximum),
            scrollable = true,
        )
    }

    fun scroll(offset: Int, delta: Double, range: TooltipScrollRange): Int {
        val direction = when {
            delta > 0.0 -> 1
            delta < 0.0 -> -1
            else -> 0
        }
        return range.clamp(offset + direction * SCROLL_STEP)
    }
}

internal class TooltipScrollSession<T> {
    private var target: T? = null
    private var range: TooltipScrollRange? = null
    var offset: Int = 0
        private set

    fun position(target: T, range: TooltipScrollRange): Int {
        if (!range.scrollable) {
            clear()
            return 0
        }

        if (this.target != target) {
            offset = range.initialOffset
        } else {
            offset = range.clamp(offset)
        }
        this.target = target
        this.range = range
        return offset
    }

    fun scroll(delta: Double): Boolean {
        val activeRange = range ?: return false
        if (delta == 0.0) return false
        offset = TooltipScrollMath.scroll(offset, delta, activeRange)
        return true
    }

    fun isTarget(target: T): Boolean = this.target == target

    fun currentTarget(): T? = target

    fun clear() {
        target = null
        range = null
        offset = 0
    }
}
