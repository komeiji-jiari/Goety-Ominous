package com.qiuyue.goetyominous.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.world.GOCultistPatrolSpawner;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GOCultistCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("goetyominous")
                .then(Commands.literal("patrol")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("spawn")
                                .executes(ctx -> spawnPatrol(ctx.getSource(), ctx.getSource().getPlayerOrException()))
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(ctx -> spawnPatrol(ctx.getSource(), EntityArgument.getPlayer(ctx, "target")))))));
    }

    private static int spawnPatrol(CommandSourceStack source, ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        int spawned = new GOCultistPatrolSpawner().spawnPatrolAt(level, player.blockPosition(), true);
        source.sendSuccess(() -> Component.literal(
                "Spawned " + spawned + " cultist patrol members at " + player.getName().getString()), false);
        return spawned;
    }
}
