package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.blocks.trial.RaidStartContext;
import com.qiuyue.goetyominous.common.init.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Raid.class)
public abstract class RaidOmenAbsorbMixin {

    @Redirect(method = "absorbBadOmen", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/effect/MobEffects;BAD_OMEN:Lnet/minecraft/world/effect/MobEffect;"), require = 1)
    private MobEffect goetyominous$absorbRaidOmenLevel(Player player) {
        return RaidStartContext.current() != null ? ModEffects.RAID_OMEN.get() : MobEffects.BAD_OMEN;
    }
}
