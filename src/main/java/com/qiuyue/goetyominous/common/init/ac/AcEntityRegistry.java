package com.qiuyue.goetyominous.common.init.ac;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.ac.BrainiacServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.GrottoceratopsServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.HullbreakerServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.MineGuardianServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.NucleeperServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorsaurusServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.VallumraptorServant;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AcEntityRegistry {

    private static final DeferredRegister<EntityType<?>> AC_ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<EntityType<GrottoceratopsServant>> GROTTOCERATOPS_SERVANT =
            AC_ENTITIES.register("grottoceratops_servant",
                    () -> EntityType.Builder.<GrottoceratopsServant>of((type, worldIn) -> new GrottoceratopsServant(type, worldIn), MobCategory.MISC)
                            .sized(2.3F, 2.5F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":grottoceratops_servant"));

    public static final RegistryObject<EntityType<TremorsaurusServant>> TREMORSAURUS_SERVANT =
            AC_ENTITIES.register("tremorsaurus_servant",
                    () -> EntityType.Builder.<TremorsaurusServant>of((type, worldIn) -> new TremorsaurusServant(type, worldIn), MobCategory.MISC)
                            .sized(2.5F, 3.85F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":tremorsaurus_servant"));

    public static final RegistryObject<EntityType<VallumraptorServant>> VALLUMRAPTOR_SERVANT =
            AC_ENTITIES.register("vallumraptor_servant",
                    () -> EntityType.Builder.<VallumraptorServant>of((type, worldIn) -> new VallumraptorServant(type, worldIn), MobCategory.MISC)
                            .sized(0.8F, 1.5F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":vallumraptor_servant"));

    public static final RegistryObject<EntityType<NucleeperServant>> NUCLEEPER_SERVANT =
            AC_ENTITIES.register("nucleeper_servant",
                    () -> EntityType.Builder.<NucleeperServant>of((type, worldIn) -> new NucleeperServant(type, worldIn), MobCategory.MISC)
                            .sized(0.98F, 3.95F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":nucleeper_servant"));

    public static final RegistryObject<EntityType<BrainiacServant>> BRAINIAC_SERVANT =
            AC_ENTITIES.register("brainiac_servant",
                    () -> EntityType.Builder.<BrainiacServant>of((type, worldIn) -> new BrainiacServant(type, worldIn), MobCategory.MISC)
                            .sized(1.1F, 2.5F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":brainiac_servant"));

    public static final RegistryObject<EntityType<MineGuardianServant>> MINE_GUARDIAN_SERVANT =
            AC_ENTITIES.register("mine_guardian_servant",
                    () -> EntityType.Builder.<MineGuardianServant>of((type, worldIn) -> new MineGuardianServant(type, worldIn), MobCategory.MISC)
                            .sized(1.3F, 1.3F)
                            .setTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .setUpdateInterval(1)
                            .build(GoetyOminous.MOD_ID + ":mine_guardian_servant"));

    public static final RegistryObject<EntityType<HullbreakerServant>> HULLBREAKER_SERVANT =
            AC_ENTITIES.register("hullbreaker_servant",
                    () -> EntityType.Builder.<HullbreakerServant>of((type, worldIn) -> new HullbreakerServant(type, worldIn), MobCategory.MISC)
                            .sized(4.65F, 4.5F)
                            .setTrackingRange(20)
                            .build(GoetyOminous.MOD_ID + ":hullbreaker_servant"));

    public static void register(IEventBus modEventBus) {
        AC_ENTITIES.register(modEventBus);
    }
}
