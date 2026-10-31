package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.DustCloudParticleOption;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.client.particles.VerticalCircleExplodeParticleOption;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.TrailEffect;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import java.util.List;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.joml.Vector3f;

public class ChasseurArrow extends AbstractArrow {

    private static final float EXPLOSION_RADIUS = 2.0F;
    private static final double MAX_DISTANCE_SQR = 64.0D * 64.0D;
    public final TrailEffect trail = new TrailEffect(0.2F, 4.0F);
    public final TrailEffect trailOuter = new TrailEffect(0.4F, 4.0F);
    private boolean exploded;

    public ChasseurArrow(EntityType<? extends ChasseurArrow> type, Level level) {
        super(type, level);
        this.setBaseDamage(5.0D);
        this.setNoGravity(true);
    }

    public ChasseurArrow(Level level, LivingEntity shooter) {
        super(ModEntityTypes.CHASSEUR_ARROW.get(), shooter, level);
        this.setBaseDamage(5.0D);
        this.setNoGravity(true);
    }

    public ChasseurArrow(Level level, double x, double y, double z) {
        super(ModEntityTypes.CHASSEUR_ARROW.get(), x, y, z, level);
        this.setBaseDamage(5.0D);
        this.setNoGravity(true);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntityTypes.CHASSEUR_ARROW.get();
    }

    public boolean hasTrail() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            Entity owner = this.getOwner();
            if (owner != null && this.distanceToSqr(owner) > MAX_DISTANCE_SQR) {
                this.explode(null);
                return;
            }
        }
        if (this.level().isClientSide && this.tickCount > 2) {
            Vec3 oldPos = new Vec3(this.xo, this.yo + this.getBbHeight() / 2.0F, this.zo);
            this.trail.update(oldPos);
            this.trailOuter.update(oldPos);
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        Entity owner = this.getOwner();
        if (target == owner) {
            return false;
        }
        if (owner instanceof LivingEntity livingOwner && target instanceof LivingEntity livingTarget
                && MobUtil.areAllies(livingOwner, livingTarget)) {
            return false;
        }
        return super.canHitEntity(target);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.level().isClientSide) {
            return;
        }
        Entity target = result.getEntity();
        if (target instanceof LivingEntity living && living.isAlive()) {
            living.invulnerableTime = 0;
            living.hurt(this.magicDamageSource(), this.ownerAttackDamage() * 1.6F);
        }
        this.explode(target);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide) {
            this.explode(null);
        }
    }

    private void explode(Entity directTarget) {
        if (this.exploded) {
            return;
        }
        this.exploded = true;
        if (!this.level().isClientSide) {
            Entity owner = this.getOwner();
            LivingEntity livingOwner = owner instanceof LivingEntity living ? living : null;
            DamageSource explosionSource = this.damageSources().explosion(this, livingOwner);
            Vec3 center = directTarget != null ? directTarget.getBoundingBox().getCenter() : this.position();
            double diameter = EXPLOSION_RADIUS * 2.0D;
            AABB bounds = new AABB(
                    center.x - diameter - 1.0D, center.y - diameter - 1.0D, center.z - diameter - 1.0D,
                    center.x + diameter + 1.0D, center.y + diameter + 1.0D, center.z + diameter + 1.0D);
            List<Entity> entities = this.level().getEntities(this, bounds);
            for (Entity entity : entities) {
                if (entity == this || entity == directTarget || entity == livingOwner || !entity.isAlive()) {
                    continue;
                }
                if (livingOwner != null && entity instanceof LivingEntity living
                        && MobUtil.areAllies(livingOwner, living)) {
                    continue;
                }
                if (entity.ignoreExplosion()) {
                    continue;
                }
                double distanceRatio = Math.sqrt(entity.distanceToSqr(center)) / diameter;
                if (distanceRatio > 1.0D) {
                    continue;
                }
                double dx = entity.getX() - center.x;
                double dy = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - center.y;
                double dz = entity.getZ() - center.z;
                double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (length == 0.0D) {
                    continue;
                }
                dx /= length;
                dy /= length;
                dz /= length;
                double seenPercent = Explosion.getSeenPercent(center, entity);
                double exposure = (1.0D - distanceRatio) * seenPercent;
                int damage = (int) ((exposure * exposure + exposure) / 2.0D * 7.0D * diameter + 1.0D);
                entity.hurt(explosionSource, damage);
                double knockback = entity instanceof LivingEntity living
                        ? ProtectionEnchantment.getExplosionKnockbackAfterDampener(living, exposure)
                        : exposure;
                entity.setDeltaMovement(entity.getDeltaMovement().add(
                        new Vec3(dx * knockback, dy * knockback, dz * knockback)));
            }
            this.spawnExplosionParticles(center, directTarget);
            this.level().playSound(null, center.x, center.y, center.z,
                    SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.8F, 1.0F);
        }
        this.discard();
    }

    private void spawnExplosionParticles(Vec3 center, Entity groundSource) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ColorUtil red = new ColorUtil(1.0F, 0.0F, 0.0F);
        ColorUtil gold = new ColorUtil(1.0F, 0.72F, 0.1F);
        serverLevel.sendParticles(new CircleExplodeParticleOption(red, 4.0F, 1),
                center.x, center.y, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(new CircleExplodeParticleOption(red, 2.0F, 1),
                center.x, center.y, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(new CircleExplodeParticleOption(gold, 3.0F, 1),
                center.x, center.y, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(new CircleExplodeParticleOption(gold, 1.5F, 1),
                center.x, center.y, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(new VerticalCircleExplodeParticleOption(1.0F, 0.0F, 0.0F, 4.0F, 1),
                center.x, center.y, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(new VerticalCircleExplodeParticleOption(1.0F, 0.0F, 0.0F, 2.0F, 1),
                center.x, center.y, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(new VerticalCircleExplodeParticleOption(gold.red(), gold.green(), gold.blue(), 3.0F, 1),
                center.x, center.y, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(new SphereExplodeParticleOption(red, 4.0F, 1),
                center.x, center.y + 0.5D, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        double groundY = BlockFinder.moveDownToGround(groundSource != null ? groundSource : this);
        serverLevel.sendParticles(new CircleExplodeParticleOption(red, 4.0F, 1),
                center.x, groundY, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(new CircleExplodeParticleOption(gold, 2.0F, 1),
                center.x, groundY, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        DustCloudParticleOption cloud = new DustCloudParticleOption(
                new Vector3f(gold.red(), gold.green(), gold.blue()), 1.0F);
        for (int i = 0; i < 2; ++i) {
            ServerParticleUtil.circularParticles(serverLevel, cloud,
                    center.x, center.y + 0.25D, center.z, 0.0D, 0.14D, 0.0D, 1.0F);
        }
    }

    private float ownerAttackDamage() {
        Entity owner = this.getOwner();
        if (owner instanceof LivingEntity living) {
            return (float) living.getAttributeValue(Attributes.ATTACK_DAMAGE);
        }
        return (float) this.getBaseDamage();
    }

    private DamageSource magicDamageSource() {
        Entity owner = this.getOwner();
        return owner instanceof LivingEntity living
                ? this.damageSources().indirectMagic(this, living)
                : this.damageSources().magic();
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (this.level().isClientSide) {
            this.trail.renderPoints.clear();
            this.trailOuter.renderPoints.clear();
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
