package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.ThrownPhantomDaggerModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.ThrownPhantomDagger;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 幻影匕首的渲染器（照抄传奇怪物的 {@code ThrownPhantomDaggerRenderer}）。
 *
 * <h2>贴图是直接借用传奇怪物的</h2>
 * 两张贴图都写死在传奇怪物的资源包里（青色 / 二阶段红色），我们<b>不复制一份</b> ——
 * 这和 {@code PossessedPaladinServantRenderer} 引圣骑贴图是同一个做法。
 * 好处是以后传奇怪物改了贴图，我们跟着变；代价是这些路径不能改名。
 */
@OnlyIn(Dist.CLIENT)
public class ThrownPhantomDaggerRenderer extends EntityRenderer<ThrownPhantomDagger> {

    /** 一阶段：青白色。 */
    public static final ResourceLocation DAGGER_TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/posessed_paladin/phantom_dagger.png");
    /** 二阶段：红色。由实体的 {@code setRed(true)} 切换。 */
    public static final ResourceLocation DAGGER_RED_TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/posessed_paladin/phantom_dagger_red.png");

    private final ThrownPhantomDaggerModel model;

    public ThrownPhantomDaggerRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ThrownPhantomDaggerModel(context.bakeLayer(ModEntityLayers.PHANTOM_DAGGER));
    }

    @Override
    public void render(ThrownPhantomDagger entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // 传给模型的不是实体的偏航角，而是它<b>取负</b> —— 模型是朝 +Z 建的，
        // 而实体朝向是另一个约定，这里不取负匕首会反着指。
        float yaw = -entity.getYRot();
        float pitch = entity.getXRot();
        this.model.setupAnim(entity, 0.0F, 0.0F, (float) entity.tickCount + partialTicks, yaw, pitch);

        // 模型自己已经躺在 y=24（脚底）了，这里往上抬 2.5 格把它顶到实体中心附近。
        poseStack.translate(0.0F, -2.5F, 0.0F);
        float scale = 1.75F;
        poseStack.scale(scale, scale, scale);

        // ---- 淡出：把 fade（0→6）换算成一个透明度 ----
        // animationFraction 是 0~1 的进度；1 - 进度 就是「还剩多少」。
        // 再减 0.45 是原版的偏置 —— 意思是「淡出动画走到 45% 之后才开始真的变透明」，
        // 也就是先保持全不透明一段时间，最后猛地化掉。Math.max(..., 0) 防止变成负透明度。
        float animationProgress = Math.min(entity.fade.getAnimationFraction(), 1.0F);
        float alpha = Math.max(1.0F - animationProgress - 0.45F, 0.0F);

        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        // 15728880 = 0xF000F0，即「满亮度」—— 匕首是自发光材质，不受环境光照影响。
        this.model.renderToBuffer(poseStack, vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, alpha);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownPhantomDagger entity) {
        return entity.getRed() ? DAGGER_RED_TEXTURE : DAGGER_TEXTURE;
    }
}
