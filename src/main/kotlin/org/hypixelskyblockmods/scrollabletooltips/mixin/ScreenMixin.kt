package org.hypixelskyblockmods.scrollabletooltips.mixin

import net.minecraft.client.gui.screens.Screen
import org.hypixelskyblockmods.scrollabletooltips.TooltipScrollController
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(Screen::class)
abstract class ScreenMixin {
    @Inject(method = ["removed"], at = [At("HEAD")])
    private fun scrollableTooltips_reset(callback: CallbackInfo) {
        TooltipScrollController.reset()
    }
}
