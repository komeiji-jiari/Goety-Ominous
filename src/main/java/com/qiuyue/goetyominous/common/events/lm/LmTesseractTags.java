package com.qiuyue.goetyominous.common.events.lm;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.compat.mod.LegendaryMonstersCompat;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.TESSERACT_LARGE;
import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.TESSERACT_MEDIUM;
import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.TESSERACT_SMALL;
import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.addToTag;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class LmTesseractTags {

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (!LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            return;
        }

        addToTag(TESSERACT_SMALL, LmEntityRegistry.HOVERING_HURRICANE_SERVANT);

        addToTag(TESSERACT_MEDIUM, LmEntityRegistry.OVERGROWN_COLOSSUS_SERVANT);
        addToTag(TESSERACT_MEDIUM, LmEntityRegistry.FROSTBITTEN_GOLEM_SERVANT);
        addToTag(TESSERACT_MEDIUM, LmEntityRegistry.ANCIENT_GUARDIAN_SERVANT);
        addToTag(TESSERACT_MEDIUM, LmEntityRegistry.ANNIHILATION_PURSUER_SERVANT);
        addToTag(TESSERACT_MEDIUM, LmEntityRegistry.SHULKER_MIMIC_SERVANT);

        addToTag(TESSERACT_LARGE, LmEntityRegistry.CLOUD_GOLEM_SERVANT);
        addToTag(TESSERACT_LARGE, LmEntityRegistry.THE_OBLITERATOR_SERVANT);
        addToTag(TESSERACT_LARGE, LmEntityRegistry.POSSESSED_PALADIN_SERVANT);
    }
}
