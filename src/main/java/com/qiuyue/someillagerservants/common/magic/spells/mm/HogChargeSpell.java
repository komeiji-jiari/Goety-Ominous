package com.qiuyue.someillagerservants.common.magic.spells.mm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.EverChargeSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.someillagerservants.common.magic.utils.SmeltHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;

public class HogChargeSpell extends EverChargeSpell {

    public static final int MAX_CAST_TICKS = 120;

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats();
    }

    @Override
    public int defaultSoulCost() {
        return com.qiuyue.someillagerservants.config.SpellConfig.HogChargeSoulCost.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return com.qiuyue.someillagerservants.config.SpellConfig.HogChargeCooldown.get();
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
        return ModSounds.MCD_HOGLIN_GALLOP.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.RADIUS.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
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
                            if (!this.isBreakable(state)) continue;

                            if (usingNetherStaff) {
                                this.breakAndSmelt(worldIn, pos, state, caster);
                            } else {
                                worldIn.destroyBlock(pos, true, caster);
                            }
                        }
                    }
                }
            }

            AABB damageBox = caster.getBoundingBox()
                    .inflate(1.5, 0.5, 1.5)
                    .expandTowards(lookVec.scale(3.0));
            for (LivingEntity target : worldIn.getEntitiesOfClass(LivingEntity.class, damageBox)) {
                if (target == caster) continue;
                if (target instanceof Player player && (player.isCreative() || player.isSpectator())) continue;
                if (!caster.canAttack(target)) continue;

                float dmg = com.qiuyue.someillagerservants.config.SpellConfig.HogChargeDamage.get().floatValue();
                target.hurt(worldIn.damageSources().mobAttack(caster), dmg);
                target.setSecondsOnFire(5);
                target.setDeltaMovement(target.getDeltaMovement().add(lookVec.scale(1.5)));
                target.hurtMarked = true;

                worldIn.sendParticles(ParticleTypes.CLOUD,
                        target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                        3, 0.3, 0.1, 0.3, 0.02);
            }

            Vec3 base = caster.position().add(0, caster.getBbHeight() * 0.4, 0);
            worldIn.sendParticles(ParticleTypes.CLOUD,
                    base.x, base.y, base.z,
                    10, 0.6, 0.3, 0.6, 0.05);
        }

        Vec3 pos = caster.position().add(0, caster.getBbHeight() * 0.5, 0);
        for (int i = 0; i < 10; i++) {
            worldIn.addParticle(ParticleTypes.CLOUD,
                    pos.x + worldIn.random.nextGaussian() * 0.7,
                    pos.y + worldIn.random.nextGaussian() * 0.5,
                    pos.z + worldIn.random.nextGaussian() * 0.7,
                    lookVec.x * worldIn.random.nextFloat() * 0.8,
                    0.05 + worldIn.random.nextFloat() * 0.1,
                    lookVec.z * worldIn.random.nextFloat() * 0.8);
        }
    }

    private boolean isBreakable(BlockState state) {
        return state.is(Tags.Blocks.STONE) || state.is(Tags.Blocks.COBBLESTONE)
                || state.is(Tags.Blocks.ORES) || state.is(net.minecraft.tags.BlockTags.BASE_STONE_OVERWORLD)
                || state.is(net.minecraft.tags.BlockTags.BASE_STONE_NETHER)
                || state.is(Blocks.NETHERRACK) || state.is(Blocks.BLACKSTONE)
                || state.is(Blocks.BASALT) || state.is(Blocks.SMOOTH_BASALT)
                || state.is(Blocks.GRAVEL) || state.is(Blocks.END_STONE);
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
