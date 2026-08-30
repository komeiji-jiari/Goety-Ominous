package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.alexander.mutantmore.config.mutant_shulker.MutantShulkerRewardsCommonConfig;
import com.alexander.mutantmore.entities.MutantShulkerBullet;
import com.alexander.mutantmore.events.ShakeCameraEvent;
import com.alexander.mutantmore.init.EffectInit;
import com.alexander.mutantmore.init.EntityTypeInit;
import com.alexander.mutantmore.init.ItemInit;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.init.SoundEventInit;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class MutantShulkerServantTrap extends Summoned {
    protected static final EntityDataAccessor<Boolean> SPAWNED_BY_MUTANT_SHULKER = SynchedEntityData.defineId(MutantShulkerServantTrap.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Byte> COLOR_ID = SynchedEntityData.defineId(MutantShulkerServantTrap.class, EntityDataSerializers.BYTE);
    public final AnimationState idleAnimation = new AnimationState();
    public final AnimationState activateAnimation = new AnimationState();
    public final AnimationState vanishAnimation = new AnimationState();
    public int activateAnimationTick;
    public int activateAnimationLength = 20;
    public int activateActionPoint = 16;
    public int vanishAnimationTick;
    public int vanishAnimationLength = 30;
    public int lifeTime;
    public int snapCooldownTick;
    public int snapCooldownLength = 40;
    // 潜影贝召唤的陷阱硬寿命上限:即使 trap_turns_to_bullet 配置被关闭(或 multiuse 开启),
    // 陷阱也会在 lifeTime 达到该值后强制播消散动画并移除,不会永久驻留。
    private static final int MAX_LIFETIME_TICKS = 600; // 30 秒

    private final Predicate<Entity> SNAPPABLE = target -> this.isHostileTarget(target)
            && target instanceof LivingEntity && target.isAlive()
            && !target.isInvulnerable() && !target.isSpectator()
            && (!(target instanceof Player) || !((Player) target).isCreative());

    private final Predicate<Entity> HURTABLE = target -> this.isHostileTarget(target)
            && target instanceof LivingEntity && target.isAlive()
            && !target.isInvulnerable() && !target.isSpectator()
            && (!(target instanceof Player) || !((Player) target).isCreative());

    public MutantShulkerServantTrap(EntityType<? extends MutantShulkerServantTrap> type, Level level) {
        super(type, level);
        this.xpReward = 1;
    }

    public static AttributeSupplier.Builder createConfiguredAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MutantShulkerRewardsCommonConfig.trap_max_health.get())
                .add(Attributes.ARMOR, MutantShulkerRewardsCommonConfig.trap_armour.get())
                .add(Attributes.ARMOR_TOUGHNESS, MutantShulkerRewardsCommonConfig.trap_armour_toughness.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, MutantShulkerRewardsCommonConfig.trap_knockback_resistance.get());
    }

    @Override
    protected void registerGoals() {

    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {

        if (!this.isSpawnedByMutantShulker() || target != null) {
            super.setTarget(target);
        }
    }

    @Override
    public void followGoal() {

    }

    @Override
    public void ownedTick() {

    }

    @Override
    public void servantTick() {

    }

    private boolean isHostileTarget(Entity target) {
        if (target == this.getTrueOwner()) {
            return false;
        }
        if (this.isAlliedTo(target)) {
            return false;
        }
        return !(target instanceof LivingEntity) || !MobUtil.areAllies(this, target);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.updateAnimations();
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.tickDownAnimTimers();
        // 下落震屏:原每 tick 发一个网络包,节流到每 5 tick 一次,强度极低视觉无差
        if (!this.level().isClientSide && !this.onGround() && this.isSpawnedByMutantShulker()
                && this.tickCount % 5 == 0) {
            ShakeCameraEvent.shake(this.level(), 3, 0.005f, this.blockPosition(), 3);
        }
        // 就绪时每 2 tick 扫描一次触发(0.1s 延迟无感知),减少大量空闲陷阱的实体扫描开销
        if (!this.level().isClientSide && this.tickCount >= 20 && this.activateAnimationTick <= 0
                && this.snapCooldownTick <= 0 && this.tickCount % 2 == 0
                && !this.level().getEntities(this, this.getBoundingBox(), this.SNAPPABLE).isEmpty()) {
            this.snapCooldownTick = this.snapCooldownLength;
            this.playSound(SoundEventInit.MUTANT_SHULKER_TRAP_TRAP.get());
            this.activateAnimationTick = this.activateAnimationLength;
            this.level().broadcastEntityEvent(this, (byte) 4);
        }
        // 伤害/减速只在 activateActionPoint 那一 tick 生效,故只在那一 tick 扫描实体,避免每 tick 空扫 AABB 并分配 List
        if (this.activateAnimationTick == this.activateActionPoint) {
            ShakeCameraEvent.shake(this.level(), 6, 0.075f, this.blockPosition(), 5);
            float damage = this.isSpawnedByMutantShulker()
                    ? MutantShulkerRewardsCommonConfig.trap_mutant_shulker_damage.get().floatValue()
                    : MutantShulkerRewardsCommonConfig.trap_player_damage.get().floatValue();
            List<Entity> hurtables = this.level().getEntities(this, this.getBoundingBox(), this.HURTABLE);
            for (Entity entity : hurtables) {
                entity.hurt(MMDamageTypes.mutantShulkerTrapAttack(this.damageSources(), this), damage);
                if (!(entity instanceof LivingEntity)) continue;
                ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                        MutantShulkerRewardsCommonConfig.trap_slowness_length.get(),
                        MutantShulkerRewardsCommonConfig.trap_slowness_level.get()));
                ((LivingEntity) entity).addEffect(new MobEffectInstance(EffectInit.JUMPING_FATIGUE.get(),
                        MutantShulkerRewardsCommonConfig.trap_slowness_length.get(), 0));
            }
        }
        if (this.isSpawnedByMutantShulker()) {
            ++this.lifeTime;
        }
        if (this.snapCooldownTick > 0) {
            --this.snapCooldownTick;
        }
        // 触发消散:vanishAnimationTick <= 0 防止消散动画期间重复触发。
        // 两个来源:1) 配置到期转弹射物(lifeTime == trap_turn_to_bullet_time);
        // 2) 硬寿命兜底(lifeTime 达到 MAX_LIFETIME_TICKS),此时即使 trap_turns_to_bullet 关闭也会强制消散。
        if (!this.level().isClientSide && this.vanishAnimationTick <= 0
                && ((MutantShulkerRewardsCommonConfig.trap_turns_to_bullet.get()
                        && this.lifeTime == MutantShulkerRewardsCommonConfig.trap_turn_to_bullet_time.get())
                    || this.lifeTime >= MAX_LIFETIME_TICKS)) {
            this.playSound(SoundEventInit.MUTANT_SHULKER_TRAP_VANISH.get());
            this.vanishAnimationTick = this.vanishAnimationLength;
            this.level().broadcastEntityEvent(this, (byte) 11);
        }
        if (!this.level().isClientSide && this.vanishAnimationTick == 1) {
            this.discard();
            // 只在配置允许转弹射物时产出子弹:硬寿命兜底触发的消散不转弹,只移除陷阱
            if (MutantShulkerRewardsCommonConfig.trap_turns_to_bullet.get() && this.getTarget() != null) {

                MutantShulkerBullet bullet = new MutantShulkerServantBullet(EntityTypeInit.MUTANT_SHULKER_BULLET.get(), this.level());
                bullet.damage = MutantShulkerRewardsCommonConfig.trap_shulker_bullet_damage.get().floatValue();
                // 仆从子弹不破坏方块,与直接发射的导弹一致;不设则继承 MutantShulkerBullet 默认 griefing=true,爆炸会 DESTROY 方块
                bullet.griefing = false;
                bullet.levitationLength = MutantShulkerRewardsCommonConfig.trap_shulker_bullet_levitation_length.get();
                bullet.levitationLevel = MutantShulkerRewardsCommonConfig.trap_shulker_bullet_levitation_level.get();
                bullet.moveTo(this.getX(), this.getY() + 0.25, this.getZ());
                LivingEntity owner = this.getTrueOwner();
                bullet.setOwner(owner != null ? owner : this);
                bullet.setTargetByID(this.getTarget().getId());
                this.level().addFreshEntity(bullet);
            }
        }
        if (!this.level().isClientSide && this.activateAnimationTick == 1
                && (this.isSpawnedByMutantShulker() && !MutantShulkerRewardsCommonConfig.trap_mutant_shulker_multiuse.get()
                || !this.isSpawnedByMutantShulker() && !MutantShulkerRewardsCommonConfig.trap_player_multiuse.get())) {
            this.discard();
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!this.level().isClientSide && this.getOwner() == player && !this.isSpawnedByMutantShulker()
                && player.getItemInHand(hand).isEmpty() && player.isCrouching()) {
            this.spawnAtLocation(ItemInit.MUTANT_SHULKER_TRAP.get());
            this.discard();
            return InteractionResult.SUCCESS;
        }
        if (this.getOwner() == player && !this.isSpawnedByMutantShulker()
                && itemstack.getItem() instanceof DyeItem && ((DyeItem) itemstack.getItem()).getDyeColor() != this.getColor()) {
            this.setColor(((DyeItem) itemstack.getItem()).getDyeColor());
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (this.getOwner() == player && !this.isSpawnedByMutantShulker()
                && itemstack.getItem() == Items.WATER_BUCKET && this.getColor() != null) {
            this.setColor(null);
            if (!player.isCreative()) {
                player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.onGround() || this.activateAnimationTick > 0 || this.vanishAnimationTick > 0) {
            return false;
        }
        if (!source.is(DamageTypeTags.WITCH_RESISTANT_TO) && source.getDirectEntity() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) source.getDirectEntity();
            if (!source.is(DamageTypeTags.IS_EXPLOSION)) {
                attacker.hurt(this.damageSources().thorns(this),
                        MutantShulkerRewardsCommonConfig.trap_thorns_damage.get().floatValue());
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SPAWNED_BY_MUTANT_SHULKER, false);
        this.entityData.define(COLOR_ID, (byte) 16);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("SpawnedByMutantShulker", this.isSpawnedByMutantShulker());
        tag.putByte("Color", this.entityData.get(COLOR_ID));
        // 保存寿命,防止区块卸载/重载后 lifeTime 清零,让硬寿命上限跨区块真正生效
        tag.putInt("LifeTime", this.lifeTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Color", 99)) {
            this.entityData.set(COLOR_ID, tag.getByte("Color"));
        }
        this.setSpawnedByMutantShulker(tag.getBoolean("SpawnedByMutantShulker"));
        if (tag.contains("LifeTime", 3)) {
            this.lifeTime = tag.getInt("LifeTime");
        }
    }

    private void setColor(@Nullable DyeColor color) {
        if (color == null) {
            this.entityData.set(COLOR_ID, (byte) 16);
        } else {
            this.entityData.set(COLOR_ID, (byte) color.getId());
        }
    }

    @Nullable
    public DyeColor getColor() {
        byte b = this.entityData.get(COLOR_ID);
        return b != 16 && b <= 15 ? DyeColor.byId(b) : null;
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.getOwnerId();
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.setOwnerId(uuid);
    }

    public boolean isSpawnedByMutantShulker() {
        return this.entityData.get(SPAWNED_BY_MUTANT_SHULKER);
    }

    public void setSpawnedByMutantShulker(boolean spawnedByMutantShulker) {
        this.entityData.set(SPAWNED_BY_MUTANT_SHULKER, spawnedByMutantShulker);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance p_34192_) {
        return false;
    }

    @Override
    public boolean causeFallDamage(float p_148711_, float p_148712_, DamageSource p_148713_) {
        return false;
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == 4) {
            this.activateAnimationTick = this.activateAnimationLength;
        } else if (event == 11) {
            this.vanishAnimationTick = this.vanishAnimationLength;
        } else {
            super.handleEntityEvent(event);
        }
    }

    @Override
    public void push(double x, double y, double z) {
    }

    @Override
    public void push(Entity entity) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isSpawnedByMutantShulker() ? SoundEventInit.MUTANT_SHULKER_TRAP_IDLE.get() : null;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 200;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return SoundEventInit.MUTANT_SHULKER_TRAP_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEventInit.MUTANT_SHULKER_TRAP_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.5f;
    }

    private void updateAnimations() {
        this.idleAnimation.animateWhen(this.onGround() || !this.isSpawnedByMutantShulker(), this.tickCount);
        this.activateAnimation.animateWhen(this.activateAnimationTick > 0, this.tickCount);
        this.vanishAnimation.animateWhen(this.vanishAnimationTick > 0, this.tickCount);
    }

    public void tickDownAnimTimers() {
        if (this.activateAnimationTick > 0) {
            --this.activateAnimationTick;
        }
        if (this.vanishAnimationTick > 0) {
            --this.vanishAnimationTick;
        }
    }
}
