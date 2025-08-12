package com.rasmus.mobesp.client;

import com.rasmus.mobesp.client.gui.MobEspConfigScreen;
import com.rasmus.mobesp.config.MobespConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobespClient implements ClientModInitializer {
    public static final String MOD_ID = "mobesp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding toggleMasterKeyBinding;
    private static KeyBinding openConfigKeyBinding;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Mobesp mod initialized!");

        toggleMasterKeyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.mobesp.toggleMaster",
                        InputUtil.Type.KEYSYM,
                        77, // M key (for Mob)
                        "key.category.mobesp"
                )
        );

        openConfigKeyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.mobesp.openConfig",
                        InputUtil.Type.KEYSYM,
                        79, // O key (for Open)
                        "key.category.mobesp"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleMasterKeyBinding.wasPressed()) {
                MobespConfig config = MobespConfig.get();
                config.masterToggle = !config.masterToggle;
                AutoConfig.getConfigHolder(MobespConfig.class).save();

                // Create colored message like in CropMod
                Text message = config.masterToggle ?
                        Text.literal("Mob ESP enabled").formatted(Formatting.GREEN) :
                        Text.literal("Mob ESP disabled").formatted(Formatting.RED);

                if (client.player != null) {
                    client.player.sendMessage(message, false);
                }
            }

            while (openConfigKeyBinding.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new MobEspConfigScreen(null));
                }
            }
        });
    }
}