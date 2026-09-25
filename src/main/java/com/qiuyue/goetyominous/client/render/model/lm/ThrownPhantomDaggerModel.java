package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.ThrownPhantomDagger;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

/**
 * 幻影匕首的模型（照抄传奇怪物的 {@code ThrownPhantomDaggerModel}）。
 *
 * <p>整把匕首就是一坨方块拼的，没有骨骼动画 —— 全部零件都挂在 {@code dagger} 这一个
 * 根节点下面，渲染时整体旋转。渲染器负责摆位置和缩放，这里只管形状和朝向。
 *
 * <p>⚠️ 这个模型用的是<b>半透明发光</b>渲染类型（{@code entityTranslucentEmissive}），
 * 所以它必须写在 {@code LayerDefinition} 的 32x32 图集里，而且贴图要有 alpha 通道。
 * 换渲染类型会让整把匕首变成不透明的黑块。
 */
public class ThrownPhantomDaggerModel extends HierarchicalModel<ThrownPhantomDagger> {

    private final ModelPart dagger;

    public ThrownPhantomDaggerModel(ModelPart root) {
        super(RenderType::entityTranslucentEmissive);
        this.dagger = root.getChild("dagger");
    }

    /**
     * 匕首的方块构成。注释里的坐标都是模型空间（16 单位 = 1 格）。
     *
     * <p>从下往上读：
     * <ul>
     *   <li>{@code texOffs(7, 11)} —— 刀柄，唯一一个有厚度的方块（1 单位厚）；</li>
     *   <li>{@code texOffs(2, 1)} —— 刀身，3x9 的薄片（第三个尺寸是 0，纯平面）；</li>
     *   <li>{@code texOffs(8, 7)} —— 刀身和刀柄之间的连接段；</li>
     *   <li>{@code texOffs(8, 4)} / {@code texOffs(8, 1)} —— 左右两片护手。</li>
     * </ul>
     *
     * <p>根节点整体绕 X 轴转了 90 度（{@code 1.5708F = π/2}），并下移到 y=24 ——
     * 也就是「先躺平、再挪到实体脚底」。渲染器随后再往上抬 2.5 格把它顶到实体中心。
     * 这两步一正一反，改其中一个必须改另一个。
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition dagger = root.addOrReplaceChild("dagger",
                CubeListBuilder.create()
                        .texOffs(7, 11).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(2, 1).addBox(-1.5F, -12.0F, 0.0F, 3.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(8, 7).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(8, 4).addBox(-4.5F, -4.0F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(8, 1).addBox(0.5F, -4.0F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        // 刀柄末端的配重球。往左下斜 45 度（-0.7854F = -π/4）。
        dagger.addOrReplaceChild("cube_r1",
                CubeListBuilder.create()
                        .texOffs(1, 12).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.01F)),
                PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    /**
     * 把整把匕首画出来。
     *
     * <p>最后一个参数 {@code pAlpha} 是透明度 —— 渲染器会按 {@code fade} 淡出进度算好了传进来，
     * 所以这里必须原样透传给 {@code dagger.render(...)}，不能写死 1.0。
     */
    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        this.dagger.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return this.dagger;
    }

    /**
     * 摆姿势：朝哪飞就把匕首转成哪个方向。
     *
     * <p>渲染器传进来的 {@code netHeadYaw} 其实是「实体偏航角取负」，
     * {@code headPitch} 是实体俯仰角 —— 参数名是继承自 {@code HierarchicalModel} 的固定签名，
     * 含义和普通生物的「头朝哪」完全无关，别被名字骗了。
     *
     * <p>这里先把上一帧的姿势清空，再重设三个轴：
     * yRot 用偏航（左右转向）、xRot 写死 90 度（让躺平的模型立起来）、zRot 用俯仰（上下抬头）。
     */
    @Override
    public void setupAnim(ThrownPhantomDagger dagger, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.dagger.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.dagger.xRot = ((float) Math.PI / 2F);
        this.dagger.zRot = headPitch * ((float) Math.PI / 180F);
    }
}
