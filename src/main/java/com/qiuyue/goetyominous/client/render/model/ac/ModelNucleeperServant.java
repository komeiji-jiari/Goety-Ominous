package com.qiuyue.goetyominous.client.render.model.ac;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.common.entities.ally.ac.NucleeperServant;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector4f;

@OnlyIn(Dist.CLIENT)
public class ModelNucleeperServant extends AdvancedEntityModel<NucleeperServant> {
    private final AdvancedModelBox base;
    private final AdvancedModelBox coreTop;
    private final AdvancedModelBox head;
    private final AdvancedModelBox rpupil;
    private final AdvancedModelBox lpupil;
    private final AdvancedModelBox rleg2;
    private final AdvancedModelBox rfoot2;
    private final AdvancedModelBox lleg2;
    private final AdvancedModelBox lfoot2;
    private final AdvancedModelBox lleg;
    private final AdvancedModelBox lfoot1;
    private final AdvancedModelBox rleg;
    private final AdvancedModelBox rfoot;
    private final AdvancedModelBox coreBottom;

    public ModelNucleeperServant(float inflate) {
        this.texWidth = 128;
        this.texHeight = 128;

        this.base = new AdvancedModelBox(this);
        this.base.setRotationPoint(0.0F, 15.5F, 0.0F);
        this.base.setTextureOffset(80, 21).addBox(-7.0F, -36.5F, -5.0F, 14.0F, 38.0F, 10.0F, inflate, false);

        this.coreTop = new AdvancedModelBox(this);
        this.coreTop.setRotationPoint(0.0F, -62.5F, 0.0F);
        this.base.addChild(this.coreTop);
        this.coreTop.setTextureOffset(0, 0).addBox(-8.0F, 3.0F, -8.0F, 16.0F, 8.0F, 16.0F, inflate, false);

        this.head = new AdvancedModelBox(this);
        this.head.setRotationPoint(0.0F, -37.5F, 0.0F);
        this.base.addChild(this.head);
        this.head.setTextureOffset(0, 48).addBox(-7.0F, -14.0F, -7.0F, 14.0F, 14.0F, 14.0F, inflate, false);
        this.head.setTextureOffset(26, 86).addBox(-6.0F, -13.5F, -6.0F, 12.0F, 12.0F, 12.0F, inflate, false);

        this.rpupil = new AdvancedModelBox(this);
        this.rpupil.setRotationPoint(-3.5F, -9.0F, -5.6F);
        this.head.addChild(this.rpupil);
        this.rpupil.setTextureOffset(26, 86).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, inflate, false);

        this.lpupil = new AdvancedModelBox(this);
        this.lpupil.setRotationPoint(3.5F, -9.0F, -5.6F);
        this.head.addChild(this.lpupil);
        this.lpupil.setTextureOffset(26, 86).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, inflate, true);

        this.rleg2 = new AdvancedModelBox(this);
        this.rleg2.setRotationPoint(-7.0F, -1.0F, 3.5F);
        this.base.addChild(this.rleg2);
        this.rleg2.setTextureOffset(80, 69).addBox(-2.0F, -2.5F, -3.5F, 6.0F, 5.0F, 12.0F, inflate, false);

        this.rfoot2 = new AdvancedModelBox(this);
        this.rfoot2.setRotationPoint(1.0F, -2.5F, 4.5F);
        this.rleg2.addChild(this.rfoot2);
        this.rfoot2.setTextureOffset(0, 93).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 12.0F, 5.0F, inflate, false);
        this.rfoot2.setTextureOffset(1, 28).addBox(0.0F, 0.0F, 5.0F, 0.0F, 6.0F, 3.0F, inflate, false);

        this.lleg2 = new AdvancedModelBox(this);
        this.lleg2.setRotationPoint(7.0F, -1.0F, 3.5F);
        this.base.addChild(this.lleg2);
        this.lleg2.setTextureOffset(80, 69).addBox(-4.0F, -2.5F, -3.5F, 6.0F, 5.0F, 12.0F, inflate, true);

        this.lfoot2 = new AdvancedModelBox(this);
        this.lfoot2.setRotationPoint(-1.0F, -2.5F, 4.5F);
        this.lleg2.addChild(this.lfoot2);
        this.lfoot2.setTextureOffset(0, 93).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 12.0F, 5.0F, inflate, true);
        this.lfoot2.setTextureOffset(1, 28).addBox(0.0F, 0.0F, 5.0F, 0.0F, 6.0F, 3.0F, inflate, true);

        this.lleg = new AdvancedModelBox(this);
        this.lleg.setRotationPoint(7.0F, -1.0F, -3.5F);
        this.base.addChild(this.lleg);
        this.lleg.setTextureOffset(44, 69).addBox(-4.0F, -2.5F, -8.5F, 6.0F, 5.0F, 12.0F, inflate, true);

        this.lfoot1 = new AdvancedModelBox(this);
        this.lfoot1.setRotationPoint(-1.0F, -2.5F, -4.5F);
        this.lleg.addChild(this.lfoot1);
        this.lfoot1.setTextureOffset(0, 76).addBox(-4.0F, 0.0F, -5.0F, 8.0F, 12.0F, 5.0F, inflate, true);
        this.lfoot1.setTextureOffset(0, 46).addBox(0.0F, 0.0F, -8.0F, 0.0F, 6.0F, 5.0F, inflate, true);

        this.rleg = new AdvancedModelBox(this);
        this.rleg.setRotationPoint(-7.0F, -1.0F, -3.5F);
        this.base.addChild(this.rleg);
        this.rleg.setTextureOffset(44, 69).addBox(-2.0F, -2.5F, -8.5F, 6.0F, 5.0F, 12.0F, inflate, false);

        this.rfoot = new AdvancedModelBox(this);
        this.rfoot.setRotationPoint(1.0F, -2.5F, -4.5F);
        this.rleg.addChild(this.rfoot);
        this.rfoot.setTextureOffset(0, 76).addBox(-4.0F, 0.0F, -5.0F, 8.0F, 12.0F, 5.0F, inflate, false);
        this.rfoot.setTextureOffset(0, 46).addBox(0.0F, 0.0F, -8.0F, 0.0F, 6.0F, 5.0F, inflate, false);

        this.coreBottom = new AdvancedModelBox(this);
        this.coreBottom.setRotationPoint(0.0F, -27.5F, 0.0F);
        this.base.addChild(this.coreBottom);
        this.coreBottom.setTextureOffset(0, 24).addBox(-8.0F, -11.0F, -8.0F, 16.0F, 8.0F, 16.0F, inflate, false);

        this.updateDefaultPose();
    }

    @Override
    public void setupAnim(NucleeperServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float speed = 0.8F;
        float degree = 1.2F;
        float partialTick = ageInTicks - entity.tickCount;
        float closeProgress = entity.getCloseProgress(partialTick);
        float explodeProgress = entity.getExplodeProgress(partialTick);
        float chargeProgress = 1 - limbSwingAmount;

        this.progressPositionPrev(head, closeProgress, 0, 8.0F, 0, 1);
        this.progressPositionPrev(coreTop, closeProgress, 0, 14.0F, 0, 1);
        this.progressPositionPrev(coreBottom, closeProgress, 0, 1, 0, 1);

        this.progressRotationPrev(lleg, chargeProgress, 0, toRad(-25.0F), 0, 1);
        this.progressRotationPrev(lleg2, chargeProgress, 0, toRad(25.0F), 0, 1);
        this.progressRotationPrev(rleg, chargeProgress, 0, toRad(25.0F), 0, 1);
        this.progressRotationPrev(rleg2, chargeProgress, 0, toRad(-25.0F), 0, 1);

        base.setScale(1 - 0.15F * explodeProgress, 1 - 0.65F * explodeProgress, 1 - 0.15F * explodeProgress);
        base.scaleChildren = true;

        this.base.flap(speed, degree * 0.1F, true, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.lleg.flap(speed, degree * 0.1F, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.rleg.flap(speed, degree * 0.1F, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.lleg2.flap(speed, degree * 0.1F, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);
        this.rleg2.flap(speed, degree * 0.1F, false, 1.0F, 0.0F, limbSwing, limbSwingAmount);

        float bob = this.walkValue(limbSwing, limbSwingAmount, speed * 1.5F, 0.5F, 2.4F, true);
        this.base.rotationPointY += bob;

        this.lleg.walk(speed, degree * 0.3F, false, 1.0F, -0.2F, limbSwing, limbSwingAmount);
        this.lfoot1.walk(speed, degree * 0.2F, false, 3.0F, 0.2F, limbSwing, limbSwingAmount);
        this.lleg.rotationPointY += Math.min(0.0F, this.walkValue(limbSwing, limbSwingAmount, speed, -0.5F, 5.0F, true)) - bob;
        this.lleg.rotationPointZ += this.walkValue(limbSwing, limbSwingAmount, speed, -0.5F, 2.5F, true);

        this.rleg.walk(speed, degree * 0.3F, true, 1.0F, 0.2F, limbSwing, limbSwingAmount);
        this.rfoot.walk(speed, degree * 0.2F, true, 3.0F, -0.2F, limbSwing, limbSwingAmount);
        this.rleg.rotationPointY += Math.min(0.0F, this.walkValue(limbSwing, limbSwingAmount, speed, -0.5F, 5.0F, false)) - bob;
        this.rleg.rotationPointZ += this.walkValue(limbSwing, limbSwingAmount, speed, -0.5F, 2.5F, false);

        this.rleg2.walk(speed, degree * 0.3F, false, 0.0F, 0.2F, limbSwing, limbSwingAmount);
        this.rfoot2.walk(speed, degree * 0.2F, false, 2.0F, -0.2F, limbSwing, limbSwingAmount);
        this.rleg2.rotationPointY += Math.min(0.0F, this.walkValue(limbSwing, limbSwingAmount, speed, -0.5F, 5.0F, true)) - bob;
        this.rleg2.rotationPointZ += this.walkValue(limbSwing, limbSwingAmount, speed, -1.5F, 2.5F, true);

        this.lleg2.walk(speed, degree * 0.3F, true, 0.0F, -0.2F, limbSwing, limbSwingAmount);
        this.lfoot2.walk(speed, degree * 0.2F, true, 2.0F, 0.2F, limbSwing, limbSwingAmount);
        this.lleg2.rotationPointY += Math.min(0.0F, this.walkValue(limbSwing, limbSwingAmount, speed, -0.5F, 5.0F, false)) - bob;
        this.lleg2.rotationPointZ += this.walkValue(limbSwing, limbSwingAmount, speed, -1.5F, 2.5F, false);

        this.base.flap(3.0F, 0.3F, true, 1.0F, 0.0F, ageInTicks, explodeProgress);

        Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
        if (cameraEntity != null) {
            Vec3 cameraEyePos = cameraEntity.getEyePosition(0);
            Vec3 nucleeperEyePos = entity.getEyePosition(0);
            float eyeOffsetY = (float) Mth.clamp(nucleeperEyePos.y - cameraEyePos.y, -1.0D, 1.0D);
            Vec3 viewVector = new Vec3(entity.getViewVector(0).x, 0, entity.getViewVector(0).z);
            Vec3 eyeVector = new Vec3(nucleeperEyePos.x - cameraEyePos.x, 0, nucleeperEyePos.z - cameraEyePos.z).normalize().yRot(1.5707964F);
            double dot = viewVector.dot(eyeVector);
            double rotation = Math.signum(dot) * Mth.sqrt((float) Math.abs(dot));
            lpupil.rotationPointX += (float) (rotation - base.rotateAngleZ);
            lpupil.rotationPointY += eyeOffsetY;
            rpupil.rotationPointX += (float) (rotation - base.rotateAngleZ);
            rpupil.rotationPointY += eyeOffsetY;
        }
    }

    public Vec3 getSirenPosition(Vec3 vec3) {
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        this.base.translateAndRotate(poseStack);
        this.head.translateAndRotate(poseStack);
        Vector4f vector4f = new Vector4f((float) vec3.x, (float) vec3.y, (float) vec3.z, 1.0F);
        vector4f.mul(poseStack.last().pose());
        Vec3 result = new Vec3(vector4f.x(), (double) (-vector4f.y()), vector4f.z());
        poseStack.popPose();
        return result.add(0.0D, 1.5D, 0.0D);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.base);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.base, this.coreBottom, this.coreTop, this.head, this.rpupil, this.lpupil, this.lleg, this.lleg2, this.rleg, this.rleg2, this.rfoot, this.rfoot2, this.lfoot2, this.lfoot1);
    }

    private float walkValue(float limbSwing, float limbSwingAmount, float speed, float offset, float weight, boolean invert) {
        return (float) (Math.cos(limbSwing * speed + offset) * weight * limbSwingAmount * (invert ? -1 : 1));
    }

    private static float toRad(float degrees) {
        return degrees * Mth.DEG_TO_RAD;
    }
}
