package com.qiuyue.goetyominous.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class CreateBlazeBurnerCompat {
    private static final String BE_CLASS = "com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity";
    private static final String HEAT_CLASS = "com.simibubi.create.content.processing.burner.BlazeBurnerBlock$HeatLevel";
    private static final ResourceLocation BLAZE_BURNER_ID = new ResourceLocation("create", "blaze_burner");

    @Nullable
    private static Block blazeBurnerBlock;
    private static boolean checked;
    private static boolean usable;
    @Nullable
    private static Class<?> beClass;
    @Nullable
    private static Object fuelNormal;
    @Nullable
    private static Object seethingHeat;
    @Nullable
    private static Field remainingBurnTimeField;
    @Nullable
    private static Field activeFuelField;
    @Nullable
    private static Method getHeatLevelFromBlockMethod;
    @Nullable
    private static Method updateBlockStateMethod;

    private static boolean ensureUsable() {
        if (!checked) {
            checked = true;
            if (ModList.get().isLoaded("create")) {
                try {
                    beClass = Class.forName(BE_CLASS);
                    Class<?> fuelClass = Class.forName(BE_CLASS + "$FuelType");
                    Class<?> heatClass = Class.forName(HEAT_CLASS);
                    fuelNormal = Enum.valueOf(fuelClass.asSubclass(Enum.class), "NORMAL");
                    seethingHeat = Enum.valueOf(heatClass.asSubclass(Enum.class), "SEETHING");
                    remainingBurnTimeField = beClass.getDeclaredField("remainingBurnTime");
                    remainingBurnTimeField.setAccessible(true);
                    activeFuelField = beClass.getDeclaredField("activeFuel");
                    activeFuelField.setAccessible(true);
                    getHeatLevelFromBlockMethod = beClass.getDeclaredMethod("getHeatLevelFromBlock");
                    updateBlockStateMethod = beClass.getDeclaredMethod("updateBlockState");
                    usable = true;
                } catch (Throwable ignored) {
                    beClass = null;
                }
            }
        }
        return usable;
    }

    public static boolean isBlazeBurner(Block block) {
        if (blazeBurnerBlock == null) {
            blazeBurnerBlock = ForgeRegistries.BLOCKS.getValue(BLAZE_BURNER_ID);
            if (blazeBurnerBlock == null) {
                return false;
            }
        }
        return block == blazeBurnerBlock;
    }

    public static boolean sustainBlazeBurner(ServerLevel level, BlockPos pos) {
        if (!ensureUsable()) {
            return false;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null || !beClass.isInstance(be)) {
            return false;
        }
        try {
            Object heat = getHeatLevelFromBlockMethod.invoke(be);
            if (heat == seethingHeat) {
                return true;
            }
            Field creativeField = beClass.getField("isCreative");
            if ((Boolean) creativeField.get(be)) {
                return true;
            }
            remainingBurnTimeField.setInt(be, 10000);
            activeFuelField.set(be, fuelNormal);
            updateBlockStateMethod.invoke(be);
            return true;
        } catch (Throwable ignored) {
            usable = false;
            return false;
        }
    }
}
