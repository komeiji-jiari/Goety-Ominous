package com.qiuyue.goetyominus.client.render.ua;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominus.client.init.ModEntityLayers;
import com.qiuyue.goetyominus.client.render.layer.ua.ThrasherServantRenderLayer;
import com.qiuyue.goetyominus.client.render.model.ua.ThrasherServantModel;
import com.qiuyue.goetyominus.common.entities.ally.ua.GreatThrasherServant;
import com.qiuyue.goetyominus.common.entities.ally.ua.ThrasherServant;
import com.teamabnormals.upgrade_aquatic.core.UpgradeAquatic;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThrasherServantRenderer extends MobRenderer<ThrasherServant, ThrasherServantModel<ThrasherServant>> {
    private static final ResourceLocation THRASHER_TEXTURE = new ResourceLocation(UpgradeAquatic.MOD_ID, "textures/entity/thrasher/thrasher.png");
    private static final ResourceLocation GREAT_THRASHER_TEXTURE = new ResourceLocation(UpgradeAquatic.MOD_ID, "textures/entity/thrasher/great_thrasher.png");

    public ThrasherServantRenderer(EntityRendererProvider.Context context) {
        super(context, new ThrasherServantModel<>(context.bakeLayer(ModEntityLayers.THRASHER_SERVANT_LAYER)), 0.9F);
        this.addLayer(new ThrasherServantRenderLayer<>(this));
    }

    @Override
    protected void scale(ThrasherServant thrasher, PoseStack matrixStack, float partialTickTime) {
        if (thrasher instanceof GreatThrasherServant) {
            matrixStack.scale(1.75F, 1.75F, 1.75F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(ThrasherServant entity) {
        return entity instanceof GreatThrasherServant ? GREAT_THRASHER_TEXTURE : THRASHER_TEXTURE;
    }
}
