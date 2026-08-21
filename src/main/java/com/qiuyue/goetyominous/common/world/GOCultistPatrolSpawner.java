package com.qiuyue.goetyominous.common.world;

import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;

import java.util.List;

public class GOCultistPatrolSpawner {
    private int ticksUntilSpawn;

    private static final List<EntityType<? extends PatrollingMonster>> MELEE_TYPES = List.of(
            ModEntityTypes.FANATIC.get(),
            ModEntityTypes.ZEALOT.get(),
            ModEntityTypes.THUG.get());

    private static final List<EntityType<? extends PatrollingMonster>> CASTER_TYPES = List.of(
            ModEntityTypes.BELDAM.get(),
            ModEntityTypes.CHANNELLER.get());

    private static final List<EntityType<? extends PatrollingMonster>> ELITE_TYPES = List.of(
            ModEntityTypes.DISCIPLE.get());

    public int tick(ServerLevel level) {
        RandomSource random = level.random;
        if (!MobsConfig.CultistPatrol.get()) return 0;

        --this.ticksUntilSpawn;
        if (this.ticksUntilSpawn > 0) return 0;
        this.ticksUntilSpawn += MobsConfig.CultistPatrolInterval.get() + random.nextInt(1200);

        if (level.getDayTime() / 24000L < 5) return 0;
        if (!level.isNight()) return 0;
        if (random.nextInt(5) != 0) return 0;

        int playerCount = level.players().size();
        if (playerCount < 1) return 0;
        Player player = level.players().get(random.nextInt(playerCount));
        if (player.isSpectator()) return 0;
        if (level.isCloseToVillage(player.blockPosition(), 2)) return 0;

        int xOffset = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        int zOffset = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        BlockPos.MutableBlockPos pos = player.blockPosition().mutable().move(xOffset, 0, zOffset);

        if (!level.hasChunksAt(pos.getX() - 10, pos.getZ() - 10, pos.getX() + 10, pos.getZ() + 10)) return 0;

        Holder<Biome> biome = level.getBiome(pos);
        if (biome.is(BiomeTags.WITHOUT_PATROL_SPAWNS)) return 0;

        return this.spawnPatrolAt(level, pos, false);
    }

    public int spawnPatrolAt(ServerLevel level, BlockPos center, boolean forced) {
        RandomSource random = level.random;
        BlockPos.MutableBlockPos pos = center.mutable();
        int spawned = 0;
        int size = (int) Math.ceil(level.getCurrentDifficultyAt(pos).getEffectiveDifficulty()) + 1;
        for (int i = 0; i < size; ++i) {
            ++spawned;
            pos.setY(level.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, pos).getY());
            if (i == 0) {
                if (!this.spawnPatrolMember(level, pos, random, true, MELEE_TYPES, forced)) break;
            } else {
                this.spawnPatrolMember(level, pos, random, false, MELEE_TYPES, forced);
            }
            pos.setX(pos.getX() + random.nextInt(5) - random.nextInt(5));
            pos.setZ(pos.getZ() + random.nextInt(5) - random.nextInt(5));
        }

        if (size >= 3) {
            pos.setY(level.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, pos).getY());
            pos.setX(pos.getX() + random.nextInt(5) - random.nextInt(5));
            pos.setZ(pos.getZ() + random.nextInt(5) - random.nextInt(5));
            if (this.spawnPatrolMember(level, pos, random, false, CASTER_TYPES, forced)) {
                ++spawned;
            }
        }

        if (level.getDifficulty() == Difficulty.HARD
                && spawned > 0
                && random.nextFloat() < 0.05F) {
            pos.setY(level.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, pos).getY());
            pos.setX(pos.getX() + random.nextInt(5) - random.nextInt(5));
            pos.setZ(pos.getZ() + random.nextInt(5) - random.nextInt(5));
            if (this.spawnPatrolMember(level, pos, random, false, ELITE_TYPES, forced)) {
                ++spawned;
            }
        }
        return spawned;
    }

    private boolean spawnPatrolMember(ServerLevel level, BlockPos pos, RandomSource random, boolean leader,
                                      List<EntityType<? extends PatrollingMonster>> types, boolean forced) {
        EntityType<? extends PatrollingMonster> type = types.get(random.nextInt(types.size()));
        if (!forced) {
            BlockState state = level.getBlockState(pos);
            if (!NaturalSpawner.isValidEmptySpawnBlock(level, pos, state, state.getFluidState(), type)) return false;
            if (!PatrollingMonster.checkPatrollingMonsterSpawnRules(type, level, MobSpawnType.PATROL, pos, random)) return false;
            if (level.getMaxLocalRawBrightness(pos) > 7) return false;
        }

        PatrollingMonster cultist = type.create(level);
        if (cultist != null) {
            if (leader) {
                cultist.setPatrolLeader(true);
                cultist.findPatrolTarget();
            }
            cultist.setPos(pos.getX(), pos.getY(), pos.getZ());
            cultist.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.PATROL, null, null);
            level.addFreshEntityWithPassengers(cultist);
            return true;
        }
        return false;
    }
}
