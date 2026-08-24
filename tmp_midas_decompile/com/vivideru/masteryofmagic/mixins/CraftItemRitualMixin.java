/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity
 *  com.Polarice3.Goety.common.ritual.CraftItemRitual
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.items.IItemHandlerModifiable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.ritual.CraftItemRitual;
import com.vivideru.masteryofmagic.ForgeRingEquipmentHelper;
import com.vivideru.masteryofmagic.MasteryData;
import com.vivideru.masteryofmagic.SupremeMasteryAdvancementHelper;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import com.vivideru.masteryofmagic.item.UndeadBloodVialItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={CraftItemRitual.class})
public class CraftItemRitualMixin {
    @Unique
    private boolean goetyMasteryOfMagic$isEmpoweredForgeRingRitual;
    @Unique
    private CompoundTag goetyMasteryOfMagic$previousForgeRingData;
    @Unique
    private List<ItemStack> goetyMasteryOfMagic$consumedForgeRingIngredients = List.of();
    @Unique
    private String goetyMasteryOfMagic$ritualAnimalShape = "";

    @Inject(method={"finish(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lcom/Polarice3/Goety/common/blocks/entities/DarkAltarBlockEntity;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"}, at={@At(value="HEAD")}, remap=false)
    private void goetyMasteryOfMagic$detectEmpoweredForgeRing(Level world, BlockPos blockPos, DarkAltarBlockEntity tileEntity, Player castingPlayer, ItemStack activationItem, CallbackInfo ci) {
        this.goetyMasteryOfMagic$ritualAnimalShape = UndeadBloodVialItem.getAnimalShape(activationItem);
        this.goetyMasteryOfMagic$isEmpoweredForgeRingRitual = activationItem.m_150930_((Item)GoetyMasteryOfMagicModItems.EMPOWERED_FORGE_RING.get());
        CompoundTag previousData = this.goetyMasteryOfMagic$isEmpoweredForgeRingRitual ? activationItem.m_41783_() : null;
        CompoundTag compoundTag = this.goetyMasteryOfMagic$previousForgeRingData = previousData == null ? null : previousData.m_6426_();
        if (this.goetyMasteryOfMagic$isEmpoweredForgeRingRitual) {
            ArrayList<ItemStack> consumedSnapshot = new ArrayList<ItemStack>();
            for (ItemStack consumed : tileEntity.consumedIngredients) {
                if (consumed.m_41619_()) continue;
                consumedSnapshot.add(consumed.m_41777_());
            }
            this.goetyMasteryOfMagic$consumedForgeRingIngredients = consumedSnapshot;
        } else {
            this.goetyMasteryOfMagic$consumedForgeRingIngredients = List.of();
        }
    }

    @Inject(method={"finish(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lcom/Polarice3/Goety/common/blocks/entities/DarkAltarBlockEntity;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"}, at={@At(value="TAIL")}, remap=false)
    private void goetyMasteryOfMagic$saveForgeRingEquipment(Level world, BlockPos blockPos, DarkAltarBlockEntity tileEntity, Player castingPlayer, ItemStack activationItem, CallbackInfo ci) {
        if (world.f_46443_) {
            return;
        }
        IItemHandler handler = (IItemHandler)tileEntity.itemStackHandler.orElseThrow(RuntimeException::new);
        ItemStack result = handler.getStackInSlot(0);
        if (result.m_150930_((Item)GoetyMasteryOfMagicModItems.THERIANTHROPY_RITUAL_TOKEN.get())) {
            handler.extractItem(0, result.m_41613_(), false);
            MasteryData.setPlanetSavedShape(castingPlayer, this.goetyMasteryOfMagic$ritualAnimalShape);
            MasteryData.setPlanetShapeGift(castingPlayer, true);
            castingPlayer.m_5661_((Component)Component.m_237115_((String)"message.goety_mastery_of_magic.planet_shape.inherited"), true);
            this.goetyMasteryOfMagic$ritualAnimalShape = "";
            return;
        }
        if (result.m_150930_((Item)GoetyMasteryOfMagicModItems.SUPREME_NETHER_MASTERY_TOKEN.get())) {
            handler.extractItem(0, result.m_41613_(), false);
            MasteryData.setSupreme(castingPlayer, MasteryData.SupremeSchool.NETHER, true);
            SupremeMasteryAdvancementHelper.grantSchool(castingPlayer, MasteryData.SupremeSchool.NETHER);
            castingPlayer.m_5661_((Component)Component.m_237115_((String)"message.goety_mastery_of_magic.supreme_nether_unlocked"), true);
            return;
        }
        if (result.m_150930_((Item)GoetyMasteryOfMagicModItems.SUPREME_SKIES_ATTUNEMENT_TOKEN.get())) {
            MasteryData.setPendingSkiesAltar(castingPlayer, world.m_46472_().m_135782_().toString(), blockPos.m_121878_(), Long.MAX_VALUE);
            castingPlayer.m_5661_((Component)Component.m_237115_((String)"message.goety_mastery_of_magic.supreme_skies_first_complete"), true);
            return;
        }
        if (result.m_150930_((Item)GoetyMasteryOfMagicModItems.SUPREME_SKIES_MASTERY_TOKEN.get())) {
            long packed = MasteryData.getPendingSkiesAltar(castingPlayer, world.m_46472_().m_135782_().toString(), world.m_46467_());
            if (packed == Long.MIN_VALUE) {
                return;
            }
            BlockPos first = BlockPos.m_122022_((long)packed);
            if (first.m_123331_((Vec3i)blockPos) > 256.0 || first.equals((Object)blockPos)) {
                return;
            }
            BlockEntity blockEntity = world.m_7702_(first);
            if (blockEntity instanceof DarkAltarBlockEntity) {
                DarkAltarBlockEntity firstAltar = (DarkAltarBlockEntity)blockEntity;
                IItemHandler firstHandler = (IItemHandler)firstAltar.itemStackHandler.orElse(null);
                if (firstHandler == null || !firstHandler.getStackInSlot(0).m_150930_((Item)GoetyMasteryOfMagicModItems.SUPREME_SKIES_ATTUNEMENT_TOKEN.get())) {
                    return;
                }
                firstHandler.extractItem(0, 1, false);
                handler.extractItem(0, result.m_41613_(), false);
                MasteryData.clearPendingSkiesAltar(castingPlayer);
                MasteryData.setSupreme(castingPlayer, MasteryData.SupremeSchool.SKIES, true);
                SupremeMasteryAdvancementHelper.grantSchool(castingPlayer, MasteryData.SupremeSchool.SKIES);
                castingPlayer.m_5661_((Component)Component.m_237115_((String)"message.goety_mastery_of_magic.supreme_skies_unlocked"), true);
            }
            return;
        }
        int planetBit = CraftItemRitualMixin.goetyMasteryOfMagic$planetRitualBit(result);
        if (planetBit != 0) {
            handler.extractItem(0, result.m_41613_(), false);
            MasteryData.completePlanetRitual(castingPlayer, planetBit);
            if ((MasteryData.getPlanetRituals(castingPlayer) & 7) == 7) {
                MasteryData.clearPlanetRituals(castingPlayer);
                MasteryData.setSupreme(castingPlayer, MasteryData.SupremeSchool.PLANET, true);
                SupremeMasteryAdvancementHelper.grantSchool(castingPlayer, MasteryData.SupremeSchool.PLANET);
                castingPlayer.m_5661_((Component)Component.m_237115_((String)"message.goety_mastery_of_magic.supreme_planet_unlocked"), true);
            } else {
                castingPlayer.m_5661_((Component)Component.m_237110_((String)"message.goety_mastery_of_magic.supreme_planet_rite_complete", (Object[])new Object[]{Integer.bitCount(MasteryData.getPlanetRituals(castingPlayer))}), true);
            }
            return;
        }
        int wizardryLevel = CraftItemRitualMixin.goetyMasteryOfMagic$wizardryLevel(result);
        if (wizardryLevel > 0) {
            handler.extractItem(0, result.m_41613_(), false);
            MasteryData.setWizardry(castingPlayer, wizardryLevel);
            SupremeMasteryAdvancementHelper.grant(castingPlayer, wizardryLevel);
            castingPlayer.m_5661_((Component)Component.m_237115_((String)("message.goety_mastery_of_magic.wizardry_unlocked." + wizardryLevel)), true);
            return;
        }
        if (!this.goetyMasteryOfMagic$isEmpoweredForgeRingRitual) {
            return;
        }
        if (!result.m_150930_((Item)GoetyMasteryOfMagicModItems.EMPOWERED_FORGE_RING.get())) {
            return;
        }
        CompoundTag completeRingData = ForgeRingEquipmentHelper.mergeConsumedIngredients(this.goetyMasteryOfMagic$previousForgeRingData, this.goetyMasteryOfMagic$consumedForgeRingIngredients);
        result.m_41751_(completeRingData);
        if (handler instanceof IItemHandlerModifiable) {
            IItemHandlerModifiable modifiable = (IItemHandlerModifiable)handler;
            modifiable.setStackInSlot(0, result);
        }
        tileEntity.m_6596_();
        this.goetyMasteryOfMagic$isEmpoweredForgeRingRitual = false;
        this.goetyMasteryOfMagic$previousForgeRingData = null;
        this.goetyMasteryOfMagic$consumedForgeRingIngredients = List.of();
    }

    @Unique
    private static int goetyMasteryOfMagic$wizardryLevel(ItemStack stack) {
        if (stack.m_150930_((Item)GoetyMasteryOfMagicModItems.WIZARDRY_MASTERY_SCROLL_I.get())) {
            return 1;
        }
        if (stack.m_150930_((Item)GoetyMasteryOfMagicModItems.WIZARDRY_MASTERY_SCROLL_II.get())) {
            return 2;
        }
        if (stack.m_150930_((Item)GoetyMasteryOfMagicModItems.WIZARDRY_MASTERY_SCROLL_III.get())) {
            return 3;
        }
        return 0;
    }

    @Unique
    private static int goetyMasteryOfMagic$planetRitualBit(ItemStack stack) {
        if (stack.m_150930_((Item)GoetyMasteryOfMagicModItems.SUPREME_PLANET_WILD_TOKEN.get())) {
            return 1;
        }
        if (stack.m_150930_((Item)GoetyMasteryOfMagicModItems.SUPREME_PLANET_GEOMANCY_TOKEN.get())) {
            return 2;
        }
        if (stack.m_150930_((Item)GoetyMasteryOfMagicModItems.SUPREME_PLANET_DEEP_TOKEN.get())) {
            return 4;
        }
        return 0;
    }
}

