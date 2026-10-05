package com.qiuyue.goetyominous.common.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.utils.WindChargeImpulse;
import com.qiuyue.goetyominous.common.init.ModEnchantments;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import com.qiuyue.goetyominous.common.init.ModTags;
import com.qiuyue.goetyominous.utils.SimpleExplosionDamageCalculator;
import com.qiuyue.goetyominous.utils.WindChargeExplosion;
import java.util.function.Predicate;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MaceItem extends Item implements Vanishable {
    private static final float BASE_ATTACK_DAMAGE = 5.0F;
    private static final float BASE_ATTACK_SPEED = -3.4F;
    public static final float SMASH_ATTACK_FALL_THRESHOLD = 1.5F;
    private static final float SMASH_ATTACK_HEAVY_THRESHOLD = 5.0F;
    public static final float SMASH_ATTACK_KNOCKBACK_RADIUS = 3.5F;
    private static final float SMASH_ATTACK_KNOCKBACK_POWER = 0.7F;

    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public MaceItem(Properties properties) {
        super(properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", BASE_ATTACK_DAMAGE, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", BASE_ATTACK_SPEED, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairMaterial) {
        return repairMaterial.is(ModItems.BREEZE_ROD.get());
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, entity -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));

        if (attacker instanceof ServerPlayer player && canSmashAttack(player)) {
            WindChargeImpulse impulse = (WindChargeImpulse) player;
            impulse.onMaceSmashImpact();

            player.setDeltaMovement(player.getDeltaMovement().with(Direction.Axis.Y, 0.01D));
            player.connection.send(new ClientboundSetEntityMotionPacket(player));

            if (target.onGround()) {
                impulse.setSpawnExtraParticlesOnFall(true);
                SoundEvent sound = player.fallDistance > SMASH_ATTACK_HEAVY_THRESHOLD
                        ? ModSounds.MACE_SMASH_GROUND_HEAVY.get()
                        : ModSounds.MACE_SMASH_GROUND.get();
                player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(), sound, player.getSoundSource(), 1.0F, 1.0F);
            } else {
                player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.MACE_SMASH_AIR.get(), player.getSoundSource(), 1.0F, 1.0F);
            }

            smashAttack(player.serverLevel(), player, target);
            player.resetFallDistance();

            int windBurst = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.WIND_BURST.get(), stack);
            if (windBurst > 0) {
                float knockbackMultiplier;
                if (windBurst == 1) {
                    knockbackMultiplier = 1.2F;
                } else if (windBurst == 2) {
                    knockbackMultiplier = 1.75F;
                } else if (windBurst == 3) {
                    knockbackMultiplier = 2.2F;
                } else {
                    knockbackMultiplier = 1.5F + 0.35F * (windBurst - 1);
                }
                WindChargeExplosion explosion = new WindChargeExplosion(
                        player.serverLevel(), null, null,
                        new SimpleExplosionDamageCalculator(true, false, Optional.of(knockbackMultiplier),
                                BuiltInRegistries.BLOCK.getTag(ModTags.Blocks.BLOCKS_WIND_CHARGE_EXPLOSIONS)
                                        .map(holders -> (HolderSet<Block>) holders)),
                        player.getX(), player.getY(), player.getZ(), 3.5F,
                        ModParticleTypes.GUST_EMITTER_SMALL.get(), ModParticleTypes.GUST_EMITTER_LARGE.get(),
                        ModSounds.BREEZE_WIND_CHARGE_BURST.get());
                explosion.explode();
                explosion.finalizeExplosion(true);
                impulse.onMaceSmashImpact();
            }
        }
        return true;
    }

    public static float getAttackDamageBonus(Player attacker) {
        if (!(attacker.getMainHandItem().getItem() instanceof MaceItem) || !canSmashAttack(attacker)) {
            return 0.0F;
        }
        float fallDistance = attacker.fallDistance;
        float bonus;
        if (fallDistance <= 3.0F) {
            bonus = 4.0F * fallDistance;
        } else if (fallDistance <= 8.0F) {
            bonus = 12.0F + 2.0F * (fallDistance - 3.0F);
        } else {
            bonus = 22.0F + fallDistance - 8.0F;
        }
        int density = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DENSITY.get(), attacker.getMainHandItem());
        if (density > 0) {
            bonus += 0.5F * density * fallDistance;
        }
        return bonus;
    }

    private static void smashAttack(ServerLevel level, Player player, Entity target) {
        level.levelEvent(2013, target.getOnPos(), 750);
        Vec3 targetPos = target.position();
        level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(SMASH_ATTACK_KNOCKBACK_RADIUS), knockbackPredicate(player, target))
                .forEach(entity -> {
                    Vec3 offset = entity.position().subtract(targetPos);
                    double power = getKnockbackPower(player, entity, offset);
                    if (power > 0.0D) {
                        Vec3 push = offset.normalize().scale(power);
                        entity.push(push.x, SMASH_ATTACK_KNOCKBACK_POWER, push.z);
                        if (entity instanceof ServerPlayer serverPlayer) {
                            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
                        }
                    }
                });
    }

    private static Predicate<LivingEntity> knockbackPredicate(Player player, Entity target) {
        return entity -> {
            boolean notSpectator = !entity.isSpectator();
            boolean notSelfOrTarget = entity != player && entity != target;
            boolean notAlly = !player.isAlliedTo(entity);
            boolean notOwnedByPlayer = !(entity instanceof OwnableEntity ownable
                    && ownable.getOwnerUUID() != null && player.getUUID().equals(ownable.getOwnerUUID()));
            boolean notMarkerStand = !(entity instanceof ArmorStand armorStand) || !armorStand.isMarker();
            boolean inRange = target.distanceToSqr(entity) <= SMASH_ATTACK_KNOCKBACK_RADIUS * SMASH_ATTACK_KNOCKBACK_RADIUS;
            return notSpectator && notSelfOrTarget && notAlly && notOwnedByPlayer && notMarkerStand && inRange;
        };
    }

    private static double getKnockbackPower(Player player, LivingEntity entity, Vec3 offset) {
        return (SMASH_ATTACK_KNOCKBACK_RADIUS - offset.length()) * SMASH_ATTACK_KNOCKBACK_POWER
                * (player.fallDistance > SMASH_ATTACK_HEAVY_THRESHOLD ? 2 : 1)
                * (1.0D - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
    }

    public static boolean canSmashAttack(LivingEntity attacker) {
        return attacker.fallDistance > SMASH_ATTACK_FALL_THRESHOLD && !attacker.isFallFlying();
    }
}
