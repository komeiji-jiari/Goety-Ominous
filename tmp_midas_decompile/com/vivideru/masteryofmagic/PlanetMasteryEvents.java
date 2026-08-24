/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.entities.IOwned
 *  com.Polarice3.Goety.common.effects.GoetyEffects
 *  com.Polarice3.Goety.utils.SEHelper
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.entity.MobType
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.animal.AbstractGolem
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodProperties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.common.ForgeMod
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$PlayerTickEvent
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.living.LivingEntityUseItemEvent$Finish
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.SEHelper;
import com.vivideru.masteryofmagic.MasteryData;
import com.vivideru.masteryofmagic.PlanetShapeAccess;
import java.util.UUID;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class PlanetMasteryEvents {
    private static final long CHANGE_COOLDOWN = 1200L;
    private static final UUID MAX_HEALTH = UUID.fromString("40e5b43b-cbe9-4a10-b2fb-3e5149b91611");
    private static final UUID ATTACK = UUID.fromString("126f5571-ac5e-4d3d-b028-a2445209560b");
    private static final UUID ARMOR = UUID.fromString("8cc4b2ee-bf8a-42ea-9328-76327134fbb0");
    private static final UUID GOLEM_ARMOR = UUID.fromString("cc14453d-8ec5-414d-b944-6e92d68dab21");
    private static final UUID FORM_SPEED = UUID.fromString("8bcb55df-2155-4894-a937-e3610c4e30a3");
    private static final UUID FORM_STEP = UUID.fromString("198e73ae-f3d9-46dc-b915-90e41de6fa53");
    private static final String FLIGHT_MARK = "gmomPlanetParrotFlight";
    private static final String FLIGHT_MAY = "gmomPlanetPreviousMayFly";
    private static final String FLIGHT_ACTIVE = "gmomPlanetPreviousFlying";
    private static final String FLIGHT_SPEED = "gmomPlanetPreviousFlySpeed";
    private static final String WAS_GROUNDED = "gmomPlanetWasGrounded";
    private static final String SPIDER_CLIMBING = "gmomPlanetSpiderClimbing";

    private PlanetMasteryEvents() {
    }

    public static boolean canCopy(LivingEntity entity) {
        if (entity instanceof Player || entity instanceof AbstractGolem || entity.m_6336_() == MobType.f_21641_) {
            return false;
        }
        String path = BuiltInRegistries.f_256780_.m_7981_((Object)entity.m_6095_()).m_135815_();
        return !path.contains("golem") && !path.contains("enderman") && !path.contains("endermite") && !path.contains("shulker") && !path.contains("dragon") && !path.contains("wither") && !path.contains("blaze") && !path.contains("ghast") && !path.contains("piglin") && !path.contains("hoglin") && !path.contains("strider") && !path.contains("magma_cube");
    }

    public static void copyShape(ServerPlayer player, LivingEntity target) {
        if (!MasteryData.hasSupreme((Player)player, MasteryData.SupremeSchool.PLANET)) {
            PlanetMasteryEvents.message(player, "message.goety_mastery_of_magic.planet_shape.requires_mastery", new Object[0]);
            return;
        }
        if (!PlanetMasteryEvents.canChange(player) || !PlanetMasteryEvents.canCopy(target)) {
            if (!PlanetMasteryEvents.canCopy(target)) {
                PlanetMasteryEvents.message(player, "message.goety_mastery_of_magic.planet_shape.invalid", new Object[0]);
            }
            return;
        }
        String id = BuiltInRegistries.f_256780_.m_7981_((Object)target.m_6095_()).toString();
        MasteryData.setPlanetSavedShape((Player)player, id);
        PlanetMasteryEvents.message(player, "message.goety_mastery_of_magic.planet_shape.copied", target.m_6095_().m_20676_());
        player.m_9236_().m_5594_(null, player.m_20183_(), SoundEvents.f_11887_, SoundSource.PLAYERS, 0.8f, 1.35f);
    }

    public static void toggleShape(ServerPlayer player) {
        LivingEntity shape;
        if (!PlanetMasteryEvents.hasShapePower((Player)player)) {
            PlanetMasteryEvents.message(player, "message.goety_mastery_of_magic.planet_shape.no_power", new Object[0]);
            return;
        }
        if (!PlanetMasteryEvents.canChange(player)) {
            return;
        }
        if (!MasteryData.getPlanetShape((Player)player).isBlank()) {
            PlanetMasteryEvents.leaveShape((Player)player, true, true);
            return;
        }
        String saved = MasteryData.getPlanetSavedShape((Player)player);
        if (saved.isBlank()) {
            PlanetMasteryEvents.message(player, "message.goety_mastery_of_magic.planet_shape.none_saved", new Object[0]);
            return;
        }
        Entity created = EntityType.m_20632_((String)saved).map(type -> type.m_20615_(player.m_9236_())).orElse(null);
        if (!(created instanceof LivingEntity) || !PlanetMasteryEvents.canCopy(shape = (LivingEntity)created)) {
            PlanetMasteryEvents.message(player, "message.goety_mastery_of_magic.planet_shape.invalid", new Object[0]);
            return;
        }
        MasteryData.setPlanetShape((Player)player, saved);
        ((PlanetShapeAccess)player).gmom$setPlanetShape(saved);
        PlanetMasteryEvents.applyStats((Player)player, shape);
        PlanetMasteryEvents.applyFormMovementAttributes((Player)player, shape);
        player.m_6210_();
        player.m_21153_(player.m_21233_());
        MasteryData.setPlanetShapeCooldown((Player)player, player.m_9236_().m_46467_() + 1200L);
        PlanetMasteryEvents.transformationEffects(player, true, saved);
        if (PlanetMasteryEvents.path(saved).equals("black_beast")) {
            PlanetMasteryEvents.spawnBlackWolves(player);
        }
        PlanetMasteryEvents.message(player, "message.goety_mastery_of_magic.planet_shape.assumed", shape.m_6095_().m_20676_());
    }

    public static void clearShape(Player player) {
        PlanetMasteryEvents.leaveShape(player, false, false);
    }

    private static void leaveShape(Player player, boolean cooldown, boolean effects) {
        String old = MasteryData.getPlanetShape(player);
        if (old.isBlank()) {
            return;
        }
        MasteryData.setPlanetShape(player, "");
        if (player instanceof PlanetShapeAccess) {
            PlanetShapeAccess access = (PlanetShapeAccess)player;
            access.gmom$setPlanetShape("");
        }
        PlanetMasteryEvents.removeShapeAttributes(player);
        PlanetMasteryEvents.removeSpiderClimbing(player);
        PlanetMasteryEvents.restoreFlight(player);
        player.m_6210_();
        if (player.m_21223_() > player.m_21233_()) {
            player.m_21153_(player.m_21233_());
        }
        if (cooldown) {
            MasteryData.setPlanetShapeCooldown(player, player.m_9236_().m_46467_() + 1200L);
        }
        if (effects && player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            PlanetMasteryEvents.transformationEffects(serverPlayer, false, old);
            PlanetMasteryEvents.message(serverPlayer, "message.goety_mastery_of_magic.planet_shape.released", new Object[0]);
        }
    }

    private static boolean canChange(ServerPlayer player) {
        long now = player.m_9236_().m_46467_();
        long moon = MasteryData.getPlanetShapeLock((Player)player);
        if (moon > now) {
            PlanetMasteryEvents.message(player, "message.goety_mastery_of_magic.planet_shape.moon_locked", new Object[0]);
            return false;
        }
        long cooldown = MasteryData.getPlanetShapeCooldown((Player)player);
        if (cooldown > now) {
            long seconds = Math.max(1L, (cooldown - now + 19L) / 20L);
            PlanetMasteryEvents.message(player, "message.goety_mastery_of_magic.planet_shape.cooldown", seconds);
            return false;
        }
        return true;
    }

    public static boolean hasShapePower(Player player) {
        return MasteryData.hasSupreme(player, MasteryData.SupremeSchool.PLANET) || MasteryData.hasPlanetShapeGift(player);
    }

    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event) {
        PlanetShapeAccess access;
        if (event.phase != TickEvent.Phase.END || event.player.m_9236_().f_46443_) {
            return;
        }
        Player player = event.player;
        boolean supreme = MasteryData.hasSupreme(player, MasteryData.SupremeSchool.PLANET);
        boolean power = supreme || MasteryData.hasPlanetShapeGift(player);
        String active = MasteryData.getPlanetShape(player);
        if (!power) {
            if (!active.isBlank()) {
                PlanetMasteryEvents.leaveShape(player, false, false);
            }
            PlanetMasteryEvents.removeSpiderClimbing(player);
            PlanetMasteryEvents.restoreFlight(player);
            return;
        }
        if (player instanceof PlanetShapeAccess && !(access = (PlanetShapeAccess)player).gmom$getPlanetShape().equals(active)) {
            access.gmom$setPlanetShape(active);
            player.m_6210_();
        }
        if (!active.isBlank()) {
            PlanetMasteryEvents.applySpecialAbilities(player, active);
        } else {
            PlanetMasteryEvents.removeSpiderClimbing(player);
            PlanetMasteryEvents.restoreFlight(player);
        }
        if (!supreme) {
            return;
        }
        if (player.f_19797_ % 40 == 0 && player.m_21223_() > 0.0f && player.m_21223_() < player.m_21233_()) {
            player.m_5634_(1.0f);
        }
        if (player.f_19797_ % 5 == 0) {
            for (Mob mob : player.m_9236_().m_6443_(Mob.class, new AABB(player.m_20183_()).m_82400_(64.0), PlanetMasteryEvents::naturalAlly)) {
                if (mob.m_5448_() != player) continue;
                mob.m_6710_(null);
            }
        }
        if (player.f_19797_ % 20 == 0) {
            for (LivingEntity entity : player.m_9236_().m_45976_(LivingEntity.class, new AABB(player.m_20183_()).m_82400_(128.0))) {
                AttributeInstance armor;
                if (!(entity instanceof IOwned)) continue;
                IOwned owned = (IOwned)entity;
                if (!player.m_20148_().equals(owned.getOwnerId()) || !BuiltInRegistries.f_256780_.m_7981_((Object)entity.m_6095_()).m_135815_().contains("golem") || (armor = entity.m_21051_(Attributes.f_22284_)) == null || armor.m_22111_(GOLEM_ARMOR) != null) continue;
                armor.m_22125_(new AttributeModifier(GOLEM_ARMOR, "Supreme Planet golem armor", 10.0, AttributeModifier.Operation.ADDITION));
            }
        }
    }

    private static void applySpecialAbilities(Player player, String id) {
        String path = PlanetMasteryEvents.path(id);
        EntityType type = EntityType.m_20632_((String)id).orElse(null);
        if (path.contains("spider")) {
            PlanetMasteryEvents.applySpiderClimbing(player);
        } else {
            PlanetMasteryEvents.removeSpiderClimbing(player);
        }
        if (path.equals("parrot")) {
            PlanetMasteryEvents.enableSlowFlight(player);
        } else {
            PlanetMasteryEvents.restoreFlight(player);
        }
        boolean grounded = player.m_20096_();
        boolean wasGrounded = player.getPersistentData().m_128471_(WAS_GROUNDED);
        if ((PlanetMasteryEvents.isHorse(path) || path.contains("rabbit") || path.contains("frog")) && wasGrounded && !grounded && player.m_20184_().f_82480_ > 0.05) {
            double multiplier = PlanetMasteryEvents.isHorse(path) ? 1.45 : 1.3;
            Vec3 movement = player.m_20184_();
            player.m_20334_(movement.f_82479_, movement.f_82480_ * multiplier, movement.f_82481_);
        }
        player.getPersistentData().m_128379_(WAS_GROUNDED, grounded);
        if (PlanetMasteryEvents.isAquatic(type, path)) {
            player.m_20301_(player.m_6062_());
            if (player.m_20069_()) {
                PlanetMasteryEvents.accelerateSwimming(player, path.contains("dolphin") ? 1.22 : 1.1, path.contains("dolphin") ? 1.35 : 0.85);
            }
        }
        if (path.contains("cat") || path.contains("ocelot") || path.contains("parrot") || path.contains("rabbit")) {
            player.f_19789_ = 0.0f;
        }
    }

    private static void accelerateSwimming(Player player, double multiplier, double cap) {
        Vec3 movement = player.m_20184_();
        double horizontal = Math.sqrt(movement.f_82479_ * movement.f_82479_ + movement.f_82481_ * movement.f_82481_);
        if (horizontal < 1.0E-4) {
            return;
        }
        double scale = Math.min(multiplier, cap / horizontal);
        player.m_20334_(movement.f_82479_ * scale, movement.f_82480_, movement.f_82481_ * scale);
    }

    private static void applyStats(Player player, LivingEntity shape) {
        PlanetMasteryEvents.remove((LivingEntity)player, Attributes.f_22276_, MAX_HEALTH);
        PlanetMasteryEvents.remove((LivingEntity)player, Attributes.f_22281_, ATTACK);
        PlanetMasteryEvents.remove((LivingEntity)player, Attributes.f_22284_, ARMOR);
        boolean inheritedForm = !MasteryData.hasSupreme(player, MasteryData.SupremeSchool.PLANET) && MasteryData.hasPlanetShapeGift(player);
        double shapeHealth = shape.m_21172_(Attributes.f_22276_);
        double formHealth = inheritedForm ? Math.min(60.0, shapeHealth * 0.5) : shapeHealth;
        PlanetMasteryEvents.add((LivingEntity)player, Attributes.f_22276_, MAX_HEALTH, formHealth - player.m_21172_(Attributes.f_22276_));
        PlanetMasteryEvents.add((LivingEntity)player, Attributes.f_22281_, ATTACK, shape.m_21172_(Attributes.f_22281_) - player.m_21172_(Attributes.f_22281_));
        PlanetMasteryEvents.add((LivingEntity)player, Attributes.f_22284_, ARMOR, shape.m_21172_(Attributes.f_22284_) - player.m_21172_(Attributes.f_22284_));
    }

    private static void applyFormMovementAttributes(Player player, LivingEntity shape) {
        EntityType type;
        String path;
        PlanetMasteryEvents.remove((LivingEntity)player, Attributes.f_22279_, FORM_SPEED);
        AttributeInstance step = player.m_21051_((Attribute)ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (step != null) {
            step.m_22120_(FORM_STEP);
        }
        double speed = PlanetMasteryEvents.isHorse(path = BuiltInRegistries.f_256780_.m_7981_((Object)(type = shape.m_6095_())).m_135815_()) ? 0.08 : (path.contains("rabbit") ? 0.04 : 0.0);
        PlanetMasteryEvents.add((LivingEntity)player, Attributes.f_22279_, FORM_SPEED, speed);
        EntityDimensions dimensions = type.m_20680_();
        double desiredStep = PlanetMasteryEvents.desiredStepHeight(dimensions, shape.m_274421_());
        double stepBonus = Math.max(0.0, desiredStep - (double)player.m_274421_());
        if (step != null && stepBonus > 0.0) {
            step.m_22125_(new AttributeModifier(FORM_STEP, "Large animal step height", stepBonus, AttributeModifier.Operation.ADDITION));
        }
    }

    private static double desiredStepHeight(EntityDimensions dimensions, double nativeStep) {
        double bySize = dimensions.f_20377_ >= 2.5f || dimensions.f_20378_ >= 3.5f ? 2.0 : (dimensions.f_20377_ >= 1.5f || dimensions.f_20378_ >= 2.4f ? 1.5 : (dimensions.f_20377_ >= 1.0f || dimensions.f_20378_ >= 1.8f ? 1.0 : 0.6));
        return Math.max(nativeStep, bySize);
    }

    private static void applySpiderClimbing(Player player) {
        MobEffectInstance current;
        if (!player.m_21023_((MobEffect)GoetyEffects.CLIMBING.get())) {
            player.m_7292_(new MobEffectInstance((MobEffect)GoetyEffects.CLIMBING.get(), 12, 0, false, false, false));
            player.getPersistentData().m_128379_(SPIDER_CLIMBING, true);
        } else if (player.getPersistentData().m_128471_(SPIDER_CLIMBING) && (current = player.m_21124_((MobEffect)GoetyEffects.CLIMBING.get())) != null && current.m_19557_() < 6) {
            player.m_7292_(new MobEffectInstance((MobEffect)GoetyEffects.CLIMBING.get(), 12, 0, false, false, false));
        }
    }

    private static void removeSpiderClimbing(Player player) {
        if (!player.getPersistentData().m_128471_(SPIDER_CLIMBING)) {
            return;
        }
        player.m_21195_((MobEffect)GoetyEffects.CLIMBING.get());
        player.getPersistentData().m_128473_(SPIDER_CLIMBING);
    }

    private static void removeShapeAttributes(Player player) {
        PlanetMasteryEvents.remove((LivingEntity)player, Attributes.f_22276_, MAX_HEALTH);
        PlanetMasteryEvents.remove((LivingEntity)player, Attributes.f_22281_, ATTACK);
        PlanetMasteryEvents.remove((LivingEntity)player, Attributes.f_22284_, ARMOR);
        PlanetMasteryEvents.remove((LivingEntity)player, Attributes.f_22279_, FORM_SPEED);
        AttributeInstance step = player.m_21051_((Attribute)ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (step != null) {
            step.m_22120_(FORM_STEP);
        }
    }

    private static void add(LivingEntity entity, Attribute attribute, UUID id, double value) {
        AttributeInstance instance = entity.m_21051_(attribute);
        if (instance != null && Math.abs(value) > 0.001) {
            instance.m_22125_(new AttributeModifier(id, "Supreme Planet shape", value, AttributeModifier.Operation.ADDITION));
        }
    }

    private static void remove(LivingEntity entity, Attribute attribute, UUID id) {
        AttributeInstance instance = entity.m_21051_(attribute);
        if (instance != null) {
            instance.m_22120_(id);
        }
    }

    private static void enableSlowFlight(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.m_128471_(FLIGHT_MARK)) {
            data.m_128379_(FLIGHT_MARK, true);
            data.m_128379_(FLIGHT_MAY, player.m_150110_().f_35936_);
            data.m_128379_(FLIGHT_ACTIVE, player.m_150110_().f_35935_);
            data.m_128350_(FLIGHT_SPEED, player.m_150110_().m_35942_());
        }
        player.m_150110_().f_35936_ = true;
        player.m_150110_().m_35943_(0.035f);
        player.m_6885_();
    }

    private static void restoreFlight(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.m_128471_(FLIGHT_MARK)) {
            return;
        }
        player.m_150110_().f_35936_ = data.m_128471_(FLIGHT_MAY);
        player.m_150110_().f_35935_ = data.m_128471_(FLIGHT_ACTIVE) && player.m_150110_().f_35936_;
        player.m_150110_().m_35943_(data.m_128457_(FLIGHT_SPEED));
        data.m_128473_(FLIGHT_MARK);
        data.m_128473_(FLIGHT_MAY);
        data.m_128473_(FLIGHT_ACTIVE);
        data.m_128473_(FLIGHT_SPEED);
        player.m_6885_();
    }

    private static void transformationEffects(ServerPlayer player, boolean transform, String shape) {
        ServerLevel level = player.m_284548_();
        level.m_8767_((ParticleOptions)ParticleTypes.f_123759_, player.m_20185_(), player.m_20186_() + (double)player.m_20206_() * 0.5, player.m_20189_(), 45, (double)player.m_20205_() * 0.65, (double)player.m_20206_() * 0.45, (double)player.m_20205_() * 0.65, 0.06);
        level.m_8767_((ParticleOptions)ParticleTypes.f_123760_, player.m_20185_(), player.m_20186_() + (double)player.m_20206_() * 0.5, player.m_20189_(), 70, (double)player.m_20205_() * 0.75, (double)player.m_20206_() * 0.5, (double)player.m_20205_() * 0.75, transform ? 0.12 : 0.04);
        level.m_5594_(null, player.m_20183_(), SoundEvents.f_12052_, SoundSource.PLAYERS, 1.15f, transform ? 0.72f : 1.25f);
        level.m_5594_(null, player.m_20183_(), transform ? SoundEvents.f_11862_ : SoundEvents.f_11852_, SoundSource.PLAYERS, 0.9f, transform ? 0.85f : 1.2f);
        if (transform && PlanetMasteryEvents.path(shape).equals("black_beast")) {
            level.m_5594_(null, player.m_20183_(), SoundEvents.f_12620_, SoundSource.PLAYERS, 1.8f, 0.72f);
        }
    }

    private static void spawnBlackWolves(ServerPlayer owner) {
        EntityType type = (EntityType)BuiltInRegistries.f_256780_.m_7745_(new ResourceLocation("goety", "black_wolf"));
        if (type == null) {
            return;
        }
        for (int i = 0; i < 2; ++i) {
            Entity made = type.m_20615_((Level)owner.m_284548_());
            if (!(made instanceof LivingEntity)) continue;
            LivingEntity wolf = (LivingEntity)made;
            double angle = (double)i * Math.PI;
            wolf.m_7678_(owner.m_20185_() + Math.cos(angle) * 1.5, owner.m_20186_(), owner.m_20189_() + Math.sin(angle) * 1.5, owner.m_146908_(), 0.0f);
            try {
                wolf.getClass().getMethod("setTrueOwner", LivingEntity.class).invoke(wolf, owner);
            }
            catch (ReflectiveOperationException ignored) {
                try {
                    wolf.getClass().getMethod("setOwnerId", UUID.class).invoke(wolf, owner.m_20148_());
                }
                catch (ReflectiveOperationException reflectiveOperationException) {
                    // empty catch block
                }
            }
            owner.m_284548_().m_7967_((Entity)wolf);
        }
    }

    @SubscribeEvent
    public static void food(LivingEntityUseItemEvent.Finish event) {
        Player player;
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player) || !MasteryData.hasSupreme(player = (Player)livingEntity, MasteryData.SupremeSchool.PLANET)) {
            return;
        }
        ItemStack stack = event.getItem();
        FoodProperties food = stack.getFoodProperties((LivingEntity)player);
        if (food != null) {
            SEHelper.increaseSouls((Player)player, (int)(food.m_38744_() * 10));
        }
    }

    @SubscribeEvent
    public static void death(LivingDeathEvent event) {
        Player player;
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player) || MasteryData.getPlanetShape(player = (Player)livingEntity).isBlank() || !PlanetMasteryEvents.hasShapePower(player)) {
            return;
        }
        event.setCanceled(true);
        String old = MasteryData.getPlanetShape(player);
        PlanetMasteryEvents.leaveShape(player, false, false);
        player.m_21153_(player.m_21233_());
        MasteryData.setPlanetShapeCooldown(player, 0L);
        MasteryData.setPlanetShapeLock(player, PlanetMasteryEvents.nextNewMoon(player));
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            PlanetMasteryEvents.transformationEffects(serverPlayer, false, old);
            PlanetMasteryEvents.message(serverPlayer, "message.goety_mastery_of_magic.planet_shape.broken", new Object[0]);
        }
    }

    @SubscribeEvent
    public static void attack(LivingAttackEvent event) {
        LivingEntity attacker;
        Player player;
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Player && MasteryData.hasSupreme(player = (Player)livingEntity, MasteryData.SupremeSchool.PLANET) && (livingEntity = event.getSource().m_7639_()) instanceof LivingEntity && PlanetMasteryEvents.naturalAlly(attacker = livingEntity)) {
            event.setCanceled(true);
        }
    }

    private static boolean naturalAlly(LivingEntity entity) {
        MobCategory category = entity.m_6095_().m_20674_();
        String path = BuiltInRegistries.f_256780_.m_7981_((Object)entity.m_6095_()).m_135815_();
        return entity.m_6336_() == MobType.f_21642_ || path.contains("guardian") || category == MobCategory.CREATURE || category == MobCategory.WATER_CREATURE || category == MobCategory.WATER_AMBIENT || category == MobCategory.AXOLOTLS || category == MobCategory.UNDERGROUND_WATER_CREATURE;
    }

    private static boolean isAquatic(EntityType<?> type, String path) {
        MobCategory category;
        if (type != null && ((category = type.m_20674_()) == MobCategory.WATER_CREATURE || category == MobCategory.WATER_AMBIENT || category == MobCategory.AXOLOTLS || category == MobCategory.UNDERGROUND_WATER_CREATURE)) {
            return true;
        }
        return path.contains("dolphin") || path.contains("guardian") || path.contains("fish") || path.contains("squid") || path.contains("turtle") || path.contains("axolotl") || path.contains("frog");
    }

    private static boolean isHorse(String path) {
        return path.contains("horse") || path.contains("donkey") || path.contains("mule") || path.contains("camel");
    }

    private static String path(String id) {
        int split = id.indexOf(58);
        return split >= 0 ? id.substring(split + 1) : id;
    }

    private static long nextNewMoon(Player player) {
        long time = player.m_9236_().m_46468_();
        long day = Math.floorDiv(time, 24000L);
        int phase = player.m_9236_().m_46941_();
        int delta = (4 - phase + 8) % 8;
        if (delta == 0) {
            delta = 8;
        }
        long remaining = (day + (long)delta) * 24000L - time;
        return player.m_9236_().m_46467_() + Math.max(1L, remaining);
    }

    private static void message(ServerPlayer player, String key, Object ... args) {
        player.m_5661_((Component)Component.m_237110_((String)key, (Object[])args), true);
    }
}

