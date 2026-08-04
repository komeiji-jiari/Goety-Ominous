package com.qiuyue.goetyominous.common.items.am;

import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.SEHelper;
import com.qiuyue.goetyominous.common.entities.ally.am.CrimsonMosquitoServant;
import com.qiuyue.goetyominous.common.entities.ally.am.WarpedMoscoServant;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * 诡异类固醇（Warped Steroids）：由疣猪蚊仆从死亡掉落。
 * <p>
 * 参照 Goety 的 HowlingSoul：对属于玩家的绯红蚊子仆从右键使用时，
 * 把绯红蚊子仆从直接转化为疣猪蚊仆从（对齐 CrimsonMosquitoServant 患病转化的流程），
 * 并消耗一个物品。
 */
public class WarpedSteroidsItem extends Item {

    public WarpedSteroidsItem() {
        super(new Item.Properties()
                .rarity(Rarity.UNCOMMON)
                .setNoRepair()
                .stacksTo(1));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        // 只能对属于自己的绯红蚊子仆从使用
        if (!(target instanceof CrimsonMosquitoServant mosquito) || mosquito.getTrueOwner() != player) {
            return InteractionResult.PASS;
        }
        Level level = player.level();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        // 复用 CrimsonMosquitoServant.tick() 里患病转化为疣猪蚊仆从的流程
        WarpedMoscoServant mosco = AmEntityRegistry.WARPED_MOSCO_SERVANT.get().create(level);
        if (mosco == null) {
            return InteractionResult.FAIL;
        }
        mosco.copyPosition(mosquito);
        mosco.setTrueOwner(player);
        // 转化后直接满血，符合"强化"的体验
        mosco.setHealth(mosco.getMaxHealth());
        mosco.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(mosquito.blockPosition()), MobSpawnType.CONVERSION, null, null);
        // 先加入新实体并播生成动画（对齐 Goety HowlingSoul 的 addFreshEntity -> spawnAnim -> discard 顺序），
        // 成功后再移除旧实体。不要对即将被移除的 mosquito 广播实体事件：broadcastEntityEvent(79) 与
        // remove(DISCARDED) 依赖同一 tick 的发包先后顺序，一旦移除包先到客户端，事件包就会因按 ID
        // 找不到实体而静默丢失粒子。这里改用服务端粒子直接在旧蚊子处复刻原 handleEntityEvent(79)
        // 的爆炸粒子，与实体是否还存在无关，包序不再有约束。
        if (level.addFreshEntity(mosco)) {
            mosco.spawnAnim();
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, mosquito.getX(), mosquito.getY() + 1.7D, mosquito.getZ(), 27, 1.6D, 1.7D, 1.6D, 0.02D);
            }
            level.playSound(null, mosquito.getX(), mosquito.getY(), mosquito.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0F, 0.5F);
            mosquito.remove(Entity.RemovalReason.DISCARDED);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.swing(hand);
            // Goety 通用冷却 5 分钟（300 秒），与疣猪蚊仆从死亡掉落物 FlyingItem.setSecondsCool(300) 的拾取冷却保持一致
            SEHelper.addCooldown(player, this, MathHelper.secondsToTicks(300));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
