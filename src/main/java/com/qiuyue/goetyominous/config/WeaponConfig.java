package com.qiuyue.goetyominous.config;

import net.minecraftforge.common.ForgeConfigSpec;
import java.io.File;

public class WeaponConfig {

    public static void loadConfig(ForgeConfigSpec spec, String path) {
        final com.electronwill.nightconfig.core.file.CommentedFileConfig configData =
                com.electronwill.nightconfig.core.file.CommentedFileConfig.builder(new File(path))
                        .sync()
                        .autoreload()
                        .writingMode(com.electronwill.nightconfig.core.io.WritingMode.REPLACE)
                        .build();
        configData.load();
        spec.setConfig(configData);
    }

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Double> BoneCudgelDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> BoneCudgelAttackSpeed;

    public static final ForgeConfigSpec.ConfigValue<Double> WitherScytheDamage;
    public static final ForgeConfigSpec.ConfigValue<Double> WitherScytheAttackSpeed;

    static {
        BUILDER.push("Bone Cudgel");
        BoneCudgelDamage = BUILDER
                .comment("Bone Cudgel base damage, Default: 13.0")
                .defineInRange("boneCudgelDamage", 13.0, 0.0, Double.MAX_VALUE);
        BoneCudgelAttackSpeed = BUILDER
                .comment("Bone Cudgel attack speed modifier, Default: -3.75")
                .defineInRange("boneCudgelAttackSpeed", -3.75, -4.0, 0.0);
        BUILDER.pop();

        BUILDER.push("Wither Scythe");
        WitherScytheDamage = BUILDER
                .comment("Wither Scythe total attack damage, Default: 9.5")
                .defineInRange("witherScytheDamage", 9.5, 0.0, Double.MAX_VALUE);
        WitherScytheAttackSpeed = BUILDER
                .comment("Wither Scythe attack speed modifier, Default: -3.3")
                .defineInRange("witherScytheAttackSpeed", -3.3, -4.0, 10.0);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
