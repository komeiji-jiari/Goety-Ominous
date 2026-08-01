package com.qiuyue.goetyominous.common.entities.hostile.cultists;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ai.AvoidTargetGoal;
import com.Polarice3.Goety.common.entities.ai.SurroundGoal;
import com.Polarice3.Goety.common.entities.ally.Hellhound;
import com.Polarice3.Goety.common.entities.ally.HoglinServant;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.common.magic.spells.IronHideSpell;
import com.Polarice3.Goety.common.magic.spells.SoulHealSpell;
import com.Polarice3.Goety.common.magic.spells.nether.FireBreathSpell;
import com.Polarice3.Goety.common.magic.spells.nether.MagmaSpell;
import com.Polarice3.Goety.common.magic.spells.wild.HuntingSpell;
import com.Polarice3.Goety.common.magic.spells.wild.MaulingSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.magic.spells.ScorchSpell;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

    public Returned(EntityType<? extends Returned> type, Level worldIn) {
        super(type, worldIn);
        this.spellCoolDown = new int[SorcererSpell.values().length + 1];
        this.spellWeights = new int[SorcererSpell.values().length + 1];
        this.coolDown = 0;
        this.currentSpell = SorcererSpell.FIRE_BREATH;
        for (int i = 0; i < this.spellWeights.length; ++i) {
            this.spellWeights[i] = 20;
        }
        this.xpReward = 10;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new BoundCastingSpellGoal());
        this.goalSelector.addGoal(2, new SpellGoal());
        this.goalSelector.addGoal(3, new AvoidTargetGoal(this, LivingEntity.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(4, new SurroundGoal<>(this, 1.0D, 8.0F));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                target -> target instanceof Player player && !CuriosFinder.isWitchFriendly(player)));
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
        FIRE_BREATH(1, new FireBreathSpell()),
        IRON_HIDE(2, new IronHideSpell()),
        HUNTING(3, new HuntingSpell()),
        HEAL(4, new SoulHealSpell()),
        MAULING(5, new MaulingSpell()),
        SCORCH(6, new ScorchSpell()),
        MAGMA(7, new MagmaSpell());

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

    class SpellGoal extends BoundUseSpellGoal {
        public SorcererSpell spell;

        @Override
        public boolean canUse() {
            if (!super.canUse()) return false;
            if (Returned.this.coolDown > 0) return false;

            List<SorcererSpell> available = new ArrayList<>();
            List<Integer> weights = new ArrayList<>();
            int totalWeight = 0;

            for (SorcererSpell s : SorcererSpell.values()) {
                Spell spell = s.getSpell();
                if (!spell.conditionsMet(Returned.this.level(), Returned.this)) continue;
                if (Returned.this.spellCoolDown[s.id] > 0) continue;
                if (spell instanceof SummonSpell
                        && Returned.this.hasEffect(GoetyEffects.SUMMON_DOWN.get())) continue;

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
                case HUNTING -> summonHellhound(Math.max(1, spellStat.getDuration()));
                case MAULING -> summonHoglinServant(Math.max(1, spellStat.getDuration()));
                default -> spell.mobSpellResult(Returned.this, ItemStack.EMPTY, spellStat);
            }

            Returned.this.resetSpellWeight(this.spell);
            Returned.this.spellCoolDown[this.spell.id] = this.getCastingInterval();
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
        protected BoundSpell getSpell() {
            return BoundSpell.byId(this.spell.id);
        }
    }
}
