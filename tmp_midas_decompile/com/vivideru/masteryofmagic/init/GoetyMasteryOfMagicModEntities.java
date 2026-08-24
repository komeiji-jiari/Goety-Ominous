/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.neutral.AbstractCairnNecromancer
 *  com.Polarice3.Goety.common.entities.neutral.AbstractMossyNecromancer
 *  com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer
 *  com.Polarice3.Goety.common.entities.neutral.AbstractWitherNecromancer
 *  com.Polarice3.Goety.common.entities.neutral.DrownedNecromancer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EntityType$Builder
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraftforge.event.entity.EntityAttributeCreationEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package com.vivideru.masteryofmagic.init;

import com.Polarice3.Goety.common.entities.neutral.AbstractCairnNecromancer;
import com.Polarice3.Goety.common.entities.neutral.AbstractMossyNecromancer;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.common.entities.neutral.AbstractWitherNecromancer;
import com.Polarice3.Goety.common.entities.neutral.DrownedNecromancer;
import com.vivideru.masteryofmagic.FocusWildfireEntity;
import com.vivideru.masteryofmagic.SummonGhiaccio;
import com.vivideru.masteryofmagic.entity.GazerEntity;
import com.vivideru.masteryofmagic.entity.GhiaccioEntity;
import com.vivideru.masteryofmagic.entity.GoldenSwordProjectileEntity;
import com.vivideru.masteryofmagic.entity.IceMonarchEntity;
import com.vivideru.masteryofmagic.entity.MidasAlchemicalCircleEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherBeamEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherBoltEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherSphereEntity;
import com.vivideru.masteryofmagic.entity.PhilosopherWindSlashEntity;
import com.vivideru.masteryofmagic.entity.VampiratorServantEntity;
import com.vivideru.masteryofmagic.entity.necromancer.JarlessCairnNecromancerServant;
import com.vivideru.masteryofmagic.entity.necromancer.JarlessDrownedNecromancerServant;
import com.vivideru.masteryofmagic.entity.necromancer.JarlessMossyNecromancerServant;
import com.vivideru.masteryofmagic.entity.necromancer.JarlessNecromancerServant;
import com.vivideru.masteryofmagic.entity.necromancer.JarlessWitherNecromancerServant;
import com.vivideru.masteryofmagic.entity.necromancer.NamelessNecromancerServant;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class GoetyMasteryOfMagicModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ENTITY_TYPES, (String)"goety_mastery_of_magic");
    public static final RegistryObject<EntityType<VampiratorServantEntity>> VAMPIRATOR_SERVANT = GoetyMasteryOfMagicModEntities.register("vampirator_servant", EntityType.Builder.m_20704_(VampiratorServantEntity::new, (MobCategory)MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(VampiratorServantEntity::new).m_20699_(0.6f, 1.8f));
    public static final RegistryObject<EntityType<GazerEntity>> GAZER = GoetyMasteryOfMagicModEntities.register("gazer", EntityType.Builder.m_20704_(GazerEntity::new, (MobCategory)MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(GazerEntity::new).m_20699_(1.0f, 2.5f));
    public static final RegistryObject<EntityType<GhiaccioEntity>> GHIACCIO = GoetyMasteryOfMagicModEntities.register("ghiaccio", EntityType.Builder.m_20704_(GhiaccioEntity::new, (MobCategory)MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(GhiaccioEntity::new).m_20699_(0.6f, 1.8f));
    public static final RegistryObject<EntityType<IceMonarchEntity>> ICE_MONARCH = GoetyMasteryOfMagicModEntities.register("ice_monarch", EntityType.Builder.m_20704_(IceMonarchEntity::new, (MobCategory)MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(IceMonarchEntity::new).m_20699_(0.8f, 2.2f));
    public static final RegistryObject<EntityType<PhilosopherKingMidasEntity>> PHILOSOPHER_KING_MIDAS = null;
    public static final RegistryObject<EntityType<GoldenSwordProjectileEntity>> GOLDEN_SWORD_PROJECTILE = null;
    public static final RegistryObject<EntityType<PhilosopherBoltEntity>> PHILOSOPHER_BOLT = null;
    public static final RegistryObject<EntityType<PhilosopherBeamEntity>> PHILOSOPHER_BEAM = null;
    public static final RegistryObject<EntityType<PhilosopherSphereEntity>> PHILOSOPHER_SPHERE = null;
    public static final RegistryObject<EntityType<PhilosopherWindSlashEntity>> PHILOSOPHER_WIND_SLASH = null;
    public static final RegistryObject<EntityType<MidasAlchemicalCircleEntity>> MIDAS_ALCHEMICAL_CIRCLE = null;
    public static final RegistryObject<EntityType<FocusWildfireEntity>> FOCUS_WILDFIRE = GoetyMasteryOfMagicModEntities.register("focus_wildfire", EntityType.Builder.m_20704_(FocusWildfireEntity::new, (MobCategory)MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(FocusWildfireEntity::new).m_20699_(0.9f, 2.7f));
    public static final RegistryObject<EntityType<JarlessNecromancerServant>> JARLESS_NECROMANCER = GoetyMasteryOfMagicModEntities.register("jarless_necromancer_servant", EntityType.Builder.m_20704_(JarlessNecromancerServant::new, (MobCategory)MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).m_20699_(0.6f, 1.95f));
    public static final RegistryObject<EntityType<JarlessWitherNecromancerServant>> JARLESS_WITHER_NECROMANCER = GoetyMasteryOfMagicModEntities.register("jarless_wither_necromancer_servant", EntityType.Builder.m_20704_(JarlessWitherNecromancerServant::new, (MobCategory)MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).m_20699_(0.7f, 2.4f));
    public static final RegistryObject<EntityType<JarlessCairnNecromancerServant>> JARLESS_CAIRN_NECROMANCER = GoetyMasteryOfMagicModEntities.register("jarless_cairn_necromancer_servant", EntityType.Builder.m_20704_(JarlessCairnNecromancerServant::new, (MobCategory)MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).m_20699_(0.6f, 1.95f));
    public static final RegistryObject<EntityType<JarlessMossyNecromancerServant>> JARLESS_MOSSY_NECROMANCER = GoetyMasteryOfMagicModEntities.register("jarless_mossy_necromancer_servant", EntityType.Builder.m_20704_(JarlessMossyNecromancerServant::new, (MobCategory)MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).m_20699_(0.6f, 1.95f));
    public static final RegistryObject<EntityType<JarlessDrownedNecromancerServant>> JARLESS_DROWNED_NECROMANCER = GoetyMasteryOfMagicModEntities.register("jarless_drowned_necromancer_servant", EntityType.Builder.m_20704_(JarlessDrownedNecromancerServant::new, (MobCategory)MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).m_20699_(0.6f, 1.95f));
    public static final RegistryObject<EntityType<NamelessNecromancerServant>> NAMELESS_NECROMANCER = GoetyMasteryOfMagicModEntities.register("nameless_necromancer_servant", EntityType.Builder.m_20704_(NamelessNecromancerServant::new, (MobCategory)MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(80).setUpdateInterval(2).m_20699_(0.7f, 2.25f));
    public static final RegistryObject<EntityType<SummonGhiaccio>> SUMMON_GHIACCIO = REGISTRY.register("summon_ghiaccio", () -> EntityType.Builder.m_20704_(SummonGhiaccio::new, (MobCategory)MobCategory.MISC).m_20699_(0.5f, 2.0f).setShouldReceiveVelocityUpdates(false).m_20702_(8).m_20717_(Integer.MAX_VALUE).m_20712_("summon_ghiaccio"));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(registryname, () -> entityTypeBuilder.m_20712_(registryname));
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            VampiratorServantEntity.init();
            GazerEntity.init();
            GhiaccioEntity.init();
            IceMonarchEntity.init();
        });
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put((EntityType)VAMPIRATOR_SERVANT.get(), VampiratorServantEntity.createAttributes().m_22265_());
        event.put((EntityType)GAZER.get(), GazerEntity.createAttributes().m_22265_());
        event.put((EntityType)GHIACCIO.get(), GhiaccioEntity.createAttributes().m_22265_());
        event.put((EntityType)ICE_MONARCH.get(), IceMonarchEntity.createAttributes().m_22265_());
        event.put((EntityType)JARLESS_NECROMANCER.get(), AbstractNecromancer.setCustomAttributes().m_22265_());
        event.put((EntityType)JARLESS_WITHER_NECROMANCER.get(), AbstractWitherNecromancer.setCustomAttributes().m_22265_());
        event.put((EntityType)JARLESS_CAIRN_NECROMANCER.get(), AbstractCairnNecromancer.setCustomAttributes().m_22265_());
        event.put((EntityType)JARLESS_MOSSY_NECROMANCER.get(), AbstractMossyNecromancer.setCustomAttributes().m_22265_());
        event.put((EntityType)JARLESS_DROWNED_NECROMANCER.get(), DrownedNecromancer.setCustomAttributes().m_22265_());
        event.put((EntityType)NAMELESS_NECROMANCER.get(), NamelessNecromancerServant.createAttributes().m_22265_());
    }
}

