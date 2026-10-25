---
name: goetyominous-compat-architecture
description: 本模组 = goety2 硬前置 + 一堆可选软前置；compat/mod 门槛层 vs compat/<abbr> 注册层两层结构，以及每个联动一套目录的约定
metadata: 
  node_type: memory
  type: project
  originSessionId: aa83982d-2283-4121-a0ca-337b4239c3a9
---

本模组 **GoetyOminous**（modid `goetyominous`）是 **Goety 2 硬前置 + 多个可选联动前置** 的结构。新增任何联动内容都按下面这套走，**不要混用不同联动的注册入口**。

## 1. 依赖分层

**硬前置（必须有）：**
- `goety` —— `build.gradle`: `implementation fg.deobf(...)`，`mods.toml`: `mandatory=true, versionRange="[2.5.57,)"`。API 见 [[goety2-servant-system]] / [[goety2-spell-system]]。
- `curios` —— `build.gradle`: `implementation fg.deobf("top.theillusivec4.curios:curios-forge:5.14.1+1.20.1")`。⚠️ **mods.toml 里没有声明 curios**，但代码直接 import `top.theillusivec4.curios.api.*`（`compat/curios/CuriosIntegration.java`），实际是硬依赖。

**软前置（全部 `compileOnly` + `mods.toml` `mandatory=false, ordering="AFTER"`）：**
`illageandspillage`、`goety_spillage`、`savage_and_ravage`、`blueprint`、`upgrade_aquatic`、`goetyawaken`、`goety_revelation`、`mutantmore`、`legendary_monsters`、`alexscaves`、`alexsmobs`。

## 2. 两层 compat 结构（核心约定）

```
compat/mod/XxxCompat.java          ← 门槛层：只做 ModList.get().isLoaded()，零软前置类引用
compat/<abbr>/XxxCompatManager.java ← 注册层：可以硬引用软前置的类，但只能在门槛后面被调用
```

**门槛层**（`compat/mod/`）一律长这样，绝**不**出现软前置的任何类型：

```java
public class AlexCavesCompat {
    private static Boolean acLoaded = null;
    public static boolean isAlexCavesLoaded() {
        if (acLoaded == null) acLoaded = ModList.get().isLoaded("alexscaves");
        return acLoaded;
    }
}
```

**注册层**（`compat/ac/AcCompatManager.java` 等）可以正常 import 软前置类型（`AtlatitanServant` 继承 AC 的实体等），但**必须**只在门槛内被触碰。

**调用约定**：在 `GoetyOminous.java` 里，`compat.mod.*` 用通配 import，而**注册层一律写全限定名内联在 if 里**：

```java
if (AlexCavesCompat.isAlexCavesLoaded()) {
    com.qiuyue.goetyominous.compat.ac.AcCompatManager.init(modEventBus);
    MinecraftForge.EVENT_BUS.register(NucleeperNukeProtectionHandler.class);
    ...
}
```

⚠️ 这是**故意**的写法，不是懒。看到 `com.qiuyue.goetyominous.compat.xx.XxxCompatManager.xxx(...)` 内联全限定名 = 该调用必须在门槛后面。**新增联动时照抄这个形状**：门槛类 → 注册层 → `GoetyOminous` 里加一个 `if` 块。别把软前置的注册塞进主注册流程（`ModEntityTypes.register` 那一串）里，否则没装该前置的整合包直接崩。

## 3. 每个联动一套目录

缩写：`ac`=Alex's Caves，`am`=Alex's Mobs，`ias`=Illage and Spillage，`lm`=Legendary Monsters，`mm`=Mutant More，`of`=Opposing Force，`sar`=Savage and Ravage，`ua`=Upgrade Aquatic，`spear`=Backported Spears。

| 用途 | 路径 |
|---|---|
| 实体 | `common/entities/ally/<abbr>/`（部分在 `common/entities/ally/mobs/<abbr>/`，如 mm） |
| 实体注册 | `common/init/<abbr>/XxEntityRegistry.java` |
| 方块/方块实体/粒子 | `common/init/<abbr>/XxBlockRegistry.java` / `XxBlockEntityRegistry.java` / `XxParticles.java` |
| 物品 | `common/items/<abbr>/XxItems.java` |
| 客户端渲染 | `client/render/<abbr>/` |
| 联动入口 | `compat/<abbr>/XxCompatManager.java` |
| 门槛 | `compat/mod/XxCompat.java` |

每个 `XxCompatManager` 一般有这些静态方法（按需）：

```java
public static void init(IEventBus modEventBus)                  // 注册 blocks/entities/items/particles/ritual
public static void setCustomAttributes(EntityAttributeCreationEvent event)  // 实体属性
```

`GoetyOminous` 里的挂载点：
- 构造函数 —— `XxxCompatManager.init(modEventBus)` + 该联动专属的 `MinecraftForge.EVENT_BUS.register(...)` 事件处理器
- `addAttributes(EntityAttributeCreationEvent)` —— `XxxCompatManager.setCustomAttributes(event)`
- `onClientSetup(FMLClientSetupEvent)` —— 客户端事件处理器（目前只有 AC 有）
- `client/render/ModModelLayers.java` —— 模型层/实体渲染器注册（内部各自 `if (XxxCompat.isLoaded())`）

⚠️ 联动的**事件处理器类**也写在 `common/events/`（不是 `compat/<abbr>/`），只在该联动的 `if` 块里 `MinecraftForge.EVENT_BUS.register(XxxHandler.class)`。例：`LuxtructosaurusTephraHandler`、`NucleeperNukeProtectionHandler` 都只在 AC 存在时注册。

## 4. 已知不一致（哪天顺手可修）

- **`opposing_force` 有完整门槛 + 注册层（`compat/mod/OpposingForceCompat`、`compat/of/OfCompatManager`、`common/init/of/OfEntityRegistry`），但 `mods.toml` 里根本没声明**。同理 `jade`（`compat/jade/PiglinJadePlugin`，走 `@WailaPlugin` 注解）和 `irons_spellbooks`（`AbstractPiglinServant` 反射引用）也没声明。没声明不会崩（`ModList.get().isLoaded` 照常工作），但拿不到加载顺序保证、整合包也不会提示玩家。
- **门槛方法命名不统一**：只有纯开关的类叫 `isXxxLoaded()`（`isAlexCavesLoaded`/`isMutantMoreLoaded`…），而兼带反射辅助方法的类叫裸 `isLoaded()`（`GoetyAwakenCompat`/`GoetyRevelationCompat`/`GoetySpillageCompat`）。
- **注册层命名不统一**：多数是 `XxCompatManager`，但 `compat/spear/SpearBackportCompat`（门槛+注册合一）和 `compat/curios/CuriosIntegration` 不是。
- `blueprint` 在 `mods.toml` 里声明了，但没有独立门槛类，判定方法挂在 `SavageRavageCompat.isBlueprintLoaded()` 上。

相关：[[goetyominous-soft-dep-reflection]]、[[goety2-src-decompile]]
