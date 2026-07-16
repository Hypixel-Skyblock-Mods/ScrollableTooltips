package org.hypixelskyblockmods.scrollabletooltips.mixin

import net.minecraft.client.MouseHandler
import org.hypixelskyblockmods.scrollabletooltips.TooltipScrollController
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(MouseHandler::class)
abstract class MouseHandlerMixin {
    @Inject(method = ["onScroll"], at = [At("HEAD")], cancellable = true)
    private fun scrollableTooltips_captureScroll(
        window: Long,
        horizontal: Double,
        vertical: Double,
        callback: CallbackInfo,
    ) {
        if (TooltipScrollController.scroll(vertical)) {
            callback.cancel()
        }
    }
}
