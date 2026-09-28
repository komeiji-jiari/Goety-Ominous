package com.qiuyue.goetyominous.common.mixin.abyss;

import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.abyss.PrismaBeamSpell;
import com.qiuyue.goetyominous.utils.KeyOfRlyehMixinHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin(PrismaBeamSpell.class)
public class PrismaBeamSpellKeyOfRlyehMixin {

    @ModifyVariable(method = "SpellResult", at = @At("STORE"), name = "damage", require = 1, remap = false)
    private float goetyominous$keyOfRlyehDamage(float damage, ServerLevel worldIn, LivingEntity caster,
                                                ItemStack staff, SpellStat spellStat) {
        return KeyOfRlyehMixinHelper.isKeyOfRlyeh(staff) ? damage + 2.0F : damage;
    }

    @ModifyVariable(method = "SpellResult", at = @At("STORE"), name = "f", require = 1, remap = false)
    private float goetyominous$keyOfRlyehMagicDamage(float f, ServerLevel worldIn, LivingEntity caster,
                                                     ItemStack staff, SpellStat spellStat) {
        return KeyOfRlyehMixinHelper.isKeyOfRlyeh(staff) ? f + 2.0F : f;
    }
}
