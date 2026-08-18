package com.rasmus.mobesp.mixin;

import com.rasmus.mobesp.config.MobespConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    // Rare Fish Finder is the authority on tropical fish glow when both mods
    // are installed: its rare/collected logic decides per variant, and the
    // ESP voting yes on every tropical fish would light up commons in the
    // middle of a rare hunt. The ESP keeps every other mob.
    private static final boolean FISH_MOD_PRESENT =
            FabricLoader.getInstance().isModLoaded("rarefishfinder");

    private static boolean espOwns(Entity entity, String mobType) {
        return !(FISH_MOD_PRESENT && mobType.equals("tropical_fish"));
    }

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void makeMobsGlow(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof Mob) {
            MobespConfig config = MobespConfig.get();

            // Get the entity type name
            Identifier entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
            String mobType = entityId.getPath();

            // Only ever vote YES. Forcing false here silences every other voter
            // on the same hook (rare-fish-finder's glow, and vanilla's own
            // spectral-arrow/status-effect glowing) for every mob the ESP simply
            // has no opinion on. Falling through leaves the decision to them.
            if (espOwns(entity, mobType)
                    && config.masterToggle && config.isMobGlowEnabled(mobType)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void useConfiguredEspColor(CallbackInfoReturnable<Integer> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof Mob) {
            MobespConfig config = MobespConfig.get();
            String mobType = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getPath();

            // Only recolor when the glow comes from this mod (isMobGlowEnabled includes the master toggle)
            if (espOwns(entity, mobType) && config.isMobGlowEnabled(mobType)) {
                cir.setReturnValue(config.espColor);
            }
        }
    }

    @Inject(method = "shouldRenderAtSqrDistance", at = @At("HEAD"), cancellable = true)
    private void alwaysRenderEspMobs(double distance, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof Mob) {
            MobespConfig config = MobespConfig.get();
            String mobType = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getPath();

            // The vanilla per-size render cutoff (~64 blocks) kicks in long before the server
            // stops sending the entity, which is why a minimap radar sees mobs the ESP does
            // not. Rendering ESP-enabled mobs unconditionally makes the glow reach as far as
            // the client knows about the mob at all.
            if (espOwns(entity, mobType) && config.isMobGlowEnabled(mobType)) {
                cir.setReturnValue(true);
            }
        }
    }
}
