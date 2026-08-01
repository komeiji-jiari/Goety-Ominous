package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.utils.MobUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

public class MagiArrow extends Arrow {
    public MagiArrow(EntityType<? extends Arrow> p_36858_, Level p_36859_) {
        super(p_36858_, p_36859_);
    }

    public MagiArrow(Level p_36866_, LivingEntity p_36867_) {
        super(p_36866_, p_36867_);
    }

    @Override
    protected boolean canHitEntity(Entity pEntity) {
        if (this.getOwner() != null) {
            if (pEntity == this.getOwner()) {
                return false;
            }
            if (MobUtil.areAllies(this.getOwner(), pEntity)) {
                return false;
            }
        }
        return super.canHitEntity(pEntity);
    }

    public void tick() {
        super.tick();
        if (this.inGround) {
            this.discard();
        }
        if (this.tickCount > 80) {
            this.discard();
        }
    }
}
