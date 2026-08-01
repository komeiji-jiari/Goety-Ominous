package com.qiuyue.goetyominous.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModRenderType;
import com.Polarice3.Goety.init.ModTags.Items;
import com.Polarice3.Goety.utils.ModelPartPose;
import com.Polarice3.Goety.utils.ModelSnapshot;
import com.Polarice3.Goety.utils.ModelUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.HeresiarchServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.HeresiarchServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class HeresiarchServantRenderer<T extends HeresiarchServant> extends MobRenderer<T, HeresiarchServantModel<T>> {
    private static final ResourceLocation RUNE = Goety.location("textures/entity/cultist/spell_rune.png");
    private static final RenderType RENDER_TYPE;
    private static final float SNAPSHOT_INTERVAL = 10.0F;
    private static final float SNAPSHOT_LIFESPAN = 20.0F;
    private final HeresiarchServantModel<T> shadowModel;

    public HeresiarchServantRenderer(EntityRendererProvider.Context p_174304_) {
        super(p_174304_, new HeresiarchServantModel(p_174304_.bakeLayer(ModEntityLayers.HERESIARCH_SERVANT_LAYER)), 0.5F);
        this.addLayer(new ItemInHandLayer<T, HeresiarchServantModel<T>>(this, p_174304_.getItemInHandRenderer()) {
            public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T heresiarch, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                if (heresiarch.getMainHandItem().is(Items.WITCH_CURRENCY)) {
                    super.render(matrixStackIn, bufferIn, packedLightIn, heresiarch, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                }

            }
        });
        this.shadowModel = new HeresiarchServantModel(p_174304_.bakeLayer(ModEntityLayers.HERESIARCH_SERVANT_SHADOW_LAYER));
    }

    protected void scale(T entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.25F, 1.25F, 1.25F);
    }

    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
        float age = (float)entity.tickCount + partialTicks;
        poseStack.pushPose();
        float runeScale = Mth.lerp(partialTicks, entity.prevRuneScale, entity.runeScale) * 2.0F;
        poseStack.translate(0.0, (double)(entity.getBbHeight() * 1.65F), 0.0);
        poseStack.scale(runeScale, runeScale, runeScale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(age));
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
        vertex(vertexconsumer, matrix4f, matrix3f, 15728880, 0.0F, 0, 0, 1);
        vertex(vertexconsumer, matrix4f, matrix3f, 15728880, 1.0F, 0, 1, 1);
        vertex(vertexconsumer, matrix4f, matrix3f, 15728880, 1.0F, 1, 1, 0);
        vertex(vertexconsumer, matrix4f, matrix3f, 15728880, 0.0F, 1, 0, 0);
        poseStack.popPose();
        if (entity.isAlive()) {
            double currentX = Mth.lerp((double)partialTicks, entity.xo, entity.getX());
            double currentY = Mth.lerp((double)partialTicks, entity.yo, entity.getY());
            double currentZ = Mth.lerp((double)partialTicks, entity.zo, entity.getZ());
            float currentTick = this.getBob(entity, partialTicks);
            HeresiarchServantModel var10001;
            if (entity.trailSnapshots.isEmpty() || currentTick - entity.lastTrailTick > 10.0F) {
                if (entity.shouldAddTrailSnapshot()) {
                    List var10000 = ((HeresiarchServantModel)this.getModel()).allPartNames;
                    var10001 = (HeresiarchServantModel)this.getModel();
                    Objects.requireNonNull(var10001);
                    Map<String, ModelPartPose> snapshot = ModelUtil.saveModelSnapshot(var10000, var10001::getAnyDescendantWithName);
                    entity.trailSnapshots.add(0, Pair.of(new Vec3(currentX, currentY, currentZ), new ModelSnapshot(0.0F, Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot), currentTick, snapshot)));
                    entity.lastTrailTick = currentTick;
                }

                entity.trailSnapshots.removeIf((p) -> {
                    return currentTick - ((ModelSnapshot)p.getSecond()).timestamp() > 20.0F;
                });

                while(entity.trailSnapshots.size() > 32) {
                    entity.trailSnapshots.remove(entity.trailSnapshots.size() - 1);
                }
            }

            for(int i = 0; i < entity.trailSnapshots.size(); ++i) {
                poseStack.pushPose();
                Vec3 trailPos = (Vec3)((Pair)entity.trailSnapshots.get(i)).getFirst();
                ModelSnapshot snapshot = (ModelSnapshot)((Pair)entity.trailSnapshots.get(i)).getSecond();
                Map var25 = snapshot.poses();
                var10001 = this.shadowModel;
                Objects.requireNonNull(var10001);
                ModelUtil.loadPoseFromSnapshot(var25, var10001::getAnyDescendantWithName);
                poseStack.translate(trailPos.x - currentX, trailPos.y - currentY, trailPos.z - currentZ);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - snapshot.yRot()));
                poseStack.scale(-1.0F, -1.0F, 1.0F);
                this.scale(entity, poseStack, partialTicks);
                poseStack.translate(0.0F, -1.5F, 0.0F);
                RenderType afterimageType = ModRenderType.entityTranslucentNoDepth(this.getTextureLocation(entity));
                VertexConsumer vertexConsumer = bufferSource.getBuffer(afterimageType);
                float modelAlpha = (1.0F - Mth.clamp(currentTick - snapshot.timestamp(), 0.0F, 20.0F) / 20.0F) * 0.35F;
                if (modelAlpha > 0.0F) {
                    this.shadowModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, modelAlpha);
                }

                poseStack.popPose();
            }
        }

    }

    private static void vertex(VertexConsumer p_254095_, Matrix4f p_254477_, Matrix3f p_253948_, int p_253829_, float p_253995_, int p_254031_, int p_253641_, int p_254243_) {
        p_254095_.vertex(p_254477_, p_253995_ - 0.5F, (float)p_254031_ - 0.5F, 0.0F).color(255, 255, 255, 255).uv((float)p_253641_, (float)p_254243_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(p_253829_).normal(p_253948_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    public ResourceLocation getTextureLocation(T entity) {
        return entity.getResourceLocation();
    }

    static {
        RENDER_TYPE = RenderType.entityCutoutNoCull(RUNE);
    }
}
