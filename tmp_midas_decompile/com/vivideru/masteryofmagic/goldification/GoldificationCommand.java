/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.phys.Vec3
 */
package com.vivideru.masteryofmagic.goldification;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.vivideru.masteryofmagic.config.GameplayConfig;
import com.vivideru.masteryofmagic.goldification.GoldificationManager;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class GoldificationCommand {
    private static final Pattern DURATION = Pattern.compile("^(\\d+)([tsmhd]?)$", 2);

    private GoldificationCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"goldify").requires(source -> source.m_6761_(2))).then(((LiteralArgumentBuilder)Commands.m_82127_((String)"clear").then(Commands.m_82127_((String)"all").executes(context -> GoldificationCommand.clearAll((CommandSourceStack)context.getSource())))).then(Commands.m_82129_((String)"radius", (ArgumentType)DoubleArgumentType.doubleArg((double)0.5)).executes(context -> GoldificationCommand.clearArea((CommandSourceStack)context.getSource(), DoubleArgumentType.getDouble((CommandContext)context, (String)"radius")))))).then(Commands.m_82129_((String)"radius", (ArgumentType)DoubleArgumentType.doubleArg((double)0.5)).then(Commands.m_82129_((String)"duration", (ArgumentType)StringArgumentType.word()).executes(context -> GoldificationCommand.execute((CommandSourceStack)context.getSource(), DoubleArgumentType.getDouble((CommandContext)context, (String)"radius"), StringArgumentType.getString((CommandContext)context, (String)"duration"))))));
    }

    private static int execute(CommandSourceStack source, double radius, String durationText) {
        long durationTicks;
        double maxRadius = (Double)GameplayConfig.GOLDIFICATION_MAX_COMMAND_RADIUS.get();
        if (radius > maxRadius) {
            source.m_81352_((Component)Component.m_237113_((String)("Radius exceeds the configured maximum of " + maxRadius + ".")));
            return 0;
        }
        try {
            durationTicks = GoldificationCommand.parseDuration(durationText);
        }
        catch (IllegalArgumentException exception) {
            source.m_81352_((Component)Component.m_237113_((String)exception.getMessage()));
            return 0;
        }
        ServerLevel level = source.m_81372_();
        Vec3 center = source.m_81371_();
        Entity sourceEntity = source.m_81373_();
        GoldificationManager.goldifyArea(level, center, radius, durationTicks, sourceEntity, result -> {
            String suffix = result.truncated() ? " (block limit reached)" : "";
            source.m_288197_(() -> Component.m_237113_((String)("Goldified " + result.goldifiedBlocks() + " blocks and " + result.goldifiedEntities() + " entities" + suffix + ".")), true);
        });
        source.m_288197_(() -> Component.m_237113_((String)("Goldification job started: radius " + radius + ", duration " + durationTicks + " ticks.")), false);
        return 1;
    }

    private static int clearArea(CommandSourceStack source, double radius) {
        double maxRadius = (Double)GameplayConfig.GOLDIFICATION_MAX_COMMAND_RADIUS.get();
        if (radius > maxRadius) {
            source.m_81352_((Component)Component.m_237113_((String)("Radius exceeds the configured maximum of " + maxRadius + ".")));
            return 0;
        }
        GoldificationManager.ClearResult result = GoldificationManager.clearArea(source.m_81372_(), source.m_81371_(), radius);
        source.m_288197_(() -> Component.m_237113_((String)("Removed goldification from " + result.blocks() + " blocks and " + result.entities() + " entities.")), true);
        return 1;
    }

    private static int clearAll(CommandSourceStack source) {
        GoldificationManager.ClearResult result = GoldificationManager.clearAll(source.m_81372_());
        source.m_288197_(() -> Component.m_237113_((String)("Removed all loaded goldification: " + result.blocks() + " blocks and " + result.entities() + " entities.")), true);
        return 1;
    }

    static long parseDuration(String input) {
        long value;
        Matcher matcher = DURATION.matcher(input.toLowerCase(Locale.ROOT));
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid duration. Use ticks, or a suffix: 10s, 2m, 1h.");
        }
        try {
            value = Long.parseLong(matcher.group(1));
        }
        catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Duration is too large.");
        }
        long multiplier = switch (matcher.group(2)) {
            case "s" -> 20L;
            case "m" -> 1200L;
            case "h" -> 72000L;
            case "d" -> 1728000L;
            default -> 1L;
        };
        try {
            long ticks = Math.multiplyExact(value, multiplier);
            if (ticks <= 0L) {
                throw new IllegalArgumentException("Duration must be positive.");
            }
            return ticks;
        }
        catch (ArithmeticException exception) {
            throw new IllegalArgumentException("Duration is too large.");
        }
    }
}

