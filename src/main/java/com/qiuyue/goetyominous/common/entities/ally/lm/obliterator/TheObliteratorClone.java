package com.qiuyue.goetyominous.common.entities.ally.lm.obliterator;

import net.miauczel.legendary_monsters.entity.client.ControlledAnim;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.AnnihilationBomb;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.LmAnimatedProjectile;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class TheObliteratorClone extends LmAnimatedProjectile {

    private int warmupDelayTicks;
    private boolean sentSpikeEvent;
    private int lifeTicks = 30;
    private boolean clientSideAttackStarted;
    private LivingEntity caster;
    private UUID casterUuid;

    private static final EntityDataAccessor<Float> DESTINATION_Z =
            SynchedEntityData.defineId(TheObliteratorClone.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DESTINATION_Y =
            SynchedEntityData.defineId(TheObliteratorClone.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DESTINATION_X =
            SynchedEntityData.defineId(TheObliteratorClone.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> ATTACK =
            SynchedEntityData.defineId(TheObliteratorClone.class, EntityDataSerializers.BOOLEAN);

    public float activateProgress;
    public float prevactivateProgress;
    public final ControlledAnim RendercontrolledAnim = new ControlledAnim(15);
    public final ControlledAnim RendercontrolledAnim2 = new ControlledAnim(15);
    public final ControlledAnim fade = new ControlledAnim(15);

    public TheObliteratorClone(EntityType<? extends TheObliteratorClone> type, Level level) {
        super(type, level);
    }

    public TheObliteratorClone(Level worldIn, double x, double y, double z, float yawRad, int warmupDelayTicks,
                               LivingEntity casterIn, float damage, float destX, float destY, float destZ) {
        this(LmEntityRegistry.THE_OBLITERATOR_CLONE.get(), worldIn);
        this.warmupDelayTicks = warmupDelayTicks;
        this.setCaster(casterIn);
        this.setYRot(yawRad * 57.295776F - 90.0F);
        this.setPos(x, y, z);
        this.setDestinationX(destX);
        this.setDestinationY(destY);
        this.setDestinationZ(destZ);
        this.setDamage(damage);
    }

    public AnimationState getAnimationState(String input) {
        if (input == "emerge") {
            return this.emergeAnimationState;
        }
        return new AnimationState();
    }

    public float getDestinationX() {
        return this.entityData.get(DESTINATION_X);
    }

    public float getDestinationY() {
        return this.entityData.get(DESTINATION_Y);
    }

    public float getDestinationZ() {
        return this.entityData.get(DESTINATION_Z);
    }

    public void setDestinationX(float destination) {
        this.entityData.set(DESTINATION_X, destination);
    }

    public void setDestinationY(float destination) {
        this.entityData.set(DESTINATION_Y, destination);
    }

    public void setDestinationZ(float destination) {
        this.entityData.set(DESTINATION_Z, destination);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ATTACK, false);
        this.entityData.define(DESTINATION_X, 0.0F);
        this.entityData.define(DESTINATION_Y, 0.0F);
        this.entityData.define(DESTINATION_Z, 0.0F);
        super.defineSynchedData();
    }

    public void setCaster(@Nullable LivingEntity casterIn) {
        this.caster = casterIn;
        this.casterUuid = casterIn == null ? null : casterIn.getUUID();
    }

    @Nullable
    public LivingEntity getCaster() {
        if (this.caster == null && this.casterUuid != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel) this.level()).getEntity(this.casterUuid);
            if (entity instanceof LivingEntity) {
                this.caster = (LivingEntity) entity;
            }
        }
        return this.caster;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.warmupDelayTicks = compound.getInt("Warmup");
        if (compound.hasUUID("Owner")) {
            this.casterUuid = compound.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Warmup", this.warmupDelayTicks);
        if (this.casterUuid != null) {
            compound.putUUID("Owner", this.casterUuid);
        }
    }

    @Override
    public int disappearTicks() {
        return 15;
    }

    public void shootDimensionalBomb(float velocity, float x, float y, float z) {
        float f = Mth.cos(this.getYRot() * ((float) Math.PI / 180));
        float f1 = Mth.sin(this.getYRot() * ((float) Math.PI / 180));
        double theta = (double) this.getYRot() * (Math.PI / 180);
        double vecX = Math.cos(theta += 1.5707963267948966);
        double vecZ = Math.sin(theta);
        float vec = 1.0F;
        float offset = 0.0F;
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.ANNIHILATION_EXPLOSION.get(),
                    this.getX() + (double) vec * vecX + (double) (f * offset), this.getY() + 3.0,
                    this.getZ() + (double) vec * vecZ + (double) (f1 * offset), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        if (this.getCaster() instanceof Mob && ((Mob) this.getCaster()).getTarget() != null) {
            AnnihilationBomb chorusBomb = new AnnihilationBomb(LmEntityRegistry.ANNIHILATION_BOMB.get(),
                    this.level(), this.caster, 12.0F, 0, true);
            chorusBomb.setPosRaw(x, y, z);
            double d0 = this.getDestinationX() - x;
            double d1 = (double) this.getDestinationY() - chorusBomb.getY();
            double d2 = this.getDestinationZ() - z;
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            chorusBomb.shoot(d0, d1 + d3 * 0.2, d2, velocity, 14 - this.level().getDifficulty().getId() * 4);
            chorusBomb.setOwner(this.getCaster());
            this.level().addFreshEntity(chorusBomb);
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.RendercontrolledAnim2.increaseTimer();
        Vec3 vec3 = new Vec3(this.getDestinationX(), this.getDestinationY(), this.getDestinationZ());
        this.lookAt(EntityAnchorArgument.Anchor.EYES, vec3);
        if (this.AnimationTicks > 23) {
            this.controlledAnim.increaseTimer();
        }
        if (this.AnimationTicks > 13) {
            this.RendercontrolledAnim.increaseTimer();
        }
        if (this.tickCount > 15) {
            this.fade.increaseTimer();
        }
        if (this.AnimationTicks == 13) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), ModSounds.THE_WARPED_ONE_SHOOT.get(),
                    this.getSoundSource(), 0.1F, 1.0F, false);
            float f = Mth.cos(this.getYRot() * ((float) Math.PI / 180));
            float f1 = Mth.sin(this.getYRot() * ((float) Math.PI / 180));
            double theta = (double) this.getYRot() * (Math.PI / 180);
            double vecX = Math.cos(theta += 1.5707963267948966);
            double vecZ = Math.sin(theta);
            float vec = 0.0F;
            float offset = 0.0F;
            this.shootDimensionalBomb(1.0F, (float) (this.getX() + (double) vec * vecX + (double) (f * offset)),
                    (float) (this.getY() + 1.5), (float) (this.getZ() + (double) vec * vecZ + (double) (f1 * offset)));
        }
        if (this.AnimationTicks == 6 && this.level().isClientSide) {
            for (int i = 0; i < 40; ++i) {
                BlockState block = this.level().getBlockState(this.blockPosition().below());
                double d0 = this.getX() + (this.random.nextDouble() * 2.0 - 1.0) * (double) this.getBbWidth() * 0.5;
                double d1 = this.getY() + 0.03;
                double d2 = this.getZ() + (this.random.nextDouble() * 2.0 - 1.0) * (double) this.getBbWidth() * 0.5;
                double d3 = this.random.nextGaussian() * 0.07;
                double d4 = this.random.nextGaussian() * 0.07;
                double d = this.random.nextGaussian() * 0.07;
            }
        }
        this.prevactivateProgress = this.activateProgress;
        if (this.level().isClientSide) {
            if (this.clientSideAttackStarted) {
                ++this.lifeTicks;
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (!this.sentSpikeEvent) {
                this.level().broadcastEntityEvent(this, (byte) 4);
                this.sentSpikeEvent = true;
            }
            if (++this.lifeTicks > 55) {
                this.discard();
            }
        }
    }

    public boolean isActivate() {
        return this.entityData.get(ATTACK);
    }

    public void setActivate(boolean activate) {
        this.entityData.set(ATTACK, activate);
    }

    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
        if (id == 4) {
            this.clientSideAttackStarted = true;
        }
        if (id <= 0) {
            this.lifeTicks = 0;
        }
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public LivingEntity target() {
        if (this.caster != null && this.caster instanceof Mob) {
            return ((Mob) this.caster).getTarget();
        }
        return null;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.getAnimationState() == 0) {
            this.setAnimationState(1);
        }
    }

    public float getAnimationProgress(float partialTicks) {
        if (!this.clientSideAttackStarted) {
            return 0.0F;
        }
        int ticks = this.lifeTicks - 2;
        return ticks <= 0 ? 1.0F : 1.0F - ((float) ticks - partialTicks) / 20.0F;
    }
}
