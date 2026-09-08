package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.utils.MobUtil;
import com.github.alexthe666.alexsmobs.entity.EntityFart;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

public class FartServantEntity extends EntityFart {

    public FartServantEntity(EntityType<? extends EntityFart> type, Level level) {
        super(type, level);
    }

    public FartServantEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        super(spawnEntity, level);
    }

    public FartServantEntity(Level level, LivingEntity shooter, boolean leftSide) {
        this(AmEntityRegistry.FART_SERVANT.get(), level);
        this.setShooter(shooter);
        Vec3 look = shooter.getViewVector(1.0F);
        double r = shooter.getBbWidth() * 0.5D;
        this.setPos(shooter.getX() + look.x * r,
                shooter.getEyeY() - 0.2D,
                shooter.getZ() + look.z * r);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && !this.isFriendlyToShooter(target);
    }

    private boolean isFriendlyToShooter(Entity target) {
        Entity shooter = this.getShooter();
        if (shooter == null) {
            return false;
        }
        if (target == shooter || target.is(shooter)) {
            return true;
        }
        if (target.isAlliedTo(shooter) || shooter.isAlliedTo(target)) {
            return true;
        }
        return MobUtil.areAllies(target, shooter);
    }
}
