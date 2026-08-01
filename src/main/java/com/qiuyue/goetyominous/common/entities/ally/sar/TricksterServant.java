package com.qiuyue.goetyominous.common.entities.ally.sar;

import com.Polarice3.Goety.common.entities.ally.illager.SpellcasterIllagerServant;
import com.qiuyue.goetyominous.common.items.sar.SarItems;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import com.teamabnormals.blueprint.core.util.NetworkUtil;
import com.teamabnormals.savage_and_ravage.core.other.SRDataProcessors;
import com.teamabnormals.savage_and_ravage.core.other.SRDataSerializers;
import com.teamabnormals.savage_and_ravage.core.other.SREvents;
import com.teamabnormals.savage_and_ravage.core.registry.SRBlocks;
import com.teamabnormals.savage_and_ravage.core.registry.SRParticleTypes;
import com.teamabnormals.savage_and_ravage.core.registry.SRSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Optional;

public class TricksterServant extends SpellcasterIllagerServant {
    private static final EntityDataAccessor<Integer> PRISON_CHARGING_TIME = SynchedEntityData.defineId(TricksterServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<Vec3>> PRISON_POS = SynchedEntityData.defineId(TricksterServant.class, SRDataSerializers.OPTIONAL_VECTOR3D);
    private static final int CHARGE_TIME = 4;
    private static final int PRISON_TIME = 60;

    public TricksterServant(EntityType<? extends TricksterServant> type, Level world) {
        super(type, world);
        this.xpReward = 0;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelectGoal();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SpellcasterCastingSpellGoal());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, LivingEntity.class, 8.0F, 0.6D, 1.0D, livingEntity -> {
            if (this.getTrueOwner() == null) {
                return false;
            }
            LivingEntity owner = this.getTrueOwner();
            return livingEntity != owner && owner.isAlliedTo(livingEntity) == false && owner.getLastHurtByMob() == livingEntity;
        }));
        this.goalSelector.addGoal(5, new CreatePrisonGoal());
        this.goalSelector.addGoal(6, new ThrowBoltGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PRISON_CHARGING_TIME, -1);
        this.entityData.define(PRISON_POS, Optional.empty());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("PrisonChargingTime", 3))
            this.entityData.set(PRISON_CHARGING_TIME, compound.getInt("PrisonChargingTime"));
        if (compound.contains("PrisonX", 6) && compound.contains("PrisonY", 6) && compound.contains("PrisonZ", 6))
            this.entityData.set(PRISON_POS, Optional.of(new Vec3(compound.getDouble("PrisonX"), compound.getDouble("PrisonY"), compound.getDouble("PrisonZ"))));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("PrisonChargingTime", this.entityData.get(PRISON_CHARGING_TIME));
        if (this.entityData.get(PRISON_CHARGING_TIME) > 0) {
            this.entityData.get(PRISON_POS).ifPresent(pos -> {
                compound.putDouble("PrisonX", pos.x);
                compound.putDouble("PrisonY", pos.y);
                compound.putDouble("PrisonZ", pos.z);
            });
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return sizeIn.height * 0.775F;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide && this.isCastingSpell()) {
            float f = this.yBodyRot * ((float) Math.PI / 180F) + Mth.cos((float) this.tickCount * 0.6662F) * 0.25F;
            float f1 = Mth.cos(f);
            float f2 = Mth.sin(f);
            ParticleOptions particle = this.getCurrentSpell() == IllagerServantSpell.FANGS ? SRParticleTypes.RUNE.get() : ParticleTypes.ENTITY_EFFECT;
            this.level().addParticle(particle, this.getX() + (double) f1 * 0.8D, this.getY() + 1.5D, this.getZ() + (double) f2 * 0.6D, 0.0, 0.0, 0.0);
            this.level().addParticle(particle, this.getX() - (double) f1 * 0.8D, this.getY() + 1.5D, this.getZ() - (double) f2 * 0.6D, 0.0, 0.0, 0.0);
        }

        this.entityData.get(PRISON_POS).ifPresent(pos -> {
            int time = this.entityData.get(PRISON_CHARGING_TIME);
            if (time > 0) {
                int loops = 10 * (CHARGE_TIME - time) + 1;
                for (int i = 0; i < loops; i++) {
                    double progress = ((double) time / CHARGE_TIME) - ((1.0 / CHARGE_TIME) * ((double) i / loops));
                    double coefficient = 0.65625D - (0.34375D * progress);
                    double adjustment = 0.34375D - (0.34375D * progress);
                    double x = pos.x + (this.random.nextInt(2) == 0 ? 1 : -1) * 0.65625D * this.random.nextDouble();
                    double z = pos.z + (this.random.nextInt(2) == 0 ? 1 : -1) * (Math.abs(pos.x - x) < 0.34375 ? ((coefficient * this.random.nextDouble()) + adjustment) : 0.65625D * this.random.nextDouble());
                    this.level().addParticle(SRParticleTypes.RUNE.get(), x, pos.y + 0.8125D, z, 0.0, 0.0, 0.0);
                }
                this.entityData.set(PRISON_CHARGING_TIME, time - 1);
            } else if (time == 0) {
                com.qiuyue.goetyominous.common.entities.ally.sar.RunePrison runePrison = new com.qiuyue.goetyominous.common.entities.ally.sar.RunePrison(this.level(), null, PRISON_TIME, false, this);
                runePrison.moveTo(pos.x, pos.y + 0.5, pos.z, 0.0F, 0.0F);
                this.level().addFreshEntity(runePrison);
                this.entityData.set(PRISON_POS, Optional.empty());
                this.entityData.set(PRISON_CHARGING_TIME, -1);
            }
        });
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        IDataManager data = (IDataManager) this;
        if (source.getDirectEntity() instanceof Projectile && this.getHealth() - amount <= 0 && data.getValue(SRDataProcessors.TOTEM_SHIELD_COOLDOWN) <= 0) {
            this.setHealth(2.0F);
            data.setValue(SRDataProcessors.TOTEM_SHIELD_COOLDOWN, 1800);
            if (!this.level().isClientSide()) {
                this.level().broadcastEntityEvent(this, (byte) 35);
                for (int i = 0; i < 64; i++) {
                    if (this.teleport())
                        return true;
                }
            }
            return false;
        }
        return super.hurt(source, amount);
    }

    protected boolean teleport() {
        if (this.isAlive()) {
            double randomX = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
            double randomZ = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
            BlockState state = this.level().getBlockState(new BlockPos.MutableBlockPos(randomX, this.getY() - 1, randomZ));
            if (state.blocksMotion() && !state.getFluidState().is(FluidTags.LAVA)) {
                AABB oldBox = this.getBoundingBox().inflate(0.5D);
                BlockPos oldPos = this.blockPosition();
                boolean successful = this.randomTeleport(randomX, this.getY(), randomZ, true);
                if (successful) {
                    this.level().playSound(null, oldPos, SRSoundEvents.GENERIC_PUFF_OF_SMOKE.get(), this.getSoundSource(), 10.0F, 1.0F);
                    this.level().playSound(null, this.blockPosition(), SRSoundEvents.GENERIC_PUFF_OF_SMOKE.get(), this.getSoundSource(), 10.0F, 1.0F);
                    this.level().playSound(null, oldPos, SRSoundEvents.ENTITY_TRICKSTER_LAUGH.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
                    ConfusionBolt.spawnGaussianParticles(this.level(), this.random, oldBox, SREvents.POOF_KEY, 50);
                    ConfusionBolt.spawnGaussianParticles(this.level(), this.random, this.getBoundingBox().inflate(0.5D), SREvents.POOF_KEY, 50);
                    if (ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
                        BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();
                        for (int x = oldPos.getX() - 2; x <= oldPos.getX() + 2; x++) {
                            for (int y = oldPos.getY() - 2; y <= oldPos.getY() + 2; y++) {
                                for (int z = oldPos.getZ() - 2; z <= oldPos.getZ() + 2; z++) {
                                    searchPos.set(x, y, z);
                                    if (this.level().getBlockState(searchPos).getBlock() == SRBlocks.GLOOMY_TILES.get()) {
                                        this.level().setBlock(searchPos, SRBlocks.RUNED_GLOOMY_TILES.get().defaultBlockState(), 2);
                                        searchPos.move(Direction.UP);
                                        if (!this.level().isClientSide && !this.level().getBlockState(searchPos).isSolidRender(this.level(), searchPos)) {
                                            for (int i = 0; i < 3; i++)
                                                NetworkUtil.spawnParticle(SRParticleTypes.RUNE.getId().toString(), this.level().dimension(), x + this.random.nextDouble(), y + 1.25, z + this.random.nextDouble(), 0.0D, 0.0D, 0.0D);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return successful;
            }
        }
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SRSoundEvents.TRICKSTER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRSoundEvents.TRICKSTER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SRSoundEvents.TRICKSTER_DEATH.get();
    }

    @Override
    public void playAmbientSound() {
        if (this.entityData.get(PRISON_CHARGING_TIME) < 0)
            super.playAmbientSound();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        super.playStepSound(pos, blockIn);
        this.playSound(SRSoundEvents.TRICKSTER_STEP.get(), 0.5F, 1.0F);
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SRSoundEvents.ENTITY_TRICKSTER_CAST_SPELL.get();
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SRSoundEvents.TRICKSTER_CELEBRATE.get();
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(SarItems.TRICKSTER_SERVANT_SPAWN_EGG.get());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.TricksterServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.TricksterServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.TricksterServantFollowRange.get());
    }

    class CreatePrisonGoal extends SpellcasterUseSpellGoal {

        @Override
        protected void performSpellCasting() {
            LivingEntity target = TricksterServant.this.getTarget();
            if (target != null) {
                TricksterServant.this.entityData.set(PRISON_POS, Optional.of(target.position()));
                TricksterServant.this.entityData.set(PRISON_CHARGING_TIME, CHARGE_TIME);
            }
        }

        @Override
        protected int getCastingTime() {
            return 40;
        }

        @Override
        protected int getCastingInterval() {
            return 100;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SRSoundEvents.GENERIC_PREPARE_ATTACK.get();
        }

        @Override
        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.FANGS;
        }
    }

    class ThrowBoltGoal extends SpellcasterUseSpellGoal {

        @Override
        protected void performSpellCasting() {
            Level world = TricksterServant.this.level();
            LivingEntity target = TricksterServant.this.getTarget();
            if (target != null) {
                ConfusionBolt bolt = new ConfusionBolt(world, TricksterServant.this, 240);
                Vec3 pos = TricksterServant.this.position();
                Vec3 targetPos = target.position();
                bolt.setPos(bolt.getX(), bolt.getY() - 0.5, bolt.getZ());
                bolt.setDeltaMovement(new Vec3(targetPos.x - pos.x, targetPos.y - pos.y, targetPos.z - pos.z).normalize().scale(0.25));
                world.addFreshEntity(bolt);
            }
        }

        @Override
        protected int getCastingTime() {
            return 80;
        }

        @Override
        protected int getCastingInterval() {
            return 340;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SRSoundEvents.GENERIC_PREPARE_ATTACK.get();
        }

        @Override
        protected IllagerServantSpell getSpell() {
            return IllagerServantSpell.SUMMON_VEX;
        }
    }
}
