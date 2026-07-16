package org.hypixelskyblockmods.scrollabletooltips.mixin

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner
import org.hypixelskyblockmods.scrollabletooltips.TooltipScrollController
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.ModifyVariable

@Mixin(GuiGraphicsExtractor::class)
abstract class GuiGraphicsExtractorMixin {
    @ModifyVariable(method = ["tooltip"], at = At("HEAD"), argsOnly = true)
    private fun scrollableTooltips_wrapPositioner(
        original: ClientTooltipPositioner,
    ): ClientTooltipPositioner = ClientTooltipPositioner {
            screenWidth,
            screenHeight,
            mouseX,
            mouseY,
            tooltipWidth,
            tooltipHeight,
        ->
        val vanilla = original.positionTooltip(
            screenWidth,
            screenHeight,
            mouseX,
            mouseY,
            tooltipWidth,
            tooltipHeight,
        )
        TooltipScrollController.position(vanilla, screenHeight, tooltipWidth, tooltipHeight)
    }
}
