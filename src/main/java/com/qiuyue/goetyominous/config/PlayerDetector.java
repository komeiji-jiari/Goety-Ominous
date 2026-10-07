package com.qiuyue.goetyominous.config;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public interface PlayerDetector {
    PlayerDetector NO_CREATIVE_PLAYERS = (level, entitySelector, pos, range, requiresLineOfSight) -> entitySelector
            .getPlayers(level, player -> player.blockPosition().closerThan(pos, range)
                    && !player.isCreative() && !player.isSpectator())
            .stream()
            .filter(player -> !requiresLineOfSight || inLineOfSight(level, pos.getCenter(), player.getEyePosition()))
            .map(Entity::getUUID)
            .toList();

    PlayerDetector INCLUDING_CREATIVE_PLAYERS = (level, entitySelector, pos, range, requiresLineOfSight) -> entitySelector
            .getPlayers(level, player -> player.blockPosition().closerThan(pos, range) && !player.isSpectator())
            .stream()
            .filter(player -> !requiresLineOfSight || inLineOfSight(level, pos.getCenter(), player.getEyePosition()))
            .map(Entity::getUUID)
            .toList();

    PlayerDetector SHEEP = (level, entitySelector, pos, range, requiresLineOfSight) -> entitySelector
            .getEntities(level, EntityType.SHEEP, new AABB(pos).inflate(range), LivingEntity::isAlive)
            .stream()
            .filter(sheep -> !requiresLineOfSight || inLineOfSight(level, pos.getCenter(), sheep.getEyePosition()))
            .map(Entity::getUUID)
            .toList();

    List<UUID> detect(ServerLevel level, EntitySelector entitySelector, BlockPos pos, double range, boolean requiresLineOfSight);

    private static boolean inLineOfSight(Level level, Vec3 from, Vec3 to) {
        BlockHitResult hitResult = level.clip(new ClipContext(to, from,
                ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, null));
        return hitResult.getBlockPos().equals(BlockPos.containing(from))
                || hitResult.getType() == HitResult.Type.MISS;
    }

    interface EntitySelector {
        EntitySelector SELECT_FROM_LEVEL = new EntitySelector() {
            @Override
            public List<? extends Player> getPlayers(ServerLevel level, Predicate<? super Player> predicate) {
                return level.getPlayers(predicate);
            }

            @Override
            public <T extends Entity> List<T> getEntities(ServerLevel level, EntityTypeTest<Entity, T> typeTest, AABB bounds, Predicate<? super T> predicate) {
                return level.getEntities(typeTest, bounds, predicate);
            }
        };

        List<? extends Player> getPlayers(ServerLevel level, Predicate<? super Player> predicate);

        <T extends Entity> List<T> getEntities(ServerLevel level, EntityTypeTest<Entity, T> typeTest, AABB bounds, Predicate<? super T> predicate);

        static EntitySelector onlySelectPlayer(Player player) {
            return onlySelectPlayers(List.of(player));
        }

        static EntitySelector onlySelectPlayers(final List<Player> players) {
            return new EntitySelector() {
                @Override
                public List<Player> getPlayers(ServerLevel level, Predicate<? super Player> predicate) {
                    return players.stream().filter(predicate).toList();
                }

                @Override
                public <T extends Entity> List<T> getEntities(ServerLevel level, EntityTypeTest<Entity, T> typeTest, AABB bounds, Predicate<? super T> predicate) {
                    return players.stream().map(typeTest::tryCast).filter(Objects::nonNull).filter(predicate).toList();
                }
            };
        }
    }
}
