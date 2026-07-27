package com.qiuyue.goetyominus.common.entities.ally.sar;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.projectiles.SpellThrowableProjectile;
import com.teamabnormals.savage_and_ravage.core.registry.SRParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.*;
import net.minecraftforge.network.NetworkHooks;

public class SporeCloud extends SpellThrowableProjectile {
    private AreaEffectCloud cloudEntity;
    private int cloudSize;
    private boolean charged = false;
    private boolean spawnCloudInstantly;
    private boolean hit;
    private int potencyLevel = 0;
    private int durationLevel = 0;

    public SporeCloud(EntityType<? extends SporeCloud> type, Level world) {
        super(type, world);
    }

    public SporeCloud(Level world, LivingEntity thrower) {
        super(com.qiuyue.goetyominus.common.init.sar.SarEntityRegistry.SPORE_CLOUD.get(), thrower, world);
    }

    public SporeCloud(Level world, double x, double y, double z) {
        super(com.qiuyue.goetyominus.common.init.sar.SarEntityRegistry.SPORE_CLOUD.get(), x, y, z, world);
    }

    private void spawnAreaEffectCloud(double x, double y, double z) {
        this.setPos(x, y, z);
        AreaEffectCloud aoe = new AreaEffectCloud(this.level(), x, y, z);
        aoe.setParticle(SRParticleTypes.CREEPER_SPORES.get());
        aoe.setRadius(this.cloudSize + 1.3F);
        aoe.setRadiusOnUse(-0.05F);
        aoe.setDuration((this.cloudSize * 20) + 60);
        aoe.setRadiusPerTick(-aoe.getRadius() / (float) aoe.getDuration());
        this.level().addFreshEntity(aoe);
        this.cloudEntity = aoe;
        this.level().broadcastEntityEvent(this, (byte) 3);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (this.charged) {
            nbt.putBoolean("Charged", true);
        }
        nbt.putInt("CloudSize", this.cloudSize);
        nbt.putBoolean("SpawnCloudInstantly", this.spawnCloudInstantly);
        nbt.putInt("PotencyLevel", this.potencyLevel);
        nbt.putInt("DurationLevel", this.durationLevel);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.charged = nbt.getBoolean("Charged");
        this.cloudSize = nbt.getInt("CloudSize");
        this.spawnCloudInstantly = nbt.getBoolean("SpawnCloudInstantly");
        this.potencyLevel = nbt.getInt("PotencyLevel");
        this.durationLevel = nbt.getInt("DurationLevel");
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    protected void onHit(HitResult result) {
        Vec3 hitVec = result.getLocation();
        if (!this.level().isClientSide()) {
            this.spawnAreaEffectCloud(hitVec.x(), hitVec.y(), hitVec.z());
        } else {
            for (int i = 0; i < 16; i++) {
                this.level().addParticle(SRParticleTypes.CREEPER_SPORE_SPRINKLES.get(), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
        this.hit = true;
        if (result instanceof BlockHitResult) {
            this.onHitBlock((BlockHitResult) result);
        }
        if (result instanceof EntityHitResult) {
            this.onHitEntity((EntityHitResult) result);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 3) {
            this.hit = true;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && this.spawnCloudInstantly) {
            this.spawnAreaEffectCloud(this.getX(), this.getY(), this.getZ());
        }

        if (this.hit) {
            this.setDeltaMovement(0, 0, 0);
        }

        if (this.level().isClientSide()) {
            if (!this.hit) {
                this.level().addParticle(SRParticleTypes.CREEPER_SPORES.get(), this.getX(), this.getY(), this.getZ(), 0, 0, 0);
            }
        } else if (this.cloudEntity != null) {
            if (!this.cloudEntity.isAlive()) {
                this.discard();
                return;
            }

            CreepieServant creepie = new CreepieServant(
                    com.qiuyue.goetyominus.common.init.sar.SarEntityRegistry.CREEPIE_SERVANT.get(),
                    this.level()
            );

            LivingEntity owner = this.getOwner();
            if (owner != null) {
                creepie.setTrueOwner(owner);
            }

            if (this.charged) {
                creepie.setCharged(true);
            }

            if (this.potencyLevel > 0) {
                creepie.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(), -1, this.potencyLevel - 1, false, false));
            }

            int baseLifespan = 1200;
            int finalLifespan = baseLifespan * (1 + this.durationLevel);
            creepie.setLifespan(finalLifespan);
            creepie.setHasLifespan(true);

            BlockPos nextPosition = null;
            if (this.cloudEntity.tickCount % 20 == 0) {
                for (int i = 0; i < 10; i++) {
                    double xPos = this.cloudEntity.getRandomX(0.1D);
                    double zPos = this.cloudEntity.getRandomZ(0.2D);
                    creepie.moveTo(xPos, this.cloudEntity.getY(), zPos, 0.0F, 0.0F);
                    AABB box = creepie.getBoundingBox();
                    if (BlockPos.betweenClosedStream(
                            Mth.floor(box.minX), Mth.floor(box.minY), Mth.floor(box.minZ),
                            Mth.ceil(box.maxX), Mth.ceil(box.maxY), Mth.ceil(box.maxZ)
                    ).distinct().noneMatch(pos -> {
                        if (this.level().getBlockState(pos).isSuffocating(this.level(), pos)) {
                            for (AABB blockBox : this.level().getBlockState(pos).getShape(this.level(), pos).toAabbs()) {
                                blockBox = new AABB(
                                        blockBox.minX + pos.getX(), blockBox.minY + pos.getY(), blockBox.minZ + pos.getZ(),
                                        blockBox.maxX + pos.getX(), blockBox.maxY + pos.getY(), blockBox.maxZ + pos.getZ()
                                );
                                if (blockBox.intersects(creepie.getBoundingBox())) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    })) {
                        nextPosition = BlockPos.containing(xPos, this.cloudEntity.getY(), zPos);
                        break;
                    }
                }
                if (nextPosition != null) {
                    this.level().addFreshEntity(creepie);
                }
            }
        }
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void setCloudSize(int cloudSize) {
        this.cloudSize = cloudSize;
    }

    public void setSpawnCloudInstantly(boolean spawnCloudInstantly) {
        this.spawnCloudInstantly = spawnCloudInstantly;
    }

    public void setCharged(boolean charged) {
        this.charged = charged;
    }

    public void setPotencyLevel(int potencyLevel) {
        this.potencyLevel = potencyLevel;
    }

    public void setDurationLevel(int durationLevel) {
        this.durationLevel = durationLevel;
    }
}