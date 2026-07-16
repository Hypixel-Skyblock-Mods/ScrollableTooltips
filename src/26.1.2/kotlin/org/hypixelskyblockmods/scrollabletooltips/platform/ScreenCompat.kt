package org.hypixelskyblockmods.scrollabletooltips.platform

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen

internal object ScreenCompat {
    fun currentScreen(): Screen? = Minecraft.getInstance().screen
}
