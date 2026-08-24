/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.magic.EverChargeSpell
 *  com.Polarice3.Goety.common.magic.SpellStat
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.vivideru.masteryofmagic.spells;

import com.Polarice3.Goety.common.magic.EverChargeSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.vivideru.masteryofmagic.block.entity.SoulBarrierBlockEntity;
import com.vivideru.masteryofmagic.config.SpellConfig;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlocks;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SoulBarrierSpell
extends EverChargeSpell {
    private static final Map<UUID, Long> LAST_PUSH_TICK = new HashMap<UUID, Long>();

    public int defaultSoulCost() {
        return (Integer)SpellConfig.SOUL_BARRIER_SOUL_COST_PER_SECOND.get();
    }

    public int defaultCastUp() {
        return 0;
    }

    public int defaultSpellCooldown() {
        return 0;
    }

    public SoundEvent CastingSound() {
        return SoundEvents.f_144243_;
    }

    public void playSound(ServerLevel serverLevel, LivingEntity caster, float volume, float pitch) {
        super.playSound(serverLevel, caster, volume * 1.0f, pitch);
    }

    public void useSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, int castTime, SpellStat spellStat) {
        if (castTime <= 1) {
            this.pushEntities(worldIn, caster);
        }
        if (castTime >= 5 && caster.f_19797_ % 5 == 0) {
            if (this.isShifting(caster)) {
                SoulBarrierSpell.createSphereBarrier(worldIn, caster);
            } else {
                SoulBarrierSpell.createWallBarrier(worldIn, caster);
            }
        }
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
    }

    private void pushEntities(ServerLevel world, LivingEntity caster) {
        long gameTime = world.m_46467_();
        UUID uuid = caster.m_20148_();
        if (LAST_PUSH_TICK.containsKey(uuid) && gameTime - LAST_PUSH_TICK.get(uuid) < 10L) {
            return;
        }
        LAST_PUSH_TICK.put(uuid, gameTime);
        boolean sphere = this.isShifting(caster);
        Vec3 look = caster.m_20252_(1.0f).m_82541_();
        AABB area = caster.m_20191_().m_82400_(10.0);
        for (Entity entity : world.m_45933_((Entity)caster, area)) {
            Vec3 direction;
            LivingEntity livingEntity;
            if (!(entity instanceof LivingEntity) || (livingEntity = (LivingEntity)entity) == caster) continue;
            if (sphere) {
                direction = livingEntity.m_20182_().m_82546_(caster.m_20182_());
                if (direction.m_82556_() < 0.001) {
                    direction = look;
                }
            } else {
                direction = look;
            }
            direction = direction.m_82541_();
            livingEntity.m_20334_(direction.f_82479_ * 1.45, 0.45, direction.f_82481_ * 1.45);
            livingEntity.f_19864_ = true;
        }
    }

    public static void createWallBarrier(ServerLevel world, LivingEntity caster) {
        Vec3 normal = caster.m_20252_(1.0f).m_82541_();
        Vec3 center = caster.m_146892_().m_82549_(normal.m_82490_(5.0));
        Vec3 up = new Vec3(0.0, 1.0, 0.0);
        if (Math.abs(normal.m_82526_(up)) > 0.95) {
            up = new Vec3(1.0, 0.0, 0.0);
        }
        Vec3 right = normal.m_82537_(up).m_82541_();
        Vec3 vertical = right.m_82537_(normal).m_82541_();
        double radius = 5.0;
        for (double x = -radius; x <= radius; x += 1.0) {
            for (double y = -radius; y <= radius; y += 1.0) {
                if (x * x + y * y > radius * radius) continue;
                for (double thickness = -0.5; thickness <= 0.5; thickness += 1.0) {
                    Vec3 placeVec = center.m_82549_(right.m_82490_(x)).m_82549_(vertical.m_82490_(y)).m_82549_(normal.m_82490_(thickness));
                    SoulBarrierSpell.placeBarrierBlock(world, BlockPos.m_274561_((double)(placeVec.f_82479_ - 0.5), (double)(placeVec.f_82480_ - 0.5), (double)(placeVec.f_82481_ - 0.5)));
                }
            }
        }
    }

    public static void createSphereBarrier(ServerLevel world, LivingEntity caster) {
        Vec3 center = caster.m_20182_().m_82520_(0.0, (double)caster.m_20206_() * 0.5, 0.0);
        double outerRadius = 5.0;
        double innerRadius = 3.5;
        BlockPos centerPos = BlockPos.m_274446_((Position)center);
        int range = 5;
        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= range; ++y) {
                for (int z = -range; z <= range; ++z) {
                    BlockPos pos = centerPos.m_7918_(x, y, z);
                    Vec3 blockCenter = Vec3.m_82528_((Vec3i)pos).m_82520_(0.5, 0.5, 0.5);
                    double distance = blockCenter.m_82554_(center);
                    if (!(distance <= outerRadius) || !(distance >= innerRadius)) continue;
                    SoulBarrierSpell.placeBarrierBlock(world, pos);
                }
            }
        }
    }

    public static void placeBarrierBlock(ServerLevel world, BlockPos pos) {
        boolean isLiquidBlock;
        BlockState oldState = world.m_8055_(pos);
        BlockEntity blockEntity = world.m_7702_(pos);
        if (blockEntity instanceof SoulBarrierBlockEntity) {
            SoulBarrierBlockEntity barrierBlockEntity = (SoulBarrierBlockEntity)blockEntity;
            barrierBlockEntity.setRemainingLifeTicks(20);
            return;
        }
        if (oldState.m_61138_((Property)BlockStateProperties.f_61362_) && ((Boolean)oldState.m_61143_((Property)BlockStateProperties.f_61362_)).booleanValue()) {
            return;
        }
        boolean isAir = oldState.m_60795_();
        boolean bl = isLiquidBlock = !oldState.m_60819_().m_76178_() && oldState.m_60812_((BlockGetter)world, pos).m_83281_();
        if (!isAir && !isLiquidBlock) {
            return;
        }
        world.m_7731_(pos, ((Block)GoetyMasteryOfMagicModBlocks.SOUL_BARRIER_BLOCK.get()).m_49966_(), 3);
        BlockEntity blockEntity2 = world.m_7702_(pos);
        if (blockEntity2 instanceof SoulBarrierBlockEntity) {
            SoulBarrierBlockEntity barrierBlockEntity = (SoulBarrierBlockEntity)blockEntity2;
            barrierBlockEntity.setRemainingLifeTicks(20);
        }
    }
}

