/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.FlyingMob
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.ai.control.FlyingMoveControl
 *  net.minecraft.world.phys.Vec3
 */
package com.vivideru.masteryofmagic.goldification;

import com.vivideru.masteryofmagic.mixins.EntitySharedFlagAccessor;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.phys.Vec3;

public final class GoldificationEntityData {
    private static final String ROOT = "goety_mastery_of_magic_goldification";
    private static final String GOLDIFIED = "Goldified";
    private static final String EXPIRE = "ExpireGameTime";
    private static final String CREATED = "CreatedGameTime";
    private static final String AUTO_SHATTER = "AutoShatterGameTime";
    private static final String SOURCE = "Source";
    private static final String FROZEN_X = "FrozenX";
    private static final String FROZEN_Z = "FrozenZ";
    private static final String FROZEN_Y_ROTATION = "FrozenYRotation";
    private static final String FROZEN_X_ROTATION = "FrozenXRotation";
    private static final String ORIGINAL_NO_AI = "OriginalNoAi";
    private static final String ORIGINAL_SILENT = "OriginalSilent";
    private static final String ORIGINAL_NO_GRAVITY = "OriginalNoGravity";
    private static final String FALL_SHATTER_ARMED = "FallShatterArmed";
    private static final String FORCED_FALL_VELOCITY = "ForcedFallVelocity";
    private static final int NO_GRAVITY_SHARED_FLAG = 5;

    private GoldificationEntityData() {
    }

    public static boolean isGoldified(Entity entity) {
        CompoundTag data = entity.getPersistentData().m_128469_(ROOT);
        return data.m_128471_(GOLDIFIED);
    }

    public static long getExpireGameTime(Entity entity) {
        return entity.getPersistentData().m_128469_(ROOT).m_128454_(EXPIRE);
    }

    public static long getCreatedGameTime(Entity entity) {
        return entity.getPersistentData().m_128469_(ROOT).m_128454_(CREATED);
    }

    public static long getAutoShatterGameTime(Entity entity) {
        CompoundTag data = entity.getPersistentData().m_128469_(ROOT);
        return data.m_128441_(AUTO_SHATTER) ? data.m_128454_(AUTO_SHATTER) : -1L;
    }

    @Nullable
    public static UUID getSourceUuid(Entity entity) {
        CompoundTag data = entity.getPersistentData().m_128469_(ROOT);
        return data.m_128403_(SOURCE) ? data.m_128342_(SOURCE) : null;
    }

    public static void set(Entity entity, long expireGameTime, long createdGameTime, long autoShatterGameTime, @Nullable UUID sourceUuid) {
        Mob mob;
        boolean alreadyGoldified = GoldificationEntityData.isGoldified(entity);
        CompoundTag data = alreadyGoldified ? entity.getPersistentData().m_128469_(ROOT).m_6426_() : new CompoundTag();
        data.m_128379_(GOLDIFIED, true);
        data.m_128356_(EXPIRE, expireGameTime);
        data.m_128356_(CREATED, createdGameTime);
        data.m_128356_(AUTO_SHATTER, autoShatterGameTime);
        if (!alreadyGoldified) {
            data.m_128347_(FROZEN_X, entity.m_20185_());
            data.m_128347_(FROZEN_Z, entity.m_20189_());
            data.m_128350_(FROZEN_Y_ROTATION, entity.m_146908_());
            data.m_128350_(FROZEN_X_ROTATION, entity.m_146909_());
            data.m_128379_(ORIGINAL_SILENT, entity.m_20067_());
            data.m_128379_(ORIGINAL_NO_GRAVITY, entity.m_20068_());
            data.m_128379_(FALL_SHATTER_ARMED, false);
            if (entity instanceof Mob) {
                mob = (Mob)entity;
                data.m_128379_(ORIGINAL_NO_AI, mob.m_21525_());
            }
        }
        if (sourceUuid != null) {
            data.m_128362_(SOURCE, sourceUuid);
        } else {
            data.m_128473_(SOURCE);
        }
        entity.getPersistentData().m_128365_(ROOT, (Tag)data);
        if (entity instanceof Mob) {
            mob = (Mob)entity;
            mob.m_21557_(true);
        }
        entity.m_20225_(true);
        GoldificationEntityData.setNoGravityDirectly(entity, false);
    }

    public static boolean clear(Entity entity) {
        if (!GoldificationEntityData.isGoldified(entity)) {
            return false;
        }
        CompoundTag data = entity.getPersistentData().m_128469_(ROOT);
        if (entity instanceof Mob) {
            Mob mob = (Mob)entity;
            if (data.m_128441_(ORIGINAL_NO_AI)) {
                mob.m_21557_(data.m_128471_(ORIGINAL_NO_AI));
            }
        }
        if (data.m_128441_(ORIGINAL_SILENT)) {
            entity.m_20225_(data.m_128471_(ORIGINAL_SILENT));
        }
        if (data.m_128441_(ORIGINAL_NO_GRAVITY)) {
            GoldificationEntityData.setNoGravityDirectly(entity, data.m_128471_(ORIGINAL_NO_GRAVITY));
        }
        entity.getPersistentData().m_128473_(ROOT);
        return true;
    }

    public static void enforceFrozenState(Entity entity) {
        if (!GoldificationEntityData.isGoldified(entity)) {
            return;
        }
        CompoundTag data = entity.getPersistentData().m_128469_(ROOT);
        Vec3 movement = entity.m_20184_();
        GoldificationEntityData.setNoGravityDirectly(entity, false);
        entity.m_6034_(data.m_128459_(FROZEN_X), entity.m_20186_(), data.m_128459_(FROZEN_Z));
        entity.m_20334_(0.0, movement.f_82480_, 0.0);
        float frozenYRotation = data.m_128457_(FROZEN_Y_ROTATION);
        float frozenXRotation = data.m_128457_(FROZEN_X_ROTATION);
        entity.m_146922_(frozenYRotation);
        entity.m_146926_(frozenXRotation);
        entity.f_19859_ = frozenYRotation;
        entity.f_19860_ = frozenXRotation;
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            living.f_20883_ = frozenYRotation;
            living.f_20884_ = frozenYRotation;
            living.f_20885_ = frozenYRotation;
            living.f_20886_ = frozenYRotation;
        }
        if (entity instanceof Mob) {
            Mob mob = (Mob)entity;
            mob.m_21557_(true);
            if (!data.m_128471_(FALL_SHATTER_ARMED) && !entity.m_20096_()) {
                data.m_128379_(FALL_SHATTER_ARMED, true);
            }
            if (GoldificationEntityData.needsForcedFlightGravity(mob, data)) {
                GoldificationEntityData.applyForcedFlightGravity(mob, data);
            }
        }
        entity.m_20225_(true);
        entity.f_19812_ = true;
    }

    public static boolean shouldShatterOnGroundImpact(Entity entity) {
        if (!(entity instanceof Mob && GoldificationEntityData.isGoldified(entity) && entity.m_20096_())) {
            return false;
        }
        return entity.getPersistentData().m_128469_(ROOT).m_128471_(FALL_SHATTER_ARMED);
    }

    private static boolean needsForcedFlightGravity(Mob mob, CompoundTag data) {
        return mob instanceof FlyingMob || mob.m_21566_() instanceof FlyingMoveControl || data.m_128471_(ORIGINAL_NO_GRAVITY);
    }

    private static void applyForcedFlightGravity(Mob mob, CompoundTag data) {
        if (mob.m_20096_()) {
            data.m_128473_(FORCED_FALL_VELOCITY);
            mob.m_20256_(Vec3.f_82478_);
            return;
        }
        double previousVelocity = data.m_128441_(FORCED_FALL_VELOCITY) ? data.m_128459_(FORCED_FALL_VELOCITY) : Math.min(0.0, mob.m_20184_().f_82480_);
        double fallVelocity = Math.max((previousVelocity - 0.08) * 0.98, -3.92);
        data.m_128347_(FORCED_FALL_VELOCITY, fallVelocity);
        mob.m_20256_(Vec3.f_82478_);
        mob.m_6478_(MoverType.SELF, new Vec3(0.0, fallVelocity, 0.0));
        mob.m_20256_(Vec3.f_82478_);
    }

    private static void setNoGravityDirectly(Entity entity, boolean noGravity) {
        ((EntitySharedFlagAccessor)entity).goetyMasteryOfMagic$setSharedFlag(5, noGravity);
    }
}

