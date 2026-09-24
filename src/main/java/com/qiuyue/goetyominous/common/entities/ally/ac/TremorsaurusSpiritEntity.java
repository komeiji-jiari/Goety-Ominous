package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.WandUtil;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class TremorsaurusSpiritEntity extends Entity {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_ID =
            SynchedEntityData.defineId(TremorsaurusSpiritEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> ATTACKING_ENTITY_ID =
            SynchedEntityData.defineId(TremorsaurusSpiritEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DELAY_SPAWN =
            SynchedEntityData.defineId(TremorsaurusSpiritEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> POTENCY =
            SynchedEntityData.defineId(TremorsaurusSpiritEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FADING =
            SynchedEntityData.defineId(TremorsaurusSpiritEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> USING_ABILITY =
            SynchedEntityData.defineId(TremorsaurusSpiritEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BONUS_DAMAGE =
            SynchedEntityData.defineId(TremorsaurusSpiritEntity.class, EntityDataSerializers.INT);

    private static final float MAX_FADE = 10.0F;
    private static final float MAX_ABILITY = 5.0F;
    private static final int MAX_LIFE = 20;

    private float fadeIn;
    private float prevFadeIn;
    private float abilityProgress;
    private float prevAbilityProgress;
    private int duration;
    private boolean dealtDamage;
    private int lSteps;
    private double lx, ly, lz, lxr, lxd, lyd, lzd;

    public TremorsaurusSpiritEntity(EntityType<? extends Entity> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    public TremorsaurusSpiritEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(AcEntityRegistry.TREMORSAURUS_SPIRIT.get(), level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_ID, Optional.empty());
        this.entityData.define(ATTACKING_ENTITY_ID, -1);
        this.entityData.define(DELAY_SPAWN, 0);
        this.entityData.define(POTENCY, 0);
        this.entityData.define(FADING, false);
        this.entityData.define(USING_ABILITY, false);
        this.entityData.define(BONUS_DAMAGE, 0);
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity owner = this.getUsingEntity();
        this.prevFadeIn = this.fadeIn;
        this.prevAbilityProgress = this.abilityProgress;

        if (this.getDelaySpawn() > 0) {
            this.setDelaySpawn(this.getDelaySpawn() - 1);
            this.fadeIn = 0.0F;
            if (this.getDelaySpawn() == 0) {
                this.playSound(ACSoundRegistry.EXTINCTION_SPEAR_SUMMON.get(), 1.0F, 1.0F);
            }
            return;
        }

        if (this.isFading() && this.fadeIn > 0.0F) --this.fadeIn;
        if (!this.isFading() && this.fadeIn < MAX_FADE) ++this.fadeIn;
        if (this.isUsingAbility() && this.abilityProgress < MAX_ABILITY) ++this.abilityProgress;
        if (!this.isUsingAbility() && this.abilityProgress > 0.0F) --this.abilityProgress;
        if (this.isFading() && this.fadeIn <= 0.0F) {
            this.discard();
            return;
        }

        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.FLAME,
                    this.getRandomX(1.0D), this.getRandomY(), this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));

        if (owner == null) {
            if (!this.level().isClientSide) {
                this.setFading(true);
            }
        } else {
            this.tickTremorsaurus(owner);
        }

        if (this.level().isClientSide) {
            if (this.lSteps > 0) {
                double dx = this.getX() + (this.lx - this.getX()) / this.lSteps;
                double dy = this.getY() + (this.ly - this.getY()) / this.lSteps;
                double dz = this.getZ() + (this.lz - this.getZ()) / this.lSteps;
                this.setXRot(this.getXRot() + (float) (this.lxr - this.getXRot()) / this.lSteps);
                --this.lSteps;
                this.setPos(dx, dy, dz);
            } else {
                this.reapplyPosition();
            }
        }
    }

    private void tickTremorsaurus(LivingEntity owner) {
        Entity target = this.getAttackingEntity();
        if (target != null) {
            this.noPhysics = true;
            this.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            boolean inRange = this.distanceTo(target) < target.getBbWidth() + 3.5D;
            if (!inRange) {
                Vec3 toTarget = target.position().subtract(this.position());
                if (toTarget.length() > 1.0D) {
                    toTarget = toTarget.normalize();
                }
                this.setDeltaMovement(toTarget.scale(0.15D));
            }
            this.setUsingAbility(true);
            if (inRange && this.abilityProgress >= MAX_ABILITY) {
                if (!this.dealtDamage) {
                    int prevInvulnerableTime = target.invulnerableTime;
                    target.invulnerableTime = 0;
                    if (target.hurt(
                            ACDamageTypes.causeSpiritDinosaurDamage(this.level().registryAccess(), owner),
                            this.spiritDamage())) {
                        this.dealtDamage = true;
                    }
                    target.invulnerableTime = prevInvulnerableTime;
                }
                this.setFading(true);
            }
        }
        if (this.duration++ > MAX_LIFE) {
            this.setFading(true);
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    private float spiritDamage() {
        float damage = SpellConfig.TremorSpiritDamage.get().floatValue() * WandUtil.damageMultiply();
        damage += SpellConfig.TremorSpiritDamagePerPotency.get().floatValue() * this.getPotency();
        if (this.getBonusDamage() > 0) {
            damage += SpellConfig.TremorSpiritStaffBonus.get().floatValue();
        }
        return damage;
    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps, boolean b) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lxr = xr;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double x, double y, double z) {
        this.lxd = x;
        this.lyd = y;
        this.lzd = z;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(OWNER_ID, Optional.ofNullable(uuid));
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_ID).orElse(null);
    }

    @Nullable
    public LivingEntity getUsingEntity() {
        UUID uuid = this.getOwnerUUID();
        if (uuid == null) {
            return null;
        }
        if (this.level().isClientSide) {
            return this.level().getPlayerByUUID(uuid);
        }
        return EntityFinder.getLivingEntityByUuiD(this.level(), uuid);
    }

    public int getAttackingEntityId() {
        return this.entityData.get(ATTACKING_ENTITY_ID);
    }

    public void setAttackingEntityId(int id) {
        this.entityData.set(ATTACKING_ENTITY_ID, id);
    }

    @Nullable
    public Entity getAttackingEntity() {
        int id = this.getAttackingEntityId();
        return id == -1 ? null : this.level().getEntity(id);
    }

    public int getDelaySpawn() {
        return this.entityData.get(DELAY_SPAWN);
    }

    public void setDelaySpawn(int ticks) {
        this.entityData.set(DELAY_SPAWN, ticks);
    }

    public int getPotency() {
        return this.entityData.get(POTENCY);
    }

    public void setPotency(int potency) {
        this.entityData.set(POTENCY, potency);
    }

    public int getBonusDamage() {
        return this.entityData.get(BONUS_DAMAGE);
    }

    public void setBonusDamage(int bonus) {
        this.entityData.set(BONUS_DAMAGE, Math.max(0, bonus));
    }

    public boolean isFading() {
        return this.entityData.get(FADING);
    }

    public void setFading(boolean fading) {
        this.entityData.set(FADING, fading);
    }

    public boolean isUsingAbility() {
        return this.entityData.get(USING_ABILITY);
    }

    public void setUsingAbility(boolean using) {
        this.entityData.set(USING_ABILITY, using);
    }

    public float getFadeIn(float partialTicks) {
        return (this.prevFadeIn + (this.fadeIn - this.prevFadeIn) * partialTicks) / MAX_FADE;
    }

    public float getAbilityProgress(float partialTicks) {
        return (this.prevAbilityProgress + (this.abilityProgress - this.prevAbilityProgress) * partialTicks) / MAX_ABILITY;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("OwnerUUID")) {
            this.setOwnerUUID(tag.getUUID("OwnerUUID"));
        }
        this.setPotency(tag.getInt("Potency"));
        this.setBonusDamage(tag.getInt("BonusDamage"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        UUID uuid = this.getOwnerUUID();
        if (uuid != null) {
            tag.putUUID("OwnerUUID", uuid);
        }
        tag.putInt("Potency", this.getPotency());
        tag.putInt("BonusDamage", this.getBonusDamage());
    }
}
