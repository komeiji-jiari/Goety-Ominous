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
 * 圣骑背后那对「灵魂之翼」的显示图层，照抄传奇怪物的 {@code PossessedPaladinWingsLayer}。
 *
 * <h2>套路</h2>
 * 拿一张<b>只有翅膀那部分不透明</b>的贴图，把<b>整个模型</b>再画一遍。
 * 翅膀挂在 {@code body → RightWing / LeftWing} 那两根骨头上，
 * 扇动的角度全由模型动画决定，这里一行坐标都不用算。
 *
 * <h2>什么时候画？</h2>
 * 只有 {@link PossessedPaladinServant#hasWings()} 为真 —— 也就是<b>状态 37</b>，
 * 二阶段那记终结技。这是唯一会展开翅膀的招，所以实际游戏中这对翅膀
 * 只在「二阶段 + 正在放终结技」时才看得见。
 *
 * <h2>透明度公式（和盾牌 / 三叉戟图层一模一样）</h2>
 * <pre>
 *   透明度 = max(1.0 - min(ghostItemFade.getAnimationFraction(), 1.0) - 0.5, 0.0)
 * </pre>
 * 计时器为 0 时半透明可见，涨到 5（总长 10 的一半）就彻底看不见了。
 * 和盾牌那层是同一套「凝出来 → 化掉」的演出。
 *
 * <h2>⚠️ 二阶段才看得到，但贴图仍然要分两张</h2>
 * 一阶段那张 {@code possessed_paladin_wings_layer.png} 按现在的招式表
 * <b>其实用不到</b>（招 37 是二阶段专属）。这里还是费一行写上，
 * 原因是：万一以后有人把招 37 也放进一阶段，贴图分支不用回头补；
 * 而且分支写法能和另外几个图层保持一致，少一处「为什么就它不一样」的疑问。
 */
@OnlyIn(Dist.CLIENT)
public class PossessedPaladinServantWingsLayer
        extends RenderLayer<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> {

    /** 一阶段：半透明的魂翼。 */
    private static final ResourceLocation WINGS = new ResourceLocation(
            "legendary_monsters",
            "textures/entity/posessed_paladin/layer/possessed_paladin_wings_layer.png");
    /** 二阶段：同一对翅膀，透着红光。招 37 实际用的就是这张。 */
    private static final ResourceLocation WINGS_RED = new ResourceLocation(
            "legendary_monsters",
            "textures/entity/posessed_paladin/layer/possessed_paladin_wings_red_layer.png");

    public PossessedPaladinServantWingsLayer(
            RenderLayerParent<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       PossessedPaladinServant entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.hasWings()) {
            return;
        }

        float alpha = Math.max(1.0F - Math.min(entity.ghostItemFade.getAnimationFraction(), 1.0F) - 0.5F, 0.0F);
        if (alpha <= 0.0F) {
            return;
        }

        VertexConsumer consumer = buffer.getBuffer(
                RenderType.entityTranslucentEmissive(entity.getPhase() >= 2 ? WINGS_RED : WINGS));
        this.getParentModel().renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, alpha);
    }
}
