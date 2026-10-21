package com.qiuyue.goetyominous.common.entities.ally.mm;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.compat.mm.MmScreenShake;
import com.alexander.mutantmore.init.EffectInit;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.init.TagInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.google.common.collect.Lists;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import java.util.List;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

public class ThrownIcicleServant extends ThrowableProjectile {
    public float damage = 0.0F;
    public int freezingLength = 0;
    public int freezingLevel = 0;
    public int disableShieldLength = 0;
    public boolean ignoresInvulTime = true;
    public List<Entity> alreadyHit = Lists.newArrayList();
    public boolean spellOwned;

    public ThrownIcicleServant(EntityType<? extends ThrownIcicleServant> p_37391_, Level p_37392_) {
        super(p_37391_, p_37392_);
    }

    public ThrownIcicleServant(Level p_37399_, LivingEntity p_37400_) {
        super(MmEntityRegistry.THROWN_ICICLE_SERVANT.get(), p_37400_, p_37399_);
    }

    private boolean isSpellFriendly(Entity target) {
        if (!this.spellOwned || this.getOwner() == null) {
            return false;
        }
        if (target == this.getOwner()) {
            return true;
        }
        LivingEntity root = this.getOwner() instanceof IOwned owned
                ? owned.getMasterOwner()
                : (LivingEntity) this.getOwner();
        if (target instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return true;
        }
        return target instanceof IOwned owned && owned.getMasterOwner() == root
                || target instanceof OwnableEntity ownable && ownable.getOwner() == root
                || MobUtil.areAllies(root, target);
    }

    protected void onHitEntity(EntityHitResult p_37386_) {
        super.onHitEntity(p_37386_);
        if (!this.level().isClientSide) {
            Entity entity = p_37386_.getEntity();
            Entity entity1 = this.getOwner();
            if (!this.alreadyHit.contains(entity) && this.canHarm(entity)) {
                if (this.ignoresInvulTime) {
                    entity.invulnerableTime = 0;
                }
                boolean flag = entity.hurt(MMDamageTypes.icicleAttack(this.damageSources(), this, entity1), this.damage);
                if (entity instanceof LivingEntity) {
                    if (flag) {
                        if (((LivingEntity) entity).hasEffect(EffectInit.FREEZING.get())) {
                            ((LivingEntity) entity).addEffect(new MobEffectInstance(EffectInit.FREEZING.get(), ((LivingEntity) entity).getEffect(EffectInit.FREEZING.get()).getDuration() + this.freezingLength, this.freezingLevel));
                        } else {
                            ((LivingEntity) entity).addEffect(new MobEffectInstance(EffectInit.FREEZING.get(), this.freezingLength, this.freezingLevel));
                        }
                    }
                    MiscUtils.disableShield((LivingEntity) p_37386_.getEntity(), this.disableShieldLength);
                }
                if (entity1 instanceof LivingEntity) {
                    this.alreadyHit.add(entity);
                    this.doEnchantDamageEffects((LivingEntity) entity1, entity);
                }
            }
        }
    }

    protected void onHitBlock(BlockHitResult p_37258_) {
        super.onHitBlock(p_37258_);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 4);
            this.discard();
        }
    }

    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            MmScreenShake.shake(this.level(), 3, 0.0075F, this.blockPosition(), 5);
        }
    }

    public void handleEntityEvent(byte p_19882_) {
        if (p_19882_ == 4) {
            for (int i = 0; i < 20; ++i) {
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.POWDER_SNOW.defaultBlockState()).setPos(this.blockPosition()), this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), 0.0, 0.0, 0.0);
            }
        } else {
            super.handleEntityEvent(p_19882_);
        }
    }

    boolean canHarm(Entity target) {
        if (this.isSpellFriendly(target)) {
            return false;
        }
        if (MiscUtils.canHarmBasedOnTeamAndTag(TagInit.EntityTypes.MUTANT_FROZEN_ZOMBIE_THROWN_ICICLE_CANT_HURT, this, target, this.getOwner(), entity -> entity instanceof MutantFrozenZombieServant)) {
            return true;
        }
        if (MiscUtils.canHarmBasedOnTeamAndTag(TagInit.EntityTypes.PLAYER_THROWN_ICICLE_CANT_HURT, this, target, this.getOwner(), entity -> entity instanceof Player)) {
            return true;
        }
        return MiscUtils.canHarmBasedOnTeamAndTag(null, this, target, this.getOwner(), entity -> !(entity instanceof MutantFrozenZombieServant) && !(entity instanceof Player));
    }

    boolean canHit(Entity target) {
        if (this.isSpellFriendly(target)) {
            return false;
        }
        if (MiscUtils.canHarmBasedOnTeamAndTag(TagInit.EntityTypes.MUTANT_FROZEN_ZOMBIE_THROWN_ICICLE_CANT_HIT, this, target, this.getOwner(), entity -> entity instanceof MutantFrozenZombieServant)) {
            return true;
        }
        if (MiscUtils.canHarmBasedOnTeamAndTag(TagInit.EntityTypes.PLAYER_THROWN_ICICLE_CANT_HIT, this, target, this.getOwner(), entity -> entity instanceof Player)) {
            return true;
        }
        return MiscUtils.canHarmBasedOnTeamAndTag(null, this, target, this.getOwner(), entity -> !(entity instanceof MutantFrozenZombieServant) && !(entity instanceof Player));
    }

    protected boolean canHitEntity(Entity target) {
        return this.canHit(target) && super.canHitEntity(target);
    }

    protected void defineSynchedData() {
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", this.damage);
        tag.putInt("FreezingLevel", this.freezingLevel);
        tag.putInt("FreezingLength", this.freezingLength);
        tag.putInt("DisableShieldTime", this.disableShieldLength);
        tag.putBoolean("IgnoresInvulTime", this.ignoresInvulTime);
        tag.putBoolean("SpellOwned", this.spellOwned);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Damage")) {
            this.damage = tag.getFloat("Damage");
        }
        if (tag.contains("FreezingLevel")) {
            this.freezingLevel = tag.getInt("FreezingLevel");
        }
        if (tag.contains("FreezingLength")) {
            this.freezingLength = tag.getInt("FreezingLength");
        }
        if (tag.contains("DisableShieldTime")) {
            this.disableShieldLength = tag.getInt("DisableShieldTime");
        }
        if (tag.contains("IgnoresInvulTime")) {
            this.ignoresInvulTime = tag.getBoolean("IgnoresInvulTime");
        }
        this.spellOwned = tag.getBoolean("SpellOwned");
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
