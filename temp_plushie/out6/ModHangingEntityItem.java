package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.common.entities.ModEntityType;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class ModHangingEntityItem extends Item {
    private static final Component TOOLTIP_RANDOM_VARIANT = Component.m_237115_("painting.random").m_130940_(ChatFormatting.GRAY);
    private final Supplier<EntityType<? extends HangingEntity>> type;

    public ModHangingEntityItem(Supplier<EntityType<? extends HangingEntity>> p_41324_, Item.Properties p_41325_) {
        super(p_41325_);
        this.type = p_41324_;
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
            HangingEntity hangingentity;
            if (this.type.get() == EntityType.f_20506_) {
                Optional<Painting> optional = Painting.m_218887_(level, blockpos1, direction);
                if (optional.isEmpty()) {
                    return InteractionResult.CONSUME;
                }

                hangingentity = (HangingEntity)optional.get();
            } else if (this.type.get() == EntityType.f_20462_) {
                hangingentity = new ItemFrame(level, blockpos1, direction);
            } else {
                if (this.type.get() != EntityType.f_147033_) {
                    return InteractionResult.m_19078_(level.f_46443_);
                }

                hangingentity = new GlowItemFrame(level, blockpos1, direction);
            }

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

    protected boolean mayPlace(Player p_41326_, Direction p_41327_, ItemStack p_41328_, BlockPos p_41329_) {
        return !p_41327_.m_122434_().m_122478_() && p_41326_.m_36204_(p_41329_, p_41327_, p_41328_);
    }

    public void m_7373_(ItemStack p_270235_, @Nullable Level p_270688_, List<Component> p_270630_, TooltipFlag p_270170_) {
        super.m_7373_(p_270235_, p_270688_, p_270630_, p_270170_);
        if (this.type.get() == EntityType.f_20506_ || this.type.get() == ModEntityType.MOD_PAINTING.get()) {
            CompoundTag compoundtag = p_270235_.m_41783_();
            if (compoundtag != null && compoundtag.m_128425_("EntityTag", 10)) {
                CompoundTag compoundtag1 = compoundtag.m_128469_("EntityTag");
                Painting.m_269030_(compoundtag1).ifPresentOrElse((p_270767_) -> {
                    p_270767_.m_203543_().ifPresent((p_270217_) -> {
                        p_270630_.add(Component.m_237115_(p_270217_.m_135782_().m_269108_("painting", "title")).m_130940_(ChatFormatting.YELLOW));
                        p_270630_.add(Component.m_237115_(p_270217_.m_135782_().m_269108_("painting", "author")).m_130940_(ChatFormatting.GRAY));
                    });
                    p_270630_.add(Component.m_237110_("painting.dimensions", new Object[]{Mth.m_184652_(((PaintingVariant)p_270767_.m_203334_()).m_218908_(), 16), Mth.m_184652_(((PaintingVariant)p_270767_.m_203334_()).m_218909_(), 16)}));
                }, () -> p_270630_.add(TOOLTIP_RANDOM_VARIANT));
            } else if (p_270170_.m_257552_()) {
                p_270630_.add(TOOLTIP_RANDOM_VARIANT);
            }
        }

    }
}
