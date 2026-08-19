package com.qiuyue.goetyominous.common.init.ac;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.entities.ac.GrottoceratopsServantEggBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class AcBlockEntityRegistry {

    private static final DeferredRegister<BlockEntityType<?>> AC_BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<BlockEntityType<GrottoceratopsServantEggBlockEntity>> GROTTOCERATOPS_SERVANT_EGG =
            AC_BLOCK_ENTITIES.register("grottoceratops_servant_egg",
                    () -> BlockEntityType.Builder.of(
                            GrottoceratopsServantEggBlockEntity::new,
                            AcBlockRegistry.GROTTOCERATOPS_SERVANT_EGG.get()).build(null));

    public static void register(IEventBus modEventBus) {
        AC_BLOCK_ENTITIES.register(modEventBus);
    }
}
