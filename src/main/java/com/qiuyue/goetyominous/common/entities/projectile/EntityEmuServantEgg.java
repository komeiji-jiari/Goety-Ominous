package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.qiuyue.goetyominous.common.entities.ally.am.EmuServant;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import com.qiuyue.goetyominous.common.items.am.AmItems;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class EntityEmuServantEgg extends ThrowableItemProjectile {

    public EntityEmuServantEgg(EntityType<? extends EntityEmuServantEgg> type, Level level) {
        super(type, level);
    }

    public EntityEmuServantEgg(Level level, LivingEntity thrower) {
        super(AmEntityRegistry.EMU_SERVANT_EGG.get(), thrower, level);
    }

    public EntityEmuServantEgg(Level level, double x, double y, double z) {
        super(AmEntityRegistry.EMU_SERVANT_EGG.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return AmItems.EMU_SERVANT_EGG.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            if (this.random.nextInt(8) == 0) {
                this.hatch();
            }
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    private void hatch() {
        if (!(this.getOwner() instanceof Player owner)) {
            return;
        }
        if (!RitualRequirements.canSummon(this.level(), owner, AmEntityRegistry.EMU_SERVANT.get())) {
            return;
        }
        int count = this.random.nextInt(32) == 0 ? 4 : 1;
        for (int i = 0; i < count; i++) {
            EmuServant baby = AmEntityRegistry.EMU_SERVANT.get().create(this.level());
            if (baby == null) {
                continue;
            }
            double x = this.getX() + (this.random.nextDouble() - this.random.nextDouble()) * 0.2D;
            double y = this.getY() + (this.random.nextDouble() - this.random.nextDouble()) * 0.2D;
            double z = this.getZ() + (this.random.nextDouble() - this.random.nextDouble()) * 0.2D;
            baby.setTrueOwner(owner);
            baby.finalizeSpawn((ServerLevel) this.level(), this.level().getCurrentDifficultyAt(this.blockPosition()),
                    MobSpawnType.MOB_SUMMONED, null, null);
            baby.setAge(-24000);
            baby.setPersistenceRequired();
            baby.moveTo(x, y, z, this.getYRot(), 0.0F);
            this.level().addFreshEntity(baby);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()),
                        this.getX(), this.getY(), this.getZ(),
                        (this.random.nextDouble() - 0.5D) * 0.08D,
                        (this.random.nextDouble() - 0.5D) * 0.08D + 0.05D,
                        (this.random.nextDouble() - 0.5D) * 0.08D);
            }
        }
    }
}
