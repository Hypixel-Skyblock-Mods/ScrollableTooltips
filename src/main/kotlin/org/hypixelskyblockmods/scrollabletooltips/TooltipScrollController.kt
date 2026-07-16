package org.hypixelskyblockmods.scrollabletooltips

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import org.hypixelskyblockmods.scrollabletooltips.mixin.AbstractContainerScreenAccessor
import org.hypixelskyblockmods.scrollabletooltips.platform.ScreenCompat
import org.joml.Vector2i
import org.joml.Vector2ic

internal object TooltipScrollController {
    private val session = TooltipScrollSession<TooltipTarget>()

    fun position(
        original: Vector2ic,
        screenHeight: Int,
        tooltipWidth: Int,
        tooltipHeight: Int,
    ): Vector2ic {
        val target = currentTarget(tooltipWidth, tooltipHeight, screenHeight) ?: run {
            reset()
            return Vector2i(original)
        }
        val range = TooltipScrollMath.range(original.y(), tooltipHeight, screenHeight)
        val offset = session.position(target, range)
        return Vector2i(original.x(), original.y() + offset)
    }

    fun scroll(delta: Double): Boolean {
        val hovered = currentHoveredItem() ?: run {
            reset()
            return false
        }
        val activeTarget = session.currentTarget() ?: return false
        val stack = hovered.slot.item
        if (
            activeTarget.screen !== hovered.screen ||
            activeTarget.slot !== hovered.slot ||
            activeTarget.itemHash != ItemStack.hashItemAndComponents(stack) ||
            activeTarget.itemCount != stack.count
        ) {
            reset()
            return false
        }
        return session.scroll(delta)
    }

    fun reset() {
        session.clear()
    }

    private fun currentTarget(
        tooltipWidth: Int,
        tooltipHeight: Int,
        screenHeight: Int,
    ): TooltipTarget? {
        val hovered = currentHoveredItem() ?: return null
        return TooltipTarget(
            screen = hovered.screen,
            slot = hovered.slot,
            itemHash = ItemStack.hashItemAndComponents(hovered.slot.item),
            itemCount = hovered.slot.item.count,
            tooltipWidth = tooltipWidth,
            tooltipHeight = tooltipHeight,
            screenHeight = screenHeight,
        )
    }

    private fun currentHoveredItem(): HoveredItem? {
        val minecraft = Minecraft.getInstance()
        if (minecraft.player == null) return null
        val screen = ScreenCompat.currentScreen() as? AbstractContainerScreen<*> ?: return null
        val slot = (screen as AbstractContainerScreenAccessor).scrollableTooltips_getHoveredSlot()
            ?: return null
        if (!slot.hasItem()) return null
        return HoveredItem(screen, slot)
    }

    private data class HoveredItem(
        val screen: AbstractContainerScreen<*>,
        val slot: Slot,
    )

    private data class TooltipTarget(
        val screen: AbstractContainerScreen<*>,
        val slot: Slot,
        val itemHash: Int,
        val itemCount: Int,
        val tooltipWidth: Int,
        val tooltipHeight: Int,
        val screenHeight: Int,
    )
}
