package com.qiuyue.goetyominous.common.events;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.qiuyue.goetyominous.common.entities.ally.ac.DeepOneMageServant;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * BUBBLED 视觉清理:客户端 tickEffects 对过期效果不做移除(Forge 补丁把移除守卫在
 * !isClientSide 内),导致 duration=0 的 BUBBLED 残留在客户端 activeEffects 中;
 * 而 ACPotionEffectLayer 以 hasEffect(BUBBLED)&&isAlive() 作为渲染门控且无时长判断,
 * 泡泡因此永不消失。
 *
 * 这里在服务端效果到期(Expired)或被移除(Remove)时,向所有玩家发送 AC 的
 * UpdateEffectVisualityEntityMessage(remove=true);其客户端处理器调用
 * removeEffectNoUpdate 无条件剥离本地效果,泡泡渲染随即消失。
 *
 * 仅在 Alex 的洞穴加载时注册(见 GoetyOminous#commonSetup),与其它 AC 联动处理器一致;
 * 该类直接引用 AC 类,若被无条件注册会在无 AC 环境下对任意效果到期事件抛
 * NoClassDefFoundError(com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry)。
 */
public class BubbledVisualCleanupHandler {

    @SubscribeEvent
    public static void onBubbledExpired(MobEffectEvent.Expired event) {
        stripBubbledVisual(event.getEntity(), event.getEffectInstance());
    }

    @SubscribeEvent
    public static void onBubbledRemoved(MobEffectEvent.Remove event) {
        stripBubbledVisual(event.getEntity(), event.getEffectInstance());
    }

    private static void stripBubbledVisual(LivingEntity entity, MobEffectInstance instance) {
        if (instance == null || instance.getEffect() != ACEffectRegistry.BUBBLED.get()) {
            return;
        }
        if (entity instanceof DeepOneMageServant) {
            // 法师的 BUBBLED 由 tick() 双端自应用(出水补、进水清、死亡随实体移除),客户端自己能管理
            // 生命周期;若这里广播 remove 消息,客户端会 removeEffectNoUpdate 剥掉本地效果,
            // 导致泡泡每 200 tick 过期时闪一帧(AC 原版靠陈旧效果常驻,无此闪烁)。跳过以对齐原版。
            return;
        }
        if (entity.level().isClientSide) {
            return;
        }
        // fromEntityID 用目标自身,使客户端处理器中的距离门控(目标到自身距离=0)恒通过;
        // 目标未加载的玩家端处理器会因 getEntity 返回 null 而直接跳过,发送全量也无副作用。
        AlexsCaves.sendMSGToAll(new UpdateEffectVisualityEntityMessage(entity.getId(), entity.getId(), 1, 0, true));
    }
}
