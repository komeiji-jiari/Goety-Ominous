package com.qiuyue.goetyominous.common.items;

import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.common.network.CBarbaricAxeShakePacket;
import com.qiuyue.goetyominous.common.rlyeh.ac.ExtinctionStyled;
import com.qiuyue.goetyominous.config.WeaponConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BarbaricAxeItem extends AxeItem implements ExtinctionStyled {

    private static final int DURABILITY = 2031;
    private static final int SLAM_COOLDOWN = 25;
    private static final int SLAM_DURABILITY_COST = 2;
    private static final float SWEEP_HALF_ANGLE = 75.0F;
    private static final int SWEEP_PARTICLE_DISTANCE = 2;
    private static final String SLAM_TICK_TAG = "BarbaricAxeSlam";

    public BarbaricAxeItem() {
        super(Tiers.NETHERITE, damageArgument(), speedArgument(),
                new Properties().stacksTo(1).rarity(Rarity.RARE).durability(DURABILITY));
    }

    private static float damageArgument() {
        return WeaponConfig.BarbaricAxeDamage.get().floatValue() - 1.0F - Tiers.NETHERITE.getAttackDamageBonus();
    }

    private static float speedArgument() {
        return WeaponConfig.BarbaricAxeAttackSpeed.get().floatValue() - 4.0F;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (player.getAttackStrengthScale(0.5F) < 1.0F) {
            return true;
        }
        if (entity instanceof LivingEntity && !player.level().isClientSide) {
            sweep(player, entity);
            CBarbaricAxeShakePacket.send(player, 0.7F, 6);
        }
        return false;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (player.getAttackStrengthScale(0.5F) < 1.0F && player.attackAnim > 0) {
                return true;
            }
            player.swingTime = -1;
        }
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean held) {
        if (entity instanceof Player player && held
                && player.getAttackStrengthScale(0.5F) < 0.95F && player.attackAnim > 0) {
            player.swingTime--;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        slam(level, player, stack, player.blockPosition());
        return InteractionResultHolder.success(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult result = super.useOn(context);
        if (result != InteractionResult.PASS) {
            return result;
        }
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        return slam(context.getLevel(), player, context.getItemInHand(),
                context.getClickedPos().relative(context.getClickedFace()));
    }

    private static void sweep(Player player, Entity primary) {
        Level level = player.level();
        double range = WeaponConfig.BarbaricAxeSweepRange.get();
        float damage = WeaponConfig.BarbaricAxeSweepDamage.get().floatValue();

        Vec3 look = player.getLookAngle();
        Vec3 forward = new Vec3(look.x, 0.0D, look.z);
        if (forward.lengthSqr() < 1.0E-4D) {
            return;
        }
        forward = forward.normalize();
        double cosLimit = Math.cos(Math.toRadians(SWEEP_HALF_ANGLE));

        AABB area = player.getBoundingBox().inflate(range);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e != primary && e.isAlive())) {
            if (target.isAlliedTo(player) || target == player.getRootVehicle()) {
                continue;
            }
            if (player.distanceTo(target) > range) {
                continue;
            }
            Vec3 offset = new Vec3(target.getX() - player.getX(), 0.0D, target.getZ() - player.getZ());
            if (offset.lengthSqr() < 1.0E-4D || forward.dot(offset.normalize()) < cosLimit) {
                continue;
            }
            target.hurt(level.damageSources().playerAttack(player), damage);
            target.knockback(0.6D, target.getX() - player.getX(), target.getZ() - player.getZ());
        }

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    player.getX() + forward.x * SWEEP_PARTICLE_DISTANCE,
                    player.getY() + player.getBbHeight() * 0.6D,
                    player.getZ() + forward.z * SWEEP_PARTICLE_DISTANCE,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private static InteractionResult slam(Level level, Player player, ItemStack stack, BlockPos center) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        long now = level.getGameTime();
        if (stack.getTag() != null && now - stack.getTag().getLong(SLAM_TICK_TAG) < SLAM_COOLDOWN) {
            return InteractionResult.SUCCESS;
        }
        stack.getOrCreateTag().putLong(SLAM_TICK_TAG, now);

        double radius = WeaponConfig.BarbaricAxeSlamRadius.get();
        double damage = WeaponConfig.BarbaricAxeSlamDamage.get();
        int fire = WeaponConfig.BarbaricAxeIgniteSeconds.get();

        double centerX = center.getX() + 0.5D;
        double centerY = center.getY() + 0.5D;
        double centerZ = center.getZ() + 0.5D;
        double ringY = center.getY() + 0.12D;

        AABB area = new AABB(centerX - radius, centerY - radius, centerZ - radius,
                centerX + radius, centerY + radius, centerZ + radius);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e.isAlive())) {
            if (target.isAlliedTo(player)
                    || target.distanceToSqr(centerX, centerY, centerZ) > radius * radius) {
                continue;
            }
            target.hurt(level.damageSources().playerAttack(player), (float) damage);
            target.knockback(1.1D, target.getX() - centerX, target.getZ() - centerZ);
            if (fire > 0) {
                target.setSecondsOnFire(fire);
            }
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(),
                        14, 0.3D, 0.4D, 0.3D, 0.04D);
                serverLevel.sendParticles(ParticleTypes.LAVA,
                        target.getX(), target.getY() + 0.2D, target.getZ(),
                        3, 0.2D, 0.1D, 0.2D, 0.0D);
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 32; i++) {
                double angle = i * (Math.PI * 2.0D) / 32.0D;
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                        centerX + Math.cos(angle) * radius, ringY, centerZ + Math.sin(angle) * radius,
                        1, 0.0D, 0.03D, 0.0D, 0.02D);
            }
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, centerX, ringY + 0.4D, centerZ,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.sendParticles(ParticleTypes.FLAME, centerX, ringY + 0.4D, centerZ,
                    40, radius * 0.5D, 0.3D, radius * 0.5D, 0.06D);
        }

        level.playSound(null, centerX, centerY, centerZ,
                ModSounds.MACE_SMASH_GROUND_HEAVY.get(), SoundSource.PLAYERS, 1.2F, 0.85F);
        stack.hurtAndBreak(SLAM_DURABILITY_COST, player,
                e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        CBarbaricAxeShakePacket.send(player, 1.5F, 14);
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.category == EnchantmentCategory.WEAPON
                || super.canApplyAtEnchantingTable(stack, enchantment);
    }
}
