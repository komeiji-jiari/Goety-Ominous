package com.qiuyue.someillagerservants.common.entities.ally.mobs;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ally.illager.cultist.*;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractObsidianMonolith;
import com.Polarice3.Goety.common.entities.neutral.BurningHoglin;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.common.entities.projectiles.HellBolt;
import com.Polarice3.Goety.common.entities.projectiles.SurgingOrb;
import com.Polarice3.Goety.common.entities.util.EffectBlastTrap;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SRepositionPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.ServantUtil;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.compat.mod.GoetyAwakenCompat;
import com.qiuyue.someillagerservants.compat.mod.GoetyRevelationCompat;
import com.qiuyue.someillagerservants.config.AttributesConfig;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags.EntityTypes;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import org.jetbrains.annotations.Nullable;

public class HeresiarchServant extends CultistServant {
    private static final EntityDataAccessor<Integer> ANIM_STATE;
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID;
    private static final EntityDataAccessor<Boolean> HAS_OBSIDIAN_SOUL;
    public static String IDLE;
    public static String BLESS;
    public static String SUMMON;
    public static String BLAST;
    public static String SHOOT;
    public static String BARRAGE;
    public static String MELEE;
    public static String INSPECT;
    public static String CHANT;
    public static final Map<Integer, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        map.put(0, new ResourceLocation(SomeIllagerServants.MOD_ID, "textures/entity/heresiarch_servant.png"));
        map.put(1, new ResourceLocation(SomeIllagerServants.MOD_ID, "textures/entity/heresiarch_servant_alt.png"));
    });
    private AbstractObsidianMonolith monolith;
    private LivingEntity goetyAwakenMonolith;
    public double prevX;
    public double prevY;
    public double prevZ;
    public int spellType = 0;
    public int hysteriaCool;
    public int meleeCool;
    public int fightTick;
    public int aboutToTeleport = 0;
    public boolean startTeleporting;
    public float runeScale, prevRuneScale;
    public final List<Pair<Vec3, com.Polarice3.Goety.utils.ModelSnapshot>> trailSnapshots = new ArrayList<>(50);
    public float lastTrailTick = 0;
    public float deathRotation = 0.0F;
    private boolean nameInitialized = false;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState blessAnimationState = new AnimationState();
    public AnimationState summonAnimationState = new AnimationState();
    public AnimationState blastAnimationState = new AnimationState();
    public AnimationState shootAnimationState = new AnimationState();
    public AnimationState barrageAnimationState = new AnimationState();
    public AnimationState meleeAnimationState = new AnimationState();
    public AnimationState chantAnimationState = new AnimationState();

    public HeresiarchServant(EntityType<? extends Owned> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 0;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new com.qiuyue.someillagerservants.common.entities.ai.HeresiarchServantBarterGoal(this) {
            public void start() {
                super.start();
                HeresiarchServant.this.setAnimationState(HeresiarchServant.INSPECT);
            }

            public void stop() {
                super.stop();
                HeresiarchServant.this.setAnimationState(HeresiarchServant.IDLE);
            }
        });
        this.goalSelector.addGoal(0, new ChantGoal(this));
        this.goalSelector.addGoal(0, new MeleeGoal(this));
        this.goalSelector.addGoal(1, new StareGoal(this));
        this.goalSelector.addGoal(2, new HysteriaGoal(this));
        this.goalSelector.addGoal(2, new SummonGoal(this));
        this.goalSelector.addGoal(2, new BlastGoal(this));
        this.goalSelector.addGoal(2, new ShootGoal(this));
        this.goalSelector.addGoal(2, new BarrageGoal(this));
        this.goalSelector.addGoal(3, new ConvertVillagerGoal(this));
    }

    @Override
    public void targetSelectGoal() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, RaiderServant.class));
        this.targetSelector.addGoal(1, new SummonTargetGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.HeresiarchServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.HeresiarchServantArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.HeresiarchServantDamage.get())
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.25)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.HeresiarchServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.HeresiarchServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.HeresiarchServantDamage.get());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
        this.entityData.define(DATA_TYPE_ID, 0);
        this.entityData.define(HAS_OBSIDIAN_SOUL, false);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("HysteriaCool", this.hysteriaCool);
        pCompound.putInt("MeleeCool", this.meleeCool);
        pCompound.putInt("SpellType", this.spellType);
        pCompound.putInt("Outfit", this.getOutfitType());
        pCompound.putBoolean("HasObsidianSoul", this.hasObsidianSoul());
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("HysteriaCool")) {
            this.hysteriaCool = pCompound.getInt("HysteriaCool");
        }
        if (pCompound.contains("MeleeCool")) {
            this.meleeCool = pCompound.getInt("MeleeCool");
        }
        if (pCompound.contains("SpellType")) {
            this.spellType = pCompound.getInt("SpellType");
        }
        if (pCompound.contains("Outfit")) {
            this.setOutfitType(pCompound.getInt("Outfit"));
        }
        if (pCompound.contains("HasObsidianSoul")) {
            this.setHasObsidianSoul(pCompound.getBoolean("HasObsidianSoul"));
        }
    }

    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.HERESIARCH_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return ModSounds.HERESIARCH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.HERESIARCH_DEATH.get();
    }

    public SoundEvent getCelebrateSound() {
        return ModSounds.HERESIARCH_AMBIENT.get();
    }

    public void die(DamageSource source) {
        this.playSound(ModSounds.DEAD_MOAN.get(), 2.0F, this.getVoicePitch() * 0.75F);
        this.deathRotation = this.getYRot();

        if (!this.level().isClientSide && this.getTrueOwner() != null) {
            ItemStack itemStack = new ItemStack(ModItems.MALEFIC_HELM.get());
            FlyingItem flyingItem = new FlyingItem(
                    com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
                    this.level(),
                    this.getX(),
                    this.getY(),
                    this.getZ());
            flyingItem.setOwner(this.getTrueOwner());
            flyingItem.setItem(itemStack);
            flyingItem.setParticle(com.Polarice3.Goety.client.particles.ModParticleTypes.TOTEM_EFFECT.get());
            flyingItem.setSecondsCool(com.Polarice3.Goety.config.ItemConfig.ReviveSecondsCool.get());
            this.level().addFreshEntity(flyingItem);
        }

        if (source.getEntity() != null) {
            Entity entity = source.getEntity();
            LivingEntity target = null;
            if (entity instanceof LivingEntity livingEntity) {
                if (!MobUtil.areAllies(this, livingEntity) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                    target = livingEntity;
                }
            }
            if (this.getTarget() != null) {
                target = this.getTarget();
            }
            LivingEntity finalTarget = target;
            for (LivingEntity cultist : this.getAlliedCultistServants()) {
                Level var7 = this.level();
                if (var7 instanceof ServerLevel serverLevel) {
                    cultist.addEffect(new MobEffectInstance((MobEffect) GoetyEffects.HYSTERIA.get(), MathHelper.secondsToTicks(45), 0, false, false));
                    ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.ANGRY_VILLAGER, cultist);
                    if (finalTarget != null && cultist instanceof RaiderServant raiderServant) {
                        raiderServant.setTarget(finalTarget);
                    }
                }
            }
        }
        super.die(source);
    }


    public List<LivingEntity> getAlliedCultistServants() {
        List<CultistServant> cultists = this.level().getEntitiesOfClass(CultistServant.class,
                this.getBoundingBox().inflate(16.0),
                (CultistServant e) -> e != this && e.isAlive()
                        && MobUtil.areAllies(this, e)
                        && !e.getType().is(EntityTypes.BOSSES)
                        && (e.hasLineOfSight(this) || (this.getTarget() != null && !MobUtil.areAllies(e, this.getTarget()))));
        return new ArrayList<>(cultists);
    }

    public void setMonolith(@Nullable AbstractObsidianMonolith monolith) {
        this.monolith = monolith;
        this.goetyAwakenMonolith = null;
    }

    public void setGoetyAwakenMonolith(@Nullable LivingEntity monolith) {
        this.goetyAwakenMonolith = monolith;
        this.monolith = null;
    }

    public @Nullable AbstractObsidianMonolith getMonolith() {
        return this.monolith;
    }

    public @Nullable LivingEntity getGoetyAwakenMonolith() {
        return this.goetyAwakenMonolith;
    }

    private void setGoetyAwakenEmpowered() {
        if (this.goetyAwakenMonolith == null) {
            return;
        }
        try {
            java.lang.reflect.Field empoweredField = this.goetyAwakenMonolith.getClass().getField("empowered");
            empoweredField.setInt(this.goetyAwakenMonolith, 10);
        } catch (Exception e) {
            System.err.println("[SomeIllagerServants] Failed to set GoetyAwaken monolith empowered: " + e.getMessage());
        }
    }

    private Vec3 getGoetyAwakenMonolithPosition() {
        if (this.goetyAwakenMonolith == null) {
            return this.position();
        }
        return this.goetyAwakenMonolith.position();
    }

    @Override
    public void setCommandPosEntityOrder(LivingEntity living) {
        if (living instanceof AbstractObsidianMonolith monolith1 && MobUtil.areAllies(this, monolith1)) {
            this.setMonolith(monolith1);
        } else if (GoetyAwakenCompat.isObsidianMonolithServant(living) && MobUtil.areAllies(this, living)) {
            this.setGoetyAwakenMonolith(living);
        }
        super.setCommandPosEntityOrder(living);
    }

    @Override
    public void setCommandPosEntity(LivingEntity living) {
        if (living instanceof AbstractObsidianMonolith monolith1 && MobUtil.areAllies(this, monolith1)) {
            this.setMonolith(monolith1);
        } else if (GoetyAwakenCompat.isObsidianMonolithServant(living) && MobUtil.areAllies(this, living)) {
            this.setGoetyAwakenMonolith(living);
        }
        super.setCommandPosEntity(living);
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public boolean shouldAddTrailSnapshot() {
        return Mth.degreesDifferenceAbs(this.getYRot(), this.yBodyRot) < 45.0F
                && Mth.degreesDifferenceAbs(this.getYRot(), this.yBodyRotO) < 45.0F
                && Mth.degreesDifferenceAbs(this.yBodyRot, this.yBodyRotO) < 45.0F
                && this.startTeleporting;
    }

    public int getAnimationState(String animation) {
        if (Objects.equals(animation, IDLE)) {
            return 0;
        } else if (Objects.equals(animation, BLESS)) {
            return 1;
        } else if (Objects.equals(animation, SUMMON)) {
            return 2;
        } else if (Objects.equals(animation, BLAST)) {
            return 3;
        } else if (Objects.equals(animation, SHOOT)) {
            return 4;
        } else if (Objects.equals(animation, BARRAGE)) {
            return 5;
        } else if (Objects.equals(animation, MELEE)) {
            return 6;
        } else if (Objects.equals(animation, INSPECT)) {
            return 7;
        } else {
            return Objects.equals(animation, CHANT) ? 8 : 0;
        }
    }

    public List<AnimationState> getAllAnimations() {
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.blessAnimationState);
        animationStates.add(this.summonAnimationState);
        animationStates.add(this.blastAnimationState);
        animationStates.add(this.shootAnimationState);
        animationStates.add(this.barrageAnimationState);
        animationStates.add(this.meleeAnimationState);
        animationStates.add(this.chantAnimationState);
        return animationStates;
    }

    public void stopMostAnimation(AnimationState exception) {
        for (AnimationState state : this.getAllAnimations()) {
            if (state != exception) {
                state.stop();
            }
        }
    }

    public int getCurrentAnimation() {
        return this.entityData.get(ANIM_STATE);
    }

    public boolean isCurrentAnimation(String animation) {
        return this.getCurrentAnimation() == this.getAnimationState(animation);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.entityData.get(ANIM_STATE)) {
                case 0:
                case 7:
                    this.stopMostAnimation(this.idleAnimationState);
                    break;
                case 1:
                    this.blessAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.blessAnimationState);
                    break;
                case 2:
                    this.summonAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.summonAnimationState);
                    break;
                case 3:
                    this.blastAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.blastAnimationState);
                    break;
                case 4:
                    this.shootAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.shootAnimationState);
                    break;
                case 5:
                    this.barrageAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.barrageAnimationState);
                    break;
                case 6:
                    this.meleeAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.meleeAnimationState);
                    break;
                case 8:
                    this.chantAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.chantAnimationState);
                    break;
            }
        }
    }

    public int getOutfitType() {
        return this.entityData.get(DATA_TYPE_ID);
    }

    public void setOutfitType(int pType) {
        if (pType < 0 || pType >= this.OutfitTypeNumber() + 1) {
            pType = this.random.nextInt(this.OutfitTypeNumber());
        }
        this.entityData.set(DATA_TYPE_ID, pType);
    }

    public int OutfitTypeNumber() {
        return TEXTURE_BY_TYPE.size();
    }

    public ResourceLocation getResourceLocation() {
        return TEXTURE_BY_TYPE.getOrDefault(this.getOutfitType(), TEXTURE_BY_TYPE.get(0));
    }

    public boolean hasObsidianSoul() {
        return this.entityData.get(HAS_OBSIDIAN_SOUL);
    }

    public void setHasObsidianSoul(boolean hasSoul) {
        this.entityData.set(HAS_OBSIDIAN_SOUL, hasSoul);
    }

    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        this.populateDefaultEquipmentSlots(worldIn.getRandom(), difficultyIn);
        this.populateDefaultEquipmentEnchantments(worldIn.getRandom(), difficultyIn);
        if (!this.hasCustomName()) {
            int random = this.random.nextInt(4);
            int random2 = this.random.nextInt(12);
            Component component = Component.translatable("title.goety.heresiarch." + random);
            Component component1 = Component.translatable("name.goety.heresiarch." + random2);
            this.setCustomName(Component.translatable(component.getString() + component1.getString()));
        }
        this.setOutfitType(this.random.nextInt(this.OutfitTypeNumber()));
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    protected void populateDefaultEquipmentSlots(RandomSource p_217055_, DifficultyInstance p_217056_) {
    }

    private void initializeDefaultName() {
        if (!this.hasCustomName()) {
            int random = this.random.nextInt(4);
            int random2 = this.random.nextInt(12);
            Component component = Component.translatable("title.goety.heresiarch." + random);
            Component component1 = Component.translatable("name.goety.heresiarch." + random2);
            this.setCustomName(Component.translatable(component.getString() + component1.getString()));
        }
        if (this.getOutfitType() == 0 && this.random.nextInt(this.OutfitTypeNumber()) > 0) {
            this.setOutfitType(this.random.nextInt(this.OutfitTypeNumber()));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && !this.nameInitialized && this.tickCount > 1) {
            this.initializeDefaultName();
            this.nameInitialized = true;
        }
        if (!this.level().isClientSide) {
            if (this.hasObsidianSoul() && this.tickCount % 40 == 0) {
                this.heal(1.0F);
            }

            AbstractObsidianMonolith goetyMonolith = this.getMonolith();
            LivingEntity goetyAwakenMonolithEntity = this.getGoetyAwakenMonolith();

            if (goetyMonolith != null && goetyMonolith.isAlive()) {
                boolean shouldHeal = false;
                Entity monolithOwner = goetyMonolith.getTrueOwner();
                if (monolithOwner instanceof net.minecraft.world.entity.player.Player) {
                    shouldHeal = true;
                } else if (monolithOwner instanceof Owned owned) {
                    Entity subOwner = owned.getTrueOwner();
                    if (subOwner instanceof net.minecraft.world.entity.player.Player) {
                        shouldHeal = true;
                    }
                }

                if (shouldHeal && this.tickCount % 40 == 0) {
                    this.heal(1.0F);
                }
            }

            if (goetyAwakenMonolithEntity != null && goetyAwakenMonolithEntity.isAlive()) {
                boolean shouldHeal = false;
                if (goetyAwakenMonolithEntity instanceof Owned owned) {
                    Entity monolithOwner = owned.getTrueOwner();
                    if (monolithOwner instanceof net.minecraft.world.entity.player.Player) {
                        shouldHeal = true;
                    } else if (monolithOwner instanceof Owned subOwned) {
                        Entity subOwner = subOwned.getTrueOwner();
                        if (subOwner instanceof net.minecraft.world.entity.player.Player) {
                            shouldHeal = true;
                        }
                    }
                }

                if (shouldHeal && this.tickCount % 40 == 0) {
                    this.heal(1.0F);
                }
            }

            if (this.hasObsidianSoul() && this.tickCount % 40 == 0 && this.getTrueOwner() != null) {
                Entity owner = this.getTrueOwner();
                if (owner instanceof net.minecraft.world.entity.player.Player player) {
                    if (player.isAlive() && player.getHealth() < player.getMaxHealth()) {
                        player.heal(1.0F);
                    }
                }
            }
        }
        if (this.level().isClientSide) {
            this.prevRuneScale = this.runeScale;
            if (this.isCurrentAnimation(BARRAGE)) {
                this.runeScale += 0.3F;
            } else {
                this.runeScale -= 0.2F;
            }
            this.runeScale = Mth.clamp(this.runeScale, 0.0F, 1.0F);
            this.idleAnimationState.animateWhen(
                    (this.isCurrentAnimation(IDLE) || this.isCurrentAnimation(INSPECT)) && !this.walkAnimation.isMoving(),
                    this.tickCount);
        }
    }


    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.hysteriaCool > 0) {
                --this.hysteriaCool;
            }
            if (this.meleeCool > 0) {
                --this.meleeCool;
            }
            if (this.fightTick > 0 && this.getTarget() == null) {
                --this.fightTick;
            }
            if (this.getMonolith() != null) {
                if (!this.getMonolith().isAlive()) {
                    this.setMonolith(null);
                } else {
                    this.getMonolith().empowered = 10;
                }
            } else if (this.getGoetyAwakenMonolith() != null) {
                if (!this.getGoetyAwakenMonolith().isAlive()) {
                    this.setGoetyAwakenMonolith(null);
                } else {
                    this.setGoetyAwakenEmpowered();
                }
            } else {
                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(64.0D, 8.0D, 64.0D))) {
                    if (entity instanceof AbstractObsidianMonolith && MobUtil.areAllies(this, entity)) {
                        this.setMonolith((AbstractObsidianMonolith) entity);
                        break;
                    } else if (GoetyAwakenCompat.isObsidianMonolithServant(entity) && MobUtil.areAllies(this, entity)) {
                        this.setGoetyAwakenMonolith(entity);
                        break;
                    }
                }
            }
            if (this.startTeleporting) {
                ++this.aboutToTeleport;
                int i = MobUtil.healthIsHalved(this) ? 30 : 60;
                if (this.aboutToTeleport >= i && !this.isCurrentAnimation(MELEE)) {
                    LivingEntity target = this.getTarget();
                    if (target != null) {
                        this.teleport();
                        if (this.hasObsidianSoul() && this.random.nextFloat() < 0.3F) {
                            this.spawnMaverickServant();
                        }
                    }
                    this.startTeleporting = false;
                }
            } else if (this.aboutToTeleport > 0) {
                this.aboutToTeleport = 0;
            }
        }
    }

    protected float getDamageAfterMagicAbsorb(DamageSource damageSource, float damage) {
        damage = super.getDamageAfterMagicAbsorb(damageSource, damage);
        if (damageSource.getEntity() == this) {
            damage = 0.0F;
        }
        if (damageSource.is(DamageTypeTags.WITCH_RESISTANT_TO) || damageSource.is(DamageTypeTags.IS_FIRE)) {
            damage *= 0.15F;
        }
        return damage;
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        boolean isOwner = this.getTrueOwner() != null && pPlayer == this.getTrueOwner();
        boolean isAlly = ((this.getTrueOwner() != null && MobUtil.areAllies(this.getTrueOwner(), pPlayer)) || this.getTrueOwner() == null) && CuriosFinder.isWitchFriendly(pPlayer);

        if (isOwner && !this.level().isClientSide && !this.hasObsidianSoul()) {
            Item soulOfObsidian = GoetyRevelationCompat.getSoulOfObsidian();
            if (soulOfObsidian != null && itemstack.is(soulOfObsidian)) {
                this.setHasObsidianSoul(true);
                if (!pPlayer.isCreative()) {
                    itemstack.shrink(1);
                }
                this.playSound(ModSounds.RUMBLE.get(), 2.0F, 1.0F);
                ServerParticleUtil.addParticlesAroundSelf((ServerLevel) this.level(), ParticleTypes.ENCHANT, this);
                return InteractionResult.SUCCESS;
            }
        }

        if (this.getMainHandItem().isEmpty() && pHand == InteractionHand.MAIN_HAND && itemstack.is(ModTags.Items.WITCH_CURRENCY)) {
            if (isOwner || isAlly) {
                if (!this.isAggressive()) {
                    this.playSound(this.getCelebrateSound());
                    ItemStack itemstack1;
                    if (pPlayer.isCreative()) {
                        itemstack1 = itemstack;
                    } else {
                        itemstack1 = itemstack.split(1);
                    }
                    this.setItemSlot(EquipmentSlot.MAINHAND, itemstack1);
                    this.setTrader(pPlayer);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (isOwner) {
            return ServantUtil.equipServantArmor(pPlayer, this, itemstack, super.mobInteract(pPlayer, pHand));
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public void warnKill(Player player) {
        this.killChance = 60;
        player.displayClientMessage(Component.translatable("info.goety.servant.tryKill", this.getDisplayName()), true);
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    protected void teleport() {
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        if (!this.level().isClientSide() && this.isAlive()) {
            Vec3 vec3 = this.position();
            if (this.getMonolith() != null) {
                vec3 = this.getMonolith().position();
            } else if (this.getGoetyAwakenMonolith() != null) {
                vec3 = this.getGoetyAwakenMonolithPosition();
            }
            for (int i = 0; i < 128; ++i) {
                double d3 = vec3.x + (this.level().getRandom().nextDouble() - 0.5) * 32.0;
                double d4 = this.getTarget() != null ? this.getTarget().getY() : vec3.y;
                double d5 = vec3.z + (this.level().getRandom().nextDouble() - 0.5) * 32.0;
                BlockPos blockPos = BlockPos.containing(d3, d4, d5);
                boolean flag = this.getTarget() == null || i >= 126 || BlockFinder.canSeeBlock(this.getTarget(), blockPos);
                EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, d3, d4, d5);
                if (event.isCanceled()) {
                    break;
                }
                Vec3 newPos = new Vec3(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                if (this.getTarget() != null && i < 126 && newPos.distanceTo(this.getTarget().position()) > this.getAttributeValue(Attributes.FOLLOW_RANGE)) {
                    flag = false;
                }
                if (flag && this.randomTeleport(newPos.x, newPos.y, newPos.z, false)) {
                    ModNetwork.sendToALL(new SRepositionPacket(this.getId(), this.getX(), this.getY(), this.getZ()));
                    this.teleportHits();
                    break;
                }
            }
        }
    }

    public void teleportHits() {
        Level var2 = this.level();
        if (var2 instanceof ServerLevel serverLevel) {
            int i = 16;
            for (int j = 0; j < i; ++j) {
                double d0 = (double) j / (double) (i - 1);
                float f = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (this.random.nextFloat() - 0.5F) * 0.2F;
                double d1 = Mth.lerp(d0, this.prevX, this.getX()) + (this.random.nextDouble() - 0.5) * (double) this.getBbWidth() * 2.0;
                double d2 = Mth.lerp(d0, this.prevY, this.getY()) + this.random.nextDouble() * (double) this.getBbHeight();
                double d3 = Mth.lerp(d0, this.prevZ, this.getZ()) + (this.random.nextDouble() - 0.5) * (double) this.getBbWidth() * 2.0;
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, d1, d2, d3, 0, (double) f, (double) f1, (double) f2, 1.0);
            }
        }
        this.level().gameEvent(GameEvent.TELEPORT, this.position(), Context.of(this));
        if (!this.isSilent()) {
            this.level().playSound(null, this.prevX, this.prevY, this.prevZ, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }

    private void spawnMaverickServant() {
        List<CultistServant> cultists = this.level().getEntitiesOfClass(CultistServant.class,
                this.getBoundingBox().inflate(32.0D),
                c -> c != null && c.isAlive() &&
                        (c instanceof MaverickServant || c instanceof HereticServant ||
                                c instanceof ReprobateServant || c instanceof WarlockServant) &&
                        c.getTrueOwner() == this);

        if (cultists.size() >= 8) {
            return;
        }

        double rand = this.random.nextDouble();
        EntityType<? extends CultistServant> servantType;

        if (rand < 0.5) {
            servantType = ModEntityType.MAVERICK_SERVANT.get();
        } else if (rand < 0.8) {
            servantType = ModEntityType.HERETIC_SERVANT.get();
        } else if (rand < 0.9) {
            servantType = ModEntityType.REPROBATE_SERVANT.get();
        } else {
            servantType = ModEntityType.WARLOCK_SERVANT.get();
        }

        CultistServant servant = servantType.create(this.level());
        if (servant != null) {
            servant.setTrueOwner(this);
            servant.setPos(this.position());

            if (this.getTarget() != null) {
                servant.setTarget(this.getTarget());
            }

            if (servant instanceof MaverickServant maverick) {
                maverick.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(net.minecraft.world.item.Items.IRON_SWORD));
            }

            this.level().addFreshEntity(servant);

            if (this.level() instanceof ServerLevel serverLevel) {
                ServerParticleUtil.summonUndeadParticles(serverLevel, servant, new ColorUtil(16753408), 16753408, 16777070);
            }
        }
    }

    static {
        ANIM_STATE = SynchedEntityData.defineId(HeresiarchServant.class, EntityDataSerializers.INT);
        DATA_TYPE_ID = SynchedEntityData.defineId(HeresiarchServant.class, EntityDataSerializers.INT);
        HAS_OBSIDIAN_SOUL = SynchedEntityData.defineId(HeresiarchServant.class, EntityDataSerializers.BOOLEAN);
        IDLE = "idle";
        BLESS = "bless";
        SUMMON = "summon";
        BLAST = "blast";
        SHOOT = "shoot";
        BARRAGE = "barrage";
        MELEE = "melee";
        INSPECT = "inspect";
        CHANT = "chant";
    }

    static class ChantGoal extends Goal {
        public HeresiarchServant heresiarch;

        public ChantGoal(HeresiarchServant heresiarch) {
            this.heresiarch = heresiarch;
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.TARGET));
        }

        public boolean canUse() {
            return (this.heresiarch.getMonolith() != null && this.heresiarch.getMonolith().isAlive()
                    || this.heresiarch.getGoetyAwakenMonolith() != null && this.heresiarch.getGoetyAwakenMonolith().isAlive())
                    && (this.heresiarch.getTarget() == null
                    || this.heresiarch.getTarget().distanceTo(this.heresiarch) > 6.0
                    || !this.heresiarch.hasLineOfSight(this.heresiarch.getTarget()))
                    && this.heresiarch.getLastHurtByMobTimestamp() <= 0
                    && this.heresiarch.fightTick <= 0
                    && this.heresiarch.getHealth() == this.heresiarch.getMaxHealth();
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void stop() {
            super.stop();
            this.heresiarch.fightTick = MathHelper.secondsToTicks(10);
            this.heresiarch.setAnimationState(HeresiarchServant.IDLE);
        }


        public void tick() {
            LivingEntity targetMonolith = this.heresiarch.getMonolith();
            if (targetMonolith == null) {
                targetMonolith = this.heresiarch.getGoetyAwakenMonolith();
            }

            if (targetMonolith != null) {
                if (this.heresiarch.distanceTo(targetMonolith) <= 8.0
                        && this.heresiarch.hasLineOfSight(targetMonolith)) {
                    if (!this.heresiarch.isCurrentAnimation(HeresiarchServant.CHANT)) {
                        this.heresiarch.setAnimationState(HeresiarchServant.CHANT);
                    }
                    MobUtil.instaLook(this.heresiarch, targetMonolith);
                    this.drawParticleBeam(this.heresiarch, targetMonolith);
                    if (this.heresiarch.tickCount % 20 == 0) {
                        targetMonolith.heal(1.0F);
                    }
                    if (this.heresiarch.getMonolith() != null) {
                        this.heresiarch.getMonolith().empowered = 10;
                    } else {
                        this.heresiarch.setGoetyAwakenEmpowered();
                    }
                    this.heresiarch.getNavigation().stop();
                    this.heresiarch.getMoveControl().strafe(0.0F, 0.0F);
                    this.heresiarch.noActionTime = 0;
                } else {
                    this.heresiarch.setAnimationState(HeresiarchServant.IDLE);
                    Vec3 vec3 = targetMonolith.position();
                    this.heresiarch.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0);
                }
            }
        }

        private void drawParticleBeam(LivingEntity pSource, LivingEntity pTarget) {
            double d0 = pTarget.getX() - pSource.getX();
            double d1 = pTarget.getY() + (double) pTarget.getBbHeight() * 0.5
                    - (pSource.getY() + (double) pSource.getBbHeight() * 0.5);
            double d2 = pTarget.getZ() - pSource.getZ();
            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d0 /= d3;
            d1 /= d3;
            d2 /= d3;
            double d4 = pSource.level().random.nextDouble();
            Level var14 = pSource.level();
            if (var14 instanceof ServerLevel serverWorld) {
                while (d4 < d3) {
                    d4 += 0.5;
                    serverWorld.sendParticles((SimpleParticleType) ModParticleTypes.CHANT.get(),
                            pSource.getX() + d0 * d4,
                            pSource.getY() + d1 * d4 + (double) pSource.getEyeHeight(),
                            pSource.getZ() + d2 * d4,
                            1, 0.0, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    static class MeleeGoal extends HeresiarchGoal {
        public int meleeTick;

        public MeleeGoal(HeresiarchServant heresiarch) {
            super(heresiarch);
        }

        public boolean canUse() {
            return (this.heresiarch.startTeleporting || this.heresiarch.isCurrentAnimation(HeresiarchServant.IDLE))
                    && this.heresiarch.meleeCool <= 0 && this.getTarget() != null
                    ? this.heresiarch.isWithinMeleeAttackRange(this.getTarget()) : false;
        }

        public boolean canContinueToUse() {
            return this.meleeTick > 0;
        }

        public void start() {
            super.start();
            this.heresiarch.setAnimationState(HeresiarchServant.MELEE);
            this.meleeTick = 25;
            this.stopAndStare();
        }

        public void tick() {
            super.tick();
            --this.meleeTick;
            this.stopAndStare();
            if (this.meleeTick == 22) {
                this.heresiarch.playSound(ModSounds.SWORD_SHING.get(), 1.0F, this.heresiarch.getVoicePitch());
            }
            if (this.meleeTick == 13) {
                this.heresiarch.playSound(ModSounds.SOUL_KNIFE_NO_SOUL_SWING.get(), 1.0F, this.heresiarch.getVoicePitch());
                float damage = (float) this.heresiarch.getAttributeValue(Attributes.ATTACK_DAMAGE) + 4.0F;
                DamageSource damageSource = this.heresiarch.damageSources().mobAttack(this.heresiarch);
                MobUtil.areaAttack(this.heresiarch, 4.0F, 0.25F, 90.0F, damage, 0.0F, 0, damageSource, true);
                if (this.getTarget() != null && this.heresiarch.isWithinMeleeAttackRange(this.getTarget())) {
                    this.getTarget().hurt(damageSource, damage);
                }
            }
        }

        public void stop() {
            this.meleeTick = 0;
            this.heresiarch.meleeCool = MathHelper.secondsToTicks(5);
            this.heresiarch.setAnimationState(HeresiarchServant.IDLE);
            this.heresiarch.startTeleporting = true;
        }
    }

    static class StareGoal extends HeresiarchGoal {
        public StareGoal(HeresiarchServant heresiarch) {
            super(heresiarch);
        }

        public boolean canUse() {
            if (this.heresiarch.meleeCool <= 0 && this.getTarget() != null
                    && this.heresiarch.isWithinMeleeAttackRange(this.getTarget())) {
                return false;
            } else {
                return this.heresiarch.startTeleporting && this.heresiarch.aboutToTeleport > 0
                        && this.heresiarch.isCurrentAnimation(HeresiarchServant.IDLE);
            }
        }

        public void start() {
            super.start();
            this.stopAndStare();
        }

        public void tick() {
            super.tick();
            this.stopAndStare();
        }

        public void stop() {
        }
    }

    static class HysteriaGoal extends HeresiarchGoal {
        public int blessTick;

        public HysteriaGoal(HeresiarchServant heresiarch) {
            super(heresiarch);
        }

        public boolean canUse() {
            if (!com.qiuyue.someillagerservants.config.MobsConfig.HeresiarchHysteriaEnabled.get()) {
                return false;
            }
            if (super.canUse() && this.heresiarch.hysteriaCool <= 0) {
                return !this.heresiarch.getAlliedCultistServants().isEmpty();
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.blessTick > 0;
        }

        public void start() {
            super.start();
            this.heresiarch.setAnimationState(HeresiarchServant.BLESS);
            this.blessTick = 50;
            this.stopAndStare();
        }

        public void tick() {
            super.tick();
            --this.blessTick;
            this.stopAndStare();
            if (this.blessTick == 30) {
                List<LivingEntity> allies = this.heresiarch.getAlliedCultistServants();
                if (!allies.isEmpty()) {
                    LivingEntity target = this.getTarget();
                    LivingEntity finalTarget = target;
                    for (LivingEntity cultist : allies) {
                        if (cultist instanceof HeresiarchServant) {
                            continue;
                        }
                        Level var4 = this.heresiarch.level();
                        if (var4 instanceof ServerLevel serverLevel) {
                            cultist.addEffect(new MobEffectInstance((MobEffect) GoetyEffects.HYSTERIA.get(),
                                    MathHelper.secondsToTicks(45), 0, false, false));

                            if (this.heresiarch.hasObsidianSoul()) {
                                try {
                                    net.minecraft.world.effect.MobEffect buffEffect =
                                            net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS.getValue(
                                                    new net.minecraft.resources.ResourceLocation("goety", "buff"));
                                    if (buffEffect != null) {
                                        cultist.addEffect(new MobEffectInstance(buffEffect,
                                                MathHelper.secondsToTicks(45), 1, false, false));
                                    }
                                } catch (Exception e) {
                                    System.err.println("[SomeIllagerServants] Failed to apply Buff effect: " + e.getMessage());
                                }
                            }

                            cultist.playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 1.0F);
                            ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.ANGRY_VILLAGER, cultist);
                            if (finalTarget != null && cultist instanceof RaiderServant raiderServant) {
                                raiderServant.setTarget(finalTarget);
                            }
                        }
                    }
                }

                if (this.heresiarch.hasObsidianSoul()) {
                    try {
                        net.minecraft.world.effect.MobEffect ralliedEffect =
                                net.minecraftforge.registries.ForgeRegistries.MOB_EFFECTS.getValue(
                                        new net.minecraft.resources.ResourceLocation("goety", "rallied"));

                        if (ralliedEffect != null && this.heresiarch.getTrueOwner() != null) {
                            net.minecraft.world.entity.Entity owner = this.heresiarch.getTrueOwner();

                            if (owner instanceof net.minecraft.world.entity.player.Player player) {
                                if (!player.isCreative()) {
                                    player.addEffect(new MobEffectInstance(ralliedEffect,
                                            MathHelper.secondsToTicks(25), 0, false, false));
                                }
                            } else if (owner instanceof LivingEntity livingOwner) {
                                List<net.minecraft.world.entity.player.Player> nearbyPlayers = this.heresiarch.level().getEntitiesOfClass(
                                        net.minecraft.world.entity.player.Player.class,
                                        livingOwner.getBoundingBox().inflate(16.0),
                                        p -> p != null && p.isAlive() && MobUtil.areAllies(livingOwner, p));

                                for (net.minecraft.world.entity.player.Player player : nearbyPlayers) {
                                    if (!player.isCreative()) {
                                        player.addEffect(new MobEffectInstance(ralliedEffect,
                                                MathHelper.secondsToTicks(25), 0, false, false));
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("[SomeIllagerServants] Failed to apply Rallied effect: " + e.getMessage());
                    }
                }
            }
        }

        public void stop() {
            this.blessTick = 0;
            this.heresiarch.hysteriaCool = MathHelper.secondsToTicks(60);
            super.stop();
        }
    }

    static class SummonGoal extends HeresiarchGoal {
        public int summonTick;

        public SummonGoal(HeresiarchServant heresiarch) {
            super(heresiarch);
        }

        public boolean canUse() {
            if (super.canUse()) {
                return this.heresiarch.spellType == 0;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.summonTick > 0;
        }

        public void start() {
            super.start();
            this.heresiarch.setAnimationState(HeresiarchServant.SUMMON);
            this.summonTick = 40;
            this.stopAndStare();
        }

        public void tick() {
            super.tick();
            --this.summonTick;
            this.stopAndStare();
            if (this.summonTick == 20) {
                int x = (int) (MobUtil.getHorizontalLeftLookAngle(this.heresiarch).x * 2.0);
                int z = (int) (MobUtil.getHorizontalLeftLookAngle(this.heresiarch).z * 2.0);
                BlockPos left = new BlockPos(this.heresiarch.blockPosition().offset(x, 3, z));
                left = BlockFinder.findGroundBelow(this.heresiarch.level(), left);
                BurningHoglin hoglin = new BurningHoglin((EntityType) ModEntityType.BURNING_HOGLIN.get(), this.heresiarch.level());
                hoglin.setTrueOwner(this.heresiarch);
                hoglin.setPos(Vec3.atBottomCenterOf(left));
                if (this.halfHealth()) {
                    hoglin.setWindUpTime(10);
                }
                hoglin.setLimitedLife(100);

                if (this.heresiarch.hasObsidianSoul()) {
                    hoglin.setExplosionPower(hoglin.getExplosionPower() + 2.0F);
                }

                Level var6 = this.heresiarch.level();
                if (var6 instanceof ServerLevel serverLevel) {
                    ForgeEventFactory.onFinalizeSpawn(hoglin, serverLevel,
                            serverLevel.getCurrentDifficultyAt(this.heresiarch.blockPosition()),
                            MobSpawnType.MOB_SUMMONED, null, null);
                    ServerParticleUtil.summonUndeadParticles(serverLevel, hoglin, new ColorUtil(16753408), 16753408, 16777070);
                }
                if (this.getTarget() != null) {
                    hoglin.setTarget(this.getTarget());
                }
                this.heresiarch.level().addFreshEntity(hoglin);
            }
        }

        public void stop() {
            this.summonTick = 0;
            this.heresiarch.spellType = 1;
            super.stop();
        }
    }

    static class BlastGoal extends HeresiarchGoal {
        public int blastTick;

        public BlastGoal(HeresiarchServant heresiarch) {
            super(heresiarch);
        }

        public boolean canUse() {
            if (super.canUse()) {
                return this.heresiarch.spellType == 1;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.blastTick > 0;
        }

        public void start() {
            super.start();
            this.heresiarch.setAnimationState(HeresiarchServant.BLAST);
            this.blastTick = 35;
            this.stopAndStare();
        }

        public void tick() {
            super.tick();
            --this.blastTick;
            this.stopAndStare();
            if (this.blastTick == 10 && this.getTarget() != null) {
                int amount = this.halfHealth() ? 4 : 3;
                this.heresiarch.level().playSound(null, this.getTarget().getX(), this.getTarget().getY(),
                        this.getTarget().getZ(), ModSounds.TOWER_WRAITH_ACID.get(),
                        this.heresiarch.getSoundSource(), 2.0F, 0.5F);
                for (int i = 0; i < amount; ++i) {
                    Vec3 vec3 = MobUtil.getFrontPos(this.getTarget(), 4.0);
                    if (i == 1) {
                        vec3 = MobUtil.getRightPos(this.getTarget(), 4.0);
                    } else if (i == 2) {
                        vec3 = MobUtil.getLeftPos(this.getTarget(), 4.0);
                    } else if (i == 3) {
                        vec3 = MobUtil.getBackPos(this.getTarget(), 4.0);
                    }
                    EffectBlastTrap effectBlastTrap = new EffectBlastTrap(this.heresiarch.level(), vec3);
                    effectBlastTrap.setOwner(this.heresiarch);
                    effectBlastTrap.setAreaOfEffect(1.5F);
                    effectBlastTrap.playSound = false;

                    int weaknessLevel = this.heresiarch.hasObsidianSoul() ? 1 : 0;
                    int slownessLevel = this.heresiarch.hasObsidianSoul() ? 1 : 0;
                    int acidVenomLevel = this.heresiarch.hasObsidianSoul() ? 1 : 0;
                    int sappedLevel = this.heresiarch.hasObsidianSoul() ? 2 : 1;

                    if (i == 0) {
                        effectBlastTrap.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1800, weaknessLevel));
                    } else if (i == 1) {
                        effectBlastTrap.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1800, slownessLevel));
                    } else if (i == 2) {
                        effectBlastTrap.addEffect(new MobEffectInstance((MobEffect) GoetyEffects.ACID_VENOM.get(), 900, acidVenomLevel));
                    } else {
                        effectBlastTrap.addEffect(new MobEffectInstance((MobEffect) GoetyEffects.SAPPED.get(), 1800, sappedLevel));
                    }
                    MobUtil.moveDownToGround(effectBlastTrap);
                    this.heresiarch.level().addFreshEntity(effectBlastTrap);
                }
            }
        }

        public void stop() {
            this.blastTick = 0;
            this.heresiarch.spellType = 2;
            super.stop();
        }
    }

    static class ShootGoal extends HeresiarchGoal {
        public int shootTick;

        public ShootGoal(HeresiarchServant heresiarch) {
            super(heresiarch);
        }

        public boolean canUse() {
            if (super.canUse()) {
                return this.heresiarch.spellType == 2;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.shootTick > 0;
        }

        public void start() {
            super.start();
            this.heresiarch.setAnimationState(HeresiarchServant.SHOOT);
            this.shootTick = 20;
            this.stopAndStare();
        }

        public void tick() {
            super.tick();
            --this.shootTick;
            this.stopAndStare();

            boolean hasObsidianSoul = this.heresiarch.hasObsidianSoul();
            boolean halfHealth = this.halfHealth();

            if (this.shootTick == 5) {
                this.heresiarch.playSound(ModSounds.HELL_BOLT_SHOOT.get(), 2.0F,
                        (this.heresiarch.getRandom().nextFloat() - this.heresiarch.getRandom().nextFloat()) * 0.2F + 1.0F);

                if (hasObsidianSoul) {
                    this.vecFromCenterToFrontOfFace(-20.0F);
                    this.vecFromCenterToFrontOfFace(-10.0F);
                    this.vecFromCenterToFrontOfFace(0.0F);
                    this.vecFromCenterToFrontOfFace(10.0F);
                    this.vecFromCenterToFrontOfFace(20.0F);
                } else {
                    this.vecFromCenterToFrontOfFace(0.0F);
                    this.vecFromCenterToFrontOfFace(-10.0F);
                    this.vecFromCenterToFrontOfFace(10.0F);
                    if (halfHealth) {
                        this.vecFromCenterToFrontOfFace(-20.0F);
                        this.vecFromCenterToFrontOfFace(20.0F);
                    }
                }
            }

            if (halfHealth && this.shootTick == 9) {
                this.heresiarch.playSound(ModSounds.HELL_BOLT_SHOOT.get(), 2.0F,
                        (this.heresiarch.getRandom().nextFloat() - this.heresiarch.getRandom().nextFloat()) * 0.2F + 1.0F);

                if (hasObsidianSoul) {
                    this.vecFromCenterToFrontOfFace(-30.0F);
                    this.vecFromCenterToFrontOfFace(-20.0F);
                    this.vecFromCenterToFrontOfFace(-10.0F);
                    this.vecFromCenterToFrontOfFace(0.0F);
                    this.vecFromCenterToFrontOfFace(10.0F);
                    this.vecFromCenterToFrontOfFace(20.0F);
                    this.vecFromCenterToFrontOfFace(30.0F);
                } else {
                    this.vecFromCenterToFrontOfFace(0.0F);
                    this.vecFromCenterToFrontOfFace(-10.0F);
                    this.vecFromCenterToFrontOfFace(10.0F);
                }
            }
        }

        private void vecFromCenterToFrontOfFace(float angle) {
            double viewDistance = 2.0;
            Vec3 viewVector = this.heresiarch.getViewVector(1.0F);
            if (angle != 0.0F) {
                float offset = (float) Math.toRadians((double) angle);
                viewVector = viewVector.yRot(offset);
            }
            double d0 = viewVector.x * viewDistance;
            double d1 = viewVector.y * viewDistance;
            double d2 = viewVector.z * viewDistance;
            HellBolt hellBolt = new HellBolt(this.heresiarch, d0, d1, d2, this.heresiarch.level());
            hellBolt.setPos(hellBolt.getX() + d0, this.heresiarch.getEyeY() + d1, hellBolt.getZ() + d2);
            this.heresiarch.level().addFreshEntity(hellBolt);
        }

        public void stop() {
            this.shootTick = 0;
            this.heresiarch.spellType = 3;
            super.stop();
        }
    }

    static class BarrageGoal extends HeresiarchGoal {
        public int barrageTick;

        public BarrageGoal(HeresiarchServant heresiarch) {
            super(heresiarch);
        }

        public boolean canUse() {
            if (super.canUse()) {
                return this.heresiarch.spellType == 3;
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.barrageTick > 0;
        }

        public void start() {
            super.start();
            this.heresiarch.setAnimationState(HeresiarchServant.BARRAGE);
            this.barrageTick = 80;
            this.stopAndStare();
        }

        public void tick() {
            super.tick();
            --this.barrageTick;
            this.stopAndStare();

            int baseSpeed = 6;
            boolean hasObsidianSoul = this.heresiarch.hasObsidianSoul();
            boolean halfHealth = this.halfHealth();

            if (hasObsidianSoul && halfHealth) {
                baseSpeed = 2;
            } else if (hasObsidianSoul) {
                baseSpeed = 4;
            } else if (halfHealth) {
                baseSpeed = 3;
            }

            if (this.barrageTick <= 70 && this.barrageTick > 10 && this.barrageTick % baseSpeed == 0) {
                Vec3 vector3d = this.heresiarch.getViewVector(1.0F);
                Vec3 vec3 = vector3d;
                if (this.getTarget() != null) {
                    Vec3 pos = this.getTarget().position().add(0.0, 0.25, 0.0);
                    vec3 = pos.subtract(this.heresiarch.position().add(0.0, (double) (this.heresiarch.getBbHeight() * 1.5F), 0.0));
                }

                int boltLevel = 0;
                if (halfHealth) {
                    ++boltLevel;
                }
                if (this.heresiarch.level().getDifficulty() == Difficulty.HARD) {
                    ++boltLevel;
                }

                AllySafeSurgingOrb mainOrb = new AllySafeSurgingOrb(
                        this.heresiarch.getX() + vector3d.x / 2.0,
                        this.heresiarch.getY() + (double) (this.heresiarch.getBbHeight() * 1.5F),
                        this.heresiarch.getZ() + vector3d.z / 2.0,
                        vec3.x, vec3.y, vec3.z, this.heresiarch.level());
                mainOrb.setOwner(this.heresiarch);
                mainOrb.setOrange(true);
                mainOrb.setExtraDamage((float) this.heresiarch.getAttributeValue(Attributes.ATTACK_DAMAGE));
                mainOrb.setBoltSpeed(boltLevel);
                this.heresiarch.level().addFreshEntity(mainOrb);

                if (hasObsidianSoul) {
                    Vec3 leftVec3 = vec3.yRot((float) Math.toRadians(-15.0));
                    AllySafeSurgingOrb leftOrb = new AllySafeSurgingOrb(
                            this.heresiarch.getX() + vector3d.x / 2.0,
                            this.heresiarch.getY() + (double) (this.heresiarch.getBbHeight() * 1.5F),
                            this.heresiarch.getZ() + vector3d.z / 2.0,
                            leftVec3.x, leftVec3.y, leftVec3.z, this.heresiarch.level());
                    leftOrb.setOwner(this.heresiarch);
                    leftOrb.setOrange(true);
                    leftOrb.setExtraDamage((float) this.heresiarch.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    leftOrb.setBoltSpeed(boltLevel);
                    this.heresiarch.level().addFreshEntity(leftOrb);

                    Vec3 rightVec3 = vec3.yRot((float) Math.toRadians(15.0));
                    AllySafeSurgingOrb rightOrb = new AllySafeSurgingOrb(
                            this.heresiarch.getX() + vector3d.x / 2.0,
                            this.heresiarch.getY() + (double) (this.heresiarch.getBbHeight() * 1.5F),
                            this.heresiarch.getZ() + vector3d.z / 2.0,
                            rightVec3.x, rightVec3.y, rightVec3.z, this.heresiarch.level());
                    rightOrb.setOwner(this.heresiarch);
                    rightOrb.setOrange(true);
                    rightOrb.setExtraDamage((float) this.heresiarch.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    rightOrb.setBoltSpeed(boltLevel);
                    this.heresiarch.level().addFreshEntity(rightOrb);
                }

                this.heresiarch.playSound(ModSounds.SHOCK_CAST.get(), 1.0F,
                        (this.heresiarch.getRandom().nextFloat() - this.heresiarch.getRandom().nextFloat()) * 0.2F + 1.0F);
            }
        }

        public void stop() {
            this.barrageTick = 0;
            this.heresiarch.spellType = 0;
            super.stop();
        }
    }

    static class AllySafeSurgingOrb extends SurgingOrb {
        public AllySafeSurgingOrb(double x, double y, double z, double mx, double my, double mz, Level level) {
            super(x, y, z, mx, my, mz, level);
        }

        @Override
        protected void onHit(HitResult hitResult) {
            if (hitResult instanceof EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                if (this.getOwner() != null && MobUtil.areAllies(this.getOwner(), entity)) {
                    this.discard();
                    return;
                }
            }
            super.onHit(hitResult);
        }
    }

    abstract static class HeresiarchGoal extends Goal {
        public HeresiarchServant heresiarch;

        public HeresiarchGoal(HeresiarchServant heresiarch) {
            this.heresiarch = heresiarch;
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.TARGET));
        }

        public boolean canUse() {
            return this.getTarget() != null && this.heresiarch.hasLineOfSight(this.getTarget())
                    && !this.heresiarch.startTeleporting
                    && this.heresiarch.isCurrentAnimation(HeresiarchServant.IDLE);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public boolean isInterruptable() {
            return false;
        }

        public @Nullable LivingEntity getTarget() {
            return this.heresiarch.getTarget();
        }

        public boolean halfHealth() {
            return MobUtil.healthIsHalved(this.heresiarch);
        }

        public void stopAndStare() {
            this.heresiarch.getNavigation().stop();
            this.heresiarch.getMoveControl().strafe(0.0F, 0.0F);
            if (this.getTarget() != null) {
                this.heresiarch.getLookControl().setLookAt(this.getTarget(), 100.0F, 100.0F);
                this.heresiarch.lookAt(this.getTarget(), 100.0F, 100.0F);
            }
        }

        public void stop() {
            super.stop();
            this.heresiarch.setAnimationState(HeresiarchServant.IDLE);
            this.heresiarch.startTeleporting = true;
        }
    }

    static class ConvertVillagerGoal extends Goal {
        private final HeresiarchServant heresiarch;
        private LivingEntity victim;
        private int convertTick;
        private int coolDown;

        public ConvertVillagerGoal(HeresiarchServant heresiarch) {
            this.heresiarch = heresiarch;
            this.coolDown = 0;
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.coolDown > 0) {
                --this.coolDown;
                return false;
            }

            if (this.heresiarch.getCommandPosEntity() != null) {
                Entity commandEntity = this.heresiarch.getCommandPosEntity();
                if (commandEntity instanceof Villager || commandEntity instanceof net.minecraft.world.entity.npc.WanderingTrader) {
                    this.victim = (LivingEntity) commandEntity;
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.victim != null && this.victim.isAlive() && this.convertTick > 0;
        }


        @Override
        public void start() {
            this.convertTick = this.getCastWarmupTime();
            this.heresiarch.getLookControl().setLookAt(this.victim);
            this.heresiarch.setAnimationState(HeresiarchServant.CHANT);
        }

        @Override
        public void tick() {
            --this.convertTick;

            if (this.victim != null && this.victim.isAlive()) {
                if (this.victim instanceof Mob mobVictim) {
                    mobVictim.getNavigation().stop();
                    mobVictim.getMoveControl().strafe(0.0F, 0.0F);
                }

                this.heresiarch.getLookControl().setLookAt(this.victim);

                double distance = this.heresiarch.distanceTo(this.victim);
                if (distance > 3.0) {
                    Vec3 offset = new Vec3(2, 0, 0);
                    Vec3 at = this.groundOf(this.victim.position().add(offset));
                    this.heresiarch.getNavigation().moveTo(at.x, at.y, at.z, 0.75F);
                } else {
                    this.heresiarch.getNavigation().stop();
                }

                if (this.heresiarch.tickCount % 5 == 0) {
                    if (this.heresiarch.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.ENCHANT,
                                this.victim.getX(), this.victim.getY() + 1.0, this.victim.getZ(),
                                10, 0.5, 0.5, 0.5, 0.0);
                    }
                }
            }
        }

        @Override
        public void stop() {
            super.stop();
            this.convertTick = 0;
            this.heresiarch.setAnimationState(HeresiarchServant.IDLE);

            LivingEntity victim = this.heresiarch.getCommandPosEntity();
            if (victim != null && victim.isAlive()) {
                EntityType<?> targetType = this.getRandomCultistType(victim);
                Player player = null;
                if (this.heresiarch.getTrueOwner() instanceof Player p) {
                    player = p;
                }

                Entity convertedEntity = MobUtil.convertTo(victim, targetType, true, player);
                if (convertedEntity instanceof Mob mob) {
                    mob.setYHeadRot(victim.getYHeadRot());
                    mob.setYRot(victim.getYRot());
                    if (mob instanceof Owned owned && player != null) {
                        owned.setTrueOwner(player);
                    }
                }

                if (!this.heresiarch.isSilent()) {
                    this.heresiarch.level().levelEvent((Player) null, 1026, this.heresiarch.blockPosition(), 0);
                }
            }

            this.heresiarch.setCommandPosEntity(null);
            this.coolDown = MathHelper.secondsToTicks(5);
        }

        private Vec3 groundOf(Vec3 in) {
            int x = (int) in.x;
            int y = (int) in.y;
            int z = (int) in.z;
            while (this.heresiarch.level().isEmptyBlock(new net.minecraft.core.BlockPos(x, y - 1, z))
                    && y > this.heresiarch.level().getMinBuildHeight()) {
                y--;
            }
            while (!this.heresiarch.level().isEmptyBlock(new net.minecraft.core.BlockPos(x, y, z))
                    && y < this.heresiarch.level().getMaxBuildHeight()) {
                y++;
            }
            return new Vec3(in.x, y, in.z);
        }

        protected int getCastWarmupTime() {
            return 60;
        }

        private EntityType<?> getRandomCultistType(LivingEntity victim) {
            if (victim instanceof net.minecraft.world.entity.npc.WanderingTrader) {
                return this.heresiarch.getRandom().nextBoolean()
                        ? ModEntityType.MAVERICK_SERVANT.get()
                        : ModEntityType.REPROBATE_SERVANT.get();
            }

            RandomSource random = this.heresiarch.getRandom();
            double rand = random.nextDouble();
            if (rand < 0.4) {
                return ModEntityType.MAVERICK_SERVANT.get();
            } else if (rand < 0.6) {
                return ModEntityType.HERETIC_SERVANT.get();
            } else if (rand < 0.8) {
                return ModEntityType.REPROBATE_SERVANT.get();
            } else if (rand < 0.9) {
                return ModEntityType.WITCH_SERVANT.get();
            } else {
                return ModEntityType.WARLOCK_SERVANT.get();
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}