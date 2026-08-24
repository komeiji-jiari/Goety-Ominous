/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.entities.ISpellEntity
 *  com.Polarice3.Goety.common.entities.projectiles.AbstractBeam
 *  com.Polarice3.Goety.common.entities.projectiles.DeathArrow
 *  com.Polarice3.Goety.common.entities.projectiles.EnderGoo
 *  com.Polarice3.Goety.utils.MobUtil
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.registries.ForgeRegistries
 */
package com.vivideru.masteryofmagic.spells.midas;

import com.Polarice3.Goety.api.entities.ISpellEntity;
import com.Polarice3.Goety.common.entities.projectiles.AbstractBeam;
import com.Polarice3.Goety.common.entities.projectiles.DeathArrow;
import com.Polarice3.Goety.common.entities.projectiles.EnderGoo;
import com.Polarice3.Goety.utils.MobUtil;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public final class MidasSpellThreatRegistry {
    private static final Class<?> IRONS_MAGIC_PROJECTILE = MidasSpellThreatRegistry.findOptionalClass("io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile");
    private static final Class<?> IRONS_MAGIC_ENTITY = MidasSpellThreatRegistry.findOptionalClass("io.redspace.ironsspellbooks.api.entity.IMagicEntity");
    public static final TagKey<EntityType<?>> ABSOLUTE_COUNTER_SPELLS = TagKey.m_203882_((ResourceKey)Registries.f_256939_, (ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "midas_absolute_counter_spells"));
    private static final Set<ResourceLocation> ABSOLUTE_COUNTERS = new LinkedHashSet<ResourceLocation>();

    private MidasSpellThreatRegistry() {
    }

    public static void registerAbsoluteCounter(ResourceLocation entityTypeId) {
        ABSOLUTE_COUNTERS.add(entityTypeId);
    }

    public static Set<ResourceLocation> absoluteCounterEntityTypes() {
        return Collections.unmodifiableSet(ABSOLUTE_COUNTERS);
    }

    public static boolean hasDangerousSpellThreat(ServerLevel level, PhilosopherKingMidasEntity midas) {
        return !MidasSpellThreatRegistry.dangerousThreats(level, midas).isEmpty();
    }

    public static List<Threat> dangerousThreats(ServerLevel level, PhilosopherKingMidasEntity midas) {
        AABB search = midas.m_20191_().m_82400_(72.0);
        ArrayList<Threat> threats = new ArrayList<Threat>();
        for (Entity entity : level.m_6249_((Entity)midas, search, candidate -> candidate instanceof ISpellEntity || candidate instanceof Projectile)) {
            if (!MidasSpellThreatRegistry.isDangerousSpellThreat(entity, midas)) continue;
            threats.add(new Threat(entity));
        }
        threats.sort(Comparator.comparingDouble(threat -> threat.entity().m_20280_((Entity)midas)));
        return threats;
    }

    public static boolean isMagicalProjectile(Entity entity) {
        if (!(entity instanceof Projectile)) {
            return false;
        }
        if (entity instanceof DeathArrow) {
            return false;
        }
        if (entity instanceof EnderGoo) {
            return true;
        }
        if (entity instanceof ISpellEntity) {
            return true;
        }
        if (IRONS_MAGIC_PROJECTILE != null && IRONS_MAGIC_PROJECTILE.isInstance(entity) || IRONS_MAGIC_ENTITY != null && IRONS_MAGIC_ENTITY.isInstance(entity)) {
            return true;
        }
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey((Object)entity.m_6095_());
        String namespace = id == null ? "" : id.m_135827_();
        String packageName = entity.getClass().getPackageName().toLowerCase(Locale.ROOT);
        boolean ironsSpell = (IRONS_MAGIC_PROJECTILE != null || IRONS_MAGIC_ENTITY != null) && id != null && "irons_spellbooks".equals(namespace) && packageName.contains(".entity.spells");
        return ironsSpell || packageName.contains(".projectile") && ("goety".equals(namespace) || "goety_mastery_of_magic".equals(namespace));
    }

    private static Class<?> findOptionalClass(String name) {
        try {
            return Class.forName(name, false, MidasSpellThreatRegistry.class.getClassLoader());
        }
        catch (ClassNotFoundException | LinkageError ignored) {
            return null;
        }
    }

    private static boolean isDangerousSpellThreat(Entity spell, PhilosopherKingMidasEntity midas) {
        AbstractBeam beam;
        Entity owner;
        Projectile projectile;
        if (spell instanceof Projectile) {
            projectile = (Projectile)spell;
            v0 = projectile.m_19749_();
        } else if (spell instanceof AbstractBeam) {
            AbstractBeam beam2 = (AbstractBeam)spell;
            v0 = beam2.getOwner();
        } else {
            v0 = owner = null;
        }
        if (owner == midas || owner != null && MobUtil.areAllies((Entity)owner, (Entity)midas)) {
            return false;
        }
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey((Object)spell.m_6095_());
        if (spell.m_6095_().m_204039_(ABSOLUTE_COUNTER_SPELLS) || id != null && ABSOLUTE_COUNTERS.contains(id)) {
            if (spell instanceof AbstractBeam) {
                beam = (AbstractBeam)spell;
                return MidasSpellThreatRegistry.beamIntersectsMidas(beam, midas);
            }
            return true;
        }
        if (spell instanceof AbstractBeam) {
            beam = (AbstractBeam)spell;
            return MidasSpellThreatRegistry.beamIntersectsMidas(beam, midas);
        }
        if (spell instanceof Projectile && (projectile = (Projectile)spell).m_20280_((Entity)midas) <= 1024.0) {
            return MidasSpellThreatRegistry.movingTowardBox(projectile.m_20182_(), projectile.m_20184_(), midas.m_20191_().m_82400_(0.75), 32.0);
        }
        return false;
    }

    private static boolean beamIntersectsMidas(AbstractBeam beam, PhilosopherKingMidasEntity midas) {
        Vec3 origin = beam.m_20182_();
        Vec3 direction = beam.m_20252_(1.0f).m_82541_();
        Vec3 toCenter = midas.m_20191_().m_82399_().m_82546_(origin);
        double projection = toCenter.m_82526_(direction);
        if (projection < 0.0 || projection > 64.0) {
            return false;
        }
        Vec3 closest = origin.m_82549_(direction.m_82490_(projection));
        return closest.m_82557_(midas.m_20191_().m_82399_()) <= 2.25;
    }

    private static boolean movingTowardBox(Vec3 origin, Vec3 velocity, AABB box, double lookAhead) {
        if (velocity.m_82556_() < 1.0E-6) {
            return false;
        }
        Vec3 direction = velocity.m_82541_();
        Vec3 toCenter = box.m_82399_().m_82546_(origin);
        double projection = toCenter.m_82526_(direction);
        if (projection < 0.0 || projection > lookAhead) {
            return false;
        }
        return origin.m_82549_(direction.m_82490_(projection)).m_82557_(box.m_82399_()) <= Math.max(2.25, box.m_82309_() * box.m_82309_() * 0.25);
    }

    static {
        MidasSpellThreatRegistry.registerAbsoluteCounter(new ResourceLocation("goety", "corrupted_beam"));
    }

    public record Threat(Entity entity) {
        public Vec3 position() {
            return this.entity.m_20191_().m_82399_();
        }
    }
}

