package com.qiuyue.goetyominous.common.entities.hostile.cultists;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ai.SurroundGoal;
import com.Polarice3.Goety.common.entities.ally.Hellhound;
import com.Polarice3.Goety.common.entities.ally.HoglinServant;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.Lavaball;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.common.magic.spells.IronHideSpell;
import com.Polarice3.Goety.common.magic.spells.SoulHealSpell;
import com.Polarice3.Goety.common.magic.spells.nether.LavaballSpell;
import com.Polarice3.Goety.common.magic.spells.nether.MagmaSpell;
import com.Polarice3.Goety.common.magic.spells.wild.HuntingSpell;
import com.Polarice3.Goety.common.magic.spells.wild.MaulingSpell;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.hostile.Scorch;
import com.qiuyue.goetyominous.common.magic.spells.ScorchSpell;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Returned extends AbstractReturned {
    protected int[] spellCoolDown;
    protected int[] spellWeights;
    public int coolDown;
    private SorcererSpell currentSpell;
    private int lavaBombShots;
    private int lavaBombTick;

    public Returned(EntityType<? extends Returned> type, Level worldIn) {
        super(type, worldIn);
        this.spellCoolDown = new int[SorcererSpell.values().length + 1];
        this.spellWeights = new int[SorcererSpell.values().length + 1];
        this.coolDown = 0;
        this.currentSpell = SorcererSpell.IRON_HIDE;
        for (int i = 0; i < this.spellWeights.length; ++i) {
            this.spellWeights[i] = 20;
        }
        this.spellWeights[SorcererSpell.LAVA_BOMB.id] = 40;
        this.spellWeights[SorcererSpell.MAULING.id] = 10;
        this.spellWeights[SorcererSpell.MAGMA.id] = 10;
        this.xpReward = 10;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new BoundCastingSpellGoal());
        this.goalSelector.addGoal(2, new SpellGoal());
        this.goalSelector.addGoal(3, new AvoidTargetGoal(this, LivingEntity.class, 8.0F, 0.6D, 1.0D));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                target -> target instanceof Player player && !CuriosFinder.isWitchFriendly(player)));
        this.goalSelector.addGoal(4, new SurroundGoal<>(this, 1.0D, 8.0F));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.FLYING_SPEED, 0.15D)
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.ReturnedFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.ReturnedArmor.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.ReturnedHealth.get());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.coolDown > 0) {
            --this.coolDown;
        }
        for (int i = 0; i < this.spellCoolDown.length; ++i) {
            if (this.spellCoolDown[i] > 0) {
                --this.spellCoolDown[i];
            }
        }

        if (this.lavaBombShots > 0) {
            if (--this.lavaBombTick <= 0) {
                this.lavaBombTick = 20;
                this.fireLavaBomb();
                this.lavaBombShots--;
            }
        }
    }

    @Override
    public boolean isAlliedTo(net.minecraft.world.entity.Entity entity) {
        if (entity instanceof net.minecraft.world.entity.raid.Raider
                && !(this.getTrueOwner() instanceof Player)) {
            return true;
        }
        return super.isAlliedTo(entity);
    }

    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level().isClientSide) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            if (this.getTrueOwner() != null && pPlayer == this.getTrueOwner() && itemstack.is(ModItems.ECTOPLASM.get()) && this.getHealth() < this.getMaxHealth()) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.heal(2.0F);
                Level var5 = this.level();
                if (var5 instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02;
                        double d1 = this.random.nextGaussian() * 0.02;
                        double d2 = this.random.nextGaussian() * 0.02;
                        serverLevel.sendParticles(ModParticleTypes.HEAL_EFFECT.get(), this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0, d0, d1, d2, 0.5);
                    }
                }
                pPlayer.swing(pHand);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    public void setIsCastingSpell(SorcererSpell spell) {
        this.currentSpell = spell;
        super.setIsCastingSpell(BoundSpell.byId(spell.id % 6 + 1));
    }

    public SorcererSpell getCurrentSorcererSpell() {
        return this.currentSpell;
    }

    protected void resetSpellWeight(SorcererSpell spell) {
        this.spellWeights[spell.id] = 20;
    }

    public MobType getMobType() {
        return ModMobType.NETHER;
    }

    public float getVoicePitch() {
        return 0.45F;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.EVOKER_PREPARE_ATTACK;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.HERETIC_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.HERETIC_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.HERETIC_DEATH.get();
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return ModSounds.HERETIC_CELEBRATE.get();
    }

    public enum SorcererSpell {
        IRON_HIDE(1, new IronHideSpell()),
        HUNTING(2, new HuntingSpell()),
        HEAL(3, new SoulHealSpell()),
        MAULING(4, new MaulingSpell()),
        SCORCH(5, new ScorchSpell()),
        MAGMA(6, new MagmaSpell()),
        LAVA_BOMB(7, new LavaballSpell());

        final int id;
        final Spell spell;

        SorcererSpell(int id, Spell spell) {
            this.id = id;
            this.spell = spell;
        }

        public Spell getSpell() {
            return this.spell;
        }
    }

    private int countOwnedMobs(Class<? extends Mob> entityClass) {
        List<? extends Mob> list = this.level().getEntitiesOfClass(entityClass,
                this.getBoundingBox().inflate(64.0D));
        int count = 0;
        for (Mob mob : list) {
            if (mob instanceof Owned owned && owned.getTrueOwner() == this) {
                count++;
            }
        }
        return count;
    }

    private void summonHellhound(int duration) {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        Hellhound hound = new Hellhound(ModEntityType.HELLHOUND.get(), serverLevel);
        hound.setTrueOwner(this);
        BlockPos pos = BlockFinder.SummonRadius(this.blockPosition(), hound, serverLevel);
        if (pos == null) return;
        hound.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, this.getYHeadRot(), 0.0F);
        MobUtil.moveDownToGround(hound);
        hound.setLimitedLife(MobUtil.getSummonLifespan(serverLevel) * duration);
        hound.spawnAnim();
        hound.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos),
                MobSpawnType.MOB_SUMMONED, null, null);
        if (this.getTarget() != null) {
            hound.setTarget(this.getTarget());
        }
        serverLevel.addFreshEntity(hound);
    }

    private void summonHoglinServant(int duration) {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        HoglinServant hoglin = new HoglinServant(ModEntityType.HOGLIN_SERVANT.get(), serverLevel);
        hoglin.setTrueOwner(this);
        hoglin.setImmuneToZombification(true);
        BlockPos pos = BlockFinder.SummonRadius(this.blockPosition(), hoglin, serverLevel);
        if (pos == null) return;
        hoglin.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, this.getYHeadRot(), 0.0F);
        MobUtil.moveDownToGround(hoglin);
        hoglin.setLimitedLife(MobUtil.getSummonLifespan(serverLevel) * duration);
        hoglin.spawnAnim();
        hoglin.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos),
                MobSpawnType.MOB_SUMMONED, null, null);
        if (this.getTarget() != null) {
            hoglin.setTarget(this.getTarget());
        }
        serverLevel.addFreshEntity(hoglin);
    }

    private void fireLavaBomb() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        LivingEntity target = this.getTarget();
        if (target == null) return;
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.5D) - this.getY(0.5D);
        double dz = target.getZ() - this.getZ();
        Lavaball fireball = new Lavaball(serverLevel, this, dx, dy, dz);
        fireball.setPos(fireball.getX(), this.getY(0.5D), fireball.getZ());
        fireball.setDangerous(false);
        serverLevel.addFreshEntity(fireball);
        this.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 1.0F);
    }

    protected abstract class ReturnedUseSpellGoal extends Goal {
        protected int attackWarmupDelay;

        protected ReturnedUseSpellGoal() {
        }

        @Override
        public boolean canUse() {
            LivingEntity target = Returned.this.getTarget();
            if (target == null || !target.isAlive()) return false;
            if (Returned.this.isCastingSpell()) return false;
            if (!Returned.this.hasLineOfSight(target)) return false;
            return Returned.this.coolDown <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = Returned.this.getTarget();
            return target != null && target.isAlive() && this.attackWarmupDelay > 0;
        }

        @Override
        public void start() {
            this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
            Returned.this.spellCastingTickCount = this.getCastingTime();
            Returned.this.spellCoolDown[this.getSpell().id] = this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            if (soundevent != null) {
                Returned.this.playSound(soundevent, 1.0F, 1.0F);
            }
            Returned.this.setIsCastingSpell(this.getSpell());
        }

        @Override
        public void tick() {
            --this.attackWarmupDelay;
            if (this.attackWarmupDelay == 0) {
                this.performSpellCasting();
                if (Returned.this.getCastingSoundEvent() != null) {
                    Returned.this.playSound(Returned.this.getCastingSoundEvent(), 1.0F, 1.0F);
                }
            }
        }

        @Override
        public void stop() {
            Returned.this.setIsCastingSpell(BoundSpell.NONE);
        }

        protected abstract void performSpellCasting();

        protected int getCastWarmupTime() {
            return 20;
        }

        protected abstract int getCastingTime();

        protected abstract int getCastingInterval();

        @Nullable
        protected abstract SoundEvent getSpellPrepareSound();

        protected abstract SorcererSpell getSpell();
    }

    class SpellGoal extends ReturnedUseSpellGoal {
        public SorcererSpell spell;

        @Override
        public boolean canUse() {
            if (!super.canUse()) return false;

            List<SorcererSpell> available = new ArrayList<>();
            List<Integer> weights = new ArrayList<>();
            int totalWeight = 0;

            for (SorcererSpell s : SorcererSpell.values()) {
                Spell spell = s.getSpell();
                if (s != SorcererSpell.LAVA_BOMB
                        && !spell.conditionsMet(Returned.this.level(), Returned.this)) continue;
                if (Returned.this.spellCoolDown[s.id] > 0) continue;
                if (spell instanceof SummonSpell
                        && Returned.this.hasEffect(GoetyEffects.SUMMON_DOWN.get())) continue;

                if (s == SorcererSpell.SCORCH
                        && Returned.this.countOwnedMobs(Scorch.class) >= 5) continue;
                if (s == SorcererSpell.MAULING
                        && Returned.this.countOwnedMobs(HoglinServant.class) >= 1) continue;
                if (s == SorcererSpell.HUNTING
                        && Returned.this.countOwnedMobs(Hellhound.class) >= 4) continue;

                available.add(s);
                int weight = Returned.this.spellWeights[s.id];
                weights.add(weight);
                totalWeight += weight;
            }

            if (available.isEmpty()) return false;

            int roll = Returned.this.random.nextInt(totalWeight);
            int cumulative = 0;
            for (int i = 0; i < available.size(); ++i) {
                cumulative += weights.get(i);
                if (roll < cumulative) {
                    this.spell = available.get(i);
                    return true;
                }
            }
            this.spell = available.get(0);
            return true;
        }

        @Override
        protected void performSpellCasting() {
            LivingEntity target = Returned.this.getTarget();
            if (target == null) return;

            Spell spell = this.spell.getSpell();
            SpellStat spellStat = WandUtil.getStats(Returned.this, spell);

            Returned.this.level().broadcastEntityEvent(Returned.this, (byte) 7);

            switch (this.spell) {
                case HUNTING -> {
                    if (Returned.this.countOwnedMobs(Hellhound.class) < 4) {
                        summonHellhound(Math.max(1, spellStat.getDuration()));
                    }
                }
                case MAULING -> {
                    if (Returned.this.countOwnedMobs(HoglinServant.class) < 1) {
                        summonHoglinServant(Math.max(1, spellStat.getDuration()));
                    }
                }
                case SCORCH -> {
                    if (Returned.this.countOwnedMobs(Scorch.class) < 5) {
                        spell.mobSpellResult(Returned.this, ItemStack.EMPTY, spellStat);
                    }
                }
                case LAVA_BOMB -> {
                    Returned.this.lavaBombShots = 3;
                    Returned.this.lavaBombTick = 20;
                    Returned.this.fireLavaBomb();
                    Returned.this.lavaBombShots--;
                }
                default -> spell.mobSpellResult(Returned.this, ItemStack.EMPTY, spellStat);
            }

            Returned.this.resetSpellWeight(this.spell);
        }

        @Override
        protected int getCastWarmupTime() {
            Spell spell = this.spell.getSpell();
            if (spell instanceof com.Polarice3.Goety.api.magic.IChargingSpell chargingSpell) {
                return chargingSpell.shotsNumber(Returned.this, ItemStack.EMPTY);
            }
            return spell.defaultCastDuration() + 5;
        }

        @Override
        protected int getCastingTime() {
            Spell spell = this.spell.getSpell();
            if (spell instanceof com.Polarice3.Goety.api.magic.IChargingSpell chargingSpell) {
                return chargingSpell.shotsNumber(Returned.this, ItemStack.EMPTY);
            }
            return spell.defaultCastDuration() + 5;
        }

        @Override
        protected int getCastingInterval() {
            Spell spell = this.spell.getSpell();
            if (spell instanceof com.Polarice3.Goety.api.magic.IChargingSpell chargingSpell) {
                return chargingSpell.defaultSpellCooldown() * 2;
            }
            return spell.defaultSpellCooldown();
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected SorcererSpell getSpell() {
            return this.spell;
        }
    }
}
