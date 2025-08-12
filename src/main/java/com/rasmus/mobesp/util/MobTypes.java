package com.rasmus.mobesp.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MobTypes {

    // Keep categories for potential future use, but main method returns alphabetical
    public static final List<String> HOSTILE_MOBS = List.of(
            "zombie", "skeleton", "creeper", "spider", "enderman", "witch",
            "blaze", "ghast", "slime", "magma_cube", "wither_skeleton",
            "stray", "husk", "drowned", "phantom", "pillager", "vindicator",
            "evoker", "ravager", "vex", "zombified_piglin", "piglin_brute",
            "hoglin", "zoglin", "warden", "breeze", "zombie_villager",
            "creaking", "silverfish", "shulker", "guardian", "endermite",
            "bogged", "cave_spider"
    );

    public static final List<String> NEUTRAL_MOBS = List.of(
            "piglin", "polar_bear", "wolf", "goat", "spider", "cave_spider",
            "enderman", "zombie_horse", "skeleton_horse", "iron_golem"
    );

    public static final List<String> PASSIVE_MOBS = List.of(
            "pig", "cow", "sheep", "chicken", "horse", "donkey", "mule",
            "llama", "trader_llama", "cat", "ocelot", "parrot", "rabbit",
            "fox", "bee", "panda", "axolotl", "glow_squid", "squid",
            "dolphin", "turtle", "cod", "salmon", "pufferfish", "tropical_fish",
            "villager", "wandering_trader", "bat", "mooshroom", "strider",
            "frog", "tadpole", "allay", "camel", "sniffer", "armadillo",
            "snow_golem", "happy_ghast"
    );

    public static final List<String> BOSS_MOBS = List.of(
            "wither", "ender_dragon", "elder_guardian"
    );

    public static List<String> getAllMobTypes() {
        List<String> allMobs = new ArrayList<>();
        allMobs.addAll(HOSTILE_MOBS);
        allMobs.addAll(NEUTRAL_MOBS);
        allMobs.addAll(PASSIVE_MOBS);
        allMobs.addAll(BOSS_MOBS);

        // Remove duplicates and sort alphabetically
        List<String> uniqueMobs = allMobs.stream().distinct().toList();
        List<String> sortedMobs = new ArrayList<>(uniqueMobs);
        Collections.sort(sortedMobs);

        return sortedMobs;
    }
}