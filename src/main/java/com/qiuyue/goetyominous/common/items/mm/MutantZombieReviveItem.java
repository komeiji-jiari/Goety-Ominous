package com.qiuyue.goetyominous.common.items.mm;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import com.Polarice3.Goety.common.ritual.RitualRequirements;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.qiuyue.goetyominous.common.entities.ally.mm.AbstractMutantServant;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class MutantZombieReviveItem extends ReviveServantItem {

    private final Supplier<? extends EntityType<? extends Owned>> servantType;
    private final Class<? extends Owned> hostType;
    private final EntityType<? extends LivingEntity> vanillaHost;

    public MutantZombieReviveItem(Supplier<? extends EntityType<? extends Owned>> servantType, Class<? extends Owned> hostType) {
        this(servantType, hostType, null);
    }

    public MutantZombieReviveItem(Supplier<? extends EntityType<? extends Owned>> servantType, Class<? extends Owned> hostType,
                                  EntityType<? extends LivingEntity> vanillaHost) {
        super(new Properties()
                .rarity(Rarity.UNCOMMON)
                .setNoRepair()
                .stacksTo(1));
        this.servantType = servantType;
        this.hostType = hostType;
        this.vanillaHost = vanillaHost;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        boolean ownedServant = this.hostType.isInstance(target) && ((Owned) target).getTrueOwner() == player;
        boolean vanillaHost = this.vanillaHost != null && target.getType() == this.vanillaHost;
        if (!ownedServant && !vanillaHost) {
            return super.interactLivingEntity(stack, player, target, hand);
        }
        Level level = player.level();
        if (level.isClientSide) {
            return super.interactLivingEntity(stack, player, target, hand);
        }
        EntityType<? extends Owned> type = this.servantType.get();
        Entity entity = getSummon(stack, level);
        if (entity == null) {
            entity = type.create(level);
            if (entity instanceof Owned owned) {
                owned.setTrueOwner(player);
            }
        }
        if (entity instanceof AbstractMutantServant servant && servant.getTrueOwner() == player
                && RitualRequirements.canSummon(level, player, type)) {
            servant.prepareRevived();
            servant.setHealth(servant.getMaxHealth());
            servant.setPos(target.getX(), target.getY(), target.getZ());
            servant.lookAt(EntityAnchorArgument.Anchor.EYES, player.position());
            if (level.addFreshEntity(servant)) {
                servant.spawnAnim();
                servant.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 0.5F);
                SoundEvent reviveSound = servant.getReviveSound();
                if (reviveSound != null) {
                    servant.playSound(reviveSound, 2.0F, 0.5F);
                }
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SOUL, servant.getX(), servant.getY(1.0D), servant.getZ(), 20, 0.6D, 0.8D, 0.6D, 0.05D);
                    serverLevel.sendParticles(ParticleTypes.POOF, servant.getX(), servant.getY(1.0D), servant.getZ(), 10, 0.6D, 0.8D, 0.6D, 0.05D);
                }
                target.discard();
                player.swing(hand);
                SEHelper.addCooldown(player, this, MathHelper.secondsToTicks(AbstractMutantServant.REVIVE_COOLDOWN_SECONDS));
                stack.shrink(1);
            }
        }
        return super.interactLivingEntity(stack, player, target, hand);
    }
}
