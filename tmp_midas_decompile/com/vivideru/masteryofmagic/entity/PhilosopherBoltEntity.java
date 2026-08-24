/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.entities.ISpellEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.boss.EnderDragonPart
 *  net.minecraft.world.entity.projectile.ThrowableProjectile
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.Level$ExplosionInteraction
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.network.NetworkHooks
 *  net.minecraftforge.network.PlayMessages$SpawnEntity
 */
package com.vivideru.masteryofmagic.entity;

import com.Polarice3.Goety.api.entities.ISpellEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.goldification.GoldificationManager;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlocks;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class PhilosopherBoltEntity
extends ThrowableProjectile
implements ISpellEntity {
    private static final int RADIUS = 5;
    private static final int GOLD_TICKS = 200;
    private boolean impacted;

    public PhilosopherBoltEntity(PlayMessages.SpawnEntity packet, Level level) {
        this((EntityType<? extends PhilosopherBoltEntity>)((EntityType)GoetyMasteryOfMagicModEntities.PHILOSOPHER_BOLT.get()), level);
    }

    public PhilosopherBoltEntity(EntityType<? extends PhilosopherBoltEntity> type, Level level) {
        super(type, level);
        this.m_20242_(true);
    }

    protected void m_8097_() {
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.m_9236_().f_46443_) {
            for (int i = 0; i < 2; ++i) {
                this.m_9236_().m_7106_((ParticleOptions)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY_SMALL.get(), this.m_20185_() + (this.f_19796_.m_188500_() - 0.5) * 0.25, this.m_20186_() + (this.f_19796_.m_188500_() - 0.5) * 0.25, this.m_20189_() + (this.f_19796_.m_188500_() - 0.5) * 0.25, 0.0, 0.0, 0.0);
            }
        }
        if (this.f_19797_ > 120) {
            this.m_146870_();
        }
    }

    protected void m_5790_(EntityHitResult hit) {
        Level level;
        if (this.impacted || !((level = this.m_9236_()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel l = (ServerLevel)level;
        Entity e = hit.m_82443_();
        if (e instanceof EnderDragonPart) {
            EnderDragonPart part = (EnderDragonPart)e;
            e = part.f_31010_;
        }
        if (e == this.m_19749_() || e instanceof PhilosopherKingMidasEntity) {
            return;
        }
        this.impacted = true;
        GoldificationManager.goldifyEntityForMidas(e, 200L, this.m_19749_());
        this.detonate(l, hit.m_82450_());
    }

    protected void m_8060_(BlockHitResult hit) {
        Level level;
        if (this.impacted || !((level = this.m_9236_()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel l = (ServerLevel)level;
        BlockState s = l.m_8055_(hit.m_82425_());
        if (PhilosopherBoltEntity.barrier(s)) {
            this.m_146870_();
            return;
        }
        this.impacted = true;
        this.goldifySurface(l, hit);
        Direction n = hit.m_82434_();
        this.detonate(l, hit.m_82450_().m_82520_((double)n.m_122429_() * 0.2, (double)n.m_122430_() * 0.2, (double)n.m_122431_() * 0.2));
    }

    private static boolean barrier(BlockState s) {
        return s.m_60713_(Blocks.f_50375_) || s.m_60713_((Block)GoetyMasteryOfMagicModBlocks.SOUL_BARRIER_BLOCK.get());
    }

    private void goldifySurface(ServerLevel l, BlockHitResult hit) {
        Direction n = hit.m_82434_();
        Direction a = n.m_122434_() == Direction.Axis.X ? Direction.UP : Direction.EAST;
        Direction b = n.m_122434_() == Direction.Axis.Z ? Direction.UP : Direction.SOUTH;
        BlockPos o = hit.m_82425_().m_5484_(n, 2);
        for (int u = -5; u <= 5; ++u) {
            block1: for (int v = -5; v <= 5; ++v) {
                if (u * u + v * v > 25) continue;
                BlockPos ray = o.m_5484_(a, u).m_5484_(b, v);
                for (int d = 0; d <= 7; ++d) {
                    BlockPos p = ray.m_5484_(n.m_122424_(), d);
                    BlockState s = l.m_8055_(p);
                    if (s.m_60795_()) continue;
                    if (PhilosopherBoltEntity.barrier(s)) continue block1;
                    GoldificationManager.goldifyBlockForMidas(l, p, 200L, this.m_19749_());
                    continue block1;
                }
            }
        }
    }

    private void detonate(ServerLevel l, Vec3 p) {
        l.m_8767_((ParticleOptions)((SimpleParticleType)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY.get()), p.f_82479_, p.f_82480_, p.f_82481_, 36, 2.5, 2.5, 2.5, 0.25);
        l.m_6263_(null, p.f_82479_, p.f_82480_, p.f_82481_, SoundEvents.f_11913_, SoundSource.HOSTILE, 2.0f, 1.15f);
        l.m_255391_(this.m_19749_(), p.f_82479_, p.f_82480_, p.f_82481_, 4.0f, false, Level.ExplosionInteraction.BLOCK);
        this.m_146870_();
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }
}

