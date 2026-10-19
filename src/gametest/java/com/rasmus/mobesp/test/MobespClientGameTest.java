package com.rasmus.mobesp.test;

import com.mojang.blaze3d.platform.InputConstants;
import com.rasmus.mobesp.client.gui.MobEspConfigScreen;
import com.rasmus.mobesp.util.ScreenCompat;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

/** Title screen, new singleplayer world, press the config key (K), screenshot, assert the screen opened. */
public class MobespClientGameTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        context.takeScreenshot("mobesp-title");
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            context.waitTicks(60);
            context.getInput().pressKey(InputConstants.KEY_K);
            context.waitFor(client -> ScreenCompat.current(client) instanceof MobEspConfigScreen);
            context.takeScreenshot("mobesp-config-screen");
            context.getInput().pressKey(InputConstants.KEY_ESCAPE);
            context.waitFor(client -> ScreenCompat.current(client) == null);
        }
    }
}
