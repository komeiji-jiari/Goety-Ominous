package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.items.ac.AcItems;
import com.qiuyue.goetyominous.compat.mod.AlexCavesCompat;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;
import java.util.Set;


@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class CuriosTagHandler {

    private static final TagKey<Item> NECKLACE =
            TagKey.create(Registries.ITEM, new ResourceLocation("curios", "necklace"));

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (AlexCavesCompat.isAlexCavesLoaded()) {
            addItemToTag(NECKLACE, AcItems.RAYCAT_AMULET);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addItemToTag(TagKey<Item> tag, RegistryObject<Item> reg) {
        if (!reg.isPresent()) return;

        ForgeRegistries.ITEMS.getResourceKey(reg.get()).flatMap(
                ForgeRegistries.ITEMS::getHolder
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
