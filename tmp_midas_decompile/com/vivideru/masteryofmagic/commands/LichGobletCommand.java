/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.EntityArgument
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.vivideru.masteryofmagic.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LichGobletCommand {
    private static final String FLIGHT = "goblet_flight_unlocked";
    private static final String BATS = "goblet_bats_unlocked";
    private static final String NOFREEZE = "goblet_nofreeze_unlocked";
    private static final String SOUL_ENERGY = "goblet_soul_energy_unlocked";
    private static final String MOD_DATA_KEY = "goetymasteryofmagic";

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher dispatcher = event.getDispatcher();
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"lichgoblet").requires(source -> source.m_6761_(2))).then(Commands.m_82127_((String)"give").then(Commands.m_82129_((String)"player", (ArgumentType)EntityArgument.m_91466_()).then(Commands.m_82129_((String)"type", (ArgumentType)StringArgumentType.word()).executes(ctx -> LichGobletCommand.setFlag((CommandSourceStack)ctx.getSource(), EntityArgument.m_91474_((CommandContext)ctx, (String)"player"), StringArgumentType.getString((CommandContext)ctx, (String)"type"), true)))))).then(Commands.m_82127_((String)"remove").then(Commands.m_82129_((String)"player", (ArgumentType)EntityArgument.m_91466_()).then(Commands.m_82129_((String)"type", (ArgumentType)StringArgumentType.word()).executes(ctx -> LichGobletCommand.setFlag((CommandSourceStack)ctx.getSource(), EntityArgument.m_91474_((CommandContext)ctx, (String)"player"), StringArgumentType.getString((CommandContext)ctx, (String)"type"), false)))))).then(Commands.m_82127_((String)"check").then(Commands.m_82129_((String)"player", (ArgumentType)EntityArgument.m_91466_()).then(Commands.m_82129_((String)"type", (ArgumentType)StringArgumentType.word()).executes(ctx -> LichGobletCommand.checkFlag((CommandSourceStack)ctx.getSource(), EntityArgument.m_91474_((CommandContext)ctx, (String)"player"), StringArgumentType.getString((CommandContext)ctx, (String)"type")))))));
    }

    private static int setFlag(CommandSourceStack source, ServerPlayer player, String type, boolean value) {
        String key = LichGobletCommand.getKey(type);
        if (key == null) {
            source.m_81352_((Component)Component.m_237113_((String)"Invalid goblet type."));
            return 0;
        }
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag modData = persistentData.m_128469_(MOD_DATA_KEY);
        modData.m_128379_(key, value);
        persistentData.m_128365_(MOD_DATA_KEY, (Tag)modData);
        source.m_288197_(() -> Component.m_237113_((String)("Set " + type + " to " + value + " for " + player.m_7755_().getString())), true);
        return 1;
    }

    private static int checkFlag(CommandSourceStack source, ServerPlayer player, String type) {
        String key = LichGobletCommand.getKey(type);
        if (key == null) {
            source.m_81352_((Component)Component.m_237113_((String)"Invalid goblet type."));
            return 0;
        }
        CompoundTag modData = player.getPersistentData().m_128469_(MOD_DATA_KEY);
        boolean value = modData.m_128471_(key);
        source.m_288197_(() -> Component.m_237113_((String)(type + " = " + value)), false);
        return 1;
    }

    private static String getKey(String type) {
        if (type.equalsIgnoreCase("flight")) {
            return FLIGHT;
        }
        if (type.equalsIgnoreCase("bats")) {
            return BATS;
        }
        if (type.equalsIgnoreCase("nofreeze")) {
            return NOFREEZE;
        }
        if (type.equalsIgnoreCase("soulenergy") || type.equalsIgnoreCase("soul_energy")) {
            return SOUL_ENERGY;
        }
        return null;
    }
}

