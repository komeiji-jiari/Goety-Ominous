package com.qiuyue.goetyominous.common.init.of;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.projectile.DicerServantLaser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class OfDamageTypes {

    public static final ResourceKey<DamageType> SERVANT_LASER =
            ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(GoetyOminous.MOD_ID, "servant_laser"));

    public static DamageSource laser(Level level, DicerServantLaser laser, @Nullable Entity indirectEntity) {
        return new DamageSource(
                level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SERVANT_LASER),
                laser, indirectEntity);
    }
}
