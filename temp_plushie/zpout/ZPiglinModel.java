package com.Polarice3.Goety.client.render.model;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

public class ZPiglinModel<T extends Mob> extends PlayerModel<T> {
    public final ModelPart rightEar = this.f_102808_.m_171324_("right_ear");
    public final ModelPart leftEar = this.f_102808_.m_171324_("left_ear");
    private final PartPose bodyDefault = this.f_102810_.m_171308_();
    private final PartPose headDefault = this.f_102808_.m_171308_();
    private final PartPose leftArmDefault = this.f_102812_.m_171308_();
    private final PartPose rightArmDefault = this.f_102811_.m_171308_();

    public ZPiglinModel(ModelPart p_170810_) {
        super(p_170810_, false);
    }

    public static MeshDefinition createMesh(CubeDeformation p_170812_) {
        MeshDefinition meshdefinition = PlayerModel.m_170825_(p_170812_, false);
        PartDefinition partdefinition = meshdefinition.m_171576_();
        partdefinition.m_171599_("body", CubeListBuilder.m_171558_().m_171514_(16, 16).m_171488_(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_170812_), PartPose.f_171404_);
        PartDefinition partdefinition1 = partdefinition.m_171599_("head", CubeListBuilder.m_171558_().m_171514_(0, 0).m_171488_(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, p_170812_).m_171514_(31, 1).m_171488_(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, p_170812_).m_171514_(2, 4).m_171488_(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, p_170812_).m_171514_(2, 0).m_171488_(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, p_170812_), PartPose.f_171404_);
        partdefinition1.m_171599_("left_ear", CubeListBuilder.m_171558_().m_171514_(51, 6).m_171488_(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, p_170812_), PartPose.m_171423_(4.5F, -6.0F, 0.0F, 0.0F, 0.0F, (-(float)Math.PI / 6F)));
        partdefinition1.m_171599_("right_ear", CubeListBuilder.m_171558_().m_171514_(39, 6).m_171488_(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, p_170812_), PartPose.m_171423_(-4.5F, -6.0F, 0.0F, 0.0F, 0.0F, ((float)Math.PI / 6F)));
        partdefinition.m_171599_("hat", CubeListBuilder.m_171558_(), PartPose.f_171404_);
        return meshdefinition;
    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.m_171565_(createMesh(CubeDeformation.f_171458_), 64, 64);
    }

    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.f_102810_.m_171322_(this.bodyDefault);
        this.f_102808_.m_171322_(this.headDefault);
        this.f_102812_.m_171322_(this.leftArmDefault);
        this.f_102811_.m_171322_(this.rightArmDefault);
        super.m_6973_(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        float f = ((float)Math.PI / 6F);
        float f1 = pAgeInTicks * 0.1F + pLimbSwing * 0.5F;
        float f2 = 0.08F + pLimbSwingAmount * 0.4F;
        this.rightEar.f_104205_ = ((float)Math.PI / 6F) + Mth.m_14089_(f1) * f2;
        this.leftEar.f_104205_ = (-(float)Math.PI / 6F) - Mth.m_14089_(f1 * 1.2F) * f2;
        AnimationUtils.m_102102_(this.f_102812_, this.f_102811_, pEntity.m_5912_(), this.f_102608_, pAgeInTicks);
        this.f_103376_.m_104315_(this.f_102814_);
        this.f_103377_.m_104315_(this.f_102813_);
        this.f_103374_.m_104315_(this.f_102812_);
        this.f_103375_.m_104315_(this.f_102811_);
        this.f_103378_.m_104315_(this.f_102810_);
        this.f_102809_.m_104315_(this.f_102808_);
    }
}
