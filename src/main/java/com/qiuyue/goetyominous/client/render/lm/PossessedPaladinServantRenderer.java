package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.lm.PossessedPaladinServantDaggerLayer;
import com.qiuyue.goetyominous.client.render.layer.lm.PossessedPaladinServantEyesLayer;
import com.qiuyue.goetyominous.client.render.layer.lm.PossessedPaladinServantGrabLayer;
import com.qiuyue.goetyominous.client.render.layer.lm.PossessedPaladinServantShieldLayer;
import com.qiuyue.goetyominous.client.render.layer.lm.PossessedPaladinServantTridentLayer;
import com.qiuyue.goetyominous.client.render.layer.lm.PossessedPaladinServantWingsLayer;
import com.qiuyue.goetyominous.client.render.model.lm.PossessedPaladinServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 堕落圣骑仆从的渲染器。
 *
 * <p>贴图直接借用传奇怪物自带的，所以必须装了 LM 才看得见 —— 这本来就属于 LM 联动内容。
 * 注意文件名里 {@code posessed} 只有一个 s，是传奇怪物自己的拼写，不是我打错的。
 *
 * <p>原版挂了 6 个附加图层，这里<b>已经全部搬完</b>，而且添加顺序和原版逐字一致：
 * <pre>
 *   眼睛 → 匕首 → 盾牌 → 三叉戟 → 翅膀 → 抓取
 * </pre>
 * ⚠️ 顺序<b>不能乱</b>：半透明图层是「一层压一层」画的，先加的在下、后加的在上。
 * 乱插的话二阶段那几层红光叠在一起会发灰。
 *
 * <p>每个图层什么时候显形，见 {@code PossessedPaladinServant} 里那组
 * {@code hasDagger() / hasShield() / hasTrident() / hasWings()}：
 * 匕首 = 招 15/28，盾牌 = 招 25，三叉戟 = 招 37/38，翅膀 = 招 37（二阶段终结技），
 * 抓取 = 招 19/20/21/33（把敌人拎在手里）。
 *
 * <p>「灵魂射线」已经抽成了公共类 {@link LmServantSoulRays} —— 死亡演出
 * （状态 36）和抓取处决都要用它，原版是把同一段代码抄了两遍。
 * 见 {@link #renderSoulRays}；脚下那片地面预警光圈见 {@link #renderTelegraph}。
 */
@OnlyIn(Dist.CLIENT)
public class PossessedPaladinServantRenderer extends MobRenderer<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> {

    /** 一阶段：一身完整的银色盔甲。 */
    private static final ResourceLocation PHASE1 = new ResourceLocation(
            "legendary_monsters", "textures/entity/posessed_paladin/new_posessed_paladin.png");
    /** 二阶段：盔甲裂开、透出红光。变身演出一结束就换这张。 */
    private static final ResourceLocation PHASE2 = new ResourceLocation(
            "legendary_monsters", "textures/entity/posessed_paladin/new_posessed_paladin_p2.png");

    /** 原版就是这个倍数。模型本身是照小尺寸捏的，不放大就只有一半高。 */
    private static final float MODEL_SCALE = 1.75F;

    public PossessedPaladinServantRenderer(EntityRendererProvider.Context context) {
        super(context, new PossessedPaladinServantModel<>(
                context.bakeLayer(ModEntityLayers.POSSESSED_PALADIN_SERVANT_LAYER)), 0.75F);
        // 6 个图层，顺序和原版 PossessedPaladinRenderer 的构造函数逐字一致。
        // ⚠️ 顺序有讲究：半透明图层是「先加的在下面」，乱插二阶段会发灰。
        //    另外抓取图层要排在最后 —— 它画的是「手里拎着的另一个实体」，
        //    应该压在所有身体图层之上。
        this.addLayer(new PossessedPaladinServantEyesLayer(this));
        this.addLayer(new PossessedPaladinServantDaggerLayer(this));
        this.addLayer(new PossessedPaladinServantShieldLayer(this));
        this.addLayer(new PossessedPaladinServantTridentLayer(this));
        this.addLayer(new PossessedPaladinServantWingsLayer(this));
        // 抓取图层需要实体渲染调度器（它要把另一个实体整个画出来），
        // 而调度器只有构造时拿到的 context 里有，所以必须从这儿传进去。
        this.addLayer(new PossessedPaladinServantGrabLayer(this, context.getEntityRenderDispatcher()));
    }

    @Override
    public void render(PossessedPaladinServant entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        // ⚠️ 下面这两个特效都必须在 super.render() 之前画，而且必须在这个「已经放大过」的坐标系里。
        //    原因：它们的位置都是照抄原版的（射线起点 y = 1.35，预警圈 y = 0.25），
        //    默认就在圣骑的局部坐标系里 —— 放大 1.75 倍之后才和模型对得上。
        //    放到 super.render() 后面画就晚了：那时候姿态栈已经 pop 回来了。
        //
        // ⚠️ 顺序也别换：先射线后预警圈。预警圈用的渲染类型排在「天气」那一档，
        //    会盖在所有实体上面；射线排在实体那一档、会被模型挡住一部分。
        //    颠倒了的话，射线会盖在预警圈上，看着像糊了一层。
        this.renderSoulRays(entity, poseStack, buffer, packedLight, partialTicks);
        this.renderTelegraph(entity, poseStack, buffer, packedLight);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    /**
     * 画死亡演出时从身体中心射出去的那几束「灵魂射线」（状态 36 用）。
     *
     * <h2>它画出来长什么样</h2>
     * 每束射线其实是<b>一个细长的三角形</b>：起点在身体中心，
     * 另外两个顶点分别往左前和右前方伸出去，围成一个尖尖的光柱。
     * 束数由 {@link PossessedPaladinServant#rayAmount} 决定，每多加一束就再画一个三角形。
     *
     * <h2>为什么每帧都要重新随机</h2>
     * 三角形的朝向是<b>每帧当场掷骰子算的</b>（连掷六次旋转），
     * 所以同一个三角形在下一帧就指到别处去了 —— 看起来就是电光在乱窜、在抖。
     * 这不是 bug，正是原版要的效果。别「优化」成缓存的固定方向。
     *
     * <p>⚠️ 随机数种子<b>写死 432L</b>：这样每一帧的六次掷骰结果都完全一样，
     * 于是抖动是「有规律的循环」而不是真的乱。原版如此，照抄。
     *
     * <h2>两处原版的死代码，本移植没抄</h2>
     * <ol>
     *   <li>原版开头有个 {@code if (attackTicks > toTicks(11.0F))}，但<b>两个分支干的是同一件事</b>
     *       （都是往上挪 1.35 格）。大概本来想写「尸体倒下后起点降低」，没改完就留下了。
     *       本移植直接写死一句 {@code translate(0, 1.35, 0)}，效果完全一样。</li>
     *   <li>原版的 {@code maxRays} 算出来<b>从来没被用过</b>，真正决定画几条的是
     *       {@code currentRay}（本移植叫 {@code rayCount}）。那个变量本移植没写。</li>
     * </ol>
     * 两条都<b>不影响画面</b>，纯粹是省掉没用的行。记在这里是为了以后对着原版核对时，
     * 看到「原版有、我们没有」不会误以为是漏搬。
     *
     * <h2>颜色分阶段，一阶段是青的不是红的</h2>
     * 这个坑（以及「原版其实把这段代码写了两遍」）详见 {@link LmServantSoulRays} 的类注释。
     * 那边是<b>唯一的实现</b>，这里只负责把参数凑齐 —— 死亡演出和抓取处决共用同一份，
     * 就不会再出现「改了一处忘了另一处」。
     */
    private void renderSoulRays(PossessedPaladinServant entity, PoseStack poseStack,
                                MultiBufferSource buffer, int packedLight, float partialTicks) {
        // ⚠️ 这个方法每帧都会被调一次（见上面的 render()），所以「没射线」时要尽早溜走。
        //    不然光是下面那句 getBuffer 就会每帧登记一次渲染类型，
        //    白白多出一批空的绘制批次 —— 平时（不在死亡演出里）绝大多数帧都是这个情况。
        if (entity.rayAmount <= 0) {
            return;
        }
        VertexConsumer consumer = buffer.getBuffer(LmServantRenderTypes.LIGHTNING_NO_CULL);
        // 起点抬到身体中间高度（1.35 格），否则射线是从脚底往上长的。
        // 抓取图层那份用的是 1.0，别的参数完全一样 —— 这正是当初抽成公共类的理由。
        LmServantSoulRays.render(consumer, poseStack, entity.rayAmount, 1.35F,
                entity.getPhase() >= 2, entity.attackTicks, partialTicks);
    }

    /**
     * 画脚下那片「地面预警光圈」—— 一层半透明的红色光膜，提示这一片马上要挨打。
     *
     * <p>它<b>纯粹是给玩家看的</b>，不参与任何伤害判定；关掉它伤害一点不少。
     *
     * <h2>整段逻辑的三层判断，缺一不可</h2>
     * <ol>
     *   <li>{@link PossessedPaladinServant#canRenderTelegraph()} —— 现在是这一招的
     *       「该亮」时段吗（状态 32 的 48~60 tick / 状态 38 的 48~69 tick）；</li>
     *   <li>透明度算出来要大于 0 —— 由 {@code telegraphFadeAway} 计时器控制，
     *       招式代码在收尾时把它涨上去，光圈就淡掉了；</li>
     *   <li>而且上面两条是「与」的关系 —— 时段对了但计时器涨满了，照样看不见。</li>
     * </ol>
     *
     * <h2>透明度公式</h2>
     * <pre>
     *   比例 = telegraphFadeAway.getAnimationFraction()   // 0.0 ~ 1.0
     *   剩余 = 1.0 - 比例
     *   透明度 = clamp(剩余 - 0.65, 0.0, 1.0)
     * </pre>
     * 计时器为 0 时透明度 0.35（能看见），涨过 5.25 之后就归零了。
     * 所以这个圈是「一亮到底、快打完才开始淡」，不是从头到尾匀速消失。
     *
     * <p>⚠️ 那个 0.65 是<b>写死的</b>，和 {@code telegraphFadeAway} 的时长（15）绑死。
     * 改时长的话这个数也得跟着改。
     *
     * <h2>为什么颜色偏红、还带点透明</h2>
     * 参数是 {@code (1.0, 0.25, 0.25)} —— 纯红掺一点点绿蓝，看着是血红。
     * 二阶段整个圣骑都是红的，这是同一套配色。渲染类型本身是「叠加增亮」的，
     * 所以铺在地上更像一层发光的薄膜，而不是一块糊上去的色块。
     */
    private void renderTelegraph(PossessedPaladinServant entity, PoseStack poseStack,
                                 MultiBufferSource buffer, int packedLight) {
        float alpha = Mth.clamp(1.0F - Math.min(entity.telegraphFadeAway.getAnimationFraction(), 1.0F) - 0.65F,
                0.0F, 1.0F);
        if (alpha <= 0.0F || !entity.canRenderTelegraph()) {
            return;
        }

        // ⚠️ 这里的 yRot 是「-entity.getYRot() + 90」而不是直接用 getYRot()。
        //    因为四边形画在<b>还没被实体朝向旋转过的</b>坐标系里（实体的旋转发生在
        //    super.render() → setupRotations 里，那一步还没执行），所以要手动补上朝向；
        //    那个 +90 是画四边形这个工具自己的约定（面和它的坐标系差 90 度）。
        //    写错的表现是：光圈方向永远朝着世界坐标的某个固定方向，不跟着圣骑转。
        float yaw = -entity.getYRot() + 90.0F;
        VertexConsumer consumer = buffer.getBuffer(LmServantRenderTypes.LIGHTNING_NO_CULL);
        // 参数：半宽 8 / 半高 2 / 位置 (0, 0.25, 0) / 俯仰 90 度（把竖着的面片放平贴地）/
        //      偏航 yaw / 翻滚 0 / 颜色 (1, 0.25, 0.25) / 透明度。
        LmServantRenderUtils.renderPivotedQuad(8.0F, 2.0F, 0.0D, 0.25D, 0.0D,
                90.0D, (double) yaw, 0.0D, consumer, poseStack,
                OverlayTexture.NO_OVERLAY, packedLight, 1.0F, 0.25F, 0.25F, alpha);
    }

    @Override
    public ResourceLocation getTextureLocation(PossessedPaladinServant entity) {
        // 换贴图的时机就是变身第 49 tick（阶段被切成 2 的那一刻），
        // 所以玩家看到的正好是「盔甲裂开 + 贴图变红」同时发生。
        return entity.getPhase() >= 2 ? PHASE2 : PHASE1;
    }

    // ⚠️ 这里<b>故意不覆写</b> setupRotations()。
    //
    // 原版 LivingEntityRenderer 里那句「把模型绕 Z 轴拧倒」是挂在 deathTime > 0 上的，
    // 而圣骑被处决时 deathTime 被锁死在 0（见 PossessedPaladinServant.tickDeath()），
    // 所以那个「直挺挺侧倒」自动就不播了 —— 这正是需求。
    //
    // 处决时该播的是传奇怪物自己的死亡动画（状态 36 的 PossessedPaladinAnimations4.death2），
    // 它走 attackTicks，和 deathTime 无关，不受影响。
    //
    // ⚠️ 以后别「顺手」把这个方法补回来 —— 补回来 = 原版侧倒又回来了。
}
