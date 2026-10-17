package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
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

    public static final TagKey<EntityType<?>> TESSERACT_SMALL =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("goety", "tesseract_small"));
    public static final TagKey<EntityType<?>> TESSERACT_MEDIUM =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("goety", "tesseract_medium"));
    public static final TagKey<EntityType<?>> TESSERACT_LARGE =
            TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("goety", "tesseract_large"));

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        addToTag(TESSERACT_SMALL, ModEntityTypes.URBHADHACH_SERVANT);
        addToTag(TESSERACT_SMALL, ModEntityTypes.HURRICANE_SERVANT);

        addToTag(TESSERACT_MEDIUM, ModEntityTypes.HERESIARCH_SERVANT);
        addToTag(TESSERACT_MEDIUM, ModEntityTypes.STORM_NECROMANCER_SERVANT);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addToTag(TagKey<EntityType<?>> tag, RegistryObject<?> reg) {
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
