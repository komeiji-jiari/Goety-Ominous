package com.qiuyue.goetyominus.common.items.mm;

import com.Polarice3.Goety.api.items.IPersist;
import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.common.items.equipment.DarkScytheItem;
import com.alexander.mutantmore.init.SoundEventInit;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.qiuyue.goetyominus.common.entities.ally.mobs.mm.WitherSlash;
import com.qiuyue.goetyominus.config.WeaponConfig;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;
import java.util.function.Consumer;

public class WitherScytheItem extends DarkScytheItem implements ISoulRepair, IPersist {

    private final Multimap<Attribute, AttributeModifier> witherScytheAttributes;

    public WitherScytheItem() {
        super();
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(
                BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                com.qiuyue.goetyominus.config.WeaponConfig.WitherScytheDamage.get() - 1.0,
                AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(
                BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                WeaponConfig.WitherScytheAttackSpeed.get(),
                AttributeModifier.Operation.ADDITION));
        builder.put(net.minecraftforge.common.ForgeMod.ENTITY_REACH.get(), new AttributeModifier(
                UUID.fromString("90f5c2f4-6f0d-4c5e-8b3a-1a2b3c4d5e6f"), "Entity reach modifier", 1.0, AttributeModifier.Operation.ADDITION));
        this.witherScytheAttributes = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.witherScytheAttributes
                : super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            return !this.isNotBroken(stack) && com.Polarice3.Goety.config.ItemConfig.DeathScythePersist.get()
                    ? com.google.common.collect.ImmutableMultimap.of()
                    : this.witherScytheAttributes;
        }
        return super.getAttributeModifiers(slot, stack);
    }

    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }

    public void strike(Level level, Player player, ItemStack stack) {
        if (player.getAttackStrengthScale(0.5F) > 0.848F && !player.isSwimming() && !player.isFallFlying()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEventInit.MUTANT_WITHER_SKELETON_FIRE_SLASH.get(), SoundSource.PLAYERS, 2.0F,
                    0.8F + level.random.nextFloat() * 0.4F / 0.8F);

            if (!level.isClientSide) {
                Vec3 look = player.getLookAngle();
                WitherSlash slash = new WitherSlash(level, player, player.getYRot());
                slash.setPos(player.getX() + look.x * 2.0, player.getY(1.0) + look.y * 2.0, player.getZ() + look.z * 2.0);
                slash.setDeltaMovement(look.x * 1.5, look.y * 1.5, look.z * 1.5);
                slash.damage = 9.5F;
                slash.leechAmount = 2.375F;
                slash.witherLength = 400;
                slash.witherLevel = 0;
                slash.ignoresInvulTime = false;
                slash.setSize(1.0F);
                slash.setWeapon(stack);
                level.addFreshEntity(slash);
            }
        }
    }

    public static void emptyClick(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof WitherScytheItem) {
            com.qiuyue.goetyominus.common.network.ModNetwork.CHANNEL.sendToServer(
                    new com.qiuyue.goetyominus.common.network.CWitherScytheStrikePacket());
        }
    }

    public static void entityClick(Player player, Level level) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof WitherScytheItem && !stack.isEmpty()) {
            if (!level.isClientSide && !player.isSpectator()) {
                ((WitherScytheItem) stack.getItem()).strike(level, player, stack);
            }
        }
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (com.Polarice3.Goety.config.ItemConfig.DeathScythePersist.get() && stack.getDamageValue() + amount >= stack.getMaxDamage()) {
            if (stack.getDamageValue() != stack.getMaxDamage() - 1) {
                stack.setDamageValue(stack.getMaxDamage() - 1);
                onBroken.accept(entity);
            }
            return 0;
        }
        return amount;
    }

    @Override
    public boolean isBroken(ItemStack stack) {
        return com.Polarice3.Goety.api.items.IPersist.super.isBroken(stack)
                && com.Polarice3.Goety.config.ItemConfig.DeathScythePersist.get();
    }

    @Override
    public float getDestroySpeed(ItemStack stack, net.minecraft.world.level.block.state.BlockState state) {
        return !this.isNotBroken(stack) && com.Polarice3.Goety.config.ItemConfig.DeathScythePersist.get()
                ? 1.0F : super.getDestroySpeed(stack, state);
    }
}
