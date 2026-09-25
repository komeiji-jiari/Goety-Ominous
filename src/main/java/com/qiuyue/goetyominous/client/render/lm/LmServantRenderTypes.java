package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 堕落圣骑仆从要用的几个「原版没有」的渲染类型。
 *
 * <h2>为什么要单独开一个类</h2>
 * 这个类<b>必须继承 {@link RenderType}</b>，因为下面用到的
 * {@code RENDERTYPE_LIGHTNING_SHADER} / {@code LIGHTNING_TRANSPARENCY} / {@code WEATHER_TARGET}
 * 在 {@link RenderStateShard} 里都是 {@code protected} —— 只有 RenderType 的子类才够得着。
 * 原版传奇怪物的 {@code LMRenderTypes} 也是这么写的（它叫 {@code LIGHTNING_NO_CULL}），
 * 我们没有直接引用它的原因和别处一样：那是 LM 的客户端类，能不碰就不碰。
 */
@OnlyIn(Dist.CLIENT)
public class LmServantRenderTypes extends RenderType {

    /**
     * 闪电专用的渲染类型，<b>但关掉了背面剔除</b>。
     *
     * <h2>和原版 {@code RenderType.lightning()} 的唯一的区别</h2>
     * 就是多了 {@code setCullState(NO_CULL)}。原版那个是给天空闪电用的，
     * 只会从固定角度看到正面，所以剔背面无所谓；我们这边这片四边形是
     * 跟着圣骑转的，玩家绕着走一圈就会看到背面 —— 不关剔除的话，
     * 走到背后预警光圈直接消失。
     *
     * <p>其余的着色器 / 透明度 / 输出目标全部和原版一致，逐项照抄 LM 的定义：
     * <ul>
     *   <li>{@code POSITION_COLOR} 顶点格式 —— 不采样贴图，纯色四边形，
     *       所以顶点上只有坐标和颜色，没有 uv（圈的颜色是写死在顶点色里的）；</li>
     *   <li>{@code LIGHTNING_TRANSPARENCY} —— 闪电那种「叠加增亮」的混合方式，
     *       在地上画出来是一层发光的薄膜，而不是一块糊上去的色块；</li>
     *   <li>{@code COLOR_DEPTH_WRITE} —— 正常写深度，会被地面正常遮挡；</li>
     *   <li>{@code WEATHER_TARGET} —— 排在天气那一档渲染，保证画在所有实体<b>之后</b>，
     *       否则会被圣骑自己的模型盖住。</li>
     * </ul>
     */
    public static final RenderType LIGHTNING_NO_CULL = create("lm_servant_lightning_no_cull",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, true,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setOutputState(WEATHER_TARGET)
                    .setCullState(NO_CULL)
                    .createCompositeState(false));

    /**
     * 纯静态工具类，不需要实例。
     *
     * <p>⚠️ 这个私有构造不是摆设：{@link RenderType} 的构造函数是 {@code protected} 的，
     * 不写一个（哪怕没人调用的）构造，编译器会因为「父类没有无参构造」而报错。
     */
    private LmServantRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                                 boolean affectsCrumbling, boolean sortOnUpload,
                                 Runnable setupTask, Runnable clearTask) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupTask, clearTask);
    }
}
