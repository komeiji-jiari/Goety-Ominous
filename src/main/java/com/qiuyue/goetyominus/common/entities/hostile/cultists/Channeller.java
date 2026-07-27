package com.qiuyue.goetyominus.common.entities.hostile.cultists;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.util.FireBlastTrap;
import com.Polarice3.Goety.common.entities.util.SummonCircle;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.qiuyue.goetyominus.config.AttributesConfig;
import com.qiuyue.goetyominus.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Channeller extends AbstractGOCultist {
    private static final EntityDataAccessor<Boolean> IS_PRAYING = SynchedEntityData.defineId(Channeller.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> ALLY_UUID = SynchedEntityData.defineId(Channeller.class, EntityDataSerializers.OPTIONAL_UUID);
    private int prayingTick;
    private int summonCooldown;
    private boolean hasSummonedOnce;
    private UUID stolenServantId;
    private UUID originalOwnerId;

    public Channeller(EntityType<? extends AbstractGOCultist> type, Level level) {
        super(type, level);
        this.xpReward = 10;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ChannellerBarterGoal());
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, LivingEntity.class, 8.0F, 0.6D, 1.0D,
                target -> !(target instanceof Player player
                        && com.Polarice3.Goety.utils.CuriosFinder.isWitchFriendly(player)
                        && !player.equals(Channeller.this.getTarget()))));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal<>(
                this, Player.class, 10, true, false, null));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ChannellerHealth.get())
                .add(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_PRAYING, false);
        this.entityData.define(ALLY_UUID, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("HasSummonedOnce", this.hasSummonedOnce);
        compound.putInt("PrayingTick", this.prayingTick);
        if (this.stolenServantId != null) {
            compound.putUUID("StolenServantId", this.stolenServantId);
            if (this.originalOwnerId != null) {
                compound.putUUID("OriginalOwnerId", this.originalOwnerId);
            }
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.hasSummonedOnce = compound.getBoolean("HasSummonedOnce");
        this.prayingTick = compound.getInt("PrayingTick");
        if (compound.hasUUID("StolenServantId")) {
            this.stolenServantId = compound.getUUID("StolenServantId");
        }
        if (compound.hasUUID("OriginalOwnerId")) {
            this.originalOwnerId = compound.getUUID("OriginalOwnerId");
        }
    }

    @Nullable
    public Mob getAlly() {
        try {
            UUID uuid = this.getAllyUUID();
            if (uuid != null) {
                if (EntityFinder.getLivingEntityByUuiD(uuid) instanceof Mob mob) {
                    return mob;
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Nullable
    public UUID getAllyUUID() {
        return this.entityData.get(ALLY_UUID).orElse(null);
    }

    public void setAllyUUID(UUID uuid) {
        this.entityData.set(ALLY_UUID, Optional.ofNullable(uuid));
    }

    public void setAlly(Mob mob) {
        this.setAllyUUID(mob.getUUID());
    }

    public boolean isPraying() {
        return this.entityData.get(IS_PRAYING);
    }

    public void setIsPraying(boolean praying) {
        this.entityData.set(IS_PRAYING, praying);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.isPraying()) {
            return super.hurt(source, amount);
        } else {
            return super.hurt(source, amount / 2);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isAreaLoaded(this.blockPosition(), 2)) return;

        if (!this.hasSummonedOnce && this.getTarget() != null && this.getAlly() == null) {
            summonZPiglin();
            this.hasSummonedOnce = true;
        }

        if (this.getAlly() != null) {
            Mob ally = this.getAlly();
            if (ally.isDeadOrDying()) {
                this.setAllyUUID(null);
                this.setIsPraying(false);
                this.prayingTick = 0;
                this.summonCooldown = 100;
                return;
            }

            if (this.prayingTick < 20) {
                ++this.prayingTick;
            } else {
                if (this.distanceTo(ally) >= 12.0D) {
                    Vec3 vec3 = ally.position();
                    this.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0D);
                } else {
                    this.setIsPraying(true);
                    this.getLookControl().setLookAt(ally, this.getMaxHeadYRot(), this.getMaxHeadXRot());
                    if (!this.level().isClientSide) {
                        this.getNavigation().stop();
                        this.noActionTime = 0;
                        ally.setTarget(this.getTarget());
                        ally.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 1));
                        ally.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 1));
                        ally.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 1));
                        ally.setPersistenceRequired();
                        if (this.getHealth() < this.getMaxHealth()) {
                            if (this.tickCount % 10 == 0) {
                                ally.hurt(this.damageSources().starve(), 2.0F);
                                this.heal(2.0F);
                            }
                        }
                        if (this.getTarget() != null && this.hasLineOfSight(this.getTarget())) {
                            if (this.tickCount % 100 == 0) {
                                this.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 1.0F);
                                double d0 = Math.min(this.getTarget().getY(), this.getY());
                                double d1 = Math.max(this.getTarget().getY(), this.getY()) + 1.0D;
                                spawnFireBlasts(this, this.getTarget().getX(), this.getTarget().getZ(), d0, d1);
                            }
                        }
                    }
                }
            }
        } else {
            this.setIsPraying(false);
            this.prayingTick = 0;

            if (this.summonCooldown > 0) {
                --this.summonCooldown;
            }

            if (this.isAggressive() || this.getTarget() != null) {
                findNewAlly();

                if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
                    if (this.summonCooldown <= 0 && !this.isDeadOrDying()) {
                        summonMinion(serverLevel);
                    }
                }
            }

            if (!this.level().isClientSide) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    ServerParticleUtil.gatheringParticles(ModParticleTypes.SMALL_FIRE.get(), this, serverLevel);
                }
            }
        }
    }

    private void summonZPiglin() {
        if (this.level().isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) this.level();

        com.Polarice3.Goety.common.entities.neutral.ZPiglinServant minion =
                ModEntityType.ZPIGLIN_SERVANT.get().create(serverLevel);
        if (minion != null) {
            minion.setTrueOwner(this);

            BlockPos summonPos = BlockFinder.SummonRadius(this.blockPosition(), minion, serverLevel);
            minion.setPos(summonPos.getX() + 0.5D, summonPos.getY(), summonPos.getZ() + 0.5D);
            MobUtil.moveDownToGround(minion);

            minion.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                    MobSpawnType.MOB_SUMMONED, null, null);
            serverLevel.addFreshEntity(minion);
            minion.setTarget(null);
            this.setAlly(minion);

            SummonCircle circle = new SummonCircle(
                    serverLevel,
                    minion.position(),
                    minion,
                    true,
                    true,
                    this);
            circle.setLifeSpan(20);
            serverLevel.addFreshEntity(circle);

            for (int k = 0; k < 60; ++k) {
                float f2 = random.nextFloat() * 4.0F;
                float f1 = random.nextFloat() * ((float) Math.PI * 2F);
                double d1 = Mth.cos(f1) * f2;
                double d2 = 0.01D + random.nextDouble() * 0.5D;
                double d3 = Mth.sin(f1) * f2;
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        minion.getX() + d1 * 0.1D, minion.getY() + 0.3D, minion.getZ() + d3 * 0.1D,
                        0, d1, d2, d3, 0.25F);
            }
        }
    }

    private boolean isControlledByOtherChanneller(Mob mob) {
        for (Channeller other : this.level().getEntitiesOfClass(Channeller.class,
                this.getBoundingBox().inflate(64.0D))) {
            if (other != this && mob.equals(other.getAlly())) {
                return true;
            }
        }
        return false;
    }

    private void findNewAlly() {
        List<Mob> summonedList = this.level().getEntitiesOfClass(Mob.class,
                this.getBoundingBox().inflate(64.0D, 8.0D, 64.0D));
        for (Mob mob : summonedList) {
            if (mob.canChangeDimensions() && this.hasLineOfSight(mob)
                    && mob.getMaxHealth() <= MobsConfig.ChannellerMaxStealHealth.get()
                    && !isControlledByOtherChanneller(mob)) {
                if (mob instanceof Summoned summoned) {
                    LivingEntity owner = summoned.getTrueOwner();
                    if (owner != null && owner != this) {
                        this.stolenServantId = mob.getUUID();
                        this.originalOwnerId = owner.getUUID();
                        summoned.setTrueOwner(this);
                        mob.setTarget(null);
                        mob.setLastHurtByMob(null);
                        this.setAlly(mob);
                        return;
                    }
                }
            }
        }
        List<Monster> monsterList = this.level().getEntitiesOfClass(Monster.class,
                this.getBoundingBox().inflate(64.0D, 8.0D, 64.0D));
        for (Monster monster : monsterList) {
            if (monster.canChangeDimensions() && this.hasLineOfSight(monster)
                    && !(monster instanceof Creeper) && !(monster instanceof Channeller)
                    && !isControlledByOtherChanneller(monster)) {
                this.setAlly(monster);
                this.stolenServantId = null;
                this.originalOwnerId = null;
                return;
            }
        }
    }

    private void summonMinion(ServerLevel serverLevel) {
        Mob minion;
        int roll = this.random.nextInt(4);
        if (roll == 0) {
            minion = ModEntityType.WITHER_SKELETON_SERVANT.get().create(serverLevel);
        } else if (roll == 1) {
            minion = ModEntityType.BLAZE_SERVANT.get().create(serverLevel);
        } else if (roll == 2) {
            minion = ModEntityType.ZPIGLIN_SERVANT.get().create(serverLevel);
        } else {
            minion = ModEntityType.ZPIGLIN_BRUTE_SERVANT.get().create(serverLevel);
        }

        if (minion != null) {
            if (minion instanceof Summoned summoned) {
                summoned.setTrueOwner(this);
            }

            BlockPos summonPos = BlockFinder.SummonRadius(this.blockPosition(), minion, serverLevel);
            minion.setPos(summonPos.getX() + 0.5D, summonPos.getY(), summonPos.getZ() + 0.5D);
            MobUtil.moveDownToGround(minion);

            minion.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                    MobSpawnType.MOB_SUMMONED, null, null);
            serverLevel.addFreshEntity(minion);
            minion.setTarget(null);

            SummonCircle circle = new SummonCircle(
                    serverLevel,
                    minion.position(),
                    minion,
                    true,
                    true,
                    this);
            circle.setLifeSpan(20);
            serverLevel.addFreshEntity(circle);
            if (this.getAlly() == null) {
                this.setAlly(minion);
            }

            for (int k = 0; k < 60; ++k) {
                float f2 = random.nextFloat() * 4.0F;
                float f1 = random.nextFloat() * ((float) Math.PI * 2F);
                double d1 = Mth.cos(f1) * f2;
                double d2 = 0.01D + random.nextDouble() * 0.5D;
                double d3 = Mth.sin(f1) * f2;
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        minion.getX() + d1 * 0.1D, minion.getY() + 0.3D, minion.getZ() + d3 * 0.1D,
                        0, d1, d2, d3, 0.25F);
            }
        }
    }

    private void spawnFireBlasts(LivingEntity caster, double posX, double posZ, double minY, double maxY) {
        for (int ring = 0; ring < 5; ++ring) {
            float radius = ring == 0 ? 0.0F : 0.5F + (ring - 1) * 1.5F;
            float angle = ring * ((float) Math.PI * 0.4F);
            double px = posX + Mth.cos(angle) * radius;
            double pz = posZ + Mth.sin(angle) * radius;

            BlockPos blockpos = BlockPos.containing(px, maxY, pz);
            boolean found = false;
            double dy = 0.0D;

            do {
                BlockPos below = blockpos.below();
                BlockState state = caster.level().getBlockState(below);
                if (state.isFaceSturdy(caster.level(), below, Direction.UP)) {
                    if (!caster.level().isEmptyBlock(blockpos)) {
                        BlockState bs = caster.level().getBlockState(blockpos);
                        var shape = bs.getCollisionShape(caster.level(), blockpos);
                        if (!shape.isEmpty()) {
                            dy = shape.max(Direction.Axis.Y);
                        }
                    }
                    found = true;
                    break;
                }
                blockpos = blockpos.below();
            } while (blockpos.getY() >= Mth.floor(minY) - 1);

            if (found) {
                FireBlastTrap trap = new FireBlastTrap(caster.level(), px, (double) blockpos.getY() + dy, pz);
                trap.setOwner(caster);
                caster.level().addFreshEntity(trap);
            }
        }
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide && this.stolenServantId != null) {
            returnStolenServant();
        }
        super.die(source);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide && this.stolenServantId != null) {
            returnStolenServant();
        }
        super.remove(reason);
    }

    private void returnStolenServant() {
        if (EntityFinder.getLivingEntityByUuiD(this.stolenServantId) instanceof Mob servant) {
            if (this.originalOwnerId != null) {
                LivingEntity originalOwner = EntityFinder.getLivingEntityByUuiD(this.originalOwnerId);
                if (originalOwner != null && servant instanceof Summoned summoned) {
                    summoned.setTrueOwner(originalOwner);
                    servant.setTarget(null);
                    servant.setLastHurtByMob(null);
                }
            }
        }
    }

    @Override
    public boolean isBarterable() {
        return true;
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (!this.level().isClientSide && itemstack.is(com.Polarice3.Goety.init.ModTags.Items.WITCH_CURRENCY) && this.getMainHandItem().isEmpty()) {
            this.setItemInHand(InteractionHand.MAIN_HAND, itemstack.split(1));
            com.Polarice3.Goety.utils.WitchBarterHelper.setTrader(this, pPlayer);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return new ResourceLocation("goety", "entities/heretic");
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.HERETIC_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.HERETIC_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.HERETIC_DEATH.get();
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.HERETIC_CELEBRATE.get();
    }

    @Override
    public void applyRaidBuffs(int wave, boolean p_213660_2_) {
    }

    @Override
    public boolean canBeLeader() {
        return false;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    @Override
    protected float getDamageAfterMagicAbsorb(DamageSource source, float damage) {
        damage = super.getDamageAfterMagicAbsorb(source, damage);
        if (source.is(net.minecraft.tags.DamageTypeTags.WITCH_RESISTANT_TO)) {
            damage = (float) ((double) damage * 0.15D);
        }
        return damage;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.62F;
    }

    class ChannellerBarterGoal extends Goal {
        private int progress = 100;

        public ChannellerBarterGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.TARGET));
        }

        @Override
        public boolean isInterruptable() { return false; }

        @Override
        public boolean canUse() {
            return Channeller.this.getMainHandItem().is(com.Polarice3.Goety.init.ModTags.Items.WITCH_CURRENCY);
        }

        @Override
        public void start() {
            this.progress = 100;
            if (!Channeller.this.level().isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) Channeller.this.level();
                for (int i = 0; i < 5; ++i) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            Channeller.this.getRandomX(1.0D), Channeller.this.getRandomY() + 1.0D,
                            Channeller.this.getRandomZ(1.0D), 0,
                            Channeller.this.getRandom().nextGaussian() * 0.02D,
                            Channeller.this.getRandom().nextGaussian() * 0.02D,
                            Channeller.this.getRandom().nextGaussian() * 0.02D, 0.5F);
                }
            }
        }

        @Override
        public void tick() {
            Channeller.this.setTarget(null);
            LivingEntity trader = com.Polarice3.Goety.utils.WitchBarterHelper.getTrader(Channeller.this);
            if (--this.progress > 0) {
                Channeller.this.getNavigation().stop();
                if (trader != null && Channeller.this.distanceTo(trader) <= 16.0F) {
                    Channeller.this.getLookControl().setLookAt(trader);
                }
            }
            if (this.progress <= 0) {
                Vec3 vec3 = trader != null ? trader.position() : Channeller.this.position();
                if (!Channeller.this.level().isClientSide() && Channeller.this.level().getServer() != null) {
                    float luck = Channeller.this.getMainHandItem().is(com.Polarice3.Goety.init.ModTags.Items.WITCH_BETTER_CURRENCY) ? 1.0F : 0.0F;

                    LootTable loottable = Channeller.this.level().getServer().getLootData().getLootTable(
                            new ResourceLocation(com.qiuyue.goetyominus.GoetyOminous.MOD_ID, "gameplay/channeller_bartering"));

                    List<ItemStack> list = loottable.getRandomItems(
                            (new LootParams.Builder((ServerLevel) Channeller.this.level()))
                                    .withParameter(LootContextParams.THIS_ENTITY, Channeller.this)
                                    .withParameter(LootContextParams.ORIGIN, Channeller.this.position())
                                    .withLuck(luck)
                                    .create(LootContextParamSets.GIFT));

                    for (ItemStack itemstack : list) {
                        BehaviorUtils.throwItem(Channeller.this, itemstack, vec3.add(0.0D, 1.0D, 0.0D));
                    }
                }
                this.clearTrade();
            }

            if (Channeller.this.hurtTime != 0) {
                if (Channeller.this.getMainHandItem().is(com.Polarice3.Goety.init.ModTags.Items.WITCH_CURRENCY)
                        || Channeller.this.getMainHandItem().is(com.Polarice3.Goety.init.ModTags.Items.WITCH_BETTER_CURRENCY)) {
                    Channeller.this.spawnAtLocation(Channeller.this.getMainHandItem());
                    this.clearTrade();
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() { return true; }

        private void clearTrade() {
            Channeller.this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            com.Polarice3.Goety.utils.WitchBarterHelper.setTimer(Channeller.this, 0);
            this.progress = 100;
        }
    }
}
