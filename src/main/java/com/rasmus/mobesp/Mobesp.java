package com.rasmus.mobesp;

import com.rasmus.mobesp.config.MobespConfig;
import net.fabricmc.api.ModInitializer;

public class Mobesp implements ModInitializer {

    @Override
    public void onInitialize() {
        MobespConfig.register();
    }
}