package com.qiuyue.goetyominous.common.entities.ally.lm.projectile;

import com.qiuyue.goetyominous.common.entities.ally.lm.ServantMath;
import com.qiuyue.goetyominous.common.init.lm.LmDamageTypes;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.common.init.lm.LmParticles;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class SoulTrident extends AbstractArrow {

    private static final EntityDataAccessor<Byte> ID_LOYALTY =
            SynchedEntityData.defineId(SoulTrident.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> ID_FOIL =
            SynchedEntityData.defineId(SoulTrident.class, EntityDataSerializers.BOOLEAN);

    private ItemStack tridentItem = new ItemStack(Items.TRIDENT);

    private boolean dealtDamage;
    public int clientSideReturnTridentTickCount;

    public SoulTrident(EntityType<? extends SoulTrident> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_LOYALTY, (byte) 0);
        this.entityData.define(ID_FOIL, false);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        Entity owner = this.getOwner();
        return (owner != null && owner.isAlliedTo(entity)) || super.isAlliedTo(entity);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        for (int i = 0; (double) i < 0.5D; ++i) {
            if (this.level().isClientSide) {
                this.level().addParticle(LmParticles.GHOSTLY_SOUL.get(),
                        this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D),
                        0.0D, 0.025D, 0.0D);
            }
        }

        Entity owner = this.getOwner();
        int loyalty = this.entityData.get(ID_LOYALTY);
        if (loyalty > 0 && (this.dealtDamage || this.isNoPhysics()) && owner != null) {
            if (!this.isAcceptibleReturnOwner()) {
                if (!this.level().isClientSide && this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }
                this.discard();
            } else {
                this.setNoPhysics(true);
                Vec3 vec3 = owner.getEyePosition().subtract(this.position());
                this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015D * (double) loyalty, this.getZ());
                if (this.level().isClientSide) {
                    this.yOld = this.getY();
                }

                double d0 = 0.05D * (double) loyalty;
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(vec3.normalize().scale(d0)));
                if (this.clientSideReturnTridentTickCount == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.clientSideReturnTridentTickCount;
            }
        }

        super.tick();
    }

    private boolean isAcceptibleReturnOwner() {
        Entity owner = this.getOwner();
        if (owner != null && owner.isAlive()) {
            return !(owner instanceof ServerPlayer) || !owner.isSpectator();
        }
        return false;
    }

    @Override
    protected ItemStack getPickupItem() {
        return this.tridentItem.copy();
    }

    public boolean isFoil() {
        return this.entityData.get(ID_FOIL);
    }

    @Nullable
    @Override
    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        return this.dealtDamage ? null : super.findHitEntity(startVec, endVec);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity hitEntity = result.getEntity();

        float damage;
        if (hitEntity instanceof LivingEntity livingEntity) {
            damage = 8.0F + ServantMath.entityBasedHpDamage(livingEntity, 3.0F);
        } else {
            damage = 8.0F;
        }
        if (hitEntity instanceof LivingEntity livingEntity) {
            damage += EnchantmentHelper.getDamageBonus(this.tridentItem, livingEntity.getMobType());
        }

        Entity owner = this.getOwner();
        if (hitEntity instanceof LivingEntity livingEntity && !this.isAlliedTo(livingEntity)) {
            DamageSource damageSource = LmDamageTypes.ghostlyOrAttackerless(this.level(), owner);
            this.dealtDamage = true;
            SoundEvent soundevent = SoundEvents.TRIDENT_HIT;

            if (hitEntity.hurt(damageSource, damage)) {
                if (owner instanceof LivingEntity livingOwner) {
                    livingOwner.heal(8.0F);
                }

                EntityUtil.applyStackingEffect(livingEntity, ModEffects.SOUL_FRACTURE.get(),
                        1, 5, ServantMath.toTicks(10.0F));

                if (hitEntity.getType() == EntityType.ENDERMAN) {
                    return;
                }

                if (owner instanceof LivingEntity livingOwner) {
                    EnchantmentHelper.doPostHurtEffects(livingEntity, owner);
                    EnchantmentHelper.doPostDamageEffects(livingOwner, livingEntity);
                }

                this.doPostHurtEffects(livingEntity);
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));

            float volume = 1.0F;
            if (this.level() instanceof ServerLevel && this.level().isThundering() && this.isChanneling()) {
                BlockPos blockpos = hitEntity.blockPosition();
                if (this.level().canSeeSky(blockpos)) {
                    LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(this.level());
                    if (lightningbolt != null) {
                        lightningbolt.moveTo(Vec3.atBottomCenterOf(blockpos));
                        lightningbolt.setCause(owner instanceof ServerPlayer serverPlayer ? serverPlayer : null);
                        this.level().addFreshEntity(lightningbolt);
                        soundevent = SoundEvents.TRIDENT_THUNDER;
                        volume = 5.0F;
                    }
                }
            }

            this.playSound(soundevent, volume, 1.0F);
        }
    }

    public void spawnSpiralStrike(double max, double gapFill, double inBetweenGapFill, int constantDelay, double delayFactor) {
        for (double t = 0.0D; t < max; t += gapFill) {
            int delay = constantDelay + (int) (t * delayFactor);
            double r = t * inBetweenGapFill;
            double x = this.getX() + r * Math.cos(t);
            double y = this.getY();
            double z = this.getZ() + r * Math.sin(t);
            this.spawnSoulPillarExplosions(x, y, z, (int) y - 1, 0.0F, delay);
        }
    }

    private boolean spawnSoulPillarExplosions(double x, double y, double z, int lowestYCheck, float yRot, int warmupDelayTicks) {
        BlockPos blockpos = BlockPos.containing(x, y, z);
        boolean foundGround = false;
        double surfaceOffset = 0.0D;

        do {
            BlockPos below = blockpos.below();
            BlockState belowState = this.level().getBlockState(below);
            if (belowState.isFaceSturdy(this.level(), below, Direction.UP)) {
                if (!this.level().isEmptyBlock(blockpos)) {
                    BlockState currentState = this.level().getBlockState(blockpos);
                    VoxelShape shape = currentState.getCollisionShape(this.level(), blockpos);
                    if (!shape.isEmpty()) {
                        surfaceOffset = shape.max(Axis.Y);
                    }
                }
                foundGround = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= lowestYCheck);

        if (!foundGround) {
            return false;
        }

        this.level().addFreshEntity(new SoulPillarExplosionEntity(
                this.level(), x, (double) blockpos.getY() + surfaceOffset, z,
                yRot, warmupDelayTicks, (LivingEntity) this.getOwner(), 20, 8.0F, true));
        return true;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        this.spawnSpiralStrike(18.0D, 0.3D, 0.65F, 3, 1.0D);
        CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.15F, 10, 5);

        float f9 = (this.random.nextFloat() - 0.5F) * 8.0F;
        float f10 = (this.random.nextFloat() - 0.5F) * 4.0F;
        float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;
        float f8 = (this.random.nextFloat() - 0.75F) * 5.0F;
        float f6 = (this.random.nextFloat() - 0.75F) * 3.0F;
        float f7 = (this.random.nextFloat() - 0.75F) * 5.0F;
        this.level().addParticle(LmParticles.SOUL_EXPLOSION_RED.get(),
                this.getX() + (double) f9, this.getY() + (double) f10, this.getZ() + (double) f2,
                0.0D, 0.0D, 0.0D);
        this.level().addParticle(LmParticles.SOUL_EXPLOSION_RED.get(),
                this.getX() + (double) f8, this.getY() + (double) f6, this.getZ() + (double) f7,
                0.0D, 0.0D, 0.0D);
        this.level().addParticle(LmParticles.SOUL_EXPLOSION_RED.get(),
                this.getX() + (double) f9, this.getY() + 2.0D + (double) f10, this.getZ() + (double) f2,
                0.0D, 0.5D, 0.0D);
        this.level().addParticle(LmParticles.SOUL_EXPLOSION_RED.get(),
                this.getX() + (double) f8, this.getY() + 2.0D + (double) f6, this.getZ() + (double) f7,
                0.0D, 0.5D, 0.0D);

        if (!this.level().isClientSide) {
            if (result instanceof BlockHitResult) {
                this.discard();
            }
        }
    }

    public boolean isChanneling() {
        return EnchantmentHelper.hasChanneling(this.tridentItem);
    }

    @Override
    protected boolean tryPickup(Player player) {
        return super.tryPickup(player)
                || this.isNoPhysics() && this.ownedBy(player)
                && player.getInventory().add(this.getPickupItem());
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.DRAGON_FIREBALL_EXPLODE;
    }

    @Override
    public void playerTouch(Player player) {
        if (this.ownedBy(player) || this.getOwner() == null) {
            super.playerTouch(player);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Trident", 10)) {
            this.tridentItem = ItemStack.of(compound.getCompound("Trident"));
        }
        this.dealtDamage = compound.getBoolean("DealtDamage");
        this.entityData.set(ID_LOYALTY, (byte) EnchantmentHelper.getLoyalty(this.tridentItem));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("Trident", this.tridentItem.save(new CompoundTag()));
        compound.putBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    public void tickDespawn() {
        int loyalty = this.entityData.get(ID_LOYALTY);
        if (this.pickup != Pickup.ALLOWED || loyalty <= 0) {
            super.tickDespawn();
        }
    }

    @Override
    protected float getWaterInertia() {
        return 0.99F;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }
}
