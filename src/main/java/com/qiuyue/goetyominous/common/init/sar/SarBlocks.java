package com.qiuyue.goetyominous.common.init.sar;

import com.qiuyue.goetyominous.compat.mod.SavageRavageCompat;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;

public class SarBlocks {

    private static Block sporeBomb = null;
    private static Block blastProofPlate = null;
    private static Block gloomyTiles = null;
    private static Block chiseledGloomyTiles = null;
    private static boolean initialized = false;

    public static void init() {
        if (initialized || !SavageRavageCompat.isSavageRavageLoaded()) {
            return;
        }

        try {
            sporeBomb = ForgeRegistries.BLOCKS.getValue(
                    new ResourceLocation("savage_and_ravage", "spore_bomb")
            );
            blastProofPlate = ForgeRegistries.BLOCKS.getValue(
                    new ResourceLocation("savage_and_ravage", "blast_proof_plates")
            );
            gloomyTiles = ForgeRegistries.BLOCKS.getValue(
                    new ResourceLocation("savage_and_ravage", "gloomy_tiles")
            );
            chiseledGloomyTiles = ForgeRegistries.BLOCKS.getValue(
                    new ResourceLocation("savage_and_ravage", "chiseled_gloomy_tiles")
            );
            initialized = true;
        } catch (Exception e) {
            System.err.println("[SarBlocks] Failed to initialize SAR blocks: " + e.getMessage());
        }
    }

    public static Block getSPORE_BOMB() {
        if (!initialized) init();
        return sporeBomb;
    }

    public static Block getBLAST_PROOF_PLATE() {
        if (!initialized) init();
        return blastProofPlate;
    }

    public static Block getGLOOMY_TILES() {
        if (!initialized) init();
        return gloomyTiles;
    }

    public static Block getCHISELED_GLOOMY_TILES() {
        if (!initialized) init();
        return chiseledGloomyTiles;
    }
}
