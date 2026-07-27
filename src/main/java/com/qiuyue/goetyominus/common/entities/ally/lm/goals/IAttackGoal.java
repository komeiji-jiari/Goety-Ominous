package com.qiuyue.goetyominus.common.entities.ally.lm.goals;

import com.qiuyue.goetyominus.common.entities.ally.lm.IAnimatedMonsterServant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * 精确复刻 LM IAttackGoal。
 * 参数顺序: (entity, getattackstate, attackstate, attackendstate, attackMaxtick, attackseetick, attackrange)
 */
public class IAttackGoal extends Goal {
    protected final IAnimatedMonsterServant entity;
    private final int getattackstate;   // canUse 要求当前攻击状态等于此值
    private final int attackstate;      // start() 设置此攻击状态
    private final int attackendstate;   // stop() 恢复到此状态
    private final int attackMaxtick;    // canContinueToUse: attackTicks < attackMaxtick
    private final int attackseetick;    // tick(): 在此 tick 之前盯着目标
    private final float attackrange;

    public IAttackGoal(IAnimatedMonsterServant entity, int getattackstate, int attackstate,
                       int attackendstate, int attackMaxtick, int attackseetick,
                       float attackrange) {
        this.entity = entity;
        this.getattackstate = getattackstate;
        this.attackstate = attackstate;
        this.attackendstate = attackendstate;
        this.attackMaxtick = attackMaxtick;
        this.attackseetick = attackseetick;
        this.attackrange = attackrange;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.entity.getTarget();
        return target != null
                && target.isAlive()
                && this.entity.distanceTo(target) <= this.attackrange
                && this.entity.getAttackState() == this.getattackstate
                && this.entity.getAttackDelayTicks() <= 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.entity.attackTicks < this.attackMaxtick;
    }

    @Override
    public void start() {
        this.entity.setAttackState(this.attackstate);
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (this.entity.attackTicks < this.attackseetick && target != null) {
            this.entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.entity.lookAt(target, 30.0F, 30.0F);
        } else {
            this.entity.setYRot(this.entity.yBodyRot);
        }
    }

    @Override
    public void stop() {
        this.entity.setAttackState(this.attackendstate);
        this.entity.attackTicks = 0;
        this.entity.attackCooldown = 0;
    }
}
