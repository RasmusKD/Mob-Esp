package com.rasmus.mobesp.mixin;

import com.rasmus.mobesp.config.MobespConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void makeMobsGlow(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof MobEntity) {
            MobespConfig config = MobespConfig.get();

            // Get the entity type name
            Identifier entityId = Registries.ENTITY_TYPE.getId(entity.getType());
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
}