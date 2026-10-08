package com.qiuyue.goetyominous.common.mixin.ac.abyss;

import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.abyss.TridentStormSpell;
import com.qiuyue.goetyominous.utils.ac.KeyOfRlyehMixinHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TridentStormSpell.class)
public class TridentStormSpellKeyOfRlyehMixin {

    @ModifyVariable(method = "startSpell", at = @At("STORE"), name = "potency", require = 1, remap = false)
    private int goetyominous$keyOfRlyehTridentDamage(int potency, ServerLevel worldIn, LivingEntity caster,
                                                     ItemStack staff, SpellStat spellStat) {
        return KeyOfRlyehMixinHelper.isKeyOfRlyeh(staff) ? potency + 3 : potency;
    }
}
