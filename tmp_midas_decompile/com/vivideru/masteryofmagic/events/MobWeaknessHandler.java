/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MobType
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.events;

import com.vivideru.masteryofmagic.config.MobWeaknessConfig;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
public class MobWeaknessHandler {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        float originalDamage = event.getAmount();
        if (originalDamage <= 0.0f) {
            return;
        }
        ResourceLocation damageId = entity.m_9236_().m_9598_().m_175515_(Registries.f_268580_).m_7981_((Object)event.getSource().m_269415_());
        if (damageId == null) {
            return;
        }
        String damageString = damageId.toString();
        List<MobWeaknessConfig.WeaknessEntry> entries = MobWeaknessConfig.getEntries();
        Map<String, List<String>> groups = MobWeaknessConfig.getDamageGroups();
        float bestMultiplier = 1.0f;
        for (MobWeaknessConfig.WeaknessEntry entry : entries) {
            if (!MobWeaknessHandler.matchesMob(entity, entry.mobId) || !MobWeaknessHandler.matchesDamage(damageString, entry.damageKey, groups, event) || !(entry.multiplier > bestMultiplier)) continue;
            bestMultiplier = entry.multiplier;
        }
        if (bestMultiplier != 1.0f) {
            event.setAmount(originalDamage * bestMultiplier);
        }
    }

    private static boolean matchesDamage(String damageIdString, String damageKey, Map<String, List<String>> groups, LivingHurtEvent event) {
        Registry registry = event.getEntity().m_9236_().m_9598_().m_175515_(Registries.f_268580_);
        Holder holder = event.getSource().m_269150_();
        if (damageIdString.equals(damageKey)) {
            return true;
        }
        if (damageKey.startsWith("#")) {
            ResourceLocation tagId = new ResourceLocation(damageKey.substring(1));
            TagKey tag = TagKey.m_203882_((ResourceKey)Registries.f_268580_, (ResourceLocation)tagId);
            return holder.m_203656_(tag);
        }
        if (groups.containsKey(damageKey)) {
            for (String entry : groups.get(damageKey)) {
                ResourceLocation tagId;
                TagKey tag;
                if (entry.equals(damageIdString)) {
                    return true;
                }
                if (!entry.startsWith("#") || !holder.m_203656_(tag = TagKey.m_203882_((ResourceKey)Registries.f_268580_, (ResourceLocation)(tagId = new ResourceLocation(entry.substring(1)))))) continue;
                return true;
            }
        }
        return false;
    }

    private static boolean matchesMob(LivingEntity entity, String mobIdOrTag) {
        if (mobIdOrTag.equals("#undead")) {
            return entity.m_6336_() == MobType.f_21641_;
        }
        if (mobIdOrTag.startsWith("#")) {
            ResourceLocation tagId = new ResourceLocation(mobIdOrTag.substring(1));
            TagKey tagKey = TagKey.m_203882_((ResourceKey)Registries.f_256939_, (ResourceLocation)tagId);
            return entity.m_6095_().m_204039_(tagKey);
        }
        ResourceLocation entityId = EntityType.m_20613_((EntityType)entity.m_6095_());
        if (entityId == null) {
            return false;
        }
        return entityId.toString().equals(mobIdOrTag);
    }
}

