package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.wind.WindBlastSpell;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.utils.CroneCuriosUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WindBlastSpell.class)
public class WindBlastSpellFelMixin {

    @Inject(method = "SpellResult", at = @At("TAIL"), remap = false)
    private void goetyominous$felWindBlast(ServerLevel worldIn, LivingEntity caster, ItemStack staff,
                                           SpellStat spellStat, CallbackInfo ci) {
        WindBlastSpell self = (WindBlastSpell) (Object) this;
        if (!self.typeStaff(staff, GoetyOminous.FEL)) {
            return;
        }

        int range = spellStat.getRange();
        if (self.rightStaff(staff)) {
            range *= 2;
        }
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getRangeLevel(caster);
        }

        Vec3 lookVec = caster.getViewVector(1.0F);
        Vec3 rangeVec = new Vec3(lookVec.x * (double) range, lookVec.y * (double) range, lookVec.z * (double) range);

        for (Entity entity : caster.level().getEntities(caster,
                caster.getBoundingBox().inflate(1.0D).expandTowards(rangeVec))) {
            if (!caster.hasLineOfSight(entity)
                    || MobUtil.areAllies(entity, caster)
                    || entity.getType().is(ModTags.EntityTypes.UNBLOWABLE_ENTITIES)) {
                continue;
            }
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, MathHelper.secondsToTicks(5)));
            MobEffect effect = MobEffects.POISON;
            if (CroneCuriosUtil.hasCroneRobe(caster)) {
                effect = GoetyEffects.ACID_VENOM.get();
            }
            living.addEffect(new MobEffectInstance(effect, MathHelper.secondsToTicks(5)));
        }
    }
}
