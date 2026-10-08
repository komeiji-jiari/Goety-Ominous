package com.qiuyue.goetyominous.common.events.ac;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.common.events.spell.CastMagicEvent;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.SEHelper;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.compat.mod.AlexCavesCompat;
import com.qiuyue.goetyominous.compat.mod.GoetyCataclysmCompat;
import com.qiuyue.goetyominous.utils.ac.KeyOfRlyehMixinHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class CataclysmKeySpellHandler {

    private static final List<Entity> PENDING_BEAM_MARKS = new ArrayList<>();
    private static final double MARK_MATCH_DISTANCE_SQR = 1.0D;

    @SubscribeEvent
    public static void onCastMagic(CastMagicEvent event) {
        if (!GoetyCataclysmCompat.isLoaded() || event.getEntity().level().isClientSide) {
            return;
        }
        LivingEntity caster = event.getEntity();
        ISpell spell = event.getSpell();
        if (spell == null || !isKeyCast(caster, spell)) {
            return;
        }
        ItemStack staff = WandUtil.findWand(caster);
        if (caster instanceof Player player && !player.isCreative()) {
            ItemStack focus = IWand.getFocus(staff);
            if (!SEHelper.getSoulsAmount(player, WandUtil.getSoulUse(player, focus, spell.soulCost(player, staff)))) {
                return;
            }
        }
        SpellStat spellStat = WandUtil.getStats(caster, spell);
        GoetyCataclysmCompat.addKeyOrbs(spell, caster, spellStat);
        GoetyCataclysmCompat.addKeyMines(spell, caster, spellStat);
        GoetyCataclysmCompat.addKeySpears(spell, caster, spellStat);
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!GoetyCataclysmCompat.isLoaded() || event.getLevel().isClientSide) {
            return;
        }
        Entity entity = event.getEntity();
        if (GoetyCataclysmCompat.isAbyssMark(entity)) {
            onMarkJoin(entity);
        } else if (!PENDING_BEAM_MARKS.isEmpty() && GoetyCataclysmCompat.isAbyssBlastPortal(entity)) {
            onPortalJoin(entity);
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!PENDING_BEAM_MARKS.isEmpty()) {
            PENDING_BEAM_MARKS.removeIf(pending -> pending.level() == event.getLevel());
        }
    }

    private static void onMarkJoin(Entity mark) {
        LivingEntity creator = GoetyCataclysmCompat.markCreator(mark);
        if (creator == null || !isHoldingKey(creator)) {
            return;
        }
        PENDING_BEAM_MARKS.removeIf(pending -> pending.isRemoved() || pending.level() != mark.level());
        PENDING_BEAM_MARKS.add(mark);
    }

    private static void onPortalJoin(Entity portal) {
        Entity mark = null;
        for (Entity pending : PENDING_BEAM_MARKS) {
            if (!pending.isRemoved() && pending.distanceToSqr(portal) < MARK_MATCH_DISTANCE_SQR) {
                mark = pending;
                break;
            }
        }
        if (mark == null) {
            return;
        }
        PENDING_BEAM_MARKS.remove(mark);
        GoetyCataclysmCompat.spawnKeyPortals(mark, portal);
    }

    private static boolean isKeyCast(LivingEntity caster, ISpell spell) {
        return isHoldingKey(caster) && WandUtil.getSpell(caster) == spell;
    }

    private static boolean isHoldingKey(LivingEntity caster) {
        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return false;
        }
        return KeyOfRlyehMixinHelper.isKeyOfRlyeh(caster.getMainHandItem())
                || KeyOfRlyehMixinHelper.isKeyOfRlyeh(caster.getOffhandItem());
    }
}
