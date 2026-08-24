/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity
 *  com.Polarice3.Goety.common.crafting.RitualRecipe
 *  com.Polarice3.Goety.common.items.magic.TaglockKit
 *  com.Polarice3.Goety.common.ritual.Ritual
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.Polarice3.Goety.common.crafting.RitualRecipe;
import com.Polarice3.Goety.common.items.magic.TaglockKit;
import com.Polarice3.Goety.common.ritual.Ritual;
import com.vivideru.masteryofmagic.MasteryData;
import com.vivideru.masteryofmagic.PlanetSupremeRitualRequirements;
import com.vivideru.masteryofmagic.SchoolSupremeRitualRequirements;
import com.vivideru.masteryofmagic.SupremeRitualRequirements;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import com.vivideru.masteryofmagic.item.UndeadBloodVialItem;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Ritual.class})
public abstract class SupremeMasteryRitualMixin {
    @Inject(method={"isValid"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    private void gmom$validateSupremeMastery(Level world, BlockPos altar, DarkAltarBlockEntity tile, Player player, ItemStack activation, List<Ingredient> ingredients, CallbackInfoReturnable<Boolean> cir) {
        Component failure;
        if (!((Boolean)cir.getReturnValue()).booleanValue() || !(world instanceof ServerLevel)) {
            return;
        }
        ServerLevel server = (ServerLevel)world;
        RitualRecipe recipe = ((Ritual)this).getRecipe();
        ItemStack result = recipe.m_8043_(world.m_9598_());
        if (result.m_150930_((Item)GoetyMasteryOfMagicModItems.THERIANTHROPY_RITUAL_TOKEN.get())) {
            boolean validTaglock = activation.m_41720_() instanceof TaglockKit && UndeadBloodVialItem.hasTherianthropicSelfBlood(activation);
            boolean validVial = activation.m_150930_((Item)GoetyMasteryOfMagicModItems.UNDEAD_BLOOD_VIAL.get());
            if (!validTaglock && !validVial || !UndeadBloodVialItem.hasAnimalShape(activation)) {
                cir.setReturnValue((Object)false);
                SupremeMasteryRitualMixin.gmom$showRequirement(server, player, (Component)Component.m_237115_((String)"message.goety_mastery_of_magic.planet_shape.blood_no_shape"));
            }
            return;
        }
        int target = SupremeMasteryRitualMixin.gmom$targetLevel(result);
        SchoolSupremeRitualRequirements.RitualKind kind = SupremeMasteryRitualMixin.gmom$schoolKind(result);
        PlanetSupremeRitualRequirements.Kind planet = SupremeMasteryRitualMixin.gmom$planetKind(result);
        if (target == 0 && kind == null && planet == null) {
            return;
        }
        Component component = planet != null ? PlanetSupremeRitualRequirements.validate(server, altar, player, planet) : (failure = kind == null ? SupremeRitualRequirements.validate(server, altar, player, target) : SchoolSupremeRitualRequirements.validate(server, altar, player, kind));
        if (failure != null) {
            cir.setReturnValue((Object)false);
            SupremeMasteryRitualMixin.gmom$showRequirement(server, player, failure);
        }
    }

    @Inject(method={"identify"}, at={@At(value="RETURN")}, remap=false)
    private void gmom$explainMissingPedestalItems(Level world, BlockPos altar, Player player, ItemStack activation, CallbackInfoReturnable<Boolean> cir) {
        if (((Boolean)cir.getReturnValue()).booleanValue() || !(world instanceof ServerLevel)) {
            return;
        }
        ServerLevel server = (ServerLevel)world;
        Ritual ritual = (Ritual)this;
        RitualRecipe recipe = ritual.getRecipe();
        int target = SupremeMasteryRitualMixin.gmom$targetLevel(recipe.m_8043_(world.m_9598_()));
        SchoolSupremeRitualRequirements.RitualKind kind = SupremeMasteryRitualMixin.gmom$schoolKind(recipe.m_8043_(world.m_9598_()));
        PlanetSupremeRitualRequirements.Kind planet = SupremeMasteryRitualMixin.gmom$planetKind(recipe.m_8043_(world.m_9598_()));
        if (target == 0 && kind == null && planet == null || !recipe.getActivationItem().test(activation)) {
            return;
        }
        if (kind == null && planet == null && target > 0 && MasteryData.getWizardry(player) != target - 1) {
            return;
        }
        Component failure = SupremeMasteryRitualMixin.gmom$missingPedestalItems(ritual, world, altar, (List<Ingredient>)recipe.m_7527_());
        if (failure != null) {
            SupremeMasteryRitualMixin.gmom$showRequirement(server, player, failure);
        }
    }

    @Unique
    private static Component gmom$missingPedestalItems(Ritual ritual, Level world, BlockPos altar, List<Ingredient> ingredients) {
        ArrayList available = new ArrayList(ritual.getItemsOnPedestals(world, altar));
        LinkedHashMap<String, Integer> missing = new LinkedHashMap<String, Integer>();
        for (Ingredient ingredient : ingredients) {
            int match = -1;
            for (int i = 0; i < available.size(); ++i) {
                if (!ingredient.test((ItemStack)available.get(i))) continue;
                match = i;
                break;
            }
            if (match >= 0) {
                available.remove(match);
                continue;
            }
            ItemStack[] choices = ingredient.m_43908_();
            String name2 = choices.length == 0 ? "?" : choices[0].m_41786_().getString();
            missing.merge(name2, 1, Integer::sum);
        }
        if (!missing.isEmpty()) {
            ArrayList parts = new ArrayList();
            missing.forEach((name, count) -> parts.add(count + "x " + name));
            return Component.m_237110_((String)"message.goety_mastery_of_magic.supreme_ritual.missing_ingredients", (Object[])new Object[]{String.join((CharSequence)", ", parts)});
        }
        if (!available.isEmpty()) {
            return Component.m_237110_((String)"message.goety_mastery_of_magic.supreme_ritual.extra_ingredients", (Object[])new Object[]{available.size()});
        }
        return null;
    }

    @Unique
    private static void gmom$showRequirement(ServerLevel server, Player player, Component failure) {
        long now = server.m_46467_();
        if (player.getPersistentData().m_128454_("gmomSupremeRitualMessage") > now) {
            return;
        }
        player.getPersistentData().m_128356_("gmomSupremeRitualMessage", now + 10L);
        server.m_7654_().execute(() -> {
            if (!player.m_213877_()) {
                player.m_5661_(failure, true);
            }
        });
    }

    @Unique
    private static int gmom$targetLevel(ItemStack result) {
        if (result.m_150930_((Item)GoetyMasteryOfMagicModItems.WIZARDRY_MASTERY_SCROLL_I.get())) {
            return 1;
        }
        if (result.m_150930_((Item)GoetyMasteryOfMagicModItems.WIZARDRY_MASTERY_SCROLL_II.get())) {
            return 2;
        }
        if (result.m_150930_((Item)GoetyMasteryOfMagicModItems.WIZARDRY_MASTERY_SCROLL_III.get())) {
            return 3;
        }
        return 0;
    }

    @Unique
    private static SchoolSupremeRitualRequirements.RitualKind gmom$schoolKind(ItemStack result) {
        if (result.m_150930_((Item)GoetyMasteryOfMagicModItems.SUPREME_NETHER_MASTERY_TOKEN.get())) {
            return SchoolSupremeRitualRequirements.RitualKind.NETHER;
        }
        if (result.m_150930_((Item)GoetyMasteryOfMagicModItems.SUPREME_SKIES_ATTUNEMENT_TOKEN.get())) {
            return SchoolSupremeRitualRequirements.RitualKind.SKIES_FIRST;
        }
        if (result.m_150930_((Item)GoetyMasteryOfMagicModItems.SUPREME_SKIES_MASTERY_TOKEN.get())) {
            return SchoolSupremeRitualRequirements.RitualKind.SKIES_SECOND;
        }
        return null;
    }

    @Unique
    private static PlanetSupremeRitualRequirements.Kind gmom$planetKind(ItemStack result) {
        if (result.m_150930_((Item)GoetyMasteryOfMagicModItems.SUPREME_PLANET_WILD_TOKEN.get())) {
            return PlanetSupremeRitualRequirements.Kind.WILD;
        }
        if (result.m_150930_((Item)GoetyMasteryOfMagicModItems.SUPREME_PLANET_GEOMANCY_TOKEN.get())) {
            return PlanetSupremeRitualRequirements.Kind.GEOMANCY;
        }
        if (result.m_150930_((Item)GoetyMasteryOfMagicModItems.SUPREME_PLANET_DEEP_TOKEN.get())) {
            return PlanetSupremeRitualRequirements.Kind.DEEP;
        }
        return null;
    }
}

