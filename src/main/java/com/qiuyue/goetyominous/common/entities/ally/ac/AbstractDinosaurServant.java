package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDinosaurServant extends AnimalSummon {

    // 与原版 DinosaurEntity 的 ALT_SKIN 完全对应：0=默认，1=复古，2=构造
    private static final EntityDataAccessor<Integer> ALT_SKIN =
            SynchedEntityData.defineId(AbstractDinosaurServant.class, EntityDataSerializers.INT);

    protected AbstractDinosaurServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ALT_SKIN, 0);   // 出生一律默认皮肤
    }

    public int getAltSkin() {
        return this.entityData.get(ALT_SKIN);
    }

    public void setAltSkin(int altSkin) {
        this.entityData.set(ALT_SKIN, altSkin);
    }

    // 镜像原版 DinosaurEntity.getAltSkinForItem：琥珀奇物→1，构造碎片→2，其他→0
    public int getAltSkinForItem(ItemStack stack) {
        if (stack.is(ACItemRegistry.AMBER_CURIOSITY.get())) {
            return 1;
        }
        if (stack.is(ACItemRegistry.TECTONIC_SHARD.get())) {
            return 2;
        }
        return 0;
    }

    /**
     * 物品右键切换皮肤，镜像原版 DinosaurEntity.mobInteract 的换皮段：
     * 消耗物品 → 播放音效 → 服务端切换(同皮肤再点=切回默认) → 广播 82/83 变换粒子。
     * 手持物品与换皮无关或非主人时返回 null，交还后续喂食/骑乘逻辑。
     */
    @Nullable
    public InteractionResult tryChangeAltSkin(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        int newSkin = this.getAltSkinForItem(itemstack);
        if (newSkin > 0 && this.getTrueOwner() != null && player == this.getTrueOwner()) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.playSound(newSkin == 2
                    ? ACSoundRegistry.TECTONIC_SHARD_TRANSFORM.get()
                    : ACSoundRegistry.AMBER_MONOLITH_SUMMON.get());
            if (newSkin == this.getAltSkin()) {
                this.setAltSkin(0);
            } else {
                this.setAltSkin(newSkin);
            }
            this.level().broadcastEntityEvent(this, (byte) (newSkin == 2 ? 83 : 82));
            return InteractionResult.SUCCESS;
        }
        return null;
    }

    // 皮肤状态存进 NBT，存档重进后保留
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AltSkin", this.getAltSkin());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setAltSkin(tag.getInt("AltSkin"));
    }

    // 镜像原版 DinosaurEntity.handleEntityEvent 的 82/83：变换粒子
    @Override
    public void handleEntityEvent(byte b) {
        if (b == 82 || b == 83) {
            ParticleOptions particle = b == 82
                    ? ACParticleRegistry.DINOSAUR_TRANSFORMATION_AMBER.get()
                    : ACParticleRegistry.DINOSAUR_TRANSFORMATION_TECTONIC.get();
            for (int i = 0; i < 15; ++i) {
                if (this.level().random.nextInt(8) < 3) {
                    this.level().addParticle(particle,
                            this.getRandomX(1.0F), this.getY() + this.getBbHeight() + 0.3F, this.getRandomZ(1.0F),
                            this.random.nextGaussian() * 0.05, this.random.nextFloat() * 0.2, this.random.nextGaussian() * 0.05);
                }
            }
        } else {
            super.handleEntityEvent(b);
        }
    }
}
