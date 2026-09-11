package com.Polarice3.Goety.common.items.block;

import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.deco.HauntedArmorStand;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HauntedArmorStandItem extends Item {
    public HauntedArmorStandItem() {
        super((new Item.Properties()).m_41487_(16));
    }

    public InteractionResult m_6225_(UseOnContext p_40510_) {
        Direction direction = p_40510_.m_43719_();
        if (direction == Direction.DOWN) {
            return InteractionResult.FAIL;
        } else {
            Level level = p_40510_.m_43725_();
            BlockPlaceContext blockplacecontext = new BlockPlaceContext(p_40510_);
            BlockPos blockpos = blockplacecontext.m_8083_();
            ItemStack itemstack = p_40510_.m_43722_();
            Vec3 vec3 = Vec3.m_82539_(blockpos);
            AABB aabb = ((EntityType)ModEntityType.HAUNTED_ARMOR_STAND.get()).m_20680_().m_20384_(vec3.m_7096_(), vec3.m_7098_(), vec3.m_7094_());
            if (level.m_45756_((Entity)null, aabb) && level.m_45933_((Entity)null, aabb).isEmpty()) {
                if (level instanceof ServerLevel) {
                    ServerLevel serverlevel = (ServerLevel)level;
                    Consumer<HauntedArmorStand> consumer = EntityType.m_263562_(serverlevel, itemstack, p_40510_.m_43723_());
                    HauntedArmorStand armorStand = (HauntedArmorStand)((EntityType)ModEntityType.HAUNTED_ARMOR_STAND.get()).m_262451_(serverlevel, itemstack.m_41783_(), consumer, blockpos, MobSpawnType.SPAWN_EGG, true, true);
                    if (armorStand == null) {
                        return InteractionResult.FAIL;
                    }

                    float f = (float)Mth.m_14143_((Mth.m_14177_(p_40510_.m_7074_() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    armorStand.m_7678_(armorStand.m_20185_(), armorStand.m_20186_(), armorStand.m_20189_(), f, 0.0F);
                    this.randomizePose(armorStand, level.f_46441_);
                    serverlevel.m_47205_(armorStand);
                    level.m_6263_((Player)null, armorStand.m_20185_(), armorStand.m_20186_(), armorStand.m_20189_(), SoundEvents.f_11684_, SoundSource.BLOCKS, 0.75F, 0.8F);
                    armorStand.m_146852_(GameEvent.f_157810_, p_40510_.m_43723_());
                }

                itemstack.m_41774_(1);
                return InteractionResult.m_19078_(level.f_46443_);
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

    private void randomizePose(ArmorStand p_219999_, RandomSource p_220000_) {
        Rotations rotations = p_219999_.m_31680_();
        float f = p_220000_.m_188501_() * 5.0F;
        float f1 = p_220000_.m_188501_() * 20.0F - 10.0F;
        Rotations rotations1 = new Rotations(rotations.m_123156_() + f, rotations.m_123157_() + f1, rotations.m_123158_());
        p_219999_.m_31597_(rotations1);
        rotations = p_219999_.m_31685_();
        f = p_220000_.m_188501_() * 10.0F - 5.0F;
        rotations1 = new Rotations(rotations.m_123156_(), rotations.m_123157_() + f, rotations.m_123158_());
        p_219999_.m_31616_(rotations1);
    }
}
