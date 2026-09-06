package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.animation.keyframe_animations.definition.RodlingKeyframeAnimations;
import com.alexander.mutantmore.animation.math_animation.MathAnim;
import com.alexander.mutantmore.animation.math_animation.definition.RodlingMathAnimations;
import com.alexander.mutantmore.models.entities.MMBaseEntityModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantBlazeServantRodProjectile;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.RodlingServant;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class RodlingServantModel<T extends Entity> extends MMBaseEntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("mutantmore", "rodling"), "main");
    private final ModelPart rodling;
    private final ModelPart projectile;
    private final ModelPart head;
    private final ModelPart rod1;
    private final ModelPart rod2;
    private final ModelPart shield1;
    private final ModelPart shield2;
    private final ModelPart headArmour;
    private final ModelPart rodArmour;
    private final ModelPart shieldArmour;
    private final ModelPart rodArmour2;
    private final ModelPart shieldArmour2;

    public RodlingServantModel(ModelPart root) {
        super(root);
        this.rodling = root.getChild("everything");
        this.projectile = root.getChild("projectile");
        this.head = this.rodling.getChild("head");
        ModelPart rod1Parts = this.rodling.getChild("rods").getChild("rod1Parts");
        ModelPart rod2Parts = this.rodling.getChild("rods").getChild("rod2Parts");
        this.rod1 = rod1Parts.getChild("rod1");
        this.rod2 = rod2Parts.getChild("rod2");
        this.shield1 = rod1Parts.getChild("shield1");
        this.shield2 = rod2Parts.getChild("shield2");
        this.headArmour = this.head.getChild("headArmour");
        this.rodArmour = this.rod1.getChild("rodArmour");
        this.shieldArmour = this.shield2.getChild("shieldArmour");
        this.rodArmour2 = this.rod2.getChild("rodArmour2");
        this.shieldArmour2 = this.shield1.getChild("shieldArmour2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition projectile = partdefinition.addOrReplaceChild("projectile", CubeListBuilder.create(), PartPose.offset(0.0F, 23.0F, 0.0F));
        projectile.addOrReplaceChild("rod", CubeListBuilder.create().texOffs(24, 18).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));
        PartDefinition everything = partdefinition.addOrReplaceChild("everything", CubeListBuilder.create(), PartPose.offset(0.0F, 18.5F, 0.0F));
        PartDefinition head = everything.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.5F, 0.0F));
        head.addOrReplaceChild("headArmour", CubeListBuilder.create().texOffs(16, 5).addBox(-4.0F, -4.0F, 0.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.25F)).texOffs(16, 0).addBox(-4.0F, -5.5F, 0.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(2.0F, 2.0F, -2.0F));
        PartDefinition rods = everything.addOrReplaceChild("rods", CubeListBuilder.create(), PartPose.offset(0.0F, 1.5F, 0.0F));
        PartDefinition rod1Parts = rods.addOrReplaceChild("rod1Parts", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.5F, 0.0F, -2.5F, 0.0F, 0.7854F, 0.0F));
        PartDefinition rod1 = rod1Parts.addOrReplaceChild("rod1", CubeListBuilder.create().texOffs(8, 14).addBox(-0.5F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        rod1.addOrReplaceChild("rodArmour", CubeListBuilder.create().texOffs(12, 14).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition shield1 = rod1Parts.addOrReplaceChild("shield1", CubeListBuilder.create().texOffs(8, 8).addBox(-0.5F, -1.0F, 2.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -1.5F, -3.0F));
        shield1.addOrReplaceChild("shieldArmour2", CubeListBuilder.create().texOffs(17, 14).addBox(-1.5F, -2.5F, -0.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offset(1.0F, 1.5F, 3.0F));
        PartDefinition rod2Parts = rods.addOrReplaceChild("rod2Parts", CubeListBuilder.create(), PartPose.offsetAndRotation(2.5F, 0.0F, 2.5F, 0.0F, 0.7854F, 0.0F));
        PartDefinition rod2 = rod2Parts.addOrReplaceChild("rod2", CubeListBuilder.create().texOffs(8, 14).addBox(-0.5F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        rod2.addOrReplaceChild("rodArmour2", CubeListBuilder.create().texOffs(12, 14).addBox(4.5F, -0.5F, 4.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offset(-5.0F, 0.0F, -5.0F));
        PartDefinition shield2 = rod2Parts.addOrReplaceChild("shield2", CubeListBuilder.create().texOffs(8, 8).addBox(-3.0F, -5.0F, 0.0F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 2.5F, -0.5F));
        shield2.addOrReplaceChild("shieldArmour", CubeListBuilder.create().texOffs(17, 14).addBox(-1.5F, -2.5F, -0.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offset(-1.5F, -2.5F, 0.5F));
        PartDefinition rodRemains = everything.addOrReplaceChild("rodRemains", CubeListBuilder.create(), PartPose.offset(0.0F, 1.5F, 0.0F));
        rodRemains.addOrReplaceChild("rodRemainsUpper", CubeListBuilder.create().texOffs(0, 8).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, -2.0F, 0.0F));
        rodRemains.addOrReplaceChild("rodRemainsLower", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 3.0F, 0.0F));
        PartDefinition coreParts = rodRemains.addOrReplaceChild("coreParts", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));
        coreParts.addOrReplaceChild("core", CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));
        coreParts.addOrReplaceChild("coreRing", CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 32, 29);
    }

    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        if (pEntity instanceof MutantBlazeServantRodProjectile projectile) {
            if (!projectile.isRodling()) {
                this.projectile.visible = true;
                this.rodling.visible = false;
                return;
            }
        }

        this.projectile.visible = false;
        this.rodling.visible = true;
        boolean var10000;
        if (pEntity instanceof RodlingServant rodling) {
            var10000 = rodling.hasArmour();
        } else {
            label76: {
                if (pEntity instanceof MutantBlazeServantRodProjectile projectile) {
                    if (projectile.hasArmour()) {
                        var10000 = true;
                        break label76;
                    }
                }

                var10000 = false;
            }
        }

        boolean hasArmour = var10000;
        if (pEntity instanceof RodlingServant rodling) {
            var10000 = rodling.hasShields();
        } else {
            label70: {
                if (pEntity instanceof MutantBlazeServantRodProjectile projectile) {
                    if (projectile.hasShields()) {
                        var10000 = true;
                        break label70;
                    }
                }

                var10000 = false;
            }
        }

        boolean hasShields = var10000;
        this.headArmour.visible = hasArmour;
        this.rodArmour.visible = hasArmour;
        this.shieldArmour.visible = hasArmour;
        this.rodArmour2.visible = hasArmour;
        this.shieldArmour2.visible = hasArmour;
        this.shield1.visible = hasShields;
        this.shield2.visible = hasShields;
        this.rod1.visible = !hasShields;
        this.rod2.visible = !hasShields;
        if (pEntity instanceof MutantBlazeServantRodProjectile projectile) {
            if (projectile.isCollectable() && projectile.landed) {
                this.sitAnimation(pAgeInTicks);
            } else {
                this.applyStatic(RodlingKeyframeAnimations.PROJECTILE);
            }
        }

        if (pEntity instanceof RodlingServant rodling) {
            if (rodling.hasShields()) {
                this.shield1.visible = rodling.getShields() > 0;
                this.shield2.visible = rodling.getShields() > 1;
            }

            this.mathAnimate(pEntity, RodlingMathAnimations.IDLE, 0L,
                    (float) rodling.getDeltaMovement().horizontalDistance() * 7.5F,
                    pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);

            this.animate(rodling.noveltyAnimation, RodlingKeyframeAnimations.NOVELTY, pAgeInTicks);
            this.animate(rodling.shootAnimation, RodlingKeyframeAnimations.SHOOT, pAgeInTicks);
            ModelPart var14 = this.head;
            var14.xRot += (float)Math.toRadians((double)pHeadPitch);
            var14 = this.head;
            var14.yRot += (float)Math.toRadians((double)pNetHeadYaw);
        }
    }

    protected void sitAnimation(float pAgeInTicks) {
        this.applyStatic(RodlingKeyframeAnimations.SIT);
        ModelPart var10000 = this.head;
        var10000.xRot += (float)Math.toRadians((double)(-15.0F - MathAnim.cosWave(pAgeInTicks / 20.0F, 50.0F, -3.5F, -50.0F)));
    }
}