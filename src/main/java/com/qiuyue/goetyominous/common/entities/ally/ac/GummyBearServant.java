package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.entity.living.SweetishFishEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.GummyColors;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.EnumSet;

public class GummyBearServant extends AnimalSummon implements IAnimatedEntity {

    private static final EntityDataAccessor<Integer> GUMMY_COLOR = SynchedEntityData.defineId(GummyBearServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(GummyBearServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(GummyBearServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(GummyBearServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DIGESTING = SynchedEntityData.defineId(GummyBearServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> STOMACH_RED = SynchedEntityData.defineId(GummyBearServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> STOMACH_GREEN = SynchedEntityData.defineId(GummyBearServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> STOMACH_BLUE = SynchedEntityData.defineId(GummyBearServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> HELD_MOB_ID = SynchedEntityData.defineId(GummyBearServant.class, EntityDataSerializers.INT);

    public static final Animation ANIMATION_FISH = Animation.create(35);
    public static final Animation ANIMATION_EAT = Animation.create(40);
    public static final Animation ANIMATION_BACKSCRATCH = Animation.create(90);
    public static final Animation ANIMATION_MAUL = Animation.create(25);
    public static final Animation ANIMATION_SWIPE = Animation.create(25);

    private Animation currentAnimation;
    private int animationTick;
    private float prevSitProgress;
    private float sitProgress;
    private float prevStandProgress;
    private float standProgress;
    private float prevDanceProgress;
    private float danceProgress;
    private float prevSleepProgress;
    private float sleepProgress;
    private float prevStomachAlpha;
    private float stomachAlpha;
    public boolean lookForTheGummyBearAlbumInStoresOnNovember13th = checkNovember13th();
    private ResourceLocation digestingEffect;
    private int standFor = 0;
    private int sitFor = 0;
    private int sleepFor = 0;
    private int jellybeansToMake = 0;

    public GummyBearServant(EntityType<? extends Owned> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(GUMMY_COLOR, GummyColors.RED.ordinal());
        this.entityData.define(SITTING, false);
        this.entityData.define(STANDING, false);
        this.entityData.define(SLEEPING, false);
        this.entityData.define(DIGESTING, false);
        this.entityData.define(STOMACH_RED, 0.0F);
        this.entityData.define(STOMACH_GREEN, 0.0F);
        this.entityData.define(STOMACH_BLUE, 0.0F);
        this.entityData.define(HELD_MOB_ID, -1);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.GummyBearServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.GummyBearServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.GummyBearServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.GummyBearServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.GummyBearServantKnockbackResistance.get())
                .add(Attributes.ARMOR, AttributesConfig.GummyBearServantArmor.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // AnimalSummon 繁殖:同主人、双方发情(BreedGoal 内部再经 canMate 过滤)。优先级 1,高于
        // 坐下/近战/闲逛,发情期优先朝伴侣走位(Summoned 的 FollowOwnerGoal 在 5,让位给繁殖)。
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new SitGoal());
        this.goalSelector.addGoal(3, new GummyBearMeleeGoal());
        // 用 Goety 的 WanderGoal(checkNoActionTime=false)游荡:AnimalSummon/Summoned 覆写 checkDespawn 后 noActionTime 永不复位,
        // 原版 RandomStrollGoal 空闲约 5 秒即 noActionTime>=100 被永久禁用而站桩不动;WanderGoal 落点限定在主人附近。
        this.goalSelector.addGoal(4, new Summoned.WanderGoal<>(this, 1.0D, 45, 0.001F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        // 主动猎鱼:对同色 SweetishFish(原版 GummyBearEntity 行为),幼体不猎鱼。优先级 3,低于继承的
        // OwnerHurtByTarget/ServantHurtBy/SummonTarget(1) 与 OwnerHurtTarget(2),即只在闲时选鱼。
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, SweetishFishEntity.class, 100, true, false,
                livingEntity -> livingEntity instanceof SweetishFishEntity sweetishFish && sweetishFish.getGummyColor() == this.getGummyColor() && !GummyBearServant.this.isBaby()));
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag tag) {
        if (this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.GummyBearServantLimit.get()) {
                this.discard();
                return null;
            }
        }
        this.setGummyColor(GummyColors.getRandom(this.random, true));
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnGroupData, tag);
    }

    /**
     * 该玩家名下当前在场的软糖熊仆从总数(含幼体)。
     * 注:finalizeSpawn 调用时本实体尚未入世界,故此处"总数"等价于"已有数",
     * spawn egg 校验与繁殖校验(canMate/getBreedOffspring)可共用同一套口径:总数到上限即不再放行。
     */
    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof GummyBearServant servant && servant.getTrueOwner() == player) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    public GummyColors getGummyColor() {
        return GummyColors.fromOrdinal(this.entityData.get(GUMMY_COLOR));
    }

    public void setGummyColor(GummyColors color) {
        this.entityData.set(GUMMY_COLOR, color.ordinal());
    }

    public float getStomachRed() {
        return this.entityData.get(STOMACH_RED);
    }

    public void setStomachRed(float stomachRed) {
        this.entityData.set(STOMACH_RED, stomachRed);
    }

    public float getStomachGreen() {
        return this.entityData.get(STOMACH_GREEN);
    }

    public void setStomachGreen(float stomachGreen) {
        this.entityData.set(STOMACH_GREEN, stomachGreen);
    }

    public float getStomachBlue() {
        return this.entityData.get(STOMACH_BLUE);
    }

    public void setStomachBlue(float stomachBlue) {
        this.entityData.set(STOMACH_BLUE, stomachBlue);
    }

    public float getStomachAlpha(float partialTicks) {
        return prevStomachAlpha + (stomachAlpha - prevStomachAlpha) * partialTicks;
    }

    public boolean digestEffect(Potion potion) {
        this.digestingEffect = ForgeRegistries.POTIONS.getKey(potion);
        this.updateDigestionColors();
        return this.digestingEffect != null;
    }

    private void updateDigestionColors() {
        Potion potion = ForgeRegistries.POTIONS.getValue(digestingEffect);
        if (potion != null) {
            int colorizer = PotionUtils.getColor(potion);
            if (colorizer != -1) {
                float f = (float) (colorizer >> 16 & 255) / 255.0F;
                float f1 = (float) (colorizer >> 8 & 255) / 255.0F;
                float f2 = (float) (colorizer & 255) / 255.0F;
                this.setStomachRed(f);
                this.setStomachGreen(f1);
                this.setStomachBlue(f2);
            }
        }
    }

    public boolean isDigestiblePotion(ItemStack itemStack) {
        if (itemStack.is(Items.POTION)) {
            Potion potion = PotionUtils.getPotion(itemStack);
            return !potion.hasInstantEffects() && !potion.getEffects().isEmpty();
        }
        return false;
    }

    public ItemStack createJellybean() {
        Potion potion = ForgeRegistries.POTIONS.getValue(digestingEffect);
        return potion == null ? new ItemStack(ACItemRegistry.JELLY_BEAN.get()) : ACEffectRegistry.createJellybean(potion);
    }

    // ---- 繁殖(恢复原版 GummyBearEntity 机制,受 AnimalSummon 控制)----
    // 食物 = 与自身同色的甜味鱼(喂食由 AnimalSummon.mobInteract 处理:成体发情、幼体催长)。
    @Override
    public boolean isFood(ItemStack stack) {
        Item fishItem = ACItemRegistry.SWEETISH_FISH_RED.get();
        switch (this.getGummyColor()) {
            case GREEN:
                fishItem = ACItemRegistry.SWEETISH_FISH_GREEN.get();
                break;
            case BLUE:
                fishItem = ACItemRegistry.SWEETISH_FISH_BLUE.get();
                break;
            case YELLOW:
                fishItem = ACItemRegistry.SWEETISH_FISH_YELLOW.get();
                break;
            case PINK:
                fishItem = ACItemRegistry.SWEETISH_FISH_PINK.get();
                break;
            case RED:
            default:
                fishItem = ACItemRegistry.SWEETISH_FISH_RED.get();
        }
        return stack.is(fishItem);
    }

    // 同主人约束:BreedGoal 只保证同类+双方发情,这里额外要求两位仆从属于同一主人;
    // 且主人名下数量已达上限时不配对(#1:繁殖同样遵守 MobsConfig.GummyBearServantLimit)。
    @Override
    public boolean canMate(AnimalSummon other) {
        LivingEntity owner = this.getTrueOwner();
        if (!super.canMate(other) || owner == null || owner != other.getTrueOwner()) {
            return false;
        }
        return !(owner instanceof Player player
                && countServants(player) >= MobsConfig.GummyBearServantLimit.get());
    }

    // 后代颜色继承召唤方;AnimalSummon.spawnChildFromBreeding 内部会 copyTrueOwner,故幼体自动归属主人。
    // 二次防线:若两对同时各自配对、先后产仔,这里按实时数量兜底,已达上限则放弃本次产仔(双方发情自然耗散)。
    @Override
    @Nullable
    public AnimalSummon getBreedOffspring(ServerLevel level, AnimalSummon otherParent) {
        LivingEntity owner = this.getTrueOwner();
        if (owner instanceof Player player
                && countServants(player) >= MobsConfig.GummyBearServantLimit.get()) {
            return null;
        }
        AnimalSummon offspring = super.getBreedOffspring(level, otherParent);
        if (offspring instanceof GummyBearServant baby) {
            baby.setGummyColor(this.getGummyColor());
        }
        return offspring;
    }

    // 幼体碰撞盒随体型同步缩小(#4),与模型 young 缩放一致;照 TusklinServant 的做法。
    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.getType().getDimensions().scale(this.isBaby() ? 0.5F : 1.0F);
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        this.refreshDimensions();
    }

    public boolean isDancing() {
        return false;
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    public void setSitting(boolean bool) {
        this.entityData.set(SITTING, bool);
    }

    public boolean isStanding() {
        return this.entityData.get(STANDING);
    }

    public void setStanding(boolean bool) {
        this.entityData.set(STANDING, bool);
    }

    public boolean isBearSleeping() {
        return this.entityData.get(SLEEPING);
    }

    public void setBearSleeping(boolean bool) {
        this.entityData.set(SLEEPING, bool);
    }

    public boolean isDigesting() {
        return this.entityData.get(DIGESTING);
    }

    public void setDigesting(boolean bool) {
        this.entityData.set(DIGESTING, bool);
    }

    public void setHeldMobId(int i) {
        this.entityData.set(HELD_MOB_ID, i);
    }

    public int getHeldMobId() {
        return this.entityData.get(HELD_MOB_ID);
    }

    public Entity getHeldMob() {
        int id = this.getHeldMobId();
        return id == -1 ? null : this.level().getEntity(id);
    }

    @Override
    public int getAnimationTick() {
        return this.animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        this.animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return this.currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        if (this.getAnimation() != animation) {
            boolean sitting = this.isSittingAnimation();
            boolean standing = this.isStandingAnimation();
            this.currentAnimation = animation;
            this.animationTick = 0;
            if (sitting) {
                this.sitFor += animation.getDuration();
            }
            if (standing) {
                this.standFor += animation.getDuration();
            }
        }
    }

    public void syncAnimation(Animation animation) {
        if (this.level().isClientSide) {
            this.setAnimation(animation);
        } else {
            AnimationHandler.INSTANCE.sendAnimationMessage(this, animation);
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_FISH, ANIMATION_EAT, ANIMATION_BACKSCRATCH, ANIMATION_MAUL, ANIMATION_SWIPE};
    }

    private static boolean checkNovember13th() {
        LocalDate localdate = LocalDate.now();
        int i = localdate.get(ChronoField.DAY_OF_MONTH);
        int j = localdate.get(ChronoField.MONTH_OF_YEAR);
        return j == 11 && i > 12 && i < 15;
    }

    public float getSitProgress(float partialTicks) {
        return (prevSitProgress + (sitProgress - prevSitProgress) * partialTicks) * 0.1F;
    }

    public float getStandProgress(float partialTicks) {
        return (prevStandProgress + (standProgress - prevStandProgress) * partialTicks) * 0.1F;
    }

    public float getDanceProgress(float partialTicks) {
        return (prevDanceProgress + (danceProgress - prevDanceProgress) * partialTicks) * 0.2F;
    }

    public float getSleepProgress(float partialTicks) {
        return (prevSleepProgress + (sleepProgress - prevSleepProgress) * partialTicks) * 0.1F;
    }

    @Override
    public void tick() {
        super.tick();
        this.prevSitProgress = this.sitProgress;
        this.prevStandProgress = this.standProgress;
        this.prevDanceProgress = this.danceProgress;
        this.prevSleepProgress = this.sleepProgress;
        this.prevStomachAlpha = this.stomachAlpha;
        if (this.isSitting() && this.sitProgress < 10.0F) {
            this.sitProgress++;
        }
        if (!this.isSitting() && this.sitProgress > 0.0F) {
            this.sitProgress--;
        }
        if (this.isStanding() && this.standProgress < 10.0F) {
            this.standProgress++;
        }
        if (!this.isStanding() && this.standProgress > 0.0F) {
            this.standProgress--;
        }
        if (this.isBearSleeping() && this.sleepProgress < 10.0F) {
            this.sleepProgress++;
        }
        if (!this.isBearSleeping() && this.sleepProgress > 0.0F) {
            this.sleepProgress--;
        }
        if (this.isDigesting()) {
            this.stomachAlpha = Mth.approach(this.stomachAlpha, 1.0F, 0.05F);
        } else {
            this.stomachAlpha = Mth.approach(this.stomachAlpha, 0.0F, 0.05F);
            if (this.stomachAlpha <= 0.0F && this.digestingEffect != null) {
                this.digestingEffect = null;
            }
        }
        if (this.level().isClientSide) {
            if (this.isBearSleeping()) {
                int sleepDiv = this.tickCount % 50;
                if (sleepDiv == 2 || sleepDiv == 10 || sleepDiv == 18) {
                    Vec3 headPos = this.getEyePosition().add(new Vec3(0.2F, -0.4F, 1.2F).yRot(-this.yBodyRot * ((float) Math.PI / 180F)));
                    this.level().addParticle(ACParticleRegistry.SLEEP.get(), headPos.x, headPos.y, headPos.z, 0, 0.1F, 0);
                }
            }
        } else {
            int animationDurationLeft = this.getAnimation() == null ? 0 : this.getAnimation().getDuration() - this.getAnimationTick();
            if (this.isSittingAnimation() && this.sitFor < animationDurationLeft) {
                this.sitFor = animationDurationLeft;
            }
            if (this.isStandingAnimation() && this.standFor < animationDurationLeft) {
                this.standFor = animationDurationLeft;
            }
            if (this.sleepFor > 0) {
                this.setStanding(false);
                this.setSitting(false);
                this.setBearSleeping(true);
                this.sleepFor--;
            } else {
                this.setBearSleeping(false);
                if (this.sitFor > 0) {
                    this.sitFor--;
                    this.setSitting(true);
                } else {
                    this.setSitting(false);
                    if (this.standFor > 0) {
                        this.standFor--;
                        this.setStanding(true);
                    } else {
                        this.setStanding(false);
                    }
                }
            }
            if (this.getAnimation() == ANIMATION_BACKSCRATCH) {
                if (this.getAnimationTick() % 15 == 0 && this.getAnimationTick() > 0) {
                    if (this.jellybeansToMake > 0) {
                        this.spawnAtLocation(this.createJellybean(), 1.0F + this.random.nextFloat());
                        this.jellybeansToMake--;
                    }
                }
                if (this.jellybeansToMake <= 0 || this.getAnimationTick() > 85) {
                    this.setDigesting(false);
                }
            }
            this.maybeStartBackScratch();
        }
        Entity heldMob = this.getHeldMob();
        if (heldMob != null && heldMob.isAlive() && heldMob.distanceTo(this) < 10) {
            Vec3 heldPos = this.getEyePosition().add(new Vec3(0.0F, 0.25F, 0.5F).yRot(-this.yBodyRot * ((float) Math.PI / 180F)));
            Vec3 minus = new Vec3(heldPos.x - heldMob.getX(), heldPos.y - heldMob.getY(), heldPos.z - heldMob.getZ());
            heldMob.setDeltaMovement(minus);
            heldMob.fallDistance = 0.0F;
            heldMob.setYRot(0.0F);
            heldMob.setYBodyRot(0.0F);
            heldMob.setYHeadRot(0.0F);
            heldMob.setXRot(0.0F);
            heldMob.setAirSupply(40);
            if (this.getAnimation() != ANIMATION_EAT) {
                this.setAnimation(ANIMATION_EAT);
            }
            if (this.tickCount % 15 == 0) {
                heldMob.hurt(this.damageSources().mobAttack(this), this.random.nextBoolean() ? 0.0F : 1.0F);
            }
        } else if (!this.level().isClientSide) {
            this.setHeldMobId(-1);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private void maybeStartBackScratch() {
        if (this.isDigesting() && !this.isBearSleeping() && this.getTarget() == null
                && this.getAnimation() == IAnimatedEntity.NO_ANIMATION && this.getNavigation().isDone()
                && !this.isSitting() && !this.isStanding()) {
            this.syncAnimation(ANIMATION_BACKSCRATCH);
        }
    }

    public boolean isStandingAnimation() {
        return this.getAnimation() == ANIMATION_BACKSCRATCH || this.getAnimation() == ANIMATION_MAUL;
    }

    public boolean isSittingAnimation() {
        return this.getAnimation() == ANIMATION_EAT;
    }

    public boolean isMovementBlocked() {
        return this.isSitting() || this.isBearSleeping() || this.getAnimation() == ANIMATION_BACKSCRATCH;
    }

    @Override
    public boolean isPushable() {
        return this.getHeldMobId() == -1;
    }

    @Override
    public void travel(Vec3 vec3d) {
        if (this.isSitting() || this.isMovementBlocked()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            vec3d = Vec3.ZERO;
        }
        super.travel(vec3d);
    }

    public boolean isSleepy() {
        return this.sleepFor > 0;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        InteractionResult prev = super.mobInteract(player, hand);
        if (prev != InteractionResult.SUCCESS) {
            ItemStack itemStack = player.getItemInHand(hand);
            // 药水消化同样限主人喂(#2,与喂鱼发情口径一致):无主(野生)熊任何玩家可喂;
            // 用 getOwnerId 判断"是否有主",避免主人在异维度时把仆从误当野生放给外人喂。
            if (this.isDigestiblePotion(itemStack) && !this.isDigesting()
                    && (this.getOwnerId() == null || player == this.getTrueOwner())) {
                if (!this.level().isClientSide) {
                    this.digestEffect(PotionUtils.getPotion(itemStack));
                    this.setDigesting(true);
                    this.sleepFor = 1200;
                    this.jellybeansToMake = this.random.nextInt(2) + 3;
                    this.playSound(ACSoundRegistry.GUMMY_BEAR_EAT.get(), this.getSoundVolume(), this.getVoicePitch());
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                        player.addItem(new ItemStack(Items.GLASS_BOTTLE));
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return prev;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }

    @Override
    public int getAmbientSoundInterval() {
        return this.isBearSleeping() ? 45 : 80;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isBearSleeping() ? ACSoundRegistry.GUMMY_BEAR_SNORE.get() : ACSoundRegistry.GUMMY_BEAR_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.GUMMY_BEAR_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.GUMMY_BEAR_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(ACSoundRegistry.GUMMY_BEAR_STEP.get(), 0.3F, this.getVoicePitch());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("GummyColor", this.getGummyColor().ordinal());
        if (this.digestingEffect != null) {
            compound.putString("DigestingEffect", this.digestingEffect.toString());
            compound.putInt("JellyBeansToMake", this.jellybeansToMake);
        }
        compound.putBoolean("BearSleeping", this.isBearSleeping());
        compound.putBoolean("BearSitting", this.isSitting());
        compound.putInt("SleepTime", this.sleepFor);
        compound.putInt("SitTime", this.sitFor);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setGummyColor(GummyColors.fromOrdinal(compound.getInt("GummyColor")));
        if (compound.contains("DigestingEffect")) {
            ResourceLocation effect = ResourceLocation.tryParse(compound.getString("DigestingEffect"));
            if (effect != null) {
                this.digestEffect(ForgeRegistries.POTIONS.getValue(effect));
                this.setDigesting(this.digestingEffect != null);
                this.jellybeansToMake = compound.getInt("JellyBeansToMake");
            }
        }
        this.setBearSleeping(compound.getBoolean("BearSleeping"));
        this.setStanding(compound.getBoolean("BearSitting"));
        this.sleepFor = compound.getInt("SleepTime");
        this.sitFor = compound.getInt("SitTime");
    }

    @Override
    public void lookAt(EntityAnchorArgument.Anchor anchor, Vec3 vec3) {
        super.lookAt(anchor, vec3);
    }

    private class SitGoal extends Goal {

        public SitGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canContinueToUse() {
            return GummyBearServant.this.isMovementBlocked();
        }

        @Override
        public boolean canUse() {
            return !GummyBearServant.this.isInWaterOrBubble() && GummyBearServant.this.isMovementBlocked();
        }

        @Override
        public void start() {
            GummyBearServant.this.getNavigation().stop();
        }
    }

    private class GummyBearMeleeGoal extends Goal {

        public GummyBearMeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = GummyBearServant.this.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void stop() {
            GummyBearServant.this.setSprinting(false);
        }

        @Override
        public void tick() {
            LivingEntity target = GummyBearServant.this.getTarget();
            if (target != null && target.isAlive()) {
                double distance = GummyBearServant.this.distanceTo(target);
                double attackDistance = GummyBearServant.this.getBbWidth() + target.getBbWidth() + 1.0;
                GummyBearServant.this.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                if (target instanceof SweetishFishEntity) {
                    if (distance >= 5.0 && GummyBearServant.this.getHeldMobId() == -1) {
                        GummyBearServant.this.getNavigation().moveTo(target, 1.3);
                    } else {
                        GummyBearServant.this.getNavigation().stop();
                    }
                    if (distance < 5.0 && GummyBearServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION && GummyBearServant.this.getHeldMobId() == -1) {
                        if (distance <= 1.0) {
                            GummyBearServant.this.syncAnimation(ANIMATION_EAT);
                        } else {
                            GummyBearServant.this.syncAnimation(ANIMATION_FISH);
                        }
                    }
                    if (GummyBearServant.this.getAnimation() == ANIMATION_FISH) {
                        if (GummyBearServant.this.getAnimationTick() == 16) {
                            GummyBearServant.this.setDeltaMovement(GummyBearServant.this.getDeltaMovement().add(0.0, 0.3F, 0.0));
                        }
                        if (GummyBearServant.this.getAnimationTick() > 15 && GummyBearServant.this.getAnimationTick() <= 20) {
                            Vec3 delta = target.position().subtract(GummyBearServant.this.position());
                            if (delta.length() > 1.0) {
                                delta = delta.normalize();
                            }
                            GummyBearServant.this.setDeltaMovement(GummyBearServant.this.getDeltaMovement().add(delta.scale(0.3F)));
                        }
                        if (GummyBearServant.this.getAnimationTick() > 16 && GummyBearServant.this.getAnimationTick() < 25 && distance < 1.5) {
                            GummyBearServant.this.setHeldMobId(target.getId());
                        }
                    }
                } else if (distance < attackDistance && GummyBearServant.this.hasLineOfSight(target)) {
                    if (GummyBearServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                        GummyBearServant.this.syncAnimation(GummyBearServant.this.getRandom().nextBoolean() ? ANIMATION_MAUL : ANIMATION_SWIPE);
                    }
                    if (GummyBearServant.this.getAnimation() == ANIMATION_MAUL && (GummyBearServant.this.getAnimationTick() > 5 && GummyBearServant.this.getAnimationTick() <= 7 || GummyBearServant.this.getAnimationTick() > 17 && GummyBearServant.this.getAnimationTick() <= 19)) {
                        this.checkAndDealDamage(target);
                    }
                    if (GummyBearServant.this.getAnimation() == ANIMATION_SWIPE && (GummyBearServant.this.getAnimationTick() > 5 && GummyBearServant.this.getAnimationTick() <= 7 || GummyBearServant.this.getAnimationTick() > 15 && GummyBearServant.this.getAnimationTick() <= 17)) {
                        this.checkAndDealDamage(target);
                    }
                } else {
                    GummyBearServant.this.getNavigation().moveTo(target, 1.3);
                    GummyBearServant.this.setSprinting(true);
                }
            }
        }

        private void checkAndDealDamage(LivingEntity target) {
            if (GummyBearServant.this.hasLineOfSight(target) && GummyBearServant.this.distanceTo(target) < GummyBearServant.this.getBbWidth() + target.getBbWidth() + 1.0F) {
                target.hurt(target.damageSources().mobAttack(GummyBearServant.this), (float) GummyBearServant.this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            }
        }
    }
}
