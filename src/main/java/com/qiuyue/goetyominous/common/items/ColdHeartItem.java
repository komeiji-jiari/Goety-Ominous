package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.illager.CryologerServant;
import com.Polarice3.Goety.common.entities.ally.illager.IceologerServant;
import com.Polarice3.Goety.common.entities.ally.undead.bound.BoundCryologer;
import com.Polarice3.Goety.common.entities.ally.undead.bound.BoundIceologer;
import com.Polarice3.Goety.common.capabilities.soulenergy.SEProvider;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;

public class ColdHeartItem extends Item {
    public ColdHeartItem() {
        super(new Item.Properties()
                .stacksTo(64)
                .rarity(Rarity.UNCOMMON));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        if (!level.isClientSide && entityLiving instanceof Player player) {
            player.getFoodData().eat(4, 1.2F);
            player.addEffect(new MobEffectInstance(GoetyEffects.CHILL_HIDE.get(), 1200, 1));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 1));
            player.addEffect(new MobEffectInstance(GoetyEffects.FROSTY_AURA.get(), 1200, 2));

            player.getCapability(SEProvider.CAPABILITY).ifPresent(se -> {
                if (se.getSEActive()) {
                    se.increaseSE(1000);
                }
            });
        }
        if (entityLiving instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return stack;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!player.level().isClientSide) {
            if (target instanceof IceologerServant iceologer) {
                return convertServant(player, iceologer, iceologer.getOwnerId(),
                        level -> {
                            CryologerServant c = ModEntityType.CRYOLOGER_SERVANT.get().create(level);
                            return c;
                        });
            }
            if (target instanceof BoundIceologer boundIce) {
                return convertServant(player, boundIce, boundIce.getOwnerId(),
                        level -> {
                            BoundCryologer c = ModEntityType.BOUND_CRYOLOGER.get().create(level);
                            return c;
                        });
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }

    private <T extends Mob & com.Polarice3.Goety.api.entities.IOwned> InteractionResult convertServant(
            Player player, Mob oldServant, java.util.UUID ownerId,
            java.util.function.Function<ServerLevel, T> factory) {
        if (!ownerId.equals(player.getUUID())) {
            return InteractionResult.FAIL;
        }

        ServerLevel serverLevel = (ServerLevel) player.level();

        T newServant = factory.apply(serverLevel);
        if (newServant != null) {
            newServant.setPos(oldServant.getX(), oldServant.getY(), oldServant.getZ());
            newServant.setYRot(oldServant.getYRot());
            newServant.setOwnerId(ownerId);

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                newServant.setItemSlot(slot, oldServant.getItemBySlot(slot));
            }
            newServant.setDropChance(EquipmentSlot.HEAD, 1.0F);
            newServant.setDropChance(EquipmentSlot.CHEST, 1.0F);
            newServant.setDropChance(EquipmentSlot.LEGS, 1.0F);
            newServant.setDropChance(EquipmentSlot.FEET, 1.0F);
            newServant.setDropChance(EquipmentSlot.MAINHAND, 1.0F);
            newServant.setDropChance(EquipmentSlot.OFFHAND, 1.0F);

            serverLevel.addFreshEntity(newServant);
            serverLevel.playSound(null, oldServant.blockPosition(), SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 1.0F, 1.0F);

            Vec3 center = oldServant.position();
            int particleRadius = 4;
            for (int ix = -particleRadius; ix <= particleRadius; ++ix) {
                for (int j = -particleRadius; j <= particleRadius; ++j) {
                    for (int k = -particleRadius; k <= particleRadius; ++k) {
                        double d13 = (double) ix + (oldServant.getRandom().nextDouble() - oldServant.getRandom().nextDouble()) * 0.5D;
                        double d15 = (double) j + (oldServant.getRandom().nextDouble() - oldServant.getRandom().nextDouble()) * 0.5D;
                        double d17 = (double) k + (oldServant.getRandom().nextDouble() - oldServant.getRandom().nextDouble()) * 0.5D;
                        double d19 = Math.sqrt(d13 * d13 + d15 * d15 + d17 * d17) / 0.5D + oldServant.getRandom().nextGaussian() * 0.05D;
                        serverLevel.sendParticles(ModParticleTypes.FROST_NOVA.get(),
                                center.x, center.y, center.z, 0,
                                d13 / d19, d15 / d19, d17 / d19, 0.5F);
                        if (ix != -particleRadius && ix != particleRadius && j != -particleRadius && j != particleRadius) {
                            k += particleRadius * 2 - 1;
                        }
                    }
                }
            }
            serverLevel.sendParticles(
                    new com.Polarice3.Goety.client.particles.ShockwaveParticleOption(0.0F, (float) particleRadius * 2.0F, 1),
                    center.x, center.y + 0.5D, center.z, 0, 0.0D, 0.0D, 0.0D, 0.0D);

            oldServant.discard();

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }
}