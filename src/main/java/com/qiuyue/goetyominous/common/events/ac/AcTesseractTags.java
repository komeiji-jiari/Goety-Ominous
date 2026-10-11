package com.qiuyue.goetyominous.common.events.ac;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.compat.mod.AlexCavesCompat;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.TESSERACT_LARGE;
import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.TESSERACT_MEDIUM;
import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.TESSERACT_SMALL;
import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.addToTag;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class AcTesseractTags {

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }

        addToTag(TESSERACT_SMALL, AcEntityRegistry.CORRODENT_SERVANT);
        addToTag(TESSERACT_SMALL, AcEntityRegistry.CANDICORN_SERVANT);
        addToTag(TESSERACT_SMALL, AcEntityRegistry.DEEP_ONE_SERVANT);
        addToTag(TESSERACT_SMALL, AcEntityRegistry.DEEP_ONE_KNIGHT_SERVANT);
        addToTag(TESSERACT_SMALL, AcEntityRegistry.DEEP_ONE_MAGE_SERVANT);

        addToTag(TESSERACT_MEDIUM, AcEntityRegistry.TREMORSAURUS_SERVANT);
        addToTag(TESSERACT_MEDIUM, AcEntityRegistry.GROTTOCERATOPS_SERVANT);
        addToTag(TESSERACT_MEDIUM, AcEntityRegistry.FORSAKEN_SERVANT);
        addToTag(TESSERACT_MEDIUM, AcEntityRegistry.RELICHEIRUS_SERVANT);
        addToTag(TESSERACT_MEDIUM, AcEntityRegistry.NUCLEEPER_SERVANT);

        addToTag(TESSERACT_LARGE, AcEntityRegistry.HULLBREAKER_SERVANT);
        addToTag(TESSERACT_LARGE, AcEntityRegistry.TREMORZILLA_SERVANT);
        addToTag(TESSERACT_LARGE, AcEntityRegistry.ATLATITAN_SERVANT);
        addToTag(TESSERACT_LARGE, AcEntityRegistry.LUXTRUCTOSAURUS_SERVANT);
        addToTag(TESSERACT_LARGE, AcEntityRegistry.GUM_WORM_SERVANT);
    }
}
