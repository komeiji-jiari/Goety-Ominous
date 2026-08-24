/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.client.particles.ModParticleTypes
 *  com.Polarice3.Goety.client.particles.ShockwaveParticleOption
 *  com.Polarice3.Goety.common.effects.GoetyEffects
 *  com.Polarice3.Goety.common.entities.ModEntityType
 *  com.Polarice3.Goety.common.entities.ai.SummonTargetGoal
 *  com.Polarice3.Goety.common.entities.ally.golem.AbstractGolemServant
 *  com.Polarice3.Goety.common.entities.neutral.Owned
 *  com.Polarice3.Goety.common.items.ModItems
 *  com.Polarice3.Goety.common.magic.spells.frost.IceChunkSpell
 *  com.Polarice3.Goety.init.ModMobType
 *  com.Polarice3.Goety.init.ModSounds
 *  com.Polarice3.Goety.utils.MathHelper
 *  com.Polarice3.Goety.utils.MobUtil
 *  com.Polarice3.Goety.utils.ModDamageSource
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageTypes
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.AnimationState
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.MobType
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.control.FlyingMoveControl
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.RandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
 *  net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
 *  net.minecraft.world.entity.ai.navigation.PathNavigation
 *  net.minecraft.world.entity.animal.IronGolem
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.ForgeMod
 *  net.minecraftforge.network.NetworkHooks
 *  net.minecraftforge.network.PlayMessages$SpawnEntity
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.jetbrains.annotations.Nullable
 */
package com.vivideru.masteryofmagic.entity;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.ShockwaveParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ally.golem.AbstractGolemServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.spells.frost.IceChunkSpell;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.vivideru.masteryofmagic.TimeFreezeManager;
import com.vivideru.masteryofmagic.entity.GhiaccioEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class IceMonarchEntity
extends AbstractGolemServant {
    private static final EntityDataAccessor<Boolean> CASTING = SynchedEntityData.m_135353_(IceMonarchEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    private static final EntityDataAccessor<Boolean> PUNCHING = SynchedEntityData.m_135353_(IceMonarchEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.m_135353_(IceMonarchEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135027_);
    private static final EntityDataAccessor<Boolean> ORAORA = SynchedEntityData.m_135353_(IceMonarchEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState castingAnimationState = new AnimationState();
    public final AnimationState punchingAnimationState = new AnimationState();
    public final AnimationState oraoraAnimationState = new AnimationState();
    public int oraoraTicks;
    public int chillHideSuppressedTicks;
    public int punchAnimationTicks;
    public int frostNovaCooldown;
    public int frostNovaWarmup;
    public int summonCooldown;
    public int summonWarmup;
    public int chunkSpellWarmup;
    public int postCastLock;
    public int chunkSpellCooldown;

    public IceMonarchEntity(PlayMessages.SpawnEntity packet, Level world) {
        this((EntityType<? extends IceMonarchEntity>)((EntityType)GoetyMasteryOfMagicModEntities.ICE_MONARCH.get()), world);
    }

    public IceMonarchEntity(EntityType<? extends IceMonarchEntity> type, Level world) {
        super(type, world);
        this.m_274367_(2.0f);
        this.f_21364_ = 500;
        this.m_21557_(false);
        this.m_21530_();
        this.f_21342_ = new FlyingMoveControl((Mob)this, 20, true);
        this.m_20242_(true);
    }

    protected void handleChillHide() {
        if (this.chillHideSuppressedTicks > 0) {
            this.m_21195_((MobEffect)GoetyEffects.CHILL_HIDE.get());
            return;
        }
        this.m_7292_(new MobEffectInstance((MobEffect)GoetyEffects.CHILL_HIDE.get(), 40, 2, false, false));
    }

    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof IceMonarchEntity;
    }

    public int getSummonLimit(LivingEntity owner) {
        return 1;
    }

    public boolean m_5829_() {
        return false;
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(ORAORA, (Object)false);
        this.f_19804_.m_135372_(CASTING, (Object)false);
        this.f_19804_.m_135372_(PUNCHING, (Object)false);
        this.f_19804_.m_135372_(DATA_FLAGS_ID, (Object)0);
    }

    public void setOraora(boolean value) {
        if (this.m_9236_().f_46443_) {
            return;
        }
        this.f_19804_.m_135381_(ORAORA, (Object)value);
    }

    public boolean isOraora() {
        return (Boolean)this.f_19804_.m_135370_(ORAORA);
    }

    private boolean getServantFlag(int mask) {
        byte flags = (Byte)this.f_19804_.m_135370_(DATA_FLAGS_ID);
        return (flags & mask) != 0;
    }

    private void setServantFlag(int mask, boolean value) {
        int flags = ((Byte)this.f_19804_.m_135370_(DATA_FLAGS_ID)).byteValue();
        flags = value ? (flags |= mask) : (flags &= ~mask);
        this.f_19804_.m_135381_(DATA_FLAGS_ID, (Object)((byte)(flags & 0xFF)));
    }

    public boolean isHostile() {
        return this.getServantFlag(1);
    }

    public void setHostile(boolean hostile) {
        this.setServantFlag(1, hostile);
    }

    public boolean isWandering() {
        return this.getServantFlag(2);
    }

    public void setWandering(boolean wandering) {
        this.setServantFlag(2, wandering);
    }

    public boolean isStaying() {
        return this.getServantFlag(4);
    }

    public void setStaying(boolean staying) {
        this.setServantFlag(4, staying);
    }

    public void setCasting(boolean value) {
        this.f_19804_.m_135381_(CASTING, (Object)value);
    }

    public boolean isCasting() {
        return (Boolean)this.f_19804_.m_135370_(CASTING);
    }

    public void setPunching(boolean value) {
        this.f_19804_.m_135381_(PUNCHING, (Object)value);
    }

    public boolean isPunching() {
        return (Boolean)this.f_19804_.m_135370_(PUNCHING);
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }

    protected PathNavigation m_6037_(Level world) {
        return new FlyingPathNavigation((Mob)this, world);
    }

    public boolean canBeCommanded() {
        return true;
    }

    protected void m_8099_() {
        super.m_8099_();
        this.f_21345_.m_25352_(0, (Goal)new IceMonarchOraOraGoal(this));
        this.f_21345_.m_25352_(4, (Goal)new IceMonarchFrostNovaGoal(this));
        this.f_21345_.m_25352_(5, (Goal)new IceMonarchSummonGoal(this));
        this.f_21345_.m_25352_(6, (Goal)new IceMonarchChunkSpellGoal(this));
        this.f_21345_.m_25352_(7, (Goal)new IceMonarchMeleeGoal(this, 1.25));
        this.f_21345_.m_25352_(8, (Goal)new RandomStrollGoal((PathfinderMob)this, 1.0));
        this.f_21345_.m_25352_(9, (Goal)new RandomLookAroundGoal((Mob)this));
        this.f_21345_.m_25352_(10, (Goal)new FloatGoal((Mob)this));
        this.f_21346_.m_25352_(2, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, 10, true, false, target -> {
            if (!(target instanceof Player)) {
                return false;
            }
            Player player = (Player)target;
            return !player.m_7500_() && !player.m_5833_() && SummonTargetGoal.predicate((LivingEntity)this).test(player);
        }));
        this.f_21346_.m_25352_(3, (Goal)new NearestAttackableTargetGoal((Mob)this, IronGolem.class, true, golem -> SummonTargetGoal.predicate((LivingEntity)this).test(golem)));
        this.f_21346_.m_25352_(4, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
    }

    public MobType m_6336_() {
        return ModMobType.FROST;
    }

    public void m_146917_(int ticks) {
        super.m_146917_(0);
    }

    public boolean m_142079_() {
        return false;
    }

    public boolean m_6785_(double distanceToClosestPlayer) {
        return false;
    }

    protected void m_7472_(DamageSource source, int looting, boolean recentlyHitIn) {
        super.m_7472_(source, looting, recentlyHitIn);
    }

    public SoundEvent m_7515_() {
        return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("goety_mastery_of_magic:ice_monarch_idle"));
    }

    public void m_7355_(BlockPos pos, BlockState blockIn) {
        this.m_5496_((SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("goety_mastery_of_magic:ice_monarch_step")), 0.15f, 1.0f);
    }

    public SoundEvent m_7975_(DamageSource ds) {
        return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("goety_mastery_of_magic:ice_monarch_hurt"));
    }

    public SoundEvent m_5592_() {
        return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("goety_mastery_of_magic:ice_monarch_death"));
    }

    public boolean m_142535_(float distance, float multiplier, DamageSource source) {
        return false;
    }

    public boolean m_6469_(DamageSource damagesource, float amount) {
        String id;
        LivingEntity livingAttacker;
        Entity attacker;
        String id2;
        if (damagesource.m_276093_(DamageTypes.f_268671_)) {
            return false;
        }
        if (damagesource.m_276093_(DamageTypes.f_268722_)) {
            return false;
        }
        if (damagesource.m_276093_(DamageTypes.f_268444_)) {
            return false;
        }
        ResourceKey damageKey = damagesource.m_269150_().m_203543_().orElse(null);
        boolean fireDamage = damagesource.m_269533_(DamageTypeTags.f_268745_);
        if (!fireDamage && damageKey != null && (id2 = damageKey.m_135782_().toString()).contains("fire")) {
            fireDamage = true;
        }
        if (fireDamage) {
            amount *= 1.5f;
            this.chillHideSuppressedTicks = 200;
            this.m_21195_((MobEffect)GoetyEffects.CHILL_HIDE.get());
        }
        if ((attacker = damagesource.m_7639_()) instanceof LivingEntity && (livingAttacker = (LivingEntity)attacker).m_6084_() && SummonTargetGoal.predicate((LivingEntity)this).test(livingAttacker) && !MobUtil.areAllies((Entity)this, (Entity)livingAttacker)) {
            this.m_6703_(livingAttacker);
            this.m_6710_(livingAttacker);
        }
        if (!fireDamage && this.m_21023_((MobEffect)GoetyEffects.CHILL_HIDE.get()) && damageKey != null && ((id = damageKey.m_135782_().toString()).contains("magic") || id.contains("spell") || id.contains("arcane") || id.contains("hex") || id.contains("void") || id.contains("eldritch"))) {
            amount *= 0.2f;
        }
        return super.m_6469_(damagesource, amount);
    }

    public InteractionResult m_6071_(Player player, InteractionHand hand) {
        if (!this.m_9236_().f_46443_) {
            ItemStack itemstack = player.m_21120_(hand);
            Item item = itemstack.m_41720_();
            if (this.getTrueOwner() != null && player == this.getTrueOwner() && (item == Items.f_41980_ || item == Items.f_42201_ || item == Items.f_42363_) && this.m_21223_() < this.m_21233_()) {
                if (!player.m_150110_().f_35937_) {
                    itemstack.m_41774_(1);
                }
                this.m_5496_(SoundEvents.f_12009_, 1.0f, 1.0f);
                if (item == Items.f_41980_) {
                    this.m_5634_(0.5f);
                } else if (item == Items.f_42201_) {
                    this.m_5634_(4.0f);
                } else {
                    this.m_5634_(20.0f);
                }
                player.m_6674_(hand);
                return InteractionResult.CONSUME;
            }
        }
        return super.m_6071_(player, hand);
    }

    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        tag.m_128405_("chunkSpellCooldown", this.chunkSpellCooldown);
        tag.m_128405_("frostNovaCooldown", this.frostNovaCooldown);
        tag.m_128405_("frostNovaWarmup", this.frostNovaWarmup);
        tag.m_128405_("summonCooldown", this.summonCooldown);
        tag.m_128405_("summonWarmup", this.summonWarmup);
        tag.m_128405_("chunkSpellWarmup", this.chunkSpellWarmup);
        tag.m_128405_("postCastLock", this.postCastLock);
        tag.m_128405_("chillHideSuppressedTicks", this.chillHideSuppressedTicks);
    }

    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        this.chunkSpellCooldown = tag.m_128451_("chunkSpellCooldown");
        this.frostNovaCooldown = tag.m_128451_("frostNovaCooldown");
        this.frostNovaWarmup = tag.m_128451_("frostNovaWarmup");
        this.summonCooldown = tag.m_128451_("summonCooldown");
        this.summonWarmup = tag.m_128451_("summonWarmup");
        this.chunkSpellWarmup = tag.m_128451_("chunkSpellWarmup");
        this.postCastLock = tag.m_128451_("postCastLock");
        this.chillHideSuppressedTicks = tag.m_128451_("chillHideSuppressedTicks");
    }

    public void m_8119_() {
        super.m_8119_();
        if (!this.m_9236_().f_46443_) {
            this.ownedTick();
            this.servantTick();
        }
        if (this.m_9236_().m_5776_()) {
            this.setupAnimationStates();
        }
    }

    protected void setupAnimationStates() {
        if (this.isOraora()) {
            this.oraoraAnimationState.m_216982_(this.f_19797_);
            this.castingAnimationState.m_216973_();
            this.punchingAnimationState.m_216973_();
            this.idleAnimationState.m_216973_();
            return;
        }
        this.oraoraAnimationState.m_216973_();
        if (this.isCasting()) {
            this.castingAnimationState.m_216982_(this.f_19797_);
            this.idleAnimationState.m_216973_();
            this.punchingAnimationState.m_216973_();
            return;
        }
        this.castingAnimationState.m_216973_();
        if (this.isPunching()) {
            this.punchingAnimationState.m_216982_(this.f_19797_);
            this.idleAnimationState.m_216973_();
            return;
        }
        this.punchingAnimationState.m_216973_();
        this.idleAnimationState.m_216982_(this.f_19797_);
    }

    protected void m_7840_(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public void m_20242_(boolean ignored) {
        super.m_20242_(true);
    }

    protected boolean canOraOraTarget(Entity entity) {
        Player player;
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        LivingEntity living = (LivingEntity)entity;
        if (!living.m_6084_()) {
            return false;
        }
        if (living == this) {
            return false;
        }
        LivingEntity owner = this.getTrueOwner();
        if (owner != null && living.m_20148_().equals(owner.m_20148_())) {
            return false;
        }
        if (MobUtil.areAllies((Entity)this, (Entity)living)) {
            return false;
        }
        if (living instanceof Player && ((player = (Player)living).m_7500_() || player.m_5833_())) {
            return false;
        }
        return TimeFreezeManager.isEntityFrozen((Entity)living);
    }

    public void m_8107_() {
        super.m_8107_();
        this.m_20242_(true);
        this.handleIdleHoverHeight();
        if (!this.m_9236_().f_46443_) {
            LivingEntity owner;
            if (this.m_9236_().m_46791_() == Difficulty.PEACEFUL && !((owner = this.getTrueOwner()) instanceof Player)) {
                this.m_142687_(Entity.RemovalReason.DISCARDED);
                return;
            }
            if (this.oraoraTicks > 0) {
                --this.oraoraTicks;
                if (this.oraoraTicks <= 0) {
                    this.setOraora(false);
                }
            }
            if (this.chunkSpellCooldown > 0) {
                --this.chunkSpellCooldown;
            }
            if (this.frostNovaCooldown > 0) {
                --this.frostNovaCooldown;
            }
            if (this.summonCooldown > 0) {
                --this.summonCooldown;
            }
            if (this.postCastLock > 0) {
                --this.postCastLock;
            }
            if (this.chillHideSuppressedTicks > 0) {
                --this.chillHideSuppressedTicks;
            }
            if (this.punchAnimationTicks > 0) {
                --this.punchAnimationTicks;
                if (this.punchAnimationTicks <= 0) {
                    this.setPunching(false);
                }
            }
            this.handleChillHide();
            this.spawnIceAuraParticles();
        }
    }

    public void m_7350_(EntityDataAccessor<?> key) {
        super.m_7350_(key);
        if (ORAORA.equals(key) && this.m_9236_().f_46443_) {
            if (this.isOraora()) {
                this.oraoraAnimationState.m_216973_();
                this.oraoraAnimationState.m_216977_(this.f_19797_);
            } else {
                this.oraoraAnimationState.m_216973_();
            }
        }
    }

    protected void handleTimeStopOraOra() {
        LivingEntity owner = this.getTrueOwner();
        if (!(owner instanceof Player)) {
            this.oraoraTicks = 0;
            this.setOraora(false);
            return;
        }
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            this.oraoraTicks = 0;
            this.setOraora(false);
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        List<LivingEntity> frozenTargets = TimeFreezeManager.getFrozenEnemiesForCaster(serverLevel, owner, 64.0);
        if (frozenTargets.isEmpty()) {
            this.oraoraTicks = 0;
            this.setOraora(false);
            return;
        }
        LivingEntity target = this.getNearestOraOraTarget(frozenTargets);
        if (target == null || !TimeFreezeManager.isEntityFrozen((Entity)target)) {
            this.oraoraTicks = 0;
            this.setOraora(false);
            return;
        }
        this.frostNovaWarmup = 0;
        this.summonWarmup = 0;
        this.chunkSpellWarmup = 0;
        this.postCastLock = 0;
        this.setCasting(false);
        this.setPunching(false);
        this.setOraora(true);
        this.m_6710_(target);
        this.m_21561_(true);
        this.m_21573_().m_26573_();
        this.oraoraTicks = 5;
        Vec3 targetCenter = target.m_20191_().m_82399_();
        Vec3 selfCenter = this.m_20191_().m_82399_();
        Vec3 toTarget = targetCenter.m_82546_(selfCenter);
        double attackDistance = 3.0;
        if (toTarget.m_82556_() > attackDistance * attackDistance) {
            Vec3 movement = toTarget.m_82541_().m_82490_(2.4);
            this.m_20256_(movement);
            this.f_19864_ = true;
        } else {
            this.m_20256_(Vec3.f_82478_);
        }
        this.m_21563_().m_24960_((Entity)target, 90.0f, 90.0f);
        this.m_21391_((Entity)target, 90.0f, 90.0f);
        this.m_146922_(this.m_6080_());
        this.f_20884_ = this.f_20883_ = this.m_6080_();
        if (this.f_19797_ % 4 == 0) {
            this.performOraOraHit(target);
        }
    }

    protected LivingEntity getNearestOraOraTarget(List<LivingEntity> targets) {
        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (LivingEntity target : targets) {
            double distance;
            if (!this.canOraOraTarget((Entity)target) || !((distance = this.m_20280_((Entity)target)) < nearestDistance)) continue;
            nearestDistance = distance;
            nearest = target;
        }
        return nearest;
    }

    protected boolean shouldPrioritizeOraOra() {
        LivingEntity owner = this.getTrueOwner();
        if (!(owner instanceof Player) && !(owner instanceof GhiaccioEntity)) {
            return false;
        }
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return false;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        List<LivingEntity> frozenTargets = TimeFreezeManager.getFrozenEnemiesForCaster(serverLevel, owner, 64.0);
        return this.getNearestOraOraTarget(frozenTargets) != null;
    }

    protected void performOraOraHit(LivingEntity target) {
        if (!this.canOraOraTarget((Entity)target)) {
            return;
        }
        target.f_19802_ = 0;
        target.f_20916_ = 0;
        target.f_20917_ = 0;
        float damage = (float)this.m_21133_(Attributes.f_22281_);
        target.m_6469_(this.m_269291_().m_269333_((LivingEntity)this), damage);
        target.f_19802_ = 0;
        target.f_20916_ = 0;
        target.f_20917_ = 0;
        target.f_19864_ = true;
        this.m_21335_((Entity)target);
        this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), SoundEvents.f_12057_, this.m_5720_(), 1.3f, 0.8f);
    }

    protected void spawnIceAuraParticles() {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        if (this.f_19797_ % 4 != 0) {
            return;
        }
        serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_175821_, this.m_20185_(), this.m_20186_() + (double)this.m_20206_() * 0.5, this.m_20189_(), 6, (double)this.m_20205_() * 0.45, (double)this.m_20206_() * 0.35, (double)this.m_20205_() * 0.45, 0.015);
        serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123754_, this.m_20185_(), this.m_20186_() + (double)this.m_20206_() * 0.5, this.m_20189_(), 2, (double)this.m_20205_() * 0.35, (double)this.m_20206_() * 0.25, (double)this.m_20205_() * 0.35, 0.01);
    }

    protected void handleIdleHoverHeight() {
        if (this.m_9236_().f_46443_) {
            return;
        }
        if (this.m_5448_() != null && this.m_5448_().m_6084_()) {
            return;
        }
        LivingEntity owner = this.getTrueOwner();
        if (owner != null && owner.m_6084_() && this.m_20280_((Entity)owner) > 64.0) {
            return;
        }
        if (this.isCasting() || this.frostNovaWarmup > 0 || this.summonWarmup > 0 || this.chunkSpellWarmup > 0 || this.postCastLock > 0) {
            return;
        }
        BlockPos groundPos = this.findGroundBelow();
        if (groundPos == null) {
            return;
        }
        double targetY = (double)groundPos.m_123342_() + 2.0;
        double difference = targetY - this.m_20186_();
        if (Math.abs(difference) < 0.08) {
            this.m_20334_(this.m_20184_().f_82479_ * 0.8, 0.0, this.m_20184_().f_82481_ * 0.8);
            return;
        }
        double verticalSpeed = Mth.m_14008_((double)(difference * 0.08), (double)-0.08, (double)0.08);
        this.m_20334_(this.m_20184_().f_82479_ * 0.8, verticalSpeed, this.m_20184_().f_82481_ * 0.8);
    }

    @Nullable
    protected BlockPos findGroundBelow() {
        BlockPos start = this.m_20183_();
        for (int y = start.m_123342_(); y > this.m_9236_().m_141937_(); --y) {
            BlockPos pos = new BlockPos(start.m_123341_(), y, start.m_123343_());
            BlockState belowState = this.m_9236_().m_8055_(pos.m_7495_());
            if (belowState.m_280555_()) {
                return pos.m_7495_();
            }
            if (belowState.m_60819_().m_76170_()) {
                return pos.m_7495_();
            }
            if (belowState.m_60819_().m_76152_() != Fluids.f_76193_ && belowState.m_60819_().m_76152_() != Fluids.f_76195_) continue;
            return pos.m_7495_();
        }
        return null;
    }

    public boolean hasValidTarget() {
        Player player;
        LivingEntity target = this.m_5448_();
        if (target == null) {
            return false;
        }
        if (!target.m_6084_()) {
            return false;
        }
        if (target instanceof Player && ((player = (Player)target).m_7500_() || player.m_5833_())) {
            this.m_6710_(null);
            this.m_21573_().m_26573_();
            this.m_21561_(false);
            return false;
        }
        return true;
    }

    protected boolean canHarmTarget(Entity entity) {
        Player player;
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        LivingEntity living = (LivingEntity)entity;
        if (!living.m_6084_()) {
            return false;
        }
        if (living == this) {
            return false;
        }
        if (MobUtil.areAllies((Entity)this, (Entity)living)) {
            return false;
        }
        if (!SummonTargetGoal.predicate((LivingEntity)this).test(living)) {
            return false;
        }
        return !(living instanceof Player) || !(player = (Player)living).m_7500_() && !player.m_5833_();
    }

    public boolean performPunchAttack(LivingEntity target) {
        if (this.isCasting() || this.postCastLock > 0) {
            return false;
        }
        if (target == null || !target.m_6084_()) {
            return false;
        }
        if (!this.canHarmTarget((Entity)target)) {
            return false;
        }
        this.m_21563_().m_24960_((Entity)target, 90.0f, 90.0f);
        this.m_21391_((Entity)target, 90.0f, 90.0f);
        this.m_146922_(this.m_6080_());
        this.f_20884_ = this.f_20883_ = this.m_6080_();
        this.m_21011_(InteractionHand.MAIN_HAND, true);
        this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), SoundEvents.f_12057_, this.m_5720_(), 1.8f, 0.55f);
        this.setPunching(true);
        this.punchAnimationTicks = 10;
        this.postCastLock = 25;
        int hitCount = 0;
        Level level = this.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            Vec3 origin = this.m_146892_();
            Vec3 forward = this.m_20154_().m_82541_();
            Vec3 end = origin.m_82549_(forward.m_82490_(3.0));
            AABB searchBox = this.m_20191_().m_82369_(forward.m_82490_(3.0)).m_82400_(2.0);
            for (LivingEntity living : serverLevel.m_6443_(LivingEntity.class, searchBox, livingEntity -> {
                if (!this.canHarmTarget((Entity)livingEntity)) {
                    return false;
                }
                AABB box = livingEntity.m_20191_().m_82400_(0.6);
                return box.m_82390_(origin) || box.m_82371_(origin, end).isPresent();
            })) {
                if (!living.m_6469_(this.m_269291_().m_269333_((LivingEntity)this), 20.0f)) continue;
                ++hitCount;
                Vec3 knockback = living.m_20182_().m_82546_(this.m_20182_());
                if (knockback.m_82556_() < 0.001) {
                    knockback = this.m_20154_();
                }
                knockback = new Vec3(knockback.f_82479_, 0.0, knockback.f_82481_).m_82541_();
                living.m_5997_(knockback.f_82479_ * 2.4, 0.65, knockback.f_82481_ * 2.4);
                living.f_19864_ = true;
                int freezeAmount = 60;
                int maxFreeze = living.m_146891_() + 100;
                living.m_146917_(Math.min(living.m_146888_() + freezeAmount, maxFreeze));
            }
        }
        return hitCount > 0;
    }

    protected void castSelfFrostNova() {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        this.chillHideSuppressedTicks = 0;
        this.m_20095_();
        Vec3 center = this.m_20182_();
        int potency = 5;
        int duration = 5;
        float radius = 16.0f;
        float damage = 20.0f + (float)potency;
        float maxDamage = 23.0f + (float)potency;
        float trueDamage = this.m_217043_().m_188501_() * (maxDamage - damage) + damage;
        this.createFrostNovaParticleBall(serverLevel, center, (int)radius);
        serverLevel.m_8767_((ParticleOptions)new ShockwaveParticleOption(0.0f, radius * 2.0f, 1), center.f_82479_, center.f_82480_ + 0.5, center.f_82481_, 0, 0.0, 0.0, 0.0, 0.0);
        AABB area = new AABB(center.f_82479_ - (double)radius, center.f_82480_ - (double)radius, center.f_82481_ - (double)radius, center.f_82479_ + (double)radius, center.f_82480_ + (double)radius, center.f_82481_ + (double)radius);
        for (LivingEntity living : serverLevel.m_6443_(LivingEntity.class, area, this::canHarmTarget)) {
            if (living.m_20238_(center) > (double)(radius * radius)) continue;
            living.m_6469_(ModDamageSource.directFreeze((LivingEntity)this), trueDamage);
            living.m_7292_(new MobEffectInstance((MobEffect)GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks((int)5) * duration, 0));
        }
        this.m_5496_((SoundEvent)ModSounds.ICE_CHUNK_HIT.get(), 2.0f, 0.6f);
    }

    protected void createFrostNovaParticleBall(ServerLevel serverLevel, Vec3 center, int radius) {
        double x = center.f_82479_;
        double y = center.f_82480_;
        double z = center.f_82481_;
        for (int i = -radius; i <= radius; ++i) {
            for (int j = -radius; j <= radius; ++j) {
                for (int k = -radius; k <= radius; ++k) {
                    double dx = (double)j + (this.m_217043_().m_188500_() - this.m_217043_().m_188500_()) * 0.5;
                    double dy = (double)i + (this.m_217043_().m_188500_() - this.m_217043_().m_188500_()) * 0.5;
                    double dz = (double)k + (this.m_217043_().m_188500_() - this.m_217043_().m_188500_()) * 0.5;
                    double divisor = Math.sqrt(dx * dx + dy * dy + dz * dz) / 0.5 + this.m_217043_().m_188583_() * 0.05;
                    serverLevel.m_8767_((ParticleOptions)((SimpleParticleType)ModParticleTypes.FROST_NOVA.get()), x, y, z, 0, dx / divisor, dy / divisor, dz / divisor, 0.5);
                    if (i == -radius || i == radius || j == -radius || j == radius) continue;
                    k += radius * 2 - 1;
                }
            }
        }
    }

    protected void summonIceGolems() {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        LivingEntity target = this.m_5448_();
        for (int i = 0; i < 2; ++i) {
            Owned owned;
            Mob mob = (Mob)((EntityType)ModEntityType.ICE_GOLEM.get()).m_20615_((Level)serverLevel);
            if (mob == null) continue;
            BlockPos spawnPos = this.findMinionSpawnPos(serverLevel);
            mob.m_7678_((double)spawnPos.m_123341_() + 0.5, (double)spawnPos.m_123342_(), (double)spawnPos.m_123343_() + 0.5, this.m_146908_(), this.m_146909_());
            if (mob instanceof Owned) {
                owned = (Owned)mob;
                owned.setTrueOwner((LivingEntity)this);
            }
            mob.m_6518_((ServerLevelAccessor)serverLevel, serverLevel.m_6436_(spawnPos), MobSpawnType.MOB_SUMMONED, null, null);
            if (mob instanceof Owned) {
                owned = (Owned)mob;
                LivingEntity owner = this.getTrueOwner();
                if (owner != null) {
                    owned.setTrueOwner(owner);
                } else {
                    owned.setTrueOwner((LivingEntity)this);
                }
            }
            this.empowerIceGolem(mob);
            if (target != null && target.m_6084_() && !MobUtil.areAllies((Entity)mob, (Entity)target)) {
                mob.m_6710_(target);
            }
            serverLevel.m_7967_((Entity)mob);
        }
    }

    protected BlockPos findMinionSpawnPos(ServerLevel serverLevel) {
        BlockPos origin = this.m_20183_();
        for (int i = 0; i < 32; ++i) {
            int dx = this.m_217043_().m_188503_(9) - 4;
            int dz = this.m_217043_().m_188503_(9) - 4;
            BlockPos pos = origin.m_7918_(dx, 0, dz);
            while (pos.m_123342_() > serverLevel.m_141937_() && serverLevel.m_8055_(pos.m_7495_()).m_60795_()) {
                pos = pos.m_7495_();
            }
            while (pos.m_123342_() < serverLevel.m_151558_() && !serverLevel.m_45756_((Entity)this, this.m_20191_().m_82383_(Vec3.m_82539_((Vec3i)pos).m_82546_(this.m_20182_())))) {
                pos = pos.m_7494_();
            }
            if (!serverLevel.m_45756_((Entity)this, this.m_20191_().m_82383_(Vec3.m_82539_((Vec3i)pos).m_82546_(this.m_20182_())))) continue;
            return pos;
        }
        return origin;
    }

    protected void empowerIceGolem(Mob mob) {
        AttributeInstance attackDamage;
        AttributeInstance maxHealth = mob.m_21051_(Attributes.f_22276_);
        if (maxHealth != null) {
            maxHealth.m_22100_(maxHealth.m_22115_() * 1.5);
            mob.m_21153_(mob.m_21233_());
        }
        if ((attackDamage = mob.m_21051_(Attributes.f_22281_)) != null) {
            attackDamage.m_22100_(attackDamage.m_22115_() * 1.25);
        }
        mob.m_7292_(new MobEffectInstance((MobEffect)GoetyEffects.CHILL_HIDE.get(), -1, 2, false, false));
        mob.m_7292_(new MobEffectInstance((MobEffect)GoetyEffects.BUFF.get(), -1, 1, false, false));
    }

    protected void castChunkSpell() {
        LivingEntity target = this.m_5448_();
        if (target == null) {
            return;
        }
        MobUtil.instaLook((Mob)this, (Entity)target);
        new IceChunkSpell().mobSpellResult((LivingEntity)this, new ItemStack((ItemLike)ModItems.FROST_STAFF.get()));
    }

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.m_21552_();
        builder = builder.m_22268_(Attributes.f_22279_, 1.0);
        builder = builder.m_22268_(Attributes.f_22276_, 150.0);
        builder = builder.m_22268_(Attributes.f_22284_, 10.0);
        builder = builder.m_22268_(Attributes.f_22281_, 20.0);
        builder = builder.m_22268_(Attributes.f_22277_, 32.0);
        builder = builder.m_22268_(Attributes.f_22278_, 0.7);
        builder = builder.m_22268_(Attributes.f_22282_, 2.0);
        builder = builder.m_22268_(Attributes.f_22280_, 1.0);
        builder = builder.m_22268_((Attribute)ForgeMod.STEP_HEIGHT_ADDITION.get(), 2.0);
        return builder;
    }

    static class IceMonarchOraOraGoal
    extends Goal {
        private final IceMonarchEntity mob;
        private LivingEntity target;

        public IceMonarchOraOraGoal(IceMonarchEntity mob) {
            this.mob = mob;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.TARGET));
        }

        public boolean m_8036_() {
            this.target = this.findTarget();
            return this.target != null;
        }

        public boolean m_8045_() {
            if (this.target == null || !this.mob.canOraOraTarget((Entity)this.target)) {
                this.target = this.findTarget();
            }
            return this.target != null;
        }

        public void m_8056_() {
            this.mob.frostNovaWarmup = 0;
            this.mob.summonWarmup = 0;
            this.mob.chunkSpellWarmup = 0;
            this.mob.postCastLock = 0;
            this.mob.setCasting(false);
            this.mob.setPunching(false);
            this.mob.setOraora(true);
            this.mob.m_21573_().m_26573_();
        }

        public void m_8041_() {
            this.mob.setOraora(false);
            this.mob.oraoraTicks = 0;
            this.target = null;
        }

        public boolean m_183429_() {
            return true;
        }

        public void m_8037_() {
            if (this.target == null || !this.mob.canOraOraTarget((Entity)this.target)) {
                this.target = this.findTarget();
                if (this.target == null) {
                    return;
                }
            }
            this.mob.frostNovaWarmup = 0;
            this.mob.summonWarmup = 0;
            this.mob.chunkSpellWarmup = 0;
            this.mob.postCastLock = 0;
            this.mob.setCasting(false);
            this.mob.setPunching(false);
            this.mob.setOraora(true);
            this.mob.oraoraTicks = 5;
            this.mob.m_6710_(this.target);
            this.mob.m_21561_(true);
            this.mob.m_21573_().m_26573_();
            Vec3 selfCenter = this.mob.m_20191_().m_82399_();
            Vec3 targetCenter = this.target.m_20191_().m_82399_();
            Vec3 toTarget = targetCenter.m_82546_(selfCenter);
            if (toTarget.m_82556_() > 4.0) {
                this.mob.m_20256_(toTarget.m_82541_().m_82490_(2.5));
            } else {
                this.mob.m_20256_(Vec3.f_82478_);
            }
            this.mob.m_21563_().m_24960_((Entity)this.target, 90.0f, 90.0f);
            this.mob.m_21391_((Entity)this.target, 90.0f, 90.0f);
            this.mob.m_146922_(this.mob.m_6080_());
            this.mob.f_20884_ = this.mob.f_20883_ = this.mob.m_6080_();
            if (this.mob.f_19797_ % 4 == 0) {
                this.mob.performOraOraHit(this.target);
            }
        }

        @Nullable
        private LivingEntity findTarget() {
            LivingEntity owner = this.mob.getTrueOwner();
            if (!(owner instanceof Player) && !(owner instanceof GhiaccioEntity)) {
                return null;
            }
            Level level = this.mob.m_9236_();
            if (!(level instanceof ServerLevel)) {
                return null;
            }
            ServerLevel serverLevel = (ServerLevel)level;
            List<LivingEntity> frozenTargets = TimeFreezeManager.getFrozenEnemiesForCaster(serverLevel, owner, 64.0);
            return this.mob.getNearestOraOraTarget(frozenTargets);
        }
    }

    static class IceMonarchFrostNovaGoal
    extends Goal {
        private final IceMonarchEntity mob;

        public IceMonarchFrostNovaGoal(IceMonarchEntity mob) {
            this.mob = mob;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean m_8036_() {
            LivingEntity target = this.mob.m_5448_();
            return target != null && !this.mob.shouldPrioritizeOraOra() && target.m_6084_() && this.mob.m_20280_((Entity)target) <= 100.0 && this.mob.frostNovaCooldown <= 0 && this.mob.frostNovaWarmup <= 0 && this.mob.summonWarmup <= 0 && this.mob.chunkSpellWarmup <= 0 && this.mob.postCastLock <= 0 && !this.mob.isCasting() && this.mob.m_217043_().m_188503_(25) == 0;
        }

        public boolean m_8045_() {
            return this.mob.frostNovaWarmup > 0;
        }

        public void m_8056_() {
            this.mob.frostNovaWarmup = 40;
            this.mob.setCasting(true);
            this.mob.setPunching(false);
            this.mob.m_21573_().m_26573_();
            if (!this.mob.m_20067_()) {
                this.mob.m_5496_((SoundEvent)ModSounds.FROST_PREPARE_SPELL.get(), 2.0f, 0.8f);
            }
        }

        public void m_8041_() {
            if (this.mob.frostNovaWarmup <= 0) {
                this.mob.setCasting(false);
            }
        }

        public boolean m_183429_() {
            return true;
        }

        public void m_8037_() {
            LivingEntity target = this.mob.m_5448_();
            if (target != null) {
                this.mob.m_21563_().m_24960_((Entity)target, 90.0f, 90.0f);
                this.mob.m_21391_((Entity)target, 90.0f, 90.0f);
            }
            this.mob.m_21573_().m_26573_();
            this.mob.m_20256_(this.mob.m_20184_().m_82542_(0.2, 0.2, 0.2));
            --this.mob.frostNovaWarmup;
            if (this.mob.frostNovaWarmup <= 0) {
                this.mob.castSelfFrostNova();
                this.mob.frostNovaCooldown = 200;
                this.mob.postCastLock = 20;
                this.mob.setCasting(false);
            }
        }
    }

    static class IceMonarchSummonGoal
    extends Goal {
        private final IceMonarchEntity mob;

        public IceMonarchSummonGoal(IceMonarchEntity mob) {
            this.mob = mob;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean m_8036_() {
            LivingEntity target = this.mob.m_5448_();
            return target != null && !this.mob.shouldPrioritizeOraOra() && target.m_6084_() && this.mob.m_20280_((Entity)target) > 100.0 && this.mob.summonCooldown <= 0 && this.mob.summonWarmup <= 0 && this.mob.frostNovaWarmup <= 0 && this.mob.chunkSpellWarmup <= 0 && this.mob.postCastLock <= 0 && !this.mob.isCasting() && this.mob.m_217043_().m_188503_(80) == 0;
        }

        public boolean m_8045_() {
            return this.mob.summonWarmup > 0;
        }

        public void m_8056_() {
            this.mob.summonWarmup = 50;
            this.mob.setCasting(true);
            this.mob.setPunching(false);
            this.mob.m_21573_().m_26573_();
            if (!this.mob.m_20067_()) {
                this.mob.m_5496_((SoundEvent)ModSounds.FROST_PREPARE_SPELL.get(), 2.0f, 0.8f);
            }
        }

        public void m_8041_() {
            if (this.mob.summonWarmup <= 0) {
                this.mob.setCasting(false);
            }
        }

        public boolean m_183429_() {
            return true;
        }

        public void m_8037_() {
            LivingEntity target = this.mob.m_5448_();
            if (target != null) {
                this.mob.m_21563_().m_24960_((Entity)target, 90.0f, 90.0f);
                this.mob.m_21391_((Entity)target, 90.0f, 90.0f);
            }
            this.mob.m_21573_().m_26573_();
            this.mob.m_20256_(this.mob.m_20184_().m_82542_(0.2, 0.2, 0.2));
            --this.mob.summonWarmup;
            if (this.mob.summonWarmup <= 0) {
                this.mob.summonIceGolems();
                this.mob.summonCooldown = 800;
                this.mob.postCastLock = 20;
                this.mob.setCasting(false);
            }
        }
    }

    static class IceMonarchChunkSpellGoal
    extends Goal {
        private final IceMonarchEntity mob;

        public IceMonarchChunkSpellGoal(IceMonarchEntity mob) {
            this.mob = mob;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean m_8036_() {
            LivingEntity target = this.mob.m_5448_();
            return target != null && !this.mob.shouldPrioritizeOraOra() && target.m_6084_() && this.mob.m_20280_((Entity)target) > 100.0 && this.mob.chunkSpellCooldown <= 0 && this.mob.chunkSpellWarmup <= 0 && this.mob.frostNovaWarmup <= 0 && this.mob.summonWarmup <= 0 && this.mob.postCastLock <= 0 && !this.mob.isCasting() && this.mob.m_217043_().m_188503_(15) == 0;
        }

        public boolean m_8045_() {
            return this.mob.chunkSpellWarmup > 0;
        }

        public void m_8056_() {
            this.mob.chunkSpellWarmup = 40;
            this.mob.setCasting(true);
            this.mob.setPunching(false);
            this.mob.m_21573_().m_26573_();
            if (!this.mob.m_20067_()) {
                this.mob.m_5496_((SoundEvent)ModSounds.FROST_PREPARE_SPELL.get(), 2.0f, 0.8f);
            }
        }

        public void m_8041_() {
            if (this.mob.chunkSpellWarmup <= 0) {
                this.mob.setCasting(false);
            }
        }

        public boolean m_183429_() {
            return true;
        }

        public void m_8037_() {
            LivingEntity target = this.mob.m_5448_();
            if (target == null) {
                this.mob.chunkSpellWarmup = 0;
                this.mob.setCasting(false);
                return;
            }
            MobUtil.instaLook((Mob)this.mob, (Entity)target);
            this.mob.m_21573_().m_26573_();
            this.mob.m_20256_(this.mob.m_20184_().m_82542_(0.2, 0.2, 0.2));
            --this.mob.chunkSpellWarmup;
            if (this.mob.chunkSpellWarmup <= 0) {
                this.mob.castChunkSpell();
                this.mob.chunkSpellCooldown = 60;
                this.mob.postCastLock = 10;
                this.mob.setCasting(false);
            }
        }
    }

    static class IceMonarchMeleeGoal
    extends Goal {
        private final IceMonarchEntity mob;
        private final double speedModifier;
        private int attackCooldown;
        private int pathRecalculateCooldown;

        public IceMonarchMeleeGoal(IceMonarchEntity mob, double speedModifier) {
            this.mob = mob;
            this.speedModifier = speedModifier;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean m_8036_() {
            LivingEntity target = this.mob.m_5448_();
            return target != null && !this.mob.shouldPrioritizeOraOra() && target.m_6084_() && !this.mob.isCasting();
        }

        public boolean m_8045_() {
            LivingEntity target = this.mob.m_5448_();
            return target != null && !this.mob.shouldPrioritizeOraOra() && target.m_6084_() && !this.mob.isCasting();
        }

        public void m_8056_() {
            this.attackCooldown = 0;
            this.pathRecalculateCooldown = 0;
            this.mob.m_21561_(true);
        }

        public void m_8041_() {
            this.mob.m_21561_(false);
            this.mob.setPunching(false);
            this.mob.m_21573_().m_26573_();
        }

        public boolean m_183429_() {
            return true;
        }

        public void m_8037_() {
            LivingEntity target = this.mob.m_5448_();
            if (this.mob.shouldPrioritizeOraOra()) {
                this.mob.setPunching(false);
                this.mob.setCasting(false);
                this.mob.m_21573_().m_26573_();
                return;
            }
            if (target == null) {
                this.mob.setPunching(false);
                return;
            }
            if (this.mob.isCasting() || this.mob.frostNovaWarmup > 0 || this.mob.summonWarmup > 0 || this.mob.chunkSpellWarmup > 0) {
                this.mob.setPunching(false);
                this.mob.m_21573_().m_26573_();
                return;
            }
            if (this.mob.postCastLock > 0) {
                this.mob.m_21573_().m_26573_();
                this.mob.m_20256_(this.mob.m_20184_().m_82542_(0.2, 1.0, 0.2));
                return;
            }
            this.mob.m_21563_().m_24960_((Entity)target, 60.0f, 60.0f);
            double distance = this.mob.m_20280_((Entity)target);
            double reach = this.getAttackReachSqr(target);
            if (this.pathRecalculateCooldown > 0) {
                --this.pathRecalculateCooldown;
            }
            if (distance > reach) {
                this.mob.setPunching(false);
                if (this.pathRecalculateCooldown <= 0) {
                    this.pathRecalculateCooldown = 4;
                    this.mob.m_21573_().m_5624_((Entity)target, this.speedModifier);
                }
            } else {
                this.mob.m_21573_().m_26573_();
                this.mob.m_20256_(this.mob.m_20184_().m_82542_(0.35, 1.0, 0.35));
            }
            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }
            if (distance <= reach && this.attackCooldown <= 0) {
                this.mob.performPunchAttack(target);
                this.attackCooldown = 20;
            }
        }

        protected double getAttackReachSqr(LivingEntity target) {
            double baseReach = 4.0;
            double verticalDifference = Math.max(0.0, this.mob.m_20186_() - 2.0 - target.m_20186_());
            double verticalPenalty = verticalDifference * 1.25;
            double finalReach = Math.max(1.5, baseReach - verticalPenalty);
            return finalReach * finalReach;
        }
    }

    static class IceMonarchFollowOwnerGoal
    extends Goal {
        private final IceMonarchEntity mob;
        private final double speedModifier;
        private final float startDistance;
        private final float stopDistance;
        private int recalculatePathCooldown;

        public IceMonarchFollowOwnerGoal(IceMonarchEntity mob, double speedModifier, float startDistance, float stopDistance) {
            this.mob = mob;
            this.speedModifier = speedModifier;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean m_8036_() {
            LivingEntity owner = this.mob.getTrueOwner();
            if (owner == null || !owner.m_6084_()) {
                return false;
            }
            if (!this.mob.canUpdateMove()) {
                return false;
            }
            if (this.mob.m_5448_() != null && this.mob.m_5448_().m_6084_()) {
                return false;
            }
            if (this.mob.isCasting() || this.mob.frostNovaWarmup > 0 || this.mob.summonWarmup > 0 || this.mob.chunkSpellWarmup > 0 || this.mob.postCastLock > 0) {
                return false;
            }
            return this.mob.m_20280_((Entity)owner) > (double)(this.startDistance * this.startDistance);
        }

        public boolean m_8045_() {
            LivingEntity owner = this.mob.getTrueOwner();
            if (owner == null || !owner.m_6084_()) {
                return false;
            }
            if (!this.mob.canUpdateMove()) {
                return false;
            }
            if (this.mob.m_5448_() != null && this.mob.m_5448_().m_6084_()) {
                return false;
            }
            return this.mob.m_20280_((Entity)owner) > (double)(this.stopDistance * this.stopDistance);
        }

        public void m_8056_() {
            this.recalculatePathCooldown = 0;
        }

        public void m_8041_() {
            this.mob.m_21573_().m_26573_();
        }

        public boolean m_183429_() {
            return true;
        }

        public void m_8037_() {
            LivingEntity owner = this.mob.getTrueOwner();
            if (owner == null) {
                return;
            }
            if (!this.mob.canUpdateMove()) {
                this.mob.m_21573_().m_26573_();
                return;
            }
            this.mob.m_21563_().m_24960_((Entity)owner, 30.0f, 30.0f);
            if (--this.recalculatePathCooldown <= 0) {
                this.recalculatePathCooldown = 10;
                if (this.mob.m_20280_((Entity)owner) > 576.0) {
                    this.mob.m_6021_(owner.m_20185_(), owner.m_20186_() + 2.0, owner.m_20189_());
                    this.mob.m_21573_().m_26573_();
                } else {
                    this.mob.m_21573_().m_5624_((Entity)owner, this.speedModifier);
                }
            }
        }
    }
}

