/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.magic.SpellType
 *  com.Polarice3.Goety.common.enchantments.ModEnchantments
 *  com.Polarice3.Goety.common.magic.BlockSpell
 *  com.Polarice3.Goety.common.magic.SpellStat
 *  com.Polarice3.Goety.init.ModSounds
 *  com.Polarice3.Goety.utils.WandUtil
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.SnowLayerBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.BlockSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class TerraformingSpell
extends BlockSpell {
    private static final int BASE_RADIUS = 5;
    private static final int VERTICAL_RANGE = 5;
    private static final int COLUMNS_PER_TICK = 10;
    private static final int MAX_MOVES_PER_TICK = 48;
    private static final List<TerraformingJob> JOBS = new ArrayList<TerraformingJob>();

    public int defaultSoulCost() {
        return 4;
    }

    public SoundEvent CastingSound() {
        return (SoundEvent)ModSounds.PREPARE_SPELL.get();
    }

    public int defaultSpellCooldown() {
        return 100;
    }

    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    public List<Enchantment> acceptedEnchantments() {
        ArrayList<Enchantment> list = new ArrayList<Enchantment>();
        list.add((Enchantment)ModEnchantments.RADIUS.get());
        return list;
    }

    public boolean rightBlock(ServerLevel worldIn, LivingEntity caster, BlockPos target, Direction direction, SpellStat spellStat) {
        return TerraformingSpell.isValidTerrainBlock(worldIn, target);
    }

    public void blockResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, BlockPos target, Direction direction, SpellStat spellStat) {
        int radius = 5;
        if (WandUtil.enchantedFocus((LivingEntity)caster)) {
            radius += 3 * WandUtil.getLevels((Enchantment)((Enchantment)ModEnchantments.RADIUS.get()), (LivingEntity)caster);
        }
        if (this.rightStaff(staff)) {
            radius = (int)((double)radius * 1.5);
        }
        int targetHeight = target.m_123342_();
        if (caster.m_6144_()) {
            --targetHeight;
        }
        JOBS.add(new TerraformingJob(worldIn, target.m_7949_(), radius, targetHeight));
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Iterator<TerraformingJob> iterator = JOBS.iterator();
        while (iterator.hasNext()) {
            TerraformingJob job = iterator.next();
            if (!job.tick()) continue;
            iterator.remove();
        }
    }

    private static BlockPos findBestMovableSource(ServerLevel worldIn, BlockPos center, int spellRadius, BlockPos destination) {
        ArrayList<BlockPos> candidates = new ArrayList<BlockPos>();
        int minY = TerraformingSpell.minTerraformY(worldIn, center);
        int maxY = TerraformingSpell.maxTerraformY(worldIn, center);
        for (int y = minY; y <= maxY; ++y) {
            for (int x = -spellRadius; x <= spellRadius; ++x) {
                for (int z = -spellRadius; z <= spellRadius; ++z) {
                    BlockPos candidate = new BlockPos(center.m_123341_() + x, y, center.m_123343_() + z);
                    if (!TerraformingSpell.isInsideCircle(center, candidate, spellRadius) || candidate.equals((Object)destination) || !TerraformingSpell.isValidTerrainBlock(worldIn, candidate) || !worldIn.m_8055_(candidate.m_7494_()).m_60795_()) continue;
                    candidates.add(candidate);
                }
            }
        }
        candidates.sort(Comparator.comparingInt(pos -> TerraformingSpell.sourceSurfacePriority(worldIn, pos)).thenComparingDouble(pos -> pos.m_123331_((Vec3i)destination)).thenComparingDouble(pos -> TerraformingSpell.getHardnessSafe(worldIn, pos)));
        if (candidates.isEmpty()) {
            return null;
        }
        return (BlockPos)candidates.get(0);
    }

    private static BlockPos findNearestFreeDestination(ServerLevel worldIn, BlockPos center, int radius, BlockPos source, int preferredY) {
        ArrayList<BlockPos> candidates = new ArrayList<BlockPos>();
        int minY = TerraformingSpell.minTerraformY(worldIn, center);
        int maxY = TerraformingSpell.maxTerraformY(worldIn, center);
        for (int y = minY; y <= maxY; ++y) {
            for (int x = -radius; x <= radius; ++x) {
                for (int z = -radius; z <= radius; ++z) {
                    BlockPos candidate = new BlockPos(center.m_123341_() + x, y, center.m_123343_() + z);
                    if (!TerraformingSpell.isInsideCircle(center, candidate, radius) || !worldIn.m_8055_(candidate).m_60795_()) continue;
                    candidates.add(candidate);
                }
            }
        }
        candidates.sort(Comparator.comparingInt(pos -> Math.abs(pos.m_123342_() - preferredY)).thenComparingDouble(pos -> pos.m_123331_((Vec3i)source)));
        if (candidates.isEmpty()) {
            return null;
        }
        return (BlockPos)candidates.get(0);
    }

    private static BlockPos findSurface(ServerLevel worldIn, BlockPos columnPos, BlockPos center, int centerY) {
        int maxY;
        int minY = TerraformingSpell.minTerraformY(worldIn, center);
        for (int y = maxY = TerraformingSpell.maxTerraformY(worldIn, center); y >= minY; --y) {
            BlockPos pos = new BlockPos(columnPos.m_123341_(), y, columnPos.m_123343_());
            if (!TerraformingSpell.isValidTerrainBlock(worldIn, pos) || !worldIn.m_8055_(pos.m_7494_()).m_60795_()) continue;
            return pos;
        }
        return null;
    }

    private static boolean moveBlock(ServerLevel worldIn, BlockPos from, BlockPos to) {
        if (from.equals((Object)to)) {
            return false;
        }
        if (!TerraformingSpell.isValidTerrainBlock(worldIn, from)) {
            return false;
        }
        if (!worldIn.m_8055_(to).m_60795_()) {
            return false;
        }
        if (worldIn.m_7702_(from) != null || worldIn.m_7702_(to) != null) {
            return false;
        }
        BlockState state = worldIn.m_8055_(from);
        worldIn.m_46796_(2001, from, Block.m_49956_((BlockState)state));
        worldIn.m_7731_(to, state, 3);
        worldIn.m_7731_(from, Blocks.f_50016_.m_49966_(), 3);
        return true;
    }

    private static boolean collapseFloatingBlocksStep(ServerLevel worldIn, BlockPos target, int radius, int moveBudget) {
        int minY = TerraformingSpell.minTerraformY(worldIn, target);
        int maxY = TerraformingSpell.maxTerraformY(worldIn, target);
        int moves = 0;
        boolean moved = false;
        for (int y = minY; y <= maxY; ++y) {
            for (int x = -radius; x <= radius; ++x) {
                for (int z = -radius; z <= radius; ++z) {
                    if (moves >= moveBudget) {
                        return moved;
                    }
                    BlockPos pos = new BlockPos(target.m_123341_() + x, y, target.m_123343_() + z);
                    BlockPos below = pos.m_7495_();
                    if (!TerraformingSpell.isInsideCircle(target, pos, radius) || !TerraformingSpell.isValidTerrainBlock(worldIn, pos) || !worldIn.m_8055_(below).m_60795_() || below.m_123342_() < minY || !TerraformingSpell.moveBlock(worldIn, pos, below)) continue;
                    moved = true;
                    ++moves;
                }
            }
        }
        return moved;
    }

    private static void reorderSurfaceBlocksStep(ServerLevel worldIn, BlockPos target, int radius, int moveBudget) {
        ArrayList<BlockPos> surfaces = new ArrayList<BlockPos>();
        int minY = TerraformingSpell.minTerraformY(worldIn, target);
        int maxY = TerraformingSpell.maxTerraformY(worldIn, target);
        for (int y = minY; y <= maxY; ++y) {
            for (int x = -radius; x <= radius; ++x) {
                for (int z = -radius; z <= radius; ++z) {
                    BlockPos pos2 = new BlockPos(target.m_123341_() + x, y, target.m_123343_() + z);
                    if (!TerraformingSpell.isInsideCircle(target, pos2, radius) || !TerraformingSpell.isValidTerrainBlock(worldIn, pos2) || !worldIn.m_8055_(pos2.m_7494_()).m_60795_()) continue;
                    surfaces.add(pos2);
                }
            }
        }
        surfaces.sort(Comparator.comparingInt(pos -> pos.m_123342_()).reversed());
        int swaps = 0;
        for (BlockPos surface : surfaces) {
            if (swaps >= moveBudget) {
                return;
            }
            if (!TerraformingSpell.tryPromoteBestSurfaceBlock(worldIn, target, radius, surface)) continue;
            ++swaps;
        }
    }

    private static boolean tryPromoteBestSurfaceBlock(ServerLevel worldIn, BlockPos target, int radius, BlockPos surface) {
        int maxY;
        BlockState surfaceState = worldIn.m_8055_(surface);
        if (TerraformingSpell.isSurfaceLayerBlock(surfaceState)) {
            return false;
        }
        BlockPos best = null;
        int minY = TerraformingSpell.minTerraformY(worldIn, target);
        for (int y = maxY = surface.m_123342_() - 1; y >= minY; --y) {
            BlockState candidateState;
            BlockPos candidate = new BlockPos(surface.m_123341_(), y, surface.m_123343_());
            if (!TerraformingSpell.isInsideCircle(target, candidate, radius) || !TerraformingSpell.isValidTerrainBlock(worldIn, candidate) || !TerraformingSpell.isSurfaceLayerBlock(candidateState = worldIn.m_8055_(candidate))) continue;
            best = candidate;
            break;
        }
        if (best == null) {
            return false;
        }
        BlockState lowerState = worldIn.m_8055_(best);
        BlockState upperState = worldIn.m_8055_(surface);
        worldIn.m_7731_(surface, lowerState, 3);
        worldIn.m_7731_(best, upperState, 3);
        return true;
    }

    private static int sourceSurfacePriority(ServerLevel worldIn, BlockPos pos) {
        BlockState state = worldIn.m_8055_(pos);
        if (TerraformingSpell.isSurfaceLayerBlock(state)) {
            return 4;
        }
        if (TerraformingSpell.getHardnessSafe(worldIn, pos) <= 0.7) {
            return 3;
        }
        if (TerraformingSpell.getHardnessSafe(worldIn, pos) <= 1.5) {
            return 2;
        }
        return 1;
    }

    private static int minTerraformY(ServerLevel worldIn, BlockPos target) {
        return Math.max(worldIn.m_141937_() + 1, target.m_123342_() - 5);
    }

    private static int maxTerraformY(ServerLevel worldIn, BlockPos target) {
        return Math.min(worldIn.m_151558_() - 2, target.m_123342_() + 5);
    }

    private static boolean isInsideCircle(BlockPos center, BlockPos pos, int radius) {
        int dz;
        int dx = pos.m_123341_() - center.m_123341_();
        return Math.sqrt(dx * dx + (dz = pos.m_123343_() - center.m_123343_()) * dz) <= (double)radius;
    }

    private static boolean isSurfaceLayerBlock(BlockState state) {
        if (state.m_60713_(Blocks.f_50440_)) {
            return true;
        }
        if (state.m_60713_(Blocks.f_50599_)) {
            return true;
        }
        if (state.m_60713_(Blocks.f_50195_)) {
            return true;
        }
        if (state.m_60713_(Blocks.f_50125_)) {
            return true;
        }
        if (state.m_60734_() instanceof SnowLayerBlock) {
            return true;
        }
        return state.m_204336_(BlockTags.f_144274_);
    }

    private static double getHardnessSafe(ServerLevel worldIn, BlockPos pos) {
        BlockState state = worldIn.m_8055_(pos);
        float hardness = state.m_60800_((BlockGetter)worldIn, pos);
        if (hardness < 0.0f) {
            return 999999.0;
        }
        return hardness;
    }

    private static boolean isValidTerrainBlock(ServerLevel worldIn, BlockPos pos) {
        BlockState state = worldIn.m_8055_(pos);
        if (state.m_60795_()) {
            return false;
        }
        if (worldIn.m_7702_(pos) != null) {
            return false;
        }
        if (state.m_60800_((BlockGetter)worldIn, pos) < 0.0f) {
            return false;
        }
        return state.m_60804_((BlockGetter)worldIn, pos) || state.m_60734_() instanceof SnowLayerBlock;
    }

    private static class TerraformingJob {
        private final ServerLevel world;
        private final BlockPos target;
        private final int radius;
        private final int targetHeight;
        private final List<ColumnTarget> columns = new ArrayList<ColumnTarget>();
        private int columnIndex = 0;
        private int collapsePasses = 0;
        private boolean collapsing = false;
        private boolean reordering = false;
        private boolean finished = false;

        private TerraformingJob(ServerLevel world, BlockPos target, int radius, int targetHeight) {
            this.world = world;
            this.target = target;
            this.radius = radius;
            this.targetHeight = targetHeight;
            this.collectColumns();
        }

        private void collectColumns() {
            for (int x = -this.radius; x <= this.radius; ++x) {
                for (int z = -this.radius; z <= this.radius; ++z) {
                    BlockPos columnPos = this.target.m_7918_(x, 0, z);
                    if (!TerraformingSpell.isInsideCircle(this.target, columnPos, this.radius)) continue;
                    double distance = Math.sqrt(x * x + z * z);
                    double strength = 1.0 - distance / (double)this.radius;
                    this.columns.add(new ColumnTarget(columnPos.m_7949_(), distance, strength));
                }
            }
            this.columns.sort(Comparator.comparingDouble(column -> column.distance).thenComparingInt(column -> column.columnPos.m_123341_()).thenComparingInt(column -> column.columnPos.m_123343_()));
        }

        private boolean tick() {
            if (this.finished) {
                return true;
            }
            if (!this.collapsing && !this.reordering) {
                this.processColumns();
                if (this.columnIndex >= this.columns.size()) {
                    this.collapsing = true;
                }
                return false;
            }
            if (this.collapsing) {
                boolean moved = TerraformingSpell.collapseFloatingBlocksStep(this.world, this.target, this.radius, 48);
                ++this.collapsePasses;
                if (!moved || this.collapsePasses >= 8) {
                    this.collapsing = false;
                    this.reordering = true;
                }
                return false;
            }
            if (this.reordering) {
                TerraformingSpell.reorderSurfaceBlocksStep(this.world, this.target, this.radius, 48);
                this.finished = true;
                return true;
            }
            return false;
        }

        private void processColumns() {
            int processedColumns = 0;
            int usedMoves = 0;
            while (this.columnIndex < this.columns.size() && processedColumns < 10 && usedMoves < 48) {
                ColumnTarget column = this.columns.get(this.columnIndex);
                ++this.columnIndex;
                ++processedColumns;
                BlockPos surface = TerraformingSpell.findSurface(this.world, column.columnPos, this.target, this.targetHeight);
                if (surface == null) continue;
                int currentHeight = surface.m_123342_();
                int wantedHeight = currentHeight + (int)Math.round((double)(this.targetHeight - currentHeight) * column.strength);
                if (wantedHeight > TerraformingSpell.maxTerraformY(this.world, this.target)) {
                    wantedHeight = TerraformingSpell.maxTerraformY(this.world, this.target);
                }
                if (wantedHeight < TerraformingSpell.minTerraformY(this.world, this.target)) {
                    wantedHeight = TerraformingSpell.minTerraformY(this.world, this.target);
                }
                if (wantedHeight > currentHeight) {
                    usedMoves += this.raiseColumn(surface, wantedHeight, 48 - usedMoves);
                    continue;
                }
                if (wantedHeight >= currentHeight || !this.lowerColumn(surface, wantedHeight)) continue;
                ++usedMoves;
            }
        }

        private int raiseColumn(BlockPos surface, int wantedHeight, int moveBudget) {
            int moves = 0;
            for (int y = surface.m_123342_() + 1; y <= wantedHeight && moves < moveBudget; ++y) {
                BlockPos source;
                BlockPos destination = new BlockPos(surface.m_123341_(), y, surface.m_123343_());
                if (!TerraformingSpell.isInsideCircle(this.target, destination, this.radius) || destination.m_123342_() < TerraformingSpell.minTerraformY(this.world, this.target) || destination.m_123342_() > TerraformingSpell.maxTerraformY(this.world, this.target) || !this.world.m_8055_(destination).m_60795_() || (source = TerraformingSpell.findBestMovableSource(this.world, this.target, this.radius, destination)) == null || !TerraformingSpell.moveBlock(this.world, source, destination)) continue;
                ++moves;
            }
            return moves;
        }

        private boolean lowerColumn(BlockPos surface, int wantedHeight) {
            BlockPos destination = new BlockPos(surface.m_123341_(), wantedHeight, surface.m_123343_());
            if (this.world.m_8055_(destination).m_60795_()) {
                return TerraformingSpell.moveBlock(this.world, surface, destination);
            }
            BlockPos sideDestination = TerraformingSpell.findNearestFreeDestination(this.world, this.target, this.radius, surface, wantedHeight);
            if (sideDestination != null) {
                return TerraformingSpell.moveBlock(this.world, surface, sideDestination);
            }
            return false;
        }
    }

    private static class ColumnTarget {
        private final BlockPos columnPos;
        private final double distance;
        private final double strength;

        private ColumnTarget(BlockPos columnPos, double distance, double strength) {
            this.columnPos = columnPos;
            this.distance = distance;
            this.strength = strength;
        }
    }
}

