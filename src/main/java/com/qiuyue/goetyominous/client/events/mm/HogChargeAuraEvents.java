package com.qiuyue.goetyominous.client.events.mm;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.client.render.ChargeAuraRenderer;
import com.Polarice3.Goety.common.entities.util.ChargeAura;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.effects.FieryHideEffect;
import com.qiuyue.goetyominous.common.magic.spells.mm.HogChargeSpell;
import com.qiuyue.goetyominous.compat.mod.MutantMoreCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HogChargeAuraEvents {

    private static final Map<UUID, ChargeAura> AURAS = new HashMap<>();

    private static ChargeAura.Settings settings;

    private HogChargeAuraEvents() {
    }

    private static ChargeAura.Settings createSettings() {
        ColorUtil aura = FieryHideEffect.FIERY_HIDE_AURA;
        float red = aura.red();
        float green = aura.green();
        float blue = aura.blue();
        int color = (int) (red * 255.0F) << 16 | (int) (green * 255.0F) << 8 | (int) (blue * 255.0F);
        return new ChargeAura.Settings(
                1.404F, 1.872F, 0.15F, 0.5F,
                red, red,
                green * 0.9F, green,
                blue, blue,
                0.35F,
                color, color,
                ParticleTypes.FLAME);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !MutantMoreCompat.isMutantMoreLoaded()) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            AURAS.clear();
            return;
        }
        if (settings == null) {
            settings = createSettings();
        }
        Set<UUID> visible = new HashSet<>();
        for (Player player : level.players()) {
            ChargeAura aura = AURAS.computeIfAbsent(player.getUUID(), uuid -> new ChargeAura(settings));
            aura.tick(player, isCasting(player));
            if (aura.isVisible()) {
                visible.add(player.getUUID());
            }
        }
        AURAS.keySet().retainAll(visible);
    }

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Post event) {
        if (!MutantMoreCompat.isMutantMoreLoaded()) {
            return;
        }
        ChargeAura aura = AURAS.get(event.getEntity().getUUID());
        if (aura == null || !aura.isVisible()) {
            return;
        }
        ChargeAuraRenderer.render(event.getEntity(), aura, event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource());
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
