/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageType
 *  net.minecraft.world.damagesource.DamageTypes
 *  net.minecraft.world.entity.AnimationState
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobType
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.RandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
 *  net.minecraft.world.entity.animal.IronGolem
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.network.NetworkHooks
 *  net.minecraftforge.network.PlayMessages$SpawnEntity
 */
package com.vivideru.masteryofmagic.entity;

import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.util.AreaAttackUtil;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class GazerEntity
extends Monster {
    private static final EntityDataAccessor<Integer> DATA_ACTIVE_ANIMATION = SynchedEntityData.m_135353_(GazerEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> DATA_ACTIVE_ANIMATION_TICKS = SynchedEntityData.m_135353_(GazerEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final int ANIMATION_NONE = 0;
    private static final int ANIMATION_CHARGE = 1;
    private static final int ANIMATION_TELEPORT_ATTACK = 2;
    private static final int ANIMATION_DODGE = 3;
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState teleportattackAnimationState = new AnimationState();
    public final AnimationState dodgeAnimationState = new AnimationState();
    public final AnimationState animationState0 = this.idleAnimationState;
    private int comboCooldown = 0;
    private int chargeCooldown = 0;
    private int teleportAttackCooldown = 0;
    private int dodgeCooldown = 0;
    private int forcedAnimationTicks = 0;
    private float teleportAfterHitChance = 0.1f;

    public GazerEntity(PlayMessages.SpawnEntity packet, Level world) {
        this((EntityType<GazerEntity>)((EntityType)GoetyMasteryOfMagicModEntities.GAZER.get()), world);
    }

    public GazerEntity(EntityType<GazerEntity> type, Level world) {
        super(type, world);
        this.m_274367_(1.0f);
        this.f_21364_ = 30;
        this.m_21557_(false);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(DATA_ACTIVE_ANIMATION, (Object)0);
        this.f_19804_.m_135372_(DATA_ACTIVE_ANIMATION_TICKS, (Object)0);
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }

    protected void m_8099_() {
        super.m_8099_();
        this.f_21345_.m_25352_(0, (Goal)new FloatGoal((Mob)this));
        this.f_21345_.m_25352_(1, (Goal)new GazerDodgeGoal(this));
        this.f_21345_.m_25352_(2, (Goal)new GazerChargeAttackGoal(this));
        this.f_21345_.m_25352_(3, (Goal)new GazerTeleportAttackGoal(this));
        this.f_21345_.m_25352_(4, (Goal)new RandomStrollGoal((PathfinderMob)this, 1.25));
        this.f_21345_.m_25352_(5, (Goal)new RandomLookAroundGoal((Mob)this));
        this.f_21346_.m_25352_(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
        this.f_21346_.m_25352_(2, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, true));
        this.f_21346_.m_25352_(3, (Goal)new NearestAttackableTargetGoal((Mob)this, IronGolem.class, true));
    }

    public MobType m_6336_() {
        return MobType.f_21640_;
    }

    protected void m_7472_(DamageSource source, int looting, boolean recentlyHitIn) {
        super.m_7472_(source, looting, recentlyHitIn);
        this.m_19983_(new ItemStack((ItemLike)Items.f_42584_));
    }

    public void m_8032_() {
        if (!this.m_9236_().m_5776_()) {
            this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), SoundEvents.f_11899_, SoundSource.HOSTILE, 1.0f, 0.6f);
        }
    }

    public void m_6667_(DamageSource source) {
        super.m_6667_(source);
        if (!this.m_9236_().m_5776_()) {
            this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), SoundEvents.f_11900_, SoundSource.HOSTILE, 1.0f, 0.55f);
        }
    }

    public boolean m_6469_(DamageSource source, float amount) {
        boolean result;
        if (!this.m_9236_().m_5776_()) {
            if (this.m_20072_() && this.randomTeleportSafe(10.0)) {
                return false;
            }
            if (this.dodgeCooldown <= 0 && source.m_7639_() instanceof LivingEntity) {
                Entity attacker = source.m_7639_();
                this.dodgeCooldown = 200;
                this.setActiveAnimation(3, 12);
                if (attacker != null) {
                    this.m_21391_(attacker, 80.0f, 80.0f);
                    this.m_21563_().m_24960_(attacker, 80.0f, 80.0f);
                }
                this.m_9236_().m_5594_(null, this.m_20183_(), SoundEvents.f_11852_, SoundSource.HOSTILE, 1.0f, 0.55f);
                this.spawnEnderParticles();
                this.performBackDodge(attacker);
                return false;
            }
        }
        if ((result = super.m_6469_(source, amount)) && !this.m_9236_().m_5776_()) {
            if (this.f_19796_.m_188501_() < this.teleportAfterHitChance) {
                if (this.randomTeleportSafe(10.0)) {
                    this.teleportAfterHitChance = 0.1f;
                } else {
                    this.increaseTeleportAfterHitChance();
                }
            } else {
                this.increaseTeleportAfterHitChance();
            }
        }
        return result;
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.chargeCooldown > 0) {
            --this.chargeCooldown;
        }
        if (this.teleportAttackCooldown > 0) {
            --this.teleportAttackCooldown;
        }
        if (this.comboCooldown > 0) {
            --this.comboCooldown;
        }
        if (this.dodgeCooldown > 0) {
            --this.dodgeCooldown;
        }
        if (this.forcedAnimationTicks > 0) {
            --this.forcedAnimationTicks;
        }
        int animation = (Integer)this.f_19804_.m_135370_(DATA_ACTIVE_ANIMATION);
        int animationTicks = (Integer)this.f_19804_.m_135370_(DATA_ACTIVE_ANIMATION_TICKS);
        if (animation != 1 && animationTicks > 0) {
            this.f_19804_.m_135381_(DATA_ACTIVE_ANIMATION_TICKS, (Object)(--animationTicks));
            if (animationTicks <= 0) {
                this.f_19804_.m_135381_(DATA_ACTIVE_ANIMATION, (Object)0);
            }
        }
        if (!this.m_9236_().m_5776_()) {
            if (this.m_20072_()) {
                this.randomTeleportSafe(10.0);
            }
            this.lookAtTargetIfNeeded();
        }
        if (this.m_9236_().m_5776_()) {
            this.setupAnimationStates();
        }
    }

    private void lookAtTargetIfNeeded() {
        if ((Integer)this.f_19804_.m_135370_(DATA_ACTIVE_ANIMATION) == 1) {
            return;
        }
        LivingEntity target = this.m_5448_();
        if (target == null || !target.m_6084_()) {
            return;
        }
        this.m_21563_().m_24960_((Entity)target, 80.0f, 80.0f);
        this.m_21391_((Entity)target, 80.0f, 80.0f);
    }

    private void setupAnimationStates() {
        int activeAnimation = (Integer)this.f_19804_.m_135370_(DATA_ACTIVE_ANIMATION);
        boolean moving = this.m_20184_().m_165925_() > 0.0025;
        this.idleAnimationState.m_246184_(!moving && activeAnimation == 0, this.f_19797_);
        this.walkAnimationState.m_246184_(moving && activeAnimation == 0, this.f_19797_);
        this.attackAnimationState.m_246184_(activeAnimation == 1, this.f_19797_);
        this.teleportattackAnimationState.m_246184_(activeAnimation == 2, this.f_19797_);
        this.dodgeAnimationState.m_246184_(activeAnimation == 3, this.f_19797_);
    }

    private void setActiveAnimation(int animation, int ticks) {
        this.f_19804_.m_135381_(DATA_ACTIVE_ANIMATION, (Object)animation);
        this.f_19804_.m_135381_(DATA_ACTIVE_ANIMATION_TICKS, (Object)ticks);
        this.forcedAnimationTicks = ticks;
    }

    private void increaseTeleportAfterHitChance() {
        this.teleportAfterHitChance += 0.1f;
        if (this.teleportAfterHitChance > 1.0f) {
            this.teleportAfterHitChance = 1.0f;
        }
    }

    private int getMeleeDamageForSpecialAttack(float multiplier) {
        double baseDamage = this.m_21133_(Attributes.f_22281_);
        return Math.max(1, (int)Math.ceil(baseDamage * (double)multiplier));
    }

    private void performBackDodge(Entity sourceEntity) {
        Vec3 dodgeDirection;
        if (sourceEntity != null) {
            dodgeDirection = this.m_20182_().m_82546_(sourceEntity.m_20182_());
            dodgeDirection = new Vec3(dodgeDirection.f_82479_, 0.0, dodgeDirection.f_82481_);
        } else {
            dodgeDirection = this.m_20154_().m_82548_();
            dodgeDirection = new Vec3(dodgeDirection.f_82479_, 0.0, dodgeDirection.f_82481_);
        }
        if (dodgeDirection.m_82556_() < 0.001) {
            dodgeDirection = this.m_20154_().m_82548_();
            dodgeDirection = new Vec3(dodgeDirection.f_82479_, 0.0, dodgeDirection.f_82481_);
        }
        dodgeDirection = dodgeDirection.m_82541_();
        this.m_20256_(dodgeDirection.m_82490_(0.9).m_82520_(0.0, 0.15, 0.0));
        this.f_19864_ = true;
    }

    private boolean randomTeleportSafe(double radius) {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return false;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        for (int i = 0; i < 48; ++i) {
            AABB box;
            double x = this.m_20185_() + (this.f_19796_.m_188500_() - 0.5) * radius * 2.0;
            double y = this.m_20186_() + ((double)this.f_19796_.m_188503_((int)radius * 2 + 1) - radius);
            double z = this.m_20189_() + (this.f_19796_.m_188500_() - 0.5) * radius * 2.0;
            BlockPos pos = BlockPos.m_274561_((double)x, (double)y, (double)z);
            while (pos.m_123342_() > serverLevel.m_141937_() && !serverLevel.m_8055_(pos.m_7495_()).m_280555_()) {
                pos = pos.m_7495_();
            }
            BlockState ground = serverLevel.m_8055_(pos.m_7495_());
            if (!ground.m_280555_() || !serverLevel.m_6425_(pos).m_76178_() || !serverLevel.m_45756_((Entity)this, box = this.m_20191_().m_82386_((double)pos.m_123341_() + 0.5 - this.m_20185_(), (double)pos.m_123342_() - this.m_20186_(), (double)pos.m_123343_() + 0.5 - this.m_20189_()))) continue;
            double oldX = this.m_20185_();
            double oldY = this.m_20186_();
            double oldZ = this.m_20189_();
            this.m_6021_((double)pos.m_123341_() + 0.5, pos.m_123342_(), (double)pos.m_123343_() + 0.5);
            this.teleportAfterHitChance = 0.1f;
            serverLevel.m_6263_(null, oldX, oldY, oldZ, SoundEvents.f_11852_, SoundSource.HOSTILE, 1.0f, 1.0f);
            serverLevel.m_5594_(null, this.m_20183_(), SoundEvents.f_11852_, SoundSource.HOSTILE, 1.0f, 0.55f);
            serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123760_, oldX, oldY + 1.0, oldZ, 36, 0.4, 0.8, 0.4, 0.08);
            this.spawnEnderParticles();
            return true;
        }
        return false;
    }

    private void spawnEnderParticles() {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123760_, this.m_20185_(), this.m_20186_() + 1.0, this.m_20189_(), 48, 0.45, 0.8, 0.45, 0.08);
    }

    private void applyKnockbackInFront(double strength, float minDistance, float maxDistance, float wideness) {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        Vec3 origin = this.m_146892_();
        Vec3 forward = this.m_20154_().m_82541_();
        Vec3 start = origin.m_82549_(forward.m_82490_((double)minDistance));
        Vec3 end = origin.m_82549_(forward.m_82490_((double)maxDistance));
        double halfWidth = (double)wideness * 0.5;
        AABB searchBox = new AABB(start, end).m_82377_(halfWidth, halfWidth, halfWidth);
        List targets = serverLevel.m_6443_(LivingEntity.class, searchBox, target -> {
            if (target == null) {
                return false;
            }
            if (target == this) {
                return false;
            }
            if (!target.m_6084_()) {
                return false;
            }
            Vec3 targetCenter = target.m_20191_().m_82399_();
            Vec3 delta = targetCenter.m_82546_(origin);
            double forwardDistance = delta.m_82526_(forward);
            if (forwardDistance < (double)minDistance || forwardDistance > (double)maxDistance) {
                return false;
            }
            Vec3 projected = forward.m_82490_(forwardDistance);
            Vec3 perpendicular = delta.m_82546_(projected);
            return perpendicular.m_82556_() <= halfWidth * halfWidth;
        });
        for (LivingEntity target2 : targets) {
            target2.m_147240_(strength, -forward.f_82479_, -forward.f_82481_);
        }
    }

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.m_21552_();
        builder = builder.m_22268_(Attributes.f_22279_, 0.27);
        builder = builder.m_22268_(Attributes.f_22276_, 70.0);
        builder = builder.m_22268_(Attributes.f_22284_, 5.0);
        builder = builder.m_22268_(Attributes.f_22281_, 30.0);
        builder = builder.m_22268_(Attributes.f_22277_, 32.0);
        builder = builder.m_22268_(Attributes.f_22278_, 0.3);
        builder = builder.m_22268_(Attributes.f_22282_, 2.0);
        return builder;
    }

    private static class GazerDodgeGoal
    extends Goal {
        private final GazerEntity gazer;
        private int ticks;

        private GazerDodgeGoal(GazerEntity gazer) {
            this.gazer = gazer;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        }

        public boolean m_8036_() {
            return this.gazer.forcedAnimationTicks > 0 && this.gazer.dodgeCooldown > 188;
        }

        public void m_8056_() {
            this.ticks = 0;
            this.gazer.m_21573_().m_26573_();
        }

        public boolean m_8045_() {
            return this.ticks < 12;
        }

        public void m_8037_() {
            ++this.ticks;
            Level level = this.gazer.m_9236_();
            if (level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123760_, this.gazer.m_20185_(), this.gazer.m_20186_() + 0.8, this.gazer.m_20189_(), 8, 0.3, 0.5, 0.3, 0.04);
            }
        }
    }

    private static class GazerChargeAttackGoal
    extends Goal {
        private final GazerEntity gazer;
        private LivingEntity target;
        private Vec3 chargeDirection;
        private int chargeTicks;
        private int windupTicks;
        private static final int BACKSTEP_DURATION = 12;

        private GazerChargeAttackGoal(GazerEntity gazer) {
            this.gazer = gazer;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        public boolean m_8036_() {
            this.target = this.gazer.m_5448_();
            if (this.target == null || !this.target.m_6084_()) {
                return false;
            }
            if (this.gazer.chargeCooldown > 0) {
                return false;
            }
            if (this.gazer.comboCooldown > 0) {
                return false;
            }
            double distanceSq = this.gazer.m_20280_((Entity)this.target);
            return distanceSq <= 64.0;
        }

        public void m_8056_() {
            if (this.target == null) {
                return;
            }
            this.chargeTicks = 0;
            this.windupTicks = 0;
            this.gazer.chargeCooldown = 55;
            this.gazer.teleportAttackCooldown = Math.max(this.gazer.teleportAttackCooldown, 25);
            Vec3 direction = this.target.m_20182_().m_82546_(this.gazer.m_20182_());
            direction = new Vec3(direction.f_82479_, 0.0, direction.f_82481_);
            if (direction.m_82556_() < 0.001) {
                direction = this.gazer.m_20154_();
                direction = new Vec3(direction.f_82479_, 0.0, direction.f_82481_);
            }
            this.chargeDirection = direction.m_82541_();
            this.gazer.m_21573_().m_26573_();
        }

        public boolean m_8045_() {
            if (this.target == null || !this.target.m_6084_()) {
                return false;
            }
            if (this.windupTicks < 12) {
                return true;
            }
            return this.chargeTicks < 16;
        }

        public void m_8037_() {
            Level level;
            if (this.target == null) {
                return;
            }
            this.gazer.m_21391_((Entity)this.target, 80.0f, 80.0f);
            if (this.windupTicks < 12) {
                Level level2;
                ++this.windupTicks;
                Vec3 backward = this.chargeDirection.m_82548_();
                this.gazer.m_20256_(backward.m_82490_(0.25).m_82520_(0.0, this.gazer.m_20184_().f_82480_, 0.0));
                this.gazer.f_19864_ = true;
                if (this.windupTicks % 4 == 0 && (level2 = this.gazer.m_9236_()) instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel)level2;
                    serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123760_, this.gazer.m_20185_(), this.gazer.m_20186_() + 0.7, this.gazer.m_20189_(), 6, 0.3, 0.2, 0.3, 0.03);
                }
                return;
            }
            if (this.chargeTicks == 0) {
                this.gazer.setActiveAnimation(1, 8);
            }
            ++this.chargeTicks;
            this.gazer.m_146922_((float)(Math.atan2(this.chargeDirection.f_82481_, this.chargeDirection.f_82479_) * 57.2957763671875) - 90.0f);
            this.gazer.f_20883_ = this.gazer.m_146908_();
            this.gazer.f_20885_ = this.gazer.m_146908_();
            this.gazer.m_20256_(this.chargeDirection.m_82490_(0.86).m_82520_(0.0, this.gazer.m_20184_().f_82480_, 0.0));
            this.gazer.f_19864_ = true;
            AreaAttackUtil.attackInFront((Entity)this.gazer, this.gazer.getMeleeDamageForSpecialAttack(0.75f), (ResourceKey<DamageType>)DamageTypes.f_268566_, 0.0f, 3.5f, 4.5f);
            this.gazer.applyKnockbackInFront(1.25, 0.0f, 3.5f, 4.5f);
            if (this.chargeTicks % 2 == 0 && (level = this.gazer.m_9236_()) instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123799_, this.gazer.m_20185_(), this.gazer.m_20186_() + 0.7, this.gazer.m_20189_(), 10, 0.45, 0.25, 0.45, 0.04);
            }
            if (this.chargeTicks == 1 || this.chargeTicks == 4) {
                this.gazer.m_9236_().m_5594_(null, this.gazer.m_20183_(), SoundEvents.f_12314_, SoundSource.HOSTILE, 0.9f, 0.6f);
            }
        }

        public void m_8041_() {
            this.gazer.m_20256_(this.gazer.m_20184_().m_82542_(0.18, 1.0, 0.18));
            this.gazer.f_19804_.m_135381_(DATA_ACTIVE_ANIMATION, (Object)0);
            this.gazer.f_19804_.m_135381_(DATA_ACTIVE_ANIMATION_TICKS, (Object)0);
            this.gazer.comboCooldown = 20;
            if (!this.gazer.m_9236_().m_5776_()) {
                this.gazer.randomTeleportSafe(5.0);
            }
        }
    }

    private static class GazerTeleportAttackGoal
    extends Goal {
        private final GazerEntity gazer;
        private LivingEntity target;
        private Vec3 startTargetPosition;
        private Vec3 predictedPosition;
        private int windupTicks;
        private boolean vanished;
        private boolean teleported;
        private BlockPos savedTeleportPos;
        private Vec3 savedTargetLook;

        private GazerTeleportAttackGoal(GazerEntity gazer) {
            this.gazer = gazer;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        public void m_8041_() {
            this.gazer.comboCooldown = 20;
            this.gazer.m_6842_(false);
            this.gazer.f_19794_ = false;
            this.gazer.m_20242_(false);
        }

        public boolean m_8036_() {
            this.target = this.gazer.m_5448_();
            if (this.target == null || !this.target.m_6084_()) {
                return false;
            }
            if (this.gazer.teleportAttackCooldown > 0) {
                return false;
            }
            if (this.gazer.comboCooldown > 0) {
                return false;
            }
            double distanceSq = this.gazer.m_20280_((Entity)this.target);
            return distanceSq > 25.0 && distanceSq <= 676.0;
        }

        public void m_8056_() {
            this.windupTicks = 0;
            this.vanished = false;
            this.teleported = false;
            this.gazer.teleportAttackCooldown = 80;
            this.gazer.chargeCooldown = Math.max(this.gazer.chargeCooldown, 20);
            this.gazer.setActiveAnimation(2, 32);
            this.gazer.m_21573_().m_26573_();
            this.gazer.m_21391_((Entity)this.target, 80.0f, 80.0f);
            Vec3 targetLook = this.target.m_20154_();
            targetLook = new Vec3(targetLook.f_82479_, 0.0, targetLook.f_82481_);
            if (targetLook.m_82556_() < 0.001) {
                targetLook = this.target.m_20182_().m_82546_(this.gazer.m_20182_());
                targetLook = new Vec3(targetLook.f_82479_, 0.0, targetLook.f_82481_);
            }
            if (targetLook.m_82556_() < 0.001) {
                targetLook = new Vec3(0.0, 0.0, 1.0);
            }
            this.savedTargetLook = targetLook.m_82541_();
            Vec3 basePos = this.target.m_20182_();
            this.savedTeleportPos = null;
            Level level = this.gazer.m_9236_();
            if (level instanceof ServerLevel) {
                double[] distances;
                ServerLevel serverLevel = (ServerLevel)level;
                for (double d : distances = new double[]{1.6, 2.0, 2.5, 3.0}) {
                    AABB box;
                    Vec3 behind = basePos.m_82546_(this.savedTargetLook.m_82490_(d));
                    BlockPos pos = BlockPos.m_274561_((double)behind.f_82479_, (double)behind.f_82480_, (double)behind.f_82481_);
                    while (pos.m_123342_() > serverLevel.m_141937_() && !serverLevel.m_8055_(pos.m_7495_()).m_280555_()) {
                        pos = pos.m_7495_();
                    }
                    if (!serverLevel.m_6425_(pos).m_76178_() || !serverLevel.m_45756_((Entity)this.gazer, box = this.gazer.m_20191_().m_82386_((double)pos.m_123341_() + 0.5 - this.gazer.m_20185_(), (double)pos.m_123342_() - this.gazer.m_20186_(), (double)pos.m_123343_() + 0.5 - this.gazer.m_20189_()))) continue;
                    this.savedTeleportPos = pos;
                    break;
                }
            }
            this.gazer.spawnEnderParticles();
        }

        public boolean m_8045_() {
            return this.windupTicks < 32 && this.target != null && this.target.m_6084_();
        }

        public void m_8037_() {
            ++this.windupTicks;
            if (this.target == null || !this.target.m_6084_()) {
                return;
            }
            if (!this.vanished) {
                this.gazer.m_21563_().m_24960_((Entity)this.target, 80.0f, 80.0f);
            }
            if (this.windupTicks == 10 && !this.vanished) {
                this.vanished = true;
                Level level = this.gazer.m_9236_();
                if (level instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel)level;
                    serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123760_, this.gazer.m_20185_(), this.gazer.m_20186_() + 1.0, this.gazer.m_20189_(), 64, 0.5, 0.9, 0.5, 0.1);
                    serverLevel.m_5594_(null, this.gazer.m_20183_(), SoundEvents.f_11852_, SoundSource.HOSTILE, 1.0f, 0.45f);
                    if (this.savedTeleportPos != null) {
                        serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123789_, (double)this.savedTeleportPos.m_123341_() + 0.5, (double)this.savedTeleportPos.m_123342_() + 0.15, (double)this.savedTeleportPos.m_123343_() + 0.5, 42, 0.65, 0.25, 0.65, 0.035);
                    }
                }
                this.gazer.m_6842_(true);
                this.gazer.f_19794_ = true;
                this.gazer.m_20242_(true);
                this.gazer.m_20256_(Vec3.f_82478_);
            }
            if (this.vanished && !this.teleported) {
                this.gazer.m_20256_(Vec3.f_82478_);
                this.gazer.f_19864_ = true;
            }
            if (this.windupTicks == 20 && !this.teleported) {
                this.teleported = true;
                this.teleportBehindTargetAndStrike();
            }
        }

        private void teleportBehindTargetAndStrike() {
            Level level = this.gazer.m_9236_();
            if (!(level instanceof ServerLevel)) {
                return;
            }
            ServerLevel serverLevel = (ServerLevel)level;
            if (this.savedTeleportPos == null) {
                this.gazer.m_6842_(false);
                this.gazer.f_19794_ = false;
                this.gazer.m_20242_(false);
                return;
            }
            double oldX = this.gazer.m_20185_();
            double oldY = this.gazer.m_20186_();
            double oldZ = this.gazer.m_20189_();
            this.gazer.m_6021_((double)this.savedTeleportPos.m_123341_() + 0.5, this.savedTeleportPos.m_123342_(), (double)this.savedTeleportPos.m_123343_() + 0.5);
            this.gazer.teleportAfterHitChance = 0.1f;
            this.gazer.m_6842_(false);
            this.gazer.f_19794_ = false;
            this.gazer.m_20242_(false);
            serverLevel.m_6263_(null, oldX, oldY, oldZ, SoundEvents.f_11852_, SoundSource.HOSTILE, 1.0f, 0.45f);
            serverLevel.m_5594_(null, this.gazer.m_20183_(), SoundEvents.f_11852_, SoundSource.HOSTILE, 1.0f, 0.55f);
            serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123760_, oldX, oldY + 1.0, oldZ, 36, 0.4, 0.8, 0.4, 0.08);
            serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123760_, this.gazer.m_20185_(), this.gazer.m_20186_() + 1.0, this.gazer.m_20189_(), 64, 0.45, 0.8, 0.45, 0.08);
            this.gazer.m_21391_((Entity)this.target, 80.0f, 80.0f);
            this.gazer.m_21563_().m_24960_((Entity)this.target, 80.0f, 80.0f);
            AreaAttackUtil.attackInFront((Entity)this.gazer, this.gazer.getMeleeDamageForSpecialAttack(1.25f), (ResourceKey<DamageType>)DamageTypes.f_268566_, 0.0f, 3.6f, 3.8f);
            this.gazer.applyKnockbackInFront(1.55, 0.0f, 3.6f, 3.8f);
            serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123799_, this.gazer.m_20185_(), this.gazer.m_20186_() + 0.7, this.gazer.m_20189_(), 32, 0.5, 0.3, 0.5, 0.05);
            serverLevel.m_5594_(null, this.gazer.m_20183_(), SoundEvents.f_12317_, SoundSource.HOSTILE, 1.0f, 0.55f);
        }
    }
}

