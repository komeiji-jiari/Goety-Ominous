package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.github.alexmodguy.alexscaves.server.entity.living.TrilocarisEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class RelicheirusServantPreyTargetGoal extends NearestAttackableTargetGoal<TrilocarisEntity> {
    private final RelicheirusServant relicheirus;

    public RelicheirusServantPreyTargetGoal(RelicheirusServant relicheirus) {
        super(relicheirus, TrilocarisEntity.class, 100, true, false, null);
        this.relicheirus = relicheirus;
    }

    @Override
    public boolean canUse() {
        return this.canHunt() && super.canUse();
    }

    private boolean canHunt() {
        return !this.relicheirus.isStaying() && !this.relicheirus.isCommanded()
                && this.relicheirus.getTarget() == null;
    }
}
