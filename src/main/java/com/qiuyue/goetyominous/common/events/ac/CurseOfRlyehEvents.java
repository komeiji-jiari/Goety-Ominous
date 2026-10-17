package com.qiuyue.goetyominous.common.events.ac;

import com.Polarice3.Goety.api.entities.ISpellEntity;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.entities.neutral.GulfTentacle;
import com.Polarice3.Goety.common.entities.projectiles.SpellHurtingProjectile;
import com.Polarice3.Goety.common.entities.projectiles.WaterHurtingProjectile;
import com.Polarice3.Goety.common.events.spell.CastMagicEvent;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.utils.ac.CurseOfRlyehHelper;
import com.qiuyue.goetyominous.utils.ac.KeyOfRlyehMixinHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class CurseOfRlyehEvents {

    private static final String ABYSS_ORB_CLASS =
            "com.Polarice3.goety_cataclysm.common.entities.projectiles.AbyssOrb";
    private static final String ABYSS_MINE_CLASS =
            "com.Polarice3.goety_cataclysm.common.entities.projectiles.AbyssMine";
    private static final String WATER_SPEAR_CLASS =
            "com.github.L_Ender.cataclysm.entity.projectile.Water_Spear_Entity";

    private static final double ORDNANCE_MATCH_DISTANCE_SQR = 0.25D;
    private static final int TRACKED_SPELL_LIMIT = 256;

    private static final Map<Entity, TrackedSpell> TRACKED_SPELLS = new HashMap<>();
    private static final Set<DamageSource> ORDNANCE_EXPLOSIONS =
            Collections.newSetFromMap(new WeakHashMap<>());

    private static final long KEY_CAST_WINDOW = 200L;
    private static final Map<LivingEntity, Long> KEY_CASTERS = new WeakHashMap<>();

    @SubscribeEvent
    public static void onCastMagic(CastMagicEvent event) {
        LivingEntity caster = event.getEntity();
        if (caster.level().isClientSide) {
            return;
        }
        ISpell spell = event.getSpell();
        boolean keyCast = spell != null
                && spell.getSpellType() == SpellType.ABYSS
                && KeyOfRlyehMixinHelper.isKeyOfRlyeh(WandUtil.findWand(caster));
        if (keyCast) {
            KEY_CASTERS.put(caster, caster.level().getGameTime() + KEY_CAST_WINDOW);
        } else {
            KEY_CASTERS.remove(caster);
        }
    }

    private static boolean isKeyCastActive(LivingEntity caster) {
        Long until = KEY_CASTERS.get(caster);
        return until != null && caster.level().getGameTime() <= until;
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) {
            return;
        }
        DamageSource source = event.getSource();
        boolean keySpellHit = isKeySpellHit(source) || ORDNANCE_EXPLOSIONS.contains(source);
        float amount = CurseOfRlyehHelper.amplifyCursedDamage(target, source, event.getAmount());
        if (keySpellHit) {
            amount = CurseOfRlyehHelper.amplifyKeySpellDamage(target, amount);
        }
        event.setAmount(amount);
        if (keySpellHit) {
            CurseOfRlyehHelper.applyCurse(target);
        }
        if (CurseOfRlyehHelper.isElemental(source)) {
            CurseOfRlyehHelper.shortenCurse(target);
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        Entity entity = event.getEntity();
        if (TRACKED_SPELLS.containsKey(entity) || !isSpellEntity(entity)) {
            return;
        }
        LivingEntity owner = ownerOf(entity);
        if (owner == null || !isKeyCastActive(owner)) {
            return;
        }
        if (TRACKED_SPELLS.size() >= TRACKED_SPELL_LIMIT) {
            TRACKED_SPELLS.values().removeIf(tracked -> tracked.entity.isRemoved());
        }
        TRACKED_SPELLS.put(entity, new TrackedSpell(serverLevel, entity, owner, isCataclysmOrdnance(entity)));
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel) || TRACKED_SPELLS.isEmpty()) {
            return;
        }
        Explosion explosion = event.getExplosion();
        Entity exploder = explosion.getExploder();
        for (TrackedSpell tracked : TRACKED_SPELLS.values()) {
            if (tracked.ordnance
                    && !tracked.entity.isRemoved()
                    && tracked.level == serverLevel
                    && tracked.caster == exploder
                    && tracked.entity.distanceToSqr(explosion.getPosition()) <= ORDNANCE_MATCH_DISTANCE_SQR) {
                ORDNANCE_EXPLOSIONS.add(explosion.getDamageSource());
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!TRACKED_SPELLS.isEmpty()) {
            TRACKED_SPELLS.values().removeIf(tracked -> tracked.level == event.getLevel());
        }
    }

    private static boolean isKeySpellHit(DamageSource source) {
        Entity direct = source.getDirectEntity();
        if (direct == null) {
            return false;
        }
        TrackedSpell tracked = TRACKED_SPELLS.get(direct);
        if (tracked != null) {
            if (!tracked.entity.isRemoved()) {
                return true;
            }
            TRACKED_SPELLS.remove(direct);
        }
        if (!isSpellEntity(direct)) {
            return false;
        }
        LivingEntity owner = ownerOf(direct);
        if (owner == null || !isKeyCastActive(owner)) {
            return false;
        }
        TRACKED_SPELLS.put(direct, new TrackedSpell((Level) direct.level(), direct, owner, isCataclysmOrdnance(direct)));
        return true;
    }

    private static boolean isSpellEntity(Entity entity) {
        if (entity instanceof ISpellEntity
                || entity instanceof SpellHurtingProjectile
                || entity instanceof WaterHurtingProjectile
                || entity instanceof GulfTentacle) {
            return true;
        }
        if (entity instanceof LivingEntity) {
            return false;
        }
        if (WATER_SPEAR_CLASS.equals(entity.getClass().getName())) {
            return true;
        }
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return key != null && "goety_cataclysm".equals(key.getNamespace());
    }

    private static boolean isCataclysmOrdnance(Entity entity) {
        String className = entity.getClass().getName();
        return ABYSS_ORB_CLASS.equals(className) || ABYSS_MINE_CLASS.equals(className);
    }

    private static LivingEntity ownerOf(Entity entity) {
        if (entity instanceof Projectile projectile) {
            return projectile.getOwner() instanceof LivingEntity owner ? owner : null;
        }
        if (entity instanceof OwnableEntity ownable) {
            return ownable.getOwner();
        }
        try {
            return entity.getClass().getMethod("getCaster").invoke(entity) instanceof LivingEntity owner
                    ? owner : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static final class TrackedSpell {
        private final Level level;
        private final Entity entity;
        private final LivingEntity caster;
        private final boolean ordnance;

        private TrackedSpell(Level level, Entity entity, LivingEntity caster, boolean ordnance) {
            this.level = level;
            this.entity = entity;
            this.caster = caster;
            this.ordnance = ordnance;
        }
    }
}
