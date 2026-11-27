package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mobs.AllyPiglinShaman;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Guzzler;
import com.qiuyue.goetyominous.common.entities.ally.mobs.ZFungusThrower;
import com.qiuyue.goetyominous.common.entities.ally.mobs.ZGuzzler;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominous.common.entities.hostile.GuzzlerBoss;
import com.qiuyue.goetyominous.common.entities.hostile.ZGuzzlerBoss;
import com.qiuyue.goetyominous.common.entities.hostile.piglin.HostileFungusThrower;
import com.qiuyue.goetyominous.common.entities.hostile.piglin.PiglinShaman;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PiglinShamanConversionHandler {
    private static final List<LivingEntity> PENDING_REMOVAL = new ArrayList<>();

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.isCanceled()) {
            return;
        }
        LivingEntity dead = event.getEntity();
        if (!(dead.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!isConvertible(dead) || isStatueBound(dead)) {
            return;
        }

        List<AllyPiglinShaman> allies = serverLevel.getEntitiesOfClass(AllyPiglinShaman.class,
                dead.getBoundingBox().inflate(32.0D),
                shaman -> shaman.isAlive() && shaman.hasLineOfSight(dead));
        allies.sort(Comparator.comparingDouble(shaman -> shaman.distanceToSqr(dead)));
        for (AllyPiglinShaman ally : allies) {
            if (convertDead(dead, ally, OwnerData.of(ally), ally.getTarget(), serverLevel)) {
                finishConversion(dead);
                return;
            }
        }

        List<PiglinShaman> hostiles = serverLevel.getEntitiesOfClass(PiglinShaman.class,
                dead.getBoundingBox().inflate(32.0D),
                shaman -> shaman.isAlive() && shaman.hasLineOfSight(dead));
        hostiles.sort(Comparator.comparingDouble(shaman -> shaman.distanceToSqr(dead)));
        if (!hostiles.isEmpty()) {
            PiglinShaman hostile = hostiles.get(0);
            if (convertDead(dead, hostile, OwnerData.of(hostile), hostile.getTarget(), serverLevel)) {
                finishConversion(dead);
            }
        }
    }

    private static boolean isStatueBound(LivingEntity dead) {
        return dead instanceof AbstractPiglinServant servant && servant.getShrine() != null;
    }

    private static void finishConversion(LivingEntity dead) {
        clearEquipment(dead);
        PENDING_REMOVAL.add(dead);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || PENDING_REMOVAL.isEmpty()) {
            return;
        }
        for (LivingEntity dead : PENDING_REMOVAL) {
            if (!dead.isRemoved()) {
                dead.discard();
            }
        }
        PENDING_REMOVAL.clear();
    }

    private static boolean isConvertible(LivingEntity dead) {
        if (dead instanceof AbstractPiglinServant) {
            return true;
        }
        if (dead instanceof HostileFungusThrower) {
            return true;
        }
        if (dead instanceof GuzzlerBoss) {
            return !(dead instanceof ZGuzzlerBoss);
        }
        return dead instanceof Piglin || dead instanceof PiglinBrute;
    }

    private static boolean convertDead(LivingEntity dead, LivingEntity shaman, OwnerData owner,
                                       @Nullable LivingEntity target, ServerLevel serverLevel) {
        if (dead.isRemoved()) {
            return false;
        }
        Mob converted = createConverted(dead, serverLevel);
        if (converted == null) {
            return false;
        }
        converted.moveTo(dead.getX(), dead.getY(), dead.getZ(), dead.getYRot(), dead.getXRot());
        copyRotation(dead, converted);
        copyName(dead, converted);
        setOwner(converted, owner);
        converted.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(converted.blockPosition()),
                MobSpawnType.CONVERSION, null, null);
        converted.setBaby(dead.isBaby());
        clearEquipment(converted);
        copyEquipment(dead, converted);
        if (dead instanceof Guzzler guzzler && converted instanceof ZGuzzler zombie) {
            zombie.setUpgrade(guzzler.getUpgradeLevel(), guzzler.getUpgradePoints());
        } else if (dead instanceof GuzzlerBoss && converted instanceof ZGuzzler zombie) {
            zombie.setUpgrade(ZGuzzler.MAX_UPGRADE_LEVEL, 0);
        }
        applyOwner(converted, owner);
        if (target != null) {
            converted.setTarget(target);
        }
        converted.setHealth(converted.getMaxHealth());
        if (!serverLevel.addFreshEntity(converted)) {
            return false;
        }
        PiglinConversionManager.spawnConvertSoul(converted, shaman);
        return true;
    }

    @Nullable
    private static Mob createConverted(LivingEntity dead, ServerLevel serverLevel) {
        if (dead instanceof AbstractPiglinServant servant) {
            EntityType<? extends Mob> variant = servant.getZombieVariant();
            return variant == null ? null : variant.create(serverLevel);
        }
        if (dead instanceof HostileFungusThrower) {
            return new ZFungusThrower(ModEntityTypes.ZFUNGUS_THROWER_SERVANT.get(), serverLevel);
        }
        if (dead instanceof GuzzlerBoss && !(dead instanceof ZGuzzlerBoss)) {
            return new ZGuzzler(ModEntityTypes.ZGUZZLER.get(), serverLevel);
        }
        if (dead instanceof PiglinBrute) {
            return ModEntityType.ZPIGLIN_BRUTE_SERVANT.get().create(serverLevel);
        }
        if (dead instanceof Piglin) {
            return ModEntityType.ZPIGLIN_SERVANT.get().create(serverLevel);
        }
        return null;
    }

    private static void setOwner(LivingEntity converted, OwnerData owner) {
        if (!(converted instanceof IOwned owned)) {
            return;
        }
        if (owner.entity() != null) {
            owned.setTrueOwner(owner.entity());
        } else if (owner.uuid() != null) {
            owned.setOwnerId(owner.uuid());
            owned.setOwnerClientId(-1);
        }
    }

    private static void applyOwner(LivingEntity outcome, OwnerData owner) {
        if (!(outcome instanceof IOwned owned)) {
            return;
        }
        if (owner.entity() != null) {
            owned.setTrueOwner(owner.entity());
            owned.checkHostility();
        } else if (owner.uuid() != null) {
            owned.setOwnerId(owner.uuid());
            owned.setOwnerClientId(-1);
        }
        owned.setNatural(false);
        if (outcome instanceof Owned mob && !mob.isHostile() && owner.entity() instanceof net.minecraft.world.entity.monster.Enemy) {
            mob.setHostile(true);
        }
        if (outcome instanceof Mob mob) {
            mob.setPersistenceRequired();
        }
    }

    private static void copyRotation(LivingEntity from, LivingEntity to) {
        to.setYHeadRot(from.getYHeadRot());
        to.setYBodyRot(from.yBodyRot);
        to.yHeadRotO = from.yHeadRotO;
        to.yBodyRotO = from.yBodyRotO;
        to.yRotO = from.yRotO;
        to.xRotO = from.xRotO;
    }

    private static void copyName(LivingEntity from, LivingEntity to) {
        if (from.hasCustomName()) {
            to.setCustomName(from.getCustomName());
            to.setCustomNameVisible(from.isCustomNameVisible());
        }
    }

    private static void copyEquipment(LivingEntity from, LivingEntity to) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = from.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                to.setItemSlot(slot, stack.copy());
            }
        }
    }

    private static void clearEquipment(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            entity.setItemSlot(slot, ItemStack.EMPTY);
        }
    }

    private record OwnerData(@Nullable LivingEntity entity, @Nullable UUID uuid) {
        private static OwnerData of(LivingEntity entity) {
            return new OwnerData(entity, null);
        }

        private static OwnerData of(UUID uuid) {
            return new OwnerData(null, uuid);
        }
    }
}
