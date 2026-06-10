package com.qiuyue.someillagerservants.common.entities.ally.mobs;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.someillagerservants.config.AttributesConfig;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class IllashooterServant extends MagiServant {
    private static final EntityDataAccessor<Boolean> ATTACKING;
    private int attackTicks;

    public IllashooterServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new AttackGoal());
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.IllashooterServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.IllashooterServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.IllashooterServantDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.IllashooterServantFollowRange.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH),
                AttributesConfig.IllashooterServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.IllashooterServantDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.IllashooterServantMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE),
                AttributesConfig.IllashooterServantFollowRange.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
    }

    public SoundEvent getCelebrateSound() {
        return null;
    }

    public boolean causeFallDamage(float p_147187_, float p_147188_, DamageSource p_147189_) {
        return false;
    }

    public void tick() {
        if (this.isAlive()) {
            if (this.isAttacking()) {
                ++this.attackTicks;
                if (this.attackTicks > 30) {
                    if (this.getTarget() != null) {
                        this.playSound(SoundEvents.DISPENSER_LAUNCH, 1.0F, 1.0F);
                        this.fireArrow(this.getTarget(), 1.0F, 1.0F);
                    }

                    this.attackTicks = 0;
                }
            } else {
                this.attackTicks = 0;
            }
        }

        super.tick();
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundEvents.ZOMBIE_ATTACK_IRON_DOOR;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_ATTACK_IRON_DOOR;
    }

    public void fireArrow(LivingEntity p_82196_1_, float p_82196_2_, float inaccuracy) {
        AbstractArrow abstractarrowentity = this.getArrow(Items.BOW.getDefaultInstance(), p_82196_2_);
        if (this.getMainHandItem().getItem() instanceof BowItem) {
            abstractarrowentity = ((BowItem) this.getMainHandItem().getItem()).customArrow(abstractarrowentity);
        }

        double d0 = p_82196_1_.getX() - this.getX();
        double d1 = p_82196_1_.getY(0.3333333333333333) - abstractarrowentity.getY();
        double d2 = p_82196_1_.getZ() - this.getZ();
        double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
        abstractarrowentity.setBaseDamage(1.0);
        abstractarrowentity.shoot(d0, d1 + d3 * 0.20000000298023224, d2, 1.6F, inaccuracy);
        this.level().addFreshEntity(abstractarrowentity);
    }

    protected AbstractArrow getArrow(ItemStack p_213624_1_, float p_213624_2_) {
        return new MagiArrow(this.level(), this);
    }

    public void die(DamageSource p_70645_1_) {
        super.die(p_70645_1_);
        if (p_70645_1_.getEntity() instanceof Mob && !(p_70645_1_.getEntity() instanceof Raider)
                && this.getOwner() != null && ((Mob) p_70645_1_.getEntity()).getTarget() == this) {
            ((Mob) p_70645_1_.getEntity()).setTarget(this.getOwner());
        }

    }

    static {
        ATTACKING = SynchedEntityData.defineId(IllashooterServant.class, EntityDataSerializers.BOOLEAN);
    }

    class AttackGoal extends Goal {
        public AttackGoal() {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        }

        public boolean canUse() {
            return IllashooterServant.this.getTarget() != null && IllashooterServant.this.getTarget().isAlive()
                    && IllashooterServant.this.distanceToSqr(IllashooterServant.this.getTarget()) < 90.0
                    && IllashooterServant.this.hasLineOfSight(IllashooterServant.this.getTarget());
        }

        public void start() {
            IllashooterServant.this.playSound(SoundEvents.PISTON_EXTEND, 1.0F, 1.5F);
            IllashooterServant.this.setAttacking(true);
        }

        public boolean canContinueToUse() {
            return IllashooterServant.this.getTarget() != null
                    && IllashooterServant.this.distanceToSqr(IllashooterServant.this.getTarget()) < 90.0
                    && IllashooterServant.this.getTarget().isAlive()
                    && IllashooterServant.this.hasLineOfSight(IllashooterServant.this.getTarget());
        }

        public void tick() {
            IllashooterServant.this.getNavigation().stop();
            if (IllashooterServant.this.getTarget() != null) {
                IllashooterServant.this.getLookControl().setLookAt(IllashooterServant.this.getTarget(), 30.0F, 30.0F);
            }

            IllashooterServant.this.navigation.stop();
        }

        public void stop() {
            IllashooterServant.this.setAttacking(false);
            IllashooterServant.this.playSound(SoundEvents.PISTON_CONTRACT, 1.0F, 1.5F);
        }
    }
}