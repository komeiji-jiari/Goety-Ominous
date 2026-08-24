/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.vivideru.masteryofmagic.goldification;

import com.vivideru.masteryofmagic.config.GameplayConfig;
import com.vivideru.masteryofmagic.goldification.GoldificationManager;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicNetwork;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class GoldificationAreaJob {
    private final ServerLevel level;
    private final Vec3 center;
    private final double radius;
    private final double radiusSquared;
    private final long expireGameTime;
    @Nullable
    private final Entity source;
    @Nullable
    private final Consumer<Result> completion;
    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private int cursorX;
    private int cursorY;
    private int cursorZ;
    private int goldifiedBlocks;
    private int goldifiedEntities;
    private boolean entitiesProcessed;
    private boolean finished;
    private boolean truncated;

    GoldificationAreaJob(ServerLevel level, Vec3 center, double radius, long durationTicks, @Nullable Entity source, @Nullable Consumer<Result> completion) {
        this.level = level;
        this.center = center;
        this.radius = radius;
        this.radiusSquared = radius * radius;
        this.expireGameTime = level.m_46467_() + Math.max(1L, durationTicks);
        this.source = source;
        this.completion = completion;
        this.minX = Mth.m_14107_((double)(center.f_82479_ - radius));
        this.minY = Math.max(level.m_141937_(), Mth.m_14107_((double)(center.f_82480_ - radius)));
        this.minZ = Mth.m_14107_((double)(center.f_82481_ - radius));
        this.maxX = Mth.m_14107_((double)(center.f_82479_ + radius));
        this.maxY = Math.min(level.m_151558_() - 1, Mth.m_14107_((double)(center.f_82480_ + radius)));
        this.maxZ = Mth.m_14107_((double)(center.f_82481_ + radius));
        this.cursorX = this.minX;
        this.cursorY = this.minY;
        this.cursorZ = this.minZ;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public Result getResult() {
        return new Result(this.goldifiedBlocks, this.goldifiedEntities, this.truncated);
    }

    void tick(int inspectionBudget) {
        if (this.finished) {
            return;
        }
        if (!this.entitiesProcessed) {
            this.entitiesProcessed = true;
            this.processEntities();
        }
        int maxBlocks = (Integer)GameplayConfig.GOLDIFICATION_MAX_BLOCKS_PER_COMMAND.get();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        LongArrayList changedThisTick = new LongArrayList();
        for (int inspected = 0; inspected < inspectionBudget && this.next(cursor); ++inspected) {
            if (this.goldifiedBlocks < maxBlocks) continue;
            this.truncated = true;
            this.finish();
            break;
        }
        if (!changedThisTick.isEmpty()) {
            GoetyMasteryOfMagicNetwork.sendGoldifiedBlocks(this.level, (LongCollection)changedThisTick, true);
        }
        if (!this.finished && this.cursorX > this.maxX) {
            this.finish();
        }
    }

    private void processEntities() {
        AABB bounds = new AABB(this.center.f_82479_ - this.radius, this.center.f_82480_ - this.radius, this.center.f_82481_ - this.radius, this.center.f_82479_ + this.radius, this.center.f_82480_ + this.radius, this.center.f_82481_ + this.radius);
        for (Entity entity : this.level.m_6249_((Entity)null, bounds, candidate -> candidate.m_20182_().m_82557_(this.center) <= this.radiusSquared)) {
            if (!GoldificationManager.goldifyEntityUntil(entity, this.expireGameTime, this.source, true)) continue;
            ++this.goldifiedEntities;
        }
    }

    private boolean next(BlockPos.MutableBlockPos output) {
        if (this.cursorX > this.maxX) {
            return false;
        }
        output.m_122178_(this.cursorX, this.cursorY, this.cursorZ);
        ++this.cursorZ;
        if (this.cursorZ > this.maxZ) {
            this.cursorZ = this.minZ;
            ++this.cursorY;
            if (this.cursorY > this.maxY) {
                this.cursorY = this.minY;
                ++this.cursorX;
            }
        }
        return true;
    }

    private void finish() {
        if (this.finished) {
            return;
        }
        this.finished = true;
        if (this.completion != null) {
            this.completion.accept(this.getResult());
        }
    }

    public record Result(int goldifiedBlocks, int goldifiedEntities, boolean truncated) {
    }
}

