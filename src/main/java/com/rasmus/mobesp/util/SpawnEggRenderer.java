package com.rasmus.mobesp.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

public class SpawnEggRenderer {

    private static final Map<String, Item> SPAWN_EGGS = new HashMap<>();

    static {
        // Hostile mobs
        SPAWN_EGGS.put("zombie", Items.ZOMBIE_SPAWN_EGG);
        SPAWN_EGGS.put("skeleton", Items.SKELETON_SPAWN_EGG);
        SPAWN_EGGS.put("creeper", Items.CREEPER_SPAWN_EGG);
        SPAWN_EGGS.put("spider", Items.SPIDER_SPAWN_EGG);
        SPAWN_EGGS.put("enderman", Items.ENDERMAN_SPAWN_EGG);
        SPAWN_EGGS.put("witch", Items.WITCH_SPAWN_EGG);
        SPAWN_EGGS.put("blaze", Items.BLAZE_SPAWN_EGG);
        SPAWN_EGGS.put("ghast", Items.GHAST_SPAWN_EGG);
        SPAWN_EGGS.put("slime", Items.SLIME_SPAWN_EGG);
        SPAWN_EGGS.put("magma_cube", Items.MAGMA_CUBE_SPAWN_EGG);
        SPAWN_EGGS.put("wither_skeleton", Items.WITHER_SKELETON_SPAWN_EGG);
        SPAWN_EGGS.put("stray", Items.STRAY_SPAWN_EGG);
        SPAWN_EGGS.put("husk", Items.HUSK_SPAWN_EGG);
        SPAWN_EGGS.put("drowned", Items.DROWNED_SPAWN_EGG);
        SPAWN_EGGS.put("phantom", Items.PHANTOM_SPAWN_EGG);
        SPAWN_EGGS.put("pillager", Items.PILLAGER_SPAWN_EGG);
        SPAWN_EGGS.put("vindicator", Items.VINDICATOR_SPAWN_EGG);
        SPAWN_EGGS.put("evoker", Items.EVOKER_SPAWN_EGG);
        SPAWN_EGGS.put("ravager", Items.RAVAGER_SPAWN_EGG);
        SPAWN_EGGS.put("vex", Items.VEX_SPAWN_EGG);
        SPAWN_EGGS.put("zombified_piglin", Items.ZOMBIFIED_PIGLIN_SPAWN_EGG);
        SPAWN_EGGS.put("piglin_brute", Items.PIGLIN_BRUTE_SPAWN_EGG);
        SPAWN_EGGS.put("hoglin", Items.HOGLIN_SPAWN_EGG);
        SPAWN_EGGS.put("zoglin", Items.ZOGLIN_SPAWN_EGG);
        SPAWN_EGGS.put("warden", Items.WARDEN_SPAWN_EGG);
        SPAWN_EGGS.put("breeze", Items.BREEZE_SPAWN_EGG);
        SPAWN_EGGS.put("zombie_villager", Items.ZOMBIE_VILLAGER_SPAWN_EGG);
        SPAWN_EGGS.put("silverfish", Items.SILVERFISH_SPAWN_EGG);
        SPAWN_EGGS.put("shulker", Items.SHULKER_SPAWN_EGG);
        SPAWN_EGGS.put("guardian", Items.GUARDIAN_SPAWN_EGG);
        SPAWN_EGGS.put("endermite", Items.ENDERMITE_SPAWN_EGG);
        SPAWN_EGGS.put("bogged", Items.BOGGED_SPAWN_EGG);
        SPAWN_EGGS.put("cave_spider", Items.CAVE_SPIDER_SPAWN_EGG);
        SPAWN_EGGS.put("creaking", Items.CREAKING_SPAWN_EGG);

        // Neutral mobs
        SPAWN_EGGS.put("piglin", Items.PIGLIN_SPAWN_EGG);
        SPAWN_EGGS.put("polar_bear", Items.POLAR_BEAR_SPAWN_EGG);
        SPAWN_EGGS.put("wolf", Items.WOLF_SPAWN_EGG);
        SPAWN_EGGS.put("goat", Items.GOAT_SPAWN_EGG);
        SPAWN_EGGS.put("zombie_horse", Items.ZOMBIE_HORSE_SPAWN_EGG);
        SPAWN_EGGS.put("skeleton_horse", Items.SKELETON_HORSE_SPAWN_EGG);
        SPAWN_EGGS.put("iron_golem", Items.IRON_GOLEM_SPAWN_EGG);

        // Passive mobs
        SPAWN_EGGS.put("pig", Items.PIG_SPAWN_EGG);
        SPAWN_EGGS.put("cow", Items.COW_SPAWN_EGG);
        SPAWN_EGGS.put("sheep", Items.SHEEP_SPAWN_EGG);
        SPAWN_EGGS.put("chicken", Items.CHICKEN_SPAWN_EGG);
        SPAWN_EGGS.put("horse", Items.HORSE_SPAWN_EGG);
        SPAWN_EGGS.put("donkey", Items.DONKEY_SPAWN_EGG);
        SPAWN_EGGS.put("mule", Items.MULE_SPAWN_EGG);
        SPAWN_EGGS.put("llama", Items.LLAMA_SPAWN_EGG);
        SPAWN_EGGS.put("trader_llama", Items.TRADER_LLAMA_SPAWN_EGG);
        SPAWN_EGGS.put("cat", Items.CAT_SPAWN_EGG);
        SPAWN_EGGS.put("ocelot", Items.OCELOT_SPAWN_EGG);
        SPAWN_EGGS.put("parrot", Items.PARROT_SPAWN_EGG);
        SPAWN_EGGS.put("rabbit", Items.RABBIT_SPAWN_EGG);
        SPAWN_EGGS.put("fox", Items.FOX_SPAWN_EGG);
        SPAWN_EGGS.put("bee", Items.BEE_SPAWN_EGG);
        SPAWN_EGGS.put("panda", Items.PANDA_SPAWN_EGG);
        SPAWN_EGGS.put("axolotl", Items.AXOLOTL_SPAWN_EGG);
        SPAWN_EGGS.put("glow_squid", Items.GLOW_SQUID_SPAWN_EGG);
        SPAWN_EGGS.put("squid", Items.SQUID_SPAWN_EGG);
        SPAWN_EGGS.put("dolphin", Items.DOLPHIN_SPAWN_EGG);
        SPAWN_EGGS.put("turtle", Items.TURTLE_SPAWN_EGG);
        SPAWN_EGGS.put("cod", Items.COD_SPAWN_EGG);
        SPAWN_EGGS.put("salmon", Items.SALMON_SPAWN_EGG);
        SPAWN_EGGS.put("pufferfish", Items.PUFFERFISH_SPAWN_EGG);
        SPAWN_EGGS.put("tropical_fish", Items.TROPICAL_FISH_SPAWN_EGG);
        SPAWN_EGGS.put("villager", Items.VILLAGER_SPAWN_EGG);
        SPAWN_EGGS.put("wandering_trader", Items.WANDERING_TRADER_SPAWN_EGG);
        SPAWN_EGGS.put("bat", Items.BAT_SPAWN_EGG);
        SPAWN_EGGS.put("mooshroom", Items.MOOSHROOM_SPAWN_EGG);
        SPAWN_EGGS.put("strider", Items.STRIDER_SPAWN_EGG);
        SPAWN_EGGS.put("frog", Items.FROG_SPAWN_EGG);
        SPAWN_EGGS.put("tadpole", Items.TADPOLE_SPAWN_EGG);
        SPAWN_EGGS.put("allay", Items.ALLAY_SPAWN_EGG);
        SPAWN_EGGS.put("camel", Items.CAMEL_SPAWN_EGG);
        SPAWN_EGGS.put("sniffer", Items.SNIFFER_SPAWN_EGG);
        SPAWN_EGGS.put("armadillo", Items.ARMADILLO_SPAWN_EGG);
        SPAWN_EGGS.put("snow_golem", Items.SNOW_GOLEM_SPAWN_EGG);
        SPAWN_EGGS.put("happy_ghast", Items.HAPPY_GHAST_SPAWN_EGG);

        // Boss mobs
        SPAWN_EGGS.put("elder_guardian", Items.ELDER_GUARDIAN_SPAWN_EGG);
        SPAWN_EGGS.put("wither", Items.WITHER_SPAWN_EGG);
        SPAWN_EGGS.put("ender_dragon", Items.ENDER_DRAGON_SPAWN_EGG);
    }

    public static void renderMobIcon(DrawContext context, String mobType, int x, int y) {
        Item spawnEgg = SPAWN_EGGS.get(mobType);
        if (spawnEgg != null) {
            ItemStack stack = new ItemStack(spawnEgg);
            // Render inline with proper positioning
            context.drawItem(stack, x + 6, y + 6);
        }
    }

    public static String getFormattedName(String mobType) {
        return mobType.substring(0, 1).toUpperCase() + mobType.substring(1).replace("_", " ");
    }
}