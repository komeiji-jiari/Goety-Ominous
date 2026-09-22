package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorsaurusSpiritEntity;
import com.qiuyue.goetyominous.common.init.ac.AcEffects;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TremorsaurusSpiritHandler {

    private static final Map<UUID, Long> SPIRIT_COOLDOWN = new HashMap<>();

    public static final String STAFF_BONUS_TAG = "GoetyOminousTremorSpiritBonus";

    public static void setStaffBonus(LivingEntity caster, boolean rightStaff) {
        caster.getPersistentData().putInt(STAFF_BONUS_TAG, rightStaff ? 1 : 0);
    }

    public static int getStaffBonus(LivingEntity caster) {
        return caster.getPersistentData().getInt(STAFF_BONUS_TAG);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!(event.getSource().getDirectEntity() instanceof LivingEntity attacker)
                || event.getSource().getEntity() != attacker
                || attacker == target) {
            return;
        }
        MobEffectInstance effect = attacker.getEffect(AcEffects.TREMORSAURUS_SPIRIT.get());
        if (effect == null) {
            return;
        }
        if (attacker instanceof Player player && player.getAttackStrengthScale(0.5F) < 0.9F) {
            return;
        }
        if (isFriendly(attacker, target)) {
            return;
        }
        long now = serverLevel.getGameTime();
        int cooldown = SpellConfig.TremorSpiritSpiritCooldown.get();
        if (cooldown > 0) {
            Long last = SPIRIT_COOLDOWN.get(attacker.getUUID());
            if (last != null && now - last < cooldown) {
                return;
            }
        }
        SPIRIT_COOLDOWN.put(attacker.getUUID(), now);
        summonSpirit(serverLevel, attacker, target, effect.getAmplifier(), getStaffBonus(attacker));
    }

    private static boolean isFriendly(LivingEntity attacker, LivingEntity target) {
        if (target == attacker) {
            return true;
        }
        if (target instanceof IOwned owned && owned.getTrueOwner() == attacker) {
            return true;
        }
        return attacker instanceof IOwned a && target instanceof IOwned t && MobUtil.ownerStack(a, t);
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        SPIRIT_COOLDOWN.clear();
    }

    private static void summonSpirit(ServerLevel level, LivingEntity attacker, LivingEntity target, int potency, int bonusDamage) {
        TremorsaurusSpiritEntity spirit = AcEntityRegistry.TREMORSAURUS_SPIRIT.get().create(level);
        if (spirit == null) {
            return;
        }
        Vec3 between = attacker.position().add(target.position()).scale(0.5D);
        spirit.setPos(between.x, attacker.getY() + 1.0D, between.z);
        spirit.setOwnerUUID(attacker.getUUID());
        spirit.setPotency(potency);
        spirit.setBonusDamage(bonusDamage);
        spirit.setAttackingEntityId(target.getId());
        spirit.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
        spirit.setDelaySpawn(5);
        level.addFreshEntity(spirit);
        target.setSecondsOnFire(5);
    }
}
