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

/**
 * Curios 槽位注入。raycat_amulet 是 Alex's Caves 联动饰品,过去静态写在
 * data/curios/tags/items/necklace.json 里:AC 未加载时该条目引用缺失物品,
 * 会让整个 curios:necklace 物品 tag 加载失败,连带所有项链物品无法佩戴。
 * 这里照搬 TesseractTagHandler 的运行时 tag 注入手法:json 只留空 tag,
 * 仅当 AC 加载(物品存在)时把 curios:necklace 绑到该物品的 Holder.Reference 上。
 * Curios 的 curios:tag 校验走 stack.is(curios:necklace),即 holder 的绑定 tag 集合,
 * bindTags 改的正是这个集合,因此注入后即可进项链槽。
 */
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
