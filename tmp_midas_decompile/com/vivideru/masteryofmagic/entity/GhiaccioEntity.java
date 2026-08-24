/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.client.particles.ModParticleTypes
 *  com.Polarice3.Goety.client.particles.ShockwaveParticleOption
 *  com.Polarice3.Goety.common.effects.GoetyEffects
 *  com.Polarice3.Goety.common.entities.ModEntityType
 *  com.Polarice3.Goety.common.entities.hostile.cultists.SpellCastingCultist
 *  com.Polarice3.Goety.common.entities.neutral.Owned
 *  com.Polarice3.Goety.common.entities.projectiles.IceStorm
 *  com.Polarice3.Goety.common.items.ModItems
 *  com.Polarice3.Goety.common.network.ModNetwork
 *  com.Polarice3.Goety.common.network.ModServerBossInfo
 *  com.Polarice3.Goety.common.network.server.SApostleSmitePacket
 *  com.Polarice3.Goety.init.ModSounds
 *  com.Polarice3.Goety.utils.ColorUtil
 *  com.Polarice3.Goety.utils.MathHelper
 *  com.Polarice3.Goety.utils.MobUtil
 *  com.Polarice3.Goety.utils.ModDamageSource
 *  com.Polarice3.Goety.utils.ServerParticleUtil
 *  com.Polarice3.Goety.utils.Vec3Util
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.BossEvent$BossBarColor
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageTypes
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.AnimationState
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntitySelector
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.MobType
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.FloatGoal
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.RandomStrollGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
 *  net.minecraft.world.entity.ai.targeting.TargetingConditions
 *  net.minecraft.world.entity.animal.IronGolem
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.gameevent.GameEvent
 *  net.minecraft.world.level.gameevent.GameEvent$Context
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.level.pathfinder.BlockPathTypes
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
import com.Polarice3.Goety.common.entities.hostile.cultists.SpellCastingCultist;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.IceStorm;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.common.network.server.SApostleSmitePacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.Vec3Util;
import com.vivideru.masteryofmagic.TimeFreezeManager;
import com.vivideru.masteryofmagic.config.BossConfig;
import com.vivideru.masteryofmagic.config.MobWeaknessConfig;
import com.vivideru.masteryofmagic.entity.IceMonarchEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModSounds;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class GhiaccioEntity
extends SpellCastingCultist {
    protected static final EntityDataAccessor<Byte> BOSS_FLAGS = SynchedEntityData.m_135353_(GhiaccioEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135027_);
    protected static final EntityDataAccessor<Float> FROST_BARRIER = SynchedEntityData.m_135353_(GhiaccioEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    protected static final EntityDataAccessor<Integer> DODGE_DIRECTION = SynchedEntityData.m_135353_(GhiaccioEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    protected static final EntityDataAccessor<Boolean> PUNCHING = SynchedEntityData.m_135353_(GhiaccioEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    public final AnimationState punchingFastAnimationState = new AnimationState();
    public final AnimationState flyingStillAnimationState = new AnimationState();
    public final AnimationState flyingFrontalAnimationState = new AnimationState();
    public final AnimationState flyingBackAnimationState = new AnimationState();
    public final AnimationState flyingSxAnimationState = new AnimationState();
    public final AnimationState flyingDxAnimationState = new AnimationState();
    public final AnimationState flyingCastingAnimationState = new AnimationState();
    public final AnimationState leMondeAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public int priorityTargetResetCooldown;
    public final ModServerBossInfo bossInfo;
    public boolean hasUsedSecondPhaseTimeStop;
    public boolean configInitialized;
    public int timeStopCooldown;
    public int timeStopWarmup;
    public int minionSummonCooldown;
    public int minionSummonWarmup;
    public int postCastLock;
    public int antiRegen;
    public int antiRegenTotal;
    public int deathTime;
    public int hitTimes;
    public int teleportCooldown;
    public int frostBarrierRegenDelay;
    public int dodgeCooldown;
    public int dodgeTicks;
    public int dodgeTotalTicks;
    public int frostNovaCooldown;
    public int frostNovaWarmup;
    public int iceStormCooldown;
    public int iceStormWarmup;
    public int iceStormShotsLeft;
    public int iceStormShotDelay;
    public boolean frostNovaDiagonal;
    public Vec3 dodgeStart = Vec3.f_82478_;
    public Vec3 dodgeEnd = Vec3.f_82478_;
    public DamageSource deathBlow = this.m_269291_().m_269264_();
    protected static final EntityDataAccessor<Boolean> TIME_STOP_CASTING = SynchedEntityData.m_135353_(GhiaccioEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);

    public GhiaccioEntity(PlayMessages.SpawnEntity packet, Level world) {
        this((EntityType<? extends GhiaccioEntity>)((EntityType)GoetyMasteryOfMagicModEntities.GHIACCIO.get()), world);
    }

    public GhiaccioEntity(EntityType<? extends GhiaccioEntity> type, Level world) {
        super(type, world);
        this.bossInfo = new ModServerBossInfo((Mob)this, BossEvent.BossBarColor.BLUE, true, true);
        this.m_274367_(0.6f);
        this.f_21364_ = 777;
        this.m_21557_(false);
        this.m_21530_();
        this.m_21441_(BlockPathTypes.WATER, 0.0f);
    }

    protected void m_8099_() {
        super.m_8099_();
        this.f_21345_.m_25352_(1, (Goal)new SecondPhaseGoal());
        this.f_21345_.m_25352_(2, (Goal)new GhiaccioPunchComboGoal(this, 2.0));
        this.f_21345_.m_25352_(3, (Goal)new RandomStrollGoal((PathfinderMob)this, 1.0));
        this.f_21345_.m_25352_(4, (Goal)new RandomLookAroundGoal((Mob)this));
        this.f_21345_.m_25352_(5, (Goal)new FloatGoal((Mob)this));
        this.f_21346_.m_25352_(1, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, 10, true, false, target -> {
            if (!(target instanceof Player)) {
                return false;
            }
            Player player = (Player)target;
            return !player.m_7500_() && !player.m_5833_();
        }));
        this.f_21346_.m_25352_(2, (Goal)new NearestAttackableTargetGoal((Mob)this, IronGolem.class, true));
        this.f_21346_.m_25352_(3, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
        this.f_21346_.m_25352_(2, (Goal)new NearestAttackableTargetGoal((Mob)this, IronGolem.class, true));
        this.f_21346_.m_25352_(3, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(TIME_STOP_CASTING, (Object)false);
        this.f_19804_.m_135372_(BOSS_FLAGS, (Object)0);
        this.f_19804_.m_135372_(FROST_BARRIER, (Object)Float.valueOf(70.0f));
        this.f_19804_.m_135372_(DODGE_DIRECTION, (Object)0);
        this.f_19804_.m_135372_(PUNCHING, (Object)false);
    }

    public boolean getBossFlag(int mask) {
        byte flags = (Byte)this.f_19804_.m_135370_(BOSS_FLAGS);
        return (flags & mask) != 0;
    }

    public void setBossFlag(int mask, boolean value) {
        int flags = ((Byte)this.f_19804_.m_135370_(BOSS_FLAGS)).byteValue();
        flags = value ? (flags |= mask) : (flags &= ~mask);
        this.f_19804_.m_135381_(BOSS_FLAGS, (Object)((byte)(flags & 0xFF)));
    }

    public void setSecondPhase(boolean value) {
        this.setBossFlag(1, value);
    }

    public void setTimeStopCasting(boolean value) {
        this.f_19804_.m_135381_(TIME_STOP_CASTING, (Object)value);
    }

    public boolean isTimeStopCasting() {
        return (Boolean)this.f_19804_.m_135370_(TIME_STOP_CASTING);
    }

    public boolean isSecondPhase() {
        return this.getBossFlag(1);
    }

    public void setSettingUpSecond(boolean value) {
        this.setBossFlag(2, value);
    }

    public boolean isSettingUpSecond() {
        return this.getBossFlag(2);
    }

    public void setCasting(boolean value) {
        this.setBossFlag(4, value);
    }

    public boolean isCasting() {
        return this.getBossFlag(4);
    }

    public void setPunching(boolean value) {
        this.f_19804_.m_135381_(PUNCHING, (Object)value);
    }

    public boolean isPunching() {
        return (Boolean)this.f_19804_.m_135370_(PUNCHING);
    }

    public float getFrostBarrier() {
        return ((Float)this.f_19804_.m_135370_(FROST_BARRIER)).floatValue();
    }

    public void setFrostBarrier(float value) {
        float max = this.ghiaccioFrostBarrierMax();
        if (value < 0.0f) {
            value = 0.0f;
        }
        if (value > max) {
            value = max;
        }
        this.f_19804_.m_135381_(FROST_BARRIER, (Object)Float.valueOf(value));
    }

    private int ghiaccioHealth() {
        try {
            return (Integer)BossConfig.GHIACCIO_HEALTH.get();
        }
        catch (IllegalStateException exception) {
            return 666;
        }
    }

    private float ghiaccioFrostBarrierMax() {
        try {
            return ((Integer)BossConfig.GHIACCIO_FROST_BARRIER_MAX.get()).floatValue();
        }
        catch (IllegalStateException exception) {
            return 70.0f;
        }
    }

    private float ghiaccioFrostBarrierRegen() {
        try {
            return ((Double)BossConfig.GHIACCIO_FROST_BARRIER_REGEN.get()).floatValue();
        }
        catch (IllegalStateException exception) {
            return 1.0f;
        }
    }

    private int ghiaccioFrostBarrierRegenDelayAfterFireDamage() {
        try {
            return (Integer)BossConfig.GHIACCIO_FROST_BARRIER_REGEN_DELAY_AFTER_FIRE_DAMAGE.get();
        }
        catch (IllegalStateException exception) {
            return 100;
        }
    }

    private float ghiaccioMaxDamageReduction() {
        try {
            return ((Double)BossConfig.GHIACCIO_MAX_DAMAGE_REDUCTION.get()).floatValue();
        }
        catch (IllegalStateException exception) {
            return 0.95f;
        }
    }

    private double ghiaccioMeleeBaseDamage() {
        try {
            return (Double)BossConfig.GHIACCIO_MELEE_BASE_DAMAGE.get();
        }
        catch (IllegalStateException exception) {
            return 20.0;
        }
    }

    private float ghiaccioMeleeTargetMaxHealthPercent() {
        try {
            return ((Double)BossConfig.GHIACCIO_MELEE_TARGET_MAX_HEALTH_PERCENT.get()).floatValue();
        }
        catch (IllegalStateException exception) {
            return 0.02f;
        }
    }

    private double ghiaccioMovementSpeed() {
        try {
            return (Double)BossConfig.GHIACCIO_MOVEMENT_SPEED.get();
        }
        catch (IllegalStateException exception) {
            return 0.22;
        }
    }

    private double ghiaccioArmor() {
        try {
            return (Double)BossConfig.GHIACCIO_ARMOR.get();
        }
        catch (IllegalStateException exception) {
            return 12.0;
        }
    }

    private double ghiaccioArmorToughness() {
        try {
            return (Double)BossConfig.GHIACCIO_ARMOR_TOUGHNESS.get();
        }
        catch (IllegalStateException exception) {
            return 8.0;
        }
    }

    private double ghiaccioKnockbackResistance() {
        try {
            return (Double)BossConfig.GHIACCIO_KNOCKBACK_RESISTANCE.get();
        }
        catch (IllegalStateException exception) {
            return 0.75;
        }
    }

    private double ghiaccioFollowRange() {
        try {
            return (Double)BossConfig.GHIACCIO_FOLLOW_RANGE.get();
        }
        catch (IllegalStateException exception) {
            return 40.0;
        }
    }

    public int getDodgeDirection() {
        return (Integer)this.f_19804_.m_135370_(DODGE_DIRECTION);
    }

    public void setDodgeDirection(int direction) {
        this.f_19804_.m_135381_(DODGE_DIRECTION, (Object)direction);
    }

    public boolean isDodging() {
        return this.dodgeTicks > 0;
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }

    public MobType m_6336_() {
        return MobType.f_21641_;
    }

    public boolean m_6785_(double distanceToClosestPlayer) {
        return false;
    }

    protected SoundEvent getCastingSoundEvent() {
        return (SoundEvent)ModSounds.APOSTLE_CAST_SPELL.get();
    }

    public SoundEvent m_7975_(DamageSource ds) {
        return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
    }

    public SoundEvent m_5592_() {
        return (SoundEvent)ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
    }

    protected SoundEvent getTrueDeathSound() {
        return SoundEvents.f_12556_;
    }

    public boolean m_7301_(MobEffectInstance effect) {
        if (effect.m_19544_() == MobEffects.f_19615_) {
            return false;
        }
        return super.m_7301_(effect);
    }

    public void setConfigurableAttributes() {
        if (this.m_21051_(Attributes.f_22276_) != null) {
            this.m_21051_(Attributes.f_22276_).m_22100_((double)this.ghiaccioHealth());
        }
        if (this.m_21051_(Attributes.f_22279_) != null) {
            this.m_21051_(Attributes.f_22279_).m_22100_(this.ghiaccioMovementSpeed());
        }
        if (this.m_21051_(Attributes.f_22284_) != null) {
            this.m_21051_(Attributes.f_22284_).m_22100_(this.ghiaccioArmor());
        }
        if (this.m_21051_(Attributes.f_22285_) != null) {
            this.m_21051_(Attributes.f_22285_).m_22100_(this.ghiaccioArmorToughness());
        }
        if (this.m_21051_(Attributes.f_22281_) != null) {
            this.m_21051_(Attributes.f_22281_).m_22100_(this.ghiaccioMeleeBaseDamage());
        }
        if (this.m_21051_(Attributes.f_22277_) != null) {
            this.m_21051_(Attributes.f_22277_).m_22100_(this.ghiaccioFollowRange());
        }
        if (this.m_21051_(Attributes.f_22278_) != null) {
            this.m_21051_(Attributes.f_22278_).m_22100_(this.ghiaccioKnockbackResistance());
        }
    }

    public void m_6457_(ServerPlayer player) {
        super.m_6457_(player);
        this.bossInfo.m_6543_(player);
    }

    public void m_6452_(ServerPlayer player) {
        super.m_6452_(player);
        this.bossInfo.m_6539_(player);
    }

    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        tag.m_128379_("hasUsedSecondPhaseTimeStop", this.hasUsedSecondPhaseTimeStop);
        tag.m_128405_("timeStopCooldown", this.timeStopCooldown);
        tag.m_128405_("timeStopWarmup", this.timeStopWarmup);
        tag.m_128405_("minionSummonCooldown", this.minionSummonCooldown);
        tag.m_128405_("minionSummonWarmup", this.minionSummonWarmup);
        tag.m_128405_("iceStormShotsLeft", this.iceStormShotsLeft);
        tag.m_128405_("iceStormShotDelay", this.iceStormShotDelay);
        tag.m_128405_("iceStormCooldown", this.iceStormCooldown);
        tag.m_128405_("iceStormWarmup", this.iceStormWarmup);
        tag.m_128405_("frostNovaCooldown", this.frostNovaCooldown);
        tag.m_128405_("frostNovaWarmup", this.frostNovaWarmup);
        tag.m_128379_("frostNovaDiagonal", this.frostNovaDiagonal);
        tag.m_128405_("antiRegen", this.antiRegen);
        tag.m_128405_("antiRegenTotal", this.antiRegenTotal);
        tag.m_128405_("hitTimes", this.hitTimes);
        tag.m_128405_("teleportCooldown", this.teleportCooldown);
        tag.m_128405_("frostBarrierRegenDelay", this.frostBarrierRegenDelay);
        tag.m_128405_("dodgeCooldown", this.dodgeCooldown);
        tag.m_128379_("secondPhase", this.isSecondPhase());
        tag.m_128379_("settingSecondPhase", this.isSettingUpSecond());
        tag.m_128350_("frostBarrier", this.getFrostBarrier());
    }

    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        this.hasUsedSecondPhaseTimeStop = tag.m_128471_("hasUsedSecondPhaseTimeStop");
        this.timeStopCooldown = tag.m_128451_("timeStopCooldown");
        this.timeStopWarmup = tag.m_128451_("timeStopWarmup");
        this.minionSummonCooldown = tag.m_128451_("minionSummonCooldown");
        this.minionSummonWarmup = tag.m_128451_("minionSummonWarmup");
        this.iceStormShotsLeft = tag.m_128451_("iceStormShotsLeft");
        this.iceStormShotDelay = tag.m_128451_("iceStormShotDelay");
        this.iceStormCooldown = tag.m_128451_("iceStormCooldown");
        this.iceStormWarmup = tag.m_128451_("iceStormWarmup");
        this.frostNovaCooldown = tag.m_128451_("frostNovaCooldown");
        this.frostNovaWarmup = tag.m_128451_("frostNovaWarmup");
        this.frostNovaDiagonal = tag.m_128471_("frostNovaDiagonal");
        this.antiRegen = tag.m_128451_("antiRegen");
        this.antiRegenTotal = tag.m_128451_("antiRegenTotal");
        this.hitTimes = tag.m_128451_("hitTimes");
        this.teleportCooldown = tag.m_128451_("teleportCooldown");
        this.frostBarrierRegenDelay = tag.m_128451_("frostBarrierRegenDelay");
        this.dodgeCooldown = tag.m_128451_("dodgeCooldown");
        this.setSecondPhase(tag.m_128471_("secondPhase"));
        this.setSettingUpSecond(tag.m_128471_("settingSecondPhase"));
        if (tag.m_128441_("frostBarrier")) {
            this.setFrostBarrier(tag.m_128457_("frostBarrier"));
        }
        if (this.m_8077_()) {
            this.bossInfo.m_6456_(this.m_5446_());
        }
    }

    public void m_6593_(@Nullable Component name) {
        super.m_6593_(name);
        this.bossInfo.m_6456_(this.m_5446_());
    }

    public int getAntiRegen() {
        return this.antiRegen;
    }

    public int getAntiRegenTotal() {
        return this.antiRegenTotal;
    }

    protected void applySmiteAntiRegen(DamageSource source) {
        int duration;
        LivingEntity attacker = null;
        Entity entity = source.m_7639_();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity;
            attacker = livingEntity = (LivingEntity)entity;
        } else {
            entity = source.m_7640_();
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity;
                attacker = livingEntity = (LivingEntity)entity;
            }
        }
        if (attacker == null) {
            return;
        }
        int smite = EnchantmentHelper.m_44836_((Enchantment)Enchantments.f_44978_, (LivingEntity)attacker);
        if (smite <= 0) {
            return;
        }
        int smiteLevel = Mth.m_14045_((int)smite, (int)1, (int)5);
        this.antiRegenTotal = duration = MathHelper.secondsToTicks((int)smiteLevel);
        this.antiRegen = duration;
        if (this.m_9236_() instanceof ServerLevel) {
            ModNetwork.sendToALL((Object)new SApostleSmitePacket(this.m_19879_(), duration));
        }
    }

    public boolean m_6469_(DamageSource source, float amount) {
        LivingEntity livingAttacker;
        String id;
        if (source.m_276093_(DamageTypes.f_268671_)) {
            return false;
        }
        if (source.m_276093_(DamageTypes.f_268722_)) {
            return false;
        }
        if (source.m_276093_(DamageTypes.f_268444_)) {
            return false;
        }
        ResourceKey damageKey = source.m_269150_().m_203543_().orElse(null);
        if (damageKey != null && ((id = damageKey.m_135782_().toString()).contains("ice") || id.contains("frost") || id.contains("freeze"))) {
            return false;
        }
        if (source.m_276093_(DamageTypes.f_268724_) && source.m_7639_() == null) {
            this.m_146870_();
            return true;
        }
        if (this.isSettingUpSecond()) {
            return false;
        }
        Entity attackerEntity = source.m_7639_();
        if (attackerEntity instanceof LivingEntity && (livingAttacker = (LivingEntity)attackerEntity).m_6084_() && !this.isOwnMinion((Entity)livingAttacker) && !this.isInvalidPlayerTarget(livingAttacker)) {
            Player targetPlayer;
            this.m_6703_(livingAttacker);
            LivingEntity livingEntity = this.m_5448_();
            if (!(livingEntity instanceof Player) || (targetPlayer = (Player)livingEntity).m_7500_() || targetPlayer.m_5833_() || !(this.m_20280_((Entity)targetPlayer) <= 3600.0)) {
                this.m_6710_(livingAttacker);
            }
        }
        this.applySmiteAntiRegen(source);
        if (this.isFireDamage(source)) {
            float barrierDamage = amount;
            this.setFrostBarrier(this.getFrostBarrier() - barrierDamage);
            this.frostBarrierRegenDelay = this.ghiaccioFrostBarrierRegenDelayAfterFireDamage();
        } else if (!source.m_269533_(DamageTypeTags.f_268738_)) {
            amount = this.applyFrostBarrierReduction(amount);
        }
        if (!this.m_9236_().f_46443_) {
            if (source.m_7639_() instanceof LivingEntity) {
                ++this.hitTimes;
            }
            if (!this.m_21525_() && this.m_21223_() > amount && this.hitTimes >= this.getHitTimeTeleport()) {
                this.teleport();
                this.hitTimes = 0;
            }
        }
        if (this.m_21224_()) {
            this.deathBlow = source;
        }
        return super.m_6469_(source, amount);
    }

    public void m_146917_(int ticks) {
        super.m_146917_(0);
    }

    protected void handleMinionSummonCasting() {
        if (TimeFreezeManager.isEntityFrozen((Entity)this)) {
            return;
        }
        if (this.minionSummonWarmup > 0) {
            --this.minionSummonWarmup;
            this.setCasting(true);
            this.setPunching(false);
            this.m_21573_().m_26573_();
            this.m_20242_(true);
            this.m_20334_(this.m_20184_().f_82479_ * 0.2, 0.02, this.m_20184_().f_82481_ * 0.2);
            LivingEntity target = this.m_5448_();
            if (target != null) {
                this.faceTargetForSpell(target);
            }
            if (this.minionSummonWarmup == 0) {
                this.summonFrostMinions();
                this.setCasting(false);
                this.m_20242_(false);
                this.postCastLock = 20;
                this.minionSummonCooldown = this.isSecondPhase() ? 2400 : 1500;
            }
            return;
        }
        if (this.isCasting() || this.frostNovaWarmup > 0 || this.iceStormWarmup > 0 || this.iceStormShotsLeft > 0 || this.isDodging() || this.isSettingUpSecond() || this.m_21224_() || this.minionSummonCooldown > 0 || this.postCastLock > 0) {
            return;
        }
        if (!this.hasValidTarget()) {
            return;
        }
        LivingEntity target = this.m_5448_();
        int currentMinions = this.countNearbyFrostMinions();
        if (currentMinions >= 8) {
            return;
        }
        if (this.m_217043_().m_188503_(this.isSecondPhase() ? 80 : 120) != 0) {
            return;
        }
        this.minionSummonWarmup = 50;
        this.setCasting(true);
        this.setPunching(false);
        this.m_21573_().m_26573_();
        this.m_20242_(true);
        if (!this.m_20067_()) {
            this.m_5496_((SoundEvent)ModSounds.FROST_PREPARE_SPELL.get(), 2.0f, 0.8f);
        }
    }

    protected int countNearbyFrostMinions() {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return 0;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        return serverLevel.m_6443_(LivingEntity.class, this.m_20191_().m_82400_(96.0), entity -> {
            if (entity instanceof IceMonarchEntity) {
                IceMonarchEntity monarch = (IceMonarchEntity)((Object)entity);
                return monarch.getTrueOwner() == this;
            }
            ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey((Object)entity.m_6095_());
            if (id == null) {
                return false;
            }
            String path = id.toString();
            return path.equals("goety:ice_golem");
        }).size();
    }

    protected boolean isInvalidPlayerTarget(LivingEntity target) {
        if (!(target instanceof Player)) {
            return false;
        }
        Player player = (Player)target;
        return player.m_7500_() || player.m_5833_();
    }

    protected void summonFrostMinions() {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        LivingEntity target = this.m_5448_();
        int summonCycles = 1;
        if (this.m_9236_().m_46791_() == Difficulty.HARD) {
            summonCycles = 2;
        }
        for (int i = 0; i < summonCycles; ++i) {
            this.trySummonFrostMinion(serverLevel, (EntityType<? extends Mob>)((EntityType)ModEntityType.ICE_GOLEM.get()), target, 1.75, 1.35);
            if (this.countOwnedIceMonarchs(serverLevel) <= 0) {
                this.trySummonIceMonarch(serverLevel, target);
            }
            if (!this.isSecondPhase()) continue;
            this.trySummonFrostMinion(serverLevel, (EntityType<? extends Mob>)((EntityType)ModEntityType.ICE_GOLEM.get()), target, 1.75, 1.35);
        }
    }

    protected int countOwnedIceMonarchs(ServerLevel serverLevel) {
        return serverLevel.m_6443_(IceMonarchEntity.class, this.m_20191_().m_82400_(96.0), monarch -> monarch.getTrueOwner() == this).size();
    }

    protected void trySummonIceMonarch(ServerLevel serverLevel, LivingEntity target) {
        IceMonarchEntity monarch = (IceMonarchEntity)((EntityType)GoetyMasteryOfMagicModEntities.ICE_MONARCH.get()).m_20615_((Level)serverLevel);
        if (monarch == null) {
            return;
        }
        BlockPos spawnPos = this.findMinionSpawnPos(serverLevel);
        monarch.m_7678_((double)spawnPos.m_123341_() + 0.5, (double)spawnPos.m_123342_() + 2.0, (double)spawnPos.m_123343_() + 0.5, this.m_146908_(), this.m_146909_());
        monarch.m_6518_((ServerLevelAccessor)serverLevel, serverLevel.m_6436_(spawnPos), MobSpawnType.MOB_SUMMONED, null, null);
        monarch.setTrueOwner((LivingEntity)this);
        if (target != null && target.m_6084_() && !MobUtil.areAllies((Entity)monarch, (Entity)target)) {
            monarch.m_6710_(target);
        }
        serverLevel.m_7967_((Entity)monarch);
    }

    protected void trySummonFrostMinion(ServerLevel serverLevel, EntityType<? extends Mob> entityType, LivingEntity target, double healthMultiplier, double damageMultiplier) {
        Owned owned;
        Mob mob = (Mob)entityType.m_20615_((Level)serverLevel);
        if (mob == null) {
            return;
        }
        BlockPos spawnPos = this.findMinionSpawnPos(serverLevel);
        mob.m_7678_((double)spawnPos.m_123341_() + 0.5, (double)spawnPos.m_123342_(), (double)spawnPos.m_123343_() + 0.5, this.m_146908_(), this.m_146909_());
        if (mob instanceof Owned) {
            owned = (Owned)mob;
            owned.setTrueOwner((LivingEntity)this);
        }
        mob.m_6518_((ServerLevelAccessor)serverLevel, serverLevel.m_6436_(spawnPos), MobSpawnType.MOB_SUMMONED, null, null);
        if (mob instanceof Owned) {
            owned = (Owned)mob;
            owned.setTrueOwner((LivingEntity)this);
        }
        this.empowerFrostMinion(mob, healthMultiplier, damageMultiplier);
        if (target != null && target.m_6084_() && !MobUtil.areAllies((Entity)mob, (Entity)target)) {
            mob.m_6710_(target);
        }
        serverLevel.m_7967_((Entity)mob);
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

    protected void empowerFrostMinion(Mob mob, double healthMultiplier, double damageMultiplier) {
        AttributeInstance attackDamage;
        AttributeInstance maxHealth = mob.m_21051_(Attributes.f_22276_);
        if (maxHealth != null) {
            maxHealth.m_22100_(maxHealth.m_22115_() * healthMultiplier);
            mob.m_21153_(mob.m_21233_());
        }
        if ((attackDamage = mob.m_21051_(Attributes.f_22281_)) != null) {
            attackDamage.m_22100_(attackDamage.m_22115_() * damageMultiplier);
        }
        mob.m_7292_(new MobEffectInstance((MobEffect)GoetyEffects.CHILL_HIDE.get(), -1, 3, false, false));
        mob.m_7292_(new MobEffectInstance((MobEffect)GoetyEffects.BUFF.get(), -1, 2, false, false));
        ResourceLocation mobId = ForgeRegistries.ENTITY_TYPES.getKey((Object)mob.m_6095_());
        if (mobId != null && (mobId.toString().equals("goety:bound_iceologer") || mobId.toString().equals("goety:bound_cryologer"))) {
            mob.m_7292_(new MobEffectInstance(MobEffects.f_19607_, -1, 0, false, false));
        }
    }

    protected float applyFrostBarrierReduction(float amount) {
        float maxBarrier = this.ghiaccioFrostBarrierMax();
        if (maxBarrier <= 0.0f) {
            return amount;
        }
        float barrierRatio = this.getFrostBarrier() / maxBarrier;
        float maxReduction = this.ghiaccioMaxDamageReduction();
        if (this.m_9236_().m_46791_() == Difficulty.NORMAL) {
            maxReduction *= 0.8f;
        } else if (this.m_9236_().m_46791_() == Difficulty.EASY) {
            maxReduction *= 0.5f;
        }
        float reduction = barrierRatio * maxReduction;
        return amount * (1.0f - reduction);
    }

    protected boolean isFireDamage(DamageSource source) {
        ResourceKey key = source.m_269150_().m_203543_().orElse(null);
        if (key == null) {
            return false;
        }
        String id = key.m_135782_().toString();
        Map<String, List<String>> groups = MobWeaknessConfig.getDamageGroups();
        List<String> fireGroup = groups.get("fire");
        if (fireGroup == null) {
            return false;
        }
        return fireGroup.contains(id);
    }

    protected void m_6475_(DamageSource source, float amount) {
        if (!source.m_269533_(DamageTypeTags.f_268738_)) {
            amount = this.m_9236_().m_46791_() == Difficulty.HARD ? Math.min(amount, 30.0f) : Math.min(amount, 60.0f);
        }
        super.m_6475_(source, amount);
    }

    public void m_5634_(float amount) {
        if (!this.isSmited()) {
            super.m_5634_(amount);
        }
    }

    public boolean isSmited() {
        return this.antiRegen > 0;
    }

    public int getHitTimeTeleport() {
        if (this.isSecondPhase() && this.m_21223_() <= this.m_21233_() * 0.25f) {
            return 1;
        }
        if (this.isSecondPhase()) {
            return 2;
        }
        return 5;
    }

    public boolean performPunchAttack(LivingEntity target) {
        if (this.isCasting() || this.frostNovaWarmup > 0 || this.timeStopWarmup > 0) {
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
        Vec3 toTarget = target.m_20182_().m_82546_(this.m_20182_());
        Vec3 horizontal = new Vec3(toTarget.f_82479_, 0.0, toTarget.f_82481_);
        if (horizontal.m_82556_() > 0.001) {
            Vec3 step = horizontal.m_82541_().m_82490_(0.18);
            this.m_20334_(step.f_82479_, this.m_20184_().f_82480_, step.f_82481_);
        }
        this.m_21011_(InteractionHand.MAIN_HAND, true);
        if (!this.m_9236_().f_46443_) {
            this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), this.m_217043_().m_188499_() ? (SoundEvent)GoetyMasteryOfMagicModSounds.GHIACCIO_PUNCH_1.get() : (SoundEvent)GoetyMasteryOfMagicModSounds.GHIACCIO_PUNCH_2.get(), this.m_5720_(), 1.5f, 1.0f);
        }
        this.setPunching(true);
        int hitCount = 0;
        Level level = this.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            Vec3 origin = this.m_146892_();
            Vec3 forward = this.m_20154_().m_82541_();
            Vec3 end = origin.m_82549_(forward.m_82490_(2.6));
            AABB searchBox = this.m_20191_().m_82369_(forward.m_82490_(2.6)).m_82400_(2.2);
            for (LivingEntity living2 : serverLevel.m_6443_(LivingEntity.class, searchBox, living -> {
                if (living == null) {
                    return false;
                }
                if (living == this) {
                    return false;
                }
                if (!living.m_6084_()) {
                    return false;
                }
                if (!this.canHarmTarget((Entity)living)) {
                    return false;
                }
                AABB box = living.m_20191_().m_82400_(0.55);
                return box.m_82390_(origin) || box.m_82371_(origin, end).isPresent();
            })) {
                int baseDamage = Mth.m_14107_((double)this.ghiaccioMeleeBaseDamage());
                int percentDamage = Mth.m_14143_((float)(living2.m_21233_() * this.ghiaccioMeleeTargetMaxHealthPercent()));
                int totalDamage = baseDamage + percentDamage;
                if (!living2.m_6469_(this.m_269291_().m_269333_((LivingEntity)this), (float)totalDamage)) continue;
                ++hitCount;
                int freezeAmount = 70;
                int maxFreeze = living2.m_146891_() + 120;
                living2.m_146917_(Math.min(living2.m_146888_() + freezeAmount, maxFreeze));
            }
        }
        return hitCount > 0;
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.f_19797_ % 5 == 0) {
            this.bossInfo.update();
        }
        this.bossInfo.m_142711_(this.m_21223_() / this.m_21233_());
        if (this.m_9236_().f_46443_) {
            this.setupAnimationStates();
        }
    }

    public void m_8107_() {
        super.m_8107_();
        if (this.m_9236_().m_46791_() == Difficulty.PEACEFUL) {
            this.m_142687_(Entity.RemovalReason.DISCARDED);
            return;
        }
        if (!this.configInitialized) {
            this.configInitialized = true;
            this.setConfigurableAttributes();
            this.setFrostBarrier(this.ghiaccioFrostBarrierMax());
            this.m_21153_(this.m_21233_());
        }
        if (this.timeStopCooldown > 0) {
            --this.timeStopCooldown;
        }
        if (this.iceStormCooldown > 0) {
            --this.iceStormCooldown;
        }
        if (this.postCastLock > 0) {
            --this.postCastLock;
        }
        if (this.antiRegen > 0) {
            --this.antiRegen;
        }
        if (this.minionSummonCooldown > 0) {
            --this.minionSummonCooldown;
        }
        if (this.teleportCooldown > 0) {
            --this.teleportCooldown;
        }
        if (this.frostBarrierRegenDelay > 0) {
            --this.frostBarrierRegenDelay;
        }
        if (this.priorityTargetResetCooldown > 0) {
            --this.priorityTargetResetCooldown;
        }
        if (!this.m_9236_().f_46443_) {
            if (!this.isSmited()) {
                this.handleHealthRegen();
            }
            if (this.isInvalidPlayerTarget(this.m_5448_())) {
                this.m_6710_(null);
                this.m_21573_().m_26573_();
                this.m_21561_(false);
                this.priorityTargetResetCooldown = 0;
            }
            if (this.priorityTargetResetCooldown <= 0 || this.m_5448_() == null || !this.m_5448_().m_6084_()) {
                this.priorityTargetResetCooldown = 100;
                this.updatePriorityTarget();
            }
            this.handleTimeStopCasting();
            if (!TimeFreezeManager.isEntityFrozen((Entity)this) && this.timeStopWarmup <= 0 && !this.isTimeStopCasting()) {
                this.handleIceStormCasting();
                this.handleFrostNovaCasting();
                this.handleMinionSummonCasting();
            }
            this.handleFrostBarrierRegen();
            this.freezeNearbyFluidSources();
            this.removePlayerFlight();
            this.tryProjectileDodge();
            this.handleDodgeMovement();
            this.handleChillHide();
            this.tryVerticalTargetTeleport();
        }
        if (this.isSettingUpSecond()) {
            this.setPunching(false);
            this.m_21573_().m_26573_();
            for (Entity entity : this.m_9236_().m_6443_(LivingEntity.class, this.m_20191_().m_82400_(3.0), Entity::m_6084_)) {
                if (MobUtil.areAllies((Entity)this, (Entity)entity)) continue;
                this.barrier(entity, (LivingEntity)this);
            }
            if (!this.m_9236_().f_46443_) {
                this.hitTimes = 0;
                Level level = this.m_9236_();
                if (level instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel)level;
                    this.spawnSecondPhaseIceParticles(serverLevel);
                }
            }
            if (this.f_19797_ % 5 == 0) {
                this.m_5634_(0.015625f * this.m_21233_());
            }
            if (this.m_21223_() >= this.m_21233_()) {
                this.setSettingUpSecond(false);
                this.setSecondPhase(true);
                if (!this.hasUsedSecondPhaseTimeStop) {
                    this.hasUsedSecondPhaseTimeStop = true;
                    this.timeStopCooldown = 0;
                    this.postCastLock = 0;
                    this.startTimeStopCast();
                }
            }
        }
    }

    protected boolean hasValidTarget() {
        LivingEntity target = this.m_5448_();
        if (target == null) {
            return false;
        }
        if (!target.m_6084_()) {
            return false;
        }
        if (this.isInvalidPlayerTarget(target)) {
            this.m_6710_(null);
            this.m_21573_().m_26573_();
            this.m_21561_(false);
            return false;
        }
        return true;
    }

    protected void tryVerticalTargetTeleport() {
        if (!this.hasValidTarget()) {
            return;
        }
        LivingEntity target = this.m_5448_();
        if (this.teleportCooldown > 0) {
            return;
        }
        if (this.isCasting() || this.isDodging() || this.isSettingUpSecond() || this.m_21224_() || this.postCastLock > 0) {
            return;
        }
        double yDifference = target.m_20186_() - this.m_20186_();
        if (yDifference < 10.0) {
            return;
        }
        if (this.m_217043_().m_188503_(20) != 0) {
            return;
        }
        this.teleportNearTargetGround(target, 8.0, 18.0);
    }

    protected boolean teleportNearTargetGround(LivingEntity target, double horizontalRange, double verticalRange) {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return false;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        BlockPos targetPos = target.m_20183_();
        for (int i = 0; i < 48; ++i) {
            int dx = this.m_217043_().m_188503_((int)horizontalRange * 2 + 1) - (int)horizontalRange;
            int dz = this.m_217043_().m_188503_((int)horizontalRange * 2 + 1) - (int)horizontalRange;
            BlockPos pos = targetPos.m_7918_(dx, 0, dz);
            int topY = Mth.m_14107_((double)(target.m_20186_() + verticalRange));
            int bottomY = Mth.m_14107_((double)(target.m_20186_() - 4.0));
            for (int y = topY; y >= bottomY; --y) {
                double z;
                double yPos;
                double x;
                BlockPos ground = new BlockPos(pos.m_123341_(), y, pos.m_123343_());
                if (!serverLevel.m_8055_(ground.m_7495_()).m_280555_() || !serverLevel.m_8055_(ground).m_60795_() || !serverLevel.m_8055_(ground.m_7494_()).m_60795_() || !this.m_20984_(x = (double)ground.m_123341_() + 0.5, yPos = (double)ground.m_123342_(), z = (double)ground.m_123343_() + 0.5, false)) continue;
                int n = this.teleportCooldown = this.isSecondPhase() && this.m_21223_() <= this.m_21233_() * 0.25f ? 15 : 30;
                if (!this.m_20067_()) {
                    this.m_5496_(SoundEvents.f_11852_, 1.5f, 0.8f);
                }
                return true;
            }
        }
        return false;
    }

    public void barrier(Entity entity, LivingEntity source) {
        double d0 = entity.m_20185_() - source.m_20185_();
        double d1 = entity.m_20189_() - source.m_20189_();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
        MobUtil.forcePush((Entity)entity, (double)(d0 / d2 * 2.0), (double)0.1, (double)(d1 / d2 * 2.0));
    }

    protected void spawnSecondPhaseIceParticles(ServerLevel serverLevel) {
        ServerParticleUtil.windParticle((ServerLevel)serverLevel, (ColorUtil)new ColorUtil(0x9FDFFF), (float)2.0f, (float)1.5f, (int)this.m_19879_(), (Vec3)this.m_20182_());
        ServerParticleUtil.windParticle((ServerLevel)serverLevel, (ColorUtil)new ColorUtil(0xDFFBFF), (float)4.0f, (float)0.5f, (int)this.m_19879_(), (Vec3)this.m_20182_());
        serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_175821_, this.m_20185_(), this.m_20186_() + 0.5, this.m_20189_(), 40, 1.5, 1.0, 1.5, 0.08);
        serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123754_, this.m_20185_(), this.m_20186_() + 0.5, this.m_20189_(), 25, 1.25, 0.75, 1.25, 0.12);
    }

    protected void handleChillHide() {
        float maxBarrier;
        float barrier = this.getFrostBarrier();
        if (barrier > (maxBarrier = this.ghiaccioFrostBarrierMax()) * 0.5f) {
            this.m_7292_(new MobEffectInstance((MobEffect)GoetyEffects.CHILL_HIDE.get(), 40, 4, false, false));
        }
    }

    protected void handleFrostNovaCasting() {
        if (this.frostNovaCooldown > 0) {
            --this.frostNovaCooldown;
        }
        if (TimeFreezeManager.isEntityFrozen((Entity)this)) {
            return;
        }
        if (this.frostNovaWarmup > 0) {
            --this.frostNovaWarmup;
            this.setCasting(true);
            this.setPunching(false);
            this.m_21573_().m_26573_();
            this.m_20242_(true);
            double hoverSpeed = 0.035;
            if (this.frostNovaWarmup > 10) {
                this.m_20334_(this.m_20184_().f_82479_ * 0.2, hoverSpeed, this.m_20184_().f_82481_ * 0.2);
            } else {
                this.m_20334_(this.m_20184_().f_82479_ * 0.2, 0.0, this.m_20184_().f_82481_ * 0.2);
            }
            LivingEntity target = this.m_5448_();
            if (target != null) {
                this.faceTargetForSpell(target);
            }
            if (this.frostNovaWarmup == 0) {
                this.castDirectionalFrostNovas();
                this.setCasting(false);
                this.m_20242_(false);
                this.postCastLock = 20;
                this.frostNovaCooldown = this.isSecondPhase() ? 240 : 360;
            }
            return;
        }
        if (this.isCasting() || this.iceStormWarmup > 0 || this.iceStormShotsLeft > 0 || this.minionSummonWarmup > 0 || this.timeStopWarmup > 0 || this.postCastLock > 0) {
            return;
        }
        if (!this.hasValidTarget()) {
            return;
        }
        LivingEntity target = this.m_5448_();
        if (this.isDodging() || this.isSettingUpSecond() || this.m_21224_()) {
            return;
        }
        if (this.frostNovaCooldown > 0) {
            return;
        }
        double distance = this.m_20280_((Entity)target);
        if (distance > 324.0) {
            return;
        }
        if (!this.m_142582_((Entity)target)) {
            return;
        }
        if (this.m_217043_().m_188503_(30) != 0) {
            return;
        }
        this.frostNovaDiagonal = this.m_217043_().m_188499_();
        this.frostNovaWarmup = 60;
        this.setCasting(true);
        this.setPunching(false);
        this.m_21573_().m_26573_();
        this.m_20242_(true);
        if (!this.m_20067_()) {
            this.m_5496_((SoundEvent)ModSounds.FROST_PREPARE_SPELL.get(), 2.0f, 0.8f);
        }
    }

    protected void castDirectionalFrostNovas() {
        Vec3 forward;
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        LivingEntity target = this.m_5448_();
        if (target != null) {
            Vec3 toTarget = target.m_20182_().m_82546_(this.m_20182_());
            forward = new Vec3(toTarget.f_82479_, 0.0, toTarget.f_82481_);
        } else {
            Vec3 look = this.m_20154_();
            forward = new Vec3(look.f_82479_, 0.0, look.f_82481_);
        }
        if (forward.m_82556_() < 0.001) {
            forward = new Vec3(0.0, 0.0, 1.0);
        }
        forward = forward.m_82541_();
        Vec3 right = new Vec3(-forward.f_82481_, 0.0, forward.f_82479_).m_82541_();
        Vec3[] directions = this.frostNovaDiagonal ? new Vec3[]{forward.m_82549_(right).m_82541_(), forward.m_82546_(right).m_82541_(), forward.m_82490_(-1.0).m_82549_(right).m_82541_(), forward.m_82490_(-1.0).m_82546_(right).m_82541_()} : new Vec3[]{forward, forward.m_82490_(-1.0), right, right.m_82490_(-1.0)};
        for (Vec3 direction : directions) {
            Vec3 center = this.m_20182_().m_82549_(direction.m_82490_(5.0));
            this.castSingleFrostNova(serverLevel, center);
        }
        this.m_5496_((SoundEvent)ModSounds.ICE_CHUNK_HIT.get(), 2.0f, 0.6f);
    }

    protected void castSingleFrostNova(ServerLevel serverLevel, Vec3 center) {
        int potency = 5;
        int duration = 5;
        float radius = 5.0f;
        float damage = 20.0f + (float)potency;
        float maxDamage = 23.0f + (float)potency;
        float trueDamage = Mth.m_14036_((float)(damage + (float)this.m_217043_().m_188503_(Math.max(1, (int)(maxDamage - damage + 1.0f)))), (float)damage, (float)maxDamage);
        this.createFrostNovaParticleBall(serverLevel, center, (int)radius);
        serverLevel.m_8767_((ParticleOptions)new ShockwaveParticleOption(0.0f, radius * 2.0f, 1), center.f_82479_, center.f_82480_ + 0.5, center.f_82481_, 0, 0.0, 0.0, 0.0, 0.0);
        AABB area = new AABB(center.f_82479_ - (double)radius, center.f_82480_ - (double)radius, center.f_82481_ - (double)radius, center.f_82479_ + (double)radius, center.f_82480_ + (double)radius, center.f_82481_ + (double)radius);
        for (LivingEntity living : serverLevel.m_6443_(LivingEntity.class, area, this::canHarmTarget)) {
            if (living.m_20238_(center) > (double)(radius * radius)) continue;
            living.m_6469_(ModDamageSource.directFreeze((LivingEntity)this), trueDamage);
            living.m_7292_(new MobEffectInstance((MobEffect)GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks((int)5) * duration, 0));
        }
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

    protected void freezeNearbyFluidSources() {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        if (this.f_19797_ % 2 != 0) {
            return;
        }
        int radius = 16;
        int radiusSq = radius * radius;
        BlockPos center = this.m_20183_();
        int checksPerTick = 512;
        int seed = this.f_19797_ / 2;
        for (int i = 0; i < checksPerTick; ++i) {
            BlockPos pos;
            BlockState state;
            int index = seed * checksPerTick + i;
            int dx = Math.floorMod(index, radius * 2 + 1) - radius;
            int dz = Math.floorMod(index / (radius * 2 + 1), radius * 2 + 1) - radius;
            int dy = Math.floorMod(index / ((radius * 2 + 1) * (radius * 2 + 1)), 9) - 4;
            if (dx * dx + dy * dy + dz * dz > radiusSq || !(state = serverLevel.m_8055_(pos = center.m_7918_(dx, dy, dz))).m_60819_().m_76170_()) continue;
            if (state.m_60819_().m_76152_() == Fluids.f_76193_) {
                serverLevel.m_7731_(pos, Blocks.f_50126_.m_49966_(), 3);
                continue;
            }
            if (state.m_60819_().m_76152_() != Fluids.f_76195_) continue;
            serverLevel.m_7731_(pos, Blocks.f_50080_.m_49966_(), 3);
        }
    }

    protected void updatePriorityTarget() {
        TargetingConditions playerTargeting = TargetingConditions.m_148352_().m_26883_(60.0).m_26888_(player -> {
            if (!(player instanceof Player)) {
                return false;
            }
            Player targetPlayer = (Player)player;
            return targetPlayer.m_6084_() && !targetPlayer.m_7500_() && !targetPlayer.m_5833_() && !MobUtil.areAllies((Entity)this, (Entity)targetPlayer);
        });
        Player nearestPlayer = this.m_9236_().m_45946_(playerTargeting, (LivingEntity)this);
        if (nearestPlayer != null) {
            this.m_6710_((LivingEntity)nearestPlayer);
            return;
        }
        LivingEntity attacker = this.m_21188_();
        if (attacker != null && attacker.m_6084_() && !this.isOwnMinion((Entity)attacker) && this.m_20280_((Entity)attacker) <= 3600.0) {
            this.m_6710_(attacker);
            return;
        }
        IronGolem nearestGolem = null;
        double nearestDistance = Double.MAX_VALUE;
        for (IronGolem golem : this.m_9236_().m_6443_(IronGolem.class, this.m_20191_().m_82400_(32.0), LivingEntity::m_6084_)) {
            double distance = this.m_20280_((Entity)golem);
            if (!(distance < nearestDistance)) continue;
            nearestDistance = distance;
            nearestGolem = golem;
        }
        if (nearestGolem != null) {
            this.m_6710_((LivingEntity)nearestGolem);
        }
    }

    protected void handleHealthRegen() {
        if (this.isSmited()) {
            return;
        }
        int regenRate = 12;
        if (this.isSecondPhase()) {
            regenRate = 5;
        }
        if (this.f_19797_ % regenRate == 0 && this.m_21223_() < this.m_21233_()) {
            this.m_5634_(1.0f);
        }
    }

    protected void handleFrostBarrierRegen() {
        if (this.frostBarrierRegenDelay > 0) {
            return;
        }
        float max = this.ghiaccioFrostBarrierMax();
        if (this.getFrostBarrier() >= max) {
            return;
        }
        float regenPerSecond = this.ghiaccioFrostBarrierRegen();
        float regenPerTick = regenPerSecond / 20.0f;
        this.setFrostBarrier(this.getFrostBarrier() + regenPerTick);
    }

    protected void removePlayerFlight() {
        for (Player player : this.m_9236_().m_6443_(Player.class, this.m_20191_().m_82400_(32.0), EntitySelector.f_20406_)) {
            player.m_150110_().f_35935_ = false;
            player.m_6885_();
        }
    }

    protected void tryProjectileDodge() {
        if (this.isCasting() || this.timeStopWarmup > 0 || this.iceStormWarmup > 0 || this.frostNovaWarmup > 0 || this.minionSummonWarmup > 0 || this.isDodging() || this.isSettingUpSecond() || this.m_21224_() || this.m_21525_()) {
            return;
        }
        AABB area = this.m_20191_().m_82400_(24.0);
        Projectile bestProjectile = null;
        double bestTime = Double.MAX_VALUE;
        for (Projectile projectile : this.m_9236_().m_6443_(Projectile.class, area, Entity::m_6084_)) {
            double impactTime;
            if (!this.isDangerousProjectile(projectile) || !this.canSeeProjectile(projectile) || (impactTime = this.getProjectileImpactTime(projectile)) < 0.0 || !(impactTime < bestTime)) continue;
            bestTime = impactTime;
            bestProjectile = projectile;
        }
        if (bestProjectile != null) {
            this.startPerpendicularDodge(bestProjectile.m_20184_(), 5.0, 5);
        }
    }

    protected double getProjectileImpactTime(Projectile projectile) {
        Vec3 projectilePos = projectile.m_20182_();
        Vec3 velocity = projectile.m_20184_();
        if (velocity.m_82556_() < 0.0025) {
            return -1.0;
        }
        Vec3 selfCenter = this.m_20191_().m_82399_();
        Vec3 toSelf = selfCenter.m_82546_(projectilePos);
        double time = toSelf.m_82526_(velocity) / velocity.m_82556_();
        if (time < 0.0 || time > 16.0) {
            return -1.0;
        }
        Vec3 closest = projectilePos.m_82549_(velocity.m_82490_(time));
        AABB expandedBox = this.m_20191_().m_82400_(1.15);
        if (!expandedBox.m_82390_(closest)) {
            return -1.0;
        }
        return time;
    }

    protected boolean isDangerousProjectile(Projectile projectile) {
        Entity owner = projectile.m_19749_();
        if (owner == this) {
            return false;
        }
        if (owner != null && MobUtil.areAllies((Entity)this, (Entity)owner)) {
            return false;
        }
        Vec3 motion = projectile.m_20184_();
        return !(motion.m_82556_() < 0.01);
    }

    public boolean m_7307_(Entity entity) {
        if (entity == this) {
            return true;
        }
        return this.isOwnMinion(entity);
    }

    protected boolean isOwnMinion(Entity entity) {
        Owned owned;
        if (entity == null) {
            return false;
        }
        if (entity == this) {
            return true;
        }
        return entity instanceof Owned && (owned = (Owned)entity).getTrueOwner() == this;
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
        if (this.isOwnMinion((Entity)living)) {
            return false;
        }
        return !(living instanceof Player) || !(player = (Player)living).m_7500_() && !player.m_5833_();
    }

    protected boolean canSeeProjectile(Projectile projectile) {
        double minDot;
        Vec3 dir;
        Vec3 eye = this.m_146892_();
        Vec3 projectilePos = projectile.m_20182_();
        Vec3 toProjectile = projectilePos.m_82546_(eye);
        if (toProjectile.m_82556_() > 576.0) {
            return false;
        }
        Vec3 look = this.m_20252_(1.0f).m_82541_();
        double dot = look.m_82526_(dir = toProjectile.m_82541_());
        if (dot < (minDot = Math.cos(Math.toRadians(115.0)))) {
            return false;
        }
        return this.m_142582_((Entity)projectile);
    }

    protected boolean projectileWillHit(Projectile projectile) {
        return this.getProjectileImpactTime(projectile) >= 0.0;
    }

    protected boolean startProjectileDodge(Projectile projectile) {
        Vec3 velocity = projectile.m_20184_();
        if (velocity.m_82556_() < 0.0025) {
            return false;
        }
        Vec3 horizontalVelocity = new Vec3(velocity.f_82479_, 0.0, velocity.f_82481_);
        if (horizontalVelocity.m_82556_() < 0.0025) {
            Vec3 toProjectile = projectile.m_20182_().m_82546_(this.m_20182_());
            horizontalVelocity = new Vec3(toProjectile.f_82479_, 0.0, toProjectile.f_82481_);
        }
        if (horizontalVelocity.m_82556_() < 0.0025) {
            return false;
        }
        Vec3 projectileDir = horizontalVelocity.m_82541_();
        Vec3 left = new Vec3(-projectileDir.f_82481_, 0.0, projectileDir.f_82479_).m_82541_();
        Vec3 right = new Vec3(projectileDir.f_82481_, 0.0, -projectileDir.f_82479_).m_82541_();
        Vec3 chosen = null;
        int direction = 0;
        LivingEntity target = this.m_5448_();
        if (target != null) {
            Vec3 toTarget = target.m_20182_().m_82546_(this.m_20182_());
            Vec3 horizontalTarget = new Vec3(toTarget.f_82479_, 0.0, toTarget.f_82481_);
            if (horizontalTarget.m_82556_() > 0.001) {
                double rightTargetDot;
                double leftTargetDot = left.m_82526_(horizontalTarget.m_82541_());
                if (leftTargetDot >= (rightTargetDot = right.m_82526_(horizontalTarget.m_82541_()))) {
                    if (this.canDodgeTo(left)) {
                        chosen = left;
                        direction = -1;
                    } else if (this.canDodgeTo(right)) {
                        chosen = right;
                        direction = 1;
                    }
                } else if (this.canDodgeTo(right)) {
                    chosen = right;
                    direction = 1;
                } else if (this.canDodgeTo(left)) {
                    chosen = left;
                    direction = -1;
                }
            }
        }
        if (chosen == null) {
            if (this.canDodgeTo(left)) {
                chosen = left;
                direction = -1;
            } else if (this.canDodgeTo(right)) {
                chosen = right;
                direction = 1;
            }
        }
        if (chosen == null) {
            return false;
        }
        this.dodgeStart = this.m_20182_();
        this.dodgeEnd = this.m_20182_().m_82549_(chosen.m_82490_(5.0));
        this.dodgeTicks = 5;
        this.dodgeTotalTicks = 5;
        this.dodgeCooldown = 0;
        this.setDodgeDirection(direction);
        this.m_20242_(true);
        this.m_21573_().m_26573_();
        this.setPunching(false);
        return true;
    }

    protected boolean canDodgeTo(Vec3 direction) {
        Vec3 targetPos = this.m_20182_().m_82549_(direction.m_82490_(5.0));
        AABB targetBox = this.m_20191_().m_82383_(targetPos.m_82546_(this.m_20182_()));
        return this.m_9236_().m_45756_((Entity)this, targetBox);
    }

    protected void startPerpendicularDodge(Vec3 projectileVelocity, double distance, int durationTicks) {
        Vec3 attackDirection = new Vec3(projectileVelocity.f_82479_, 0.0, projectileVelocity.f_82481_);
        if (attackDirection.m_82556_() < 1.0E-4) {
            return;
        }
        attackDirection = attackDirection.m_82541_();
        Vec3 dodgeDirection = new Vec3(-attackDirection.f_82481_, 0.0, attackDirection.f_82479_).m_82541_();
        if (this.f_19796_.m_188499_()) {
            dodgeDirection = dodgeDirection.m_82490_(-1.0);
            this.setDodgeDirection(-1);
        } else {
            this.setDodgeDirection(1);
        }
        if (!this.canDodgeTo(dodgeDirection)) {
            Vec3 oppositeDirection = dodgeDirection.m_82490_(-1.0);
            if (!this.canDodgeTo(oppositeDirection)) {
                return;
            }
            dodgeDirection = oppositeDirection;
            this.setDodgeDirection(this.getDodgeDirection() * -1);
        }
        this.dodgeStart = this.m_20182_();
        this.dodgeEnd = this.dodgeStart.m_82549_(dodgeDirection.m_82490_(distance));
        this.dodgeTotalTicks = durationTicks;
        this.dodgeTicks = durationTicks;
        this.dodgeCooldown = 0;
        this.m_20242_(true);
        this.m_21573_().m_26573_();
        this.setPunching(false);
    }

    protected void handleDodgeMovement() {
        if (!this.isDodging()) {
            if (this.m_20068_()) {
                this.m_20242_(false);
            }
            if (this.getDodgeDirection() != 0) {
                this.setDodgeDirection(0);
            }
            return;
        }
        LivingEntity target = this.m_5448_();
        if (target != null) {
            this.m_21563_().m_24960_((Entity)target, 60.0f, 60.0f);
            this.m_21391_((Entity)target, 60.0f, 60.0f);
        }
        int elapsed = this.dodgeTotalTicks - this.dodgeTicks + 1;
        double progress = (double)elapsed / (double)this.dodgeTotalTicks;
        Vec3 nextPos = Vec3Util.lerp((float)((float)progress), (Vec3)this.dodgeStart, (Vec3)this.dodgeEnd);
        Vec3 movement = nextPos.m_82546_(this.m_20182_());
        this.m_6478_(MoverType.SELF, movement);
        this.m_20256_(Vec3.f_82478_);
        --this.dodgeTicks;
        if (this.dodgeTicks <= 0) {
            this.m_20242_(false);
            this.setDodgeDirection(0);
        }
    }

    protected void teleport() {
        if (this.m_9236_().f_46443_) {
            return;
        }
        if (this.m_21525_() || !this.m_6084_() || this.isSettingUpSecond() || this.isDodging() || this.isCasting() || this.frostNovaWarmup > 0 || this.postCastLock > 0) {
            return;
        }
        if (this.teleportCooldown > 0) {
            return;
        }
        this.teleportCooldown = this.isSecondPhase() && this.m_21223_() <= this.m_21233_() * 0.25f ? 15 : (this.isSecondPhase() ? 25 : 40);
        int attempts = this.isSecondPhase() && this.m_21223_() <= this.m_21233_() * 0.25f ? 128 : 64;
        double range = this.isSecondPhase() && this.m_21223_() <= this.m_21233_() * 0.25f ? 32.0 : 24.0;
        LivingEntity target = this.m_5448_();
        if (target != null && target.m_20186_() - this.m_20186_() >= 10.0 && this.teleportNearTargetGround(target, 8.0, 18.0)) {
            return;
        }
        for (int i = 0; i < attempts; ++i) {
            double z;
            double y;
            double x = this.m_20185_() + (this.f_19796_.m_188500_() - 0.5) * range;
            if (!this.m_20984_(x, y = target != null ? target.m_20186_() : this.m_20186_(), z = this.m_20189_() + (this.f_19796_.m_188500_() - 0.5) * range, false)) continue;
            this.m_9236_().m_214171_(GameEvent.f_238175_, this.m_20182_(), GameEvent.Context.m_223717_((Entity)this));
            if (this.m_20067_()) break;
            this.m_5496_(SoundEvents.f_11852_, 1.5f, 0.8f);
            break;
        }
    }

    protected void handleIceStormCasting() {
        if (TimeFreezeManager.isEntityFrozen((Entity)this)) {
            return;
        }
        if (this.iceStormWarmup > 0 || this.iceStormShotsLeft > 0) {
            this.setCasting(true);
            this.setPunching(false);
            this.m_21573_().m_26573_();
            this.m_20242_(true);
            this.m_20334_(this.m_20184_().f_82479_ * 0.2, 0.025, this.m_20184_().f_82481_ * 0.2);
            LivingEntity target = this.m_5448_();
            if (target != null) {
                this.faceTargetForSpell(target);
            }
            if (this.iceStormWarmup > 0) {
                --this.iceStormWarmup;
                if (this.iceStormWarmup == 0) {
                    this.iceStormShotsLeft = 3;
                    this.iceStormShotDelay = 0;
                }
                return;
            }
            if (this.iceStormShotDelay > 0) {
                --this.iceStormShotDelay;
                return;
            }
            if (this.iceStormShotsLeft > 0) {
                int shotIndex = 3 - this.iceStormShotsLeft;
                if (shotIndex == 0) {
                    this.castSingleTrackedIceStorm(8, 3, 3, 1);
                } else if (shotIndex == 1) {
                    this.castSingleTrackedIceStorm(10, 3, 3, 2);
                } else {
                    this.castSingleTrackedIceStorm(12, 3, 3, 3);
                }
                --this.iceStormShotsLeft;
                if (this.iceStormShotsLeft > 0) {
                    this.iceStormShotDelay = 30;
                } else {
                    this.setCasting(false);
                    this.m_20242_(false);
                    this.postCastLock = 20;
                    this.iceStormCooldown = this.isSecondPhase() ? 320 : 480;
                }
            }
            return;
        }
        if (this.isCasting() || this.frostNovaWarmup > 0 || this.isDodging() || this.isSettingUpSecond() || this.m_21224_() || this.iceStormCooldown > 0 || this.postCastLock > 0) {
            return;
        }
        if (!this.hasValidTarget()) {
            return;
        }
        LivingEntity target = this.m_5448_();
        if (!this.m_142582_((Entity)target)) {
            return;
        }
        double distance = this.m_20280_((Entity)target);
        if (distance < 25.0 || distance > 784.0) {
            return;
        }
        if (this.m_217043_().m_188503_(35) != 0) {
            return;
        }
        this.iceStormWarmup = 20;
        this.iceStormShotsLeft = 0;
        this.iceStormShotDelay = 0;
        this.setCasting(true);
        this.setPunching(false);
        this.m_21573_().m_26573_();
        this.m_20242_(true);
        if (!this.m_20067_()) {
            this.m_5496_((SoundEvent)ModSounds.FROST_PREPARE_SPELL.get(), 2.0f, 0.8f);
        }
    }

    protected void castSingleTrackedIceStorm(int potency, int duration, int range, int velocity) {
        Vec3 direction;
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        LivingEntity target = this.m_5448_();
        if (target != null && target.m_6084_()) {
            this.faceTargetForSpell(target);
            Vec3 from = new Vec3(this.m_20185_(), this.m_20188_() - 0.2, this.m_20189_());
            Vec3 to = target.m_146892_();
            direction = to.m_82546_(from);
            direction = direction.m_82556_() < 0.001 ? this.m_20252_(1.0f) : direction.m_82541_();
        } else {
            direction = this.m_20252_(1.0f);
        }
        IceStorm iceStorm = new IceStorm(this.m_20185_() + direction.f_82479_ / 2.0, this.m_20188_() - 0.2, this.m_20189_() + direction.f_82481_ / 2.0, direction.f_82479_, direction.f_82480_, direction.f_82481_, (Level)serverLevel);
        iceStorm.setExtraDamage((float)potency);
        iceStorm.setDuration(duration);
        iceStorm.setRange(range);
        iceStorm.setBoltSpeed(velocity);
        iceStorm.m_5602_((Entity)this);
        serverLevel.m_7967_((Entity)iceStorm);
        if (target != null && this.canHarmTarget((Entity)target) && this.m_142582_((Entity)target)) {
            target.m_6469_(ModDamageSource.directFreeze((LivingEntity)this), 8.0f + (float)potency);
            target.m_7292_(new MobEffectInstance((MobEffect)GoetyEffects.FREEZING.get(), MathHelper.secondsToTicks((int)5) * duration, 0));
        }
        if (!this.m_20067_()) {
            this.m_5496_((SoundEvent)ModSounds.WIND_BLAST.get(), 2.0f, 0.75f);
        }
    }

    protected void faceTargetForSpell(LivingEntity target) {
        this.m_21563_().m_24960_((Entity)target, 90.0f, 90.0f);
        this.m_21391_((Entity)target, 90.0f, 90.0f);
        this.m_146922_(this.m_6080_());
        this.f_20884_ = this.f_20883_ = this.m_6080_();
    }

    protected void castSingleIceStorm(ServerLevel serverLevel, int potency, int duration, int range, int velocity) {
        Vec3 view = this.m_20252_(1.0f);
        IceStorm iceStorm = new IceStorm(this.m_20185_() + view.f_82479_ / 2.0, this.m_20188_() - 0.2, this.m_20189_() + view.f_82481_ / 2.0, view.f_82479_, view.f_82480_, view.f_82481_, (Level)serverLevel);
        iceStorm.setExtraDamage((float)potency);
        iceStorm.setDuration(duration);
        iceStorm.setRange(range);
        iceStorm.setBoltSpeed(velocity);
        iceStorm.m_5602_((Entity)this);
        serverLevel.m_7967_((Entity)iceStorm);
    }

    protected void setupAnimationStates() {
        boolean isSpecialAnimation;
        if (this.isTimeStopCasting()) {
            this.leMondeAnimationState.m_216982_(this.f_19797_);
            this.flyingCastingAnimationState.m_216973_();
            this.punchingFastAnimationState.m_216973_();
            this.flyingSxAnimationState.m_216973_();
            this.flyingDxAnimationState.m_216973_();
            this.flyingStillAnimationState.m_216973_();
            this.flyingFrontalAnimationState.m_216973_();
            this.flyingBackAnimationState.m_216973_();
            this.idleAnimationState.m_216973_();
            return;
        }
        this.leMondeAnimationState.m_216973_();
        if (this.isCasting()) {
            this.flyingCastingAnimationState.m_216982_(this.f_19797_);
            this.punchingFastAnimationState.m_216973_();
            this.flyingSxAnimationState.m_216973_();
            this.flyingDxAnimationState.m_216973_();
            this.flyingStillAnimationState.m_216973_();
            this.flyingFrontalAnimationState.m_216973_();
            this.flyingBackAnimationState.m_216973_();
            this.idleAnimationState.m_216973_();
            return;
        }
        this.flyingCastingAnimationState.m_216973_();
        if (this.isPunching()) {
            this.punchingFastAnimationState.m_216982_(this.f_19797_);
        } else {
            this.punchingFastAnimationState.m_216973_();
        }
        int dodgeDirection = this.getDodgeDirection();
        if (dodgeDirection < 0) {
            this.flyingSxAnimationState.m_216982_(this.f_19797_);
        } else {
            this.flyingSxAnimationState.m_216973_();
        }
        if (dodgeDirection > 0) {
            this.flyingDxAnimationState.m_216982_(this.f_19797_);
        } else {
            this.flyingDxAnimationState.m_216973_();
        }
        if (dodgeDirection == 0) {
            this.flyingStillAnimationState.m_216973_();
            this.flyingFrontalAnimationState.m_216973_();
            this.flyingBackAnimationState.m_216973_();
        }
        boolean bl = isSpecialAnimation = this.isTimeStopCasting() || this.isCasting() || this.isPunching() || dodgeDirection != 0;
        if (!isSpecialAnimation) {
            this.idleAnimationState.m_216982_(this.f_19797_);
        } else {
            this.idleAnimationState.m_216973_();
        }
    }

    protected boolean m_6107_() {
        return super.m_6107_() || this.isSettingUpSecond() || this.isDodging();
    }

    public boolean m_142582_(Entity entity) {
        return !this.isSettingUpSecond() && super.m_142582_(entity);
    }

    public void m_6667_(DamageSource source) {
        if (this.deathTime > 0) {
            super.m_6667_(source);
        } else {
            this.deathBlow = source;
        }
    }

    protected void m_6153_() {
        ServerLevel serverLevel;
        Level level;
        ++this.deathTime;
        if (this.deathTime == 1) {
            this.antiRegen = 0;
            this.antiRegenTotal = 0;
            this.setPunching(false);
            this.setDodgeDirection(0);
            this.m_20242_(true);
            this.m_20256_(Vec3.f_82478_);
            this.m_21573_().m_26573_();
            this.killOwnedFrostMinions();
            this.dropGhiaccioDeathLoot();
            this.m_5496_(this.getTrueDeathSound(), 5.0f, 1.0f);
            level = this.m_9236_();
            if (level instanceof ServerLevel) {
                serverLevel = (ServerLevel)level;
                this.spawnDeathFrostParticles(serverLevel);
            }
        }
        if (this.deathTime <= 12) {
            this.m_20334_(0.0, 0.04, 0.0);
            level = this.m_9236_();
            if (level instanceof ServerLevel) {
                serverLevel = (ServerLevel)level;
                this.spawnDeathFrostParticles(serverLevel);
            }
        }
        if (this.deathTime >= 12) {
            this.m_142687_(Entity.RemovalReason.KILLED);
        }
    }

    protected void spawnDeathFrostParticles(ServerLevel serverLevel) {
        serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_175821_, this.m_20185_(), this.m_20186_() + (double)this.m_20206_() * 0.5, this.m_20189_(), 80, (double)this.m_20205_() * 0.75, (double)this.m_20206_() * 0.45, (double)this.m_20205_() * 0.75, 0.12);
        serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123754_, this.m_20185_(), this.m_20186_() + (double)this.m_20206_() * 0.5, this.m_20189_(), 40, (double)this.m_20205_() * 0.6, (double)this.m_20206_() * 0.35, (double)this.m_20205_() * 0.6, 0.16);
        serverLevel.m_8767_((ParticleOptions)((SimpleParticleType)ModParticleTypes.FROST_NOVA.get()), this.m_20185_(), this.m_20186_() + (double)this.m_20206_() * 0.5, this.m_20189_(), 25, (double)this.m_20205_() * 0.5, (double)this.m_20206_() * 0.35, (double)this.m_20205_() * 0.5, 0.2);
    }

    protected void killOwnedFrostMinions() {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        for (Mob mob2 : serverLevel.m_6443_(Mob.class, this.m_20191_().m_82400_(96.0), mob -> {
            Owned owned;
            return mob instanceof Owned && (owned = (Owned)mob).getTrueOwner() == this;
        })) {
            mob2.m_146870_();
        }
    }

    protected void dropGhiaccioDeathLoot() {
        if (this.m_9236_().f_46443_) {
            return;
        }
        for (int i = 0; i < 3; ++i) {
            this.m_19983_(new ItemStack((ItemLike)ModItems.TREASURE_POUCH.get()));
        }
        this.m_19983_(new ItemStack((ItemLike)GoetyMasteryOfMagicModItems.GLACIAL_UNHOLY_BLOOD.get()));
        if (this.f_19796_.m_188501_() < 0.25f) {
            this.m_19983_(new ItemStack((ItemLike)GoetyMasteryOfMagicModItems.CHILLING_TIMES_DISK.get()));
        }
    }

    public void m_6074_() {
        this.m_142687_(Entity.RemovalReason.KILLED);
    }

    public boolean m_142535_(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Nullable
    public Entity getLeader() {
        return null;
    }

    protected void handleTimeStopCasting() {
        if (this.timeStopWarmup > 0) {
            --this.timeStopWarmup;
            this.setCasting(true);
            this.setTimeStopCasting(true);
            this.setPunching(false);
            this.m_21573_().m_26573_();
            this.m_20242_(true);
            this.m_20334_(this.m_20184_().f_82479_ * 0.1, 0.025, this.m_20184_().f_82481_ * 0.1);
            LivingEntity target = this.m_5448_();
            if (target != null) {
                this.faceTargetForSpell(target);
            }
            if (this.timeStopWarmup == 0) {
                Level level = this.m_9236_();
                if (level instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel)level;
                    TimeFreezeManager.create(serverLevel, (LivingEntity)this, 16.0, 200);
                }
                this.setCasting(false);
                this.setTimeStopCasting(false);
                this.setPunching(false);
                this.m_20242_(false);
                this.postCastLock = 0;
                this.timeStopCooldown = this.getTimeStopCooldownByHealth();
            }
            return;
        }
        if (this.isTimeStopCasting()) {
            this.setTimeStopCasting(false);
        }
        if (this.isCasting() || this.frostNovaWarmup > 0 || this.iceStormWarmup > 0 || this.iceStormShotsLeft > 0 || this.minionSummonWarmup > 0 || this.isDodging() || this.isSettingUpSecond() || this.m_21224_() || this.timeStopCooldown > 0 || this.postCastLock > 0) {
            return;
        }
        if (!this.hasValidTarget()) {
            return;
        }
        LivingEntity target = this.m_5448_();
        if (!this.m_142582_((Entity)target)) {
            return;
        }
        if (!this.isSecondPhase()) {
            return;
        }
        if (this.m_217043_().m_188503_(this.isSecondPhase() ? 80 : 120) != 0) {
            return;
        }
        this.startTimeStopCast();
    }

    protected void startTimeStopCast() {
        this.timeStopWarmup = 60;
        this.setCasting(true);
        this.setTimeStopCasting(true);
        this.setPunching(false);
        this.m_21573_().m_26573_();
        this.m_20242_(true);
    }

    protected int getTimeStopCooldownByHealth() {
        float healthRatio = this.m_21223_() / this.m_21233_();
        if (healthRatio <= 0.1f) {
            return 400;
        }
        float clamped = Mth.m_14036_((float)healthRatio, (float)0.1f, (float)1.0f);
        float progress = (clamped - 0.1f) / 0.9f;
        return Mth.m_14143_((float)(400.0f + progress * 800.0f));
    }

    public void setLeader(@Nullable Entity entity) {
    }

    public boolean isBarterable() {
        return false;
    }

    public static void init() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.m_21552_();
        builder = builder.m_22268_(Attributes.f_22279_, 0.22);
        builder = builder.m_22268_(Attributes.f_22276_, 666.0);
        builder = builder.m_22268_(Attributes.f_22284_, 12.0);
        builder = builder.m_22268_(Attributes.f_22285_, 8.0);
        builder = builder.m_22268_(Attributes.f_22281_, 14.0);
        builder = builder.m_22268_(Attributes.f_22277_, 40.0);
        builder = builder.m_22268_(Attributes.f_22278_, 0.75);
        builder = builder.m_22268_(Attributes.f_22282_, 0.5);
        builder = builder.m_22268_((Attribute)ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0);
        return builder;
    }

    class SecondPhaseGoal
    extends Goal {
        SecondPhaseGoal() {
        }

        public boolean m_8036_() {
            return GhiaccioEntity.this.m_21223_() <= GhiaccioEntity.this.m_21233_() / 2.0f && GhiaccioEntity.this.m_5448_() != null && !GhiaccioEntity.this.isSecondPhase() && !GhiaccioEntity.this.isSettingUpSecond();
        }

        public void m_8037_() {
            GhiaccioEntity.this.setSettingUpSecond(true);
        }
    }

    static class GhiaccioPunchComboGoal
    extends Goal {
        private final GhiaccioEntity mob;
        private final double speedModifier;
        private int attackCooldown;
        private int pathRecalculateCooldown;

        public GhiaccioPunchComboGoal(GhiaccioEntity mob, double speedModifier) {
            this.mob = mob;
            this.speedModifier = speedModifier;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean m_8036_() {
            LivingEntity target = this.mob.m_5448_();
            return target != null && target.m_6084_() && !this.mob.isInvalidPlayerTarget(target) && !this.mob.isSettingUpSecond();
        }

        public boolean m_8045_() {
            LivingEntity target = this.mob.m_5448_();
            return target != null && target.m_6084_() && !this.mob.isInvalidPlayerTarget(target) && !this.mob.isSettingUpSecond();
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
            if (target == null) {
                this.mob.setPunching(false);
                return;
            }
            if (this.mob.isCasting() || this.mob.frostNovaWarmup > 0 || this.mob.iceStormWarmup > 0 || this.mob.minionSummonWarmup > 0 || this.mob.timeStopWarmup > 0 || this.mob.postCastLock > 0) {
                this.mob.setPunching(false);
                this.mob.m_21573_().m_26573_();
                return;
            }
            this.mob.m_21563_().m_24960_((Entity)target, 60.0f, 60.0f);
            if (this.mob.isDodging()) {
                this.mob.setPunching(false);
                return;
            }
            double distance = this.mob.m_20280_((Entity)target);
            double reach = this.getAttackReachSqr(target);
            if (this.pathRecalculateCooldown > 0) {
                --this.pathRecalculateCooldown;
            }
            if (distance > reach) {
                this.mob.setPunching(false);
                if (this.pathRecalculateCooldown <= 0) {
                    this.pathRecalculateCooldown = 2;
                    this.mob.m_21573_().m_5624_((Entity)target, this.speedModifier);
                }
            } else {
                this.mob.m_21573_().m_26573_();
                this.mob.m_20256_(this.mob.m_20184_().m_82542_(0.35, 1.0, 0.35));
                this.mob.setPunching(true);
            }
            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }
            if (distance <= reach && this.attackCooldown <= 0) {
                this.mob.performPunchAttack(target);
                this.attackCooldown = 5;
            }
        }

        protected double getAttackReachSqr(LivingEntity target) {
            double reach = (double)this.mob.m_20205_() * 0.5 + (double)target.m_20205_() * 0.5 + 2.0;
            return reach * reach;
        }
    }
}

