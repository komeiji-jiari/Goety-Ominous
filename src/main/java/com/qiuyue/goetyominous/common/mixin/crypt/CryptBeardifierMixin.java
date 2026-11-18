package com.qiuyue.goetyominous.common.mixin.crypt;

import com.qiuyue.goetyominous.common.worldgen.crypt.CryptMound;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Beardifier.class)
public abstract class CryptBeardifierMixin {

    @Redirect(method = "lambda$forStructuresInChunk$2(Lnet/minecraft/world/level/ChunkPos;Lit/unimi/dsi/fastutil/objects/ObjectList;IILit/unimi/dsi/fastutil/objects/ObjectList;Lnet/minecraft/world/level/levelgen/structure/StructureStart;)V", require = 1,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/StructurePiece;isCloseToChunk(Lnet/minecraft/world/level/ChunkPos;I)Z"))
    private static boolean goetyominous$skipCryptSurfacePiece(StructurePiece piece, ChunkPos chunkPos, int distance) {
        if (CryptMound.isSurfacePiece(piece)) {
            return false;
        }
        return piece.isCloseToChunk(chunkPos, distance);
    }
}
