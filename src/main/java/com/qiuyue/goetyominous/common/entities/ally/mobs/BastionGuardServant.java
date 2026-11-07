package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.api.entities.IShielded;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.SmashParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class BastionGuardServant extends AbstractPiglinServant implements IShielded {
    public static final int ANIM_IDLE = 0;
    public static final int ANIM_ATTACK_SHIELD = 1;
    public static final int ANIM_ATTACK_NO_SHIELD = 2;
    public static final int ANIM_COMBO = 3;

    private static final EntityDataAccessor<Boolean> HAS_SHIELD = SynchedEntityData.defineId(
            BastionGuardServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(
            BastionGuardServant.class, EntityDataSerializers.INT);

    private static final int SHIELD_MAX = 30;
    private static final float SHIELD_DAMAGE_CAP = 10.0F;
    private static final int STUN_TICKS = 21;
    private static final int ATTACK_RECOVERY = 10;
    private static final int COMBO_COOLDOWN = 300;
    private static final int RAMPAGE_DURATION = 100;
    private static final int RAMPAGE_REFRESH = 60;
    private static final int RAMPAGE_COLOR = 0xFF8C00;

    private int shieldHealth = SHIELD_MAX;
    private int attackTick;
    private int attackCoolTick;
    private int comboCoolTick;
    private int stunTicks;
    private boolean enraged;
    private boolean lastShieldClient = true;
    private int shieldBreakTicks;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState noShieldIdleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState attackNoShieldAnimationState = new AnimationState();
    public final AnimationState comboAnimationState = new AnimationState();
    public final AnimationState shieldBreakAnimationState = new AnimationState();

    public BastionGuardServant(EntityType<? extends AbstractPiglinServant> type, Level level) {
        super(type, level);
        this.xpReward = 15;
        this.setImmuneToZombification(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.BastionGuardServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.BastionGuardServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.BastionGuardServantArmorToughness.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BastionGuardServantAttackDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BastionGuardServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.BastionGuardServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS), AttributesConfig.BastionGuardServantArmorToughness.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED), 0.25D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.BastionGuardServantAttackDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.KNOCKBACK_RESISTANCE), 1.0D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), 24.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SHIELD, true);
        this.entityData.define(ANIM_STATE, ANIM_IDLE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addShieldedData(tag);
        tag.putInt("AttackCool", this.attackCoolTick);
        tag.putInt("ComboCool", this.comboCoolTick);
        tag.putInt("StunTicks", this.stunTicks);
        tag.putBoolean("Enraged", this.enraged);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readShieldedData(tag);
        this.attackCoolTick = tag.getInt("AttackCool");
        this.comboCoolTick = tag.getInt("ComboCool");
        this.stunTicks = tag.getInt("StunTicks");
        this.enraged = tag.getBoolean("Enraged");
        this.setConfigurableAttributes();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new BastionGuardMeleeGoal(this));
    }

    @Override
    protected boolean canHunt() {
        return false;
    }

    @Override
    public PiglinArmPose getArmPose() {
        return this.isAggressive() ? PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON : PiglinArmPose.DEFAULT;
    }

    @Override
    protected void playConvertedSound() {
        this.playSound(SoundEvents.PIGLIN_BRUTE_CONVERTED_TO_ZOMBIFIED, 1.0F, this.getVoicePitch());
    }

    @Override
    protected boolean canBarter() {
        return false;
    }

    @Override
    public boolean canWearArmor() {
        return false;
    }

    @Override
    public boolean canHaveWeapon() {
        return false;
    }

    @Override
    public void populateDefaultWeapons(RandomSource random, DifficultyInstance difficulty) {
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PIGLIN_BRUTE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PIGLIN_BRUTE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PIGLIN_BRUTE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ModSounds.ROYAL_GUARD_STEP.get(), 0.15F, 1.0F);
    }

    @Override
    public boolean hasShield() {
        return this.entityData.get(HAS_SHIELD);
    }

    @Override
    public void setShield(boolean shield) {
        this.entityData.set(HAS_SHIELD, shield);
    }

    @Override
    public int getShieldHealth() {
        return this.shieldHealth;
    }

    @Override
    public void setShieldHealth(int health) {
        this.shieldHealth = Math.max(0, Math.min(health, SHIELD_MAX));
    }

    @Override
    public int getMaxShieldHealth() {
        return SHIELD_MAX;
    }

    @Override
    public boolean hitBaseShield() {
        return false;
    }

    @Override
    public void destroyShield(DamageSource source, float amount) {
        IShielded.super.destroyShield(source, Math.min(amount, SHIELD_DAMAGE_CAP));
    }

    @Override
    public ItemStack getShieldMaterial() {
        return new ItemStack(Items.SHIELD);
    }

    @Override
    public boolean isShieldRepair(ItemStack stack) {
        return stack.is(Items.NETHERITE_SCRAP);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.getTrueOwner() != null && player.getUUID().equals(this.getTrueOwner().getUUID())
                && this.getShieldHealth() < this.getMaxShieldHealth()
                && this.isShieldRepair(stack)
                && this.getTarget() == null && this.hurtTime <= 0) {
            return this.repairShield(player, stack);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_FALL) || source.is(DamageTypeTags.IS_EXPLOSION)) {
            return false;
        }
        float reduced = amount;
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            reduced *= 0.7F;
        }
        float finalAmount = reduced;
        boolean hadShield = this.hasShield();
        boolean result = this.hurtShielded(source, finalAmount, () -> super.hurt(source, finalAmount));
        if (!this.level().isClientSide && hadShield && !this.hasShield()) {
            this.onShieldBroken();
        }
        return result;
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (!this.hasShield()) {
            super.knockback(strength, x, z);
        }
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == 4 || event == 5 || event == 6) {
            this.handleShieldedEvent(event);
        } else {
            super.handleEntityEvent(event);
        }
    }

    @Override
    public void animateAttack() {
        this.setAnimationState(this.hasShield() ? ANIM_ATTACK_SHIELD : ANIM_ATTACK_NO_SHIELD);
    }

    @Override
    public boolean isMeleeAttacking() {
        return this.getCurrentAnimation() != ANIM_IDLE;
    }

    @Override
    public void setMeleeAttacking(boolean attacking) {
        if (!attacking) {
            this.setAnimationState(ANIM_IDLE);
        }
    }

    @Override
    public int getAttackTick() {
        return this.attackTick;
    }

    @Override
    public void setAttackTick(int tick) {
        this.attackTick = tick;
    }

    public void setAnimationState(int state) {
        this.entityData.set(ANIM_STATE, state);
    }

    public int getCurrentAnimation() {
        return this.entityData.get(ANIM_STATE);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor) && this.level().isClientSide) {
            this.stopAttackAnimations();
            switch (this.getCurrentAnimation()) {
                case ANIM_ATTACK_SHIELD -> this.attackAnimationState.start(this.tickCount);
                case ANIM_ATTACK_NO_SHIELD -> this.attackNoShieldAnimationState.start(this.tickCount);
                case ANIM_COMBO -> this.comboAnimationState.start(this.tickCount);
                default -> {
                    if (this.hasShield()) {
                        this.idleAnimationState.startIfStopped(this.tickCount);
                    } else {
                        this.noShieldIdleAnimationState.startIfStopped(this.tickCount);
                    }
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    private void stopAttackAnimations() {
        this.attackAnimationState.stop();
        this.attackNoShieldAnimationState.stop();
        this.comboAnimationState.stop();
    }

    public boolean isBastionStunned() {
        return this.stunTicks > 0 || this.hasEffect(GoetyEffects.STUNNED.get());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            boolean shielded = this.hasShield();
            if (!shielded && this.lastShieldClient) {
                this.stopAttackAnimations();
                this.shieldBreakAnimationState.start(this.tickCount);
                this.shieldBreakTicks = 0;
            }
            if (shielded && !this.lastShieldClient) {
                this.shieldBreakAnimationState.stop();
            }
            this.lastShieldClient = shielded;
            if (!shielded && this.shieldBreakAnimationState.isStarted()) {
                ++this.shieldBreakTicks;
                if (this.shieldBreakTicks >= STUN_TICKS) {
                    this.shieldBreakAnimationState.stop();
                }
            }
            this.idleAnimationState.animateWhen(!this.isDeadOrDying(), this.tickCount);
            this.noShieldIdleAnimationState.animateWhen(!this.isDeadOrDying(), this.tickCount);
        } else {
            if (this.attackCoolTick > 0) {
                --this.attackCoolTick;
            }
            if (this.comboCoolTick > 0) {
                --this.comboCoolTick;
            }
            if (this.stunTicks > 0) {
                --this.stunTicks;
                this.getNavigation().stop();
                Vec3 motion = this.getDeltaMovement();
                this.setDeltaMovement(0.0D, motion.y, 0.0D);
                if (this.stunTicks == 0 && !this.hasShield()) {
                    this.enraged = true;
                    this.applyRampage(true);
                }
            }
            if (this.enraged) {
                if (this.hasShield()) {
                    this.enraged = false;
                } else {
                    MobEffectInstance effect = this.getEffect(GoetyEffects.RAMPAGE.get());
                    if (effect == null) {
                        this.applyRampage(true);
                    } else if (effect.getDuration() <= RAMPAGE_REFRESH) {
                        this.applyRampage(false);
                    }
                }
            }
        }
    }

    private void onShieldBroken() {
        this.setTarget(null);
        this.getNavigation().stop();
        this.attackCoolTick = 0;
        this.enraged = false;
        this.stunTicks = STUN_TICKS;
        this.addEffect(new MobEffectInstance(GoetyEffects.STUNNED.get(), STUN_TICKS, 0, false, false, false), this);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticleTypes.STUN.get(),
                    this.getX(), this.getY() + this.getBbHeight() + 0.5D, this.getZ(),
                    1, 0.0D, 0.0D, 0.0D, 0.5D);
        }
    }

    private void applyRampage(boolean sound) {
        this.addEffect(new MobEffectInstance(GoetyEffects.RAMPAGE.get(), RAMPAGE_DURATION, 0, false, true, true), this);
        if (sound) {
            this.playSound(SoundEvents.PIGLIN_BRUTE_ANGRY, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    private static class BastionGuardMeleeGoal extends Goal {
        private final BastionGuardServant mob;
        private LivingEntity target;
        private int attackTime = -1;
        private int endTick;
        private boolean combo;
        private boolean hit1;
        private boolean hit2;
        private boolean hit3;

        BastionGuardMeleeGoal(BastionGuardServant mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && target.isAlive()
                    && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)
                    && !this.mob.isCommanded()
                    && !this.mob.isBastionStunned()
                    && this.mob.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            return this.target != null && this.target.isAlive()
                    && !this.mob.isBastionStunned()
                    && !this.mob.isCommanded()
                    && this.mob.hasLineOfSight(this.target);
        }

        @Override
        public void start() {
            this.target = this.mob.getTarget();
            this.attackTime = -1;
            this.endTick = 0;
            this.combo = false;
            this.hit1 = false;
            this.hit2 = false;
            this.hit3 = false;
            this.mob.getNavigation().stop();
        }

        @Override
        public void stop() {
            this.mob.setAnimationState(ANIM_IDLE);
            this.mob.getNavigation().stop();
            this.attackTime = -1;
            this.combo = false;
            this.target = null;
            if (this.mob.getTarget() == null) {
                this.mob.setAggressive(false);
            }
        }

        @Override
        public void tick() {
            if (this.target == null) {
                return;
            }
            if (this.mob.isBastionStunned()) {
                this.mob.getNavigation().stop();
                return;
            }
            MobUtil.instaLook(this.mob, this.target);
            if (this.attackTime >= 0) {
                this.mob.getNavigation().stop();
                this.mob.getMoveControl().strafe(0.0F, 0.0F);
                ++this.attackTime;
                this.mob.setAttackTick(this.attackTime);
                if (this.combo) {
                    this.tickCombo();
                } else {
                    this.tickNormal();
                }
                return;
            }
            double distance = this.mob.distanceTo(this.target);
            if (this.mob.attackCoolTick > 0) {
                if (distance > 3.0D) {
                    this.mob.getNavigation().moveTo(this.target, 1.25D);
                }
                return;
            }
            if (distance > 3.0D) {
                this.mob.getNavigation().moveTo(this.target, 1.25D);
                return;
            }
            this.mob.getNavigation().stop();
            this.startAttack();
        }

        private void tickNormal() {
            int hitTick = this.mob.hasShield() ? 17 : 15;
            if (!this.hit1 && this.attackTime >= hitTick) {
                this.hit1 = true;
                this.fanAttack(this.getAttackDamage());
            }
            if (this.attackTime >= (this.mob.hasShield() ? 33 : 30)) {
                this.finishAttack();
            }
        }

        private void tickCombo() {
            if (!this.hit1 && this.attackTime >= 17) {
                this.hit1 = true;
                this.fanAttack(this.getAttackDamage());
            }
            if (this.attackTime == 26) {
                MobUtil.instaLook(this.mob, this.target);
            }
            if (!this.hit2 && this.attackTime >= 27) {
                this.hit2 = true;
                this.fanAttack(this.getAttackDamage() * 10.0F / 12.0F);
            }
            if (this.attackTime == 28) {
                this.lunge();
            }
            if (this.attackTime == 46) {
                MobUtil.instaLook(this.mob, this.target);
                this.lunge();
            }
            if (!this.hit3 && this.attackTime >= 47) {
                this.hit3 = true;
                this.squareAttack(this.getAttackDamage() * 18.0F / 12.0F);
            }
            if (this.attackTime >= 66) {
                this.finishAttack();
            }
        }

        private void startAttack() {
            this.combo = !this.mob.hasShield() && this.mob.comboCoolTick <= 0;
            this.attackTime = 0;
            this.hit1 = false;
            this.hit2 = false;
            this.hit3 = false;
            this.mob.setAttackTick(0);
            if (this.combo) {
                this.mob.setAnimationState(ANIM_COMBO);
                this.mob.comboCoolTick = COMBO_COOLDOWN;
            } else if (this.mob.hasShield()) {
                this.mob.setAnimationState(ANIM_ATTACK_SHIELD);
            } else {
                this.mob.setAnimationState(ANIM_ATTACK_NO_SHIELD);
            }
        }

        private void finishAttack() {
            this.attackTime = -1;
            this.combo = false;
            this.mob.attackCoolTick = ATTACK_RECOVERY;
            this.mob.setAnimationState(ANIM_IDLE);
            this.mob.getNavigation().stop();
        }

        private float getAttackDamage() {
            return (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
        }

        private void fanAttack(float damage) {
            for (LivingEntity candidate : MobUtil.getAttackableLivingEntitiesNearby(this.mob, 3.0D, 3.0D, 3.0D, 3.0D)) {
                if (candidate == this.mob || MobUtil.areAllies(this.mob, candidate)) {
                    continue;
                }
                float angle = MobUtil.getRelativeAngle(this.mob, candidate);
                if (Math.abs(angle) > 45.0F && angle < 315.0F && angle > -315.0F) {
                    continue;
                }
                this.hurtTarget(candidate, damage);
            }
        }

        private void squareAttack(float damage) {
            Vec3 look = this.mob.getLookAngle();
            double x = this.mob.getX() + look.x * 1.5D;
            double z = this.mob.getZ() + look.z * 1.5D;
            AABB area = MobUtil.makeAttackRange(x, this.mob.getY(), z, 3.0D, 3.0D, 3.0D);
            for (LivingEntity candidate : this.mob.level().getEntitiesOfClass(LivingEntity.class, area)) {
                this.hurtTarget(candidate, damage);
            }
            this.particles(x, this.mob.getY(), z);
            this.smashSound();
        }

        private void hurtTarget(LivingEntity target, float damage) {
            if (target == this.mob || !target.isAlive()
                    || !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)
                    || MobUtil.areAllies(this.mob, target)) {
                return;
            }
            if (target.hurt(this.mob.getServantAttack(), damage)) {
                target.knockback(0.4D, this.mob.getX() - target.getX(), this.mob.getZ() - target.getZ());
                if (target instanceof Player player && player.isBlocking()) {
                    player.disableShield(true);
                }
            }
        }

        private void lunge() {
            LivingEntity target = this.mob.getTarget();
            if (target == null) {
                return;
            }
            Vec3 delta = target.position().subtract(this.mob.position());
            Vec3 horizontal = new Vec3(delta.x, 0.0D, delta.z);
            if (horizontal.lengthSqr() < 1.0E-4D) {
                return;
            }
            this.mob.move(MoverType.SELF, horizontal.normalize().scale(0.5D));
        }

        private void smashSound() {
            this.mob.playSound(ModSounds.BLACKGUARD_SMASH.get(), 1.0F, this.mob.getVoicePitch());
        }

        private void particles(double x, double y, double z) {
            if (!(this.mob.level() instanceof ServerLevel serverLevel)) {
                return;
            }
            double groundY = BlockFinder.findGroundY(serverLevel, x, y, z);
            BlockPos pos = BlockPos.containing(x, groundY - 1.0D, z);
            BlockState state = serverLevel.getBlockState(pos);
            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, state);
            for (int i = 0; i < 2; ++i) {
                ServerParticleUtil.circularParticles(serverLevel, option, x, groundY + 0.25D, z, 1.5F);
            }
            ColorUtil color = new ColorUtil(RAMPAGE_COLOR);
            serverLevel.sendParticles(new SmashParticleOption(color.red(), color.green(), color.blue(), 1.5F, 10),
                    x, groundY, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
