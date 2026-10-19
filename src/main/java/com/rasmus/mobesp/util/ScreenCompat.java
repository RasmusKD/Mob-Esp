package com.rasmus.mobesp.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * The current screen lives in the field Minecraft.screen on 26.1 and behind Minecraft.gui
 * (Gui.screen() and Gui.setScreen) from 26.2. This jar serves both, so the members are
 * resolved by name. The 26.3 jar replaces this class with direct calls.
 */
public final class ScreenCompat {
    private static final Field MINECRAFT_SCREEN = field(Minecraft.class, "screen");
    private static final Method MINECRAFT_SET_SCREEN = method(Minecraft.class, "setScreen", Screen.class);
    private static final Method GUI_SCREEN = method(Gui.class, "screen");
    private static final Method GUI_SET_SCREEN = method(Gui.class, "setScreen", Screen.class);

    private ScreenCompat() {
    }

    public static Screen current(Minecraft minecraft) {
        try {
            if (MINECRAFT_SCREEN != null) {
                return (Screen) MINECRAFT_SCREEN.get(minecraft);
            }
            return (Screen) GUI_SCREEN.invoke(minecraft.gui);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot read the current screen", e);
        }
    }

    public static void set(Minecraft minecraft, Screen screen) {
        try {
            if (MINECRAFT_SET_SCREEN != null) {
                MINECRAFT_SET_SCREEN.invoke(minecraft, screen);
            } else {
                GUI_SET_SCREEN.invoke(minecraft.gui, screen);
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot set the screen", e);
        }
    }

    private static Field field(Class<?> owner, String name) {
        try {
            return owner.getField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static Method method(Class<?> owner, String name, Class<?>... params) {
        try {
            return owner.getMethod(name, params);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
