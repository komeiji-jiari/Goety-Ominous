package com.qiuyue.goetyominous.common.entities.ally.lm.goals.obliterator;

import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class TeleportDoubleSlashGoal extends IAttackGoal {
    protected final TheObliteratorServant entity;

    public TeleportDoubleSlashGoal(TheObliteratorServant entity, int getattackstate, int attackstate,
                                   int attackendstate, int attackMaxtick, int attackseetick, float attackrange) {
        super(entity, getattackstate, attackstate, attackendstate, attackMaxtick, attackseetick, attackrange);
        this.entity = entity;
    }

    @Override
    public void start() {
        super.start();
        int attackTicks = this.entity.getAttackTicks();
        if (attackTicks == 14) {
            Level level = this.entity.level();
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ModParticles.TELEPORT_EFFECT.get(), this.entity.getX(),
                        this.entity.getY() + 3.0D, this.entity.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
        if (attackTicks == 9) {
            Level level = this.entity.level();
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ModParticles.TELEPORT_EFFECT.get(), this.entity.getX(),
                        this.entity.getY() + 3.0D, this.entity.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return false;
    }
}
