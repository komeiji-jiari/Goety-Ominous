package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.common.events.spell.CastMagicEvent;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.items.curios.DarkAnkh;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DarkAnkhSpellHandler {

    @SubscribeEvent
    public static void onCastSpell(CastMagicEvent event) {
        if (event.getEntity() instanceof Player player) {
            ISpell spell = event.getSpell();
            if (spell != null) {
                DarkAnkh.triggerServantBuffOnSpellCast(player, spell, player.getUseItem());
            }
        }
    }
}