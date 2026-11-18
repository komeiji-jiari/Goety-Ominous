package com.qiuyue.goetyominous.common.worldgen.crypt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasContext;
import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CryptWingStructure extends Structure {

    public enum Kind implements StringRepresentable {
        STAIRS("stairs", CryptPlacementRules.Kind.STAIRS),
        BOSS("boss", CryptPlacementRules.Kind.BOSS),
        VAULT("vault", CryptPlacementRules.Kind.VAULT),
        DEEP("deep", CryptPlacementRules.Kind.DEEP);

        public static final Codec<Kind> CODEC = StringRepresentable.fromEnum(Kind::values);
        private final String name;
        public final CryptPlacementRules.Kind rules;

        Kind(String name, CryptPlacementRules.Kind rules) {
            this.name = name;
            this.rules = rules;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    private static final ResourceKey<StructureTemplatePool> CORRIDOR_EXITS = ResourceKey.create(
            Registries.TEMPLATE_POOL, new ResourceLocation("goetyominous", "crypt/corridor_exits"));
    private static final int TERMINAL_ATTEMPTS = 48;

    public static final Codec<CryptWingStructure> CODEC = RecordCodecBuilder.<CryptWingStructure>mapCodec(instance -> instance.group(
            settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
            ResourceLocation.CODEC.fieldOf("start_jigsaw_name").forGetter(s -> s.startJigsawName),
            Codec.intRange(0, 30).fieldOf("size").forGetter(s -> s.maxDepth),
            Codec.intRange(1, 256).fieldOf("max_distance_from_center").forGetter(s -> s.maxDistanceFromCenter),
            Codec.intRange(1, 2).fieldOf("level").forGetter(s -> s.level),
            Codec.intRange(0, 3).fieldOf("door").forGetter(s -> s.door),
            Kind.CODEC.fieldOf("kind").forGetter(s -> s.kind),
            Codec.intRange(1, 32).optionalFieldOf("search_chunks", 12).forGetter(s -> s.searchChunks),
            Codec.STRING.optionalFieldOf("terminal").forGetter(s -> s.terminal),
            ResourceLocation.CODEC.optionalFieldOf("exit_pool").forGetter(s -> s.exitPool)
    ).apply(instance, CryptWingStructure::new)).codec();

    public final Holder<StructureTemplatePool> startPool;
    public final ResourceLocation startJigsawName;
    public final int maxDepth;
    public final int maxDistanceFromCenter;
    public final int level;
    public final int door;
    public final Kind kind;
    public final int searchChunks;
    public final Optional<String> terminal;
    public final Optional<ResourceLocation> exitPool;

    public CryptWingStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, ResourceLocation startJigsawName,
                              int maxDepth, int maxDistanceFromCenter, int level, int door, Kind kind, int searchChunks,
                              Optional<String> terminal, Optional<ResourceLocation> exitPool) {
        super(settings);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.maxDepth = maxDepth;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.level = level;
        this.door = door;
        this.kind = kind;
        this.searchChunks = searchChunks;
        this.terminal = terminal;
        this.exitPool = exitPool;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (CryptGeneration.classic() || this.kind == Kind.STAIRS || this.kind == Kind.DEEP) {
            return Optional.empty();
        }
        CryptResolver resolver = CryptResolver.of(context);
        Optional<CryptPlans.Plan> parent = CryptPlans.withDoorIn(resolver, context.chunkPos(), this.level, this.door, this.searchChunks);
        if (parent.isEmpty()) {
            return Optional.empty();
        }
        CryptPlans.Plan plan = parent.get();
        CryptResolver.Door door = plan.door(this.level, this.door).orElseThrow();
        Rotation rotation = Rotation.NONE;
        for (Rotation candidate : Rotation.values()) {
            if (candidate.rotate(Direction.NORTH) == door.facing().getOpposite()) {
                rotation = candidate;
            }
        }
        BlockPos start = door.target().above();
        List<BoundingBox> obstacles = new ArrayList<>(plan.obstaclesFor(this.level, this.door));
        for (int other = 0; other < CryptPlans.DOORS; other++) {
            if (other != this.door) {
                plan.door(this.level, other).ifPresent(d -> obstacles.add(CryptExits.landingZone(d.target(), d.facing())));
            }
        }
        BoundingBox startBox = this.startBox(this.startPool.value(), context.structureTemplateManager(), start, rotation);
        if (startBox == null || obstacles.stream().anyMatch(o -> o.intersects(startBox))) {
            return Optional.empty();
        }
        PoolAliasLookup aliases = PoolAliasLookup.EMPTY;
        if (this.exitPool.isPresent()) {
            ResourceKey<StructureTemplatePool> exits = ResourceKey.create(Registries.TEMPLATE_POOL, this.exitPool.get());
            aliases = key -> key.equals(CORRIDOR_EXITS) ? exits : key;
        }
        Optional<GenerationStub> stub;
        CryptPlacementRules.forceRotation(rotation);
        try {
            stub = JigsawPlacement.addPieces(context, this.startPool, Optional.of(this.startJigsawName), this.maxDepth,
                    start, false, Optional.empty(), this.maxDistanceFromCenter);
        } finally {
            CryptPlacementRules.clearForcedRotation();
        }
        Rotation forced = rotation;
        PoolAliasLookup aliasLookup = aliases;
        return stub.map(s -> new GenerationStub(s.position(), builder -> {
            List<StructurePiece> best = null;
            CryptPlacementRules.Outcome bestOutcome = CryptPlacementRules.Outcome.NONE;
            int maxAttempts = this.terminal.isPresent() ? TERMINAL_ATTEMPTS : CryptGeneration.ATTEMPTS;
            for (int attempt = 0; attempt < maxAttempts; attempt++) {
                Optional<GenerationStub> retry;
                if (attempt == 0) {
                    retry = Optional.of(s);
                } else {
                    CryptPlacementRules.forceRotation(forced);
                    try {
                        retry = JigsawPlacement.addPieces(CryptGeneration.retryContext(context, attempt), this.startPool,
                                Optional.of(this.startJigsawName), this.maxDepth, start, false, Optional.empty(),
                                this.maxDistanceFromCenter);
                    } finally {
                        CryptPlacementRules.clearForcedRotation();
                    }
                }
                if (retry.isEmpty()) {
                    continue;
                }
                StructurePiecesBuilder temp = new StructurePiecesBuilder();
                CryptPlacementRules.beginWing(context.chunkPos(), obstacles, this.kind.rules, this.terminal.orElse(null),
                        this.maxDepth, plan.towerCenter(), door.facing(), this.level,
                        context.heightAccessor().getMinBuildHeight());
                CryptPlacementRules.Outcome outcome;
                try {
                    retry.get().generator().ifLeft(generator -> PoolAliasContext.run(aliasLookup, () -> generator.accept(temp)));
                } finally {
                    outcome = CryptPlacementRules.end();
                }
                if (best == null || outcome.betterThan(bestOutcome, this.kind.rules)) {
                    best = new ArrayList<>(temp.build().pieces());
                    bestOutcome = outcome;
                }
                if (outcome.satisfies(this.kind.rules)) {
                    break;
                }
            }
            if (best == null) {
                return;
            }
            CryptExits.openExits(best, context.structureTemplateManager(), context.seed());
            CryptExits.closeOpenDoors(best, context.structureTemplateManager(),
                    context.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL), context.seed());
            for (StructurePiece piece : best) {
                builder.addPiece(piece);
            }
        }));
    }

    private BoundingBox startBox(StructureTemplatePool pool, StructureTemplateManager templates, BlockPos start, Rotation rotation) {
        List<StructurePoolElement> elements = pool.getShuffledTemplates(RandomSource.create(0L));
        if (elements.isEmpty()) {
            return null;
        }
        StructurePoolElement element = elements.get(0);
        for (StructureTemplate.StructureBlockInfo info : element.getShuffledJigsawBlocks(templates, start, rotation, RandomSource.create(0L))) {
            if (info.nbt() != null && this.startJigsawName.toString().equals(info.nbt().getString("name"))) {
                BlockPos origin = start.subtract(info.pos().subtract(start));
                BoundingBox box = element.getBoundingBox(templates, origin, rotation);
                return box.moved(0, start.getY() - (box.minY() + element.getGroundLevelDelta()), 0);
            }
        }
        return null;
    }

    @Override
    public StructureType<?> type() {
        return CryptStructures.CRYPT_WING.get();
    }
}
