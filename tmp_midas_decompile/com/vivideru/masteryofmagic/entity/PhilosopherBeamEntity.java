/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.projectiles.CorruptedBeam
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraftforge.network.PlayMessages$SpawnEntity
 */
package com.vivideru.masteryofmagic.entity;

import com.Polarice3.Goety.common.entities.projectiles.CorruptedBeam;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherSphereEntity;
import com.vivideru.masteryofmagic.goldification.GoldificationManager;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlocks;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.PlayMessages;

public class PhilosopherBeamEntity
extends CorruptedBeam {
    public static final int GOLDIFY_AFTER_TICKS = 20;
    public static final int SHATTER_AFTER_TICKS = 40;
    private static final long GOLDIFICATION_DURATION_TICKS = 200L;
    private final Map<UUID, Integer> entityExposureTicks = new HashMap<UUID, Integer>();
    private BlockPos exposedBlock;
    private int blockExposureTicks;

    public PhilosopherBeamEntity(PlayMessages.SpawnEntity packet, Level level) {
        this((EntityType)GoetyMasteryOfMagicModEntities.PHILOSOPHER_BEAM.get(), level);
    }

    public PhilosopherBeamEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public PhilosopherBeamEntity(Level level, LivingEntity owner) {
        this((EntityType)GoetyMasteryOfMagicModEntities.PHILOSOPHER_BEAM.get(), level);
        this.setOwner(owner);
    }

    public Predicate<LivingEntity> canHitEntity(LivingEntity owner) {
        Predicate parent = super.canHitEntity(owner);
        return target -> {
            Player player;
            return parent.test(target) && !(target instanceof PhilosopherKingMidasEntity) && !(target instanceof PhilosopherSphereEntity) && (!(target instanceof Player) || !(player = (Player)target).m_7500_() && !player.m_5833_());
        };
    }

    public void damageEntities(Set<LivingEntity> targets) {
        super.damageEntities(targets);
        if (!(this.m_9236_() instanceof ServerLevel)) {
            return;
        }
        HashSet<UUID> currentlyHit = new HashSet<UUID>();
        for (LivingEntity target : targets) {
            if (!target.m_6084_() || target.m_213877_()) continue;
            UUID uuid2 = target.m_20148_();
            currentlyHit.add(uuid2);
            int exposure = this.entityExposureTicks.merge(uuid2, 1, Integer::sum);
            if (exposure == 20) {
                GoldificationManager.goldifyEntityForMidas((Entity)target, 200L, this.transmutationSource());
                continue;
            }
            if (exposure < 40 || !GoldificationManager.shatterEntity((Entity)target, this.transmutationSource())) continue;
            this.entityExposureTicks.remove(uuid2);
        }
        this.entityExposureTicks.keySet().removeIf(uuid -> !currentlyHit.contains(uuid));
    }

    public void m_8119_() {
        BlockHitResult blockHit;
        ServerLevel level;
        block10: {
            block9: {
                Level level2;
                super.m_8119_();
                if (this.m_213877_() || !((level2 = this.m_9236_()) instanceof ServerLevel)) {
                    return;
                }
                level = (ServerLevel)level2;
                HitResult result = this.beamTraceResult(64.0, 1.0f, false);
                if (!(result instanceof BlockHitResult)) break block9;
                blockHit = (BlockHitResult)result;
                if (result.m_6662_() == HitResult.Type.BLOCK && !this.isBarrier(level, blockHit.m_82425_())) break block10;
            }
            this.clearBlockExposure();
            return;
        }
        BlockPos position = blockHit.m_82425_().m_7949_();
        if (!position.equals((Object)this.exposedBlock)) {
            this.exposedBlock = position;
            this.blockExposureTicks = 1;
        } else {
            ++this.blockExposureTicks;
        }
        if (this.blockExposureTicks == 20) {
            GoldificationManager.goldifyBlockForMidas(level, position, 200L, this.transmutationSource());
        } else if (this.blockExposureTicks >= 40 && GoldificationManager.shatterBlock(level, position, this.transmutationSource())) {
            this.clearBlockExposure();
        }
    }

    private boolean isBarrier(ServerLevel level, BlockPos position) {
        return level.m_8055_(position).m_60713_(Blocks.f_50375_) || level.m_8055_(position).m_60713_((Block)GoetyMasteryOfMagicModBlocks.SOUL_BARRIER_BLOCK.get());
    }

    private Entity transmutationSource() {
        PhilosopherSphereEntity sphere;
        PhilosopherKingMidasEntity midas;
        LivingEntity livingEntity = this.getOwner();
        if (livingEntity instanceof PhilosopherSphereEntity && (midas = (sphere = (PhilosopherSphereEntity)livingEntity).getMidasOwner()) != null) {
            return midas;
        }
        return this.getOwner() == null ? this : this.getOwner();
    }

    private void clearBlockExposure() {
        this.exposedBlock = null;
        this.blockExposureTicks = 0;
    }
}

