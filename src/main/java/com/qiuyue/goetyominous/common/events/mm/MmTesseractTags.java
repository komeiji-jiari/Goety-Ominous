package com.qiuyue.goetyominous.common.events.mm;

import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.compat.mod.MutantMoreCompat;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.TESSERACT_MEDIUM;
import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.addToTag;

public class MmTesseractTags {

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (!MutantMoreCompat.isMutantMoreLoaded()) {
            return;
        }

        addToTag(TESSERACT_MEDIUM, MmEntityRegistry.MUTANT_HOGLIN_SERVANT);
        addToTag(TESSERACT_MEDIUM, MmEntityRegistry.MUTANT_WITHER_SKELETON_SERVANT);
        addToTag(TESSERACT_MEDIUM, MmEntityRegistry.MUTANT_SHULKER_SERVANT);
        addToTag(TESSERACT_MEDIUM, MmEntityRegistry.MUTANT_BLAZE_SERVANT);
        addToTag(TESSERACT_MEDIUM, MmEntityRegistry.MUTANT_HUSK_SERVANT);
        addToTag(TESSERACT_MEDIUM, MmEntityRegistry.MUTANT_JUNGLE_ZOMBIE_SERVANT);
        addToTag(TESSERACT_MEDIUM, MmEntityRegistry.MUTANT_FROZEN_ZOMBIE_SERVANT);
    }
}
