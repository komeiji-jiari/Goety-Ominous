package com.qiuyue.goetyominous.common.entities.util;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class PureDarkVoid extends Entity {

    public static final int LIFETIME = 300;

    private static final EntityDataAccessor<Integer> SPAWN_TIME =
            SynchedEntityData.defineId(PureDarkVoid.class, EntityDataSerializers.INT);

    private int nextIdleTime = 60;

    // 仪式祭坛(仅服务端用于中断自清理)。IRitualType 无 interrupt 钩子,
    // 若玩家中途打断,靠轮询祭坛是否仍在施法来及时销毁特效实体,避免留下"幽灵云"。
    private BlockPos anchorPos;
    private int anchorCheckTick;
    private int anchorMisses;

    public PureDarkVoid(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SPAWN_TIME, -1);
    }

    public void setSpawnTime(long gameTime) {
        this.entityData.set(SPAWN_TIME, (int) gameTime);
    }

    public int getSpawnTime() {
        return this.entityData.get(SPAWN_TIME);
    }

    /** 绑定主持仪式的祭坛,中断时据此销毁实体。仅服务端使用,无需同步/存档。 */
    public void setAltarAnchor(BlockPos altarPos) {
        this.anchorPos = altarPos;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        int spawnTime = this.getSpawnTime();
        if (spawnTime < 0) {
            this.discard();
            return;
        }
        int age = (int) (this.level().getGameTime() - spawnTime);
        if (age >= LIFETIME) {
            this.discard();
            return;
        }
        this.checkAnchorAlive();
        if (age == LIFETIME - 10) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ACSoundRegistry.DARK_CLOUD_DISAPPEAR.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
        }
        if (age >= this.nextIdleTime && age < LIFETIME - 60) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ACSoundRegistry.DARK_CLOUD_IDLE.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
            this.nextIdleTime = age + 80 + this.random.nextInt(60);
        }
    }

    /** 每 5 tick 查一次祭坛是否仍在施法;连续 ~1s 未施法则自毁(被打断)。 */
    private void checkAnchorAlive() {
        if (this.anchorPos == null) {
            return;
        }
        if (--this.anchorCheckTick > 0) {
            return;
        }
        this.anchorCheckTick = 5;
        boolean running = false;
        if (this.level().getBlockEntity(this.anchorPos) instanceof DarkAltarBlockEntity altar) {
            running = altar.getCurrentRitualRecipe() != null && altar.castingPlayer != null;
        }
        if (running) {
            this.anchorMisses = 0;
        } else if (++this.anchorMisses >= 4) {
            this.discard();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 16384.0D;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(5.0D);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }
}
