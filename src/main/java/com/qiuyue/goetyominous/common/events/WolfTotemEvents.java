package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.WaystoneItem;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.entities.WolfTotemHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class WolfTotemEvents {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide() != LogicalSide.SERVER) return;
        LivingEntity target = event.getTarget() instanceof LivingEntity le ? le : null;
        if (target == null) return;
        ItemStack held = event.getItemStack();
        if (!held.is(ModItems.WAYSTONE.get())) {
            return;
        }
        if (!WaystoneItem.hasBlock(held)) return;

        InteractionResult result = WolfTotemHooks.tryLinkToTotem(held, event.getEntity(), target, event.getHand());
        if (result.consumesAction()) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    private static void teleportBesideTotem(LivingEntity entity, BlockPos totemPos) {
        Level level = entity.level();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int radius = 1; radius <= 4; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) {
                        continue;
                    }
                    pos.set(totemPos.getX() + dx, totemPos.getY() + 2, totemPos.getZ() + dz);
                    while (pos.getY() > level.getMinBuildHeight() && !level.getBlockState(pos).isSolid()) {
                        pos.move(0, -1, 0);
                    }
                    if (level.getBlockState(pos).isSolid()) {
                        Vec3 vec3 = new Vec3(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                        if (level.noCollision(entity.getDimensions(entity.getPose()).makeBoundingBox(vec3))) {
                            entity.teleportTo(vec3.x, vec3.y, vec3.z);
                            return;
                        }
                    }
                }
            }
        }
        entity.teleportTo(totemPos.getX() + 0.5, totemPos.getY() + 2, totemPos.getZ() + 0.5);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof IOwned owned) || !(owned.getTrueOwner() instanceof Player)) return;
        LivingEntity entity = event.getEntity();
        if (WolfTotemHooks.canRevive(entity, event.getSource())) {
            event.setCanceled(true);
            WolfTotemHooks.onRevive(entity);
            BlockPos revivePos = WolfTotemHooks.getStoredRevivePos(owned);
            if (revivePos != null) {
                teleportBesideTotem(entity, revivePos);
            }
            entity.setHealth(Math.max(1.0F, entity.getHealth()));
        }
    }
}
