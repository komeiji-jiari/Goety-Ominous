package com.qiuyue.goetyominous.compat.ias;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.illager.AbsorberServant;
import com.qiuyue.goetyominous.common.entities.ally.illager.MagispellerServant;
import com.qiuyue.goetyominous.common.entities.ally.illager.TwittollagerServant;
import com.qiuyue.goetyominous.common.entities.ally.mobs.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * IllageAndSpillage 联动实体注册类
 * 负责注册所有 I&S 仆从实体类型
 * 注意：这个类只在 IllageAndSpillage 模组加载时才会被调用
 */
public class IasEntityRegistry {

    /**
     * IllageAndSpillage 实体延迟注册表
     */
    private static final DeferredRegister<EntityType<?>> IAS_ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<EntityType<TwittollagerServant>> TWITTOLLAGER_SERVANT = IAS_ENTITIES
            .register(
                    "twittollager_servant",
                    () -> EntityType.Builder.of(TwittollagerServant::new, MobCategory.MISC)
                            .sized(0.6F, 1.8F)
                            .build(GoetyOminous.MOD_ID + ":twittollager_servant"));

    public static final RegistryObject<EntityType<AbsorberServant>> ABSORBER_SERVANT = IAS_ENTITIES.register(
            "absorber_servant",
            () -> EntityType.Builder.of(AbsorberServant::new, MobCategory.MISC)
                    .sized(1.3F, 2.9F)
                    .build(GoetyOminous.MOD_ID + ":absorber_servant"));

    public static final RegistryObject<EntityType<KaboomerServant>> KABOOMER_SERVANT = IAS_ENTITIES.register(
            "kaboomer_servant",
            () -> EntityType.Builder.of(KaboomerServant::new, MobCategory.MISC)
                    .sized(1.25F, 3.4F).fireImmune()
                    .build(GoetyOminous.MOD_ID + ":kaboomer_servant"));

    public static final RegistryObject<EntityType<CrashagerServant>> CRASHAGER_SERVANT = IAS_ENTITIES.register(
            "crashager_servant",
            () -> EntityType.Builder.of(CrashagerServant::new, MobCategory.MISC)
                    .sized(1.95F, 2.2F).fireImmune()
                    .build(GoetyOminous.MOD_ID + ":crashager_servant"));

    public static final RegistryObject<EntityType<IllashooterServant>> ILLASHOOTER_SERVANT = IAS_ENTITIES.register(
            "illashooter_servant",
            () -> EntityType.Builder.of(IllashooterServant::new, MobCategory.MISC)
                    .sized(0.5F, 1.0F)
                    .build(GoetyOminous.MOD_ID + ":illashooter_servant"));

    public static final RegistryObject<EntityType<DispenserServant>> DISPENSER_SERVANT = IAS_ENTITIES.register(
            "dispenser_servant",
            () -> EntityType.Builder.of(DispenserServant::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .build(GoetyOminous.MOD_ID + ":dispenser_servant"));

    public static final RegistryObject<EntityType<FakeMagispeller>> FAKEMAGISPELLER = IAS_ENTITIES.register(
            "fakemagispeller",
            () -> EntityType.Builder.of(FakeMagispeller::new, MobCategory.MISC)
                    .sized(0.6F, 2.3F)
                    .build(GoetyOminous.MOD_ID + ":fakemagispeller"));

    public static final RegistryObject<EntityType<MagiHeal>> MAGIHEAL = IAS_ENTITIES.register(
            "magiheal",
            () -> EntityType.Builder.of(MagiHeal::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(GoetyOminous.MOD_ID + ":magiheal"));

    public static final RegistryObject<EntityType<MagiArrow>> MAGIARROW = IAS_ENTITIES.register(
            "magiarrow",
            () -> EntityType.Builder.<MagiArrow>of(MagiArrow::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1)
                    .build(GoetyOminous.MOD_ID + ":magiarrow"));

    public static final RegistryObject<EntityType<MagiFireball>> MAGIFIREBALL = IAS_ENTITIES.register(
            "magifireball",
            () -> EntityType.Builder.<MagiFireball>of(MagiFireball::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1)
                    .build(GoetyOminous.MOD_ID + ":magifireball"));

    public static final RegistryObject<EntityType<MagispellerServant>> MAGISPELLER_SERVANT = IAS_ENTITIES.register(
            "magispeller_servant",
            () -> EntityType.Builder.of(MagispellerServant::new, MobCategory.MISC)
                    .sized(0.6F, 2.3F)
                    .build(GoetyOminous.MOD_ID + ":magispeller_servant"));

    /**
     * 注册 I&S 实体到模组事件总线
     * @param modEventBus 模组事件总线
     */
    public static void register(IEventBus modEventBus) {
        IAS_ENTITIES.register(modEventBus);
    }
}
