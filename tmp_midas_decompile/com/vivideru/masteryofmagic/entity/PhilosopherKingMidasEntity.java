/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.entities.ISpellEntity
 *  com.Polarice3.Goety.common.entities.projectiles.DeathArrow
 *  com.Polarice3.Goety.common.entities.projectiles.EnderGoo
 *  com.Polarice3.Goety.utils.MobUtil
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerBossEvent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.BossEvent$BossBarColor
 *  net.minecraft.world.BossEvent$BossBarOverlay
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageTypes
 *  net.minecraft.world.entity.AnimationState
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LightningBolt
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobType
 *  net.minecraft.world.entity.PathfinderMob
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
 *  net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
 *  net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
 *  net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
 *  net.minecraft.world.entity.ai.navigation.PathNavigation
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.chunk.LevelChunk
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.util.FakePlayer
 *  net.minecraftforge.network.NetworkHooks
 *  net.minecraftforge.network.PlayMessages$SpawnEntity
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.jetbrains.annotations.Nullable
 */
package com.vivideru.masteryofmagic.entity;

import com.Polarice3.Goety.api.entities.ISpellEntity;
import com.Polarice3.Goety.common.entities.projectiles.DeathArrow;
import com.Polarice3.Goety.common.entities.projectiles.EnderGoo;
import com.Polarice3.Goety.utils.MobUtil;
import com.vivideru.masteryofmagic.block.entity.ChargedRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.entity.GoldenSwordProjectileEntity;
import com.vivideru.masteryofmagic.entity.MidasAlchemicalCircleEntity;
import com.vivideru.masteryofmagic.entity.midas.MidasFlightMoveControl;
import com.vivideru.masteryofmagic.events.MidasWorldSpawnSuppression;
import com.vivideru.masteryofmagic.goldification.GoldificationManager;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlocks;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModSounds;
import com.vivideru.masteryofmagic.spells.midas.MidasAlchemicalCirclesSpell;
import com.vivideru.masteryofmagic.spells.midas.MidasAlchemicalOrbVolleySpell;
import com.vivideru.masteryofmagic.spells.midas.MidasAlchemicalShockwaveSpell;
import com.vivideru.masteryofmagic.spells.midas.MidasBossSpell;
import com.vivideru.masteryofmagic.spells.midas.MidasGoldenSwordBarrageSpell;
import com.vivideru.masteryofmagic.spells.midas.MidasMagicBarrierSpell;
import com.vivideru.masteryofmagic.spells.midas.MidasPhilosopherBoltSpell;
import com.vivideru.masteryofmagic.spells.midas.MidasPhilosopherSphereSpell;
import com.vivideru.masteryofmagic.spells.midas.MidasPhilosopherWindSlashSpell;
import com.vivideru.masteryofmagic.spells.midas.MidasSpellThreatRegistry;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class PhilosopherKingMidasEntity
extends Monster {
    private static final int GLOBAL_SPELL_WINDUP_TICKS = 30;
    public static final int CASTING_SWORDS_FLAG = 1;
    public static final int CASTING_BARRIER_FLAG = 2;
    public static final int CASTING_SPHERE_FLAG = 4;
    public static final int CASTING_SLASH_FLAG = 8;
    public static final int CASTING_BOLT_FLAG = 16;
    public static final int CASTING_SHOCKWAVE_FLAG = 32;
    public static final int CASTING_ALCHEMICAL_CIRCLES_FLAG = 64;
    public static final double AURA_RADIUS = 6.5;
    public static final double AURA_PULSE_AMPLITUDE = 0.5;
    public static final long AURA_GOLDIFICATION_TICKS = 200L;
    public static final long AURA_SHATTER_DELAY = 20L;
    private static final int FLIGHT_ANIMATION_DELAY_TICKS = 40;
    private static final int FLIGHT_ANIMATION_IDLE = 0;
    private static final int FLIGHT_ANIMATION_FORWARD = 1;
    private static final int FLIGHT_ANIMATION_BACKWARD = -1;
    private static final double BASE_ARMOR = 5.0;
    private static final float BASE_DAMAGE_MULTIPLIER = 0.875f;
    private static final float MAX_DAMAGE_PER_HIT = 25.0f;
    private static final float MAX_ADAPTIVE_DEFENSE = 0.95f;
    private static final long ADAPTIVE_DEFENSE_DURATION_TICKS = 1200L;
    private static final double REAR_BLIND_CONE_DOT = -0.7071067811865476;
    private static final int DAMAGE_BARRIER_REACTION_TICKS = 60;
    private static final EntityDataAccessor<Integer> CASTING_FLAGS = SynchedEntityData.m_135353_(PhilosopherKingMidasEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> SLASH_ANIMATION_TICKS = SynchedEntityData.m_135353_(PhilosopherKingMidasEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> CAST_WINDUP_TICKS = SynchedEntityData.m_135353_(PhilosopherKingMidasEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState flyingForwardAnimationState = new AnimationState();
    public final AnimationState flyingBackwardAnimationState = new AnimationState();
    public final AnimationState castingOneAnimationState = new AnimationState();
    public final AnimationState castingTwoAnimationState = new AnimationState();
    public final AnimationState castingThreeAnimationState = new AnimationState();
    private final ServerBossEvent bossEvent = new ServerBossEvent(this.m_5446_(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
    private final Long2LongOpenHashMap auraBlockStart = new Long2LongOpenHashMap();
    private final Map<UUID, Long> auraEntityStart = new HashMap<UUID, Long>();
    private final ActiveCast[] spellSlots = new ActiveCast[2];
    private final Map<ResourceLocation, Integer> spellCooldowns = new HashMap<ResourceLocation, Integer>();
    private final Map<String, ArrayDeque<AdaptiveDefenseStack>> adaptiveDefenses = new HashMap<String, ArrayDeque<AdaptiveDefenseStack>>();
    private final Map<UUID, Boolean> rearThreatAwareness = new HashMap<UUID, Boolean>();
    private final ArrayDeque<Long> recentDamageTicks = new ArrayDeque();
    private int transmutationSoundCooldown;
    private int roarCooldown;
    private int antiRegenerationTicks;
    private int pendingFlightAnimation = 0;
    private int pendingFlightAnimationTicks;
    private int activeFlightAnimation = 0;
    private int swordBarragePattern;
    private int philosopherSlashVolleyCount = 2;
    private int philosopherSlashesSpawned;
    private int philosopherSlashNextTick;
    private float previousPhilosopherSlashRoll = Float.NaN;
    private int alchemicalOrbVolleyCount;
    private int alchemicalOrbsSpawned;
    private int alchemicalOrbNextTick;
    private final Set<UUID> alchemicalShockwaveHits = new HashSet<UUID>();
    private Vec3 alchemicalShockwaveOrigin = Vec3.f_82478_;
    private Vec3 alchemicalShockwaveDirection = Vec3.f_82478_;
    @Nullable
    private UUID stationaryTargetUuid;
    @Nullable
    private Vec3 stationaryTargetAnchor;
    private int stationaryTargetTicks;
    @Nullable
    private BlockPos philosopherBoltTarget;
    private int nextLazethystScanTick;
    private boolean applyingNormalDamage;
    private int pendingBarrierDamageReactionTicks;
    private int apocalypseLightningCooldown = 40;
    @Nullable
    private Vec3 pendingBarrierThreatPosition;

    public PhilosopherKingMidasEntity(PlayMessages.SpawnEntity packet, Level level) {
        this((EntityType<? extends PhilosopherKingMidasEntity>)((EntityType)GoetyMasteryOfMagicModEntities.PHILOSOPHER_KING_MIDAS.get()), level);
    }

    public PhilosopherKingMidasEntity(EntityType<? extends PhilosopherKingMidasEntity> type, Level level) {
        super(type, level);
        this.f_21342_ = new MidasFlightMoveControl(this);
        this.m_20242_(true);
        this.m_21530_();
        this.f_21364_ = 1000;
        this.auraBlockStart.defaultReturnValue(Long.MIN_VALUE);
        this.bossEvent.m_7003_(false);
        this.bossEvent.m_7005_(true);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(CASTING_FLAGS, (Object)0);
        this.f_19804_.m_135372_(SLASH_ANIMATION_TICKS, (Object)0);
        this.f_19804_.m_135372_(CAST_WINDUP_TICKS, (Object)0);
    }

    protected PathNavigation m_6037_(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation((Mob)this, level);
        navigation.m_26440_(false);
        navigation.m_7008_(true);
        navigation.m_26443_(true);
        return navigation;
    }

    protected void m_8099_() {
        this.f_21345_.m_25352_(1, (Goal)new MaintainCombatAltitudeGoal(this));
        this.f_21345_.m_25352_(7, (Goal)new AscendToIdleAltitudeGoal(this));
        this.f_21345_.m_25352_(8, (Goal)new RandomLookAroundGoal((Mob)this));
        this.f_21346_.m_25352_(1, (Goal)new HurtByTargetGoal((PathfinderMob)this, new Class[0]));
        this.f_21346_.m_25352_(2, (Goal)new NearestAttackableTargetGoal((Mob)this, Player.class, 10, true, false, living -> {
            Player player;
            return living instanceof Player && !(player = (Player)living).m_7500_() && !player.m_5833_();
        }));
        this.f_21346_.m_25352_(3, (Goal)new NearestAttackableTargetGoal((Mob)this, Mob.class, 10, true, false, living -> living != this && living.m_6084_() && !(living instanceof Enemy)));
    }

    public void m_8119_() {
        if (!this.m_9236_().f_46443_ && this.m_9236_().m_46791_() == Difficulty.PEACEFUL) {
            this.m_142687_(Entity.RemovalReason.DISCARDED);
            return;
        }
        super.m_8119_();
        this.m_20242_(true);
        if (this.m_9236_().f_46443_) {
            this.updateAnimationStates();
        } else {
            Level level = this.m_9236_();
            if (level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                if ((Integer)this.f_19804_.m_135370_(SLASH_ANIMATION_TICKS) > 0) {
                    this.f_19804_.m_135381_(SLASH_ANIMATION_TICKS, (Object)((Integer)this.f_19804_.m_135370_(SLASH_ANIMATION_TICKS) - 1));
                }
                if ((Integer)this.f_19804_.m_135370_(CAST_WINDUP_TICKS) > 0) {
                    this.f_19804_.m_135381_(CAST_WINDUP_TICKS, (Object)((Integer)this.f_19804_.m_135370_(CAST_WINDUP_TICKS) - 1));
                }
                if (GoldificationManager.isEntityGoldified((Entity)this)) {
                    GoldificationManager.removeEntityGoldification((Entity)this);
                }
                serverLevel.m_8615_(18000L);
                serverLevel.m_8606_(6000, 0, false, false);
                serverLevel.m_46734_(0.0f);
                serverLevel.m_46707_(0.0f);
                MidasWorldSpawnSuppression.markActive(serverLevel);
                this.tickApocalypseLightning(serverLevel);
                if (this.transmutationSoundCooldown > 0) {
                    --this.transmutationSoundCooldown;
                }
                if (this.roarCooldown > 0) {
                    --this.roarCooldown;
                }
                if (this.antiRegenerationTicks > 0) {
                    --this.antiRegenerationTicks;
                } else if (this.f_19797_ % 40 == 0 && this.m_21223_() < this.m_21233_()) {
                    this.m_5634_(1.0f);
                }
                this.tickSpellcasting(serverLevel);
                this.tickMidasAura(serverLevel);
                this.bossEvent.m_142711_(this.m_21223_() / this.m_21233_());
            }
        }
    }

    public void m_142687_(Entity.RemovalReason reason) {
        if ((reason == Entity.RemovalReason.KILLED || reason == Entity.RemovalReason.DISCARDED) && this.m_21223_() > 0.0f && this.m_9236_().m_46791_() != Difficulty.PEACEFUL) {
            return;
        }
        super.m_142687_(reason);
    }

    public void m_21153_(float health) {
        if (this.f_19797_ > 0 && health <= 0.0f && this.m_21223_() > 0.0f && !this.applyingNormalDamage) {
            return;
        }
        super.m_21153_(health);
    }

    private void tickApocalypseLightning(ServerLevel serverLevel) {
        if (--this.apocalypseLightningCooldown > 0) {
            return;
        }
        this.apocalypseLightningCooldown = 40 + this.f_19796_.m_188503_(61);
        int lightningCount = 2 + this.f_19796_.m_188503_(3);
        for (int index = 0; index < lightningCount; ++index) {
            double angle = this.f_19796_.m_188500_() * 6.2831854820251465;
            double distance = 72.0 + this.f_19796_.m_188500_() * 56.0;
            int x = Mth.m_14107_((double)(this.m_20185_() + Math.cos(angle) * distance));
            int z = Mth.m_14107_((double)(this.m_20189_() + Math.sin(angle) * distance));
            int y = serverLevel.m_6924_(Heightmap.Types.MOTION_BLOCKING, x, z);
            LightningBolt lightning = (LightningBolt)EntityType.f_20465_.m_20615_((Level)serverLevel);
            if (lightning == null) continue;
            lightning.m_6027_((double)x + 0.5, (double)y, (double)z + 0.5);
            lightning.m_20874_(true);
            serverLevel.m_7967_((Entity)lightning);
        }
    }

    private void updateAnimationStates() {
        int desiredAnimation;
        boolean slashWindupAnimation;
        int castingFlags = (Integer)this.f_19804_.m_135370_(CASTING_FLAGS);
        boolean fastSlashAnimation = (castingFlags & 8) != 0 && (Integer)this.f_19804_.m_135370_(SLASH_ANIMATION_TICKS) > 0;
        boolean bl = slashWindupAnimation = (castingFlags & 8) != 0 && (Integer)this.f_19804_.m_135370_(CAST_WINDUP_TICKS) > 0;
        if ((castingFlags & 0x11) != 0 || fastSlashAnimation || slashWindupAnimation) {
            this.castingOneAnimationState.m_216982_(this.f_19797_);
        } else {
            this.castingOneAnimationState.m_216973_();
        }
        if ((castingFlags & 0x64) != 0) {
            this.castingTwoAnimationState.m_216982_(this.f_19797_);
        } else {
            this.castingTwoAnimationState.m_216973_();
        }
        if ((castingFlags & 2) != 0) {
            this.castingThreeAnimationState.m_216982_(this.f_19797_);
        } else {
            this.castingThreeAnimationState.m_216973_();
        }
        Vec3 movement = this.m_20184_();
        double motionSquared = movement.m_82556_();
        if (motionSquared < 0.0025) {
            this.pendingFlightAnimation = 0;
            this.pendingFlightAnimationTicks = 0;
            this.activeFlightAnimation = 0;
            this.flyingForwardAnimationState.m_216973_();
            this.flyingBackwardAnimationState.m_216973_();
            this.idleAnimationState.m_216982_(this.f_19797_);
            return;
        }
        Vec3 horizontal = new Vec3(movement.f_82479_, 0.0, movement.f_82481_);
        Vec3 look = new Vec3(this.m_20154_().f_82479_, 0.0, this.m_20154_().f_82481_);
        boolean movingBackwards = horizontal.m_82556_() > 1.0E-5 && look.m_82556_() > 1.0E-5 && horizontal.m_82541_().m_82526_(look.m_82541_()) < -0.15;
        int n = desiredAnimation = movingBackwards ? -1 : 1;
        if (this.pendingFlightAnimation != desiredAnimation) {
            this.pendingFlightAnimation = desiredAnimation;
            this.pendingFlightAnimationTicks = 1;
            this.activeFlightAnimation = 0;
        } else if (this.activeFlightAnimation == 0 && ++this.pendingFlightAnimationTicks >= 40) {
            this.activeFlightAnimation = desiredAnimation;
        }
        if (this.activeFlightAnimation == 0) {
            this.flyingForwardAnimationState.m_216973_();
            this.flyingBackwardAnimationState.m_216973_();
            this.idleAnimationState.m_216982_(this.f_19797_);
        } else if (this.activeFlightAnimation == -1) {
            this.idleAnimationState.m_216973_();
            this.flyingForwardAnimationState.m_216973_();
            this.flyingBackwardAnimationState.m_216982_(this.f_19797_);
        } else {
            this.idleAnimationState.m_216973_();
            this.flyingBackwardAnimationState.m_216973_();
            this.flyingForwardAnimationState.m_216982_(this.f_19797_);
        }
    }

    private void tickMidasAura(ServerLevel level) {
        long now = level.m_46467_();
        Vec3 center = this.m_20182_().m_82520_(0.0, (double)this.m_20206_() * 0.5, 0.0);
        double auraRadius = this.getAuraRadius(0.0f);
        double auraRadiusSquared = auraRadius * auraRadius;
        int minX = Mth.m_14107_((double)(center.f_82479_ - auraRadius));
        int minY = Math.max(level.m_141937_(), Mth.m_14107_((double)(center.f_82480_ - auraRadius)));
        int minZ = Mth.m_14107_((double)(center.f_82481_ - auraRadius));
        int maxX = Mth.m_14107_((double)(center.f_82479_ + auraRadius));
        int maxY = Math.min(level.m_151558_() - 1, Mth.m_14107_((double)(center.f_82480_ + auraRadius)));
        int maxZ = Mth.m_14107_((double)(center.f_82481_ + auraRadius));
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    cursor.m_122178_(x, y, z);
                    if (!(Vec3.m_82512_((Vec3i)cursor).m_82557_(center) <= auraRadiusSquared)) continue;
                    this.affectAuraBlock(level, (BlockPos)cursor, now);
                }
            }
        }
        AABB bounds = new AABB(center, center).m_82400_(auraRadius);
        for (Entity entity : level.m_6249_((Entity)this, bounds, this::canAuraAffectEntity)) {
            if (!PhilosopherKingMidasEntity.intersectsAura(center, entity.m_20191_(), auraRadiusSquared)) continue;
            this.affectAuraEntity(entity, now);
        }
        this.cleanupAuraTracking(level, now);
    }

    private void affectAuraBlock(ServerLevel level, BlockPos position, long now) {
        long key = position.m_121878_();
        long start = this.auraBlockStart.get(key);
        if (!GoldificationManager.isBlockGoldified(level, position)) {
            if (GoldificationManager.goldifyBlockForMidas(level, position.m_7949_(), 200L, (Entity)this)) {
                this.auraBlockStart.put(key, now);
                this.playAuraTransmutationSound(level);
            }
            return;
        }
        if (start == Long.MIN_VALUE) {
            this.auraBlockStart.put(key, now);
        } else if (now - start >= 20L && GoldificationManager.shatterBlock(level, position.m_7949_(), (Entity)this)) {
            this.auraBlockStart.remove(key);
        }
    }

    private void affectAuraEntity(Entity entity, long now) {
        UUID uuid = entity.m_20148_();
        Long start = this.auraEntityStart.get(uuid);
        if (!GoldificationManager.isEntityGoldified(entity)) {
            if (GoldificationManager.goldifyEntityForMidas(entity, 200L, (Entity)this)) {
                this.auraEntityStart.put(uuid, now);
                this.playAuraTransmutationSound((ServerLevel)entity.m_9236_());
            }
            return;
        }
        if (start == null) {
            this.auraEntityStart.put(uuid, now);
        } else if (now - start >= 20L && GoldificationManager.shatterEntity(entity, (Entity)this)) {
            this.auraEntityStart.remove(uuid);
        }
    }

    private boolean canAuraAffectEntity(Entity entity) {
        ItemEntity itemEntity;
        Player player;
        if (entity == this || entity.m_213877_() || entity.isMultipartEntity()) {
            return false;
        }
        if (entity instanceof GoldenSwordProjectileEntity) {
            return false;
        }
        if (entity instanceof MidasAlchemicalCircleEntity) {
            return false;
        }
        if (entity instanceof EnderGoo) {
            return false;
        }
        if (entity instanceof DeathArrow) {
            return true;
        }
        if (entity instanceof ISpellEntity) {
            return false;
        }
        if (OptionalModCompat.isIronsSpellbooksMagicProjectile(entity)) {
            return false;
        }
        if (entity instanceof Player && ((player = (Player)entity).m_7500_() || player.m_5833_())) {
            return false;
        }
        if (entity instanceof ItemEntity && (itemEntity = (ItemEntity)entity).m_32055_().m_150930_(Items.f_42587_)) {
            return false;
        }
        String packageName = entity.getClass().getPackageName().toLowerCase(Locale.ROOT);
        String className = entity.getClass().getSimpleName().toLowerCase(Locale.ROOT);
        String namespace = Optional.ofNullable(ForgeRegistries.ENTITY_TYPES.getKey((Object)entity.m_6095_())).map(location -> location.m_135827_().toLowerCase(Locale.ROOT)).orElse("");
        boolean projectileClass = packageName.contains(".projectile");
        boolean comesFromGoetyMagic = projectileClass && ("goety".equals(namespace) || "goety_mastery_of_magic".equals(namespace));
        boolean explicitlyMagical = className.contains("spellprojectile") || className.contains("magicprojectile") || className.contains("spellbolt") || className.contains("magicbolt") || packageName.contains(".magic.projectile") || packageName.contains(".spell.projectile");
        return !comesFromGoetyMagic && !explicitlyMagical;
    }

    private static boolean intersectsAura(Vec3 center, AABB box, double auraRadiusSquared) {
        double z;
        double y;
        double x = Mth.m_14008_((double)center.f_82479_, (double)box.f_82288_, (double)box.f_82291_);
        return center.m_82531_(x, y = Mth.m_14008_((double)center.f_82480_, (double)box.f_82289_, (double)box.f_82292_), z = Mth.m_14008_((double)center.f_82481_, (double)box.f_82290_, (double)box.f_82293_)) <= auraRadiusSquared;
    }

    private void playAuraTransmutationSound(ServerLevel level) {
        if (this.transmutationSoundCooldown > 0) {
            return;
        }
        level.m_6263_(null, this.m_20185_(), this.m_20186_() + (double)this.m_20206_() * 0.5, this.m_20189_(), (SoundEvent)GoetyMasteryOfMagicModSounds.MIDAS_TRANSMUTE.get(), SoundSource.HOSTILE, 1.35f, 0.94f + this.f_19796_.m_188501_() * 0.12f);
        this.transmutationSoundCooldown = 20;
    }

    private void cleanupAuraTracking(ServerLevel level, long now) {
        LongIterator blocks = this.auraBlockStart.keySet().iterator();
        while (blocks.hasNext()) {
            long packed = blocks.nextLong();
            long start = this.auraBlockStart.get(packed);
            if (now - start <= 220L && GoldificationManager.isBlockGoldified(level, BlockPos.m_122022_((long)packed))) continue;
            blocks.remove();
        }
        Iterator<Map.Entry<UUID, Long>> entities = this.auraEntityStart.entrySet().iterator();
        while (entities.hasNext()) {
            Map.Entry<UUID, Long> entry = entities.next();
            Entity entity = level.m_8791_(entry.getKey());
            if (now - entry.getValue() <= 220L && entity != null && GoldificationManager.isEntityGoldified(entity)) continue;
            entities.remove();
        }
    }

    private void tickSpellcasting(ServerLevel level) {
        this.updateStationaryTargetTracking(this.m_5448_());
        if (this.pendingBarrierDamageReactionTicks > 0) {
            --this.pendingBarrierDamageReactionTicks;
            if (this.pendingBarrierDamageReactionTicks == 0) {
                this.pendingBarrierThreatPosition = null;
            }
        }
        Iterator<Map.Entry<ResourceLocation, Integer>> cooldowns = this.spellCooldowns.entrySet().iterator();
        while (cooldowns.hasNext()) {
            Map.Entry<ResourceLocation, Integer> entry = cooldowns.next();
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                cooldowns.remove();
                continue;
            }
            entry.setValue(remaining);
        }
        for (int slot = 0; slot < this.spellSlots.length; ++slot) {
            ActiveCast cast = this.spellSlots[slot];
            if (cast == null) continue;
            LivingEntity target = cast.resolveTarget(level);
            if (!cast.started) {
                if (--cast.windupTicks > 0) continue;
                cast.started = true;
                cast.castTick = 0;
                cast.spell.start(level, this, target);
                continue;
            }
            cast.spell.tick(level, this, target, cast.castTick);
            ++cast.castTick;
            if (cast.spell.shouldContinue(level, this, target, cast.castTick)) continue;
            cast.spell.stop(level, this, target, cast.castTick);
            if (cast.spell.cooldownTicks() > 0) {
                this.spellCooldowns.put(cast.spell.id(), cast.spell.cooldownTicks());
            }
            this.spellSlots[slot] = null;
        }
        boolean barrierRaised = this.isCastingSpell(MidasMagicBarrierSpell.INSTANCE.id());
        if (!barrierRaised) {
            boolean needsBarrier;
            MidasSpellThreatRegistry.Threat detectedThreat = this.findDetectedBarrierThreat(level);
            boolean bl = needsBarrier = detectedThreat != null || this.hasPendingBarrierDamageReaction() || this.hasRecentDamagePressure();
            if (needsBarrier) {
                Vec3 threatPosition;
                Vec3 vec3 = threatPosition = detectedThreat == null ? this.pendingBarrierThreatPosition : detectedThreat.position();
                if (threatPosition == null && this.m_5448_() != null) {
                    threatPosition = this.m_5448_().m_20191_().m_82399_();
                }
                if (threatPosition != null && !this.isCastingAlchemicalShockwave()) {
                    this.faceBarrierThreat(threatPosition);
                }
                this.startSpell(level, MidasMagicBarrierSpell.INSTANCE, this.m_5448_());
                barrierRaised = this.isCastingSpell(MidasMagicBarrierSpell.INSTANCE.id());
            }
        }
        if (!barrierRaised) {
            boolean crowdedByEnemies;
            LivingEntity target = this.m_5448_();
            boolean circlesReady = this.canStartAlchemicalCircles(level, target);
            boolean bl = crowdedByEnemies = circlesReady && this.countNearbyEnemies(level, 20.0) > 4;
            if (crowdedByEnemies) {
                this.startSpell(level, MidasAlchemicalCirclesSpell.INSTANCE, target);
            }
            if (target != null && target.m_6084_() && !this.hasActiveOffensiveCast() && this.spellCooldowns.getOrDefault(MidasAlchemicalShockwaveSpell.INSTANCE.id(), 0) <= 0 && this.shouldStartAlchemicalShockwave(level, target)) {
                this.startSpell(level, MidasAlchemicalShockwaveSpell.INSTANCE, target);
            }
            if (!this.isCastingAlchemicalShockwave()) {
                BlockPos mechanism = this.findLazethystTarget(level);
                if (mechanism != null && !this.isCastingSpell(MidasPhilosopherBoltSpell.INSTANCE.id())) {
                    this.philosopherBoltTarget = mechanism;
                    this.startSpell(level, MidasPhilosopherBoltSpell.INSTANCE, target);
                }
                if (!crowdedByEnemies && circlesReady) {
                    this.startSpell(level, MidasAlchemicalCirclesSpell.INSTANCE, target);
                }
                if (target != null && target.m_6084_() && !this.isCastingSpell(MidasPhilosopherSphereSpell.INSTANCE.id()) && MidasPhilosopherSphereSpell.canSummon(level, this)) {
                    this.startSpell(level, MidasPhilosopherSphereSpell.INSTANCE, target);
                }
                if (target != null && target.m_6084_() && !this.isCastingSpell(MidasPhilosopherWindSlashSpell.INSTANCE.id()) && !this.isCastingSpell(MidasAlchemicalOrbVolleySpell.INSTANCE.id())) {
                    boolean launchOrbs = this.spellCooldowns.getOrDefault(MidasAlchemicalOrbVolleySpell.INSTANCE.id(), 0) <= 0 && this.f_19796_.m_188501_() < 0.35f;
                    this.startSpell(level, launchOrbs ? MidasAlchemicalOrbVolleySpell.INSTANCE : MidasPhilosopherWindSlashSpell.INSTANCE, target);
                }
                if (target != null && target.m_6084_() && !this.isCastingSpell(MidasGoldenSwordBarrageSpell.INSTANCE.id())) {
                    this.startSpell(level, MidasGoldenSwordBarrageSpell.INSTANCE, target);
                }
            }
        }
        this.synchronizeCastingFlags();
    }

    private boolean canStartAlchemicalCircles(ServerLevel level, @Nullable LivingEntity target) {
        return target != null && target.m_6084_() && this.m_20280_((Entity)target) < 400.0 && this.spellCooldowns.getOrDefault(MidasAlchemicalCirclesSpell.INSTANCE.id(), 0) <= 0 && !this.isCastingSpell(MidasAlchemicalCirclesSpell.INSTANCE.id());
    }

    private int countNearbyEnemies(ServerLevel level, double radius) {
        return level.m_6443_(LivingEntity.class, this.m_20191_().m_82400_(radius), living -> {
            Player player;
            return living != this && living.m_6084_() && (!(living instanceof Player) || !(player = (Player)living).m_7500_() && !player.m_5833_()) && !MobUtil.areAllies((Entity)this, (Entity)living);
        }).size();
    }

    @Nullable
    private BlockPos findLazethystTarget(ServerLevel level) {
        if (this.philosopherBoltTarget != null && this.m_20238_(Vec3.m_82512_((Vec3i)this.philosopherBoltTarget)) <= 4900.0 && PhilosopherKingMidasEntity.isDangerousMechanism(level, this.philosopherBoltTarget)) {
            return this.philosopherBoltTarget;
        }
        if (this.f_19797_ < this.nextLazethystScanTick) {
            return null;
        }
        this.nextLazethystScanTick = this.f_19797_ + 20;
        BlockPos best = null;
        double bestDistance = 4900.0;
        int cx = this.m_20183_().m_123341_() >> 4;
        int cz = this.m_20183_().m_123343_() >> 4;
        for (int x = cx - 5; x <= cx + 5; ++x) {
            for (int z = cz - 5; z <= cz + 5; ++z) {
                LevelChunk chunk = level.m_7726_().m_7131_(x, z);
                if (chunk == null) continue;
                for (BlockEntity be : chunk.m_62954_().values()) {
                    double distance;
                    if (!(be instanceof ChargedRunedLazethystBlockEntity) && !OptionalModCompat.isWaystonesBlock(be.m_58900_()) || !((distance = this.m_20238_(Vec3.m_82512_((Vec3i)be.m_58899_()))) < bestDistance)) continue;
                    bestDistance = distance;
                    best = be.m_58899_().m_7949_();
                }
            }
        }
        return best;
    }

    private static boolean isDangerousMechanism(ServerLevel level, BlockPos position) {
        return level.m_7702_(position) instanceof ChargedRunedLazethystBlockEntity || OptionalModCompat.isWaystonesBlock(level.m_8055_(position));
    }

    @Nullable
    public BlockPos getPhilosopherBoltTarget() {
        return this.philosopherBoltTarget;
    }

    public void setPhilosopherBoltTarget(@Nullable BlockPos target) {
        this.philosopherBoltTarget = target;
    }

    private boolean startSpell(ServerLevel level, MidasBossSpell spell, @Nullable LivingEntity target) {
        if (!spell.id().equals((Object)MidasMagicBarrierSpell.INSTANCE.id()) && this.isCastingSpell(MidasMagicBarrierSpell.INSTANCE.id())) {
            return false;
        }
        if (this.spellCooldowns.getOrDefault(spell.id(), 0) > 0 || this.isCastingSpell(spell.id())) {
            return false;
        }
        for (int slot = 0; slot < this.spellSlots.length; ++slot) {
            ActiveCast cast;
            if (this.spellSlots[slot] != null) continue;
            this.spellSlots[slot] = cast = new ActiveCast(spell, target);
            this.f_19804_.m_135381_(CAST_WINDUP_TICKS, (Object)30);
            this.synchronizeCastingFlags();
            return true;
        }
        return false;
    }

    @Nullable
    private MidasSpellThreatRegistry.Threat findDetectedBarrierThreat(ServerLevel level) {
        List<MidasSpellThreatRegistry.Threat> threats = MidasSpellThreatRegistry.dangerousThreats(level, this);
        HashSet<UUID> activeThreats = new HashSet<UUID>();
        for (MidasSpellThreatRegistry.Threat threat : threats) {
            activeThreats.add(threat.entity().m_20148_());
        }
        this.rearThreatAwareness.keySet().removeIf(uuid -> !activeThreats.contains(uuid));
        for (MidasSpellThreatRegistry.Threat threat : threats) {
            if (!this.isInsideRearBlindCone(threat.position())) {
                return threat;
            }
            boolean noticed = this.rearThreatAwareness.computeIfAbsent(threat.entity().m_20148_(), ignored -> this.f_19796_.m_188501_() < 0.2f);
            if (!noticed) continue;
            return threat;
        }
        return null;
    }

    private boolean isInsideRearBlindCone(Vec3 threatPosition) {
        Vec3 horizontalFacing = new Vec3(this.m_20154_().f_82479_, 0.0, this.m_20154_().f_82481_);
        Vec3 horizontalThreatDirection = threatPosition.m_82546_(this.m_146892_()).m_82542_(1.0, 0.0, 1.0);
        if (horizontalFacing.m_82556_() < 1.0E-6 || horizontalThreatDirection.m_82556_() < 1.0E-6) {
            return false;
        }
        return horizontalFacing.m_82541_().m_82526_(horizontalThreatDirection.m_82541_()) < -0.7071067811865476;
    }

    private void faceBarrierThreat(Vec3 threatPosition) {
        Vec3 direction = threatPosition.m_82546_(this.m_146892_());
        double horizontalDistance = Math.sqrt(direction.f_82479_ * direction.f_82479_ + direction.f_82481_ * direction.f_82481_);
        if (direction.m_82556_() < 1.0E-6) {
            return;
        }
        float yaw = (float)(Mth.m_14136_((double)direction.f_82481_, (double)direction.f_82479_) * 57.2957763671875) - 90.0f;
        float pitch = (float)(-(Mth.m_14136_((double)direction.f_82480_, (double)horizontalDistance) * 57.2957763671875));
        this.m_146922_(yaw);
        this.f_20885_ = yaw;
        this.f_20883_ = yaw;
        this.m_146926_(Mth.m_14036_((float)pitch, (float)-90.0f, (float)90.0f));
    }

    private boolean isCastingSpell(ResourceLocation spellId) {
        for (ActiveCast cast : this.spellSlots) {
            if (cast == null || !cast.spell.id().equals((Object)spellId)) continue;
            return true;
        }
        return false;
    }

    public boolean isCastingAlchemicalShockwave() {
        return this.isCastingSpell(MidasAlchemicalShockwaveSpell.INSTANCE.id());
    }

    private boolean hasActiveOffensiveCast() {
        for (ActiveCast cast : this.spellSlots) {
            if (cast == null || cast.spell.id().equals((Object)MidasMagicBarrierSpell.INSTANCE.id())) continue;
            return true;
        }
        return false;
    }

    private void updateStationaryTargetTracking(@Nullable LivingEntity target) {
        if (target == null || !target.m_6084_()) {
            this.stationaryTargetUuid = null;
            this.stationaryTargetAnchor = null;
            this.stationaryTargetTicks = 0;
            return;
        }
        if (!target.m_20148_().equals(this.stationaryTargetUuid) || this.stationaryTargetAnchor == null) {
            this.stationaryTargetUuid = target.m_20148_();
            this.stationaryTargetAnchor = target.m_20182_();
            this.stationaryTargetTicks = 0;
            return;
        }
        if (target.m_20182_().m_82557_(this.stationaryTargetAnchor) > 2.25) {
            this.stationaryTargetAnchor = target.m_20182_();
            this.stationaryTargetTicks = 0;
        } else {
            ++this.stationaryTargetTicks;
        }
    }

    private boolean shouldStartAlchemicalShockwave(ServerLevel level, LivingEntity target) {
        boolean camping = this.stationaryTargetTicks >= 100;
        boolean fortified = this.hasNearbyMagicalBarrier(level, target.m_20183_());
        float chance = 0.03f;
        if (camping) {
            chance += 0.22f;
        }
        if (fortified) {
            chance += 0.35f;
        }
        if (camping && fortified) {
            chance += 0.25f;
        }
        return this.f_19796_.m_188501_() < chance;
    }

    private boolean hasNearbyMagicalBarrier(ServerLevel level, BlockPos center) {
        for (BlockPos position : BlockPos.m_121940_((BlockPos)center.m_7918_(-8, -8, -8), (BlockPos)center.m_7918_(8, 8, 8))) {
            if (!level.m_8055_(position).m_60713_(Blocks.f_50375_) && !level.m_8055_(position).m_60713_((Block)GoetyMasteryOfMagicModBlocks.SOUL_BARRIER_BLOCK.get())) continue;
            return true;
        }
        return false;
    }

    private void synchronizeCastingFlags() {
        int flags = 0;
        for (ActiveCast cast : this.spellSlots) {
            if (cast == null) continue;
            flags |= cast.spell.castingFlag();
        }
        this.f_19804_.m_135381_(CASTING_FLAGS, (Object)flags);
    }

    public float getCastingMovementMultiplier() {
        return (Integer)this.f_19804_.m_135370_(CASTING_FLAGS) == 0 ? 1.0f : 0.1f;
    }

    public void setSwordBarragePattern(int swordBarragePattern) {
        this.swordBarragePattern = Mth.m_14045_((int)swordBarragePattern, (int)0, (int)2);
    }

    public int getSwordBarragePattern() {
        return this.swordBarragePattern;
    }

    public void setPhilosopherSlashVolleyCount(int count) {
        this.philosopherSlashVolleyCount = Mth.m_14045_((int)count, (int)2, (int)5);
    }

    public int getPhilosopherSlashVolleyCount() {
        return this.philosopherSlashVolleyCount;
    }

    public void beginPhilosopherSlashVolley(int count, int firstSlashTick) {
        this.setPhilosopherSlashVolleyCount(count);
        this.philosopherSlashesSpawned = 0;
        this.philosopherSlashNextTick = Math.max(0, firstSlashTick);
        this.previousPhilosopherSlashRoll = Float.NaN;
    }

    public float nextPhilosopherSlashRoll() {
        if (Float.isNaN(this.previousPhilosopherSlashRoll)) {
            this.previousPhilosopherSlashRoll = this.f_19796_.m_188501_() * 360.0f;
        } else {
            float separation = 30.0f + this.f_19796_.m_188501_() * 150.0f;
            this.previousPhilosopherSlashRoll = Mth.m_14177_((float)(this.previousPhilosopherSlashRoll + (this.f_19796_.m_188499_() ? separation : -separation)));
        }
        return this.previousPhilosopherSlashRoll;
    }

    public void triggerFastSlashAnimation() {
        this.f_19804_.m_135381_(SLASH_ANIMATION_TICKS, (Object)9);
    }

    public boolean isFastSlashAnimationActive() {
        return (Integer)this.f_19804_.m_135370_(SLASH_ANIMATION_TICKS) > 0;
    }

    public double getAuraRadius(float partialTick) {
        return 6.5 + 0.5 * Math.sin((double)((float)this.f_19797_ + partialTick) * 0.08);
    }

    public int getPhilosopherSlashesSpawned() {
        return this.philosopherSlashesSpawned;
    }

    public int getPhilosopherSlashNextTick() {
        return this.philosopherSlashNextTick;
    }

    public void advancePhilosopherSlashVolley(int currentTick, int minimumDelay, int maximumDelay, int endLag) {
        ++this.philosopherSlashesSpawned;
        if (this.philosopherSlashesSpawned >= this.philosopherSlashVolleyCount) {
            this.philosopherSlashNextTick = currentTick + Math.max(1, endLag);
            return;
        }
        int minimum = Math.max(1, minimumDelay);
        int maximum = Math.max(minimum, maximumDelay);
        this.philosopherSlashNextTick = currentTick + minimum + this.f_19796_.m_188503_(maximum - minimum + 1);
    }

    public void beginAlchemicalOrbVolley(int count, int firstTick) {
        this.alchemicalOrbVolleyCount = Mth.m_14045_((int)count, (int)2, (int)4);
        this.alchemicalOrbsSpawned = 0;
        this.alchemicalOrbNextTick = Math.max(0, firstTick);
    }

    public void advanceAlchemicalOrbVolley(int currentTick, int delay) {
        ++this.alchemicalOrbsSpawned;
        this.alchemicalOrbNextTick = currentTick + Math.max(1, delay);
    }

    public int getAlchemicalOrbVolleyCount() {
        return this.alchemicalOrbVolleyCount;
    }

    public int getAlchemicalOrbsSpawned() {
        return this.alchemicalOrbsSpawned;
    }

    public int getAlchemicalOrbNextTick() {
        return this.alchemicalOrbNextTick;
    }

    public void beginAlchemicalShockwave() {
        this.alchemicalShockwaveHits.clear();
        this.alchemicalShockwaveOrigin = Vec3.f_82478_;
        this.alchemicalShockwaveDirection = Vec3.f_82478_;
    }

    public void releaseAlchemicalShockwave(Vec3 origin, Vec3 direction) {
        this.alchemicalShockwaveOrigin = origin;
        this.alchemicalShockwaveDirection = direction.m_82541_();
    }

    public Vec3 getAlchemicalShockwaveOrigin() {
        return this.alchemicalShockwaveOrigin;
    }

    public Vec3 getAlchemicalShockwaveDirection() {
        return this.alchemicalShockwaveDirection;
    }

    public boolean markAlchemicalShockwaveHit(LivingEntity target) {
        return this.alchemicalShockwaveHits.add(target.m_20148_());
    }

    private void recordDamageHit() {
        long now = this.m_9236_().m_46467_();
        this.recentDamageTicks.addLast(now);
        this.trimRecentDamage(now);
    }

    private void trimRecentDamage(long now) {
        while (!this.recentDamageTicks.isEmpty() && now - this.recentDamageTicks.peekFirst() > 30L) {
            this.recentDamageTicks.removeFirst();
        }
    }

    public boolean hasRecentDamagePressure() {
        this.trimRecentDamage(this.m_9236_().m_46467_());
        return this.recentDamageTicks.size() >= 3;
    }

    public void clearRecentDamagePressure() {
        this.recentDamageTicks.clear();
    }

    public boolean hasPendingBarrierDamageReaction() {
        return this.pendingBarrierDamageReactionTicks > 0;
    }

    public void clearPendingBarrierDamageReaction() {
        this.pendingBarrierDamageReactionTicks = 0;
        this.pendingBarrierThreatPosition = null;
    }

    private void scheduleBarrierReactionAfterDamage(@Nullable Entity direct, @Nullable Entity causing) {
        Entity attacker;
        this.pendingBarrierDamageReactionTicks = 60;
        this.pendingBarrierThreatPosition = null;
        Entity entity = attacker = causing != null && causing != this ? causing : direct;
        if (attacker != null && attacker != this) {
            this.pendingBarrierThreatPosition = attacker.m_20191_().m_82399_();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean m_6469_(DamageSource source, float amount) {
        LivingEntity smitingEntity;
        int smite;
        LivingEntity attacker;
        boolean damaged;
        Level level;
        Projectile projectile;
        Entity mechanismActor;
        Entity direct = source.m_7640_();
        Entity causing = source.m_7639_();
        Object object = causing instanceof FakePlayer ? causing : (mechanismActor = direct instanceof Projectile && (projectile = (Projectile)direct).m_19749_() instanceof FakePlayer ? projectile.m_19749_() : null);
        if (mechanismActor != null && (level = this.m_9236_()) instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            BlockPos center = mechanismActor.m_20183_();
            double nearest = Double.MAX_VALUE;
            for (BlockPos candidate : BlockPos.m_121940_((BlockPos)center.m_7918_(-4, -4, -4), (BlockPos)center.m_7918_(4, 4, 4))) {
                double distance;
                if (serverLevel.m_7702_(candidate) == null || !((distance = candidate.m_123331_((Vec3i)center)) < nearest)) continue;
                nearest = distance;
                this.philosopherBoltTarget = candidate.m_7949_();
                this.nextLazethystScanTick = 0;
            }
        }
        if (causing == this || direct instanceof GoldenSwordProjectileEntity) {
            return false;
        }
        if (direct instanceof Projectile && !MidasSpellThreatRegistry.isMagicalProjectile((Entity)(projectile = (Projectile)direct))) {
            Entity nearest = projectile.m_19749_();
            if (nearest instanceof LivingEntity) {
                LivingEntity owner = (LivingEntity)nearest;
                this.m_6710_(owner);
            }
            projectile.m_146870_();
            return false;
        }
        long now = this.m_9236_().m_46467_();
        float adaptiveDefense = this.getAdaptiveDefense(source, now);
        float mitigatedAmount = Math.min(25.0f, Math.max(0.0f, amount) * (1.0f - adaptiveDefense) * 0.875f);
        float healthBefore = this.m_21223_();
        this.applyingNormalDamage = true;
        try {
            damaged = super.m_6469_(source, mitigatedAmount);
        }
        finally {
            this.applyingNormalDamage = false;
        }
        if (!damaged) {
            return false;
        }
        float actualDamage = Math.max(0.0f, healthBefore - this.m_21223_());
        this.addAdaptiveDefense(source, actualDamage, now);
        if (this.canTriggerBarrierReaction(source, direct, causing)) {
            this.recordDamageHit();
            this.scheduleBarrierReactionAfterDamage(direct, causing);
        }
        if (causing instanceof LivingEntity && (attacker = (LivingEntity)causing) != this) {
            this.m_6710_(attacker);
        }
        if (direct instanceof LivingEntity && (smite = EnchantmentHelper.m_44843_((Enchantment)Enchantments.f_44978_, (ItemStack)(smitingEntity = (LivingEntity)direct).m_21205_())) > 0) {
            this.antiRegenerationTicks = Mth.m_14045_((int)smite, (int)1, (int)5) * 20;
        }
        if (causing instanceof LivingEntity) {
            attacker = (LivingEntity)causing;
            if (PhilosopherKingMidasEntity.isMeleeDamage(source)) {
                GoldificationManager.goldifyEntityForMidas((Entity)attacker, 200L, (Entity)this);
                if (GoldificationManager.isEntityGoldified((Entity)attacker)) {
                    GoldificationManager.shatterEntity((Entity)attacker, (Entity)this);
                }
            }
        }
        return true;
    }

    private boolean canTriggerBarrierReaction(DamageSource source, @Nullable Entity direct, @Nullable Entity causing) {
        if (source.m_269533_(DamageTypeTags.f_268745_) || source.m_269533_(DamageTypeTags.f_276146_)) {
            return false;
        }
        return direct != null || causing != null;
    }

    private float getAdaptiveDefense(DamageSource source, long now) {
        String damageType = PhilosopherKingMidasEntity.damageTypeKey(source);
        ArrayDeque<AdaptiveDefenseStack> stacks = this.adaptiveDefenses.get(damageType);
        if (stacks == null) {
            return 0.0f;
        }
        while (!stacks.isEmpty() && stacks.peekFirst().expiresAt <= now) {
            stacks.removeFirst();
        }
        if (stacks.isEmpty()) {
            this.adaptiveDefenses.remove(damageType);
            return 0.0f;
        }
        float total = 0.0f;
        for (AdaptiveDefenseStack stack : stacks) {
            total += stack.reduction;
        }
        return Math.min(0.95f, total);
    }

    private void addAdaptiveDefense(DamageSource source, float actualDamage, long now) {
        if (actualDamage <= 0.0f) {
            return;
        }
        String damageType = PhilosopherKingMidasEntity.damageTypeKey(source);
        float current = this.getAdaptiveDefense(source, now);
        float additional = Math.min(actualDamage / 100.0f, 0.95f - current);
        if (additional <= 0.0f) {
            return;
        }
        this.adaptiveDefenses.computeIfAbsent(damageType, ignored -> new ArrayDeque()).addLast(new AdaptiveDefenseStack(additional, now + 1200L));
    }

    private static String damageTypeKey(DamageSource source) {
        return source.m_269150_().m_203543_().map(key -> key.m_135782_().toString()).orElse(source.m_19385_());
    }

    private static boolean isMeleeDamage(DamageSource source) {
        return source.m_7640_() == source.m_7639_() && source.m_7639_() instanceof LivingEntity && !source.m_269533_(DamageTypeTags.f_268524_) && !source.m_269533_(DamageTypeTags.f_268415_) && (source.m_276093_(DamageTypes.f_268464_) || source.m_276093_(DamageTypes.f_268566_) || source.m_276093_(DamageTypes.f_268511_));
    }

    public void m_5634_(float amount) {
        if (this.antiRegenerationTicks <= 0) {
            super.m_5634_(amount);
        }
    }

    public MobType m_6336_() {
        return MobType.f_21641_;
    }

    public boolean m_6673_(DamageSource source) {
        return source.m_276093_(DamageTypes.f_268612_) || source.m_276093_(DamageTypes.f_268722_) || super.m_6673_(source);
    }

    public void m_6710_(@Nullable LivingEntity target) {
        Player player;
        if (target instanceof Player && ((player = (Player)target).m_7500_() || player.m_5833_())) {
            target = null;
        }
        LivingEntity previousTarget = this.m_5448_();
        super.m_6710_(target);
        if (!this.m_9236_().f_46443_ && target != null && target != previousTarget && this.roarCooldown <= 0) {
            this.m_5496_((SoundEvent)GoetyMasteryOfMagicModSounds.MIDAS_ROAR.get(), 2.25f, 0.92f + this.f_19796_.m_188501_() * 0.1f);
            this.roarCooldown = 200;
        }
    }

    protected SoundEvent m_7515_() {
        return (SoundEvent)GoetyMasteryOfMagicModSounds.MIDAS_GRUNT.get();
    }

    protected SoundEvent m_7975_(DamageSource source) {
        return (SoundEvent)GoetyMasteryOfMagicModSounds.MIDAS_DAMAGE.get();
    }

    protected SoundEvent m_5592_() {
        return (SoundEvent)GoetyMasteryOfMagicModSounds.MIDAS_ROAR.get();
    }

    public int m_8100_() {
        return 160;
    }

    protected float m_6121_() {
        return 1.5f;
    }

    public boolean m_142535_(float distance, float multiplier, DamageSource source) {
        return false;
    }

    public AABB m_6921_() {
        return super.m_6921_().m_82400_(7.0);
    }

    public boolean m_6785_(double distanceToClosestPlayer) {
        return false;
    }

    public void m_6457_(ServerPlayer player) {
        super.m_6457_(player);
        this.bossEvent.m_6543_(player);
    }

    public void m_6452_(ServerPlayer player) {
        super.m_6452_(player);
        this.bossEvent.m_6539_(player);
    }

    public void m_6593_(Component name) {
        super.m_6593_(name);
        this.bossEvent.m_6456_(this.m_5446_());
    }

    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        tag.m_128405_("MidasAntiRegeneration", this.antiRegenerationTicks);
        ListTag adaptiveDefenseList = new ListTag();
        long now = this.m_9236_().m_46467_();
        for (Map.Entry<String, ArrayDeque<AdaptiveDefenseStack>> entry : this.adaptiveDefenses.entrySet()) {
            for (AdaptiveDefenseStack stack : entry.getValue()) {
                if (stack.expiresAt <= now || stack.reduction <= 0.0f) continue;
                CompoundTag stackTag = new CompoundTag();
                stackTag.m_128359_("DamageType", entry.getKey());
                stackTag.m_128350_("Reduction", stack.reduction);
                stackTag.m_128356_("ExpiresAt", stack.expiresAt);
                adaptiveDefenseList.add((Object)stackTag);
            }
        }
        tag.m_128365_("MidasAdaptiveDefenses", (Tag)adaptiveDefenseList);
        this.bossEvent.m_6456_(this.m_5446_());
    }

    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        if (this.m_21051_(Attributes.f_22284_) != null) {
            this.m_21051_(Attributes.f_22284_).m_22100_(5.0);
        }
        this.antiRegenerationTicks = tag.m_128451_("MidasAntiRegeneration");
        this.adaptiveDefenses.clear();
        long now = this.m_9236_().m_46467_();
        ListTag adaptiveDefenseList = tag.m_128437_("MidasAdaptiveDefenses", 10);
        for (int index = 0; index < adaptiveDefenseList.size(); ++index) {
            CompoundTag stackTag = adaptiveDefenseList.m_128728_(index);
            String damageType = stackTag.m_128461_("DamageType");
            float reduction = stackTag.m_128457_("Reduction");
            long expiresAt = stackTag.m_128454_("ExpiresAt");
            if (damageType.isBlank() || !(reduction > 0.0f) || expiresAt <= now) continue;
            this.adaptiveDefenses.computeIfAbsent(damageType, ignored -> new ArrayDeque()).addLast(new AdaptiveDefenseStack(Math.min(reduction, 0.95f), expiresAt));
        }
        this.bossEvent.m_6456_(this.m_5446_());
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.m_21552_().m_22268_(Attributes.f_22276_, 1000.0).m_22268_(Attributes.f_22284_, 5.0).m_22268_(Attributes.f_22278_, 0.25).m_22268_(Attributes.f_22281_, 30.0).m_22268_(Attributes.f_22277_, 128.0).m_22268_(Attributes.f_22279_, 0.85).m_22268_(Attributes.f_22280_, 0.85);
    }

    private static final class ActiveCast {
        private final MidasBossSpell spell;
        @Nullable
        private final UUID targetUuid;
        private int castTick;
        private int windupTicks = 30;
        private boolean started;

        private ActiveCast(MidasBossSpell spell, @Nullable LivingEntity target) {
            this.spell = spell;
            this.targetUuid = target == null ? null : target.m_20148_();
        }

        @Nullable
        private LivingEntity resolveTarget(ServerLevel level) {
            LivingEntity living;
            if (this.targetUuid == null) {
                return null;
            }
            Entity entity = level.m_8791_(this.targetUuid);
            return entity instanceof LivingEntity ? (living = (LivingEntity)entity) : null;
        }
    }

    private static final class MaintainCombatAltitudeGoal
    extends Goal {
        private static final double HEIGHT_ABOVE_TARGET = 5.0;
        private static final double APPROACH_DISTANCE = 2.0;
        private static final double FREE_FLIGHT_APPROACH_SPEED = 0.12;
        private static final double CASTING_APPROACH_SPEED = 1.15;
        private final PhilosopherKingMidasEntity midas;

        private MaintainCombatAltitudeGoal(PhilosopherKingMidasEntity midas) {
            this.midas = midas;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean m_8036_() {
            Player player;
            LivingEntity target = this.midas.m_5448_();
            return target != null && target.m_6084_() && (!(target instanceof Player) || !(player = (Player)target).m_7500_() && !player.m_5833_());
        }

        public boolean m_8045_() {
            return this.m_8036_();
        }

        public void m_8037_() {
            LivingEntity target = this.midas.m_5448_();
            if (target == null) {
                return;
            }
            if (!this.midas.isCastingAlchemicalShockwave()) {
                Vec3 lookDirection = target.m_20191_().m_82399_().m_82546_(this.midas.m_146892_());
                double horizontalLookDistance = Math.sqrt(lookDirection.f_82479_ * lookDirection.f_82479_ + lookDirection.f_82481_ * lookDirection.f_82481_);
                float targetYaw = (float)(Mth.m_14136_((double)lookDirection.f_82481_, (double)lookDirection.f_82479_) * 57.2957763671875) - 90.0f;
                float targetPitch = (float)(-(Mth.m_14136_((double)lookDirection.f_82480_, (double)horizontalLookDistance) * 57.2957763671875));
                this.midas.m_146922_(targetYaw);
                this.midas.f_20885_ = targetYaw;
                this.midas.f_20883_ = targetYaw;
                this.midas.m_146926_(Mth.m_14036_((float)targetPitch, (float)-90.0f, (float)90.0f));
            }
            Vec3 fromTarget = this.midas.m_20182_().m_82546_(target.m_20182_());
            Vec3 horizontal = new Vec3(fromTarget.f_82479_, 0.0, fromTarget.f_82481_);
            double distance = horizontal.m_82553_();
            Vec3 direction = distance < 1.0E-4 ? new Vec3(1.0, 0.0, 0.0) : horizontal.m_82490_(1.0 / distance);
            double desiredDistance = 2.0;
            double desiredX = target.m_20185_() + direction.f_82479_ * desiredDistance;
            double desiredZ = target.m_20189_() + direction.f_82481_ * desiredDistance;
            double desiredY = target.m_20186_() + 5.0;
            double speedModifier = this.midas.getCastingMovementMultiplier() < 1.0f ? 1.15 : 0.12;
            this.midas.m_21566_().m_6849_(desiredX, desiredY, desiredZ, speedModifier);
        }
    }

    private static final class AscendToIdleAltitudeGoal
    extends Goal {
        private static final double HEIGHT_ABOVE_GROUND = 12.0;
        private final PhilosopherKingMidasEntity midas;

        private AscendToIdleAltitudeGoal(PhilosopherKingMidasEntity midas) {
            this.midas = midas;
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean m_8036_() {
            return this.midas.m_5448_() == null && Math.abs(this.midas.m_20186_() - this.getIdleAltitude()) > 0.2;
        }

        public boolean m_8045_() {
            return this.m_8036_();
        }

        public void m_8037_() {
            this.midas.m_21566_().m_6849_(this.midas.m_20185_(), this.getIdleAltitude(), this.midas.m_20189_(), 1.0);
        }

        public void m_8041_() {
            if (this.midas.m_5448_() == null && Math.abs(this.midas.m_20186_() - this.getIdleAltitude()) <= 0.2) {
                this.midas.m_20256_(Vec3.f_82478_);
            }
        }

        private double getIdleAltitude() {
            int groundY = this.midas.m_9236_().m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.m_14107_((double)this.midas.m_20185_()), Mth.m_14107_((double)this.midas.m_20189_()));
            return (double)groundY + 12.0;
        }
    }

    private static final class OptionalModCompat {
        private static final Class<?> IRONS_MAGIC_PROJECTILE = OptionalModCompat.findClass("io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile");
        private static final Class<?> IRONS_MAGIC_ENTITY = OptionalModCompat.findClass("io.redspace.ironsspellbooks.api.entity.IMagicEntity");
        private static final boolean WAYSTONES_PRESENT = OptionalModCompat.findClass("net.blay09.mods.waystones.Waystones") != null;

        private OptionalModCompat() {
        }

        private static boolean isIronsSpellbooksMagicProjectile(Entity entity) {
            if (IRONS_MAGIC_PROJECTILE == null && IRONS_MAGIC_ENTITY == null) {
                return false;
            }
            boolean reflectedMagicType = IRONS_MAGIC_PROJECTILE != null && IRONS_MAGIC_PROJECTILE.isInstance(entity) || IRONS_MAGIC_ENTITY != null && IRONS_MAGIC_ENTITY.isInstance(entity);
            ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey((Object)entity.m_6095_());
            boolean ironsSpellEntity = key != null && "irons_spellbooks".equals(key.m_135827_()) && entity.getClass().getPackageName().contains(".entity.spells");
            return reflectedMagicType || ironsSpellEntity;
        }

        private static boolean isWaystonesBlock(BlockState state) {
            if (!WAYSTONES_PRESENT) {
                return false;
            }
            ResourceLocation key = ForgeRegistries.BLOCKS.getKey((Object)state.m_60734_());
            return key != null && "waystones".equals(key.m_135827_());
        }

        @Nullable
        private static Class<?> findClass(String className) {
            try {
                return Class.forName(className, false, PhilosopherKingMidasEntity.class.getClassLoader());
            }
            catch (ClassNotFoundException | LinkageError ignored) {
                return null;
            }
        }
    }

    private record AdaptiveDefenseStack(float reduction, long expiresAt) {
    }
}

