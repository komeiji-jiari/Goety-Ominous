package com.qiuyue.goetyominous.common.items.curios;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class ScreamingSkullJar extends Item implements ICurioItem {

    private static final int MAX_SKULLS = 8;
    private static final String SKULL_COUNT_TAG = "SkullCount";
    private static final int AUTO_FEED_INTERVAL = 20;
    private static final TagKey<Item> TALL_SKULL_TAG = ItemTags.create(new ResourceLocation("goety", "skulls"));

    public ScreamingSkullJar() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        tooltip.add(Component.translatable("info.goetyominous.screaming_skull_jar").withStyle(ChatFormatting.DARK_PURPLE));

        tooltip.add(Component.translatable("info.goetyominous.screaming_skull_jar.effect").withStyle(ChatFormatting.BLUE));

        int skullCount = getSkullCount(stack);
        tooltip.add(Component.translatable("info.goetyominous.screaming_skull_jar.skulls", skullCount, MAX_SKULLS).withStyle(ChatFormatting.AQUA));
    }

    public static int getSkullCount(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return 0;
        }
        return tag.getInt(SKULL_COUNT_TAG);
    }

    public static void setSkullCount(ItemStack stack, int count) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(SKULL_COUNT_TAG, Math.max(0, Math.min(count, MAX_SKULLS)));
    }

    public static boolean consumeSkull(ItemStack stack) {
        int currentCount = getSkullCount(stack);
        if (currentCount > 0) {
            setSkullCount(stack, currentCount - 1);
            return true;
        }
        return false;
    }

    public static boolean hasSkulls(ItemStack stack) {
        return getSkullCount(stack) > 0;
    }

    public static boolean addSkull(ItemStack stack) {
        int currentCount = getSkullCount(stack);
        if (currentCount < MAX_SKULLS) {
            setSkullCount(stack, currentCount + 1);
            return true;
        }
        return false;
    }

    public static boolean isTallSkull(ItemStack stack) {
        return stack.is(TALL_SKULL_TAG);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity wearer = slotContext.entity();
        if (wearer.level().isClientSide || !(wearer instanceof Player player)) {
            return;
        }

        int currentSkullCount = getSkullCount(stack);

        if (currentSkullCount < MAX_SKULLS && player.tickCount % AUTO_FEED_INTERVAL == 0) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack invStack = player.getInventory().getItem(i);

                if (isTallSkull(invStack)) {
                    addSkull(stack);
                    invStack.shrink(1);

                    if (player.level() instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(null, player.blockPosition(), SoundEvents.BONE_BLOCK_PLACE, SoundSource.PLAYERS, 1.0F, 1.0F);

                        for (int j = 0; j < 10; j++) {
                            double xOffset = (serverLevel.random.nextDouble() - 0.5) * 0.5;
                            double yOffset = serverLevel.random.nextDouble() * 0.5;
                            double zOffset = (serverLevel.random.nextDouble() - 0.5) * 0.5;
                            serverLevel.sendParticles(ParticleTypes.SOUL, player.getX() + xOffset, player.getY() + yOffset, player.getZ() + zOffset, 1, 0, 0, 0, 0.02);
                        }
                    }

                    break;
                }
            }
        }
    }
}