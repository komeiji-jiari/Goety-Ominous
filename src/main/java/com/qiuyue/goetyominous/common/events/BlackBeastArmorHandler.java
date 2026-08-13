package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.common.items.CursedBlackBeastArmorItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class BlackBeastArmorHandler {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide() != LogicalSide.SERVER) return;

        LivingEntity target = event.getTarget() instanceof LivingEntity le ? le : null;
        if (!(target instanceof BlackBeast blackBeast)) return;

        Player player = event.getEntity();
        ItemStack held = event.getItemStack();
        ItemStack armor = blackBeast.getItemBySlot(EquipmentSlot.CHEST);

        if (blackBeast.getTrueOwner() != player) return;

        if (held.getItem() instanceof CursedBlackBeastArmorItem && armor.isEmpty()) {
            if (player.getAbilities().instabuild) {
                blackBeast.setItemSlot(EquipmentSlot.CHEST, held.copy());
            } else {
                blackBeast.setItemSlot(EquipmentSlot.CHEST, held.split(1));
            }
            blackBeast.playSound(ModSounds.WOLF_ARMOR_EQUIP.get(), 1.0F, 1.0F);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (held.is(Items.SHEARS) && armor.getItem() instanceof CursedBlackBeastArmorItem) {
            held.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(event.getHand()));
            blackBeast.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
            blackBeast.spawnAtLocation(armor);
            blackBeast.playSound(ModSounds.WOLF_ARMOR_UNEQUIP.get(), 1.0F, 1.0F);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (armor.isDamageableItem()
                && armor.getItem().isValidRepairItem(armor, held)
                && armor.isDamaged()) {
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            int repair = (int) (armor.getMaxDamage() * 0.125F);
            armor.setDamageValue(Math.max(0, armor.getDamageValue() - repair));
            blackBeast.playSound(ModSounds.WOLF_ARMOR_REPAIR.get(), 1.0F, 1.0F);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
}
