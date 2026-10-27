package com.qiuyue.goetyominous.common.worldgen.jigsaw;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public final class EncapsulatingBeardifier extends Beardifier {

    private final Beardifier vanilla;
    private final List<BoundingBox> boxes;
    private final List<JigsawJunction> junctions;

    private EncapsulatingBeardifier(Beardifier vanilla, List<BoundingBox> boxes, List<JigsawJunction> junctions) {
        super(new ObjectArrayList<Beardifier.Rigid>().iterator(), new ObjectArrayList<JigsawJunction>().iterator());
        this.vanilla = vanilla;
        this.boxes = boxes;
        this.junctions = junctions;
    }

    public static Beardifier wrap(Beardifier vanilla, List<StructureStart> starts, ChunkPos chunk) {
        int minX = chunk.getMinBlockX();
        int minZ = chunk.getMinBlockZ();
        List<BoundingBox> boxes = new ArrayList<>();
        List<JigsawJunction> junctions = new ArrayList<>();
        for (StructureStart start : starts) {
            for (StructurePiece piece : start.getPieces()) {
                if (!piece.isCloseToChunk(chunk, 12)) {
                    continue;
                }
                if (piece instanceof PoolElementStructurePiece element) {
                    if (element.getElement().getProjection() == StructureTemplatePool.Projection.RIGID) {
                        boxes.add(element.getBoundingBox());
                    }
                    for (JigsawJunction junction : element.getJunctions()) {
                        int x = junction.getSourceX();
                        int z = junction.getSourceZ();
                        if (x <= minX - 12 || z <= minZ - 12 || x >= minX + 15 + 12 || z >= minZ + 15 + 12) {
                            continue;
                        }
                        junctions.add(junction);
                    }
                    continue;
                }
                boxes.add(piece.getBoundingBox());
            }
        }
        return boxes.isEmpty() && junctions.isEmpty() ? vanilla : new EncapsulatingBeardifier(vanilla, List.copyOf(boxes), List.copyOf(junctions));
    }

    public static double encapsulate(BoundingBox box, int x, int y, int z) {
        int dx = Math.max(0, Math.max(box.minX() - x, x - box.maxX()));
        int dy = Math.max(0, Math.max(box.minY() - y, y - box.maxY()));
        int dz = Math.max(0, Math.max(box.minZ() - z, z - box.maxZ()));
        return Mth.clampedMap(Mth.length(dx / 2.0D, dy / 2.0D, dz / 2.0D), 0.0D, 6.0D, 1.0D, 0.0D) * 0.8D;
    }

    @Override
    public double compute(DensityFunction.FunctionContext context) {
        int x = context.blockX();
        int y = context.blockY();
        int z = context.blockZ();
        double density = this.vanilla.compute(context);
        for (BoundingBox box : this.boxes) {
            density += encapsulate(box, x, y, z);
        }
        for (JigsawJunction junction : this.junctions) {
            int dy = y - junction.getSourceGroundY();
            density += getBeardContribution(x - junction.getSourceX(), dy, z - junction.getSourceZ(), dy) * 0.4D;
        }
        return density;
    }
}
