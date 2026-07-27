package com.qiuyue.goetyominus.common.entities.projectile;

import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.utils.BrewUtils;
import com.qiuyue.goetyominus.common.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class WitchBombEntity extends ThrowableItemProjectile {

    public WitchBombEntity(EntityType<? extends WitchBombEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public WitchBombEntity(Level worldIn, LivingEntity throwerIn) {
        super(ModEntityTypes.WITCH_BOMB.get(), throwerIn, worldIn);
    }

    @Override
    protected Item getDefaultItem() {
        return com.qiuyue.goetyominus.common.items.ModItems.WITCH_BOMB.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            BlockPos pos = this.blockPosition();

            this.level().explode(null, this.getX(), this.getY(), this.getZ(),
                    1.5F, Level.ExplosionInteraction.NONE);

            ItemStack heldStack = this.getItem();
            if (!heldStack.isEmpty() && BrewUtils.hasEffect(heldStack) && this.getOwner() instanceof LivingEntity owner) {
                ItemStack brewStack = ItemStack.EMPTY;
                if (heldStack.getTag() != null && heldStack.getTag().contains("BrewItem")) {
                    brewStack = ItemStack.of(heldStack.getTag().getCompound("BrewItem"));
                }

                if (!brewStack.isEmpty() && BrewUtils.isLingering(brewStack)) {
                    BrewUtils.makeAreaOfEffectCloud(owner, heldStack, this, pos);
                } else if (!brewStack.isEmpty() && BrewUtils.isGas(brewStack)) {
                    BrewUtils.makeBrewGas(owner, heldStack, this, pos);
                } else {
                    List<BrewEffectInstance> brewEffects = BrewUtils.getBrewEffects(heldStack);
                    List<MobEffectInstance> mobEffects = PotionUtils.getMobEffects(heldStack);
                    BrewUtils.applySplash(owner, heldStack, this, pos, mobEffects, brewEffects);
                }
                this.level().levelEvent(2002, pos, PotionUtils.getColor(heldStack));
            }
            this.discard();
        }
    }
}
