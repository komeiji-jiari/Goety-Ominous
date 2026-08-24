/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.ally.Summoned
 *  com.Polarice3.Goety.common.items.ModItems
 *  com.Polarice3.Goety.utils.CuriosFinder
 *  com.Polarice3.Goety.utils.SEHelper
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.SEHelper;
import com.vivideru.masteryofmagic.ForgeRingEquipmentHelper;
import com.vivideru.masteryofmagic.enchantment.DarkenedEnchantment;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Summoned.class})
public abstract class SummonedMixin {
    @Unique
    private boolean goetyMasteryOfMagic$forgeRingEquipmentApplied = false;

    @Redirect(method={"canSpawnArmor"}, at=@At(value="INVOKE", target="Lcom/Polarice3/Goety/utils/CuriosFinder;hasCurio(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/Item;)Z"), remap=false, require=0)
    private boolean goetyMasteryOfMagic$empoweredForgeRingCountsAsForgeRing(LivingEntity owner, Item forgeRing) {
        return CuriosFinder.hasCurio((LivingEntity)owner, (Item)((Item)ModItems.RING_OF_THE_FORGE.get())) || CuriosFinder.hasCurio((LivingEntity)owner, (Item)((Item)GoetyMasteryOfMagicModItems.EMPOWERED_FORGE_RING.get()));
    }

    @Inject(method={"tick()V", "m_8119_()V"}, at={@At(value="HEAD")}, remap=false, require=0)
    private void goetyMasteryOfMagic$useEmpoweredForgeRingEquipmentOnTick(CallbackInfo ci) {
        this.goetyMasteryOfMagic$repairDarkenedEquipment();
        if (this.goetyMasteryOfMagic$forgeRingEquipmentApplied) {
            return;
        }
        this.goetyMasteryOfMagic$tryApplyForgeRingEquipment();
    }

    @Unique
    private void goetyMasteryOfMagic$repairDarkenedEquipment() {
        Summoned summoned = (Summoned)this;
        Mob mob = (Mob)this;
        if (mob.m_9236_().f_46443_ || mob.f_19797_ % 20 != 0) {
            return;
        }
        LivingEntity livingEntity = summoned.getTrueOwner();
        if (!(livingEntity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer owner = (ServerPlayer)livingEntity;
        int costPerPoint = 2;
        for (ItemStack stack : mob.m_6168_()) {
            if (stack.m_41619_() || !stack.m_41763_() || !stack.m_41768_() || !DarkenedEnchantment.has(stack) || SEHelper.getSoulAmountInt((Player)owner) < costPerPoint) continue;
            SEHelper.decreaseSouls((Player)owner, (int)costPerPoint);
            stack.m_41721_(Math.max(0, stack.m_41773_() - 1));
        }
    }

    @Unique
    private boolean goetyMasteryOfMagic$tryApplyForgeRingEquipment() {
        Summoned summoned = (Summoned)this;
        Mob mob = (Mob)this;
        CompoundTag mobData = mob.getPersistentData();
        if (mob.f_19797_ <= 5) {
            mobData.m_128379_("GoetyMasteryForgeRingSpawnedAfterInstall", true);
        }
        if (mob.f_19797_ > 5 && !mobData.m_128471_("GoetyMasteryForgeRingSpawnedAfterInstall")) {
            mobData.m_128379_("GoetyMasteryForgeRingChecked", true);
            this.goetyMasteryOfMagic$forgeRingEquipmentApplied = true;
            return false;
        }
        if (mobData.m_128471_("GoetyMasteryForgeRingChecked")) {
            this.goetyMasteryOfMagic$forgeRingEquipmentApplied = true;
            return false;
        }
        LivingEntity owner = summoned.getTrueOwner();
        if (owner == null) {
            if (mob.f_19797_ > 20) {
                mobData.m_128379_("GoetyMasteryForgeRingChecked", true);
                this.goetyMasteryOfMagic$forgeRingEquipmentApplied = true;
            }
            return false;
        }
        ItemStack ring = ForgeRingEquipmentHelper.getEmpoweredForgeRing(owner);
        if (ring.m_41619_() || !ring.m_41782_()) {
            if (mob.f_19797_ > 20) {
                mobData.m_128379_("GoetyMasteryForgeRingChecked", true);
                this.goetyMasteryOfMagic$forgeRingEquipmentApplied = true;
            }
            return false;
        }
        CompoundTag ringTag = ring.m_41784_();
        if (!(ringTag.m_128441_("ForgeHelmet") || ringTag.m_128441_("ForgeChestplate") || ringTag.m_128441_("ForgeLeggings") || ringTag.m_128441_("ForgeBoots") || ringTag.m_128441_("ForgeMainHand") || ringTag.m_128441_("ForgeOffHand") || ringTag.m_128441_("ForgeWeapons"))) {
            mobData.m_128379_("GoetyMasteryForgeRingChecked", true);
            this.goetyMasteryOfMagic$forgeRingEquipmentApplied = true;
            return false;
        }
        ForgeRingEquipmentHelper.applySavedEquipment(mob, ring);
        mobData.m_128379_("GoetyMasteryForgeRingChecked", true);
        this.goetyMasteryOfMagic$forgeRingEquipmentApplied = true;
        return true;
    }
}

