package com.qiuyue.someillagerservants.common.events;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.common.entities.hostile.cultists.AbstractSISCultist;
import com.qiuyue.someillagerservants.common.entities.hostile.cultists.Beldam;
import com.qiuyue.someillagerservants.config.MobsConfig;
import com.qiuyue.someillagerservants.utils.SISCultistHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SomeIllagerServants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SISCultistEvents {

    @SubscribeEvent
    public static void onVillagerSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (event.getEntity() instanceof Villager villager) {
            if (MobsConfig.CultistSpread.get()) {
                if (!villager.isBaby() && event.getSpawnType() == MobSpawnType.STRUCTURE) {
                    if (villager.level().random.nextFloat() <= 0.05F) {
                        villager.addTag(SISCultistHelper.SECRET_CULTIST_TAG);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity == null) return;

        if (livingEntity instanceof Witch witch) {
            if (MobsConfig.CultistSpread.get()) {
                List<AbstractSISCultist> list = witch.level().getEntitiesOfClass(
                        AbstractSISCultist.class, witch.getBoundingBox().inflate(8.0D));
                if (!witch.level().isClientSide && list.size() >= 5) {
                    ServerLevel serverWorld = (ServerLevel) witch.level();
                    Beldam beldam = witch.convertTo(
                            com.qiuyue.someillagerservants.common.init.ModEntityTypes.BELDAM.get(), true);
                    if (beldam != null) {
                        beldam.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(beldam.blockPosition()),
                                MobSpawnType.CONVERSION, null, null);
                        net.minecraftforge.event.ForgeEventFactory.onLivingConvert(witch, beldam);
                    }
                }
            }
        }

        if (livingEntity instanceof Villager villager) {
            if (MobsConfig.CultistSpread.get() && villager.getTags().contains(SISCultistHelper.SECRET_CULTIST_TAG)) {
                if (villager.getVillagerData().getLevel() >= 2) {
                    villager.removeTag(SISCultistHelper.SECRET_CULTIST_TAG);
                    return;
                }

                if (!SISCultistHelper.getWitnesses(villager)) {
                    if (villager.tickCount % 1000 == 0 && villager.level().random.nextFloat() <= 0.25F) {
                        float pitch = villager.isBaby()
                                ? (villager.level().random.nextFloat() - villager.level().random.nextFloat()) * 0.2F + 1.5F
                                : (villager.level().random.nextFloat() - villager.level().random.nextFloat()) * 0.2F + 1.0F;
                        villager.playSound(net.minecraft.sounds.SoundEvents.EVOKER_AMBIENT, 1.0F, pitch);
                    }
                }

                if (!villager.level().isClientSide) {
                    ServerLevel serverWorld = (ServerLevel) villager.level();
                    net.minecraft.world.entity.raid.Raid raid = serverWorld.getRaidAt(villager.blockPosition());
                    if (raid != null && raid.isActive() && !raid.isOver()) {
                        SISCultistHelper.revealCultist(serverWorld, villager);
                        return;
                    }

                    Player player = serverWorld.getNearestPlayer(villager, 16.0F);
                    if (player != null && !player.hasLineOfSight(villager)) {
                        if (villager.tickCount % 1000 == 0 && serverWorld.random.nextFloat() <= 0.25F) {
                            serverWorld.playLocalSound(player.getX(), player.getY(), player.getZ(),
                                    net.minecraft.sounds.SoundEvents.ELDER_GUARDIAN_CURSE,
                                    net.minecraft.sounds.SoundSource.HOSTILE, 1.0F, 0.5F, false);
                        }
                    }

                    if (villager.tickCount % 100 == 0) {
                        List<Villager> villagers = serverWorld.getEntitiesOfClass(
                                Villager.class, villager.getBoundingBox().inflate(32.0D, 10.0D, 32.0D));
                        boolean hasRegular = villagers.stream()
                                .anyMatch(v -> !v.getTags().contains(SISCultistHelper.SECRET_CULTIST_TAG));
                        if (!hasRegular) {
                            SISCultistHelper.revealCultist(serverWorld, villager);
                        }
                    }
                }

                if (villager.tickCount % 1200 == 0) {
                    SISCultistHelper.secretConversion(villager);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onTargetSet(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof Zombie && event.getNewTarget() instanceof Villager villager) {
            if (villager.getTags().contains(SISCultistHelper.SECRET_CULTIST_TAG)) {
                event.setNewTarget(null);
            }
        }
    }

    @SubscribeEvent
    public static void onVillagerHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof LivingEntity && victim instanceof Villager villager) {
            if (villager.getTags().contains(SISCultistHelper.SECRET_CULTIST_TAG) && !villager.isBaby()) {
                if (!villager.level().isClientSide) {
                    ServerLevel serverWorld = (ServerLevel) villager.level();
                    if (SISCultistHelper.getWitnesses(villager)) {
                        if (villager.getRandom().nextFloat() <= 0.25F) {
                            SISCultistHelper.revealCultist(serverWorld, villager);
                            if (!(attacker instanceof net.minecraft.world.entity.monster.Enemy)) {
                                SISCultistHelper.calmIronGolems(serverWorld, (LivingEntity) attacker);
                            }
                        }
                    } else {
                        SISCultistHelper.revealCultist(serverWorld, villager);
                        if (!(attacker instanceof net.minecraft.world.entity.monster.Enemy)) {
                            SISCultistHelper.calmIronGolems(serverWorld, (LivingEntity) attacker);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onVillagerDeath(LivingDeathEvent event) {
        Entity killed = event.getEntity();
        Entity killer = event.getSource().getEntity();
        if (killed instanceof Villager && killer instanceof Player player) {
            if (killed.getTags().contains(SISCultistHelper.REVEALED_CULTIST_TAG)) {
                for (Villager villager : killed.level().getEntitiesOfClass(
                        Villager.class, player.getBoundingBox().inflate(16.0D))) {
                    villager.getGossips().add(player.getUUID(),
                            net.minecraft.world.entity.ai.gossip.GossipType.MINOR_POSITIVE, 25);
                }
                SISCultistHelper.calmIronGolems(killed.level(), player);
            }
        }
    }
}
