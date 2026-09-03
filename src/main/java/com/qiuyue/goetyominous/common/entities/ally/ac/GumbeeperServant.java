package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominous.common.entities.projectile.GumballServantEntity;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

/**
 * 糖球苦力怕(Gumbeeper)仆从:以 AC 原版 GumbeeperEntity 行为为蓝本移植成 Goety 仆从。
 *
 * <p>忠实还原:</p>
 * <ul>
 *   <li>头内糖球层随弹药(6 发)逐层减少,攻击前旋钮蓄力(dialRot→450),随后发射弹跳糖球;</li>
 *   <li><b>自爆阵亡</b>:弹药耗尽且贴到目标身旁时启动自爆,1 秒后喷出 15 颗(蓄电 30 颗)弹跳糖球并自行销毁;</li>
 *   <li>被雷击后变为"蓄电"形态(heal、3 连发、更高伤害、更多弹跳、能量外圈),原样保留。</li>
 * </ul>
 *
 * <p>仆从化调整:丢开野生敌对目标/猫恐惧/被 Licowitch 附身等逻辑;改用基类 Summoned 的
 * 主人跟随(默认 FollowOwnerGoal,有目标时自动暂停)与 SummonTargetGoal 索敌;蛋召唤时按数量上限拦截。
 * 保留原版"打火石/火焰弹右键点燃自爆"(仅限主人触发,见 {@link #mobInteract})。
 * 弹丸 owner 直接指向主人,保证自爆阵亡后弹跳糖球仍不误伤主人的其他仆从(见 {@link GumballServantEntity})。</p>
 */
public class GumbeeperServant extends Summoned implements PowerableMob {

    private static final int DEFAULT_GUMBALLS = 6;
    private static final float MAX_DIAL_ROT = 450.0F;

    private static final EntityDataAccessor<Boolean> EXPLODING = SynchedEntityData.defineId(GumbeeperServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOOTING = SynchedEntityData.defineId(GumbeeperServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ATTACK_CHARGE = SynchedEntityData.defineId(GumbeeperServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> GUMBALLS_LEFT = SynchedEntityData.defineId(GumbeeperServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CHARGED = SynchedEntityData.defineId(GumbeeperServant.class, EntityDataSerializers.BOOLEAN);

    private float explodeProgress;
    private float prevExplodeProgress;
    private float prevDialRot;
    private float dialRot;
    private float shootProgress;
    private float prevShootProgress;
    private int postShootTime;
    private boolean hasExploded;

    public GumbeeperServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.GumbeeperServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.GumbeeperServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.GumbeeperServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GumbeeperServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.GumbeeperServantKnockbackResistance.get())
                .add(Attributes.ARMOR, AttributesConfig.GumbeeperServantArmor.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AttackGoal());
        // 无目标时以 1.0 速度游荡(45 tick 间隔),落点限定在主人附近(跟随时不走远)。
        // 必须用 Goety 的 WanderGoal(checkNoActionTime=false):Summoned 覆写 checkDespawn 后非敌对仆从的
        // noActionTime 永不复位,原版 RandomStrollGoal 空闲约 5 秒后 noActionTime>=100 即被永久禁用 → 站桩不动。
        this.goalSelector.addGoal(3, new Summoned.WanderGoal<>(this, 1.0D, 45, 0.001F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EXPLODING, false);
        this.entityData.define(ATTACK_CHARGE, 0.0F);
        this.entityData.define(GUMBALLS_LEFT, DEFAULT_GUMBALLS);
        this.entityData.define(SHOOTING, false);
        this.entityData.define(CHARGED, false);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevExplodeProgress = this.explodeProgress;
        this.prevDialRot = this.dialRot;
        this.prevShootProgress = this.shootProgress;
        float attackCharge = this.getAttackCharge();
        if (this.isExploding() && this.explodeProgress < 20.0F) {
            this.explodeProgress++;
        }
        if (!this.isExploding() && this.explodeProgress > 0.0F) {
            this.explodeProgress--;
        }
        if (this.isShooting() && this.shootProgress < 5.0F) {
            this.shootProgress = Math.min(5.0F, this.shootProgress + 2.5F);
        }
        if (!this.isShooting() && this.shootProgress > 0.0F) {
            this.shootProgress = Math.max(0.0F, this.shootProgress - 1.0F);
        }
        if (attackCharge == 0.0F) {
            if (Mth.wrapDegrees(this.dialRot) != 0.0F) {
                this.dialRot = Mth.approachDegrees(this.dialRot, 0.0F, 30.0F);
            } else {
                this.dialRot = 0.0F;
            }
        } else {
            this.dialRot = Mth.approach(this.dialRot, MAX_DIAL_ROT * attackCharge, 10.0F);
        }
        if (this.postShootTime > 0) {
            this.postShootTime--;
        } else {
            this.setShooting(false);
        }
        if (this.isExploding()) {
            if (this.level().isClientSide && this.explodeProgress >= 18.0F) {
                for (int i = 0; i < 3 + this.random.nextInt(2); i++) {
                    this.level().addParticle(ParticleTypes.EXPLOSION, this.getRandomX(0.3F), this.getRandomY(), this.getRandomZ(0.3F), 0.0D, 0.0D, 0.0D);
                }
            }
            if (this.explodeProgress >= 20.0F) {
                if (!this.level().isClientSide && !this.hasExploded) {
                    this.explodeIntoGumballs();
                }
                this.playSound(ACSoundRegistry.GUMBEEPER_EXPLODE.get());
            }
        }
        if (this.isCharged() && this.isAlive() && this.tickCount % 150 == 0) {
            this.heal(1.0F);
        }
    }

    /**
     * 自爆阵亡:喷出弹跳糖球后以 DISCARDED 方式销毁(不走死亡事件,主人不会收到"仆从战死"反馈)。
     * 弹丸 owner 取主人,使阵亡瞬间喷出的糖球仍按主人身份判定友军。
     */
    private void explodeIntoGumballs() {
        LivingEntity source = this.getTrueOwner() != null ? this.getTrueOwner() : this;
        int count = this.isCharged() ? 30 : 15;
        for (int i = 0; i < count + this.random.nextInt(5); i++) {
            GumballServantEntity gumball = new GumballServantEntity(this.level(), source);
            gumball.setPos(new Vec3(this.getRandomX(0.3F), this.getY() + 0.7F + this.random.nextFloat() * 0.5F, this.getRandomZ(0.3F)));
            Vec3 delta = new Vec3(this.random.nextFloat() - 0.5F, this.random.nextFloat() - 0.25F, this.random.nextFloat() - 0.5F)
                    .normalize().scale(this.random.nextFloat() * 0.25F + 0.75F);
            gumball.setDeltaMovement(delta);
            this.level().addFreshEntity(gumball);
            if (this.isCharged()) {
                gumball.setMaximumBounces(10);
                gumball.setDamage((float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() + 2.0F);
            } else {
                gumball.setDamage((float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            }
        }
        this.hasExploded = true;
        this.discard();
    }

    @Override
    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, flying ? this.getY() - this.yo : 0.0D, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 8.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    @Override
    public void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt) {
        super.thunderHit(serverLevel, lightningBolt);
        this.setCharged(true);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(ItemTags.CREEPER_IGNITERS)) {
            // 与 AC 原版一致:打火石/火焰弹右键点燃自爆。仆从化:仅限主人触发,其余情况放行给基类指令。
            if (this.getOwnerId() == null || !player.getUUID().equals(this.getOwnerId())) {
                return InteractionResult.PASS;
            }
            SoundEvent soundevent = itemstack.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
            this.level().playSound(player, this.getX(), this.getY(), this.getZ(), soundevent, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.level().isClientSide) {
                this.setExploding(true);
                itemstack.hurtAndBreak(1, player, (p_32290_) -> p_32290_.broadcastBreakEvent(hand));
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    public boolean isExploding() {
        return this.entityData.get(EXPLODING);
    }

    public void setExploding(boolean exploding) {
        this.entityData.set(EXPLODING, exploding);
    }

    public void setGumballsLeft(int gumballsLeft) {
        this.entityData.set(GUMBALLS_LEFT, gumballsLeft);
    }

    public int getGumballsLeft() {
        return this.entityData.get(GUMBALLS_LEFT);
    }

    public void setAttackCharge(float attackCharge) {
        this.entityData.set(ATTACK_CHARGE, attackCharge);
    }

    public float getAttackCharge() {
        return this.entityData.get(ATTACK_CHARGE);
    }

    public boolean isShooting() {
        return this.entityData.get(SHOOTING);
    }

    public void setShooting(boolean shooting) {
        this.entityData.set(SHOOTING, shooting);
    }

    public boolean isCharged() {
        return this.entityData.get(CHARGED);
    }

    public void setCharged(boolean charged) {
        this.entityData.set(CHARGED, charged);
    }

    @Override
    public boolean isPowered() {
        return this.isCharged();
    }

    public float getExplodeProgress(float partialTick) {
        return (this.prevExplodeProgress + (this.explodeProgress - this.prevExplodeProgress) * partialTick) * 0.05F;
    }

    public float getShootProgress(float partialTick) {
        return (this.prevShootProgress + (this.shootProgress - this.prevShootProgress) * partialTick) * 0.2F;
    }

    public double getDialRot(float partialTick) {
        return this.prevDialRot + (this.dialRot - this.prevDialRot) * partialTick;
    }

    public boolean canShootGumball() {
        return this.getGumballsLeft() > 0 && this.dialRot >= MAX_DIAL_ROT && this.getAttackCharge() == 1.0F;
    }

    public void shootGumball(LivingEntity target) {
        LivingEntity source = this.getTrueOwner() != null ? this.getTrueOwner() : this;
        Vec3 spawnGumballFrom = new Vec3(0.0D, 0.3F, 0.4F).yRot(-this.yBodyRot * ((float) Math.PI / 180F)).add(this.position());
        int shotCount = this.isCharged() ? 3 : 1;
        this.playSound(ACSoundRegistry.GUMBALL_LAUNCH.get());
        for (int i = 0; i < shotCount; i++) {
            GumballServantEntity gumball = new GumballServantEntity(this.level(), source);
            gumball.setPos(spawnGumballFrom);
            Vec3 targetVec = new Vec3(target.getX(), target.getY(0.6D), target.getZ());
            if (this.isCharged() && i != shotCount / 2) {
                Vec3 vec3 = new Vec3(i < shotCount / 2 ? 3.0F : -3.0F, 0.0F, 0.0F).yRot(-this.yBodyRot * ((float) Math.PI / 180F));
                targetVec = targetVec.add(vec3);
            }
            double d0 = targetVec.x() - spawnGumballFrom.x;
            double d1 = targetVec.y() - spawnGumballFrom.y;
            double d2 = targetVec.z() - spawnGumballFrom.z;
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            gumball.shoot(d0, d1 + d3 * 0.2F, d2, 1.2F, 14 - this.level().getDifficulty().getId() * 4);
            this.level().addFreshEntity(gumball);
            if (this.isCharged()) {
                gumball.setMaximumBounces(10);
                gumball.setDamage((float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() + 2.0F);
            } else {
                gumball.setDamage((float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            }
        }
        this.playSound(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, 1.0F, this.getRandom().nextFloat() * 0.4F + 0.8F);
        if (!this.isCharged() || this.random.nextFloat() < 0.33F) {
            this.setGumballsLeft(this.getGumballsLeft() - 1);
        }
        this.setAttackCharge(0.0F);
        this.setShooting(true);
        this.postShootTime = 5;
    }

    public boolean hasLineOfSightToGumballHole(Entity entity) {
        if (entity.level() != this.level()) {
            return false;
        }
        Vec3 vec3 = new Vec3(this.getX(), this.getY() + 0.3F, this.getZ());
        Vec3 vec31 = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
        if (vec31.distanceTo(vec3) > 128.0D) {
            return false;
        }
        return this.level().clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setCharged(compoundTag.getBoolean("Charged"));
        this.setGumballsLeft(compoundTag.getInt("Gumballs"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("Charged", this.isCharged());
        compoundTag.putInt("Gumballs", this.getGumballsLeft());
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GUMBEEPER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GUMBEEPER_DEATH.get();
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag tag) {
        if (this.getTrueOwner() instanceof Player player) {
            if (this.countServants(player) >= MobsConfig.GumbeeperServantLimit.get()) {
                this.discard();
                return null;
            }
        }
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnGroupData, tag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof GumbeeperServant servant && servant != this) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * 复刻 AC GumbeeperEntity.AttackGoal:有弹药时在 16 格内定身走位蓄力发射;
     * 无弹药且贴脸(目标体型 +1.5 格)则引爆自毁,否则持续逼近。
     */
    public class AttackGoal extends Goal {

        private int seeTime;
        private int strafingTime = -1;
        private boolean strafingClockwise;
        private boolean strafingBackwards;

        public AttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = GumbeeperServant.this.getTarget();
            return target != null && target.isAlive() && GumbeeperServant.this.canAttack(target);
        }

        @Override
        public void stop() {
            super.stop();
            this.seeTime = 0;
            this.strafingTime = -1;
            GumbeeperServant.this.setAttackCharge(0.0F);
            GumbeeperServant.this.setExploding(false);
        }

        @Override
        public void tick() {
            LivingEntity target = GumbeeperServant.this.getTarget();
            boolean canRange = GumbeeperServant.this.getGumballsLeft() > 0;
            if (target != null) {
                double dist = GumbeeperServant.this.distanceTo(target);
                if (!canRange) {
                    if (dist < target.getBbWidth() + 1.5F) {
                        GumbeeperServant.this.setExploding(true);
                    } else {
                        GumbeeperServant.this.getNavigation().moveTo(target, 1.5D);
                    }
                } else if (dist < 16.0D && GumbeeperServant.this.hasLineOfSightToGumballHole(target)) {
                    GumbeeperServant.this.getNavigation().stop();
                    this.strafingTime++;
                } else {
                    GumbeeperServant.this.getNavigation().moveTo(target, 1.0D);
                    this.strafingTime = -1;
                }
                if (this.strafingTime >= 20) {
                    if (GumbeeperServant.this.getRandom().nextFloat() < 0.3F) {
                        this.strafingClockwise = !this.strafingClockwise;
                    }
                    if (GumbeeperServant.this.getRandom().nextFloat() < 0.3F) {
                        this.strafingBackwards = !this.strafingBackwards;
                    }
                    this.strafingTime = 0;
                }
                if (this.strafingTime > -1) {
                    if (dist > 12.0D) {
                        this.strafingBackwards = false;
                    } else if (dist < 5.0D) {
                        this.strafingBackwards = true;
                    }
                    GumbeeperServant.this.getMoveControl().strafe(this.strafingBackwards ? -1.0F : 1.0F, this.strafingClockwise ? 0.5F : -0.5F);
                    GumbeeperServant.this.lookAt(target, 30.0F, 30.0F);
                }
                if (canRange && GumbeeperServant.this.hasLineOfSightToGumballHole(target)) {
                    GumbeeperServant.this.setAttackCharge(Math.min(1.0F, GumbeeperServant.this.getAttackCharge() + (GumbeeperServant.this.isCharged() ? 0.3F : 0.1F)));
                    if (GumbeeperServant.this.canShootGumball()) {
                        GumbeeperServant.this.shootGumball(target);
                    }
                }
            }
        }
    }
}
