/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package com.vivideru.masteryofmagic.mixins;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={Entity.class})
public interface EntitySharedFlagAccessor {
    @Invoker(value="setSharedFlag")
    public void goetyMasteryOfMagic$setSharedFlag(int var1, boolean var2);
}

