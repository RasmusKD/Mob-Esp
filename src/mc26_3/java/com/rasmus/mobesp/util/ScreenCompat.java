package com.rasmus.mobesp.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/** 26.3 keeps the screen behind Minecraft.gui. Overlays the reflective 26.1/26.2 class of the same name. */
public final class ScreenCompat {
    private ScreenCompat() {
    }

    public static Screen current(Minecraft minecraft) {
        return minecraft.gui.screen();
    }

    public static void set(Minecraft minecraft, Screen screen) {
        minecraft.gui.setScreen(screen);
    }
}
