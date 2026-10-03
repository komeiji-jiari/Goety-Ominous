package com.qiuyue.goetyominous.client.render.model.ac;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.qiuyue.goetyominous.common.entities.ally.ac.GumWormSegmentServantEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelGumWormSegmentServant extends AdvancedEntityModel<GumWormSegmentServantEntity> {

    private final AdvancedModelBox main;
    private final AdvancedModelBox segment;
    private final AdvancedModelBox gum_back;
    private final AdvancedModelBox gum_front;

    public ModelGumWormSegmentServant() {
        this.texWidth = 128;
        this.texHeight = 128;
        this.main = new AdvancedModelBox(this);
        this.main.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.segment = new AdvancedModelBox(this);
        this.segment.setRotationPoint(0.0F, -16.0F, 0.0F);
        this.main.addChild(this.segment);
        this.segment.setTextureOffset(0, 0).addBox(-16.0F, -16.0F, -16.0F, 32.0F, 32.0F, 32.0F, 0.0F, false);
        this.gum_back = new AdvancedModelBox(this);
        this.gum_back.setRotationPoint(0.0F, 0.0F, 16.5F);
        this.segment.addChild(this.gum_back);
        this.gum_back.setTextureOffset(0, 64).addBox(-16.0F, -16.0F, 0.0F, 32.0F, 32.0F, 0.0F, 0.0F, false);
        this.gum_front = new AdvancedModelBox(this);
        this.gum_front.setRotationPoint(0.0F, 0.0F, -16.5F);
        this.segment.addChild(this.gum_front);
        this.gum_front.setTextureOffset(64, 64).addBox(-16.0F, -16.0F, 0.0F, 32.0F, 32.0F, 0.0F, 0.0F, false);
        this.updateDefaultPose();
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(this.main, this.segment, this.gum_back, this.gum_front);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(this.main);
    }

    public void setGumVisible(boolean front, boolean back) {
        this.gum_front.showModel = front;
        this.gum_back.showModel = back;
    }

    @Override
    public void setupAnim(GumWormSegmentServantEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        float partialTicks = ageInTicks - (float) entity.tickCount;
        this.segment.rotateAngleZ += (float) Math.toRadians(entity.getBodyZRot(partialTicks));
    }
}
