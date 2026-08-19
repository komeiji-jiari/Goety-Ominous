package com.qiuyue.goetyominous.common.items.am;

import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class JerboaAmuletItem extends Item implements ICurioItem {

    public static final String SEED_TAG = "Stored Seeds";
    public static final int MAX_SEEDS = 256;
    public static final int BUFF_DURATION = 1200;
    public static final int REFRESH_THRESHOLD = 100;

    private static final TagKey<Item> SEED_TAG_KEY = ItemTags.create(new ResourceLocation("forge", "seeds"));

    public JerboaAmuletItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player player)) return;
        if (!isEquippedStack(player, stack)) return;

        if (!stack.hasTag()) {
            stack.getOrCreateTag().putInt(SEED_TAG, 0);
        }

        if (getSeedCount(stack) < MAX_SEEDS) {
            int slot = findSeedSlot(player);
            if (slot >= 0) {
                setSeedCount(stack, getSeedCount(stack) + 1);
                player.getInventory().removeItem(slot, 1);
            }
        }

        if (getSeedCount(stack) <= 0) return;

        MobEffectInstance current = player.getEffect(AMEffectRegistry.FLEET_FOOTED.get());
        boolean needBuff = current == null
                || (current.getAmplifier() == 0 && current.getDuration() <= REFRESH_THRESHOLD);
        if (!needBuff) return;

        if (player.addEffect(new MobEffectInstance(
                AMEffectRegistry.FLEET_FOOTED.get(), BUFF_DURATION, 0, false, false, true))) {
            setSeedCount(stack, getSeedCount(stack) - 1);
            player.playSound(SoundEvents.BONE_MEAL_USE, 1.0F, 1.0F);
        }
    }

    private static int findSeedSlot(Player player) {
        var inv = player.getInventory();
        for (int i = 0; i < inv.items.size(); i++) {
            ItemStack s = inv.items.get(i);
            if (!s.isEmpty() && s.is(SEED_TAG_KEY)) return i;
        }
        return -1;
    }

    private static boolean isEquippedStack(Player player, ItemStack stack) {
        return top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player)
                .map(inv -> inv.findFirstCurio(AmItems.JERBOA_AMULET.get()))
                .orElse(Optional.empty())
                .map(slotResult -> slotResult.stack() == stack)
                .orElse(false);
    }

    private static int getSeedCount(ItemStack stack) {
        return stack.getTag() != null ? stack.getTag().getInt(SEED_TAG) : 0;
    }

    private static void setSeedCount(ItemStack stack, int count) {
        stack.getOrCreateTag().putInt(SEED_TAG, count);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.hasTag();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(getSeedCount(stack) * 13F / MAX_SEEDS);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb(Math.max(0.0F, getSeedCount(stack) / (float) MAX_SEEDS) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("info.goetyominous.jerboa_amulet").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("info.goetyominous.jerboa_amulet.seeds",
                getSeedCount(stack), MAX_SEEDS).withStyle(ChatFormatting.GREEN));
    }
}
