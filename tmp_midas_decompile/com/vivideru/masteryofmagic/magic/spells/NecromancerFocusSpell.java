/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.magic.SpellType
 *  com.Polarice3.Goety.common.enchantments.ModEnchantments
 *  com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer
 *  com.Polarice3.Goety.common.items.ModItems
 *  com.Polarice3.Goety.common.magic.SpellStat
 *  com.Polarice3.Goety.common.magic.SummonSpell
 *  com.Polarice3.Goety.init.ModSounds
 *  com.Polarice3.Goety.utils.BlockFinder
 *  com.Polarice3.Goety.utils.MobUtil
 *  com.Polarice3.Goety.utils.WandUtil
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 */
package com.vivideru.masteryofmagic.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.vivideru.masteryofmagic.MasteryData;
import com.vivideru.masteryofmagic.config.SpellConfig;
import com.vivideru.masteryofmagic.entity.necromancer.JarlessCairnNecromancerServant;
import com.vivideru.masteryofmagic.entity.necromancer.JarlessDrownedNecromancerServant;
import com.vivideru.masteryofmagic.entity.necromancer.JarlessMossyNecromancerServant;
import com.vivideru.masteryofmagic.entity.necromancer.JarlessNecromancerServant;
import com.vivideru.masteryofmagic.entity.necromancer.JarlessWitherNecromancerServant;
import com.vivideru.masteryofmagic.entity.necromancer.NamelessNecromancerServant;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class NecromancerFocusSpell
extends SummonSpell {
    public int defaultSoulCost() {
        return (Integer)SpellConfig.NECROMANCER_FOCUS_SOUL_COST.get();
    }

    public int defaultCastDuration() {
        return (Integer)SpellConfig.NECROMANCER_FOCUS_CAST_TIME.get();
    }

    public int SummonDownDuration() {
        return (Integer)SpellConfig.NECROMANCER_FOCUS_SUMMON_DOWN.get();
    }

    public int defaultSpellCooldown() {
        return (Integer)SpellConfig.NECROMANCER_FOCUS_COOLDOWN.get();
    }

    public int summonLimit() {
        return (Integer)SpellConfig.NECROMANCER_FOCUS_LIMIT.get();
    }

    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
    }

    public List<Enchantment> acceptedEnchantments() {
        return List.of((Enchantment)ModEnchantments.POTENCY.get(), (Enchantment)ModEnchantments.DURATION.get());
    }

    public Predicate<LivingEntity> summonPredicate() {
        return entity -> entity instanceof JarlessNecromancerServant || entity instanceof JarlessWitherNecromancerServant || entity instanceof JarlessCairnNecromancerServant || entity instanceof JarlessMossyNecromancerServant || entity instanceof JarlessDrownedNecromancerServant || entity instanceof NamelessNecromancerServant;
    }

    public void SpellResult(ServerLevel level, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        EntityType<? extends AbstractNecromancer> type;
        AbstractNecromancer necromancer;
        this.commonResult(level, caster);
        if (this.isShifting(caster)) {
            return;
        }
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus((LivingEntity)caster)) {
            potency += WandUtil.getPotencyLevel((LivingEntity)caster);
            duration += WandUtil.getLevels((Enchantment)((Enchantment)ModEnchantments.DURATION.get()), (LivingEntity)caster);
        }
        if ((necromancer = (AbstractNecromancer)(type = NecromancerFocusSpell.selectNecromancer(caster, staff)).m_20615_((Level)level)) == null) {
            return;
        }
        BlockPos position = BlockFinder.SummonRadius((BlockPos)caster.m_20183_(), (Entity)necromancer, (Level)level);
        if (necromancer instanceof JarlessDrownedNecromancerServant && caster.m_5842_()) {
            position = BlockFinder.SummonWaterRadius((LivingEntity)caster, (Level)level);
        }
        if (necromancer instanceof NamelessNecromancerServant) {
            position = NecromancerFocusSpell.findFlyingSpawn(level, caster, position);
        }
        necromancer.setTrueOwner(caster);
        necromancer.m_20035_(position, caster.m_146908_(), 0.0f);
        if (!(necromancer instanceof NamelessNecromancerServant)) {
            MobUtil.moveDownToGround((Entity)necromancer);
        }
        necromancer.m_6518_((ServerLevelAccessor)level, level.m_6436_(position), MobSpawnType.MOB_SUMMONED, null, null);
        necromancer.setNecroLevel(Mth.m_14045_((int)(potency / 2), (int)0, (int)2));
        necromancer.m_21153_(necromancer.m_21233_());
        necromancer.setLimitedLife(1200 * (1 + Math.max(0, duration)));
        necromancer.m_21530_();
        this.buffSummon(caster, (LivingEntity)necromancer, potency);
        this.SummonSap(caster, (LivingEntity)necromancer);
        this.setTarget(caster, (Mob)necromancer);
        if (level.m_7967_((Entity)necromancer)) {
            this.uponSummon(level, caster, staff, (LivingEntity)necromancer);
        }
        this.summonAdvancement(caster, (LivingEntity)necromancer);
        this.SummonDown(caster);
        this.playSound(level, (Entity)caster, (SoundEvent)ModSounds.SUMMON_SPELL.get());
    }

    private static BlockPos findFlyingSpawn(ServerLevel level, LivingEntity caster, BlockPos horizontalPosition) {
        int minimumY = Math.max(caster.m_20183_().m_123342_(), horizontalPosition.m_123342_());
        for (int offset = 0; offset <= 8; ++offset) {
            BlockPos candidate = new BlockPos(horizontalPosition.m_123341_(), minimumY + offset, horizontalPosition.m_123343_());
            boolean clear = true;
            for (int height = 0; height < 3; ++height) {
                BlockPos occupied = candidate.m_6630_(height);
                if (level.m_8055_(occupied).m_60812_((BlockGetter)level, occupied).m_83281_()) continue;
                clear = false;
                break;
            }
            if (!clear) continue;
            return candidate;
        }
        return new BlockPos(horizontalPosition.m_123341_(), minimumY + 9, horizontalPosition.m_123343_());
    }

    private static EntityType<? extends AbstractNecromancer> selectNecromancer(LivingEntity caster, ItemStack staff) {
        Player player;
        if (staff.m_150930_((Item)ModItems.NAMELESS_STAFF.get()) && caster instanceof Player && MasteryData.get(player = (Player)caster, MasteryData.MasteryId.NECROMANCY) >= 3) {
            return (EntityType)GoetyMasteryOfMagicModEntities.NAMELESS_NECROMANCER.get();
        }
        if (staff.m_150930_((Item)ModItems.NETHER_STAFF.get())) {
            return (EntityType)GoetyMasteryOfMagicModEntities.JARLESS_WITHER_NECROMANCER.get();
        }
        if (staff.m_150930_((Item)ModItems.FROST_STAFF.get())) {
            return (EntityType)GoetyMasteryOfMagicModEntities.JARLESS_CAIRN_NECROMANCER.get();
        }
        if (staff.m_150930_((Item)ModItems.WILD_STAFF.get()) || staff.m_150930_((Item)ModItems.GEO_STAFF.get())) {
            return (EntityType)GoetyMasteryOfMagicModEntities.JARLESS_MOSSY_NECROMANCER.get();
        }
        if (staff.m_150930_((Item)ModItems.ABYSS_STAFF.get())) {
            return (EntityType)GoetyMasteryOfMagicModEntities.JARLESS_DROWNED_NECROMANCER.get();
        }
        return (EntityType)GoetyMasteryOfMagicModEntities.JARLESS_NECROMANCER.get();
    }
}

