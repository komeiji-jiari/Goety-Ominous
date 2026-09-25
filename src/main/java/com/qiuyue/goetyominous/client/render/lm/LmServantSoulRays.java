package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

/**
 * 画「灵魂射线」—— 从圣骑身体里朝四面八方射出去的一束束三角光柱。
 *
 * <h2>为什么单独抽成一个类</h2>
 * 原版<b>把同一段代码写了两遍</b>，藏在两个不同的类里：
 * <ul>
 *   <li>{@code PossessedPaladinRenderer.renderSoulRays} —— 死亡演出时从<b>身体中心</b>炸出来；</li>
 *   <li>{@code PossessedPaladinGrabLayer.renderSoulRays} —— 处决过程中从<b>被拎起来的敌人</b>
 *       身上炸出来。</li>
 * </ul>
 * 两份的逻辑<b>只有两个数不一样</b>：起点高度（1.35 格 vs 1 格）和「画几条」的来源。
 * 其余（随机数种子、六次旋转、长度 5~25、粗细 1~3、四个顶点、颜色）逐字相同。
 *
 * <p>⚠️ 这两份「看起来一样的代码」正是本项目栽过跟头的地方：曾经以为两个类里的
 * {@code vertex2 / vertex3} 也是重复的，就把阶段分支合并掉了 —— 结果<b>只有颜色不一样</b>，
 * 一阶段的射线被画成了红色。所以这里宁可抽成一个方法，让两份<b>物理上就是同一份</b>，
 * 从根上杜绝「改了一处忘了另一处」。
 *
 * <h2>颜色分阶段</h2>
 * 尖点（第 1、第 4 个顶点）永远是白色；中间两个侧顶点：
 * <table border="1">
 *   <caption>侧顶点颜色</caption>
 *   <tr><th>阶段</th><th>颜色</th></tr>
 *   <tr><td>一阶段</td><td>灵魂青 (57, 190, 197)</td></tr>
 *   <tr><td>二阶段</td><td>血红 (255, 0, 0)</td></tr>
 * </table>
 *
 * <p>透明度全部写死在顶点里（110），所以射线本身不会随时间变淡。
 */
@OnlyIn(Dist.CLIENT)
public final class LmServantSoulRays {

    /** √3 ÷ 2 ≈ 0.866。把左右两个顶点摆到正三角形的位置上。原版就是这个常量。 */
    private static final float HALF_SQRT_3 = 0.8660254F;

    /** 顶点色里的透明度，0~255。原版四个顶点全都写这个数。 */
    private static final int RAY_ALPHA = 110;

    /** 纯静态工具类，不需要实例。 */
    private LmServantSoulRays() {
    }

    /**
     * 画一簇灵魂射线。
     *
     * <p>本方法会自己 {@code pushPose / popPose}，调用方不用管坐标系。
     *
     * @param consumer      必须是 {@link LmServantRenderTypes#LIGHTNING_NO_CULL} 那个缓冲区 ——
     *                      它的顶点格式是 {@code POSITION_COLOR}，只认坐标和颜色，不采样贴图
     * @param poseStack     当前的坐标系。射线会以「这里 + {@code yOffset} 格高」为球心往外射
     * @param count         画几条。传 0 或负数就什么都不画
     * @param yOffset       球心相对当前坐标系抬高多少格。死亡演出用 1.35，抓取用 1.0
     * @param secondPhase   是不是二阶段。只影响侧顶点的颜色，见类注释
     * @param attackTicks   跟着旋转用的计时器。原版读的是实体的 {@code attackTicks} 字段
     * @param partialTicks  插值用的零头，让旋转看起来连贯
     */
    public static void render(VertexConsumer consumer, PoseStack poseStack, int count, float yOffset,
                              boolean secondPhase, int attackTicks, float partialTicks) {
        if (count <= 0) {
            return;
        }

        // ⚠️ 侧顶点（第 2、3 个）的颜色分阶段，一阶段是青的不是红的。见类注释。
        int sideRed = secondPhase ? 255 : 57;
        int sideGreen = secondPhase ? 0 : 190;
        int sideBlue = secondPhase ? 0 : 197;

        // ⚠️ 种子写死 432L —— 每一帧都从同一个随机序列重新开始，
        //    所以每条射线指向哪个方向是<b>固定</b>的，不会一帧一个样地乱闪。
        //    真正让它动起来的是下面第六次旋转里的 attackTicks。
        RandomSource random = RandomSource.create(432L);

        poseStack.pushPose();
        poseStack.translate(0.0F, yOffset, 0.0F);

        for (int i = 0; i < count; ++i) {
            // 六次旋转：前五次决定这束射线指向哪个随机方向，
            // 最后一次多带了一个 attackTicks + partialTicks ——
            // 所以就算别的都不变，整个三角形也会随 tick 慢慢自转。
            poseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F
                    + (float) attackTicks + partialTicks));

            // 长度（5~25 格）和粗细（1~3 格）。名字照抄原版。
            float length = random.nextFloat() * 20.0F + 5.0F;
            float width = random.nextFloat() * 2.0F + 1.0F;

            Matrix4f matrix = poseStack.last().pose();
            // 形状：尖点（原点，白）→ 左 → 右 → 尖点（原点，白）。
            //
            // ⚠️ 第 4 个顶点不能省，它就是把尖点又写了一遍。
            //    渲染类型是 QUADS（见 LmServantRenderTypes），一个图元要凑满 4 个顶点，
            //    写 3 个的话第 4 个位置会去「借」下一束射线的第一个顶点，
            //    整条顶点缓冲区就此错位，最后那束射线会画不出来。
            //    几何上第 4 个顶点和尖点重合，所以这个四边形是退化的，看着就是个三角形 ——
            //    原版就是这么写的，照抄。
            //
            // 左右两点都往后缩半个宽度（z = -0.5 × width），这是原版的写法，
            // 让三角形看着有点「厚度」而不是贴在一个平面上。
            vertex(consumer, matrix, 0.0F, 0.0F, 0.0F, 255, 255, 255);
            vertex(consumer, matrix, -HALF_SQRT_3 * width, length, -0.5F * width, sideRed, sideGreen, sideBlue);
            vertex(consumer, matrix, HALF_SQRT_3 * width, length, -0.5F * width, sideRed, sideGreen, sideBlue);
            vertex(consumer, matrix, 0.0F, 0.0F, 0.0F, 255, 255, 255);
        }

        poseStack.popPose();
    }

    /**
     * 提交一个顶点。颜色用 0~255 的整数（这是 {@code LIGHTNING_NO_CULL} 那个
     * {@code POSITION_COLOR} 顶点格式的要求，它不带贴图坐标和法线，
     * 所以这里只有坐标和颜色两项，比实体模型简单得多）。
     */
    private static void vertex(VertexConsumer consumer, Matrix4f matrix,
                               float x, float y, float z, int r, int g, int b) {
        consumer.vertex(matrix, x, y, z).color(r, g, b, RAY_ALPHA).endVertex();
    }
}
