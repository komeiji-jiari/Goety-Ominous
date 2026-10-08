package com.qiuyue.goetyominous.common.mixin.ac.abyss;

import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.wild.HuntingSpell;
import com.qiuyue.goetyominous.utils.ac.KeyOfRlyehMixinHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HuntingSpell.class)
public class HuntingSpellKeyOfRlyehMixin {

    @ModifyVariable(method = "SpellResult", at = @At("STORE"), name = "i", require = 1, remap = false)
    private int goetyominous$keyOfRlyehCount(int i, ServerLevel worldIn, LivingEntity caster,
                                             ItemStack staff, SpellStat spellStat) {
        return KeyOfRlyehMixinHelper.isKeyOfRlyeh(staff) ? 3 : i;
    }
}
