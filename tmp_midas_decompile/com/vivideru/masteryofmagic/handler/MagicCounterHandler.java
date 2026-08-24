/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.damagesource.DamageType
 *  net.minecraft.world.damagesource.DamageTypes
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.vivideru.masteryofmagic.handler;

import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModSounds;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic")
public final class MagicCounterHandler {
    private static final int MAX_RENEWALS = 7;
    private static final double COUNTER_RANGE = 64.0;
    private static final Map<UUID, CounterState> ACTIVE = new LinkedHashMap<UUID, CounterState>();

    private MagicCounterHandler() {
    }

    public static void activate(LivingEntity caster, int windowTicks) {
        Level level = caster.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        CounterState previous = ACTIVE.remove(caster.m_20148_());
        if (previous != null && !previous.absorbed.isEmpty()) {
            MagicCounterHandler.release(level2, caster, previous);
        }
        ACTIVE.put(caster.m_20148_(), new CounterState(Math.max(1, windowTicks), level2.m_46467_() + (long)Math.max(1, windowTicks)));
        level2.m_8767_((ParticleOptions)ParticleTypes.f_123809_, caster.m_20185_(), caster.m_20227_(0.55), caster.m_20189_(), 22, 0.45, 0.75, 0.45, 0.18);
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity caster = event.getEntity();
        Level level = caster.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        CounterState state = ACTIVE.get(caster.m_20148_());
        if (state == null || level2.m_46467_() >= state.expiresAt) {
            return;
        }
        event.setCanceled(true);
        DamageSource source = event.getSource();
        if (!MagicCounterHandler.isReflectableMagic(source)) {
            return;
        }
        ResourceKey type = source.m_269150_().m_203543_().orElse(DamageTypes.f_268515_);
        AccumulatedDamage accumulated = state.absorbed.computeIfAbsent((ResourceKey<DamageType>)type, ignored -> new AccumulatedDamage(MagicCounterHandler.particleKind(source)));
        accumulated.amount += Math.max(0.0f, event.getAmount());
        if (state.renewals < 7) {
            ++state.renewals;
            state.expiresAt = level2.m_46467_() + (long)state.windowTicks;
        }
        level2.m_5594_(null, caster.m_20183_(), (SoundEvent)GoetyMasteryOfMagicModSounds.MAGIC_COUNTER_PARRY.get(), SoundSource.PLAYERS, 1.35f, 0.94f + level2.f_46441_.m_188501_() * 0.12f);
        MagicCounterHandler.spawnParryParticles(level2, caster, source);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ACTIVE.isEmpty()) {
            return;
        }
        for (Map.Entry<UUID, CounterState> entry : new ArrayList<Map.Entry<UUID, CounterState>>(ACTIVE.entrySet())) {
            LivingEntity caster = null;
            ServerLevel casterLevel = null;
            for (ServerLevel level : event.getServer().m_129785_()) {
                LivingEntity living;
                Entity entity = level.m_8791_(entry.getKey());
                if (!(entity instanceof LivingEntity)) continue;
                caster = living = (LivingEntity)entity;
                casterLevel = level;
                break;
            }
            if (caster == null || casterLevel == null) {
                ACTIVE.remove(entry.getKey());
                continue;
            }
            CounterState state = entry.getValue();
            if (casterLevel.m_46467_() < state.expiresAt && caster.m_6084_()) continue;
            ACTIVE.remove(entry.getKey());
            if (!caster.m_6084_() || state.absorbed.isEmpty()) continue;
            MagicCounterHandler.release(casterLevel, caster, state);
        }
    }

    private static boolean isReflectableMagic(DamageSource source) {
        Entity direct = source.m_7640_();
        if (source.m_269533_(DamageTypeTags.f_268415_) || source.m_269533_(DamageTypeTags.f_268745_) || source.m_276093_(DamageTypes.f_268450_) || source.m_276093_(DamageTypes.f_268515_) || source.m_276093_(DamageTypes.f_268530_) || source.m_276093_(DamageTypes.f_268482_) || source.m_276093_(DamageTypes.f_268679_) || source.m_276093_(DamageTypes.f_268444_) || source.m_276093_(DamageTypes.f_268493_)) {
            return true;
        }
        String key = source.m_269150_().m_203543_().map(ResourceKey::m_135782_).map(ResourceLocation::toString).orElse("").toLowerCase(Locale.ROOT);
        return key.contains("magic") || key.contains("spell") || key.contains("element") || key.contains("fire") || key.contains("flame") || key.contains("lava") || key.contains("lightning") || key.contains("thunder") || key.contains("shock") || key.contains("frost") || key.contains("freeze") || key.contains("ice") || key.contains("explosion") || key.contains("blast") || key.contains("beam") || key.contains("sonic") || key.contains("wither") || key.contains("acid");
    }

    private static void release(ServerLevel level, LivingEntity caster, CounterState state) {
        LivingEntity target = MagicCounterHandler.findLookTarget(level, caster);
        if (target == null) {
            return;
        }
        Vec3 start = caster.m_146892_().m_82549_(caster.m_20154_().m_82490_(0.7));
        Vec3 end = target.m_20191_().m_82399_();
        int index = 0;
        for (Map.Entry<ResourceKey<DamageType>, AccumulatedDamage> entry : state.absorbed.entrySet()) {
            Holder.Reference holder = level.m_9598_().m_175515_(Registries.f_268580_).m_246971_(entry.getKey());
            DamageSource reflected = new DamageSource((Holder)holder, (Entity)caster);
            target.f_19802_ = 0;
            target.m_6469_(reflected, entry.getValue().amount);
            target.f_19802_ = 0;
            MagicCounterHandler.spawnCounterTrail(level, start, end, entry.getValue().particle.options(), index++);
        }
        level.m_5594_(null, caster.m_20183_(), (SoundEvent)GoetyMasteryOfMagicModSounds.MAGIC_COUNTER_PARRY.get(), SoundSource.PLAYERS, 1.5f, 1.18f);
    }

    private static LivingEntity findLookTarget(ServerLevel level, LivingEntity caster) {
        Vec3 eye = caster.m_146892_();
        Vec3 look = caster.m_20154_().m_82541_();
        Vec3 end = eye.m_82549_(look.m_82490_(64.0));
        AABB search = caster.m_20191_().m_82369_(look.m_82490_(64.0)).m_82400_(3.0);
        return level.m_6443_(LivingEntity.class, search, entity -> entity != caster && entity.m_6084_() && !entity.m_5833_()).stream().filter(entity -> entity.m_20191_().m_82400_(1.5).m_82371_(eye, end).isPresent()).filter(arg_0 -> ((LivingEntity)caster).m_142582_(arg_0)).min(Comparator.comparingDouble(entity -> eye.m_82557_(entity.m_20191_().m_82400_(1.5).m_82371_(eye, end).orElse(entity.m_20182_())))).orElse(null);
    }

    private static void spawnParryParticles(ServerLevel level, LivingEntity caster, DamageSource source) {
        ParticleOptions particle = MagicCounterHandler.particleKind(source).options();
        level.m_8767_(particle, caster.m_20185_(), caster.m_20227_(0.55), caster.m_20189_(), 28, 0.55, 0.8, 0.55, 0.22);
    }

    private static void spawnCounterTrail(ServerLevel level, Vec3 start, Vec3 end, ParticleOptions particle, int offset) {
        Vec3 delta = end.m_82546_(start);
        int steps = Math.max(6, Math.min(48, (int)(delta.m_82553_() * 1.5)));
        for (int i = 0; i <= steps; ++i) {
            Vec3 point = start.m_82549_(delta.m_82490_((double)i / (double)steps));
            level.m_8767_(particle, point.f_82479_, point.f_82480_ + (double)offset * 0.025, point.f_82481_, 1, 0.035, 0.035, 0.035, 0.0);
        }
    }

    private static ParticleKind particleKind(DamageSource source) {
        String path = source.m_269150_().m_203543_().map(ResourceKey::m_135782_).map(ResourceLocation::m_135815_).orElse("").toLowerCase(Locale.ROOT);
        if (path.contains("fire") || path.contains("flame") || path.contains("lava")) {
            return ParticleKind.FIRE;
        }
        if (path.contains("lightning") || path.contains("thunder") || path.contains("shock")) {
            return ParticleKind.LIGHTNING;
        }
        if (path.contains("ice") || path.contains("frost") || path.contains("freeze")) {
            return ParticleKind.FROST;
        }
        if (path.contains("explosion") || path.contains("blast")) {
            return ParticleKind.EXPLOSION;
        }
        if (path.contains("wither") || path.contains("void")) {
            return ParticleKind.VOID;
        }
        return ParticleKind.MAGIC;
    }

    private static final class CounterState {
        private final int windowTicks;
        private long expiresAt;
        private int renewals;
        private final Map<ResourceKey<DamageType>, AccumulatedDamage> absorbed = new LinkedHashMap<ResourceKey<DamageType>, AccumulatedDamage>();

        private CounterState(int windowTicks, long expiresAt) {
            this.windowTicks = windowTicks;
            this.expiresAt = expiresAt;
        }
    }

    private static final class AccumulatedDamage {
        private float amount;
        private final ParticleKind particle;

        private AccumulatedDamage(ParticleKind particle) {
            this.particle = particle;
        }
    }

    private static enum ParticleKind {
        FIRE,
        LIGHTNING,
        FROST,
        EXPLOSION,
        VOID,
        MAGIC;


        private ParticleOptions options() {
            return switch (this) {
                default -> throw new IncompatibleClassChangeError();
                case FIRE -> ParticleTypes.f_123744_;
                case LIGHTNING -> ParticleTypes.f_175830_;
                case FROST -> ParticleTypes.f_175821_;
                case EXPLOSION -> ParticleTypes.f_123759_;
                case VOID -> ParticleTypes.f_123789_;
                case MAGIC -> ParticleTypes.f_123771_;
            };
        }
    }
}

