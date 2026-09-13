package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.ac.ModelLuxtructosaurusServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.LuxtructosaurusServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;
import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public class RenderLuxtructosaurusServant extends MobRenderer<LuxtructosaurusServant, ModelLuxtructosaurusServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/luxtructosaurus.png");
    private static final ResourceLocation TEXTURE_ENRAGED = new ResourceLocation("alexscaves:textures/entity/luxtructosaurus_enraged.png");
    private static final ResourceLocation TEXTURE_ENRAGED_GLOW = new ResourceLocation("alexscaves:textures/entity/luxtructosaurus_enraged_glow.png");
    private static final HashMap<Integer, Vec3> mouthParticlePositions = new HashMap<>();

    public RenderLuxtructosaurusServant(EntityRendererProvider.Context context) {
        super(context, new ModelLuxtructosaurusServant(), 4.0F);
        this.addLayer(new LayerGlow());
        this.addLayer(new LuxtructosaurusServantRiderLayer(this));
    }

    @Override
    protected void scale(LuxtructosaurusServant mob, PoseStack poseStack, float partialTicks) {
    }

    @Override
    protected float getFlipDegrees(LuxtructosaurusServant entity) {
        return 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(LuxtructosaurusServant entity) {
        return entity.isEnraged() ? TEXTURE_ENRAGED : TEXTURE;
    }

    @Nullable
    @Override
    protected RenderType getRenderType(LuxtructosaurusServant mob, boolean normal, boolean translucent, boolean outline) {
        ResourceLocation resourceLocation = this.getTextureLocation(mob);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourceLocation);
        }
        if (normal) {
            return RenderType.entityCutoutNoCull(resourceLocation);
        }
        return outline ? RenderType.outline(resourceLocation) : null;
    }

    @Override
    public void render(LuxtructosaurusServant entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLight) {
        this.shadowRadius = 4.0F * entity.getScale();
        super.render(entity, entityYaw, partialTicks, poseStack, source, packedLight);
        mouthParticlePositions.put(entity.getId(), this.getModel().getMouthPosition(Vec3.ZERO));
    }

    public static Vec3 getMouthPositionFor(int entityId) {
        return mouthParticlePositions.get(entityId);
    }

    @Override
    public boolean shouldRender(LuxtructosaurusServant entity, Frustum camera, double x, double y, double z) {
        if (super.shouldRender(entity, camera, x, y, z)) {
            return true;
        }
        for (PartEntity<?> part : entity.getParts()) {
            if (camera.isVisible(part.getBoundingBoxForCulling())) {
                return true;
            }
        }
        return false;
    }

    class LayerGlow extends RenderLayer<LuxtructosaurusServant, ModelLuxtructosaurusServant> {

        public LayerGlow() {
            super(RenderLuxtructosaurusServant.this);
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, LuxtructosaurusServant luxtructosaurus, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            float enragedAlpha = ((float) Math.sin(ageInTicks * 0.2F) * 0.15F + 0.85F) * luxtructosaurus.getEnragedProgress(partialTicks);
            VertexConsumer enragedGlowConsumer = bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE_ENRAGED_GLOW));
            this.getParentModel().renderToBuffer(matrixStackIn, enragedGlowConsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(luxtructosaurus, 0.0F), 1.0F, 1.0F, 1.0F, enragedAlpha);
        }
    }
}
