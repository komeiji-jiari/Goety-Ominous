package com.qiuyue.goetyominous.common.events.of;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import com.qiuyue.goetyominous.compat.mod.OpposingForceCompat;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.TESSERACT_MEDIUM;
import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.TESSERACT_SMALL;
import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.addToTag;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class OfTesseractTags {

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (!OpposingForceCompat.isOpposingForceLoaded()) {
            return;
        }

        addToTag(TESSERACT_SMALL, OfEntityRegistry.DICER_SERVANT);
        addToTag(TESSERACT_SMALL, OfEntityRegistry.RAMBLER_SERVANT);

        addToTag(TESSERACT_MEDIUM, OfEntityRegistry.SKYVERN_SERVANT);
    }
}
