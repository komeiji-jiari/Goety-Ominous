/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.entities.ModEntityType
 *  com.Polarice3.Goety.common.entities.ally.undead.skeleton.NecromancerServant
 *  com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer
 *  com.Polarice3.Goety.common.entities.neutral.Owned
 *  com.Polarice3.Goety.common.entities.projectiles.NecroBolt
 *  com.Polarice3.Goety.common.items.ModItems
 *  com.Polarice3.Goety.init.ModSounds
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.control.FlyingMoveControl
 *  net.minecraft.world.entity.ai.goal.Goal
 *  net.minecraft.world.entity.ai.goal.Goal$Flag
 *  net.minecraft.world.entity.ai.navigation.FlyingPathNavigation
 *  net.minecraft.world.entity.ai.navigation.PathNavigation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.phys.Vec3
 */
package com.vivideru.masteryofmagic.entity.necromancer;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.NecromancerServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.NecroBolt;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.init.ModSounds;
import com.vivideru.masteryofmagic.config.SpellConfig;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class NamelessNecromancerServant
extends NecromancerServant {
    private static final double BASE_MAX_HEALTH = 350.0;
    private static final double HEALTH_REDUCTION_FACTOR = 0.6666666666666666;
    private static final double RETREAT_DISTANCE_MULTIPLIER = 1.35;
    private static final double BASE_ARMOR = 5.0;
    private static final double RETREAT_FLYING_SPEED = 0.48;
    private static final double RETREAT_MOVEMENT_SPEED = 0.38;
    private static final int APOSTLE_REGENERATION_INTERVAL = 400;
    private static final int CLONE_COUNT = 4;
    private static final int CLONE_CHARGE_TICKS = 30;
    private static final int CLONE_COOLDOWN_TICKS = 360;
    private static final int CLONE_LIFESPAN_TICKS = 400;
    private static final int SUMMON_CHARGE_TICKS = 30;
    private static final int SUMMON_COOLDOWN_TICKS = 260;
    private int cloneCooldown;
    private int armyCooldown;
    private int antiRegenerationTicks;
    private boolean decoyClone;
    private UUID cloneSourceUuid;

    public NamelessNecromancerServant(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
        this.f_21342_ = new FlyingMoveControl((Mob)this, 20, true);
        this.m_20242_(true);
        this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)ModItems.NAMELESS_STAFF.get()));
        this.m_21409_(EquipmentSlot.MAINHAND, 0.0f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractNecromancer.setCustomAttributes().m_22268_(Attributes.f_22276_, 233.33333333333331).m_22268_(Attributes.f_22284_, 5.0).m_22268_(Attributes.f_22280_, 0.48).m_22268_(Attributes.f_22279_, 0.38).m_22268_(Attributes.f_22277_, 96.0);
    }

    protected PathNavigation m_6037_(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation((Mob)this, level);
        navigation.m_26440_(false);
        navigation.m_7008_(true);
        navigation.m_26443_(true);
        return navigation;
    }

    protected void m_8099_() {
        super.m_8099_();
        this.f_21345_.m_25352_(1, (Goal)new NamelessCloneGoal());
        this.f_21345_.m_25352_(2, (Goal)new NamelessArmyGoal());
    }

    public void summonSpells(int priority) {
    }

    public void m_8119_() {
        super.m_8119_();
        this.m_20242_(true);
        this.f_19789_ = 0.0f;
        if (!this.m_9236_().f_46443_) {
            if (this.antiRegenerationTicks > 0) {
                --this.antiRegenerationTicks;
            } else if (!this.decoyClone && this.f_19797_ % 400 == 0 && this.m_21223_() < this.m_21233_()) {
                this.m_5634_(1.0f);
            }
            if (this.cloneCooldown > 0) {
                --this.cloneCooldown;
            }
            if (this.armyCooldown > 0) {
                --this.armyCooldown;
            }
            if (this.f_19797_ == 1 || this.f_19797_ % 100 == 0) {
                this.applyConfigurableAttributes();
            }
            if (this.decoyClone && !this.hasLivingCloneSource()) {
                this.m_146870_();
                return;
            }
            this.maintainConfiguredCombatDistance();
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean hasLivingCloneSource() {
        if (this.cloneSourceUuid == null) return false;
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) return false;
        ServerLevel serverLevel = (ServerLevel)level;
        Entity source = serverLevel.m_8791_(this.cloneSourceUuid);
        if (!(source instanceof NamelessNecromancerServant)) return false;
        NamelessNecromancerServant necromancer = (NamelessNecromancerServant)source;
        if (necromancer.decoyClone) return false;
        if (!necromancer.m_6084_()) return false;
        if (necromancer.m_213877_()) return false;
        return true;
    }

    private void maintainConfiguredCombatDistance() {
        boolean mustRetreat;
        LivingEntity target = this.m_5448_();
        if (target == null || !target.m_6084_()) {
            return;
        }
        this.m_21573_().m_26573_();
        Vec3 away = this.m_20182_().m_82546_(target.m_20182_()).m_82542_(1.0, 0.0, 1.0);
        if (away.m_82556_() < 1.0E-4) {
            away = new Vec3(1.0, 0.0, 0.0);
        }
        double desiredDistance = this.configuredCombatDistance() * 1.35;
        Vec3 desired = target.m_20182_().m_82549_(away.m_82541_().m_82490_(desiredDistance));
        double dx = this.m_20185_() - target.m_20185_();
        double dz = this.m_20189_() - target.m_20189_();
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        Vec3 awayDirection = away.m_82541_();
        Vec3 movement = this.m_20184_();
        Vec3 horizontalMovement = new Vec3(movement.f_82479_, 0.0, movement.f_82481_);
        double outwardSpeed = horizontalMovement.m_82526_(awayDirection);
        if (outwardSpeed < 0.0) {
            horizontalMovement = horizontalMovement.m_82546_(awayDirection.m_82490_(outwardSpeed));
            this.m_20334_(horizontalMovement.f_82479_, movement.f_82480_, horizontalMovement.f_82481_);
        }
        boolean bl = mustRetreat = horizontalDistance < desiredDistance - 1.0 || this.isSpellCasting();
        if (!mustRetreat) {
            this.m_21566_().m_6849_(this.m_20185_(), target.m_20186_() + 4.0, this.m_20189_(), 1.0);
            return;
        }
        double retreatDistance = Math.max(desiredDistance, horizontalDistance + (this.isSpellCasting() ? 5.0 : 2.0));
        desired = target.m_20182_().m_82549_(awayDirection.m_82490_(retreatDistance));
        double speed = horizontalDistance < desiredDistance - 1.0 ? 1.85 : 1.35;
        this.m_21566_().m_6849_(desired.f_82479_, target.m_20186_() + 4.0, desired.f_82481_, speed);
    }

    private void applyConfigurableAttributes() {
        double maximumHealth;
        int level = Mth.m_14045_((int)this.getNecroLevel(), (int)0, (int)2);
        double d = maximumHealth = this.decoyClone ? 1.0 : this.configuredBaseHealth() * 0.6666666666666666 * (1.0 + (double)level * 0.25);
        if (this.m_21051_(Attributes.f_22276_) != null) {
            this.m_21051_(Attributes.f_22276_).m_22100_(maximumHealth);
            if ((double)this.m_21223_() > maximumHealth) {
                this.m_21153_((float)maximumHealth);
            }
        }
        if (this.m_21051_(Attributes.f_22284_) != null) {
            this.m_21051_(Attributes.f_22284_).m_22100_(5.0);
        }
        if (this.m_21051_(Attributes.f_22277_) != null) {
            this.m_21051_(Attributes.f_22277_).m_22100_(this.configuredFollowRange());
        }
        if (this.m_21051_(Attributes.f_22280_) != null) {
            this.m_21051_(Attributes.f_22280_).m_22100_(0.48);
        }
        if (this.m_21051_(Attributes.f_22279_) != null) {
            this.m_21051_(Attributes.f_22279_).m_22100_(0.38);
        }
    }

    private double configuredBaseHealth() {
        try {
            return (Double)SpellConfig.NAMELESS_NECROMANCER_BASE_HEALTH.get();
        }
        catch (IllegalStateException ignored) {
            return 350.0;
        }
    }

    private double configuredCombatDistance() {
        try {
            return (Double)SpellConfig.NAMELESS_NECROMANCER_COMBAT_DISTANCE.get();
        }
        catch (IllegalStateException ignored) {
            return 20.0;
        }
    }

    private double configuredFollowRange() {
        try {
            return (Double)SpellConfig.NAMELESS_NECROMANCER_FOLLOW_RANGE.get();
        }
        catch (IllegalStateException ignored) {
            return 96.0;
        }
    }

    public boolean m_142535_(float distance, float multiplier, DamageSource source) {
        return false;
    }

    public void soulJar() {
    }

    public void setNecroLevel(int level) {
        super.setNecroLevel(level);
        int clamped = Mth.m_14045_((int)level, (int)0, (int)2);
        if (this.m_21051_(Attributes.f_22276_) != null) {
            this.m_21051_(Attributes.f_22276_).m_22100_(this.decoyClone ? 1.0 : this.configuredBaseHealth() * 0.6666666666666666 * (1.0 + (double)clamped * 0.25));
        }
        if (this.m_21051_(Attributes.f_22284_) != null) {
            this.m_21051_(Attributes.f_22284_).m_22100_(5.0);
        }
        if (this.m_21051_(Attributes.f_22277_) != null) {
            this.m_21051_(Attributes.f_22277_).m_22100_(this.configuredFollowRange());
        }
        if (this.m_21051_(Attributes.f_22280_) != null) {
            this.m_21051_(Attributes.f_22280_).m_22100_(0.48);
        }
        if (this.m_21051_(Attributes.f_22279_) != null) {
            this.m_21051_(Attributes.f_22279_).m_22100_(0.38);
        }
    }

    public void m_6504_(LivingEntity target, float distanceFactor) {
        if (this.m_9236_().f_46443_ || target == null) {
            return;
        }
        Vec3 direction = target.m_146892_().m_82546_(this.m_146892_()).m_82541_();
        NecroBolt bolt = new NecroBolt((LivingEntity)this, direction.f_82479_, direction.f_82480_, direction.f_82481_, this.m_9236_());
        bolt.setExtraDamage(2.0f + (float)this.getNecroLevel() * 1.5f);
        this.m_9236_().m_7967_((Entity)bolt);
        this.m_5496_((SoundEvent)ModSounds.NECRO_CAST.get(), 1.0f, 0.86f + this.f_19796_.m_188501_() * 0.12f);
    }

    public boolean m_6469_(DamageSource source, float amount) {
        LivingEntity attacker;
        int smite;
        Entity entity = source.m_7640_();
        if (entity instanceof LivingEntity && (smite = EnchantmentHelper.m_44836_((Enchantment)Enchantments.f_44978_, (LivingEntity)(attacker = (LivingEntity)entity))) > 0) {
            this.antiRegenerationTicks = Mth.m_14045_((int)smite, (int)1, (int)5) * 20;
        }
        return super.m_6469_(source, amount);
    }

    public void m_5634_(float amount) {
        if (this.antiRegenerationTicks <= 0) {
            super.m_5634_(amount);
        }
    }

    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        tag.m_128405_("NamelessCloneCooldown", this.cloneCooldown);
        tag.m_128405_("NamelessArmyCooldown", this.armyCooldown);
        tag.m_128405_("NamelessAntiRegeneration", this.antiRegenerationTicks);
        tag.m_128379_("NamelessDecoyClone", this.decoyClone);
        if (this.cloneSourceUuid != null) {
            tag.m_128362_("NamelessCloneSource", this.cloneSourceUuid);
        }
    }

    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        this.cloneCooldown = Math.max(0, tag.m_128451_("NamelessCloneCooldown"));
        this.armyCooldown = Math.max(0, tag.m_128451_("NamelessArmyCooldown"));
        this.antiRegenerationTicks = Math.max(0, tag.m_128451_("NamelessAntiRegeneration"));
        this.decoyClone = tag.m_128471_("NamelessDecoyClone");
        this.cloneSourceUuid = tag.m_128403_("NamelessCloneSource") ? tag.m_128342_("NamelessCloneSource") : null;
        this.applyConfigurableAttributes();
    }

    private void setDecoyClone(UUID sourceUuid) {
        this.decoyClone = true;
        this.cloneSourceUuid = sourceUuid;
        this.cloneCooldown = Integer.MAX_VALUE;
        this.armyCooldown = Integer.MAX_VALUE;
        this.applyConfigurableAttributes();
        this.m_21153_(1.0f);
    }

    private void summonDecoyClones(ServerLevel level, LivingEntity target) {
        level.m_6443_(NamelessNecromancerServant.class, this.m_20191_().m_82400_(this.configuredFollowRange()), candidate -> candidate.decoyClone && this.m_20148_().equals(candidate.cloneSourceUuid)).forEach(Entity::m_146870_);
        ArrayList<NamelessNecromancerServant> clones = new ArrayList<NamelessNecromancerServant>();
        double initialAngle = this.f_19796_.m_188500_() * 6.2831854820251465;
        for (int index = 0; index < 4; ++index) {
            NamelessNecromancerServant clone = (NamelessNecromancerServant)((EntityType)GoetyMasteryOfMagicModEntities.NAMELESS_NECROMANCER.get()).m_20615_((Level)level);
            if (clone == null) continue;
            double angle = initialAngle + (double)((float)Math.PI * 2 * (float)index / 4.0f);
            double radius = 8.0 + (double)(index % 2) * 1.5;
            BlockPos spawnPos = BlockPos.m_274561_((double)(this.m_20185_() + Math.cos(angle) * radius), (double)(this.m_20186_() + (double)(index % 2) * 0.75), (double)(this.m_20189_() + Math.sin(angle) * radius));
            clone.m_7678_((double)spawnPos.m_123341_() + 0.5, spawnPos.m_123342_(), (double)spawnPos.m_123343_() + 0.5, this.m_146908_(), this.m_146909_());
            clone.m_6518_((ServerLevelAccessor)level, level.m_6436_(spawnPos), MobSpawnType.MOB_SUMMONED, null, null);
            clone.setNecroLevel(this.getNecroLevel());
            clone.setDecoyClone(this.m_20148_());
            LivingEntity master = this.getTrueOwner();
            clone.setTrueOwner((LivingEntity)(master != null ? master : this));
            clone.setHasLifespan(true);
            clone.setLifespan(400);
            if (target != null && target.m_6084_()) {
                clone.m_6710_(target);
            }
            level.m_7967_((Entity)clone);
            clones.add(clone);
            level.m_8767_((ParticleOptions)ParticleTypes.f_123745_, clone.m_20185_(), clone.m_20186_() + 1.0, clone.m_20189_(), 18, 0.45, 0.8, 0.45, 0.025);
        }
        this.redirectAggroToNearestClone(level, clones);
    }

    private void redirectAggroToNearestClone(ServerLevel level, List<NamelessNecromancerServant> clones) {
        if (clones.isEmpty()) {
            return;
        }
        List attackers = level.m_6443_(Mob.class, this.m_20191_().m_82400_(this.configuredFollowRange()), mob -> mob.m_6084_() && mob.m_5448_() == this);
        for (Mob attacker : attackers) {
            NamelessNecromancerServant nearest = null;
            double nearestDistance = Double.MAX_VALUE;
            for (NamelessNecromancerServant clone : clones) {
                double distance = attacker.m_20280_((Entity)clone);
                if (!clone.m_6084_() || !(distance < nearestDistance)) continue;
                nearest = clone;
                nearestDistance = distance;
            }
            if (nearest == null) continue;
            attacker.m_6710_(nearest);
            attacker.m_6703_(nearest);
        }
    }

    private void summonMinion(ServerLevel level, EntityType<? extends Mob> type, LivingEntity target, int index) {
        Mob minion = (Mob)type.m_20615_((Level)level);
        if (minion == null) {
            return;
        }
        double angle = (double)index * 2.399963229728653 + this.f_19796_.m_188500_() * 0.35;
        double radius = 2.5 + (double)(index % 3);
        BlockPos spawnPos = BlockPos.m_274561_((double)(this.m_20185_() + Math.cos(angle) * radius), (double)this.m_20186_(), (double)(this.m_20189_() + Math.sin(angle) * radius));
        minion.m_7678_((double)spawnPos.m_123341_() + 0.5, (double)spawnPos.m_123342_(), (double)spawnPos.m_123343_() + 0.5, this.m_146908_(), 0.0f);
        minion.m_6518_((ServerLevelAccessor)level, level.m_6436_(spawnPos), MobSpawnType.MOB_SUMMONED, null, null);
        if (minion instanceof Owned) {
            Owned owned = (Owned)minion;
            LivingEntity master = this.getTrueOwner();
            owned.setTrueOwner((LivingEntity)(master != null ? master : this));
            owned.setHasLifespan(true);
            owned.setLifespan(1200);
        }
        if (minion instanceof AbstractNecromancer) {
            AbstractNecromancer necromancer = (AbstractNecromancer)minion;
            necromancer.setNecroLevel(Math.max(0, this.getNecroLevel() - 1));
        }
        if (target != null && target.m_6084_()) {
            minion.m_6710_(target);
        }
        level.m_7967_((Entity)minion);
    }

    private final class NamelessCloneGoal
    extends Goal {
        private int castTicks;

        private NamelessCloneGoal() {
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean m_8036_() {
            LivingEntity target = NamelessNecromancerServant.this.m_5448_();
            return !NamelessNecromancerServant.this.decoyClone && NamelessNecromancerServant.this.cloneCooldown <= 0 && target != null && target.m_6084_();
        }

        public boolean m_8045_() {
            LivingEntity target = NamelessNecromancerServant.this.m_5448_();
            return this.castTicks <= 30 && target != null && target.m_6084_();
        }

        public void m_8056_() {
            this.castTicks = 0;
            NamelessNecromancerServant.this.setSpellCasting(true);
            NamelessNecromancerServant.this.setAnimationState(AbstractNecromancer.SUMMON);
            NamelessNecromancerServant.this.m_5496_((SoundEvent)ModSounds.PREPARE_SPELL.get(), 1.25f, 0.7f);
        }

        public void m_8037_() {
            Level level;
            ++this.castTicks;
            LivingEntity target = NamelessNecromancerServant.this.m_5448_();
            NamelessNecromancerServant.this.m_21573_().m_26573_();
            if (target != null) {
                NamelessNecromancerServant.this.m_21563_().m_24960_((Entity)target, 35.0f, 35.0f);
            }
            if (this.castTicks == 30 && (level = NamelessNecromancerServant.this.m_9236_()) instanceof ServerLevel) {
                ServerLevel level2 = (ServerLevel)level;
                NamelessNecromancerServant.this.summonDecoyClones(level2, target);
                NamelessNecromancerServant.this.m_5496_((SoundEvent)ModSounds.NECROMANCER_SUMMON.get(), 1.3f, 1.08f);
            }
        }

        public void m_8041_() {
            NamelessNecromancerServant.this.setSpellCasting(false);
            NamelessNecromancerServant.this.setAnimationState(AbstractNecromancer.IDLE);
            NamelessNecromancerServant.this.cloneCooldown = 360;
        }

        public boolean m_183429_() {
            return true;
        }
    }

    private final class NamelessArmyGoal
    extends Goal {
        private int castTicks;

        private NamelessArmyGoal() {
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean m_8036_() {
            LivingEntity target = NamelessNecromancerServant.this.m_5448_();
            return !NamelessNecromancerServant.this.decoyClone && NamelessNecromancerServant.this.armyCooldown <= 0 && target != null && target.m_6084_();
        }

        public boolean m_8045_() {
            return this.castTicks <= 30;
        }

        public void m_8056_() {
            this.castTicks = 0;
            NamelessNecromancerServant.this.setSpellCasting(true);
            NamelessNecromancerServant.this.setAnimationState(AbstractNecromancer.SUMMON);
            NamelessNecromancerServant.this.m_5496_((SoundEvent)ModSounds.NECROMANCER_SUMMON.get(), 1.2f, 0.82f);
        }

        public void m_8037_() {
            Level level;
            ++this.castTicks;
            LivingEntity target = NamelessNecromancerServant.this.m_5448_();
            if (target != null) {
                NamelessNecromancerServant.this.m_21563_().m_24960_((Entity)target, 30.0f, 30.0f);
            }
            if (this.castTicks == 30 && (level = NamelessNecromancerServant.this.m_9236_()) instanceof ServerLevel) {
                int index;
                ServerLevel level2 = (ServerLevel)level;
                int count = 3 + NamelessNecromancerServant.this.f_19796_.m_188503_(3);
                for (index = 0; index < count; ++index) {
                    EntityType type = switch (NamelessNecromancerServant.this.f_19796_.m_188503_(5)) {
                        case 0 -> (EntityType)ModEntityType.ZOMBIE_SERVANT.get();
                        case 1 -> (EntityType)ModEntityType.SKELETON_SERVANT.get();
                        case 2 -> (EntityType)GoetyMasteryOfMagicModEntities.JARLESS_NECROMANCER.get();
                        case 3 -> (EntityType)ModEntityType.VANGUARD_SERVANT.get();
                        default -> (EntityType)ModEntityType.BLACKGUARD_SERVANT.get();
                    };
                    NamelessNecromancerServant.this.summonMinion(level2, (EntityType<? extends Mob>)type, target, index);
                }
                if (target != null && target.m_20186_() > NamelessNecromancerServant.this.m_20186_() + 4.0) {
                    for (index = 0; index < 2; ++index) {
                        NamelessNecromancerServant.this.summonMinion(level2, (EntityType<? extends Mob>)((EntityType)ModEntityType.REAPER_SERVANT.get()), target, count + index);
                    }
                }
            }
        }

        public void m_8041_() {
            NamelessNecromancerServant.this.setSpellCasting(false);
            NamelessNecromancerServant.this.setAnimationState(AbstractNecromancer.IDLE);
            NamelessNecromancerServant.this.armyCooldown = 260;
        }

        public boolean m_183429_() {
            return true;
        }
    }

    private final class HoverAtTargetGoal
    extends Goal {
        private HoverAtTargetGoal() {
            this.m_7021_(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean m_8036_() {
            LivingEntity target = NamelessNecromancerServant.this.m_5448_();
            return target != null && target.m_6084_() && !NamelessNecromancerServant.this.isSpellCasting();
        }

        public boolean m_8045_() {
            return this.m_8036_();
        }

        public void m_8037_() {
            LivingEntity target = NamelessNecromancerServant.this.m_5448_();
            if (target == null) {
                return;
            }
            Vec3 horizontal = NamelessNecromancerServant.this.m_20182_().m_82546_(target.m_20182_()).m_82542_(1.0, 0.0, 1.0);
            if (horizontal.m_82556_() < 0.01) {
                horizontal = new Vec3(1.0, 0.0, 0.0);
            }
            horizontal = horizontal.m_82541_().m_82490_(12.0);
            NamelessNecromancerServant.this.m_21566_().m_6849_(target.m_20185_() + horizontal.f_82479_, target.m_20186_() + 4.0, target.m_20189_() + horizontal.f_82481_, 1.0);
        }
    }
}

