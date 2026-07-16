package org.hypixelskyblockmods.scrollabletooltips

import net.fabricmc.api.ClientModInitializer
import org.slf4j.LoggerFactory

object ScrollableTooltipsClient : ClientModInitializer {
    const val MOD_ID = "scrollabletooltips"
    private val logger = LoggerFactory.getLogger("ScrollableTooltips")

    override fun onInitializeClient() {
        logger.info("Scrollable Tooltips initialized")
    }
}
