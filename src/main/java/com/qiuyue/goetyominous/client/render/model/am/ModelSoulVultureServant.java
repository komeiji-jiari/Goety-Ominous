package com.qiuyue.goetyominous.client.render.model.am;

import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.qiuyue.goetyominous.common.entities.ally.am.SoulVultureServant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSoulVultureServant extends AdvancedEntityModel<SoulVultureServant> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox leftWing;
    private final AdvancedModelBox rightWing;
    private final AdvancedModelBox heart;
    private final AdvancedModelBox leftLeg;
    private final AdvancedModelBox leftFoot;
    private final AdvancedModelBox rightLeg;
    private final AdvancedModelBox rightFoot;
    private final AdvancedModelBox neck;
    private final AdvancedModelBox head;

    public ModelSoulVultureServant() {
        texWidth = 128;
        texHeight = 128;

        root = new AdvancedModelBox(this, "root");
        root.setPos(0.0F, 24.0F, 0.0F);

        body = new AdvancedModelBox(this, "body");
        body.setPos(0.0F, -5.0F, 4.0F);
        root.addChild(body);
        this.setRotationAngle(body, -0.3054F, 0.0F, 0.0F);
        body.setTextureOffset(0, 15).addBox(-3.0F, -6.0F, -9.0F, 6.0F, 6.0F, 10.0F, 0.0F, false);
        body.setTextureOffset(26, 25).addBox(-3.5F, -6.5F, -9.2F, 7.0F, 6.0F, 7.0F, 0.0F, false);

        leftWing = new AdvancedModelBox(this, "leftWing");
        leftWing.setPos(3.0F, -2.0F, -7.0F);
        body.addChild(leftWing);
        this.setRotationAngle(leftWing, 1.1345F, -1.3265F, 0.3491F);
        leftWing.setTextureOffset(36, 15).addBox(-1.0F, -1.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, false);
        leftWing.setTextureOffset(0, 0).addBox(-1.0F, 0.0F, 0.0F, 24.0F, 0.0F, 14.0F, 0.0F, false);

        rightWing = new AdvancedModelBox(this, "rightWing");
        rightWing.setPos(-3.0F, -2.0F, -7.0F);
        body.addChild(rightWing);
        this.setRotationAngle(rightWing, 1.1345F, 1.3265F, -0.3491F);
        rightWing.setTextureOffset(36, 15).addBox(-6.0F, -1.0F, -1.0F, 7.0F, 2.0F, 2.0F, 0.0F, true);
        rightWing.setTextureOffset(0, 0).addBox(-23.0F, 0.0F, 0.0F, 24.0F, 0.0F, 14.0F, 0.0F, true);

        heart = new AdvancedModelBox(this, "heart");
        heart.setPos(0.0F, 0.0F, -6.0F);
        body.addChild(heart);
        heart.setTextureOffset(0, 15).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        leftLeg = new AdvancedModelBox(this, "leftLeg");
        leftLeg.setPos(3.0F, 0.0F, 0.0F);
        body.addChild(leftLeg);
        this.setRotationAngle(leftLeg, 0.7418F, 0.0F, 0.0F);
        leftLeg.setTextureOffset(0, 6).addBox(-1.0F, -1.0F, -4.0F, 1.0F, 4.0F, 4.0F, 0.0F, false);

        leftFoot = new AdvancedModelBox(this, "leftFoot");
        leftFoot.setPos(0.0F, 3.0F, -4.0F);
        leftLeg.addChild(leftFoot);
        this.setRotationAngle(leftFoot, -0.4363F, 0.0F, 0.0F);
        leftFoot.setTextureOffset(0, 0).addBox(-2.0F, 0.0F, -2.0F, 3.0F, 2.0F, 3.0F, 0.0F, false);

        rightLeg = new AdvancedModelBox(this, "rightLeg");
        rightLeg.setPos(-3.0F, 0.0F, 0.0F);
        body.addChild(rightLeg);
        this.setRotationAngle(rightLeg, 0.7418F, 0.0F, 0.0F);
        rightLeg.setTextureOffset(0, 6).addBox(0.0F, -1.0F, -4.0F, 1.0F, 4.0F, 4.0F, 0.0F, true);

        rightFoot = new AdvancedModelBox(this, "rightFoot");
        rightFoot.setPos(0.0F, 3.0F, -4.0F);
        rightLeg.addChild(rightFoot);
        this.setRotationAngle(rightFoot, -0.4363F, 0.0F, 0.0F);
        rightFoot.setTextureOffset(0, 0).addBox(-1.0F, 0.0F, -2.0F, 3.0F, 2.0F, 3.0F, 0.0F, true);

        neck = new AdvancedModelBox(this, "neck");
        neck.setPos(0.0F, -2.5F, -10.0F);
        body.addChild(neck);
        this.setRotationAngle(neck, 0.48F, 0.0F, 0.0F);
        neck.setTextureOffset(17, 39).addBox(-1.5F, -1.5F, -7.0F, 3.0F, 3.0F, 8.0F, 0.0F, false);

        head = new AdvancedModelBox(this, "head");
        head.setPos(0.0F, -2.5F, -6.0F);
        neck.addChild(head);
        this.setRotationAngle(head, -0.1745F, 0.0F, 0.0F);
        head.setTextureOffset(0, 32).addBox(-2.5F, -3.0F, -5.0F, 5.0F, 4.0F, 7.0F, 0.0F, false);
        head.setTextureOffset(23, 15).addBox(-1.5F, -2.0F, -11.0F, 3.0F, 3.0F, 6.0F, 0.0F, false);
        head.setTextureOffset(32, 39).addBox(-1.5F, 1.0F, -11.0F, 3.0F, 1.0F, 2.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, neck, heart, head, rightFoot, leftFoot, rightWing, leftWing, leftLeg, rightLeg);
    }

    @Override
    public void setupAnim(SoulVultureServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float idleSpeed = 0.1F;
        float idleDegree = 0.1F;
        float flapSpeed = 0.4F;
        float flapDegree = 0.2F;
        float walkSpeed = 0.7F;
        float walkDegree = 0.4F;
        float partialTick = ageInTicks - (float) entity.tickCount;
        float flyProgress = entity.prevFlyProgress + (entity.flyProgress - entity.prevFlyProgress) * partialTick;
        float tackleProgress = entity.prevTackleProgress + (entity.tackleProgress - entity.prevTackleProgress) * partialTick;
        this.progressRotationPrev(body, flyProgress, Maths.rad(15.0), 0.0F, 0.0F, 5.0F);
        this.progressRotationPrev(neck, flyProgress, Maths.rad(-35.0), 0.0F, 0.0F, 5.0F);
        this.progressRotationPrev(head, flyProgress, Maths.rad(25.0), 0.0F, 0.0F, 5.0F);
        this.progressRotationPrev(leftLeg, flyProgress, Maths.rad(55.0), 0.0F, 0.0F, 5.0F);
        this.progressRotationPrev(rightLeg, flyProgress, Maths.rad(55.0), 0.0F, 0.0F, 5.0F);
        this.progressRotationPrev(rightWing, flyProgress, Maths.rad(-70.0), Maths.rad(-90.0), 0.0F, 5.0F);
        this.progressRotationPrev(leftWing, flyProgress, Maths.rad(-70.0), Maths.rad(90.0), 0.0F, 5.0F);
        this.progressPositionPrev(rightWing, flyProgress, 0.0F, -2.0F, -1.0F, 5.0F);
        this.progressPositionPrev(leftWing, flyProgress, 0.0F, -2.0F, -1.0F, 5.0F);
        this.progressPositionPrev(body, flyProgress, 0.0F, 2.0F, 0.0F, 5.0F);
        this.progressPositionPrev(leftLeg, flyProgress, 0.0F, -3.0F, 1.0F, 5.0F);
        this.progressPositionPrev(rightLeg, flyProgress, 0.0F, -3.0F, 1.0F, 5.0F);
        this.progressPositionPrev(head, flyProgress, 0.0F, 3.0F, -2.0F, 5.0F);
        this.progressRotationPrev(body, tackleProgress, -Maths.rad(55.0), 0.0F, 0.0F, 5.0F);
        this.progressRotationPrev(neck, tackleProgress, Maths.rad(-35.0), 0.0F, 0.0F, 5.0F);
        this.progressRotationPrev(head, tackleProgress, Maths.rad(90.0), 0.0F, 0.0F, 5.0F);
        this.progressRotationPrev(rightLeg, tackleProgress, Maths.rad(-100.0), 0.0F, 0.0F, 5.0F);
        this.progressRotationPrev(leftLeg, tackleProgress, Maths.rad(-100.0), 0.0F, 0.0F, 5.0F);
        this.progressPositionPrev(leftLeg, tackleProgress, 0.0F, 3.0F, -2.0F, 5.0F);
        this.progressPositionPrev(rightLeg, tackleProgress, 0.0F, 3.0F, -2.0F, 5.0F);
        if (flyProgress > 0.0F) {
            this.walk(rightLeg, walkSpeed, walkDegree * 0.4F, false, 0.0F, 0.0F, limbSwing, limbSwingAmount);
            this.walk(leftLeg, walkSpeed, walkDegree * 0.4F, true, 0.0F, 0.0F, limbSwing, limbSwingAmount);
            this.bob(body, flapSpeed * 0.5F, flapDegree * 8.0F, true, ageInTicks, 1.0F);
            this.walk(neck, flapSpeed, flapDegree * 0.5F, false, 0.0F, 0.0F, ageInTicks, 1.0F);
            this.walk(head, flapSpeed, flapDegree, true, 0.0F, -0.1F, ageInTicks, 1.0F);
            this.flap(rightWing, flapSpeed, flapDegree * 5.0F, true, 0.0F, 0.0F, ageInTicks, 1.0F);
            this.flap(leftWing, flapSpeed, flapDegree * 5.0F, false, 0.0F, 0.0F, ageInTicks, 1.0F);
        } else {
            this.walk(body, walkSpeed, walkDegree * 0.5F, false, 2.0F, 0.0F, limbSwing, limbSwingAmount);
            this.walk(neck, walkSpeed, walkDegree * 0.4F, true, 1.0F, 0.0F, limbSwing, limbSwingAmount);
            this.swing(rightWing, walkSpeed, walkDegree * 0.4F, true, 1.0F, 0.2F, limbSwing, limbSwingAmount);
            this.swing(leftWing, walkSpeed, walkDegree * 0.4F, false, 1.0F, 0.2F, limbSwing, limbSwingAmount);
            this.walk(rightLeg, walkSpeed, walkDegree * 1.85F, false, 0.0F, 0.0F, limbSwing, limbSwingAmount);
            this.walk(leftLeg, walkSpeed, walkDegree * 1.85F, true, 0.0F, 0.0F, limbSwing, limbSwingAmount);
        }
        this.walk(heart, idleSpeed, idleDegree, false, 2.0F, 0.0F, ageInTicks, 1.0F);
        this.bob(heart, idleSpeed, idleDegree * 4.0F, false, ageInTicks, 1.0F);
        this.walk(neck, idleSpeed, idleDegree, false, 0.0F, 0.0F, ageInTicks, 1.0F);
        this.walk(head, idleSpeed, idleDegree, false, -1.0F, 0.2F, ageInTicks, 1.0F);
        this.faceTarget(netHeadYaw, headPitch, 2.0F, neck, head);
        float bloatScale = 1.0F + Math.min(1.0F, (float) entity.getSoulLevel() * 0.5F);
        heart.setScale(bloatScale, bloatScale, bloatScale);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    public void setRotationAngle(AdvancedModelBox AdvancedModelBox, float x, float y, float z) {
        AdvancedModelBox.rotateAngleX = x;
        AdvancedModelBox.rotateAngleY = y;
        AdvancedModelBox.rotateAngleZ = z;
    }
}
