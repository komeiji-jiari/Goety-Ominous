/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$PlayerTickEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.LogicalSide
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.registries.ForgeRegistries
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.MasteryData;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class MasteryAttributeApplier {
    private static final UUID NECRO_ID = UUID.fromString("c1f82424-323a-4a89-bd8b-7e5c2d497f28");
    private static final UUID GEO_ID = UUID.fromString("b4b9dcec-8c29-4f27-b1de-e3aab93b5db4");
    private static final UUID SKY_ID = UUID.fromString("a1e871fb-36da-47ba-b389-9c44f0484c39");
    private static final UUID NETHER_ID = UUID.fromString("e9f05d10-0f77-4c5a-ad5b-e566d774e41a");
    private static final UUID END_ID = UUID.fromString("f90a4d7d-432e-4b9f-8c41-33c1d58c534a");
    private static final UUID WILD_ID = UUID.fromString("d661896f-6f53-4af9-848d-1cf6fd0a7484");
    private static final UUID DEEP_ID = UUID.fromString("02f27643-2fd3-4674-ad35-44b7112f07e2");
    private static final UUID FROST_ID = UUID.fromString("ab77590a-b1e2-4a49-94b2-74049c0e9b05");
    private static final UUID STORM_ID = UUID.fromString("c9870958-f4bd-4488-b6a7-83c7393e1db9");
    private static final UUID WIZARD_CAST_ID = UUID.fromString("2c19bd1c-215d-4687-aa21-0406972e7e8e");
    private static final UUID WIZARD_DURATION_ID = UUID.fromString("25cba47a-ea89-4e33-875b-bc0ca78a520d");
    private static final UUID WIZARD_RANGE_ID = UUID.fromString("a908bb69-dacb-4295-9d4b-a64758c768c0");
    private static final UUID WIZARD_RADIUS_ID = UUID.fromString("de103132-8710-45b1-9e56-6a8f82a333c7");
    private static final UUID WIZARD_SOUL_ID = UUID.fromString("890fa1a2-fcf6-405c-8360-7bcf32b472fd");

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side != LogicalSide.SERVER) {
            return;
        }
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Player player = event.player;
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        MasteryAttributeApplier.applyAll(player2);
    }

    private static void applyAll(ServerPlayer p) {
        int necro = MasteryData.get((Player)p, MasteryData.MasteryId.NECROMANCY);
        int geot = MasteryData.get((Player)p, MasteryData.MasteryId.GEOTURGY);
        int sky = MasteryData.get((Player)p, MasteryData.MasteryId.SKY);
        int nether = MasteryData.get((Player)p, MasteryData.MasteryId.NETHER);
        int end = MasteryData.get((Player)p, MasteryData.MasteryId.END);
        int wild = MasteryData.get((Player)p, MasteryData.MasteryId.WILD);
        int deep = MasteryData.get((Player)p, MasteryData.MasteryId.DEEP);
        int frost = MasteryData.get((Player)p, MasteryData.MasteryId.FROST);
        int storm = MasteryData.get((Player)p, MasteryData.MasteryId.STORM);
        MasteryAttributeApplier.apply(p, "necromancy_potency", NECRO_ID, necro);
        MasteryAttributeApplier.apply(p, "geomancy_potency", GEO_ID, geot);
        MasteryAttributeApplier.apply(p, "wind_potency", SKY_ID, sky);
        MasteryAttributeApplier.apply(p, "nether_potency", NETHER_ID, nether);
        MasteryAttributeApplier.apply(p, "void_potency", END_ID, end);
        MasteryAttributeApplier.apply(p, "wild_potency", WILD_ID, wild);
        MasteryAttributeApplier.apply(p, "abyss_potency", DEEP_ID, deep);
        MasteryAttributeApplier.apply(p, "frost_potency", FROST_ID, frost);
        MasteryAttributeApplier.apply(p, "storm_potency", STORM_ID, storm);
        int wizardry = MasteryData.getWizardry((Player)p);
        MasteryAttributeApplier.applyAmount(p, "casting_speed", WIZARD_CAST_ID, wizardry >= 1 ? 0.2 : 0.0);
        MasteryAttributeApplier.applyAmount(p, "spell_duration", WIZARD_DURATION_ID, wizardry >= 2 ? 1.0 : 0.0);
        MasteryAttributeApplier.applyAmount(p, "spell_range", WIZARD_RANGE_ID, wizardry >= 2 ? 1.0 : 0.0);
        MasteryAttributeApplier.applyAmount(p, "spell_radius", WIZARD_RADIUS_ID, wizardry >= 2 ? 1.0 : 0.0);
        MasteryAttributeApplier.applyAmount(p, "soul_discount", WIZARD_SOUL_ID, 0.0);
    }

    private static void applyAmount(ServerPlayer player, String attributeName, UUID id, double amount) {
        AttributeInstance instance = MasteryAttributeApplier.getInstance(player, attributeName);
        if (instance == null) {
            return;
        }
        instance.m_22120_(id);
        if (amount > 0.0) {
            instance.m_22125_(new AttributeModifier(id, "supreme_wizardry_" + attributeName, amount, AttributeModifier.Operation.ADDITION));
        }
    }

    private static void apply(ServerPlayer p, String attributeName, UUID id, int level) {
        if (level <= 0) {
            MasteryAttributeApplier.removeIfPresent(p, attributeName, id);
            return;
        }
        AttributeInstance inst = MasteryAttributeApplier.getInstance(p, attributeName);
        if (inst == null) {
            return;
        }
        inst.m_22120_(id);
        AttributeModifier mod = new AttributeModifier(id, "mastery_" + attributeName, (double)level, AttributeModifier.Operation.ADDITION);
        inst.m_22125_(mod);
    }

    private static void removeIfPresent(ServerPlayer p, String attributeName, UUID id) {
        AttributeInstance inst = MasteryAttributeApplier.getInstance(p, attributeName);
        if (inst != null) {
            inst.m_22120_(id);
        }
    }

    private static AttributeInstance getInstance(ServerPlayer p, String attributeName) {
        Attribute attr = (Attribute)ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("goety", attributeName));
        if (attr == null) {
            return null;
        }
        return p.m_21051_(attr);
    }
}

