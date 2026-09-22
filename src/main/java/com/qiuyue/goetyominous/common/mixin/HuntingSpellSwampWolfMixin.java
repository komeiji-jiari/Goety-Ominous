package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.wild.HuntingSpell;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mobs.SwampWolf;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HuntingSpell.class)
public class HuntingSpellSwampWolfMixin {

    @ModifyVariable(method = "SpellResult", at = @At("STORE"), name = "summonedentity", require = 1, remap = false)
    private Summoned goetyominous$swampWolf(Summoned summoned, ServerLevel worldIn, LivingEntity caster,
                                            ItemStack staff, SpellStat spellStat) {
        if (summoned == null) {
            return null;
        }
        if (isFelStaff(staff)) {
            return new SwampWolf(ModEntityTypes.SWAMP_WOLF.get(), worldIn);
        }
        if (summoned.getType() == ModEntityType.BLACK_WOLF.get()
                && worldIn.getBiome(caster.blockPosition()).is(Tags.Biomes.IS_SWAMP)) {
            return new SwampWolf(ModEntityTypes.SWAMP_WOLF.get(), worldIn);
        }
        return summoned;
    }

    @ModifyVariable(method = "SpellResult", at = @At("STORE"), name = "i", require = 1, remap = false)
    private int goetyominous$felSwampWolfCount(int count, ServerLevel worldIn, LivingEntity caster,
                                               ItemStack staff, SpellStat spellStat) {
        return isFelStaff(staff) ? 2 : count;
    }

    private static boolean isFelStaff(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IWand wand
                && wand.getSpellTypes().contains(GoetyOminous.FEL);
    }
}
