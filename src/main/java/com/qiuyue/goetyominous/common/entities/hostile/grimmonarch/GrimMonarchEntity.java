package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

import com.Polarice3.Goety.client.particles.SoulSummonVFX;
import com.Polarice3.Goety.client.particles.TeleportInShockwaveParticleOption;
import com.Polarice3.Goety.client.particles.TeleportShockwaveParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.projectiles.AbstractBeam;
import com.Polarice3.Goety.common.entities.projectiles.AbstractSpellCloud;
import com.Polarice3.Goety.common.entities.projectiles.AcidPool;
import com.Polarice3.Goety.common.entities.util.BrewEffectCloud;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.SEHelper;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

public class GrimMonarchEntity extends Monster {
    private static final EntityDataAccessor<Integer> ACTION = SynchedEntityData.defineId(
            GrimMonarchEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Long> START = SynchedEntityData.defineId(
            GrimMonarchEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Boolean> READY = SynchedEntityData.defineId(
            GrimMonarchEntity.class, EntityDataSerializers.BOOLEAN);

    private final ModServerBossInfo bossInfo;
    final Map<String, Integer> cooldowns = new HashMap<>();
    final Map<String, Long> lastAttackUse = new HashMap<>();
    final Set<UUID> hitThisWindow = new HashSet<>();
    final Map<String, Set<UUID>> clawVictims = new HashMap<>();
    private long attackSequence;
    private Vec3 heading = new Vec3(0.0, 0.0, 1.0);
    private Vec3 moveOrigin = Vec3.ZERO;
    private Vec3 lastShard = Vec3.ZERO;
    private Vec3 circleOrigin = Vec3.ZERO;
    private Vec3 jumpOrigin = Vec3.ZERO;
    private Vec3 landingPosition = Vec3.ZERO;
    private Vec3 flightTop;
    private Vec3 calamityAim;
    private boolean ascending;
    private boolean landingActive;
    private double moveDistance;
    private float dashFinishStart = 1.685F;
    private boolean focus = true;
    private boolean echoPending;
    private boolean echoSpawnedThisCast;
    private UUID ownerUUID;
    private UUID echoUUID;
    private int echoFarTicks;
    private boolean echoFollowing;
    private int echoRepathTicks;
    private float pendingSmitingBonus;
    private int pendingSmiteReserveDeduction;
    private int teleportCooldown;
    private int rapidTeleportCooldown;
    private int teleportAttemptTimer;
    private int rapidHits;
    private int rapidHitTicks;
    private boolean selectingAttackTarget;
    private GrimMonarchMotion activeRootMotion;
    private int circleRemaining;
    private int circleIndex;
    private int decisionDelay;
    private int targetHeldUntil;
    private final Map<UUID, SoulDebt> soulDebts = new HashMap<>();
    private int regenHeal;
    private int regenPayoutTicks;
    private boolean bypassHealthCap;
    private int moddedInvul;
    private int independentInvul;
    private int dynamicReductionTicks;
    private int summonStarsRemaining;
    private float summonStarTimer;
    private float summonStarSpacing;
    private boolean nightRegistered;
    private final GrimMonarchMoveRunner moveRunner = new GrimMonarchMoveRunner(this);

    public GrimMonarchEntity(EntityType<? extends GrimMonarchEntity> type, Level level) {
        super(type, level);
        this.xpReward = this.isEcho() ? 0 : AttributesConfig.GrimMonarchExperience.get();
        this.setMaxUpStep((float) (double) GrimMonarchConstants.MAX_UP_STEP);
        this.setPersistenceRequired();
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.bossInfo = new ModServerBossInfo(this,
                this.isEcho() ? BossEvent.BossBarColor.BLUE : BossEvent.BossBarColor.GREEN, false, false);
        this.moveControl = new MoveControl(this) {
            @Override
            protected float rotlerp(float current, float wanted, float limit) {
                return super.rotlerp(current, wanted,
                        (float) (double) GrimMonarchConstants.TURN_DEGREES_PER_TICK);
            }
        };
        this.lookControl = new LookControl(this) {
            @Override
            public void tick() {
                if (GrimMonarchEntity.this.action() == GrimMonarchAction.IDLE) {
                    super.tick();
                }
            }
        };
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.GrimMonarchHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.GrimMonarchArmor.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.GrimMonarchSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.GrimMonarchFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GrimMonarchAttackDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.GrimMonarchKnockbackResistance.get());
    }

    public static AttributeSupplier.Builder createEchoAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.GrimMonarchEchoHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.GrimMonarchArmor.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.GrimMonarchSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.GrimMonarchFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GrimMonarchAttackDamage.get()
                        * AttributesConfig.GrimMonarchEchoDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.GrimMonarchKnockbackResistance.get());
    }

    public boolean isEcho() {
        return this.getType() == ModEntityTypes.GRIM_MONARCH_ECHO.get();
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public float pendingSmitingBonus() {
        return this.pendingSmitingBonus;
    }

    @Nullable
    private GrimMonarchEntity echoOwnerEntity() {
        if (!this.isEcho() || this.ownerUUID == null || !(this.level() instanceof ServerLevel server)) {
            return null;
        }
        Entity entity = server.getEntity(this.ownerUUID);
        return entity instanceof GrimMonarchEntity owner ? owner : null;
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (entity instanceof GrimMonarchEntity other && other != this) {
            GrimMonarchEntity thisLeader = this.isEcho() ? this.echoOwnerEntity() : this;
            GrimMonarchEntity otherLeader = other.isEcho() ? other.echoOwnerEntity() : other;
            if (thisLeader != null && thisLeader == otherLeader) {
                return true;
            }
        }
        return super.isAlliedTo(entity);
    }

    void bindEchoOwner(GrimMonarchEntity owner) {
        this.ownerUUID = owner.getUUID();
    }

    void linkEcho(GrimMonarchEntity echo) {
        this.echoUUID = echo.getUUID();
    }

    void onEchoDied(GrimMonarchEntity echo) {
        if (this.echoUUID != null && this.echoUUID.equals(echo.getUUID())) {
            this.echoUUID = null;
        }
        this.cooldowns.put("echo", ticks(GrimMonarchConstants.ECHO_DEATH_COOLDOWN_SECONDS));
    }

    @Nullable
    UUID echoUUID() {
        return this.echoUUID;
    }

    public double echoDamageScale() {
        return this.isEcho() ? AttributesConfig.GrimMonarchEchoDamage.get() : 1.0D;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ACTION, GrimMonarchAction.SPAWN.ordinal());
        this.entityData.define(START, 0L);
        this.entityData.define(READY, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new CombatGoal());
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return GrimMonarchEntity.this.action() == GrimMonarchAction.IDLE
                        && !GrimMonarchEntity.this.isDeadOrDying()
                        && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return GrimMonarchEntity.this.action() == GrimMonarchAction.IDLE
                        && !GrimMonarchEntity.this.isDeadOrDying()
                        && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 24.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GrimMonarchNavigation(this, level);
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new BodyRotationControl(this) {
            @Override
            public void clientTick() {
                GrimMonarchEntity.this.setYBodyRot(GrimMonarchEntity.this.getYRot());
                GrimMonarchEntity.this.yBodyRot = GrimMonarchEntity.this.getYRot();
            }
        };
    }

    public GrimMonarchAction action() {
        int id = this.entityData.get(ACTION);
        return GrimMonarchAction.values()[Mth.clamp(id, 0, GrimMonarchAction.values().length - 1)];
    }

    public long actionStarted() {
        return this.entityData.get(START);
    }

    public boolean ready() {
        return this.entityData.get(READY);
    }

    public float actionSeconds(float partial) {
        return Math.max(0.0F, ((float) (this.level().getGameTime() - this.actionStarted()) + partial) * 0.05F);
    }

    private String group(GrimMonarchAction action) {
        if (action == GrimMonarchAction.DASH_WINDUP || action == GrimMonarchAction.DASH_RUN
                || action == GrimMonarchAction.DASH_FINISH) {
            return "dash";
        }
        if (action.calamity()) {
            return "calamity";
        }
        return switch (action) {
            case SWEEP -> "sweep";
            case SUMMON -> "summon";
            case CLONE -> "echo";
            default -> null;
        };
    }

    private void cool(String group) {
        this.cooldowns.put(group, switch (group) {
            case "dash" -> ticks(GrimMonarchConstants.DASH_COOLDOWN_SECONDS);
            case "sweep" -> ticks(GrimMonarchConstants.SWEEP_COOLDOWN_SECONDS);
            case "summon" -> ticks(GrimMonarchConstants.SUMMON_COOLDOWN_SECONDS);
            case "echo" -> 0;
            case "calamity" -> ticks(GrimMonarchConstants.CALAMITY_COOLDOWN_SECONDS);
            default -> 0;
        });
    }

    private static int ticks(double seconds) {
        return Math.max(0, (int) Math.ceil(seconds * 20.0D - 1.0E-6D));
    }

    private void setVisualAction(GrimMonarchAction action) {
        this.entityData.set(ACTION, action.ordinal());
        this.entityData.set(START, this.level().getGameTime());
    }

    private void startMove(String moveId, GrimMonarchAction visual) {
        GrimMonarchMove move = GrimMonarchMoveTable.get(moveId);
        String nextGroup = this.group(visual);
        if (nextGroup != null && !nextGroup.equals(this.group(this.action()))) {
            this.lastAttackUse.put(nextGroup, ++this.attackSequence);
        }
        this.getNavigation().stop();
        Vec3 movement = this.getDeltaMovement();
        this.setDeltaMovement(0.0, movement.y, 0.0);
        this.setVisualAction(visual);
        this.hitThisWindow.clear();
        this.clawVictims.clear();
        this.activeRootMotion = null;
        this.focus = true;
        this.moveOrigin = this.position();
        this.lastShard = this.position();
        this.jumpOrigin = this.position();
        this.landingPosition = this.position();
        this.flightTop = null;
        this.ascending = false;
        this.landingActive = false;
        if (!visual.calamity()) {
            this.calamityAim = null;
        }
        this.setNoGravity(false);
        this.moveDistance = 0.0D;
        this.dashFinishStart = 1.685F;
        if (visual == GrimMonarchAction.CLONE) {
            this.echoSpawnedThisCast = false;
        }
        if (this.attackMovement()) {
            this.commitAttackAim();
        }
        if (GrimMonarchMoveTable.DASH.equals(moveId)) {
            this.moveDistance = GrimMonarchConstants.DASH_MAX_BLOCKS;
        }
        if (GrimMonarchMoveTable.DOOM_DIVE_RUSH.equals(moveId)) {
            this.moveDistance = GrimMonarchConstants.COMBO_DASH_BLOCKS;
        }
        this.moveRunner.start(move);
    }

    private void finishMove() {
        GrimMonarchAction visual = this.action();
        String group = this.group(visual);
        if (visual != GrimMonarchAction.DASH_FINISH) {
            if (group != null) {
                this.cool(group);
            }
            if (visual == GrimMonarchAction.CLONE) {
                this.cooldowns.put("echo", this.echoSpawnedThisCast
                        ? 0 : ticks(GrimMonarchConstants.ECHO_RETRY_SECONDS));
                this.echoPending = !this.echoSpawnedThisCast;
            }
        }
        this.moveRunner.stop();
        this.setNoGravity(false);
        this.ascending = false;
        this.landingActive = false;
        this.setVisualAction(GrimMonarchAction.IDLE);
        this.decisionDelay = 6;
    }

    private void advanceMove() {
        GrimMonarchMove move = this.moveRunner.move();
        if (move == null) {
            return;
        }
        switch (move.id()) {
            case GrimMonarchMoveTable.DOOM_DIVE_START ->
                    this.startMove(GrimMonarchMoveTable.DOOM_DIVE_WINDUP, GrimMonarchAction.CALAMITY_WINDUP);
            case GrimMonarchMoveTable.DOOM_DIVE_WINDUP -> this.finishMove();
            case GrimMonarchMoveTable.DOOM_DIVE_RUSH -> {
                if (this.available("calamityFollowup")) {
                    this.cooldowns.put("calamityDash",
                            ticks(GrimMonarchConstants.CALAMITY_DASH_COOLDOWN_SECONDS));
                    this.startMove(GrimMonarchMoveTable.DOOM_DIVE_FINISH, GrimMonarchAction.CALAMITY_FINISH);
                }
            }
            case GrimMonarchMoveTable.DOOM_DIVE_FINISH -> {
                this.cooldowns.put("calamityFollowup",
                        ticks(GrimMonarchConstants.CALAMITY_FOLLOWUP_COOLDOWN_SECONDS));
                this.startMove(GrimMonarchMoveTable.DOOM_DIVE_LAND, GrimMonarchAction.CALAMITY_THREE);
            }
            case GrimMonarchMoveTable.DEATH -> {
            }
            default -> this.finishMove();
        }
    }

    private void tickMove(float old, float now) {
        GrimMonarchMove move = this.moveRunner.move();
        if (move == null || GrimMonarchMoveTable.DEATH.equals(move.id())) {
            return;
        }
        if (GrimMonarchMoveTable.SWEEP.equals(move.id())) {
            Vec3 oldPosition = this.position();
            this.faceTarget();
            this.applyMoveEvents(move, old, now);
            this.applyBackstep(move, old, now);
            if (old < 1.6F && now > 1.0F) {
                GrimMonarchCombat.circularSweep(this, old, now, oldPosition);
            }
            return;
        }
        if (GrimMonarchMoveTable.DASH.equals(move.id())) {
            this.tickDashMove(old, now);
            return;
        }
        if (GrimMonarchMoveTable.DOOM_DIVE_WINDUP.equals(move.id())) {
            this.tickDoomWindup(move, old, now);
            return;
        }
        if (GrimMonarchMoveTable.DOOM_DIVE_RUSH.equals(move.id())) {
            this.tickDoomRush(move, old, now);
            return;
        }
        if (GrimMonarchMoveTable.DOOM_DIVE_START.equals(move.id())
                || GrimMonarchMoveTable.DOOM_DIVE_FINISH.equals(move.id())
                || GrimMonarchMoveTable.DOOM_DIVE_LAND.equals(move.id())) {
            this.tickDoomMove(move, old, now);
            return;
        }
        this.applyMoveEvents(move, old, now);
    }

    private void tickDashMove(float old, float now) {
        GrimMonarchAction visual = this.action();
        if (visual == GrimMonarchAction.DASH_WINDUP) {
            if (now >= 1.049F) {
                this.setVisualAction(GrimMonarchAction.DASH_RUN);
                this.commitAttackAim();
            }
            return;
        }
        if (visual == GrimMonarchAction.DASH_RUN) {
            if (now >= 2.0F || this.moveDistance <= 0.05D) {
                this.beginDashFinish(now);
                return;
            }
            Vec3 before = this.position();
            Vec3 step = this.heading.scale(Math.min(GrimMonarchConstants.DASH_BLOCKS_PER_TICK,
                    Math.max(0.0D, this.moveDistance)));
            boolean moved = this.safeMove(step, false);
            double travelled = this.position().distanceTo(before);
            this.moveDistance = Math.max(0.0D, this.moveDistance - travelled);
            GrimMonarchCombat.sweptDashHit(this, before, this.position(),
                    GrimMonarchConstants.DASH_DAMAGE_MULTIPLIER);
            if (this.position().distanceTo(this.lastShard) >= GrimMonarchConstants.SOUL_STAR_SPACING) {
                GrimMonarchCombat.spawnQuadStar(this, before.add(0.0D, GrimMonarchConstants.QUAD_STAR_SPAWN_HEIGHT, 0.0D));
                this.lastShard = this.position();
            }
            if (!moved || this.moveDistance <= 0.05D) {
                this.beginDashFinish(now);
            }
            return;
        }
        if (visual == GrimMonarchAction.DASH_FINISH) {
            GrimMonarchClawSweep.hitWindow(this, "dash_finish", old, now,
                    1.5D, this.dashFinishStart + 0.3018F, this.dashFinishStart + 0.4718F, false);
        }
    }

    private void beginDashFinish(float now) {
        this.dashFinishStart = now;
        this.moveDistance = 0.0D;
        this.cooldowns.put("dash", ticks(GrimMonarchConstants.DASH_COOLDOWN_SECONDS));
        this.setVisualAction(GrimMonarchAction.DASH_FINISH);
        this.commitAttackAim();
    }

    private void tickDoomWindup(GrimMonarchMove move, float old, float now) {
        this.applyFocusEvents(move, old, now);
        if (this.focus && !this.ascending) {
            this.faceTarget();
        }
        if (now >= 0.700531F) {
            if (this.available("calamityDash")) {
                this.startMove(GrimMonarchMoveTable.DOOM_DIVE_RUSH, GrimMonarchAction.CALAMITY_RUN);
            }
            return;
        }
        this.applyMoveEvents(move, old, now);
    }

    private void tickDoomRush(GrimMonarchMove move, float old, float now) {
        GrimMonarchMotion motion = this.dashMotion(move);
        if (motion == null) {
            return;
        }
        boolean within = now > motion.start() && old < motion.end();
        boolean moved = false;
        Vec3 before = this.position();
        if (within) {
            if (this.moveDistance > 0.05D) {
                Vec3 step = this.heading.scale(Math.min(motion.speed(), this.moveDistance));
                moved = this.safeMove(step, false);
                double travelled = this.position().distanceTo(before);
                this.moveDistance = Math.max(0.0D, this.moveDistance - travelled);
            }
            if (this.pathHit(move, old, now)) {
                GrimMonarchCombat.sweptDashHit(this, before, this.position(),
                        GrimMonarchConstants.COMBO_DAMAGE_MULTIPLIER);
            }
            if (this.position().distanceToSqr(this.lastShard) >= GrimMonarchConstants.SOUL_STAR_SPACING) {
                GrimMonarchCombat.spawnQuadStar(this, before.add(0.0D, GrimMonarchConstants.QUAD_STAR_SPAWN_HEIGHT, 0.0D));
                this.lastShard = this.position();
            }
        }
        boolean done = !within || !moved || this.moveDistance <= 0.05D || now >= motion.end();
        if (done && this.available("calamityFollowup")) {
            this.cooldowns.put("calamityDash", ticks(GrimMonarchConstants.CALAMITY_DASH_COOLDOWN_SECONDS));
            this.startMove(GrimMonarchMoveTable.DOOM_DIVE_FINISH, GrimMonarchAction.CALAMITY_FINISH);
        }
    }

    private void tickDoomMove(GrimMonarchMove move, float old, float now) {
        this.applyFocusEvents(move, old, now);
        boolean lunging = this.isLunging(move, old, now);
        if (this.focus && !this.ascending && !lunging) {
            this.faceTarget();
        }
        for (GrimMonarchPhase phase : move.phases()) {
            for (GrimMonarchMotion motion : phase.motions()) {
                if (motion.kind() == GrimMonarchMotion.Kind.LUNGE
                        || motion.kind() == GrimMonarchMotion.Kind.SIDESTEP) {
                    this.applyGroundMotion(motion, old, now);
                }
            }
        }
        for (GrimMonarchPhase phase : move.phases()) {
            for (GrimMonarchMotion motion : phase.motions()) {
                if (motion.kind() == GrimMonarchMotion.Kind.FLIGHT && !this.tickFlight(motion, now)) {
                    return;
                }
            }
        }
        this.applyMoveEvents(move, old, now);
        this.applyHitWindows(move, old, now);
    }

    private void applyFocusEvents(GrimMonarchMove move, float old, float now) {
        for (GrimMonarchMoveEvent event : move.events()) {
            if (!crossed(old, now, event.time())) {
                continue;
            }
            if ("focus_on".equals(event.key())) {
                this.focus = true;
            } else if ("focus_off".equals(event.key())) {
                this.focus = false;
            }
        }
    }

    private boolean isLunging(GrimMonarchMove move, float old, float now) {
        for (GrimMonarchPhase phase : move.phases()) {
            for (GrimMonarchMotion motion : phase.motions()) {
                if ((motion.kind() == GrimMonarchMotion.Kind.LUNGE
                        || motion.kind() == GrimMonarchMotion.Kind.SIDESTEP)
                        && now > motion.start() && old < motion.end()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void applyGroundMotion(GrimMonarchMotion motion, float old, float now) {
        double overlap = Math.max(0.0F, Math.min(now, motion.end()) - Math.max(old, motion.start()));
        if (overlap <= 0.0D) {
            return;
        }
        if (this.activeRootMotion != motion) {
            this.activeRootMotion = motion;
            if (motion.kind() == GrimMonarchMotion.Kind.LUNGE) {
                this.commitAttackAim();
            } else {
                this.heading = this.forward();
            }
        }
        Vec3 direction = motion.kind() == GrimMonarchMotion.Kind.SIDESTEP
                ? new Vec3(-this.heading.z, 0.0D, this.heading.x)
                : this.heading;
        double distance = motion.distance() * overlap / (double) motion.duration();
        this.safeMove(direction.scale(distance), false);
        if (motion.kind() == GrimMonarchMotion.Kind.SIDESTEP
                && this.position().distanceToSqr(this.lastShard) >= GrimMonarchConstants.SOUL_STAR_SPACING) {
            GrimMonarchCombat.spawnQuadStar(this, this.position().add(0.0D, GrimMonarchConstants.QUAD_STAR_SPAWN_HEIGHT, 0.0D));
            this.lastShard = this.position();
        }
    }

    private boolean tickFlight(GrimMonarchMotion motion, float now) {
        double begin = motion.start();
        double riseTime = motion.distance();
        double markAt = motion.speed();
        double diveAt = motion.mark();
        double landAt = motion.land();
        if ((double) now < begin || (double) now > landAt + 0.051D) {
            return true;
        }
        double height = motion.height();
        if (!this.ascending && (double) now < begin + 0.06D) {
            this.jumpOrigin = this.position();
            Vec3 away = this.towardTarget().scale(-1.0D);
            this.flightTop = null;
            double retreat = GrimMonarchConstants.CALAMITY_FLIGHT_RETREAT;
            while (retreat >= 0.0D) {
                Vec3 candidate = this.jumpOrigin.add(away.scale(retreat)).add(0.0D, height, 0.0D);
                if (this.clearTakeoff(this.jumpOrigin, candidate)) {
                    this.flightTop = candidate;
                    break;
                }
                retreat = Math.max(-1.0D, retreat - 2.0D);
            }
            if (this.flightTop == null) {
                this.finishMove();
                return false;
            }
            this.ascending = true;
            this.setNoGravity(true);
            this.setDeltaMovement(Vec3.ZERO);
        }
        if (!this.ascending) {
            return true;
        }
        if ((double) now >= markAt && !this.landingActive) {
            Vec3 ground = this.chooseLanding();
            if (ground == null) {
                this.finishMove();
                return false;
            }
            this.landingPosition = ground;
            this.landingActive = true;
        }
        Vec3 desired;
        if ((double) now < diveAt) {
            double u = Math.min(1.0D, ((double) now - begin) / riseTime);
            desired = this.jumpOrigin.add(
                    (this.flightTop.x - this.jumpOrigin.x) * GrimMonarchCombatMath.flightTravel(u),
                    height * GrimMonarchCombatMath.rise(u),
                    (this.flightTop.z - this.jumpOrigin.z) * GrimMonarchCombatMath.flightTravel(u));
            if ((double) now >= markAt) {
                desired = desired.lerp(new Vec3(this.landingPosition.x, desired.y, this.landingPosition.z),
                        GrimMonarchCombatMath.smooth(((double) now - markAt) / (diveAt - markAt)));
            }
        } else {
            desired = new Vec3(this.landingPosition.x, this.flightTop.y, this.landingPosition.z)
                    .lerp(this.landingPosition, GrimMonarchCombatMath.smooth(((double) now - diveAt) / (landAt - diveAt)));
        }
        this.safeMove(desired.subtract(this.position()), true);
        this.setDeltaMovement(Vec3.ZERO);
        this.fallDistance = 0.0F;
        if (this.position().distanceToSqr(desired) > 0.16D) {
            this.finishMove();
            return false;
        }
        if ((double) now >= landAt) {
            this.setNoGravity(false);
            this.ascending = false;
            this.landingActive = false;
        }
        return true;
    }

    private void applyHitWindows(GrimMonarchMove move, float old, float now) {
        int index = 0;
        for (GrimMonarchPhase phase : move.phases()) {
            for (GrimMonarchHitWindow window : phase.hits()) {
                String key = move.id() + ":" + index++;
                if (window.kind() == GrimMonarchHitWindow.Kind.CLAW) {
                    GrimMonarchClawSweep.hitWindow(this, key, old, now,
                            window.damageMultiplier(), window.start(), window.end(), true);
                } else if (window.kind() == GrimMonarchHitWindow.Kind.RADIAL
                        && crossed(old, now, window.start())
                        && this.position().distanceToSqr(this.landingPosition) < 0.25D
                        && this.supported(this.position())) {
                    GrimMonarchCombat.radialHit(this, this.landingPosition, window.radius(),
                            window.damageMultiplier(), 2.0D, true, true);
                }
            }
        }
    }

    @Nullable
    private GrimMonarchMotion dashMotion(GrimMonarchMove move) {
        for (GrimMonarchPhase phase : move.phases()) {
            for (GrimMonarchMotion motion : phase.motions()) {
                if (motion.kind() == GrimMonarchMotion.Kind.DASH) {
                    return motion;
                }
            }
        }
        return null;
    }

    private boolean pathHit(GrimMonarchMove move, float old, float now) {
        for (GrimMonarchPhase phase : move.phases()) {
            for (GrimMonarchHitWindow window : phase.hits()) {
                if (window.kind() == GrimMonarchHitWindow.Kind.PATH
                        && old < window.end() && now > window.start()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void applyBackstep(GrimMonarchMove move, float old, float now) {
        for (GrimMonarchPhase phase : move.phases()) {
            for (GrimMonarchMotion motion : phase.motions()) {
                if (motion.kind() != GrimMonarchMotion.Kind.BACKSTEP) {
                    continue;
                }
                double overlap = Math.max(0.0F, Math.min(now, motion.start() + motion.duration())
                        - Math.max(old, motion.start()));
                if (overlap <= 0.0D) {
                    continue;
                }
                double distance = motion.distance() * overlap / (double) motion.duration();
                this.safeMove(this.heading.scale(-distance), false);
            }
        }
    }

    private void applyMoveEvents(GrimMonarchMove move, float old, float now) {
        for (GrimMonarchMoveEvent event : move.events()) {
            if (!crossed(old, now, event.time())) {
                continue;
            }
            if ("soul_ring".equals(event.key())) {
                this.beginCircle();
            } else if ("summon_wave".equals(event.key())) {
                this.summonSoulStars();
            } else if ("echo_spawn".equals(event.key())) {
                this.echoSpawnedThisCast = GrimMonarchCombat.spawnEcho(this);
            } else if ("calamity_ripple".equals(event.key())) {
                GrimMonarchCombat.spawnCalamityRipple(this);
            }
        }
    }

    private static boolean crossed(float old, float now, float time) {
        return old < time && now >= time;
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide && !this.ready()) {
            this.startMove(this.isEcho() ? GrimMonarchMoveTable.ECHO_SPAWN : GrimMonarchMoveTable.SPAWN,
                    this.isEcho() ? GrimMonarchAction.ECHO_SPAWN : GrimMonarchAction.SPAWN);
            this.entityData.set(READY, true);
        }
        this.riseFromLiquid();
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        if (this.isEcho() && !this.isDeadOrDying()) {
            this.tickEchoOwner();
        }
        MiscCapHelper.updateMobTarget(this);
        if (this.action() != GrimMonarchAction.IDLE) {
            this.getNavigation().stop();
            Vec3 movement = this.getDeltaMovement();
            this.setDeltaMovement(0.0, movement.y, 0.0);
            if (this.moveRunner.isRunning()) {
                float old = this.moveRunner.seconds();
                this.moveRunner.tick();
                this.tickMove(old, this.moveRunner.seconds());
                if (this.moveRunner.isRunning() && this.moveRunner.finished()) {
                    this.advanceMove();
                }
            }
        }
        if (this.isDeadOrDying()) {
            return;
        }
        if (!this.isEcho() && !this.nightRegistered) {
            this.nightRegistered = true;
            this.registerNight();
        }
        this.tickSoulMechanics();
        if (this.moddedInvul > 0) {
            --this.moddedInvul;
        }
        if (this.independentInvul > 0) {
            --this.independentInvul;
        }
        if (this.dynamicReductionTicks > 0) {
            --this.dynamicReductionTicks;
        }
        if (this.teleportCooldown > 0) {
            --this.teleportCooldown;
        }
        if (this.rapidTeleportCooldown > 0) {
            --this.rapidTeleportCooldown;
        }
        if (this.teleportAttemptTimer > 0) {
            --this.teleportAttemptTimer;
        }
        if (this.rapidHitTicks > 0) {
            --this.rapidHitTicks;
            if (this.rapidHitTicks <= 0) {
                this.rapidHits = 0;
            }
        }
        if (this.summonStarsRemaining > 0) {
            if (this.summonStarTimer <= 0.0F) {
                GrimMonarchCombat.summonSoulStar(this, this.getTarget());
                --this.summonStarsRemaining;
                this.summonStarTimer += this.summonStarSpacing;
                this.level().broadcastEntityEvent(this, (byte) 61);
            }
            this.summonStarTimer -= 1.0F;
        }
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            target.addEffect(new MobEffectInstance(
                    GoetyEffects.PLUNGE.get(),
                    GrimMonarchConstants.PLUNGE_TICKS,
                    0,
                    false,
                    false));
        }
        this.cooldowns.replaceAll((key, value) -> Math.max(0, value - 1));
        if (this.decisionDelay > 0) {
            --this.decisionDelay;
        }
        if (!this.isEchoSummoner() && this.getHealth() / this.getMaxHealth()
                <= GrimMonarchConstants.ECHO_HEALTH_THRESHOLD) {
            this.echoPending = true;
        }
        if (this.circleRemaining > 0 && this.tickCount % 2 == 0) {
            int total = this.circleSize();
            double angle = (double) this.circleIndex++ * Math.PI * 2.0D / (double) Math.max(1, total);
            this.spawnCircleShard(this.circleOrigin.add(
                    Math.cos(angle) * GrimMonarchConstants.SOUL_STAR_CIRCLE_RADIUS, 0.8D,
                    Math.sin(angle) * GrimMonarchConstants.SOUL_STAR_CIRCLE_RADIUS));
            --this.circleRemaining;
        }
        if (MobsConfig.GrimMonarchBossBar.get() && this.tickCount % 5 == 0) {
            this.bossInfo.update();
        }
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
    }

    private void registerNight() {
        if (!this.level().isClientSide && !this.isEcho()
                && this.level() instanceof ServerLevel server
                && server.getServer() != null) {
            GrimMonarchNightData.get(server).register(this.getUUID());
        }
    }

    private void unregisterNight() {
        if (!this.level().isClientSide && !this.isEcho()
                && this.level() instanceof ServerLevel server
                && server.getServer() != null) {
            GrimMonarchNightData.get(server).unregister(this.getUUID());
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 61) {
            SoulSummonVFX.soul(this.level(), this.position(), this.getYRot(), 1.0D, false);
            SoulSummonVFX.spark(this.level(), this.position(), this.getRandom(), false);
            return;
        }
        super.handleEntityEvent(id);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide
                && source.is(DamageTypes.FELL_OUT_OF_WORLD)
                && source.getDirectEntity() == null) {
            if (this.isEcho()) {
                GrimMonarchEntity owner = this.echoOwnerEntity();
                if (owner != null) {
                    owner.onEchoDied(this);
                }
            }
            this.discard();
            return false;
        }
        if (!this.level().isClientSide && source.is(DamageTypes.GENERIC_KILL)) {
            this.bypassHealthCap = true;
            try {
                return super.hurt(source, amount);
            } finally {
                this.bypassHealthCap = false;
            }
        }
        if (!this.level().isClientSide && this.isEcho()
                && (this.action() == GrimMonarchAction.ECHO_SPAWN || !this.ready())) {
            return false;
        }
        if (this.moddedInvul > 0) {
            return false;
        }
        if (this.independentInvul > 0
                && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        Entity attacker = source.getEntity();
        if (attacker != null
                && this.distanceToSqr(attacker) > GrimMonarchConstants.DAMAGE_IMMUNITY_RANGE_SQR
                && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        amount = this.applySmiteSuppression(source, amount);
        if (this.action().calamity()) {
            amount *= GrimMonarchConstants.CALAMITY_DAMAGE_MULTIPLIER;
        }
        if (attacker instanceof OwnableEntity ownable && ownable.getOwner() != null) {
            amount *= GrimMonarchConstants.OWNED_DAMAGE_MULTIPLIER;
        }
        if (this.dynamicReductionTicks > 0
                && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            amount *= GrimMonarchConstants.DYNAMIC_REDUCTION_MULTIPLIER;
        }
        boolean bypassInvulnerability = source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                || source.is(DamageTypeTags.BYPASSES_COOLDOWN);
        float healthBefore = this.getHealth();
        boolean result = super.hurt(source, amount);
        if (result && this.getHealth() < healthBefore) {
            this.dynamicReductionTicks = GrimMonarchConstants.DYNAMIC_REDUCTION_TICKS;
            if (!this.isDeadOrDying()) {
                this.handleRapidDamage(source);
            }
        }
        if (result && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.independentInvul = GrimMonarchConstants.INDEPENDENT_INVULNERABILITY_TICKS;
        }
        if (result && bypassInvulnerability) {
            this.moddedInvul = GrimMonarchConstants.BYPASS_INVULNERABILITY_TICKS;
        }
        if (this.pendingSmiteReserveDeduction > 0 && this.getHealth() < healthBefore) {
            this.regenHeal = Math.max(0, this.regenHeal - this.pendingSmiteReserveDeduction);
        }
        this.pendingSmitingBonus = 0.0F;
        this.pendingSmiteReserveDeduction = 0;
        return result;
    }

    private float applySmiteSuppression(DamageSource source, float amount) {
        this.pendingSmitingBonus = 0.0F;
        this.pendingSmiteReserveDeduction = 0;
        if (this.level().isClientSide || this.regenHeal <= 0) {
            return amount;
        }
        if (!(source.getDirectEntity() instanceof LivingEntity attacker)) {
            return amount;
        }
        if (!ModDamageSource.physicalAttacks(source)) {
            return amount;
        }
        ItemStack weapon = attacker.getMainHandItem();
        int weaponLevel = weapon.getEnchantmentLevel(Enchantments.SMITE);
        MobEffectInstance smiting = attacker.getEffect(GoetyEffects.SMITING.get());
        int smitingLevel = smiting != null ? smiting.getAmplifier() + 1 : 0;
        if (weaponLevel <= 0 && smitingLevel <= 0) {
            return amount;
        }
        int level = Mth.clamp(Math.max(weaponLevel, smitingLevel), 1, 5);
        float vanillaBonus = Enchantments.SMITE.getDamageBonus(weaponLevel, MobType.UNDEAD, weapon);
        if (attacker instanceof Player player) {
            vanillaBonus *= player.getAttackStrengthScale(0.5F);
        }
        amount = Math.max(0.0F, amount - vanillaBonus);
        if (smitingLevel > weaponLevel) {
            this.pendingSmitingBonus = Enchantments.SMITE.getDamageBonus(smitingLevel, MobType.UNDEAD, weapon);
        }
        this.pendingSmiteReserveDeduction = Math.min(this.regenHeal, level * 4);
        return amount;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return effect.getEffect().isBeneficial() && super.canBeAffected(effect);
    }

    @Override
    public boolean addEffect(MobEffectInstance effect, @Nullable Entity source) {
        return effect.getEffect().isBeneficial() && super.addEffect(effect, source);
    }

    @Override
    public void setHealth(float health) {
        if (this.level() != null
                && !this.level().isClientSide
                && !this.bypassHealthCap) {
            float cap = (float) (double) AttributesConfig.GrimMonarchDamageCap.get();
            if (cap > 0.0F && this.getHealth() - health > cap) {
                health = this.getHealth() - cap;
            }
        }
        super.setHealth(health);
    }

    public void drainSoulFrom(Player player) {
        if (this.level().isClientSide) {
            return;
        }
        SoulDebt debt = this.soulDebts.computeIfAbsent(player.getUUID(), key -> new SoulDebt());
        debt.decayTicks = 100;
        int cost = Math.min(5, Math.max(1, debt.level)) * 10;
        if (SEHelper.getSoulsAmount(player, cost)) {
            SEHelper.decreaseSoulsNoDiscount(player, cost);
            this.regenHeal += 10;
            this.regenPayoutTicks = 100;
            if (debt.level < 5) {
                ++debt.level;
            }
        } else {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false));
        }
    }

    private void tickSoulMechanics() {
        if (!this.soulDebts.isEmpty()) {
            Iterator<Map.Entry<UUID, SoulDebt>> iterator = this.soulDebts.entrySet().iterator();
            while (iterator.hasNext()) {
                SoulDebt debt = iterator.next().getValue();
                if (debt.decayTicks > 0) {
                    --debt.decayTicks;
                }
                if (debt.decayTicks <= 0) {
                    if (debt.level > 1) {
                        --debt.level;
                        debt.decayTicks = 100;
                    } else {
                        iterator.remove();
                    }
                }
            }
        }
        if (this.regenPayoutTicks > 0 && this.tickCount % 10 == 0) {
            int intervals = Math.max(1, (this.regenPayoutTicks + 9) / 10);
            int amount = intervals <= 1 ? this.regenHeal : this.regenHeal / intervals;
            if (amount > 0) {
                this.heal(amount);
                this.regenHeal -= amount;
            }
            this.regenPayoutTicks = Math.max(0, this.regenPayoutTicks - 10);
            if (this.regenPayoutTicks == 0 && this.regenHeal > 0) {
                this.heal(this.regenHeal);
                this.regenHeal = 0;
            }
        }
    }

    private static final class SoulDebt {
        private int level = 1;
        private int decayTicks = 100;
    }

    private boolean isEchoSummoner() {
        return false;
    }

    @Nullable
    private Vec3 chooseLanding() {
        double range = GrimMonarchConstants.CALAMITY_JUMP_RANGE;
        AABB search = this.getBoundingBox().move(this.jumpOrigin.subtract(this.position())).inflate(range);
        List<LivingEntity> candidates = this.level().getEntitiesOfClass(LivingEntity.class, search,
                target -> this.attackCandidate(target)
                        && this.calamityAimPoint(target).distanceToSqr(this.jumpOrigin) <= range * range
                        && this.hasLineOfSight(target));
        candidates.sort(Comparator.comparingDouble(target -> this.calamityAimPoint(target).distanceToSqr(this.jumpOrigin)));
        for (int i = 0; i < Math.min(8, candidates.size()); ++i) {
            LivingEntity target = candidates.get(i);
            Vec3 landing = this.landingNear(this.calamityAimPoint(target), range);
            if (landing != null) {
                this.selectAttackTarget(target);
                return landing;
            }
        }
        LivingEntity current = this.getTarget();
        if (this.attackCandidate(current) && this.hasLineOfSight(current)) {
            this.calamityAim = this.calamityAimPoint(current);
        }
        if (this.calamityAim == null) {
            return null;
        }
        Vec3 offset = this.calamityAim.subtract(this.jumpOrigin);
        double distance = offset.length();
        double scale = distance > range ? range / distance : 1.0D;
        Vec3 aim = this.jumpOrigin.add(offset.x * scale, 0.0D, offset.z * scale);
        Vec3 landing = this.landingNear(aim, range);
        if (landing == null && current != null && largeTarget(current)) {
            landing = this.landingNear(current.getBoundingBox().getCenter(), range);
        }
        return landing;
    }

    private static boolean largeTarget(LivingEntity target) {
        return target.getBbWidth() >= GrimMonarchConstants.LARGE_TARGET_WIDTH
                || target.getBbHeight() >= GrimMonarchConstants.LARGE_TARGET_HEIGHT;
    }

    private double targetDistanceSqr(LivingEntity target, Vec3 origin) {
        double distance = target.distanceToSqr(origin);
        if (!largeTarget(target)) {
            return distance;
        }
        AABB box = target.getBoundingBox();
        double dx = origin.x - Mth.clamp(origin.x, box.minX, box.maxX);
        double dy = origin.y - Mth.clamp(origin.y, box.minY, box.maxY);
        double dz = origin.z - Mth.clamp(origin.z, box.minZ, box.maxZ);
        return Math.min(distance, dx * dx + dy * dy + dz * dz);
    }

    private double calamityReach(LivingEntity target) {
        double distance = this.distanceTo(target);
        if (!largeTarget(target)) {
            return distance;
        }
        AABB box = target.getBoundingBox();
        AABB self = this.getBoundingBox();
        double dx = Math.max(0.0D, Math.max(self.minX - box.maxX, box.minX - self.maxX));
        double dy = Math.max(0.0D, Math.max(self.minY - box.maxY, box.minY - self.maxY));
        double dz = Math.max(0.0D, Math.max(self.minZ - box.maxZ, box.minZ - self.maxZ));
        return Math.min(distance, Math.sqrt(dx * dx + dy * dy + dz * dz));
    }

    private Vec3 calamityAimPoint(LivingEntity target) {
        if (!largeTarget(target)) {
            return target.position();
        }
        AABB box = target.getBoundingBox();
        return new Vec3(
                Mth.clamp(this.jumpOrigin.x, box.minX, box.maxX),
                target.position().y,
                Mth.clamp(this.jumpOrigin.z, box.minZ, box.maxZ));
    }

    @Nullable
    private Vec3 landingNear(Vec3 target, double range) {
        if (this.flightTop == null) {
            return null;
        }
        double spread = Math.min(2.5D, GrimMonarchConstants.CALAMITY_SLAM_RADIUS * 0.75D);
        for (int i = 0; i < 17; ++i) {
            double distance = i == 0 ? 0.0D : (i <= 8 ? spread * 0.5D : spread);
            double angle = (double) ((i - 1) % 8) * Math.PI / 4.0D;
            Vec3 wanted = target.add(Math.cos(angle) * distance, 0.0D, Math.sin(angle) * distance);
            if (wanted.distanceToSqr(this.jumpOrigin) > range * range) {
                continue;
            }
            Vec3 ground = this.findGround(wanted, 6.0D);
            if (ground == null || !this.supported(ground) || !this.fitsAt(ground)) {
                continue;
            }
            Vec3 above = new Vec3(ground.x, this.flightTop.y, ground.z);
            if (!this.canFlyRoute(this.flightTop, above) || !this.canFlyRoute(above, ground)) {
                continue;
            }
            return ground;
        }
        return null;
    }

    private boolean clearTakeoff(Vec3 from, Vec3 top) {
        Vec3 previous = from;
        for (int i = 1; i <= 40; ++i) {
            double u = (double) i / 40.0D;
            Vec3 next = from.add(
                    (top.x - from.x) * GrimMonarchCombatMath.flightTravel(u),
                    (top.y - from.y) * GrimMonarchCombatMath.rise(u),
                    (top.z - from.z) * GrimMonarchCombatMath.flightTravel(u));
            if (!this.canFlyRoute(previous, next)) {
                return false;
            }
            previous = next;
        }
        return true;
    }

    private boolean canFlyRoute(Vec3 from, Vec3 to) {
        int steps = Math.max(1, (int) Math.ceil(from.distanceTo(to) / 0.25D));
        for (int i = 0; i <= steps; ++i) {
            if (!this.fitsAt(from.lerp(to, (double) i / (double) steps))) {
                return false;
            }
        }
        return true;
    }

    private void beginCircle() {
        if (this.circleSize() == 0 || !this.circleReady()) {
            return;
        }
        this.circleRemaining = this.circleSize();
        this.circleIndex = 0;
        this.circleOrigin = this.position();
        this.cooldowns.put("shards", ticks(GrimMonarchConstants.SOUL_STAR_COOLDOWN_SECONDS));
    }

    private int circleSize() {
        return GrimMonarchConstants.SOUL_STARS_PER_CIRCLE;
    }

    private boolean circleReady() {
        return this.circleSize() == 0
                || (this.cooldowns.getOrDefault("shards", 0) == 0 && this.circleRemaining == 0);
    }

    private void spawnCircleShard(Vec3 at) {
        Vec3 ground = this.findGround(at, 6.0D);
        Vec3 point = (ground == null ? at : ground).add(
                0.0D, GrimMonarchConstants.QUAD_STAR_SPAWN_HEIGHT, 0.0D);
        GrimMonarchCombat.spawnQuadStar(this, point);
    }

    private void summonSoulStars() {
        int count = this.soulStarCount();
        this.summonStarsRemaining = count;
        this.summonStarSpacing = count > 1
                ? GrimMonarchConstants.SUMMON_STAR_WINDOW_TICKS / (float) (count - 1)
                : 0.0F;
        this.summonStarTimer = 0.0F;
    }

    private int soulStarCount() {
        if (this.isEcho()) {
            return GrimMonarchConstants.SUMMON_BASE_STARS;
        }
        float health = this.getHealth() / this.getMaxHealth();
        double factor = health < 0.2F ? 2.5D
                : health < 0.4F ? 2.0D
                : health < 0.6F ? 1.5D
                : health < 0.8F ? 1.25D
                : 1.0D;
        return Math.max(1, (int) Math.round(GrimMonarchConstants.SUMMON_BASE_STARS * factor));
    }

    private void chooseAction() {
        if (this.decisionDelay <= 0) {
            this.refreshDecisionTarget();
        }
        LivingEntity target = this.getTarget();
        if (!this.validVictim(target)) {
            if (this.isEcho() && this.followEchoOwner()) {
                return;
            }
            this.getNavigation().stop();
            return;
        }
        double distance = this.distanceTo(target);
        if (this.decisionDelay > 0) {
            this.faceTarget();
            return;
        }
        boolean sight = this.hasLineOfSight(target);
        if (!this.isEcho() && this.teleportCooldown <= 0 && this.teleportAttemptTimer <= 0
                && (!sight || distance > GrimMonarchConstants.TELEPORT_PURSUIT_RANGE)) {
            this.teleportAttemptTimer = ticks(GrimMonarchConstants.TELEPORT_ATTEMPT_SECONDS);
            if ((!sight || this.getRandom().nextFloat() < 0.5F) && this.teleportNearTarget(target)) {
                return;
            }
        }
        if (!this.isEcho() && this.available("echo") && !GrimMonarchCombat.hasEcho(this)
                && (this.echoPending || this.getHealth() / this.getMaxHealth()
                <= GrimMonarchConstants.ECHO_HEALTH_THRESHOLD)) {
            this.startMove(GrimMonarchMoveTable.ECHO, GrimMonarchAction.CLONE);
            return;
        }
        double health = this.getHealth() / this.getMaxHealth();
        List<GrimMonarchAction> ready = new ArrayList<>();
        if (sight && this.available("dash")
                && distance >= GrimMonarchConstants.DASH_MIN_RANGE
                && distance <= GrimMonarchConstants.DASH_MAX_RANGE) {
            ready.add(GrimMonarchAction.DASH_WINDUP);
        }
        if (this.available("summon") && distance >= GrimMonarchConstants.SUMMON_MIN_RANGE
                && distance < 32.0D) {
            ready.add(GrimMonarchAction.SUMMON);
        }
        if (sight && this.available("sweep")
                && distance <= GrimMonarchConstants.SWEEP_TRIGGER_RANGE && this.circleReady()) {
            ready.add(GrimMonarchAction.SWEEP);
        }
        if (sight && health <= GrimMonarchConstants.CALAMITY_HEALTH_THRESHOLD
                && this.available("calamity")
                && this.calamityReach(target) <= GrimMonarchConstants.CALAMITY_JUMP_RANGE) {
            ready.add(GrimMonarchAction.CALAMITY_ONE);
        }
        GrimMonarchAction next = ready.stream()
                .min(Comparator.comparingLong(action -> this.lastAttackUse.getOrDefault(this.group(action), -1L)))
                .orElse(null);
        if (next != null) {
            if (next == GrimMonarchAction.DASH_WINDUP) {
                this.startMove(GrimMonarchMoveTable.DASH, GrimMonarchAction.DASH_WINDUP);
            } else if (next == GrimMonarchAction.SWEEP) {
                this.startMove(GrimMonarchMoveTable.SWEEP, GrimMonarchAction.SWEEP);
            } else if (next == GrimMonarchAction.SUMMON) {
                this.startMove(GrimMonarchMoveTable.SUMMON, GrimMonarchAction.SUMMON);
            } else {
                this.startMove(GrimMonarchMoveTable.DOOM_DIVE_START, GrimMonarchAction.CALAMITY_ONE);
            }
            return;
        }
        if (distance > 3.1D) {
            if (this.tickCount % 8 == 0 || this.getNavigation().isDone()) {
                this.getNavigation().moveTo(target, 1.0D);
            }
        } else {
            this.getNavigation().stop();
            this.faceTarget();
        }
    }

    private void tickEchoOwner() {
        GrimMonarchEntity owner = this.echoOwnerEntity();
        if (owner == null || !owner.isAlive()) {
            this.echoFarTicks = 0;
            return;
        }
        if (this.getTarget() == null) {
            LivingEntity inherited = owner.getTarget();
            if (this.validVictim(inherited)) {
                this.selectAttackTarget(inherited);
            }
        }
        if (this.action() != GrimMonarchAction.ECHO_SPAWN && this.action() != GrimMonarchAction.DEATH
                && this.isStuckInWall()) {
            if (this.teleportNearOwner(owner)) {
                this.echoFarTicks = 0;
                return;
            }
        }
        boolean far = owner.level() != this.level()
                || this.distanceToSqr(owner) > GrimMonarchConstants.ECHO_TELEPORT_DISTANCE
                * GrimMonarchConstants.ECHO_TELEPORT_DISTANCE;
        if (far) {
            ++this.echoFarTicks;
            if (this.echoFarTicks >= GrimMonarchConstants.ECHO_TELEPORT_TICKS
                    && this.teleportNearOwner(owner)) {
                this.echoFarTicks = 0;
            }
        } else {
            this.echoFarTicks = 0;
        }
    }

    private boolean followEchoOwner() {
        GrimMonarchEntity owner = this.echoOwnerEntity();
        if (owner == null || !owner.isAlive()) {
            return false;
        }
        double stand = ((double) owner.getBbWidth() + (double) this.getBbWidth()) * 0.5D
                + GrimMonarchConstants.ECHO_FOLLOW_STAND_GAP;
        double start = stand + GrimMonarchConstants.ECHO_FOLLOW_START_GAP;
        double distance = this.distanceToSqr(owner);
        if (distance > start * start || this.echoFollowing && distance > stand * stand) {
            this.echoFollowing = true;
            if (--this.echoRepathTicks <= 0) {
                this.echoRepathTicks = GrimMonarchConstants.ECHO_FOLLOW_REPATH_TICKS;
                this.getNavigation().moveTo(owner, GrimMonarchConstants.ECHO_FOLLOW_SPEED);
            }
        } else if (this.echoFollowing) {
            this.echoFollowing = false;
            this.echoRepathTicks = 0;
            this.getNavigation().stop();
        }
        this.getLookControl().setLookAt(owner, 10.0F, (float) this.getMaxHeadXRot());
        return true;
    }

    private boolean isStuckInWall() {
        return !this.level().noCollision(this, this.getBoundingBox().deflate(0.05D));
    }

    private boolean teleportNearOwner(GrimMonarchEntity owner) {
        if (!(owner.level() instanceof ServerLevel ownerLevel)) {
            return false;
        }
        if (this.level() != ownerLevel) {
            Entity changed = this.changeDimension(ownerLevel);
            if (changed == null) {
                return false;
            }
            if (changed != this) {
                return changed instanceof GrimMonarchEntity echo && echo != this
                        && echo.teleportNearOwner(owner);
            }
        }
        for (double radius : new double[]{4.0D, 6.0D, 8.0D}) {
            for (int attempt = 0; attempt < 16; ++attempt) {
                double angle = this.getRandom().nextDouble() * Math.PI * 2.0D;
                Vec3 candidate = owner.position().add(Math.cos(angle) * radius, 0.0D, Math.sin(angle) * radius);
                Vec3 at = this.findGround(candidate, 6.0D);
                if (at == null || !this.supported(at) || !this.fitsAt(at)) {
                    continue;
                }
                this.teleportTo(at.x, at.y, at.z);
                this.setYRot(owner.getYRot());
                this.yHeadRot = this.getYRot();
                this.yBodyRot = this.getYRot();
                this.getNavigation().stop();
                this.echoFollowing = false;
                this.echoRepathTicks = 0;
                this.fallDistance = 0.0F;
                return true;
            }
        }
        return false;
    }

    private void handleRapidDamage(DamageSource source) {
        if (this.level().isClientSide || this.rapidTeleportCooldown > 0) {
            return;
        }
        GrimMonarchAction action = this.action();
        if (action != GrimMonarchAction.IDLE && action != GrimMonarchAction.SUMMON) {
            return;
        }
        if (this.isContinuousDamage(source)) {
            this.rapidHits = 0;
            this.rapidHitTicks = 0;
            this.tryRapidTeleport();
            return;
        }
        if (this.rapidHitTicks <= 0) {
            this.rapidHits = 0;
            this.rapidHitTicks = GrimMonarchConstants.TELEPORT_RAPID_WINDOW;
        }
        ++this.rapidHits;
        if (this.rapidHits >= GrimMonarchConstants.TELEPORT_RAPID_HITS) {
            this.rapidHits = 0;
            this.rapidHitTicks = 0;
            this.tryRapidTeleport();
        }
    }

    private boolean tryRapidTeleport() {
        if (this.level().isClientSide || this.rapidTeleportCooldown > 0) {
            return false;
        }
        boolean summoning = this.action() == GrimMonarchAction.SUMMON;
        if (!this.teleportRandomAroundSelf()) {
            return false;
        }
        this.rapidTeleportCooldown = ticks(GrimMonarchConstants.TELEPORT_RAPID_COOLDOWN_SECONDS);
        if (summoning) {
            this.interruptSummon();
        }
        this.rapidHits = 0;
        this.rapidHitTicks = 0;
        return true;
    }

    private void interruptSummon() {
        this.moveRunner.stop();
        this.summonStarsRemaining = 0;
        this.summonStarTimer = 0.0F;
        this.summonStarSpacing = 0.0F;
        this.setVisualAction(GrimMonarchAction.IDLE);
    }

    private boolean isContinuousDamage(DamageSource source) {
        if (ModDamageSource.hellfireAttacks(source)) {
            return true;
        }
        Entity direct = source.getDirectEntity();
        if (direct instanceof AbstractSpellCloud || direct instanceof AcidPool) {
            return true;
        }
        String id = source.getMsgId().toLowerCase(Locale.ROOT);
        if (id.contains("hellfire") || id.contains("acid") || id.contains("cloud")
                || id.contains("beam") || id.contains("corrupt") || id.contains("monsoon")
                || id.contains("potion") || id.contains("brew") || id.contains("gas")
                || id.contains("venom")) {
            return true;
        }
        return this.beamHitsMe(direct) || this.standingInHarmfulCloud();
    }

    private boolean beamHitsMe(@Nullable Entity caster) {
        if (!(caster instanceof LivingEntity)) {
            return false;
        }
        AABB box = this.getBoundingBox().inflate(0.6D);
        for (AbstractBeam beam : this.level().getEntitiesOfClass(AbstractBeam.class,
                this.getBoundingBox().inflate(AbstractBeam.MAX_RAYTRACE_DISTANCE))) {
            if (beam.getOwner() != caster) {
                continue;
            }
            Vec3 start = beam.position();
            Vec3 end = start.add(beam.getViewVector(1.0F).scale(AbstractBeam.MAX_RAYTRACE_DISTANCE));
            if (box.clip(start, end).isPresent()) {
                return true;
            }
        }
        return false;
    }

    private boolean standingInHarmfulCloud() {
        AABB box = this.getBoundingBox();
        for (AreaEffectCloud cloud : this.level().getEntitiesOfClass(AreaEffectCloud.class, box.inflate(2.0D))) {
            if (!cloud.isWaiting() && cloud.getBoundingBox().intersects(box)
                    && this.hostileCloud(cloud.getOwner())) {
                return true;
            }
        }
        for (BrewEffectCloud cloud : this.level().getEntitiesOfClass(BrewEffectCloud.class, box.inflate(2.0D))) {
            if (!cloud.isWaiting() && cloud.getBoundingBox().intersects(box)
                    && this.hostileCloud(cloud.getOwner())) {
                return true;
            }
        }
        return false;
    }

    private boolean hostileCloud(@Nullable LivingEntity owner) {
        return owner != null && owner != this && !MobUtil.areAllies(this, owner);
    }

    private boolean teleportNearTarget(LivingEntity target) {
        if (this.level().isClientSide || target == null || !target.isAlive()) {
            return false;
        }
        for (int attempt = 0; attempt < 24; ++attempt) {
            double angle = this.getRandom().nextDouble() * Math.PI * 2.0D;
            double radius = GrimMonarchConstants.TELEPORT_NEAR_MIN
                    + this.getRandom().nextDouble()
                    * (GrimMonarchConstants.TELEPORT_NEAR_MAX - GrimMonarchConstants.TELEPORT_NEAR_MIN);
            Vec3 candidate = target.position().add(
                    Math.cos(angle) * radius, 0.0D, Math.sin(angle) * radius);
            Vec3 at = this.findGround(candidate, 8.0D);
            if (at == null || !this.supported(at) || !this.fitsAt(at)
                    || !this.canSeeFrom(at, target)) {
                continue;
            }
            this.performTeleport(at, target);
            this.teleportCooldown = ticks(GrimMonarchConstants.TELEPORT_COOLDOWN_SECONDS);
            return true;
        }
        Vec3 delta = target.position().subtract(this.position()).multiply(1.0D, 0.0D, 1.0D);
        if (delta.lengthSqr() > 1.0E-6D) {
            Vec3 candidate = target.position().subtract(delta.normalize().scale(
                    GrimMonarchConstants.TELEPORT_NEAR_FALLBACK));
            Vec3 at = this.findGround(candidate, 8.0D);
            if (at != null && this.supported(at) && this.fitsAt(at)
                    && this.canSeeFrom(at, target)) {
                this.performTeleport(at, target);
                this.teleportCooldown = ticks(GrimMonarchConstants.TELEPORT_COOLDOWN_SECONDS);
                return true;
            }
        }
        return false;
    }

    private boolean teleportRandomAroundSelf() {
        if (this.level().isClientSide) {
            return false;
        }
        LivingEntity target = this.getTarget();
        double radius = GrimMonarchConstants.TELEPORT_RANDOM_RADIUS;
        double clearance = GrimMonarchConstants.TELEPORT_TARGET_CLEARANCE;
        for (int attempt = 0; attempt < 32; ++attempt) {
            double dx = (this.getRandom().nextDouble() - 0.5D) * 2.0D * radius;
            double dy = (this.getRandom().nextDouble() - 0.5D) * 2.0D * radius;
            double dz = (this.getRandom().nextDouble() - 0.5D) * 2.0D * radius;
            Vec3 candidate = this.position().add(dx, dy, dz);
            Vec3 at = this.findGround(candidate, 20.0D);
            if (at == null || !this.supported(at) || !this.fitsAt(at)) {
                continue;
            }
            if (target != null && target.isAlive()
                    && at.distanceToSqr(target.position()) < clearance * clearance) {
                continue;
            }
            this.performTeleport(at, target);
            return true;
        }
        return false;
    }

    private boolean canSeeFrom(Vec3 at, LivingEntity target) {
        Vec3 from = at.add(0.0D, (double) this.getEyeHeight() * 0.85D, 0.0D);
        return this.level().clip(new ClipContext(from, target.getEyePosition(),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    private void performTeleport(Vec3 at, @Nullable LivingEntity target) {
        Vec3 from = this.position();
        this.teleportTo(at.x, at.y, at.z);
        this.setDeltaMovement(Vec3.ZERO);
        this.getNavigation().stop();
        this.fallDistance = 0.0F;
        if (target != null && target.isAlive()) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            if (dx * dx + dz * dz > 1.0E-6D) {
                float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
                this.setYRot(yaw);
                this.yHeadRot = yaw;
                this.yBodyRot = yaw;
            }
        }
        this.teleportAttemptTimer = ticks(GrimMonarchConstants.TELEPORT_ATTEMPT_SECONDS);
        this.decisionDelay = GrimMonarchConstants.TELEPORT_DECISION_DELAY;
        this.teleportVfx(from, at);
    }

    private void teleportVfx(Vec3 from, Vec3 to) {
        if (!(this.level() instanceof ServerLevel server)) {
            return;
        }
        for (int i = 0; i < 16; ++i) {
            server.sendParticles(ParticleTypes.LARGE_SMOKE,
                    from.x + (this.getRandom().nextDouble() - 0.5D) * this.getBbWidth(),
                    from.y + this.getRandom().nextDouble() * this.getBbHeight(),
                    from.z + (this.getRandom().nextDouble() - 0.5D) * this.getBbWidth(),
                    0, 0.0D, 0.0D, 0.0D, 0.5D);
        }
        server.sendParticles(new TeleportInShockwaveParticleOption(),
                from.x, from.y + 0.5D, from.z, 0, 0.0D, 0.0D, 0.0D, 0.5D);
        for (int i = 0; i < 16; ++i) {
            server.sendParticles(ParticleTypes.LARGE_SMOKE,
                    to.x + (this.getRandom().nextDouble() - 0.5D) * this.getBbWidth(),
                    to.y + this.getRandom().nextDouble() * this.getBbHeight(),
                    to.z + (this.getRandom().nextDouble() - 0.5D) * this.getBbWidth(),
                    0, 0.0D, 0.0D, 0.0D, 0.5D);
        }
        server.sendParticles(new TeleportShockwaveParticleOption(10),
                to.x, to.y + 0.5D, to.z, 0, 0.0D, 0.0D, 0.0D, 0.5D);
        this.level().playSound(null, from.x, from.y, from.z,
                ModSounds.WIGHT_TELEPORT.get(), SoundSource.HOSTILE, 1.0F, 0.5F);
    }

    private boolean available(String key) {
        return this.cooldowns.getOrDefault(key, 0) == 0;
    }

    private void refreshDecisionTarget() {
        if (this.tickCount % 8 != 0) {
            return;
        }
        double range = Math.min(AttributesConfig.GrimMonarchFollowRange.get(),
                GrimMonarchConstants.DASH_MAX_RANGE);
        LivingEntity current = this.getTarget();
        if (this.validVictim(current) && this.targetDistanceSqr(current, this.position()) <= range * range
                && this.hasLineOfSight(current)) {
            return;
        }
        LivingEntity nearby = this.nearestVisibleTarget(this.position(), range);
        if (nearby != null) {
            this.selectAttackTarget(nearby);
        }
    }

    @Nullable
    private LivingEntity nearestVisibleTarget(Vec3 origin, double range) {
        LivingEntity nearest = this.getTarget();
        double distance = range * range;
        if (this.attackCandidate(nearest) && this.hasLineOfSight(nearest)
                && this.targetDistanceSqr(nearest, origin) <= distance) {
            distance = this.targetDistanceSqr(nearest, origin);
        } else {
            nearest = null;
        }
        AABB search = this.getBoundingBox().move(origin.subtract(this.position())).inflate(range);
        for (LivingEntity candidate : this.level().getEntitiesOfClass(LivingEntity.class, search, this::attackCandidate)) {
            double next = this.targetDistanceSqr(candidate, origin);
            if (next < distance - 1.0E-6D && this.hasLineOfSight(candidate)) {
                nearest = candidate;
                distance = next;
            }
        }
        return nearest;
    }

    private void selectAttackTarget(@Nullable LivingEntity target) {
        this.selectingAttackTarget = true;
        try {
            this.setTarget(target);
        } finally {
            this.selectingAttackTarget = false;
        }
    }

    private boolean attackCandidate(@Nullable LivingEntity target) {
        return this.validVictim(target);
    }

    public boolean validVictim(@Nullable LivingEntity target) {
        return target != null && target != this && target.isAlive() && !this.isAlliedTo(target)
                && !target.isAlliedTo(this) && this.canAttack(target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        LivingEntity current = this.getTarget();
        double follow = AttributesConfig.GrimMonarchFollowRange.get();
        if (!this.level().isClientSide && !this.selectingAttackTarget && current != target && this.isAlive()
                && this.validVictim(current) && this.targetDistanceSqr(current, this.position()) <= follow * follow
                && (this.attackMovement() || this.action() == GrimMonarchAction.SUMMON
                || this.action() == GrimMonarchAction.CLONE || this.tickCount < this.targetHeldUntil)) {
            return;
        }
        super.setTarget(target);
        if (!this.level().isClientSide && this.getTarget() != current && this.getTarget() != null) {
            this.targetHeldUntil = this.tickCount + 20;
        }
    }

    private boolean attackMovement() {
        GrimMonarchAction action = this.action();
        return action.calamity() || action == GrimMonarchAction.SWEEP
                || action == GrimMonarchAction.DASH_WINDUP || action == GrimMonarchAction.DASH_RUN
                || action == GrimMonarchAction.DASH_FINISH;
    }

    private void commitAttackAim() {
        double range = Math.min(AttributesConfig.GrimMonarchFollowRange.get(),
                GrimMonarchConstants.DASH_MAX_RANGE);
        LivingEntity next = this.nearestVisibleTarget(this.position(), range);
        this.selectAttackTarget(next);
        this.heading = this.towardTarget();
        this.setYRot((float) Math.toDegrees(Math.atan2(-this.heading.x, this.heading.z)));
        this.yHeadRot = this.getYRot();
        this.yBodyRot = this.getYRot();
    }

    private Vec3 forward() {
        double radians = Math.toRadians(this.getYRot());
        return new Vec3(-Math.sin(radians), 0.0D, Math.cos(radians));
    }

    private Vec3 towardTarget() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return this.forward();
        }
        Vec3 delta = target.position().subtract(this.position()).multiply(1.0D, 0.0D, 1.0D);
        return delta.lengthSqr() < 1.0E-8D ? this.forward() : delta.normalize();
    }

    private void faceTarget() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        if (dx * dx + dz * dz < 0.0025D) {
            return;
        }
        float wanted = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float speed = (float) (double) GrimMonarchConstants.ATTACK_TURN_DEGREES_PER_TICK;
        if (this.action() == GrimMonarchAction.IDLE) {
            speed = (float) (double) GrimMonarchConstants.TURN_DEGREES_PER_TICK;
        }
        float yaw = (float) GrimMonarchCombatMath.turn(this.getYRot(), wanted, speed);
        this.setYRot(yaw);
        this.yHeadRot = yaw;
        this.yBodyRot = yaw;
    }

    private void riseFromLiquid() {
        if (this.level().isClientSide || !this.isAlive() || this.noPhysics || this.isPassenger()) {
            return;
        }
        BlockPos feet = this.blockPosition();
        FluidState fluid = this.level().getFluidState(feet);
        if (fluid.isEmpty()) {
            return;
        }
        double top = (float) feet.getY() + fluid.getHeight(this.level(), feet);
        if (this.getY() < top - 0.005D) {
            Vec3 movement = this.getDeltaMovement();
            this.setDeltaMovement(movement.x, Math.min(0.2D, top - this.getY()), movement.z);
            this.fallDistance = 0.0F;
        }
    }

    public boolean canWalkRoute(Vec3 from, Vec3 to) {
        double distance = from.distanceTo(to);
        if (Math.abs(to.y - from.y) > 1.05D) {
            return false;
        }
        int steps = Math.max(1, (int) Math.ceil(distance / 0.35D));
        for (int i = 0; i <= steps; ++i) {
            Vec3 point = from.lerp(to, (double) i / (double) steps);
            if (!this.supported(point)) {
                return false;
            }
            AABB body = this.getBoundingBox().move(point.subtract(this.position())).inflate(0.03D);
            AABB clearance = new AABB(body.minX, body.minY + 1.02D, body.minZ, body.maxX, body.maxY, body.maxZ);
            if (!this.level().noCollision(this, clearance)) {
                return false;
            }
        }
        return true;
    }

    public boolean supported(Vec3 at) {
        double half = (double) this.getBbWidth() * 0.35D;
        for (int i = 0; i < 4; ++i) {
            double x = i % 2 == 0 ? -half : half;
            double z = i < 2 ? -half : half;
            Vec3 point = at.add(x, 0.15D, z);
            BlockHitResult hit = this.level().clip(new ClipContext(point, point.add(0.0D, -1.15D, 0.0D),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hit.getType() == HitResult.Type.MISS
                    && !Double.isFinite(GrimMonarchFluidSurface.column(this.level(), point.x, point.z,
                    point.y - 1.15D, at.y + (double) this.maxUpStep() + 0.001D))) {
                return false;
            }
        }
        return true;
    }

    public boolean fitsAt(Vec3 point) {
        AABB box = this.getBoundingBox().move(point.subtract(this.position()));
        AABB clearance = new AABB(box.minX, box.minY + 0.05D, box.minZ, box.maxX, box.maxY, box.maxZ);
        return this.level().noCollision(this, clearance.inflate(0.03D, 0.0D, 0.03D));
    }

    @Nullable
    public Vec3 findGround(Vec3 around, double search) {
        BlockHitResult hit = this.level().clip(new ClipContext(around.add(0.0D, 2.0D, 0.0D),
                around.add(0.0D, -search, 0.0D), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        double liquid = GrimMonarchFluidSurface.column(this.level(), around.x, around.z,
                around.y - search, around.y + 2.0D);
        if (Double.isFinite(liquid) && (hit.getType() == HitResult.Type.MISS || liquid > hit.getLocation().y)) {
            return new Vec3(around.x, liquid + 0.015D, around.z);
        }
        return hit.getType() == HitResult.Type.MISS ? null : hit.getLocation().add(0.0D, 0.015D, 0.0D);
    }

    private boolean safeMove(Vec3 delta, boolean air) {
        if (delta.lengthSqr() < 1.0E-9D) {
            return false;
        }
        Vec3 from = this.position();
        int steps = Math.max(1, (int) Math.ceil(delta.length() / 0.2D));
        Vec3 step = delta.scale(1.0D / (double) steps);
        for (int i = 0; i < steps; ++i) {
            Vec3 next = this.position().add(step);
            if (!air && !this.supported(next)) {
                break;
            }
            Vec3 before = this.position();
            this.move(MoverType.SELF, step);
            if (this.position().distanceToSqr(before) < step.lengthSqr() * 0.25D) {
                break;
            }
        }
        this.fallDistance = 0.0F;
        return this.position().distanceToSqr(from) > delta.lengthSqr() * 0.5D;
    }

    @Override
    public void move(MoverType type, Vec3 motion) {
        if (!this.level().isClientSide && this.action() == GrimMonarchAction.IDLE && this.onGround()
                && motion.lengthSqr() > 1.0E-8D) {
            Vec3 target = this.position().add(motion.x, 0.0D, motion.z);
            if (!this.canWalkRoute(this.position(), target)) {
                this.getNavigation().stop();
                motion = new Vec3(0.0D, motion.y, 0.0D);
                this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
            }
        }
        if (!this.isPassenger() && !this.noPhysics && motion.y <= 0.0D) {
            double high = this.getY() + (this.onGround() ? (double) this.maxUpStep() : 0.001D);
            double fluidFloor = GrimMonarchFluidSurface.beneath(this.level(),
                    this.getBoundingBox().move(motion.x, 0.0D, motion.z),
                    this.getY() + motion.y - 0.001D, high);
            if (Double.isFinite(fluidFloor) && fluidFloor >= this.getY() + motion.y) {
                motion = new Vec3(motion.x, fluidFloor - this.getY(), motion.z);
            }
        }
        super.move(type, motion);
        if (!this.isPassenger() && !this.noPhysics) {
            double surface = GrimMonarchFluidSurface.beneath(this.level(), this.getBoundingBox(),
                    this.getY() - 0.002D, this.getY() + 0.002D);
            if (Double.isFinite(surface) && this.getDeltaMovement().y <= 0.0D) {
                this.setOnGround(true);
                this.fallDistance = 0.0F;
                Vec3 movement = this.getDeltaMovement();
                this.setDeltaMovement(movement.x, 0.0D, movement.z);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("GrimMonarchAction", this.action().ordinal());
        tag.putLong("GrimMonarchStart", this.actionStarted());
        tag.putBoolean("GrimMonarchReady", this.ready());
        tag.putBoolean("GrimMonarchEchoPending", this.echoPending);
        if (this.ownerUUID != null) {
            tag.putUUID("GrimMonarchOwner", this.ownerUUID);
        }
        if (this.echoUUID != null) {
            tag.putUUID("GrimMonarchEcho", this.echoUUID);
        }
        tag.putInt("GrimMonarchEchoFar", this.echoFarTicks);
        CompoundTag cooldownTag = new CompoundTag();
        this.cooldowns.forEach(cooldownTag::putInt);
        tag.put("GrimMonarchCooldowns", cooldownTag);
        tag.putInt("GrimMonarchRegenHeal", this.regenHeal);
        tag.putInt("GrimMonarchRegenPayout", this.regenPayoutTicks);
        tag.putInt("GrimMonarchModdedInvul", this.moddedInvul);
        tag.putInt("GrimMonarchDynamicReduction", this.dynamicReductionTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }
        if (tag.contains("GrimMonarchAction")) {
            this.entityData.set(READY, tag.getBoolean("GrimMonarchReady"));
        }
        this.echoPending = tag.getBoolean("GrimMonarchEchoPending");
        if (tag.hasUUID("GrimMonarchOwner")) {
            this.ownerUUID = tag.getUUID("GrimMonarchOwner");
        }
        if (tag.hasUUID("GrimMonarchEcho")) {
            this.echoUUID = tag.getUUID("GrimMonarchEcho");
        }
        this.echoFarTicks = tag.getInt("GrimMonarchEchoFar");
        this.regenHeal = tag.getInt("GrimMonarchRegenHeal");
        this.regenPayoutTicks = tag.getInt("GrimMonarchRegenPayout");
        CompoundTag cooldownTag = tag.getCompound("GrimMonarchCooldowns");
        for (String key : cooldownTag.getAllKeys()) {
            this.cooldowns.put(key, cooldownTag.getInt(key));
        }
        if (this.isDeadOrDying()) {
            this.entityData.set(ACTION, GrimMonarchAction.DEATH.ordinal());
            this.entityData.set(START, this.level().getGameTime() - (long) this.deathTime);
            this.entityData.set(READY, true);
        } else {
            this.entityData.set(ACTION, GrimMonarchAction.IDLE.ordinal());
            this.entityData.set(START, this.level().getGameTime());
        }
        if (tag.contains("Health")) {
            super.setHealth(tag.getFloat("Health"));
        }
        this.moddedInvul = tag.getInt("GrimMonarchModdedInvul");
        this.dynamicReductionTicks = tag.getInt("GrimMonarchDynamicReduction");
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void knockback(double strength, double x, double z) {
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(double x, double y, double z) {
    }

    @Override
    public void push(Entity entity) {
    }

    @Override
    protected boolean canRide(Entity entity) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else {
            this.noActionTime = 0;
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (MobsConfig.GrimMonarchBossBar.get() && MainConfig.SpecialBossBar.get()) {
            this.bossInfo.addPlayer(player);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide && this.isEcho()) {
            GrimMonarchEntity owner = this.echoOwnerEntity();
            if (owner != null) {
                owner.onEchoDied(this);
            }
        }
        if (this.isEcho()) {
            this.moveRunner.stop();
            this.setVisualAction(GrimMonarchAction.IDLE);
        } else {
            this.unregisterNight();
            this.startMove(GrimMonarchMoveTable.DEATH, GrimMonarchAction.DEATH);
        }
        super.die(source);
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide && !this.isEcho()
                && reason != Entity.RemovalReason.UNLOADED_TO_CHUNK) {
            this.unregisterNight();
            this.nightRegistered = false;
        }
        super.remove(reason);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        int duration = this.isEcho() ? 40 : 64;
        if (!this.level().isClientSide && this.deathTime >= duration) {
            if (!this.isEcho() && this.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.POOF,
                        this.getX(), this.getY() + (double) this.getBbHeight() * 0.5D, this.getZ(),
                        60, (double) this.getBbWidth() * 0.5D, (double) this.getBbHeight() * 0.5D,
                        (double) this.getBbWidth() * 0.5D, 0.02D);
            }
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    public float echoDissolveProgress(float partial) {
        return this.isEcho()
                ? Mth.clamp(((float) this.deathTime + partial) / 40.0F, 0.0F, 1.0F)
                : 0.0F;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    private class CombatGoal extends Goal {
        private CombatGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        private boolean hasCombatFocus() {
            return !GrimMonarchEntity.this.isDeadOrDying()
                    && (GrimMonarchEntity.this.validVictim(GrimMonarchEntity.this.getTarget())
                    || GrimMonarchEntity.this.isEcho()
                    && GrimMonarchEntity.this.echoOwnerEntity() != null);
        }

        @Override
        public boolean canUse() {
            return this.hasCombatFocus();
        }

        @Override
        public boolean canContinueToUse() {
            return this.hasCombatFocus();
        }

        @Override
        public void tick() {
            if (GrimMonarchEntity.this.action() == GrimMonarchAction.IDLE) {
                GrimMonarchEntity.this.chooseAction();
            }
        }

        @Override
        public void stop() {
            GrimMonarchEntity.this.getNavigation().stop();
        }
    }
}
