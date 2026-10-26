package com.qiuyue.goetyominous.common.items.ac;

import com.Polarice3.Goety.common.magic.spells.geomancy.QuakingSpell;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.common.network.ac.CBarbaricAxeShakePacket;
import com.qiuyue.goetyominous.common.rlyeh.ac.ExtinctionStyled;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
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
import net.minecraftforge.registries.ForgeRegistries;
import com.qiuyue.goetyominous.config.ac.AcConfig;

public class BarbaricAxeItem extends AxeItem implements ExtinctionStyled {

    private static final int SLAM_COOLDOWN = 100;
    private static final float SWEEP_HALF_ANGLE = 75.0F;
    private static final int SWEEP_PARTICLE_DISTANCE = 2;
    private static final float FINISHER_DAMAGE_MULTIPLIER = 2.0F;
    private static final double FINISHER_KNOCKBACK = 1.6D;
    private static final double FINISHER_LAUNCH = 0.6D;
    private static final int FINISHER_PARTICLE_SCALE = 3;
    private static final float SLAM_SHAKE_STRENGTH = 5.0F;
    private static final float FINISHER_SHAKE_STRENGTH = 9.0F;
    private static final int SLAM_SHAKE_TICKS = 14;
    private static final int FINISHER_SHAKE_TICKS = 22;
    private static final int COMBO_MAX = 7;
    private static final double FINISHER_RADIUS = 7.0D;
    private static final int FINISHER_HEAVE_RINGS = 5;
    private static final int COMBO_BAR_COLOR = 0x4CFF6E;
    private static final int COMBO_BAR_COLOR_FULL = 0xFF4A16;
    private static final String COMBO_COUNT_TAG = "BarbaricAxeCombo";
    private static final ResourceLocation SLAM_FLAME = new ResourceLocation("alexscaves", "tephra_flame");
    private static final int HEAVE_RINGS = 3;
    private static final double HEAVE_HEIGHT = 3.0D;
    private static final float HEAVE_KNOCK_UP = 0.1F;

    public BarbaricAxeItem() {
        super(Tiers.NETHERITE, damageArgument(), speedArgument(),
                new Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    private static float damageArgument() {
        return AcConfig.BarbaricAxeDamage.get().floatValue() - 1.0F - Tiers.NETHERITE.getAttackDamageBonus();
    }

    private static float speedArgument() {
        return AcConfig.BarbaricAxeAttackSpeed.get().floatValue() - 4.0F;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (player.getAttackStrengthScale(0.5F) < 1.0F) {
            return true;
        }
        if (entity instanceof LivingEntity && !player.level().isClientSide) {
            sweep(player, entity);
            addCombo(stack);
            CBarbaricAxeShakePacket.send(player, 0.7F, 6);
        }
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return combo(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(combo(stack) * 13.0F / COMBO_MAX);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return combo(stack) >= COMBO_MAX ? COMBO_BAR_COLOR_FULL : COMBO_BAR_COLOR;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        slam(level, player, stack, player.blockPosition(), finisherReady(stack, player));
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
        ItemStack stack = context.getItemInHand();
        return slam(context.getLevel(), player, stack,
                context.getClickedPos().relative(context.getClickedFace()), finisherReady(stack, player));
    }

    private static boolean finisherReady(ItemStack stack, Player player) {
        return combo(stack) >= COMBO_MAX && !player.onGround();
    }

    private static int combo(ItemStack stack) {
        return stack.getTag() == null ? 0 : stack.getTag().getInt(COMBO_COUNT_TAG);
    }

    private static void addCombo(ItemStack stack) {
        int combo = combo(stack);
        if (combo < COMBO_MAX) {
            combo++;
        }
        stack.getOrCreateTag().putInt(COMBO_COUNT_TAG, combo);
    }

    private static void sweep(Player player, Entity primary) {
        Level level = player.level();
        double range = AcConfig.BarbaricAxeSweepRange.get();
        float damage = AcConfig.BarbaricAxeSweepDamage.get().floatValue();

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
            if (ServantAllyUtil.areAllied(player, target) || target == player.getRootVehicle()) {
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

    private static InteractionResult slam(Level level, Player player, ItemStack stack, BlockPos center,
            boolean finisher) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (finisher) {
            stack.getOrCreateTag().putInt(COMBO_COUNT_TAG, 0);
        } else {
            if (player.getCooldowns().isOnCooldown(stack.getItem())) {
                return InteractionResult.SUCCESS;
            }
            player.getCooldowns().addCooldown(stack.getItem(), SLAM_COOLDOWN);
        }

        double radius = finisher ? FINISHER_RADIUS : AcConfig.BarbaricAxeSlamRadius.get();
        double damage = AcConfig.BarbaricAxeSlamDamage.get() * (finisher ? FINISHER_DAMAGE_MULTIPLIER : 1.0D);
        int fire = AcConfig.BarbaricAxeIgniteSeconds.get();
        ParticleOptions flame = slamFlame();

        double centerX = center.getX() + 0.5D;
        double centerY = center.getY() + 0.5D;
        double centerZ = center.getZ() + 0.5D;
        double ringY = center.getY() + 0.12D;

        AABB area = new AABB(centerX - radius, centerY - radius, centerZ - radius,
                centerX + radius, centerY + radius, centerZ + radius);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e.isAlive())) {
            if (ServantAllyUtil.areAllied(player, target)
                    || target.distanceToSqr(centerX, centerY, centerZ) > radius * radius) {
                continue;
            }
            target.hurt(level.damageSources().playerAttack(player), (float) damage);
            double offsetX = target.getX() - centerX;
            double offsetZ = target.getZ() - centerZ;
            if (finisher) {
                target.knockback(FINISHER_KNOCKBACK, offsetX, offsetZ);
                target.push(0.0D, FINISHER_LAUNCH, 0.0D);
            } else {
                target.knockback(1.1D, offsetX, offsetZ);
            }
            if (fire > 0) {
                target.setSecondsOnFire(fire);
            }
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(flame,
                        target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(),
                        finisher ? 14 * FINISHER_PARTICLE_SCALE : 14, 0.3D, 0.4D, 0.3D, 0.04D);
                serverLevel.sendParticles(ParticleTypes.LAVA,
                        target.getX(), target.getY() + 0.2D, target.getZ(),
                        finisher ? 3 * FINISHER_PARTICLE_SCALE : 3, 0.2D, 0.1D, 0.2D, 0.0D);
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            int ringPoints = finisher ? 32 * FINISHER_PARTICLE_SCALE : 32;
            for (int i = 0; i < ringPoints; i++) {
                double angle = i * (Math.PI * 2.0D) / ringPoints;
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                        centerX + Math.cos(angle) * radius, ringY, centerZ + Math.sin(angle) * radius,
                        1, 0.0D, 0.03D, 0.0D, 0.02D);
            }
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, centerX, ringY + 0.4D, centerZ,
                    finisher ? 4 : 1,
                    finisher ? radius * 0.4D : 0.0D, finisher ? 0.3D : 0.0D, finisher ? radius * 0.4D : 0.0D, 0.0D);
            serverLevel.sendParticles(flame, centerX, ringY + 0.4D, centerZ,
                    finisher ? 40 * FINISHER_PARTICLE_SCALE : 40, radius * 0.5D, 0.3D, radius * 0.5D, 0.06D);
        }

        heave(player, radius, finisher ? FINISHER_HEAVE_RINGS : HEAVE_RINGS);

        level.playSound(null, centerX, centerY, centerZ,
                ModSounds.MACE_SMASH_GROUND_HEAVY.get(), SoundSource.PLAYERS,
                finisher ? 1.6F : 1.2F, finisher ? 0.65F : 0.85F);
        CBarbaricAxeShakePacket.send(player,
                finisher ? FINISHER_SHAKE_STRENGTH : SLAM_SHAKE_STRENGTH,
                finisher ? FINISHER_SHAKE_TICKS : SLAM_SHAKE_TICKS);
        return InteractionResult.SUCCESS;
    }

    private static ParticleOptions slamFlame() {
        ParticleType<?> type = ForgeRegistries.PARTICLE_TYPES.getValue(SLAM_FLAME);
        return type instanceof SimpleParticleType simple ? simple : ParticleTypes.FLAME;
    }

    private static void heave(Player player, double radius, int rings) {
        int outer = (int) Math.round(radius);
        for (int ring = 0; ring < rings; ring++) {
            int r = outer - ring;
            if (r <= 0) {
                break;
            }
            QuakingSpell.surroundTremor(player, r, HEAVE_HEIGHT, 0.0F, false, 0.0F, HEAVE_KNOCK_UP);
        }
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
