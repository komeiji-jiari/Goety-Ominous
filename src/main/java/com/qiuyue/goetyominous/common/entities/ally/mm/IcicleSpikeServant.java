package com.qiuyue.goetyominous.common.entities.ally.mm;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.MobUtil;
import com.alexander.mutantmore.init.EffectInit;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.init.TagInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.google.common.collect.Lists;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class IcicleSpikeServant extends Entity {
    public boolean spellOwned;
    public int startDelay;
    public AnimationState spikeAnimationState = new AnimationState();
    public int spikeAnimationLength = 60;
    public int spikeAnimationTick = 0;
    public float damage = 0.0F;
    public int freezingLength = 0;
    public int freezingLevel = 0;
    public boolean ignoresInvulTime = true;
    public float xRotation;
    public float yRotation;
    public LivingEntity owner;
    public List<Entity> alreadyHurtEntities = Lists.newArrayList();
    public List<IcicleSpikeServant> connectedSpikes = Lists.newArrayList();

    public IcicleSpikeServant(EntityType<?> type, Level level) {
        super(type, level);
    }

    public IcicleSpikeServant(Level level, int startDelay, LivingEntity owner) {
        super(MmEntityRegistry.ICICLE_SPIKE_SERVANT.get(), level);
        this.startDelay = startDelay;
        this.owner = owner;
        this.setXRot((float) this.random.nextGaussian() * 10.0F);
        this.setYRot(this.random.nextFloat() * 360.0F);
    }

    protected void defineSynchedData() {
    }

    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("StartDelay")) {
            this.startDelay = tag.getInt("StartDelay");
        }
        if (tag.contains("Damage")) {
            this.damage = tag.getFloat("Damage");
        }
        if (tag.contains("FreezingLevel")) {
            this.freezingLevel = tag.getInt("FreezingLevel");
        }
        if (tag.contains("FreezingLength")) {
            this.freezingLength = tag.getInt("FreezingLength");
        }
        if (tag.contains("IgnoresInvulTime")) {
            this.ignoresInvulTime = tag.getBoolean("IgnoresInvulTime");
        }
        this.spellOwned = tag.getBoolean("SpellOwned");
        if (tag.contains("OwnerID") && tag.getUUID("OwnerID") != null && !this.level().isClientSide) {
            Entity entity = ((ServerLevel) this.level()).getEntity(tag.getUUID("OwnerID"));
            this.owner = entity instanceof LivingEntity ? (LivingEntity) entity : null;
        }
    }

    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("StartDelay", this.startDelay);
        tag.putFloat("Damage", this.damage);
        tag.putInt("FreezingLevel", this.freezingLevel);
        tag.putInt("FreezingLength", this.freezingLength);
        tag.putBoolean("IgnoresInvulTime", this.ignoresInvulTime);
        tag.putBoolean("SpellOwned", this.spellOwned);
        if (this.owner != null) {
            tag.putUUID("OwnerID", this.owner.getUUID());
        }
    }

    public void handleEntityEvent(byte p_19882_) {
        if (p_19882_ == 4) {
            this.spikeAnimationTick = this.spikeAnimationLength;
            this.spikeAnimationState.start(this.tickCount);
        } else {
            super.handleEntityEvent(p_19882_);
        }
    }

    public void tick() {
        super.tick();
        if (!this.spikeAnimationState.isStarted()) {
            BlockPos blockpos = this.blockPosition().below();
            BlockState blockstate = this.level().getBlockState(blockpos);
            if (!blockstate.isAir() && this.random.nextBoolean()) {
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(blockpos), (double) blockpos.getX() + this.random.nextGaussian() * 0.1, (double) blockpos.above().getY() + 0.1, (double) blockpos.getZ() + this.random.nextGaussian() * 0.1, this.random.nextGaussian(), 0.0, this.random.nextGaussian());
            }
        }
        if (!this.level().isClientSide) {
            if (this.tickCount >= this.startDelay && this.spikeAnimationTick <= 0) {
                this.spikeAnimationTick = this.spikeAnimationLength;
                this.level().broadcastEntityEvent(this, (byte) 4);
            }
            if (this.tickCount == this.startDelay + 2) {
                LivingEntity root = this.owner instanceof IOwned ownedOwner
                        ? ownedOwner.getMasterOwner()
                        : this.owner;
                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
                    if (entity == this.owner || !this.canHarm(entity) || this.alreadyHurtEntities.contains(entity)) continue;
                    if (this.spellOwned) {
                        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) continue;
                        if (root != null && (entity == root
                                || entity instanceof IOwned owned && owned.getMasterOwner() == root
                                || entity instanceof OwnableEntity ownable && ownable.getOwner() == root
                                || MobUtil.areAllies(root, entity))) {
                            continue;
                        }
                    }
                    if (this.ignoresInvulTime) {
                        entity.invulnerableTime = 0;
                    }
                    entity.hurtMarked = true;
                    entity.push(0.0, 0.25, 0);
                    boolean flag = entity.hurt(MMDamageTypes.icicleAttack(this.damageSources(), this, this.owner), this.damage);
                    if (flag) {
                        if (entity.hasEffect(EffectInit.FREEZING.get())) {
                            entity.addEffect(new MobEffectInstance(EffectInit.FREEZING.get(), entity.getEffect(EffectInit.FREEZING.get()).getDuration() + this.freezingLength, this.freezingLevel));
                        } else {
                            entity.addEffect(new MobEffectInstance(EffectInit.FREEZING.get(), this.freezingLength, this.freezingLevel));
                        }
                    }
                    for (IcicleSpikeServant spike : this.connectedSpikes) {
                        spike.alreadyHurtEntities.add(entity);
                    }
                }
            }
            if (this.spikeAnimationTick > 0) {
                --this.spikeAnimationTick;
            }
            if (this.spikeAnimationTick == 1) {
                this.discard();
            }
        }
    }

    boolean canHarm(Entity target) {
        if (MiscUtils.canHarmBasedOnTeamAndTag(TagInit.EntityTypes.MUTANT_FROZEN_ZOMBIE_CANT_HURT, this, target, this.owner, entity -> entity instanceof MutantFrozenZombieServant)) {
            return true;
        }
        return MiscUtils.canHarmBasedOnTeamAndTag(null, this, target, this.owner, entity -> !(entity instanceof MutantFrozenZombieServant));
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static IcicleSpikeServant spawn(ServerLevel level, LivingEntity owner, Vec3 pos, int startDelay,
                                           float damage, int freezingLength, int freezingLevel,
                                           boolean ignoresInvulnerability, boolean spellOwned) {
        IcicleSpikeServant icicle = new IcicleSpikeServant(level, startDelay, owner);
        icicle.moveTo(pos);
        icicle.damage = damage;
        icicle.freezingLength = freezingLength;
        icicle.freezingLevel = freezingLevel;
        icicle.ignoresInvulTime = ignoresInvulnerability;
        icicle.spellOwned = spellOwned;
        PositionUtils.moveToCorrectHeight(icicle, true);
        if (!icicle.isRemoved()) {
            level.addFreshEntityWithPassengers(icicle);
        }
        return icicle;
    }
}
