package com.qiuyue.goetyominous.client.render.projectile;

import com.qiuyue.goetyominous.common.entities.projectile.BurningPotionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BurningPotionRenderer extends ThrownItemRenderer<BurningPotionEntity> {
    public BurningPotionRenderer(EntityRendererProvider.Context context) {
        super(context, 0.75F, true);
    }
}
