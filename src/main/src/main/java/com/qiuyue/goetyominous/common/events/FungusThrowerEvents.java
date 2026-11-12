package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.common.entities.projectiles.AcidPool;
import com.qiuyue.goetyominous.common.entities.ally.mobs.EliteFungusThrower;
import com.qiuyue.goetyominous.common.entities.ally.mobs.FungusThrower;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FungusThrowerEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide) return;
        FungusThrower thrower = null;
        if (event.getSource().getDirectEntity() instanceof AcidPool pool
                && pool.getOwner() instanceof FungusThrower owner) {
            thrower = owner;
        } else if (event.getSource().getEntity() instanceof FungusThrower owner) {
            thrower = owner;
        }
        if (thrower == null || thrower.isRemoved() || !thrower.isAlive()
                || thrower instanceof EliteFungusThrower) return;

        int dealt = (int) Math.ceil(event.getAmount());
        if (dealt <= 0) return;

        thrower.onRangedDamageDealt(dealt);
        if (thrower.getRangedDamageDealt() < MobsConfig.FungusThrowerEvolutionDamage.get()) return;

        ItemStack[] gear = new ItemStack[EquipmentSlot.values().length];
        ItemStack[] inv = new ItemStack[thrower.getInventory().getContainerSize()];
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            gear[slot.ordinal()] = thrower.getItemBySlot(slot).copy();
        }
        for (int i = 0; i < inv.length; i++) {
            inv[i] = thrower.getInventory().getItem(i).copy();
        }

        EliteFungusThrower elite = new EliteFungusThrower(
                ModEntityTypes.ELITE_FUNGUS_THROWER.get(), (ServerLevel) thrower.level());
        elite.getPersistentData().putBoolean("Upgraded", true);
        elite.copyTrueOwner(thrower);
        elite.moveTo(thrower.getX(), thrower.getY(), thrower.getZ(), thrower.getYRot(), thrower.getXRot());
        elite.setBaby(thrower.isBaby());
        elite.setHealth(elite.getMaxHealth());
        if (thrower.isImmune()) elite.setImmuneToZombification(true);

        thrower.discard();
        ServerLevel serverLevel = (ServerLevel) thrower.level();
        serverLevel.addFreshEntity(elite);

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = gear[slot.ordinal()];
            if (!stack.isEmpty()) elite.setItemSlot(slot, stack);
        }
        for (int i = 0; i < inv.length; i++) {
            if (!inv[i].isEmpty()) elite.getInventory().setItem(i, inv[i]);
        }
        serverLevel.sendParticles(ParticleTypes.FLASH,
                thrower.getX(), thrower.getY() + 1.0, thrower.getZ(), 1, 0, 0, 0, 0);
    }
}
