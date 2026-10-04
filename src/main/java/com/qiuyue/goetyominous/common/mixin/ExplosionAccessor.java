package com.qiuyue.goetyominous.common.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Explosion.class)
public interface ExplosionAccessor {
    @Accessor("level") Level getLevel();
    @Accessor("x") double getX();
    @Accessor("y") double getY();
    @Accessor("z") double getZ();
    @Accessor("radius") float getRadius();
    @Accessor("source") Entity getSource();
    @Accessor("damageCalculator") ExplosionDamageCalculator getDamageCalculator();
    @Accessor("toBlow") ObjectArrayList<BlockPos> getToBlow();
    @Accessor("hitPlayers") Map<Player, Vec3> getHitPlayers();
}
