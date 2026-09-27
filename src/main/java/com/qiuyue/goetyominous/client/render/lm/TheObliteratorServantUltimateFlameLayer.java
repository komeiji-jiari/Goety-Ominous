package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.lm.TheObliteratorServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import net.miauczel.legendary_monsters.entity.client.Render.LMRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TheObliteratorServantUltimateFlameLayer extends RenderLayer<TheObliteratorServant, TheObliteratorServantModel<TheObliteratorServant>> {

    private static final ResourceLocation[] ULTIMATE_FLAME_TEXTURES = new ResourceLocation[]{
            new ResourceLocation("legendary_monsters", "textures/entity/the_warped_one/ultimate_flame/ultimate_flame_0.png"),
            new ResourceLocation("legendary_monsters", "textures/entity/the_warped_one/ultimate_flame/ultimate_flame_1.png"),
            new ResourceLocation("legendary_monsters", "textures/entity/the_warped_one/ultimate_flame/ultimate_flame_2.png"),
            new ResourceLocation("legendary_monsters", "textures/entity/the_warped_one/ultimate_flame/ultimate_flame_3.png"),
            new ResourceLocation("legendary_monsters", "textures/entity/the_warped_one/ultimate_flame/ultimate_flame_4.png"),
    };

    public TheObliteratorServantUltimateFlameLayer(RenderLayerParent<TheObliteratorServant, TheObliteratorServantModel<TheObliteratorServant>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, TheObliteratorServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        int totalFrames = ULTIMATE_FLAME_TEXTURES.length;
        float speed = 10.0F;
        int frame = (int) (((float) entity.tickCount + partialTicks) * speed / 20.0F) % totalFrames;
        RenderType rt = LMRenderTypes.getGlowEyes(ULTIMATE_FLAME_TEXTURES[frame]);
        VertexConsumer vertexConsumer = buffer.getBuffer(rt);
        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
        if (entity.getIsThirdPhase() && (entity.getAttackState() == 53) && !entity.isDuringTeleportation(entity.level().isClientSide)) {
            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, overlay, 0.0F, 0.0F, 0.0F, 0.0F);
        }
    }
}
