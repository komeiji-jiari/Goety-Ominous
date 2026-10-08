package com.qiuyue.goetyominous.common.blocks.trial;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.config.PlayerDetector;
import com.qiuyue.goetyominous.config.TrialSpawnerConfig;
import java.util.ArrayList;
import java.util.List;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TrialSpawner {
    public static final String NORMAL_CONFIG_TAG_NAME = "normal_config";
    public static final String OMINOUS_CONFIG_TAG_NAME = "ominous_config";
    public static final int DETECT_PLAYER_SPAWN_BUFFER = 40;
    private static final int DEFAULT_TARGET_COOLDOWN_LENGTH = 36000;
    private static final int DEFAULT_PLAYER_SCAN_RANGE = 14;
    private static final int MAX_MOB_TRACKING_DISTANCE = 47;
    private static final int MAX_MOB_TRACKING_DISTANCE_SQR = Mth.square(47);
    private static final float SPAWNING_AMBIENT_SOUND_CHANCE = 0.02F;
    private final TrialSpawnerConfig normalConfig;
    private final TrialSpawnerConfig ominousConfig;
    private final TrialSpawnerData data;
    private final int requiredPlayerRange;
    private final int targetCooldownLength;
    private final StateAccessor stateAccessor;
    private PlayerDetector playerDetector;
    private final PlayerDetector.EntitySelector entitySelector;
    private boolean overridePeacefulAndMobSpawnRule;
    private boolean isOminous;

    public Codec<TrialSpawner> codec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                TrialSpawnerConfig.CODEC.optionalFieldOf(NORMAL_CONFIG_TAG_NAME, TrialSpawnerConfig.DEFAULT)
                        .forGetter(TrialSpawner::getNormalConfig),
                TrialSpawnerConfig.CODEC.optionalFieldOf(OMINOUS_CONFIG_TAG_NAME, TrialSpawnerConfig.DEFAULT)
                        .forGetter(TrialSpawner::getOminousConfigForSerialization),
                TrialSpawnerData.MAP_CODEC.forGetter(TrialSpawner::getData),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("target_cooldown_length", DEFAULT_TARGET_COOLDOWN_LENGTH)
                        .forGetter(TrialSpawner::getTargetCooldownLength),
                Codec.intRange(1, 128).optionalFieldOf("required_player_range", DEFAULT_PLAYER_SCAN_RANGE)
                        .forGetter(TrialSpawner::getRequiredPlayerRange)
        ).apply(instance, (normalConfig, ominousConfig, data, targetCooldownLength, requiredPlayerRange) ->
                new TrialSpawner(normalConfig, ominousConfig, data, targetCooldownLength, requiredPlayerRange,
                        this.stateAccessor, this.playerDetector, this.entitySelector)));
    }

    public TrialSpawner(StateAccessor stateAccessor, PlayerDetector playerDetector,
                        PlayerDetector.EntitySelector entitySelector) {
        this(TrialSpawnerConfig.DEFAULT, TrialSpawnerConfig.DEFAULT, new TrialSpawnerData(),
                DEFAULT_TARGET_COOLDOWN_LENGTH, DEFAULT_PLAYER_SCAN_RANGE, stateAccessor, playerDetector, entitySelector);
    }

    public TrialSpawner(TrialSpawnerConfig normalConfig, TrialSpawnerConfig ominousConfig, TrialSpawnerData data,
                        int targetCooldownLength, int requiredPlayerRange, StateAccessor stateAccessor,
                        PlayerDetector playerDetector, PlayerDetector.EntitySelector entitySelector) {
        this.normalConfig = normalConfig;
        this.ominousConfig = ominousConfig;
        this.data = data;
        this.targetCooldownLength = targetCooldownLength;
        this.requiredPlayerRange = requiredPlayerRange;
        this.stateAccessor = stateAccessor;
        this.playerDetector = playerDetector;
        this.entitySelector = entitySelector;
    }

    public TrialSpawnerConfig getConfig() {
        return this.isOminous ? this.ominousConfig : this.normalConfig;
    }

    @VisibleForTesting
    public TrialSpawnerConfig getNormalConfig() {
        return this.normalConfig;
    }

    @VisibleForTesting
    public TrialSpawnerConfig getOminousConfig() {
        return this.ominousConfig;
    }

    private TrialSpawnerConfig getOminousConfigForSerialization() {
        return !this.ominousConfig.equals(this.normalConfig) ? this.ominousConfig : TrialSpawnerConfig.DEFAULT;
    }

    public void applyOminous(ServerLevel level, BlockPos pos) {
        level.setBlock(pos, level.getBlockState(pos).setValue(TrialSpawnerBlock.OMINOUS, Boolean.TRUE), 3);
        level.levelEvent(3020, pos, 1);
        this.isOminous = true;
        this.data.resetAfterBecomingOminous(this, level);
    }

    public void removeOminous(ServerLevel level, BlockPos pos) {
        level.setBlock(pos, level.getBlockState(pos).setValue(TrialSpawnerBlock.OMINOUS, Boolean.FALSE), 3);
        this.isOminous = false;
    }

    public boolean isOminous() {
        return this.isOminous;
    }

    public TrialSpawnerData getData() {
        return this.data;
    }

    public int getTargetCooldownLength() {
        return this.targetCooldownLength;
    }

    public int getRequiredPlayerRange() {
        return this.requiredPlayerRange;
    }

    public TrialSpawnerState getState() {
        return this.stateAccessor.getState();
    }

    public void setState(Level level, TrialSpawnerState state) {
        this.stateAccessor.setState(level, state);
    }

    public void markUpdated() {
        this.stateAccessor.markUpdated();
    }

    public PlayerDetector getPlayerDetector() {
        return this.playerDetector;
    }

    public PlayerDetector.EntitySelector getEntitySelector() {
        return this.entitySelector;
    }

    public boolean canSpawnInLevel(Level level) {
        if (this.overridePeacefulAndMobSpawnRule) {
            return true;
        }
        return level.getDifficulty() == Difficulty.PEACEFUL
                ? false
                : level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
    }

    public Optional<UUID> spawnMob(ServerLevel level, BlockPos pos) {
        RandomSource random = level.getRandom();
        SpawnData spawnData = this.data.getOrCreateNextSpawnData(this, level.getRandom());
        CompoundTag tag = spawnData.getEntityToSpawn();
        ListTag posList = tag.getList("Pos", 6);
        Optional<EntityType<?>> optional = EntityType.by(tag);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        int posCount = posList.size();
        double x = posCount >= 1 ? posList.getDouble(0)
                : (double) pos.getX() + (random.nextDouble() - random.nextDouble()) * (double) this.getConfig().spawnRange() + 0.5D;
        double y = posCount >= 2 ? posList.getDouble(1)
                : (double) (pos.getY() + random.nextInt(3) - 1);
        double z = posCount >= 3 ? posList.getDouble(2)
                : (double) pos.getZ() + (random.nextDouble() - random.nextDouble()) * (double) this.getConfig().spawnRange() + 0.5D;
        if (!level.noCollision(optional.get().getAABB(x, y, z))) {
            return Optional.empty();
        }
        Vec3 spawnPos = new Vec3(x, y, z);
        if (!inLineOfSight(level, pos.getCenter(), spawnPos)) {
            return Optional.empty();
        }
        BlockPos spawnBlockPos = BlockPos.containing(spawnPos);
        if (!checkSpawnRulesForTrialSpawner(optional.get(), level, spawnBlockPos, level.getRandom())) {
            return Optional.empty();
        }
        if (spawnData.getCustomSpawnRules().isPresent()) {
            SpawnData.CustomSpawnRules customSpawnRules = spawnData.getCustomSpawnRules().get();
            if (!customSpawnRules.blockLightLimit().isValueInRange(level.getBrightness(LightLayer.BLOCK, pos))
                    || !customSpawnRules.skyLightLimit().isValueInRange(level.getBrightness(LightLayer.SKY, pos))) {
                return Optional.empty();
            }
        }
        Entity entity = EntityType.loadEntityRecursive(tag, level, loaded -> {
            loaded.moveTo(x, y, z, random.nextFloat() * 360.0F, 0.0F);
            return loaded;
        });
        if (entity == null) {
            return Optional.empty();
        }
        if (entity instanceof Mob mob) {
            if (!mob.checkSpawnObstruction(level)) {
                return Optional.empty();
            }
            boolean hasNoCustomNbt = spawnData.getEntityToSpawn().size() == 1
                    && spawnData.getEntityToSpawn().contains("id", 8);
            if (hasNoCustomNbt) {
                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()),
                        MobSpawnType.SPAWNER, null, null);
            }
            mob.setPersistenceRequired();
            this.data.getNextSpawnEquipment().ifPresent(table -> equipMob(mob, table, level));
        }
        if (!level.addFreshEntity(entity)) {
            return Optional.empty();
        }
        FlameParticle flameParticle = this.isOminous ? FlameParticle.OMINOUS : FlameParticle.NORMAL;
        level.levelEvent(3011, pos, flameParticle.getId());
        level.levelEvent(3012, spawnBlockPos, flameParticle.getId());
        level.gameEvent(entity, GameEvent.ENTITY_PLACE, spawnBlockPos);
        return Optional.of(entity.getUUID());
    }

    private static boolean checkSpawnRulesForTrialSpawner(EntityType<?> type, ServerLevel level, BlockPos pos, RandomSource random) {
        if (type.getCategory() == MobCategory.MONSTER) {
            return level.getDifficulty() != Difficulty.PEACEFUL;
        }
        return SpawnPlacements.checkSpawnRules(type, level, MobSpawnType.SPAWNER, pos, random);
    }

    private static void equipMob(Mob mob, EquipmentTable table, ServerLevel level) {
        if (table.lootTable().equals(BuiltInLootTables.EMPTY)) {
            return;
        }
        LootTable lootTable = level.getServer().getLootData().getLootTable(table.lootTable());
        if (lootTable == LootTable.EMPTY) {
            return;
        }
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, mob.position())
                .withParameter(LootContextParams.THIS_ENTITY, mob)
                .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic())
                .create(LootContextParamSets.ENTITY);
        ObjectArrayList<ItemStack> items = lootTable.getRandomItems(params);
        List<EquipmentSlot> usedSlots = new ArrayList<>();
        for (ItemStack stack : items) {
            EquipmentSlot slot = resolveEquipmentSlot(stack, usedSlots);
            if (slot == null) {
                continue;
            }
            if (slot.getType() == EquipmentSlot.Type.ARMOR || slot == EquipmentSlot.OFFHAND) {
                stack.setCount(1);
            }
            mob.setItemSlot(slot, stack);
            Float chance = table.slotDropChances().get(slot);
            if (chance != null) {
                mob.setDropChance(slot, chance);
            }
            usedSlots.add(slot);
        }
    }

    @Nullable
    private static EquipmentSlot resolveEquipmentSlot(ItemStack stack, List<EquipmentSlot> usedSlots) {
        if (stack.isEmpty()) {
            return null;
        }
        EquipmentSlot slot;
        if (stack.getItem() instanceof ArmorItem armorItem) {
            slot = armorItem.getEquipmentSlot();
        } else if (stack.is(Items.SHIELD)) {
            slot = EquipmentSlot.OFFHAND;
        } else {
            slot = stack.getItem().getEquipmentSlot(stack);
            if (slot == null) {
                slot = EquipmentSlot.MAINHAND;
            }
        }
        return usedSlots.contains(slot) ? null : slot;
    }

    public void ejectReward(ServerLevel level, BlockPos pos, ResourceLocation lootTable) {
        LootTable table = level.getServer().getLootData().getLootTable(lootTable);
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        ObjectArrayList<ItemStack> items = table.getRandomItems(params);
        if (!items.isEmpty()) {
            for (ItemStack stack : items) {
                DefaultDispenseItemBehavior.spawnItem(level, stack, 2, Direction.UP,
                        Vec3.atBottomCenterOf(pos).relative(Direction.UP, 1.2D));
            }
            level.levelEvent(3014, pos, 0);
        }
    }

    public void tickClient(Level level, BlockPos pos, boolean isOminous) {
        TrialSpawnerState state = this.getState();
        state.emitParticles(level, pos, isOminous);
        if (state.hasSpinningMob()) {
            double delay = Math.max(0L, this.data.nextMobSpawnsAt - level.getGameTime());
            this.data.oSpin = this.data.spin;
            this.data.spin = (this.data.spin + state.spinningMobSpeed() / (delay + 200.0D)) % 360.0D;
        }
        if (state.isCapableOfSpawning()) {
            RandomSource random = level.getRandom();
            if (random.nextFloat() <= SPAWNING_AMBIENT_SOUND_CHANCE) {
                SoundEvent sound = isOminous
                        ? ModSounds.TRIAL_SPAWNER_AMBIENT_OMINOUS.get()
                        : ModSounds.TRIAL_SPAWNER_AMBIENT.get();
                level.playLocalSound(pos, sound, SoundSource.BLOCKS,
                        random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
            }
        }
    }

    public void tickServer(ServerLevel level, BlockPos pos, boolean isOminous) {
        this.isOminous = isOminous;
        TrialSpawnerState state = this.getState();
        if (!this.canSpawnInLevel(level)) {
            if (state.isCapableOfSpawning()) {
                this.data.resetKeepingSpawnData();
                this.setState(level, TrialSpawnerState.INACTIVE);
            }
            return;
        }
        if (this.data.currentMobs.removeIf(uuid -> shouldMobBeUntracked(level, pos, uuid))) {
            this.data.nextMobSpawnsAt = level.getGameTime() + (long) this.getConfig().ticksBetweenSpawn();
        }
        TrialSpawnerState nextState = state.tickAndGetNext(pos, this, level);
        if (nextState != state) {
            this.setState(level, nextState);
        }
    }

    private static boolean shouldMobBeUntracked(ServerLevel level, BlockPos pos, UUID uuid) {
        Entity entity = level.getEntity(uuid);
        return entity == null
                || !entity.isAlive()
                || !entity.level().dimension().equals(level.dimension())
                || entity.blockPosition().distSqr(pos) > (double) MAX_MOB_TRACKING_DISTANCE_SQR;
    }

    private static boolean inLineOfSight(Level level, Vec3 spawnerPos, Vec3 mobPos) {
        BlockHitResult hitResult = level.clip(new ClipContext(mobPos, spawnerPos,
                ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null));
        return hitResult.getBlockPos().equals(BlockPos.containing(spawnerPos))
                || hitResult.getType() == HitResult.Type.MISS;
    }

    public static void addSpawnParticles(Level level, BlockPos pos, RandomSource random, SimpleParticleType particleType) {
        for (int i = 0; i < 20; ++i) {
            double x = (double) pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
            double y = (double) pos.getY() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
            double z = (double) pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
            level.addParticle(particleType, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    public static void addBecomeOminousParticles(Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 20; ++i) {
            double x = (double) pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
            double y = (double) pos.getY() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
            double z = (double) pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D;
            double xd = random.nextGaussian() * 0.02D;
            double yd = random.nextGaussian() * 0.02D;
            double zd = random.nextGaussian() * 0.02D;
            level.addParticle(ModParticleTypes.TRIAL_OMEN.get(), x, y, z, xd, yd, zd);
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, xd, yd, zd);
        }
    }

    public static void addDetectPlayerParticles(Level level, BlockPos pos, RandomSource random, int data,
                                                ParticleOptions particle) {
        for (int i = 0; i < 30 + Math.min(data, 10) * 5; ++i) {
            double offsetX = (2.0F * random.nextFloat() - 1.0F) * 0.65D;
            double offsetZ = (2.0F * random.nextFloat() - 1.0F) * 0.65D;
            double x = (double) pos.getX() + 0.5D + offsetX;
            double y = (double) pos.getY() + 0.1D + (double) random.nextFloat() * 0.8D;
            double z = (double) pos.getZ() + 0.5D + offsetZ;
            level.addParticle(particle, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    public static void addEjectItemParticles(Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 20; ++i) {
            double x = (double) pos.getX() + 0.4D + random.nextDouble() * 0.2D;
            double y = (double) pos.getY() + 0.4D + random.nextDouble() * 0.2D;
            double z = (double) pos.getZ() + 0.4D + random.nextDouble() * 0.2D;
            double xd = random.nextGaussian() * 0.02D;
            double yd = random.nextGaussian() * 0.02D;
            double zd = random.nextGaussian() * 0.02D;
            level.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, xd, yd, zd * 0.25D);
            level.addParticle(ParticleTypes.SMOKE, x, y, z, xd, yd, zd);
        }
    }

    @Deprecated(forRemoval = true)
    @VisibleForTesting
    public void setPlayerDetector(PlayerDetector playerDetector) {
        this.playerDetector = playerDetector;
    }

    @Deprecated(forRemoval = true)
    @VisibleForTesting
    public void overridePeacefulAndMobSpawnRule() {
        this.overridePeacefulAndMobSpawnRule = true;
    }

    public enum FlameParticle {
        NORMAL(ParticleTypes.SMALL_FLAME),
        OMINOUS(ParticleTypes.SOUL_FIRE_FLAME);

        public final SimpleParticleType particleType;

        FlameParticle(SimpleParticleType particleType) {
            this.particleType = particleType;
        }

        public static FlameParticle byId(int id) {
            FlameParticle[] values = values();
            if (id > values.length || id < 0) {
                return NORMAL;
            }
            return values[id];
        }

        public int getId() {
            return this.ordinal();
        }
    }

    public interface StateAccessor {
        void setState(Level level, TrialSpawnerState state);

        TrialSpawnerState getState();

        void markUpdated();
    }
}
