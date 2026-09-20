package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModTags;
import com.qiuyue.goetyominous.utils.CroneCuriosUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CroneRobeEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        float finalDamage = event.getAmount();
        if (CroneCuriosUtil.hasCroneRobe(victim) && event.getSource().is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            finalDamage *= 0.15F;
        }
        if (finalDamage != event.getAmount()) {
            event.setAmount(finalDamage);
        }

        if (victim instanceof Player player && CroneCuriosUtil.hasCroneRobe(victim)) {
            int thorns = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.THORNS,
                    com.Polarice3.Goety.utils.CuriosFinder.findCurio(victim,
                            stack -> stack.getItem() instanceof com.qiuyue.goetyominous.common.items.curios.CroneRobeItem));
            int soulCost = com.Polarice3.Goety.config.ItemConfig.SpitefulBeltUseAmount.get() * (thorns + 1);
            if (com.Polarice3.Goety.utils.SEHelper.getSoulsAmount(player, soulCost)
                    && !event.getSource().is(DamageTypeTags.IS_FIRE)
                    && !event.getSource().is(DamageTypes.IN_FIRE)) {
                net.minecraft.world.entity.Entity source = event.getSource().getEntity();
                if (source instanceof LivingEntity livingAttacker && livingAttacker != victim) {
                    livingAttacker.hurt(livingAttacker.damageSources().thorns(victim), 2.0F + (float) thorns);
                    com.Polarice3.Goety.utils.SEHelper.decreaseSouls(player, soulCost);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onTarget(LivingChangeTargetEvent event) {
        LivingEntity attacker = event.getEntity();
        LivingEntity target = event.getOriginalTarget();
        if (attacker instanceof Mob mobAttacker && target instanceof Player) {
            if (MobUtil.isWitchType(mobAttacker) && CroneCuriosUtil.isCroneWitchFriendly(target)) {
                if (mobAttacker.getLastHurtByMob() != target) {
                    if (event.getTargetType() == LivingChangeTargetEvent.LivingTargetType.MOB_TARGET) {
                        event.setNewTarget(null);
                    } else {
                        event.setCanceled(true);
                    }
                } else {
                    mobAttacker.setLastHurtByMob(target);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide && CroneCuriosUtil.hasCroneSet(entity)) {
            entity.spawnAtLocation(Items.STICK);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        LivingEntity owner = MobUtil.getOwner(entity);

        if (owner != null && CroneCuriosUtil.hasCroneHat(owner)
                && (entity.getMobType() == com.qiuyue.goetyominous.utils.ModMobType.FEL
                || entity.getType().is(ModTags.EntityTypes.FEL_HEAL))
                && entity instanceof com.Polarice3.Goety.api.entities.ally.IServant servant) {
            servant.setHasLifespan(false);
        }
    }

    @SubscribeEvent
    public static void onClientTick(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        net.minecraft.client.player.LocalPlayer player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null && CroneCuriosUtil.hasCroneSet(player) && player.tickCount % 5 == 0) {
            player.level().addParticle(ParticleTypes.WITCH,
                    player.getX(), player.getY() + player.getBbHeight() + 0.3D, player.getZ(),
                    0.0D, 0.02D, 0.0D);
        }
    }
}
