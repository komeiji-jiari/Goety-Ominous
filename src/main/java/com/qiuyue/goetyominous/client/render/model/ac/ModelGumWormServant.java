package com.qiuyue.goetyominous.client.render.model.ac;

import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.qiuyue.goetyominous.common.entities.ally.ac.GumWormServant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelGumWormServant extends AdvancedEntityModel<GumWormServant> {

    private final AdvancedModelBox main;
    private final AdvancedModelBox head;
    private final AdvancedModelBox bottom_Eye;
    private final AdvancedModelBox left_Eye;
    private final AdvancedModelBox right_Eye;
    private final AdvancedModelBox top_Eye;
    private final AdvancedModelBox gum_Strand1;
    private final AdvancedModelBox gum_Strand2;
    private final AdvancedModelBox gum_Strand3;
    private final AdvancedModelBox bottom_Jaw;
    private final AdvancedModelBox top_Jaw;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;

    public ModelGumWormServant() {
        this.texWidth = 256;
        this.texHeight = 256;
        float hatLayer = 1.0F;
        this.main = new AdvancedModelBox(this);
        this.main.setRotationPoint(0.0F, 24.0F, -6.0F);
        this.head = new AdvancedModelBox(this);
        this.head.setRotationPoint(0.0F, -16.5F, 35.25F);
        this.main.addChild(this.head);
        this.head.setTextureOffset(116, 67).addBox(-16.5F, -16.5F, -22.25F, 33.0F, 33.0F, 22.0F, 0.0F, false);
        this.head.setTextureOffset(17, 220).addBox(-16.5F, -16.5F, -22.25F, 33.0F, 33.0F, 3.0F, hatLayer, false);
        this.bottom_Eye = new AdvancedModelBox(this);
        this.bottom_Eye.setRotationPoint(0.0F, 18.0F, -11.75F);
        this.head.addChild(this.bottom_Eye);
        this.cube_r1 = new AdvancedModelBox(this);
        this.cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bottom_Eye.addChild(this.cube_r1);
        this.setRotateAngle(this.cube_r1, 0.0F, 0.0F, -1.5708F);
        this.cube_r1.setTextureOffset(0, 0).addBox(-1.5F, -6.0F, -6.0F, 3.0F, 12.0F, 12.0F, 0.0F, true);
        this.left_Eye = new AdvancedModelBox(this);
        this.left_Eye.setRotationPoint(18.0F, 0.0F, -10.75F);
        this.head.addChild(this.left_Eye);
        this.left_Eye.setTextureOffset(0, 0).addBox(-1.5F, -6.0F, -6.0F, 3.0F, 12.0F, 12.0F, 0.0F, false);
        this.right_Eye = new AdvancedModelBox(this);
        this.right_Eye.setRotationPoint(-18.0F, 0.0F, -10.75F);
        this.head.addChild(this.right_Eye);
        this.right_Eye.setTextureOffset(0, 0).addBox(-1.5F, -6.0F, -6.0F, 3.0F, 12.0F, 12.0F, 0.0F, true);
        this.top_Eye = new AdvancedModelBox(this);
        this.top_Eye.setRotationPoint(0.0F, -18.0F, -11.75F);
        this.head.addChild(this.top_Eye);
        this.cube_r2 = new AdvancedModelBox(this);
        this.cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.top_Eye.addChild(this.cube_r2);
        this.setRotateAngle(this.cube_r2, 0.0F, 0.0F, 1.5708F);
        this.cube_r2.setTextureOffset(0, 0).addBox(-1.5F, -6.0F, -6.0F, 3.0F, 12.0F, 12.0F, 0.0F, true);
        this.gum_Strand1 = new AdvancedModelBox(this);
        this.gum_Strand1.setRotationPoint(9.0F, 1.0F, -35.75F);
        this.head.addChild(this.gum_Strand1);
        this.gum_Strand1.setTextureOffset(218, 103).addBox(0.0F, -18.5F, -9.5F, 0.0F, 37.0F, 19.0F, 0.0F, false);
        this.gum_Strand2 = new AdvancedModelBox(this);
        this.gum_Strand2.setRotationPoint(-9.0F, 1.0F, -35.75F);
        this.head.addChild(this.gum_Strand2);
        this.gum_Strand2.setTextureOffset(218, 103).addBox(0.0F, -18.5F, -9.5F, 0.0F, 37.0F, 19.0F, 0.0F, false);
        this.gum_Strand3 = new AdvancedModelBox(this);
        this.gum_Strand3.setRotationPoint(0.0F, 1.0F, -35.75F);
        this.head.addChild(this.gum_Strand3);
        this.setRotateAngle(this.gum_Strand3, 0.0F, 0.7854F, 0.0F);
        this.gum_Strand3.setTextureOffset(218, 103).addBox(0.0F, -18.5F, -9.5F, 0.0F, 37.0F, 19.0F, 0.0F, true);
        this.bottom_Jaw = new AdvancedModelBox(this);
        this.bottom_Jaw.setRotationPoint(0.0F, 6.5F, -22.0F);
        this.head.addChild(this.bottom_Jaw);
        this.bottom_Jaw.setTextureOffset(102, 9).addBox(-16.5F, 0.0F, -36.25F, 33.0F, 10.0F, 36.0F, 0.0F, false);
        this.bottom_Jaw.setTextureOffset(0, 43).addBox(-16.5F, -10.0F, -36.25F, 33.0F, 10.0F, 36.0F, 0.0F, false);
        this.bottom_Jaw.setTextureOffset(118, 210).addBox(-16.5F, 0.0F, -36.25F, 33.0F, 10.0F, 36.0F, hatLayer + 0.01F, false);
        this.top_Jaw = new AdvancedModelBox(this);
        this.top_Jaw.setRotationPoint(0.0F, -6.5F, -22.25F);
        this.head.addChild(this.top_Jaw);
        this.top_Jaw.setTextureOffset(118, 165).addBox(-16.5F, -10.0F, -36.0F, 33.0F, 10.0F, 36.0F, hatLayer + 0.01F, false);
        this.top_Jaw.setTextureOffset(0, 144).addBox(-16.5F, 0.0F, -36.0F, 33.0F, 10.0F, 36.0F, 0.0F, false);
        this.top_Jaw.setTextureOffset(0, 89).addBox(-16.5F, -10.0F, -36.0F, 33.0F, 10.0F, 36.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.main);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.main, this.head, this.bottom_Eye, this.top_Eye, this.left_Eye, this.right_Eye,
                this.top_Jaw, this.bottom_Jaw, this.gum_Strand1, this.gum_Strand2, this.gum_Strand3, this.cube_r1,
                this.cube_r2);
    }

    @Override
    public void setupAnim(GumWormServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTicks = ageInTicks - (float) entity.tickCount;
        float mouthOpenProgress = entity.getMouthOpenProgress(partialTicks);
        this.walk(this.bottom_Jaw, 0.2F, 0.1F, true, 1.0F, -0.8F, ageInTicks, mouthOpenProgress);
        this.walk(this.top_Jaw, 0.2F, 0.1F, false, 1.0F, -0.8F, ageInTicks, mouthOpenProgress);
        float gumStretchVertical = Math.max(ACMath.walkValue(ageInTicks, mouthOpenProgress, 0.2F, 1.0F, 0.15F, true) + mouthOpenProgress, 0.0F) + 0.45F;
        this.gum_Strand1.setScale(1.0F, gumStretchVertical, 1.0F - gumStretchVertical * 0.2F);
        this.gum_Strand2.setScale(1.0F, gumStretchVertical, 1.0F - gumStretchVertical * 0.2F);
        this.gum_Strand3.setScale(1.0F, gumStretchVertical, 1.0F - gumStretchVertical * 0.2F);
        this.head.rotateAngleZ += (float) Math.toRadians(entity.getBodyZRot(partialTicks));
    }
}
