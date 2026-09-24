package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.ShulkerMimicServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.ShulkerMimicServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerMimicServantRenderer extends MobRenderer<ShulkerMimicServant, ShulkerMimicServantModel<ShulkerMimicServant>> {

    private static final ResourceLocation DEFAULT = new ResourceLocation("legendary_monsters", "textures/entity/shulker_mimic/shulker_mimic.png");
    private static final ResourceLocation CRACKY = new ResourceLocation("legendary_monsters", "textures/entity/shulker_mimic/crack/crack_1.png");
    private static final ResourceLocation CRACKED = new ResourceLocation("legendary_monsters", "textures/entity/shulker_mimic/crack/crack_2.png");

    public ShulkerMimicServantRenderer(EntityRendererProvider.Context context) {
        super(context, new ShulkerMimicServantModel<>(context.bakeLayer(ModEntityLayers.SHULKER_MIMIC_SERVANT_LAYER)), 1.5F);
        this.addLayer(new ShulkerMimicServantGrabLayer(this, context.getEntityRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(ShulkerMimicServant entity) {
        return switch (entity.getPhase()) {
            case 2 -> CRACKY;
            case 3 -> CRACKED;
            default -> DEFAULT;
        };
    }

    @Override
    public void render(ShulkerMimicServant entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        entity.setPartXRot(-this.model.head.xRot * 57.295776F);
        entity.setPartYRot(-this.model.head.yRot * 57.295776F);
        poseStack.scale(1.5F, 1.5F, 1.5F);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
