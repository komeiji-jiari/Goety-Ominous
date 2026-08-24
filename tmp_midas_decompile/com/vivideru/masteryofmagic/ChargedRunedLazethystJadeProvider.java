/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  snownee.jade.api.BlockAccessor
 *  snownee.jade.api.IBlockComponentProvider
 *  snownee.jade.api.ITooltip
 *  snownee.jade.api.IWailaClientRegistration
 *  snownee.jade.api.IWailaPlugin
 *  snownee.jade.api.WailaPlugin
 *  snownee.jade.api.config.IPluginConfig
 */
package com.vivideru.masteryofmagic;

import com.vivideru.masteryofmagic.block.ChargedCryptRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedDeepRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedFrostRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedGeomancyRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedNetherRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedOminousRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedSkyRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedStormRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedVoidRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.ChargedWildRunedLazethystBlock;
import com.vivideru.masteryofmagic.block.entity.ChargedRunedLazethystBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class ChargedRunedLazethystJadeProvider
implements IWailaPlugin {
    private static final ResourceLocation UID = new ResourceLocation("goety_mastery_of_magic", "charged_lazethyst");

    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent((IBlockComponentProvider)Provider.INSTANCE, ChargedRunedLazethystBlock.class);
        registration.registerBlockComponent((IBlockComponentProvider)Provider.INSTANCE, ChargedNetherRunedLazethystBlock.class);
        registration.registerBlockComponent((IBlockComponentProvider)Provider.INSTANCE, ChargedFrostRunedLazethystBlock.class);
        registration.registerBlockComponent((IBlockComponentProvider)Provider.INSTANCE, ChargedStormRunedLazethystBlock.class);
        registration.registerBlockComponent((IBlockComponentProvider)Provider.INSTANCE, ChargedWildRunedLazethystBlock.class);
        registration.registerBlockComponent((IBlockComponentProvider)Provider.INSTANCE, ChargedSkyRunedLazethystBlock.class);
        registration.registerBlockComponent((IBlockComponentProvider)Provider.INSTANCE, ChargedVoidRunedLazethystBlock.class);
        registration.registerBlockComponent((IBlockComponentProvider)Provider.INSTANCE, ChargedCryptRunedLazethystBlock.class);
        registration.registerBlockComponent((IBlockComponentProvider)Provider.INSTANCE, ChargedOminousRunedLazethystBlock.class);
        registration.registerBlockComponent((IBlockComponentProvider)Provider.INSTANCE, ChargedGeomancyRunedLazethystBlock.class);
        registration.registerBlockComponent((IBlockComponentProvider)Provider.INSTANCE, ChargedDeepRunedLazethystBlock.class);
    }

    private static enum Provider implements IBlockComponentProvider
    {
        INSTANCE;


        public ResourceLocation getUid() {
            return UID;
        }

        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            BlockEntity blockEntity = accessor.getBlockEntity();
            if (!(blockEntity instanceof ChargedRunedLazethystBlockEntity)) {
                return;
            }
            ChargedRunedLazethystBlockEntity be = (ChargedRunedLazethystBlockEntity)blockEntity;
            tooltip.add((Component)Component.m_237113_((String)("Soul Energy: " + be.getStoredSE() + " / " + be.getSECapacity())).m_130940_(ChatFormatting.AQUA));
            ChargedRunedLazethystBlockEntity.TargetMode mode = be.getTargetMode();
            tooltip.add((Component)Component.m_237113_((String)("Mode: " + mode.name() + " (Range " + be.getTargetRange() + ")")).m_130940_(ChatFormatting.GOLD));
        }
    }
}

