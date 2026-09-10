package com.qiuyue.goetyominous.common.items;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.items.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class BatWingItem extends Item {

    private static final FoodProperties BAT_WING_FOOD = new FoodProperties.Builder()
            .nutrition(1)
            .saturationMod(0.5f)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 100, 0), 1.0f)
            .effect(() -> new MobEffectInstance(GoetyEffects.LEECHING.get(), 200, 0), 0.35f)
            .effect(() -> new MobEffectInstance(GoetyEffects.ACID_VENOM.get(), 1200, 4), 0.01f)
            .build();

    public BatWingItem(Item.@NotNull Properties properties) {
        super(properties
                .stacksTo(64)
                .food(BAT_WING_FOOD)
        );
    }

    static {
        MinecraftForge.EVENT_BUS.register(BatWingItem.class);
    }

    @SubscribeEvent
    public static void onBatKilledByBoline(LivingDropsEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof Bat bat)) return;

        Entity killer = event.getSource().getDirectEntity();
        if (!(killer instanceof Player player)) return;

        boolean boline = player.getMainHandItem().is(ModItems.WICKED_BOLINE.get())
                || player.getOffhandItem().is(ModItems.WICKED_BOLINE.get());
        if (!boline) return;

        if (bat.level().random.nextFloat() < 0.5F) {
            int count = 1 + bat.level().random.nextInt(2);
            event.getDrops().add(new ItemEntity(bat.level(),
                    bat.getX(), bat.getY(), bat.getZ(),
                    new ItemStack(com.qiuyue.goetyominous.common.items.ModItems.BAT_WING.get(), count)));
        }
    }
}
