package com.qiuyue.goetyominous.common.magic.spells.mm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.alexander.mutantmore.init.SoundEventInit;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServantTrap;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class ShulkerScatterSpell extends Spell {
    private static final int SCATTER_WINDOW = 20;

    @Override
    public int defaultSoulCost() {
        return com.qiuyue.goetyominous.config.SpellConfig.ShulkerScatterSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return com.qiuyue.goetyominous.config.SpellConfig.ShulkerScatterCastDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return com.qiuyue.goetyominous.config.SpellConfig.ShulkerScatterCooldown.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return (SoundEvent) ModSounds.VOID_PREPARE_SPELL.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.VOID;
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
        float velocity = spellStat.getVelocity();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        }
        boolean usingRightStaff = this.rightStaff(staff);

        int scatterTicks = usingRightStaff ? SCATTER_WINDOW + SCATTER_WINDOW / 2 : SCATTER_WINDOW;

        for (int i = 0; i < scatterTicks; ++i) {
            if (caster.getRandom().nextBoolean()) {
                MutantShulkerServantTrap trap = new MutantShulkerServantTrap(MmEntityRegistry.MUTANT_SHULKER_SERVANT_TRAP.get(), worldIn);
                if (caster.getTeam() != null) {
                    worldIn.getScoreboard().addPlayerToTeam(trap.getScoreboardName(),
                            worldIn.getScoreboard().getPlayerTeam(caster.getTeam().getName()));
                }
                trap.setOwnerUUID(caster.getUUID());
                trap.moveTo(caster.getX(), caster.getEyeY(), caster.getZ());
                trap.setDeltaMovement(caster.getRandom().nextGaussian() * 0.8, 0.6, caster.getRandom().nextGaussian() * 0.8);
                trap.setSpawnedByMutantShulker(true);
                trap.bonusDamage = potency;
                trap.voidTouchedHit = usingRightStaff;
                trap.bulletTurnTimeReduction = Math.min((int) velocity * 20, 100);
                trap.bulletTrackSpeed = 1.25F + velocity * 0.2F;
                worldIn.addFreshEntity(trap);
            }
        }
        this.playSound(worldIn, caster, SoundEventInit.MUTANT_SHULKER_SHOOT_TRAP.get(), 2.0F, 1.0F);
    }
}
