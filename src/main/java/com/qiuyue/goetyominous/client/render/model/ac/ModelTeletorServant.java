package com.qiuyue.goetyominous.client.render.model.ac;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.entities.ally.ac.TeletorServant;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector4f;

/**
 * 传送使徒仆从模型:逐字移植 AC TeletorModel(Citadel 几何),保证与原版外观一致。
 * 动画驱动:
 * <ul>
 *   <li>legsCrossed 时切换为交叉悬浮腿(隐藏两条竖直细腿);</li>
 *   <li>controlProgress 控制双臂抬举/挥舞以引导武器、躯干与头微俯;</li>
 *   <li>悬浮时整体缓慢 bob/摆动;命中目标时双臂/躯干朝向武器所在方向回正;</li>
 *   <li>translateToHead() 供渲染器把世界/模型偏移换算成头部空间位置(用于绘制牵引闪电与轨迹)。</li>
 * </ul>
 */
@OnlyIn(Dist.CLIENT)
public class ModelTeletorServant extends AdvancedEntityModel<TeletorServant> {
    private final AdvancedModelBox body;
    private final AdvancedModelBox head;
    private final AdvancedModelBox rarm;
    private final AdvancedModelBox larm;
    private final AdvancedModelBox rarmPivot;
    private final AdvancedModelBox larmPivot;
    private final AdvancedModelBox rlegcrossed;
    private final AdvancedModelBox llegcrossed;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox lleg;

    public ModelTeletorServant() {
        this.texWidth = 128;
        this.texHeight = 128;
        this.body = new AdvancedModelBox(this);
        this.body.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.body.setTextureOffset(40, 37).addBox(-3.0F, -16.0F, -2.0F, 6.0F, 6.0F, 4.0F, 0.0F, false);
        this.head = new AdvancedModelBox(this);
        this.head.setRotationPoint(0.0F, -16.0F, 0.0F);
        this.body.addChild(this.head);
        this.head.setTextureOffset(0, 0).addBox(-6.0F, -18.0F, -6.0F, 12.0F, 17.0F, 12.0F, 0.0F, false);
        this.head.setTextureOffset(40, 29).addBox(6.0F, -9.0F, -2.0F, 9.0F, 4.0F, 4.0F, 0.0F, false);
        this.head.setTextureOffset(20, 29).addBox(9.0F, -23.0F, -2.0F, 6.0F, 14.0F, 4.0F, 0.0F, false);
        this.head.setTextureOffset(40, 29).addBox(-15.0F, -9.0F, -2.0F, 9.0F, 4.0F, 4.0F, 0.0F, true);
        this.head.setTextureOffset(0, 29).addBox(-15.0F, -23.0F, -2.0F, 6.0F, 14.0F, 4.0F, 0.0F, false);
        this.rarmPivot = new AdvancedModelBox(this);
        this.rarmPivot.setRotationPoint(-3.0F, -16.0F, 0.0F);
        this.body.addChild(this.rarmPivot);
        this.rarm = new AdvancedModelBox(this);
        this.rarmPivot.addChild(this.rarm);
        this.rarm.setTextureOffset(34, 8).addBox(-9.0F, 0.0F, -1.0F, 9.0F, 0.0F, 2.0F, 0.0F, true);
        this.larmPivot = new AdvancedModelBox(this);
        this.larmPivot.setRotationPoint(3.0F, -16.0F, 0.0F);
        this.body.addChild(this.larmPivot);
        this.larm = new AdvancedModelBox(this);
        this.larmPivot.addChild(this.larm);
        this.larm.setTextureOffset(34, 8).addBox(0.0F, 0.0F, -1.0F, 9.0F, 0.0F, 2.0F, 0.0F, false);
        this.rlegcrossed = new AdvancedModelBox(this);
        this.rlegcrossed.setRotationPoint(-1.5F, -10.0F, 0.0F);
        this.body.addChild(this.rlegcrossed);
        this.rlegcrossed.setTextureOffset(0, 47).addBox(-1.5F, 0.0F, -5.0F, 3.0F, 2.0F, 5.0F, 0.0F, true);
        this.llegcrossed = new AdvancedModelBox(this);
        this.llegcrossed.setRotationPoint(1.5F, -9.0F, -3.0F);
        this.body.addChild(this.llegcrossed);
        this.llegcrossed.setTextureOffset(0, 47).addBox(-1.5F, -1.0F, -2.0F, 3.0F, 2.0F, 5.0F, 0.0F, false);
        this.rleg = new AdvancedModelBox(this);
        this.rleg.setRotationPoint(-2.0F, -10.0F, 0.0F);
        this.body.addChild(this.rleg);
        this.rleg.setTextureOffset(4, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 6.0F, 0.0F, 0.0F, false);
        this.lleg = new AdvancedModelBox(this);
        this.lleg.setRotationPoint(2.0F, -10.0F, 0.0F);
        this.body.addChild(this.lleg);
        this.lleg.setTextureOffset(4, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 6.0F, 0.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.body, this.head, this.larm, this.lleg, this.llegcrossed, this.rarm, this.rleg, this.rlegcrossed, this.rarmPivot, this.larmPivot);
    }

    @Override
    public void setupAnim(TeletorServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        if (entity.areLegsCrossed(limbSwingAmount)) {
            this.lleg.showModel = false;
            this.rleg.showModel = false;
            this.llegcrossed.showModel = true;
            this.rlegcrossed.showModel = true;
        } else {
            this.lleg.showModel = true;
            this.rleg.showModel = true;
            this.llegcrossed.showModel = false;
            this.rlegcrossed.showModel = false;
        }
        float partialTick = ageInTicks - entity.tickCount;
        float controlProgress = entity.getControlProgress(partialTick);
        this.progressRotationPrev(this.body, limbSwingAmount, (float) Math.toRadians(20.0D), 0.0F, 0.0F, 1.0F);
        this.progressRotationPrev(this.head, limbSwingAmount, (float) Math.toRadians(-10.0D), 0.0F, 0.0F, 1.0F);
        this.progressRotationPrev(this.head, controlProgress * limbSwingAmount, (float) Math.toRadians(-10.0D), 0.0F, 0.0F, 1.0F);
        this.progressRotationPrev(this.lleg, limbSwingAmount, (float) Math.toRadians(40.0D), 0.0F, 0.0F, 1.0F);
        this.progressRotationPrev(this.rleg, limbSwingAmount, (float) Math.toRadians(40.0D), 0.0F, 0.0F, 1.0F);
        this.progressRotationPrev(this.rarm, limbSwingAmount, (float) Math.toRadians(40.0D), (float) Math.toRadians(40.0D), 0.0F, 1.0F);
        this.progressRotationPrev(this.larm, limbSwingAmount, (float) Math.toRadians(40.0D), (float) Math.toRadians(-40.0D), 0.0F, 1.0F);
        this.progressRotationPrev(this.rarm, controlProgress * limbSwingAmount, (float) Math.toRadians(-60.0D), (float) Math.toRadians(-40.0D), (float) Math.toRadians(60.0D), 1.0F);
        this.progressRotationPrev(this.larm, controlProgress * limbSwingAmount, (float) Math.toRadians(-60.0D), (float) Math.toRadians(40.0D), (float) Math.toRadians(-60.0D), 1.0F);
        this.progressPositionPrev(this.body, limbSwingAmount, 0.0F, 1.0F, 2.0F, 1.0F);
        this.progressPositionPrev(this.head, limbSwingAmount, 0.0F, -1.0F, -2.0F, 1.0F);
        this.progressPositionPrev(this.rarm, limbSwingAmount, -1.0F, 1.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.larm, limbSwingAmount, 1.0F, 1.0F, 0.0F, 1.0F);
        this.progressPositionPrev(this.rarm, controlProgress, -1.0F, 1.0F, -2.0F, 1.0F);
        this.progressPositionPrev(this.larm, controlProgress, 1.0F, 1.0F, -2.0F, 1.0F);
        this.progressRotationPrev(this.rarm, controlProgress, (float) Math.toRadians(-10.0D), (float) Math.toRadians(-90.0D), 0.0F, 1.0F);
        this.progressRotationPrev(this.larm, controlProgress, (float) Math.toRadians(-10.0D), (float) Math.toRadians(90.0D), 0.0F, 1.0F);
        this.bob(this.body, 0.1F, 2.0F, false, ageInTicks, 1.0F);
        this.bob(this.head, 0.1F, 1.0F, false, ageInTicks, 1.0F);
        this.flap(this.larm, 0.1F, 0.2F, true, -1.0F, -0.4F, ageInTicks, 1.0F);
        this.flap(this.rarm, 0.1F, 0.2F, false, -1.0F, -0.4F, ageInTicks, 1.0F);
        this.swing(this.llegcrossed, 0.1F, 0.2F, false, -2.0F, 0.0F, ageInTicks, 1.0F);
        this.swing(this.rlegcrossed, 0.1F, 0.2F, true, -2.0F, 0.0F, ageInTicks, 1.0F);
        this.swing(this.rarm, 0.5F, 0.2F, false, 2.0F, 0.0F, ageInTicks, controlProgress);
        this.swing(this.larm, 0.5F, 0.2F, true, 2.0F, 0.0F, ageInTicks, controlProgress);
        this.rarm.rotationPointZ -= (float) (Math.sin(ageInTicks * 0.5F) * controlProgress);
        this.rarm.rotationPointX -= (float) (Math.sin(ageInTicks * 0.5F + 2.0F) * controlProgress * 0.5D);
        this.larm.rotationPointZ -= (float) (Math.sin(ageInTicks * 0.5F) * controlProgress);
        this.larm.rotationPointX += (float) (Math.sin(ageInTicks * 0.5F + 2.0F) * controlProgress * 0.5D);
        this.faceTarget(netHeadYaw, headPitch, 1.0F, new AdvancedModelBox[]{this.head});
        Entity look = entity.getWeapon();
        if (look != null) {
            Vec3 vector3d = look.getEyePosition(partialTick);
            Vec3 vector3d1 = entity.getEyePosition(partialTick);
            double d0 = Mth.clamp((vector3d.y - vector3d1.y) * 0.5D, -1.0D, 1.0D) * Math.PI / 2.0D;
            Vec3 vector3d2 = entity.getViewVector(0.0F);
            vector3d2 = new Vec3(vector3d2.x, 0.0D, vector3d2.z);
            Vec3 vector3d3 = new Vec3(vector3d.x - vector3d1.x, 0.0D, vector3d.z - vector3d1.z).normalize().yRot(1.5707964F);
            double d1 = vector3d2.dot(vector3d3);
            this.rarmPivot.rotateAngleX -= (float) (d0 * controlProgress);
            this.larmPivot.rotateAngleX -= (float) (d0 * controlProgress);
            this.rarmPivot.rotateAngleY += (float) (d1 * controlProgress);
            this.larmPivot.rotateAngleY += (float) (d1 * controlProgress);
            if (d0 > 0.0D) {
                this.head.rotationPointY -= (float) (d0 * controlProgress * 5.0D);
            }
        }
    }

    public Vec3 translateToHead(Vec3 in, float yawIn) {
        PoseStack modelTranslateStack = new PoseStack();
        modelTranslateStack.mulPose(Axis.YP.rotationDegrees(180.0F - yawIn));
        modelTranslateStack.translate(this.body.rotationPointX / 16.0F, this.body.rotationPointY / 16.0F, this.body.rotationPointZ / 16.0F);
        modelTranslateStack.mulPose(Axis.ZN.rotation(this.body.rotateAngleZ));
        modelTranslateStack.mulPose(Axis.YN.rotation(this.body.rotateAngleY));
        modelTranslateStack.mulPose(Axis.XN.rotation(this.body.rotateAngleX));
        modelTranslateStack.translate(this.head.rotationPointX / 16.0F, this.head.rotationPointY / 16.0F, this.head.rotationPointZ / 16.0F);
        modelTranslateStack.mulPose(Axis.ZN.rotation(this.head.rotateAngleZ));
        modelTranslateStack.mulPose(Axis.YN.rotation(this.head.rotateAngleY));
        modelTranslateStack.mulPose(Axis.XN.rotation(this.head.rotateAngleX));
        Vector4f bodyOffsetVec = new Vector4f((float) in.x, (float) in.y, (float) in.z, 1.0F);
        bodyOffsetVec.mul(modelTranslateStack.last().pose());
        Vec3 offset = new Vec3(bodyOffsetVec.x(), bodyOffsetVec.y(), bodyOffsetVec.z());
        modelTranslateStack.popPose();
        return offset;
    }
}
