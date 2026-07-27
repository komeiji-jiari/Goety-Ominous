package com.qiuyue.goetyominus.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig;
import com.alexander.mutantmore.config.mutant_wither_skeleton.MutantWitherSkeletonCommonConfig;
import com.alexander.mutantmore.init.TagInit.Blocks;
import com.alexander.mutantmore.interfaces.IMutatable;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeConfigSpec;

public abstract class AbstractMutantServant extends Summoned implements IMutatable {
    private int destroyBlocksTick;
    private static final EntityDataAccessor<Integer> TARGETED_ENTITY_ID;

    protected AbstractMutantServant(EntityType<? extends Owned> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    protected PathNavigation createNavigation(Level p_33348_) {
        return new MutantNavigation(this, p_33348_);
    }

    public abstract NodeEvaluatorDimensions getNodeEvaluatorDimensions();

    public abstract TagKey<Block> walksThroughTag();

    public abstract ForgeConfigSpec.ConfigValue<Boolean> walkGriefingConfig();

    public abstract ForgeConfigSpec.ConfigValue<Boolean> walkGriefingDropsBlocksConfig();

    public abstract ForgeConfigSpec.ConfigValue<Boolean> hurtGriefingConfig();

    public abstract ForgeConfigSpec.ConfigValue<Boolean> hurtGriefingDropsBlocksConfig();

    public abstract ForgeConfigSpec.ConfigValue<Boolean> showHealthBarConfig();

    public abstract ForgeConfigSpec.ConfigValue<Boolean> despawnsConfig();

    public void readAdditionalSaveData(CompoundTag p_21450_) {
        super.readAdditionalSaveData(p_21450_);
    }

    public void setCustomName(@Nullable Component p_31476_) {
        super.setCustomName(p_31476_);
    }

    protected void customServerAiStep() {
        if (this.destroyBlocksTick > 0 && (Boolean)this.hurtGriefingConfig().get() && !(Boolean)MutantMoreGroupedOptionsCommonConfig.mob_griefing_off.get()) {
            --this.destroyBlocksTick;
            if (this.destroyBlocksTick == 0) {
                int j1 = Mth.floor(this.getY());
                int i2 = Mth.floor(this.getX());
                int j2 = Mth.floor(this.getZ());
                boolean flag = false;
                AABB aabb = this.getBoundingBox().inflate(1.0, 0.0, 1.0).move(0.0, 0.1, 0.0);
                Iterator var6 = BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ)).iterator();

                label48:
                while(true) {
                    BlockPos blockpos;
                    BlockState blockstate;
                    Block block;
                    do {
                        do {
                            if (!var6.hasNext()) {
                                break label48;
                            }

                            blockpos = (BlockPos)var6.next();
                            blockstate = this.level().getBlockState(blockpos);
                            block = blockstate.getBlock();
                        } while(blockstate.is(Blocks.UNBREAKABLE));
                    } while(this.cantBreakWithHurtGriefing() != null && this.cantBreakWithHurtGriefing().contains(block));

                    flag = this.level().destroyBlock(blockpos, (Boolean)this.hurtGriefingDropsBlocksConfig().get() || (Boolean)MutantMoreGroupedOptionsCommonConfig.griefing_drops_blocks_on.get(), this) || flag;
                }
            }
        }

        super.customServerAiStep();
    }

    public List<Block> cantBreakWithHurtGriefing() {
        return null;
    }

    public void stopSeenByPlayer(ServerPlayer p_31488_) {
        super.stopSeenByPlayer(p_31488_);
    }

    public void aiStep() {
        super.aiStep();
        if (this.horizontalCollision && (Boolean)this.walkGriefingConfig().get() && !(Boolean)MutantMoreGroupedOptionsCommonConfig.mob_griefing_off.get()) {
            boolean flag = false;
            AABB aabb = this.getBoundingBox().inflate(0.2);
            Iterator var3 = BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ)).iterator();

            while(true) {
                BlockPos blockpos;
                BlockState blockstate;
                do {
                    if (!var3.hasNext()) {
                        return;
                    }

                    blockpos = (BlockPos)var3.next();
                    blockstate = this.level().getBlockState(blockpos);
                    Block block = blockstate.getBlock();
                } while(!blockstate.is(this.walksThroughTag()));

                flag = this.level().destroyBlock(blockpos, (Boolean)this.walkGriefingDropsBlocksConfig().get() || (Boolean)MutantMoreGroupedOptionsCommonConfig.griefing_drops_blocks_on.get(), this) || flag;
            }
        }
    }

    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        if (!this.isInvulnerableTo(p_21016_) && this.destroyBlocksTick <= 0) {
            this.destroyBlocksTick = 20;
        }

        return super.hurt(p_21016_, p_21017_);
    }

    public float getStepHeight() {
        return 1.6F;
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.updateAnimations();
        }

    }

    protected void updateAnimations() {
    }

    public void baseTick() {
        super.baseTick();

        if (!this.level().isClientSide) {
            if (this.getTarget() != null && !this.getTarget().isRemoved()) {
                this.setTargetedEntityID(this.getTarget().getId());
            } else {
                this.setTargetedEntityID(0);
            }
        }

    }

    public boolean causeFallDamage(float p_148711_, float p_148712_, DamageSource p_148713_) {
        return false;
    }

    public void checkDespawn() {
        if (!(Boolean)MutantWitherSkeletonCommonConfig.despawns.get() && !(Boolean)MutantMoreGroupedOptionsCommonConfig.mutant_despawning_on.get()) {
            if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
                this.discard();
            } else {
                this.noActionTime = 0;
            }
        } else {
            super.checkDespawn();
        }

    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGETED_ENTITY_ID, 0);
    }

    public int getTargetedEntityID() {
        return (Integer)this.entityData.get(TARGETED_ENTITY_ID);
    }

    public void setTargetedEntityID(int setTo) {
        this.entityData.set(TARGETED_ENTITY_ID, setTo);
    }

    static {
        TARGETED_ENTITY_ID = SynchedEntityData.defineId(AbstractMutantServant.class, EntityDataSerializers.INT);
    }

    static class MutantNavigation extends GroundPathNavigation {
        public MutantNavigation(Mob p_33379_, Level p_33380_) {
            super(p_33379_, p_33380_);
        }

        protected PathFinder createPathFinder(int p_33382_) {
            this.nodeEvaluator = new MutantNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, p_33382_);
        }

        public boolean canCutCorner(BlockPathTypes p_265292_) {
            return true;
        }
    }

    public class NodeEvaluatorDimensions {
        public int entityWidth;
        public int entityHeight;
        public int entityDepth;

        public NodeEvaluatorDimensions(int entityWidth, int entityHeight, int entityDepth) {
            this.entityWidth = entityWidth;
            this.entityHeight = entityHeight;
            this.entityDepth = entityDepth;
        }
    }

    static class MutantNodeEvaluator extends WalkNodeEvaluator {
        public AbstractMutantServant mutant;

        MutantNodeEvaluator() {
        }

        public void prepare(PathNavigationRegion p_77620_, Mob p_77621_) {
            super.prepare(p_77620_, p_77621_);
            this.mutant = (AbstractMutantServant)p_77621_;
            if (((AbstractMutantServant)p_77621_).getNodeEvaluatorDimensions() != null) {
                this.entityWidth = ((AbstractMutantServant)p_77621_).getNodeEvaluatorDimensions().entityWidth;
                this.entityHeight = ((AbstractMutantServant)p_77621_).getNodeEvaluatorDimensions().entityHeight;
                this.entityDepth = ((AbstractMutantServant)p_77621_).getNodeEvaluatorDimensions().entityDepth;
            }

        }

        public BlockPathTypes getBlockPathType(BlockGetter p_77576_, int p_77577_, int p_77578_, int p_77579_) {
            return p_77576_.getBlockState(new BlockPos(p_77577_, p_77578_, p_77579_)).is(this.mutant.walksThroughTag()) && (Boolean)this.mutant.walkGriefingConfig().get() && !(Boolean)MutantMoreGroupedOptionsCommonConfig.mob_griefing_off.get() ? BlockPathTypes.OPEN : getBlockPathTypeStatic(p_77576_, new BlockPos.MutableBlockPos(p_77577_, p_77578_, p_77579_));
        }
    }
}