package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig;
import com.alexander.mutantmore.interfaces.IMutatable;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraftforge.common.ForgeConfigSpec;

public abstract class AbstractMutantServant extends Summoned implements IMutatable {
    private static final EntityDataAccessor<Integer> TARGETED_ENTITY_ID;
    public float walkPhase;
    public float walkPhaseAge = -1.0F;

    protected AbstractMutantServant(EntityType<? extends Owned> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    protected PathNavigation createNavigation(Level p_33348_) {
        return new MutantNavigation(this, p_33348_);
    }

    protected BodyRotationControl createBodyControl() {
        return new MutantBodyRotationControl(this);
    }

    public abstract NodeEvaluatorDimensions getNodeEvaluatorDimensions();

    public abstract ForgeConfigSpec.ConfigValue<Boolean> despawnsConfig();

    public void readAdditionalSaveData(CompoundTag p_21450_) {
        super.readAdditionalSaveData(p_21450_);
    }

    public void setCustomName(@Nullable Component p_31476_) {
        super.setCustomName(p_31476_);
    }

    public void stopSeenByPlayer(ServerPlayer p_31488_) {
        super.stopSeenByPlayer(p_31488_);
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
        if (!(Boolean)this.despawnsConfig().get() && !(Boolean)MutantMoreGroupedOptionsCommonConfig.mutant_despawning_on.get()) {
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
    }
}