package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.DiscipleServantEyesLayer;
import com.qiuyue.goetyominous.client.render.model.DiscipleServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.DiscipleServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DiscipleServantRenderer extends MobRenderer<DiscipleServant, DiscipleServantModel<DiscipleServant>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/disciple_servant.png");

    public DiscipleServantRenderer(EntityRendererProvider.Context context) {
        super(context, new DiscipleServantModel<>(context.bakeLayer(ModEntityLayers.DISCIPLE_SERVANT_LAYER)), 0.6F);
        this.addLayer(new DiscipleServantEyesLayer(this));
    }

    @Override
    protected void scale(DiscipleServant disciple, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.0F, 1.0F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(DiscipleServant disciple) {
        return TEXTURE;
    }
}