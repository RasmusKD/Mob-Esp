package com.rasmus.mobesp.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

import java.util.HashMap;
import java.util.Map;

@Config(name = "mobesp")
public class MobespConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public boolean masterToggle = true;

    @ConfigEntry.Gui.Tooltip
    public Map<String, Boolean> mobGlowStates = new HashMap<>();

    public boolean isMobGlowEnabled(String mobType) {
        if (!masterToggle) return false;
        return mobGlowStates.getOrDefault(mobType, false);
    }

    public void setMobGlowEnabled(String mobType, boolean enabled) {
        mobGlowStates.put(mobType, enabled);
    }

    public static void register() {
        AutoConfig.register(MobespConfig.class, GsonConfigSerializer::new);
    }

    public static MobespConfig get() {
        return AutoConfig.getConfigHolder(MobespConfig.class).getConfig();
    }
}