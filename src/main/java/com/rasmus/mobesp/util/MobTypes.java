package com.rasmus.mobesp.util;

import java.util.List;

public class MobTypes {

    private static List<String> allMobTypes;

    public static List<String> getAllMobTypes() {
        if (allMobTypes == null) {
            // The spawn egg map is a TreeMap, so the keys are already sorted alphabetically
            allMobTypes = List.copyOf(SpawnEggRenderer.getSpawnEggs().keySet());
        }
        return allMobTypes;
    }
}
