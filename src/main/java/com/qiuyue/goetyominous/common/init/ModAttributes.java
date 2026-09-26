package com.qiuyue.goetyominous.common.init;

import com.Polarice3.Goety.common.entities.ai.attributes.SpellAttribute;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 本模组自己的属性。
 *
 * <p>目前只有两个，都是给自定义流派 {@code FEL}（邪能）用的法术属性。
 * Goety 会给它自己的每个流派各注册一对「强效 / 折扣」，
 * 但它不认识我们自己 {@code SpellType.create} 出来的 FEL，所以得自己补上。
 *
 * <h2>⚠️ 这里为什么要自己建 DeferredRegister，不能借用 Goety 的</h2>
 * 原先这两行注册用的是 {@code com.Polarice3.Goety.init.ModAttributes.ATTRIBUTES} ——
 * 往 Goety 的注册表里塞我们自己的属性。那在 Goety 2.5.56 上能跑，
 * 到 2.5.57.3 直接<b>开不了游戏</b>：
 * <pre>
 * java.lang.IllegalAccessError: class com.qiuyue.goetyominous.common.init.ModAttributes
 *   tried to access private field com.Polarice3.Goety.init.ModAttributes.ATTRIBUTES
 * </pre>
 * Goety 把那个字段从 public 改成了 private。这不是我们写错了，
 * 是<b>依赖方的内部字段本来就随时会变</b> —— 借别人的注册表用等于把身家押在别人的私有实现上。
 *
 * <p>改成自己的 DeferredRegister 之后，我们只依赖 Goety 的<b>公开 API</b>
 * （{@link SpellAttribute} 和 {@code SpellType}），那两样是有意给外部用的，不会说变就变。
 *
 * <h2>⚠️ 属性的翻译 key 没有跟着变，别去改 lang 文件</h2>
 * 改完之后属性的注册名从 {@code goety:fel_potency} 变成了
 * {@code goetyominous:fel_potency}，看起来 lang 里那几行
 * {@code attribute.name.goety.fel_potency} 该跟着改 —— <b>但不用改，也改了没用</b>。
 *
 * <p>因为显示名取的是 {@code Attribute.getDescriptionId()}，而这个字符串是
 * {@link SpellAttribute} 内部拼出来的，它<b>写死了 {@code "attribute.name.goety."} 前缀</b>，
 * 跟属性注册在哪个命名空间毫无关系。证据在 Goety 自己的语言文件里：
 * 它注册的几十个流派属性（{@code goety:abyss_potency}、{@code goety:frost_potency}……）
 * 用的全是同一个 {@code attribute.name.goety.} 前缀。
 *
 * <p>所以 lang 里那四行（中英各两对，含 {@code .desc}）保持原样就是对的。
 */
public class ModAttributes {

    /** 本模组的属性注册表。命名空间是 {@code goetyominous}。 */
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES,
            GoetyOminous.MOD_ID);

    /**
     * 邪能法术强效。让 FEL 流派的法术打得更疼。
     *
     * <p>⚠️ 这里的 lambda 是<b>延迟执行</b>的（真正跑是在 Forge 的注册事件里，
     * 那时所有 mod 的构造器都已经执行完了）。所以 {@link GoetyOminous#FEL}
     * 在被读到时一定已经赋值了 —— 哪怕它是在构造器第 175 行、也就是
     * {@link #init()} 的<b>下一行</b>才赋值的。这个顺序不能想当然，写反了会拿到 null。
     */
    public static final RegistryObject<Attribute> FEL_POTENCY =
            ATTRIBUTES.register("fel_potency",
                    () -> SpellAttribute.potency(GoetyOminous.FEL, 0.0D, 0.0D, 2048.0D).setSyncable(true));

    /** 邪能法术折扣。降低施放 FEL 法术要的灵魂能量。上下限 -1 ~ 1，即最多全额减免。 */
    public static final RegistryObject<Attribute> FEL_DISCOUNT =
            ATTRIBUTES.register("fel_discount",
                    () -> SpellAttribute.discount(GoetyOminous.FEL, 0.0D, -1.0D, 1.0D).setSyncable(true));

    /**
     * 把注册表挂到 mod 事件总线上。
     *
     * <p>调用点在 {@code GoetyOminous} 的构造器里，紧挨着 {@code FEL} 的创建。
     * 写法照抄 {@code ModItems.init()}，那边也是自己进去取事件总线的。
     */
    public static void init() {
        ModAttributes.ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
