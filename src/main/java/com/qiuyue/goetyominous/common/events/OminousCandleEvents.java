package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.SoulSummonVFX;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.spells.ShockwaveSpell;
import com.Polarice3.Goety.common.research.ResearchList;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ModDamageTypes;
import com.qiuyue.goetyominous.common.items.ModItems;
import com.qiuyue.goetyominous.common.items.OminousCandleItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OminousCandleEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        Player player = event.player;
        if (player.isDeadOrDying()) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof OminousCandleItem)) {
            return;
        }
        if (LichdomHelper.isLich(player)) {
            return;
        }
        if (SEHelper.hasResearch(player, ResearchList.FORBIDDEN)
                && SEHelper.getSoulAmountInt(player) >= 20000) {
            return;
        }
        player.addEffect(new MobEffectInstance(GoetyEffects.SOUL_HUNGER.get(), 60));
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide || event.getAmount() <= 0.0F || event.getEntity() == event.getSource().getEntity()) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }
        if (!event.getSource().is(DamageTypes.PLAYER_ATTACK)) {
            return;
        }
        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof OminousCandleItem)) {
            return;
        }
        event.getEntity().addEffect(new MobEffectInstance(GoetyEffects.CURSED.get(), 100, 1));
        if (LichdomHelper.isLich(player)) {
            int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SOUL_EATER.get(), weapon);
            player.heal(event.getAmount() * 0.05F * (level + 1));
        }
        if (SEHelper.getSoulAmountInt(player) >= 70000) {
            long now = player.level().getGameTime();
            Long readyAt = OminousCandleItem.SHOCKWAVE_COOLDOWNS.get(player);
            if (readyAt == null || now >= readyAt) {
                OminousCandleItem.SHOCKWAVE_COOLDOWNS.put(player, now + 60);
                triggerShockwave(player);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }
        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof OminousCandleItem)) {
            return;
        }
        DamageSource source = event.getSource();
        boolean direct = source.is(DamageTypes.PLAYER_ATTACK) && source.getDirectEntity() == player;
        boolean shockwave = source.is(ModDamageTypes.OMINOUS_SHOCKWAVE);
        if (!direct && !shockwave) {
            return;
        }
        LivingEntity victim = event.getEntity();
        ResourceLocation type = ForgeRegistries.ENTITY_TYPES.getKey(victim.getType());
        if (type != null) {
            OminousCandleItem.addUniqueKill(weapon, type);
        }
        if (direct) {
            int base = SEHelper.getSoulGiven(victim);
            if (base > 0) {
                SEHelper.increaseSouls(player, 2 * base * MainConfig.SoulTakenMultiplier.get());
            }
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (projectile.level().isClientSide || projectile.isRemoved()) {
            return;
        }
        if (!(event.getRayTraceResult() instanceof EntityHitResult hit) || !(hit.getEntity() instanceof Player player)) {
            return;
        }
        if (!player.isUsingItem() || !player.getUseItem().is(ModItems.OMINOUS_CANDLE.get())) {
            return;
        }
        if (projectile.getOwner() == player) {
            return;
        }
        Vec3 delta = projectile.position().subtract(player.position());
        Vec3 view = player.getViewVector(1.0F);
        if (delta.x * delta.x + delta.z * delta.z > 1.0E-4D && delta.x * view.x + delta.z * view.z >= 0.0D) {
            return;
        }
        event.setCanceled(true);
        projectile.discard();
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 0.8F, 1.0F);
            serverLevel.sendParticles(ParticleTypes.CRIT, projectile.getX(), projectile.getY(), projectile.getZ(),
                    8, 0.2D, 0.2D, 0.2D, 0.1D);
        }
    }

    @SubscribeEvent
    public static void onShieldBlock(ShieldBlockEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }
        if (!event.getDamageSource().is(DamageTypeTags.IS_PROJECTILE)) {
            return;
        }
        if (!player.getUseItem().is(ModItems.OMINOUS_CANDLE.get())) {
            return;
        }
        event.setShieldTakesDamage(false);
    }

    private static void triggerShockwave(Player player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        DamageSource source = ModDamageTypes.ominousShockwave(serverLevel, player);
        ItemStack weapon = player.getMainHandItem();
        float weaponDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        int fireAspect = EnchantmentHelper.getFireAspect(player);
        for (LivingEntity victim : serverLevel.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(8.0D))) {
            if (victim == player || MobUtil.areAllies(player, victim)
                    || victim.distanceToSqr(player) > 64.0D) {
                continue;
            }
            float damage = (weaponDamage + EnchantmentHelper.getDamageBonus(weapon, victim.getMobType())) / 2.0F;
            victim.addEffect(new MobEffectInstance(GoetyEffects.CURSED.get(), 100, 1));
            int previousInvulnerableTime = victim.invulnerableTime;
            victim.invulnerableTime = 0;
            boolean hit = victim.hurt(source, damage);
            victim.invulnerableTime = previousInvulnerableTime;
            if (hit && fireAspect > 0) {
                victim.setSecondsOnFire(fireAspect * 4);
            }
        }
        ColorUtil color = ShockwaveSpell.SHOCKWAVE_COLOR;
        Vec3 center = player.position().add(0.0D, 0.5D, 0.0D);
        int souls = 2 + serverLevel.random.nextInt(2);
        for (int i = 0; i < souls; i++) {
            SoulSummonVFX.bombSoul(serverLevel, player.position(), serverLevel.random, false);
        }
        serverLevel.sendParticles(new CircleExplodeParticleOption(color.red(), color.green(), color.blue(), 8.0F, 1),
                center.x, center.y, center.z, 0, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(new SphereExplodeParticleOption(color.red(), color.green(), color.blue(), 8.0F, 1),
                center.x, center.y, center.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.playSound(null, player.blockPosition(), ModSounds.SOUL_EXPLODE.get(), SoundSource.PLAYERS, 0.6F, 1.0F);
    }
}
