package com.qiuyue.goetyominous.client.events.mm;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.client.render.mm.HogChargeShellRenderer;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantHoglinServant;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.common.magic.spells.mm.HogChargeSpell;
import com.qiuyue.goetyominous.compat.mod.MutantMoreCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class HogChargeShellEvents {

    private static final Map<UUID, MutantHoglinServant> SHELLS = new HashMap<>();
    private static EntityRenderer<? super MutantHoglinServant> shellRenderer;

    private HogChargeShellEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !MutantMoreCompat.isMutantMoreLoaded()) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            SHELLS.clear();
            return;
        }
        Set<UUID> casting = new HashSet<>();
        for (Player player : level.players()) {
            if (!isCasting(player)) {
                continue;
            }
            MutantHoglinServant shell = SHELLS.get(player.getUUID());
            if (shell == null || shell.level() != level) {
                shell = new MutantHoglinServant(MmEntityRegistry.MUTANT_HOGLIN_SERVANT.get(), level);
                SHELLS.put(player.getUUID(), shell);
            }
            tickShell(shell, player);
            casting.add(player.getUUID());
        }
        SHELLS.keySet().retainAll(casting);
    }

    private static void tickShell(MutantHoglinServant shell, Player player) {
        shell.tickCount = player.tickCount;
        shell.setPos(player.getX(), player.getY(), player.getZ());
        double dx = player.getX() - player.xOld;
        double dz = player.getZ() - player.zOld;
        shell.setDeltaMovement(new Vec3(dx, 0.0D, dz));
        shell.setOnGround(player.onGround());
        shell.setCharging(true);
        shell.deathTime = 0;
        shell.setYRot(player.getYRot());
        shell.yRotO = player.yRotO;
        shell.setXRot(player.getXRot());
        shell.xRotO = player.xRotO;
        shell.yBodyRot = player.yBodyRot;
        shell.yBodyRotO = player.yBodyRotO;
        shell.yHeadRot = player.yHeadRot;
        shell.yHeadRotO = player.yHeadRotO;
    }

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Post event) {
        if (!MutantMoreCompat.isMutantMoreLoaded()) {
            return;
        }
        MutantHoglinServant shell = SHELLS.get(event.getEntity().getUUID());
        if (shell == null) {
            return;
        }
        EntityRenderer<? super MutantHoglinServant> renderer = getShellRenderer();
        if (renderer == null) {
            return;
        }
        renderer.render(shell, Mth.lerp(event.getPartialTick(), shell.yRotO, shell.getYRot()), event.getPartialTick(),
                event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
    }

    private static EntityRenderer<? super MutantHoglinServant> getShellRenderer() {
        if (shellRenderer == null) {
            EntityRendererProvider.Context context = MmClientEvents.getRendererContext();
            if (context == null) {
                return null;
            }
            shellRenderer = new HogChargeShellRenderer(context);
        }
        return shellRenderer;
    }

    private static boolean isCasting(Player player) {
        if (!player.isUsingItem()) {
            return false;
        }
        ItemStack stack = player.getUseItem();
        if (!(stack.getItem() instanceof IWand)) {
            return false;
        }
        return WandUtil.getSpellOnHand(player, player.getUsedItemHand()) instanceof HogChargeSpell;
    }
}
