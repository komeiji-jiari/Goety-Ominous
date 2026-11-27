package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.common.entities.ally.HoglinServant;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.items.NetherWartPotion;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class HoglinServantImmunityEvents {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide() != LogicalSide.SERVER) return;
        if (!(event.getTarget() instanceof HoglinServant hoglin)) return;
        if (!(event.getItemStack().getItem() instanceof NetherWartPotion)) return;

        Player player = event.getEntity();
        LivingEntity owner = hoglin.getTrueOwner();
        if (owner == null || !owner.getUUID().equals(player.getUUID())) return;

        CompoundTag tag = new CompoundTag();
        hoglin.addAdditionalSaveData(tag);
        if (tag.getBoolean("IsImmuneToZombification")) return;

        ItemStack held = event.getItemStack();
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            if (!player.getInventory().add(bottle)) {
                player.drop(bottle, false);
            }
        }
        hoglin.setImmuneToZombification(true);
        hoglin.playSound(SoundEvents.PIGLIN_CELEBRATE, 1.0F, 1.0F);
        if (hoglin.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    hoglin.getX(), hoglin.getY() + 1.0D, hoglin.getZ(),
                    7, 0.5D, 0.5D, 0.5D, 0.0D);
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
