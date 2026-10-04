package com.qiuyue.goetyominous.utils;

import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.entities.projectiles.EarthFist;
import com.Polarice3.Goety.common.entities.projectiles.ScatterMine;
import com.Polarice3.Goety.common.entities.projectiles.SmackStone;
import com.qiuyue.goetyominous.common.entities.ally.mobs.BreezeServant;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractHurricane;
import com.qiuyue.goetyominous.common.entities.hostile.BreezeEntity;
import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class SpellTypeDamage {
    public static final float GEOMANCY_VS_WIND_MULTIPLIER = 2.0F;
    @Nullable
    private static SpellType casting;

    private SpellTypeDamage() {
    }

    public static void cast(ISpell spell, Runnable step) {
        SpellType previous = casting;
        casting = spell.getSpellType();
        try {
            step.run();
        } finally {
            casting = previous;
        }
    }

    public static boolean isWindMob(Entity entity) {
        return entity instanceof BreezeEntity || entity instanceof BreezeServant || entity instanceof AbstractHurricane;
    }

    public static boolean isGeomancy(DamageSource source) {
        if (casting == SpellType.GEOMANCY) {
            return true;
        }
        Entity direct = source.getDirectEntity();
        return direct instanceof EarthFist || direct instanceof SmackStone || direct instanceof ScatterMine;
    }

    public static float adjust(LivingEntity target, DamageSource source, float amount) {
        return isWindMob(target) && isGeomancy(source) ? amount * GEOMANCY_VS_WIND_MULTIPLIER : amount;
    }
}
