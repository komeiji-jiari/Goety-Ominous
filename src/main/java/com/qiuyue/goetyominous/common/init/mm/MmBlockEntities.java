package com.qiuyue.goetyominous.common.init.mm;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.entities.mm.ServantQuicksandBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MmBlockEntities {

    private static final DeferredRegister<BlockEntityType<?>> MM_BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<BlockEntityType<ServantQuicksandBlockEntity>> SERVANT_QUICKSAND =
            MM_BLOCK_ENTITIES.register("servant_quicksand",
                    () -> BlockEntityType.Builder.of(
                            ServantQuicksandBlockEntity::new,
                            MmBlocks.SERVANT_QUICKSAND.get(),
                            MmBlocks.SERVANT_RED_QUICKSAND.get()).build(null));

    public static void register(IEventBus modEventBus) {
        MM_BLOCK_ENTITIES.register(modEventBus);
    }
}
