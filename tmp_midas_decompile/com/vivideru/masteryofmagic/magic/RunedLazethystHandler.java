/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IFocus
 *  com.Polarice3.Goety.api.magic.IBlockSpell
 *  com.Polarice3.Goety.api.magic.ISpell
 *  com.Polarice3.Goety.api.magic.ITouchSpell
 *  com.Polarice3.Goety.common.magic.Spell
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.registries.ForgeRegistries
 */
package com.vivideru.masteryofmagic.magic;

import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.api.magic.IBlockSpell;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.api.magic.ITouchSpell;
import com.Polarice3.Goety.common.magic.Spell;
import com.vivideru.masteryofmagic.block.entity.ChargedCryptRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedDeepRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedFrostRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedGeomancyRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedNetherRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedOminousRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedSkyRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedStormRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedVoidRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedWildRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.config.RunedBlocksConfig;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlocks;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class RunedLazethystHandler {
    private static final ResourceLocation WITHER_SKULL_FOCUS = new ResourceLocation("goety", "wither_skull_focus");

    private static boolean isFocusBlacklisted(ResourceLocation focusId) {
        if (focusId == null) {
            return true;
        }
        if (((List)RunedBlocksConfig.FOCUS_BLACKLIST.get()).contains(focusId.toString())) {
            return true;
        }
        return focusId.equals((Object)WITHER_SKULL_FOCUS);
    }

    public static InteractionResult handleUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        BlockEntity blockEntity;
        if (level.f_46443_) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = player.m_21120_(hand);
        if (stack.m_150930_(Items.f_42409_)) {
            BlockEntity blockEntity2 = level.m_7702_(pos);
            if (blockEntity2 instanceof ChargedFrostRunedLazethystBlockEntity) {
                ChargedFrostRunedLazethystBlockEntity be = (ChargedFrostRunedLazethystBlockEntity)blockEntity2;
                be.ownerUUID = new UUID(0L, 776L);
            } else if (blockEntity2 instanceof ChargedStormRunedLazethystBlockEntity) {
                ChargedStormRunedLazethystBlockEntity be = (ChargedStormRunedLazethystBlockEntity)blockEntity2;
                be.ownerUUID = new UUID(0L, 776L);
            } else if (blockEntity2 instanceof ChargedWildRunedLazethystBlockEntity) {
                ChargedWildRunedLazethystBlockEntity be = (ChargedWildRunedLazethystBlockEntity)blockEntity2;
                be.ownerUUID = new UUID(0L, 776L);
            } else if (blockEntity2 instanceof ChargedSkyRunedLazethystBlockEntity) {
                ChargedSkyRunedLazethystBlockEntity be = (ChargedSkyRunedLazethystBlockEntity)blockEntity2;
                be.ownerUUID = new UUID(0L, 776L);
            } else if (blockEntity2 instanceof ChargedDeepRunedLazethystBlockEntity) {
                ChargedDeepRunedLazethystBlockEntity be = (ChargedDeepRunedLazethystBlockEntity)blockEntity2;
                be.ownerUUID = new UUID(0L, 776L);
            } else if (blockEntity2 instanceof ChargedGeomancyRunedLazethystBlockEntity) {
                ChargedGeomancyRunedLazethystBlockEntity be = (ChargedGeomancyRunedLazethystBlockEntity)blockEntity2;
                be.ownerUUID = new UUID(0L, 776L);
            } else if (blockEntity2 instanceof ChargedNetherRunedLazethystBlockEntity) {
                ChargedNetherRunedLazethystBlockEntity be = (ChargedNetherRunedLazethystBlockEntity)blockEntity2;
                be.ownerUUID = new UUID(0L, 776L);
            } else if (blockEntity2 instanceof ChargedVoidRunedLazethystBlockEntity) {
                ChargedVoidRunedLazethystBlockEntity be = (ChargedVoidRunedLazethystBlockEntity)blockEntity2;
                be.ownerUUID = new UUID(0L, 776L);
            } else if (blockEntity2 instanceof ChargedCryptRunedLazethystBlockEntity) {
                ChargedCryptRunedLazethystBlockEntity be = (ChargedCryptRunedLazethystBlockEntity)blockEntity2;
                be.ownerUUID = new UUID(0L, 776L);
            } else if (blockEntity2 instanceof ChargedOminousRunedLazethystBlockEntity) {
                ChargedOminousRunedLazethystBlockEntity be = (ChargedOminousRunedLazethystBlockEntity)blockEntity2;
                be.ownerUUID = new UUID(0L, 776L);
            } else if (blockEntity2 instanceof ChargedRunedLazethystBlockEntity) {
                ChargedRunedLazethystBlockEntity be = (ChargedRunedLazethystBlockEntity)blockEntity2;
                be.ownerUUID = new UUID(0L, 776L);
            } else {
                return InteractionResult.PASS;
            }
            blockEntity2.m_6596_();
            level.m_7260_(pos, state, state, 3);
            level.m_5594_(null, pos, SoundEvents.f_11705_, SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.CONSUME;
        }
        if (stack.m_150930_((Item)GoetyMasteryOfMagicModItems.PERMA_LAZETHYST.get())) {
            BlockEntity blockEntity3 = level.m_7702_(pos);
            if (blockEntity3 instanceof ChargedFrostRunedLazethystBlockEntity) {
                ChargedFrostRunedLazethystBlockEntity be = (ChargedFrostRunedLazethystBlockEntity)blockEntity3;
                be.ISDUNGEON = true;
            } else if (blockEntity3 instanceof ChargedStormRunedLazethystBlockEntity) {
                ChargedStormRunedLazethystBlockEntity be = (ChargedStormRunedLazethystBlockEntity)blockEntity3;
                be.ISDUNGEON = true;
            } else if (blockEntity3 instanceof ChargedWildRunedLazethystBlockEntity) {
                ChargedWildRunedLazethystBlockEntity be = (ChargedWildRunedLazethystBlockEntity)blockEntity3;
                be.ISDUNGEON = true;
            } else if (blockEntity3 instanceof ChargedSkyRunedLazethystBlockEntity) {
                ChargedSkyRunedLazethystBlockEntity be = (ChargedSkyRunedLazethystBlockEntity)blockEntity3;
                be.ISDUNGEON = true;
            } else if (blockEntity3 instanceof ChargedDeepRunedLazethystBlockEntity) {
                ChargedDeepRunedLazethystBlockEntity be = (ChargedDeepRunedLazethystBlockEntity)blockEntity3;
                be.ISDUNGEON = true;
            } else if (blockEntity3 instanceof ChargedGeomancyRunedLazethystBlockEntity) {
                ChargedGeomancyRunedLazethystBlockEntity be = (ChargedGeomancyRunedLazethystBlockEntity)blockEntity3;
                be.ISDUNGEON = true;
            } else if (blockEntity3 instanceof ChargedNetherRunedLazethystBlockEntity) {
                ChargedNetherRunedLazethystBlockEntity be = (ChargedNetherRunedLazethystBlockEntity)blockEntity3;
                be.ISDUNGEON = true;
            } else if (blockEntity3 instanceof ChargedVoidRunedLazethystBlockEntity) {
                ChargedVoidRunedLazethystBlockEntity be = (ChargedVoidRunedLazethystBlockEntity)blockEntity3;
                be.ISDUNGEON = true;
            } else if (blockEntity3 instanceof ChargedCryptRunedLazethystBlockEntity) {
                ChargedCryptRunedLazethystBlockEntity be = (ChargedCryptRunedLazethystBlockEntity)blockEntity3;
                be.ISDUNGEON = true;
            } else if (blockEntity3 instanceof ChargedOminousRunedLazethystBlockEntity) {
                ChargedOminousRunedLazethystBlockEntity be = (ChargedOminousRunedLazethystBlockEntity)blockEntity3;
                be.ISDUNGEON = true;
            } else if (blockEntity3 instanceof ChargedRunedLazethystBlockEntity) {
                ChargedRunedLazethystBlockEntity be = (ChargedRunedLazethystBlockEntity)blockEntity3;
                be.ISDUNGEON = true;
            } else {
                return InteractionResult.PASS;
            }
            blockEntity3.m_6596_();
            level.m_7260_(pos, state, state, 3);
            level.m_5594_(null, pos, SoundEvents.f_11705_, SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.CONSUME;
        }
        Item be = stack.m_41720_();
        if (!(be instanceof IFocus)) {
            return InteractionResult.PASS;
        }
        IFocus focus = (IFocus)be;
        ISpell iSpell = focus.getSpell();
        if (!(iSpell instanceof Spell)) {
            return InteractionResult.PASS;
        }
        Spell spell = (Spell)iSpell;
        if (spell instanceof IBlockSpell || spell instanceof ITouchSpell) {
            return InteractionResult.FAIL;
        }
        ResourceLocation blockId = BuiltInRegistries.f_256975_.m_7981_((Object)state.m_60734_());
        School school = RunedLazethystHandler.getSchoolFromBlock(blockId);
        Block charged = RunedLazethystHandler.getChargedBlockForSchool(school);
        if (charged == null) {
            return InteractionResult.PASS;
        }
        ResourceLocation focusId = BuiltInRegistries.f_257033_.m_7981_((Object)stack.m_41720_());
        if (RunedLazethystHandler.isFocusBlacklisted(focusId)) {
            return InteractionResult.CONSUME;
        }
        level.m_5594_(null, pos, SoundEvents.f_11887_, SoundSource.BLOCKS, 1.0f, 1.2f);
        level.m_7731_(pos, charged.m_49966_(), 3);
        if (school == School.NETHER && (blockEntity = level.m_7702_(pos)) instanceof ChargedNetherRunedLazethystBlockEntity) {
            ChargedNetherRunedLazethystBlockEntity be2 = (ChargedNetherRunedLazethystBlockEntity)blockEntity;
            be2.applyFocus(stack, player);
        } else if (school == School.CRYPT && (blockEntity = level.m_7702_(pos)) instanceof ChargedCryptRunedLazethystBlockEntity) {
            ChargedCryptRunedLazethystBlockEntity be3 = (ChargedCryptRunedLazethystBlockEntity)blockEntity;
            be3.applyFocus(stack, player);
        } else if (school == School.VOID && (blockEntity = level.m_7702_(pos)) instanceof ChargedVoidRunedLazethystBlockEntity) {
            ChargedVoidRunedLazethystBlockEntity be4 = (ChargedVoidRunedLazethystBlockEntity)blockEntity;
            be4.applyFocus(stack, player);
        } else if (school == School.SKY && (blockEntity = level.m_7702_(pos)) instanceof ChargedSkyRunedLazethystBlockEntity) {
            ChargedSkyRunedLazethystBlockEntity be5 = (ChargedSkyRunedLazethystBlockEntity)blockEntity;
            be5.applyFocus(stack, player);
        } else if (school == School.DEEP && (blockEntity = level.m_7702_(pos)) instanceof ChargedDeepRunedLazethystBlockEntity) {
            ChargedDeepRunedLazethystBlockEntity be6 = (ChargedDeepRunedLazethystBlockEntity)blockEntity;
            be6.applyFocus(stack, player);
        } else if (school == School.STORM && (blockEntity = level.m_7702_(pos)) instanceof ChargedStormRunedLazethystBlockEntity) {
            ChargedStormRunedLazethystBlockEntity be7 = (ChargedStormRunedLazethystBlockEntity)blockEntity;
            be7.applyFocus(stack, player);
        } else if (school == School.GEOMANCY && (blockEntity = level.m_7702_(pos)) instanceof ChargedGeomancyRunedLazethystBlockEntity) {
            ChargedGeomancyRunedLazethystBlockEntity be8 = (ChargedGeomancyRunedLazethystBlockEntity)blockEntity;
            be8.applyFocus(stack, player);
        } else if (school == School.WILD && (blockEntity = level.m_7702_(pos)) instanceof ChargedWildRunedLazethystBlockEntity) {
            ChargedWildRunedLazethystBlockEntity be9 = (ChargedWildRunedLazethystBlockEntity)blockEntity;
            be9.applyFocus(stack, player);
        } else if (school == School.OMINOUS && (blockEntity = level.m_7702_(pos)) instanceof ChargedOminousRunedLazethystBlockEntity) {
            ChargedOminousRunedLazethystBlockEntity be10 = (ChargedOminousRunedLazethystBlockEntity)blockEntity;
            be10.applyFocus(stack, player);
        } else if (school == School.FROST && (blockEntity = level.m_7702_(pos)) instanceof ChargedFrostRunedLazethystBlockEntity) {
            ChargedFrostRunedLazethystBlockEntity be11 = (ChargedFrostRunedLazethystBlockEntity)blockEntity;
            be11.applyFocus(stack, player);
        } else {
            blockEntity = level.m_7702_(pos);
            if (blockEntity instanceof ChargedRunedLazethystBlockEntity) {
                ChargedRunedLazethystBlockEntity be12 = (ChargedRunedLazethystBlockEntity)blockEntity;
                be12.applyFocus(stack, player);
            }
        }
        return InteractionResult.CONSUME;
    }

    public static School getSchoolFromBlock(ResourceLocation blockId) {
        if (blockId == null) {
            return School.UNKNOWN;
        }
        String path = blockId.m_135815_();
        if (path.startsWith("frost_")) {
            return School.FROST;
        }
        if (path.startsWith("storm_")) {
            return School.STORM;
        }
        if (path.startsWith("wild_")) {
            return School.WILD;
        }
        if (path.startsWith("wind_")) {
            return School.SKY;
        }
        if (path.startsWith("sky_")) {
            return School.SKY;
        }
        if (path.startsWith("geomancy_")) {
            return School.GEOMANCY;
        }
        if (path.startsWith("nether_")) {
            return School.NETHER;
        }
        if (path.startsWith("void_")) {
            return School.VOID;
        }
        if (path.startsWith("crypt_")) {
            return School.CRYPT;
        }
        if (path.startsWith("ominous_")) {
            return School.OMINOUS;
        }
        if (path.startsWith("deep_")) {
            return School.DEEP;
        }
        return School.UNKNOWN;
    }

    public static Block getChargedBlockForSchool(School school) {
        return switch (school) {
            case School.FROST -> (Block)GoetyMasteryOfMagicModBlocks.FROST_RUNED_LAZETHYST_BLOCK_CHARGED.get();
            case School.STORM -> (Block)GoetyMasteryOfMagicModBlocks.STORM_RUNED_LAZETHYST_BLOCK_CHARGED.get();
            case School.WILD -> (Block)GoetyMasteryOfMagicModBlocks.WILD_RUNED_LAZETHYST_BLOCK_CHARGED.get();
            case School.SKY -> (Block)GoetyMasteryOfMagicModBlocks.SKY_RUNED_LAZETHYST_BLOCK_CHARGED.get();
            case School.GEOMANCY -> (Block)GoetyMasteryOfMagicModBlocks.GEOMANCY_RUNED_LAZETHYST_BLOCK_CHARGED.get();
            case School.NETHER -> (Block)GoetyMasteryOfMagicModBlocks.NETHER_RUNED_LAZETHYST_BLOCK_CHARGED.get();
            case School.VOID -> (Block)GoetyMasteryOfMagicModBlocks.VOID_RUNED_LAZETHYST_BLOCK_CHARGED.get();
            case School.CRYPT -> (Block)GoetyMasteryOfMagicModBlocks.CRYPT_RUNED_LAZETHYST_BLOCK_CHARGED.get();
            case School.OMINOUS -> (Block)GoetyMasteryOfMagicModBlocks.OMINOUS_RUNED_LAZETHYST_BLOCK_CHARGED.get();
            case School.DEEP -> (Block)GoetyMasteryOfMagicModBlocks.DEEP_RUNED_LAZETHYST_BLOCK_CHARGED.get();
            default -> null;
        };
    }

    public static ItemStack createWandForSchool(School school) {
        ResourceLocation wandId = switch (school) {
            case School.FROST -> new ResourceLocation("goety", "frost_staff");
            case School.STORM -> new ResourceLocation("goety", "storm_staff");
            case School.WILD -> new ResourceLocation("goety", "wild_staff");
            case School.SKY -> new ResourceLocation("goety", "wind_staff");
            case School.GEOMANCY -> new ResourceLocation("goety", "geo_staff");
            case School.NETHER -> new ResourceLocation("goety", "nether_staff");
            case School.VOID -> new ResourceLocation("goety", "void_staff");
            case School.CRYPT -> new ResourceLocation("goety", "nameless_staff");
            case School.OMINOUS -> new ResourceLocation("goety", "ominous_staff");
            case School.DEEP -> new ResourceLocation("goety", "abyss_staff");
            default -> new ResourceLocation("goety", "dark_wand");
        };
        Item item = (Item)ForgeRegistries.ITEMS.getValue(wandId);
        if (item == null) {
            item = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("goety", "dark_wand"));
        }
        return new ItemStack((ItemLike)item);
    }

    private RunedLazethystHandler() {
    }

    public static enum School {
        DEEP,
        FROST,
        GEOMANCY,
        STORM,
        WILD,
        SKY,
        VOID,
        NETHER,
        CRYPT,
        OMINOUS,
        UNKNOWN;

    }
}

