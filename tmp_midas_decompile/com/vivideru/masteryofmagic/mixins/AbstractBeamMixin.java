/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.projectiles.AbstractBeam
 *  com.Polarice3.Goety.common.magic.spells.CorruptedBeamSpell
 *  com.Polarice3.Goety.utils.MobUtil
 *  net.minecraft.world.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.common.entities.projectiles.AbstractBeam;
import com.Polarice3.Goety.common.magic.spells.CorruptedBeamSpell;
import com.Polarice3.Goety.utils.MobUtil;
import com.vivideru.masteryofmagic.SpellRingHelper;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={AbstractBeam.class})
public class AbstractBeamMixin {
    @Redirect(method={"tick()V", "m_8119_()V"}, at=@At(value="INVOKE", target="Lcom/Polarice3/Goety/utils/MobUtil;isSpellCasting(Lnet/minecraft/world/entity/LivingEntity;)Z"), remap=false, require=0)
    private boolean goetyMasteryOfMagic$spellRingKeepsCorruptedBeamAlive(LivingEntity owner) {
        return MobUtil.isSpellCasting((LivingEntity)owner) || SpellRingHelper.isCastingSpell(owner, CorruptedBeamSpell.class);
    }
}

