package com.qiuyue.goetyominous.common.worldgen.jigsaw;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasBinding;
import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasBindings;
import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasContext;
import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasLookup;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public final class TrialJigsawStructure extends Structure {

    public static final int MAX_DEPTH = 20;
    public static final int MAX_TOTAL_STRUCTURE_RANGE = 128;
    public static final TagKey<Biome> EXCLUDED_BIOMES = TagKey.create(Registries.BIOME, new ResourceLocation(GoetyOminous.MOD_ID, "trial_chambers_excluded"));
    private static final String IGNORE_WATERLOGGING = "ignore_waterlogging";

    public static final Codec<TrialJigsawStructure> CODEC = ExtraCodecs.validate(
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(Structure::biomes),
                    Codec.simpleMap(MobCategory.CODEC, StructureSpawnOverride.CODEC, StringRepresentable.keys(MobCategory.values())).fieldOf("spawn_overrides").forGetter(Structure::spawnOverrides),
                    GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(Structure::step),
                    StrictFields.optional("terrain_adaptation", TrialTerrainAdaptation.CODEC, TrialTerrainAdaptation.NONE).forGetter(structure -> structure.adaptation),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    StrictFields.optional("start_jigsaw_name", ResourceLocation.CODEC).forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, MAX_DEPTH).fieldOf("size").forGetter(structure -> structure.maxDepth),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter(structure -> structure.useExpansionHack),
                    StrictFields.optional("project_start_to_heightmap", Heightmap.Types.CODEC).forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, MAX_TOTAL_STRUCTURE_RANGE).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                    StrictFields.optional("pool_aliases", PoolAliasBindings.bindings().listOf(), List.of()).forGetter(structure -> structure.poolAliases),
                    StrictFields.optional("dimension_padding", TrialDimensionPadding.CODEC, TrialDimensionPadding.ZERO).forGetter(structure -> structure.dimensionPadding),
                    StrictFields.optional("liquid_settings", Codec.STRING, IGNORE_WATERLOGGING).forGetter(structure -> structure.liquidSettings)
            ).apply(instance, TrialJigsawStructure::new)),
            TrialJigsawStructure::verify
    ).codec();

    private final TrialTerrainAdaptation adaptation;
    private final Holder<StructureTemplatePool> startPool;
    private final Optional<ResourceLocation> startJigsawName;
    private final int maxDepth;
    private final HeightProvider startHeight;
    private final boolean useExpansionHack;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final int maxDistanceFromCenter;
    private final List<PoolAliasBinding> poolAliases;
    private final TrialDimensionPadding dimensionPadding;
    private final String liquidSettings;

    public TrialJigsawStructure(HolderSet<Biome> biomes, Map<MobCategory, StructureSpawnOverride> spawnOverrides,
                                GenerationStep.Decoration step, TrialTerrainAdaptation adaptation,
                                Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName,
                                int maxDepth, HeightProvider startHeight, boolean useExpansionHack,
                                Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter,
                                List<PoolAliasBinding> poolAliases, TrialDimensionPadding dimensionPadding,
                                String liquidSettings) {
        super(new Structure.StructureSettings(biomes, spawnOverrides, step, adaptation.vanilla()));
        this.adaptation = adaptation;
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = maxDepth;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.poolAliases = List.copyOf(poolAliases);
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
    }

    private static DataResult<TrialJigsawStructure> verify(TrialJigsawStructure structure) {
        if (!IGNORE_WATERLOGGING.equals(structure.liquidSettings)) {
            return DataResult.error(() -> "liquid_settings '" + structure.liquidSettings + "' is not supported");
        }
        int margin = structure.adaptation.hasMargin() ? 12 : 0;
        return structure.maxDistanceFromCenter + margin > MAX_TOTAL_STRUCTURE_RANGE
                ? DataResult.error(() -> "Structure size including terrain adaptation must not exceed " + MAX_TOTAL_STRUCTURE_RANGE)
                : DataResult.success(structure);
    }

    public boolean encapsulates() {
        return this.adaptation == TrialTerrainAdaptation.ENCAPSULATE;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        ChunkPos chunk = context.chunkPos();
        int y = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos startPos = new BlockPos(chunk.getMinBlockX(), y, chunk.getMinBlockZ());
        Optional<Structure.GenerationStub> stub = TrialJigsawContext.withPriorities(() -> JigsawPlacement.addPieces(
                context, this.startPool, this.startJigsawName, this.maxDepth, startPos,
                this.useExpansionHack, this.projectStartToHeightmap, this.maxDistanceFromCenter));
        if (stub.isEmpty() || excludedBiome(context, stub.get().position())) {
            return Optional.empty();
        }
        PoolAliasLookup aliases = PoolAliasLookup.create(this.poolAliases, startPos, context.seed());
        TrialDimensionPadding padding = this.dimensionPadding;
        LevelHeightAccessor level = context.heightAccessor();
        Structure.GenerationStub original = stub.get();
        return Optional.of(new Structure.GenerationStub(original.position(), original.generator().mapLeft(
                generator -> (Consumer<StructurePiecesBuilder>) builder -> TrialJigsawContext.run(padding,
                        () -> PoolAliasContext.run(aliases, () -> {
                            if (padding.isZero()) {
                                generator.accept(builder);
                                return;
                            }
                            StructurePiecesBuilder scratch = new StructurePiecesBuilder();
                            generator.accept(scratch);
                            PiecesContainer pieces = scratch.build();
                            if (pieces.isEmpty() || padding.startTooClose(level, pieces.pieces().get(0).getBoundingBox())) {
                                return;
                            }
                            for (StructurePiece piece : pieces.pieces()) {
                                builder.addPiece(piece);
                            }
                        })))));
    }

    private static boolean excludedBiome(Structure.GenerationContext context, BlockPos pos) {
        return context.biomeSource().getNoiseBiome(
                QuartPos.fromBlock(pos.getX()),
                QuartPos.fromBlock(pos.getY()),
                QuartPos.fromBlock(pos.getZ()),
                context.randomState().sampler()
        ).is(EXCLUDED_BIOMES);
    }

    @Override
    public BoundingBox adjustBoundingBox(BoundingBox box) {
        return this.encapsulates() ? box.inflatedBy(12) : super.adjustBoundingBox(box);
    }

    @Override
    public StructureType<?> type() {
        return TrialJigsaw.TRIAL_JIGSAW.get();
    }
}
