package com.rasmus.mobesp.mixin;

import com.rasmus.mobesp.config.MobespConfig;
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

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void makeMobsGlow(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof Mob) {
            MobespConfig config = MobespConfig.get();

            // Get the entity type name
            Identifier entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
            String mobType = entityId.getPath();

            // Force override - if ESP is disabled or mob type is disabled, never glow
            if (!config.masterToggle || !config.isMobGlowEnabled(mobType)) {
                cir.setReturnValue(false);
                return;
            }

            // If ESP is enabled for this mob type, make it glow
            if (config.isMobGlowEnabled(mobType)) {
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
            if (config.isMobGlowEnabled(mobType)) {
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
            if (config.isMobGlowEnabled(mobType)) {
                cir.setReturnValue(true);
            }
        }
    }
}
