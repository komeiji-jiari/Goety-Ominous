package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractPiglinServant;
import com.Polarice3.Goety.common.effects.brew.BlindJumpBrewEffect;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.effects.brew.LaunchBrewEffect;
import com.Polarice3.Goety.common.effects.brew.PurifyBrewEffect;
import com.Polarice3.Goety.common.effects.brew.StripBrewEffect;
import com.Polarice3.Goety.common.effects.brew.TransposeBrewEffect;
import com.Polarice3.Goety.common.effects.brew.WebbedBrewEffect;
import com.Polarice3.Goety.common.effects.brew.block.HarvestBlockEffect;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.*;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.common.entities.util.EffectBlastTrap;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.BrewUtils;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.SoundUtil;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.projectile.FelBolt;
import com.qiuyue.goetyominous.common.entities.projectile.PiglinShamanBrew;
import com.qiuyue.goetyominous.common.entities.projectile.ShamanHealBrew;
import com.qiuyue.goetyominous.common.events.PiglinConversionManager;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class AllyPiglinShaman extends AbstractPiglinServant {

    public static final int ANIM_IDLE = 0;
    public static final int ANIM_MELEE = 1;
    public static final int ANIM_RANGED = 2;
    public static final int ANIM_POTION = 3;
    public static final int ANIM_SPELL = 4;
    public static final int ANIM_SUMMON = 5;
    public static final int ANIM_DEATH = 6;
    public static final int ANIM_TRADE = 7;
    public static final int ANIM_DRINK = 8;
    public static final int ANIM_CONVERT = 9;
    public static final int ANIM_TELEPORT_OUT = 10;

    private static final EntityDataAccessor<Integer> ANIM_STATE =
            SynchedEntityData.defineId(AllyPiglinShaman.class, EntityDataSerializers.INT);

    private static final int MELEE_LENGTH = 30;
    private static final int MELEE_HIT_TICK = 13;
    private static final int MELEE_COOLDOWN_AFTER = 100;
    private static final double RANGED_ATTACK_RADIUS = 12.0D;
    private static final double RANGED_ATTACK_RADIUS_SQR = RANGED_ATTACK_RADIUS * RANGED_ATTACK_RADIUS;
    private static final int POTION_LENGTH = 35;
    private static final int POTION_THROW_TICK = 21;
    private static final int POTION_COOLDOWN = 200;
    private static final int POTION_COOLDOWN_MIN = 40;
    private static final int SPELL_LENGTH = 40;
    private static final int SPELL_CAST_TICK = 30;
    private static final int SPELL_COOLDOWN = 200;
    private static final int BLAST_TRAP_COOLDOWN = 200;
    private static final int SUMMON_LENGTH = 40;
    private static final int SUMMON_SPAWN_TICK = 20;
    private static final int SUMMON_COOLDOWN_FULL = 400;
    private static final int SUMMON_COOLDOWN_HALF = 200;
    private static final int TRADE_LENGTH = 80;
    private static final int TRADE_THROW_TICK = 65;
    private static final int DRINK_LENGTH = 25;
    private static final int DRINK_EFFECT_TICK = 18;
    private static final int HEAL_IDLE_TICKS = 300;
    private static final int HEAL_COOLDOWN = 100;
    private static final int HEAL_ALLY_COOLDOWN = 200;
    private static final int DEATH_LENGTH = 55;
    private static final int TELEPORT_OUT_LENGTH = 13;
    private static final int CONVERT_LENGTH = 60;
    private static final double CONVERT_RANGE = 32.0D;
    private static final int COMBAT_MEMORY = 100;
    private static final int SUMMON_INTERACT_DELAY = 20;
    private static final String[] DISMISS_MESSAGES = {
            "message.goetyominous.ally_shaman.dismiss.1",
            "message.goetyominous.ally_shaman.dismiss.2",
            "message.goetyominous.ally_shaman.dismiss.3",
            "message.goetyominous.ally_shaman.dismiss.4",
            "message.goetyominous.ally_shaman.dismiss.5"
    };

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState meleeAnimationState = new AnimationState();
    public final AnimationState rangedAnimationState = new AnimationState();
    public final AnimationState potionAnimationState = new AnimationState();
    public final AnimationState spellAnimationState = new AnimationState();
    public final AnimationState summonAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();
    public final AnimationState tradeAnimationState = new AnimationState();
    public final AnimationState drinkAnimationState = new AnimationState();
    public final AnimationState convertAnimationState = new AnimationState();
    public final AnimationState teleportOutAnimationState = new AnimationState();

    private boolean summonIdle;
    private int idleSummonCooldown;
    private int actionTick;
    private int meleeCooldown;
    private int potionCooldown;
    private int spellCooldown;
    private int blastCooldown;
    private int summonCooldown;
    private int healCooldown;
    private int healAllyCooldown;
    private int piglinConvertCooldown;
    private int lastHitTime;
    private int lastDamageTicks;
    private int idleTicks;
    private boolean pendingEmergency;
    private boolean pendingHeal;
    private boolean healingDrink;
    private BrewSelection pendingSelection;
    private final List<MobEffectInstance> drinkMobEffects = new ArrayList<>();
    private final List<BrewEffectInstance> drinkBrewEffects = new ArrayList<>();
    private int drinkArea;
    private boolean tradeActive;
    private int tradeTick;
    @Nullable
    private UUID tradePlayerUUID;
    @Nullable
    private DamageSource lastDeathSource;
    private int dismissMessageIndex = -1;
    private final CombatTracker dismissCombatTracker = new CombatTracker(this) {
        @Override
        public Component getDeathMessage() {
            if (AllyPiglinShaman.this.lastDeathSource != null
                    && AllyPiglinShaman.this.lastDeathSource.is(ModDamageSource.DISMISSED)) {
                return AllyPiglinShaman.this.getDismissDeathMessage();
            }
            return super.getDeathMessage();
        }
    };

    public AllyPiglinShaman(EntityType<? extends AllyPiglinShaman> type, Level level) {
        super(type, level);
        this.xpReward = 50;
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 16.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.PiglinShamanHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.PiglinShamanArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.PiglinShamanDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.PiglinShamanFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.PiglinShamanHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.PiglinShamanArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.PiglinShamanDamage.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.PiglinShamanFollowRange.get());
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.ShamanServantLimit.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, ANIM_IDLE);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new TradeGuardGoal());
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new MeleeGoal());
        this.goalSelector.addGoal(1, new IdleSummonGoal());
        this.goalSelector.addGoal(1, new DrinkGoal());
        this.goalSelector.addGoal(2, new HealAllyGoal());
        this.goalSelector.addGoal(2, new SummonZPiglinGoal());
        this.goalSelector.addGoal(3, new ConvertPiglinGoal());
        this.goalSelector.addGoal(3, new SummonHoglinGoal());
        this.goalSelector.addGoal(4, new BlastTrapGoal());
        this.goalSelector.addGoal(5, new ThrowPotionGoal());
        this.goalSelector.addGoal(6, new FelBoltRangedGoal());
        this.goalSelector.addGoal(7, new ApproachGoal());
        this.goalSelector.addGoal(8, new com.Polarice3.Goety.common.entities.ally.Summoned.WanderGoal<>(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MeleeCooldown", this.meleeCooldown);
        tag.putInt("PotionCooldown", this.potionCooldown);
        tag.putInt("SpellCooldown", this.spellCooldown);
        tag.putInt("BlastCooldown", this.blastCooldown);
        tag.putInt("SummonCooldown", this.summonCooldown);
        tag.putInt("HealCooldown", this.healCooldown);
        tag.putInt("HealAllyCooldown", this.healAllyCooldown);
        tag.putInt("PiglinConvertCooldown", this.piglinConvertCooldown);
        tag.putInt("LastHitTime", this.lastHitTime);
        tag.putInt("IdleTicks", this.idleTicks);
        tag.putBoolean("SummonIdle", this.summonIdle);
        tag.putInt("IdleSummonCooldown", this.idleSummonCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.meleeCooldown = tag.getInt("MeleeCooldown");
        this.potionCooldown = tag.getInt("PotionCooldown");
        this.spellCooldown = tag.getInt("SpellCooldown");
        this.blastCooldown = tag.getInt("BlastCooldown");
        this.summonCooldown = tag.getInt("SummonCooldown");
        this.healCooldown = tag.getInt("HealCooldown");
        this.healAllyCooldown = tag.getInt("HealAllyCooldown");
        this.piglinConvertCooldown = tag.getInt("PiglinConvertCooldown");
        this.lastHitTime = tag.getInt("LastHitTime");
        this.idleTicks = tag.getInt("IdleTicks");
        this.summonIdle = tag.getBoolean("SummonIdle");
        this.idleSummonCooldown = tag.getInt("IdleSummonCooldown");
        this.setConfigurableAttributes();
        if (this.getHealth() > this.getMaxHealth()) {
            this.setHealth(this.getMaxHealth());
        }
    }

    public int getAnimState() {
        return this.entityData.get(ANIM_STATE);
    }

    public void setAnimState(int state) {
        this.entityData.set(ANIM_STATE, state);
    }

    public boolean isIdle() {
        int state = this.getAnimState();
        return (state == ANIM_IDLE || state == ANIM_RANGED)
                && !this.isDeadOrDying() && !this.tradeActive;
    }

    public boolean isOwnSummon(Entity entity) {
        if (!(entity instanceof Owned owned)) {
            return false;
        }
        if (owned.getTrueOwner() != this) {
            return false;
        }
        return entity instanceof ZPiglinServant || entity instanceof BurningHoglin;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor) && this.level().isClientSide) {
            this.stopActionAnimations();
            switch (this.getAnimState()) {
                case ANIM_MELEE -> this.meleeAnimationState.start(this.tickCount);
                case ANIM_RANGED -> this.rangedAnimationState.start(this.tickCount);
                case ANIM_POTION -> this.potionAnimationState.start(this.tickCount);
                case ANIM_SPELL -> this.spellAnimationState.start(this.tickCount);
                case ANIM_SUMMON -> this.summonAnimationState.start(this.tickCount);
                case ANIM_DEATH -> this.deathAnimationState.start(this.tickCount);
                case ANIM_TRADE -> this.tradeAnimationState.start(this.tickCount);
                case ANIM_DRINK -> this.drinkAnimationState.start(this.tickCount);
                case ANIM_CONVERT -> this.convertAnimationState.start(this.tickCount);
                case ANIM_TELEPORT_OUT -> this.teleportOutAnimationState.start(this.tickCount);
                default -> {
                    this.idleAnimationState.start(this.tickCount);
                    this.walkAnimationState.startIfStopped(this.tickCount);
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    private void stopActionAnimations() {
        this.meleeAnimationState.stop();
        this.rangedAnimationState.stop();
        this.potionAnimationState.stop();
        this.spellAnimationState.stop();
        this.summonAnimationState.stop();
        this.deathAnimationState.stop();
        this.tradeAnimationState.stop();
        this.drinkAnimationState.stop();
        this.convertAnimationState.stop();
        this.teleportOutAnimationState.stop();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        if (this.idleSummonCooldown > 0) {
            --this.idleSummonCooldown;
        }
        if (this.meleeCooldown > 0) {
            --this.meleeCooldown;
        }
        if (this.potionCooldown > 0) {
            --this.potionCooldown;
        }
        if (this.spellCooldown > 0) {
            --this.spellCooldown;
        }
        if (this.blastCooldown > 0) {
            --this.blastCooldown;
        }
        if (this.summonCooldown > 0) {
            --this.summonCooldown;
        }
        if (this.healCooldown > 0) {
            --this.healCooldown;
        }
        if (this.healAllyCooldown > 0) {
            --this.healAllyCooldown;
        }
        if (this.piglinConvertCooldown > 0) {
            --this.piglinConvertCooldown;
        }
        if (this.lastHitTime > 0) {
            --this.lastHitTime;
        }
        if (this.lastDamageTicks > 0) {
            --this.lastDamageTicks;
        }
        this.tickTrade();
        this.tickTargetGold();
        this.tickHeal();
        this.tickEmergencyDrink();
    }

    private void tickTargetGold() {
        LivingEntity target = this.getTarget();
        if (target instanceof Player player
                && net.minecraft.world.entity.monster.piglin.PiglinAi.isWearingGold(player)
                && this.getLastHurtByMob() != player) {
            this.setTarget(null);
        }
    }

    private void tickHeal() {
        if (this.getTarget() == null && !this.tradeActive && !this.isDeadOrDying()) {
            ++this.idleTicks;
        } else {
            this.idleTicks = 0;
        }
        if (this.idleTicks >= HEAL_IDLE_TICKS && this.healCooldown <= 0
                && this.getHealth() < this.getMaxHealth() && !this.tradeActive) {
            this.pendingHeal = true;
        }
    }

    private void tickEmergencyDrink() {
        if (this.tradeActive || this.pendingEmergency || this.pendingHeal) {
            return;
        }
        if (this.shouldConsiderDrinking()) {
            this.pendingEmergency = true;
        }
    }

    private boolean shouldConsiderDrinking() {
        LivingEntity target = this.getTarget();
        return this.needsEmergencyDrink() || target != null && target.isAlive();
    }

    private boolean needsEmergencyDrink() {
        if (this.isOnFire() || this.isInWall()) {
            return true;
        }
        for (MobEffectInstance instance : this.getActiveEffects()) {
            if (instance.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                return true;
            }
        }
        if (this.lastDamageTicks > 0) {
            DamageSource source = this.getLastDamageSource();
            if (source != null && (source.is(DamageTypeTags.IS_FIRE)
                    || source.is(DamageTypeTags.IS_FALL)
                    || source.is(DamageTypes.IN_WALL)
                    || source.is(DamageTypes.CACTUS)
                    || source.is(DamageTypes.SWEET_BERRY_BUSH)
                    || ModDamageSource.physicalAttacks(source))) {
                return true;
            }
            if (this.getHealth() < this.getMaxHealth()) {
                return true;
            }
        }
        if (this.isEyeInFluidType(ForgeMod.WATER_TYPE.get())) {
            return true;
        }
        return MobUtil.isInWeb(this) || MobUtil.hasLongNegativeEffects(this);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && this.tradeActive) {
            return;
        }
        super.setTarget(target);
        if (target != null) {
            this.idleTicks = 0;
            this.pendingHeal = false;
            this.healCooldown = 0;
        }
    }

    @Override
    public void setCommandPosEntity(@Nullable LivingEntity living) {
        if (!this.level().isClientSide && living != null
                && PiglinConversionManager.isConvertible(living)
                && !this.canAcceptConvertCommand(living)) {
            return;
        }
        super.setCommandPosEntity(living);
    }

    private boolean canAcceptConvertCommand(LivingEntity target) {
        if (this.isInCombatWithAnything()) {
            return false;
        }
        return !(target instanceof Mob mob) || mob.getTarget() != this;
    }

    private boolean isInCombatWithAnything() {
        return this.getTarget() != null
                || (this.getLastHurtMob() != null && this.tickCount - this.getLastHurtMobTimestamp() <= COMBAT_MEMORY)
                || (this.getLastHurtByMob() != null && this.tickCount - this.getLastHurtByMobTimestamp() <= COMBAT_MEMORY);
    }

    public void setPiglinConvertCooldown(int ticks) {
        this.piglinConvertCooldown = ticks;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_FIRE) && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return false;
        }
        if (this.getHealth() < 10.0F && (source.is(DamageTypes.CACTUS)
                || source.is(DamageTypes.SWEET_BERRY_BUSH)
                || source.is(DamageTypeTags.WITCH_RESISTANT_TO))) {
            return false;
        }
        boolean tradingBefore = this.tradeActive;
        boolean tradeNotThrown = this.tradeTick < TRADE_THROW_TICK;
        boolean result = super.hurt(source, amount);
        if (result) {
            this.lastDamageTicks = 100;
            if (source.getEntity() instanceof LivingEntity living && living != this) {
                this.lastHitTime = 300;
            }
        }
        if (result && (tradingBefore || this.tradeActive)) {
            if (tradeNotThrown) {
                this.spawnAtLocation(new ItemStack(Items.GOLD_INGOT));
                this.playSound(SoundEvents.PIGLIN_JEALOUS, 1.0F, 1.0F);
            }
            this.endTrade();
        }
        return result;
    }

    @Override
    public void tryKill(Player player) {
        if (this.getKillChance() <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    @Override
    protected float getDamageAfterMagicAbsorb(DamageSource source, float damage) {
        damage = super.getDamageAfterMagicAbsorb(source, damage);
        if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            damage *= 0.1F;
        }
        return damage;
    }

    @Override
    public void die(DamageSource source) {
        this.lastDeathSource = source;
        this.dismissMessageIndex = source.is(ModDamageSource.DISMISSED)
                ? this.random.nextInt(DISMISS_MESSAGES.length) : -1;
        this.setAnimState(source.is(ModDamageSource.DISMISSED) ? ANIM_TELEPORT_OUT : ANIM_DEATH);
        this.actionTick = 0;
        this.getNavigation().stop();
        this.pendingEmergency = false;
        this.pendingHeal = false;
        this.healingDrink = false;
        this.tradeActive = false;
        this.tradePlayerUUID = null;
        this.tradeTick = 0;
        super.die(source);
    }

    @Override
    public CombatTracker getCombatTracker() {
        return this.dismissCombatTracker != null ? this.dismissCombatTracker : super.getCombatTracker();
    }

    public Component getDismissDeathMessage() {
        int index = this.dismissMessageIndex;
        if (index < 0 || index >= DISMISS_MESSAGES.length) {
            index = this.random.nextInt(DISMISS_MESSAGES.length);
        }
        return Component.translatable(DISMISS_MESSAGES[index], this.getDisplayName());
    }

    @Override
    public void reviveOwned() {
        super.reviveOwned();
        this.lastDeathSource = null;
        this.dismissMessageIndex = -1;
        this.deathTime = 0;
        this.hurtTime = 0;
        this.actionTick = 0;
        this.pendingEmergency = false;
        this.pendingHeal = false;
        this.healingDrink = false;
        this.tradeActive = false;
        this.tradePlayerUUID = null;
        this.tradeTick = 0;
        this.getNavigation().stop();
        this.setAnimState(ANIM_IDLE);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 35) {
            this.deathTime = 0;
            this.hurtTime = 0;
            this.actionTick = 0;
            this.dismissMessageIndex = -1;
            this.setAnimState(ANIM_IDLE);
        }
        super.handleEntityEvent(id);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        this.getNavigation().stop();
        int deathLength = this.lastDeathSource != null && this.lastDeathSource.is(ModDamageSource.DISMISSED)
                ? TELEPORT_OUT_LENGTH : DEATH_LENGTH;
        if (!this.level().isClientSide && this.deathTime >= deathLength && !this.isRemoved()) {
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.PORTAL, this.getX(),
                        this.getY() + this.getBbHeight() * 0.5D, this.getZ(), 128,
                        this.getBbWidth() * 0.5D, this.getBbHeight() * 0.5D, this.getBbWidth() * 0.5D, 0.02D);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 0.75F);
            this.returnShamanTotem();
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    private void returnShamanTotem() {
        if (this.lastDeathSource != null && this.lastDeathSource.is(ModDamageSource.DISMISSED)) {
            return;
        }
        LivingEntity owner = this.getTrueOwner();
        if (!(owner instanceof Player)) {
            return;
        }
        ItemStack stack = new ItemStack(com.qiuyue.goetyominous.common.items.ModItems.SHAMAN_TOTEM.get());
        ReviveServantItem.setOwnerName(owner, stack);
        ReviveServantItem.setSummon(this, stack);
        FlyingItem flyingItem = new FlyingItem(ModEntityType.FLYING_ITEM.get(), this.level(),
                this.getX(), this.getY(), this.getZ());
        flyingItem.setOwner(owner);
        flyingItem.setItem(stack);
        flyingItem.setParticle(ParticleTypes.WITCH);
        flyingItem.setSecondsCool(ItemConfig.ReviveSecondsCool.get());
        this.level().addFreshEntity(flyingItem);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand == InteractionHand.MAIN_HAND && stack.isEmpty()
                && this.getOwnerId() != null && this.getOwnerId().equals(player.getUUID())) {
            if (this.tickCount < SUMMON_INTERACT_DELAY) {
                return InteractionResult.SUCCESS;
            }
            if (!this.level().isClientSide && !this.summonIdle) {
                if (this.idleSummonCooldown <= 0 && this.spellCooldown <= 0) {
                    this.summonIdle = true;
                } else {
                    this.playSound(this.getHurtSound(this.damageSources().generic()), 1.0F, 1.0F);
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.is(Items.GOLD_INGOT) && this.canTrade(player)
                && this.getTarget() == null && this.isIdle() && !this.tradeActive) {
            if (!this.level().isClientSide) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.beginTrade(player);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    private boolean canTrade(Player player) {
        if (player.isCreative()) {
            return true;
        }
        if (this.getOwnerId() != null && this.getOwnerId().equals(player.getUUID())) {
            return true;
        }
        LivingEntity owner = this.getTrueOwner();
        if (owner != null && MobUtil.areAllies(owner, player)) {
            return true;
        }
        return PiglinAi.isWearingGold(player);
    }

    private void beginTrade(Player player) {
        this.tradeActive = true;
        this.tradeTick = 0;
        this.tradePlayerUUID = player.getUUID();
        this.actionTick = 0;
        this.setAnimState(ANIM_TRADE);
        this.getNavigation().stop();
        this.setTarget(null);
        this.playSound(SoundEvents.PIGLIN_ADMIRING_ITEM, 1.0F, 1.0F);
    }

    private void tickTrade() {
        if (!this.tradeActive) {
            return;
        }
        this.getNavigation().stop();
        Player player = this.getTradePlayer();
        if (player != null) {
            this.getLookControl().setLookAt(player, 30.0F, 30.0F);
        }
        ++this.tradeTick;
        if (this.tradeTick == TRADE_THROW_TICK) {
            if (player == null) {
                this.spawnAtLocation(new ItemStack(Items.GOLD_INGOT));
                this.endTrade();
                return;
            }
            this.throwTradeLoot(player);
        }
        if (this.tradeTick >= TRADE_LENGTH) {
            this.endTrade();
        }
    }

    @Nullable
    private Player getTradePlayer() {
        return this.tradePlayerUUID == null ? null : this.level().getPlayerByUUID(this.tradePlayerUUID);
    }

    private void throwTradeLoot(Player player) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(
                new ResourceLocation(GoetyOminous.MOD_ID, "gameplay/piglin_shaman_bartering"));
        List<ItemStack> loot = lootTable.getRandomItems(new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .withParameter(LootContextParams.ORIGIN, this.position())
                .create(LootContextParamSets.GIFT));
        Vec3 target = player.position().add(0.0D, 1.0D, 0.0D);
        for (ItemStack stack : loot) {
            net.minecraft.world.entity.ai.behavior.BehaviorUtils.throwItem(this, stack, target);
        }
        this.playSound(SoundEvents.PIGLIN_CELEBRATE, 1.0F, 1.0F);
    }

    private void endTrade() {
        this.tradeActive = false;
        this.tradePlayerUUID = null;
        this.tradeTick = 0;
        if (this.getAnimState() == ANIM_TRADE) {
            this.setAnimState(ANIM_IDLE);
        }
    }

    @Override
    protected boolean canHunt() {
        return false;
    }

    @Override
    public net.minecraft.world.entity.monster.piglin.PiglinArmPose getArmPose() {
        return net.minecraft.world.entity.monster.piglin.PiglinArmPose.DEFAULT;
    }

    @Override
    protected void playConvertedSound() {
    }

    @Override
    public boolean isConverting() {
        return false;
    }

    @Nullable
    @Override
    protected EntityType<? extends net.minecraft.world.entity.Mob> getConversionTarget() {
        return null;
    }

    @Nullable
    @Override
    protected EntityType<? extends net.minecraft.world.entity.Mob> getZombieVariantType() {
        return null;
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public boolean canWearArmor() {
        return false;
    }

    @Override
    public boolean canSpawnArmor() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PIGLIN_BRUTE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PIGLIN_BRUTE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PIGLIN_BRUTE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.PIGLIN_BRUTE_STEP, 0.15F, 1.0F);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.6875F;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data, tag);
        if (!this.hasCustomName()) {
            int title = this.random.nextInt(4);
            int name = this.random.nextInt(16);
            this.setCustomName(Component.translatable("title.goetyominous.shaman." + title)
                    .append(" ")
                    .append(Component.translatable("name.goetyominous.shaman." + name)));
        }
        this.setPersistenceRequired();
        return result;
    }

    private void startAction(int state) {
        this.actionTick = 0;
        this.setAnimState(state);
        this.getNavigation().stop();
    }

    private void endAction() {
        this.actionTick = 0;
        if (this.getAnimState() != ANIM_DEATH) {
            this.setAnimState(ANIM_IDLE);
        }
    }

    private void faceTarget() {
        LivingEntity target = this.getTarget();
        if (target != null) {
            this.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }
    }

    private void summonIdleServants() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        LivingEntity owner = this.getTrueOwner() != null ? this.getTrueOwner() : this;
        int count = 2 + this.random.nextInt(4);
        for (int i = 0; i < count; ++i) {
            ZPiglinServant servant;
            if (serverLevel.getDifficulty() == Difficulty.HARD && this.random.nextFloat() < 0.05F) {
                servant = new ZPiglinBruteServant(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), serverLevel);
            } else {
                servant = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), serverLevel);
            }
            BlockPos pos = BlockFinder.SummonRadius(this.blockPosition(), servant, serverLevel);
            servant.setTrueOwner(owner);
            servant.moveTo(pos, this.getYRot(), this.getXRot());
            MobUtil.moveDownToGround(servant);
            servant.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
            servant.setPersistenceRequired();
            servant.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                    MobSpawnType.MOB_SUMMONED, null, null);
            if (serverLevel.addFreshEntity(servant)) {
                SoundUtil.playNecromancerSummon(servant);
                ServerParticleUtil.summonUndeadParticles(serverLevel, servant,
                        new ColorUtil(16753408), 16753408, 16777070);
            }
        }
    }

    private int countOwnZPiglins() {
        return this.level().getEntitiesOfClass(ZPiglinServant.class,
                this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D),
                servant -> servant.isAlive() && servant.getTrueOwner() == this).size();
    }

    private void summonZPiglins() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for (int i = 0; i < 2; ++i) {
            if (this.countOwnZPiglins() >= 6) {
                break;
            }
            ZPiglinServant servant;
            if (serverLevel.getDifficulty() == Difficulty.HARD && this.random.nextFloat() < 0.05F) {
                servant = new ZPiglinBruteServant(ModEntityType.ZPIGLIN_BRUTE_SERVANT.get(), serverLevel);
            } else {
                servant = new ZPiglinServant(ModEntityType.ZPIGLIN_SERVANT.get(), serverLevel);
            }
            BlockPos pos = BlockFinder.SummonRadius(this.blockPosition(), servant, serverLevel);
            servant.setTrueOwner(this);
            servant.moveTo(pos, 0.0F, 0.0F);
            MobUtil.moveDownToGround(servant);
            servant.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
            servant.setPersistenceRequired();
            servant.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                    MobSpawnType.MOB_SUMMONED, null, null);
            if (serverLevel.addFreshEntity(servant)) {
                ServerParticleUtil.summonUndeadParticles(serverLevel, servant,
                        new ColorUtil(16753408), 16753408, 16777070);
                serverLevel.playSound(null, servant.getX(), servant.getY(), servant.getZ(),
                        SoundEvents.PIGLIN_ADMIRING_ITEM, this.getSoundSource(), 1.0F, 1.0F);
            }
        }
    }

    private void summonHoglins() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        boolean halfHealth = MobUtil.healthIsHalved(this);
        float yaw = this.yBodyRot * Mth.DEG_TO_RAD;
        Vec3 forward = new Vec3(-Mth.sin(yaw), 0.0D, Mth.cos(yaw));
        Vec3 left = new Vec3(Mth.cos(yaw), 0.0D, Mth.sin(yaw));
        Vec3 frontLeft = this.position().add(forward.scale(1.5D)).add(left.scale(1.5D));
        Vec3 frontRight = this.position().add(forward.scale(1.5D)).subtract(left.scale(1.5D));
        Vec3 backLeft = this.position().subtract(forward.scale(1.5D)).add(left.scale(1.5D));
        Vec3 backRight = this.position().subtract(forward.scale(1.5D)).subtract(left.scale(1.5D));
        Vec3[] positions = halfHealth
                ? new Vec3[]{frontLeft, frontRight, backLeft, backRight}
                : new Vec3[]{frontLeft, frontRight};
        for (Vec3 pos : positions) {
            BlockPos ground = BlockFinder.findGroundBelow(serverLevel,
                    BlockPos.containing(pos).above(3));
            BurningHoglin hoglin = new BurningHoglin(ModEntityType.BURNING_HOGLIN.get(), serverLevel);
            hoglin.setTrueOwner(this);
            hoglin.setPos(Vec3.atBottomCenterOf(ground));
            if (halfHealth) {
                hoglin.setWindUpTime(10);
            }
            hoglin.setLimitedLife(100);
            ForgeEventFactory.onFinalizeSpawn(hoglin, serverLevel,
                    serverLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            ServerParticleUtil.summonUndeadParticles(serverLevel, hoglin,
                    new ColorUtil(16753408), 16753408, 16777070);
            LivingEntity target = this.getTarget();
            if (target != null) {
                hoglin.setTarget(target);
            }
            serverLevel.addFreshEntity(hoglin);
        }
        this.playSound(ModSounds.RUMBLE.get(), 1.0F, 1.0F);
    }

    private void fireFelBolts() {
        LivingEntity target = this.getTarget();
        if (target != null) {
            this.setLastHurtMob(target);
        }
        Vec3 view = this.getViewVector(1.0F);
        for (int i = -1; i <= 1; ++i) {
            FelBolt bolt = new FelBolt(this, view.x + (double) ((float) i / 10.0F), view.y,
                    view.z + (double) ((float) i / 10.0F), this.level());
            bolt.setPos(this.getX() + view.x / 2.0D, this.getEyeY() - 0.2D,
                    this.getZ() + view.z / 2.0D);
            bolt.setExtraDamage(0.0F);
            bolt.rotateToMatchMovement();
            this.level().addFreshEntity(bolt);
        }
        SoundUtil.playSoulBolt(this);
        this.swing(InteractionHand.MAIN_HAND);
    }

    private int getPotionCooldown() {
        float ratio = this.getHealth() / this.getMaxHealth();
        return POTION_COOLDOWN_MIN + Math.round((POTION_COOLDOWN - POTION_COOLDOWN_MIN) * ratio);
    }

    private void spawnBlastTrap() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }
        this.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                ModSounds.TOWER_WRAITH_ACID.get(), this.getSoundSource(), 2.0F, 0.5F);
        EffectBlastTrap trap = new EffectBlastTrap(this.level(), target.position());
        trap.setOwner(com.qiuyue.goetyominous.utils.ServantAllyUtil.getSummonOwner(this));
        trap.setAreaOfEffect(1.5F);
        trap.playSound = false;
        int amp = MobUtil.healthIsHalved(this) ? 1 : 0;
        int roll = this.random.nextInt(4);
        if (roll == 0) {
            trap.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1800, amp));
        } else if (roll == 1) {
            trap.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1800, amp));
        } else if (roll == 2) {
            trap.addEffect(new MobEffectInstance(GoetyEffects.ACID_VENOM.get(), 900, amp));
        } else {
            trap.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), 1800, 1 + amp));
        }
        MobUtil.moveDownToGround(trap);
        this.level().addFreshEntity(trap);
    }

    private void throwPotion() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }
        BrewSelection selection = this.selectThrownPotion(target);
        if (selection.isEmpty()) {
            return;
        }
        ItemStack brew = new ItemStack(ModItems.SPLASH_BREW.get());
        BrewUtils.setCustomEffects(brew, selection.mobEffects, selection.brewEffects);
        BrewUtils.setAreaOfEffect(brew, selection.area);
        brew.getOrCreateTag().putInt("CustomPotionColor",
                BrewUtils.getColor(selection.mobEffects, selection.brewEffects));
        PiglinShamanBrew thrown = new PiglinShamanBrew(this.level(), this);
        thrown.setItem(brew);
        Vec3 delta = target.getDeltaMovement();
        double d0 = target.getX() + delta.x - this.getX();
        double d1 = target.getEyeY() - 1.1D - this.getY();
        double d2 = target.getZ() + delta.z - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        float velocity = 0.75F;
        if (target.distanceTo(this) >= 4.0F) {
            thrown.setXRot(thrown.getXRot() + 20.0F);
        } else {
            velocity = 1.0F;
        }
        thrown.shoot(d0, d1 + d3 * 0.2D, d2, velocity, 8.0F);
        this.level().addFreshEntity(thrown);
        this.playSound(SoundEvents.WITCH_THROW, 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
    }

    private void meleeAttack() {
        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + 4.0F;
        DamageSource damageSource = this.damageSources().mobAttack(this);
        this.areaAttack(4.0F, 0.25F, 90.0F, damage, damageSource);
        LivingEntity target = this.getTarget();
        if (target != null) {
            this.setLastHurtMob(target);
            if (this.isWithinMeleeAttackRange(target)) {
                target.hurt(damageSource, damage);
            }
        }
        this.playSound(SoundEvents.PIGLIN_BRUTE_ANGRY, 1.0F, this.getVoicePitch());
    }

    private void areaAttack(float range, float height, float arc, float damage, DamageSource damageSource) {
        if (this.level().isClientSide) {
            return;
        }
        for (LivingEntity hit : MobUtil.getEntityLivingBaseNearby(this, range, height, range, range)) {
            float relativeAngle = MobUtil.getRelativeAngle(this, hit);
            double dx = hit.getX() - this.getX();
            double dz = hit.getZ() - this.getZ();
            float distance = (float) Math.sqrt(dx * dx + dz * dz);
            if ((distance <= range && relativeAngle <= arc / 2.0F && relativeAngle >= -arc / 2.0F
                    || relativeAngle >= 360.0F - arc / 2.0F || relativeAngle <= -360.0F + arc / 2.0F)
                    && !ServantAllyUtil.areAllied(this, hit)) {
                if (hit.hurt(damageSource, damage)) {
                    double d0 = hit.getX() - this.getX();
                    double d1 = hit.getZ() - this.getZ();
                    double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
                    hit.push(d0 / d2 * 2.5D, 0.18D, d1 / d2 * 2.2D);
                }
            }
        }
    }

    private BrewSelection selectDrinkPotion() {
        int amp = 0;
        if (this.random.nextFloat() <= 0.05F && this.level().getDifficulty() == Difficulty.NORMAL) {
            amp = 2;
        } else if (this.random.nextFloat() <= 0.25F) {
            amp = 1;
        }
        BrewSelection selection = new BrewSelection();
        if (!(this.random.nextFloat() < 0.15F) || !this.isInWall()
                && (this.getLastDamageSource() == null || !this.getLastDamageSource().is(DamageTypes.IN_WALL))) {
            if (!(this.random.nextFloat() < 0.15F) || this.getLastDamageSource() == null
                    || !this.getLastDamageSource().is(DamageTypes.CACTUS)
                    && !this.getLastDamageSource().is(DamageTypes.SWEET_BERRY_BUSH)) {
                if (!(this.random.nextFloat() < 0.15F) || !(this.getHealth() < this.getMaxHealth())
                        || this.getTarget() != null && this.lastHitTime != 0) {
                    if (this.random.nextFloat() < 0.15F
                            && this.isEyeInFluidType(ForgeMod.WATER_TYPE.get())
                            && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                        selection.mobEffects.add(new MobEffectInstance(MobEffects.WATER_BREATHING, 3600));
                        selection.mobEffects.add(new MobEffectInstance(GoetyEffects.SWIFT_SWIM.get(), 3600));
                    } else if (this.random.nextFloat() < 0.15F
                            && (this.isOnFire() || this.getLastDamageSource() != null
                            && this.getLastDamageSource().is(DamageTypeTags.IS_FIRE))
                            && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                        selection.mobEffects.add(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600));
                    } else if (this.random.nextFloat() < 0.15F
                            && this.getLastDamageSource() != null
                            && this.getLastDamageSource().is(DamageTypeTags.IS_FALL)
                            && !this.hasEffect(MobEffects.SLOW_FALLING)) {
                        selection.mobEffects.add(new MobEffectInstance(MobEffects.SLOW_FALLING, 3600));
                    } else if (this.random.nextFloat() < 0.15F
                            && this.getLastDamageSource() != null
                            && ModDamageSource.physicalAttacks(this.getLastDamageSource())
                            && !this.hasEffect(GoetyEffects.REPULSIVE.get())) {
                        selection.mobEffects.add(new MobEffectInstance(GoetyEffects.REPULSIVE.get(),
                                1800 / (amp + 1), amp));
                    } else if (this.random.nextFloat() < 0.15F && this.getTarget() != null) {
                        if (this.random.nextFloat() <= 0.15F
                                && this.getTarget().distanceTo(this) <= 4.0F) {
                            selection.brewEffects.add(new BrewEffectInstance(new BlindJumpBrewEffect(0), 1, amp));
                        } else if (this.random.nextFloat() <= 0.15F
                                && !this.hasEffect(MobEffects.REGENERATION)) {
                            selection.mobEffects.add(new MobEffectInstance(MobEffects.REGENERATION,
                                    900 / (amp + 1), amp));
                            if (this.random.nextFloat() < 0.25F && MobUtil.isInSunlight(this)
                                    && !this.hasEffect(GoetyEffects.PHOTOSYNTHESIS.get())) {
                                selection.mobEffects.add(new MobEffectInstance(
                                        GoetyEffects.PHOTOSYNTHESIS.get(), 1800));
                            }
                        } else if (this.random.nextFloat() < 0.05F
                                && !this.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
                            selection.mobEffects.add(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1800));
                        } else if (this.random.nextFloat() < 0.05F
                                && !this.hasEffect(GoetyEffects.FROSTY_AURA.get())
                                && !this.hasEffect(GoetyEffects.FIERY_AURA.get())) {
                            if (this.random.nextBoolean()) {
                                if (!this.getTarget().hasEffect(GoetyEffects.FREEZING.get())
                                        && !this.getTarget().hasEffect(MobEffects.FIRE_RESISTANCE)
                                        && !this.getTarget().fireImmune()) {
                                    selection.mobEffects.add(new MobEffectInstance(
                                            GoetyEffects.FIERY_AURA.get(), 1800 / (amp + 1), amp));
                                }
                            } else if (!this.getTarget().isOnFire() && this.getTarget().canFreeze()) {
                                selection.mobEffects.add(new MobEffectInstance(
                                        GoetyEffects.FROSTY_AURA.get(), 1800 / (amp + 1), amp));
                            }
                        }
                    } else if (this.random.nextFloat() <= 0.15F && MobUtil.isInWeb(this)
                            && !this.hasEffect(GoetyEffects.CLIMBING.get())) {
                        selection.mobEffects.add(new MobEffectInstance(GoetyEffects.CLIMBING.get(), 3600));
                    } else if (this.random.nextFloat() < 0.05F && MobUtil.hasLongNegativeEffects(this)) {
                        selection.brewEffects.add(new BrewEffectInstance(new PurifyBrewEffect(
                                "purify_debuff", 0, 0, MobEffectCategory.BENEFICIAL, 3692632, true)));
                    }
                } else {
                    selection.mobEffects.add(new MobEffectInstance(MobEffects.HEAL, 1, amp));
                }
            } else {
                selection.brewEffects.add(new BrewEffectInstance(new HarvestBlockEffect()));
            }
        } else {
            selection.brewEffects.add(new BrewEffectInstance(new BlindJumpBrewEffect(0), 1, amp));
        }
        selection.area = this.random.nextInt(amp + 1);
        return selection;
    }

    private BrewSelection selectThrownPotion(LivingEntity target) {
        int amp = 0;
        if (this.random.nextFloat() <= 0.05F && this.level().getDifficulty() != Difficulty.EASY) {
            amp = 2;
        } else if (this.random.nextFloat() <= 0.25F) {
            amp = 1;
        }
        BrewSelection selection = new BrewSelection();
        boolean grief = ForgeEventFactory.getMobGriefingEvent(this.level(), this);
        Vec3 vec3 = target.getDeltaMovement();
        double d0 = target.getX() + vec3.x - this.getX();
        double d2 = target.getZ() + vec3.z - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        if (d3 >= 8.0D && !target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
            selection.mobEffects.add(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1800 / (amp + 1), amp));
            if (this.random.nextFloat() <= 0.25F && this.noBrewMinions(target) && grief) {
                selection.brewEffects.add(new BrewEffectInstance(new WebbedBrewEffect(0, 0), 1, amp));
            } else if (this.random.nextFloat() <= 0.5F && this.random.nextBoolean()) {
                selection.brewEffects.add(new BrewEffectInstance(new TransposeBrewEffect()));
            }
        } else if (target.getHealth() >= 8.0F && !target.hasEffect(MobEffects.POISON)
                && target.canBeAffected(new MobEffectInstance(MobEffects.POISON))) {
            if (!target.hasEffect(MobEffects.POISON)
                    && target.canBeAffected(new MobEffectInstance(MobEffects.POISON))) {
                selection.mobEffects.add(new MobEffectInstance(MobEffects.POISON, 900 / (amp + 1), amp));
            }
            if (this.random.nextFloat() <= 0.25F && !this.hasEffect(GoetyEffects.FIERY_AURA.get())
                    && !target.hasEffect(GoetyEffects.FREEZING.get())
                    && !target.getType().is(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)) {
                selection.mobEffects.add(new MobEffectInstance(GoetyEffects.FREEZING.get(),
                        900 / (amp + 1), amp));
            } else if (this.random.nextFloat() <= 0.25F
                    && !this.hasEffect(GoetyEffects.FROSTY_AURA.get())
                    && !target.hasEffect(GoetyEffects.FLAMMABLE.get())
                    && !target.fireImmune()) {
                selection.mobEffects.add(new MobEffectInstance(GoetyEffects.FLAMMABLE.get(),
                        900 / (amp + 1), amp));
            } else if (this.random.nextFloat() <= 0.05F
                    && !target.hasEffect(GoetyEffects.TRIPPING.get())) {
                selection.mobEffects.add(new MobEffectInstance(GoetyEffects.TRIPPING.get(),
                        1800 / (amp + 1), amp));
            }
        } else if (target.hasEffect(MobEffects.REGENERATION)
                && !target.hasEffect(GoetyEffects.CURSED.get())) {
            selection.mobEffects.add(new MobEffectInstance(GoetyEffects.CURSED.get(), 600, 0));
        } else if (this.getLastDamageSource() != null
                && ModDamageSource.physicalAttacks(this.getLastDamageSource())
                && !target.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
            selection.mobEffects.add(new MobEffectInstance(MobEffects.WEAKNESS, 1800 / (amp + 1), amp));
            if (this.random.nextFloat() <= 0.25F) {
                selection.brewEffects.add(new BrewEffectInstance(new TransposeBrewEffect(), 1, amp));
            } else if (this.random.nextFloat() <= 0.5F && !target.fireImmune()
                    && MobUtil.isInSunlight(target) && !target.hasEffect(MobEffects.FIRE_RESISTANCE)
                    && !target.hasEffect(GoetyEffects.SUN_ALLERGY.get())) {
                selection.mobEffects.add(new MobEffectInstance(GoetyEffects.SUN_ALLERGY.get(),
                        3600 / (amp + 1), amp));
            } else if (this.random.nextFloat() <= 0.75F
                    && target.level().getMaxLocalRawBrightness(target.blockPosition()) < 2
                    && !target.hasEffect(GoetyEffects.NYCTOPHOBIA.get())) {
                selection.mobEffects.add(new MobEffectInstance(GoetyEffects.NYCTOPHOBIA.get(),
                        1800 / (amp + 1), amp));
            } else if (!target.hasEffect(GoetyEffects.SAPPED.get())) {
                selection.mobEffects.add(new MobEffectInstance(GoetyEffects.SAPPED.get(),
                        1800 / (amp + 1), amp));
            }
        } else if (this.random.nextFloat() <= 0.05F) {
            selection.brewEffects.add(new BrewEffectInstance(new StripBrewEffect(0, 0)));
        } else if (!(this.random.nextFloat() <= 0.25F)
                || !target.onGround() && (target.hasEffect(MobEffects.LEVITATION)
                || target.hasEffect(GoetyEffects.PLUNGE.get()))) {
            if (this.random.nextFloat() <= 0.55F) {
                selection.brewEffects.add(new BrewEffectInstance(new LaunchBrewEffect(), 1));
            } else {
                selection.mobEffects.add(new MobEffectInstance(MobEffects.HARM, 1, amp));
            }
        } else if (!target.hasEffect(MobEffects.LEVITATION)
                && !target.hasEffect(GoetyEffects.PLUNGE.get())) {
            selection.mobEffects.add(new MobEffectInstance(GoetyEffects.PLUNGE.get(),
                    900 / (amp + 1), amp));
        }
        selection.area = this.random.nextInt(amp + 1);
        return selection;
    }

    private boolean noBrewMinions(LivingEntity target) {
        return this.level().getEntitiesOfClass(VampireBat.class, target.getBoundingBox().inflate(2.0D)).isEmpty()
                && this.level().getEntitiesOfClass(net.minecraft.world.entity.animal.Bee.class,
                target.getBoundingBox().inflate(2.0D)).isEmpty();
    }

    private class TradeGuardGoal extends Goal {
        public TradeGuardGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public boolean canUse() {
            return AllyPiglinShaman.this.tradeActive;
        }

        @Override
        public boolean canContinueToUse() {
            return AllyPiglinShaman.this.tradeActive && !AllyPiglinShaman.this.isDeadOrDying();
        }

        @Override
        public void start() {
            AllyPiglinShaman.this.getNavigation().stop();
        }

        @Override
        public void tick() {
            AllyPiglinShaman.this.getNavigation().stop();
            Player player = AllyPiglinShaman.this.getTradePlayer();
            if (player != null) {
                AllyPiglinShaman.this.getLookControl().setLookAt(player, 30.0F, 30.0F);
            }
        }
    }

    private abstract class ShamanActionGoal extends Goal {
        protected ShamanActionGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        protected abstract int getActionLength();

        protected abstract int getActionState();

        @Override
        public boolean canContinueToUse() {
            return AllyPiglinShaman.this.actionTick < this.getActionLength()
                    && !AllyPiglinShaman.this.isDeadOrDying();
        }

        @Override
        public void start() {
            AllyPiglinShaman.this.startAction(this.getActionState());
        }

        @Override
        public void stop() {
            AllyPiglinShaman.this.endAction();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            ++AllyPiglinShaman.this.actionTick;
            AllyPiglinShaman.this.faceTarget();
        }
    }

    private class SummonZPiglinGoal extends ShamanActionGoal {
        @Override
        protected int getActionLength() {
            return SPELL_LENGTH;
        }

        @Override
        protected int getActionState() {
            return ANIM_SPELL;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = AllyPiglinShaman.this.getTarget();
            return AllyPiglinShaman.this.isIdle() && AllyPiglinShaman.this.spellCooldown <= 0
                    && AllyPiglinShaman.this.countOwnZPiglins() < 6
                    && target != null && target.isAlive() && AllyPiglinShaman.this.hasLineOfSight(target);
        }

        @Override
        public void start() {
            super.start();
            AllyPiglinShaman.this.playSound(ModSounds.PREPARE_SUMMON.get(), 0.85F, 0.9F);
        }

        @Override
        public void stop() {
            super.stop();
            AllyPiglinShaman.this.spellCooldown = SPELL_COOLDOWN;
        }

        @Override
        public void tick() {
            super.tick();
            if (AllyPiglinShaman.this.actionTick == SPELL_CAST_TICK) {
                AllyPiglinShaman.this.summonZPiglins();
            }
        }
    }

    private class SummonHoglinGoal extends ShamanActionGoal {
        @Override
        protected int getActionLength() {
            return SUMMON_LENGTH;
        }

        @Override
        protected int getActionState() {
            return ANIM_SUMMON;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = AllyPiglinShaman.this.getTarget();
            return AllyPiglinShaman.this.isIdle() && AllyPiglinShaman.this.summonCooldown <= 0
                    && target != null && target.isAlive() && AllyPiglinShaman.this.hasLineOfSight(target);
        }

        @Override
        public void stop() {
            super.stop();
            AllyPiglinShaman.this.summonCooldown = MobUtil.healthIsHalved(AllyPiglinShaman.this)
                    ? SUMMON_COOLDOWN_HALF : SUMMON_COOLDOWN_FULL;
        }

        @Override
        public void tick() {
            super.tick();
            if (AllyPiglinShaman.this.actionTick == SUMMON_SPAWN_TICK) {
                AllyPiglinShaman.this.summonHoglins();
            }
        }
    }

    private class ConvertPiglinGoal extends ShamanActionGoal {
        @Nullable
        private LivingEntity convertTarget;

        @Override
        protected int getActionLength() {
            return CONVERT_LENGTH;
        }

        @Override
        protected int getActionState() {
            return ANIM_CONVERT;
        }

        @Override
        public boolean isInterruptable() {
            return true;
        }

        @Override
        public boolean canUse() {
            if (AllyPiglinShaman.this.piglinConvertCooldown > 0
                    || !AllyPiglinShaman.this.isIdle()
                    || !(AllyPiglinShaman.this.getTrueOwner() instanceof Player)) {
                return false;
            }
            LivingEntity target = AllyPiglinShaman.this.getCommandPosEntity();
            if (target == null || !PiglinConversionManager.isConvertible(target)
                    || PiglinConversionManager.isConverting(target)
                    || !AllyPiglinShaman.this.canAcceptConvertCommand(target)) {
                return false;
            }
            if (AllyPiglinShaman.this.distanceToSqr(target) > CONVERT_RANGE * CONVERT_RANGE
                    || !AllyPiglinShaman.this.hasLineOfSight(target)) {
                return false;
            }
            this.convertTarget = target;
            return true;
        }

        @Override
        public void start() {
            AllyPiglinShaman.this.setCommandPos(null, true);
            PiglinConversionManager.start(this.convertTarget, AllyPiglinShaman.this);
            super.start();
        }

        @Override
        public void tick() {
            ++AllyPiglinShaman.this.actionTick;
            if (this.convertTarget != null) {
                AllyPiglinShaman.this.getLookControl().setLookAt(this.convertTarget, 30.0F, 30.0F);
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.convertTarget = null;
        }
    }

    private class BlastTrapGoal extends ShamanActionGoal {
        @Override
        protected int getActionLength() {
            return SPELL_LENGTH;
        }

        @Override
        protected int getActionState() {
            return ANIM_SPELL;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = AllyPiglinShaman.this.getTarget();
            return AllyPiglinShaman.this.isIdle() && AllyPiglinShaman.this.blastCooldown <= 0
                    && target != null && target.isAlive() && AllyPiglinShaman.this.hasLineOfSight(target);
        }

        @Override
        public void stop() {
            super.stop();
            AllyPiglinShaman.this.blastCooldown = BLAST_TRAP_COOLDOWN;
        }

        @Override
        public void tick() {
            super.tick();
            if (AllyPiglinShaman.this.actionTick == SPELL_CAST_TICK) {
                AllyPiglinShaman.this.spawnBlastTrap();
            }
        }
    }

    private class ThrowPotionGoal extends ShamanActionGoal {
        @Override
        protected int getActionLength() {
            return POTION_LENGTH;
        }

        @Override
        protected int getActionState() {
            return ANIM_POTION;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = AllyPiglinShaman.this.getTarget();
            return AllyPiglinShaman.this.isIdle() && AllyPiglinShaman.this.potionCooldown <= 0
                    && target != null && target.isAlive()
                    && AllyPiglinShaman.this.hasLineOfSight(target)
                    && AllyPiglinShaman.this.distanceToSqr(target) <= 100.0D;
        }

        @Override
        public void stop() {
            super.stop();
            AllyPiglinShaman.this.potionCooldown = AllyPiglinShaman.this.getPotionCooldown();
        }

        @Override
        public void tick() {
            super.tick();
            if (AllyPiglinShaman.this.actionTick == POTION_THROW_TICK) {
                AllyPiglinShaman.this.throwPotion();
            }
        }
    }

    private class HealAllyGoal extends ShamanActionGoal {
        @Nullable
        private LivingEntity healTarget;

        @Override
        protected int getActionLength() {
            return POTION_LENGTH;
        }

        @Override
        protected int getActionState() {
            return ANIM_POTION;
        }

        @Override
        public boolean canUse() {
            if (!AllyPiglinShaman.this.isIdle() || AllyPiglinShaman.this.healAllyCooldown > 0) {
                return false;
            }
            this.healTarget = this.findHealTarget();
            return this.healTarget != null;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.healTarget != null && this.healTarget.isAlive();
        }

        @Override
        public void start() {
            super.start();
            if (this.healTarget == null || !this.healTarget.isAlive()) {
                this.healTarget = this.findHealTarget();
            }
        }

        @Override
        public void tick() {
            ++AllyPiglinShaman.this.actionTick;
            if (this.healTarget != null && this.healTarget.isAlive()) {
                AllyPiglinShaman.this.getLookControl().setLookAt(this.healTarget, 30.0F, 30.0F);
            }
            if (AllyPiglinShaman.this.actionTick == POTION_THROW_TICK) {
                this.throwHealBrew();
            }
        }

        @Override
        public void stop() {
            super.stop();
            AllyPiglinShaman.this.healAllyCooldown = HEAL_ALLY_COOLDOWN;
            this.healTarget = null;
        }

        @Nullable
        private LivingEntity findHealTarget() {
            List<LivingEntity> allies = AllyPiglinShaman.this.level().getEntitiesOfClass(LivingEntity.class,
                    AllyPiglinShaman.this.getBoundingBox().inflate(64.0D),
                    entity -> entity != AllyPiglinShaman.this
                            && entity instanceof IOwned
                            && entity.isAlive()
                            && ServantAllyUtil.areAllied(AllyPiglinShaman.this, entity)
                            && entity.getType() != ModEntityType.VEX_SERVANT.get()
                            && entity.getType() != ModEntityType.IRK_SERVANT.get());
            for (LivingEntity ally : allies) {
                if (this.hasSevereNegativeEffects(ally)) {
                    return ally;
                }
            }
            for (LivingEntity ally : allies) {
                if (ally.isOnFire()) {
                    return ally;
                }
            }
            for (LivingEntity ally : allies) {
                if (this.isInCombat(ally)) {
                    return ally;
                }
            }
            for (LivingEntity ally : allies) {
                if (this.needsHealing(ally)) {
                    return ally;
                }
            }
            return null;
        }

        private boolean hasSevereNegativeEffects(LivingEntity target) {
            int negativeEffectCount = 0;
            int maxAmplifier = 0;
            for (MobEffectInstance instance : target.getActiveEffects()) {
                if (instance.getEffect().getCategory() != MobEffectCategory.HARMFUL) {
                    continue;
                }
                if (instance.getDuration() > 400) {
                    return true;
                }
                if (instance.getAmplifier() > 1) {
                    return true;
                }
                ++negativeEffectCount;
                maxAmplifier = Math.max(maxAmplifier, instance.getAmplifier());
            }
            return negativeEffectCount > 2 || maxAmplifier > 1;
        }

        private boolean isInCombat(LivingEntity target) {
            return target instanceof Mob mob && mob.getTarget() != null;
        }

        private boolean needsHealing(LivingEntity target) {
            return target.getHealth() < target.getMaxHealth() * 0.8F;
        }

        private boolean isUndead(LivingEntity target) {
            return target.getMobType() == MobType.UNDEAD;
        }

        private int getRandomAmplifier() {
            double roll = AllyPiglinShaman.this.random.nextDouble();
            if (roll < 0.5D) {
                return 0;
            }
            if (roll < 0.8D) {
                return 1;
            }
            return 2;
        }

        private ItemStack createSupportBrew(LivingEntity target) {
            List<MobEffectInstance> mobEffects = new ArrayList<>();
            List<BrewEffectInstance> brewEffects = new ArrayList<>();
            if (this.hasSevereNegativeEffects(target)) {
                brewEffects.add(new BrewEffectInstance(new PurifyBrewEffect(
                        "purify_debuff", 0, 0, MobEffectCategory.BENEFICIAL, 3692632, true)));
            } else if (target.isOnFire()) {
                mobEffects.add(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000));
            } else if (this.isInCombat(target)) {
                List<MobEffect> combatEffects = List.of(
                        MobEffects.DAMAGE_BOOST,
                        MobEffects.DAMAGE_RESISTANCE,
                        MobEffects.MOVEMENT_SPEED,
                        MobEffects.REGENERATION,
                        MobEffects.ABSORPTION,
                        GoetyEffects.DEFLECTIVE.get(),
                        GoetyEffects.VENOMOUS_HANDS.get(),
                        GoetyEffects.RADIANCE.get(),
                        GoetyEffects.RALLYING.get(),
                        GoetyEffects.SHIELDING.get(),
                        GoetyEffects.PHOTOSYNTHESIS.get(),
                        GoetyEffects.FLAME_HANDS.get(),
                        GoetyEffects.CLIMBING.get(),
                        GoetyEffects.SWIRLING.get(),
                        GoetyEffects.LEECHING.get());
                MobEffect effect = combatEffects.get(AllyPiglinShaman.this.random.nextInt(combatEffects.size()));
                int duration = 600 + AllyPiglinShaman.this.random.nextInt(900);
                int amplifier = AllyPiglinShaman.this.random.nextInt(3);
                mobEffects.add(new MobEffectInstance(effect, duration, amplifier));
            } else if (this.needsHealing(target)) {
                if (this.isUndead(target)) {
                    mobEffects.add(new MobEffectInstance(MobEffects.HARM, 1, this.getRandomAmplifier()));
                } else if (AllyPiglinShaman.this.random.nextBoolean()) {
                    mobEffects.add(new MobEffectInstance(MobEffects.HEAL, 1, this.getRandomAmplifier()));
                } else {
                    int duration = 600 + AllyPiglinShaman.this.random.nextInt(1200);
                    mobEffects.add(new MobEffectInstance(MobEffects.REGENERATION, duration,
                            this.getRandomAmplifier()));
                }
            }
            if (mobEffects.isEmpty() && brewEffects.isEmpty()) {
                return ItemStack.EMPTY;
            }
            ItemStack brew = new ItemStack(ModItems.SPLASH_BREW.get());
            BrewUtils.setCustomEffects(brew, mobEffects, brewEffects);
            BrewUtils.setAreaOfEffect(brew, AllyPiglinShaman.this.random.nextInt(3));
            brew.getOrCreateTag().putInt("CustomPotionColor", BrewUtils.getColor(mobEffects, brewEffects));
            return brew;
        }

        private void throwHealBrew() {
            LivingEntity target = this.healTarget;
            if (target == null || !target.isAlive()) {
                return;
            }
            ItemStack brew = this.createSupportBrew(target);
            if (brew.isEmpty()) {
                return;
            }
            ShamanHealBrew thrown = new ShamanHealBrew(AllyPiglinShaman.this.level(), AllyPiglinShaman.this);
            thrown.setItem(brew);
            Vec3 delta = target.getDeltaMovement();
            double d0 = target.getX() + delta.x - AllyPiglinShaman.this.getX();
            double d1 = target.getEyeY() - 1.1D - AllyPiglinShaman.this.getY();
            double d2 = target.getZ() + delta.z - AllyPiglinShaman.this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            float velocity = 0.75F;
            if (target.distanceTo(AllyPiglinShaman.this) >= 4.0F) {
                thrown.setXRot(thrown.getXRot() + 20.0F);
            } else {
                velocity = 1.0F;
            }
            thrown.shoot(d0, d1 + d3 * 0.2D, d2, velocity, 8.0F);
            AllyPiglinShaman.this.level().addFreshEntity(thrown);
            AllyPiglinShaman.this.playSound(SoundEvents.WITCH_THROW, 1.0F,
                    0.8F + AllyPiglinShaman.this.random.nextFloat() * 0.4F);
        }
    }

    private class MeleeGoal extends ShamanActionGoal {
        @Override
        protected int getActionLength() {
            return MELEE_LENGTH;
        }

        @Override
        protected int getActionState() {
            return ANIM_MELEE;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = AllyPiglinShaman.this.getTarget();
            return AllyPiglinShaman.this.isIdle() && AllyPiglinShaman.this.meleeCooldown <= 0
                    && target != null && target.isAlive()
                    && AllyPiglinShaman.this.isWithinMeleeAttackRange(target);
        }

        @Override
        public void stop() {
            super.stop();
            AllyPiglinShaman.this.meleeCooldown = MELEE_COOLDOWN_AFTER;
        }

        @Override
        public void tick() {
            super.tick();
            if (AllyPiglinShaman.this.actionTick == MELEE_HIT_TICK) {
                AllyPiglinShaman.this.meleeAttack();
            }
        }
    }

    private class FelBoltRangedGoal extends Goal {
        private int attackTime = -1;
        private int rangedTicks;
        private boolean interruptible;

        public FelBoltRangedGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = AllyPiglinShaman.this.getTarget();
            return target != null && target.isAlive() && AllyPiglinShaman.this.isIdle();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = AllyPiglinShaman.this.getTarget();
            return target != null && target.isAlive() && AllyPiglinShaman.this.isIdle();
        }

        @Override
        public boolean isInterruptable() {
            return this.interruptible;
        }

        @Override
        public void start() {
            this.attackTime = -1;
            this.rangedTicks = 0;
            this.interruptible = false;
        }

        @Override
        public void stop() {
            this.attackTime = -1;
            this.rangedTicks = 0;
            this.interruptible = false;
            if (AllyPiglinShaman.this.getAnimState() == ANIM_RANGED) {
                AllyPiglinShaman.this.setAnimState(ANIM_IDLE);
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = AllyPiglinShaman.this.getTarget();
            if (target == null) {
                return;
            }
            double distanceSqr = AllyPiglinShaman.this.distanceToSqr(target);
            boolean sight = AllyPiglinShaman.this.hasLineOfSight(target);
            if (distanceSqr <= RANGED_ATTACK_RADIUS_SQR && sight) {
                AllyPiglinShaman.this.getNavigation().stop();
            } else {
                AllyPiglinShaman.this.getNavigation().moveTo(target, 1.0D);
            }
            AllyPiglinShaman.this.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (AllyPiglinShaman.this.getAnimState() != ANIM_RANGED) {
                if (!sight) {
                    return;
                }
                AllyPiglinShaman.this.setAnimState(ANIM_RANGED);
                this.attackTime = 12;
                this.rangedTicks = 0;
                this.interruptible = false;
                return;
            }

            ++this.rangedTicks;
            if (this.attackTime > 0) {
                --this.attackTime;
            }
            if (this.attackTime == 0) {
                if (sight) {
                    AllyPiglinShaman.this.fireFelBolts();
                }
                this.attackTime = 20;
            }
            if (this.rangedTicks >= 20) {
                this.rangedTicks = 0;
                this.attackTime = 12;
                this.interruptible = false;
            } else {
                this.interruptible = this.rangedTicks == 19;
            }
        }
    }

    private class DrinkGoal extends Goal {
        public DrinkGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public boolean canUse() {
            if (!AllyPiglinShaman.this.isIdle()) {
                return false;
            }
            if (AllyPiglinShaman.this.pendingEmergency) {
                if (!AllyPiglinShaman.this.shouldConsiderDrinking()) {
                    AllyPiglinShaman.this.pendingEmergency = false;
                } else {
                    AllyPiglinShaman.this.pendingSelection = AllyPiglinShaman.this.selectDrinkPotion();
                    if (!AllyPiglinShaman.this.pendingSelection.isEmpty()) {
                        AllyPiglinShaman.this.healingDrink = false;
                        return true;
                    }
                }
            }
            if (AllyPiglinShaman.this.pendingHeal) {
                if (AllyPiglinShaman.this.getHealth() >= AllyPiglinShaman.this.getMaxHealth()
                        || AllyPiglinShaman.this.getTarget() != null) {
                    AllyPiglinShaman.this.pendingHeal = false;
                    return false;
                }
                AllyPiglinShaman.this.healingDrink = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return AllyPiglinShaman.this.actionTick < DRINK_LENGTH
                    && !AllyPiglinShaman.this.isDeadOrDying()
                    && !(AllyPiglinShaman.this.healingDrink && AllyPiglinShaman.this.getTarget() != null);
        }

        @Override
        public void start() {
            AllyPiglinShaman.this.pendingHeal = false;
            AllyPiglinShaman.this.pendingEmergency = false;
            AllyPiglinShaman.this.startAction(ANIM_DRINK);
            AllyPiglinShaman.this.playSound(SoundEvents.GENERIC_DRINK, 1.0F,
                    0.8F + AllyPiglinShaman.this.random.nextFloat() * 0.4F);
            if (AllyPiglinShaman.this.healingDrink) {
                AllyPiglinShaman.this.healCooldown = HEAL_COOLDOWN;
            } else {
                AllyPiglinShaman.this.drinkMobEffects.clear();
                AllyPiglinShaman.this.drinkMobEffects.addAll(AllyPiglinShaman.this.pendingSelection.mobEffects);
                AllyPiglinShaman.this.drinkBrewEffects.clear();
                AllyPiglinShaman.this.drinkBrewEffects.addAll(AllyPiglinShaman.this.pendingSelection.brewEffects);
                AllyPiglinShaman.this.drinkArea = AllyPiglinShaman.this.pendingSelection.area;
                AllyPiglinShaman.this.pendingSelection = null;
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            ++AllyPiglinShaman.this.actionTick;
            AllyPiglinShaman.this.faceTarget();
            if (AllyPiglinShaman.this.actionTick == DRINK_EFFECT_TICK) {
                if (AllyPiglinShaman.this.healingDrink) {
                    if (AllyPiglinShaman.this.getTarget() == null
                            && AllyPiglinShaman.this.getHealth() < AllyPiglinShaman.this.getMaxHealth()) {
                        AllyPiglinShaman.this.heal(30.0F);
                    }
                } else {
                    for (MobEffectInstance instance : AllyPiglinShaman.this.drinkMobEffects) {
                        AllyPiglinShaman.this.addEffect(new MobEffectInstance(instance));
                    }
                    for (BrewEffectInstance instance : AllyPiglinShaman.this.drinkBrewEffects) {
                        instance.getEffect().drinkBlockEffect(AllyPiglinShaman.this, AllyPiglinShaman.this,
                                AllyPiglinShaman.this, instance.getAmplifier(), AllyPiglinShaman.this.drinkArea);
                    }
                }
            }
        }

        @Override
        public void stop() {
            AllyPiglinShaman.this.endAction();
            AllyPiglinShaman.this.healingDrink = false;
            AllyPiglinShaman.this.drinkMobEffects.clear();
            AllyPiglinShaman.this.drinkBrewEffects.clear();
        }
    }

    private class ApproachGoal extends Goal {
        private int recalc;

        public ApproachGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return AllyPiglinShaman.this.getTarget() != null && AllyPiglinShaman.this.isIdle();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = AllyPiglinShaman.this.getTarget();
            return target != null && target.isAlive() && AllyPiglinShaman.this.isIdle();
        }

        @Override
        public void tick() {
            LivingEntity target = AllyPiglinShaman.this.getTarget();
            if (target == null) {
                return;
            }
            AllyPiglinShaman.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (--this.recalc <= 0) {
                this.recalc = 10;
                if (AllyPiglinShaman.this.distanceToSqr(target) > 4.0D) {
                    AllyPiglinShaman.this.getNavigation().moveTo(target, 1.0D);
                } else {
                    AllyPiglinShaman.this.getNavigation().stop();
                }
            }
        }
    }

    private class IdleSummonGoal extends ShamanActionGoal {
        @Override
        protected int getActionLength() {
            return SPELL_LENGTH;
        }

        @Override
        protected int getActionState() {
            return ANIM_SPELL;
        }

        @Override
        public boolean canUse() {
            return AllyPiglinShaman.this.summonIdle
                    && AllyPiglinShaman.this.getTarget() == null
                    && AllyPiglinShaman.this.isIdle()
                    && AllyPiglinShaman.this.spellCooldown <= 0
                    && AllyPiglinShaman.this.idleSummonCooldown <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && AllyPiglinShaman.this.hurtTime <= 0;
        }

        @Override
        public void start() {
            super.start();
            AllyPiglinShaman.this.spellCooldown = 100;
            AllyPiglinShaman.this.playSound(ModSounds.PREPARE_SUMMON.get(), 0.85F, 1.0F);
        }

        @Override
        public void tick() {
            super.tick();
            if (AllyPiglinShaman.this.actionTick == SPELL_CAST_TICK) {
                AllyPiglinShaman.this.summonIdleServants();
                AllyPiglinShaman.this.summonIdle = false;
                AllyPiglinShaman.this.idleSummonCooldown = 400;
            }
        }

        @Override
        public void stop() {
            boolean cast = AllyPiglinShaman.this.actionTick >= SPELL_CAST_TICK;
            super.stop();
            if (cast) {
                AllyPiglinShaman.this.summonIdle = false;
            }
        }
    }

    private static class BrewSelection {
        private final List<MobEffectInstance> mobEffects = new ArrayList<>();
        private final List<BrewEffectInstance> brewEffects = new ArrayList<>();
        private int area;

        private boolean isEmpty() {
            return this.mobEffects.isEmpty() && this.brewEffects.isEmpty();
        }
    }
}
