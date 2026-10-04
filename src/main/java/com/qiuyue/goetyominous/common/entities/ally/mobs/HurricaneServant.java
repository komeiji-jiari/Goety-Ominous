package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import com.Polarice3.Goety.config.ItemConfig;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractHurricane;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.common.init.ModTags;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.config.MobsConfig;
import java.util.function.Predicate;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HurricaneServant extends AbstractHurricane {

    public HurricaneServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
    }

    @Override
    public int xpReward() {
        return 20;
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof HurricaneServant;
    }

    @Override
    public void tryKill(Player player) {
        if (this.getKillChance() <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.HurricaneServantLimit.get();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            ItemStack itemstack = player.getItemInHand(hand);
            if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
                boolean rod = itemstack.is(ModTags.BREEZE_RODS);
                if ((rod || itemstack.is(ModItems.WIND_CHARGE.get())) && this.getHealth() < this.getMaxHealth()) {
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(ModSounds.BREEZE_IDLE_GROUND.get(), 1.0F, 1.25F);
                    this.heal(rod ? 4.0F : 1.0F);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    player.swing(hand);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        if (this.deathTime == 40 && this.getTrueOwner() != null && MobsConfig.HurricaneServantCore.get()) {
            ItemStack core = new ItemStack(ModItems.HURRICANE_CORE.get());
            ReviveServantItem.setOwnerName(this.getTrueOwner(), core);
            ReviveServantItem.setSummon(this, core);
            FlyingItem flyingItem = new FlyingItem(ModEntityType.FLYING_ITEM.get(), this.level(), this.getX(), this.getY(), this.getZ());
            flyingItem.setOwner(this.getTrueOwner());
            flyingItem.setItem(core);
            flyingItem.setParticle(ParticleTypes.POOF);
            flyingItem.setSecondsCool(ItemConfig.ReviveSecondsCool.get());
            this.level().addFreshEntity(flyingItem);
        }
    }
}
