package com.qiuyue.goetyominous.client.render.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class HandheldSeparateTransformsModel implements IUnbakedGeometry<HandheldSeparateTransformsModel> {

    private final BlockModel baseModel;
    private final ImmutableMap<ItemDisplayContext, BlockModel> perspectives;

    public HandheldSeparateTransformsModel(BlockModel baseModel, ImmutableMap<ItemDisplayContext, BlockModel> perspectives) {
        this.baseModel = baseModel;
        this.perspectives = perspectives;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
                           Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState,
                           ItemOverrides overrides, ResourceLocation modelLocation) {
        BakedModel bakedBase = this.baseModel.bake(baker, this.baseModel, spriteGetter, modelState,
                modelLocation, context.useBlockLight());
        ImmutableMap<ItemDisplayContext, BakedModel> bakedPerspectives = ImmutableMap.copyOf(
                Maps.transformValues(this.perspectives, value -> value.bake(baker, value, spriteGetter,
                        modelState, modelLocation, context.useBlockLight())));
        TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
        return new Baked(context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(),
                particle, overrides, bakedBase, bakedPerspectives);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        this.baseModel.resolveParents(modelGetter);
        this.perspectives.values().forEach(model -> model.resolveParents(modelGetter));
    }

    public static class Baked implements IDynamicBakedModel {
        private final boolean isAmbientOcclusion;
        private final boolean isGui3d;
        private final boolean isSideLit;
        private final TextureAtlasSprite particle;
        private final ItemOverrides overrides;
        private final BakedModel baseModel;
        private final ImmutableMap<ItemDisplayContext, BakedModel> perspectives;

        public Baked(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit,
                     TextureAtlasSprite particle, ItemOverrides overrides, BakedModel baseModel,
                     ImmutableMap<ItemDisplayContext, BakedModel> perspectives) {
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.isSideLit = isSideLit;
            this.particle = particle;
            this.overrides = overrides;
            this.baseModel = baseModel;
            this.perspectives = perspectives;
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side,
                                                 @NotNull RandomSource rand, @NotNull ModelData data,
                                                 @Nullable RenderType renderType) {
            return this.baseModel.getQuads(state, side, rand, data, renderType);
        }

        @Override
        public boolean useAmbientOcclusion() {
            return this.isAmbientOcclusion;
        }

        @Override
        public boolean isGui3d() {
            return this.isGui3d;
        }

        @Override
        public boolean usesBlockLight() {
            return this.isSideLit;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return this.particle;
        }

        @Override
        public ItemOverrides getOverrides() {
            return this.overrides;
        }

        @Override
        public ItemTransforms getTransforms() {
            return this.baseModel.getTransforms();
        }

        @Override
        public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack,
                                         boolean applyLeftHandTransform) {
            if (this.perspectives.containsKey(transformType)) {
                return this.perspectives.get(transformType)
                        .applyTransform(transformType, poseStack, applyLeftHandTransform);
            }
            return this.baseModel.applyTransform(transformType, poseStack, applyLeftHandTransform);
        }

        @Override
        public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand,
                                                 @NotNull ModelData data) {
            return this.baseModel.getRenderTypes(state, rand, data);
        }
    }

    public static final class Loader implements IGeometryLoader<HandheldSeparateTransformsModel> {

        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @Override
        public HandheldSeparateTransformsModel read(JsonObject jsonObject,
                                                    JsonDeserializationContext deserializationContext)
                throws JsonParseException {
            BlockModel baseModel = deserializationContext.deserialize(
                    GsonHelper.getAsJsonObject(jsonObject, "base"), BlockModel.class);
            JsonObject perspectiveData = GsonHelper.getAsJsonObject(jsonObject, "perspectives");
            Map<ItemDisplayContext, BlockModel> perspectives = new HashMap<>();
            for (ItemDisplayContext transform : ItemDisplayContext.values()) {
                if (!perspectiveData.has(transform.getSerializedName())) {
                    continue;
                }
                BlockModel perspectiveModel = deserializationContext.deserialize(
                        GsonHelper.getAsJsonObject(perspectiveData, transform.getSerializedName()),
                        BlockModel.class);
                perspectives.put(transform, perspectiveModel);
            }
            return new HandheldSeparateTransformsModel(baseModel, ImmutableMap.copyOf(perspectives));
        }
    }
}
