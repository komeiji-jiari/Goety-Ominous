package com.qiuyue.goetyominous.common.entities.hostile;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.blocks.entities.PithosBlockEntity;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.hostile.SkullLord;
import com.Polarice3.Goety.common.entities.projectiles.HauntedSkullProjectile;
import com.Polarice3.Goety.common.magic.spells.ShockwaveSpell;
import com.Polarice3.Goety.common.magic.spells.storm.ElectroOrbSpell;
import com.Polarice3.Goety.config.AttributesConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.common.mixin.RandomizableContainerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.UUID;

import static com.Polarice3.Goety.client.particles.ModParticleTypes.BIG_SOUL_FIRE;
import static com.Polarice3.Goety.client.particles.ModParticleTypes.BIG_SOUL_FIRE_DROP;

public class OminousSkullLord extends SkullLord {

    private static final UUID OMINOUS_HEALTH_ID = UUID.fromString("b7e1f2a4-6c3d-4f8e-9a1b-2c3d4e5f6a7b");
    private static final double OMINOUS_HEALTH_BONUS = 220.0D / 150.0D - 1.0D;

    private int ominousShockWaveCooldown;

    public OminousSkullLord(EntityType<? extends OminousSkullLord> type, Level level) {
        super(type, level);
        this.applyOminousHealth();
        this.setHealth(this.getMaxHealth());
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (entity instanceof IOwned owned && owned.getTrueOwner() == this) {
            return true;
        }
        return super.isAlliedTo(entity);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SkullLordDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MAX_HEALTH, AttributesConfig.SkullLordHealth.get())
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    public void setConfigurableAttributes() {
        super.setConfigurableAttributes();
        this.applyOminousHealth();
        if (this.getHealth() > this.getMaxHealth()) {
            this.setHealth(this.getMaxHealth());
        }
    }

    private void applyOminousHealth() {
        AttributeInstance maxHealth = this.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && maxHealth.getModifier(OMINOUS_HEALTH_ID) == null) {
            maxHealth.addPermanentModifier(new AttributeModifier(OMINOUS_HEALTH_ID,
                    "goetyominous:ominous_skull_lord_health", OMINOUS_HEALTH_BONUS,
                    AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.removeAllGoals(goal -> goal.getClass().getEnclosingClass() == SkullLord.class);
        this.goalSelector.addGoal(4, new SoulSkullGoal());
        this.goalSelector.addGoal(4, new ChargeAttackGoal());
        this.goalSelector.addGoal(4, new ElectroOrbAttackGoal());
        this.goalSelector.addGoal(4, new ShockwaveAttackGoal());
        this.goalSelector.addGoal(8, new MoveRandomGoal());
        this.goalSelector.addGoal(7, new LookAroundGoal());
    }

    @Override
    public void tick() {
        if (this.ominousShockWaveCooldown > 0) {
            --this.ominousShockWaveCooldown;
        }
        super.tick();
        if (this.getBoneLord() != null && !this.getBoneLord().isDeadOrDying()) {
            this.boneLordRegen = 200;
        }
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel
                && !this.isDeadOrDying() && !this.isSpawning() && !this.isShockWave() && !this.isElectroOrb()) {
            ServerParticleUtil.gatheringParticles(ModParticleTypes.SOUL_FLAME_GATHER.get(), this, serverLevel, 1);
        }
        if (this.level().isClientSide && this.isAlive()) {
            this.emitOminousParticles();
            this.emitCandleFlames();
        }
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos origin = this.getBoundOrigin();
        if (origin == null) {
            return;
        }
        if (!serverLevel.isLoaded(origin)) {
            serverLevel.getChunkAt(origin);
        }
        BlockEntity blockEntity = serverLevel.getBlockEntity(origin);
        if (!(blockEntity instanceof PithosBlockEntity pithos)) {
            return;
        }
        ResourceLocation lootTable = ((RandomizableContainerAccessor) pithos).goetyominous$getLootTable();
        if (!ModLootTables.CRYPT_TOMB.equals(lootTable)) {
            return;
        }
        ItemStack reward = this.createPithosReward(serverLevel.random);
        for (int i = 0; i < pithos.getContainerSize(); i++) {
            if (pithos.getItem(i).isEmpty()) {
                pithos.setItem(i, reward);
                return;
            }
        }
    }

    private ItemStack createPithosReward(RandomSource random) {
        ItemStack stack = new ItemStack(ModItems.OMINOUS_CANDLE.get());
        int maxDamage = stack.getMaxDamage();
        stack.setDamageValue(Mth.nextInt(random, maxDamage / 10, maxDamage / 2));
        EnchantmentHelper.enchantItem(random, stack, 30, false);
        return stack;
    }

    private void emitCandleFlames() {
        if (this.tickCount % 40 == 0) {
            int index = this.tickCount / 40;
            this.emitCandleFlame((index & 1) == 0 ? -1.0D : 1.0D, (index & 2) == 0);
        }
    }

    private void emitCandleFlame(double side, boolean drop) {
        double xm = side * 6.9375D / 16.0D;
        double ym = -14.8126D / 16.0D;
        double zm = 0.4375D / 16.0D;
        float pitch = this.getXRot() * Mth.DEG_TO_RAD;
        double yaw = (180.0F - this.yHeadRot) * Mth.DEG_TO_RAD;

        double xs = -xm;
        double ys = -ym;
        double zs = zm;

        double yp = ys * Math.cos(-pitch) - zs * Math.sin(-pitch);
        double zp = ys * Math.sin(-pitch) + zs * Math.cos(-pitch);
        double xw = xs * Math.cos(yaw) + zp * Math.sin(yaw);
        double zw = -xs * Math.sin(yaw) + zp * Math.cos(yaw);

        double px = this.getX() + xw;
        double py = this.getY() + 0.001D + yp;
        double pz = this.getZ() + zw;
        if (drop) {
            this.level().addParticle(BIG_SOUL_FIRE_DROP.get(), px, py, pz, 0.0D, 0.0D, 0.0D);
        } else {
            this.level().addParticle(BIG_SOUL_FIRE.get(), px, py, pz, 0.0D, 0.0D, 0.0D);
        }
    }

    private void emitOminousParticles() {
        int amount = this.random.nextInt(1, 2);
        for (int i = 0; i < amount; i++) {
            double angle = this.random.nextDouble() * Math.PI * 2.0D;
            double radius = 0.3D + this.random.nextDouble() * 0.3D;
            double x = this.getX() + Math.cos(angle) * radius;
            double y = this.getY() + this.random.nextDouble() * this.getBbHeight();
            double z = this.getZ() + Math.sin(angle) * radius;
            double speed = 0.03D + this.random.nextDouble() * 0.03D;
            this.level().addParticle(ModParticleTypes.TRIAL_OMEN.get(), x, y, z,
                    -Math.sin(angle) * speed, 0.02D + this.random.nextDouble() * 0.03D,
                    Math.cos(angle) * speed);
        }
    }

    @Override
    public void spawnMobs() {
        int spawnRange = 2;
        boolean random = this.level().getRandom().nextBoolean();
        Level level = this.level();
        if (level instanceof ServerLevel serverLevel) {
            for (int p = 0; p < 4; ++p) {
                double d0 = (double) this.blockPosition().getX() + serverLevel.getRandom().nextDouble();
                double d1 = (double) this.blockPosition().getY() + serverLevel.getRandom().nextDouble();
                double d2 = (double) this.blockPosition().getZ() + serverLevel.getRandom().nextDouble();
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, d0, d1, d2, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(ParticleTypes.SMOKE, d0, d1, d2, 1, 0.0D, 5.0E-4D, 0.0D, 5.0E-4D);
            }
            SoundUtil.playNecromancerSummon(this);
            for (int i = 0; i < 2 + serverLevel.getRandom().nextInt(2) * serverLevel.getRandom().nextInt(1); ++i) {
                Summoned summoned;
                double d3 = (double) this.blockPosition().getX() + (serverLevel.getRandom().nextDouble() - serverLevel.getRandom().nextDouble()) * (double) spawnRange + 0.5D;
                double d4 = this.blockPosition().getY();
                double d5 = (double) this.blockPosition().getZ() + (serverLevel.getRandom().nextDouble() - serverLevel.getRandom().nextDouble()) * (double) spawnRange + 0.5D;
                BlockPos blockPos = BlockPos.containing(d3, d4, d5);
                blockPos = BlockFinder.SummonPosition(this, blockPos);
                if (!this.level().getBlockState(blockPos).getCollisionShape(this.level(), blockPos).isEmpty()) {
                    continue;
                }
                if (serverLevel.isWaterAt(blockPos)) {
                    summoned = random
                            ? (Summoned) ModEntityType.DROWNED_SERVANT.get().create(serverLevel)
                            : (Summoned) ModEntityType.SUNKEN_SKELETON_SERVANT.get().create(serverLevel);
                } else {
                    summoned = random
                            ? (serverLevel.getRandom().nextFloat() <= 0.8F
                            ? (Summoned) ModEntityType.FROZEN_ZOMBIE_SERVANT.get().create(serverLevel)
                            : (Summoned) ModEntityType.ZOMBIE_SERVANT.get().create(serverLevel))
                            : (serverLevel.getRandom().nextFloat() <= 0.8F
                            ? (Summoned) ModEntityType.STRAY_SERVANT.get().create(serverLevel)
                            : (Summoned) ModEntityType.SKELETON_SERVANT.get().create(serverLevel));
                    if (serverLevel.getRandom().nextFloat() <= 0.15F) {
                        summoned = (Summoned) (serverLevel.getRandom().nextFloat() <= 0.5F
                                ? ModEntityTypes.DREDEN_SERVANT.get().create(serverLevel)
                                : ModEntityType.BORDER_WRAITH_SERVANT.get().create(serverLevel));
                    }
                }
                if (summoned == null) {
                    continue;
                }
                summoned.setTrueOwner(this);
                summoned.setUpgraded(true);
                summoned.moveTo(blockPos, this.getYRot(), this.getXRot());
                summoned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(summoned.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                if (this.random.nextFloat() < 0.10F && this.isArmoredSummon(summoned)) {
                    summoned.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
                    summoned.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
                    summoned.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
                    summoned.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
                    summoned.setDropChance(EquipmentSlot.HEAD, 0.0F);
                    summoned.setDropChance(EquipmentSlot.CHEST, 0.0F);
                    summoned.setDropChance(EquipmentSlot.LEGS, 0.0F);
                    summoned.setDropChance(EquipmentSlot.FEET, 0.0F);
                }
                ServerParticleUtil.summonUndeadParticles(serverLevel, summoned);
                serverLevel.addFreshEntityWithPassengers(summoned);
            }
        }
    }

    private boolean isArmoredSummon(Summoned summoned) {
        EntityType<?> type = summoned.getType();
        return type == ModEntityType.ZOMBIE_SERVANT.get()
                || type == ModEntityType.FROZEN_ZOMBIE_SERVANT.get()
                || type == ModEntityType.DROWNED_SERVANT.get()
                || type == ModEntityType.SKELETON_SERVANT.get()
                || type == ModEntityType.STRAY_SERVANT.get()
                || type == ModEntityType.SUNKEN_SKELETON_SERVANT.get();
    }

    class SoulSkullGoal extends Goal {
        public int shootTime;

        @Override
        public boolean canUse() {
            if (OminousSkullLord.this.getTarget() != null && OminousSkullLord.this.isNotAbility()
                    && OminousSkullLord.this.hasLineOfSight(OminousSkullLord.this.getTarget())
                    && OminousSkullLord.this.isInvulnerable()) {
                return !MobUtil.areAllies(OminousSkullLord.this.getTarget(), OminousSkullLord.this);
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return OminousSkullLord.this.getTarget() != null && OminousSkullLord.this.isNotAbility()
                    && OminousSkullLord.this.hasLineOfSight(OminousSkullLord.this.getTarget())
                    && OminousSkullLord.this.getTarget().isAlive()
                    && OminousSkullLord.this.getBoneLord() != null;
        }

        @Override
        public void tick() {
            LivingEntity target = OminousSkullLord.this.getTarget();
            if (target != null) {
                int shoot = 45;
                if (OminousSkullLord.this.isHalfHealth()) {
                    shoot = 30;
                }
                if (--this.shootTime <= 0) {
                    double d1 = target.getX() - OminousSkullLord.this.getX();
                    double d2 = target.getY(0.5D) - OminousSkullLord.this.getY(0.5D);
                    double d3 = target.getZ() - OminousSkullLord.this.getZ();
                    HauntedSkullProjectile soulSkull = new HauntedSkullProjectile(OminousSkullLord.this, d1, d2, d3,
                            OminousSkullLord.this.level());
                    soulSkull.setPos(soulSkull.getX(), OminousSkullLord.this.getY(0.75D), soulSkull.getZ());
                    soulSkull.setYRot(OminousSkullLord.this.getYRot());
                    soulSkull.setXRot(OminousSkullLord.this.getXRot());
                    soulSkull.setDamage((float) OminousSkullLord.this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    this.shootTime = shoot;
                    OminousSkullLord.this.level().addFreshEntity(soulSkull);
                    OminousSkullLord.this.playSound(ModSounds.SKULL_LORD_SHOOT.get(), 1.0F, 1.0F);
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class ChargeAttackGoal extends Goal {
        private Vec3 chargePos;

        ChargeAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (OminousSkullLord.this.getTarget() != null && OminousSkullLord.this.isNotAbility()
                    && OminousSkullLord.this.hasLineOfSight(OminousSkullLord.this.getTarget())
                    && OminousSkullLord.this.distanceTo(OminousSkullLord.this.getTarget()) <= 8.0F) {
                if (!OminousSkullLord.this.isHalfHealth()) {
                    return OminousSkullLord.this.random.nextInt(60) == 0;
                }
                return OminousSkullLord.this.random.nextInt(30) == 0;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return OminousSkullLord.this.isCharging() && OminousSkullLord.this.getTarget() != null
                    && OminousSkullLord.this.distanceTo(OminousSkullLord.this.getTarget()) <= 8.0F
                    && OminousSkullLord.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity target = OminousSkullLord.this.getTarget();
            if (target != null) {
                Vec3 eye = target.getEyePosition(1.0F);
                double dx = OminousSkullLord.this.getX() - eye.x();
                double dy = OminousSkullLord.this.getY() - eye.y();
                double dz = OminousSkullLord.this.getZ() - eye.z();
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double velocity = OminousSkullLord.this.isHalfHealth() ? 3.5D : 3.0D;
                double xPower = -(dx / distance * velocity * 0.2D);
                double yPower = -(dy / distance * velocity * 0.2D);
                double zPower = -(dz / distance * velocity * 0.2D);
                this.chargePos = new Vec3(xPower, yPower, zPower);
                OminousSkullLord.this.setIsCharging(true);
                OminousSkullLord.this.playSound(ModSounds.SKULL_LORD_CHARGE.get(), 1.0F, 1.0F);
            }
        }

        @Override
        public void stop() {
            OminousSkullLord.this.setIsCharging(false);
            OminousSkullLord.this.setDeltaMovement(Vec3.ZERO);
        }

        @Override
        public void tick() {
            LivingEntity target = OminousSkullLord.this.getTarget();
            if (this.chargePos != null) {
                OminousSkullLord.this.setDeltaMovement(this.chargePos);
            }
            if (target != null && OminousSkullLord.this.getBoundingBox().intersects(target.getBoundingBox())
                    && target.hurt(OminousSkullLord.this.damageSources().indirectMagic(OminousSkullLord.this,
                    OminousSkullLord.this),
                    (float) OminousSkullLord.this.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                if (OminousSkullLord.this.isOnFire()) {
                    target.setSecondsOnFire(5);
                }
                target.addEffect(new MobEffectInstance(GoetyEffects.SAPPED.get(), 160));
                OminousSkullLord.this.level().explode(OminousSkullLord.this, OminousSkullLord.this.getX(),
                        OminousSkullLord.this.getY(), OminousSkullLord.this.getZ(),
                        OminousSkullLord.this.explosionRadius, Level.ExplosionInteraction.NONE);
                OminousSkullLord.this.setIsCharging(false);
            }
        }
    }

    class ElectroOrbAttackGoal extends Goal {
        private int electroTime;

        ElectroOrbAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (OminousSkullLord.this.getTarget() != null && OminousSkullLord.this.isNotAbility()
                    && OminousSkullLord.this.hasLineOfSight(OminousSkullLord.this.getTarget())) {
                return OminousSkullLord.this.random.nextInt(200) == 0;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return OminousSkullLord.this.isElectroOrb() && OminousSkullLord.this.getTarget() != null
                    && this.electroTime > 0 && OminousSkullLord.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity target = OminousSkullLord.this.getTarget();
            if (target != null) {
                this.electroTime = 80;
                OminousSkullLord.this.setElectroOrb(true);
                OminousSkullLord.this.playSound(ModSounds.ZAP.get(), 1.0F, 1.0F);
            }
        }

        @Override
        public void stop() {
            this.electroTime = 0;
            OminousSkullLord.this.setElectroOrb(false);
            OminousSkullLord.this.setDeltaMovement(Vec3.ZERO);
        }

        @Override
        public void tick() {
            LivingEntity target = OminousSkullLord.this.getTarget();
            if (target != null && OminousSkullLord.this.level() instanceof ServerLevel serverLevel) {
                if (this.electroTime > 0) {
                    --this.electroTime;
                    if (!OminousSkullLord.this.hasLineOfSight(target)) {
                        OminousSkullLord.this.setDeltaMovement(0.0D, 0.08D, 0.0D);
                    } else {
                        OminousSkullLord.this.setDeltaMovement(Vec3.ZERO);
                    }
                    double d1 = target.getX() - OminousSkullLord.this.getX();
                    double d2 = target.getZ() - OminousSkullLord.this.getZ();
                    OminousSkullLord.this.getLookControl().setLookAt(target, 10.0F,
                            (float) OminousSkullLord.this.getMaxHeadXRot());
                    OminousSkullLord.this.setYRot(-((float) Mth.atan2(d1, d2)) * 57.295776F);
                    OminousSkullLord.this.yBodyRot = OminousSkullLord.this.getYRot();
                    if (this.electroTime > 60) {
                        ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel,
                                com.Polarice3.Goety.client.particles.ModParticleTypes.BIG_ELECTRIC.get(),
                                OminousSkullLord.this);
                    }
                    if (this.electroTime <= 60 && this.electroTime % 10 == 0) {
                        new ElectroOrbSpell().mobSpellResult(OminousSkullLord.this, ItemStack.EMPTY);
                    }
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class ShockwaveAttackGoal extends Goal {
        private int shockWave;

        ShockwaveAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (OminousSkullLord.this.getTarget() != null && OminousSkullLord.this.isNotAbility()
                    && OminousSkullLord.this.distanceTo(OminousSkullLord.this.getTarget()) <= 4.0F
                    && OminousSkullLord.this.hasLineOfSight(OminousSkullLord.this.getTarget())) {
                return OminousSkullLord.this.ominousShockWaveCooldown <= 0;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return OminousSkullLord.this.isShockWave() && OminousSkullLord.this.getTarget() != null
                    && this.shockWave > 0 && OminousSkullLord.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity target = OminousSkullLord.this.getTarget();
            if (target != null) {
                this.shockWave = 50;
                OminousSkullLord.this.setShockWave(true);
                OminousSkullLord.this.level().broadcastEntityEvent(OminousSkullLord.this, (byte) 6);
                OminousSkullLord.this.playSound(ModSounds.BOMB_FUSE.get(), 3.0F, 1.0F);
                OminousSkullLord.this.playSound(ModSounds.SKULL_LORD_CHARGE.get(), 3.0F, 0.25F);
            }
        }

        @Override
        public void stop() {
            this.shockWave = 0;
            OminousSkullLord.this.ominousShockWaveCooldown = 100;
            OminousSkullLord.this.setShockWave(false);
            OminousSkullLord.this.level().broadcastEntityEvent(OminousSkullLord.this, (byte) 7);
            OminousSkullLord.this.setDeltaMovement(Vec3.ZERO);
        }

        @Override
        public void tick() {
            LivingEntity target = OminousSkullLord.this.getTarget();
            if (target != null && OminousSkullLord.this.level() instanceof ServerLevel serverLevel) {
                if (this.shockWave > 0) {
                    --this.shockWave;
                    OminousSkullLord.this.setDeltaMovement(Vec3.ZERO);
                    double d1 = target.getX() - OminousSkullLord.this.getX();
                    double d2 = target.getZ() - OminousSkullLord.this.getZ();
                    OminousSkullLord.this.getLookControl().setLookAt(target, 10.0F,
                            (float) OminousSkullLord.this.getMaxHeadXRot());
                    OminousSkullLord.this.setYRot(-((float) Mth.atan2(d1, d2)) * 57.295776F);
                    OminousSkullLord.this.yBodyRot = OminousSkullLord.this.getYRot();
                    ServerParticleUtil.gatheringParticles(
                            com.Polarice3.Goety.client.particles.ModParticleTypes.LASER_GATHER.get(),
                            OminousSkullLord.this, serverLevel);
                    if (this.shockWave == 10) {
                        new ShockwaveSpell().mobSpellResult(OminousSkullLord.this, ItemStack.EMPTY);
                    }
                }
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class MoveRandomGoal extends Goal {
        MoveRandomGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return OminousSkullLord.this.isNotAbility();
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void tick() {
            int distance = 8;
            BlockPos blockPos = null;
            if (OminousSkullLord.this.getBoneLord() != null) {
                blockPos = OminousSkullLord.this.getBoneLord().blockPosition().above(2);
            } else if (OminousSkullLord.this.getTarget() != null) {
                blockPos = OminousSkullLord.this.getTarget().blockPosition().above(2);
            } else if (OminousSkullLord.this.getBoundOrigin() != null) {
                blockPos = OminousSkullLord.this.getBoundOrigin();
            }
            for (int i = 0; i < 64; ++i) {
                if (blockPos != null) {
                    Vec3 center = Vec3.atCenterOf(blockPos);
                    double x = center.x + (double) OminousSkullLord.this.random.nextIntBetweenInclusive(-distance, distance);
                    double y = center.y + (double) OminousSkullLord.this.random.nextIntBetweenInclusive(-distance, distance);
                    double z = center.z + (double) OminousSkullLord.this.random.nextIntBetweenInclusive(-distance, distance);
                    BlockPos targetPos = BlockPos.containing(x, y, z);
                    if (!OminousSkullLord.this.level().isEmptyBlock(targetPos)) {
                        continue;
                    }
                    OminousSkullLord.this.getMoveControl().setWantedPosition(x, y, z, 0.05D);
                    break;
                }
                double x = OminousSkullLord.this.getX() + (double) OminousSkullLord.this.random.nextIntBetweenInclusive(-(distance /= 2), distance);
                double y = OminousSkullLord.this.getY() + (double) OminousSkullLord.this.random.nextIntBetweenInclusive(-distance, distance);
                double z = OminousSkullLord.this.getZ() + (double) OminousSkullLord.this.random.nextIntBetweenInclusive(-distance, distance);
                BlockPos targetPos = BlockPos.containing(x, y, z);
                if (!OminousSkullLord.this.level().isEmptyBlock(targetPos)) {
                    continue;
                }
                OminousSkullLord.this.getMoveControl().setWantedPosition(x, y, z, 0.05D);
                break;
            }
        }
    }

    class LookAroundGoal extends Goal {
        LookAroundGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            if (OminousSkullLord.this.getTarget() == null) {
                Vec3 movement = OminousSkullLord.this.getDeltaMovement();
                OminousSkullLord.this.setYRot(-((float) Mth.atan2(movement.x, movement.z)) * 57.295776F);
            } else {
                LivingEntity target = OminousSkullLord.this.getTarget();
                double d1 = target.getX() - OminousSkullLord.this.getX();
                double d2 = target.getZ() - OminousSkullLord.this.getZ();
                OminousSkullLord.this.getLookControl().setLookAt(target, 10.0F,
                        (float) OminousSkullLord.this.getMaxHeadXRot());
                OminousSkullLord.this.setYRot(-((float) Mth.atan2(d1, d2)) * 57.295776F);
            }
            OminousSkullLord.this.yBodyRot = OminousSkullLord.this.getYRot();
        }
    }
}
