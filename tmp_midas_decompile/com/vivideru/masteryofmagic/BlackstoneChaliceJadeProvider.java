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

import com.vivideru.masteryofmagic.block.BlackstoneChaliceBlock;
import com.vivideru.masteryofmagic.block.entity.BlackstoneChaliceBlockEntity;
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
public class BlackstoneChaliceJadeProvider
implements IWailaPlugin {
    private static final ResourceLocation UID = new ResourceLocation("goety_mastery_of_magic", "blackstone_chalice");

    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent((IBlockComponentProvider)Provider.INSTANCE, BlackstoneChaliceBlock.class);
    }

    private static enum Provider implements IBlockComponentProvider
    {
        INSTANCE;


        public ResourceLocation getUid() {
            return UID;
        }

        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            BlockEntity blockEntity = accessor.getBlockEntity();
            if (!(blockEntity instanceof BlackstoneChaliceBlockEntity)) {
                return;
            }
            BlackstoneChaliceBlockEntity be = (BlackstoneChaliceBlockEntity)blockEntity;
            int blood = be.getBlood();
            int bloodLevel = be.getBloodLevel();
            tooltip.add((Component)Component.m_237113_((String)("Blood Level: " + bloodLevel + " / 9")).m_130940_(ChatFormatting.DARK_RED));
            tooltip.add((Component)Component.m_237113_((String)("Blood: " + blood + " / 9000")).m_130940_(ChatFormatting.RED));
            if (be.hasOwner() && be.getOwnerName() != null) {
                tooltip.add((Component)Component.m_237113_((String)("Owner: " + be.getOwnerName())).m_130940_(ChatFormatting.GRAY));
            } else {
                tooltip.add((Component)Component.m_237113_((String)"Owner: none").m_130940_(ChatFormatting.DARK_GRAY));
            }
        }
    }
}

