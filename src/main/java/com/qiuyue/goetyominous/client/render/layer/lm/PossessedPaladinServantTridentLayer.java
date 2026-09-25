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
 * 圣骑手里那把「灵魂三叉戟」的显示图层，照抄传奇怪物的 {@code PossessedPaladinTridentLayer}。
 *
 * <h2>它和眼睛图层是同一个套路</h2>
 * 拿一张<b>只有三叉戟那部分不透明</b>的贴图，把<b>整个模型</b>再画一遍。
 * 身体、四肢在贴图上都是透明的，等于没画，只有手里那把戟显出来。
 *
 * <p>所以「三叉戟拿在哪个位置」这件事完全由<b>模型本身的动画</b>决定
 * （模型里有一组专门管三叉戟的方块，跟着手臂走），这个图层一行坐标都不用算。
 *
 * <h2>什么时候画？两个条件</h2>
 * <ol>
 *   <li>{@link PossessedPaladinServant#hasTrident()} —— 只有状态 37（终结技）
 *       和 38（掷三叉戟）期间才画。别的招手里握的是剑或盾。</li>
 *   <li>透明度算出来要大于 0 —— 见下面的公式。</li>
 * </ol>
 *
 * <h2>透明度公式怎么来的</h2>
 * <pre>
 *   比例 = ghostItemFade.getAnimationFraction()   // 0.0 ~ 1.0
 *   剩余 = 1.0 - 比例                              // 1.0 ~ 0.0
 *   透明度 = max(剩余 - 0.5, 0.0)                  // 0.5 ~ 0.0
 * </pre>
 * 也就是说：计时器为 0 时半透明可见，涨到 5（总长 10 的一半）就彻底看不见了。
 *
 * <p>招式代码就是靠「把计时器从 5 减到 0」让戟<b>淡入</b>（招 38 第 5~10 tick），
 * 再靠「从 0 加回 6」让它<b>淡出</b>（第 64~69 tick，紧接着就是那八道冲击波）。
 * 所以这把戟是「凭空凝出来，扔出去之前先化掉」——和原作一致。
 *
 * <p>⚠️ 那 0.5 是<b>写死的</b>，和 {@code ghostItemFade} 的时长（10）绑在一起。
 * 以后要是把时长改了，这个数也得跟着改，不然淡入淡出的节奏会跑偏。
 *
 * <h2>顺带一提：招 25（盾击）也推同一个计时器，但看不到这把戟</h2>
 * 招 25 的 UpdateWithAttack 里同样在推进 {@code ghostItemFade}（见那边的注释），
 * 但这一层只在 {@code hasTrident()} 时才画，所以<b>不会</b>冒出一把三叉戟来。
 * 那次推进是给盾牌图层用的（{@code PossessedPaladinServantShieldLayer}），
 * 两个图层共用同一个计时器、各画各的东西 —— 这是原版的设计，不是巧合。
 */
@OnlyIn(Dist.CLIENT)
public class PossessedPaladinServantTridentLayer
        extends RenderLayer<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> {

    /** 一阶段：银白色的三叉戟。 */
    private static final ResourceLocation TRIDENT = new ResourceLocation(
            "legendary_monsters",
            "textures/entity/posessed_paladin/layer/possessed_paladin_trident_layer.png");
    /** 二阶段：同一把戟，透着红光。招 38 是二阶段专属，所以实际看到的几乎总是这张。 */
    private static final ResourceLocation TRIDENT_RED = new ResourceLocation(
            "legendary_monsters",
            "textures/entity/posessed_paladin/layer/possessed_paladin_trident_red_layer.png");

    public PossessedPaladinServantTridentLayer(
            RenderLayerParent<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       PossessedPaladinServant entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.hasTrident()) {
            return;
        }

        float alpha = Math.max(1.0F - Math.min(entity.ghostItemFade.getAnimationFraction(), 1.0F) - 0.5F, 0.0F);
        if (alpha <= 0.0F) {
            // 已经淡没了就别再提交一遍绘制 —— 白费的顶点数据，还给显卡添乱。
            // 原版没这一步（它靠 alpha=0 硬画），结果一样，这里顺手省掉。
            return;
        }

        // 用半透明发光渲染类型：它的着色器<b>不乘光照值</b>，所以戟是自发光的，
        // 夜里也是亮的 —— 这正是「灵魂做的」该有的样子。
        // 传奇怪物用的是它自己那份 LMRenderTypes（多一个 NO_CULL），
        // 这里用原版同款的 entityTranslucentEmissive：两者着色器一模一样，
        // 只差背面剔除，而这个模型是实心的封闭几何体，正面永远朝外，看不出区别。
        VertexConsumer consumer = buffer.getBuffer(
                RenderType.entityTranslucentEmissive(entity.getPhase() >= 2 ? TRIDENT_RED : TRIDENT));
        // 光照直接透传外面给的 packedLight —— 反正发光着色器不读它。
        // ⚠️ 这里<b>不要</b>照抄眼睛图层的 0xF00000：那边眼睛用的是 RenderType.eyes，
        //    那个着色器是<b>真的</b>会乘光照的，所以必须手动拉满；这个不用。
        this.getParentModel().renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, alpha);
    }
}
