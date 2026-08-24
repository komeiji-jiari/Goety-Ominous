/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IWand
 *  com.Polarice3.Goety.api.magic.ISpell
 *  com.Polarice3.Goety.api.magic.SpellType
 *  com.Polarice3.Goety.common.items.magic.DarkWand
 *  com.mojang.blaze3d.vertex.PoseStack
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.model.HumanoidModel$ArmPose
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.UseAnim
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraftforge.client.extensions.common.IClientItemExtensions
 *  net.minecraftforge.common.capabilities.ICapabilityProvider
 */
package com.vivideru.masteryofmagic.item;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vivideru.masteryofmagic.MasterStaffHelper;
import com.vivideru.masteryofmagic.capability.MasterStaffCapability;
import com.vivideru.masteryofmagic.capability.MasterStaffItemHandler;
import com.vivideru.masteryofmagic.client.ModKeyMappings;
import com.vivideru.masteryofmagic.client.renderer.MasterStaffItemRenderer;
import com.vivideru.masteryofmagic.mixins.LivingEntityUseItemAccessor;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class MasterStaffItem
extends DarkWand {
    public static final int SKIN_COUNT = 8;

    public MasterStaffItem() {
        super(new Item.Properties().m_41487_(1).setNoRepair().m_41497_(Rarity.EPIC), SpellType.NONE);
    }

    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new MasterStaffCapability(stack);
    }

    public ISpell getSpell(ItemStack masterStaff) {
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        Item item = selectedWand.m_41720_();
        if (item instanceof IWand) {
            IWand wand = (IWand)item;
            return wand.getSpell(selectedWand);
        }
        return null;
    }

    public float getWandVisualHeight(Level level, LivingEntity entity, ItemStack masterStaff) {
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        Item item = selectedWand.m_41720_();
        if (item instanceof IWand) {
            IWand wand = (IWand)item;
            return wand.getWandVisualHeight(level, entity, selectedWand);
        }
        return super.getWandVisualHeight(level, entity, masterStaff);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder delegatedResult;
        ItemStack masterStaff = player.m_21120_(hand);
        MasterStaffItemHandler handler = MasterStaffHelper.getHandler(masterStaff);
        if (handler == null || handler.getSelectedWand().m_41619_()) {
            return InteractionResultHolder.m_19098_((Object)masterStaff);
        }
        ItemStack selectedWand = handler.getSelectedWand();
        player.m_21008_(hand, selectedWand);
        try {
            delegatedResult = selectedWand.m_41720_().m_7203_(level, player, hand);
        }
        finally {
            player.m_21008_(hand, masterStaff);
            if (player.m_6117_()) {
                ((LivingEntityUseItemAccessor)player).goetyMasteryOfMagic$setUseItem(masterStaff);
            }
        }
        ItemStack delegatedStack = (ItemStack)delegatedResult.m_19095_();
        int activeSlot = handler.getActiveSlot();
        ItemStack storedStack = delegatedStack.m_41619_() || handler.isItemValid(activeSlot, delegatedStack) ? delegatedStack : selectedWand;
        handler.setStackInSlot(activeSlot, storedStack);
        handler.markDirty();
        return new InteractionResultHolder(delegatedResult.m_19089_(), (Object)masterStaff);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void m_5929_(Level level, LivingEntity entity, ItemStack masterStaff, int remainingUseDuration) {
        boolean replaceHeldStack;
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        if (selectedWand.m_41619_()) {
            entity.m_5810_();
            return;
        }
        InteractionHand usedHand = entity.m_7655_();
        boolean bl = replaceHeldStack = entity.m_21120_(usedHand) == masterStaff;
        if (replaceHeldStack) {
            entity.m_21008_(usedHand, selectedWand);
        }
        ((LivingEntityUseItemAccessor)entity).goetyMasteryOfMagic$setUseItem(selectedWand);
        try {
            selectedWand.m_41720_().m_5929_(level, entity, selectedWand, remainingUseDuration);
        }
        finally {
            if (replaceHeldStack) {
                entity.m_21008_(usedHand, masterStaff);
            }
            if (entity.m_6117_()) {
                ((LivingEntityUseItemAccessor)entity).goetyMasteryOfMagic$setUseItem(masterStaff);
            }
        }
        MasterStaffItem.markDirty(masterStaff, selectedWand);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void m_5551_(ItemStack masterStaff, Level level, LivingEntity entity, int remainingUseDuration) {
        boolean replaceHeldStack;
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        if (selectedWand.m_41619_()) {
            return;
        }
        InteractionHand usedHand = entity.m_7655_();
        boolean bl = replaceHeldStack = entity.m_21120_(usedHand) == masterStaff;
        if (replaceHeldStack) {
            entity.m_21008_(usedHand, selectedWand);
        }
        ((LivingEntityUseItemAccessor)entity).goetyMasteryOfMagic$setUseItem(selectedWand);
        try {
            selectedWand.m_41720_().m_5551_(selectedWand, level, entity, remainingUseDuration);
        }
        finally {
            if (replaceHeldStack) {
                entity.m_21008_(usedHand, masterStaff);
            }
            if (entity.m_6117_()) {
                ((LivingEntityUseItemAccessor)entity).goetyMasteryOfMagic$setUseItem(masterStaff);
            }
        }
        MasterStaffItem.markDirty(masterStaff, selectedWand);
    }

    public ItemStack m_5922_(ItemStack masterStaff, Level level, LivingEntity entity) {
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        if (!selectedWand.m_41619_()) {
            ItemStack result = selectedWand.m_41720_().m_5922_(selectedWand, level, entity);
            MasterStaffHelper.persistSelectedWand(masterStaff, result);
        }
        return masterStaff;
    }

    public int m_8105_(ItemStack masterStaff) {
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        return selectedWand.m_41619_() ? super.m_8105_(masterStaff) : selectedWand.m_41779_();
    }

    public UseAnim m_6164_(ItemStack masterStaff) {
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        return selectedWand.m_41619_() ? UseAnim.NONE : selectedWand.m_41780_();
    }

    public InteractionResult m_6880_(ItemStack masterStaff, Player player, LivingEntity target, InteractionHand hand) {
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        if (!selectedWand.m_41619_()) {
            InteractionResult result = selectedWand.m_41647_(player, target, hand);
            MasterStaffItem.markDirty(masterStaff, selectedWand);
            return result;
        }
        return InteractionResult.PASS;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public InteractionResult m_6225_(UseOnContext context) {
        ItemStack masterStaff = context.m_43722_();
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        Player player = context.m_43723_();
        if (selectedWand.m_41619_() || player == null) {
            return InteractionResult.PASS;
        }
        InteractionHand hand = context.m_43724_();
        BlockHitResult hitResult = new BlockHitResult(context.m_43720_(), context.m_43719_(), context.m_8083_(), context.m_43721_());
        player.m_21008_(hand, selectedWand);
        try {
            InteractionResult result = selectedWand.m_41720_().m_6225_(new UseOnContext(player, hand, hitResult));
            MasterStaffItem.markDirty(masterStaff, selectedWand);
            InteractionResult interactionResult = result;
            return interactionResult;
        }
        finally {
            player.m_21008_(hand, masterStaff);
        }
    }

    public boolean onLeftClickEntity(ItemStack masterStaff, Player player, Entity entity) {
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        if (selectedWand.m_41619_()) {
            return false;
        }
        boolean cancelAttack = selectedWand.m_41720_().onLeftClickEntity(selectedWand, player, entity);
        MasterStaffItem.markDirty(masterStaff, selectedWand);
        return cancelAttack;
    }

    public void m_6883_(ItemStack masterStaff, Level level, Entity entity, int slot, boolean selected) {
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        if (!selectedWand.m_41619_() && selectedWand.m_41720_() != this) {
            selectedWand.m_41666_(level, entity, slot, selected);
            if (!level.m_5776_()) {
                MasterStaffItem.markDirty(masterStaff, selectedWand);
            }
        }
    }

    public int SoulUse(LivingEntity entity, ItemStack masterStaff) {
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        Item item = selectedWand.m_41720_();
        if (item instanceof IWand) {
            IWand wand = (IWand)item;
            return wand.SoulUse(entity, selectedWand);
        }
        return 0;
    }

    public int SoulCost(ItemStack masterStaff) {
        DarkWand wand = MasterStaffItem.selectedDarkWand(masterStaff);
        return wand == null ? super.SoulCost(masterStaff) : wand.SoulCost(MasterStaffHelper.getSelectedWand(masterStaff));
    }

    public int CastDuration(ItemStack masterStaff) {
        DarkWand wand = MasterStaffItem.selectedDarkWand(masterStaff);
        return wand == null ? super.CastDuration(masterStaff) : wand.CastDuration(MasterStaffHelper.getSelectedWand(masterStaff));
    }

    public int Cooldown(ItemStack masterStaff) {
        DarkWand wand = MasterStaffItem.selectedDarkWand(masterStaff);
        return wand == null ? super.Cooldown(masterStaff) : wand.Cooldown(MasterStaffHelper.getSelectedWand(masterStaff));
    }

    public int ShotsFired(ItemStack masterStaff) {
        DarkWand wand = MasterStaffItem.selectedDarkWand(masterStaff);
        return wand == null ? super.ShotsFired(masterStaff) : wand.ShotsFired(MasterStaffHelper.getSelectedWand(masterStaff));
    }

    public void increaseShots(ItemStack masterStaff) {
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        DarkWand wand = MasterStaffItem.directDarkWand(selectedWand);
        if (wand == null) {
            super.increaseShots(masterStaff);
        } else {
            wand.increaseShots(selectedWand);
            MasterStaffItem.markDirty(masterStaff, selectedWand);
        }
    }

    public void setShots(ItemStack masterStaff, int shots) {
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        DarkWand wand = MasterStaffItem.directDarkWand(selectedWand);
        if (wand == null) {
            super.setShots(masterStaff, shots);
        } else {
            wand.setShots(selectedWand, shots);
            MasterStaffItem.markDirty(masterStaff, selectedWand);
        }
    }

    public SoundEvent CastingSound(ItemStack masterStaff, LivingEntity entity) {
        DarkWand wand = MasterStaffItem.selectedDarkWand(masterStaff);
        return wand == null ? super.CastingSound(masterStaff, entity) : wand.CastingSound(MasterStaffHelper.getSelectedWand(masterStaff), entity);
    }

    public float castingVolume(ItemStack masterStaff) {
        DarkWand wand = MasterStaffItem.selectedDarkWand(masterStaff);
        return wand == null ? super.castingVolume(masterStaff) : wand.castingVolume(MasterStaffHelper.getSelectedWand(masterStaff));
    }

    public float castingPitch(ItemStack masterStaff) {
        DarkWand wand = MasterStaffItem.selectedDarkWand(masterStaff);
        return wand == null ? super.castingPitch(masterStaff) : wand.castingPitch(MasterStaffHelper.getSelectedWand(masterStaff));
    }

    public boolean canCastTouch(ItemStack masterStaff, Level level, LivingEntity entity, @Nullable LivingEntity target) {
        DarkWand wand = MasterStaffItem.selectedDarkWand(masterStaff);
        return wand == null ? super.canCastTouch(masterStaff, level, entity, target) : wand.canCastTouch(MasterStaffHelper.getSelectedWand(masterStaff), level, entity, target);
    }

    public void setSpellConditions(ISpell spell, ItemStack masterStaff, LivingEntity entity) {
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        DarkWand wand = MasterStaffItem.directDarkWand(selectedWand);
        if (wand == null) {
            super.setSpellConditions(spell, masterStaff, entity);
        } else {
            wand.setSpellConditions(spell, selectedWand, entity);
            MasterStaffItem.markDirty(masterStaff, selectedWand);
        }
    }

    public void MagicResults(ItemStack masterStaff, Level level, LivingEntity entity) {
        ISpell spell = this.getSpell(masterStaff);
        if (spell != null) {
            this.MagicResults(masterStaff, level, entity, spell);
        }
    }

    public void MagicResults(ItemStack masterStaff, Level level, LivingEntity entity, ISpell spell) {
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        DarkWand wand = MasterStaffItem.directDarkWand(selectedWand);
        if (wand == null) {
            super.MagicResults(masterStaff, level, entity, spell);
        } else {
            wand.MagicResults(selectedWand, level, entity, spell);
            MasterStaffItem.markDirty(masterStaff, selectedWand);
        }
    }

    public boolean m_5812_(ItemStack masterStaff) {
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        return selectedWand.m_41790_() || super.m_5812_(masterStaff);
    }

    public void m_7373_(ItemStack masterStaff, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
        int activeSlot = MasterStaffHelper.getActiveSlot(masterStaff) + 1;
        tooltip.add((Component)Component.m_237110_((String)"tooltip.goety_mastery_of_magic.master_staff.slot", (Object[])new Object[]{activeSlot}).m_130940_(ChatFormatting.GRAY));
        if (selectedWand.m_41619_()) {
            tooltip.add((Component)Component.m_237115_((String)"tooltip.goety_mastery_of_magic.master_staff.empty").m_130940_(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add((Component)Component.m_237110_((String)"tooltip.goety_mastery_of_magic.master_staff.selected", (Object[])new Object[]{selectedWand.m_41786_()}).m_130940_(ChatFormatting.LIGHT_PURPLE));
            ItemStack focus = IWand.getFocus((ItemStack)selectedWand);
            if (!focus.m_41619_()) {
                tooltip.add((Component)Component.m_237110_((String)"tooltip.goety_mastery_of_magic.master_staff.focus", (Object[])new Object[]{focus.m_41786_()}).m_130940_(ChatFormatting.AQUA));
            }
        }
        tooltip.add((Component)Component.m_237110_((String)"tooltip.goety_mastery_of_magic.master_staff.controls", (Object[])new Object[]{ModKeyMappings.CYCLE_MASTER_STAFF.m_90863_(), ModKeyMappings.OPEN_MASTER_STAFF.m_90863_()}).m_130940_(ChatFormatting.BLUE));
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions(){
            private final MasterStaffItemRenderer renderer = new MasterStaffItemRenderer();

            private ItemStack selectedOrMaster(ItemStack masterStaff) {
                ItemStack selectedWand = MasterStaffHelper.getSelectedWand(masterStaff);
                return selectedWand.m_41619_() ? masterStaff : selectedWand;
            }

            public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack masterStaff) {
                ItemStack animatedStack = this.selectedOrMaster(masterStaff);
                if (animatedStack == masterStaff) {
                    return super.getArmPose(entity, hand, masterStaff);
                }
                return IClientItemExtensions.of((ItemStack)animatedStack).getArmPose(entity, hand, animatedStack);
            }

            public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack masterStaff, float partialTick, float equipProcess, float swingProcess) {
                ItemStack animatedStack = this.selectedOrMaster(masterStaff);
                if (animatedStack == masterStaff) {
                    return false;
                }
                return IClientItemExtensions.of((ItemStack)animatedStack).applyForgeHandTransform(poseStack, player, arm, animatedStack, partialTick, equipProcess, swingProcess);
            }

            public MasterStaffItemRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }

    @Nullable
    private static DarkWand selectedDarkWand(ItemStack masterStaff) {
        DarkWand darkWand;
        Item item = MasterStaffHelper.getSelectedWand(masterStaff).m_41720_();
        return item instanceof DarkWand ? (darkWand = (DarkWand)item) : null;
    }

    @Nullable
    private static DarkWand directDarkWand(ItemStack selectedWand) {
        DarkWand darkWand;
        Item item = selectedWand.m_41720_();
        return item instanceof DarkWand ? (darkWand = (DarkWand)item) : null;
    }

    private static void markDirty(ItemStack masterStaff, ItemStack selectedWand) {
        MasterStaffHelper.persistSelectedWand(masterStaff, selectedWand);
    }
}

