package com.qiuyue.goetyominous.common.entities.ally.neutral;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.*;
import com.Polarice3.Goety.common.entities.ally.undead.ReaperServant;
import com.Polarice3.Goety.common.entities.ally.undead.WraithServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SunkenSkeletonServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.VanguardServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.BlackguardServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.DrownedServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.common.entities.projectiles.BouncyBubble;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import com.Polarice3.Goety.common.entities.ai.path.ModWaterPathNavigation;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import com.qiuyue.goetyominous.common.items.revive.SunkenSoulJar;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class AbstractSunkenNecromancer extends AbstractNecromancer {
    private boolean searchingForLand;
    protected final ModWaterPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;


    public AbstractSunkenNecromancer(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
        this.moveControl = new MoveHelperController(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.waterNavigation = new ModWaterPathNavigation(this, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new com.Polarice3.Goety.common.entities.ally.Summoned.FollowOwnerWaterGoal(this, 1.0D, 10.0F, 2.0F));
    }

    @Override
    public void projectileGoal(int priority) {
        this.goalSelector.addGoal(priority, new PersistentRangedGoal(this, 1.0D, 20, 10.0F));
    }

    @Override
    public void avoidGoal(int priority) {
    }

    public void summonSpells(int priority) {
        this.goalSelector.addGoal(priority, new SummonServantSpell());
        this.goalSelector.addGoal(priority + 1, new SummonUndeadGoal() {
            @Override
            protected void populateDefaultEquipmentSlots(LivingEntity livingEntity, RandomSource p_217055_) {
            }
        });
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SunkenNecromancerHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.SunkenNecromancerArmor.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.SunkenNecromancerFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SunkenNecromancerDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.SunkenNecromancerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.SunkenNecromancerArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.SunkenNecromancerFollowRange.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.SunkenNecromancerDamage.get());
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.NECROMANCER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return ModSounds.NECROMANCER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.NECROMANCER_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return ModSounds.NECROMANCER_STEP.get();
    }

    @Nullable
    @Override
    public SoundEvent getLaughSound() {
        return ModSounds.NECROMANCER_LAUGH.get();
    }

    public void soulJar() {
        if (this.getTrueOwner() instanceof Player player && MobsConfig.NecromancerSoulJar.get()) {
            Optional<ItemStack> optional = player.getInventory().items.stream().filter(itemStack1 -> itemStack1.is(com.Polarice3.Goety.common.items.ModItems.EMPTY_SOUL_JAR.get())).findFirst();
            if (optional.isPresent()) {
                ItemStack original = optional.get();
                if (original.is(com.Polarice3.Goety.common.items.ModItems.EMPTY_SOUL_JAR.get())) {
                    if (!player.isCreative()) {
                        original.shrink(1);
                    }
                    ItemStack itemStack = new ItemStack(com.qiuyue.goetyominous.common.items.ModItems.SUNKEN_SOUL_JAR.get());
                    SunkenSoulJar.setOwnerName(this.getTrueOwner(), itemStack);
                    SunkenSoulJar.setSummon(this, itemStack);
                    SunkenSoulJar.setSunken(itemStack);
                    SEHelper.addCooldown(player, com.Polarice3.Goety.common.items.ModItems.SOUL_JAR.get(), MathHelper.secondsToTicks(30));
                    SEHelper.addCooldown(player, itemStack.getItem(), MathHelper.secondsToTicks(30));
                    if (!player.getInventory().add(itemStack)) {
                        player.drop(itemStack, false, true);
                    }
                }
            }
        }
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float distanceFactor) {
        if (this.getNecroLevel() < 2) {
            double d0 = target.getX() - this.getX();
            double d1 = target.getY(0.3333333333333333D) - this.getEyeY();
            double d2 = target.getZ() - this.getZ();
            double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
            BouncyBubble bouncyBubble = new BouncyBubble(
                    this.getX(),
                    this.getEyeY() - 0.3D,
                    this.getZ(),
                    d0,
                    d1 + d3 * (double) 0.1F,
                    d2,
                    this.level());
            Vec3 shootVec = new Vec3(d0, d1 + d3 * 0.1F, d2).normalize();
            bouncyBubble.shoot(shootVec);
            bouncyBubble.setOwner(this);
            bouncyBubble.damage = 2 * this.getNecroLevel() + (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            bouncyBubble.setSize(0.6F + this.getNecroLevel() / 4.0F);
            if (this.level().addFreshEntity(bouncyBubble)) {
                this.swing(InteractionHand.MAIN_HAND);
            }
        } else {
            for (int i = -1; i <= 1; i++) {
                Vec3 vector3d = this.getViewVector(1.0F);
                BouncyBubble bouncyBubble = new BouncyBubble(
                        this.getX() + vector3d.x / 2,
                        this.getEyeY() - 0.2D,
                        this.getZ() + vector3d.z / 2,
                        vector3d.x + (i / 10.0F),
                        vector3d.y,
                        vector3d.z + (i / 10.0F),
                        this.level());
                Vec3 shootVec = new Vec3(vector3d.x + (i / 10.0F), vector3d.y, vector3d.z + (i / 10.0F));
                bouncyBubble.shoot(shootVec);
                bouncyBubble.setOwner(this);
                bouncyBubble.damage = 2 * this.getNecroLevel() + (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                bouncyBubble.setSize(0.6F + this.getNecroLevel() / 4.0F);
                if (this.level().addFreshEntity(bouncyBubble)) {
                    this.swing(InteractionHand.MAIN_HAND);
                }
            }
        }
    }

    @Override
    protected boolean isSunSensitive() {
        return true;
    }

    public Summoned getDefaultSummon() {
        return new SunkenSkeletonServant(ModEntityType.SUNKEN_SKELETON_SERVANT.get(), this.level());
    }

    public Summoned getSummon() {
        Summoned summoned = getDefaultSummon();
        boolean hasWaterNearby = this.hasWaterInArea(5);

        if (this.getSummonList().stream().anyMatch(entityType -> entityType == ModEntityType.DROWNED_SERVANT.get())) {
            if (this.level().random.nextBoolean()) {
                summoned = new DrownedServant(ModEntityType.DROWNED_SERVANT.get(), this.level());
            }
        }
        if (hasWaterNearby && this.getSummonList().contains(ModEntityType.SNAPPER.get())) {
            if (this.level().random.nextBoolean()) {
                summoned = new Snapper(ModEntityType.SNAPPER.get(), this.level());
            }
        }
        if (this.getSummonList().contains(ModEntityType.WRAITH_SERVANT.get())) {
            if (this.level().random.nextFloat() <= 0.05F) {
                summoned = new WraithServant(ModEntityType.WRAITH_SERVANT.get(), this.level());
            }
        }
        if (this.getSummonList().contains(ModEntityType.REAPER_SERVANT.get())) {
            if (this.level().random.nextFloat() <= 0.05F) {
                summoned = new ReaperServant(ModEntityType.REAPER_SERVANT.get(), this.level());
            }
        }
        if (hasWaterNearby && this.getSummonList().contains(ModEntityType.GUARDIAN_SERVANT.get())) {
            if (this.level().random.nextFloat() <= 0.05F) {
                summoned = new GuardianServant(ModEntityType.GUARDIAN_SERVANT.get(), this.level());
            }
        }
        if (this.getSummonList().contains(ModEntityType.VANGUARD_SERVANT.get())) {
            if (this.level().random.nextFloat() <= 0.15F) {
                summoned = new VanguardServant(ModEntityType.VANGUARD_SERVANT.get(), this.level());
            }
        }
        if (hasWaterNearby && this.getSummonList().contains(ModEntityType.GNASHER.get())) {
            if (this.level().random.nextFloat() <= 0.05F) {
                summoned = new Gnasher(ModEntityType.GNASHER.get(), this.level());
            }
        }
        if (this.getSummonList().contains(ModEntityType.BLACKGUARD_SERVANT.get())) {
            if (this.level().random.nextFloat() <= 0.05F) {
                summoned = new BlackguardServant(ModEntityType.BLACKGUARD_SERVANT.get(), this.level());
            }
        }
        return summoned;
    }

    private boolean hasWaterInArea(int range) {
        BlockPos centerPos = this.blockPosition();
        int waterCount = 0;
        int totalBlocks = 0;

        for (int x = -range; x <= range; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos checkPos = centerPos.offset(x, y, z);
                    totalBlocks++;
                    if (this.level().getBlockState(checkPos).getFluidState().isSourceOfType(net.minecraft.world.level.material.Fluids.WATER)) {
                        waterCount++;
                    }
                }
            }
        }

        double waterRatio = (double) waterCount / totalBlocks;
        return waterRatio >= 0.3;
    }

    @Override
    public boolean summonVariants() {
        return false;
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level().isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (!this.spawnUndeadIdle() && pHand == InteractionHand.MAIN_HAND && itemstack.isEmpty()) {
                    if (this.idleSpellCool <= 0 && this.getSpellCooldown() <= 0) {
                        this.setUndeadIdle(true);
                    } else {
                        SoundEvent soundEvent = this.getHurtSound(this.damageSources().generic());
                        this.playSound(Objects.requireNonNullElseGet(soundEvent, ModSounds.NECROMANCER_HURT));
                        this.level().broadcastEntityEvent(this, (byte) 9);
                    }
                    return InteractionResult.SUCCESS;
                } else if (item == Items.BONE && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(ModSounds.NECROMANCER_STEP.get(), 1.0F, 1.25F);
                    this.heal(2.0F);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    return InteractionResult.SUCCESS;
                } else if (this.getSummonList().stream().noneMatch(entityType -> entityType == ModEntityType.DROWNED_SERVANT.get()) && item == ModItems.ROTTING_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.addSummon(ModEntityType.DROWNED_SERVANT.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (this.getSummonList().stream().noneMatch(entityType -> entityType == ModEntityType.GUARDIAN_SERVANT.get()) && item == ModItems.GUARDIAN_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.addSummon(ModEntityType.GUARDIAN_SERVANT.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (this.getSummonList().stream().noneMatch(entityType -> entityType == ModEntityType.SNAPPER.get()) && item == ModItems.HUNTING_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.addSummon(ModEntityType.SNAPPER.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (this.getSummonList().stream().noneMatch(entityType -> entityType == ModEntityType.GNASHER.get()) && item == ModItems.MAULING_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.addSummon(ModEntityType.GNASHER.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (this.getSummonList().stream().noneMatch(entityType -> entityType == ModEntityType.SUNKEN_SKELETON_SERVANT.get()) && item == ModItems.OSSEOUS_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.addSummon(ModEntityType.SUNKEN_SKELETON_SERVANT.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (!this.getSummonList().contains(ModEntityType.WRAITH_SERVANT.get()) && item == ModItems.SPOOKY_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.addSummon(ModEntityType.WRAITH_SERVANT.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (!this.getSummonList().contains(ModEntityType.REAPER_SERVANT.get()) && item == ModItems.REAPING_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.addSummon(ModEntityType.REAPER_SERVANT.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (!this.getSummonList().contains(ModEntityType.VANGUARD_SERVANT.get()) && item == ModItems.VANGUARD_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.addSummon(ModEntityType.VANGUARD_SERVANT.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (!this.getSummonList().contains(ModEntityType.BLACKGUARD_SERVANT.get()) && item == ModItems.BLACKGUARD_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.addSummon(ModEntityType.BLACKGUARD_SERVANT.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (item == com.qiuyue.goetyominous.common.items.ModItems.SUNKEN_SOUL_JAR.get()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (this.getNecroLevel() < 2) {
                        this.setNecroLevel(this.getNecroLevel() + 1);
                    }
                    this.heal(AttributesConfig.SunkenNecromancerHealth.get().floatValue());
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }


    public boolean isPushedByFluid() {
        return !this.isSwimming();
    }

    public boolean checkSpawnObstruction(LevelReader pLevel) {
        return pLevel.isUnobstructed(this);
    }

    private boolean wantsToSwim() {
        if (this.searchingForLand) {
            return true;
        } else if (this.getTarget() != null && this.getTarget().isInWater()) {
            return true;
        } else {
            return this.getTrueOwner() != null && this.isFollowing() && (this.getTrueOwner().isInWater() || (this.isInWater() && this.getTrueOwner().getY() > this.getY()));
        }
    }

    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
            this.moveRelative(0.01F, pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
        }
    }

    public void updateSwimming() {
        if (!this.level().isClientSide) {
            if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }
    }

    public boolean isVisuallySwimming() {
        return this.isSwimming();
    }

    protected boolean closeToNextPos() {
        Path path = this.getNavigation().getPath();
        if (path != null) {
            BlockPos blockpos = path.getTarget();
            if (blockpos != null) {
                double d0 = this.distanceToSqr((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
                return d0 < 4.0D;
            }
        }
        return false;
    }

    public void setSearchingForLand(boolean searchingForLand) {
        this.searchingForLand = searchingForLand;
    }

    public class SummonServantSpell extends SummoningSpellGoal {

        public boolean canUse() {
            Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof IOwned owned && owned.getTrueOwner() == AbstractSunkenNecromancer.this;
            int i = AbstractSunkenNecromancer.this.level().getEntitiesOfClass(LivingEntity.class, AbstractSunkenNecromancer.this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D)
                    , predicate).size();
            return super.canUse() && i < 6;
        }

        protected void castSpell(){
            if (AbstractSunkenNecromancer.this.level() instanceof ServerLevel serverLevel) {
                for (int i1 = 0; i1 < 2; ++i1) {
                    Summoned summonedentity = AbstractSunkenNecromancer.this.getSummon();
                    BlockPos blockPos = BlockFinder.SummonRadius(AbstractSunkenNecromancer.this.blockPosition(), summonedentity, serverLevel);
                    summonedentity.setTrueOwner(AbstractSunkenNecromancer.this);
                    summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                    MobUtil.moveDownToGround(summonedentity);
                    if (MobsConfig.NecromancerSummonsLife.get()) {
                        summonedentity.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
                    }
                    summonedentity.setPersistenceRequired();
                    summonedentity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(AbstractSunkenNecromancer.this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    if (serverLevel.addFreshEntity(summonedentity)){
                        SoundUtil.playNecromancerSummon(summonedentity);
                        ServerParticleUtil.summonUndeadParticles(serverLevel, summonedentity);
                    }
                }
            }
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return ModSounds.PREPARE_SUMMON.get();
        }

        @Override
        protected NecromancerSpellType getNecromancerSpellType() {
            return NecromancerSpellType.ZOMBIE;
        }

        protected void playLaughSound() {
            AbstractSunkenNecromancer.this.playSound(ModSounds.NECROMANCER_LAUGH.get(), 2.0F, AbstractSunkenNecromancer.this.getVoicePitch() - 0.5F);
        }
    }

    static class MoveHelperController extends MoveControl {
        private final AbstractSunkenNecromancer sunkenNecromancer;

        public MoveHelperController(AbstractSunkenNecromancer sunkenNecromancer) {
            super(sunkenNecromancer);
            this.sunkenNecromancer = sunkenNecromancer;
        }

        public void tick() {
            LivingEntity livingentity = this.sunkenNecromancer.getTarget();
            LivingEntity owner = this.sunkenNecromancer.getTrueOwner();
            if (this.sunkenNecromancer.wantsToSwim() && this.sunkenNecromancer.isInWater()) {
                if ((livingentity != null && livingentity.getY() > this.sunkenNecromancer.getY())
                        || this.sunkenNecromancer.searchingForLand
                        || (owner != null && owner.getY() > this.sunkenNecromancer.getY() && this.sunkenNecromancer.isFollowing())) {
                    this.sunkenNecromancer.setDeltaMovement(this.sunkenNecromancer.getDeltaMovement().add(0.0D, 0.003D, 0.0D));
                }

                if (this.operation != Operation.MOVE_TO || this.sunkenNecromancer.getNavigation().isDone()) {
                    this.sunkenNecromancer.setSpeed(0.0F);
                    return;
                }

                double d0 = this.wantedX - this.sunkenNecromancer.getX();
                double d1 = this.wantedY - this.sunkenNecromancer.getY();
                double d2 = this.wantedZ - this.sunkenNecromancer.getZ();
                double d3 = Mth.sqrt((float) (d0 * d0 + d1 * d1 + d2 * d2));
                d1 = d1 / d3;
                float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.sunkenNecromancer.setYRot(this.rotlerp(this.sunkenNecromancer.getYRot(), f, 90.0F));
                this.sunkenNecromancer.yBodyRot = this.sunkenNecromancer.getYRot();
                float f1 = (float)(this.speedModifier * this.sunkenNecromancer.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float f2 = Mth.lerp(0.125F, this.sunkenNecromancer.getSpeed(), f1);
                this.sunkenNecromancer.setSpeed(f2);
                this.sunkenNecromancer.setDeltaMovement(this.sunkenNecromancer.getDeltaMovement().add((double)f2 * d0 * 0.008D, (double)f2 * d1 * 0.15D, (double)f2 * d2 * 0.008D));
            } else {
                if (!this.sunkenNecromancer.onGround()) {
                    this.sunkenNecromancer.setDeltaMovement(this.sunkenNecromancer.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }

                super.tick();
            }
        }
    }

    public static class PersistentRangedGoal extends Goal {
        private final AbstractSunkenNecromancer mob;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private int seeTime;
        private final int attackInterval;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public PersistentRangedGoal(AbstractSunkenNecromancer mob, double speed, int attackInterval, float attackRadius) {
            this.mob = mob;
            this.speedModifier = speed;
            this.attackInterval = attackInterval;
            this.attackRadius = attackRadius;
            this.attackRadiusSqr = attackRadius * attackRadius;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return !this.mob.isSpellCasting();
            } else {
                return false;
            }
        }

        public boolean canContinueToUse() {
            return this.canUse() || (this.target != null && this.target.isAlive() && !this.mob.getNavigation().isDone() && !this.mob.isSpellCasting());
        }

        public void stop() {
            this.mob.setShooting(false);
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target != null && !this.mob.isSpellCasting()) {
                double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
                boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
                if (flag) {
                    ++this.seeTime;
                } else {
                    this.seeTime = 0;
                }

                if (d0 > (double) this.attackRadiusSqr) {
                    this.mob.getNavigation().moveTo(this.target, this.speedModifier);
                } else {
                    this.mob.getNavigation().stop();
                }

                int speed = Mth.floor(Math.max(this.mob.getAttackSpeed(), 1.0F));
                this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                int attackIntervalMin = this.attackInterval / speed;
                --this.attackTime;
                if (this.attackTime <= 5) {
                    this.mob.setShooting(true);
                    if (this.mob.getCurrentAnimation() != this.mob.getAnimationState(ATTACK)) {
                        this.mob.setAnimationState(ATTACK);
                    }
                }
                if (this.attackTime == 0) {
                    if (!flag) {
                        return;
                    }

                    float f = (float) Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    this.mob.performRangedAttack(this.target, f1);
                    this.attackTime = attackIntervalMin;
                } else if (this.attackTime < 0) {
                    this.mob.setShooting(false);
                    this.attackTime = attackIntervalMin;
                }
            }
        }
    }
}
