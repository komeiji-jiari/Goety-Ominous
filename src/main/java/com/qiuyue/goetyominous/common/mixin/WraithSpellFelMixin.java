package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.necromancy.WraithSpell;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WraithSpell.class)
public class WraithSpellFelMixin {

    @Redirect(
            method = "SpellResult",
            at = @At(value = "INVOKE",
                    target = "Lcom/Polarice3/Goety/common/magic/spells/necromancy/WraithSpell;typeStaff(Lnet/minecraft/world/item/ItemStack;Lcom/Polarice3/Goety/api/magic/SpellType;)Z"),
            remap = false)
    private boolean goetyominous$felOrWild(WraithSpell self, ItemStack staff, SpellType type) {
        return self.typeStaff(staff, type) || self.typeStaff(staff, GoetyOminous.FEL);
    }

    @ModifyVariable(method = "SpellResult", at = @At("STORE"), name = "i", remap = false)
    private int goetyominous$felWraithCount(int count, ServerLevel level, LivingEntity caster,
                                            ItemStack staff, SpellStat stat) {
        return isFelStaff(staff) ? 2 : count;
    }

    private static boolean isFelStaff(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IWand wand
                && wand.getSpellTypes().contains(GoetyOminous.FEL);
    }
}
