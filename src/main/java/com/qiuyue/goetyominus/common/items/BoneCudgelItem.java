package com.qiuyue.goetyominus.common.items;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.qiuyue.goetyominus.common.init.ModSounds;
import com.qiuyue.goetyominus.config.WeaponConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

public class BoneCudgelItem extends Item {

    private static final float BASE_DAMAGE = WeaponConfig.BoneCudgelDamage.get().floatValue();
    private static final float BASE_ATTACK_SPEED = WeaponConfig.BoneCudgelAttackSpeed.get().floatValue();
    private static final float SPEED_PER_LEVEL = 0.15F;
    private static final int DURABILITY = 930;

    private final Multimap<Attribute, AttributeModifier>[] defaultModifiers = new ImmutableMultimap[4];

    public BoneCudgelItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON).durability(DURABILITY));
        for (int i = 0; i <= 3; i++) {
            this.defaultModifiers[i] = getStatsForSwiftwoodLevel(i);
        }
    }

    private static final java.util.UUID ENTITY_REACH_UUID = java.util.UUID.fromString("90f5c2f4-6f0d-4c5e-8b3a-1a2b3c4d5e6f");

    private ImmutableMultimap<Attribute, AttributeModifier> getStatsForSwiftwoodLevel(int level) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", BASE_DAMAGE, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                BASE_ATTACK_SPEED_UUID, "Weapon modifier", BASE_ATTACK_SPEED, AttributeModifier.Operation.ADDITION));
        builder.put(net.minecraftforge.common.ForgeMod.ENTITY_REACH.get(), new AttributeModifier(
                ENTITY_REACH_UUID, "Entity reach modifier", 1.0, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? defaultModifiers[0] : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return player.getAttackStrengthScale(0.5F) < 1.0F;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (player.getAttackStrengthScale(0.5F) < 1.0F && player.attackAnim > 0) {
                return true;
            } else {
                player.swingTime = -1;
            }
        }
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean held) {
        if (entity instanceof Player player && held) {
            if (player.getAttackStrengthScale(0.5F) < 0.95F && player.attackAnim > 0) {
                player.swingTime--;
            }
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));

        target.knockback(2.0D, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());

        if (target instanceof Player player && player.isBlocking()) {
            player.disableShield(true);
        }

        if (!target.level().isClientSide) {
            SoundEvent sound = ModSounds.BONE_CUDGEL_1.get();

            if (target.getRandom().nextFloat() < 0.75F) {
                int duration = 140 + target.getRandom().nextInt(160);
                MobEffectInstance stun = new MobEffectInstance(GoetyEffects.STUNNED.get(), duration, 0, false, false);
                if (target.addEffect(stun)) {
                    sound = ModSounds.BONE_CUDGEL_3.get();

                    int sweepLevel = stack.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.SWEEPING_EDGE);
                    if (sweepLevel > 0) {
                        float range = sweepLevel + 1.2F;
                        AABB aabb = AABB.ofSize(target.position(), range, range, range);
                        for (Entity entity : target.level().getEntities(attacker, aabb, Entity::canBeHitByProjectile)) {
                            if (entity != target && !entity.isAlliedTo(attacker)
                                    && entity.distanceTo(target) <= range
                                    && entity instanceof LivingEntity inflict) {
                                inflict.hurt(inflict.level().damageSources().mobAttack(attacker), 1.0F);
                                inflict.addEffect(new MobEffectInstance(
                                        GoetyEffects.STUNNED.get(), 80 + target.getRandom().nextInt(80), 0, false, false));
                            }
                        }
                    }
                }
            }

            target.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                    sound, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
            stack.hurtAndBreak(2, miner, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getCount() == 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.world.item.enchantment.Enchantment enchantment) {
        return enchantment.category == net.minecraft.world.item.enchantment.EnchantmentCategory.WEAPON
                || enchantment == net.minecraft.world.item.enchantment.Enchantments.SWEEPING_EDGE
                || enchantment == net.minecraft.world.item.enchantment.Enchantments.VANISHING_CURSE;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
        return repair.is(net.minecraft.world.item.Items.BONE_BLOCK);
    }
}