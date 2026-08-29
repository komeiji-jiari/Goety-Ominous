package com.qiuyue.goetyominous.utils;

import com.qiuyue.goetyominous.common.items.curios.CroneRobeItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

public class CroneCuriosUtil {

    public static boolean hasCroneRobe(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .map(inv -> inv.findFirstCurio(s -> s.getItem() instanceof CroneRobeItem).isPresent())
                .orElse(false);
    }

    public static boolean hasCroneHat(LivingEntity entity) {
        ItemStack hat = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("goety:crone_hat")));
        if (hat.isEmpty()) return false;
        return CuriosApi.getCuriosInventory(entity)
                .map(inv -> inv.findFirstCurio(s -> s.is(hat.getItem())).isPresent())
                .orElse(false);
    }

    public static boolean hasCroneSet(LivingEntity entity) {
        return hasCroneRobe(entity) && hasCroneHat(entity);
    }
}
