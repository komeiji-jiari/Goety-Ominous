package com.qiuyue.goetyominous.client.render.layer.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.lm.PossessedPaladinServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 圣骑手里那对「幽灵匕首」的显示图层，照抄传奇怪物的 {@code PossessedPaladinDaggerLayer}。
 *
 * <h2>和眼睛 / 三叉戟图层是同一个套路</h2>
 * 拿一张<b>只有匕首那部分不透明</b>的贴图，把<b>整个模型</b>再画一遍。
 * 身体、四肢在贴图上都是透明的，等于没画，只有手里那两把匕首显出来。
 *
 * <p>所以「匕首攥在哪个位置」完全由<b>模型自身的动画</b>决定
 * （模型里 {@code lowerarm → dagger} 那根骨头跟着手臂走），这个图层一行坐标都不用算。
 *
 * <h2>什么时候画？</h2>
 * 只有一个条件：{@link PossessedPaladinServant#hasDagger()} —— 也就是
 * 状态 15 / 28（投匕首）期间。这两招是「凭空凝出匕首 → 甩出去」，
 * 匕首实体飞出去之后就归弹射物自己渲染了，手里这把是该藏起来的 ——
 * 好在状态一换，{@code hasDagger()} 就变 false，这一层自然不再画。
 *
 * <h2>⚠️ 它和三叉戟图层最容易混淆的一点：没有淡入淡出</h2>
 * 三叉戟 / 盾牌 / 翅膀那三层都要算 {@code ghostItemFade} 的透明度，
 * <b>这一层不算</b> —— 原版匕首图层从头到尾都是 <b>alpha = 1.0</b>，
 * 要么整个画出来，要么整个不画。别看着别的图层有公式就顺手抄一个过来，
 * 那样匕首会在招式前半段变成半透明的。
 */
@OnlyIn(Dist.CLIENT)
public class PossessedPaladinServantDaggerLayer
        extends RenderLayer<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> {

    /** 一阶段：灰白色的灵魂匕首。 */
    private static final ResourceLocation DAGGER = new ResourceLocation(
            "legendary_monsters",
            "textures/entity/posessed_paladin/layer/possessed_paladin_dagger_layer.png");
    /** 二阶段：同一对匕首，透着红光。 */
    private static final ResourceLocation DAGGER_RED = new ResourceLocation(
            "legendary_monsters",
            "textures/entity/posessed_paladin/layer/possessed_paladin_dagger_red_layer.png");

    public PossessedPaladinServantDaggerLayer(
            RenderLayerParent<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       PossessedPaladinServant entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.hasDagger()) {
            return;
        }

        // ⚠️ 先判断再取 buffer。原版是先取 buffer 再判断 —— 那样没拿匕首时
        //    也白白登记了一次渲染类型，等于每次都多绘一遍空模型（贴图全透明）。
        //    行为完全一样，只是省点事。
        //
        // 半透明发光渲染类型：着色器<b>不乘光照值</b>，所以匕首是自发光的，
        // 夜里也是亮的 —— 灵魂做的东西该有的样子。用法见三叉戟图层的同类注释。
        VertexConsumer consumer = buffer.getBuffer(
                RenderType.entityTranslucentEmissive(entity.getPhase() >= 2 ? DAGGER_RED : DAGGER));
        this.getParentModel().renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
    }
}
