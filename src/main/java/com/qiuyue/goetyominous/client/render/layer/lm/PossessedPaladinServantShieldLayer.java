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
 * 圣骑左手那面「灵魂盾」的显示图层，照抄传奇怪物的 {@code PossessedPaladinShieldLayer}。
 *
 * <h2>套路</h2>
 * 拿一张<b>只有盾牌那部分不透明</b>的贴图，把<b>整个模型</b>再画一遍。
 * 盾牌挂在 {@code leftArm → lowerarm → bone2 → shield} 那根骨头上，
 * 位置和角度全由模型动画决定，这里一行坐标都不用算。
 *
 * <h2>什么时候画？两个条件</h2>
 * <ol>
 *   <li>{@link PossessedPaladinServant#hasShield()} —— 只有状态 25（盾击）期间才举盾，
 *       别的招手里握的是剑、匕首或三叉戟；</li>
 *   <li>透明度算出来要大于 0 —— 见下面的公式。</li>
 * </ol>
 *
 * <h2>透明度公式（和三叉戟 / 翅膀图层一模一样）</h2>
 * <pre>
 *   比例 = ghostItemFade.getAnimationFraction()   // 0.0 ~ 1.0
 *   剩余 = 1.0 - 比例                              // 1.0 ~ 0.0
 *   透明度 = max(剩余 - 0.5, 0.0)                  // 0.5 ~ 0.0
 * </pre>
 * 计时器为 0 时半透明可见，涨到 5（总长 10 的一半）就彻底看不见了。
 * 招式代码就是靠「把计时器从 5 减到 0」让盾<b>凝出来</b>，再靠「从 0 加回去」让它<b>化掉</b>。
 *
 * <p>⚠️ 那 0.5 是<b>写死的</b>，和 {@code ghostItemFade} 的时长（10）绑死。
 * 改时长的话这个数也得跟着改，否则凝出/化掉的节奏会跑偏。
 */
@OnlyIn(Dist.CLIENT)
public class PossessedPaladinServantShieldLayer
        extends RenderLayer<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> {

    /** 一阶段：银白色的灵魂盾。 */
    private static final ResourceLocation SHIELD = new ResourceLocation(
            "legendary_monsters",
            "textures/entity/posessed_paladin/layer/possessed_paladin_shield_layer.png");
    /** 二阶段：同一面盾，透着红光。 */
    private static final ResourceLocation SHIELD_RED = new ResourceLocation(
            "legendary_monsters",
            "textures/entity/posessed_paladin/layer/possessed_paladin_shield_red_layer.png");

    public PossessedPaladinServantShieldLayer(
            RenderLayerParent<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       PossessedPaladinServant entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.hasShield()) {
            return;
        }

        float alpha = Math.max(1.0F - Math.min(entity.ghostItemFade.getAnimationFraction(), 1.0F) - 0.5F, 0.0F);
        if (alpha <= 0.0F) {
            // 已经淡没了就别再提交一遍绘制 —— 白费的顶点数据，还给显卡添乱。
            // 原版没这一步（它靠 alpha=0 硬画），结果一样，这里顺手省掉。
            return;
        }

        VertexConsumer consumer = buffer.getBuffer(
                RenderType.entityTranslucentEmissive(entity.getPhase() >= 2 ? SHIELD_RED : SHIELD));
        this.getParentModel().renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, alpha);
    }
}
