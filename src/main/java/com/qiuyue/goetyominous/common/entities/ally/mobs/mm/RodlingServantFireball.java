package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.utils.MobUtil;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

public class RodlingServantFireball extends Fireball {
    public float damage = 0.0F;
    public boolean griefing = true;
    public int fireLength = 0;
    public boolean ignoresInvulTime = true;

    public RodlingServantFireball(EntityType<? extends RodlingServantFireball> type, Level level) {
        super(type, level);
    }

    public RodlingServantFireball(Level level, LivingEntity shooter, double dx, double dy, double dz) {
        super(MmEntityRegistry.RODLING_SERVANT_FIREBALL.get(), shooter, dx, dy, dz, level);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", this.damage);
        tag.putBoolean("Griefing", this.griefing);
        tag.putInt("FireLength", this.fireLength);
        tag.putBoolean("IgnoresInvulTime", this.ignoresInvulTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Damage")) this.damage = tag.getFloat("Damage");
        if (tag.contains("Griefing")) this.griefing = tag.getBoolean("Griefing");
        if (tag.contains("FireLength")) this.fireLength = tag.getInt("FireLength");
        if (tag.contains("IgnoresInvulTime")) this.ignoresInvulTime = tag.getBoolean("IgnoresInvulTime");
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (this.allyOfMyOwner(target)) {
            return com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig.friendly_fire_on.get()
                    && super.canHitEntity(target);
        }
        return this.canHit(target) && super.canHitEntity(target);
    }

    boolean allyOfMyOwner(Entity target) {
        Entity owner = this.getOwner();
        return owner instanceof LivingEntity livingOwner && MobUtil.areAllies(livingOwner, target);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!this.level().isClientSide) {
            Entity entity = hitResult.getEntity();
            Entity owner = this.getOwner();
            if (this.canHarm(entity)) {
                LivingEntity master = this.getOwner() instanceof LivingEntity shooter
                        && shooter instanceof com.Polarice3.Goety.api.entities.IOwned owned
                        ? owned.getMasterOwner() : (this.getOwner() instanceof LivingEntity living ? living : null);
                boolean netherRobe = master != null
                        && com.Polarice3.Goety.utils.CuriosFinder.hasNetherRobe(master);
                boolean fireImmune = entity.fireImmune();
                if (fireImmune && !netherRobe) {
                    return;
                }
                if (this.ignoresInvulTime) {
                    entity.invulnerableTime = 0;
                }
                entity.hurt(MMDamageTypes.fireResBypassingFireball(this.damageSources(), this, owner instanceof LivingEntity ? owner : null),
                        fireImmune ? this.damage * 0.5F : this.damage);
                entity.setSecondsOnFire(this.fireLength);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (!this.level().isClientSide && this.griefing) {
            net.minecraft.core.BlockPos firePos = hitResult.getBlockPos().relative(hitResult.getDirection());
            if (this.level().isEmptyBlock(firePos)) {
                this.level().setBlockAndUpdate(firePos, BaseFireBlock.getState(this.level(), firePos));
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= 120) {
            this.discard();
        }
    }

    boolean canHarm(Entity target) {
        if (!(this.getOwner() instanceof LivingEntity owner)) {
            return true;
        }
        return !MobUtil.areAllies(owner, target);
    }

    boolean canHit(Entity target) {
        if (!(this.getOwner() instanceof LivingEntity owner)) {
            return true;
        }
        return !MobUtil.areAllies(owner, target);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
