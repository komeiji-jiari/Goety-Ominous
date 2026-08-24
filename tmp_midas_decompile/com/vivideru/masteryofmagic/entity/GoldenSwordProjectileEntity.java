/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.utils.MobUtil
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.DamageTypeTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.boss.EnderDragonPart
 *  net.minecraft.world.entity.boss.enderdragon.EnderDragon
 *  net.minecraft.world.entity.monster.Enemy
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.ItemSupplier
 *  net.minecraft.world.entity.projectile.ThrowableProjectile
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.network.NetworkHooks
 *  net.minecraftforge.network.PlayMessages$SpawnEntity
 *  org.jetbrains.annotations.Nullable
 */
package com.vivideru.masteryofmagic.entity;

import com.Polarice3.Goety.utils.MobUtil;
import com.vivideru.masteryofmagic.entity.PhilosopherKingMidasEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModParticleTypes;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.Nullable;

public class GoldenSwordProjectileEntity
extends ThrowableProjectile
implements ItemSupplier {
    public static final int DIRECT = 0;
    public static final int CURVE_LEFT = 1;
    public static final int CURVE_RIGHT = 2;
    public static final int RAIN = 3;
    public static final int HOVER_TICKS = 40;
    public static final int AIM_TICKS = 12;
    public static final int LAUNCH_TICK = 52;
    public static final int FLIGHT_LIFETIME_TICKS = 100;
    public static final int DISSOLVE_TICKS = 8;
    public static final float MAX_HEALTH = 20.0f;
    private static final double FLIGHT_SPEED = 1.65;
    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.m_135353_(GoldenSwordProjectileEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> PATH_MODE = SynchedEntityData.m_135353_(GoldenSwordProjectileEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Boolean> DISSOLVING = SynchedEntityData.m_135353_(GoldenSwordProjectileEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    private static final EntityDataAccessor<Integer> LAUNCH_DELAY = SynchedEntityData.m_135353_(GoldenSwordProjectileEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> FORMATION_INDEX = SynchedEntityData.m_135353_(GoldenSwordProjectileEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> FORMATION_TOTAL = SynchedEntityData.m_135353_(GoldenSwordProjectileEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private UUID targetUuid;
    private boolean rainDiving;
    private float health = 20.0f;
    private int dissolveTime;

    public GoldenSwordProjectileEntity(PlayMessages.SpawnEntity packet, Level level) {
        this((EntityType<? extends GoldenSwordProjectileEntity>)((EntityType)GoetyMasteryOfMagicModEntities.GOLDEN_SWORD_PROJECTILE.get()), level);
    }

    public GoldenSwordProjectileEntity(EntityType<? extends GoldenSwordProjectileEntity> type, Level level) {
        super(type, level);
        this.m_20242_(true);
    }

    protected void m_8097_() {
        this.f_19804_.m_135372_(TARGET_ID, (Object)-1);
        this.f_19804_.m_135372_(PATH_MODE, (Object)0);
        this.f_19804_.m_135372_(DISSOLVING, (Object)false);
        this.f_19804_.m_135372_(LAUNCH_DELAY, (Object)0);
        this.f_19804_.m_135372_(FORMATION_INDEX, (Object)0);
        this.f_19804_.m_135372_(FORMATION_TOTAL, (Object)1);
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.f_19804_.m_135381_(TARGET_ID, (Object)(target == null ? -1 : target.m_19879_()));
        this.targetUuid = target == null ? null : target.m_20148_();
    }

    @Nullable
    public LivingEntity getTargetEntity() {
        ServerLevel serverLevel;
        Entity byUuid;
        Level level;
        Entity byId = this.m_9236_().m_6815_(((Integer)this.f_19804_.m_135370_(TARGET_ID)).intValue());
        if (byId instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)byId;
            return living;
        }
        if (!this.m_9236_().f_46443_ && this.targetUuid != null && (level = this.m_9236_()) instanceof ServerLevel && (byUuid = (serverLevel = (ServerLevel)level).m_8791_(this.targetUuid)) instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)byUuid;
            this.f_19804_.m_135381_(TARGET_ID, (Object)living.m_19879_());
            return living;
        }
        return null;
    }

    public void setPathMode(int pathMode) {
        this.f_19804_.m_135381_(PATH_MODE, (Object)Mth.m_14045_((int)pathMode, (int)0, (int)3));
    }

    public int getPathMode() {
        return (Integer)this.f_19804_.m_135370_(PATH_MODE);
    }

    public void setLaunchDelay(int ticks) {
        this.f_19804_.m_135381_(LAUNCH_DELAY, (Object)Mth.m_14045_((int)ticks, (int)0, (int)100));
    }

    public int getLaunchTick() {
        return 52 + (Integer)this.f_19804_.m_135370_(LAUNCH_DELAY);
    }

    public void setFormationSlot(int index, int total) {
        this.f_19804_.m_135381_(FORMATION_INDEX, (Object)Math.max(0, index));
        this.f_19804_.m_135381_(FORMATION_TOTAL, (Object)Math.max(1, total));
    }

    public boolean isOwnedBy(Entity entity) {
        return this.m_19749_() == entity;
    }

    public boolean isDissolving() {
        return (Boolean)this.f_19804_.m_135370_(DISSOLVING);
    }

    public float getDissolveScale(float partialTick) {
        if (!this.isDissolving()) {
            return 1.0f;
        }
        return Mth.m_14036_((float)(1.0f - ((float)this.dissolveTime + partialTick) / 8.0f), (float)0.0f, (float)1.0f);
    }

    public void m_8119_() {
        int launchTick;
        if (this.isDissolving()) {
            this.m_20256_(Vec3.f_82478_);
            super.m_8119_();
            ++this.dissolveTime;
            if (!this.m_9236_().f_46443_) {
                this.spawnDissolveFragments(this.dissolveTime == 1 ? 10 : 3);
                if (this.dissolveTime >= 8) {
                    this.m_146870_();
                }
            }
            return;
        }
        if (!this.m_9236_().f_46443_) {
            this.retargetFromMidasIfNecessary();
        }
        if (this.f_19797_ < (launchTick = this.getLaunchTick())) {
            this.m_20256_(Vec3.f_82478_);
        } else if (!this.m_9236_().f_46443_) {
            this.steerTowardTarget();
        }
        super.m_8119_();
        if (this.m_9236_().f_46443_ && this.f_19797_ % 6 == 0) {
            this.spawnClientAlchemyParticle();
        }
        if (this.m_9236_().f_46443_ && this.f_19797_ % 2 == 0) {
            this.spawnStrongEnchantmentParticles();
        }
        if (!this.m_9236_().f_46443_ && this.f_19797_ >= launchTick + 100) {
            this.beginDissolving();
        }
    }

    private void spawnClientAlchemyParticle() {
        Vec3 bladeDirection = this.getRenderDirection(0.0f);
        double alongBlade = (this.f_19796_.m_188500_() - 0.5) * 1.8;
        Vec3 point = this.m_20182_().m_82549_(bladeDirection.m_82490_(alongBlade));
        Vec3 movement = this.m_20184_();
        Vec3 drift = movement.m_82556_() > 1.0E-5 ? movement.m_82541_().m_82548_().m_82490_(0.025) : new Vec3(0.0, 0.008, 0.0);
        this.m_9236_().m_7106_((ParticleOptions)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY_SMALL.get(), point.f_82479_ + (this.f_19796_.m_188500_() - 0.5) * 0.12, point.f_82480_ + (this.f_19796_.m_188500_() - 0.5) * 0.12, point.f_82481_ + (this.f_19796_.m_188500_() - 0.5) * 0.12, drift.f_82479_, drift.f_82480_, drift.f_82481_);
    }

    private void spawnStrongEnchantmentParticles() {
        Vec3 bladeDirection = this.getRenderDirection(0.0f);
        for (int index = 0; index < 2; ++index) {
            double alongBlade = (this.f_19796_.m_188500_() - 0.5) * 2.5;
            Vec3 point = this.m_20182_().m_82549_(bladeDirection.m_82490_(alongBlade));
            this.m_9236_().m_7106_((ParticleOptions)ParticleTypes.f_123809_, point.f_82479_ + (this.f_19796_.m_188500_() - 0.5) * 0.45, point.f_82480_ + (this.f_19796_.m_188500_() - 0.5) * 0.45, point.f_82481_ + (this.f_19796_.m_188500_() - 0.5) * 0.45, (this.f_19796_.m_188500_() - 0.5) * 0.18, 0.08 + this.f_19796_.m_188500_() * 0.1, (this.f_19796_.m_188500_() - 0.5) * 0.18);
        }
    }

    private void retargetFromMidasIfNecessary() {
        PhilosopherKingMidasEntity midas;
        Entity entity;
        block7: {
            block6: {
                LivingEntity current = this.getTargetEntity();
                if (current != null && current.m_6084_() && !current.m_213877_()) {
                    return;
                }
                entity = this.m_19749_();
                if (!(entity instanceof PhilosopherKingMidasEntity)) break block6;
                midas = (PhilosopherKingMidasEntity)entity;
                entity = this.m_9236_();
                if (entity instanceof ServerLevel) break block7;
            }
            this.setTarget(null);
            return;
        }
        ServerLevel serverLevel = (ServerLevel)entity;
        LivingEntity replacement = midas.m_5448_();
        if (!this.isValidMidasEnemy(replacement)) {
            LivingEntity attacker = midas.m_21188_();
            Object object = replacement = this.isValidMidasEnemy(attacker) ? attacker : null;
        }
        if (replacement == null) {
            replacement = serverLevel.m_6443_(LivingEntity.class, midas.m_20191_().m_82400_(64.0), this::isValidMidasEnemy).stream().min(Comparator.comparingDouble(arg_0 -> ((PhilosopherKingMidasEntity)midas).m_20280_(arg_0))).orElse(null);
        }
        this.setTarget(replacement);
    }

    private boolean isValidMidasEnemy(@Nullable LivingEntity candidate) {
        if (candidate == null || !candidate.m_6084_() || candidate.m_213877_() || candidate == this.m_19749_()) {
            return false;
        }
        if (candidate instanceof Player) {
            Player player = (Player)candidate;
            return !player.m_7500_() && !player.m_5833_();
        }
        return candidate instanceof Mob && !(candidate instanceof Enemy);
    }

    public boolean m_6469_(DamageSource source, float amount) {
        if (source.m_269533_(DamageTypeTags.f_268415_) || this.m_213877_() || amount <= 0.0f) {
            return false;
        }
        Entity owner = this.m_19749_();
        Entity attacker = source.m_7639_();
        Entity direct = source.m_7640_();
        if (owner != null && (attacker == owner || direct == owner || attacker != null && MobUtil.areAllies((Entity)owner, (Entity)attacker))) {
            return false;
        }
        if (this.m_9236_().f_46443_) {
            return true;
        }
        this.health -= amount;
        this.m_5834_();
        if (this.health <= 0.0f) {
            this.beginDissolving();
        }
        return true;
    }

    public boolean m_6087_() {
        return !this.isDissolving();
    }

    public boolean m_271807_() {
        return true;
    }

    public boolean m_6097_() {
        return true;
    }

    public boolean m_6128_() {
        return true;
    }

    public Vec3 getRenderDirection(float partialTick) {
        Vec3 aim;
        LivingEntity target;
        Vec3 spawnDirection = new Vec3(0.0, -1.0, 0.0);
        int launchTick = this.getLaunchTick();
        int aimStartTick = launchTick - 12;
        if (this.f_19797_ < aimStartTick) {
            return spawnDirection;
        }
        if (this.f_19797_ < launchTick && (target = this.getTargetEntity()) != null && (aim = this.initialGuidancePoint(target).m_82546_(this.m_20182_())).m_82556_() > 1.0E-5) {
            aim = aim.m_82541_();
            double progress = Mth.m_14008_((double)(((float)this.f_19797_ + partialTick - (float)aimStartTick) / 12.0f), (double)0.0, (double)1.0);
            Vec3 turning = spawnDirection.m_82490_(1.0 - (progress = progress * progress * (3.0 - 2.0 * progress))).m_82549_(aim.m_82490_(progress));
            return turning.m_82556_() > 1.0E-5 ? turning.m_82541_() : aim;
        }
        Vec3 movement = this.m_20184_();
        return movement.m_82556_() > 1.0E-5 ? movement.m_82541_() : spawnDirection;
    }

    private void steerTowardTarget() {
        Vec3 current;
        Vec3 desired;
        LivingEntity target = this.getTargetEntity();
        if (target == null || !target.m_6084_()) {
            return;
        }
        int path = this.getPathMode();
        int flightTick = Math.max(0, this.f_19797_ - this.getLaunchTick());
        if (path == 3 && !this.rainDiving) {
            Vec3 overhead = this.initialGuidancePoint(target);
            Vec3 toOverhead = overhead.m_82546_(this.m_20182_());
            double horizontalX = overhead.f_82479_ - this.m_20185_();
            double horizontalZ = overhead.f_82481_ - this.m_20189_();
            double horizontalDistanceSqr = horizontalX * horizontalX + horizontalZ * horizontalZ;
            if (toOverhead.m_82556_() <= 4.0 || horizontalDistanceSqr <= 4.0 && this.m_20186_() >= overhead.f_82480_ - 1.0 || flightTick >= 70) {
                this.rainDiving = true;
                desired = target.m_20191_().m_82399_().m_82546_(this.m_20182_());
            } else {
                desired = toOverhead;
            }
        } else {
            desired = target.m_20191_().m_82399_().m_82546_(this.m_20182_());
        }
        if (desired.m_82556_() < 1.0E-5) {
            return;
        }
        double guidanceDistance = desired.m_82553_();
        desired = desired.m_82541_();
        if (path == 1 || path == 2) {
            double curveStrength = Math.max(0.0, 1.0 - (double)flightTick / 42.0) * 0.95;
            Vec3 tangent = new Vec3(-desired.f_82481_, 0.0, desired.f_82479_);
            if (tangent.m_82556_() > 1.0E-5) {
                tangent = tangent.m_82541_();
                if (path == 2) {
                    tangent = tangent.m_82548_();
                }
                desired = desired.m_82549_(tangent.m_82490_(curveStrength)).m_82541_();
            }
        }
        Vec3 currentDirection = (current = this.m_20184_()).m_82556_() < 1.0E-5 ? desired : current.m_82541_();
        double distanceFactor = Mth.m_14008_((double)((guidanceDistance - 3.0) / 17.0), (double)0.0, (double)1.0);
        double farHoming = path == 3 && this.rainDiving ? 0.78 : (path == 0 ? 0.72 : 0.64);
        double homingStrength = Mth.m_14139_((double)distanceFactor, (double)0.06, (double)farHoming);
        Vec3 steered = currentDirection.m_82490_(1.0 - homingStrength).m_82549_(desired.m_82490_(homingStrength)).m_82541_();
        this.m_20256_(steered.m_82490_(1.65));
    }

    private Vec3 initialGuidancePoint(LivingEntity target) {
        if (this.getPathMode() == 3) {
            int index = (Integer)this.f_19804_.m_135370_(FORMATION_INDEX);
            int total = Math.max(1, (Integer)this.f_19804_.m_135370_(FORMATION_TOTAL));
            double angle = (double)index * 2.399963229728653;
            double radius = 5.5 * Math.sqrt(((double)index + 0.5) / (double)total);
            return target.m_20191_().m_82399_().m_82520_(Math.cos(angle) * radius, 12.0, Math.sin(angle) * radius);
        }
        return target.m_20191_().m_82399_();
    }

    protected boolean m_5603_(Entity entity) {
        if (this.isDissolving() || entity instanceof PhilosopherKingMidasEntity || entity instanceof GoldenSwordProjectileEntity) {
            return false;
        }
        Entity owner = this.m_19749_();
        return entity != owner && super.m_5603_(entity);
    }

    protected void m_5790_(EntityHitResult hitResult) {
        if (!this.m_9236_().f_46443_) {
            EnderDragon target;
            Entity struck = hitResult.m_82443_();
            if (struck instanceof EnderDragonPart) {
                EnderDragonPart part = (EnderDragonPart)struck;
                v0 = part.f_31010_;
            } else if (struck instanceof LivingEntity) {
                LivingEntity living = (LivingEntity)struck;
                v0 = living;
            } else {
                v0 = target = null;
            }
            if (target != null) {
                double baseHealth = target.m_21172_(Attributes.f_22276_);
                float damage = (float)(Math.max(1.0, baseHealth) * 0.2);
                Entity owner = this.m_19749_();
                target.f_19802_ = 0;
                if (owner instanceof LivingEntity) {
                    LivingEntity livingOwner = (LivingEntity)owner;
                    target.m_6469_(this.m_269291_().m_269299_((Entity)this, livingOwner), damage);
                } else {
                    target.m_6469_(this.m_269291_().m_269104_((Entity)this, owner), damage);
                }
            }
            this.beginDissolving();
        }
    }

    protected void m_8060_(BlockHitResult hitResult) {
        if (!this.m_9236_().f_46443_) {
            this.beginDissolving();
        }
    }

    private void beginDissolving() {
        if (this.isDissolving() || this.m_213877_()) {
            return;
        }
        this.f_19804_.m_135381_(DISSOLVING, (Object)true);
        this.dissolveTime = 0;
        this.m_20256_(Vec3.f_82478_);
        this.f_19794_ = true;
        this.m_9236_().m_5594_(null, this.m_20183_(), SoundEvents.f_12018_, SoundSource.HOSTILE, 1.25f, 0.82f + this.f_19796_.m_188501_() * 0.18f);
        this.spawnDissolveFragments(14);
    }

    private void spawnDissolveFragments(int count) {
        ServerLevel serverLevel;
        block4: {
            block3: {
                Level level = this.m_9236_();
                if (!(level instanceof ServerLevel)) break block3;
                serverLevel = (ServerLevel)level;
                if (count > 0) break block4;
            }
            return;
        }
        ItemParticleOption fragment = new ItemParticleOption(ParticleTypes.f_123752_, this.m_7846_());
        Vec3 direction = this.getRenderDirection(1.0f);
        for (int i = 0; i < count; ++i) {
            double alongBlade = (this.f_19796_.m_188500_() - 0.5) * 2.4;
            Vec3 point = this.m_20182_().m_82549_(direction.m_82490_(alongBlade));
            serverLevel.m_8767_((ParticleOptions)fragment, point.f_82479_, point.f_82480_, point.f_82481_, 1, 0.18, 0.18, 0.18, 0.14);
        }
    }

    protected float m_7139_() {
        return 0.0f;
    }

    public ItemStack m_7846_() {
        ItemStack sword = new ItemStack((ItemLike)Items.f_42430_);
        sword.m_41663_(Enchantments.f_44986_, 1);
        return sword;
    }

    protected void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        if (this.targetUuid != null) {
            tag.m_128362_("Target", this.targetUuid);
        }
        tag.m_128405_("PathMode", this.getPathMode());
        tag.m_128379_("RainDiving", this.rainDiving);
        tag.m_128350_("Health", this.health);
        tag.m_128379_("Dissolving", this.isDissolving());
        tag.m_128405_("DissolveTime", this.dissolveTime);
        tag.m_128405_("LaunchDelay", ((Integer)this.f_19804_.m_135370_(LAUNCH_DELAY)).intValue());
        tag.m_128405_("FormationIndex", ((Integer)this.f_19804_.m_135370_(FORMATION_INDEX)).intValue());
        tag.m_128405_("FormationTotal", ((Integer)this.f_19804_.m_135370_(FORMATION_TOTAL)).intValue());
    }

    protected void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        if (tag.m_128403_("Target")) {
            this.targetUuid = tag.m_128342_("Target");
        }
        this.setPathMode(tag.m_128451_("PathMode"));
        this.rainDiving = tag.m_128471_("RainDiving");
        this.health = tag.m_128441_("Health") ? Mth.m_14036_((float)tag.m_128457_("Health"), (float)0.0f, (float)20.0f) : 20.0f;
        this.f_19804_.m_135381_(DISSOLVING, (Object)tag.m_128471_("Dissolving"));
        this.dissolveTime = Mth.m_14045_((int)tag.m_128451_("DissolveTime"), (int)0, (int)8);
        this.setLaunchDelay(tag.m_128451_("LaunchDelay"));
        this.setFormationSlot(tag.m_128451_("FormationIndex"), tag.m_128451_("FormationTotal"));
        if (this.isDissolving()) {
            this.f_19794_ = true;
            this.m_20256_(Vec3.f_82478_);
        }
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }
}

