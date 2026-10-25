package com.qiuyue.goetyominous.common.magic.spells.mm;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import com.alexander.mutantmore.init.MMDamageTypes;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.google.common.collect.Lists;
import com.qiuyue.goetyominous.common.entities.ally.mm.AreaDamage;
import com.qiuyue.goetyominous.compat.mm.MmScreenShake;
import com.qiuyue.goetyominous.config.mm.MmConfig;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SandGeyserSpell extends Spell {

    private static final int BASE_EXTRA_TIME = 25;
    private static final int EXTRA_TIME_PER_DURATION = 20;
    private static final int RIGHT_STAFF_EXTRA_TIME = 100;
    private static final float GEYSER_SIZE = 2.5F;
    private static final float GEYSER_HEIGHT = 12.0F;
    private static final double KNOCKBACK_Y = 2.0D;

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(12);
    }

    @Override
    public int defaultSoulCost() {
        return MmConfig.SandGeyserSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return MmConfig.SandGeyserCastDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return MmConfig.SandGeyserCooldown.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    @Override
    public SoundEvent CastingSound() {
        return SoundEventInit.MUTANT_HUSK_GEYSER_WARN.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        return List.of(ModEnchantments.POTENCY.get(), ModEnchantments.RANGE.get(), ModEnchantments.DURATION.get());
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int range = spellStat.getRange();
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getRangeLevel(caster);
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
        }

        float damage = MmConfig.SandGeyserDamage.get().floatValue() + potency;
        int extraTime = BASE_EXTRA_TIME + duration * EXTRA_TIME_PER_DURATION;
        if (this.rightStaff(staff)) {
            extraTime += RIGHT_STAFF_EXTRA_TIME;
        }
        DamageSource source = caster instanceof Player player
                ? caster.damageSources().playerAttack(player)
                : MMDamageTypes.geyserAttack(caster.damageSources(), caster);

        Predicate<Entity> friendly = this.createFriendlyCheck(caster);
        List<AreaDamage> geysers = Lists.newArrayList();
        if (this.isShifting(caster)) {
            this.spawnUnderEntities(worldIn, caster, range, damage, source, extraTime, geysers, friendly);
        } else {
            this.spawnLine(worldIn, caster, range, damage, source, extraTime, geysers, friendly);
        }
        for (AreaDamage geyser : geysers) {
            geyser.connectedAreaDamages = Lists.newArrayList(geysers);
            geyser.connectedAreaDamages.remove(geyser);
        }

        MmScreenShake.shake(worldIn, 20, 0.15F, caster.blockPosition(), 25);
        this.playSound(worldIn, caster, SoundEventInit.MUTANT_HUSK_GEYSER_VOCAL.get(), 2.5F, 1.0F);
    }

    private void spawnLine(ServerLevel worldIn, LivingEntity caster, int range, float damage, DamageSource source, int extraTime, List<AreaDamage> geysers, Predicate<Entity> friendly) {
        for (int i = 0; i <= range - 2; ++i) {
            double distance = 2.0D + i;
            Vec3 pos = PositionUtils.getOffsetPos(caster, 0.0D, 0.0D, distance, 0.0F, caster.getYRot());
            geysers.add(this.spawnGeyser(worldIn, caster, pos, damage, source, extraTime, i, friendly));
        }
    }

    private void spawnUnderEntities(ServerLevel worldIn, LivingEntity caster, int range, float damage, DamageSource source, int extraTime, List<AreaDamage> geysers, Predicate<Entity> friendly) {
        int i = (int) caster.getX();
        int j = (int) caster.getY();
        int k = (int) caster.getZ();
        AABB area = new AABB(i, j, k, i, j - 4, k).inflate(range);
        Set<BlockPos> used = new HashSet<>();
        for (LivingEntity entity : worldIn.getEntitiesOfClass(LivingEntity.class, area, entity -> entity.isAlive() && !friendly.test(entity))) {
            if (used.add(entity.blockPosition())) {
                geysers.add(this.spawnGeyser(worldIn, caster, entity.position(), damage, source, extraTime, 0, friendly));
            }
        }
    }

    private AreaDamage spawnGeyser(ServerLevel worldIn, LivingEntity caster, Vec3 pos, float damage, DamageSource source, int extraTime, int startDelay, Predicate<Entity> friendly) {
        AreaDamage geyser = AreaDamage.spawnAreaDamage(worldIn, pos, caster, damage, source,
                GEYSER_SIZE, GEYSER_SIZE, 0.0F, GEYSER_HEIGHT,
                extraTime, startDelay,
                true, false, 0.0D, KNOCKBACK_Y, true, false, 0,
                false, null, 2);
        geyser.spellOwned = true;
        geyser.friendlyCheck = friendly;
        PositionUtils.moveToCorrectHeight(geyser, true);
        geyser.setSandType(MiscUtils.sandStringForPos(worldIn, geyser.getOnPos()));
        worldIn.addFreshEntityWithPassengers(geyser);
        return geyser;
    }

    private Predicate<Entity> createFriendlyCheck(LivingEntity caster) {
        LivingEntity root = caster instanceof IOwned owned && owned.getMasterOwner() != null
                ? owned.getMasterOwner()
                : caster;
        return target -> target == root
                || target instanceof IOwned ownedTarget && ownedTarget.getMasterOwner() == root
                || target instanceof OwnableEntity ownable && ownable.getOwner() == root
                || root instanceof Player player && target instanceof LivingEntity living && SEHelper.isAlly(player, living)
                || ServantAllyUtil.areAllied(root, target);
    }
}
