package com.rasmus.mobesp.config;

import com.rasmus.mobesp.client.gui.MobEspConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class MobespModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return MobEspConfigScreen::new;
    }
}