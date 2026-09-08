package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelLicowitchServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.LicowitchServant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderLicowitchServant extends MobRenderer<LicowitchServant, ModelLicowitchServant> {

    public static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/licowitch.png");

    public RenderLicowitchServant(EntityRendererProvider.Context context) {
        super(context, new ModelLicowitchServant(), 0.5F);
        this.addLayer(new ItemLayer(this, context.getItemInHandRenderer()));
    }

    @Override
    protected void scale(LicowitchServant entity, PoseStack poseStack, float partialTicks) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public ResourceLocation getTextureLocation(LicowitchServant entity) {
        return TEXTURE;
    }

    private static class ItemLayer extends ItemInHandLayer<LicowitchServant, ModelLicowitchServant> {

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
}
