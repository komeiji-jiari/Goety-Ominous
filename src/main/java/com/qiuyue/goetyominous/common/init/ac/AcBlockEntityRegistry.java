package com.qiuyue.goetyominous.common.init.ac;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.entities.ac.GrottoceratopsServantEggBlockEntity;
import com.qiuyue.goetyominous.common.blocks.entities.ac.TremorsaurusServantEggBlockEntity;
import com.qiuyue.goetyominous.common.blocks.entities.ac.TremorzillaServantEggBlockEntity;
import com.qiuyue.goetyominous.common.blocks.entities.ac.VallumraptorServantEggBlockEntity;
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

    public static final RegistryObject<BlockEntityType<TremorsaurusServantEggBlockEntity>> TREMORSAURUS_SERVANT_EGG =
            AC_BLOCK_ENTITIES.register("tremorsaurus_servant_egg",
                    () -> BlockEntityType.Builder.of(
                            TremorsaurusServantEggBlockEntity::new,
                            AcBlockRegistry.TREMORSAURUS_SERVANT_EGG.get()).build(null));

    public static final RegistryObject<BlockEntityType<VallumraptorServantEggBlockEntity>> VALLUMRAPTOR_SERVANT_EGG =
            AC_BLOCK_ENTITIES.register("vallumraptor_servant_egg",
                    () -> BlockEntityType.Builder.of(
                            VallumraptorServantEggBlockEntity::new,
                            AcBlockRegistry.VALLUMRAPTOR_SERVANT_EGG.get()).build(null));

    public static final RegistryObject<BlockEntityType<TremorzillaServantEggBlockEntity>> TREMORZILLA_SERVANT_EGG =
            AC_BLOCK_ENTITIES.register("tremorzilla_servant_egg",
                    () -> BlockEntityType.Builder.of(
                            TremorzillaServantEggBlockEntity::new,
                            AcBlockRegistry.TREMORZILLA_SERVANT_EGG.get()).build(null));

    public static void register(IEventBus modEventBus) {
        AC_BLOCK_ENTITIES.register(modEventBus);
    }
}
