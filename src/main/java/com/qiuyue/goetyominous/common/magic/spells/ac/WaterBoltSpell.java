package com.qiuyue.goetyominous.common.magic.spells.ac;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.github.alexmodguy.alexscaves.server.item.SeaStaffItem;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.entities.projectile.WaterBoltProjectile;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class WaterBoltSpell extends Spell {

    private static final float BOLT_VELOCITY = 1.5F;
    private static final float VELOCITY_PER_LEVEL = 0.1F;
    private static final float SPREAD_ANGLE = 8.0F;
    private static final float INACCURACY = 1.0F;
    private static final float PITCH_OFFSET = 0.0F;
    private static final double TARGET_RANGE = 128.0D;

    @Override
    public int defaultSoulCost() {
        return SpellConfig.WaterBoltCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return 0;
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.WaterBoltCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ACSoundRegistry.SEA_STAFF_CAST.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.VELOCITY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
        }

        boolean straight = caster.isShiftKeyDown();
        Entity arcTarget = null;
        if (!straight && caster instanceof Player player) {
            arcTarget = SeaStaffItem.getClosestLookingAtEntityFor(worldIn, player, TARGET_RANGE);
        }

        int count = this.rightStaff(staff) ? 3 : 1;

        for (int i = 0; i < count; ++i) {
            float spread = 0.0F;
            if (count == 3) {
                spread = (i == 1) ? 0.0F : (i == 0 ? -SPREAD_ANGLE : SPREAD_ANGLE);
            }

            WaterBoltProjectile bolt = new WaterBoltProjectile(worldIn, caster);
            bolt.setDamageBonus((float) potency);

            if (arcTarget != null) {
                bolt.setArcingTowards(arcTarget.getUUID());
            }

            float velocity = BOLT_VELOCITY
                    + WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster) * VELOCITY_PER_LEVEL;

            bolt.shootFromRotation(caster, caster.getXRot() - PITCH_OFFSET, caster.getYRot() + spread,
                    0.0F, velocity, INACCURACY);

            bolt.setBubbling(worldIn.random.nextInt(2) == 0);
            worldIn.addFreshEntity(bolt);
        }

        this.playSound(worldIn, caster, ACSoundRegistry.SEA_STAFF_WOOSH.get());
    }
}
