package com.qiuyue.goetyominous.common.blocks.trial;

import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.config.TrialSpawnerConfig;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public enum TrialSpawnerState implements StringRepresentable {
    INACTIVE("inactive", 0, TrialSpawnerState.ParticleEmission.NONE, -1.0D, false),
    WAITING_FOR_PLAYERS("waiting_for_players", 4, TrialSpawnerState.ParticleEmission.SMALL_FLAMES, 200.0D, true),
    ACTIVE("active", 8, TrialSpawnerState.ParticleEmission.FLAMES_AND_SMOKE, 1000.0D, true),
    WAITING_FOR_REWARD_EJECTION("waiting_for_reward_ejection", 8, TrialSpawnerState.ParticleEmission.SMALL_FLAMES, -1.0D, false),
    EJECTING_REWARD("ejecting_reward", 8, TrialSpawnerState.ParticleEmission.SMALL_FLAMES, -1.0D, false),
    COOLDOWN("cooldown", 0, TrialSpawnerState.ParticleEmission.SMOKE_INSIDE_AND_TOP_FACE, -1.0D, false);

    private static final float DELAY_BEFORE_EJECT_AFTER_KILLING_LAST_MOB = 40.0F;
    private static final int TIME_BETWEEN_EACH_EJECTION = Mth.floor(30.0F);
    private final String name;
    private final int lightLevel;
    private final double spinningMobSpeed;
    private final ParticleEmission particleEmission;
    private final boolean isCapableOfSpawning;

    private TrialSpawnerState(String name, int lightLevel, ParticleEmission particleEmission, double spinningMobSpeed,
                              boolean isCapableOfSpawning) {
        this.name = name;
        this.lightLevel = lightLevel;
        this.particleEmission = particleEmission;
        this.spinningMobSpeed = spinningMobSpeed;
        this.isCapableOfSpawning = isCapableOfSpawning;
    }

    TrialSpawnerState tickAndGetNext(BlockPos pos, TrialSpawner spawner, ServerLevel level) {
        TrialSpawnerData data = spawner.getData();
        TrialSpawnerConfig config = spawner.getConfig();
        return switch (this) {
            case INACTIVE -> {
                if (data.getOrCreateDisplayEntity(spawner, level, WAITING_FOR_PLAYERS) == null) {
                    yield this;
                }
                yield WAITING_FOR_PLAYERS;
            }
            case WAITING_FOR_PLAYERS -> {
                if (!spawner.canSpawnInLevel(level)) {
                    data.reset();
                    yield this;
                }
                if (!data.hasMobToSpawn(spawner, level.random)) {
                    yield INACTIVE;
                }
                data.tryDetectPlayers(level, pos, spawner);
                if (data.detectedPlayers.isEmpty()) {
                    yield this;
                }
                yield ACTIVE;
            }
            case ACTIVE -> {
                if (!spawner.canSpawnInLevel(level)) {
                    data.reset();
                    yield WAITING_FOR_PLAYERS;
                }
                if (!data.hasMobToSpawn(spawner, level.random)) {
                    yield INACTIVE;
                }
                int additionalPlayers = data.countAdditionalPlayers(pos);
                data.tryDetectPlayers(level, pos, spawner);
                if (spawner.isOminous()) {
                    this.spawnOminousOminousItemSpawner(level, pos, spawner);
                }
                if (data.hasFinishedSpawningAllMobs(config, additionalPlayers)) {
                    if (data.haveAllCurrentMobsDied()) {
                        data.cooldownEndsAt = level.getGameTime() + (long) spawner.getTargetCooldownLength();
                        data.totalMobsSpawned = 0;
                        data.nextMobSpawnsAt = 0L;
                        yield WAITING_FOR_REWARD_EJECTION;
                    }
                } else if (data.isReadyToSpawnNextMob(level, config, additionalPlayers)) {
                    spawner.spawnMob(level, pos).ifPresent(uuid -> {
                        data.currentMobs.add(uuid);
                        ++data.totalMobsSpawned;
                        data.nextMobSpawnsAt = level.getGameTime() + (long) config.ticksBetweenSpawn();
                        config.spawnPotentialsDefinition().getRandom(level.random).ifPresent(wrapper -> {
                            data.nextSpawnData = Optional.of(wrapper.getData());
                            spawner.markUpdated();
                        });
                    });
                }
                yield this;
            }
            case WAITING_FOR_REWARD_EJECTION -> {
                if (data.isReadyToOpenShutter(level, DELAY_BEFORE_EJECT_AFTER_KILLING_LAST_MOB,
                        spawner.getTargetCooldownLength())) {
                    level.playSound(null, pos, ModSounds.TRIAL_SPAWNER_OPEN_SHUTTER.get(), SoundSource.BLOCKS);
                    yield EJECTING_REWARD;
                }
                yield this;
            }
            case EJECTING_REWARD -> {
                if (!data.isReadyToEjectItems(level, (float) TIME_BETWEEN_EACH_EJECTION,
                        spawner.getTargetCooldownLength())) {
                    yield this;
                }
                if (data.detectedPlayers.isEmpty()) {
                    level.playSound(null, pos, ModSounds.TRIAL_SPAWNER_CLOSE_SHUTTER.get(), SoundSource.BLOCKS);
                    data.ejectingLootTable = Optional.empty();
                    yield COOLDOWN;
                }
                if (data.ejectingLootTable.isEmpty()) {
                    data.ejectingLootTable = config.lootTablesToEject().getRandomValue(level.random);
                }
                data.ejectingLootTable.ifPresent(lootTable -> spawner.ejectReward(level, pos, lootTable));
                data.detectedPlayers.remove(data.detectedPlayers.iterator().next());
                yield this;
            }
            case COOLDOWN -> {
                data.tryDetectPlayers(level, pos, spawner);
                if (!data.detectedPlayers.isEmpty()) {
                    data.totalMobsSpawned = 0;
                    data.nextMobSpawnsAt = 0L;
                    yield ACTIVE;
                }
                if (data.isCooldownFinished(level)) {
                    spawner.removeOminous(level, pos);
                    data.reset();
                    yield WAITING_FOR_PLAYERS;
                }
                yield this;
            }
        };
    }

    private void spawnOminousOminousItemSpawner(ServerLevel level, BlockPos pos, TrialSpawner spawner) {
        TrialSpawnerConfig config = spawner.getConfig();
        TrialSpawnerData data = spawner.getData();
        ItemStack itemStack = data.getDispensingItems(level, config, pos).getRandomValue(level.random).orElse(ItemStack.EMPTY);
        if (itemStack.isEmpty()) {
            return;
        }
        if (!this.timeToSpawnItemSpawner(level, data)) {
            return;
        }
        calculatePositionToSpawnSpawner(level, pos, spawner, data).ifPresent(vec3 -> {
            OminousItemSpawner itemSpawner = OminousItemSpawner.create(level, itemStack);
            itemSpawner.setPos(vec3.x, vec3.y, vec3.z);
            level.addFreshEntity(itemSpawner);
            float pitch = (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F;
            level.playSound(null, BlockPos.containing(vec3), ModSounds.TRIAL_SPAWNER_SPAWN_ITEM_BEGIN.get(),
                    SoundSource.BLOCKS, 1.0F, pitch);
            data.cooldownEndsAt = level.getGameTime() + spawner.getOminousConfig().ticksBetweenItemSpawners();
        });
    }

    private static Optional<Vec3> calculatePositionToSpawnSpawner(ServerLevel level, BlockPos pos, TrialSpawner spawner,
                                                                  TrialSpawnerData spawnerData) {
        List<Player> players = spawnerData.detectedPlayers.stream()
                .map(level::getPlayerByUUID)
                .filter(Objects::nonNull)
                .filter(player -> !player.isSpectator() && !player.isCreative() && player.isAlive()
                        && player.distanceToSqr(pos.getCenter()) <= (double) Mth.square(spawner.getRequiredPlayerRange()))
                .toList();
        if (players.isEmpty()) {
            return Optional.empty();
        }
        Entity entity = selectEntityToSpawnItemAbove(players, spawnerData.currentMobs, spawner, pos, level);
        return entity == null ? Optional.empty() : calculatePositionAbove(entity, level);
    }

    private static Optional<Vec3> calculatePositionAbove(Entity entity, ServerLevel level) {
        Vec3 start = entity.position();
        Vec3 end = start.relative(Direction.UP,
                (double) (entity.getBbHeight() + 2.0F + (float) level.random.nextInt(4)));
        BlockHitResult hitResult = level.clip(new ClipContext(start, end,
                ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null));
        Vec3 target = hitResult.getBlockPos().getCenter().relative(Direction.DOWN, 1.0D);
        BlockPos blockPos = BlockPos.containing(target);
        return !level.getBlockState(blockPos).getCollisionShape(level, blockPos).isEmpty()
                ? Optional.empty()
                : Optional.of(target);
    }

    @Nullable
    private static Entity selectEntityToSpawnItemAbove(List<Player> players, Set<UUID> currentMobs, TrialSpawner spawner,
                                                       BlockPos pos, ServerLevel level) {
        Stream<Entity> stream = currentMobs.stream()
                .map(level::getEntity)
                .filter(Objects::nonNull)
                .filter(entity -> entity.isAlive()
                        && entity.distanceToSqr(pos.getCenter()) <= (double) Mth.square(spawner.getRequiredPlayerRange()));
        List<? extends Entity> list = level.random.nextBoolean() ? stream.toList() : players;
        if (list.isEmpty()) {
            return null;
        }
        return list.size() == 1 ? list.get(0) : Util.getRandom(list, level.random);
    }

    private boolean timeToSpawnItemSpawner(ServerLevel level, TrialSpawnerData spawnerData) {
        return level.getGameTime() >= spawnerData.cooldownEndsAt;
    }

    public int lightLevel() {
        return this.lightLevel;
    }

    public double spinningMobSpeed() {
        return this.spinningMobSpeed;
    }

    public boolean hasSpinningMob() {
        return this.spinningMobSpeed >= 0.0D;
    }

    public boolean isCapableOfSpawning() {
        return this.isCapableOfSpawning;
    }

    public void emitParticles(Level level, BlockPos pos, boolean isOminous) {
        this.particleEmission.emit(level, level.getRandom(), pos, isOminous);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    interface ParticleEmission {
        ParticleEmission NONE = (level, random, pos, isOminous) -> {
        };
        ParticleEmission SMALL_FLAMES = (level, random, pos, isOminous) -> {
            if (random.nextInt(2) == 0) {
                Vec3 vec3 = pos.getCenter().offsetRandom(random, 0.9F);
                emitParticle(isOminous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME, vec3, level);
            }
        };
        ParticleEmission FLAMES_AND_SMOKE = (level, random, pos, isOminous) -> {
            Vec3 vec3 = pos.getCenter().offsetRandom(random, 1.0F);
            emitParticle(ParticleTypes.SMOKE, vec3, level);
            emitParticle(isOminous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, vec3, level);
        };
        ParticleEmission SMOKE_INSIDE_AND_TOP_FACE = (level, random, pos, isOminous) -> {
            Vec3 vec3 = pos.getCenter().offsetRandom(random, 0.9F);
            if (random.nextInt(3) == 0) {
                emitParticle(ParticleTypes.SMOKE, vec3, level);
            }
            if (level.getGameTime() % 20L == 0L) {
                Vec3 vec31 = pos.getCenter().add(0.0D, 0.5D, 0.0D);
                int count = level.random.nextInt(4) + 20;
                for (int i = 0; i < count; ++i) {
                    emitParticle(ParticleTypes.SMOKE, vec31, level);
                }
            }
        };

        static void emitParticle(ParticleOptions particle, Vec3 pos, Level level) {
            level.addParticle(particle, pos.x(), pos.y(), pos.z(), 0.0D, 0.0D, 0.0D);
        }

        void emit(Level level, RandomSource random, BlockPos pos, boolean isOminous);
    }
}
