package com.qiuyue.goetyominous.common.entities.ally.lm;

/**
 * 一个在 {@code 0 ~ duration} 之间来回走的计时器（对应原版传奇怪物的 {@code ControlledAnim}）。
 *
 * <h2>它是干什么用的</h2>
 * 圣骑身上有几样「手里拿着的东西」需要淡入淡出 —— 盾牌、三叉戟这类。
 * 渲染器每帧读一次这个计时器，按它当前的值决定东西画多透明：
 * 0 = 完全看不见，等于 duration = 完全显形。
 *
 * <p>所以它<b>不做任何判定、不碰任何游戏逻辑</b>，就是个被读来读去的数字。
 * 招式代码负责推进它（几时显形、几时消失），渲染器负责读它。
 *
 * <h2>为什么不用原版那个类</h2>
 * 原版用的是 {@code net.miauczel.legendary_monsters.entity.client.ControlledAnim}，
 * 位于<b>客户端包</b>下。实体代码（跑在服务端）去引用客户端包的类是有风险的，
 * 而这个类本身简单到只有几十行、不含任何客户端 API，所以这里自己写一份。
 * 行为与原版一致：同样是 {@code setTimer / getTimer / increaseTimer / decreaseTimer / resetTimer} 五个方法。
 *
 * <h2>当前状态</h2>
 * ⚠️ 目前<b>还没有任何代码读它</b>。圣骑的盾牌 / 三叉戟渲染图层属于 Stage 2D，
 * 还没搬过来。现在先把计时器按原版逻辑推进着，等图层做好就能直接读，
 * 不用回头再改招式代码。
 *
 * <p>两个实例的用途（数值照抄原版）：
 * <ul>
 *   <li>{@code ghostItemFade}（duration 10）—— 手里那件「幽灵物品」的显隐，
 *       招 25（盾击）和招 38（掷三叉戟）都会用到；</li>
 *   <li>{@code telegraphFadeAway}（duration 15）—— 地面预警光圈的显隐，招 38 用。</li>
 * </ul>
 */
public class ControlledAnim {

    /** 上限。{@link #increaseTimer()} 涨到这儿就停住，不会越界。 */
    private final int duration;

    /** 当前值，恒在 {@code [0, duration]} 区间内。 */
    private int timer;

    public ControlledAnim(int duration) {
        this.duration = duration;
    }

    public int getTimer() {
        return this.timer;
    }

    /**
     * 直接赋值。
     *
     * <p>原版招式起手时会用 {@code setTimer(5)} 把计时器一口气推到中间，
     * 这样物品是「立刻半透明地出现」而不是从 0 慢慢淡进来。
     *
     * <p>这里做了区间夹取 —— 原版没有，但传进来的值本来就都在范围内，
     * 夹一下可以防止以后手滑写出越界的数，属于白拿的保险。
     */
    public void setTimer(int value) {
        this.timer = Math.max(0, Math.min(value, this.duration));
    }

    /** 归零。原版在预警光圈上用它让光圈瞬间消失，而不是慢慢淡出。 */
    public void resetTimer() {
        this.timer = 0;
    }

    /** 每 tick 加 1，到 duration 封顶。 */
    public void increaseTimer() {
        if (this.timer < this.duration) {
            ++this.timer;
        }
    }

    /** 每 tick 减 1，到 0 保底。 */
    public void decreaseTimer() {
        if (this.timer > 0) {
            --this.timer;
        }
    }

    /**
     * 把当前值换算成 {@code 0.0 ~ 1.0} 的比例，也就是「走到哪儿了」。
     *
     * <p>渲染图层读的就是这个数，而不是 {@link #getTimer()} 本身 ——
     * 因为它要用的是<b>比例</b>（算透明度时得除以总长），
     * 而 {@code duration} 是构造时就定死的、外面拿不到，
     * 所以这一步必须在类里面做。
     *
     * <p>原版的算法就是简单相除，逐字照抄。注意 {@code duration} 永远是正数
     * （构造时传的是写死的常量），所以不用防除零。
     */
    public float getAnimationFraction() {
        return (float) this.timer / (float) this.duration;
    }
}
