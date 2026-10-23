---
name: goety-mold-hardcoded-mixin
description: Goety 召唤结构(Mold)写死实体类型无扩展点，只能 mixin spawnServant；给继承 MC 方法的类注入 tick 必须 remap=true，用生成的 refmap 验证
metadata: 
  node_type: memory
  type: reference
  originSessionId: cbb0bb01-e4b1-49d0-aaf8-d63c99c52faa
  modified: 2026-09-10T12:22:31.512Z
---

Goety 的「动画核心搭结构召唤」这套东西**没有任何扩展点**，想加变体只能注入 mixin：

- `GolemType implements IExtensibleEnum`，把 `BlockState` → `IMold` 实例（`getGolemList()` 每次调用**新建 HashMap + new Mold 实例**，所以改枚举是徒劳）。
- 每个 `XxxMold implements IMold` 内部把实体类型**写死**（`LeapleafMold` 写死 `ModEntityType.LEAPLEAF`，`WhispererMold` 写死 `WHISPERER`/`WAVEWHISPERER`）。
- 触发链：`AnimationCore.useOn` → `GolemType.getGolemList().get(blockState).spawnServant(...)`。**不经过 `getVariant`** —— 想按位置换变体时注入 `getVariant` 是死代码。
- 正确做法：`@Mixin(XxxMold.class)` 注入 `spawnServant` 的 `@At("HEAD")` + `cancellable`，判据不成立就 return 放行。

**Mixin remap 判据**（本仓库 AP 开着 `defaultRequire: 0`，注入失败**静默无操作**）：

- 目标是 Goety 自己声明的方法（`spawnServant`、`hatchEgg`、`getVariant`）→ `remap = false`。
- 目标是**从 MC 继承并被覆写**的方法（`tick`、`getMaxStackSize`）→ **不能写 `remap = false`**，必须走 refmap。Goety 反编译里这类方法显示为 `m_8119_` 之类就是信号。
- 验证手段：编译后读 `build/tmp/compileJava/goetyominous.mixins.refmap.json`，里面出现 `"...Mixin": {"tick": "...Leapleaf;m_8119_()V"}` 就说明注入点解析成功了；`remap = false` 的注入不会出现在 refmap 里，需另外核对目标 class 文件里有该名字面量（`grep -ao "spawnServant" X.class`）。

相关：[[goety-summonspawn-owner-order]]（先 setTrueOwner 再 finalizeSpawn）、[[goety-ritual-percast-state-stale]]。
