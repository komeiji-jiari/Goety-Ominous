package com.qiuyue.goetyominous.compat.mod;

import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.WandUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class GoetyCataclysmCompat {

    private static final double MINE_SPREAD_XZ = 12.0D;
    private static final double MINE_SPREAD_Y = 8.0D;
    private static final double PORTAL_SPREAD = 12.0D;
    private static final int KEY_ORB_RING_SIX = 6;
    private static final int KEY_ORB_RING_THREE = 3;
    private static final int KEY_MINE_COUNT = 8;
    private static final int KEY_MINE_INDEX_START = 35;
    private static final int KEY_PORTAL_COUNT = 4;
    private static final int PORTAL_DELAY_STEP = 5;
    private static final int KEY_SPEAR_BOUNCES = 8;

    private static Boolean modLoaded;
    private static boolean checked;
    private static boolean usable;
    private static boolean spearChecked;
    private static boolean spearUsable;

    @Nullable private static Class<?> orbSpellClass;
    @Nullable private static Class<?> minesSpellClass;
    @Nullable private static Class<?> spearSpellClass;
    @Nullable private static Class<?> beamSpellClass;
    @Nullable private static Class<?> markClass;
    @Nullable private static Class<?> portalClass;
    @Nullable private static Method shootAbyssOrbMethod;
    @Nullable private static Method spawnMinesMethod;
    @Nullable private static Method spawnUnderPortalMethod;
    @Nullable private static Method markCreatorMethod;
    @Nullable private static Method portalCasterMethod;
    @Nullable private static Field markFinalTargetField;
    @Nullable private static Field markPotencyField;
    @Nullable private static Constructor<?> waterSpearCtor;
    @Nullable private static Method totalBouncesMethod;
    @Nullable private static Field spearDamageConfig;
    @Nullable private static Method configValueGet;

    private GoetyCataclysmCompat() {
    }

    public static boolean isLoaded() {
        if (modLoaded == null) {
            modLoaded = ModList.get().isLoaded("goety_cataclysm");
        }
        return modLoaded;
    }

    private static boolean ensureUsable() {
        if (!checked) {
            checked = true;
            if (isLoaded()) {
                try {
                    orbSpellClass = Class.forName("com.Polarice3.goety_cataclysm.common.magic.spells.abyss.AbyssalOrbsSpell");
                    minesSpellClass = Class.forName("com.Polarice3.goety_cataclysm.common.magic.spells.abyss.AbyssalMinesSpell");
                    spearSpellClass = Class.forName("com.Polarice3.goety_cataclysm.common.magic.spells.abyss.WaterSpearSpell");
                    beamSpellClass = Class.forName("com.Polarice3.goety_cataclysm.common.magic.spells.abyss.AbyssalBeamSpell");
                    markClass = Class.forName("com.Polarice3.goety_cataclysm.common.entities.util.AbyssMark");
                    portalClass = Class.forName("com.Polarice3.goety_cataclysm.common.entities.util.AbyssBlastPortal");

                    shootAbyssOrbMethod = orbSpellClass.getMethod("shootAbyssOrb", LivingEntity.class,
                            double.class, double.class, double.class, int.class, float.class);
                    spawnMinesMethod = minesSpellClass.getDeclaredMethod("spawnMines", LivingEntity.class,
                            double.class, double.class, double.class, float.class, int.class, double.class);
                    spawnMinesMethod.setAccessible(true);
                    spawnUnderPortalMethod = markClass.getDeclaredMethod("spawnUnderPortal", LivingEntity.class,
                            double.class, double.class, double.class, double.class, float.class, int.class, int.class);
                    spawnUnderPortalMethod.setAccessible(true);

                    markCreatorMethod = markClass.getMethod("getCreatorEntity");
                    portalCasterMethod = portalClass.getMethod("getCaster");
                    markFinalTargetField = markClass.getDeclaredField("finalTarget");
                    markFinalTargetField.setAccessible(true);
                    markPotencyField = markClass.getDeclaredField("potency");
                    markPotencyField.setAccessible(true);

                    usable = true;
                } catch (Throwable ignored) {
                    usable = false;
                }
            }
        }
        return usable;
    }

    private static boolean ensureSpearUsable() {
        if (!spearChecked) {
            spearChecked = true;
            if (ensureUsable() && ModList.get().isLoaded("cataclysm")) {
                try {
                    Class<?> cfg = Class.forName("com.Polarice3.goety_cataclysm.config.GCSpellConfig");
                    spearDamageConfig = cfg.getField("WaterSpearDamage");
                    configValueGet = Class.forName("net.minecraftforge.common.ForgeConfigSpec$ConfigValue")
                            .getMethod("get");
                    Class<?> spear = Class.forName("com.github.L_Ender.cataclysm.entity.projectile.Water_Spear_Entity");
                    waterSpearCtor = spear.getConstructor(LivingEntity.class, Vec3.class, Level.class,
                            float.class, double.class);
                    totalBouncesMethod = spear.getMethod("setTotalBounces", int.class);
                    spearUsable = true;
                } catch (Throwable ignored) {
                    spearUsable = false;
                }
            }
        }
        return spearUsable;
    }

    public static boolean isOrbSpell(ISpell spell) {
        return ensureUsable() && orbSpellClass.isInstance(spell);
    }

    public static boolean isMinesSpell(ISpell spell) {
        return ensureUsable() && minesSpellClass.isInstance(spell);
    }

    public static boolean isSpearSpell(ISpell spell) {
        return ensureUsable() && spearSpellClass.isInstance(spell);
    }

    public static boolean isBeamSpell(ISpell spell) {
        return ensureUsable() && beamSpellClass.isInstance(spell);
    }

    public static boolean isAbyssMark(Entity entity) {
        return ensureUsable() && markClass.isInstance(entity);
    }

    public static boolean isAbyssBlastPortal(Entity entity) {
        return ensureUsable() && portalClass.isInstance(entity);
    }

    @Nullable
    public static LivingEntity markCreator(Entity mark) {
        if (!ensureUsable()) {
            return null;
        }
        try {
            Object creator = markCreatorMethod.invoke(mark);
            return creator instanceof LivingEntity living ? living : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static int keyPotency(LivingEntity caster, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
        }
        return potency;
    }

    private static float keyRadius(LivingEntity caster, SpellStat spellStat) {
        float radius = (float) spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)) {
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) / 2.0F;
        }
        return radius;
    }

    public static void addKeyOrbs(ISpell spell, LivingEntity caster, SpellStat spellStat) {
        if (!isOrbSpell(spell)) {
            return;
        }
        int potency = keyPotency(caster, spellStat);
        float radius = keyRadius(caster, spellStat);
        for (int i = 0; i < KEY_ORB_RING_SIX; ++i) {
            invokeOrb(spell, caster, new Vec3(1.0D, 0.0D, 1.0D)
                    .yRot((float) Math.toRadians(-(30.0D + 60.0D * i))), potency, radius);
        }
        for (int i = 0; i < KEY_ORB_RING_THREE; ++i) {
            invokeOrb(spell, caster, new Vec3(0.5D, 0.0D, 0.5D)
                    .yRot((float) Math.toRadians(-(60.0D + 120.0D * i))), potency, radius);
        }
    }

    private static void invokeOrb(ISpell spell, LivingEntity caster, Vec3 offset, int potency, float radius) {
        try {
            shootAbyssOrbMethod.invoke(spell, caster, offset.x, offset.y, offset.z, potency, radius);
        } catch (Throwable ignored) {
        }
    }

    public static void addKeyMines(ISpell spell, LivingEntity caster, SpellStat spellStat) {
        if (!isMinesSpell(spell)) {
            return;
        }
        int range = spellStat.getRange();
        double radius = spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getLevels(ModEnchantments.RANGE.get(), caster);
            radius += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster) / 2.0D;
        }
        Vec3 aim = spell.rayTrace(caster.level(), caster, range, 3.0D).getLocation();
        LivingEntity target = spell instanceof Spell goetySpell ? goetySpell.getTarget(caster, range) : null;
        if (target != null) {
            aim = target.position();
        }
        float angle = (float) Math.atan2(aim.z - caster.getZ(), aim.x - caster.getX());
        for (int i = KEY_MINE_INDEX_START; i < KEY_MINE_INDEX_START + KEY_MINE_COUNT; ++i) {
            double x = aim.x + caster.getRandom().nextDouble() * MINE_SPREAD_XZ;
            double y = aim.y + caster.getRandom().nextDouble() * MINE_SPREAD_Y;
            double z = aim.z + caster.getRandom().nextDouble() * MINE_SPREAD_XZ;
            if (!caster.level().isFluidAtPosition(BlockPos.containing(x, y, z), fluid -> fluid.is(FluidTags.WATER))) {
                y = BlockFinder.moveDownToGround(caster) + 1.0D;
            }
            try {
                spawnMinesMethod.invoke(spell, caster, x, y, z, angle, (int) (2.0F * i), radius);
            } catch (Throwable ignored) {
                return;
            }
        }
    }

    public static void addKeySpears(ISpell spell, LivingEntity caster, SpellStat spellStat) {
        if (!isSpearSpell(spell) || !ensureSpearUsable()) {
            return;
        }
        int potency = keyPotency(caster, spellStat);
        float velocity = spellStat.getVelocity();
        if (WandUtil.enchantedFocus(caster)) {
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster) / 10.0F;
        }
        float damage;
        try {
            damage = ((Number) configValueGet.invoke(spearDamageConfig.get(null))).floatValue()
                    * WandUtil.damageMultiply() + (float) potency;
        } catch (Throwable ignored) {
            return;
        }
        Vec3 view = caster.getViewVector(1.0F);
        float spearYRot = (float) Math.atan2(view.z, view.x) * 57.29577951308232F + 90.0F;
        float spearXRot = -(float) Math.atan2(view.y, Math.sqrt(view.x * view.x + view.z * view.z))
                * 57.29577951308232F;
        double spawnX = caster.getX() + Mth.sin(caster.yBodyRot * 0.017453292F) * -0.5D;
        double spawnZ = caster.getZ() + Mth.cos(caster.yBodyRot * 0.017453292F) * -0.5D;
        for (int i : new int[]{0, 6}) {
            double a = (i - 3) * Math.toRadians(15.0D);
            Vec3 dir = new Vec3(view.x * Math.cos(a) + view.z * Math.sin(a), view.y,
                    -view.x * Math.sin(a) + view.z * Math.cos(a)).normalize();
            try {
                Object spear = waterSpearCtor.newInstance(caster, dir, caster.level(), damage, 2.0D + velocity);
                if (spear instanceof Entity entity) {
                    entity.setYRot(spearYRot);
                    entity.setXRot(spearXRot);
                    entity.setPos(spawnX, caster.getEyeY() - 0.2D, spawnZ);
                    totalBouncesMethod.invoke(spear, KEY_SPEAR_BOUNCES + potency);
                    caster.level().addFreshEntity(entity);
                }
            } catch (Throwable ignored) {
                return;
            }
        }
    }

    public static void spawnKeyPortals(Entity mark, Entity firstPortal) {
        if (!ensureUsable() || !markClass.isInstance(mark)) {
            return;
        }
        try {
            LivingEntity hitEntity = portalCaster(firstPortal);
            Entity finalTarget = (Entity) markFinalTargetField.get(mark);
            int potency = markPotencyField.getInt(mark);
            Vec3 view = mark.getViewVector(1.0F);
            double minY = Math.min(view.y, mark.getY()) - 50.0D;
            double maxY = Math.max(view.y, mark.getY()) + 3.0D;
            float yaw = (float) Math.atan2(view.z - mark.getZ(), view.x - mark.getX());
            if (finalTarget != null) {
                minY = Math.min(finalTarget.getY(), mark.getY()) - 50.0D;
                maxY = Math.max(finalTarget.getY(), mark.getY()) + 3.0D;
                yaw = (float) Math.atan2(finalTarget.getZ() - mark.getZ(), finalTarget.getX() - mark.getX());
            }
            for (int i = 0; i < KEY_PORTAL_COUNT; ++i) {
                double x = view.x + mark.level().getRandom().nextDouble() * PORTAL_SPREAD;
                double z = view.z + mark.level().getRandom().nextDouble() * PORTAL_SPREAD;
                if (finalTarget != null) {
                    x = finalTarget.getX() + mark.level().getRandom().nextDouble() * PORTAL_SPREAD;
                    z = finalTarget.getZ() + mark.level().getRandom().nextDouble() * PORTAL_SPREAD;
                }
                spawnUnderPortalMethod.invoke(mark, hitEntity, x, z, minY, maxY, yaw,
                        (int) (PORTAL_DELAY_STEP * (float) i), potency);
            }
        } catch (Throwable ignored) {
        }
    }

    private static LivingEntity portalCaster(Entity portal) {
        try {
            Object caster = portalCasterMethod.invoke(portal);
            return caster instanceof LivingEntity living ? living : null;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
