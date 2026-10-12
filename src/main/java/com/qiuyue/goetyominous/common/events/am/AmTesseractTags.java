package com.qiuyue.goetyominous.common.events.am;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import com.qiuyue.goetyominous.compat.mod.AlexMobsCompat;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.TESSERACT_MEDIUM;
import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.TESSERACT_SMALL;
import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.addToTag;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class AmTesseractTags {

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (!AlexMobsCompat.isAlexMobsLoaded()) {
            return;
        }

        addToTag(TESSERACT_SMALL, AmEntityRegistry.FARSEER_SERVANT);
        addToTag(TESSERACT_SMALL, AmEntityRegistry.ZOMBIE_CROCODILE_SERVANT);
        addToTag(TESSERACT_SMALL, AmEntityRegistry.TUSKLIN_SERVANT);
        addToTag(TESSERACT_SMALL, AmEntityRegistry.TIGER_SERVANT);
        addToTag(TESSERACT_SMALL, AmEntityRegistry.BUNFUNGUS_SERVANT);

        addToTag(TESSERACT_MEDIUM, AmEntityRegistry.WARPED_MOSCO_SERVANT);
        addToTag(TESSERACT_MEDIUM, AmEntityRegistry.ILLAGER_ELEPHANT_SERVANT);
    }
}
