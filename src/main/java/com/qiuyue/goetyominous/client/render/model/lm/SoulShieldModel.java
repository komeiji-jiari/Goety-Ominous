package com.qiuyue.goetyominous.client.render.model.lm;

import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SoulShield;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class SoulShieldModel extends HierarchicalModel<SoulShield> {

    private final ModelPart shield;

    public SoulShieldModel(ModelPart root) {
        this.shield = root.getChild("shield");
    }

    public static LayerDefinition createBodyLayer() {
        return net.miauczel.legendary_monsters.entity.client.Model.SoulShieldModel.createBodyLayer();
    }

    @Override
    public void setupAnim(SoulShield entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        double dx = (double) entity.getDestinationX() - entity.getX();
        double dy = (double) entity.getDestinationY() - entity.getY();
        double dz = (double) entity.getDestinationZ() - entity.getZ();
        float horizontalDist = (float) Math.sqrt(dx * dx + dz * dz);
        float pitchDeg = (float) (-Math.toDegrees(Math.atan2(dy, horizontalDist)));

        this.shield.xRot = pitchDeg * ((float) Math.PI / 180F);
    }

    @Override
    public ModelPart root() {
        return this.shield;
    }
}
