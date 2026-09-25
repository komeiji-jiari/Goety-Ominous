package com.qiuyue.goetyominous.client.render.layer.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.lm.LmServantRenderTypes;
import com.qiuyue.goetyominous.client.render.lm.LmServantSoulRays;
import com.qiuyue.goetyominous.client.render.model.lm.PossessedPaladinServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

/**
 * 把圣骑<b>拎在手里</b>的那个敌人画出来 —— 照抄传奇怪物的 {@code PossessedPaladinGrabLayer}。
 *
 * <h2>它和另外几个图层不是一个路子</h2>
 * 匕首 / 盾牌 / 翅膀 / 三叉戟那四层都是「拿一张专用贴图，把整个模型再画一遍」。
 * 这一层不一样：它<b>不画圣骑自己</b>，而是把<b>另一个实体</b>画在圣骑手上。
 *
 * <h2>为什么必须手动画</h2>
 * 被抓的敌人是 {@code startRiding} 真挂在圣骑身上的乘客，而
 * {@code positionRider} 把它摆在「身前 1 格、抬高 1 格」（见
 * {@code PossessedPaladinServant#positionRider}）。如果不做任何处理，
 * 游戏通常会在那个位置把它画一遍 —— 然后再被这一层在<b>手上</b>画一遍，
 * 屏幕上就出现<b>两个</b>一模一样的怪。
 *
 * <p>所以被抓的那一个必须被「就地藏起来」，只留手上这一份。
 * 负责藏的是 {@link com.qiuyue.goetyominous.client.events.PossessedPaladinGrabRenderEvents}，
 * 本类负责告诉它「现在画的这份是我自己画的，别拦」——
 * 靠的就是下面那对 {@link #currentlyRendering}。
 *
 * <h2>手心的坐标系是怎么来的</h2>
 * {@link PossessedPaladinServantModel#translateModel} 会把右臂那条骨头链
 * （根 → 下半身 → 身体 → 万向节 → 上臂 → 小臂 → 剑 → 大剑）逐级压进矩阵。
 * 压完之后坐标系的原点就在右手握剑的位置，且<b>跟着手臂的动画走</b>。
 * 后面的缩放 / 旋转 / 平移都是在这个「手」的坐标系里做的，
 * 所以被抓的敌人会跟着招式动画一起甩动。
 */
@OnlyIn(Dist.CLIENT)
public class PossessedPaladinServantGrabLayer
        extends RenderLayer<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> {

    private final EntityRenderDispatcher dispatcher;

    /**
     * 这一刻正在被本图层手动绘制的那个乘客。
     *
     * <p>⚠️ 这个字段是<b>唯一</b>能让「藏起来」那套逻辑不至于把本图层自己画的这一份
     * 也一起拦掉的东西。少了它，事件处理器会把这次手动绘制一并取消，
     * 于是被抓的敌人<b>彻底看不见</b>（既没有原地那份，也没有手上那份）。
     *
     * <p>写成 {@code static} 是因为事件处理器是个静态方法，够不着实例字段。
     * 渲染只发生在客户端的渲染线程上，不存在并发问题。
     */
    private static UUID currentlyRendering = null;

    public PossessedPaladinServantGrabLayer(
            RenderLayerParent<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> parent,
            EntityRenderDispatcher dispatcher) {
        super(parent);
        this.dispatcher = dispatcher;
    }

    /** 给事件处理器问的：这个实体是不是「正被本图层画着」。 */
    public static boolean isCurrentlyRendering(UUID uuid) {
        return currentlyRendering != null && currentlyRendering.equals(uuid);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       PossessedPaladinServant entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        for (Entity passenger : entity.getPassengers()) {
            // 第一人称下被抓的如果就是玩家本人，就别在他自己眼前再画一个他自己 ——
            // 那样玩家会看到自己的后脑勺糊在屏幕中间。原版就是这么判断的。
            // （注意这个判断和事件处理器里那个是配套的，两边要一致，
            //   否则会出现「事件那边没拦、这边也不画」= 被抓的玩家完全消失。）
            if (passenger == Minecraft.getInstance().player
                    && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                continue;
            }
            this.renderHeldMob(passenger, poseStack, buffer, packedLight, partialTicks, entity);
        }
    }

    /**
     * 在圣骑右手的位置，把被抓的那个实体画一遍。
     *
     * <p>⚠️ 这里必须 {@code try / finally}：中间要调别的实体的渲染器，
     * 那是个外部调用，万一它在动画求值时抛异常，不写 finally 的话
     * {@link #currentlyRendering} 会永远停在「正在画」的状态 ——
     * 之后所有被抓的敌人都藏不起来了（原版那份会重新冒出来变成两个）。
     * 同理 {@code popPose()} 也放 finally 里，不然坐标系会一直往下套。
     */
    private void renderHeldMob(Entity passenger, PoseStack poseStack, MultiBufferSource buffer,
                               int packedLight, float partialTicks, PossessedPaladinServant paladin) {
        // 存旧值、事后还原，而不是简单地设 true/false —— 这样万一出现
        // 「被抓的怪自己也是个抓着人的圣骑」这种套娃，里层画完不会把外层的标记清掉。
        UUID previous = currentlyRendering;
        currentlyRendering = passenger.getUUID();
        poseStack.pushPose();
        try {
            // 把坐标系挪到右手（见类注释）。
            this.getParentModel().translateModel(poseStack);
            // 缩小到 0.7 —— 被抓的怪比圣骑小一号，看着才像被拎着而不是粘在身上。
            poseStack.scale(0.7F, 0.7F, 0.7F);
            // 转 90 度把它「放倒」。原版这么写的，照抄。
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            poseStack.translate(-1.0F, -1.0F, 1.25F);

            // ---- 灵魂射线 ----
            // 和死亡演出共用 {@link LmServantSoulRays}，只有起点高度不同：
            // 那边是身体中心 1.35 格，这里是手心 1.0 格。
            //
            // 束数直接读 {@code soulRaysCount} —— 招式代码在处决过程中
            // 第 20 / 30 / 40 tick 各 +1（见 PossessedPaladinServant 状态 20 的分支），
            // 于是每锤一下，敌人身上就多炸出一圈光，第三下最盛。
            //
            // ⚠️ 先判断再取 buffer：这一层每帧都跑，而绝大多数帧这个数就是 0。
            //    不判断的话每帧都会白白登记一次渲染类型，多出一批空的绘制批次。
            if (paladin.soulRaysCount > 0) {
                VertexConsumer consumer = buffer.getBuffer(LmServantRenderTypes.LIGHTNING_NO_CULL);
                LmServantSoulRays.render(consumer, poseStack, paladin.soulRaysCount, 1.0F,
                        paladin.getPhase() >= 2, paladin.attackTicks, partialTicks);
            }

            // ---- 被抓的敌人本体 ----
            // 坐标传全 0：坐标系已经被上面的 translateModel 挪到手上了，
            // 实体就画在当前原点。偏航也传 0，让它保持自己的朝向而不是跟着圣骑转。
            //
            // ⚠️ 这一句会走完整的实体渲染流程（包括给别的 mod 发
            //    RenderLivingEvent.Pre / Post），所以上面那个 currentlyRendering 标记是必需的。
            this.dispatcher.render(passenger, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks,
                    poseStack, buffer, packedLight);
        } finally {
            poseStack.popPose();
            currentlyRendering = previous;
        }
    }
}
