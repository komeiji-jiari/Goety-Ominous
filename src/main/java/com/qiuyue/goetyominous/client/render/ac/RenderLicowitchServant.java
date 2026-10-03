package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelLicowitchServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.LicowitchServant;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class RenderLicowitchServant extends MobRenderer<LicowitchServant, ModelLicowitchServant> {

    public static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/licowitch.png");

    private static final Set<LicowitchServant> TELEPORTING_ON_SCREEN = new HashSet<>();
    private static final ModelLicowitchServant TELEPORTING_MODEL = new ModelLicowitchServant();

    public RenderLicowitchServant(EntityRendererProvider.Context context) {
        super(context, new ModelLicowitchServant(), 0.5F);
        this.addLayer(new ItemLayer(this, context.getItemInHandRenderer()));
        this.addLayer(new TeleportingDoubleLayer());
    }

    @Override
    protected void scale(LicowitchServant entity, PoseStack poseStack, float partialTicks) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public ResourceLocation getTextureLocation(LicowitchServant entity) {
        return TEXTURE;
    }

    @Override
    public void render(LicowitchServant entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        if (entity.getTeleportingToPos() != null) {
            TELEPORTING_ON_SCREEN.add(entity);
        }
    }

    @Override
    public boolean shouldRender(LicowitchServant entity, Frustum camera, double camX, double camY, double camZ) {
        return super.shouldRender(entity, camera, camX, camY, camZ) || entity.getTeleportingToPos() != null;
    }

    public static void renderEntireBatch(LevelRenderer levelRenderer, PoseStack poseStack, int renderTick, Camera camera, float partialTick) {
        for (LicowitchServant licowitch : TELEPORTING_ON_SCREEN) {
            Vec3 to = licowitch.getTeleportingToPos();
            float progress = licowitch.getTeleportingProgress(partialTick);
            if (to == null || progress <= 0.0F) {
                continue;
            }
            Vec3 cameraPos = camera.getPosition();
            float scale = 0.9375F;
            float bodyYaw = Mth.rotLerp(partialTick, licowitch.yBodyRotO, licowitch.yBodyRot);
            float headYaw = Mth.rotLerp(partialTick, licowitch.yHeadRotO, licowitch.yHeadRot);
            float netHeadYaw = headYaw - bodyYaw;
            float headPitch = Mth.rotLerp(partialTick, licowitch.xRotO, licowitch.getXRot());
            poseStack.pushPose();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            poseStack.translate(to.x, to.y + 1.5D, to.z);
            poseStack.scale(-scale, -scale, scale);
            poseStack.mulPose(Axis.YN.rotationDegrees(180.0F - bodyYaw));
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            VertexConsumer textureBuffer = bufferSource.getBuffer(RenderType.entityTranslucentCull(TEXTURE));
            TELEPORTING_MODEL.setupAnim(licowitch, licowitch.walkAnimation.position(partialTick), licowitch.walkAnimation.speed(partialTick),
                    (float) licowitch.tickCount + partialTick, netHeadYaw, headPitch);
            TELEPORTING_MODEL.renderToBuffer(poseStack, textureBuffer, 240, LivingEntityRenderer.getOverlayCoords(licowitch, 0.0F),
                    1.0F, progress, 1.0F, progress);
            PostEffectRegistry.renderEffectForNextTick(ClientProxy.PURPLE_WITCH_SHADER);
            VertexConsumer witchEffectBuffer = bufferSource.getBuffer(ACRenderTypes.getPurpleWitch(TEXTURE));
            TELEPORTING_MODEL.renderToBuffer(poseStack, witchEffectBuffer, 240, LivingEntityRenderer.getOverlayCoords(licowitch, 0.0F),
                    1.0F, 0.0F, 1.0F, progress);
            poseStack.popPose();
        }
        TELEPORTING_ON_SCREEN.clear();
    }

    private class ItemLayer extends ItemInHandLayer<LicowitchServant, ModelLicowitchServant> {

        private final ItemInHandRenderer witchItemInHandRenderer;

        public ItemLayer(RenderLicowitchServant renderer, ItemInHandRenderer itemInHandRenderer) {
            super(renderer, itemInHandRenderer);
            this.witchItemInHandRenderer = itemInHandRenderer;
        }

        @Override
        protected void renderArmWithItem(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext displayContext,
                                         HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource,
                                         int packedLight) {
            if (!itemStack.isEmpty() && livingEntity instanceof LicowitchServant licowitch) {
                float partialTicks = Minecraft.getInstance().getPartialTick();
                boolean crossedArms = licowitch.areArmsVisuallyCrossed(partialTicks);
                boolean staff = itemStack.is(ACItemRegistry.SUGAR_STAFF.get());
                poseStack.pushPose();
                if (crossedArms) {
                    this.getParentModel().translateToCrossedArms(humanoidArm, poseStack);
                } else {
                    this.getParentModel().translateToHand(humanoidArm, poseStack);
                }
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                boolean flag = humanoidArm == HumanoidArm.LEFT;
                if (crossedArms) {
                    poseStack.translate(0.0F, 0.125F, -0.125F);
                } else {
                    poseStack.translate((float) (flag ? 1 : -1) * 0.0325F, 0.125F, -0.125F);
                }
                if (staff) {
                    float forwardsBend = 0.0F;
                    if (licowitch.getAnimation() == LicowitchServant.ANIMATION_SPELL_0) {
                        forwardsBend = 0.66F * ACMath.cullAnimationTick(licowitch.getAnimationTick(), 1.0F, LicowitchServant.ANIMATION_SPELL_0, partialTicks, 18, 25);
                    }
                    if (licowitch.getAnimation() == LicowitchServant.ANIMATION_SPELL_1) {
                        forwardsBend = 0.4F * ACMath.cullAnimationTick(licowitch.getAnimationTick(), 1.0F, LicowitchServant.ANIMATION_SPELL_1, partialTicks, 25, 27);
                    }
                    if (crossedArms) {
                        poseStack.translate(0.0F, 0.0F, 0.1F);
                        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                        poseStack.mulPose(Axis.ZP.rotationDegrees(5.0F));
                        poseStack.mulPose(Axis.XP.rotationDegrees(flag ? 20.0F : -20.0F));
                        poseStack.translate(0.0F, 0.0F, -0.05F);
                    } else {
                        poseStack.mulPose(Axis.XP.rotationDegrees(-2.5F));
                    }
                    poseStack.translate(0.0F, -0.25F * forwardsBend, 0.0F);
                    poseStack.mulPose(Axis.XP.rotationDegrees(forwardsBend * -90.0F));
                } else {
                    poseStack.mulPose(Axis.XP.rotationDegrees(10.0F));
                }
                if (licowitch.getAnimation() == LicowitchServant.ANIMATION_EAT) {
                    float animationIntensity = ACMath.cullAnimationTick(licowitch.getAnimationTick(), 4.0F, LicowitchServant.ANIMATION_EAT, partialTicks, 0);
                    poseStack.mulPose(Axis.XP.rotationDegrees(animationIntensity * 30.0F));
                }
                this.witchItemInHandRenderer.renderItem(livingEntity, itemStack, displayContext, flag, poseStack, multiBufferSource, packedLight);
                poseStack.popPose();
            }
        }
    }

    private class TeleportingDoubleLayer extends RenderLayer<LicowitchServant, ModelLicowitchServant> {

        public TeleportingDoubleLayer() {
            super(RenderLicowitchServant.this);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LicowitchServant witch,
                           float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            float progress = witch.getTeleportingProgress(partialTicks);
            Vec3 to = witch.getTeleportingToPos();
            if (progress > 0.0F && to != null && to.subtract(witch.getPosition(partialTicks)).length() > 0.5D) {
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.PURPLE_WITCH_SHADER);
                VertexConsumer textureBuffer = bufferIn.getBuffer(RenderType.entityTranslucentCull(TEXTURE));
                this.getParentModel().renderToBuffer(poseStack, textureBuffer, packedLightIn,
                        LivingEntityRenderer.getOverlayCoords(witch, 0.0F), 1.0F, 1.0F - progress, 1.0F, progress);
                VertexConsumer witchEffectBuffer = bufferIn.getBuffer(ACRenderTypes.getPurpleWitch(TEXTURE));
                this.getParentModel().renderToBuffer(poseStack, witchEffectBuffer, packedLightIn,
                        LivingEntityRenderer.getOverlayCoords(witch, 0.0F), 1.0F, 0.0F, 1.0F, progress);
            }
        }
    }
}
