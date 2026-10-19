package com.qiuyue.goetyominous.client.render.model.lm;

import com.Polarice3.Goety.client.render.animation.EndersentAnimations;
import com.qiuyue.goetyominous.client.render.model.animation.lm.EndersentServantAnimations;
import com.qiuyue.goetyominous.common.entities.ally.lm.EndersentServant;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class EndersentServantModel extends HierarchicalModel<EndersentServant> {

    private static final float BLEND_TICKS = 4.0F;

    private final ModelPart root;
    private final ModelPart head;
    private final List<ModelPart> parts;
    private final Map<EndersentServant, PoseCache> poseCache = new WeakHashMap<>();

    public EndersentServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("upperBody").getChild("head");
        this.parts = this.root.getAllParts().toList();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));
        root.addOrReplaceChild("leftLeg",
                CubeListBuilder.create().texOffs(0, 63).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 56.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(4.0F, -55.0F, 0.0F));
        root.addOrReplaceChild("rightLeg",
                CubeListBuilder.create().texOffs(50, 35).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 56.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-4.0F, -55.0F, 0.0F));
        PartDefinition body = root.addOrReplaceChild("upperBody",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-9.0F, -28.0F, -4.0F, 18.0F, 27.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(86, 89).addBox(-4.0F, -19.0F, -6.0F, 8.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -55.0F, 0.0F));
        body.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(66, 89).addBox(0.0F, -11.0F, -0.001F, 10.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -17.0F, 4.0F, -0.3927F, 0.0F, 0.0F));
        body.addOrReplaceChild("cube_r2",
                CubeListBuilder.create().texOffs(84, 6).addBox(0.0F, -11.0F, -0.001F, 10.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-9.0F, -23.0F, 4.0F, -0.3927F, 0.0F, 0.0F));
        body.addOrReplaceChild("cube_r3",
                CubeListBuilder.create().texOffs(100, 47).addBox(-4.0F, 3.3294F, 2.4146F, 8.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -15.0F, -5.9567F, -0.3927F, 0.0F, 1.5708F));
        body.addOrReplaceChild("cube_r4",
                CubeListBuilder.create().texOffs(100, 52).addBox(-4.0F, -8.3294F, 2.4146F, 8.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -15.0F, -5.9567F, 0.3927F, 0.0F, 1.5708F));
        body.addOrReplaceChild("cube_r5",
                CubeListBuilder.create().texOffs(100, 42).addBox(-8.0F, -5.0F, 0.0F, 8.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(4.0F, -6.3806F, -6.9134F, -0.3927F, 0.0F, 0.0F));
        body.addOrReplaceChild("cube_r6",
                CubeListBuilder.create().texOffs(100, 37).addBox(-8.0F, -5.0F, 0.0F, 8.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(4.0F, -19.0F, -5.0F, 0.3927F, 0.0F, 0.0F));
        body.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(52, 0).addBox(-4.0F, -10.9F, -7.0F, 8.0F, 13.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(86, 99).addBox(4.0F, -8.9F, -5.0F, 5.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(52, 21).addBox(6.0F, -14.9F, -5.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(100, 17).addBox(-9.0F, -8.9F, -5.0F, 5.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(100, 27).addBox(-9.0F, -14.9F, -5.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -26.1F, -3.0F));
        PartDefinition leftArm = body.addOrReplaceChild("leftArm",
                CubeListBuilder.create()
                        .texOffs(66, 55).addBox(-2.0F, -2.3333F, -2.1667F, 4.0F, 30.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(82, 38).addBox(2.0F, 8.6667F, 0.8333F, 9.0F, 17.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(11.0F, -25.6667F, 0.1667F));
        PartDefinition leftArmBone = leftArm.addOrReplaceChild("leftArmBone",
                CubeListBuilder.create()
                        .texOffs(32, 63).addBox(-2.0F, -0.3333F, -2.1667F, 4.0F, 41.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(82, 21).addBox(2.0F, 14.6667F, 0.8333F, 9.0F, 17.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 28.0F, 0.0F));
        PartDefinition leftHand = leftArmBone.addOrReplaceChild("leftHand",
                CubeListBuilder.create()
                        .texOffs(0, 35).addBox(-4.0F, 0.0F, -2.1667F, 8.0F, 11.0F, 17.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 40.6667F, 0.0F));
        leftHand.addOrReplaceChild("cube_r7",
                CubeListBuilder.create().texOffs(48, 95).mirror().addBox(0.0F, 0.0F, -0.001F, 0.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-4.0004F, 0.0F, 9.8343F, 0.0F, -0.6981F, 0.0F));
        leftHand.addOrReplaceChild("cube_r8",
                CubeListBuilder.create().texOffs(48, 95).addBox(0.0F, 0.0F, -0.001F, 0.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(4.0F, 0.0F, 9.8343F, 0.0F, 0.6981F, 0.0F));
        leftHand.addOrReplaceChild("cube_r9",
                CubeListBuilder.create().texOffs(84, 0).addBox(-8.0F, -0.001F, 0.0F, 8.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(4.0F, 11.0018F, 9.8326F, -0.6981F, 0.0F, 0.0F));
        leftHand.addOrReplaceChild("cube_r10",
                CubeListBuilder.create().texOffs(84, 0).addBox(-8.0F, 0.001F, 0.0F, 8.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(4.0F, -0.001F, 9.8333F, 0.6981F, 0.0F, 0.0F));
        PartDefinition rightArm = body.addOrReplaceChild("rightArm",
                CubeListBuilder.create()
                        .texOffs(66, 21).addBox(-2.0F, -2.3333F, -2.1667F, 4.0F, 30.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(82, 72).addBox(-11.0F, 0.6667F, 0.8333F, 9.0F, 17.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-11.0F, -25.6667F, 0.1667F));
        PartDefinition rightArmBone = rightArm.addOrReplaceChild("rightArmBone",
                CubeListBuilder.create()
                        .texOffs(16, 63).addBox(-2.0F, -0.3333F, -2.1667F, 4.0F, 41.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 35).mirror().addBox(-4.0F, 40.6667F, -2.1667F, 8.0F, 11.0F, 17.0F, new CubeDeformation(0.0F))
                        .mirror(false)
                        .texOffs(82, 55).addBox(-11.0F, -1.3333F, 0.8333F, 9.0F, 17.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 28.0F, 0.0F));
        rightArmBone.addOrReplaceChild("cube_r11",
                CubeListBuilder.create().texOffs(84, 0).mirror().addBox(0.0F, 0.001F, 0.0F, 8.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-4.0F, 40.6657F, 9.8333F, 0.6981F, 0.0F, 0.0F));
        rightArmBone.addOrReplaceChild("cube_r12",
                CubeListBuilder.create().texOffs(48, 95).mirror().addBox(0.0F, 0.0F, -0.001F, 0.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-4.0F, 40.6667F, 9.8343F, 0.0F, -0.6981F, 0.0F));
        rightArmBone.addOrReplaceChild("cube_r13",
                CubeListBuilder.create().texOffs(84, 0).mirror().addBox(0.0F, -0.001F, 0.0F, 8.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-4.0F, 51.6685F, 9.8325F, -0.6981F, 0.0F, 0.0F));
        rightArmBone.addOrReplaceChild("cube_r14",
                CubeListBuilder.create().texOffs(48, 95).addBox(0.0F, 0.0F, -0.001F, 0.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(4.0004F, 40.6667F, 9.8342F, 0.0F, 0.6981F, 0.0F));
        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(EndersentServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);
        if (!entity.isDeadOrDying()) {
            this.head.yRot = netHeadYaw * ((float) Math.PI / 180.0F);
            this.head.xRot = headPitch * ((float) Math.PI / 180.0F);
        }
        if (!(entity.isCurrentAnimation(EndersentServant.DEADLY_ESCAPE)
                || entity.teleportInAnimationState.isStarted()
                || entity.teleportOutAnimationState.isStarted())) {
            this.animateWalk(EndersentServantAnimations.WALK, limbSwing, limbSwingAmount, 1.5F, 4.0F);
        }
        this.animate(entity.attackAnimationState, EndersentAnimations.ATTACK, ageInTicks);
        this.animate(entity.swipeAnimationState, EndersentAnimations.SWING, ageInTicks);
        this.animate(entity.deadlyEscapeAnimationState, EndersentAnimations.DEADLY_ESCAPE, ageInTicks);
        this.animate(entity.teleportInAnimationState, EndersentServantAnimations.TELEPORT_IN, ageInTicks);
        this.animate(entity.teleportOutAnimationState, EndersentServantAnimations.TELEPORT_OUT, ageInTicks);
        this.animate(entity.deathAnimationState, EndersentAnimations.DEATH, ageInTicks);
        this.blendPose(entity, ageInTicks);
    }

    private void blendPose(EndersentServant entity, float ageInTicks) {
        PoseCache cache = this.poseCache.get(entity);
        if (cache == null) {
            cache = new PoseCache(this.parts.size() * 9);
            this.readPose(cache.target);
            System.arraycopy(cache.target, 0, cache.rendered, 0, cache.target.length);
            cache.animation = entity.getCurrentAnimation();
            cache.start = ageInTicks;
            this.poseCache.put(entity, cache);
            return;
        }
        this.readPose(cache.target);
        int animation = entity.getCurrentAnimation();
        if (animation != cache.animation) {
            System.arraycopy(cache.rendered, 0, cache.from, 0, cache.rendered.length);
            cache.animation = animation;
            cache.start = ageInTicks;
        }
        float weight = Mth.clamp((ageInTicks - cache.start) / BLEND_TICKS, 0.0F, 1.0F);
        if (weight < 1.0F) {
            int i = 0;
            for (ModelPart part : this.parts) {
                part.x = Mth.lerp(weight, cache.from[i], cache.target[i]);
                part.y = Mth.lerp(weight, cache.from[i + 1], cache.target[i + 1]);
                part.z = Mth.lerp(weight, cache.from[i + 2], cache.target[i + 2]);
                part.xRot = Mth.lerp(weight, cache.from[i + 3], cache.target[i + 3]);
                part.yRot = Mth.lerp(weight, cache.from[i + 4], cache.target[i + 4]);
                part.zRot = Mth.lerp(weight, cache.from[i + 5], cache.target[i + 5]);
                part.xScale = Mth.lerp(weight, cache.from[i + 6], cache.target[i + 6]);
                part.yScale = Mth.lerp(weight, cache.from[i + 7], cache.target[i + 7]);
                part.zScale = Mth.lerp(weight, cache.from[i + 8], cache.target[i + 8]);
                i += 9;
            }
        }
        this.readPose(cache.rendered);
    }

    private void readPose(float[] pose) {
        int i = 0;
        for (ModelPart part : this.parts) {
            pose[i++] = part.x;
            pose[i++] = part.y;
            pose[i++] = part.z;
            pose[i++] = part.xRot;
            pose[i++] = part.yRot;
            pose[i++] = part.zRot;
            pose[i++] = part.xScale;
            pose[i++] = part.yScale;
            pose[i++] = part.zScale;
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    private static final class PoseCache {

        private final float[] target;
        private final float[] rendered;
        private final float[] from;
        private int animation = -1;
        private float start;

        private PoseCache(int size) {
            this.target = new float[size];
            this.rendered = new float[size];
            this.from = new float[size];
        }
    }
}
