package com.rasmus.mobesp.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.rasmus.mobesp.client.gui.MobEspConfigScreen;
import com.rasmus.mobesp.config.MobespConfig;
import com.rasmus.mobesp.util.ScreenCompat;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobespClient implements ClientModInitializer {
    public static final String MOD_ID = "mobesp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyMapping toggleMasterKeyBinding;
    private static KeyMapping openConfigKeyBinding;

    private static final KeyMapping.Category MOBESP_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("mobesp", "category"));

    @Override
    public void onInitializeClient() {
        LOGGER.info("Mobesp mod initialized!");

        toggleMasterKeyBinding = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.mobesp.toggleMaster",
                        InputConstants.KEY_M, // M key (for Mob)
                        MOBESP_CATEGORY
                )
        );

        openConfigKeyBinding = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.mobesp.openConfig",
                        InputConstants.KEY_K, // K: vanilla uses O for Friends from 26.2 on
                        MOBESP_CATEGORY
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleMasterKeyBinding.consumeClick()) {
                MobespConfig config = MobespConfig.get();
                config.masterToggle = !config.masterToggle;
                AutoConfig.getConfigHolder(MobespConfig.class).save();

                Component message = config.masterToggle ?
                        Component.literal("Mob ESP enabled").withStyle(ChatFormatting.GREEN) :
                        Component.literal("Mob ESP disabled").withStyle(ChatFormatting.RED);

                if (client.player != null) {
                    client.player.sendSystemMessage(message);
                }
            }

            while (openConfigKeyBinding.consumeClick()) {
                if (ScreenCompat.current(client) == null) {
                    ScreenCompat.set(client, new MobEspConfigScreen(null));
                }
            }
        });
    }
}
