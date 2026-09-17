package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.necromancy.SkeletonSpell;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SkeletonSpell.class)
public class SkeletonSpellBoggedMixin {

    @Redirect(
            method = "SpellResult",
            at = @At(value = "INVOKE",
                    target = "Lcom/Polarice3/Goety/common/entities/ally/undead/skeleton/AbstractSkeletonServant;getVariant(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/entity/EntityType;"),
            remap = false)
    private EntityType<?> goetyominous$boggedOnFel(AbstractSkeletonServant servant, Player player,
                                                   Level level, BlockPos pos) {
        if (player != null && isFelStaff(WandUtil.findWand(player))) {
            return ModEntityTypes.BOGGED_SERVANT.get();
        }
        return servant.getVariant(player, level, pos);
    }

    @ModifyVariable(method = "SpellResult", at = @At("STORE"), name = "i", remap = false)
    private int goetyominous$felBoggedCount(int count, ServerLevel level, LivingEntity caster,
                                            ItemStack staff, SpellStat stat) {
        return isFelStaff(staff) ? 2 + level.random.nextInt(2) : count;
    }

    private static boolean isFelStaff(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IWand wand
                && wand.getSpellTypes().contains(GoetyOminous.FEL);
    }
}
