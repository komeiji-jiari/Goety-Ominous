package com.qiuyue.someillagerservants.common.entities.ally.mobs;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinBruteServant;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class StrongZPiglinBruteServant extends ZPiglinBruteServant {

    public StrongZPiglinBruteServant(EntityType<? extends ZPiglinBruteServant> type, Level level) {
        super(type, level);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        Item heldItem = heldStack.getItem();
        ItemStack mainHandStack = this.getMainHandItem();

        if (this.getTrueOwner() != null && player.getUUID().equals(this.getTrueOwner().getUUID())
                && !(player.getOffhandItem().getItem() instanceof IWand)) {
            if (heldItem instanceof SwordItem || heldItem instanceof AxeItem
                    || heldItem instanceof com.qiuyue.someillagerservants.common.items.BoneCudgelItem) {
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                this.setItemSlot(EquipmentSlot.MAINHAND, heldStack.copyWithCount(1));
                this.dropEquipment(EquipmentSlot.MAINHAND, mainHandStack);
                this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                if (!player.getAbilities().instabuild) {
                    heldStack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }
}