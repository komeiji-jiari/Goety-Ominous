/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.EntityArgument
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.event.RegisterCommandsEvent
 */
package com.vivideru.masteryofmagic;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.vivideru.masteryofmagic.MasteryData;
import com.vivideru.masteryofmagic.SupremeMasteryAdvancementHelper;
import com.vivideru.masteryofmagic.TimeFreezeManager;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;

public class GoetyMasteryCommands {
    private static CompletableFuture<Suggestions> suggestMastery(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        for (MasteryData.MasteryId id : MasteryData.MasteryId.values()) {
            builder.suggest(id.name().toLowerCase());
        }
        builder.suggest("wizardry");
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestSupremeMastery(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        builder.suggest("master_wizard");
        builder.suggest("supreme_wizard");
        builder.suggest("overlord");
        return builder.buildFuture();
    }

    private static int supremeLevel(String name) {
        return switch (name.toLowerCase()) {
            case "master_wizard" -> 1;
            case "supreme_wizard" -> 2;
            case "overlord", "overlord_of_magic" -> 3;
            default -> -1;
        };
    }

    private static String supremeName(int level) {
        return switch (level) {
            case 1 -> "Master Wizard";
            case 2 -> "Supreme Wizard";
            case 3 -> "Overlord of Magic";
            default -> "None";
        };
    }

    private static int grantSupreme(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String name = StringArgumentType.getString(ctx, (String)"supreme_mastery");
        int level = GoetyMasteryCommands.supremeLevel(name);
        if (level < 1) {
            ((CommandSourceStack)ctx.getSource()).m_81352_((Component)Component.m_237113_((String)("Unknown Supreme Mastery: " + name)));
            return 0;
        }
        Collection targets = EntityArgument.m_91477_(ctx, (String)"targets");
        for (ServerPlayer target : targets) {
            MasteryData.setWizardry((Player)target, level);
            for (int advancementLevel = 1; advancementLevel <= level; ++advancementLevel) {
                SupremeMasteryAdvancementHelper.grant((Player)target, advancementLevel);
            }
        }
        ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)("Granted " + GoetyMasteryCommands.supremeName(level) + " to " + targets.size() + " player(s)")), true);
        return targets.size();
    }

    private static CompletableFuture<Suggestions> suggestSchoolSupreme(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        builder.suggest("nether");
        builder.suggest("skies");
        builder.suggest("planet");
        return builder.buildFuture();
    }

    private static MasteryData.SupremeSchool schoolSupreme(String value) {
        return value.equalsIgnoreCase("nether") ? MasteryData.SupremeSchool.NETHER : (value.equalsIgnoreCase("skies") || value.equalsIgnoreCase("sky") ? MasteryData.SupremeSchool.SKIES : (value.equalsIgnoreCase("planet") ? MasteryData.SupremeSchool.PLANET : null));
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"goetymastery").requires(src -> src.m_6761_(2))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"supreme").then(Commands.m_82127_((String)"give").then(Commands.m_82129_((String)"targets", (ArgumentType)EntityArgument.m_91470_()).then(Commands.m_82129_((String)"supreme_mastery", (ArgumentType)StringArgumentType.word()).suggests(GoetyMasteryCommands::suggestSupremeMastery).executes(GoetyMasteryCommands::grantSupreme))))).then(Commands.m_82127_((String)"remove").then(Commands.m_82129_((String)"targets", (ArgumentType)EntityArgument.m_91470_()).executes(ctx -> {
            Collection targets = EntityArgument.m_91477_((CommandContext)ctx, (String)"targets");
            targets.forEach(target -> MasteryData.setWizardry((Player)target, 0));
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)("Removed Supreme Mastery from " + targets.size() + " player(s)")), true);
            return targets.size();
        })))).then(Commands.m_82127_((String)"get").then(Commands.m_82129_((String)"target", (ArgumentType)EntityArgument.m_91466_()).executes(ctx -> {
            ServerPlayer target = EntityArgument.m_91474_((CommandContext)ctx, (String)"target");
            int level = MasteryData.getWizardry((Player)target);
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)(target.m_36316_().getName() + ": " + GoetyMasteryCommands.supremeName(level) + " (" + level + ")")), false);
            return level;
        }))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"school_supreme").then(Commands.m_82127_((String)"give").then(Commands.m_82129_((String)"targets", (ArgumentType)EntityArgument.m_91470_()).then(Commands.m_82129_((String)"school", (ArgumentType)StringArgumentType.word()).suggests(GoetyMasteryCommands::suggestSchoolSupreme).executes(ctx -> {
            MasteryData.SupremeSchool school = GoetyMasteryCommands.schoolSupreme(StringArgumentType.getString((CommandContext)ctx, (String)"school"));
            if (school == null) {
                ((CommandSourceStack)ctx.getSource()).m_81352_((Component)Component.m_237113_((String)"Unknown school Supreme Mastery"));
                return 0;
            }
            Collection targets = EntityArgument.m_91477_((CommandContext)ctx, (String)"targets");
            for (ServerPlayer target : targets) {
                MasteryData.setSupreme((Player)target, school, true);
                SupremeMasteryAdvancementHelper.grantSchool((Player)target, school);
            }
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)("Granted Supreme " + school.name() + " Mastery to " + targets.size() + " player(s)")), true);
            return targets.size();
        }))))).then(Commands.m_82127_((String)"remove").then(Commands.m_82129_((String)"targets", (ArgumentType)EntityArgument.m_91470_()).then(Commands.m_82129_((String)"school", (ArgumentType)StringArgumentType.word()).suggests(GoetyMasteryCommands::suggestSchoolSupreme).executes(ctx -> {
            MasteryData.SupremeSchool school = GoetyMasteryCommands.schoolSupreme(StringArgumentType.getString((CommandContext)ctx, (String)"school"));
            if (school == null) {
                return 0;
            }
            Collection targets = EntityArgument.m_91477_((CommandContext)ctx, (String)"targets");
            targets.forEach(p -> MasteryData.setSupreme((Player)p, school, false));
            return targets.size();
        }))))).then(Commands.m_82127_((String)"get").then(Commands.m_82129_((String)"target", (ArgumentType)EntityArgument.m_91466_()).then(Commands.m_82129_((String)"school", (ArgumentType)StringArgumentType.word()).suggests(GoetyMasteryCommands::suggestSchoolSupreme).executes(ctx -> {
            MasteryData.SupremeSchool school = GoetyMasteryCommands.schoolSupreme(StringArgumentType.getString((CommandContext)ctx, (String)"school"));
            if (school == null) {
                return 0;
            }
            ServerPlayer target = EntityArgument.m_91474_((CommandContext)ctx, (String)"target");
            boolean has = MasteryData.hasSupreme((Player)target, school);
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)(target.m_36316_().getName() + " Supreme " + school.name() + ": " + has)), false);
            return has ? 1 : 0;
        })))))).then(Commands.m_82127_((String)"add").then(Commands.m_82129_((String)"mastery", (ArgumentType)StringArgumentType.word()).suggests(GoetyMasteryCommands::suggestMastery).then(Commands.m_82129_((String)"level", (ArgumentType)IntegerArgumentType.integer((int)0, (int)3)).executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            String masteryName = StringArgumentType.getString((CommandContext)ctx, (String)"mastery");
            int level = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"level");
            if (masteryName.equalsIgnoreCase("wizardry")) {
                MasteryData.setWizardry((Player)player, level);
            } else {
                MasteryData.set((Player)player, MasteryData.MasteryId.valueOf(masteryName.toUpperCase()), level);
            }
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)("Set " + masteryName + " mastery to " + level)), true);
            return 1;
        }))))).then(Commands.m_82127_((String)"clear").then(Commands.m_82129_((String)"mastery", (ArgumentType)StringArgumentType.word()).suggests(GoetyMasteryCommands::suggestMastery).executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            String masteryName = StringArgumentType.getString((CommandContext)ctx, (String)"mastery");
            if (masteryName.equalsIgnoreCase("wizardry")) {
                MasteryData.setWizardry((Player)player, 0);
            } else {
                MasteryData.clear((Player)player, MasteryData.MasteryId.valueOf(masteryName.toUpperCase()));
            }
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)("Cleared " + masteryName + " mastery")), true);
            return 1;
        })))).then(Commands.m_82127_((String)"get").then(Commands.m_82129_((String)"mastery", (ArgumentType)StringArgumentType.word()).suggests(GoetyMasteryCommands::suggestMastery).executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            String masteryName = StringArgumentType.getString((CommandContext)ctx, (String)"mastery");
            int value = masteryName.equalsIgnoreCase("wizardry") ? MasteryData.getWizardry((Player)player) : MasteryData.get((Player)player, MasteryData.MasteryId.valueOf(masteryName.toUpperCase()));
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)(masteryName + " mastery = " + value)), false);
            return 1;
        })))).then(Commands.m_82127_((String)"reset_all").executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            MasteryData.resetAll((Player)player);
            MasteryData.setWizardry((Player)player, 0);
            MasteryData.setSupreme((Player)player, MasteryData.SupremeSchool.NETHER, false);
            MasteryData.setSupreme((Player)player, MasteryData.SupremeSchool.SKIES, false);
            MasteryData.setSupreme((Player)player, MasteryData.SupremeSchool.PLANET, false);
            MasteryData.clearPlanetRituals((Player)player);
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)"All masteries reset to 0"), true);
            return 1;
        }))).then(Commands.m_82127_((String)"max_all").executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            MasteryData.maxAll((Player)player);
            MasteryData.setWizardry((Player)player, 3);
            MasteryData.setSupreme((Player)player, MasteryData.SupremeSchool.NETHER, true);
            MasteryData.setSupreme((Player)player, MasteryData.SupremeSchool.SKIES, true);
            MasteryData.setSupreme((Player)player, MasteryData.SupremeSchool.PLANET, true);
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)"All masteries set to 3"), true);
            return 1;
        }))).then(Commands.m_82127_((String)"time_freeze").then(Commands.m_82129_((String)"radius", (ArgumentType)DoubleArgumentType.doubleArg((double)1.0, (double)128.0)).then(Commands.m_82129_((String)"seconds", (ArgumentType)IntegerArgumentType.integer((int)1, (int)600)).executes(ctx -> {
            ServerPlayer player = ((CommandSourceStack)ctx.getSource()).m_81375_();
            ServerLevel level = player.m_284548_();
            double radius = DoubleArgumentType.getDouble((CommandContext)ctx, (String)"radius");
            int seconds = IntegerArgumentType.getInteger((CommandContext)ctx, (String)"seconds");
            int ticks = seconds * 20;
            TimeFreezeManager.createGeneric(level, player.m_20182_(), radius, ticks);
            ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)("Created generic time freeze: radius " + radius + ", duration " + seconds + "s")), true);
            return 1;
        })))));
    }
}

