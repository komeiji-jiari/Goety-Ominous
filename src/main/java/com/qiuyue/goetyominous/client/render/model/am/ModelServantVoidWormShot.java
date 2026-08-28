package com.qiuyue.goetyominous.client.render.model.am;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.qiuyue.goetyominous.common.entities.projectile.EntityServantVoidWormShot;

public class ModelServantVoidWormShot extends AdvancedEntityModel<EntityServantVoidWormShot> {
    private final AdvancedModelBox root;
    private final AdvancedModelBox glass;
    private final AdvancedModelBox cube;

    public ModelServantVoidWormShot() {
        this.texWidth = 64;
        this.texHeight = 64;
        this.root = new AdvancedModelBox(this, "root");
        this.root.setPos(0.0F, 24.0F, 0.0F);
        this.glass = new AdvancedModelBox(this, "glass");
        this.glass.setPos(0.0F, -5.0F, 0.0F);
        this.root.addChild(this.glass);
        this.glass.setTextureOffset(0, 21).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
        this.cube = new AdvancedModelBox(this, "cube");
        this.cube.setPos(0.0F, -5.0F, 0.0F);
        this.root.addChild(this.cube);
        this.cube.setTextureOffset(0, 0).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable parts() {
        return ImmutableList.of(this.root);
    }

    @Override
    public Iterable getAllParts() {
        return ImmutableList.of(this.root, this.cube, this.glass);
    }

    @Override
    public void setupAnim(EntityServantVoidWormShot entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
    }

    public void setRotationAngle(AdvancedModelBox box, float x, float y, float z) {
        box.rotateAngleX = x;
        box.rotateAngleY = y;
        box.rotateAngleZ = z;
    }

    public void animate(EntityServantVoidWormShot entityIn, float ageInTicks) {
        this.resetToDefaultPose();
        float innerScale = (float) (1.0D + 0.25D * Math.abs(Math.sin((double) (ageInTicks * 0.6F))));
        float outerScale = (float) (1.0D + 0.5D * Math.abs(Math.cos((double) (ageInTicks * 0.2F))));
        this.glass.setScale(innerScale, innerScale, innerScale);
        this.glass.rotateAngleX += ageInTicks * 0.25F;
        this.cube.rotateAngleX += ageInTicks * 0.5F;
        this.glass.setShouldScaleChildren(false);
        this.cube.setScale(outerScale, outerScale, outerScale);
    }
}
