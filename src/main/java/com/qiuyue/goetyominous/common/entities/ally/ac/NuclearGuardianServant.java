package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.events.ac.NucleeperNukeProtectionHandler;
import com.qiuyue.goetyominous.common.magic.spells.ac.XRaySpell;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import com.qiuyue.goetyominous.common.network.ac.NuclearExplosionEffectPacket;
import com.qiuyue.goetyominous.common.network.XRayPacket;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.utils.IRayMuzzle;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class NuclearGuardianServant extends MineGuardianServant implements IRayMuzzle {

    private static final int BEAM_CHARGE = 20;
    private static final int BEAM_DURATION = 100;
    private static final int BEAM_COOLDOWN = 40;
    private static final int HIT_INTERVAL = 3;
    private static final int SYNC_INTERVAL = 2;
    private static final double BEAM_REACH = 128.0D;
    private static final double SYNC_RANGE = 128.0D;
    private static final float BEAM_RADIUS = 1.0F;
    private static final double AIM_TURN_RATE = 0.2D;
    private static final float BODY_TURN_SPEED = 11.5F;
    private static final double AIM_FIRE_DOT = 0.978D;
    private static final int BEAM_SWITCH_HOLD = 10;
    private static final double EYE_FORWARD = 1.09D;
    private static final float NUKE_SIZE = 1.0F;
    private static final float BLAST_RADIUS = 5.0F;
    private static final int RADIATION_LEVEL = 2;

    private static final EntityDataAccessor<Boolean> DATA_LASERING =
            SynchedEntityData.defineId(NuclearGuardianServant.class, EntityDataSerializers.BOOLEAN);

    private int beamCooldown;
    private boolean manuallyIgnited;

    public NuclearGuardianServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.NuclearGuardianServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.58F;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_LASERING, false);
    }

    public boolean isLasering() {
        return this.entityData.get(DATA_LASERING);
    }

    public void setLasering(boolean lasering) {
        this.entityData.set(DATA_LASERING, lasering);
    }

    @Override
    protected float tickHeadTurn(float bodyRotation, float headRotation) {
        float result = super.tickHeadTurn(bodyRotation, headRotation);
        if (this.isLasering()) {
            this.yBodyRot = this.getYRot();
        }
        return result;
    }

    @Override
    public Vec3 getRayMuzzle(float partialTicks) {
        Vec3 base = this.getEyePosition(partialTicks);
        float yaw = Mth.lerp(partialTicks, this.yBodyRotO, this.yBodyRot) * ((float) Math.PI / 180.0F);
        return base.add(-Mth.sin(yaw) * EYE_FORWARD, 0.0D, Mth.cos(yaw) * EYE_FORWARD);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(ItemTags.CREEPER_IGNITERS)
                && this.isAlive() && !this.isExploding()
                && player == this.getTrueOwner()) {
            SoundEvent soundEvent = itemStack.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
            this.level().playSound(player, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.level().isClientSide) {
                this.manuallyIgnited = true;
                this.setExploding(true);
                itemStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected boolean spawnsExplosionParticles() {
        return false;
    }

    @Override
    protected void explode() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 origin = this.position();
        Set<UUID> ownerIds = NucleeperNukeProtectionHandler.collectOwnerIds(this);
        NucleeperNukeProtectionHandler.registerProtection(serverLevel, origin, NUKE_SIZE, ownerIds);
        NucleeperNukeProtectionHandler.syncZoneToClients(serverLevel, origin, NUKE_SIZE, ownerIds);
        NucleeperNukeProtectionHandler.irradiateBlast(serverLevel, origin, BLAST_RADIUS * 2.0D + 1.0D, RADIATION_LEVEL);
        NuclearExplosionEffectPacket.send(serverLevel, origin, NUKE_SIZE);
        super.explode();
    }

    @Override
    protected boolean isDesperate() {
        return this.manuallyIgnited
                || this.getHealth() <= this.getMaxHealth() * AttributesConfig.NuclearGuardianServantSelfDestructHealth.get().floatValue();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new LaserGoal());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.manuallyIgnited && !this.level().isClientSide && this.isAlive() && !this.isExploding()) {
            this.setExploding(true);
        }
        if (this.beamCooldown > 0) {
            --this.beamCooldown;
        }
    }

    private static Vec3 aimTowards(Vec3 from, Vec3 to, double maxRadians) {
        double angle = Math.acos(Mth.clamp(from.dot(to), -1.0D, 1.0D));
        if (angle <= maxRadians) {
            return to;
        }
        Vec3 axis = from.cross(to);
        if (axis.lengthSqr() < 1.0E-8D) {
            axis = new Vec3(0.0D, 1.0D, 0.0D).cross(from);
            if (axis.lengthSqr() < 1.0E-8D) {
                return to;
            }
        }
        axis = axis.normalize();
        return from.scale(Math.cos(maxRadians)).add(axis.cross(from).scale(Math.sin(maxRadians))).normalize();
    }

    private class LaserGoal extends Goal {

        private int beamTime;
        private int holdTime;
        private LivingEntity beamTarget;
        private Vec3 aim;

        LaserGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return NuclearGuardianServant.this.beamCooldown <= 0 && this.hasTarget();
        }

        @Override
        public boolean canContinueToUse() {
            return this.hasTarget();
        }

        private boolean hasTarget() {
            NuclearGuardianServant servant = NuclearGuardianServant.this;
            LivingEntity target = servant.getTarget();
            return !servant.isDesperate() && !servant.isStaying() && target != null && target.isAlive();
        }

        @Override
        public void start() {
            NuclearGuardianServant servant = NuclearGuardianServant.this;
            this.beamTime = 0;
            this.holdTime = 0;
            this.beamTarget = null;
            this.aim = servant.getViewVector(1.0F);
            servant.getNavigation().stop();
            servant.setLasering(true);
        }

        @Override
        public void stop() {
            NuclearGuardianServant servant = NuclearGuardianServant.this;
            servant.setLasering(false);
            servant.beamCooldown = BEAM_COOLDOWN;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            NuclearGuardianServant servant = NuclearGuardianServant.this;
            LivingEntity target = servant.getTarget();
            if (target == null) {
                return;
            }
            servant.getNavigation().stop();
            if (!servant.hasLineOfSight(target)) {
                servant.setTarget(null);
                return;
            }
            Vec3 from = servant.getRayMuzzle(1.0F);
            Vec3 desired = target.getEyePosition(1.0F).subtract(from);
            if (desired.lengthSqr() < 1.0E-6D) {
                return;
            }
            desired = desired.normalize();
            this.aim = aimTowards(this.aim, desired, AIM_TURN_RATE);
            float beamYaw = (float) (Mth.atan2(this.aim.z, this.aim.x) * (180.0D / Math.PI)) - 90.0F;
            servant.setYRot(Mth.approachDegrees(servant.getYRot(), beamYaw, BODY_TURN_SPEED));
            if (target != this.beamTarget) {
                this.beamTarget = target;
                this.beamTime = 0;
                this.holdTime = BEAM_SWITCH_HOLD;
            }
            if (this.holdTime > 0) {
                --this.holdTime;
                return;
            }
            boolean aligned = this.aim.dot(desired) >= AIM_FIRE_DOT;
            ++this.beamTime;
            if (this.beamTime == 1) {
                servant.playSound(ACSoundRegistry.RAYGUN_START.get(), 2.0F, 1.0F);
            }
            int active = this.beamTime - BEAM_CHARGE;
            if (active == 0) {
                servant.playSound(ACSoundRegistry.RAYGUN_LOOP.get(), 2.0F, 1.0F);
            }
            if (active > BEAM_DURATION) {
                servant.setTarget(null);
                return;
            }
            this.beam(from, active, aligned);
        }

        private void beam(Vec3 from, int active, boolean aligned) {
            NuclearGuardianServant servant = NuclearGuardianServant.this;
            double reach = BEAM_REACH;
            if (active < 0) {
                reach *= (double) this.beamTime / BEAM_CHARGE;
            }
            Vec3 to = from.add(this.aim.scale(reach));
            Vec3 end = servant.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, servant)).getLocation();
            HitResult entityHit = ProjectileUtil.getEntityHitResult(servant, from, to,
                    new AABB(from, to).inflate(1.0D),
                    Entity::canBeHitByProjectile, from.distanceToSqr(to));
            if (entityHit instanceof EntityHitResult hit && hit.getLocation().distanceToSqr(from) <= end.distanceToSqr(from)) {
                end = hit.getLocation();
            }
            if (!(servant.level() instanceof ServerLevel serverLevel)) {
                return;
            }
            if (this.beamTime % SYNC_INTERVAL == 0) {
                ModNetwork.CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                                servant.getX(), servant.getY(), servant.getZ(), SYNC_RANGE, serverLevel.dimension())),
                        new XRayPacket(servant.getId(), end, false));
            }
            if (active >= 0 && aligned && active % HIT_INTERVAL == 0) {
                XRaySpell.hurtAround(serverLevel, servant, end, BEAM_RADIUS, false,
                        AttributesConfig.NuclearGuardianServantBeamDamage.get().floatValue());
            }
        }
    }
}
