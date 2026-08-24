/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.CapabilityManager
 *  net.minecraftforge.common.capabilities.CapabilityToken
 *  net.minecraftforge.common.capabilities.ICapabilityProvider
 *  net.minecraftforge.common.util.LazyOptional
 *  net.minecraftforge.event.AttachCapabilitiesEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$Clone
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class MasterySystem {
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation("goety_mastery_of_magic", "mastery"), (ICapabilityProvider)new Provider());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }
        event.getOriginal().getCapability(MasteryCapability.MASTERY).ifPresent(oldStore -> event.getEntity().getCapability(MasteryCapability.MASTERY).ifPresent(newStore -> newStore.loadNBT(oldStore.saveNBT())));
    }

    public static class Provider
    implements ICapabilityProvider {
        private final PlayerMastery inst = new PlayerMastery();
        private final LazyOptional<PlayerMastery> optional = LazyOptional.of(() -> this.inst);

        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return cap == MasteryCapability.MASTERY ? this.optional.cast() : LazyOptional.empty();
        }
    }

    public static class MasteryCapability {
        public static final Capability<PlayerMastery> MASTERY = CapabilityManager.get((CapabilityToken)new CapabilityToken<PlayerMastery>(){});
    }

    public static class PlayerMastery {
        private int necromancy = 0;
        private int geoturgy = 0;
        private int sky = 0;
        private int nether = 0;
        private int end = 0;
        private int wild = 0;
        private int deep = 0;
        private int frost = 0;
        private int storm = 0;

        private int clamp(int lvl) {
            return Math.min(3, Math.max(0, lvl));
        }

        public int getNecromancy() {
            return this.necromancy;
        }

        public int getGeoturgy() {
            return this.geoturgy;
        }

        public int getSky() {
            return this.sky;
        }

        public int getNether() {
            return this.nether;
        }

        public int getEnd() {
            return this.end;
        }

        public int getWild() {
            return this.wild;
        }

        public int getDeep() {
            return this.deep;
        }

        public int getFrost() {
            return this.frost;
        }

        public int getStorm() {
            return this.storm;
        }

        public void setNecromancy(int lvl) {
            this.necromancy = this.clamp(lvl);
        }

        public void setGeoturgy(int lvl) {
            this.geoturgy = this.clamp(lvl);
        }

        public void setSky(int lvl) {
            this.sky = this.clamp(lvl);
        }

        public void setNether(int lvl) {
            this.nether = this.clamp(lvl);
        }

        public void setEnd(int lvl) {
            this.end = this.clamp(lvl);
        }

        public void setWild(int lvl) {
            this.wild = this.clamp(lvl);
        }

        public void setDeep(int lvl) {
            this.deep = this.clamp(lvl);
        }

        public void setFrost(int lvl) {
            this.frost = this.clamp(lvl);
        }

        public void setStorm(int lvl) {
            this.storm = this.clamp(lvl);
        }

        public CompoundTag saveNBT() {
            CompoundTag tag = new CompoundTag();
            tag.m_128405_("Necromancy", this.necromancy);
            tag.m_128405_("Geoturgy", this.geoturgy);
            tag.m_128405_("Sky", this.sky);
            tag.m_128405_("Nether", this.nether);
            tag.m_128405_("End", this.end);
            tag.m_128405_("Wild", this.wild);
            tag.m_128405_("Deep", this.deep);
            tag.m_128405_("Frost", this.frost);
            tag.m_128405_("Storm", this.storm);
            return tag;
        }

        public void loadNBT(CompoundTag tag) {
            this.necromancy = tag.m_128451_("Necromancy");
            this.geoturgy = tag.m_128451_("Geoturgy");
            this.sky = tag.m_128451_("Sky");
            this.nether = tag.m_128451_("Nether");
            this.end = tag.m_128451_("End");
            this.wild = tag.m_128451_("Wild");
            this.deep = tag.m_128451_("Deep");
            this.frost = tag.m_128451_("Frost");
            this.storm = tag.m_128451_("Storm");
        }
    }
}

