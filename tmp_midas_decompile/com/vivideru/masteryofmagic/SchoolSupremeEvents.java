/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.entities.IOwned
 *  com.Polarice3.Goety.common.entities.util.FirePillar
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.damagesource.DamageTypes
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$PlayerTickEvent
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.living.LivingDamageEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  org.joml.Vector3f
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.util.FirePillar;
import com.vivideru.masteryofmagic.MasteryData;
import com.vivideru.masteryofmagic.SchoolSupremeDamageHelper;
import java.util.UUID;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class SchoolSupremeEvents {
    public static final String DEATH_FLAME = "gmom_death_flame";
    public static final String DEATH_FLAME_ORIGIN = "gmom_death_flame_origin";
    public static final String DEATH_FLAME_NAME = "Death Flames";
    private static final String FLIGHT_MARKER = "gmom_skies_flight_boost";
    private static final String FLIGHT_BASE = "gmom_skies_flight_base";

    private SchoolSupremeEvents() {
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.m_9236_().f_46443_) {
            return;
        }
        Player player = event.player;
        if (MasteryData.hasSupreme(player, MasteryData.SupremeSchool.NETHER)) {
            player.m_21195_(MobEffects.f_19615_);
        }
        boolean skies = MasteryData.hasSupreme(player, MasteryData.SupremeSchool.SKIES);
        CompoundTag tag = player.getPersistentData();
        if (skies && !tag.m_128471_(FLIGHT_MARKER)) {
            float base = player.m_150110_().m_35942_();
            tag.m_128350_(FLIGHT_BASE, base);
            tag.m_128379_(FLIGHT_MARKER, true);
            player.m_150110_().m_35943_(base * 2.0f);
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                serverPlayer.m_6885_();
            }
        } else if (skies && Math.abs(player.m_150110_().m_35942_() - tag.m_128457_(FLIGHT_BASE) * 2.0f) > 1.0E-4f) {
            player.m_150110_().m_35943_(tag.m_128457_(FLIGHT_BASE) * 2.0f);
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                serverPlayer.m_6885_();
            }
        } else if (!skies && tag.m_128471_(FLIGHT_MARKER)) {
            player.m_150110_().m_35943_(tag.m_128457_(FLIGHT_BASE));
            tag.m_128473_(FLIGHT_BASE);
            tag.m_128473_(FLIGHT_MARKER);
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                serverPlayer.m_6885_();
            }
        }
    }

    @SubscribeEvent
    public static void entityJoin(EntityJoinLevelEvent event) {
        Player player;
        if (event.getLevel().m_5776_()) {
            return;
        }
        Entity entity = event.getEntity();
        String path = BuiltInRegistries.f_256780_.m_7981_((Object)entity.m_6095_()).m_135815_();
        if (!SchoolSupremeEvents.isInfernalFlame(path)) {
            return;
        }
        Entity owner = SchoolSupremeEvents.owner(entity);
        if (owner instanceof Player && MasteryData.hasSupreme(player = (Player)owner, MasteryData.SupremeSchool.NETHER)) {
            entity.m_20049_(DEATH_FLAME);
            entity.m_20049_(DEATH_FLAME_ORIGIN);
            entity.m_6593_((Component)Component.m_237113_((String)DEATH_FLAME_NAME));
            entity.m_20340_(false);
            if (entity instanceof FirePillar) {
                FirePillar pillar = (FirePillar)entity;
                int oldWarm = Math.max(0, pillar.warmUp);
                int fastCast = Math.max(10, (int)Math.ceil((double)(oldWarm + 10) * 0.1));
                int fastWarm = Math.max(0, fastCast - 10);
                pillar.setDuration(Math.max(1, pillar.getDuration() - oldWarm + fastWarm));
                pillar.warmUp = fastWarm;
            }
        }
    }

    @SubscribeEvent
    public static void livingHurt(LivingHurtEvent event) {
        Player player;
        LivingEntity victim = event.getEntity();
        if (victim instanceof Player && MasteryData.hasSupreme(player = (Player)victim, MasteryData.SupremeSchool.SKIES) && event.getSource().m_276093_(DamageTypes.f_268576_)) {
            event.setCanceled(true);
            return;
        }
        Entity direct = event.getSource().m_7640_();
        if (direct != null && direct.m_19880_().contains(DEATH_FLAME)) {
            event.setAmount(event.getAmount() * 1.15f);
            victim.m_7292_(new MobEffectInstance(MobEffects.f_19615_, 100, 1));
        }
    }

    @SubscribeEvent
    public static void livingDamage(LivingDamageEvent event) {
        if (event.getAmount() > 0.0f && event.getAmount() < 1.0f && SchoolSupremeDamageHelper.isEmpoweredSkyDamage(event.getSource())) {
            event.setAmount(1.0f);
        }
    }

    public static void tickDeathFlame(Entity entity) {
        ServerLevel level;
        block10: {
            block9: {
                Level level2 = entity.m_9236_();
                if (!(level2 instanceof ServerLevel)) break block9;
                level = (ServerLevel)level2;
                if (entity.m_19880_().contains(DEATH_FLAME_ORIGIN)) break block10;
            }
            return;
        }
        if (entity.f_19797_ <= 60 && entity.m_19880_().contains(DEATH_FLAME)) {
            if ((entity.f_19797_ & 1) == 0) {
                level.m_8767_((ParticleOptions)new DustParticleOptions(new Vector3f(0.03f, 0.03f, 0.05f), 1.1f), entity.m_20185_(), entity.m_20186_() + 0.15, entity.m_20189_(), 3, 0.12, 0.12, 0.12, 0.01);
            }
        } else if (entity.f_19797_ > 60) {
            entity.m_20137_(DEATH_FLAME);
            if (entity.m_8077_() && DEATH_FLAME_NAME.equals(entity.m_7770_().getString())) {
                entity.m_6593_(null);
            }
        }
    }

    public static void deathFlameRemoved(Entity entity) {
        ServerLevel level;
        block13: {
            block12: {
                Level level2 = entity.m_9236_();
                if (!(level2 instanceof ServerLevel)) break block12;
                level = (ServerLevel)level2;
                if (entity.m_19880_().contains(DEATH_FLAME_ORIGIN) && !entity.getPersistentData().m_128471_("gmom_inferno_checked")) break block13;
            }
            return;
        }
        entity.getPersistentData().m_128379_("gmom_inferno_checked", true);
        if (level.f_46441_.m_188501_() >= 0.025f) {
            return;
        }
        Entity owner = SchoolSupremeEvents.owner(entity);
        if (!(owner instanceof Player)) {
            return;
        }
        Player player = (Player)owner;
        EntityType type = (EntityType)BuiltInRegistries.f_256780_.m_7745_(new ResourceLocation("goety", "inferno"));
        if (type == null) {
            return;
        }
        Entity spawned = type.m_20615_((Level)level);
        if (!(spawned instanceof LivingEntity)) {
            return;
        }
        LivingEntity living = (LivingEntity)spawned;
        living.m_7678_(entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), level.f_46441_.m_188501_() * 360.0f, 0.0f);
        if (living instanceof IOwned) {
            IOwned owned = (IOwned)living;
            try {
                living.getClass().getMethod("setTrueOwner", LivingEntity.class).invoke(living, player);
            }
            catch (ReflectiveOperationException ignored) {
                try {
                    living.getClass().getMethod("setOwnerId", UUID.class).invoke(living, player.m_20148_());
                }
                catch (ReflectiveOperationException reflectiveOperationException) {
                    // empty catch block
                }
            }
        }
        level.m_7967_((Entity)living);
    }

    public static Entity owner(Entity entity) {
        if (entity instanceof Projectile) {
            Projectile projectile = (Projectile)entity;
            return projectile.m_19749_();
        }
        try {
            Entity e;
            Object value = entity.getClass().getMethod("getOwner", new Class[0]).invoke(entity, new Object[0]);
            return value instanceof Entity ? (e = (Entity)value) : null;
        }
        catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static boolean isInfernalFlame(String path) {
        return path.equals("hellfire") || path.equals("hell_bolt") || path.equals("hell_blast") || path.equals("fire_pillar");
    }
}

