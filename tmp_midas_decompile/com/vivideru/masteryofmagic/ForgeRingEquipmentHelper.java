/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.AxeItem
 *  net.minecraft.world.item.BowItem
 *  net.minecraft.world.item.CrossbowItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.ShieldItem
 *  net.minecraft.world.item.SwordItem
 *  net.minecraft.world.item.TridentItem
 *  net.minecraft.world.level.ItemLike
 *  top.theillusivec4.curios.api.CuriosApi
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.ImprovedForgingRingBlacklist;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.ItemLike;
import top.theillusivec4.curios.api.CuriosApi;

public class ForgeRingEquipmentHelper {
    public static final String HELMET = "ForgeHelmet";
    public static final String CHESTPLATE = "ForgeChestplate";
    public static final String LEGGINGS = "ForgeLeggings";
    public static final String BOOTS = "ForgeBoots";
    public static final String MAINHAND = "ForgeMainHand";
    public static final String OFFHAND = "ForgeOffHand";
    public static final String WEAPONS = "ForgeWeapons";

    public static CompoundTag mergeConsumedIngredients(CompoundTag previousRingData, List<ItemStack> consumedIngredients) {
        CompoundTag result = previousRingData == null ? new CompoundTag() : previousRingData.m_6426_();
        for (ItemStack consumed : consumedIngredients) {
            if (!ForgeRingEquipmentHelper.isValidForgeRingEquipment(consumed)) continue;
            String equipmentKey = ForgeRingEquipmentHelper.getKeyForStack(consumed);
            if (equipmentKey.equals(MAINHAND)) {
                ForgeRingEquipmentHelper.saveWeapon(result, consumed.m_41777_());
                continue;
            }
            ForgeRingEquipmentHelper.saveStack(result, equipmentKey, consumed.m_41777_());
        }
        return result;
    }

    private static void saveWeapon(CompoundTag ringData, ItemStack stack) {
        if (stack.m_41619_()) {
            return;
        }
        ListTag weapons = ringData.m_128437_(WEAPONS, 10).m_6426_();
        CompoundTag stackTag = new CompoundTag();
        stack.m_41777_().m_41739_(stackTag);
        weapons.add((Object)stackTag);
        ringData.m_128365_(WEAPONS, (Tag)weapons);
    }

    private static List<ItemStack> getSavedWeapons(ItemStack ring) {
        ArrayList<ItemStack> result = new ArrayList<ItemStack>();
        CompoundTag tag = ring.m_41784_();
        if (!tag.m_128441_(WEAPONS)) {
            ItemStack legacy;
            if (tag.m_128441_(MAINHAND) && !(legacy = ItemStack.m_41712_((CompoundTag)tag.m_128469_(MAINHAND))).m_41619_()) {
                result.add(legacy);
            }
            return result;
        }
        ListTag weapons = tag.m_128437_(WEAPONS, 10);
        for (int i = 0; i < weapons.size(); ++i) {
            ItemStack stack = ItemStack.m_41712_((CompoundTag)weapons.m_128728_(i));
            if (stack.m_41619_()) continue;
            result.add(stack);
        }
        return result;
    }

    public static ItemStack getEmpoweredForgeRing(LivingEntity entity) {
        AtomicReference<ItemStack> result = new AtomicReference<ItemStack>(ItemStack.f_41583_);
        CuriosApi.getCuriosInventory((LivingEntity)entity).ifPresent(handler -> handler.findFirstCurio(stack -> stack.m_150930_((Item)GoetyMasteryOfMagicModItems.EMPOWERED_FORGE_RING.get())).ifPresent(slotResult -> result.set(slotResult.stack())));
        return result.get();
    }

    public static boolean hasEmpoweredForgeRing(LivingEntity entity) {
        return !ForgeRingEquipmentHelper.getEmpoweredForgeRing(entity).m_41619_();
    }

    public static void applySavedEquipment(Mob mob, ItemStack ring) {
        if (mob == null || ring.m_41619_() || !ring.m_41782_()) {
            return;
        }
        boolean supportsArmor = ForgeRingEquipmentHelper.supportsArmorEquipment(mob);
        if (supportsArmor) {
            ForgeRingEquipmentHelper.applyArmorSlot(mob, ring, EquipmentSlot.HEAD, HELMET);
            ForgeRingEquipmentHelper.applyArmorSlot(mob, ring, EquipmentSlot.CHEST, CHESTPLATE);
            ForgeRingEquipmentHelper.applyArmorSlot(mob, ring, EquipmentSlot.LEGS, LEGGINGS);
            ForgeRingEquipmentHelper.applyArmorSlot(mob, ring, EquipmentSlot.FEET, BOOTS);
        }
        boolean mainUsedSavedMain = false;
        if (ForgeRingEquipmentHelper.supportsWeaponEquipment(mob)) {
            mainUsedSavedMain = ForgeRingEquipmentHelper.applyWeaponSlotUnique(mob, ring, EquipmentSlot.MAINHAND, false);
        }
        if (!mob.m_6844_(EquipmentSlot.OFFHAND).m_41619_()) {
            ForgeRingEquipmentHelper.applyWeaponSlotUnique(mob, ring, EquipmentSlot.OFFHAND, mainUsedSavedMain);
        }
    }

    private static boolean supportsArmorEquipment(Mob mob) {
        return ForgeRingEquipmentHelper.hasAnyArmorEquipped(mob) || ForgeRingEquipmentHelper.queryEquipmentCapability(mob, "canWearArmor");
    }

    private static boolean supportsWeaponEquipment(Mob mob) {
        return !mob.m_6844_(EquipmentSlot.MAINHAND).m_41619_() || !mob.m_6844_(EquipmentSlot.OFFHAND).m_41619_() || ForgeRingEquipmentHelper.queryEquipmentCapability(mob, "canHaveWeapon");
    }

    private static boolean queryEquipmentCapability(Mob mob, String methodName) {
        try {
            Method method = mob.getClass().getMethod(methodName, new Class[0]);
            if (method.getReturnType() == Boolean.TYPE) {
                return (Boolean)method.invoke(mob, new Object[0]);
            }
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            // empty catch block
        }
        return false;
    }

    private static boolean hasAnyArmorEquipped(Mob mob) {
        return !mob.m_6844_(EquipmentSlot.HEAD).m_41619_() || !mob.m_6844_(EquipmentSlot.CHEST).m_41619_() || !mob.m_6844_(EquipmentSlot.LEGS).m_41619_() || !mob.m_6844_(EquipmentSlot.FEET).m_41619_();
    }

    private static boolean supportsNaturalEquipment(Mob mob) {
        for (Class<?> clazz = mob.getClass(); clazz != null && clazz != Mob.class; clazz = clazz.getSuperclass()) {
            for (Method method : clazz.getDeclaredMethods()) {
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length != 2 || parameters[0] != RandomSource.class || parameters[1] != DifficultyInstance.class || method.getReturnType() != Void.TYPE) continue;
                return true;
            }
        }
        return false;
    }

    private static boolean canUseSlot(Mob mob, EquipmentSlot slot) {
        ItemStack current = mob.m_6844_(slot);
        if (!current.m_41619_()) {
            return true;
        }
        ItemStack testStack = ForgeRingEquipmentHelper.getTestStackForSlot(slot);
        if (testStack.m_41619_()) {
            return false;
        }
        mob.m_8061_(slot, testStack);
        ItemStack afterSet = mob.m_6844_(slot);
        boolean accepted = !afterSet.m_41619_() && afterSet.m_150930_(testStack.m_41720_());
        mob.m_8061_(slot, ItemStack.f_41583_);
        return accepted;
    }

    private static ItemStack getTestStackForSlot(EquipmentSlot slot) {
        if (slot == EquipmentSlot.HEAD) {
            return new ItemStack((ItemLike)Items.f_42407_);
        }
        if (slot == EquipmentSlot.CHEST) {
            return new ItemStack((ItemLike)Items.f_42408_);
        }
        if (slot == EquipmentSlot.LEGS) {
            return new ItemStack((ItemLike)Items.f_42462_);
        }
        if (slot == EquipmentSlot.FEET) {
            return new ItemStack((ItemLike)Items.f_42463_);
        }
        if (slot == EquipmentSlot.MAINHAND) {
            return new ItemStack((ItemLike)Items.f_42383_);
        }
        if (slot == EquipmentSlot.OFFHAND) {
            return new ItemStack((ItemLike)Items.f_42740_);
        }
        return ItemStack.f_41583_;
    }

    private static void applyArmorSlot(Mob mob, ItemStack ring, EquipmentSlot slot, String tagName) {
        CompoundTag tag = ring.m_41784_();
        if (!tag.m_128441_(tagName)) {
            return;
        }
        ItemStack saved = ItemStack.m_41712_((CompoundTag)tag.m_128469_(tagName));
        if (saved.m_41619_()) {
            return;
        }
        mob.m_8061_(slot, saved.m_41777_());
        mob.m_21409_(slot, 0.0f);
    }

    private static boolean applyWeaponSlotUnique(Mob mob, ItemStack ring, EquipmentSlot slot, boolean savedMainAlreadyUsed) {
        ItemStack current = mob.m_6844_(slot);
        SavedWeaponMatch match = ForgeRingEquipmentHelper.findCompatibleSavedWeaponUnique(ring, current, savedMainAlreadyUsed);
        if (match.stack().m_41619_()) {
            return false;
        }
        mob.m_8061_(slot, match.stack().m_41777_());
        mob.m_21409_(slot, 0.0f);
        return match.usedMain();
    }

    private static SavedWeaponMatch findCompatibleSavedWeaponUnique(ItemStack ring, ItemStack current, boolean savedMainAlreadyUsed) {
        CompoundTag tag = ring.m_41784_();
        List<ItemStack> weapons = ForgeRingEquipmentHelper.getSavedWeapons(ring);
        ItemStack off = ItemStack.f_41583_;
        if (tag.m_128441_(OFFHAND)) {
            off = ItemStack.m_41712_((CompoundTag)tag.m_128469_(OFFHAND));
        }
        if (!current.m_41619_()) {
            if (!savedMainAlreadyUsed) {
                for (ItemStack weapon : weapons) {
                    if (weapon.m_41619_() || !ForgeRingEquipmentHelper.isSameWeaponType(current, weapon)) continue;
                    return new SavedWeaponMatch(weapon, true);
                }
            }
            if (!off.m_41619_() && ForgeRingEquipmentHelper.isSameWeaponType(current, off)) {
                return new SavedWeaponMatch(off, false);
            }
            return new SavedWeaponMatch(ItemStack.f_41583_, false);
        }
        if (!savedMainAlreadyUsed && !weapons.isEmpty()) {
            return new SavedWeaponMatch(weapons.get(0), true);
        }
        if (!off.m_41619_()) {
            return new SavedWeaponMatch(off, false);
        }
        return new SavedWeaponMatch(ItemStack.f_41583_, false);
    }

    private static boolean isSameWeaponType(ItemStack current, ItemStack saved) {
        if (current.m_41619_()) {
            return true;
        }
        if (ForgeRingEquipmentHelper.isBow(current) && ForgeRingEquipmentHelper.isBow(saved)) {
            return true;
        }
        if (ForgeRingEquipmentHelper.isCrossbow(current) && ForgeRingEquipmentHelper.isCrossbow(saved)) {
            return true;
        }
        if (ForgeRingEquipmentHelper.isSword(current) && ForgeRingEquipmentHelper.isSword(saved)) {
            return true;
        }
        if (ForgeRingEquipmentHelper.isAxe(current) && ForgeRingEquipmentHelper.isAxe(saved)) {
            return true;
        }
        if (ForgeRingEquipmentHelper.isTrident(current) && ForgeRingEquipmentHelper.isTrident(saved)) {
            return true;
        }
        return ForgeRingEquipmentHelper.isShield(current) && ForgeRingEquipmentHelper.isShield(saved);
    }

    private static boolean isBow(ItemStack stack) {
        return stack.m_41720_() instanceof BowItem || stack.m_204117_(ItemTags.create((ResourceLocation)new ResourceLocation("forge", "tools/bows")));
    }

    private static boolean isCrossbow(ItemStack stack) {
        return stack.m_41720_() instanceof CrossbowItem || stack.m_204117_(ItemTags.create((ResourceLocation)new ResourceLocation("forge", "tools/crossbows")));
    }

    private static boolean isSword(ItemStack stack) {
        return stack.m_41720_() instanceof SwordItem || stack.m_204117_(ItemTags.f_271388_);
    }

    private static boolean isAxe(ItemStack stack) {
        return stack.m_41720_() instanceof AxeItem || stack.m_204117_(ItemTags.f_271207_);
    }

    private static boolean isTrident(ItemStack stack) {
        return stack.m_41720_() instanceof TridentItem || stack.m_204117_(ItemTags.create((ResourceLocation)new ResourceLocation("forge", "tools/tridents")));
    }

    private static boolean isShield(ItemStack stack) {
        return stack.m_41720_() instanceof ShieldItem;
    }

    private static void saveStack(CompoundTag ringData, String key, ItemStack stack) {
        if (stack.m_41619_()) {
            ringData.m_128473_(key);
            return;
        }
        CompoundTag stackTag = new CompoundTag();
        stack.m_41777_().m_41739_(stackTag);
        ringData.m_128365_(key, (Tag)stackTag);
    }

    public static boolean isValidForgeRingEquipment(ItemStack stack) {
        return !stack.m_41619_() && !ImprovedForgingRingBlacklist.matches(stack);
    }

    public static String getKeyForStack(ItemStack stack) {
        Item item = stack.m_41720_();
        if (item instanceof ArmorItem) {
            ArmorItem armorItem = (ArmorItem)item;
            EquipmentSlot slot = armorItem.m_40402_();
            if (slot == EquipmentSlot.HEAD) {
                return HELMET;
            }
            if (slot == EquipmentSlot.CHEST) {
                return CHESTPLATE;
            }
            if (slot == EquipmentSlot.LEGS) {
                return LEGGINGS;
            }
            if (slot == EquipmentSlot.FEET) {
                return BOOTS;
            }
        }
        if (stack.getEquipmentSlot() == EquipmentSlot.HEAD) {
            return HELMET;
        }
        if (stack.getEquipmentSlot() == EquipmentSlot.CHEST) {
            return CHESTPLATE;
        }
        if (stack.getEquipmentSlot() == EquipmentSlot.LEGS) {
            return LEGGINGS;
        }
        if (stack.getEquipmentSlot() == EquipmentSlot.FEET) {
            return BOOTS;
        }
        if (stack.m_41720_() instanceof ShieldItem) {
            return OFFHAND;
        }
        return MAINHAND;
    }

    private static class SavedWeaponMatch {
        private final ItemStack stack;
        private final boolean usedMain;

        private SavedWeaponMatch(ItemStack stack, boolean usedMain) {
            this.stack = stack;
            this.usedMain = usedMain;
        }

        public ItemStack stack() {
            return this.stack;
        }

        public boolean usedMain() {
            return this.usedMain;
        }
    }
}

