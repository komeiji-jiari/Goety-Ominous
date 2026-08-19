package com.qiuyue.goetyominous.common.entities.ai.ac;

import com.github.alexmodguy.alexscaves.server.block.DinosaurEggBlock;
import com.qiuyue.goetyominous.common.blocks.ac.GrottoceratopsServantEggBlock;
import com.qiuyue.goetyominous.common.blocks.entities.ac.GrottoceratopsServantEggBlockEntity;
import com.qiuyue.goetyominous.common.entities.ally.ac.GrottoceratopsServant;
import com.qiuyue.goetyominous.common.init.ac.AcBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;


public class ServantLayEggGoal extends MoveToBlockGoal {

    private final GrottoceratopsServant mob;
    private final DinosaurEggBlock eggBlock;
    private final int maxTime;
    private int layEggCounter;

    public ServantLayEggGoal(GrottoceratopsServant mob, int maxTime, double speed) {
        super(mob, speed, 16);
        this.mob = mob;
        this.maxTime = maxTime;
        this.eggBlock = (GrottoceratopsServantEggBlock) AcBlockRegistry.GROTTOCERATOPS_SERVANT_EGG.get();
    }

    @Override
    public boolean canUse() {
        return this.mob.hasEgg() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.mob.hasEgg();
    }

    @Override
    public void start() {
        super.start();
        this.layEggCounter = 0;
    }

    @Override
    public double acceptedDistance() {
        return (double) Math.ceil(this.mob.getBbWidth()) + 0.5D;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isReachedTarget()) {
            BlockPos eggPos = this.blockPos.above();
            this.mob.onLayEggTick(eggPos, this.layEggCounter);
            // 与原版 AnimalLayEggGoal 一致：后自增，layEggCounter 旧值 > maxTime（即站立第 102 tick）才落蛋
            if (this.layEggCounter++ > this.maxTime) {
                Level level = this.mob.level();
                level.playSound(null, this.blockPos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + level.random.nextFloat() * 0.2F);
                BlockState eggState = this.mob.createEggBlockState();
                level.setBlockAndUpdate(eggPos, eggState);
                if (level.getBlockEntity(eggPos) instanceof GrottoceratopsServantEggBlockEntity eggBe) {
                    if (this.mob.getOwnerId() != null) {
                        eggBe.setOwnerUUID(this.mob.getOwnerId());
                        LivingEntity owner = this.mob.getTrueOwner();
                        eggBe.setOwnerId(owner != null ? owner.getId() : -1);
                    }
                    eggBe.setChanged();
                    level.sendBlockUpdated(eggPos, eggState, eggState, 3);
                }
                level.gameEvent(GameEvent.BLOCK_PLACE, eggPos, GameEvent.Context.of(this.mob, eggState));
                this.mob.setHasEgg(false);
                this.mob.setInLoveTime(600);
                level.broadcastEntityEvent(this.mob, (byte) 78);
                // 与原版一致：泥土地面换成蕨垫（DinosaurEggBlock.canGrow 在蕨垫上孵化概率 1/10，其余 1/20）
                if (level.getBlockState(this.blockPos).is(BlockTags.DIRT)) {
                    level.setBlockAndUpdate(this.blockPos, this.mob.createEggBeddingBlockState());
                }
            }
        } else {
            this.layEggCounter = 0;
        }
    }

    @Override
    protected boolean isValidTarget(LevelReader reader, BlockPos pos) {
        BlockPos eggPos = pos.above();
        if (!reader.isEmptyBlock(eggPos)) {
            return false;
        }
        return this.eggBlock.isProperHabitat(reader, eggPos);
    }
}
