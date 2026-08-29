package com.qiuyue.goetyominous.client.render.model.ac;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.qiuyue.goetyominous.common.entities.projectile.DeepOneMageServantWaterBolt;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelDeepOneMageServantWaterBolt extends AdvancedEntityModel<DeepOneMageServantWaterBolt> {

    private final AdvancedModelBox bb_main;
    private final AdvancedModelBox cube_r1;
    private final AdvancedModelBox cube_r2;
    private final AdvancedModelBox cube_r3;
    private final AdvancedModelBox cube_r4;

    public ModelDeepOneMageServantWaterBolt() {
        this.texWidth = 64;
        this.texHeight = 64;
        this.bb_main = new AdvancedModelBox(this);
        this.bb_main.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bb_main.setTextureOffset(0, 24).addBox(-3.0F, -3.0F, -8.0F, 6.0F, 6.0F, 11.0F, 0.0F, false);
        this.bb_main.setTextureOffset(0, 0).addBox(0.0F, -3.0F, -5.0F, 6.0F, 18.0F, 0.0F, 0.0F, false);
        this.bb_main.setTextureOffset(38, 245).addBox(0.0F, 3.0F, -8.0F, 6.0F, 11.0F, 0.0F, 0.0F, false);
        this.cube_r1 = new AdvancedModelBox(this);
        this.cube_r1.setRotationPoint(0.0F, -3.0F, 4.0F);
        this.bb_main.addChild(this.cube_r1);
        this.setRotateAngle(this.cube_r1, 0.0F, 0.0F, 1.5708F);
        this.cube_r1.setTextureOffset(0, 0).addBox(3.0F, -3.0F, -9.0F, 6.0F, 18.0F, 0.0F, 0.0F, false);
        this.cube_r2 = new AdvancedModelBox(this);
        this.cube_r2.setRotationPoint(3.0F, 0.0F, -2.5F);
        this.bb_main.addChild(this.cube_r2);
        this.setRotateAngle(this.cube_r2, 0.0F, 0.0F, -1.5708F);
        this.cube_r2.setTextureOffset(38, 245).addBox(0.0F, 0.0F, -5.5F, 6.0F, 11.0F, 0.0F, 0.0F, true);
        this.cube_r3 = new AdvancedModelBox(this);
        this.cube_r3.setRotationPoint(-3.0F, 0.0F, -2.5F);
        this.bb_main.addChild(this.cube_r3);
        this.setRotateAngle(this.cube_r3, 0.0F, 0.0F, 1.5708F);
        this.cube_r3.setTextureOffset(38, 245).addBox(0.0F, 0.0F, -5.5F, 6.0F, 11.0F, 0.0F, 0.0F, false);
        this.cube_r4 = new AdvancedModelBox(this);
        this.cube_r4.setRotationPoint(0.0F, -6.0F, -2.5F);
        this.bb_main.addChild(this.cube_r4);
        this.setRotateAngle(this.cube_r4, 3.1416F, 0.0F, 0.0F);
        this.cube_r4.setTextureOffset(27, 245).addBox(0.0F, -3.0F, -5.5F, 6.0F, 11.0F, 0.0F, 0.0F, false);
        this.bb_main.scaleChildren = true;
        this.updateDefaultPose();
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.bb_main);
    }

    @Override
    public void setupAnim(DeepOneMageServantWaterBolt entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float f = Math.min(20.0F, ageInTicks) / 20.0F;
        this.bb_main.setScale(1.0F, 1.0F, 1.0F + f);
        this.bb_main.rotationPointZ += f * 12.0F;
        this.bb_main.rotateAngleZ += ageInTicks * 0.2F;
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.bb_main, this.cube_r1, this.cube_r2, this.cube_r3, this.cube_r4);
    }
}
