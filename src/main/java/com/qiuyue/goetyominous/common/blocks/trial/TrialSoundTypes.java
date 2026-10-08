package com.qiuyue.goetyominous.common.blocks.trial;

import com.qiuyue.goetyominous.common.init.ModSounds;
import net.minecraft.world.level.block.SoundType;

public class TrialSoundTypes {
    public static final SoundType POLISHED_TUFF = new SoundType(1.0F, 1.0F,
            TrialSounds.POLISHED_TUFF_BREAK.get(),
            TrialSounds.POLISHED_TUFF_STEP.get(),
            TrialSounds.POLISHED_TUFF_PLACE.get(),
            TrialSounds.POLISHED_TUFF_HIT.get(),
            TrialSounds.POLISHED_TUFF_FALL.get());

    public static final SoundType TUFF_BRICKS = new SoundType(1.0F, 1.0F,
            TrialSounds.TUFF_BRICKS_BREAK.get(),
            TrialSounds.TUFF_BRICKS_STEP.get(),
            TrialSounds.TUFF_BRICKS_PLACE.get(),
            TrialSounds.TUFF_BRICKS_HIT.get(),
            TrialSounds.TUFF_BRICKS_FALL.get());

    public static final SoundType COPPER_GRATE = new SoundType(1.0F, 1.0F,
            TrialSounds.COPPER_GRATE_BREAK.get(),
            TrialSounds.COPPER_GRATE_STEP.get(),
            TrialSounds.COPPER_GRATE_PLACE.get(),
            TrialSounds.COPPER_GRATE_HIT.get(),
            TrialSounds.COPPER_GRATE_FALL.get());

    public static final SoundType COPPER_BULB = new SoundType(1.0F, 1.0F,
            TrialSounds.COPPER_BULB_BREAK.get(),
            TrialSounds.COPPER_BULB_STEP.get(),
            TrialSounds.COPPER_BULB_PLACE.get(),
            TrialSounds.COPPER_BULB_HIT.get(),
            TrialSounds.COPPER_BULB_FALL.get());

    public static final SoundType VAULT = new SoundType(1.0F, 1.0F,
            ModSounds.VAULT_BREAK.get(),
            ModSounds.VAULT_STEP.get(),
            ModSounds.VAULT_PLACE.get(),
            ModSounds.VAULT_HIT.get(),
            ModSounds.VAULT_FALL.get());

    public static final SoundType TRIAL_SPAWNER = new SoundType(1.0F, 1.0F,
            ModSounds.TRIAL_SPAWNER_BREAK.get(),
            ModSounds.TRIAL_SPAWNER_STEP.get(),
            ModSounds.TRIAL_SPAWNER_PLACE.get(),
            ModSounds.TRIAL_SPAWNER_HIT.get(),
            ModSounds.TRIAL_SPAWNER_FALL.get());
}
