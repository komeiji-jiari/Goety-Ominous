package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.magic.spells.mm.WitherBreathSpell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSpellControllers {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<EntityType<WitherBreathSpell.WitherBreathController>> WITHER_BREATH_CONTROLLER =
            ENTITY_TYPES.register("wither_breath_controller",
                    () -> EntityType.Builder.of(
                                    (EntityType<WitherBreathSpell.WitherBreathController> type, Level level)
                                            -> new WitherBreathSpell.WitherBreathController(type, level),
                                    MobCategory.MISC)
                            .sized(0.1F, 0.1F)
                            .clientTrackingRange(0)
                            .updateInterval(Integer.MAX_VALUE)
                            .build(new ResourceLocation(GoetyOminous.MOD_ID, "wither_breath_controller").toString()));
}