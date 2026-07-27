package com.qiuyue.goetyominus.common.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.qiuyue.goetyominus.common.entities.projectile.PitchforkEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PitchforkItem extends Item implements Vanishable {
    private final Multimap<Attribute, AttributeModifier> pitchforkAttributes;

    public PitchforkItem() {
        super(new Item.Properties().durability(128));
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 3.0D, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -2.9D, AttributeModifier.Operation.ADDITION));
        this.pitchforkAttributes = builder.build();
    }

    @Override
    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.is(Items.IRON_INGOT);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            int i = this.getUseDuration(stack) - timeLeft;
            if (i >= 10) {
                stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(entityLiving.getUsedItemHand()));
                PitchforkEntity pitchfork = new PitchforkEntity(worldIn, player, stack);
                pitchfork.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F + 1.5F, 1.0F);
                if (player.getAbilities().instabuild) {
                    pitchfork.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
                worldIn.addFreshEntity(pitchfork);
                worldIn.playSound(null, pitchfork, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                if (!player.getAbilities().instabuild) {
                    player.getInventory().removeItem(stack);
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1) {
            return InteractionResultHolder.fail(itemstack);
        }
        playerIn.startUsingItem(handIn);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        if (pState.is(Blocks.HAY_BLOCK)) return 15.0F;
        return pState.is(Blocks.COBWEB) ? 15.0F : 1.0F;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (state.getDestroySpeed(worldIn, pos) != 0.0F) {
            stack.hurtAndBreak(2, entityLiving, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        } else {
            stack.hurtAndBreak(1, entityLiving, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return (enchantment.category == EnchantmentCategory.BREAKABLE
                || enchantment.category == EnchantmentCategory.WEAPON
                || enchantment == Enchantments.MOB_LOOTING
                || enchantment == Enchantments.LOYALTY)
                && !(enchantment instanceof Enchantment);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.MAINHAND ? this.pitchforkAttributes : super.getDefaultAttributeModifiers(equipmentSlot);
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}
