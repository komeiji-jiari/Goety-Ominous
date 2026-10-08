package com.qiuyue.goetyominous.common.effects;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class WeavingEffect extends MobEffect {
    private static final int CUBE_SIZE = 15;
    private static final int CUBE_RADIUS = 1;
    private static final int MIN_COBWEBS = 2;
    private static final int MAX_COBWEBS = 3;

    public WeavingEffect() {
        super(MobEffectCategory.HARMFUL, 7891290);
    }

    public void onMobRemoved(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        if (reason != Entity.RemovalReason.KILLED) {
            return;
        }
        if (!(entity instanceof Player) && !entity.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return;
        }
        this.spawnCobwebsRandomlyAround(entity.level(), entity.getRandom(), entity.getOnPos());
    }

    private void spawnCobwebsRandomlyAround(Level level, RandomSource random, BlockPos pos) {
        Set<BlockPos> cobwebs = new HashSet<>();
        int maxCobwebs = Mth.randomBetweenInclusive(random, MIN_COBWEBS, MAX_COBWEBS);
        for (BlockPos candidate : BlockPos.randomInCube(random, CUBE_SIZE, pos, CUBE_RADIUS)) {
            BlockPos below = candidate.below();
            if (cobwebs.contains(candidate) || !level.getBlockState(candidate).canBeReplaced()
                    || !level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
                continue;
            }
            cobwebs.add(candidate.immutable());
            if (cobwebs.size() >= maxCobwebs) {
                break;
            }
        }
        for (BlockPos cobweb : cobwebs) {
            level.setBlock(cobweb, Blocks.COBWEB.defaultBlockState(), 3);
            level.levelEvent(3018, cobweb, 0);
        }
    }
}
