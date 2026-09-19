package com.qiuyue.goetyominous.common.blocks.entities;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ally.*;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Cerberus;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Warg;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.world.CerberusTotemData;
import com.qiuyue.goetyominous.common.world.WargTotemData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WolfTotemHooks {
    public static final int REVIVE_COST = 100;
    public static final int CERBERUS_FUSION_COUNT = 3;
    public static final String REVIVE_POS_TAG = "GoetyOminousTotemRevivePos";
    public static final String REVIVE_DIM_TAG = "GoetyOminousTotemReviveDim";
    public static final TagKey<EntityType<?>> WOLF_TOTEM_SERVANTS =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("goety", "wolf_totem_servants"));

    public static InteractionResult tryLinkToTotem(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        boolean ownedByPlayer;
        if (entity instanceof IOwned owned) {
            ownedByPlayer = owned.getTrueOwner() == player;
        } else if (entity instanceof Wolf wolf) {
            ownedByPlayer = wolf.isTame() && wolf.isOwnedBy(player);
        } else {
            return InteractionResult.PASS;
        }
        if (!ownedByPlayer) {
            return InteractionResult.PASS;
        }
        if (!canUseTotem(entity)) {
            return InteractionResult.PASS;
        }
        if (!stack.is(ModItems.WAYSTONE.get())) {
            return InteractionResult.PASS;
        }
        if (!WaystoneItem.hasBlock(stack)) {
            return InteractionResult.PASS;
        }
        if (!WaystoneItem.isSameDimension(entity, stack)) {
            return InteractionResult.PASS;
        }
        BlockEntity blockEntity = WaystoneItem.getBlockEntity(stack, entity.level());
        if (!(blockEntity instanceof WolfTotemBlockEntity totem)) {
            return InteractionResult.PASS;
        }
        if (totem.getTrueOwner() != player) {
            return InteractionResult.PASS;
        }
        if (!totem.hasSpace()) {
            totem.getServants().size();
            com.Polarice3.Goety.config.MainConfig.OminousIdolLimit.get();
            return InteractionResult.PASS;
        }
        boolean isWarg = entity instanceof Warg;
        boolean wargAlreadyBound = isWarg && entity instanceof IOwned boundWarg && getStoredRevivePos(boundWarg) != null;
        if (!entity.level().isClientSide) {
            if (isWarg && !wargAlreadyBound && (totem.hasCreatedWarg()
                    || !WargTotemData.get((ServerLevel) entity.level()).canCreate(player.getUUID(), entity.level().dimension(), totem.getBlockPos()))) {
                return InteractionResult.FAIL;
            }
            if (isWarg && wargAlreadyBound && totem.hasCreatedWarg() && !totem.getCreatedWarg().equals(entity.getUUID())) {
                return InteractionResult.FAIL;
            }
            if (isWargPromotionCandidate(entity) && tryTransformWarg(player, entity, totem, hand)) {
                return InteractionResult.SUCCESS;
            }
            if (!(entity instanceof IOwned owned)) {
                return InteractionResult.PASS;
            }
            WolfTotemBlockEntity oldTotem = getTotem(owned);
            if (oldTotem != null && oldTotem != totem) {
                oldTotem.removeServant(entity);
                if (isWarg) {
                    oldTotem.releaseWarg(entity.getUUID());
                }
                oldTotem.markUpdated();
            }
            BlockPos blockPos = totem.getBlockPos();
            totem.addServant(entity);
            owned.setRevivePos(blockPos);
            owned.setReviveDim(entity.level().dimension());
            entity.getPersistentData().putLong(REVIVE_POS_TAG, blockPos.asLong());
            entity.getPersistentData().putString(REVIVE_DIM_TAG, entity.level().dimension().location().toString());
            if (owned instanceof IServant servant) {
                servant.setWandering(false);
                servant.setStaying(false);
            }
            if (isWarg && entity.level() instanceof ServerLevel serverLevel) {
                WargTotemData wargData = WargTotemData.get(serverLevel);
                if (wargAlreadyBound) {
                    wargData.unregister(entity.getUUID());
                }
                totem.setCreatedWarg(entity.getUUID());
                wargData.register(entity.getUUID(), player.getUUID(), serverLevel.dimension(), totem.getBlockPos());
            }
            entity.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0F, 0.45F);
            player.displayClientMessage(Component.translatable("info.goety.servant.guard", entity.getDisplayName()), true);
            if (entity.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 7; ++i) {
                    double d0 = entity.getRandom().nextGaussian() * 0.02D;
                    double d1 = entity.getRandom().nextGaussian() * 0.02D;
                    double d2 = entity.getRandom().nextGaussian() * 0.02D;
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, entity.getRandomX(1.0D), entity.getRandomY() + 0.5D, entity.getRandomZ(1.0D), 1, d0, d1, d2, 0.5F);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean isWargPromotionCandidate(LivingEntity entity) {
        return entity instanceof SkeletonWolf
                || entity instanceof Wolf wolf && wolf.isTame()
                || entity instanceof BlackWolf blackWolf && !(blackWolf instanceof Hellhound) && !(blackWolf instanceof Warg);
    }

    private static boolean isPlayerOwnedWolf(LivingEntity living, Player player) {
        if (living instanceof Warg) {
            return false;
        }
        if (living instanceof Wolf wolf) {
            return wolf.isTame() && wolf.isOwnedBy(player);
        }
        if (living instanceof BlackWolf || living instanceof SkeletonWolf) {
            return living instanceof IOwned owned && owned.getTrueOwner() == player;
        }
        return false;
    }

    private static boolean tryTransformWarg(Player player, LivingEntity wolf, WolfTotemBlockEntity totem, InteractionHand hand) {
        if (!(wolf.level() instanceof ServerLevel serverLevel) || totem.hasCreatedWarg()) {
            return false;
        }
        UUID ownerId = player.getUUID();
        long nearbyWolves = serverLevel.getEntitiesOfClass(LivingEntity.class,
                        new AABB(totem.getBlockPos()).inflate(8.0D),
                        living -> isPlayerOwnedWolf(living, player))
                .size();
        if (nearbyWolves < 4 || !WargTotemData.get(serverLevel).canCreate(ownerId, serverLevel.dimension(), totem.getBlockPos())) {
            return false;
        }
        Warg warg = ModEntityTypes.WARG.get().create(serverLevel);
        if (warg == null) {
            return false;
        }
        Component sourceName = wolf.getDisplayName().copy();

        WolfTotemBlockEntity oldTotem = getTotem(wolf);
        if (oldTotem != null) {
            oldTotem.removeServant(wolf);
            oldTotem.markUpdated();
        }
        if (!wolf.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            wolf.spawnAtLocation(wolf.getItemBySlot(EquipmentSlot.CHEST).copy());
        }
        warg.moveTo(wolf.getX(), wolf.getY(), wolf.getZ(), wolf.getYRot(), wolf.getXRot());
        warg.setTrueOwner(player);
        warg.setVariant(wolf instanceof SkeletonWolf ? Warg.Variant.SKELETAL
                : wolf instanceof WinterWolf ? Warg.Variant.COLD
                : wolf instanceof Stormhound ? Warg.Variant.MODERATE
                : wolf instanceof Wolf ? Warg.Variant.GRAY
                : Warg.Variant.BLACK);
        if (wolf instanceof IServant servant) {
            warg.setUpgraded(servant.isUpgraded());
        }
        if (wolf.hasCustomName()) {
            warg.setCustomName(wolf.getCustomName());
            warg.setCustomNameVisible(wolf.isCustomNameVisible());
        }
        warg.setRevivePos(totem.getBlockPos());
        warg.setReviveDim(serverLevel.dimension());
        warg.getPersistentData().putLong(REVIVE_POS_TAG, totem.getBlockPos().asLong());
        warg.getPersistentData().putString(REVIVE_DIM_TAG, serverLevel.dimension().location().toString());
        warg.setWandering(false);
        warg.setStaying(false);
        serverLevel.addFreshEntity(warg);
        totem.addServant(warg);
        totem.setCreatedWarg(warg.getUUID());
        WargTotemData.get(serverLevel).register(warg.getUUID(), ownerId, serverLevel.dimension(), totem.getBlockPos());
        wolf.discard();
        for (int i = 0; i < 7; ++i) {
            double d0 = warg.getRandom().nextGaussian() * 0.02D;
            double d1 = warg.getRandom().nextGaussian() * 0.02D;
            double d2 = warg.getRandom().nextGaussian() * 0.02D;
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    warg.getRandomX(1.0D), warg.getRandomY() + 0.5D, warg.getRandomZ(1.0D),
                    1, d0, d1, d2, 0.5F);
        }
        warg.playSound(SoundEvents.WOLF_HOWL, 0.20F, 0.65F);
        player.swing(hand);
        player.displayClientMessage(Component.translatable("info.goety.warg.created", sourceName), true);
        return true;
    }

    public static boolean tryFuseCerberus(Player player, Hellhound hellhound, InteractionHand hand) {
        if (!(hellhound.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        if (hellhound.getTrueOwner() != player) {
            return false;
        }
        WolfTotemBlockEntity totem = getTotem((LivingEntity) hellhound);
        if (totem == null || totem.hasCreatedCerberus() || !totem.hasSpace()) {
            return false;
        }
        UUID ownerId = player.getUUID();
        List<Hellhound> bound = new ArrayList<>();
        for (LivingEntity servant : totem.getServants()) {
            if (servant instanceof Hellhound boundHellhound && servant.isAlive() && !servant.isRemoved()) {
                bound.add(boundHellhound);
            }
        }
        if (bound.size() < CERBERUS_FUSION_COUNT
                || !CerberusTotemData.get(serverLevel).canCreate(ownerId, serverLevel.dimension(), totem.getBlockPos())) {
            return false;
        }
        Cerberus cerberus = ModEntityTypes.CERBERUS.get().create(serverLevel);
        if (cerberus == null) {
            return false;
        }
        Component sourceName = hellhound.getDisplayName().copy();

        int consumed = 0;
        for (Hellhound extra : bound) {
            if (consumed >= CERBERUS_FUSION_COUNT - 1) {
                break;
            }
            if (extra == hellhound) {
                continue;
            }
            totem.removeServant(extra);
            totem.markUpdated();
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    extra.getX(), extra.getY() + (double) extra.getBbHeight() * 0.5D, extra.getZ(),
                    12, (double) extra.getBbWidth() * 0.5D, (double) extra.getBbHeight() * 0.5D,
                    (double) extra.getBbWidth() * 0.5D, 0.02D);
            extra.discard();
            ++consumed;
        }

        totem.removeServant(hellhound);
        totem.markUpdated();
        ItemStack hellhoundArmor = hellhound.getItemBySlot(EquipmentSlot.CHEST);
        if (!hellhoundArmor.isEmpty()) {
            hellhound.spawnAtLocation(hellhoundArmor.copy());
        }
        cerberus.moveTo(hellhound.getX(), hellhound.getY(), hellhound.getZ(), hellhound.getYRot(), hellhound.getXRot());
        cerberus.setTrueOwner(player);
        cerberus.setUpgraded(hellhound.isUpgraded());
        cerberus.setHostile(hellhound.isHostile());
        if (hellhound.hasCustomName()) {
            cerberus.setCustomName(hellhound.getCustomName());
            cerberus.setCustomNameVisible(hellhound.isCustomNameVisible());
        }
        cerberus.setRevivePos(totem.getBlockPos());
        cerberus.setReviveDim(serverLevel.dimension());
        cerberus.getPersistentData().putLong(REVIVE_POS_TAG, totem.getBlockPos().asLong());
        cerberus.getPersistentData().putString(REVIVE_DIM_TAG, serverLevel.dimension().location().toString());
        cerberus.setWandering(false);
        cerberus.setStaying(false);
        serverLevel.addFreshEntity(cerberus);
        totem.addServant(cerberus);
        totem.setCreatedCerberus(cerberus.getUUID());
        CerberusTotemData.get(serverLevel).register(cerberus.getUUID(), ownerId, serverLevel.dimension(), totem.getBlockPos());
        hellhound.discard();
        for (int i = 0; i < 8; ++i) {
            ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_FIRE.get(), cerberus);
            ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.SMOKE, cerberus);
        }
        cerberus.playSound(SoundEvents.WOLF_HOWL, 0.20F, 0.5F);
        player.swing(hand);
        player.displayClientMessage(Component.translatable("info.goety.cerberus.created", sourceName), true);
        return true;
    }

    public static boolean canRevive(LivingEntity entity, DamageSource damageSource) {
        if (damageSource.is(ModDamageSource.DISMISSED)) {
            return false;
        }
        if (!canUseTotem(entity)) {
            return false;
        }
        if (entity.hasEffect(GoetyEffects.WOUNDED.get())) {
            return false;
        }
        WolfTotemBlockEntity totem = getTotem(entity);
        if (totem == null) {
            return false;
        }
        int souls = totem.getSoulEnergy();
        return souls >= REVIVE_COST;
    }

    public static void onRevive(LivingEntity entity) {
        WolfTotemBlockEntity totem = getTotem(entity);
        if (totem == null) {
            return;
        }
        if (entity instanceof Mob mob) {
            mob.setTarget(null);
        }
        entity.level().broadcastEntityEvent(entity, (byte) 35);
        entity.addEffect(new MobEffectInstance(GoetyEffects.WOUNDED.get(), MathHelper.minecraftDayToTicks(1)));
        entity.addEffect(new MobEffectInstance(GoetyEffects.CRIPPLED.get(), MathHelper.minutesToTicks(5)));
        totem.siphonSoulEnergy(REVIVE_COST);
    }

    public static boolean isAssignedToWolfTotem(LivingEntity entity) {
        return entity instanceof IOwned owned && getTotem(owned) != null;
    }

    public static boolean canUseTotem(LivingEntity entity) {
        return entity.getType().is(WOLF_TOTEM_SERVANTS)
                || entity instanceof BlackWolf
                || entity instanceof Warg
                || entity instanceof SkeletonWolf
                || entity instanceof BlackBeast
                || entity instanceof Wolf;
    }

    public static WolfTotemBlockEntity getTotem(LivingEntity entity) {
        if (entity instanceof IOwned owned) {
            return getTotem(owned);
        }
        return null;
    }

    public static BlockPos getStoredRevivePos(IOwned owned) {
        if (owned instanceof Entity entity && entity.getPersistentData().contains(REVIVE_POS_TAG)) {
            return BlockPos.of(entity.getPersistentData().getLong(REVIVE_POS_TAG));
        }
        return null;
    }

    public static ResourceKey<Level> getStoredReviveLevel(IOwned owned) {
        if (owned instanceof Entity entity && entity.getPersistentData().contains(REVIVE_DIM_TAG)) {
            ResourceLocation dimLoc = ResourceLocation.tryParse(entity.getPersistentData().getString(REVIVE_DIM_TAG));
            if (dimLoc != null) {
                return ResourceKey.create(Registries.DIMENSION, dimLoc);
            }
        }
        return owned.getReviveLevel();
    }

    public static WolfTotemBlockEntity getTotem(IOwned owned) {
        if (!(owned instanceof Entity entity) || entity.getServer() == null) {
            return null;
        }
        BlockPos revivePos = getStoredRevivePos(owned);
        if (revivePos == null) {
            return null;
        }
        ResourceKey<Level> reviveLevel = getStoredReviveLevel(owned);
        for (Level level : entity.getServer().getAllLevels()) {
            if (reviveLevel != null && level.dimension() == reviveLevel) {
                BlockEntity blockEntity = level.getBlockEntity(revivePos);
                if (blockEntity instanceof WolfTotemBlockEntity totem) {
                    boolean ownerOk = totem.getTrueOwner() == owned.getTrueOwner();
                    return totem;
                }
            }
        }
        return null;
    }
}
