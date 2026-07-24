package com.qiuyue.someillagerservants.common.init;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.common.blocks.PiglinMerchantSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SomeIllagerServants.MOD_ID);

    public static final RegistryObject<BlockEntityType<PiglinMerchantSpawnerBlockEntity>> PIGLIN_MERCHANT_SPAWNER =
            BLOCK_ENTITIES.register("piglin_merchant_spawner",
                    () -> BlockEntityType.Builder.of(
                            PiglinMerchantSpawnerBlockEntity::new,
                            ModBlocks.PIGLIN_MERCHANT_SPAWNER.get()).build(null));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}