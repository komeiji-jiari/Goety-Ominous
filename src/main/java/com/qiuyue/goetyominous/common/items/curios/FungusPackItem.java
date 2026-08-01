package com.qiuyue.goetyominous.common.items.curios;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.FungusPackModel;
import com.qiuyue.goetyominous.common.init.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

public class FungusPackItem extends ArmorItem implements ICurioItem {

    private static final ArmorMaterial MATERIAL = new ArmorMaterial() {
        @Override public String getName() { return GoetyOminous.MOD_ID + ":fungus_pack"; }
        @Override public int getDurabilityForType(Type type) { return 0; }
        @Override public int getDefenseForType(Type type) { return 2; }
        @Override public int getEnchantmentValue() { return 9; }
        @Override public SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_LEATHER; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(net.minecraft.world.item.Items.BROWN_MUSHROOM); }
        @Override public float getToughness() { return 0.0F; }
        @Override public float getKnockbackResistance() { return 0.0F; }
    };

    public FungusPackItem() {
        super(MATERIAL, Type.CHESTPLATE, new Properties());
    }

    public boolean isDamageable() {
        return false;
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(MobEffects.CONFUSION)) {
            ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
            if (chest.is(ModTags.FUNGUS_PACKS)) {
                entity.removeEffect(MobEffects.CONFUSION);
                return;
            }

            top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(entity)
                    .map(inv -> inv.findFirstCurio(s -> s.is(ModTags.FUNGUS_PACKS)))
                    .orElse(java.util.Optional.empty())
                    .ifPresent(slotResult -> entity.removeEffect(MobEffects.CONFUSION));
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.equals(newStack) && slotChanged;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private HumanoidModel<?> armorModel;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                if (armorModel == null) {
                    FungusPackModel<?> packModel = new FungusPackModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(ModEntityLayers.FUNGUS_PACK_LAYER));
                    var empty = new ModelPart(Collections.emptyList(), Collections.emptyMap());
                    armorModel = new HumanoidModel<>(new ModelPart(Collections.emptyList(), Map.of(
                            "body", packModel.Pack,
                            "left_arm", packModel.left_arm,
                            "right_arm", packModel.right_arm,
                            "head", empty,
                            "hat", empty,
                            "right_leg", empty,
                            "left_leg", empty
                    )));
                }
                armorModel.young = original.young;
                armorModel.crouching = original.crouching;
                armorModel.riding = original.riding;
                armorModel.rightArmPose = original.rightArmPose;
                armorModel.leftArmPose = original.leftArmPose;
                return armorModel;
            }
        });
    }

    @Override
    public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, net.minecraft.nbt.CompoundTag nbt) {
        return new com.qiuyue.goetyominous.common.items.capability.FungusPackItemCapability(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return GoetyOminous.MOD_ID + ":textures/models/armor/fungus_pack.png";
    }

    @Override
    public net.minecraft.nbt.CompoundTag getShareTag(net.minecraft.world.item.ItemStack stack) {
        net.minecraftforge.items.IItemHandler handler = com.qiuyue.goetyominous.common.items.handler.FungusPackItemHandler.get(stack);
        net.minecraft.nbt.CompoundTag tag = stack.getTag() != null ? stack.getTag() : new net.minecraft.nbt.CompoundTag();
        if (handler instanceof net.minecraftforge.items.ItemStackHandler h) {
            tag.put("cap", h.serializeNBT());
        }
        return tag;
    }

    @Override
    public void readShareTag(net.minecraft.world.item.ItemStack stack, net.minecraft.nbt.CompoundTag nbt) {
        net.minecraftforge.items.IItemHandler handler = com.qiuyue.goetyominous.common.items.handler.FungusPackItemHandler.get(stack);
        if (handler instanceof net.minecraftforge.items.ItemStackHandler h && nbt != null) {
            h.deserializeNBT(nbt.getCompound("cap"));
        }
        stack.setTag(nbt);
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }
}
