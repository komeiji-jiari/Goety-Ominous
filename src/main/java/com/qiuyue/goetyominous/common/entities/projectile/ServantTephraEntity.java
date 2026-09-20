package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.github.alexmodguy.alexscaves.server.entity.item.TephraEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.TephraExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class ServantTephraEntity extends TephraEntity {

    private int fireSeconds = 5;
    private float fireRadius = 3.0F;

    public ServantTephraEntity(EntityType<? extends TephraEntity> type, Level level) {
        super(type, level);
    }

    public ServantTephraEntity(Level level, LivingEntity owner) {
        this(com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.SERVANT_TEPHRA.get(), level);
        float f = owner instanceof net.minecraft.world.entity.player.Player ? 0.3F : 0.1F;
        this.setPos(owner.getX(), owner.getEyeY() - (double) f, owner.getZ());
        this.setOwner(owner);
    }

    public void setFireSeconds(int seconds) {
        this.fireSeconds = seconds;
    }

    public void setFireRadius(float radius) {
        this.fireRadius = radius;
    }

    public int getFireSeconds() {
        return this.fireSeconds;
    }

    public float getFireRadius() {
        return this.fireRadius;
    }

    private int stunSeconds = 2;

    public void setStunSeconds(int seconds) {
        this.stunSeconds = seconds;
    }

    public int getStunSeconds() {
        return this.stunSeconds;
    }

    private boolean dangerous = true;

    public void setDangerous(boolean dangerous) {
        this.dangerous = dangerous;
    }

    public boolean isDangerous() {
        return this.dangerous;
    }

    private boolean groundFire;

    public void setGroundFire(boolean groundFire) {
        this.groundFire = groundFire;
    }

    public boolean isGroundFire() {
        return this.groundFire;
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (!super.canHitEntity(target)) {
            return false;
        }
        LivingEntity owner = this.getOwner() instanceof LivingEntity living ? living : null;
        if (owner == null) {
            return true;
        }
        if (target instanceof IOwned owned && owned.getTrueOwner() == owner) {
            return false;
        }
        return !owner.isAlliedTo(target) && !target.isAlliedTo(owner);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        this.servantExplode();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.servantExplode();
    }

    private void servantExplode() {
        if (this.level().isClientSide || this.isRemoved()) {
            return;
        }
        TephraExplosion explosion = new TephraExplosion(this.level(),
                this,
                this.getX(), this.getY(), this.getZ(),
                1.0F + this.getMaxScale(), Explosion.BlockInteraction.KEEP);
        explosion.explode();
        if (!this.groundFire) {
            explosion.clearToBlow();
        }
        explosion.finalizeExplosion(true);
        this.affectAround();
        this.igniteAround();
        this.discard();
    }

    private void affectAround() {
        if (!this.dangerous) {
            return;
        }
        LivingEntity owner = this.getOwner() instanceof LivingEntity living ? living : null;
        if (this.fireSeconds <= 0 && this.stunSeconds <= 0) {
            return;
        }
        AABB aabb = this.getBoundingBox().inflate(this.getMaxScale());
        for (LivingEntity living : this.level().getEntitiesOfClass(LivingEntity.class, aabb,
                EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
            if (owner != null
                    && (living == owner
                    || (living instanceof IOwned owned && owned.getTrueOwner() == owner)
                    || owner.isAlliedTo(living) || living.isAlliedTo(owner))) {
                continue;
            }
            if (this.fireSeconds > 0) {
                living.setSecondsOnFire(this.fireSeconds);
            }
            if (this.stunSeconds > 0) {
                living.addEffect(new MobEffectInstance(GoetyEffects.STUNNED.get(),
                        this.stunSeconds * 20, 0), owner);
            }
        }
    }

    private void igniteAround() {
        if (!this.dangerous || this.fireRadius <= 0.0F) {
            return;
        }
        BlockPos origin = this.blockPosition();
        int r = (int) Math.ceil(this.fireRadius);
        float rSqr = this.fireRadius * this.fireRadius;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = -r; x <= r; ++x) {
            for (int z = -r; z <= r; ++z) {
                if ((float) (x * x + z * z) > rSqr) {
                    continue;
                }
                for (int y = -1; y <= 0; ++y) {
                    pos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if (!this.level().isEmptyBlock(pos)) {
                        continue;
                    }
                    BlockState fire = BaseFireBlock.getState(this.level(), pos);
                    if (fire.canSurvive(this.level(), pos)) {
                        this.level().setBlockAndUpdate(pos, fire);
                    }
                }
            }
        }
    }
}
