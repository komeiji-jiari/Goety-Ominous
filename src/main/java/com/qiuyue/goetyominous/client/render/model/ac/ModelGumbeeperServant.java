package com.qiuyue.goetyominous.client.render.model.ac;

import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.qiuyue.goetyominous.common.entities.ally.ac.GumbeeperServant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 糖球苦力怕仆从模型:逐字移植 AC GumbeeperModel(Codec 方式搭建几何),保证与原版外观一致。
 * <ul>
 *   <li>头部玻璃罩内 6 层糖球随 {@code getGumballsLeft()} 逐层隐去;</li>
 *   <li>coin_Wheel 旋钮随 dialRot(蓄力)旋转并前伸,攻击时后坐(shootProgress)压低姿态;</li>
 *   <li>自爆时 body 随 explodeProgress 压扁;行走时四腿摆动 + 身体上下起伏。</li>
 * </ul>
 * 构造参数 f 与 AC 原版一致:主模型用 0.0F,蓄电能量外圈(swirl)复制体用 1.0F(几何外扩、无糖球层)。
 */
@OnlyIn(Dist.CLIENT)
public class ModelGumbeeperServant extends AdvancedEntityModel<GumbeeperServant> {

    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox coin_Wheel;
    private final AdvancedModelBox right_backLeg;
    private final AdvancedModelBox left_backLeg;
    private final AdvancedModelBox right_frontLeg;
    private final AdvancedModelBox left_frontLeg;
    private final AdvancedModelBox head;
    private final AdvancedModelBox gum_layers;
    private final AdvancedModelBox gum_layerFinal;
    private final AdvancedModelBox gum_layer6;
    private final AdvancedModelBox gum_layer5;
    private final AdvancedModelBox gum_layer4;
    private final AdvancedModelBox gum_layer3;
    private final AdvancedModelBox gum_layer2;
    private final AdvancedModelBox gum_layer;

    public ModelGumbeeperServant(float f) {
        this.texWidth = 128;
        this.texHeight = 128;
        this.root = new AdvancedModelBox(this);
        this.root.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.body = new AdvancedModelBox(this);
        this.body.setRotationPoint(1.0F, -5.0F, 0.0F);
        this.root.addChild(this.body);
        this.body.setTextureOffset(16, 49).addBox(-5.0F, -7.0F, -4.0F, 8.0F, 9.0F, 8.0F, f + 0.25F, false);
        this.body.setTextureOffset(40, 40).addBox(-5.0F, -7.0F, -4.0F, 8.0F, 9.0F, 8.0F, f, false);
        this.coin_Wheel = new AdvancedModelBox(this);
        this.coin_Wheel.setRotationPoint(-1.0F, -4.0F, -5.0F);
        this.body.addChild(this.coin_Wheel);
        this.coin_Wheel.setTextureOffset(0, 0).addBox(0.0F, -2.0F, -1.0F, 0.0F, 4.0F, 2.0F, f, false);
        this.right_backLeg = new AdvancedModelBox(this);
        this.right_backLeg.setRotationPoint(-4.0F, 1.0F, 3.0F);
        this.body.addChild(this.right_backLeg);
        this.right_backLeg.setTextureOffset(52, 26).addBox(-3.0F, -1.0F, -1.0F, 4.0F, 5.0F, 4.0F, f, true);
        this.left_backLeg = new AdvancedModelBox(this);
        this.left_backLeg.setRotationPoint(2.0F, 1.0F, 3.0F);
        this.body.addChild(this.left_backLeg);
        this.left_backLeg.setTextureOffset(52, 26).addBox(-1.0F, -1.0F, -1.0F, 4.0F, 5.0F, 4.0F, f, false);
        this.right_frontLeg = new AdvancedModelBox(this);
        this.right_frontLeg.setRotationPoint(-4.0F, 1.0F, -3.0F);
        this.body.addChild(this.right_frontLeg);
        this.right_frontLeg.setTextureOffset(36, 26).addBox(-3.0F, -1.0F, -3.0F, 4.0F, 5.0F, 4.0F, f, true);
        this.left_frontLeg = new AdvancedModelBox(this);
        this.left_frontLeg.setRotationPoint(2.0F, 1.0F, -3.0F);
        this.body.addChild(this.left_frontLeg);
        this.left_frontLeg.setTextureOffset(36, 26).addBox(-1.0F, -1.0F, -3.0F, 4.0F, 5.0F, 4.0F, f, false);
        this.head = new AdvancedModelBox(this);
        this.head.setRotationPoint(-1.0F, -7.0F, 0.0F);
        this.body.addChild(this.head);
        this.head.setTextureOffset(0, 24).addBox(-6.0F, -14.0F, -6.0F, 12.0F, 12.0F, 12.0F, f, false);
        this.head.setTextureOffset(36, 12).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 2.0F, 12.0F, f, false);
        this.head.setTextureOffset(36, 0).addBox(-4.0F, -17.0F, -4.0F, 8.0F, 3.0F, 8.0F, f, false);
        this.gum_layers = new AdvancedModelBox(this);
        this.gum_layers.setRotationPoint(-0.5F, -7.75F, -0.5F);
        this.head.addChild(this.gum_layers);
        this.gum_layerFinal = new AdvancedModelBox(this);
        this.gum_layerFinal.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.gum_layers.addChild(this.gum_layerFinal);
        if (f <= 0.0F) {
            this.gum_layerFinal.setTextureOffset(2, 7).addBox(-5.0F, 0.25F, -5.0F, 11.0F, 6.0F, 11.0F, f, false);
        }
        this.gum_layer6 = new AdvancedModelBox(this);
        this.gum_layer6.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.gum_layers.addChild(this.gum_layer6);
        if (f <= 0.0F) {
            this.gum_layer6.setTextureOffset(2, 6).addBox(-5.0F, 2.25F, -5.0F, 11.0F, 1.0F, 11.0F, f, false);
        }
        this.gum_layer5 = new AdvancedModelBox(this);
        this.gum_layer5.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.gum_layers.addChild(this.gum_layer5);
        if (f <= 0.0F) {
            this.gum_layer5.setTextureOffset(2, 5).addBox(-5.0F, 2.25F, -5.0F, 11.0F, 1.0F, 11.0F, f, false);
        }
        this.gum_layer4 = new AdvancedModelBox(this);
        this.gum_layer4.setRotationPoint(0.0F, -5.0F, 0.0F);
        this.gum_layers.addChild(this.gum_layer4);
        if (f <= 0.0F) {
            this.gum_layer4.setTextureOffset(2, 4).addBox(-5.0F, 2.25F, -5.0F, 11.0F, 1.0F, 11.0F, f, false);
        }
        this.gum_layer3 = new AdvancedModelBox(this);
        this.gum_layer3.setRotationPoint(0.0F, -6.0F, 0.0F);
        this.gum_layers.addChild(this.gum_layer3);
        if (f <= 0.0F) {
            this.gum_layer3.setTextureOffset(2, 3).addBox(-5.0F, 2.25F, -5.0F, 11.0F, 1.0F, 11.0F, f, false);
        }
        this.gum_layer2 = new AdvancedModelBox(this);
        this.gum_layer2.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.gum_layers.addChild(this.gum_layer2);
        if (f <= 0.0F) {
            this.gum_layer2.setTextureOffset(2, 2).addBox(-5.0F, 2.25F, -5.0F, 11.0F, 1.0F, 11.0F, f, false);
        }
        this.gum_layer = new AdvancedModelBox(this);
        this.gum_layer.setRotationPoint(0.0F, -8.0F, 0.0F);
        this.gum_layers.addChild(this.gum_layer);
        if (f <= 0.0F) {
            this.gum_layer.setTextureOffset(2, 1).addBox(-5.0F, 2.25F, -5.0F, 11.0F, 1.0F, 11.0F, f, false);
        }
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.root, this.body, this.head, this.gum_layer, this.gum_layers, this.gum_layer2,
                this.gum_layer3, this.gum_layer4, this.gum_layer5, this.gum_layer6, this.gum_layerFinal, this.coin_Wheel,
                this.left_backLeg, this.left_frontLeg, this.right_backLeg, this.right_frontLeg);
    }

    @Override
    public void setupAnim(GumbeeperServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float walkSpeed = 0.8F;
        float walkDegree = 1.2F;
        float partialTick = ageInTicks - (float) entity.tickCount;
        float explodeProgress = entity.getExplodeProgress(partialTick);
        float dialRot = (float) Math.toRadians(entity.getDialRot(partialTick));
        float dialRotFinalStretch = Math.max(dialRot - 400.0F, 0.0F) / 50.0F;
        float shootProgress = entity.getShootProgress(partialTick);
        int gumballsLeft = entity.getGumballsLeft();
        this.gum_layer6.showModel = gumballsLeft > 0;
        this.gum_layer5.showModel = gumballsLeft > 1;
        this.gum_layer4.showModel = gumballsLeft > 2;
        this.gum_layer3.showModel = gumballsLeft > 3;
        this.gum_layer2.showModel = gumballsLeft > 4;
        this.gum_layer.showModel = gumballsLeft > 5;
        this.progressRotationPrev(this.body, shootProgress, (float) Math.toRadians(-15.0D), 0.0F, 0.0F, 1.0F);
        this.progressRotationPrev(this.head, shootProgress, (float) Math.toRadians(-5.0D), 0.0F, 0.0F, 1.0F);
        this.progressRotationPrev(this.left_backLeg, shootProgress, (float) Math.toRadians(15.0D), 0.0F, 0.0F, 1.0F);
        this.progressRotationPrev(this.right_backLeg, shootProgress, (float) Math.toRadians(15.0D), 0.0F, 0.0F, 1.0F);
        this.progressRotationPrev(this.left_frontLeg, shootProgress, (float) Math.toRadians(15.0D), 0.0F, 0.0F, 1.0F);
        this.progressRotationPrev(this.right_frontLeg, shootProgress, (float) Math.toRadians(15.0D), 0.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.body, shootProgress, 0.0F, -1.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.head, shootProgress, 0.0F, 1.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.right_frontLeg, shootProgress, 0.0F, 2.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.left_frontLeg, shootProgress, 0.0F, 2.0F, 0.0F, 1.0F);
        this.coin_Wheel.rotateAngleZ += dialRot;
        this.coin_Wheel.rotationPointZ += dialRotFinalStretch * 1.5F;
        this.body.setScale(1.0F + explodeProgress * 0.15F, 1.0F - explodeProgress * 0.2F, 1.0F + explodeProgress * 0.15F);
        this.body.scaleChildren = true;
        float bodyBob = ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed * 1.5F, 0.5F, 0.5F, true);
        this.body.rotationPointY += bodyBob;
        this.walk(this.right_frontLeg, walkSpeed, walkDegree * 0.4F, true, 2.5F, 0.2F, limbSwing, limbSwingAmount);
        this.right_frontLeg.rotationPointY += Math.min(0.0F, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 0.0F, 4.0F, false)) - bodyBob;
        this.walk(this.left_frontLeg, walkSpeed, walkDegree * 0.4F, false, 2.5F, -0.2F, limbSwing, limbSwingAmount);
        this.left_frontLeg.rotationPointY += Math.min(0.0F, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 0.0F, 4.0F, true)) - bodyBob;
        this.walk(this.right_backLeg, walkSpeed, walkDegree * 0.4F, false, 2.5F, 0.2F, limbSwing, limbSwingAmount);
        this.right_backLeg.rotationPointY += Math.min(0.0F, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 0.0F, 4.0F, true)) - bodyBob;
        this.walk(this.left_backLeg, walkSpeed, walkDegree * 0.4F, true, 2.5F, -0.2F, limbSwing, limbSwingAmount);
        this.left_backLeg.rotationPointY += Math.min(0.0F, ACMath.walkValue(limbSwing, limbSwingAmount, walkSpeed, 0.0F, 4.0F, false)) - bodyBob;
        this.swing(this.body, 3.0F, 0.2F, true, 1.0F, 0.0F, ageInTicks, explodeProgress);
        this.swing(this.head, 1.3F, 0.1F, true, 1.0F, 0.0F, ageInTicks, shootProgress);
    }
}
