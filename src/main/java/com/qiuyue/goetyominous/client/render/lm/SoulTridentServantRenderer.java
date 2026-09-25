package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SoulTrident;
import net.miauczel.legendary_monsters.entity.ProjectileEntityRenderer.SoulTridentModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * {@link SoulTrident} 的渲染器，照抄传奇怪物的 {@code SoulTridentRenderer}。
 *
 * <h2>模型和贴图都是借的，一行没抄</h2>
 * <ul>
 *   <li><b>模型</b>：直接用传奇怪物的 {@link SoulTridentModel}。
 *       它和本项目的 {@code ThrownPhantomDaggerModel} 不同 —— 那个是我们自己抄的一份，
 *       这边没必要，因为三叉戟的模型我们一点不想改。</li>
 *   <li><b>贴图</b>：直接指向传奇怪物资源包里的那张，不复制进本项目。</li>
 * </ul>
 *
 * <h2>⚠️ 模型是自己烤的，没走 ModelLayer 注册</h2>
 * 原版是 {@code context.bakeLayer(ModModelLayers.SOUL_TRIDENT_LAYER)} —— 从
 * 传奇怪物注册好的模型层里取。这里改成 {@code createBodyLayer().bakeRoot()}，
 * 也就是<b>就地烤一个根节点</b>。两种做法出来的模型完全一样，但后者不需要
 * 去碰传奇怪物的 {@code ModModelLayers}，少一处外部依赖。
 *
 * <p>渲染器每个实体类型只 new 一次，所以这多出来的一份模型常驻内存也无所谓。
 *
 * <h2>两个照抄过来的细节，别以为是笔误</h2>
 * <ol>
 *   <li><b>俯仰角是负数</b>：{@code -entity.xRotO} / {@code -entity.getXRot()}。
 *       原版就这么写的，三叉戟飞出去时尖端朝前而不是朝下。</li>
 *   <li><b>先画模型再调 {@code super.render()}</b>：{@code super} 那一步负责画
 *       名牌之类的附属物，颠倒过来会糊在模型下面。</li>
 * </ol>
 */
@OnlyIn(Dist.CLIENT)
public class SoulTridentServantRenderer extends EntityRenderer<SoulTrident> {

    /**
     * 用的是<b>二阶段</b>那张红色贴图。这不是偷懒 ——
     * 传奇怪物的 {@code SoulTridentRenderer} 里写死的也是这一张（它只有一个常量，
     * 没有按阶段切换的写法）。招 38 本身又要求 {@code getPhase() >= 2}，
     * 所以砸出去的三叉戟本来就只可能是红色的。
     */
    private static final ResourceLocation SOUL_TRIDENT = new ResourceLocation(
            "legendary_monsters",
            "textures/entity/posessed_paladin/layer/possessed_paladin_trident_red_layer.png");

    /** 原版的放大倍数。模型本身捏得很小，不放大只有三分之一长。 */
    private static final float MODEL_SCALE = 1.5F;

    private final SoulTridentModel model;

    public SoulTridentServantRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SoulTridentModel(SoulTridentModel.createBodyLayer().bakeRoot());
    }

    @Override
    public void render(SoulTrident entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        // 朝向按「上一 tick → 这一 tick」插值，否则飞行中的三叉戟会一顿一顿地抖。
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, -entity.xRotO, -entity.getXRot())));
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // getFoilBuffer 的第二个 false 是「不用发光贴图」，第四个 entity.isFoil() 是「附魔流光」——
        // 模型构造时已经声明了 entityTranslucentEmissive，所以整把戟本身就是自发光的。
        VertexConsumer consumer = ItemRenderer.getFoilBuffer(buffer,
                this.model.renderType(this.getTextureLocation(entity)), false, entity.isFoil());
        // 最后一个 0.5F 是整体透明度，和原版一致。
        this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 0.5F);
        RenderSystem.disableBlend();

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SoulTrident entity) {
        return SOUL_TRIDENT;
    }
}
