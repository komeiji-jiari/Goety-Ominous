package com.qiuyue.goetyominous.common.entities.util;

import com.github.alexmodguy.alexscaves.server.entity.item.DinosaurSpiritEntity;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;

import javax.annotation.Nullable;

public class DinosaurSpiritServant extends DinosaurSpiritEntity {

    public DinosaurSpiritServant(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public DinosaurSpiritServant(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(AcEntityRegistry.DINOSAUR_SPIRIT_SERVANT.get(), level);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Nullable
    public static DinosaurSpiritServant summon(Level level, Vec3 pos, DinosaurType type,
                                               @Nullable Player owner, @Nullable Entity target) {
        DinosaurSpiritServant spirit = AcEntityRegistry.DINOSAUR_SPIRIT_SERVANT.get().create(level);
        if (spirit == null) {
            return null;
        }
        spirit.setPos(pos.x, pos.y, pos.z);
        spirit.setDinosaurType(type);
        if (owner != null) {
            spirit.setPlayerUUID(owner.getUUID());
        }
        if (target != null) {
            spirit.setAttackingEntityId(target.getId());
            spirit.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
        }
        level.addFreshEntity(spirit);
        return spirit;
    }
}
