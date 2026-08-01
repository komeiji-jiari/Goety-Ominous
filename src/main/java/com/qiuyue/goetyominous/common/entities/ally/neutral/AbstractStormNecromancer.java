package com.qiuyue.goetyominous.common.entities.ally.neutral;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.undead.ReaperServant;
import com.Polarice3.Goety.common.entities.ally.undead.WraithServant;
import com.Polarice3.Goety.common.entities.ally.SpriteMob;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.RattledServant;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.VanguardServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.BlackguardServant;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.FrayedServant;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.common.entities.projectiles.SurgingOrb;
import com.Polarice3.Goety.common.entities.util.MagicLightningTrap;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import com.qiuyue.goetyominous.common.items.revive.StormSoulJar;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class AbstractStormNecromancer extends AbstractNecromancer {
    public AbstractStormNecromancer(EntityType<? extends AbstractNecromancer> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
    }

    private int lightningRodCooldown = 0;

    private int resetSpellTicks = 0;

    private int electrifiedFadeTicks = -1;

    public void projectileGoal(int priority) {
        this.goalSelector.addGoal(priority, new StormNecromancerRangedGoal(this, 1.0D, 20, 12.0F));
    }

    public void avoidGoal(int priority) {
    }

    public void summonSpells(int priority) {
        this.goalSelector.addGoal(priority + 1, new StormSummonServantSpell());
        this.goalSelector.addGoal(priority, new LightningStormGoal());
        this.goalSelector.addGoal(priority + 2, ((AbstractNecromancer) this).new SummonUndeadGoal());
        this.goalSelector.addGoal(priority, new MonsoonSummonGoal());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.StormNecromancerHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.StormNecromancerArmor.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.StormNecromancerFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.StormNecromancerDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.StormNecromancerHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.StormNecromancerArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.FOLLOW_RANGE), AttributesConfig.StormNecromancerFollowRange.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.StormNecromancerDamage.get());
    }

    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof AbstractStormNecromancer;
    }

    public int getSummonLimit(LivingEntity owner) {
        return 8;
    }

    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        float f1 = (float) this.getNecroLevel();
        float size = 1.0F + Math.max(f1 * 0.15F, 0.0F);
        return 2.523F * size;
    }

    public int xpReward() {
        return 40;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.NECROMANCER_AMBIENT.get();
    }

    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return ModSounds.NECROMANCER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ModSounds.NECROMANCER_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return ModSounds.NECROMANCER_STEP.get();
    }

    @Nullable
    @Override
    public SoundEvent getLaughSound() {
        return ModSounds.NECROMANCER_LAUGH.get();
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_LIGHTNING)
                || source.is(net.minecraft.world.damagesource.DamageTypes.LIGHTNING_BOLT)) {
            amount = amount / 3.0F;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return effect.getEffect() != GoetyEffects.SPASMS.get() && super.canBeAffected(effect);
    }

    public void spellCastParticles() {
        for (int i = 0; i < this.level().random.nextInt(35) + 10; ++i) {
            this.level().addParticle(ModParticleTypes.ELECTRIC.get(), this.getX(), this.getY(), this.getZ(), 0.45, 0.45, 0.45);
        }
    }

    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            if (this.level().random.nextInt(5) == 0) {
                for (int i = 0; i < 3; ++i) {
                    this.level().addParticle(ModParticleTypes.ELECTRIC.get(),
                            this.getRandomX(0.5D), this.getY() + 0.1D, this.getRandomZ(0.5D),
                            0.0D, 0.0D, 0.0D);
                }
            }
        } else if (this.level() instanceof ServerLevel serverLevel) {
            if (this.tickCount % 20 == 0) {
                ServerParticleUtil.circularParticles(serverLevel,
                        ModParticleTypes.STATION_CULT_SPELL.get(), this,
                        new com.Polarice3.Goety.utils.ColorUtil(0xFFFF00).red(),
                        new com.Polarice3.Goety.utils.ColorUtil(0xFFFF00).green(),
                        new com.Polarice3.Goety.utils.ColorUtil(0xFFFF00).blue(), 0.5F);
            }
            if (this.lightningRodCooldown > 0) {
                --this.lightningRodCooldown;
            }
            if (this.resetSpellTicks > 0) {
                --this.resetSpellTicks;
                if (this.resetSpellTicks == 0) {
                    this.setSpellCasting(false);
                    this.setNecromancerSpellType(NecromancerSpellType.NONE);
                }
            }
            if (this.tickCount % 5 == 0 && this.random.nextBoolean()) {
                ServerParticleUtil.addParticlesAroundMiddleSelf(
                        serverLevel,
                        ModParticleTypes.BIG_ELECTRIC.get(),
                        this);
            }
            if (this.tickCount % 100 == 0) {
                this.playSound(ModSounds.ZAP.get(), 1.0F, 1.0F);
            }
            this.tickElectrifiedEffect();

            Vec3 motion = this.getDeltaMovement();
            if (motion.y < 0.0D && !this.onGround() && !this.isInWater() && !this.isInLava() && this.fallDistance >= 2.0F) {
                this.setDeltaMovement(motion.multiply(1.0D, 0.875D, 1.0D));
                ServerParticleUtil.windParticle(serverLevel, new com.Polarice3.Goety.utils.ColorUtil(0xFFFFFF), 1.0F, this.random.nextFloat() * 0.5F + 1.0F, 0, this.getId(), this.position());
            }
        }
    }

    private void tickElectrifiedEffect() {
        int necroLevel = this.getNecroLevel();
        boolean shouldHaveEffect = necroLevel >= 2;

        if (!shouldHaveEffect) {
            boolean isThundering = this.level().isThundering();
            if (this instanceof net.minecraft.world.entity.monster.Enemy) {
                shouldHaveEffect = this.level().getDifficulty() == Difficulty.HARD && isThundering;
            } else {
                shouldHaveEffect = isThundering;
            }
        }

        if (shouldHaveEffect) {
            this.electrifiedFadeTicks = -1;
            MobEffectInstance current = this.getEffect(GoetyEffects.ELECTRIFIED.get());
            if (current == null) {
                this.addEffect(new MobEffectInstance(GoetyEffects.ELECTRIFIED.get(), -1, 0, false, false, true));
            }
        } else if (this.hasEffect(GoetyEffects.ELECTRIFIED.get())) {
            if (this.electrifiedFadeTicks < 0) {
                this.electrifiedFadeTicks = MathHelper.secondsToTicks(10);
            }
            this.electrifiedFadeTicks--;
            if (this.electrifiedFadeTicks <= 0) {
                this.removeEffect(GoetyEffects.ELECTRIFIED.get());
                this.electrifiedFadeTicks = -1;
            }
        } else {
            this.electrifiedFadeTicks = -1;
        }
    }

    public void performRangedAttack(@NotNull LivingEntity target, float distanceFactor) {
        if (this.getNecroLevel() < 2) {
            Vec3 vector3d = this.getViewVector(1.0F);
            double d0 = target.getX() - this.getX();
            double d1 = target.getY(0.3333333333333333D) - this.getEyeY();
            double d2 = target.getZ() - this.getZ();
            double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));

            SurgingOrb surgingOrb = new SurgingOrb(
                    this.getX() + vector3d.x / 2.0,
                    this.getEyeY() - 0.2,
                    this.getZ() + vector3d.z / 2.0,
                    d0, d1 + d3 * 0.1F, d2,
                    this.level());
            surgingOrb.setOwner(this);
            surgingOrb.setExtraDamage(((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + (float) (this.getNecroLevel()) + 2.0F));
            surgingOrb.setOrange(this.getMainHandItem().is(ModItems.STORM_STAFF.get()));

            if (this.level().addFreshEntity(surgingOrb)) {
                this.playSound(ModSounds.SHOCK_CAST.get(), 1.0F, 0.5F);
                this.swing(InteractionHand.MAIN_HAND);
            }
        } else {
            boolean stormStaff = this.getMainHandItem().is(ModItems.STORM_STAFF.get());
            Vec3 vector3d = this.getViewVector(1.0F);
            float angleRad = 10.0F * ((float) Math.PI / 180.0F);
            for (int i = -1; i <= 1; i++) {
                float rad = i * angleRad;
                float cos = Mth.cos(rad);
                float sin = Mth.sin(rad);
                double dx = vector3d.x * (double) cos - vector3d.z * (double) sin;
                double dz = vector3d.x * (double) sin + vector3d.z * (double) cos;
                SurgingOrb surgingOrb = new SurgingOrb(
                        this.getX() + vector3d.x / 2.0,
                        this.getEyeY() - 0.2,
                        this.getZ() + vector3d.z / 2.0,
                        dx,
                        vector3d.y,
                        dz,
                        this.level());
                surgingOrb.setOwner(this);
                surgingOrb.setExtraDamage(((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) + (float) (this.getNecroLevel()) + 2.0F));
                surgingOrb.setOrange(stormStaff);
                if (this.level().addFreshEntity(surgingOrb)) {
                    this.swing(InteractionHand.MAIN_HAND);
                }
            }
            this.playSound(ModSounds.SHOCK_CAST.get(), 1.0F, 0.5F);
        }
    }

    public boolean doHurtTarget(Entity target) {
        if (!super.doHurtTarget(target)) {
            return false;
        } else {
            if (target instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(GoetyEffects.SPASMS.get(), MathHelper.secondsToTicks(5)), this);
            }
            return true;
        }
    }

    public void playLaughSound() {
        this.playSound(ModSounds.NECROMANCER_LAUGH.get(), 1.0F, 0.05F);
    }

    public Summoned getDefaultSummon() {
        return new RattledServant(ModEntityType.RATTLED_SERVANT.get(), this.level());
    }

    public Summoned getSummon() {
        Summoned summoned = this.getDefaultSummon();

        if (this.getSummonList().stream().anyMatch(entityType -> entityType == ModEntityType.FRAYED_SERVANT.get())
                && this.level().random.nextBoolean()) {
            summoned = new FrayedServant(ModEntityType.FRAYED_SERVANT.get(), this.level());
        }

        if (this.getSummonList().stream().anyMatch(entityType -> entityType == ModEntityType.RATTLED_SERVANT.get())
                && this.level().random.nextBoolean()) {
            summoned = new RattledServant(ModEntityType.RATTLED_SERVANT.get(), this.level());
        }

        if (this.getSummonList().contains(ModEntityType.SPRITE.get()) && this.level().random.nextBoolean()) {
            summoned = new SpriteMob(ModEntityType.SPRITE.get(), this.level());
        }

        if (this.getSummonList().contains(ModEntityType.WRAITH_SERVANT.get()) && this.level().random.nextFloat() <= 0.05F) {
            summoned = new WraithServant(ModEntityType.WRAITH_SERVANT.get(), this.level());
        }

        if (this.getSummonList().contains(ModEntityType.REAPER_SERVANT.get()) && this.level().random.nextFloat() <= 0.05F) {
            summoned = new ReaperServant(ModEntityType.REAPER_SERVANT.get(), this.level());
        }

        if (this.getSummonList().contains(ModEntityType.VANGUARD_SERVANT.get()) && this.level().random.nextFloat() <= 0.15F) {
            summoned = new VanguardServant(ModEntityType.VANGUARD_SERVANT.get(), this.level());
        }

        if (this.getSummonList().contains(ModEntityType.BLACKGUARD_SERVANT.get()) && this.level().random.nextFloat() <= 0.05F) {
            summoned = new BlackguardServant(ModEntityType.BLACKGUARD_SERVANT.get(), this.level());
        }

        return summoned;
    }

    public void setNecroLevel(int shot) {
        int i = Mth.clamp(shot, 0, 2);
        this.entityData.set(LEVEL, i);
        AttributeInstance attributeInstance = this.getAttribute(Attributes.MAX_HEALTH);
        if (attributeInstance != null) {
            attributeInstance.setBaseValue(AttributesConfig.StormNecromancerHealth.get() * (double) Math.max((float) i * 1.25F, 1.0F));
        }
        this.reapplyPosition();
        this.refreshDimensions();
    }

    public void soulJar() {
        if (this.getTrueOwner() instanceof Player player && MobsConfig.NecromancerSoulJar.get()) {
            Optional<ItemStack> optional = player.getInventory().items.stream().filter(itemStack1 -> itemStack1.is(com.Polarice3.Goety.common.items.ModItems.EMPTY_SOUL_JAR.get())).findFirst();
            if (optional.isPresent()) {
                ItemStack original = optional.get();
                if (original.is(com.Polarice3.Goety.common.items.ModItems.EMPTY_SOUL_JAR.get())) {
                    if (!player.isCreative()) {
                        original.shrink(1);
                    }
                    ItemStack itemStack = new ItemStack(com.qiuyue.goetyominous.common.items.ModItems.STORM_SOUL_JAR.get());
                    StormSoulJar.setOwnerName(this.getTrueOwner(), itemStack);
                    StormSoulJar.setSummon(this, itemStack);
                    StormSoulJar.setStorm(itemStack);
                    SEHelper.addCooldown(player, com.Polarice3.Goety.common.items.ModItems.SOUL_JAR.get(), MathHelper.secondsToTicks(30));
                    SEHelper.addCooldown(player, itemStack.getItem(), MathHelper.secondsToTicks(30));
                    if (!player.getInventory().add(itemStack)) {
                        player.drop(itemStack, false, true);
                    }
                }
            }
        }
    }

    @Override
    public boolean summonVariants() {
        return false;
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level().isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            Item item = itemstack.getItem();
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner()) {
                if (!this.spawnUndeadIdle() && pHand == InteractionHand.MAIN_HAND && itemstack.isEmpty()) {
                    if (this.idleSpellCool <= 0 && this.getSpellCooldown() <= 0) {
                        this.setUndeadIdle(true);
                    } else {
                        SoundEvent soundEvent = this.getHurtSound(this.damageSources().generic());
                        this.playSound(Objects.requireNonNullElseGet(soundEvent, ModSounds.NECROMANCER_HURT));
                        this.level().broadcastEntityEvent(this, (byte) 9);
                    }
                    return InteractionResult.SUCCESS;
                } else if (item == Items.LIGHTNING_ROD) {
                    if (this.lightningRodCooldown > 0) {
                        SoundEvent soundEvent = this.getHurtSound(this.damageSources().generic());
                        this.playSound(Objects.requireNonNullElseGet(soundEvent, ModSounds.NECROMANCER_HURT));
                        this.level().broadcastEntityEvent(this, (byte) 9);
                        return InteractionResult.SUCCESS;
                    }
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.setSpellCooldown(100);
                    this.setSpellCasting(true);
                    this.resetSpellTicks = 70;
                    this.setNecromancerSpellType(NecromancerSpellType.ZOMBIE);
                    this.setAnimationState(SPELL_ANIM);
                    this.playSound(ModSounds.PREPARE_SUMMON.get(), 0.85F, 1.0F);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        int weatherDuration = 6000 + this.random.nextInt(12000);
                        serverLevel.setWeatherParameters(0, weatherDuration, true, true);
                    }
                    this.lightningRodCooldown = MathHelper.secondsToTicks(300);
                    return InteractionResult.SUCCESS;
                } else if (item == Items.BONE && this.getHealth() < this.getMaxHealth()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    this.playSound(ModSounds.NECROMANCER_STEP.get(), 1.0F, 1.25F);
                    this.heal(2.0F);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    return InteractionResult.SUCCESS;
                } else if (this.getSummonList().stream().noneMatch(et -> et == ModEntityType.FRAYED_SERVANT.get())
                        && item == com.Polarice3.Goety.common.items.ModItems.ROTTING_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) itemstack.shrink(1);
                    this.addSummon(ModEntityType.FRAYED_SERVANT.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (this.getSummonList().stream().noneMatch(et -> et == ModEntityType.RATTLED_SERVANT.get())
                        && item == com.Polarice3.Goety.common.items.ModItems.OSSEOUS_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) itemstack.shrink(1);
                    this.addSummon(ModEntityType.RATTLED_SERVANT.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (!this.getSummonList().contains(ModEntityType.SPRITE.get())
                        && item == com.Polarice3.Goety.common.items.ModItems.SPRIGHTLY_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) itemstack.shrink(1);
                    this.addSummon(ModEntityType.SPRITE.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (!this.getSummonList().contains(ModEntityType.WRAITH_SERVANT.get())
                        && item == com.Polarice3.Goety.common.items.ModItems.SPOOKY_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) itemstack.shrink(1);
                    this.addSummon(ModEntityType.WRAITH_SERVANT.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (!this.getSummonList().contains(ModEntityType.REAPER_SERVANT.get())
                        && item == com.Polarice3.Goety.common.items.ModItems.REAPING_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) itemstack.shrink(1);
                    this.addSummon(ModEntityType.REAPER_SERVANT.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (!this.getSummonList().contains(ModEntityType.VANGUARD_SERVANT.get())
                        && item == com.Polarice3.Goety.common.items.ModItems.VANGUARD_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) itemstack.shrink(1);
                    this.addSummon(ModEntityType.VANGUARD_SERVANT.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (!this.getSummonList().contains(ModEntityType.BLACKGUARD_SERVANT.get())
                        && item == com.Polarice3.Goety.common.items.ModItems.BLACKGUARD_FOCUS.get()) {
                    if (!pPlayer.getAbilities().instabuild) itemstack.shrink(1);
                    this.addSummon(ModEntityType.BLACKGUARD_SERVANT.get());
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                } else if (item == com.qiuyue.goetyominous.common.items.ModItems.STORM_SOUL_JAR.get()) {
                    if (!pPlayer.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (this.getNecroLevel() < 2) {
                        this.setNecroLevel(this.getNecroLevel() + 1);
                    }
                    this.heal(AttributesConfig.StormNecromancerHealth.get().floatValue());
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02D;
                            double d1 = this.random.nextGaussian() * 0.02D;
                            double d2 = this.random.nextGaussian() * 0.02D;
                            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                        }
                    }
                    this.playLaughSound();
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public class LightningStormGoal extends Goal {
        protected int spellTime;

        public LightningStormGoal() {
        }

        public boolean canUse() {
            LivingEntity target = AbstractStormNecromancer.this.getTarget();
            if (AbstractStormNecromancer.this.isSpellCasting()) {
                return false;
            }
            return target != null && target.isAlive()
                    && AbstractStormNecromancer.this.random.nextFloat() <= 0.40F
                    && AbstractStormNecromancer.this.idleSpellCool <= 0;
        }

        public boolean canContinueToUse() {
            return this.spellTime > 0;
        }

        public void start() {
            this.spellTime = MathHelper.secondsToTicks(5);
            AbstractStormNecromancer.this.setSpellCooldown(AbstractStormNecromancer.this.getSpellCooldown() + 100);
            AbstractStormNecromancer.this.setSpellCasting(true);
            AbstractStormNecromancer.this.setNecromancerSpellType(NecromancerSpellType.CLOUD);
            AbstractStormNecromancer.this.setAnimationState(SPELL_ANIM);
            AbstractStormNecromancer.this.playSound(ModSounds.WIND_BLAST.get(), 1.0F, 1.0F);
        }

        public void tick() {
            --this.spellTime;
            LivingEntity target = AbstractStormNecromancer.this.getTarget();
            if (target != null && AbstractStormNecromancer.this.level() instanceof ServerLevel serverLevel
                    && this.spellTime % 10 == 0 && this.spellTime > 0) {
                BlockPos targetPos = target.blockPosition();
                for (int i = 0; i < 2; i++) {
                    BlockPos pos1 = targetPos.offset(
                            AbstractStormNecromancer.this.random.nextInt(33) - 16, 0,
                            AbstractStormNecromancer.this.random.nextInt(33) - 16);
                    BlockPos pos2 = targetPos.offset(
                            AbstractStormNecromancer.this.random.nextInt(33) - 16, 0,
                            AbstractStormNecromancer.this.random.nextInt(33) - 16);

                    MagicLightningTrap trap1 = new MagicLightningTrap(serverLevel,
                            pos1.getX() + 0.5, pos1.getY(), pos1.getZ() + 0.5);
                    trap1.setOwner(AbstractStormNecromancer.this);
                    trap1.setDuration(20);
                    trap1.setRadius(4.0F);
                    MobUtil.moveDownToGround(trap1);
                    serverLevel.addFreshEntity(trap1);

                    MagicLightningTrap trap2 = new MagicLightningTrap(serverLevel,
                            pos2.getX() + 0.5, pos2.getY(), pos2.getZ() + 0.5);
                    trap2.setOwner(AbstractStormNecromancer.this);
                    trap2.setDuration(20);
                    trap2.setRadius(4.0F);
                    MobUtil.moveDownToGround(trap2);
                    serverLevel.addFreshEntity(trap2);
                }
                if (AbstractStormNecromancer.this.random.nextInt(4) == 0) {
                    AbstractStormNecromancer.this.playSound(ModSounds.SHOCK_CAST.get(), 1.0F, 1.0F);
                }
            }
        }

        public void stop() {
            super.stop();
            AbstractStormNecromancer.this.setSpellCasting(false);
            AbstractStormNecromancer.this.setAnimationState(IDLE);
            AbstractStormNecromancer.this.setNecromancerSpellType(NecromancerSpellType.NONE);
            AbstractStormNecromancer.this.idleSpellCool = MathHelper.secondsToTicks(10);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public class StormNecromancerRangedGoal extends Goal {
        private final AbstractStormNecromancer mob;
        @Nullable
        private LivingEntity target;
        private int attackTime = -1;
        private final double speedModifier;
        private int seeTime;
        private final int attackInterval;
        private final float attackRadius;
        private final float attackRadiusSqr;

        public StormNecromancerRangedGoal(AbstractStormNecromancer mob, double speed, int attackInterval, float attackRadius) {
            this.mob = mob;
            this.speedModifier = speed;
            this.attackInterval = attackInterval;
            this.attackRadius = attackRadius;
            this.attackRadiusSqr = attackRadius * attackRadius;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity != null && livingentity.isAlive()) {
                this.target = livingentity;
                return !this.mob.isSpellCasting() && this.mob.hasLineOfSight(livingentity);
            }
            return false;
        }

        public boolean canContinueToUse() {
            return this.canUse() || (this.target != null && this.target.isAlive()
                    && !this.mob.getNavigation().isDone() && !this.mob.isSpellCasting());
        }

        public void stop() {
            this.mob.setShooting(false);
            this.target = null;
            this.seeTime = 0;
            this.attackTime = -1;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target != null && !this.mob.isSpellCasting()) {
                double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
                boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
                if (flag) {
                    ++this.seeTime;
                } else {
                    this.seeTime = 0;
                }
                if (d0 > (double) this.attackRadiusSqr) {
                    this.mob.getNavigation().moveTo(this.target, this.speedModifier);
                } else {
                    this.mob.getNavigation().stop();
                }
                int speed = Mth.floor(Math.max(this.mob.getAttackSpeed(), 1.0F));
                this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                int attackIntervalMin = this.attackInterval / speed;
                --this.attackTime;
                if (this.attackTime <= 5) {
                    this.mob.setShooting(true);
                    if (this.mob.getCurrentAnimation() != this.mob.getAnimationState(ATTACK)) {
                        this.mob.setAnimationState(ATTACK);
                    }
                }
                if (this.attackTime == 0) {
                    if (!flag) return;
                    float f = (float) Math.sqrt(d0) / this.attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    this.mob.performRangedAttack(this.target, f1);
                    this.attackTime = attackIntervalMin;
                } else if (this.attackTime < 0) {
                    this.mob.setShooting(false);
                    this.attackTime = attackIntervalMin;
                }
            }
        }
    }

    public class StormSummonServantSpell extends SummoningSpellGoal {
        public boolean canUse() {
            Predicate<Entity> predicate = entity -> entity.isAlive() && entity instanceof IOwned owned
                    && owned.getTrueOwner() == AbstractStormNecromancer.this;
            int i = AbstractStormNecromancer.this.level().getEntitiesOfClass(
                    LivingEntity.class,
                    AbstractStormNecromancer.this.getBoundingBox().inflate(64.0D, 16.0D, 64.0D),
                    predicate).size();
            return super.canUse() && i < 6;
        }

        public void tick() {
            --this.spellTime;
            if (this.spellTime == 10) {
                AbstractStormNecromancer.this.playSound(ModSounds.NECROMANCER_LAUGH.get(), 2.0F, 0.05F);
                this.castSpell();
                AbstractStormNecromancer.this.setNecromancerSpellType(NecromancerSpellType.NONE);
            }
        }

        protected void castSpell() {
            if (AbstractStormNecromancer.this.level() instanceof ServerLevel serverLevel) {
                for (int i1 = 0; i1 < 2; ++i1) {
                    Summoned summoned = AbstractStormNecromancer.this.getSummon();
                    BlockPos blockPos = BlockFinder.SummonRadius(AbstractStormNecromancer.this.blockPosition(), summoned, serverLevel);
                    summoned.setTrueOwner(AbstractStormNecromancer.this);
                    summoned.moveTo(blockPos, 0.0F, 0.0F);
                    MobUtil.moveDownToGround(summoned);
                    if (com.Polarice3.Goety.config.MobsConfig.NecromancerSummonsLife.get()) {
                        summoned.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
                    }
                    summoned.setPersistenceRequired();
                    summoned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(
                            AbstractStormNecromancer.this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    if (serverLevel.addFreshEntity(summoned)) {
                        SoundUtil.playNecromancerSummon(summoned);
                        ServerParticleUtil.summonUndeadParticles(serverLevel, summoned);
                    }
                }
            }
        }

        protected int getCastingInterval() {
            return 200;
        }

        protected NecromancerSpellType getNecromancerSpellType() {
            return NecromancerSpellType.ZOMBIE;
        }
    }

    public class StormSummonUndeadGoal extends SummonUndeadGoal {
        public StormSummonUndeadGoal() {
            AbstractStormNecromancer.this.super();
        }

        @Override
        public void playLaughSound() {
            AbstractStormNecromancer.this.playSound(ModSounds.NECROMANCER_LAUGH.get(), 2.0F, 0.05F);
        }

        @Override
        public void summonUndeadParticles(ServerLevel serverLevel, Entity entity) {
            ServerParticleUtil.summonUndeadParticles(serverLevel, entity);
        }
    }

    public class MonsoonSummonGoal extends Goal {
        protected int spellTime;

        public MonsoonSummonGoal() {
        }

        public boolean canUse() {
            LivingEntity target = AbstractStormNecromancer.this.getTarget();
            if (AbstractStormNecromancer.this.isSpellCasting()) {
                return false;
            }
            return target != null && target.isAlive()
                    && AbstractStormNecromancer.this.random.nextFloat() <= 0.25F
                    && AbstractStormNecromancer.this.idleSpellCool <= 0;
        }

        public boolean canContinueToUse() {
            return this.spellTime > 0;
        }

        public void start() {
            this.spellTime = 60;
            AbstractStormNecromancer.this.setSpellCooldown(AbstractStormNecromancer.this.getSpellCooldown() + 200);
            AbstractStormNecromancer.this.setSpellCasting(true);
            AbstractStormNecromancer.this.setNecromancerSpellType(NecromancerSpellType.CLOUD);
            AbstractStormNecromancer.this.setAnimationState(SPELL_ANIM);
            AbstractStormNecromancer.this.playSound(ModSounds.WIND_BLAST.get(), 1.0F, 1.0F);
        }

        public void tick() {
            --this.spellTime;
            if (this.spellTime == 38 && AbstractStormNecromancer.this.getTarget() != null
                    && AbstractStormNecromancer.this.level() instanceof ServerLevel serverLevel) {
                LivingEntity target = AbstractStormNecromancer.this.getTarget();
                com.Polarice3.Goety.common.entities.projectiles.MonsoonCloud cloud =
                        new com.Polarice3.Goety.common.entities.projectiles.MonsoonCloud(
                                serverLevel, AbstractStormNecromancer.this, target);
                cloud.setExtraDamage((float) AbstractStormNecromancer.this.getNecroLevel());
                cloud.setRadius(3.0F);
                cloud.setLifeSpan(100);
                cloud.setStaff(true);
                serverLevel.addFreshEntity(cloud);
                AbstractStormNecromancer.this.playSound(
                        net.minecraft.sounds.SoundEvents.LIGHTNING_BOLT_THUNDER, 0.5F, 1.25F);
            }
        }

        public void stop() {
            super.stop();
            this.spellTime = 0;
            AbstractStormNecromancer.this.setSpellCasting(false);
            AbstractStormNecromancer.this.setNecromancerSpellType(NecromancerSpellType.NONE);
            AbstractStormNecromancer.this.idleSpellCool = MathHelper.secondsToTicks(10);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

}