package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.common.entities.ally.BlackWolf;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonWolf;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.common.items.CursedMetalWolfArmorItem;
import com.qiuyue.goetyominous.utils.GoetyOminousWolfArmorUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class WolfArmorEquipHandler {

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Summoned summoned
                && (summoned instanceof BlackWolf || summoned instanceof SkeletonWolf)) {
            GoetyOminousWolfArmorUtil.equipRingGrantedArmor(summoned);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide() != LogicalSide.SERVER) return;

        LivingEntity target = event.getTarget() instanceof LivingEntity le ? le : null;
        if (target == null) return;

        boolean isWolf = target instanceof Wolf
                || target instanceof BlackWolf
                || target instanceof SkeletonWolf;
        if (!isWolf) return;

        Player player = event.getEntity();
        ItemStack held = event.getItemStack();
        ItemStack armor = target.getItemBySlot(EquipmentSlot.CHEST);

        boolean owned;
        if (target instanceof Wolf wolf) {
            owned = wolf.isTame() && wolf.isOwnedBy(player);
        } else if (target instanceof Owned ownedEntity) {
            owned = ownedEntity.getTrueOwner() == player;
        } else {
            owned = false;
        }
        if (!owned) return;

        if (held.getItem() instanceof CursedMetalWolfArmorItem) {
            if (!armor.isEmpty()) return;
            if (player.getAbilities().instabuild) {
                target.setItemSlot(EquipmentSlot.CHEST, held.copy());
            } else {
                target.setItemSlot(EquipmentSlot.CHEST, held.split(1));
            }
            target.playSound(ModSounds.WOLF_ARMOR_EQUIP.get(), 1.0F, 1.0F);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (held.is(Items.SHEARS)
                && armor.getItem() instanceof CursedMetalWolfArmorItem
                && !armor.getOrCreateTag().getBoolean(GoetyOminousWolfArmorUtil.SUMMONED_ARMOR_TAG)) {
            held.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(event.getHand()));
            target.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
            target.spawnAtLocation(armor);
            target.playSound(ModSounds.WOLF_ARMOR_UNEQUIP.get(), 1.0F, 1.0F);
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
            target.playSound(ModSounds.WOLF_ARMOR_REPAIR.get(), 1.0F, 1.0F);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
}
