package com.qiuyue.someillagerservants.client.render.projectile;

import com.qiuyue.someillagerservants.common.entities.projectile.WitchBombEntity;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitchBombRenderer extends ThrownItemRenderer<WitchBombEntity> {
    public WitchBombRenderer(EntityRendererProvider.Context context) {
        super(context, 0.75F, false);
    }
}