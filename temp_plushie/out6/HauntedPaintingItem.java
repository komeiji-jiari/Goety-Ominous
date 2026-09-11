package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.deco.HauntedPainting;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class HauntedPaintingItem extends ModHangingEntityItem {
    public HauntedPaintingItem() {
        super(ModEntityType.MOD_PAINTING::get, new Item.Properties());
    }

    public InteractionResult m_6225_(UseOnContext p_41331_) {
        BlockPos blockpos = p_41331_.m_8083_();
        Direction direction = p_41331_.m_43719_();
        BlockPos blockpos1 = blockpos.m_121945_(direction);
        Player player = p_41331_.m_43723_();
        ItemStack itemstack = p_41331_.m_43722_();
        if (player != null && !this.mayPlace(player, direction, itemstack, blockpos1)) {
            return InteractionResult.FAIL;
        } else {
            Level level = p_41331_.m_43725_();
            Optional<HauntedPainting> optional = HauntedPainting.createModded(level, blockpos1, direction);
            if (optional.isEmpty()) {
                return InteractionResult.CONSUME;
            } else {
                HangingEntity hangingentity = (HangingEntity)optional.get();
                CompoundTag compoundtag = itemstack.m_41783_();
                if (compoundtag != null) {
                    EntityType.m_20620_(level, player, hangingentity, compoundtag);
                }

                if (hangingentity.m_7088_()) {
                    if (!level.f_46443_) {
                        hangingentity.m_7084_();
                        level.m_220400_(player, GameEvent.f_157810_, hangingentity.m_20182_());
                        level.m_7967_(hangingentity);
                    }

                    itemstack.m_41774_(1);
                    return InteractionResult.m_19078_(level.f_46443_);
                } else {
                    return InteractionResult.CONSUME;
                }
            }
        }
    }

    protected boolean mayPlace(Player p_41326_, Direction p_41327_, ItemStack p_41328_, BlockPos p_41329_) {
        return !p_41327_.m_122434_().m_122478_() && p_41326_.m_36204_(p_41329_, p_41327_, p_41328_);
    }
}
