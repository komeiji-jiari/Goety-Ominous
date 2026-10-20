package com.qiuyue.goetyominous.common.entities.ally.mm;

import com.Polarice3.Goety.utils.MobUtil;
import com.alexander.mutantmore.util.MiscUtils;
import com.google.common.collect.Lists;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.utils.BossUtil;
import java.util.ArrayList;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class MutantShulkerServantTrapBullet extends Projectile {
    @Nullable
    private Entity finalTarget;
    @Nullable
    private Direction currentMoveDirection;
    private int flightSteps;
    private double targetDeltaX;
    private double targetDeltaY;
    private double targetDeltaZ;
    @Nullable
    private UUID targetId;
    public float damage = 0.0F;
    public int levitationLength = 0;
    public int levitationLevel = 0;

    public MutantShulkerServantTrapBullet(EntityType<? extends MutantShulkerServantTrapBullet> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public MutantShulkerServantTrapBullet(Level level, LivingEntity owner, Entity target, Direction.Axis axis) {
        this(MmEntityRegistry.MUTANT_SHULKER_SERVANT_TRAP_BULLET.get(), level);
        this.setOwner(owner);
        BlockPos pos = owner.blockPosition();
        this.moveTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, this.getYRot(), this.getXRot());
        this.finalTarget = target;
        this.currentMoveDirection = Direction.UP;
        this.selectNextMoveDirection(axis);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.finalTarget != null) {
            tag.putUUID("Target", this.finalTarget.getUUID());
        }
        if (this.currentMoveDirection != null) {
            tag.putInt("Dir", this.currentMoveDirection.get3DDataValue());
        }
        tag.putInt("Steps", this.flightSteps);
        tag.putDouble("TXD", this.targetDeltaX);
        tag.putDouble("TYD", this.targetDeltaY);
        tag.putDouble("TZD", this.targetDeltaZ);
        tag.putFloat("Damage", this.damage);
        tag.putInt("LevitationLength", this.levitationLength);
        tag.putInt("LevitationLevel", this.levitationLevel);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.flightSteps = tag.getInt("Steps");
        this.targetDeltaX = tag.getDouble("TXD");
        this.targetDeltaY = tag.getDouble("TYD");
        this.targetDeltaZ = tag.getDouble("TZD");
        if (tag.contains("Dir", 99)) {
            this.currentMoveDirection = Direction.from3DDataValue(tag.getInt("Dir"));
        }
        if (tag.hasUUID("Target")) {
            this.targetId = tag.getUUID("Target");
        }
        this.damage = tag.getFloat("Damage");
        this.levitationLength = tag.getInt("LevitationLength");
        this.levitationLevel = tag.getInt("LevitationLevel");
    }

    @Override
    protected void defineSynchedData() {
    }

    @Nullable
    private Direction getMoveDirection() {
        return this.currentMoveDirection;
    }

    private void setMoveDirection(@Nullable Direction direction) {
        this.currentMoveDirection = direction;
    }

    private void selectNextMoveDirection(@Nullable Direction.Axis axis) {
        BlockPos blockpos;
        double d0 = 0.5D;
        if (this.finalTarget == null) {
            blockpos = this.blockPosition().below();
        } else {
            d0 = (double) this.finalTarget.getBbHeight() * 0.5D;
            blockpos = BlockPos.containing(this.finalTarget.getX(), this.finalTarget.getY() + d0, this.finalTarget.getZ());
        }
        double d1 = (double) blockpos.getX() + 0.5D;
        double d2 = (double) blockpos.getY() + d0;
        double d3 = (double) blockpos.getZ() + 0.5D;
        Direction direction = null;
        if (!blockpos.closerToCenterThan(this.position(), 2.0D)) {
            BlockPos blockpos1 = this.blockPosition();
            ArrayList<Direction> list = Lists.newArrayList();
            if (axis != Direction.Axis.X) {
                if (blockpos1.getX() < blockpos.getX() && this.level().isEmptyBlock(blockpos1.east())) {
                    list.add(Direction.EAST);
                } else if (blockpos1.getX() > blockpos.getX() && this.level().isEmptyBlock(blockpos1.west())) {
                    list.add(Direction.WEST);
                }
            }
            if (axis != Direction.Axis.Y) {
                if (blockpos1.getY() < blockpos.getY() && this.level().isEmptyBlock(blockpos1.above())) {
                    list.add(Direction.UP);
                } else if (blockpos1.getY() > blockpos.getY() && this.level().isEmptyBlock(blockpos1.below())) {
                    list.add(Direction.DOWN);
                }
            }
            if (axis != Direction.Axis.Z) {
                if (blockpos1.getZ() < blockpos.getZ() && this.level().isEmptyBlock(blockpos1.south())) {
                    list.add(Direction.SOUTH);
                } else if (blockpos1.getZ() > blockpos.getZ() && this.level().isEmptyBlock(blockpos1.north())) {
                    list.add(Direction.NORTH);
                }
            }
            direction = Direction.getRandom(this.random);
            if (list.isEmpty()) {
                for (int i = 5; !this.level().isEmptyBlock(blockpos1.relative(direction)) && i > 0; --i) {
                    direction = Direction.getRandom(this.random);
                }
            } else {
                direction = list.get(this.random.nextInt(list.size()));
            }
            d1 = this.getX() + (double) direction.getStepX();
            d2 = this.getY() + (double) direction.getStepY();
            d3 = this.getZ() + (double) direction.getStepZ();
        }
        this.setMoveDirection(direction);
        double d6 = d1 - this.getX();
        double d7 = d2 - this.getY();
        double d4 = d3 - this.getZ();
        double d5 = Math.sqrt(d6 * d6 + d7 * d7 + d4 * d4);
        if (d5 == 0.0D) {
            this.targetDeltaX = 0.0D;
            this.targetDeltaY = 0.0D;
            this.targetDeltaZ = 0.0D;
        } else {
            this.targetDeltaX = d6 / d5 * 0.15D;
            this.targetDeltaY = d7 / d5 * 0.15D;
            this.targetDeltaZ = d4 / d5 * 0.15D;
        }
        this.hasImpulse = true;
        this.flightSteps = 10 + this.random.nextInt(5) * 10;
    }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.finalTarget == null && this.targetId != null) {
                this.finalTarget = ((ServerLevel) this.level()).getEntity(this.targetId);
                if (this.finalTarget == null) {
                    this.targetId = null;
                }
            }
            if (this.finalTarget == null || !this.finalTarget.isAlive() || this.finalTarget instanceof Player && this.finalTarget.isSpectator()) {
                if (!this.isNoGravity()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
                }
            } else {
                this.targetDeltaX = Mth.clamp(this.targetDeltaX * 1.025D, -1.0D, 1.0D);
                this.targetDeltaY = Mth.clamp(this.targetDeltaY * 1.025D, -1.0D, 1.0D);
                this.targetDeltaZ = Mth.clamp(this.targetDeltaZ * 1.025D, -1.0D, 1.0D);
                Vec3 vec3 = this.getDeltaMovement();
                this.setDeltaMovement(vec3.add((this.targetDeltaX - vec3.x) * 0.2D, (this.targetDeltaY - vec3.y) * 0.2D, (this.targetDeltaZ - vec3.z) * 0.2D));
            }
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                this.onHit(hitresult);
            }
        }
        this.checkInsideBlocks();
        Vec3 vec31 = this.getDeltaMovement();
        this.setPos(this.getX() + vec31.x, this.getY() + vec31.y, this.getZ() + vec31.z);
        ProjectileUtil.rotateTowardsMovement(this, 0.5F);
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.END_ROD, this.getX() - vec31.x, this.getY() - vec31.y + 0.15D, this.getZ() - vec31.z, 0.0D, 0.0D, 0.0D);
        } else if (this.finalTarget != null && !this.finalTarget.isRemoved()) {
            if (this.flightSteps > 0) {
                --this.flightSteps;
                if (this.flightSteps == 0) {
                    this.selectNextMoveDirection(this.currentMoveDirection == null ? null : this.currentMoveDirection.getAxis());
                }
            }
            if (this.currentMoveDirection != null) {
                BlockPos blockpos = this.blockPosition();
                Direction.Axis directionAxis = this.currentMoveDirection.getAxis();
                if (this.level().loadedAndEntityCanStandOn(blockpos.relative(this.currentMoveDirection), this)) {
                    this.selectNextMoveDirection(directionAxis);
                } else {
                    BlockPos blockpos1 = this.finalTarget.blockPosition();
                    if (directionAxis == Direction.Axis.X && blockpos.getX() == blockpos1.getX()
                            || directionAxis == Direction.Axis.Z && blockpos.getZ() == blockpos1.getZ()
                            || directionAxis == Direction.Axis.Y && blockpos.getY() == blockpos1.getY()) {
                        this.selectNextMoveDirection(directionAxis);
                    }
                }
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        Entity owner = this.getOwner();
        if (owner != null && (target == owner || MobUtil.areAllies(owner, target))) {
            return false;
        }
        return this.canHarm(target) && super.canHitEntity(target) && !target.noPhysics;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 16384.0D;
    }

    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity entity = hitResult.getEntity();
        Entity entity1 = this.getOwner();
        if (this.canHarm(entity)) {
            LivingEntity livingentity = entity1 instanceof LivingEntity ? (LivingEntity) entity1 : null;
            boolean flag = entity.hurt(this.damageSources().mobProjectile(this, livingentity), this.damage);
            if (flag) {
                this.doEnchantDamageEffects(livingentity, entity);
                if (entity instanceof LivingEntity living && !BossUtil.isBoss(living)) {
                    living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, this.levitationLength, this.levitationLevel),
                            entity1 != null ? entity1 : this);
                }
            }
        }
    }

    boolean canHarm(Entity target) {
        return MiscUtils.canHarmBasedOnTeamAndTag(null, this, target, this.getOwner(), entity -> true);
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        ((ServerLevel) this.level()).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.0D);
        this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0F, 1.0F);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        this.discard();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
            this.discard();
        }
        return true;
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        double d0 = packet.getXa();
        double d1 = packet.getYa();
        double d2 = packet.getZa();
        this.setDeltaMovement(d0, d1, d2);
    }
}
