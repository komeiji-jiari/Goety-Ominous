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
 * 堕落圣骑仆从的「眼睛发光」图层，照抄传奇怪物原版的 PossessedPaladinEyesLayer。
 *
 * <p>做法不是什么高科技：拿一张只有眼睛亮着的贴图（其余全黑），
 * 把<b>整个模型</b>用满亮度和 0.5 透明度再画一遍。黑的地方等于没画，
 * 只有眼睛那两笔亮起来，看起来就是眼睛在发光。
 *
 * <p>两个关键参数：
 * <ul>
 *   <li>{@code RenderType.eyes(...)} —— 原版的发光渲染类型，不受光照影响</li>
 *   <li>packedLight 传 {@code 0xF00000} —— 光照值拉满，否则夜里眼睛会跟着变暗，就不「发光」了</li>
 * </ul>
 *
 * <p>⚠️ 原版这里还包着一层 {@code if (entity.getAttackState() != 34)}，本移植<b>故意没搬</b>。
 * 状态 34 是<b>「沉睡」</b>——原版圣骑没被发现时会原地睡下（眼睛当然不该亮），
 * 状态 35 是「醒来」。这套休眠系统和仆从完全不搭（仆从一直跟着主人，没有「待机沉眠」这一说），
 * 所以整体没搬，见 {@code PossessedPaladinServant} 的类注释。
 *
 * <p>没搬的后果是：<b>这两个状态永远不会出现</b>（全文件搜不到一处
 * {@code setAttackState(34)}），那个判断搬过来也只是恒真的死代码。
 * 哪天要是真把休眠搬进来了，记得回来把这一层补上。
 */
@OnlyIn(Dist.CLIENT)
public class PossessedPaladinServantEyesLayer
        extends RenderLayer<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> {

    /** 一阶段的发光贴图。 */
    private static final ResourceLocation EYES_1 = new ResourceLocation(
            "legendary_monsters", "textures/entity/posessed_paladin/posessed_paladin_glow.png");
    /** 二阶段的发光贴图 —— 眼睛更红、更凶。 */
    private static final ResourceLocation EYES_2 = new ResourceLocation(
            "legendary_monsters", "textures/entity/posessed_paladin/posessed_paladin_glow_p2.png");

    /**
     * 0xF00000。Minecraft 把光照和天空光各压进 4 bit，0xF 是最大值，
     * 所以 (15 << 20) | (15 << 4) 就是「天光满、方块光满」，即满亮度。
     */
    private static final int FULL_BRIGHT = 0xF00000;

    public PossessedPaladinServantEyesLayer(
            RenderLayerParent<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       PossessedPaladinServant entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        // 眼睛的发光贴图也分阶段 —— 二阶段换成 EYES_2 才和变红的身体对得上。
        VertexConsumer consumer = buffer.getBuffer(
                RenderType.eyes(entity.getPhase() >= 2 ? EYES_2 : EYES_1));
        this.getParentModel().renderToBuffer(poseStack, consumer, FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 0.5F);
    }
}
