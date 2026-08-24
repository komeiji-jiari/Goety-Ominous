/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraftforge.event.entity.living.LivingDamageEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.config.MobWeaknessConfig;
import com.vivideru.masteryofmagic.entity.GhiaccioEntity;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic")
public class GhiaccioFrostDamageHandler {
    private static boolean applyingBonusDamage = false;

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (applyingBonusDamage) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (target.m_9236_().f_46443_) {
            return;
        }
        if (target instanceof GhiaccioEntity) {
            return;
        }
        DamageSource source = event.getSource();
        if (!GhiaccioFrostDamageHandler.isFrostDamage(source)) {
            return;
        }
        if (!GhiaccioFrostDamageHandler.isNearGhiaccio(target)) {
            return;
        }
        float bonusDamage = target.m_21233_() * 0.01f + target.m_21223_() * 0.02f;
        if (bonusDamage <= 0.0f) {
            return;
        }
        applyingBonusDamage = true;
        target.m_6469_(target.m_269291_().m_269104_((Entity)target, null), bonusDamage);
        applyingBonusDamage = false;
    }

    private static boolean isNearGhiaccio(LivingEntity target) {
        AABB area;
        Level level = target.m_9236_();
        return !level.m_6443_(GhiaccioEntity.class, area = target.m_20191_().m_82400_(32.0), ghiaccio -> ghiaccio != null && ghiaccio.m_6084_()).isEmpty();
    }

    private static boolean isFrostDamage(DamageSource source) {
        ResourceKey key = source.m_269150_().m_203543_().orElse(null);
        if (key == null) {
            return false;
        }
        String id = key.m_135782_().toString();
        List<String> iceGroup = MobWeaknessConfig.getDamageGroups().get("ice");
        if (iceGroup == null) {
            return false;
        }
        return iceGroup.contains(id);
    }
}

