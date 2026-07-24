package com.qiuyue.someillagerservants.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.someillagerservants.common.entities.hostile.UrbhadhachEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class UrbhadhachModel<T extends UrbhadhachEntity> extends EntityModel<T> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;

    public UrbhadhachModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.jaw = head.getChild("jaw");
        this.leg0 = root.getChild("leg0");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 19)
                        .addBox(-5.0F, -13.0F, -7.0F, 14.0F, 14.0F, 11.0F, new CubeDeformation(-0.25F)),
                PartPose.offsetAndRotation(-2.0F, 9.0F, 12.0F, 1.5708F, 0.0F, 0.0F));
        body.addOrReplaceChild("bodyUpper", CubeListBuilder.create()
                        .texOffs(39, 0)
                        .addBox(-4.0F, -25.0F, -7.0F, 12.0F, 12.0F, 10.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3.5F, -3.0F, -3.0F, 7.0F, 8.0F, 7.0F)
                        .texOffs(26, 0)
                        .addBox(-4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F)
                        .texOffs(26, 0)
                        .addBox(2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F, true)
                        .texOffs(0, 44)
                        .addBox(-2.5F, 1.0F, -6.0F, 5.0F, 3.0F, 3.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 10.0F, -16.0F));

        head.addOrReplaceChild("jaw", CubeListBuilder.create()
                        .texOffs(0, 52)
                        .addBox(-2.5F, -0.25F, -3.5F, 5.0F, 2.0F, 3.0F),
                PartPose.offset(0.0F, 3.25F, -2.5F));

        root.addOrReplaceChild("leg0", CubeListBuilder.create()
                        .texOffs(50, 22)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F, true),
                PartPose.offset(-4.5F, 14.0F, 6.0F));

        root.addOrReplaceChild("leg1", CubeListBuilder.create()
                        .texOffs(50, 22)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F),
                PartPose.offset(4.5F, 14.0F, 6.0F));

        root.addOrReplaceChild("leg2", CubeListBuilder.create()
                        .texOffs(50, 40)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F, true),
                PartPose.offset(-3.5F, 14.0F, -8.0F));

        root.addOrReplaceChild("leg3", CubeListBuilder.create()
                        .texOffs(50, 40)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F),
                PartPose.offset(3.5F, 14.0F, -8.0F));

        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.body.xRot = ((float) Math.PI / 2F);
        this.leg0.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leg1.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.leg2.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.leg3.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        float f = ageInTicks - (float) entity.tickCount;
        float f1 = entity.getStandingAnimationScale(f);
        f1 = f1 * f1;
        float f2 = 1.0F - f1;
        this.body.xRot = ((float) Math.PI / 2F) - f1 * (float) Math.PI * 0.35F;
        this.body.y = 9.0F * f2 + 11.0F * f1;
        this.leg2.y = 14.0F * f2 - 6.0F * f1;
        this.leg2.z = -8.0F * f2 - 4.0F * f1;
        this.leg2.xRot -= f1 * (float) Math.PI * 0.45F;
        this.leg3.y = this.leg2.y;
        this.leg3.z = this.leg2.z;
        this.leg3.xRot -= f1 * (float) Math.PI * 0.45F;
        this.head.y = 10.0F * f2 - 14.0F * f1;
        this.head.z = -16.0F * f2 - 3.0F * f1;
        this.head.xRot += f1 * (float) Math.PI * 0.15F;
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        int roarTick = entity.getRoarTick();
        if (entity.isStanding()) {
            this.jaw.xRot = 0.7854F;
        } else {
            if (roarTick > 0) {
                float f7 = Mth.sin(((float) (20 - roarTick) - partialTick) / 20.0F * (float) Math.PI * 0.25F);
                this.jaw.xRot = ((float) Math.PI / 2F) * f7;
            } else {
                this.jaw.xRot = 0.0F;
            }
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leg0.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leg1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leg2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leg3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
