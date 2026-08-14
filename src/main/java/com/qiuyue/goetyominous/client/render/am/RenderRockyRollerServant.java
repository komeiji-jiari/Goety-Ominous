package com.qiuyue.goetyominous.client.render.am;

import com.qiuyue.goetyominous.client.render.model.am.ModelRockyRollerServant;
import com.qiuyue.goetyominous.common.entities.ally.am.RockyRollerServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderRockyRollerServant extends MobRenderer<RockyRollerServant, ModelRockyRollerServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/rocky_roller.png");
    private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/rocky_roller_angry.png");
    private static final ResourceLocation TEXTURE_ROLLING = new ResourceLocation("alexsmobs:textures/entity/rocky_roller_rolling.png");

    public RenderRockyRollerServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelRockyRollerServant(), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(RockyRollerServant entity) {
        if (entity.isRolling()) {
            return TEXTURE_ROLLING;
        } else if (entity.isAngry()) {
            return TEXTURE_ANGRY;
        } else {
            return TEXTURE;
        }
    }
}
