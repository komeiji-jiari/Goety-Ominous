package com.qiuyue.goetyominous.common.init.ac;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.ac.BrainiacServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.DeepOneKnightServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.DeepOneMageServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.DeepOneServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.GammaroachServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.GrottoceratopsServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.HullbreakerServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.MineGuardianServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.NucleeperServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorsaurusServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorzillaServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.VallumraptorServant;
import com.qiuyue.goetyominous.common.entities.projectile.DeepOneMageServantWaterBolt;
import com.qiuyue.goetyominous.common.entities.projectile.DeepOneMageServantWave;
import com.qiuyue.goetyominous.common.entities.projectile.DeepOneServantWave;
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

    public static final RegistryObject<EntityType<TremorzillaServant>> TREMORZILLA_SERVANT =
            AC_ENTITIES.register("tremorzilla_servant",
                    () -> EntityType.Builder.<TremorzillaServant>of((type, worldIn) -> new TremorzillaServant(type, worldIn), MobCategory.MISC)
                            .sized(4.5F, 11.0F)
                            .setTrackingRange(11)
                            .fireImmune()
                            .build(GoetyOminous.MOD_ID + ":tremorzilla_servant"));

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

    public static final RegistryObject<EntityType<GammaroachServant>> GAMMAROACH_SERVANT =
            AC_ENTITIES.register("gammaroach_servant",
                    () -> EntityType.Builder.<GammaroachServant>of((type, worldIn) -> new GammaroachServant(type, worldIn), MobCategory.MISC)
                            .sized(1.25F, 0.9F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":gammaroach_servant"));

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

    public static final RegistryObject<EntityType<DeepOneServant>> DEEP_ONE_SERVANT =
            AC_ENTITIES.register("deep_one_servant",
                    () -> EntityType.Builder.<DeepOneServant>of((type, worldIn) -> new DeepOneServant(type, worldIn), MobCategory.MISC)
                            .sized(0.9F, 2.2F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":deep_one_servant"));

    public static final RegistryObject<EntityType<DeepOneKnightServant>> DEEP_ONE_KNIGHT_SERVANT =
            AC_ENTITIES.register("deep_one_knight_servant",
                    () -> EntityType.Builder.<DeepOneKnightServant>of((type, worldIn) -> new DeepOneKnightServant(type, worldIn), MobCategory.MISC)
                            .sized(1.2F, 2.4F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":deep_one_knight_servant"));

    public static final RegistryObject<EntityType<DeepOneMageServant>> DEEP_ONE_MAGE_SERVANT =
            AC_ENTITIES.register("deep_one_mage_servant",
                    () -> EntityType.Builder.<DeepOneMageServant>of((type, worldIn) -> new DeepOneMageServant(type, worldIn), MobCategory.MISC)
                            .sized(1.2F, 2.4F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":deep_one_mage_servant"));

    public static final RegistryObject<EntityType<DeepOneServantWave>> DEEP_ONE_SERVANT_WAVE =
            AC_ENTITIES.register("deep_one_servant_wave",
                    () -> EntityType.Builder.<DeepOneServantWave>of((type, worldIn) -> new DeepOneServantWave(type, worldIn), MobCategory.MISC)
                            .sized(0.8F, 0.9F)
                            .setTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .setUpdateInterval(1)
                            .build(GoetyOminous.MOD_ID + ":deep_one_servant_wave"));

    public static final RegistryObject<EntityType<DeepOneMageServantWave>> DEEP_ONE_MAGE_SERVANT_WAVE =
            AC_ENTITIES.register("deep_one_mage_servant_wave",
                    () -> EntityType.Builder.<DeepOneMageServantWave>of((type, worldIn) -> new DeepOneMageServantWave(type, worldIn), MobCategory.MISC)
                            .sized(0.9F, 0.9F)
                            .setTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .setUpdateInterval(1)
                            .build(GoetyOminous.MOD_ID + ":deep_one_mage_servant_wave"));

    public static final RegistryObject<EntityType<DeepOneMageServantWaterBolt>> DEEP_ONE_MAGE_SERVANT_WATER_BOLT =
            AC_ENTITIES.register("deep_one_mage_servant_water_bolt",
                    () -> EntityType.Builder.<DeepOneMageServantWaterBolt>of((type, worldIn) -> new DeepOneMageServantWaterBolt(type, worldIn), MobCategory.MISC)
                            .sized(0.6F, 0.6F)
                            .setTrackingRange(8)
                            .setShouldReceiveVelocityUpdates(true)
                            .setUpdateInterval(1)
                            .build(GoetyOminous.MOD_ID + ":deep_one_mage_servant_water_bolt"));

    public static void register(IEventBus modEventBus) {
        AC_ENTITIES.register(modEventBus);
    }
}
