package com.qiuyue.goetyominous.common.entities.hostile;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.util.BoggedLike;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class BoggedEntity extends AbstractSkeleton implements ICustomAttributes, BoggedLike {

    private static final int HARD_ATTACK_INTERVAL = 50;
    private static final int NORMAL_ATTACK_INTERVAL = 70;
    private static final int POISON_DURATION = MathHelper.secondsToTicks(4);

    private static final EntityDataAccessor<Boolean> DATA_SHEARED =
            SynchedEntityData.defineId(BoggedEntity.class, EntityDataSerializers.BOOLEAN);

    private RangedBowAttackGoal<BoggedEntity> bowGoal;
    private MeleeAttackGoal meleeGoal;

    public BoggedEntity(EntityType<? extends AbstractSkeleton> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.BoggedHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.BoggedArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BoggedDamage.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BoggedHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.BoggedArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.BoggedDamage.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SHEARED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("sheared", this.isSheared());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSheared(tag.getBoolean("sheared"));
    }

    @Override
    public boolean isSheared() {
        return this.entityData.get(DATA_SHEARED);
    }

    @Override
    public void setSheared(boolean sheared) {
        this.entityData.set(DATA_SHEARED, sheared);
    }

    @Override
    public boolean readyForShearing() {
        return !this.isSheared() && this.isAlive();
    }

    @Override
    public void shear(SoundSource source) {
        this.level().playSound(null, this, SoundEvents.SHEEP_SHEAR, source, 1.0F, 1.0F);
        if (!this.level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                this.spawnAtLocation(new ItemStack(this.random.nextBoolean()
                        ? Items.RED_MUSHROOM : Items.BROWN_MUSHROOM));
            }
        }
        this.setSheared(true);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.SHEARS) && this.readyForShearing()) {
            this.shear(SoundSource.PLAYERS);
            this.gameEvent(GameEvent.SHEAR, player);
            if (!this.level().isClientSide) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void reassessWeaponGoal() {
        if (this.level() == null || this.level().isClientSide) {
            return;
        }
        this.goalSelector.removeAllGoals(goal ->
                goal instanceof RangedBowAttackGoal || goal instanceof MeleeAttackGoal);
        int interval = this.level().getDifficulty() == Difficulty.HARD
                ? HARD_ATTACK_INTERVAL : NORMAL_ATTACK_INTERVAL;
        ItemStack stack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this,
                item -> item instanceof BowItem));
        if (stack.is(Items.BOW)) {
            if (this.bowGoal == null) {
                this.bowGoal = new RangedBowAttackGoal<>(this, 1.0D, interval, 15.0F);
            } else {
                this.bowGoal.setMinAttackInterval(interval);
            }
            this.goalSelector.addGoal(4, this.bowGoal);
        } else {
            if (this.meleeGoal == null) {
                this.meleeGoal = new MeleeAttackGoal(this, 1.2D, false) {
                    @Override
                    public void start() {
                        super.start();
                        BoggedEntity.this.setAggressive(true);
                    }

                    @Override
                    public void stop() {
                        super.stop();
                        BoggedEntity.this.setAggressive(false);
                    }
                };
            }
            this.goalSelector.addGoal(4, this.meleeGoal);
        }
    }

    @Override
    protected AbstractArrow getArrow(ItemStack arrowStack, float velocity) {
        AbstractArrow arrow = super.getArrow(arrowStack, velocity);
        if (arrow instanceof Arrow a) {
            a.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION));
        }
        return arrow;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            amount *= 0.75F;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.BOGGED_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BOGGED_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BOGGED_DEATH.get();
    }

    @Override
    protected SoundEvent getStepSound() {
        return ModSounds.BOGGED_STEP.get();
    }
}
