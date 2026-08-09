package com.qiuyue.goetyominous.common.entities.hostile.illagers;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.golem.RedstoneMinistrosity;
import com.Polarice3.Goety.common.entities.hostile.illagers.HuntingIllagerEntity;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.neutral.TotemicBomb;
import com.Polarice3.Goety.common.entities.projectiles.SmackStone;
import com.Polarice3.Goety.common.entities.util.ModFallingBlock;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.geomancy.EruptionSpell;
import com.Polarice3.Goety.common.network.ModServerBossInfo;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.qiuyue.goetyominous.common.entities.projectile.ImpactBlockEntity;
import com.qiuyue.goetyominous.common.entities.projectile.TremorBlockEntity;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class ArchGeomancerEntity extends HuntingIllagerEntity {
    private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(ArchGeomancerEntity.class,
            EntityDataSerializers.INT);
    private static final int ANIM_ATTACK = 1;
    private static final int ANIM_SUMMON = 2;
    private static final int ANIM_BIG_ATTACK = 3;
    private static final int ANIM_SPELL_ATTACK = 4;
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState walkAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState summonAnimationState = new AnimationState();
    public AnimationState bigAttackAnimationState = new AnimationState();
    public AnimationState spellAttackAnimationState = new AnimationState();
    public int summonCool;
    public int whirlwindCool;
    public int quakeCool;
    public int barrageCool;
    public int rangedCool;
    private int quakeCastTime;
    private List<BlockPos> cachedBarrageBlocks = new ArrayList<>();
    private int lastBarrageCheck;
    private final ModServerBossInfo bossInfo;

    public ArchGeomancerEntity(EntityType<? extends HuntingIllagerEntity> type, Level level) {
        super(type, level);
        this.xpReward = 40;
        this.bossInfo = new ModServerBossInfo(this, BossEvent.BossBarColor.YELLOW, false, false);
        this.setPersistenceRequired();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(2, new WhirlwindGoal());
        this.goalSelector.addGoal(3, new SummonGoal());
        this.goalSelector.addGoal(4, new QuakeGoal());
        this.goalSelector.addGoal(5, new BarrageGoal());
        this.goalSelector.addGoal(6, new RangedGoal());
        this.goalSelector.addGoal(7, new MoveTowardsTargetGoal(this, 1.0D, 13.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ArchGeomancerHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.ArchGeomancerArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.ArchGeomancerArmorToughness.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.ArchGeomancerMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ArchGeomancerAttackDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.ArchGeomancerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.ArchGeomancerArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR_TOUGHNESS),
                AttributesConfig.ArchGeomancerArmorToughness.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MOVEMENT_SPEED),
                AttributesConfig.ArchGeomancerMovementSpeed.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE),
                AttributesConfig.ArchGeomancerAttackDamage.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANIM_STATE, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SummonCool", this.summonCool);
        compound.putInt("WhirlwindCool", this.whirlwindCool);
        compound.putInt("QuakeCool", this.quakeCool);
        compound.putInt("BarrageCool", this.barrageCool);
        compound.putInt("RangedCool", this.rangedCool);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("SummonCool")) {
            this.summonCool = compound.getInt("SummonCool");
        }
        if (compound.contains("WhirlwindCool")) {
            this.whirlwindCool = compound.getInt("WhirlwindCool");
        }
        if (compound.contains("QuakeCool")) {
            this.quakeCool = compound.getInt("QuakeCool");
        }
        if (compound.contains("BarrageCool")) {
            this.barrageCool = compound.getInt("BarrageCool");
        }
        if (compound.contains("RangedCool")) {
            this.rangedCool = compound.getInt("RangedCool");
        }
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationStateId(input));
    }

    public int getAnimationStateId(String animation) {
        if (Objects.equals(animation, "attack")) {
            return ANIM_ATTACK;
        } else if (Objects.equals(animation, "summon")) {
            return ANIM_SUMMON;
        } else if (Objects.equals(animation, "big_attack")) {
            return ANIM_BIG_ATTACK;
        } else if (Objects.equals(animation, "spell_attack")) {
            return ANIM_SPELL_ATTACK;
        } else {
            return 0;
        }
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getCurrentAnimation() {
        return this.entityData.get(ANIM_STATE);
    }

    public List<AnimationState> getAllAnimations() {
        List<AnimationState> list = new ArrayList<>();
        list.add(this.idleAnimationState);
        list.add(this.walkAnimationState);
        list.add(this.attackAnimationState);
        list.add(this.summonAnimationState);
        list.add(this.bigAttackAnimationState);
        list.add(this.spellAttackAnimationState);
        return list;
    }

    public void stopMostAnimation(AnimationState exception) {
        for (AnimationState state : this.getAllAnimations()) {
            if (state != exception) {
                state.stop();
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ANIM_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.entityData.get(ANIM_STATE)) {
                case ANIM_ATTACK -> {
                    this.attackAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.attackAnimationState);
                }
                case ANIM_SUMMON -> {
                    this.summonAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.summonAnimationState);
                }
                case ANIM_BIG_ATTACK -> {
                    this.bigAttackAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.bigAttackAnimationState);
                }
                case ANIM_SPELL_ATTACK -> {
                    this.spellAttackAnimationState.start(this.tickCount);
                    this.stopMostAnimation(this.spellAttackAnimationState);
                }
                default -> this.stopMostAnimation(null);
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            if (this.walkAnimation.isMoving() || this.getCurrentAnimation() != 0) {
                this.idleAnimationState.stop();
            } else {
                this.idleAnimationState.animateWhen(this.getCurrentAnimation() == 0, this.tickCount);
            }
        }
        super.tick();
        if (!this.level().isClientSide) {
            this.bossInfo.setVisible(this.getTarget() != null);
            if (this.tickCount % 5 == 0) {
                this.bossInfo.update();
            }
            this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
            if (this.summonCool > 0) {
                --this.summonCool;
            }
            if (this.whirlwindCool > 0) {
                --this.whirlwindCool;
            }
            if (this.quakeCool > 0) {
                --this.quakeCool;
            }
            if (this.barrageCool > 0) {
                --this.barrageCool;
            }
            if (this.rangedCool > 0) {
                --this.rangedCool;
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GEOMANCER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.GEOMANCER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GEOMANCER_DEATH.get();
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.GEOMANCER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return null;
    }

    @Override
    protected float getStandingEyeHeight(net.minecraft.world.entity.Pose pose,
            net.minecraft.world.entity.EntityDimensions dimensions) {
        return 1.62F;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer pPlayer) {
        super.startSeenByPlayer(pPlayer);
        if (MainConfig.SpecialBossBar.get()) {
            this.bossInfo.addPlayer(pPlayer);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pPlayer) {
        super.stopSeenByPlayer(pPlayer);
        this.bossInfo.removePlayer(pPlayer);
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    public void applyRaidBuffs(int wave, boolean p_213660_2_) {
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0, this.getYRot());
    }

    private int countOwned() {
        int count = 0;
        for (Owned owned : this.level().getEntitiesOfClass(Owned.class, this.getBoundingBox().inflate(32.0D))) {
            if (owned.getTrueOwner() == this) {
                ++count;
            }
        }
        return count;
    }

    private List<Integer> availableSummonVariants() {
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(2);
        LivingEntity target = this.getTarget();
        if (target != null) {
            double dist = this.distanceTo(target);
            if (dist >= 4.0D && dist <= 16.0D) {
                list.add(1);
            }
            if (dist <= 24.0D) {
                list.add(3);
            }
        }
        return list;
    }

    private void groundSmash() {
        if (this.level() instanceof ServerLevel serverLevel) {
            Vec3 look = this.getHorizontalLookAngle();
            double x = this.getX() + look.x * 1.5D;
            double y = this.getY() + this.getBbHeight() / 2.0D;
            double z = this.getZ() + look.z * 1.5D;
            double groundY = BlockFinder.findGroundY(serverLevel, x, y, z);
            if (groundY < y) {
                for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class,
                        new AABB(x - 2.0D, y - 2.0D, z - 2.0D, x + 2.0D, y + 2.0D, z + 2.0D))) {
                    if (target != this && !MobUtil.areAllies(target, this) && target.distanceToSqr(x, y, z) <= 4.0D) {
                        this.doHurtTarget(target);
                    }
                }
                BlockPos groundPos = BlockPos.containing(x, groundY - 1.0D, z);
                BlockState groundState = serverLevel.getBlockState(groundPos);
                if (groundState.isAir()) {
                    groundState = Blocks.STONE.defaultBlockState();
                }
                BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, groundState);
                for (int i = 0; i < 2; ++i) {
                    ServerParticleUtil.circularParticles(serverLevel, option, x, groundY + 0.25D, z, 1.5F);
                }
                ColorUtil colorUtil = new ColorUtil(groundState.getMapColor(serverLevel, groundPos).col);
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.5F, 10),
                        x, groundY, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                for (int i = 0; i < 10; ++i) {
                    double theta = ((double) i / 10.0D) * Math.PI * 2.0D;
                    double px = x + Math.cos(theta) * 1.5D;
                    double pz = z + Math.sin(theta) * 1.5D;
                    BlockPos blockPos = new BlockPos(Mth.floor(px), Mth.floor(groundY - 1.0D), Mth.floor(pz));
                    BlockState blockState;
                    for (blockState = serverLevel.getBlockState(blockPos); blockState
                            .getRenderShape() != RenderShape.MODEL; blockState = serverLevel.getBlockState(blockPos)) {
                        blockPos = blockPos.below();
                        if (blockPos.getY() <= serverLevel.getMinBuildHeight()) {
                            break;
                        }
                    }
                    BlockState blockAbove = serverLevel.getBlockState(blockPos.above());
                    if (!blockState.isAir() && !blockState.hasBlockEntity() && !blockAbove.blocksMotion()) {
                        ModFallingBlock fallingBlock = new ModFallingBlock(serverLevel,
                                Vec3.atCenterOf(blockPos.above()), blockState,
                                (float) (0.2D + this.random.nextGaussian() * 0.15D));
                        serverLevel.addFreshEntity(fallingBlock);
                    }
                }
            }
        }
    }

    private void verticalSlash() {
        if (this.level() instanceof ServerLevel serverLevel) {
            Vec3 look = this.getHorizontalLookAngle();
            Vec3 side = new Vec3(-look.z, 0.0D, look.x);
            double minY = this.getY();
            double maxY = this.getY() + 2.0D;
            AABB area = new AABB(this.getX() - 2.0D, minY, this.getZ() - 2.0D, this.getX() + 2.0D, maxY,
                    this.getZ() + 2.0D);
            for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, area)) {
                if (target != this && !MobUtil.areAllies(target, this)) {
                    Vec3 rel = target.position().subtract(this.getX(), this.getY(), this.getZ());
                    double forward = rel.dot(look);
                    double sideDist = Math.abs(rel.dot(side));
                    if (forward >= 0.0D && forward <= 1.5D && sideDist <= 0.5D && target.getY() >= minY
                            && target.getY() <= maxY) {
                        this.doHurtTarget(target);
                    }
                }
            }
        }
    }

    private void throwSmackStones() {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 start = this.getEyePosition();
        Vec3 targetPoint = new Vec3(target.getX(), target.getY(0.5D), target.getZ());
        Vec3 velocity = this.computeTrajectory(start, targetPoint, 1.6F);
        if (velocity == null) {
            return;
        }
        this.spawnSmackStone(start, velocity);
        this.spawnSmackStone(start, velocity.yRot((float) (15.0D * Math.PI / 180.0D)));
        this.spawnSmackStone(start, velocity.yRot((float) (-15.0D * Math.PI / 180.0D)));
    }

    @Nullable
    private Vec3 computeTrajectory(Vec3 start, Vec3 end, float preferredSpeed) {
        double dx = end.x - start.x;
        double dy = end.y - start.y;
        double dz = end.z - start.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist <= 0.01D) {
            return null;
        }
        Vec3 best = null;
        double bestDiff = Double.MAX_VALUE;
        for (int n = 10; n <= 45; ++n) {
            double a = 100.0D * (1.0D - Math.pow(0.99D, n));
            double vh = dist / a;
            double vy = (dy + 3.0D * n) / a - 3.0D;
            double speed = Math.sqrt(vh * vh + vy * vy);
            double diff = Math.abs(speed - preferredSpeed);
            if (diff < bestDiff) {
                bestDiff = diff;
                best = new Vec3(dx / dist * vh, vy, dz / dist * vh);
            }
        }
        return best;
    }

    private void spawnSmackStone(Vec3 pos, Vec3 velocity) {
        if (this.level() instanceof ServerLevel serverLevel) {
            SmackStone smackStone = new SmackStone(this, serverLevel);
            smackStone.setPos(pos.x, pos.y, pos.z);
            smackStone.setDeltaMovement(velocity);
            smackStone.setOwner(this);
            serverLevel.addFreshEntity(smackStone);
        }
    }

    private void whirlwindDamage() {
        if (this.level() instanceof ServerLevel serverLevel) {
            for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(1.3D))) {
                if (target != this && !MobUtil.areAllies(target, this)) {
                    if (this.doHurtTarget(target)) {
                        double dx = target.getX() - this.getX();
                        double dz = target.getZ() - this.getZ();
                        target.knockback(0.6D, dx, dz);
                    }
                }
            }
        }
    }

    private void whirlwindParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockPos blockPos = BlockPos.containing(this.getX(), this.getY() - 1.0F, this.getZ());
            BlockState state = serverLevel.getBlockState(blockPos);
            if (state.isAir()) {
                state = Blocks.STONE.defaultBlockState();
            }
            ServerParticleUtil.circularParticles(serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, state),
                    this.getX(), this.getY() + 0.25D, this.getZ(), 1.3F);
            ColorUtil colorUtil = new ColorUtil(0xac9b8f);
            serverLevel.sendParticles(ModParticleTypes.BIG_CULT_SPELL.get(), this.getRandomX(1.3D), this.getRandomY(),
                    this.getRandomZ(1.3D), 0, colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.0F);
        }
    }

    private void throwWhirlwindStones() {
        if (this.level() instanceof ServerLevel serverLevel) {
            int count = 2 + this.random.nextInt(2);
            for (int i = 0; i < count; ++i) {
                SmackStone smackStone = new SmackStone(this, serverLevel);
                float yaw = this.random.nextFloat() * 360.0F;
                smackStone.shootFromRotation(this, 0.0F, yaw, 0.0F, 1.6F, 0.5F);
                smackStone.setOwner(this);
                serverLevel.addFreshEntity(smackStone);
            }
        }
    }

    private void quake() {
        if (this.level() instanceof ServerLevel serverLevel) {
            ++this.quakeCastTime;
            if (this.quakeCastTime <= 16) {
                int d = this.quakeCastTime;
                float damage = (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5D);
                this.spawnTremorBlocks(serverLevel, d, damage);
                double theta = (this.getYRot() + 90.0F) * (Math.PI / 180.0D);
                double sx = this.getX() + d * Math.cos(theta);
                double sz = this.getZ() + d * Math.sin(theta);
                serverLevel.playSound(null, sx, this.getY(), sz, ModSounds.DIRT_DEBRIS.get(), SoundSource.PLAYERS, 1.0F,
                        0.8F + this.random.nextFloat() * 0.4F);
            }
        }
    }

    private void spawnTremorBlocks(ServerLevel serverLevel, int distance, float damage) {
        double yRotRad = this.getYRot() * (Math.PI / 180.0D);
        double facingAngle = yRotRad + Math.PI / 2.0D;
        double spread = Math.PI * 0.35D;
        int arcLen = Mth.ceil(distance * spread);
        for (int i = 0; i < arcLen; ++i) {
            double theta = (i / (arcLen - 1.0) - 0.5) * spread + facingAngle;
            double vx = Math.cos(theta);
            double vz = Math.sin(theta);
            double px = this.getX() + vx * distance - 2.0D * Math.cos((this.getYRot() + 90.0F) * Math.PI / 180.0D);
            double pz = this.getZ() + vz * distance - 2.0D * Math.sin((this.getYRot() + 90.0F) * Math.PI / 180.0D);
            double groundY = BlockFinder.findGroundY(serverLevel, px, this.getY(), pz);
            BlockState state = serverLevel.getBlockState(BlockPos.containing(px, groundY - 1.0D, pz));
            if (state.isAir()) {
                state = Blocks.STONE.defaultBlockState();
            }
            TremorBlockEntity tremorBlock = ModEntityTypes.TREMOR_BLOCK.get().create(serverLevel);
            if (tremorBlock != null) {
                tremorBlock.setBlockState(state);
                tremorBlock.setOwner(this);
                tremorBlock.setDamage(damage);
                tremorBlock.setPos(px, groundY - 0.4D, pz);
                tremorBlock.setDeltaMovement(0.0D, 0.2366D - this.random.nextDouble() * 0.04D, 0.0D);
                serverLevel.addFreshEntity(tremorBlock);
            }
        }
    }

    private List<BlockPos> ringPositions(double radius, int count) {
        List<BlockPos> list = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            double angle = (Math.PI * 2.0D * i) / count;
            double px = this.getX() + Math.cos(angle) * radius;
            double pz = this.getZ() + Math.sin(angle) * radius;
            double groundY = BlockFinder.findGroundY(this.level(), px, this.getY(), pz);
            list.add(new BlockPos(Mth.floor(px), Mth.floor(groundY), Mth.floor(pz)));
        }
        return list;
    }

    private void warnPositions(List<BlockPos> positions) {
        if (this.level() instanceof ServerLevel serverLevel) {
            for (BlockPos pos : positions) {
                BlockPos groundPos = pos;
                BlockState state = serverLevel.getBlockState(groundPos);
                while ((state.isAir() || state.getRenderShape() != RenderShape.MODEL)
                        && groundPos.getY() > serverLevel.getMinBuildHeight()) {
                    groundPos = groundPos.below();
                    state = serverLevel.getBlockState(groundPos);
                }
                if (state.isAir()) {
                    state = Blocks.STONE.defaultBlockState();
                }
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5D,
                        groundPos.getY() + 1.05D, pos.getZ() + 0.5D, 2, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private BlockPos bombPositionAt(Vec3 dir, double dist, int idx) {
        double t = (idx + 1) / 5.0D;
        double px = this.getX() + dir.x * dist * t;
        double pz = this.getZ() + dir.z * dist * t;
        double groundY = BlockFinder.findGroundY(this.level(), px, this.getY(), pz);
        return new BlockPos(Mth.floor(px), Mth.floor(groundY), Mth.floor(pz));
    }

    private void spawnBomb(BlockPos pos) {
        if (this.level() instanceof ServerLevel serverLevel) {
            TotemicBomb bomb = ModEntityType.TOTEMIC_BOMB.get().create(serverLevel);
            if (bomb != null) {
                bomb.setTrueOwner(this);
                bomb.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                bomb.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED,
                        null, null);
                serverLevel.addFreshEntity(bomb);
            }
        }
    }

    private void summonRedstoneMinistrosities() {
        if (this.level() instanceof ServerLevel serverLevel) {
            int count = 2 + this.random.nextInt(2);
            for (int i = 0; i < count; ++i) {
                RedstoneMinistrosity ministrosity = ModEntityType.REDSTONE_MINISTROSITY.get().create(serverLevel);
                if (ministrosity != null) {
                    ministrosity.setTrueOwner(this);
                    double angle = this.random.nextDouble() * Math.PI * 2.0D;
                    double dist = 2.0D + this.random.nextDouble() * 3.0D;
                    double px = this.getX() + Math.cos(angle) * dist;
                    double pz = this.getZ() + Math.sin(angle) * dist;
                    double groundY = BlockFinder.findGroundY(serverLevel, px, this.getY(), pz);
                    ministrosity.setPos(px, groundY, pz);
                    ministrosity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                            MobSpawnType.MOB_SUMMONED, null, null);
                    serverLevel.addFreshEntity(ministrosity);
                }
            }
        }
    }

    private void castEruption() {
        if (this.level() instanceof ServerLevel serverLevel) {
            SpellStat spellStat = new EruptionSpell().defaultStats().increaseDuration(2).increaseRadius(1)
                    .increasePotency(2).increaseBurning(1);
            new EruptionSpell().SpellResult(serverLevel, this, new ItemStack(ModItems.GEO_STAFF.get()), spellStat);
        }
    }

    private void castingParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockPos blockPos = BlockPos.containing(this.getX(), this.getY() - 1.0F, this.getZ());
            BlockState state = serverLevel.getBlockState(blockPos);
            if (state.isAir()) {
                state = Blocks.STONE.defaultBlockState();
            }
            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, state);
            for (int i = 0; i < 4; ++i) {
                ServerParticleUtil.circularParticles(serverLevel, option, this.getX(), this.getY() + 0.25D, this.getZ(),
                        1.0F);
            }
        }
    }

    private boolean isStoneOrDirt(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.DIRT)
                || state.is(BlockTags.STONE_BRICKS) || state.is(Blocks.SANDSTONE)
                || state.is(Blocks.COBBLESTONE) || state.is(Blocks.COBBLED_DEEPSLATE)
                || state.is(Blocks.MAGMA_BLOCK) || state.is(Blocks.CLAY)
                || state.is(Blocks.NETHERRACK) || state.is(Blocks.END_STONE);
    }

    private List<BlockPos> findAvailableBlocks(int count) {
        List<BlockPos> candidates = new ArrayList<>();
        BlockPos center = this.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-16, -4, -16), center.offset(16, 4, 16))) {
            BlockState state = this.level().getBlockState(pos);
            if (this.isStoneOrDirt(state) && !state.hasBlockEntity() && this.level().isEmptyBlock(pos.above())) {
                candidates.add(pos.immutable());
            }
        }
        Collections.shuffle(candidates);
        if (candidates.size() <= count) {
            return candidates;
        }
        return new ArrayList<>(candidates.subList(0, count));
    }

    private void barrageRipple(int index) {
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockPos blockPos = BlockPos.containing(this.getX(), this.getY() - 1.0F, this.getZ());
            BlockState state = serverLevel.getBlockState(blockPos);
            if (state.isAir()) {
                state = Blocks.STONE.defaultBlockState();
            }
            float radius = Math.min(16.0F, 3.0F + index * 1.5F);
            int count = Math.max(4, Mth.ceil(radius * 2.0F));
            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, state);
            for (int i = 0; i < count; ++i) {
                double angle = (Math.PI * 2.0D * i) / count;
                serverLevel.sendParticles(option, this.getX() + Math.cos(angle) * radius, this.getY() + 0.25D,
                        this.getZ() + Math.sin(angle) * radius, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private void spawnImpactBlock(BlockPos pos) {
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockState state = serverLevel.getBlockState(pos);
            if (state.isAir()) {
                return;
            }
            ImpactBlockEntity impact = ModEntityTypes.IMPACT_BLOCK.get().create(serverLevel);
            if (impact != null) {
                impact.setBlockState(state);
                impact.setTarget(this.getTarget());
                impact.setOwner(this);
                impact.initializePhase(24 + serverLevel.random.nextInt(13), 12 + serverLevel.random.nextInt(17),
                        0.5D + serverLevel.random.nextDouble() * 0.3D);

                impact.setExtraDamage((float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5D));
                impact.setPos(pos.getX() + 0.5D, pos.getY() + 0.01D, pos.getZ() + 0.5D);
                serverLevel.addFreshEntity(impact);
                ColorUtil colorUtil = new ColorUtil(state.getMapColor(serverLevel, pos).col);
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), 1.5F, 1),
                        pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5D,
                        pos.getY() + 0.5D, pos.getZ() + 0.5D, 8, 0.2D, 0.3D, 0.2D, 0.3D);
            }
        }
    }

    class MeleeGoal extends Goal {
        private int attackTick;

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = ArchGeomancerEntity.this.getTarget();
            return target != null && target.isAlive()
                    && ArchGeomancerEntity.this.getCurrentAnimation() == 0
                    && ArchGeomancerEntity.this.distanceTo(target) <= 3.0D
                    && (ArchGeomancerEntity.this.distanceTo(target) > 2.0D || ArchGeomancerEntity.this.whirlwindCool > 0);
        }

        @Override
        public boolean canContinueToUse() {
            return this.attackTick < 21;
        }

        @Override
        public void start() {
            ArchGeomancerEntity.this.getNavigation().stop();
            ArchGeomancerEntity.this.setAnimationState("attack");
            this.attackTick = 0;
        }

        @Override
        public void stop() {
            ArchGeomancerEntity.this.setAnimationState(0);
            this.attackTick = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = ArchGeomancerEntity.this.getTarget();
            if (target != null) {
                MobUtil.instaLook(ArchGeomancerEntity.this, target);
            }
            ++this.attackTick;
            if (this.attackTick == 1) {
                ArchGeomancerEntity.this.playSound(ModSounds.GEOMANCER_PRE_ATTACK.get(), 0.55F, 0.8F);
            }
            if (this.attackTick == 13) {
                ArchGeomancerEntity.this.groundSmash();
            }
            if (this.attackTick >= 11 && this.attackTick <= 13) {
                ArchGeomancerEntity.this.verticalSlash();
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class RangedGoal extends Goal {
        private int attackTick;

        public RangedGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = ArchGeomancerEntity.this.getTarget();
            return target != null && target.isAlive()
                    && ArchGeomancerEntity.this.getCurrentAnimation() == 0
                    && ArchGeomancerEntity.this.rangedCool <= 0
                    && ArchGeomancerEntity.this.distanceTo(target) > 6.0D;
        }

        @Override
        public boolean canContinueToUse() {
            return this.attackTick < 21;
        }

        @Override
        public void start() {
            ArchGeomancerEntity.this.getNavigation().stop();
            ArchGeomancerEntity.this.setAnimationState("attack");
            this.attackTick = 0;
        }

        @Override
        public void stop() {
            ArchGeomancerEntity.this.setAnimationState(0);
            ArchGeomancerEntity.this.rangedCool = 20;
            this.attackTick = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = ArchGeomancerEntity.this.getTarget();
            if (target != null) {
                MobUtil.instaLook(ArchGeomancerEntity.this, target);
            }
            ++this.attackTick;
            if (this.attackTick == 12) {
                ArchGeomancerEntity.this.playSound(ModSounds.GEOMANCER_ATTACK.get(), 0.55F, 0.8F);
                ArchGeomancerEntity.this.throwSmackStones();
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class WhirlwindGoal extends Goal {
        private int attackTick;

        public WhirlwindGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = ArchGeomancerEntity.this.getTarget();
            return target != null && target.isAlive()
                    && ArchGeomancerEntity.this.getCurrentAnimation() == 0
                    && ArchGeomancerEntity.this.whirlwindCool <= 0
                    && ArchGeomancerEntity.this.distanceTo(target) <= 2.0D;
        }

        @Override
        public boolean canContinueToUse() {
            return this.attackTick < 70;
        }

        @Override
        public void start() {
            ArchGeomancerEntity.this.getNavigation().stop();
            ArchGeomancerEntity.this.setAnimationState("big_attack");
            this.attackTick = 0;
        }

        @Override
        public void stop() {
            ArchGeomancerEntity.this.setAnimationState(0);
            ArchGeomancerEntity.this.whirlwindCool = 200;
            this.attackTick = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = ArchGeomancerEntity.this.getTarget();
            if (target != null) {
                MobUtil.instaLook(ArchGeomancerEntity.this, target);
            }
            ++this.attackTick;
            if (this.attackTick == 1) {
                ArchGeomancerEntity.this.playSound(ModSounds.GEOMANCER_PRE_ATTACK.get(), 0.55F, 0.8F);
            }
            if (this.attackTick >= 18 && this.attackTick <= 39) {
                ArchGeomancerEntity.this.whirlwindDamage();
                if (this.attackTick % 2 == 0) {
                    ArchGeomancerEntity.this.whirlwindParticles();
                }
                if (this.attackTick % 3 == 0) {
                    ArchGeomancerEntity.this.throwWhirlwindStones();
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class SummonGoal extends Goal {
        private int summonTick;
        private int summonVariant;
        private Vec3 bombDir;
        private double bombDist;
        private List<BlockPos> innerRing = new ArrayList<>();
        private List<BlockPos> outerRing = new ArrayList<>();

        public SummonGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = ArchGeomancerEntity.this.getTarget();
            if (target == null || !target.isAlive() || ArchGeomancerEntity.this.getCurrentAnimation() != 0
                    || ArchGeomancerEntity.this.summonCool > 0) {
                return false;
            }
            return ArchGeomancerEntity.this.countOwned() <= 2
                    && !ArchGeomancerEntity.this.availableSummonVariants().isEmpty();
        }

        @Override
        public boolean canContinueToUse() {
            return this.summonTick < 33;
        }

        @Override
        public void start() {
            List<Integer> variants = ArchGeomancerEntity.this.availableSummonVariants();
            this.summonVariant = variants.get(ArchGeomancerEntity.this.random.nextInt(variants.size()));
            ArchGeomancerEntity.this.getNavigation().stop();
            ArchGeomancerEntity.this.setAnimationState("summon");
            this.summonTick = 0;
            this.bombDir = null;
            if (this.summonVariant == 1) {
                this.innerRing = ArchGeomancerEntity.this.ringPositions(6.0D, 8);
                this.outerRing = ArchGeomancerEntity.this.ringPositions(12.0D, 12);
            }
        }

        @Override
        public void stop() {
            ArchGeomancerEntity.this.setAnimationState(0);
            ArchGeomancerEntity.this.summonCool = 200;
            this.summonTick = 0;
            this.bombDir = null;
        }

        @Override
        public void tick() {
            LivingEntity target = ArchGeomancerEntity.this.getTarget();
            if (target != null) {
                MobUtil.instaLook(ArchGeomancerEntity.this, target);
            }
            ++this.summonTick;
            if (this.summonTick == 1) {
                ArchGeomancerEntity.this.playSound(ModSounds.GEOMANCER_PRE_ATTACK.get(), 0.55F, 0.8F);
            }
            if (this.summonTick == 17) {
                ArchGeomancerEntity.this.playSound(ModSounds.GEOMANCER_ATTACK.get(), 0.55F, 0.8F);
            }
            if (this.summonVariant == 3 && this.summonTick == 10 && target != null) {
                Vec3 toTarget = target.position().subtract(ArchGeomancerEntity.this.position()).multiply(1.0D, 0.0D,
                        1.0D);
                double length = toTarget.horizontalDistance();
                if (length > 0.01D) {
                    this.bombDir = toTarget.scale(1.0D / length);
                    this.bombDist = length;
                }
            }
            if (this.summonVariant == 3 && this.summonTick >= 10 && this.summonTick <= 19) {
                ArchGeomancerEntity.this.castingParticles();
            }
            if (this.summonVariant == 1) {
                if (this.summonTick >= 10 && this.summonTick < 13) {
                    ArchGeomancerEntity.this.warnPositions(this.innerRing);
                }
                if (this.summonTick >= 12 && this.summonTick < 15) {
                    ArchGeomancerEntity.this.warnPositions(this.outerRing);
                }
                if (this.summonTick == 13) {
                    for (BlockPos pos : this.innerRing) {
                        ArchGeomancerEntity.this.spawnBomb(pos);
                    }
                }
                if (this.summonTick == 15) {
                    for (BlockPos pos : this.outerRing) {
                        ArchGeomancerEntity.this.spawnBomb(pos);
                    }
                }
            }
            if (this.summonVariant == 0 && this.summonTick == 13) {
                ArchGeomancerEntity.this.summonRedstoneMinistrosities();
            }
            if (this.summonVariant == 2 && this.summonTick == 13) {
                ArchGeomancerEntity.this.castEruption();
            }
            if (this.summonVariant == 3 && this.bombDir != null) {
                if (this.summonTick >= 8 && this.summonTick <= 17) {
                    int idx = (this.summonTick - 8) / 2;
                    if (idx >= 0 && idx < 5) {
                        ArchGeomancerEntity.this.warnPositions(
                                List.of(ArchGeomancerEntity.this.bombPositionAt(this.bombDir, this.bombDist, idx)));
                    }
                }
                if (this.summonTick >= 10 && this.summonTick <= 18 && this.summonTick % 2 == 0) {
                    int idx = (this.summonTick - 10) / 2;
                    if (idx >= 0 && idx < 5) {
                        ArchGeomancerEntity.this
                                .spawnBomb(ArchGeomancerEntity.this.bombPositionAt(this.bombDir, this.bombDist, idx));
                    }
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class QuakeGoal extends Goal {
        private int attackTick;

        public QuakeGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = ArchGeomancerEntity.this.getTarget();
            return target != null && target.isAlive()
                    && ArchGeomancerEntity.this.getCurrentAnimation() == 0
                    && ArchGeomancerEntity.this.quakeCool <= 0
                    && ArchGeomancerEntity.this.distanceTo(target) <= 13.0D;
        }

        @Override
        public boolean canContinueToUse() {
            return this.attackTick < 35;
        }

        @Override
        public void start() {
            ArchGeomancerEntity.this.getNavigation().stop();
            ArchGeomancerEntity.this.setAnimationState("spell_attack");
            ArchGeomancerEntity.this.quakeCastTime = 0;
            this.attackTick = 0;
        }

        @Override
        public void stop() {
            ArchGeomancerEntity.this.setAnimationState(0);
            ArchGeomancerEntity.this.quakeCool = 400;
            this.attackTick = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = ArchGeomancerEntity.this.getTarget();
            if (target != null) {
                MobUtil.instaLook(ArchGeomancerEntity.this, target);
            }
            ++this.attackTick;
            if (this.attackTick >= 14) {
                ArchGeomancerEntity.this.quake();
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class BarrageGoal extends Goal {
        private int attackTick;
        private int blockIndex;
        private List<BlockPos> blocks = new ArrayList<>();

        public BarrageGoal() {
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = ArchGeomancerEntity.this.getTarget();
            if (target == null || !target.isAlive() || ArchGeomancerEntity.this.getCurrentAnimation() != 0
                    || ArchGeomancerEntity.this.barrageCool > 0
                    || ArchGeomancerEntity.this.distanceTo(target) <= 3.0D) {
                return false;
            }
            if (ArchGeomancerEntity.this.tickCount - ArchGeomancerEntity.this.lastBarrageCheck > 10) {
                ArchGeomancerEntity.this.lastBarrageCheck = ArchGeomancerEntity.this.tickCount;
                ArchGeomancerEntity.this.cachedBarrageBlocks = ArchGeomancerEntity.this.findAvailableBlocks(6);
            }
            return !ArchGeomancerEntity.this.cachedBarrageBlocks.isEmpty();
        }

        @Override
        public boolean canContinueToUse() {
            return this.attackTick < 35;
        }

        @Override
        public void start() {
            DifficultyInstance difficulty = ArchGeomancerEntity.this.level()
                    .getCurrentDifficultyAt(ArchGeomancerEntity.this.blockPosition());
            int count = Mth.clamp(6 + (int) (difficulty.getSpecialMultiplier() / 6.75D * 6.0D), 6, 12);
            this.blocks = ArchGeomancerEntity.this.findAvailableBlocks(count);
            if (this.blocks.isEmpty()) {
                return;
            }
            ArchGeomancerEntity.this.getNavigation().stop();
            ArchGeomancerEntity.this.setAnimationState("spell_attack");
            this.attackTick = 0;
            this.blockIndex = 0;
        }

        @Override
        public void stop() {
            ArchGeomancerEntity.this.setAnimationState(0);
            ArchGeomancerEntity.this.barrageCool = 400;
            this.attackTick = 0;
            this.blockIndex = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = ArchGeomancerEntity.this.getTarget();
            if (target != null) {
                MobUtil.instaLook(ArchGeomancerEntity.this, target);
            }
            ++this.attackTick;
            if (this.attackTick == 1) {
                ArchGeomancerEntity.this.playSound(ModSounds.GEOMANCER_PRE_ATTACK.get(), 0.55F, 0.8F);
            }
            if (this.attackTick >= 14 && this.attackTick <= 23) {
                ArchGeomancerEntity.this.barrageRipple(this.attackTick - 14);
            }
            if (this.attackTick >= 14 && this.attackTick <= 32) {
                int size = this.blocks.size();
                if (size > 0) {
                    int targetIndex = (this.attackTick - 14) * size / 19;
                    while (this.blockIndex <= targetIndex && this.blockIndex < size) {
                        ArchGeomancerEntity.this.spawnImpactBlock(this.blocks.get(this.blockIndex));
                        ++this.blockIndex;
                    }
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }
}
