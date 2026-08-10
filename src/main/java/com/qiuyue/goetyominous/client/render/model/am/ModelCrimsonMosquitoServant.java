package com.qiuyue.goetyominous.client.render.model.am;

import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.qiuyue.goetyominous.common.entities.ally.am.CrimsonMosquitoServant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelCrimsonMosquitoServant extends AdvancedEntityModel<CrimsonMosquitoServant> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox body;
    private final AdvancedModelBox wingL;
    private final AdvancedModelBox wingR;
    private final AdvancedModelBox legsL;
    private final AdvancedModelBox legL1;
    private final AdvancedModelBox legL2;
    private final AdvancedModelBox legL3;
    private final AdvancedModelBox legsR;
    private final AdvancedModelBox legR1;
    private final AdvancedModelBox legR2;
    private final AdvancedModelBox legR3;
    private final AdvancedModelBox tail;
    private final AdvancedModelBox head;
    private final AdvancedModelBox antennaL;
    private final AdvancedModelBox antennaR;
    private final AdvancedModelBox mouth;

    public float renderPartialTicks;

    public ModelCrimsonMosquitoServant() {
        this.texWidth = 128;
        this.texHeight = 128;
        this.root = new AdvancedModelBox(this, "root");
        this.root.setPos(0.0F, 24.0F, 0.0F);
        this.body = new AdvancedModelBox(this, "body");
        this.body.setPos(0.0F, -9.5F, -0.25F);
        this.root.addChild(this.body);
        this.body.setTextureOffset(31, 65).addBox(-3.0F, -3.5F, -3.75F, 6.0F, 6.0F, 6.0F, 0.0F, false);
        this.wingL = new AdvancedModelBox(this, "wingL");
        this.wingL.setPos(2.7F, -3.2F, -0.75F);
        this.body.addChild(this.wingL);
        this.wingL.setTextureOffset(37, 59).addBox(0.0F, 0.0F, -1.0F, 18.0F, 0.0F, 5.0F, 0.0F, false);
        this.wingR = new AdvancedModelBox(this, "wingR");
        this.wingR.setPos(-2.7F, -3.2F, -0.75F);
        this.body.addChild(this.wingR);
        this.wingR.setTextureOffset(37, 53).addBox(-18.0F, 0.0F, -1.0F, 18.0F, 0.0F, 5.0F, 0.0F, false);
        this.legsL = new AdvancedModelBox(this, "legsL");
        this.legsL.setPos(3.0F, 2.5F, -2.75F);
        this.body.addChild(this.legsL);
        this.legL1 = new AdvancedModelBox(this, "legL1");
        this.legL1.setPos(0.0F, 0.0F, 0.0F);
        this.legsL.addChild(this.legL1);
        setRotationAngle(this.legL1, 0.0F, 0.5236F, 0.0F);
        this.legL1.setTextureOffset(0, 51).addBox(0.0F, -8.0F, 0.0F, 18.0F, 15.0F, 0.0F, 0.0F, false);
        this.legL2 = new AdvancedModelBox(this, "legL2");
        this.legL2.setPos(0.0F, 0.0F, 0.4F);
        this.legsL.addChild(this.legL2);
        this.legL2.setTextureOffset(37, 16).addBox(0.0F, -8.0F, 0.0F, 18.0F, 15.0F, 0.0F, 0.0F, false);
        this.legL3 = new AdvancedModelBox(this, "legL3");
        this.legL3.setPos(0.0F, 0.0F, 0.9F);
        this.legsL.addChild(this.legL3);
        setRotationAngle(this.legL3, 0.0F, -0.8727F, 0.0F);
        this.legL3.setTextureOffset(37, 0).addBox(0.0F, -8.0F, 0.0F, 18.0F, 15.0F, 0.0F, 0.0F, false);
        this.legsR = new AdvancedModelBox(this, "legsR");
        this.legsR.setPos(-3.0F, 2.5F, -2.75F);
        this.body.addChild(this.legsR);
        this.legR1 = new AdvancedModelBox(this, "legR1");
        this.legR1.setPos(0.0F, 0.0F, 0.0F);
        this.legsR.addChild(this.legR1);
        setRotationAngle(this.legR1, 0.0F, -0.5236F, 0.0F);
        this.legR1.setTextureOffset(37, 37).addBox(-18.0F, -8.0F, 0.0F, 18.0F, 15.0F, 0.0F, 0.0F, false);
        this.legR2 = new AdvancedModelBox(this, "legR2");
        this.legR2.setPos(0.0F, 0.0F, 0.4F);
        this.legsR.addChild(this.legR2);
        this.legR2.setTextureOffset(0, 35).addBox(-18.0F, -8.0F, 0.0F, 18.0F, 15.0F, 0.0F, 0.0F, false);
        this.legR3 = new AdvancedModelBox(this, "legR3");
        this.legR3.setPos(0.0F, 0.0F, 0.9F);
        this.legsR.addChild(this.legR3);
        setRotationAngle(this.legR3, 0.0F, 0.8727F, 0.0F);
        this.legR3.setTextureOffset(0, 19).addBox(-18.0F, -8.0F, 0.0F, 18.0F, 15.0F, 0.0F, 0.0F, false);
        this.tail = new AdvancedModelBox(this, "tail");
        this.tail.setPos(0.0F, -1.5F, 2.25F);
        this.body.addChild(this.tail);
        this.tail.setTextureOffset(48, 83).addBox(-2.0F, -1.4F, 0.0F, 4.0F, 4.0F, 16.0F, 0.0F, false);
        this.head = new AdvancedModelBox(this, "head");
        this.head.setPos(0.0F, 0.5F, -3.75F);
        this.body.addChild(this.head);
        this.head.setTextureOffset(56, 65).addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 4.0F, 0.0F, false);
        this.antennaL = new AdvancedModelBox(this, "antennaL");
        this.antennaL.setPos(1.0F, -0.1F, -4.0F);
        this.head.addChild(this.antennaL);
        setRotationAngle(this.antennaL, 1.2217F, -0.48F, 0.0436F);
        this.antennaL.setTextureOffset(5, 0).addBox(0.0F, -8.0F, 0.0F, 0.0F, 8.0F, 2.0F, 0.0F, false);
        this.antennaR = new AdvancedModelBox(this, "antennaR");
        this.antennaR.setPos(-1.0F, -0.1F, -4.0F);
        this.head.addChild(this.antennaR);
        setRotationAngle(this.antennaR, 1.2217F, 0.48F, -0.0436F);
        this.antennaR.setTextureOffset(0, 0).addBox(0.0F, -8.0F, 0.0F, 0.0F, 8.0F, 2.0F, 0.0F, false);
        this.mouth = new AdvancedModelBox(this, "mouth");
        this.mouth.setPos(0.0F, 2.0F, -3.5F);
        this.head.addChild(this.mouth);
        setRotationAngle(this.mouth, -1.0036F, 0.0F, 0.0F);
        this.mouth.setTextureOffset(23, 0).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 8.0F, 1.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.root, this.body, this.wingL, this.wingR, this.legsL, this.legL1, this.legL2, this.legL3, this.legsR, this.legR1, this.legR2, this.legR3, this.tail, this.head, this.antennaL, this.antennaR, this.mouth);
    }

    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.root);
    }

    private static void setRotationAngle(AdvancedModelBox box, float x, float y, float z) {
        box.rotateAngleX = x;
        box.rotateAngleY = y;
        box.rotateAngleZ = z;
    }

    public void setupAnim(CrimsonMosquitoServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float flySpeed = 0.5F;
        float flyDegree = 0.5F;
        float partialTicks = this.renderPartialTicks;
        float flyProgress = entity.prevFlyProgress + (entity.flyProgress - entity.prevFlyProgress) * partialTicks;
        float shootProgress = entity.prevShootProgress + (entity.shootProgress - entity.prevShootProgress) * partialTicks;

        this.walk(this.antennaR, flySpeed, flyDegree * 0.15F, false, 0.0F, 0.1F, ageInTicks, 1.0F);
        this.walk(this.antennaL, flySpeed, flyDegree * 0.15F, false, 0.0F, 0.1F, ageInTicks, 1.0F);

        boolean wingFlap = flyProgress > 0.0F || entity.randomWingFlapTick > 0;

        this.progressRotationPrev(this.head, shootProgress, Maths.rad(-10.0D), 0.0F, 0.0F, 5.0F);
        this.progressRotationPrev(this.mouth, shootProgress, Maths.rad(-20.0D), 0.0F, 0.0F, 5.0F);

        if (entity.isPassenger()) {
            this.progressRotationPrev(this.body, 5.0F, Maths.rad(-90.0D), Maths.rad(180.0D), 0.0F, 5.0F);
            this.progressRotationPrev(this.head, 5.0F, Maths.rad(40.0D), 0.0F, 0.0F, 5.0F);
            this.progressRotationPrev(this.mouth, 5.0F, Maths.rad(10.0D), 0.0F, 0.0F, 5.0F);
            this.progressRotationPrev(this.legR1, 5.0F, 0.0F, 0.0F, Maths.rad(-50.0D), 5.0F);
            this.progressRotationPrev(this.legR2, 5.0F, 0.0F, 0.0F, Maths.rad(-50.0D), 5.0F);
            this.progressRotationPrev(this.legR3, 5.0F, 0.0F, 0.0F, Maths.rad(-50.0D), 5.0F);
            this.progressRotationPrev(this.legL1, 5.0F, 0.0F, 0.0F, Maths.rad(50.0D), 5.0F);
            this.progressRotationPrev(this.legL2, 5.0F, 0.0F, 0.0F, Maths.rad(50.0D), 5.0F);
            this.progressRotationPrev(this.legL3, 5.0F, 0.0F, 0.0F, Maths.rad(50.0D), 5.0F);
            this.mouth.setScale(1.0F, 0.85F + 0.15F * (float) Math.sin((double) ageInTicks), 1.0F);
        } else {
            this.mouth.setScale(1.0F, 1.0F, 1.0F);
        }

        if (shootProgress > 0.0F) {
            this.mouth.setScale(1.0F + shootProgress * 0.1F, 1.0F - shootProgress * 0.1F, 1.0F + shootProgress * 0.1F);
        }

        if (wingFlap) {
            this.flap(this.wingL, flySpeed * 3.3F, flyDegree, true, 0.0F, 0.2F, ageInTicks, 1.0F);
            this.flap(this.wingR, flySpeed * 3.3F, flyDegree, false, 0.0F, 0.2F, ageInTicks, 1.0F);
        } else {
            this.wingR.rotateAngleX = Maths.rad(30.0D);
            this.wingR.rotateAngleY = Maths.rad(70.0D);
            this.wingL.rotateAngleX = Maths.rad(30.0D);
            this.wingL.rotateAngleY = Maths.rad(-70.0D);
        }

        if (flyProgress > 0.0F) {
            this.progressPositionPrev(this.body, flyProgress, 0.0F, -10.0F, 0.0F, 5.0F);
            this.progressRotationPrev(this.legL1, flyProgress, 0.0F, Maths.rad(-30.0D), Maths.rad(60.0D), 5.0F);
            this.progressRotationPrev(this.legR1, flyProgress, 0.0F, Maths.rad(30.0D), Maths.rad(-60.0D), 5.0F);
            this.progressRotationPrev(this.legL2, flyProgress, 0.0F, Maths.rad(-20.0D), Maths.rad(60.0D), 5.0F);
            this.progressRotationPrev(this.legR2, flyProgress, 0.0F, Maths.rad(20.0D), Maths.rad(-60.0D), 5.0F);
            this.progressRotationPrev(this.legL3, flyProgress, 0.0F, Maths.rad(-5.0D), Maths.rad(60.0D), 5.0F);
            this.progressRotationPrev(this.legR3, flyProgress, 0.0F, Maths.rad(5.0D), Maths.rad(-60.0D), 5.0F);
            this.bob(this.body, flySpeed * 0.5F, flyDegree * 5.0F, false, ageInTicks, 1.0F);
            this.flap(this.legL1, flySpeed, flyDegree * 0.5F, true, 1.0F, 0.1F, ageInTicks, 1.0F);
            this.flap(this.legR1, flySpeed, flyDegree * 0.5F, false, 1.0F, 0.1F, ageInTicks, 1.0F);
            this.flap(this.legL2, flySpeed, flyDegree * 0.5F, true, 2.0F, 0.1F, ageInTicks, 1.0F);
            this.flap(this.legR2, flySpeed, flyDegree * 0.5F, false, 2.0F, 0.1F, ageInTicks, 1.0F);
            this.flap(this.legL3, flySpeed, flyDegree * 0.5F, true, 2.0F, 0.1F, ageInTicks, 1.0F);
            this.flap(this.legR3, flySpeed, flyDegree * 0.5F, false, 2.0F, 0.1F, ageInTicks, 1.0F);
            this.walk(this.tail, flySpeed, flyDegree * 0.15F, false, 0.0F, -0.1F, ageInTicks, 1.0F);
        }

        float f = 1.0F + (float) entity.getBloodLevel() * 0.1F;
        this.tail.rotateAngleX -= (float) entity.getBloodLevel() * 0.05F;
        this.tail.setScale(f, f, f);
    }
}
