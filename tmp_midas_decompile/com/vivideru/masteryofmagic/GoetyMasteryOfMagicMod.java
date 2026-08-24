/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  net.minecraftforge.fml.util.thread.SidedThreadGroups
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.NetworkRegistry
 *  net.minecraftforge.network.simple.SimpleChannel
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.vivideru.masteryofmagic;

import com.mojang.brigadier.CommandDispatcher;
import com.vivideru.masteryofmagic.GoetyMasteryCommands;
import com.vivideru.masteryofmagic.GoetyMasteryOfMagicModRecipes;
import com.vivideru.masteryofmagic.ModCompat;
import com.vivideru.masteryofmagic.client.ModBlockRenderLayers;
import com.vivideru.masteryofmagic.config.BossConfig;
import com.vivideru.masteryofmagic.config.GameplayConfig;
import com.vivideru.masteryofmagic.config.MobWeaknessConfig;
import com.vivideru.masteryofmagic.config.RunedBlocksConfig;
import com.vivideru.masteryofmagic.config.SpellConfig;
import com.vivideru.masteryofmagic.config.SupremeMasteryConfig;
import com.vivideru.masteryofmagic.goldification.GoldificationCommand;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlockEntities;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlocks;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEnchantments;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModMenus;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModMobEffects;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModParticleTypes;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModSounds;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModTabs;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicNetwork;
import com.vivideru.masteryofmagic.init.ModFocuses;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value="goety_mastery_of_magic")
public class GoetyMasteryOfMagicMod {
    public static final Logger LOGGER = LogManager.getLogger(GoetyMasteryOfMagicMod.class);
    public static final String MODID = "goety_mastery_of_magic";
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "goety_mastery_of_magic"), () -> "1", "1"::equals, "1"::equals);
    private static int messageID = 0;
    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<AbstractMap.SimpleEntry<Runnable, Integer>>();

    public GoetyMasteryOfMagicMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModBlockRenderLayers::register);
        MinecraftForge.EVENT_BUS.register((Object)this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        GoetyMasteryOfMagicModSounds.REGISTRY.register(bus);
        GoetyMasteryOfMagicModBlocks.REGISTRY.register(bus);
        GoetyMasteryOfMagicModBlockEntities.REGISTRY.register(bus);
        GoetyMasteryOfMagicModItems.REGISTRY.register(bus);
        GoetyMasteryOfMagicModEntities.REGISTRY.register(bus);
        GoetyMasteryOfMagicModParticleTypes.REGISTRY.register(bus);
        GoetyMasteryOfMagicModEnchantments.REGISTRY.register(bus);
        GoetyMasteryOfMagicModTabs.REGISTRY.register(bus);
        GoetyMasteryOfMagicModMobEffects.REGISTRY.register(bus);
        GoetyMasteryOfMagicModRecipes.RECIPE_SERIALIZERS.register(bus);
        GoetyMasteryOfMagicModMenus.register(FMLJavaModLoadingContext.get().getModEventBus());
        RunedBlocksConfig.register();
        SpellConfig.register();
        MobWeaknessConfig.register();
        BossConfig.register();
        GameplayConfig.register();
        SupremeMasteryConfig.register();
        ModFocuses.init();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        GoetyMasteryCommands.register(event);
        GoldificationCommand.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher());
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(GoetyMasteryOfMagicNetwork::register);
        ModCompat.setup(event);
    }

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        ++messageID;
    }

    public static void queueServerWork(int tick, Runnable action) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
            workQueue.add(new AbstractMap.SimpleEntry<Runnable, Integer>(action, tick));
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ArrayList actions = new ArrayList();
            workQueue.forEach(work -> {
                work.setValue((Integer)work.getValue() - 1);
                if ((Integer)work.getValue() == 0) {
                    actions.add(work);
                }
            });
            actions.forEach(e -> ((Runnable)e.getKey()).run());
            workQueue.removeAll(actions);
        }
    }
}

