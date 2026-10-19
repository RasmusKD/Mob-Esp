package com.rasmus.mobesp.util;

import java.util.List;

public class MobTypes {

    private static List<String> allMobTypes;

    public static List<String> getAllMobTypes() {
        if (allMobTypes == null) {
            // The spawn egg map is a TreeMap, so the keys are already sorted alphabetically
            List<String> types = List.copyOf(SpawnEggRenderer.getSpawnEggs().keySet());
            if (types.isEmpty()) {
                return types; // scan not ready (reload in progress): do not cache
            }
            allMobTypes = types;
        }
        return allMobTypes;
    }
}
