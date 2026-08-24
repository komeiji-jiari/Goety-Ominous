/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IWand
 *  com.Polarice3.Goety.common.entities.projectiles.ScytheSlash
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.vivideru.masteryofmagic.projectile.homing;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.projectiles.ScytheSlash;
import com.vivideru.masteryofmagic.SpellRingHelper;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEnchantments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class HomingProjectileSpawnHandler {
    public static final String HOMING_LEVEL_TAG = "GoetyMasteryHomingLevel";
    public static final String HOMING_TARGET_TAG = "GoetyMasteryHomingTarget";
    private static final boolean DEBUG = false;

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity.m_9236_().m_5776_()) {
            return;
        }
        HomingProjectileSpawnHandler.debug("Entity joined level: entity=" + entity.m_6095_().m_147048_() + " id=" + entity.m_19879_());
        if (!(entity instanceof Projectile)) {
            HomingProjectileSpawnHandler.debug("Entity ignored: not a Projectile. entity=" + entity.m_6095_().m_147048_() + " id=" + entity.m_19879_());
            return;
        }
        Projectile projectile = (Projectile)entity;
        if (projectile instanceof ScytheSlash) {
            return;
        }
        HomingProjectileSpawnHandler.debug("Projectile joined level: projectile=" + projectile.m_6095_().m_147048_() + " id=" + projectile.m_19879_());
        Entity owner = projectile.m_19749_();
        if (owner == null) {
            HomingProjectileSpawnHandler.debug("Projectile ignored: owner is null. projectile=" + projectile.m_6095_().m_147048_() + " id=" + projectile.m_19879_());
            return;
        }
        HomingProjectileSpawnHandler.debug("Projectile owner: owner=" + owner.m_6095_().m_147048_() + " ownerId=" + owner.m_19879_());
        if (!(owner instanceof LivingEntity)) {
            HomingProjectileSpawnHandler.debug("Projectile ignored: owner is not LivingEntity. owner=" + owner.m_6095_().m_147048_() + " ownerId=" + owner.m_19879_());
            return;
        }
        LivingEntity livingOwner = (LivingEntity)owner;
        ItemStack castingFocus = HomingProjectileSpawnHandler.findActivelyCastingFocus(livingOwner);
        int homingLevel = castingFocus.m_41619_() ? 0 : EnchantmentHelper.m_44843_((Enchantment)((Enchantment)GoetyMasteryOfMagicModEnchantments.HOMING.get()), (ItemStack)castingFocus);
        HomingProjectileSpawnHandler.debug("Owner focus check: owner=" + livingOwner.m_6095_().m_147048_() + " ownerId=" + livingOwner.m_19879_() + " castingFocus=" + castingFocus + " homingLevel=" + homingLevel);
        if (homingLevel <= 0) {
            HomingProjectileSpawnHandler.debug("Projectile ignored: homing level <= 0. projectile=" + projectile.m_6095_().m_147048_() + " id=" + projectile.m_19879_());
            return;
        }
        CompoundTag persistentData = projectile.getPersistentData();
        persistentData.m_128405_(HOMING_LEVEL_TAG, Math.min(homingLevel, 3));
        persistentData.m_128473_(HOMING_TARGET_TAG);
        HomingProjectileSpawnHandler.debug("Projectile marked as homing: projectile=" + projectile.m_6095_().m_147048_() + " id=" + projectile.m_19879_() + " homingLevel=" + Math.min(homingLevel, 3));
    }

    private static ItemStack findActivelyCastingFocus(LivingEntity owner) {
        ItemStack swungStack;
        Player player;
        ItemStack ringFocus;
        if (owner instanceof Player && !(ringFocus = SpellRingHelper.getCastingFocus(player = (Player)owner)).m_41619_()) {
            return ringFocus;
        }
        if (owner.m_6117_()) {
            ItemStack usedStack = owner.m_21211_();
            if (usedStack.m_41720_() instanceof IWand) {
                return IWand.getFocus((ItemStack)usedStack);
            }
            return ItemStack.f_41583_;
        }
        if (owner.f_20911_ && (swungStack = owner.m_21120_(owner.f_20912_)).m_41720_() instanceof IWand) {
            return IWand.getFocus((ItemStack)swungStack);
        }
        return ItemStack.f_41583_;
    }

    private static void debug(String message) {
    }
}

