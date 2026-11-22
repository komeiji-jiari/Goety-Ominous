package com.qiuyue.goetyominous.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;

public class WorldGenConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ClassicCrypt;
    public static final ForgeConfigSpec.BooleanValue ClassicWindShrine;

    static {
        BUILDER.push("World Generation");
        ClassicCrypt = BUILDER.comment("Generate the original Goety crypt instead of the layered Goety 3 crypt, Default: true")
                .define("classicCrypt", true);
        ClassicWindShrine = BUILDER.comment("Generate the original Goety wind shrine instead of the Goety 3 mountain wind shrine, Default: false")
                .define("classicWindShrine", false);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static boolean classicWindShrine() {
        return ClassicWindShrine.get();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path))
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        file.load();
        config.setConfig(file);
    }
}
