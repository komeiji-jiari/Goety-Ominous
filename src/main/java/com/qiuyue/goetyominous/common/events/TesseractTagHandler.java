package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.common.init.ua.UaEntityRegistry;
import com.qiuyue.goetyominous.compat.ias.IasEntityRegistry;
import com.qiuyue.goetyominous.compat.mod.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class TesseractTagHandler {

    private static final TagKey<EntityType<?>> TESSERACT_SMALL =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("goety", "tesseract_small"));
    private static final TagKey<EntityType<?>> TESSERACT_MEDIUM =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("goety", "tesseract_medium"));
    private static final TagKey<EntityType<?>> TESSERACT_LARGE =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("goety", "tesseract_large"));

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        addToTag(TESSERACT_SMALL, ModEntityTypes.URBHADHACH_SERVANT);
        if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
            addToTag(TESSERACT_SMALL, IasEntityRegistry.ABSORBER_SERVANT);
        }
        if (UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            addToTag(TESSERACT_SMALL, UaEntityRegistry.THRASHER_SERVANT);
        }
        if (AlexMobsCompat.isAlexMobsLoaded()) {
            addToTag(TESSERACT_SMALL, AmEntityRegistry.FARSEER_SERVANT);
            addToTag(TESSERACT_SMALL, AmEntityRegistry.ZOMBIE_CROCODILE_SERVANT);
            addToTag(TESSERACT_SMALL, AmEntityRegistry.TUSKLIN_SERVANT);
            addToTag(TESSERACT_SMALL, AmEntityRegistry.BUNFUNGUS_SERVANT);
        }
        if (AlexCavesCompat.isAlexCavesLoaded()) {
            addToTag(TESSERACT_SMALL, AcEntityRegistry.GAMMAROACH_SERVANT);
        }

        addToTag(TESSERACT_MEDIUM, ModEntityTypes.HERESIARCH_SERVANT);
        addToTag(TESSERACT_MEDIUM, ModEntityTypes.STORM_NECROMANCER_SERVANT);
        if (IllageAndSpillageCompat.isIllageAndSpillageLoaded()) {
            addToTag(TESSERACT_MEDIUM, IasEntityRegistry.MAGISPELLER_SERVANT);
        }
        if (UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            addToTag(TESSERACT_MEDIUM, UaEntityRegistry.GREAT_THRASHER_SERVANT);
        }
        if (LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            addToTag(TESSERACT_MEDIUM, LmEntityRegistry.OVERGROWN_COLOSSUS_SERVANT);
        }
        if (AlexMobsCompat.isAlexMobsLoaded()) {
            addToTag(TESSERACT_MEDIUM, AmEntityRegistry.WARPED_MOSCO_SERVANT);
            addToTag(TESSERACT_MEDIUM, AmEntityRegistry.ILLAGER_ELEPHANT_SERVANT);
        }
        if (AlexCavesCompat.isAlexCavesLoaded()) {
            addToTag(TESSERACT_MEDIUM, AcEntityRegistry.TREMORSAURUS_SERVANT);
            addToTag(TESSERACT_MEDIUM, AcEntityRegistry.GROTTOCERATOPS_SERVANT);
        }

        if (MutantMoreCompat.isMutantMoreLoaded()) {
            addToTag(TESSERACT_LARGE, MmEntityRegistry.MUTANT_HOGLIN_SERVANT);
            addToTag(TESSERACT_LARGE, MmEntityRegistry.MUTANT_WITHER_SKELETON_SERVANT);
            addToTag(TESSERACT_LARGE, MmEntityRegistry.MUTANT_SHULKER_SERVANT);
        }

        if (AlexCavesCompat.isAlexCavesLoaded()) {
            addToTag(TESSERACT_LARGE, AcEntityRegistry.HULLBREAKER_SERVANT);
            addToTag(TESSERACT_LARGE, AcEntityRegistry.TREMORZILLA_SERVANT);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addToTag(TagKey<EntityType<?>> tag, RegistryObject<?> reg) {
        if (!reg.isPresent()) return;

        ForgeRegistries.ENTITY_TYPES.getResourceKey((EntityType<?>) reg.get()).flatMap(
                ForgeRegistries.ENTITY_TYPES::getHolder
        ).ifPresent(holder -> {
            if (holder instanceof Holder.Reference) {
                Holder.Reference ref = (Holder.Reference) holder;
                Set<TagKey> tags = new HashSet<>();
                ref.tags().forEach(t -> tags.add((TagKey) t));
                tags.add(tag);
                ref.bindTags(tags);
            }
        });
    }
}
