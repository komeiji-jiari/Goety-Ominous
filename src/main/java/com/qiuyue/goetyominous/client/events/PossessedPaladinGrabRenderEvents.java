package com.qiuyue.goetyominous.client.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.layer.lm.PossessedPaladinServantGrabLayer;
import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 「被抓的敌人只画一份」—— 配合
 * {@link PossessedPaladinServantGrabLayer} 用的事件处理器。
 *
 * <h2>要解决的问题</h2>
 * 圣骑的突刺抓取会把敌人 {@code startRiding} 真挂到自己身上，
 * 而 {@code Entity#positionRider} 把乘客摆在「身前 1 格、抬高 1 格」。
 * 游戏默认就会在那儿把它画一遍；抓取图层又会在<b>手上</b>画一遍。
 * 两处位置不同，所以玩家会看到<b>两个</b>同一只怪 —— 一个飘在身前，一个被拎在手里。
 *
 * <h2>原版怎么处理的（以及为什么不能照抄）</h2>
 * 原版有一样的东西：{@code LegendaryMonsters.PROXY.releaseRenderingEntity()}
 * 和 {@code blockRenderingEntity()}，一前一后包着手动绘制，往一张叫
 * {@code blockedEntityRenders} 的名单里删/加 UUID。
 * <b>但那张名单在整个传奇怪物的代码里从来没有人读过</b> —— 只有往里面写的地方，
 * 没有拿出来判断的地方。换句话说原版的拦截<b>从来没有生效过</b>，
 * 原版游戏里那两只怪是实打实一起显示的。
 * 所以我们只借「一前一后维护一份名单」这个思路，判断这一步得自己补上。
 *
 * <h2>为什么要手动补一个 Post 事件</h2>
 * Forge 在 {@code LivingEntityRenderer.render} 里的写法是：
 * <pre>
 *   if (post(RenderLivingEvent.Pre)) return;   // ← 取消就 return
 *   pushPose();
 *   ... 真正的绘制 ...
 *   popPose();
 *   post(RenderLivingEvent.Post);
 * </pre>
 * 所以<b>取消 Pre 会把末尾那个 Post 一起跳过</b>，Pre / Post 就不成对了。
 * 别的 mod 要是在 Pre 里存了状态、等着 Post 清掉，就会一直留着。
 * 因此取消之前先手动补发一个 Post，把这一对凑齐 ——
 * 项目里 {@code AtlatitanRenderEvents} 等几处早就是同一个写法了。
 *
 * <h2>顺带一提：取消不会弄坏矩阵栈</h2>
 * 那句拦在 {@code pushPose()} <b>之前</b>，所以取消时并没有多压一层，
 * 不会出现「栈越压越深、画面慢慢飘走」的问题。
 */
@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT)
public final class PossessedPaladinGrabRenderEvents {

    private PossessedPaladinGrabRenderEvents() {
    }

    @SuppressWarnings("rawtypes")
    @SubscribeEvent
    public static void hideHeldVictim(RenderLivingEvent.Pre event) {
        LivingEntity entity = event.getEntity();

        // 只关心「正被圣骑拎着」的实体，别的实体一律不碰。
        if (!(entity.getVehicle() instanceof PossessedPaladinServant)) {
            return;
        }

        // 被抓的是玩家本人、而且正开着第一人称时不拦 ——
        // 那种情况下玩家看的是自己的视角，本来就看不到自己的模型，
        // 强行取消反而可能把别的 mod 的第一人称手臂渲染一起干掉。
        // ⚠️ 这个判断必须和 PossessedPaladinServantGrabLayer#render 里那个保持一致。
        if (isFirstPersonPlayer(entity)) {
            return;
        }

        // 这一份是抓取图层自己正在画的，放行 —— 拦了的话手上那份也没了，
        // 被抓的敌人会彻底从屏幕上消失。
        if (PossessedPaladinServantGrabLayer.isCurrentlyRendering(entity.getUUID())) {
            return;
        }

        // 见类注释：取消 Pre 会连带跳过末尾的 Post，这里手动补一个，让两者成对。
        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(
                entity, event.getRenderer(), event.getPartialTick(),
                event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight()));
        event.setCanceled(true);
    }

    private static boolean isFirstPersonPlayer(Entity entity) {
        return entity.equals(Minecraft.getInstance().cameraEntity)
                && Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }
}
