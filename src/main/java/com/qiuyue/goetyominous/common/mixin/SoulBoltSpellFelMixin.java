package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.spells.SoulBoltSpell;
import com.Polarice3.Goety.utils.SoundUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.projectile.FelBolt;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoulBoltSpell.class)
public class SoulBoltSpellFelMixin {

    @Inject(method = "SpellResult", at = @At("HEAD"), cancellable = true, remap = false)
    private void goetyominous$felBolt(ServerLevel worldIn, LivingEntity caster, ItemStack staff,
                                      com.Polarice3.Goety.common.magic.SpellStat spellStat, CallbackInfo ci) {
        SoulBoltSpell self = (SoulBoltSpell) (Object) this;
        if (!self.typeStaff(staff, GoetyOminous.FEL)) {
            return;
        }

        int potency = spellStat.getPotency();
        float velocity = spellStat.getVelocity();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            velocity += (float) WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        }

        Vec3 view = caster.getViewVector(1.0F);
        FelBolt bolt = new FelBolt(
                caster.getX() + view.x / 2.0D, caster.getEyeY() - 0.2D, caster.getZ() + view.z / 2.0D,
                view.x, view.y, view.z, worldIn);
        bolt.setExtraDamage((float) potency);
        bolt.setBoltSpeed((int) velocity);
        bolt.setOwner(caster);
        worldIn.addFreshEntity(bolt);
        SoundUtil.playSoulBolt(caster);

        ci.cancel();
    }
}
