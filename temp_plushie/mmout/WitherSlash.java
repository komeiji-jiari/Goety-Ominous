package com.alexander.mutantmore.entities;

import com.alexander.mutantmore.events.ShakeCameraEvent;
import com.alexander.mutantmore.init.EntityTypeInit;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.init.TagInit.EntityTypes;
import com.alexander.mutantmore.util.HandleLoopingSoundInstances;
import com.alexander.mutantmore.util.MiscUtils;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class WitherSlash extends ThrowableProjectile {
    private static final EntityDataAccessor<Float> SIZE = SynchedEntityData.m_135353_(WitherSlash.class, EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> FIXED_YAW = SynchedEntityData.m_135353_(WitherSlash.class, EntityDataSerializers.f_135029_);
    public int textureChange = 0;
    public float damage = 0.0F;
    public float leechAmount = 0.0F;
    public int witherLength = 0;
    public int witherLevel = 0;
    public boolean ignoresInvulTime = true;
    public List<Entity> alreadyHit = Lists.newArrayList();

    public WitherSlash(EntityType<? extends WitherSlash> p_37391_, Level p_37392_) {
        super(p_37391_, p_37392_);
    }

    public WitherSlash(Level p_37399_, LivingEntity p_37400_, float yRot) {
        super((EntityType)EntityTypeInit.WITHER_SLASH.get(), p_37400_, p_37399_);
        this.setFixedYaw(yRot);
    }

    protected float m_7139_() {
        return 0.0F;
    }

    protected boolean m_5603_(Entity target) {
        return this.canHit(target) && super.m_5603_(target);
    }

    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.m_9236_().f_46443_) {
            HandleLoopingSoundInstances.addFireSlashAudio(this, this.m_9236_());
        }

    }

    public EntityDimensions m_6972_(Pose p_19975_) {
        return super.m_6972_(p_19975_).m_20388_(this.getSize());
    }

    public boolean m_6060_() {
        return false;
    }

    public boolean m_6087_() {
        return false;
    }

    public boolean m_6469_(DamageSource p_37616_, float p_37617_) {
        return false;
    }

    protected boolean shouldBurn() {
        return false;
    }

    protected void m_8097_() {
        this.f_19804_.m_135372_(SIZE, 1.0F);
        this.f_19804_.m_135372_(FIXED_YAW, 0.0F);
    }

    public float getSize() {
        return this.f_19804_.m_135370_(SIZE);
    }

    public void setSize(float value) {
        this.f_19804_.m_135381_(SIZE, value);
    }

    public float getFixedYaw() {
        return this.f_19804_.m_135370_(FIXED_YAW);
    }

    public void setFixedYaw(float yaw) {
        if (!this.m_9236_().f_46443_) {
            this.f_19804_.m_135381_(FIXED_YAW, yaw);
        }

    }

    public void m_8119_() {
        Vec3 deltaMovementO = this.m_20184_();
        super.m_8119_();
        this.m_6210_();
        this.m_20256_(deltaMovementO);

        for(Entity entity : this.m_9236_().m_45933_(this, this.m_20191_())) {
            if (!this.m_9236_().f_46443_ && this.m_5603_(entity)) {
                Entity owner = this.m_19749_();
                if (!this.alreadyHit.contains(entity) && this.canHarm(entity)) {
                    if (this.ignoresInvulTime) {
                        entity.f_19802_ = 0;
                    }

                    boolean flag = entity.m_6469_(MMDamageTypes.witherSlashAttack(this.m_269291_(), this, owner), this.damage);
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingTarget = (LivingEntity)entity;
                        if (flag) {
                            if (owner instanceof LivingEntity) {
                                LivingEntity livingOwner = (LivingEntity)owner;
                                MiscUtils.witherLeech(livingOwner, this.leechAmount, new Vec3(livingTarget.m_20185_(), livingTarget.m_20227_(0.75D), livingTarget.m_20189_()));
                            }

                            livingTarget.m_147207_(new MobEffectInstance(MobEffects.f_19615_, this.witherLength, this.witherLevel), owner);
                        }

                        flag = true;
                    } else {
                        flag = false;
                    }

                    if (owner instanceof LivingEntity) {
                        LivingEntity livingOwner = (LivingEntity)owner;
                        this.alreadyHit.add(entity);
                        this.m_19970_(livingOwner, entity);
                    }
                }
            }
        }

        if (this.m_9236_().f_46443_) {
            ShakeCameraEvent.shake(this.m_9236_(), 3, 0.0075F, this.m_20183_(), 5);
        }

        int particleColour = 5254710;
        double d0 = (double)((float)(particleColour >> 16 & 255) / 255.0F);
        double d1 = (double)((float)(particleColour >> 8 & 255) / 255.0F);
        double d2 = (double)((float)(particleColour & 255) / 255.0F);
        this.m_9236_().m_7107_(ParticleTypes.f_123811_, this.m_20208_(1.0D), this.m_20187_(), this.m_20262_(1.0D), d0, d1, d2);
        if (this.f_19797_ % 5 == 0) {
            ++this.textureChange;
        }

    }

    protected void m_5790_(EntityHitResult p_37386_) {
    }

    protected void m_8060_(BlockHitResult p_37384_) {
        super.m_8060_(p_37384_);
        if (!this.m_9236_().f_46443_) {
            this.m_146870_();
        }

    }

    boolean canHarm(Entity target) {
        if (MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.MUTANT_WITHER_SKELETON_WITHER_SLASH_CANT_HURT, this, target, this.m_19749_(), (entity) -> entity instanceof MutantWitherSkeleton)) {
            return true;
        } else if (MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.PLAYER_WITHER_SLASH_CANT_HURT, this, target, this.m_19749_(), (entity) -> entity instanceof Player)) {
            return true;
        } else {
            return MiscUtils.canHarmBasedOnTeamAndTag((TagKey)null, this, target, this.m_19749_(), (entity) -> !(entity instanceof MutantWitherSkeleton) && !(entity instanceof Player));
        }
    }

    boolean canHit(Entity target) {
        if (MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.MUTANT_WITHER_SKELETON_WITHER_SLASH_CANT_HIT, this, target, this.m_19749_(), (entity) -> entity instanceof MutantWitherSkeleton)) {
            return true;
        } else if (MiscUtils.canHarmBasedOnTeamAndTag(EntityTypes.PLAYER_WITHER_SLASH_CANT_HIT, this, target, this.m_19749_(), (entity) -> entity instanceof Player)) {
            return true;
        } else {
            return MiscUtils.canHarmBasedOnTeamAndTag((TagKey)null, this, target, this.m_19749_(), (entity) -> !(entity instanceof MutantWitherSkeleton) && !(entity instanceof Player));
        }
    }

    protected void m_6532_(HitResult p_37388_) {
        super.m_6532_(p_37388_);
    }

    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        if (tag.m_128441_("Damage")) {
            this.damage = tag.m_128457_("Damage");
        }

        if (tag.m_128441_("Size")) {
            this.setSize(tag.m_128457_("Size"));
        }

        if (tag.m_128441_("LeechAmount")) {
            this.leechAmount = tag.m_128457_("LeechAmount");
        }

        if (tag.m_128441_("WitherLength")) {
            this.witherLength = tag.m_128451_("WitherLength");
        }

        if (tag.m_128441_("WitherLevel")) {
            this.witherLevel = tag.m_128451_("WitherLevel");
        }

        if (tag.m_128441_("IgnoresInvulTime")) {
            this.ignoresInvulTime = tag.m_128471_("IgnoresInvulTime");
        }

    }

    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        tag.m_128350_("Damage", this.damage);
        tag.m_128350_("Size", this.getSize());
        tag.m_128350_("LeechAmount", this.leechAmount);
        tag.m_128405_("WitherLength", this.witherLength);
        tag.m_128405_("WitherLevel", this.witherLevel);
        tag.m_128379_("IgnoresInvulTime", this.ignoresInvulTime);
    }

    public boolean m_6097_() {
        return false;
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
