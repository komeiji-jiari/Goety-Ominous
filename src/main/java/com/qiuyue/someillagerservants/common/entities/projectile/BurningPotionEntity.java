package com.qiuyue.someillagerservants.common.entities.projectile;

import com.qiuyue.someillagerservants.common.entities.util.BurningGroundEntity;
import com.qiuyue.someillagerservants.common.init.ModEntityTypes;
import com.qiuyue.someillagerservants.common.items.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class BurningPotionEntity extends ThrowableItemProjectile {

    public BurningPotionEntity(EntityType<? extends BurningPotionEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public BurningPotionEntity(Level worldIn, LivingEntity throwerIn) {
        super(ModEntityTypes.BURNING_POTION.get(), throwerIn, worldIn);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BURNING_POTION.get();
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (pResult.getType() != HitResult.Type.ENTITY) {
            if (!this.level().isClientSide) {
                BurningGroundEntity burningGround = new BurningGroundEntity(this.level(), this.getX(), this.getY(), this.getZ());
                if (this.getOwner() instanceof LivingEntity owner) {
                    burningGround.setOwner(owner);
                }
                burningGround.setDuration(600);
                this.level().addFreshEntity(burningGround);
                this.level().levelEvent(2007, this.blockPosition(), 16760890);
                this.discard();
            }
        }
    }
}
