/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.magic.SpellType
 *  com.Polarice3.Goety.utils.WandUtil
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Projectile
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.utils.WandUtil;
import com.vivideru.masteryofmagic.MasteryData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;

public final class SchoolSupremeDamageHelper {
    private SchoolSupremeDamageHelper() {
    }

    public static boolean isEmpoweredSkyDamage(DamageSource source) {
        String entity;
        Player player;
        Entity direct = source.m_7640_();
        Entity owner = source.m_7639_();
        if (!(owner instanceof Player) && direct instanceof Projectile) {
            Projectile p = (Projectile)direct;
            owner = p.m_19749_();
        }
        if (!(owner instanceof Player) || !MasteryData.hasSupreme(player = (Player)owner, MasteryData.SupremeSchool.SKIES)) {
            return false;
        }
        String damage = source.m_269150_().m_203543_().map(k -> k.m_135782_().m_135815_()).orElse("");
        String string = entity = direct == null ? "" : BuiltInRegistries.f_256780_.m_7981_((Object)direct.m_6095_()).m_135815_();
        if (SchoolSupremeDamageHelper.skyName(damage) || SchoolSupremeDamageHelper.skyName(entity)) {
            return true;
        }
        if (direct == null || direct == owner) {
            return false;
        }
        try {
            return WandUtil.getSpell((LivingEntity)player).getSpellTypes().stream().anyMatch(t -> t == SpellType.WIND || t == SpellType.STORM);
        }
        catch (RuntimeException ignored) {
            return false;
        }
    }

    private static boolean skyName(String value) {
        return value.contains("wind") || value.contains("storm") || value.contains("lightning") || value.contains("thunder") || value.contains("shock") || value.contains("cyclone") || value.contains("tornado");
    }
}

