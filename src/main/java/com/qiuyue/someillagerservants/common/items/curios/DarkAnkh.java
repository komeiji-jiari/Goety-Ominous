package com.qiuyue.someillagerservants.common.items.curios;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public class DarkAnkh extends Item implements ICurioItem {

    private static final Map<Player, Integer> LAST_HEAL_TICK_MAP = new WeakHashMap<>();
    private static final int HEAL_INTERVAL_TICKS = 20;
    private static final int SOUL_COST_PER_TICK = 2;
    private static final float BASE_HEAL_AMOUNT = 1.0F;
    private static final float DEATH_SAVE_BASE_CHANCE = 0.05F;
    private static final float DEATH_SAVE_MAX_CHANCE = 0.15F;
    private static final int MAX_DURATION_LEVEL = 3;
    private static final float LOW_HEALTH_THRESHOLD = 120.0F;
    private static final double SERVANT_TRIGGER_RADIUS = 16.0D;
    private static final int MAX_POTENCY_LEVEL = 3;

    public DarkAnkh() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean canEquipFromUse(SlotContext context, ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(Component.translatable("info.someillagerservants.dark_ankh").withStyle(ChatFormatting.DARK_PURPLE));

        int duration = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DURATION.get(), stack);
        int velocity = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.VELOCITY.get(), stack);
        int potency = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.POTENCY.get(), stack);

        if (duration > 0) {
            int effectiveDuration = Math.min(duration, MAX_DURATION_LEVEL);
            float deathSaveChance = DEATH_SAVE_BASE_CHANCE + (effectiveDuration * 0.05F);
            deathSaveChance = Math.min(deathSaveChance, DEATH_SAVE_MAX_CHANCE);
            int chancePercent = (int) (deathSaveChance * 100);
            tooltip.add(Component.translatable("info.someillagerservants.dark_ankh.duration", effectiveDuration, chancePercent).withStyle(ChatFormatting.BLUE));
        }

        if (velocity > 0 || potency > 0) {
            tooltip.add(Component.translatable("info.someillagerservants.dark_ankh.spell_buff").withStyle(ChatFormatting.DARK_PURPLE));
        }

        if (velocity > 0) {
            int velocityLevel = Math.min(velocity, 5);
            int speedDurationSeconds = velocityLevel;
            int speedAmplifier = velocityLevel >= 2 ? 1 : 0;
            tooltip.add(Component.translatable("info.someillagerservants.dark_ankh.velocity", velocity, speedAmplifier + 1, speedDurationSeconds).withStyle(ChatFormatting.BLUE));
        }

        if (potency > 0) {
            int effectivePotency = Math.min(potency, MAX_POTENCY_LEVEL);
            int buffDurationSeconds = calculateBuffDuration(effectivePotency);
            int buffAmplifier = Math.min(effectivePotency, 3) - 1;
            tooltip.add(Component.translatable("info.someillagerservants.dark_ankh.potency", effectivePotency, buffAmplifier + 1, buffDurationSeconds).withStyle(ChatFormatting.BLUE));
        }
    }

    private static int calculateBuffDuration(int potencyLevel) {
        switch (potencyLevel) {
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
            default:
                return 5;
        }
    }

    public static void tryHealServant(LivingEntity servant, Player owner) {
        if (servant.level().isClientSide) return;
        if (!(servant.level() instanceof ServerLevel serverLevel)) return;

        if (!hasDarkAnkh(owner)) return;

        ItemStack ankh = getDarkAnkh(owner);
        if (ankh.isEmpty()) return;

        if (isInHealHalt(servant)) {
            return;
        }

        int currentTick = (int) serverLevel.getGameTime();
        Integer lastHealTick = LAST_HEAL_TICK_MAP.get(owner);

        if (lastHealTick != null && (currentTick - lastHealTick) < HEAL_INTERVAL_TICKS) {
            return;
        }

        if (servant.getHealth() < servant.getMaxHealth()) {
            tryHealServantInternal(servant, owner, ankh, serverLevel, currentTick);
        }
    }

    public static boolean trySaveFromDeath(LivingEntity servant, Player owner) {
        if (servant.level().isClientSide) return false;
        if (!(servant.level() instanceof ServerLevel serverLevel)) return false;

        if (!hasDarkAnkh(owner)) return false;

        ItemStack ankh = getDarkAnkh(owner);
        if (ankh.isEmpty()) return false;

        if (wasKilledByOwner(servant, owner)) {
            return false;
        }

        if (isExpiredServant(servant)) {
            return false;
        }

        if (!killedByHostileMob(servant)) {
            return false;
        }

        int duration = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.DURATION.get(), ankh);
        int effectiveDuration = Math.min(duration, MAX_DURATION_LEVEL);
        float deathSaveChance = DEATH_SAVE_BASE_CHANCE + (effectiveDuration * 0.05F);
        deathSaveChance = Math.min(deathSaveChance, DEATH_SAVE_MAX_CHANCE);

        if (servant.getRandom().nextFloat() >= deathSaveChance) {
            return false;
        }

        if (servant.getMaxHealth() > LOW_HEALTH_THRESHOLD) {
            return false;
        }

        float reviveHealth = servant.getMaxHealth() / 3.0F;
        int soulCost = (int) Math.ceil(reviveHealth * 1.5F);

        if (!SEHelper.getSoulsAmount(owner, soulCost)) {
            return false;
        }

        SEHelper.decreaseSouls(owner, soulCost);

        servant.setHealth(reviveHealth);
        servant.invulnerableTime = 60;

        playTotemAnimation(servant, serverLevel);

        return true;
    }

    public static void triggerServantBuffOnSpellCast(Player caster, ISpell spell, ItemStack wand) {
        if (caster.level().isClientSide) return;
        if (!(caster.level() instanceof ServerLevel serverLevel)) return;

        if (!hasDarkAnkh(caster)) return;

        ItemStack ankh = getDarkAnkh(caster);
        if (ankh.isEmpty()) return;

        if (spell == null || spell.getSpellType() != SpellType.NECROMANCY) return;

        if (spell.defaultCastDuration() <= 0) return;

        ItemStack focus = IWand.getFocus(wand);
        if (focus.isEmpty()) return;

        int velocityLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.VELOCITY.get(), ankh);
        int potencyLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.POTENCY.get(), ankh);

        if (velocityLevel <= 0 && potencyLevel <= 0) return;

        int speedDuration = Math.min(velocityLevel, 5) * 20;

        int effectivePotency = Math.min(potencyLevel, MAX_POTENCY_LEVEL);
        int buffDuration = calculateBuffDuration(effectivePotency) * 20;
        int buffAmplifier = Math.min(effectivePotency, 3) - 1;

        List<LivingEntity> servants = serverLevel.getEntitiesOfClass(LivingEntity.class,
                caster.getBoundingBox().inflate(SERVANT_TRIGGER_RADIUS),
                entity -> entity instanceof IOwned owned && owned.getTrueOwner() == caster
        );

        for (LivingEntity servant : servants) {
            boolean applied = false;
            if (speedDuration > 0) {
                applySpeedEffect(servant, speedDuration);
                applied = true;
            }
            if (buffDuration > 0 && buffAmplifier >= 0) {
                applyBuffEffect(servant, buffDuration, buffAmplifier);
                applied = true;
            }
            if (applied) {
                spawnSoulHealParticles(servant, serverLevel);
            }
        }
    }

    private static void applySpeedEffect(LivingEntity servant, int durationTicks) {
        if (durationTicks <= 0) return;

        int amplifier = durationTicks >= 40 ? 1 : 0;

        MobEffectInstance existingEffect = servant.getEffect(MobEffects.MOVEMENT_SPEED);
        if (existingEffect != null && existingEffect.getAmplifier() >= amplifier) {
            int newDuration = Math.max(existingEffect.getDuration(), durationTicks);
            servant.removeEffect(MobEffects.MOVEMENT_SPEED);
            servant.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, newDuration, amplifier, false, false, true));
        } else {
            servant.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, durationTicks, amplifier, false, false, true));
        }
    }

    private static void applyBuffEffect(LivingEntity servant, int durationTicks, int amplifier) {
        if (durationTicks <= 0 || amplifier < 0) return;

        MobEffectInstance existingEffect = servant.getEffect(GoetyEffects.BUFF.get());
        if (existingEffect != null && existingEffect.getAmplifier() >= amplifier) {
            int newDuration = Math.max(existingEffect.getDuration(), durationTicks);
            servant.removeEffect(GoetyEffects.BUFF.get());
            servant.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(), newDuration, amplifier, false, false, true));
        } else {
            servant.addEffect(new MobEffectInstance(GoetyEffects.BUFF.get(), durationTicks, amplifier, false, false, true));
        }
    }

    private static void spawnSoulHealParticles(LivingEntity servant, ServerLevel serverLevel) {
        double colorRed = 0.1647;
        double colorGreen = 0.7882;
        double colorBlue = 0.8118;

        serverLevel.sendParticles(
                new com.Polarice3.Goety.client.particles.RisingCircleParticleOption(0),
                servant.getX(), servant.getY(), servant.getZ(),
                0, colorRed, colorGreen, colorBlue, 1.0F
        );
        serverLevel.sendParticles(
                new com.Polarice3.Goety.client.particles.RisingCircleParticleOption(5),
                servant.getX(), servant.getY(), servant.getZ(),
                0, colorRed, colorGreen, colorBlue, 1.0F
        );
        serverLevel.sendParticles(
                new com.Polarice3.Goety.client.particles.RisingCircleParticleOption(10),
                servant.getX(), servant.getY(), servant.getZ(),
                0, colorRed, colorGreen, colorBlue, 1.0F
        );
    }

    private static boolean wasKilledByOwner(LivingEntity servant, Player owner) {
        if (servant.getLastHurtByMob() == owner) {
            return true;
        }

        if (servant.getLastHurtByMobTimestamp() > 0) {
            LivingEntity lastAttacker = servant.getLastHurtByMob();
            if (lastAttacker == owner) {
                return true;
            }
        }

        return false;
    }

    private static boolean killedByHostileMob(LivingEntity servant) {
        LivingEntity lastAttacker = servant.getLastHurtByMob();
        if (lastAttacker == null) {
            return false;
        }

        return isHostileMob(lastAttacker);
    }

    private static boolean isHostileMob(LivingEntity entity) {
        if (entity instanceof Player) {
            return false;
        }

        if (entity instanceof net.minecraft.world.entity.Mob mob) {
            return mob.getTarget() != null || !mob.getType().getCategory().isFriendly();
        }

        return false;
    }

    private static boolean isExpiredServant(LivingEntity servant) {
        if (servant instanceof IOwned owned) {
            return owned.hasLifespan() && owned.getLifespan() <= 20;
        }
        return false;
    }

    private static boolean isInHealHalt(LivingEntity servant) {
        return MiscCapHelper.getNoHealTime(servant) > 0;
    }

    private static void playTotemAnimation(LivingEntity livingEntity, ServerLevel serverLevel) {
        livingEntity.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);

        for (int i = 0; i < 30; ++i) {
            double d0 = livingEntity.getRandomX(0.5D);
            double d1 = livingEntity.getRandomY() + livingEntity.getBbHeight() * 0.5D;
            double d2 = livingEntity.getRandomZ(0.5D);
            double d3 = livingEntity.getRandom().nextGaussian() * 0.3D;
            double d4 = livingEntity.getRandom().nextGaussian() * 0.3D;
            double d5 = livingEntity.getRandom().nextGaussian() * 0.3D;
            serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, d0, d1, d2, 1, d3, d4, d5, 0.5D);
        }
    }

    private static void tryHealServantInternal(LivingEntity servant, Player owner, ItemStack ankh, ServerLevel serverLevel, int currentTick) {
        if (!consumeSoulsAndRecordTick(owner, serverLevel, ankh, currentTick)) return;

        servant.heal(BASE_HEAL_AMOUNT);
        healParticles(servant, serverLevel);
    }

    private static boolean consumeSoulsAndRecordTick(Player owner, ServerLevel serverLevel, ItemStack ankh, int currentTick) {
        if (!SEHelper.getSoulsAmount(owner, SOUL_COST_PER_TICK)) return false;

        LAST_HEAL_TICK_MAP.put(owner, currentTick);
        SEHelper.decreaseSouls(owner, SOUL_COST_PER_TICK);
        return true;
    }

    private static boolean hasDarkAnkh(Player player) {
        return top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player)
                .map(inv -> inv.findFirstCurio(com.qiuyue.someillagerservants.common.items.ModItems.DARK_ANKH.get()))
                .orElse(Optional.empty())
                .isPresent();
    }

    private static ItemStack getDarkAnkh(Player player) {
        return top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player)
                .map(inv -> inv.findFirstCurio(com.qiuyue.someillagerservants.common.items.ModItems.DARK_ANKH.get()))
                .orElse(Optional.empty())
                .map(slotResult -> slotResult.stack())
                .orElse(ItemStack.EMPTY);
    }

    private static void healParticles(LivingEntity livingEntity, ServerLevel serverLevel) {
        double d0 = livingEntity.getRandomX(0.5D);
        double d1 = livingEntity.getRandomY();
        double d2 = livingEntity.getRandomZ(0.5D);
        serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, d0, d1, d2, 3, 0.0D, 0.1D, 0.0D, 0.02D);
    }
}