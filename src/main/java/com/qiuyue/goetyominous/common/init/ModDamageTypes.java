package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ModDamageTypes {

    public static final ResourceKey<DamageType> OMINOUS_SHOCKWAVE = ResourceKey.create(
            Registries.DAMAGE_TYPE, new ResourceLocation(GoetyOminous.MOD_ID, "ominous_shockwave"));

    public static DamageSource ominousShockwave(Level level, LivingEntity attacker) {
        return new DamageSource(
                level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(OMINOUS_SHOCKWAVE),
                attacker, attacker);
    }
}
