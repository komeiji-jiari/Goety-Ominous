package com.qiuyue.goetyominous.utils;

import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;
public class GOCultistHelper {

    public static final String SECRET_CULTIST_TAG = "sis_secret_cultist";
    public static final String REVEALED_CULTIST_TAG = "sis_revealed_cultist";

    public static void secretConversion(LivingEntity livingEntity) {
        if (!MobsConfig.CultistSpread.get()) return;

        for (Villager villager : livingEntity.level().getEntitiesOfClass(
                Villager.class, livingEntity.getBoundingBox().inflate(4.0F))) {

            if (!villager.getTags().contains(SECRET_CULTIST_TAG)) {
                float chance = 0.05F;
                if (villager.isBaby()) {
                    chance = 0.25F;
                }
                if (livingEntity.getRandom().nextFloat() <= chance) {
                    villager.addTag(SECRET_CULTIST_TAG);
                }
            }
        }
    }

    public static boolean getWitnesses(LivingEntity livingEntity) {
        for (Villager villager : livingEntity.level().getEntitiesOfClass(
                Villager.class, livingEntity.getBoundingBox().inflate(16.0F))) {
            if (villager.getSensing().hasLineOfSight(livingEntity)) {
                return true;
            }
        }
        for (IronGolem golemEntity : livingEntity.level().getEntitiesOfClass(
                IronGolem.class, livingEntity.getBoundingBox().inflate(16.0F))) {
            if (golemEntity.getSensing().hasLineOfSight(livingEntity)) {
                return true;
            }
        }
        return false;
    }

    public static void villagerReleasePoi(Villager villager) {
        villager.releasePoi(net.minecraft.world.entity.ai.memory.MemoryModuleType.HOME);
        villager.releasePoi(net.minecraft.world.entity.ai.memory.MemoryModuleType.JOB_SITE);
        villager.releasePoi(net.minecraft.world.entity.ai.memory.MemoryModuleType.POTENTIAL_JOB_SITE);
        villager.releasePoi(net.minecraft.world.entity.ai.memory.MemoryModuleType.MEETING_POINT);
    }

    public static void revealCultist(ServerLevel pLevel, Villager villager) {
        if (!MobsConfig.CultistSpread.get()) return;
        if (pLevel.getDifficulty() == Difficulty.PEACEFUL) return;
        if (!villager.getTags().contains(SECRET_CULTIST_TAG) || villager.isBaby()) return;

        VillagerProfession profession = villager.getVillagerData().getProfession();
        Monster cultist = ModEntityTypes.FANATIC.get().create(pLevel);

        if ((profession == VillagerProfession.CLERIC || profession == VillagerProfession.LIBRARIAN)
                && pLevel.random.nextBoolean()) {
            cultist = ModEntityTypes.BELDAM.get().create(pLevel);
        }

        if (villager.getVillagerData().getType() == VillagerType.SWAMP
                && pLevel.random.nextFloat() <= 0.25F) {
            cultist = EntityType.WITCH.create(pLevel);
        }

        if (cultist != null) {
            cultist.moveTo(villager.getX(), villager.getY(), villager.getZ(), villager.getYRot(), villager.getXRot());
            cultist.finalizeSpawn(pLevel, pLevel.getCurrentDifficultyAt(cultist.blockPosition()),
                    MobSpawnType.CONVERSION, null, null);
            cultist.setNoAi(villager.isNoAi());
            if (villager.hasCustomName()) {
                cultist.setCustomName(villager.getCustomName());
                cultist.setCustomNameVisible(villager.isCustomNameVisible());
            }
            if (profession == VillagerProfession.ARMORER) {
                cultist.setItemSlot(EquipmentSlot.HEAD, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_HELMET));
                cultist.setItemSlot(EquipmentSlot.CHEST, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_CHESTPLATE));
                cultist.setItemSlot(EquipmentSlot.LEGS, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_LEGGINGS));
                cultist.setItemSlot(EquipmentSlot.FEET, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.IRON_BOOTS));
            }

            villager.removeTag(SECRET_CULTIST_TAG);
            villager.addTag(REVEALED_CULTIST_TAG);
            villagerReleasePoi(villager);
            cultist.setPos(villager.getX(), villager.getY(), villager.getZ());
            pLevel.addFreshEntity(cultist);
            villager.discard();

            pLevel.broadcastEntityEvent(cultist, (byte) 100);
        }
    }

    public static void calmIronGolems(Level world, LivingEntity livingEntity) {
        for (IronGolem ironGolem : world.getEntitiesOfClass(
                IronGolem.class, livingEntity.getBoundingBox().inflate(16.0D))) {
            if (ironGolem.getTarget() == livingEntity) {
                ironGolem.setTarget(null);
                ironGolem.stopBeingAngry();
            }
        }
    }
}
