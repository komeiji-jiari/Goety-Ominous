package com.qiuyue.someillagerservants.common.entities.hostile;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.magic.IChargingSpell;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.SurroundGoal;
import com.Polarice3.Goety.common.entities.ally.undead.zombie.ZombieVillagerServant;
import com.Polarice3.Goety.common.entities.hostile.cultists.Cultist;
import com.Polarice3.Goety.common.entities.hostile.servants.Damned;
import com.Polarice3.Goety.common.entities.projectiles.Lavaball;
import com.Polarice3.Goety.common.entities.projectiles.ModWitherSkull;
import com.Polarice3.Goety.common.entities.util.LightningTrap;
import com.Polarice3.Goety.common.entities.util.SummonCircle;
import com.Polarice3.Goety.client.particles.FoggyCloudParticleOption;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.common.magic.spells.nether.*;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.*;
import com.Polarice3.Goety.utils.WitchBarterHelper;
import com.qiuyue.someillagerservants.common.entities.ai.AcolyteBarterGoal;
import com.qiuyue.someillagerservants.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Acolyte extends Cultist {
    private static final EntityDataAccessor<Boolean> CASTING;
    private static final EntityDataAccessor<Integer> CURRENT_SPELL;

    public int spellCooldown;
    public int fireBlastCooldown;
    public int meteorCooldown;
    public int zombieVillagerCount;
    public int coolDown = 0;

    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState attackAnimationState = new AnimationState();
    public AnimationState summonAnimationState = new AnimationState();

    public static final int IDLE = 0;
    public static final int ATTACK = 1;
    public static final int SUMMON = 2;

    private int attackAnimRestartTick;

    private int currentAnimationState = IDLE;

    public Acolyte(EntityType<? extends Cultist> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 100;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AcolyteBarterGoal<>(this, "gameplay/acolyte_bartering"));
        this.goalSelector.addGoal(2, new CastingSpellGoal());
        this.goalSelector.addGoal(2, new FireBlastDefenseGoal(this));
        this.goalSelector.addGoal(3, new SpellGoal());
        this.goalSelector.addGoal(4, new SurroundGoal<>(this, 0.5F, 8.0F));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, null));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.AcolyteHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.AcolyteArmor.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.AcolyteDamage.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.AcolyteMovementSpeed.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.AcolyteFollowRange.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), 50.0D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), 2.0D);
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), 3.0D);
    }

    protected SoundEvent getAmbientSound() {
        int random = this.random.nextInt(5);
        return switch (random) {
            case 0 -> com.qiuyue.someillagerservants.common.init.ModSounds.ACOLYTE_IDLE_1.get();
            case 1 -> com.qiuyue.someillagerservants.common.init.ModSounds.ACOLYTE_IDLE_2.get();
            case 2 -> com.qiuyue.someillagerservants.common.init.ModSounds.ACOLYTE_IDLE_3.get();
            case 3 -> com.qiuyue.someillagerservants.common.init.ModSounds.ACOLYTE_IDLE_4.get();
            default -> com.qiuyue.someillagerservants.common.init.ModSounds.ACOLYTE_IDLE_5.get();
        };
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        int random = this.random.nextInt(3);
        return switch (random) {
            case 0 -> com.qiuyue.someillagerservants.common.init.ModSounds.ACOLYTE_HURT_1.get();
            case 1 -> com.qiuyue.someillagerservants.common.init.ModSounds.ACOLYTE_HURT_2.get();
            default -> com.qiuyue.someillagerservants.common.init.ModSounds.ACOLYTE_HURT_3.get();
        };
    }

    protected SoundEvent getDeathSound() {
        int random = this.random.nextInt(3);
        return switch (random) {
            case 0 -> com.qiuyue.someillagerservants.common.init.ModSounds.ACOLYTE_DEATH_1.get();
            case 1 -> com.qiuyue.someillagerservants.common.init.ModSounds.ACOLYTE_DEATH_2.get();
            default -> com.qiuyue.someillagerservants.common.init.ModSounds.ACOLYTE_DEATH_3.get();
        };
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(CASTING, false);
        this.getEntityData().define(CURRENT_SPELL, 0);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("SpellCooldown", this.spellCooldown);
        pCompound.putInt("FireBlastCooldown", this.fireBlastCooldown);
        pCompound.putInt("MeteorCooldown", this.meteorCooldown);
        pCompound.putInt("ZombieVillagerCount", this.zombieVillagerCount);
        pCompound.putInt("CoolDown", this.coolDown);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("SpellCooldown")) {
            this.spellCooldown = pCompound.getInt("SpellCooldown");
        }
        if (pCompound.contains("FireBlastCooldown")) {
            this.fireBlastCooldown = pCompound.getInt("FireBlastCooldown");
        }
        if (pCompound.contains("MeteorCooldown")) {
            this.meteorCooldown = pCompound.getInt("MeteorCooldown");
        }
        if (pCompound.contains("ZombieVillagerCount")) {
            this.zombieVillagerCount = pCompound.getInt("ZombieVillagerCount");
        }
        if (pCompound.contains("CoolDown")) {
            this.coolDown = pCompound.getInt("CoolDown");
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData data = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
        this.spellCooldown = 20;
        this.fireBlastCooldown = 40;
        this.meteorCooldown = 200;
        return data;
    }

    public void setCasting(boolean casting) {
        this.getEntityData().set(CASTING, casting);
    }

    public boolean isCasting() {
        return this.getEntityData().get(CASTING);
    }

    public void setCurrentSpell(int spell) {
        this.getEntityData().set(CURRENT_SPELL, spell);
    }

    public int getCurrentSpell() {
        return this.getEntityData().get(CURRENT_SPELL);
    }

    public boolean isCurrentAnimation(int state) {
        return this.currentAnimationState == state;
    }

    public void setCurrentAnimationState(int state) {
        this.currentAnimationState = state;
    }

    protected float getDamageAfterMagicAbsorb(DamageSource damageSource, float damage) {
        damage = super.getDamageAfterMagicAbsorb(damageSource, damage);
        if (damageSource.getEntity() == this) {
            damage = 0.0F;
        }
        if (damageSource.is(DamageTypeTags.WITCH_RESISTANT_TO) || damageSource.is(DamageTypeTags.IS_FIRE)) {
            damage *= 0.15F;
        }
        return damage;
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    public boolean fireImmune() {
        return true;
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (!this.level().isClientSide && itemstack.is(ModTags.Items.WITCH_CURRENCY) && this.getMainHandItem().isEmpty()) {
            this.setItemInHand(InteractionHand.MAIN_HAND, itemstack.split(1));
            WitchBarterHelper.setTrader(this, pPlayer);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(pPlayer, pHand);
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.spellCooldown > 0) {
            --this.spellCooldown;
        }
        if (this.fireBlastCooldown > 0) {
            --this.fireBlastCooldown;
        }
        if (this.meteorCooldown > 0) {
            --this.meteorCooldown;
        }
        if (this.coolDown > 0) {
            --this.coolDown;
        }
    }

    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            this.setupAnimationStates();

            if (this.isCasting()) {
                this.castingParticles();
            }
        }

        if (!this.level().isClientSide) {
            List<ZombieVillagerServant> list = this.level().getEntitiesOfClass(ZombieVillagerServant.class, this.getBoundingBox().inflate(64.0D),
                    entity -> entity instanceof IOwned && ((IOwned) entity).getTrueOwner() == this);
            this.zombieVillagerCount = list.size();
        }
    }

    private void setupAnimationStates() {
        if (this.isCurrentAnimation(SUMMON)) {
            this.summonAnimationState.startIfStopped(this.tickCount);
        } else {
            this.summonAnimationState.stop();
        }

        if (this.isCasting() && !this.isCurrentAnimation(SUMMON)) {
            this.attackAnimationState.startIfStopped(this.tickCount);
            // 对于持续施法（Meteor Shower），每 30 tick 重新触发攻击动画使之循环
            if (this.getCurrentSpell() == AcolyteSpell.METEOR.id) {
                if (this.tickCount - this.attackAnimRestartTick >= 30) {
                    this.attackAnimationState.start(this.tickCount);
                    this.attackAnimRestartTick = this.tickCount;
                }
            }
        } else {
            this.attackAnimationState.stop();
        }

        if (!this.isCasting() && !this.isCurrentAnimation(SUMMON)) {
            this.idleAnimationState.startIfStopped(this.tickCount);
        } else {
            this.idleAnimationState.stop();
        }
    }

    public void castingParticles() {
    }

    public void handleEntityEvent(byte pId) {
        if (pId == 6) {
            this.setCasting(true);
        } else if (pId == 7) {
            this.setCasting(false);
        } else {
            super.handleEntityEvent(pId);
        }
    }

    static {
        CASTING = SynchedEntityData.defineId(Acolyte.class, EntityDataSerializers.BOOLEAN);
        CURRENT_SPELL = SynchedEntityData.defineId(Acolyte.class, EntityDataSerializers.INT);
    }

    class CastingSpellGoal extends Goal {
        public CastingSpellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            return Acolyte.this.isCasting();
        }

        public void start() {
            super.start();
            Acolyte.this.getNavigation().stop();
        }

        public void stop() {
            super.stop();
            Acolyte.this.setCasting(false);
            Acolyte.this.level().broadcastEntityEvent(Acolyte.this, (byte) 7);
            Acolyte.this.setCurrentSpell(0);
            Acolyte.this.setCurrentAnimationState(IDLE);
            Acolyte.this.coolDown = 20;
        }

        public void tick() {
            LivingEntity target = Acolyte.this.getTarget();
            if (target != null) {
                MobUtil.instaLook(Acolyte.this, target);
            }
            Acolyte.this.getNavigation().stop();
            Acolyte.this.getMoveControl().strafe(0.0F, 0.0F);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    class SpellGoal extends Goal {
        private int castingTime;
        private int selectedSpell;
        private int chargeTicks;

        public boolean canUse() {
            LivingEntity target = Acolyte.this.getTarget();
            if (target != null && target.isAlive()) {
                return !Acolyte.this.isCasting()
                        && Acolyte.this.hasLineOfSight(target)
                        && Acolyte.this.coolDown <= 0
                        && Acolyte.this.spellCooldown <= 0;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return this.castingTime > 0 && Acolyte.this.isCasting();
        }

        public void start() {
            super.start();
            this.selectSpell();
            Acolyte.this.getNavigation().stop();
        }

        public void stop() {
            super.stop();
            Acolyte.this.setCasting(false);
            Acolyte.this.level().broadcastEntityEvent(Acolyte.this, (byte) 7);
            Acolyte.this.setCurrentSpell(0);
            Acolyte.this.setCurrentAnimationState(IDLE);
            Acolyte.this.coolDown = 20;
        }

        private void selectSpell() {
            List<AcolyteSpell> spells = new ArrayList<>();

            for (AcolyteSpell spell : AcolyteSpell.values()) {
                if (spell == AcolyteSpell.ZOMBIE_VILLAGER && Acolyte.this.zombieVillagerCount >= 8) {
                    continue;
                }
                if (spell == AcolyteSpell.METEOR && Acolyte.this.meteorCooldown > 0) {
                    continue;
                }
                spells.add(spell);
            }

            if (!spells.isEmpty()) {
                AcolyteSpell chosen = spells.get(Acolyte.this.getRandom().nextInt(spells.size()));
                this.selectedSpell = chosen.id;

                AcolyteSpell spellEnum = AcolyteSpell.byId(this.selectedSpell);
                if (spellEnum.spell instanceof IChargingSpell chargingSpell) {
                    this.castingTime = chargingSpell.shotsNumber(Acolyte.this, ItemStack.EMPTY);
                    this.chargeTicks = 10;
                } else {
                    this.castingTime = spellEnum.castingTime;
                }

                switch (chosen) {
                    case ZOMBIE_VILLAGER:
                    case DAMNED:
                        Acolyte.this.setCurrentAnimationState(SUMMON);
                        break;
                    default:
                        Acolyte.this.setCurrentAnimationState(ATTACK);
                        break;
                }

                Acolyte.this.setCurrentSpell(this.selectedSpell);
                Acolyte.this.setCasting(true);
                Acolyte.this.level().broadcastEntityEvent(Acolyte.this, (byte) 6);
                Acolyte.this.playSound(ModSounds.APOSTLE_PREPARE_SPELL.get(), 1.0F, 1.0F);
            } else {
                Acolyte.this.coolDown = 20;
            }
        }

        public void tick() {
            --this.castingTime;
            LivingEntity target = Acolyte.this.getTarget();
            if (target != null) {
                MobUtil.instaLook(Acolyte.this, target);
            }
            Acolyte.this.getNavigation().stop();
            Acolyte.this.getMoveControl().strafe(0.0F, 0.0F);

            AcolyteSpell spellEnum = AcolyteSpell.byId(this.selectedSpell);
            if (spellEnum.spell instanceof IChargingSpell) {
                if (!spellEnum.spell.conditionsMet(Acolyte.this.level(), Acolyte.this)) {
                    this.cancelSpell();
                    return;
                }
                --this.chargeTicks;
                if (this.chargeTicks <= 0) {
                    Spell spell1 = spellEnum.spell;
                    SpellStat spellStat = WandUtil.getStats(Acolyte.this, spell1);
                    spell1.mobSpellResult(Acolyte.this, ItemStack.EMPTY, spellStat);
                    this.chargeTicks = 10;
                }
                Acolyte.this.level().broadcastEntityEvent(Acolyte.this, (byte) 4);
            } else {
                Acolyte.this.level().broadcastEntityEvent(Acolyte.this, (byte) 5);
            }
            spellEnum.spell.useParticle(Acolyte.this.level(), Acolyte.this, ItemStack.EMPTY);

            if (this.castingTime <= 0) {
                this.castSpell();
            }
        }

        public void cancelSpell() {
            this.castingTime = 0;
            Acolyte.this.setCasting(false);
            Acolyte.this.level().broadcastEntityEvent(Acolyte.this, (byte) 7);
            Acolyte.this.setCurrentSpell(0);
            Acolyte.this.setCurrentAnimationState(IDLE);
            Acolyte.this.coolDown = 20;
        }

        private void castSpell() {
            LivingEntity target = Acolyte.this.getTarget();
            if (target == null) return;

            AcolyteSpell spellEnum = AcolyteSpell.byId(this.selectedSpell);

            if (!(spellEnum.spell instanceof IChargingSpell)) {
                spellEnum.spell.mobSpellResult(Acolyte.this, ItemStack.EMPTY, WandUtil.getStats(Acolyte.this, spellEnum.spell));
            }

            if (this.selectedSpell == AcolyteSpell.METEOR.id) {
                Acolyte.this.meteorCooldown = 400;
            }

            Acolyte.this.spellCooldown = 40 + Acolyte.this.getRandom().nextInt(40);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    public static class FireBlastDefenseGoal extends Goal {
        private final Acolyte acolyte;
        private int chargeTime;

        public FireBlastDefenseGoal(Acolyte acolyte) {
            this.acolyte = acolyte;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        public boolean canUse() {
            if (this.acolyte.fireBlastCooldown > 0) {
                return false;
            }

            LivingEntity target = this.acolyte.getTarget();
            if (target == null) {
                return false;
            }

            return this.acolyte.distanceToSqr(target) < 9.0;
        }

        public boolean canContinueToUse() {
            LivingEntity target = this.acolyte.getTarget();
            return target != null && this.acolyte.distanceToSqr(target) < 9.0 && this.chargeTime > 0;
        }

        public void start() {
            super.start();
            this.chargeTime = 20;
            this.acolyte.fireBlastCooldown = 60;
        }

        public void tick() {
            --this.chargeTime;
            LivingEntity target = this.acolyte.getTarget();
            if (target != null) {
                MobUtil.instaLook(this.acolyte, target);
            }

            if (this.chargeTime <= 0) {
                this.performFireBlast();
            }
        }

        private void performFireBlast() {
            new FireBlastSpell().mobSpellResult(this.acolyte, ItemStack.EMPTY, WandUtil.getStats(this.acolyte, new FireBlastSpell()));

            LivingEntity target = this.acolyte.getTarget();
            if (target != null) {
                Vec3 knockback = target.position().subtract(this.acolyte.position()).normalize().scale(3.5D);
                target.push(knockback.x, 0.5D, knockback.z);
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
    }

    enum AcolyteSpell {
        ZOMBIE_VILLAGER(new ZombieVillagerServantSummonSpell(), 0, 60),
        NETHER_SPELL(new RandomNetherSpell(), 1, 10),
        LIGHTNING_TRAP(new LightningTrapSpell(), 2, 60),
        DAMNED(new DamnedSummonSpell(), 3, 100),
        METEOR(new MeteorShowerSpellWrapper(), 4, 100);

        final Spell spell;
        final int id;
        final int castingTime;

        AcolyteSpell(Spell spell, int id, int castingTime) {
            this.spell = spell;
            this.id = id;
            this.castingTime = castingTime;
        }

        static AcolyteSpell byId(int id) {
            for (AcolyteSpell spell : values()) {
                if (spell.id == id) return spell;
            }
            return NETHER_SPELL;
        }
    }

    static class ZombieVillagerServantSummonSpell extends SummonSpell {
        @Override
        public void mobSpellResult(LivingEntity caster, ItemStack staff, SpellStat stats) {
            if (!(caster.level() instanceof ServerLevel serverLevel)) return;

            Acolyte acolyte = (Acolyte) caster;
            int count = acolyte.getRandom().nextBoolean() ? 2 : 1;

            for (int i = 0; i < count; ++i) {
                boolean shouldSpawnSkeleton = acolyte.getRandom().nextFloat() < 0.3F;

                if (shouldSpawnSkeleton) {
                    com.Polarice3.Goety.common.entities.hostile.servants.SkeletonVillagerServant skeletonServant =
                            new com.Polarice3.Goety.common.entities.hostile.servants.SkeletonVillagerServant(
                                    com.Polarice3.Goety.common.entities.ModEntityType.SKELETON_VILLAGER_SERVANT.get(),
                                    acolyte.level());
                    BlockPos spawnPos = BlockFinder.SummonRadius(acolyte.blockPosition(), skeletonServant, serverLevel);

                    if (spawnPos != null) {
                        skeletonServant.moveTo((double) spawnPos.getX() + 0.5, spawnPos.getY(), (double) spawnPos.getZ() + 0.5, acolyte.getYHeadRot(), acolyte.getXRot());
                        skeletonServant.setTrueOwner(acolyte);
                        skeletonServant.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(spawnPos), MobSpawnType.MOB_SUMMONED, null, null);

                        skeletonServant.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));

                        SummonCircle summonCircle = new SummonCircle(acolyte.level(), Vec3.atCenterOf(spawnPos), skeletonServant, true, true, acolyte);
                        summonCircle.setLifeSpan(20);
                        ((ServerLevel) acolyte.level()).addFreshEntity(summonCircle);

                        ((ServerLevel) acolyte.level()).addFreshEntity(skeletonServant);

                        ServerParticleUtil.summonUndeadParticles(serverLevel, skeletonServant, new ColorUtil(0xffa300), 0xffa300, 0xffff6e);
                    }
                } else {
                    ZombieVillagerServant servant = new ZombieVillagerServant(ModEntityType.ZOMBIE_VILLAGER_SERVANT.get(), acolyte.level());
                    BlockPos spawnPos = BlockFinder.SummonRadius(acolyte.blockPosition(), servant, serverLevel);

                    if (spawnPos != null) {
                        servant.moveTo((double) spawnPos.getX() + 0.5, spawnPos.getY(), (double) spawnPos.getZ() + 0.5, acolyte.getYHeadRot(), acolyte.getXRot());
                        servant.setTrueOwner(acolyte);
                        servant.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(spawnPos), MobSpawnType.MOB_SUMMONED, null, null);

                        servant.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));

                        SummonCircle summonCircle = new SummonCircle(acolyte.level(), Vec3.atCenterOf(spawnPos), servant, true, true, acolyte);
                        summonCircle.setLifeSpan(20);
                        ((ServerLevel) acolyte.level()).addFreshEntity(summonCircle);

                        ((ServerLevel) acolyte.level()).addFreshEntity(servant);

                        ServerParticleUtil.summonUndeadParticles(serverLevel, servant, new ColorUtil(0xffa300), 0xffa300, 0xffff6e);
                    }
                }
            }

            acolyte.playSound(com.Polarice3.Goety.init.ModSounds.SUMMON_SPELL.get(), 1.0F, 1.0F);
        }

        @Override
        public int defaultCastDuration() {
            return 60;
        }

        @Override
        public int defaultSpellCooldown() {
            return 60;
        }

        @Override
        public int defaultSoulCost() {
            return 0;
        }

        @Override
        public int SummonDownDuration() {
            return 0;
        }
    }

    static class RandomNetherSpell extends Spell {
        @Override
        public void mobSpellResult(LivingEntity caster, ItemStack staff, SpellStat stats) {
            Spell[] spells = {
                    new LavaballSpellNoGrief(),
                    new LavaballSpellNoGrief(),
                    new WitherSkullSpellNoGrief(),
                    new WitherSkullSpellNoGrief()
            };

            Spell chosen = spells[caster.getRandom().nextInt(spells.length)];
            chosen.mobSpellResult(caster, staff, WandUtil.getStats(caster, chosen));
        }

        @Override
        public int defaultCastDuration() {
            return 10;
        }

        @Override
        public int defaultSpellCooldown() {
            return 20;
        }

        @Override
        public int defaultSoulCost() {
            return 0;
        }
    }

    static class LightningTrapSpell extends Spell {
        @Override
        public void mobSpellResult(LivingEntity caster, ItemStack staff, SpellStat stats) {
            if (!(caster.level() instanceof ServerLevel serverLevel)) return;

            if (!(caster instanceof Mob mobCaster)) return;

            LivingEntity target = mobCaster.getTarget();
            if (target == null) return;

            BlockPos centerPos = target.blockPosition();

            LightningTrap centerTrap = new LightningTrap(caster.level(), centerPos.getX() + 0.5, centerPos.getY(), centerPos.getZ() + 0.5);
            centerTrap.setOwner(caster);
            centerTrap.setDuration(50);
            caster.level().addFreshEntity(centerTrap);

            double[] angles = {Math.PI / 2, Math.PI * 7 / 6, Math.PI * 11 / 6};
            for (int i = 0; i < 3; ++i) {
                double offsetX = Math.cos(angles[i]) * 5.0;
                double offsetZ = Math.sin(angles[i]) * 5.0;

                LightningTrap trap = new LightningTrap(caster.level(),
                        centerPos.getX() + 0.5 + offsetX,
                        centerPos.getY(),
                        centerPos.getZ() + 0.5 + offsetZ);
                trap.setOwner(caster);
                trap.setDuration(50);
                caster.level().addFreshEntity(trap);
            }

            caster.playSound(ModSounds.APOSTLE_PREPARE_SPELL.get(), 1.0F, 1.0F);
        }

        @Override
        public int defaultCastDuration() {
            return 60;
        }

        @Override
        public int defaultSpellCooldown() {
            return 60;
        }

        @Override
        public int defaultSoulCost() {
            return 0;
        }
    }

    static class DamnedSummonSpell extends SummonSpell {
        @Override
        public void mobSpellResult(LivingEntity caster, ItemStack staff, SpellStat stats) {
            if (!(caster.level() instanceof ServerLevel serverLevel)) return;

            Acolyte acolyte = (Acolyte) caster;

            Damned damned = new Damned(ModEntityType.DAMNED.get(), acolyte.level());
            BlockPos blockPos0 = acolyte.blockPosition().offset(
                    serverLevel.getRandom().nextIntBetweenInclusive(-3, 3),
                    0,
                    serverLevel.getRandom().nextIntBetweenInclusive(-3, 3));
            BlockPos blockPos = BlockFinder.SummonPosition(acolyte, blockPos0);

            if (blockPos == null) {
                return;
            }

            damned.moveTo((double) blockPos.below(2).getX() + 0.5, blockPos.below(2).getY(), (double) blockPos.below(2).getZ() + 0.5, acolyte.getYHeadRot(), acolyte.getXRot());
            damned.setTrueOwner(acolyte);
            damned.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(blockPos.below(2)), MobSpawnType.MOB_SUMMONED, null, null);

            LivingEntity target = acolyte.getTarget();
            if (target != null) {
                damned.setTarget(target);
            }

            ServerParticleUtil.addParticlesAroundSelf(serverLevel, ModParticleTypes.BIG_FIRE.get(), damned);
            acolyte.level().addFreshEntity(damned);

            acolyte.playSound(ModSounds.APOSTLE_PREPARE_SUMMON.get(), 1.0F, 1.0F);
        }

        @Override
        public int defaultCastDuration() {
            return 100;
        }

        @Override
        public int defaultSpellCooldown() {
            return 100;
        }

        @Override
        public int defaultSoulCost() {
            return 0;
        }

        @Override
        public int SummonDownDuration() {
            return 0;
        }
    }

    static class MeteorShowerSpellWrapper extends Spell implements IChargingSpell {
        @Override
        public void mobSpellResult(LivingEntity caster, ItemStack staff, SpellStat stats) {
            if (!(caster.level() instanceof ServerLevel serverLevel)) return;

            int potency = stats.getPotency();
            int burning = stats.getBurning();
            int range = stats.getRange();
            float radius = (float) stats.getRadius();

            HitResult rayTrace = this.rayTrace(serverLevel, caster, range, 3.0D);
            Vec3 location = rayTrace.getLocation();
            LivingEntity target = ((Mob) caster).getTarget();
            if (target != null) {
                location = target.position();
            }

            BlockPos mutableBlockPos = new BlockPos.MutableBlockPos(location.x, location.y + 1.5D, location.z);
            int maxHeight = 15;
            int i = 0;
            while (mutableBlockPos.getY() < serverLevel.getMaxBuildHeight() && serverLevel.isEmptyBlock(mutableBlockPos) && i < maxHeight) {
                mutableBlockPos = mutableBlockPos.above();
                i++;
            }

            for (int j = 0; j < potency + 1; ++j) {
                float xZ = 24.0F;
                float y = 4.0F;
                Vec3 vec3 = mutableBlockPos.getCenter()
                        .add(serverLevel.random.nextFloat() * xZ - (xZ / 2.0F),
                                serverLevel.random.nextFloat() * y - (y / 2.0F),
                                serverLevel.random.nextFloat() * xZ - (xZ / 2.0F));

                int clearTries = 0;
                while (clearTries < 6 && !serverLevel.isEmptyBlock(BlockPos.containing(vec3)) && serverLevel.getFluidState(BlockPos.containing(vec3)).isEmpty()) {
                    clearTries++;
                    vec3 = mutableBlockPos.getCenter()
                            .add(serverLevel.random.nextFloat() * xZ - (xZ / 2.0F),
                                    serverLevel.random.nextFloat() * y - (y / 2.0F),
                                    serverLevel.random.nextFloat() * xZ - (xZ / 2.0F));
                }

                if (!serverLevel.isEmptyBlock(BlockPos.containing(vec3)) && serverLevel.getFluidState(BlockPos.containing(vec3)).isEmpty()) {
                    vec3 = mutableBlockPos.getCenter();
                }

                Vec3 vec30 = vec3.offsetRandom(serverLevel.getRandom(), 1.5F);
                serverLevel.sendParticles(new FoggyCloudParticleOption(new ColorUtil(0x1a090d), 3.0F, 1), vec30.x(), vec30.y(), vec30.z(), 1, 0, 0, 0, 0);
                serverLevel.sendParticles(new FoggyCloudParticleOption(new ColorUtil(0x240d12), 3.0F, 1), vec3.x(), vec3.y(), vec3.z(), 1, 0, 0, 0, 0);

                Vec3 vec31 = location.subtract(vec3);
                AbstractHurtingProjectile fireball = new Lavaball(serverLevel, vec3.x, vec3.y, vec3.z, vec31.x, vec31.y, vec31.z);

                fireball.setOwner(caster);
                fireball.setPos(vec3);

                if (fireball instanceof Lavaball lavaball) {
                    lavaball.setUpgraded(true);
                    lavaball.setExtraDamage(potency);
                    lavaball.setFiery(burning);
                    lavaball.setExplosionPower(lavaball.getExplosionPower() + radius);
                    lavaball.setDangerous(false);
                }

                if (serverLevel.addFreshEntity(fireball)) {
                    this.playSound(serverLevel, fireball, SoundEvents.GHAST_SHOOT, 2.0F, this.projPitch(serverLevel.getRandom()));
                }
            }
        }

        @Override
        public int Cooldown() {
            return 0;
        }

        @Override
        public int shotsNumber(LivingEntity caster, ItemStack staff) {
            return 400;
        }

        @Override
        public int defaultCastDuration() {
            return 100;
        }

        @Override
        public int defaultSpellCooldown() {
            return 400;
        }

        @Override
        public int defaultSoulCost() {
            return 0;
        }
    }

    static class LavaballSpellNoGrief extends Spell {
        @Override
        public void mobSpellResult(LivingEntity caster, ItemStack staff, SpellStat stats) {
            if (!(caster.level() instanceof ServerLevel worldIn)) return;

            int potency = stats.getPotency();
            int burning = stats.getBurning();
            float radius = (float) stats.getRadius();
            if (WandUtil.enchantedFocus(caster)){
                potency += WandUtil.getPotencyLevel(caster);
                burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), caster);
                radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) / 2.0F;
            }
            Vec3 vector3d = caster.getViewVector(1.0F);
            Lavaball lavaball = new Lavaball(worldIn,
                    caster.getX() + vector3d.x / 2,
                    caster.getEyeY() - 0.2,
                    caster.getZ() + vector3d.z / 2,
                    vector3d.x,
                    vector3d.y,
                    vector3d.z);
            lavaball.setOwner(caster);
            lavaball.setExtraDamage(potency);
            lavaball.setFiery(burning);
            lavaball.setExplosionPower(lavaball.getExplosionPower() + radius);
            lavaball.setDangerous(false);
            worldIn.addFreshEntity(lavaball);
            if (rightStaff(staff)) {
                for (int i = 0; i < 2; ++i) {
                    Lavaball lavaball1 = new Lavaball(worldIn,
                            caster.getX() + vector3d.x / 2 + worldIn.random.nextGaussian(),
                            caster.getEyeY() - 0.2,
                            caster.getZ() + vector3d.z / 2 + worldIn.random.nextGaussian(),
                            vector3d.x,
                            vector3d.y,
                            vector3d.z);
                    lavaball1.setOwner(caster);
                    lavaball1.setExtraDamage(potency);
                    lavaball1.setFiery(burning);
                    lavaball1.setExplosionPower(lavaball.getExplosionPower() + radius);
                    lavaball1.setDangerous(false);
                    worldIn.addFreshEntity(lavaball1);
                }
            }
            this.playSound(worldIn, caster, 2.0F, (caster.getRandom().nextFloat() - caster.getRandom().nextFloat()) * 0.2F + 1.0F);
        }

        @Override
        public int defaultCastDuration() {
            return 10;
        }

        @Override
        public int defaultSpellCooldown() {
            return 20;
        }

        @Override
        public int defaultSoulCost() {
            return 0;
        }
    }

    static class WitherSkullSpellNoGrief extends Spell {
        @Override
        public void mobSpellResult(LivingEntity caster, ItemStack staff, SpellStat stats) {
            if (!(caster.level() instanceof ServerLevel worldIn)) return;

            Vec3 vector3d = caster.getViewVector(1.0F);
            float extraBlast = (float) (stats.getRadius() + WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) / 2.0F);
            ModWitherSkull witherSkull = new ModWitherSkull(
                    caster.getX() + vector3d.x / 2,
                    caster.getEyeY() - 0.2,
                    caster.getZ() + vector3d.z / 2,
                    vector3d.x,
                    vector3d.y,
                    vector3d.z, worldIn);
            witherSkull.setOwner(caster);
            witherSkull.setExtraDamage(stats.getPotency() + WandUtil.getPotencyLevel(caster));
            witherSkull.setFiery(stats.getBurning() + WandUtil.getLevels(ModEnchantments.BURNING.get(), caster));
            witherSkull.setExplosionPower(witherSkull.getExplosionPower() + extraBlast);
            worldIn.addFreshEntity(witherSkull);
            if (rightStaff(staff)) {
                for (int i = 0; i < 2; ++i) {
                    ModWitherSkull witherSkull1 = new ModWitherSkull(
                            caster.getX() + vector3d.x / 2 + worldIn.random.nextGaussian(),
                            caster.getEyeY() - 0.2,
                            caster.getZ() + vector3d.z / 2 + worldIn.random.nextGaussian(),
                            vector3d.x,
                            vector3d.y,
                            vector3d.z, worldIn);
                    witherSkull1.setOwner(caster);
                    witherSkull1.setExtraDamage(stats.getPotency() + WandUtil.getPotencyLevel(caster));
                    witherSkull1.setFiery(stats.getBurning() + WandUtil.getLevels(ModEnchantments.BURNING.get(), caster));
                    witherSkull1.setExplosionPower(witherSkull.getExplosionPower() + extraBlast);
                    worldIn.addFreshEntity(witherSkull1);
                }
            }
            this.playSound(worldIn, caster, 2.0F, (caster.getRandom().nextFloat() - caster.getRandom().nextFloat()) * 0.2F + 1.0F);
        }

        @Override
        public int defaultCastDuration() {
            return 10;
        }

        @Override
        public int defaultSpellCooldown() {
            return 20;
        }

        @Override
        public int defaultSoulCost() {
            return 0;
        }
    }
}