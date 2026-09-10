package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class WormyAppleItem extends Item {

    private static final FoodProperties WORMY_APPLE_FOOD = new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 100, 0), 100.0f)
            .build();

    private static final float APPLE_CHANCE = 0.05F;

    public WormyAppleItem(@NotNull Properties properties) {
        super(properties
                .stacksTo(64)
                .food(WORMY_APPLE_FOOD)
        );
    }

    static {
        MinecraftForge.EVENT_BUS.register(WormyAppleItem.class);
    }

    @SubscribeEvent
    public static void onLeafBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;
        BlockState state = event.getState();
        if (!state.is(Blocks.OAK_LEAVES) && !state.is(Blocks.DARK_OAK_LEAVES)) return;
        Player player = event.getPlayer();
        if (player == null) return;

        boolean boline = player.getMainHandItem().is(ModItems.WICKED_BOLINE.get())
                || player.getOffhandItem().is(ModItems.WICKED_BOLINE.get());
        if (!boline) return;

        ItemStack tool = player.getMainHandItem();
        if (tool.is(Items.SHEARS)
                || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0) {
            return;
        }

        if (player.level().random.nextFloat() < APPLE_CHANCE) {
            BlockPos pos = event.getPos();
            player.level().addFreshEntity(new ItemEntity(player.level(),
                    pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    new ItemStack(com.qiuyue.goetyominous.common.items.ModItems.WORMY_APPLE.get())));
        }
    }
}