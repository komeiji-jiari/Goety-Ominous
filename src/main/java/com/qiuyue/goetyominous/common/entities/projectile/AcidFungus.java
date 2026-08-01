package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.AcidPool;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.items.FungusPackHelper;
import com.qiuyue.goetyominous.common.items.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AcidFungus extends ThrowableItemProjectile implements ItemSupplier {

    public AcidFungus(EntityType<? extends AcidFungus> type, Level level) {
        super(type, level);
    }

    public AcidFungus(Level level, LivingEntity shooter) {
        super(ModEntityTypes.ACID_FUNGUS.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.ACID_FUNGUS.get();
    }

    @Override
    protected float getGravity() {
        return 0.05F;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level().isClientSide) return;

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                com.Polarice3.Goety.init.ModSounds.BLAST_FUNGUS_EXPLODE.get(),
                net.minecraft.sounds.SoundSource.NEUTRAL, 1.0F, 1.0F);

        AcidPool pool = new AcidPool(ModEntityType.ACID_POOL.get(), this.level());
        Vec3 hitPos = result.getLocation();
        if (result instanceof EntityHitResult entityHit) {
            hitPos = entityHit.getEntity().position();
        }
        pool.setPos(hitPos);
        if (this.getOwner() instanceof LivingEntity owner) {
            pool.setOwner(owner);
        }
        pool.setColor(0x87CEEB);
        pool.setWarmupColor(0x87CEEB);
        pool.setRadius(3.0F);
        pool.setDamage(4.0F);
        int duration = 80;
        if (this.getOwner() instanceof LivingEntity owner
                && FungusPackHelper.hasMatchingFungus(owner, ModItems.ACID_FUNGUS.get())) {
            duration = 120;
        }
        pool.setDuration(duration);
        this.level().addFreshEntity(pool);

        this.discard();
    }
}
