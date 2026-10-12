package com.qiuyue.goetyominous.common.events.ua;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ua.UaEntityRegistry;
import com.qiuyue.goetyominous.compat.mod.UpgradeAquaticCompat;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.TESSERACT_MEDIUM;
import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.TESSERACT_SMALL;
import static com.qiuyue.goetyominous.common.events.TesseractTagHandler.addToTag;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID)
public class UaTesseractTags {

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (!UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            return;
        }

        addToTag(TESSERACT_SMALL, UaEntityRegistry.THRASHER_SERVANT);

        addToTag(TESSERACT_MEDIUM, UaEntityRegistry.GREAT_THRASHER_SERVANT);
    }
}
