package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.api.items.IPersist;
import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.items.ModItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.qiuyue.goetyominous.config.WeaponConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class OminousCandleItem extends SwordItem implements ISoulRepair, IPersist {

    public static final String UNIQUE_KILLS = "UniqueKills";
    public static final int DAMAGE_STEP_CAP = 10;
    public static final int SPEED_STEP_CAP = 10;
    public static final Map<Player, Long> SHOCKWAVE_COOLDOWNS = new WeakHashMap<>();

    private static final Tier TIER = new Tier() {
        @Override
        public int getUses() {
            return 2048;
        }

        @Override
        public float getSpeed() {
            return 9.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0.0F;
        }

        @Override
        public int getLevel() {
            return 4;
        }

        @Override
        public int getEnchantmentValue() {
            return 22;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    };

    public OminousCandleItem() {
        super(TIER, 4, -3.0F, new Item.Properties().rarity(Rarity.EPIC));
    }

    public static int getUniqueKillCount(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(UNIQUE_KILLS, Tag.TAG_LIST)) {
            return 0;
        }
        return tag.getList(UNIQUE_KILLS, Tag.TAG_STRING).size();
    }

    public static boolean addUniqueKill(ItemStack stack, ResourceLocation type) {
        if (type == null) {
            return false;
        }
        CompoundTag tag = stack.getOrCreateTag();
        ListTag list = tag.contains(UNIQUE_KILLS, Tag.TAG_LIST)
                ? tag.getList(UNIQUE_KILLS, Tag.TAG_STRING)
                : new ListTag();
        if (list.size() >= getMaxUniqueKills()) {
            return false;
        }
        String id = type.toString();
        for (int i = 0; i < list.size(); i++) {
            if (list.getString(i).equals(id)) {
                return false;
            }
        }
        list.add(StringTag.valueOf(id));
        tag.put(UNIQUE_KILLS, list);
        return true;
    }

    public static int getMaxUniqueKills() {
        int perLevel = Math.max(1, WeaponConfig.OminousCandleKillsPerLevel.get());
        return perLevel * Math.max(DAMAGE_STEP_CAP, SPEED_STEP_CAP);
    }

    private Multimap<Attribute, AttributeModifier> buildModifiers(ItemStack stack) {
        int perLevel = Math.max(1, WeaponConfig.OminousCandleKillsPerLevel.get());
        int steps = getUniqueKillCount(stack) / perLevel;
        double baseDamage = WeaponConfig.OminousCandleBaseDamage.get();
        double perLevelDamage = WeaponConfig.OminousCandleDamagePerLevel.get();
        double damage = (baseDamage - 1.0D) + perLevelDamage * Math.min(steps, DAMAGE_STEP_CAP);
        double speed = -3.0D + 0.1D * Math.min(steps, SPEED_STEP_CAP);

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", damage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                BASE_ATTACK_SPEED_UUID, "Weapon modifier", speed, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot != EquipmentSlot.MAINHAND) {
            return super.getAttributeModifiers(slot, stack);
        }
        if (this.isBroken(stack)) {
            return ImmutableMultimap.of();
        }
        return this.buildModifiers(stack);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND
                ? this.buildModifiers(ItemStack.EMPTY)
                : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairMaterial) {
        return repairMaterial.is(ModItems.CURSED_METAL_INGOT.get());
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (stack.getDamageValue() + amount >= stack.getMaxDamage()) {
            if (stack.getDamageValue() != stack.getMaxDamage() - 1) {
                stack.setDamageValue(stack.getMaxDamage() - 1);
                onBroken.accept(entity);
            }
            return 0;
        }
        return amount;
    }

    @Override
    public boolean isBroken(ItemStack stack) {
        return IPersist.super.isBroken(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return this.isDamaged(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return this.isBroken(stack) ? 0x800000 : super.getBarColor(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return this.isBroken(stack) ? 13 : super.getBarWidth(stack);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return !this.isNotBroken(stack) ? 1.0F : super.getDestroySpeed(stack, state);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.isBroken(stack)) {
            return InteractionResultHolder.fail(stack);
        }
        if (hand == InteractionHand.MAIN_HAND) {
            ItemStack offhand = player.getOffhandItem();
            if (!offhand.isEmpty() && offhand.getUseAnimation() == UseAnim.BLOCK) {
                return InteractionResultHolder.pass(stack);
            }
            if (offhand.getItem() instanceof IWand wand
                    && IWand.getFocus(offhand) != null
                    && !IWand.getFocus(offhand).isEmpty()
                    && !wand.isOnCooldown(player, offhand)) {
                return InteractionResultHolder.pass(stack);
            }
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return !this.isBroken(stack) && ToolActions.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
    }
}
