package com.qiuyue.goetyominous.common.mixin.ac.abyss;

import com.Polarice3.Goety.common.entities.projectiles.SpellHurtingProjectile;
import com.Polarice3.Goety.common.entities.projectiles.SteamMissile;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.abyss.SteamSpell;
import com.qiuyue.goetyominous.utils.ac.KeyOfRlyehMixinHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SteamSpell.class)
public class SteamSpellKeyOfRlyehMixin {

    @Unique
    private float goetyominous$steamVelocity;

    @ModifyVariable(method = "SpellResult", at = @At("STORE"), name = "velocity", require = 1, remap = false)
    private float goetyominous$captureVelocity(float velocity) {
        this.goetyominous$steamVelocity = velocity;
        return velocity;
    }

    @ModifyVariable(method = "SpellResult", at = @At("STORE"), name = "steamMissile", require = 1, remap = false)
    private SpellHurtingProjectile goetyominous$duplicateMissile(SpellHurtingProjectile steamMissile,
                                                                 ServerLevel worldIn, LivingEntity caster,
                                                                 ItemStack staff, SpellStat spellStat) {
        if (!KeyOfRlyehMixinHelper.isKeyOfRlyeh(staff)) {
            return steamMissile;
        }

        Vec3 vector3d = caster.getViewVector(1.0F);
        double accuracy = 8.0D;
        Vec3 vec3 = (new Vec3(vector3d.x, vector3d.y, vector3d.z)).normalize().add(
                worldIn.random.triangle(0.0D, 0.0172275D * accuracy), 0.0D,
                worldIn.random.triangle(0.0D, 0.0172275D * accuracy));
        SpellHurtingProjectile extra = new SteamMissile(
                caster.getX() + vector3d.x / 2.0D,
                caster.getEyeY() - 0.2D,
                caster.getZ() + vector3d.z / 2.0D,
                vec3.x, vec3.y, vec3.z, worldIn);

        extra.setExtraDamage((float) spellStat.getPotency());
        extra.setBoltSpeed((int) this.goetyominous$steamVelocity);
        extra.setOwner(caster);
        worldIn.addFreshEntity(extra);

        return steamMissile;
    }
}
