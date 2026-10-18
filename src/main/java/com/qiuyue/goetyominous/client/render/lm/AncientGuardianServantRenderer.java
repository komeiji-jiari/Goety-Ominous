package com.qiuyue.goetyominous.client.render.lm;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.AncientGuardianServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.AncientGuardianServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AncientGuardianServantRenderer extends MobRenderer<AncientGuardianServant, AncientGuardianServantModel<AncientGuardianServant>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/ancient_guardian/ancient_guardian.png");
    private static final ResourceLocation CRACK_TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/ancient_guardian/ancient_guardian_crack.png");

    public AncientGuardianServantRenderer(EntityRendererProvider.Context context) {
        super(context, new AncientGuardianServantModel<>(
                context.bakeLayer(ModEntityLayers.ANCIENT_GUARDIAN_SERVANT_LAYER)), 1.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(AncientGuardianServant entity) {
        return switch (entity.getCrackiness()) {
            case MEDIUM, HIGH -> CRACK_TEXTURE;
            default -> TEXTURE;
        };
    }
}
