---
name: goetyominous-soft-dep-reflection
description: 软前置访问的三条铁律——能放门槛后就别反射；mixin 和共享代码路径必须反射；优先用注册名字符串判定
metadata: 
  node_type: memory
  type: feedback
  originSessionId: aa83982d-2283-4121-a0ca-337b4239c3a9
---

**Why:** 本模组是 goety2 硬前置 + 一堆 `compileOnly` 软前置（见 [[goetyominous-compat-architecture]]）。软前置缺失时，任何**硬引用**（字段/方法/`extends`）都会在类加载或链接阶段抛 `NoClassDefFoundError` / `NoSuchMethodError`，直接崩游戏。整合包里没装某个联动是常态，所以这是最高频的崩服来源。

**How to apply:** 按下面优先级选手段，**能靠前就别用后面的**。

## 手段优先级

### ① 门槛隔离（首选）
把软前置代码放进 `compat/<abbr>/XxxCompatManager.java`，只在 `if (XxxCompat.isLoaded())` 后面用全限定名内联调用。这是绝大多数情况的正解，永远不会崩。

### ② 注册名字符串判定（次选，比反射稳）
完全绕开反射，靠 `ForgeRegistries` 的 id 字符串判断。改动最小、最不容易随版本失效：

```java
// compat/mod/GoetyAwakenCompat.java
public static boolean isGoetyAwakenHeresiarchServant(LivingEntity entity) {
    if (!isLoaded()) return false;
    try {
        return "goetyawaken:heresiarch_servant".equals(
                ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString());
    } catch (Exception e) { return false; }
}
```

### ③ 反射（前两条都走不通时才用）
只有**两种**情况必须反射：

**(a) Mixin** —— mixin 类无条件加载/变换，@Mixin 目标类被加载时软前置可能不在，所以 mixin 里绝不能硬引用软前置类型。

**(b) 共享代码路径** —— 类本身在主流程里（`utils/`、`common/entities/` 等），被无门槛的代码调用，不能整类挪进 `compat/<abbr>/`。

## Mixin 铁律

**永远不要 `@Mixin(软前置的类)`。** 本模组 40 个 mixin **全部**只 @Mixin 原版类或 Goety 类（`Item`、`AbstractArrow`、`Raid`、`Spell`、`Summoned`、`BewitchmentBlockEntity`…），没有一个是软前置的类。走这两条路之一：

1. **@Mixin 稳定类 + 注册名字符串过滤**（最干净）：
   ```java
   @Mixin(Item.class)   // 原版类，永远存在
   public class MutantMoreItemMixin {
       @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
       private void goetyominous$increaseStackSize(CallbackInfoReturnable<Integer> cir) {
           String itemId = ForgeRegistries.ITEMS.getKey((Item)(Object)this).toString();
           if ("mutantmore:formula_y".equals(itemId) || "mutantmore:compound_z".equals(itemId)) {
               cir.setReturnValue(3);
           }
       }
   }
   ```
2. **私有静态辅助方法里 `Class.forName` + 反射**：
   ```java
   // common/mixin/RitualRequirementsMixin.java
   private static boolean isNonTuskedElephant(Mob mob) {
       if (mob == null) return false;
       try {
           Class<?> elephantClass = Class.forName("com.github.alexthe666.alexsmobs.entity.EntityElephant");
           if (!elephantClass.isInstance(mob)) return false;
           Method isTusked = elephantClass.getMethod("isTusked");
           return !(boolean) isTusked.invoke(mob);
       } catch (ReflectiveOperationException e) { return false; }
   }
   ```

`src/main/resources/goetyominous.mixins.json` 里 `"injectors": {"defaultRequire": 0}` —— 注入点找不到时不崩。**别把这个改成 1。**

## 反射写法约定

**惰性初始化 + 全量缓存 + 吞异常**（`utils/CreateBlazeBurnerCompat.java` 是范本）：把 `Class` / `Field` / `Method` 缓存在 `@Nullable static` 字段里，用一个 `checked`/`usable` 布尔做一次性初始化，初始化失败就整体标记不可用，调用点见 `usable` 直接走降级分支。捕获用 `catch (Throwable ignored)`（`ReflectiveOperationException` 漏 `LinkageError`）。

```java
private static boolean checked;
private static boolean usable;
@Nullable private static Method getHeatLevelFromBlockMethod;

private static boolean ensureUsable() {
    if (!checked) {
        checked = true;
        if (ModList.get().isLoaded("create")) {
            try {
                beClass = Class.forName(BE_CLASS);
                getHeatLevelFromBlockMethod = beClass.getDeclaredMethod("getHeatLevelFromBlock");
                usable = true;
            } catch (Throwable ignored) { beClass = null; }
        }
    }
    return usable;
}
```

**每次调用都 `try` 的短路径**（`compat/mod/GoetyAwakenCompat.isObsidianMonolithServant`）：判定类方法用 `clazz.isInstance(entity)` 而不是强转，失败返回**安全默认值**（`false` / `true`，看语义）。

**读别人的 ForgeConfigSpec**（`common/entities/ally/sar/CreepieServant.shouldExplosionDestroyBlocks`）：`Class.forName(SRConfig)` → `getDeclaredField("COMMON").get(null)` → 再取配置字段 → `getMethod("get").invoke(value)`；失败返回实现默认值。⚠️ 配置字段名是**字符串**，联动方改名就静默失效，留意。

## 现有反射点清单（改动这些地方时注意）

| 位置 | 反射对象 | 原因 |
|---|---|---|
| `common/mixin/RitualRequirementsMixin.java:33` | Alex's Mobs `EntityElephant#isTusked` | mixin 不能硬引用 |
| `common/mixin/MixinOssuaryBlockEntity.java:61` | 软前置类 | mixin 不能硬引用 |
| `compat/mod/GoetyAwakenCompat.java:23` | goetyawaken `ObsidianMonolithServant` | 共享判定路径 |
| `compat/mod/GoetyRevelationCompat.java:36` | 自己模组的 `HeresiarchServant` | 共享判定路径 |
| `compat/mod/GoetySpillageCompat.java:23` | Goety `CuriosFinder#hasNamelessSet` | 跨前置版本差异 |
| `utils/CreateBlazeBurnerCompat.java` | Create `BlazeBurnerBlockEntity` 全套 | Create 无门槛类、被通用代码调用 |
| `common/entities/ally/neutral/AbstractPiglinServant.java:878` | `irons_spellbooks` `NetherwardTinctureItem` | 共享实体基类 |
| `common/entities/ally/sar/CreepieServant.java:266` | SAR `SRConfig` | 读联动方配置 |
| `common/entities/ally/mobs/mm/WitherSlash.java:66` | mutantmore `HandleLoopingSoundInstances` | 播放联动方音效 |
| `common/entities/ally/mobs/mm/MutantWitherSkeletonServant.java:430` | cataclysm `Cataclysm_Weapon_Item` | 共享实体基类 |

相关：[[goetyominous-compat-architecture]]、[[goety2-src-decompile]]
