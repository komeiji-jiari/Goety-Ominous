package com.qiuyue.goetyominous.client.render.layer.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.of.RamblerServantModel;
import com.qiuyue.goetyominous.common.entities.ally.of.RamblerServant;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RamblerServantSkullLayer extends RenderLayer<RamblerServant, RamblerServantModel> {
    private final DrawSelector<RamblerServant, RamblerServantModel> drawSelector;
    private final Function<RamblerServant, ResourceLocation> texture;

    public RamblerServantSkullLayer(RenderLayerParent<RamblerServant, RamblerServantModel> parentModel,
                                    DrawSelector<RamblerServant, RamblerServantModel> drawSelector,
                                    Function<RamblerServant, ResourceLocation> texture) {
        super(parentModel);
        this.drawSelector = drawSelector;
        this.texture = texture;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int i,
                       @NotNull RamblerServant rambler, float f, float g, float h, float j, float k, float l) {
        if (!rambler.isInvisible()) {
            this.onlyDrawSelectedPart();
            VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityTranslucent(this.texture.apply(rambler)));
            this.getParentModel().renderToBuffer(poseStack, vertexconsumer, i,
                    LivingEntityRenderer.getOverlayCoords(rambler, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            this.resetDrawForAllParts();
        }
    }

    private void onlyDrawSelectedPart() {
        ModelPart modelPart = this.drawSelector.getPartToDraw(this.getParentModel());
        this.getParentModel().root().getAllParts().forEach(p -> p.skipDraw = true);
        modelPart.skipDraw = false;
    }

    private void resetDrawForAllParts() {
        this.getParentModel().root().getAllParts().forEach(p -> p.skipDraw = false);
    }

    public interface DrawSelector<T extends RamblerServant, M extends EntityModel<T>> {
        ModelPart getPartToDraw(M var1);
    }
}
