package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * 画「一片纯色四边形」的小工具，照抄传奇怪物的 {@code RenderUtils}。
 *
 * <h2>为什么原版要自己写这个</h2>
 * 原版 MC 没有「随手画一个面片」的现成方法 —— {@code VertexConsumer} 是底层接口，
 * 每个顶点都得手工组装：坐标、颜色、贴图坐标、光照、法线，五样缺一不可
 * （少填一样，顶点格式对不上，游戏直接崩或者画出来全黑）。
 * 所以这类小工具在模组里很常见，就是把那套繁琐的调用包一层。
 *
 * <h2>「Pivoted（带支点）」是什么意思</h2>
 * 注意这个方法比普通的画四边形多了一步 {@code translate(-scale, 0, 0)}：
 * <ul>
 *   <li>普通画法是「以四边形自己的中心为轴转」；</li>
 *   <li>这个画法是「把左边那条边当成合页，绕着它转」。</li>
 * </ul>
 * 用来画预警光圈时，就相当于把一条光带钉在圣骑身上、让它朝外甩出去。
 *
 * <h2>⚠️ 只搬了实际用到的那一个重载</h2>
 * 传奇怪物的 {@code RenderUtils} 里有三个方法（一个 {@code renderQuad}、
 * 两个同名不同参数的 {@code renderPivotedQuad}）。这里只搬了预警光圈在用的这个，
 * 另外两个等真有图层需要时再加 —— 搬过来没人调用的代码只会变成日后的负担。
 */
@OnlyIn(Dist.CLIENT)
public class LmServantRenderUtils {

    /**
     * 画一片长方形面片，并绕它的左边那条边旋转。
     *
     * @param scale     半宽（往左半格、往右半格，所以实际宽度是它的两倍）
     * @param scale2    半高，同理
     * @param x         面片在<b>当前坐标系里</b>的位置（不是世界坐标 —— 调用方已经摆好位置了）
     * @param y         同上，高度
     * @param z         同上
     * @param xRot      俯仰角，单位是<b>度</b>。⚠️ 内部会取负号，所以传正数 = 往上翻
     * @param yRot      偏航角，单位是度。绕竖轴转，用来对准圣骑的朝向
     * @param zRot      翻滚角，单位是度
     * @param consumer  往哪个缓冲区写顶点
     * @param poseStack 当前的姿态栈。方法会自己 push / pop，调用方不用管
     * @param overlay   受击闪红的覆盖层。画特效一般传 {@code OverlayTexture.NO_OVERLAY}
     * @param packedLight 光照值
     * @param r / g / b / a 颜色和透明度，取值都是 {@code 0.0 ~ 1.0}
     */
    public static void renderPivotedQuad(float scale, float scale2, double x, double y, double z,
                                         double xRot, double yRot, double zRot,
                                         VertexConsumer consumer, PoseStack poseStack,
                                         int overlay, int packedLight,
                                         float r, float g, float b, float a) {
        poseStack.pushPose();

        // ── 第一步：摆位置和朝向 ──
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees((float) yRot));
        // 先横着挪出去半个宽度 —— 这就是「合页」那一步：转轴从面片中间挪到了左边那条边上。
        poseStack.translate(-scale, 0.0D, 0.0D);
        // ⚠️ 取负号是原版就这么写的（MC 的俯仰角正负方向和直觉相反），别改成正的。
        poseStack.mulPose(Axis.XP.rotationDegrees((float) (-xRot)));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) zRot));

        // 顶点是手工拼的，法线也得自己算 —— 从当前矩阵里取，
        // 这样面片转到哪个方向，法线就跟着指向哪边，光照才对得上。
        Matrix3f normalMat = poseStack.last().normal();
        Matrix4f matrix = poseStack.last().pose();
        // ⚠️ 四个顶点的顺序不能乱：必须是「左下 → 右下 → 右上 → 左上」的环形，
        //    也就是逆时针。顺序错了会被当成背面剔除掉，画出来什么都没有。
        //    四个顶点的 uv 依次是 (0,1) (1,1) (1,0) (0,0)，正好铺满整张「虚拟贴图」——
        //    这个渲染类型不采样贴图，uv 只是占位，但顶点格式要求必须填。
        vertex(consumer, matrix, normalMat, -scale, -scale2, overlay, packedLight, r, g, b, a, 0.0F, 1.0F);
        vertex(consumer, matrix, normalMat, scale, -scale2, overlay, packedLight, r, g, b, a, 1.0F, 1.0F);
        vertex(consumer, matrix, normalMat, scale, scale2, overlay, packedLight, r, g, b, a, 1.0F, 0.0F);
        vertex(consumer, matrix, normalMat, -scale, scale2, overlay, packedLight, r, g, b, a, 0.0F, 0.0F);

        poseStack.popPose();
    }

    /** 提交一个顶点。原版是四行重复的代码抄四遍，这里抽成一个方法，参数一一对应。 */
    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normalMat,
                               float px, float py, int overlay, int packedLight,
                               float r, float g, float b, float a, float u, float v) {
        consumer.vertex(matrix, px, py, 0.0F)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(packedLight)
                .normal(normalMat, 0.0F, 0.0F, 1.0F)
                .endVertex();
    }

    /** 静态工具类，不要实例。 */
    private LmServantRenderUtils() {
    }
}
