package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.BigShulkerBullet;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.GravityBigShulkerBullet;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.config.ModConfig;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.LMFallingBlockEntity;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ShulkerMimicServant extends IAnimatedMiniBossServant {

    private static final EntityDataAccessor<Integer> PHASE =
            SynchedEntityData.defineId(ShulkerMimicServant.class, EntityDataSerializers.INT);

    public static final int SHOOT_COOLDOWN = 30;
    public static final int DOUBLE_SLASH_COOLDOWN = 40;
    public static final int BIG_SHOOT_COOLDOWN = 60;
    public static final int FIREWORK_GRAB_COOLDOWN = 100;
    public static final int REPEL_SLAM_COOLDOWN = 60;
    public static final int BACKSTEP_COOLDOWN = 80;

    public int shootCooldown = SHOOT_COOLDOWN;
    public int doubleSlashCooldown = DOUBLE_SLASH_COOLDOWN;
    public int bigShootCooldown = BIG_SHOOT_COOLDOWN;
    public int fireworkGrabCooldown = FIREWORK_GRAB_COOLDOWN;
    public int repelSlamCooldown = REPEL_SLAM_COOLDOWN;
    public int backstepCooldown = BACKSTEP_COOLDOWN;

    private final ShulkerMimicServantPart[] entityParts;
    public final ShulkerMimicServantPart head;
    public float partXRot;
    public float partYRot;
    public int backstepType = 2;
    public int shootType = 4;
    public boolean succedGrabbing;
    public int mimicDeathTime;

    public final AnimationState biteAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState backstepDashAnimationState = new AnimationState();
    public final AnimationState backstepAnimationState = new AnimationState();
    public final AnimationState tripleShootAnimationState = new AnimationState();
    public final AnimationState tripleShootTp1AnimationState = new AnimationState();
    public final AnimationState tripleShootTp2AnimationState = new AnimationState();
    public final AnimationState doubleSlashAnimationState = new AnimationState();
    public final AnimationState pullHitAnimationState = new AnimationState();
    public final AnimationState bigShootAnimationState = new AnimationState();
    public final AnimationState doubleShootAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();
    public final AnimationState fireworkGrabPreAnimationState = new AnimationState();
    public final AnimationState fireworkGrabFailAnimationState = new AnimationState();
    public final AnimationState fireworkGrabSuccessAnimationState = new AnimationState();

    public ShulkerMimicServant(EntityType<? extends ShulkerMimicServant> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 20;
        this.setMaxUpStep(1.5F);
        this.head = new ShulkerMimicServantPart(this, "head", 0.7F, 0.7F);
        this.entityParts = new ShulkerMimicServantPart[]{this.head};
        this.setNoAi(false);
        this.setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, 1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ShulkerMimicServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.ShulkerMimicServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.ShulkerMimicServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.ShulkerMimicServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.ShulkerMimicServantMovementSpeed.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.ShulkerMimicServantAttackKnockback.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ShulkerMimicServantDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.ShulkerMimicServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ShulkerMimicServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.ShulkerMimicServantDamage.get());
    }

    @Override
    public double getFollowSpeed() {
        return 3.0D;
    }

    @Override
    public double getCommandSpeed() {
        return 3.0D;
    }

    public float damageNerf() {
        return 5.0F;
    }

    @Override
    public int attackDelayTicksValue() {
        return 3;
    }

    @Override
    protected boolean isCarryImmune() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return true;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public PartEntity<?>[] getParts() {
        return this.entityParts;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < this.entityParts.length; ++i) {
            this.entityParts[i].setId(id + i + 1);
        }
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return null;
    }

    public float getPartXRot() {
        return this.partXRot;
    }

    public void setPartXRot(float partXRot) {
        this.partXRot = partXRot;
    }

    public float getPartYRot() {
        return this.partYRot;
    }

    public void setPartYRot(float partYRot) {
        this.partYRot = partYRot;
    }

    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    public void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    public boolean isDuringTeleportation() {
        return this.getAttackState() == 9 && this.attackTicks >= 23 && this.attackTicks <= 28
                || this.getAttackState() == 10 && this.attackTicks >= 33 && this.attackTicks <= 38;
    }

    public Crackiness getCrackiness() {
        return Crackiness.byFraction(this.getHealth() / this.getMaxHealth());
    }

    public boolean isMediumCrackiness() {
        return this.getCrackiness() == Crackiness.MEDIUM;
    }

    public boolean isHighCrackiness() {
        return this.getCrackiness() == Crackiness.HIGH;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SHULKER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SHULKER_HURT_CLOSED;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SHULKER_DEATH;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player == this.getTrueOwner()
                && itemStack.is(Items.ENDER_PEARL)
                && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.playSound(SoundEvents.SHULKER_AMBIENT, 1.0F, 1.0F);
                this.heal(2.0F);
                this.gameEvent(GameEvent.EAT, this);
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ParticleTypes.HEART,
                                this.getRandomX(1.0D),
                                this.getY() + this.getBbHeight() + 0.3D + this.random.nextDouble() * 0.5D,
                                this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                    }
                }
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
            }
            player.swing(hand);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_PROJECTILE) || this.isDuringTeleportation() || source.is(DamageTypes.FALL)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    public boolean applyPartDamage(ShulkerMimicServantPart part, DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    private boolean isFriendlyTo(LivingEntity other) {
        return other == this || other instanceof ShulkerMimicServant || MobUtil.areAllies(this, other);
    }

    private void tickPart(ShulkerMimicServantPart part, double offsetX, double offsetY, double offsetZ) {
        part.setPos(this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ);
    }

    @Override
    public void tick() {
        if (this.isVehicle() && this.getFirstPassenger() instanceof Player player) {
            player.setShiftKeyDown(false);
        }
        if (this.isVehicle() && this.getAttackState() != 14 && this.getAttackState() != 16) {
            this.ejectPassengers();
        }
        if (this.isMediumCrackiness() && this.getPhase() != 2) {
            this.setPhase(2);
            this.playSound(SoundEvents.SHULKER_HURT, 1.0F, 1.0F);
        }
        if (this.isHighCrackiness() && this.getPhase() != 3) {
            this.setPhase(3);
            this.playSound(SoundEvents.SHULKER_HURT, 1.0F, 1.0F);
        }
        float partBodyYaw = this.getPartYRot() * ((float) Math.PI / 180);
        float partBodyRotYaw = this.yBodyRot - this.getPartYRot() * ((float) Math.PI / 180);
        float partBodyPitch = 1.0F + this.getPartXRot() * ((float) Math.PI / 180);
        float f = Mth.cos(partBodyRotYaw * ((float) Math.PI / 180));
        float f1 = Mth.sin(partBodyRotYaw * ((float) Math.PI / 180));
        double theta = partBodyRotYaw * (Math.PI / 180) + Math.PI / 2;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        float y = 1.5F;
        float vec = Mth.clamp(y - partBodyPitch, 1.0F, y);
        float offset = Mth.sin(partBodyYaw) * 2.0F;
        this.tickPart(this.head, vec * vecX + f * offset, partBodyPitch, vec * vecZ + f1 * offset);
        Vec3[] previous = new Vec3[this.entityParts.length];
        for (int i = 0; i < this.entityParts.length; ++i) {
            previous[i] = new Vec3(this.entityParts[i].getX(), this.entityParts[i].getY(), this.entityParts[i].getZ());
        }
        for (int i = 0; i < this.entityParts.length; ++i) {
            this.entityParts[i].xo = previous[i].x;
            this.entityParts[i].yo = previous[i].y;
            this.entityParts[i].zo = previous[i].z;
            this.entityParts[i].xOld = previous[i].x;
            this.entityParts[i].yOld = previous[i].y;
            this.entityParts[i].zOld = previous[i].z;
        }
        if (this.shootCooldown > 0) {
            --this.shootCooldown;
        }
        if (this.backstepCooldown > 0) {
            --this.backstepCooldown;
        }
        if (this.repelSlamCooldown > 0) {
            --this.repelSlamCooldown;
        }
        if (this.bigShootCooldown > 0) {
            --this.bigShootCooldown;
        }
        if (this.fireworkGrabCooldown > 0) {
            --this.fireworkGrabCooldown;
        }
        if (this.doubleSlashCooldown > 0) {
            --this.doubleSlashCooldown;
        }
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
        }
        super.tick();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            if (this.isDuringTeleportation() && !this.isInvisible()) {
                this.setInvisible(true);
            } else if (!this.isDuringTeleportation() && this.isInvisible()) {
                this.setInvisible(false);
            }
        }
        this.UpdateWithAttack();
    }

    @Override
    public void die(DamageSource source) {
        this.setAttackState(13);
        this.stopAllAnimationStates();
        super.die(source);
    }

    @Override
    protected void tickDeath() {
        ++this.mimicDeathTime;
        if (this.mimicDeathTime == 60) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("phase", this.getPhase());
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setPhase(compound.getInt("phase"));
        super.readAdditionalSaveData(compound);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player
                && countServants(player) >= MobsConfig.ShulkerMimicServantLimit.get()) {
            return null;
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    private static int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof ShulkerMimicServant servant && servant.getTrueOwner() == player) {
                    ++count;
                }
            }
        }
        return count;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.setPersistenceRequired();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 3.0D));

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 4, 0, 51, 25, 5.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && ShulkerMimicServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && ShulkerMimicServant.this.getTarget() != null
                        && ShulkerMimicServant.this.getNextRandomBackstep() == 2
                        && ShulkerMimicServant.this.backstepCooldown <= 0;
            }

            @Override
            public void stop() {
                ShulkerMimicServant.this.backstepCooldown = BACKSTEP_COOLDOWN;
                ShulkerMimicServant.this.randomizeAttackPatterns();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 5, 0, 17, 25, 5.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && ShulkerMimicServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && ShulkerMimicServant.this.getTarget() != null
                        && ShulkerMimicServant.this.getNextRandomBackstep() == 1
                        && ShulkerMimicServant.this.backstepCooldown <= 0;
            }

            @Override
            public void stop() {
                ShulkerMimicServant.this.backstepCooldown = BACKSTEP_COOLDOWN;
                ShulkerMimicServant.this.randomizeAttackPatterns();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 6, 0, 48, 42, 12.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && ShulkerMimicServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && ShulkerMimicServant.this.getTarget() != null
                        && ShulkerMimicServant.this.shootCooldown <= 0
                        && ShulkerMimicServant.this.getNextRandomShootType() == 1;
            }

            @Override
            public void stop() {
                ShulkerMimicServant.this.shootCooldown = SHOOT_COOLDOWN;
                ShulkerMimicServant.this.randomizeAttackPatterns();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 6, 0, 48, 39, 12.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && ShulkerMimicServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && ShulkerMimicServant.this.getTarget() != null
                        && ShulkerMimicServant.this.shootCooldown <= 0
                        && ShulkerMimicServant.this.getNextRandomShootType() == 3;
            }

            @Override
            public void stop() {
                ShulkerMimicServant.this.shootCooldown = SHOOT_COOLDOWN;
                ShulkerMimicServant.this.randomizeAttackPatterns();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 9, 0, 67, 60, 12.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && ShulkerMimicServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && ShulkerMimicServant.this.getTarget() != null
                        && ShulkerMimicServant.this.shootCooldown <= 0
                        && ShulkerMimicServant.this.getNextRandomShootType() == 2;
            }

            @Override
            public void stop() {
                ShulkerMimicServant.this.shootCooldown = SHOOT_COOLDOWN;
                ShulkerMimicServant.this.randomizeAttackPatterns();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 10, 0, 67, 60, 12.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && ShulkerMimicServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && ShulkerMimicServant.this.getTarget() != null
                        && ShulkerMimicServant.this.shootCooldown <= 0
                        && ShulkerMimicServant.this.getNextRandomShootType() == 3;
            }

            @Override
            public void stop() {
                ShulkerMimicServant.this.shootCooldown = SHOOT_COOLDOWN;
                ShulkerMimicServant.this.randomizeAttackPatterns();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 11, 0, 42, 38, 12.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && ShulkerMimicServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && ShulkerMimicServant.this.getTarget() != null
                        && ShulkerMimicServant.this.bigShootCooldown <= 0;
            }

            @Override
            public void stop() {
                ShulkerMimicServant.this.bigShootCooldown = BIG_SHOOT_COOLDOWN;
                ShulkerMimicServant.this.randomizeAttackPatterns();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 12, 0, 45, 38, 12.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && ShulkerMimicServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && ShulkerMimicServant.this.getTarget() != null
                        && ShulkerMimicServant.this.bigShootCooldown <= 0
                        && ShulkerMimicServant.this.getNextRandomShootType() == 4;
            }

            @Override
            public void stop() {
                ShulkerMimicServant.this.bigShootCooldown = BIG_SHOOT_COOLDOWN;
                ShulkerMimicServant.this.randomizeAttackPatterns();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 7, 0, 65, 5, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && ShulkerMimicServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && ShulkerMimicServant.this.getTarget() != null
                        && ShulkerMimicServant.this.repelSlamCooldown <= 0;
            }

            @Override
            public void stop() {
                ShulkerMimicServant.this.repelSlamCooldown = REPEL_SLAM_COOLDOWN;
                ShulkerMimicServant.this.randomizeAttackPatterns();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 3, 0, 36, 36, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && ShulkerMimicServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && ShulkerMimicServant.this.getTarget() != null;
            }

            @Override
            public void stop() {
                ShulkerMimicServant.this.randomizeAttackPatterns();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 8, 0, 50, 20, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && ShulkerMimicServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && ShulkerMimicServant.this.getTarget() != null
                        && ShulkerMimicServant.this.getY() - ShulkerMimicServant.this.getTarget().getY() >= -1.0D
                        && ShulkerMimicServant.this.doubleSlashCooldown <= 0;
            }

            @Override
            public void stop() {
                ShulkerMimicServant.this.doubleSlashCooldown = DOUBLE_SLASH_COOLDOWN;
                ShulkerMimicServant.this.randomizeAttackPatterns();
                super.stop();
            }
        });
        this.goalSelector.addGoal(1, new IStateGoal(this, 13, 13, 0, 60, 0));
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 14, 15, 27, 5, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && ShulkerMimicServant.this.getRandom().nextFloat() * 100.0F < 32.0F
                        && ShulkerMimicServant.this.getTarget() != null
                        && ShulkerMimicServant.this.fireworkGrabCooldown <= 0;
            }

            @Override
            public void stop() {
                ShulkerMimicServant.this.randomizeAttackPatterns();
                if (ShulkerMimicServant.this.succedGrabbing) {
                    if (!ShulkerMimicServant.this.level().isClientSide) {
                        ShulkerMimicServant.this.setAttackState(16);
                    }
                    ShulkerMimicServant.this.succedGrabbing = false;
                } else {
                    super.stop();
                }
            }
        });
        this.goalSelector.addGoal(0, new IStateGoal(this, 15, 15, 0, 20, 0) {
            @Override
            public void stop() {
                ShulkerMimicServant.this.fireworkGrabCooldown = FIREWORK_GRAB_COOLDOWN;
                super.stop();
            }
        });
        this.goalSelector.addGoal(0, new IStateGoal(this, 16, 16, 0, 44, 0) {
            @Override
            public void stop() {
                ShulkerMimicServant.this.fireworkGrabCooldown = FIREWORK_GRAB_COOLDOWN;
                super.stop();
            }
        });
    }

    public void randomizeAttackPatterns() {
        this.randomizeNextShootType();
        this.randomizeNextBackstepType();
    }

    public int getNextRandomBackstep() {
        return this.backstepType;
    }

    public void randomizeNextBackstepType() {
        if (this.random.nextInt(2) == 0) {
            this.backstepType = 2;
        } else {
            this.backstepType = this.getPhase() <= 1 ? 2 : 1;
        }
    }

    public int getNextRandomShootType() {
        return this.shootType;
    }

    public void randomizeNextShootType() {
        switch (this.random.nextInt(4)) {
            case 0 -> this.shootType = this.getPhase() <= 1 ? 4 : 1;
            case 1 -> this.shootType = this.getPhase() <= 2 ? (this.getPhase() <= 1 ? 4 : 1) : 2;
            default -> this.shootType = this.getPhase() <= 2 ? (this.getPhase() <= 1 ? 4 : 1) : 3;
        }
    }

    public void backStep(float strength) {
        float yaw = (float) Math.toRadians(this.getYRot() + 90.0F);
        Vec3 dodgePos = this.getDeltaMovement().add(strength * Math.cos(yaw), 0.0D, strength * Math.sin(yaw));
        this.setDeltaMovement(dodgePos.x, dodgePos.y, dodgePos.z);
    }

    public void shootGravityShulkerBullet(float velocity, float x, float y, float z, int inaccuracy, float size) {
        Vec3 headPos = this.head.position();
        if (this.level().isClientSide) {
            float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
            float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
            this.level().addParticle(ModParticles.SHULKER_EXPLOSION.get(), headPos.x, headPos.y + 1.0D, headPos.z, 0.0D, 0.0D, 0.0D);
        }
        if (this.targetIsNotNull()) {
            GravityBigShulkerBullet bomb = new GravityBigShulkerBullet(LmEntityRegistry.GRAVITY_BIG_SHULKER_BULLET.get(), this.level());
            bomb.setBulletSize(size);
            bomb.setPosRaw(x, y + 1.5F, z);
            double d0 = this.target().getX() - x;
            double d1 = this.target().getBoundingBox().minY + this.target().getBbHeight() / 2.0F - bomb.getY();
            double d2 = this.target().getZ() - z;
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            bomb.shoot(d0, d1 + d3 * 0.2D, d2, velocity, inaccuracy - this.level().getDifficulty().getId() * 4);
            bomb.setOwner(this);
            this.level().addFreshEntity(bomb);
        }
    }

    public void shootBigShulkerBullet(double destX, double destY, double destZ, double x, double y, double z,
                                      float size, boolean isFirework, LivingEntity passenger) {
        Vec3 headPos = this.head.position();
        if (this.level() instanceof ServerLevel serverLevel && !isFirework) {
            serverLevel.sendParticles(ModParticles.SHULKER_EXPLOSION.get(), headPos.x, headPos.y + 1.0D, headPos.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        this.backStep(-0.5F);
        if (this.getTarget() != null) {
            BigShulkerBullet bullet = new BigShulkerBullet(this.level(), this, destX - x, destY - y, destZ - z, size, isFirework, passenger);
            bullet.setOwner(this);
            bullet.setPosRaw(x, y, z);
            this.level().addFreshEntity(bullet);
        }
    }

    private void UpdateWithAttack() {
        float smallBulletSize = 0.75F;
        float mediumBulletSize = 1.0F;
        float bigBulletSize = 1.5F;

        if (this.getAttackState() == 3) {
            if (this.attackTicks == 14) {
                this.calculatedDash(0.2F);
                this.playSound(SoundEvents.EVOKER_FANGS_ATTACK, 1.0F, 1.0F);
            }
            if (this.attackTicks == 17) {
                this.AreaAttack(3.9F, 4.0F, 180.0F, 16.0F, 100, true, false);
            }
        }
        if (this.getAttackState() == 4) {
            if (this.attackTicks > 23 && this.attackTicks < 30 && this.level().isClientSide) {
                float g = (float) Math.toRadians(-this.getYRot() + 180.0F);
                double theta = (double) this.getYRot() * (Math.PI / 180) + Math.PI / 2;
                double vecX = Math.cos(theta);
                double vecZ = Math.sin(theta);
                this.level().addParticle(new Circle.RingData(g, 0.0F, 30, 1.0F, 1.0F, 1.0F, 1.0F, 40.0F, false, Circle.EnumRingBehavior.GROW_THEN_SHRINK),
                        this.getX() + vecX * 1.5D, this.getY() + 1.0D, this.getZ() + vecZ * 1.5D, 0.0D, 0.0D, 0.0D);
            }
            if (this.attackTicks > 3 && this.attackTicks < 7) {
                this.backStep(-0.75F);
            }
            if (this.attackTicks == 23) {
                this.calculatedDash(0.35F);
            }
            if (this.attackTicks == 31) {
                this.StraightLineAreaAttack(0.005F, 4.0F, 100, 20.0F, true);
            }
        }
        if (this.getAttackState() == 5 && this.attackTicks > 3 && this.attackTicks < 7) {
            this.backStep(-0.75F);
        }
        if (this.getAttackState() == 6) {
            if (this.attackTicks == 10) {
                this.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 1.0F);
                if (this.targetIsNotNull()) {
                    this.shootBigShulkerBullet(this.target().getX(), this.target().getY() + 1.5D, this.target().getZ(),
                            this.getX(), this.getY() + 2.0D, this.getZ(), smallBulletSize, false, null);
                }
            }
            if (this.attackTicks == 21) {
                this.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 1.0F);
                if (this.targetIsNotNull()) {
                    this.shootBigShulkerBullet(this.target().getX(), this.target().getY() + 1.5D, this.target().getZ(),
                            this.getX(), this.getY() + 2.0D, this.getZ(), mediumBulletSize, false, null);
                }
            }
            if (this.attackTicks == 32) {
                this.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 1.0F);
                if (this.targetIsNotNull()) {
                    this.shootBigShulkerBullet(this.target().getX(), this.target().getY() + 1.5D, this.target().getZ(),
                            this.getX(), this.getY() + 2.0D, this.getZ(), bigBulletSize, false, null);
                }
            }
        }
        if (this.getAttackState() == 12) {
            if (this.attackTicks == 10) {
                this.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 1.0F);
                if (this.targetIsNotNull()) {
                    this.shootBigShulkerBullet(this.target().getX(), this.target().getY() + 1.5D, this.target().getZ(),
                            this.getX(), this.getY() + 2.0D, this.getZ(), smallBulletSize, false, null);
                }
            }
            if (this.attackTicks == 21) {
                this.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 1.0F);
                if (this.targetIsNotNull()) {
                    this.shootBigShulkerBullet(this.target().getX(), this.target().getY() + 1.5D, this.target().getZ(),
                            this.getX(), this.getY() + 2.0D, this.getZ(), mediumBulletSize, false, null);
                }
            }
        }
        if (this.getAttackState() == 9) {
            if (this.attackTicks == 23 && this.getTarget() != null) {
                this.teleportBehindTarget(-5.0F, 2.0F);
            }
            if (this.attackTicks == 10) {
                this.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 1.0F);
                if (this.targetIsNotNull()) {
                    this.shootBigShulkerBullet(this.target().getX(), this.target().getY() + 1.5D, this.target().getZ(),
                            this.getX(), this.getY() + 2.0D, this.getZ(), smallBulletSize, false, null);
                }
            }
            if (this.attackTicks == 33) {
                this.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 1.0F);
                if (this.targetIsNotNull()) {
                    this.shootBigShulkerBullet(this.target().getX(), this.target().getY() + 1.5D, this.target().getZ(),
                            this.getX(), this.getY() + 2.0D, this.getZ(), mediumBulletSize, false, null);
                }
            }
            if (this.attackTicks == 43) {
                this.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 1.0F);
                if (this.targetIsNotNull()) {
                    this.shootBigShulkerBullet(this.target().getX(), this.target().getY() + 1.5D, this.target().getZ(),
                            this.getX(), this.getY() + 2.0D, this.getZ(), bigBulletSize, false, null);
                }
            }
        }
        if (this.getAttackState() == 10) {
            if (this.attackTicks == 34 && this.getTarget() != null) {
                this.teleportBehindTarget(-5.0F, 2.0F);
            }
            if (this.attackTicks == 10) {
                this.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 1.0F);
                if (this.targetIsNotNull()) {
                    this.shootBigShulkerBullet(this.target().getX(), this.target().getY() + 1.5D, this.target().getZ(),
                            this.getX(), this.getY() + 2.0D, this.getZ(), smallBulletSize, false, null);
                }
            }
            if (this.attackTicks == 21) {
                this.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 1.0F);
                if (this.targetIsNotNull()) {
                    this.shootBigShulkerBullet(this.target().getX(), this.target().getY() + 1.5D, this.target().getZ(),
                            this.getX(), this.getY() + 2.0D, this.getZ(), mediumBulletSize, false, null);
                }
            }
            if (this.attackTicks == 44) {
                this.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 1.0F);
                if (this.targetIsNotNull()) {
                    this.shootBigShulkerBullet(this.target().getX(), this.target().getY() + 1.5D, this.target().getZ(),
                            this.getX(), this.getY() + 2.0D, this.getZ(), bigBulletSize, false, null);
                }
            }
        }
        if (this.getAttackState() == 7) {
            int i = 24;
            int j = 2;
            while (i <= 29) {
                if (this.attackTicks == i) {
                    this.SpawnDamagingBlocks(0.7F, j, 4.0F, 2.0F, 1.0F, 1.0F, 0.05F);
                }
                if (this.attackTicks >= 24 && this.attackTicks <= 28) {
                    float f2 = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
                    float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
                    double theta = (double) this.yBodyRot * (Math.PI / 180) + Math.PI / 2;
                    double vecX = Math.cos(theta);
                    double vecZ = Math.sin(theta);
                    float forward = 2.5F;
                    float offset1 = 2.0F;
                    float offset2 = -2.0F;
                    float f3 = (this.random.nextFloat() - 0.5F) * 4.0F;
                    float f5 = (this.random.nextFloat() - 0.5F) * 4.0F;
                    if (this.level().isClientSide) {
                        this.level().addParticle(ModParticles.SHULKER_EXPLOSION.get(),
                                this.getX() + forward * vecX + f2 * offset1 + f3, this.getY(), this.getZ() + forward * vecZ + f1 * offset1 + f5, 0.0D, 0.0D, 0.0D);
                        this.level().addParticle(ModParticles.SHULKER_EXPLOSION.get(),
                                this.getX() + forward * vecX + f2 * offset2 + f3, this.getY(), this.getZ() + forward * vecZ + f1 * offset2 + f5, 0.0D, 0.0D, 0.0D);
                    }
                }
                if (this.attackTicks == 24) {
                    CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.1F, 10, 5);
                    this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 1.0F, 1.0F);
                    this.AreaAttack(5.0F, 4.0F, 180.0F, 20.0F, 140, true, false);
                }
                if (this.attackTicks > 24 && this.attackTicks < 46) {
                    if (this.level().isClientSide) {
                        float yaw = (float) Math.toRadians(-this.getYRot());
                        float pitch = (float) Math.toRadians(-this.getXRot());
                        float spread = 0.7F;
                        float speed = 2.0F;
                        float xComp = (float) (Math.sin(yaw) * Math.cos(pitch));
                        float yComp = (float) Math.sin(pitch);
                        float zComp = (float) (Math.cos(yaw) * Math.cos(pitch));
                        double theta = (double) this.getYRot() * (Math.PI / 180) + Math.PI / 2;
                        double vecX = Math.cos(theta);
                        double vecZ = Math.sin(theta);
                        for (int g = 0; g < 3; ++g) {
                            double xSpeed = speed * xComp + spread * (this.random.nextFloat() * 2.0F - 1.0F) * Math.sqrt(1.0F - xComp * xComp);
                            double ySpeed = speed * yComp + spread * (this.random.nextFloat() * 2.0F - 1.0F) * Math.sqrt(1.0F - yComp * yComp);
                            double zSpeed = speed * zComp + spread * (this.random.nextFloat() * 2.0F - 1.0F) * Math.sqrt(1.0F - zComp * zComp);
                            this.level().addAlwaysVisibleParticle(ParticleTypes.CLOUD, this.getX() + 2.0D * vecX, this.getY() + 2.0D, this.getZ() + 2.0D * vecZ, xSpeed, ySpeed, zSpeed);
                        }
                    }
                    this.PushEntitiesAwayInFrontOf(-0.5F, 0.0F, 14.0F, 0, 5.0F);
                }
                i += 2;
                ++j;
            }
        }
        if (this.getAttackState() == 8) {
            if (this.attackTicks == 14) {
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 1.0F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == 17) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.05F, 5, 5);
                this.AreaAttack(4.5F, 1.1F, 180.0F, 18.0F, 100, false, false);
            }
            if (this.attackTicks == 27) {
                this.playSound(ModSounds.POSSESSED_PALADIN_SWING.get(), 1.0F, 1.0F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == 30) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.05F, 5, 5);
                this.AreaAttack(4.5F, 1.1F, 180.0F, 18.0F, 100, false, false);
            }
        }
        if (this.getAttackState() == 11) {
            float y = (float) (this.getY() + 0.5D);
            float f3 = Mth.cos(this.yBodyRot * ((float) Math.PI / 180));
            float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180));
            double theta = (double) this.yBodyRot * (Math.PI / 180) + Math.PI / 2;
            double vecX = Math.cos(theta);
            double vecZ = Math.sin(theta);
            float forward = 0.75F;
            if (this.attackTicks == 18) {
                this.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 1.0F);
                this.shootGravityShulkerBullet(1.0F, (float) (this.getX() + forward * vecX), y, (float) (this.getZ() + forward * vecZ), 15, smallBulletSize);
                this.shootGravityShulkerBullet(1.0F, (float) (this.getX() + forward * vecX), y, (float) (this.getZ() + forward * vecZ), 45, smallBulletSize);
            }
            if (this.attackTicks == 19) {
                this.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 1.0F);
            }
        }
        if (this.getAttackState() == 14) {
            if (this.attackTicks == 22) {
                this.calculatedDash(0.2F);
                this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.5F);
            }
            if (this.attackTicks == 25) {
                this.Grab(0.0025F, 3.5F, 2.0F, 100, 5.0F);
            }
        }
        if (this.getAttackState() == 16) {
            if (this.attackTicks > 20 && this.attackTicks < 23) {
                float f1 = (this.random.nextFloat() - 0.75F) * 3.0F;
                float f2 = (this.random.nextFloat() - 0.75F) * 2.5F;
                float f3 = (this.random.nextFloat() - 0.7F) * 3.0F;
                float f4 = (this.random.nextFloat() - 0.5F) * 4.0F;
                float f5 = (this.random.nextFloat() - 0.5F) * 2.0F;
                float f6 = (this.random.nextFloat() - 0.5F) * 4.0F;
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 10; ++i) {
                        serverLevel.sendParticles(ModParticles.SHULKER_EXPLOSION.get(),
                                this.getX() + f1, this.getY() + 3.0D + f2, this.getZ() + f3, 15, 0.0D, 0.0D, 0.0D, 0.0D);
                        serverLevel.sendParticles(ModParticles.PURPLE_SHULKER_EXPLOSION.get(),
                                this.getX() + f4, this.getY() + 3.0D + f5, this.getZ() + f6, 15, 0.0D, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
            if (this.attackTicks == 22) {
                this.playSound(SoundEvents.SHULKER_SHOOT, 1.0F, 0.75F);
                if (this.targetIsNotNull()) {
                    this.shootBigShulkerBullet(this.getX(), this.getY() + 10.0D, this.getZ(),
                            this.getX(), this.getY() + 0.45D, this.getZ(), mediumBulletSize, true, this.target());
                }
            }
        }
    }

    private void teleportBehindTarget(float back, float side) {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }
        float f = Mth.cos(target.yBodyRot * ((float) Math.PI / 180));
        float f1 = Mth.sin(target.yBodyRot * ((float) Math.PI / 180));
        double theta = (double) target.yBodyRot * (Math.PI / 180) + Math.PI / 2;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        if (this.level() instanceof ServerLevel) {
            this.teleport(target.getX() + back * vecX + f * side, target.getY(), target.getZ() + back * vecZ + f1 * side);
        }
    }

    private void StraightLineAreaAttack(float rangeXZ, float range, int brokenShieldTicks, float damage, boolean launch) {
        double rad = Math.toRadians(this.getYRot() + 90.0F);
        AABB attackRange = this.getBoundingBox()
                .inflate(rangeXZ, 4.0D, rangeXZ)
                .expandTowards(range * Math.cos(rad), 0.0D, range * Math.sin(rad));
        for (LivingEntity entityHit : this.level().getEntitiesOfClass(LivingEntity.class, attackRange)) {
            if (this.isFriendlyTo(entityHit)) {
                continue;
            }
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this),
                    (float) ((damage - this.damageNerf()) * ModConfig.MOB_CONFIG.ShulkerMimicDamageMutliplier.get()));
            if (flag) {
                this.playSound(ModSounds.POSESSED_PALADIN_ATTACK3.get(), 1.0F, 0.5F);
                if (launch) {
                    this.launch(entityHit, true);
                }
            }
            if (entityHit instanceof Player player && player.isBlocking() && brokenShieldTicks > 0) {
                disableShield(player, brokenShieldTicks);
            }
        }
    }

    private void PushEntitiesAwayInFrontOf(float rangeXZ, float y, float range, int brokenShieldTicks, float damage) {
        double rad = Math.toRadians(this.getYRot() + 90.0F);
        AABB attackRange = this.getBoundingBox()
                .inflate(rangeXZ, y, rangeXZ)
                .expandTowards(range * Math.cos(rad), 0.0D, range * Math.sin(rad));
        for (LivingEntity entityHit : this.level().getEntitiesOfClass(LivingEntity.class, attackRange)) {
            if (this.isFriendlyTo(entityHit)) {
                continue;
            }
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this),
                    (float) ((damage - this.damageNerf()) * ModConfig.MOB_CONFIG.ShulkerMimicDamageMutliplier.get()));
            if (flag) {
                this.addEffect(new MobEffectInstance(ModEffects.GRAVITY_PULL.get(), 100, 2));
            }
            if (entityHit instanceof Player player && player.getAbilities().invulnerable) {
                continue;
            }
            Vec3 diff = entityHit.position().subtract(this.position()).normalize().scale(-0.06D);
            entityHit.setDeltaMovement(entityHit.getDeltaMovement().subtract(diff));
            if (entityHit instanceof Player player && player.isBlocking() && brokenShieldTicks > 0) {
                disableShield(player, brokenShieldTicks);
            }
        }
    }

    private void AreaAttack(float range, float height, float arc, float damage, int brokenShieldTicks, boolean canlaunch, boolean canStun) {
        for (LivingEntity entityHit : this.getEntityLivingBaseNearby(range, height, range, range)) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * (180.0D / Math.PI) - 90.0D) % 360.0D);
            float entityAttackingAngle = this.yBodyRot % 360.0F;
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ())
                    + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            boolean inArc = entityHitDistance <= range && entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F
                    || entityRelativeAngle >= 360.0F - arc / 2.0F || entityRelativeAngle <= -360.0F + arc / 2.0F;
            if (!inArc || this.isFriendlyTo(entityHit)) {
                continue;
            }
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this),
                    (float) ((damage - this.damageNerf()) * ModConfig.MOB_CONFIG.ShulkerMimicDamageMutliplier.get()));
            if (flag) {
                if (canStun) {
                    if (entityHit == this.getTarget()) {
                        this.playSound(SoundEvents.ANVIL_PLACE, 2.0F, 1.0F);
                    }
                    entityHit.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 80, 1));
                }
                if (canlaunch) {
                    this.launch(entityHit, true);
                }
            }
            if (entityHit instanceof Player player && player.isBlocking() && brokenShieldTicks > 0) {
                disableShield(player, brokenShieldTicks);
            }
        }
    }

    private void Grab(float rangeXZ, float range, float y, int brokenShieldTicks, float damage) {
        if (this.level().isClientSide) {
            return;
        }
        boolean hitAny = false;
        double rad = Math.toRadians(this.getYRot() + 90.0F);
        AABB attackRange = this.getBoundingBox()
                .inflate(rangeXZ, y, rangeXZ)
                .expandTowards(range * Math.cos(rad), 0.0D, range * Math.sin(rad));
        for (LivingEntity entityHit : this.level().getEntitiesOfClass(LivingEntity.class, attackRange)) {
            if (this.isFriendlyTo(entityHit)) {
                continue;
            }
            hitAny = true;
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this),
                    (float) (damage - this.damageNerf() * ModConfig.MOB_CONFIG.ShulkerMimicDamageMutliplier.get()
                            + (this.getTarget() != null ? this.getTarget().getMaxHealth() * 0.03F : 0.0F)));
            boolean mounted = entityHit == this.target() && entityHit.startRiding(this, true);
            if (flag && mounted) {
                this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.75F);
                this.succedGrabbing = true;
            } else {
                this.succedGrabbing = false;
            }
            if (entityHit instanceof Player player && player.isBlocking() && brokenShieldTicks > 0) {
                disableShield(player, brokenShieldTicks);
            }
            break;
        }
        if (!hitAny) {
            this.succedGrabbing = false;
        }
    }

    @Override
    public void SpawnDamagingBlocks(float spreadarc, int distance, float mxy, float vec, float damage, float hpdamage, float airborne) {
        double perpFacing = this.yBodyRot * (Math.PI / 180);
        double facingAngle = perpFacing + Math.PI / 2;
        int hitY = Mth.floor(this.getBoundingBox().minY - 0.5D);
        double spread = Math.PI * spreadarc;
        int arcLen = Mth.ceil(distance * spread);
        double minY = this.getY() - 1.0D;
        double maxY = this.getY() + mxy;
        for (int i = 0; i < arcLen; ++i) {
            double theta = (i / (arcLen - 1.0D) - 0.5D) * spread + facingAngle;
            double vx = Math.cos(theta);
            double vz = Math.sin(theta);
            double px = this.getX() + vx * distance + vec * Math.cos((this.yBodyRot + 90.0F) * Math.PI / 180.0D);
            double pz = this.getZ() + vz * distance + vec * Math.sin((this.yBodyRot + 90.0F) * Math.PI / 180.0D);
            int hitX = Mth.floor(px);
            int hitZ = Mth.floor(pz);
            BlockPos pos = new BlockPos(hitX, hitY, hitZ);
            BlockState block = this.level().getBlockState(pos);
            for (int depth = 0; depth < 30 && block.getRenderShape() != RenderShape.MODEL; ++depth) {
                pos = pos.below();
                block = this.level().getBlockState(pos);
            }
            if (block.getRenderShape() != RenderShape.MODEL) {
                block = Blocks.AIR.defaultBlockState();
            }
            LMFallingBlockEntity fallingBlockEntity = new LMFallingBlockEntity(this.level(), hitX + 0.5D, hitY + 1.0D, hitZ + 0.5D, block, 10);
            fallingBlockEntity.push(0.0D, 0.2D + this.getRandom().nextGaussian() * 0.15D, 0.0D);
            this.level().addFreshEntity(fallingBlockEntity);
            this.level().addAlwaysVisibleParticle(ModParticles.SHULKER_EXPLOSION.get(), hitX + 0.5D, hitY + 2.0D, hitZ + 0.5D, 0.0D, 0.0D, 0.0D);
            AABB selection = new AABB(px - 0.5D, minY, pz - 0.5D, px + 0.5D, maxY, pz + 0.5D);
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, selection)) {
                if (this.isFriendlyTo(entity)) {
                    continue;
                }
                if (entity.hurt(this.level().damageSources().mobAttack(this),
                        11.0F - this.damageNerf() * damage + Math.min(11.0F * damage, entity.getMaxHealth() * hpdamage))) {
                    entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, airborne * distance + this.level().random.nextDouble() * 0.15D, 0.0D));
                }
            }
        }
    }

    public boolean teleport(double x, double y, double z) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(x, y, z);
        while (mutablePos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(mutablePos).blocksMotion()) {
            mutablePos.move(Direction.DOWN);
        }
        if (!this.level().getBlockState(mutablePos).blocksMotion()) {
            return false;
        }
        EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
        if (event.isCanceled()) {
            return false;
        }
        Vec3 oldPos = this.position();
        if (this.teleportBoolean(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
            this.level().gameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Context.of(this));
            if (!this.isSilent()) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.1F, 5, 5);
                this.playSound(SoundEvents.SHULKER_TELEPORT, 4.0F, 1.0F);
            }
            return true;
        }
        return false;
    }

    private boolean teleportBoolean(double x, double y, double z, boolean broadcast) {
        double oldX = this.getX();
        double oldY = this.getY();
        double oldZ = this.getZ();
        double newY = y;
        boolean success = false;
        BlockPos blockpos = BlockPos.containing(x, y, z);
        Level level = this.level();
        if (level.hasChunkAt(blockpos)) {
            boolean foundGround = false;
            while (!foundGround && blockpos.getY() > level.getMinBuildHeight()) {
                BlockPos below = blockpos.below();
                if (level.getBlockState(below).blocksMotion()) {
                    foundGround = true;
                } else {
                    newY -= 1.0D;
                    blockpos = below;
                }
            }
            if (foundGround) {
                this.teleportTo(x, newY, z);
                EntityUtil.applyServerTeleport(this);
                if (level.noCollision(this) && !level.containsAnyLiquid(this.getBoundingBox())) {
                    success = true;
                }
            }
        }
        if (!success) {
            this.teleportTo(oldX, oldY, oldZ);
            EntityUtil.applyServerTeleport(this);
            return false;
        }
        if (broadcast) {
            level.broadcastEntityEvent(this, (byte) 46);
        }
        this.getNavigation().stop();
        return true;
    }

    public AnimationState getAnimationState(String input) {
        if (input.equals("idle")) {
            return this.idleAnimationState;
        }
        if (input.equals("bite")) {
            return this.biteAnimationState;
        }
        if (input.equals("backstep_charge")) {
            return this.backstepDashAnimationState;
        }
        if (input.equals("backstep")) {
            return this.backstepAnimationState;
        }
        if (input.equals("triple_shoot")) {
            return this.tripleShootAnimationState;
        }
        if (input.equals("triple_shoot_tp1")) {
            return this.tripleShootTp1AnimationState;
        }
        if (input.equals("triple_shoot_tp2")) {
            return this.tripleShootTp2AnimationState;
        }
        if (input.equals("pull_hit")) {
            return this.pullHitAnimationState;
        }
        if (input.equals("double_slash")) {
            return this.doubleSlashAnimationState;
        }
        if (input.equals("big_shoot")) {
            return this.bigShootAnimationState;
        }
        if (input.equals("double_shoot")) {
            return this.doubleShootAnimationState;
        }
        if (input.equals("death")) {
            return this.deathAnimationState;
        }
        if (input.equals("firework_grab_pre")) {
            return this.fireworkGrabPreAnimationState;
        }
        if (input.equals("firework_grab_fail")) {
            return this.fireworkGrabFailAnimationState;
        }
        if (input.equals("firework_grab_success")) {
            return this.fireworkGrabSuccessAnimationState;
        }
        return new AnimationState();
    }

    public void stopAllAnimationStates() {
        this.idleAnimationState.stop();
        this.biteAnimationState.stop();
        this.backstepDashAnimationState.stop();
        this.backstepAnimationState.stop();
        this.tripleShootAnimationState.stop();
        this.tripleShootTp1AnimationState.stop();
        this.tripleShootTp2AnimationState.stop();
        this.doubleSlashAnimationState.stop();
        this.pullHitAnimationState.stop();
        this.bigShootAnimationState.stop();
        this.doubleShootAnimationState.stop();
        this.deathAnimationState.stop();
        this.fireworkGrabPreAnimationState.stop();
        this.fireworkGrabFailAnimationState.stop();
        this.fireworkGrabSuccessAnimationState.stop();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ATTACK_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.getAttackState()) {
                case 0 -> this.stopAllAnimationStates();
                case 3 -> this.startAnimation(this.biteAnimationState);
                case 4 -> this.startAnimation(this.backstepDashAnimationState);
                case 5 -> this.startAnimation(this.backstepAnimationState);
                case 6 -> this.startAnimation(this.tripleShootAnimationState);
                case 7 -> this.startAnimation(this.pullHitAnimationState);
                case 8 -> this.startAnimation(this.doubleSlashAnimationState);
                case 9 -> this.startAnimation(this.tripleShootTp1AnimationState);
                case 10 -> this.startAnimation(this.tripleShootTp2AnimationState);
                case 11 -> this.startAnimation(this.bigShootAnimationState);
                case 12 -> this.startAnimation(this.doubleShootAnimationState);
                case 13 -> this.startAnimation(this.deathAnimationState);
                case 14 -> this.startAnimation(this.fireworkGrabPreAnimationState);
                case 15 -> this.startAnimation(this.fireworkGrabFailAnimationState);
                case 16 -> this.startAnimation(this.fireworkGrabSuccessAnimationState);
                default -> {
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    private void startAnimation(AnimationState state) {
        this.stopAllAnimationStates();
        state.startIfStopped(this.tickCount);
    }

    public enum Crackiness {
        NONE(1.0F),
        LOW(0.75F),
        MEDIUM(0.5F),
        HIGH(0.25F);

        private static final List<Crackiness> BY_DAMAGE;
        public final float fraction;

        Crackiness(float fraction) {
            this.fraction = fraction;
        }

        public static Crackiness byFraction(float fraction) {
            for (Crackiness crackiness : BY_DAMAGE) {
                if (fraction < crackiness.fraction) {
                    return crackiness;
                }
            }
            return NONE;
        }

        static {
            BY_DAMAGE = Stream.of(values()).sorted(Comparator.comparingDouble(c -> c.fraction)).toList();
        }
    }
}
