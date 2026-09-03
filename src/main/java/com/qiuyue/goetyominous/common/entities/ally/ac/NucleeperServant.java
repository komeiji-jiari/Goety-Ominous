package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearSirenBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearExplosionEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.RaycatEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorzillaEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.ActivatesSirens;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.google.common.base.Predicates;
import com.qiuyue.goetyominous.client.sound.NucleeperServantSoundHandler;
import com.qiuyue.goetyominous.common.events.NucleeperNukeKillHandler;
import com.qiuyue.goetyominous.common.events.NucleeperNukeProtectionHandler;
import com.qiuyue.goetyominous.common.items.ac.RaycatAmuletItem;
import com.qiuyue.goetyominous.common.init.ac.AcParticles;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.stream.Stream;

public class NucleeperServant extends Summoned implements ActivatesSirens, PowerableMob {

    private float closeProgress;
    private float prevCloseProgress;
    private float explodeProgress;
    private float prevExplodeProgress;
    private float sirenAngle;
    private float prevSirenAngle;
    private int catScareTime = 0;

    private boolean spawnedExplosion = false;
    private boolean manuallyIgnited = false;
    private static final EntityDataAccessor<Boolean> TRIGGERED = SynchedEntityData.defineId(NucleeperServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CLOSE_TIME = SynchedEntityData.defineId(NucleeperServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> EXPLODING = SynchedEntityData.defineId(NucleeperServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CHARGED = SynchedEntityData.defineId(NucleeperServant.class, EntityDataSerializers.BOOLEAN);

    public NucleeperServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, RaycatEntity.class, 10.0F, 1.0D, 1.2D) {
            public void tick() {
                super.tick();
                NucleeperServant.this.catScareTime = 20;
            }
        });
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, 10.0F, 1.0D, 1.2D,
                target -> target instanceof Player player
                        && RaycatAmuletItem.hasAmulet(player)
                        && player != this.getTrueOwner()) {
            public void tick() {
                super.tick();
                NucleeperServant.this.catScareTime = 20;
            }
        });
        this.goalSelector.addGoal(2, new MeleeGoal());
        // 用 Goety 的 WanderGoal(checkNoActionTime=false):非敌对 Summoned 的 noActionTime 永不复位,原版 RandomStrollGoal 空闲约5秒即被永久禁用而站桩。
        this.goalSelector.addGoal(3, new Summoned.WanderGoal<>(this, 1.0D, 45, 0.001F));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.NucleeperServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.NucleeperServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.NucleeperServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.NucleeperServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.NucleeperServantKnockbackResistance.get())
                .add(Attributes.ARMOR, AttributesConfig.NucleeperServantArmor.get());
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag tag) {
        if (this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.NucleeperServantLimit.get()) {
                this.discard();
                return null;
            }
        }
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnGroupData, tag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof NucleeperServant servant && servant != this) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TRIGGERED, false);
        this.entityData.define(CLOSE_TIME, 0);
        this.entityData.define(EXPLODING, false);
        this.entityData.define(CHARGED, false);
    }

    public int getCloseTime() {
        return this.entityData.get(CLOSE_TIME);
    }

    public void setCloseTime(int time) {
        this.entityData.set(CLOSE_TIME, time);
    }

    public boolean isTriggered() {
        return this.entityData.get(TRIGGERED);
    }

    public void setTriggered(boolean triggered) {
        this.entityData.set(TRIGGERED, triggered);
    }

    public boolean isExploding() {
        return this.entityData.get(EXPLODING);
    }

    public void setExploding(boolean explode) {
        this.entityData.set(EXPLODING, explode);
    }

    public boolean isCharged() {
        return this.entityData.get(CHARGED);
    }

    public void setCharged(boolean explode) {
        this.entityData.set(CHARGED, explode);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("Charged", this.isCharged());
        compoundTag.putInt("CloseTime", this.getCloseTime());
        compoundTag.putBoolean("ManuallyIgnited", this.manuallyIgnited);
    }

    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setCharged(compoundTag.getBoolean("Charged"));
        this.setCloseTime(compoundTag.getInt("CloseTime"));
        this.manuallyIgnited = compoundTag.getBoolean("ManuallyIgnited");
    }

    public void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt) {
        super.thunderHit(serverLevel, lightningBolt);
        this.setCharged(true);
    }

    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(ItemTags.CREEPER_IGNITERS)) {
            if (this.getOwnerId() == null || !player.getUUID().equals(this.getOwnerId())) {
                return InteractionResult.PASS;
            }
            SoundEvent soundevent = itemstack.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
            this.level().playSound(player, this.getX(), this.getY(), this.getZ(), soundevent, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.level().isClientSide) {
                this.manuallyIgnited = true;
                this.setTriggered(true);
                itemstack.hurtAndBreak(1, player, (p_32290_) -> {
                    p_32290_.broadcastBreakEvent(hand);
                });
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    public void tick() {
        super.tick();
        prevCloseProgress = closeProgress;
        prevExplodeProgress = explodeProgress;
        prevSirenAngle = sirenAngle;
        int time = this.getCloseTime();
        if (this.isExploding() && explodeProgress < 5F) {
            explodeProgress++;
        }
        if (!this.isExploding() && explodeProgress > 0F) {
            explodeProgress--;
        }
        if (this.isTriggered() && !level().isClientSide) {
            LivingEntity target = this.getTarget();
            boolean noTarget = !this.manuallyIgnited && !this.isExploding() && (target == null || !target.isAlive());
            if ((this.catScareTime > 0 && !this.isExploding()) || noTarget) {
                if (time > 0) {
                    this.setCloseTime(time - 1);
                } else {
                    this.setTriggered(false);
                    this.manuallyIgnited = false;
                }
            } else if (time < AlexsCaves.COMMON_CONFIG.nucleeperFuseTime.get()) {
                this.setCloseTime(time + 1);
            } else if (this.isAlive()) {
                this.setExploding(true);
            }
            if ((tickCount + this.getId()) % 10 == 0 && level() instanceof ServerLevel serverLevel) {
                getNearbySirens(serverLevel, 256).forEach(this::activateSiren);
            }
        }
        if (this.isTriggered() && this.isAlive() && this.level().isClientSide) {
            NucleeperServantSoundHandler.startSirenFor(this);
        }
        sirenAngle += (10F + 30F * closeProgress) % 360F;
        closeProgress = (float) time / AlexsCaves.COMMON_CONFIG.nucleeperFuseTime.get();
        if (this.catScareTime > 0) {
            this.catScareTime--;
        }
        if (this.isExploding() && explodeProgress >= 5F) {
            if (!this.level().isClientSide && !spawnedExplosion) {
                this.explode();
                spawnedExplosion = true;
            }
            this.discard();
        }
        if (this.isCharged() && this.isAlive() && this.tickCount % 150 == 0) {
            this.heal(1);
        }
    }

    public void remove(Entity.RemovalReason removalReason) {
        if (this.level().isClientSide) {
            AlexsCaves.PROXY.clearSoundCacheFor(this);
            NucleeperServantSoundHandler.clearSoundFor(this);
        }
        super.remove(removalReason);
    }

    @Override
    protected void pushEntities() {
        super.pushEntities();
        if (this.level().isClientSide || (this.tickCount + this.getId()) % 10 != 0) {
            return;
        }
        LivingEntity owner = this.getTrueOwner();
        double tauntRange = AttributesConfig.NucleeperServantTauntRange.get();
        if (owner == null || tauntRange <= 0.0) {
            return;
        }
        AABB tauntArea = this.getBoundingBox().inflate(tauntRange, 1.0, tauntRange);
        for (Entity entity : this.level().getEntities(this, tauntArea)) {
            if (entity instanceof Mob mob && mob.getTarget() == owner && this.isAlive()) {
                mob.setTarget(this);
            }
        }
    }

    private Stream<BlockPos> getNearbySirens(ServerLevel world, int range) {
        PoiManager pointofinterestmanager = world.getPoiManager();
        return pointofinterestmanager.findAll(poiTypeHolder -> poiTypeHolder.is(ACPOIRegistry.NUCLEAR_SIREN.getKey()), Predicates.alwaysTrue(), this.blockPosition(), range, PoiManager.Occupancy.ANY);
    }

    private void activateSiren(BlockPos pos) {
        if (level().getBlockEntity(pos) instanceof NuclearSirenBlockEntity nuclearSirenBlock) {
            nuclearSirenBlock.setNearestNuclearBomb(this);
        }
    }

    private void explode() {
        if (level() instanceof ServerLevel serverLevel) {
            NucleeperNukeProtectionHandler.protectOwnerAndServants(serverLevel, this);
            NucleeperNukeProtectionHandler.syncZoneToClients(serverLevel, this);
        }
        NuclearExplosionEntity explosion = ACEntityRegistry.NUCLEAR_EXPLOSION.get().create(level());
        explosion.copyPosition(this);
        explosion.setSize(isCharged() ? 1.75F : 1F);
        explosion.setNoGriefing(true);
        // 服务端实体在加入世界前直接把 spawnedParticle 置 true,保证服务端 tick 不发送 AC 原版
        // MUSHROOM_CLOUD 粒子(客户端实例由 NucleeperNukeProtectionHandler.onExplosionJoin 抑制)。
        NucleeperNukeProtectionHandler.suppressVanillaCloud(explosion);
        level().addFreshEntity(explosion);
        if (level() instanceof ServerLevel serverLevel) {
            spawnSurfaceCloud(serverLevel);
        }
        NucleeperNukeKillHandler.register(this);
    }

    private void spawnSurfaceCloud(ServerLevel level) {
        double size = isCharged() ? 1.75F : 1F;
        level.sendParticles((SimpleParticleType) AcParticles.NUCLEEPER_MUSHROOM_CLOUD.get(),
                getX(), findSurfaceY(level, getX(), getZ()), getZ(), 0, 1.0, 0.0, 0.0, size);
    }

    private static double findSurfaceY(Level level, double x, double z) {
        int cx = Mth.floor(x);
        int cz = Mth.floor(z);
        int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, cx, cz);
        for (int dx = -8; dx <= 8; dx += 4) {
            for (int dz = -8; dz <= 8; dz += 4) {
                int h = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, cx + dx, cz + dz);
                if (h > surface) {
                    surface = h;
                }
            }
        }
        return surface + 1.0;
    }

    public float getCloseProgress(float partialTick) {
        return (prevCloseProgress + (closeProgress - prevCloseProgress) * partialTick);
    }

    public float getSirenAngle(float partialTick) {
        return (prevSirenAngle + (sirenAngle - prevSirenAngle) * partialTick);
    }

    public float getExplodeProgress(float partialTick) {
        return (prevExplodeProgress + (explodeProgress - prevExplodeProgress) * partialTick) * 0.2F;
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, flying ? this.getY() - this.yo : 0, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 8.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    public float maxUpStep() {
        return 1.1F;
    }

    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.NUCLEEPER_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.NUCLEEPER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.NUCLEEPER_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isBaby()) {
            this.playSound(ACSoundRegistry.NUCLEEPER_STEP.get(), 1.0F, 1.0F);
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damageSource, int experience, boolean idk) {
        super.dropCustomDeathLoot(damageSource, experience, idk);
        if (damageSource.getEntity() instanceof TremorzillaEntity && damageSource.is(ACDamageTypes.TREMORZILLA_BEAM)) {
            this.spawnAtLocation(ACItemRegistry.MUSIC_DISC_FUSION_FRAGMENT.get());
        }
    }

    @Override
    public boolean shouldStopBlaringSirens() {
        return !this.isTriggered() && !this.isExploding() || this.isRemoved();
    }

    @Override
    public boolean isPowered() {
        return this.isCharged();
    }

    private class MeleeGoal extends Goal {

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = NucleeperServant.this.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = NucleeperServant.this.getTarget();
            if (target != null && target.isAlive()) {
                NucleeperServant.this.setTriggered(true);
                NucleeperServant.this.getNavigation().moveTo(target, 1.0D);
            }
        }
    }

}
