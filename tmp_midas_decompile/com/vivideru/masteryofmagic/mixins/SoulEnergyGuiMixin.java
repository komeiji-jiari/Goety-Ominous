/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IFocus
 *  com.Polarice3.Goety.api.magic.IChargingSpell
 *  com.Polarice3.Goety.api.magic.ISpell
 *  com.Polarice3.Goety.client.gui.overlay.SoulEnergyGui
 *  com.Polarice3.Goety.config.MainConfig
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.api.magic.IChargingSpell;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.client.gui.overlay.SoulEnergyGui;
import com.Polarice3.Goety.config.MainConfig;
import com.vivideru.masteryofmagic.SpellRingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SoulEnergyGui.class})
public class SoulEnergyGuiMixin {
    @Inject(method={"drawHUD"}, at={@At(value="TAIL")}, remap=false)
    private static void goetyMasteryOfMagic$drawSpellRingChargeBar(ForgeGui gui, GuiGraphics guiGraphics, float partialTicks, int screenWidth, int screenHeight, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.m_91087_();
        LocalPlayer player = minecraft.f_91074_;
        if (player == null) {
            return;
        }
        ItemStack ring = SpellRingHelper.findSpellRing((Player)player);
        if (ring.m_41619_()) {
            return;
        }
        int slot = SpellRingHelper.getCastingSlot(ring);
        if (slot < 0) {
            return;
        }
        ItemStack focus = SpellRingHelper.getFocus(ring, slot);
        Item item = focus.m_41720_();
        if (!(item instanceof IFocus)) {
            return;
        }
        IFocus magicFocus = (IFocus)item;
        ISpell spell = magicFocus.getSpell();
        if (!(spell instanceof IChargingSpell)) {
            return;
        }
        IChargingSpell chargingSpell = (IChargingSpell)spell;
        int castUp = chargingSpell.castUp((LivingEntity)player, ring);
        if (castUp <= 0) {
            return;
        }
        int castTime = SpellRingHelper.getSlotCastTime(ring, slot);
        float progress = (float)castTime / (float)castUp;
        progress = Math.min(1.0f, Math.max(0.0f, progress));
        int x = screenWidth / 2 + (Integer)MainConfig.SoulGuiHorizontal.get();
        int height = screenHeight + (Integer)MainConfig.SoulGuiVertical.get();
        int useTime = (int)(117.0f * progress);
        guiGraphics.m_280163_(new ResourceLocation("goety", "textures/gui/soul_energy.png"), x + 9, height - 9, 9.0f, 27.0f, useTime, 9, 128, 90);
    }
}

