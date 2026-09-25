package com.qiuyue.goetyominous.common.magic.spells.mm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import com.alexander.mutantmore.events.ShakeCameraEvent;
import com.alexander.mutantmore.init.EffectInit;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.util.PositionUtils;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.AreaDamage;
import com.qiuyue.goetyominous.common.init.ModSpellControllers;
import com.qiuyue.goetyominous.common.magic.utils.ContinuousControllerSpell;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WitherBreathSpell extends ContinuousControllerSpell<WitherBreathSpell.WitherBreathController> {

    private static final int CHARGE_UP_TICKS = 20;
    private static final int BREATH_TICKS = 55;
    private static final int END_LAG_TICKS = 20;

    private static final float BASE_RADIUS = 16.0F;
    private static final float DAMAGE_DISTANCE_FACTOR = 0.7F;

    private static final float AREA_DAMAGE_AMOUNT = 3.0F;
    private static final int AREA_DAMAGE_EXTRA_TIME_BASE = 200;
    private static final int AREA_DAMAGE_EXTRA_TIME_PER_DURATION = 40;

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRadius(BASE_RADIUS);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.WitherBreathSoulCost.get();
    }

    @Override
    public int defaultCastUp() {
        return 0;
    }

    @Override
    public int shotsNumber() {
        return CHARGE_UP_TICKS + BREATH_TICKS + END_LAG_TICKS;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.WitherBreathCooldown.get();
    }

    @Override
    public int soulCost(LivingEntity caster, ItemStack staff) {
        return 0;
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return SoundEventInit.MUTANT_WITHER_SKELETON_DOUBLEATTACK.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    protected Class<WitherBreathController> getControllerType() {
        return WitherBreathController.class;
    }

    @Override
    protected Object controllerKey(LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        return "wither_breath";
    }

    @Override
    protected WitherBreathController createController(ServerLevel world, LivingEntity caster,
                                                      ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }
        boolean usingNetherStaff = this.rightStaff(staff);
        int soulPerSecond = SpellConfig.WitherBreathSoulCost.get();

        return new WitherBreathController(
                world, caster.getUUID(),
                potency, duration, usingNetherStaff,
                soulPerSecond);
    }

    public static class WitherBreathController extends ContinuousControllerSpell.BaseController {

        private static final EntityDataAccessor<Integer> TICKS =
                SynchedEntityData.defineId(WitherBreathController.class, EntityDataSerializers.INT);

        private int potency;
        private int duration;
        private boolean usingNetherStaff;
        private int soulCostPerSecond;

        private int soulCounter;
        private boolean areaDamageSpawned;
        private boolean roarPlayed;

        public WitherBreathController(EntityType<?> type, Level level) {
            super(type, level);
        }

        public WitherBreathController(ServerLevel world, UUID casterUUID,
                                      int potency, int duration, boolean usingNetherStaff,
                                      int soulCostPerSecond) {
            this(ModSpellControllers.WITHER_BREATH_CONTROLLER.get(), world);
            bindCaster(casterUUID);
            this.potency = potency;
            this.duration = duration;
            this.usingNetherStaff = usingNetherStaff;
            this.soulCostPerSecond = soulCostPerSecond;
        }

        @Override
        public Object getControllerKey() {
            return "wither_breath";
        }

        @Override
        protected void defineSynchedData() {
            entityData.define(TICKS, 0);
        }

        @Override
        protected void readAdditionalSaveData(CompoundTag tag) {
            super.readAdditionalSaveData(tag);
            potency = tag.getInt("Potency");
            duration = tag.getInt("Duration");
            usingNetherStaff = tag.getBoolean("UsingNetherStaff");
            soulCostPerSecond = tag.getInt("SoulCostPerSecond");
            soulCounter = tag.getInt("SoulCounter");
            areaDamageSpawned = tag.getBoolean("AreaDamageSpawned");
            roarPlayed = tag.getBoolean("RoarPlayed");
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putInt("Potency", potency);
            tag.putInt("Duration", duration);
            tag.putBoolean("UsingNetherStaff", usingNetherStaff);
            tag.putInt("SoulCostPerSecond", soulCostPerSecond);
            tag.putInt("SoulCounter", soulCounter);
            tag.putBoolean("AreaDamageSpawned", areaDamageSpawned);
            tag.putBoolean("RoarPlayed", roarPlayed);
        }

        @Override
        protected void onTick(ServerLevel level, LivingEntity caster) {
            int tick = entityData.get(TICKS);

            if (tick >= CHARGE_UP_TICKS + BREATH_TICKS + END_LAG_TICKS) {
                discard();
                return;
            }

            float progress;

            if (tick < CHARGE_UP_TICKS) {
                progress = 1.0F * tick / CHARGE_UP_TICKS;
            } else if (tick < CHARGE_UP_TICKS + BREATH_TICKS) {
                progress = 1.0F + 2.75F * (tick - CHARGE_UP_TICKS) / BREATH_TICKS;
            } else {
                progress = 3.75F + 1.25F * (tick - CHARGE_UP_TICKS - BREATH_TICKS) / END_LAG_TICKS;
            }

            if (!roarPlayed && progress >= 1.0F) {
                roarPlayed = true;
                level.playSound(null, caster.getX(), caster.getY(), caster.getZ(),
                        SoundEventInit.MUTANT_WITHER_SKELETON_ROAR.get(),
                        caster.getSoundSource(), 3.0F, 1.0F);
                ShakeCameraEvent.shake(level, 100, 0.1F, caster.blockPosition(), 30);
            }

            if (progress > 1.0F) {
                Vec3 particlePos = PositionUtils.getOffsetPos(caster,
                        0.0, caster.getBbHeight() * 0.6, caster.getBbWidth(), 0.0F, caster.yBodyRot);

                for (int i = 0; i < 10; i++) {
                    level.sendParticles(ParticleTypeInit.WITHER_GAS.get(),
                            particlePos.x, particlePos.y, particlePos.z,
                            1, 0.2, 0.2, 0.2, 0.1);
                }
                for (int i = 0; i < 20; i++) {
                    level.sendParticles(ParticleTypes.SMOKE,
                            particlePos.x, particlePos.y, particlePos.z,
                            1, 0.2, 0.2, 0.2, 0.1);
                }

                applyBreathEffects(level, caster, progress);

                soulCounter++;
                if (soulCounter >= 20) {
                    soulCounter = 0;
                    if (!tryPaySoulCost(caster, soulCostPerSecond)) {
                        discard();
                        return;
                    }
                }
            }

            if (!areaDamageSpawned && tick >= CHARGE_UP_TICKS + BREATH_TICKS) {
                spawnAreaDamage(level, caster);
                areaDamageSpawned = true;
            }

            entityData.set(TICKS, tick + 1);
        }

        private void applyBreathEffects(ServerLevel level, LivingEntity caster, float progress) {
            Vec3 offsetPos = PositionUtils.getOffsetPos(caster,
                    0.0, 0.0, BASE_RADIUS / 2.0F + caster.getBbWidth(), 0.0F, caster.yBodyRot);

            AABB bb = new AABB(
                    offsetPos.add(BASE_RADIUS / 2.0F, 1.0, BASE_RADIUS / 2.0F),
                    offsetPos.subtract(BASE_RADIUS / 2.0F, -1.0, BASE_RADIUS / 2.0F)
            ).move(0.0, caster.getBbHeight() / 2.0F, 0.0);

            float damageDistance = (progress - 1.0F)
                    * (BASE_RADIUS / 2.0F + caster.getBbWidth())
                    * DAMAGE_DISTANCE_FACTOR;

            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, bb)) {
                if (entity == caster) continue;
                if (entity instanceof Player p && (p.isCreative() || p.isSpectator())) continue;
                if (caster instanceof Owned owned
                        && owned.getTrueOwner() != null
                        && entity == owned.getTrueOwner()) continue;
                if (MobUtil.areAllies(caster, entity)) continue;
                if (!caster.hasLineOfSight(entity)) continue;
                if (caster.distanceTo(entity) > damageDistance) continue;

                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60));
                entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, potency));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                entity.addEffect(new MobEffectInstance(EffectInit.JUMPING_FATIGUE.get(), 60));
                entity.hurtMarked = true;

                double d0 = entity.getX() - caster.getX();
                double d1 = entity.getZ() - caster.getZ();
                double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
                entity.push(d0 / d2, 0.0, d1 / d2);
            }
        }

        private void spawnAreaDamage(ServerLevel level, LivingEntity caster) {
            Vec3 offsetPos = PositionUtils.getOffsetPos(caster,
                    0.0, 0.0, BASE_RADIUS / 2.0F + caster.getBbWidth(), 0.0F, caster.yBodyRot);

            int extraTime = AREA_DAMAGE_EXTRA_TIME_BASE + duration * AREA_DAMAGE_EXTRA_TIME_PER_DURATION;

            AreaDamage areaDamage = AreaDamage.spawnAreaDamage(
                    level, offsetPos, caster,
                    0.0F, null,
                    BASE_RADIUS, BASE_RADIUS,
                    0.0F, AREA_DAMAGE_AMOUNT,
                    extraTime, 0,
                    false, false, 0.0, 0.0, false, false, 0, false,
                    null, 4);
            areaDamage.setSentFrom(BlockPos.containing(
                    caster.position().add(0.0, caster.getBbHeight() * 0.6, 0.0)));
            areaDamage.witherBreathBuffLevelBonus = potency;
            areaDamage.witherBreathUsingNetherStaff = usingNetherStaff;
            level.addFreshEntity(areaDamage);
        }

        private boolean tryPaySoulCost(LivingEntity caster, int cost) {
            if (!(caster instanceof Player player)) return true;
            if (cost <= 0) return true;
            if (!SEHelper.getSoulsAmount(player, cost)) return false;
            SEHelper.decreaseSouls(player, cost);
            SEHelper.sendSEUpdatePacket(player);
            return true;
        }
    }
}