package com.qiuyue.goetyominus.client.render;

import com.qiuyue.goetyominus.client.render.model.CrashagerServantModel;
import com.qiuyue.goetyominus.common.entities.ally.mobs.CrashagerServant;
import com.qiuyue.goetyominus.client.render.layer.CrashagerGlowLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class CrashagerServantRenderer extends MobRenderer<CrashagerServant, CrashagerServantModel<CrashagerServant>> {
    private final Random random = new Random();
    private static final ResourceLocation TEXTURE = new ResourceLocation("illageandspillage", "textures/entity/magispeller/magispeller_nothing.png");

    public CrashagerServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new CrashagerServantModel<>(renderManagerIn.bakeLayer(CrashagerServantModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new CrashagerGlowLayer<>(this));
    }

    public Vec3 getRenderOffset(CrashagerServant p_225627_1_, float p_225627_2_) {
        if (p_225627_1_.getAttackStage() == 2) {
            return new Vec3(this.random.nextGaussian() * 0.05, 0.0, this.random.nextGaussian() * 0.05);
        } else {
            return super.getRenderOffset(p_225627_1_, p_225627_2_);
        }
    }

    public ResourceLocation getTextureLocation(CrashagerServant p_110775_1_) {
        return TEXTURE;
    }
}
