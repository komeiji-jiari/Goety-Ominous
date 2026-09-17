package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.utils.CroneCuriosUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class BoggedServant extends AbstractSkeletonServant implements Shearable {

    private static final EntityDataAccessor<Boolean> DATA_SHEARED =
            SynchedEntityData.defineId(BoggedServant.class, EntityDataSerializers.BOOLEAN);

    private boolean lastNecroCape;
    private static final int REGROW_DELAY = 12000;
    private int regrowCooldown;

    public BoggedServant(EntityType<? extends AbstractSkeletonServant> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.BoggedServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.BoggedServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BoggedServantDamage.get());
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.BoggedServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.BoggedServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.BoggedServantDamage.get());
    }

    public boolean hasNecroCapeOwner() {
        LivingEntity owner = this.getTrueOwner();
        return owner != null && CuriosFinder.hasNecroCape(owner);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        if (this.tickCount % 20 == 0) {
            boolean cape = this.hasNecroCapeOwner();
            if (cape != this.lastNecroCape) {
                this.lastNecroCape = cape;
                this.reassessWeaponGoal();
            }
        }
        if (this.isSheared() && this.tickCount % 20 == 0) {
            if (this.regrowCooldown > 0) {
                this.regrowCooldown -= 20;
                if (this.regrowCooldown < 0) {
                    this.regrowCooldown = 0;
                }
            } else if (this.random.nextFloat() < 0.15F) {
                this.setSheared(false);
                this.level().playSound(null, this, SoundEvents.BONE_MEAL_USE,
                        SoundSource.PLAYERS, 0.8F, 1.2F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.getX(), this.getY() + (double) this.getBbHeight() * 0.5D, this.getZ(),
                            8,
                            (double) this.getBbWidth() * 0.5D,
                            (double) this.getBbHeight() * 0.4D,
                            (double) this.getBbWidth() * 0.5D,
                            0.0D);
                }
            }
        }
    }

    @Override
    public double getBaseRangeDamage() {
        return AttributesConfig.BoggedServantRangeDamage.get();
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
        tag.putInt("RegrowCooldown", this.regrowCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSheared(tag.getBoolean("sheared"));
        this.regrowCooldown = tag.getInt("RegrowCooldown");
    }

    public boolean isSheared() {
        return this.entityData.get(DATA_SHEARED);
    }

    public void setSheared(boolean sheared) {
        this.entityData.set(DATA_SHEARED, sheared);
        this.regrowCooldown = sheared ? REGROW_DELAY : 0;
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
                        ? Items.BROWN_MUSHROOM : Items.RED_MUSHROOM));
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
        if (stack.is(Items.BONE_MEAL) && this.isSheared() && this.getTrueOwner() == player) {
            if (!this.level().isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.setSheared(false);
                this.level().playSound(null, this, SoundEvents.BONE_MEAL_USE,
                        SoundSource.PLAYERS, 1.0F, 1.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.getX(), this.getY() + (double) this.getBbHeight() * 0.5D, this.getZ(),
                            8,
                            (double) this.getBbWidth() * 0.5D,
                            (double) this.getBbHeight() * 0.4D,
                            (double) this.getBbWidth() * 0.5D,
                            0.0D);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected AbstractArrow getMobArrow(ItemStack arrowStack, float velocity) {
        AbstractArrow arrow = super.getMobArrow(arrowStack, velocity);
        if (arrow instanceof Arrow a) {
            a.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                    MathHelper.secondsToTicks(5), 0, false, false));
            MobEffect poison = MobEffects.POISON;
            LivingEntity owner = this.getTrueOwner();
            if (owner != null && CroneCuriosUtil.hasCroneRobe(owner)) {
                poison = GoetyEffects.ACID_VENOM.get();
            }
            a.addEffect(new MobEffectInstance(poison, MathHelper.secondsToTicks(5), 0));
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
        return ModSounds.BOGGED_SERVANT_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BOGGED_SERVANT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BOGGED_SERVANT_DEATH.get();
    }

    @Override
    protected SoundEvent getStepSound() {
        return ModSounds.BOGGED_SERVANT_STEP.get();
    }
}
