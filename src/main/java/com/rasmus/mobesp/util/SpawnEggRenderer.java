package com.rasmus.mobesp.util;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

import java.util.Map;
import java.util.TreeMap;

public class SpawnEggRenderer {

    // Derived from the item registry on first use, so every mob that has a spawn egg is
    // covered automatically on new Minecraft versions with no mod update. Built lazily
    // because item registration must be complete before the scan runs; the config screen
    // can only open in-game, long after registries are frozen.
    private static Map<String, Item> spawnEggs;

    static Map<String, Item> getSpawnEggs() {
        if (spawnEggs == null) {
            Map<String, Item> eggs = new TreeMap<>();
            for (Item item : BuiltInRegistries.ITEM) {
                if (item instanceof SpawnEggItem) {
                    EntityType<?> type = SpawnEggItem.getType(new ItemStack(item));
                    if (type != null) {
                        eggs.put(BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath(), item);
                    }
                }
            }
            spawnEggs = eggs;
        }
        return spawnEggs;
    }

    public static void renderMobIcon(GuiGraphicsExtractor extractor, String mobType, int x, int y) {
        Item spawnEgg = getSpawnEggs().get(mobType);
        if (spawnEgg != null) {
            ItemStack stack = new ItemStack(spawnEgg);
            // Render inline with proper positioning
            extractor.item(stack, x + 6, y + 6);
        }
    }

    public static String getFormattedName(String mobType) {
        return mobType.substring(0, 1).toUpperCase() + mobType.substring(1).replace("_", " ");
    }
}
