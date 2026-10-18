package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.FrostbittenGolemServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.FrostbittenGolemServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FrostbittenGolemServantRenderer extends MobRenderer<FrostbittenGolemServant, FrostbittenGolemServantModel<FrostbittenGolemServant>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/frostbitten_golem/frostbitten_golem.png");
    private static final ResourceLocation CRACKED_TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/frostbitten_golem/crackiness/frostbitten_golem_crack2.png");
    private static final ResourceLocation BROKEN_TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/frostbitten_golem/crackiness/frostbitten_golem_crack3.png");
    private static final RenderType GLOW = RenderType.eyes(new ResourceLocation(
            "legendary_monsters", "textures/entity/frostbitten_golem/glow/frostbitten_golem_glow.png"));

    public FrostbittenGolemServantRenderer(EntityRendererProvider.Context context) {
        super(context, new FrostbittenGolemServantModel<>(
                context.bakeLayer(ModEntityLayers.FROSTBITTEN_GOLEM_SERVANT_LAYER)), 1.5F);
        this.addLayer(new EyesLayer<FrostbittenGolemServant, FrostbittenGolemServantModel<FrostbittenGolemServant>>(this) {
            @Override
            public RenderType renderType() {
                return GLOW;
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(FrostbittenGolemServant entity) {
        return switch (entity.getCrackiness()) {
            case MEDIUM -> CRACKED_TEXTURE;
            case HIGH -> BROKEN_TEXTURE;
            default -> TEXTURE;
        };
    }

    @Override
    public void render(FrostbittenGolemServant entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.scale(1.5F, 1.5F, 1.5F);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
