package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.google.common.collect.ImmutableList;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.TieredItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TeletorServant extends Summoned {
    private static final TagKey<Item> MAGNETIC_TOOLS = TagKey.create(Registries.ITEM,
            new ResourceLocation("alexscaves", "ferromagnetic_items"));

    private static final Item[] IRON_TOOLS = {Items.IRON_SWORD, Items.IRON_PICKAXE,
            Items.IRON_AXE, Items.IRON_SHOVEL, Items.IRON_HOE};

    private static ImmutableList<ItemStack> DARK_METAL_TOOL_CATALOG = null;

    private static final String FORGE_BLESSED_TAG = "goetyominous_teletor_forge_blessed";
    private static final EntityDataAccessor<Optional<UUID>> WEAPON_UUID = SynchedEntityData.defineId(TeletorServant.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> WEAPON_ID = SynchedEntityData.defineId(TeletorServant.class, EntityDataSerializers.INT);

    private float prevControlProgress = 0;
    private float controlProgress = 0;

    private final Vec3[][] trailPositions = new Vec3[64][2];
    private int trailPointer = -1;

    private int floatingTicks = 0;

    private int weaponMissingTicks = 0;
    private int weaponRespawnCooldown = 0;

    private boolean forgeBlessed = false;

    public TeletorServant(EntityType<? extends Summoned> teletor, Level level) {
        super(teletor, level);
        this.moveControl = new MoveController();
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FLYING_SPEED, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.TeletorServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.TeletorServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.TeletorServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.TeletorServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.TeletorServantKnockbackResistance.get())
                .add(Attributes.ARMOR, AttributesConfig.TeletorServantArmor.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WEAPON_UUID, Optional.empty());
        this.entityData.define(WEAPON_ID, -1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new MeleeGoal());
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    /**
     * 待命 = 原地悬浮"守卫":身体不移动,但索敌保持启用(挨打反击/主人受击/主人开打/就近敌对),
     * 让悬浮兵刃(TeletorWeaponServantEntity)自主飞出攻击,本体重置原地。
     * 身体不动的三重保障:① MeleeGoal 在 canUse 时遇待命直接 false;② 基类跟随目标在非跟随态不运行;
     * ③ MoveController 待命时把 MOVE_TO 转 WAIT。解除待命即恢复跟随/缠斗,不留脏状态。
     */

    @Override
    public void setStaying(boolean staying) {
        super.setStaying(staying);
        if (!this.level().isClientSide) {
            this.getNavigation().stop();
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingPathNavigation = new FlyingPathNavigation(this, level) {
            @Override
            public boolean isStableDestination(BlockPos pos) {
                return !this.level.getBlockState(pos.below()).isAir();
            }
        };
        flyingPathNavigation.setCanOpenDoors(false);
        flyingPathNavigation.setCanFloat(false);
        flyingPathNavigation.setCanPassDoors(true);
        return flyingPathNavigation;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Nullable
    public UUID getWeaponUUID() {
        return this.entityData.get(WEAPON_UUID).orElse(null);
    }

    public void setWeaponUUID(@Nullable UUID uniqueId) {
        this.entityData.set(WEAPON_UUID, Optional.ofNullable(uniqueId));
    }

    @Nullable
    public Entity getWeapon() {
        if (!this.level().isClientSide) {
            UUID id = this.getWeaponUUID();
            return id == null ? null : ((ServerLevel) this.level()).getEntity(id);
        }
        int id = this.entityData.get(WEAPON_ID);
        return id == -1 ? null : this.level().getEntity(id);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.55F * dimensions.height;
    }

    public boolean areLegsCrossed(float limbSwing) {
        return this.isAlive() && limbSwing <= 0.35F;
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 currentMotion = this.getDeltaMovement();
        if (!Double.isFinite(currentMotion.x) || !Double.isFinite(currentMotion.y) || !Double.isFinite(currentMotion.z)) {
            // 兜底:任何 NaN 速度入体立即清零,避免"切状态后卡死不动"再犯
            this.setDeltaMovement(Vec3.ZERO);
        }
        this.prevControlProgress = this.controlProgress;
        Entity weapon = this.getWeapon();
        if (weapon instanceof TeletorWeaponServantEntity teletorWeapon) {
            this.entityData.set(WEAPON_ID, teletorWeapon.getId());
            teletorWeapon.setControllerUUID(this.getUUID());
            Entity e = teletorWeapon.getTarget();
            boolean control = e != null && e.isAlive();
            if (control && this.controlProgress < 5.0F) {
                this.controlProgress++;
            }
            if (!control && this.controlProgress > 0.0F) {
                this.controlProgress--;
            }
        }
        if (this.level().isClientSide) {
            this.tickVisual();
        } else {
            this.tickWeaponMaintenance();
        }
        if (this.floatingTicks-- <= 0) {
            this.floatingTicks = 30;
            this.playSound(ACSoundRegistry.TELETOR_FLOAT.get());
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.98F, 0.98F, 0.98F));
    }

    public void tickVisual() {
        Vec3 blue = getHelmetPosition(0);
        Vec3 red = getHelmetPosition(1);
        if (this.trailPointer == -1) {
            for (int i = 0; i < this.trailPositions.length; i++) {
                this.trailPositions[i][0] = blue;
                this.trailPositions[i][1] = red;
            }
        }
        if (++this.trailPointer == this.trailPositions.length) {
            this.trailPointer = 0;
        }
        this.trailPositions[this.trailPointer][0] = blue;
        this.trailPositions[this.trailPointer][1] = red;
    }

    public boolean hasTrail() {
        return this.trailPointer != -1;
    }

    public float getControlProgress(float partialTick) {
        return (this.prevControlProgress + (this.controlProgress - this.prevControlProgress) * partialTick) * 0.2F;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public Vec3 getTrailPosition(int pointer, int side, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 63;
        int j = this.trailPointer - pointer - 1 & 63;
        Vec3 d0 = this.trailPositions[j][side];
        Vec3 d1 = this.trailPositions[i][side].subtract(d0);
        return d0.add(d1.scale(partialTick));
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn,
                                        @Nullable CompoundTag dataTag) {
        if (this.getTrueOwner() instanceof Player player) {
            if (this.countServants(player) >= MobsConfig.TeletorServantLimit.get()) {
                this.discard();
                return null;
            }
        }
        SpawnGroupData spawnGroupData = super.finalizeSpawn(level, difficulty, reason, spawnDataIn, dataTag);
        if (!this.level().isClientSide) {
            this.checkForgeBlessing(reason);
            if (this.getWeapon() == null) {
                this.spawnDefaultWeapon();
            }
        }
        return spawnGroupData;
    }

    public ItemStack createItemStack(RandomSource random) {
        if (this.isForgeBlessed()) {
            List<ItemStack> darkMetal = getDarkMetalToolCatalog();
            if (!darkMetal.isEmpty()) {
                return darkMetal.get(random.nextInt(darkMetal.size())).copy();
            }
        }
        return new ItemStack(IRON_TOOLS[random.nextInt(IRON_TOOLS.length)]);
    }

    private static List<ItemStack> getDarkMetalToolCatalog() {
        if (DARK_METAL_TOOL_CATALOG == null) {
            DARK_METAL_TOOL_CATALOG = ForgeRegistries.ITEMS.getValues().stream()
                    .filter(TeletorServant::isGoetyDarkMetalTool)
                    .sorted(Comparator.comparing(item -> ForgeRegistries.ITEMS.getKey(item).toString()))
                    .map(ItemStack::new)
                    .collect(ImmutableList.toImmutableList());
        }
        return DARK_METAL_TOOL_CATALOG;
    }

    public boolean isForgeBlessed() {
        return this.forgeBlessed;
    }

    private void checkForgeBlessing(MobSpawnType reason) {
        if (this.forgeBlessed || reason != MobSpawnType.MOB_SUMMONED) {
            return;
        }
        LivingEntity owner = this.getTrueOwner();
        if (owner != null && CuriosFinder.hasCurio(owner, ModItems.RING_OF_THE_FORGE.get())) {
            this.forgeBlessed = true;
        }
    }

    private static boolean isGrantableTool(Item item) {
        if (item instanceof TieredItem && item.builtInRegistryHolder().is(MAGNETIC_TOOLS)) {
            return true;
        }
        return isGoetyDarkMetalTool(item);
    }

    private static boolean isGoetyDarkMetalTool(Item item) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        if (key == null || !key.getNamespace().equals("goety")) {
            return false;
        }
        String path = key.getPath();
        boolean metalTool = path.startsWith("dark_") || path.startsWith("dark_metal_");
        return metalTool && (path.endsWith("_sword") || path.endsWith("_pickaxe")
                || path.endsWith("_axe") || path.endsWith("_shovel") || path.endsWith("_hoe")
                || path.endsWith("_scythe"));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player == this.getTrueOwner() && hand == InteractionHand.MAIN_HAND
                && isGrantableTool(player.getItemInHand(hand).getItem())) {
            if (!this.level().isClientSide) {
                ItemStack held = player.getItemInHand(hand);
                TeletorWeaponServantEntity weapon;
                boolean rearmed = false;
                if (this.getWeapon() instanceof TeletorWeaponServantEntity existing) {
                    weapon = existing;
                } else {
                    weapon = this.spawnWeapon(TeletorWeaponServantEntity.markAsPlayerProvided(held.copy()));
                    rearmed = weapon != null;
                }
                if (weapon != null) {
                    if (rearmed) {
                        held.shrink(1);
                        if (held.isEmpty()) {
                            player.setItemInHand(hand, ItemStack.EMPTY);
                        }
                    } else {
                        ItemStack old = weapon.getItemStack();
                        weapon.setItemStack(TeletorWeaponServantEntity.markAsPlayerProvided(held.copy()));
                        held.shrink(1);
                        if (held.isEmpty()) {
                            player.setItemInHand(hand, ItemStack.EMPTY);
                        }
                        if (TeletorWeaponServantEntity.isPlayerProvided(old)) {
                            if (!player.getInventory().add(old)) {
                                this.spawnAtLocation(old);
                            }
                        }
                    }
                    this.feedbackForWeaponChange(weapon, player, hand);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    public void onWeaponDestroyed() {
        if (!this.level().isClientSide) {
            this.setWeaponUUID(null);
            this.weaponMissingTicks = 0;
        }
    }

    private void tickWeaponMaintenance() {
        if (!this.isAlive() || this.isRemoved()) {
            return;
        }
        LivingEntity owner = this.getTrueOwner();
        if (owner == null || !owner.isAlive()) {
            return;
        }
        if (this.getWeapon() != null) {
            this.weaponMissingTicks = 0;
            return;
        }
        if (this.weaponMissingTicks < this.weaponGraceTicks()) {
            this.weaponMissingTicks++;
            return;
        }
        if (--this.weaponRespawnCooldown > 0) {
            return;
        }
        this.weaponRespawnCooldown = 100;
        this.weaponMissingTicks = 0;
        this.setWeaponUUID(null);
        this.spawnDefaultWeapon();
    }

    private int weaponGraceTicks() {
        return this.getWeaponUUID() == null ? 40 : 200;
    }

    public void spawnDefaultWeapon() {
        this.spawnWeapon(null);
    }

    @Nullable
    public TeletorWeaponServantEntity spawnWeapon(@Nullable ItemStack stack) {
        if (this.level().isClientSide || this.getWeapon() != null) {
            return null;
        }
        TeletorWeaponServantEntity weapon = AcEntityRegistry.TELETOR_WEAPON_SERVANT.get().create(this.level());
        if (weapon == null) {
            return null;
        }
        ItemStack toHold = (stack == null || stack.isEmpty())
                ? TeletorWeaponServantEntity.markAsGenerated(this.createItemStack(this.random))
                : stack.copy();
        weapon.setItemStack(toHold);
        weapon.setPos(this.getWeaponPosition());
        weapon.setControllerUUID(this.getUUID());
        this.setWeaponUUID(weapon.getUUID());
        if (!this.level().addFreshEntity(weapon)) {
            this.setWeaponUUID(null);
            return null;
        }
        this.weaponMissingTicks = 0;
        return weapon;
    }

    private void feedbackForWeaponChange(TeletorWeaponServantEntity weapon, Player player, InteractionHand hand) {
        this.playSound(SoundEvents.ITEM_PICKUP, 0.6F, 1.4F);
        if (this.level() instanceof ServerLevel serverLevel) {
            Vec3 pos = weapon.position();
            serverLevel.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 8,
                    0.3D, 0.3D, 0.3D, 0.02D);
        }
        player.displayClientMessage(Component.translatable("message.goetyominous.teletor_switched_tool",
                weapon.getItemStack().getHoverName()), true);
        player.swing(hand);
    }

    public Vec3 getWeaponPosition() {
        return this.getEyePosition().add(0.0D, 1.4F - Math.sin(this.tickCount * 0.1F) * 0.2F, 0.0D);
    }

    public Vec3 getHelmetPosition(int offsetFlag) {
        Vec3 helmet = new Vec3(offsetFlag == 0 ? -0.65F : 0.65F, 1.1F, 0.0F)
                .xRot(-this.getXRot() * ((float) Math.PI / 180.0F))
                .yRot(-this.getYHeadRot() * ((float) Math.PI / 180.0F));
        return this.getEyePosition().add(helmet);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("WeaponUUID")) {
            this.setWeaponUUID(compound.getUUID("WeaponUUID"));
        }
        this.forgeBlessed = compound.getBoolean(FORGE_BLESSED_TAG);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getWeaponUUID() != null) {
            compound.putUUID("WeaponUUID", this.getWeaponUUID());
        }
        compound.putBoolean(FORGE_BLESSED_TAG, this.forgeBlessed);
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!this.level().isClientSide && this.getWeapon() instanceof TeletorWeaponServantEntity weapon) {
            weapon.handleOwnerDeath();
        }
        super.die(damageSource);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != ACEffectRegistry.MAGNETIZING.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.TELETOR_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.TELETOR_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.TELETOR_DEATH.get();
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof TeletorServant servant && servant != this) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private class MeleeGoal extends Goal {
        private int executionTime = 0;
        private BlockPos strafeOrigin = null;

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (TeletorServant.this.isStaying()) {
                return false;
            }
            LivingEntity target = TeletorServant.this.getTarget();
            return target != null && target.isAlive() && TeletorServant.this.getWeapon() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return !TeletorServant.this.isStaying() && super.canContinueToUse();
        }

        @Override
        public void start() {
            this.executionTime = 0;
            this.strafeOrigin = null;
        }

        @Override
        public void tick() {
            this.executionTime++;
            LivingEntity target = TeletorServant.this.getTarget();
            double dist = TeletorServant.this.distanceTo(target);
            if (dist < 2.0D) {
                this.strafeOrigin = TeletorServant.this.blockPosition().offset(
                        TeletorServant.this.random.nextInt(16) - 8,
                        TeletorServant.this.random.nextInt(8),
                        TeletorServant.this.random.nextInt(16) - 8);
            }
            if (dist < 16.0D) {
                Vec3 lookDist = target.getEyePosition().subtract(TeletorServant.this.getEyePosition());
                float targetXRot = (float) (-(Mth.atan2(lookDist.y, lookDist.horizontalDistance()) * (180.0D / Math.PI)));
                float targetYRot = (float) (-Mth.atan2(lookDist.x, lookDist.z) * (180.0D / Math.PI));
                TeletorServant.this.getNavigation().stop();
                float f = this.executionTime * 0.1F;
                Vec3 strafe = new Vec3(Math.sin(f) * 5.0F, Math.cos(f) * 2.0F, 0.0D)
                        .yRot(-targetYRot * ((float) Math.PI / 180.0F));
                if (this.strafeOrigin == null) {
                    this.strafeOrigin = TeletorServant.this.blockPosition();
                }
                Vec3 moveTo = Vec3.atCenterOf(this.strafeOrigin).add(strafe);
                TeletorServant.this.getMoveControl().setWantedPosition(moveTo.x, moveTo.y, moveTo.z, 1.0D);
                TeletorServant.this.setXRot(targetXRot);
                TeletorServant.this.setYRot(targetYRot);
            } else {
                this.strafeOrigin = null;
                TeletorServant.this.getNavigation().moveTo(target, 1.0D);
            }
        }
    }

    class MoveController extends MoveControl {
        private final Mob parentEntity;

        public MoveController() {
            super(TeletorServant.this);
            this.parentEntity = TeletorServant.this;
        }

        @Override
        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                // 待命期间身体绝不位移:即便残留了旧的 MOVE_TO 指令也直接转为 WAIT,让本体稳定悬停。
                if (TeletorServant.this.isStaying()) {
                    this.operation = MoveControl.Operation.WAIT;
                    this.parentEntity.setDeltaMovement(Vec3.ZERO);
                    return;
                }
                Vec3 vector3d = new Vec3(this.wantedX - this.parentEntity.getX(),
                        this.wantedY - this.parentEntity.getY(),
                        this.wantedZ - this.parentEntity.getZ());
                double d0 = vector3d.length();
                if (d0 < 1.0E-4D) {
                    // 目标点就在脚下(或根本没设距离):原代码 d0==0 时 0/0 得 NaN → 永久卡死,先落位停住。
                    this.operation = MoveControl.Operation.WAIT;
                    this.parentEntity.setDeltaMovement(Vec3.ZERO);
                    return;
                }
                double width = this.parentEntity.getBoundingBox().getSize();
                LivingEntity attackTarget = this.parentEntity.getTarget();
                Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.025D / d0);
                this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vector3d1));
                if (d0 < width * 0.3F) {
                    this.operation = MoveControl.Operation.WAIT;
                } else if (d0 >= width && attackTarget == null) {
                    this.parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180.0F / (float) Math.PI));
                    if (TeletorServant.this.getTarget() != null) {
                        this.parentEntity.yBodyRot = this.parentEntity.getYRot();
                    }
                }
            }
        }
    }
}
