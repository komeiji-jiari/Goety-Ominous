package com.qiuyue.goetyominous.common.magic.spells.mm;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.common.magic.EverChargeSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.SpellExplosion;
import com.Polarice3.Goety.utils.WandUtil;
import com.alexander.mutantmore.config.mutant_hoglin.MutantHoglinCommonConfig;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.qiuyue.goetyominous.common.magic.utils.SmeltHelper;
import com.qiuyue.goetyominous.utils.BossUtil;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.qiuyue.goetyominous.config.mm.MmConfig;

public class HogChargeSpell extends EverChargeSpell {

    public static final int MAX_CAST_TICKS = 120;
    private static final int SOUND_INTERVAL = 10;
    private final Set<UUID> channeling = new HashSet<>();

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats();
    }

    @Override
    public int defaultSoulCost() {
        return MmConfig.HogChargeSoulCost.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return MmConfig.HogChargeCooldown.get();
    }

    @Override
    public int defaultCastUp() {
        return 0;
    }

    @Override
    public int shotsNumber() {
        return MAX_CAST_TICKS;
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEventInit.MUTANT_HOGLIN_STEP.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RADIUS.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.channeling.add(caster.getUUID());
        int potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
        }
        double radius = spellStat.getRadius() + WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
        float velocity = spellStat.getVelocity() + WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        boolean usingNetherStaff = this.rightStaff(staff);

        int breakRadius = 1 + (int) radius;
        double speed = 0.5 + velocity * 0.1;

        Vec3 lookVec = caster.getLookAngle();
        Vec3 push = lookVec.scale(speed);
        caster.setDeltaMovement(caster.getDeltaMovement().add(push.x, 0, push.z));
        caster.hurtMarked = true;
        caster.resetFallDistance();

        Vec3 motion = caster.getDeltaMovement();
        if (caster.onGround() && motion.y > 0.0D) {
            caster.setDeltaMovement(motion.x, 0.0D, motion.z);
        }

        if (caster.tickCount % SOUND_INTERVAL == 0) {
            this.playSound(worldIn, caster, SoundEventInit.MUTANT_HOGLIN_STEP.get(), 0.75F, 1.0F);
        }

        if (!worldIn.isClientSide) {
            if (!caster.isShiftKeyDown()) {
                BlockPos center = caster.blockPosition();
                for (int dx = -breakRadius; dx <= breakRadius; dx++) {
                    for (int dy = 0; dy <= 2; dy++) {
                        for (int dz = -breakRadius; dz <= breakRadius; dz++) {
                            BlockPos pos = center.offset(dx, dy, dz);
                            Vec3 blockCenter = Vec3.atCenterOf(pos);
                            Vec3 toBlock = blockCenter.subtract(caster.position());
                            double dist = toBlock.length();
                            if (dist > breakRadius + 3.0) continue;

                            BlockState state = worldIn.getBlockState(pos);
                            if (state.isAir() || state.getDestroySpeed(worldIn, pos) < 0) continue;
                            if (!this.isBreakable(worldIn, pos, state)) continue;

                            if (usingNetherStaff) {
                                this.breakAndSmelt(worldIn, pos, state, caster);
                            } else {
                                worldIn.destroyBlock(pos, true, caster);
                            }
                        }
                    }
                }
            }

            AABB damageBox = new AABB(
                    caster.getX() - 1.45D, caster.getY(), caster.getZ() - 1.45D,
                    caster.getX() + 1.45D, caster.getY() + 3.0D, caster.getZ() + 1.45D);
            for (LivingEntity target : worldIn.getEntitiesOfClass(LivingEntity.class, damageBox)) {
                if (target == caster) continue;
                if (ServantAllyUtil.areAllied(caster, target)) continue;
                if (target instanceof Player player && (player.isCreative() || player.isSpectator())) continue;
                if (!caster.canAttack(target)) continue;

                if (!BossUtil.isBoss(target)) {
                    double dx = target.getX() - caster.getX();
                    double dz = target.getZ() - caster.getZ();
                    double distSqr = Math.max(dx * dx + dz * dz, 0.001D);
                    target.push(dx / distSqr * 4.0D, 0.2D, dz / distSqr * 4.0D);
                }

                float dmg = MmConfig.HogChargeDamage.get().floatValue() * WandUtil.damageMultiply() + potency;
                target.hurt(worldIn.damageSources().mobAttack(caster), dmg);
                MiscUtils.disableShield(target, MutantHoglinCommonConfig.charge_disable_shield_length.get());
                target.setSecondsOnFire(5);

                worldIn.sendParticles(ModParticleTypes.SMALL_FIRE.get(),
                        target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                        3, 0.3, 0.1, 0.3, 0.02);
            }

            Vec3 base = caster.position().add(0, 3.0D * 0.4D, 0);
            worldIn.sendParticles(ModParticleTypes.SMALL_FIRE.get(),
                    base.x, base.y, base.z,
                    10, 0.6, 0.3, 0.6, 0.05);

            if (staff.getItem() instanceof IWand wand && wand.ShotsFired(staff) >= MAX_CAST_TICKS) {
                this.endChannel(worldIn, caster);
            }
        }
    }

    @Override
    public void stopSpell(ServerLevel worldIn, LivingEntity caster, ItemStack staff, ItemStack focus, int castTime, SpellStat spellStat) {
        this.channeling.remove(caster.getUUID());
    }

    private void endChannel(ServerLevel level, LivingEntity caster) {
        if (!this.channeling.remove(caster.getUUID())) {
            return;
        }
        Vec3 pos = caster.position();
        this.explode(level, caster, pos.x, pos.y, pos.z, 5.0F);
    }

    private void explode(ServerLevel level, LivingEntity caster, double x, double y, double z, float power) {
        ColorUtil color = new ColorUtil(16746757);
        level.sendParticles(new CircleExplodeParticleOption(color.red, color.green, color.blue, power, 1),
                x, y, z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
        level.sendParticles(new SphereExplodeParticleOption(color.red, color.green, color.blue, power, 1),
                x, y, z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
        for (int i = 0; i < 32; i++) {
            ColorUtil fireColor = new ColorUtil(11312015);
            level.sendParticles(ModParticleTypes.BIG_CULT_SPELL.get(),
                    x + (level.random.nextDouble() - 0.5D) * 2.0D, y,
                    z + (level.random.nextDouble() - 0.5D) * 2.0D,
                    0, fireColor.red, fireColor.green, fireColor.blue, 1.0D);
            level.sendParticles(ModParticleTypes.BIG_FIRE_GROUND.get(),
                    x + (level.random.nextDouble() - 0.5D) * 1.0D, y,
                    z + (level.random.nextDouble() - 0.5D) * 1.0D,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        level.sendParticles(ParticleTypeInit.SHOCKWAVE.get(), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        new SpellExplosion(level, caster, level.damageSources().explosion(caster, caster), x, y, z, power, 0.0F);
        level.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 4.0F,
                (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);
        CameraShake.cameraShake(level, new Vec3(x, y, z), power * 2.0F, 0.1F, 0, 20);
    }

    private boolean isBreakable(ServerLevel worldIn, BlockPos pos, BlockState state) {
        float hardness = state.getDestroySpeed(worldIn, pos);
        return hardness >= 0.0F && hardness <= 50.0F;
    }

    private void breakAndSmelt(ServerLevel worldIn, BlockPos pos, BlockState state, LivingEntity caster) {
        BlockEntity blockEntity = state.hasBlockEntity() ? worldIn.getBlockEntity(pos) : null;
        LootParams.Builder builder = new LootParams.Builder(worldIn)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, caster)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);

        List<ItemStack> drops = state.getDrops(builder);

        for (ItemStack drop : drops) {
            ItemStack smelted = SmeltHelper.getSmeltResult(worldIn, drop);
            if (!smelted.isEmpty()) {
                Block.popResource(worldIn, pos, smelted);
            } else {
                Block.popResource(worldIn, pos, drop);
            }
        }
        worldIn.removeBlock(pos, false);
    }
}
