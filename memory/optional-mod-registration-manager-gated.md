---
name: optional-mod-registration-manager-gated
description: 可选上游模组的方块/物品/实体注册必须放进对应 CompatManager，放 core 会缺模启动崩
metadata: 
  node_type: memory
  type: project
  originSessionId: 126b8681-5e3a-46a0-965d-ebb5f31b5880
  modified: 2026-10-19T17:09:06.799Z
---

2026-10-19：`ServantQuicksandBlock` / `ServantQuicksandBlockEntity` 原先在 core 的 `ModBlocks` / `ModBlockEntities` 里无条件注册，而方块 `extends com.alexander.mutantmore.blocks.QuicksandBlock`。只装 AM+UA（缺 MutantMore）时，方块注册事件执行 supplier → 类加载解析父类失败 → `NoClassDefFoundError`，启动即崩。事件 handler 里的 `isXxxLoaded()` 守卫挡不住这个。

已移到 `common/init/mm/MmBlocks` + `MmBlockEntities`，由 `MmCompatManager.init` 在 `isMutantMoreLoaded()` 门控内注册。

**Why:** `DeferredRegister` 的 supplier 在 registry 事件时无条件执行；类加载会立刻解析父类/接口，所以凡是 `extends`/`implements` 第三方类型的类，其注册位置就是缺模崩溃点，与运行时守卫无关。

**How to apply:** 任何带第三方父类型的方块/物品/实体/BE，注册必须放在该 upstream 的 `XxxCompatManager` 里（新建 `MmBlocks` 这类模块注册类），core 只保留纯原版/Goety 依赖的注册。审计方式：core 文件对模块包的 import + FQN 扫描，再对命中类检查 `extends/implements` 与静态初始化。关联 [[goetyominous-split-into-modules]]、[[mm-servants-no-terrain-placement]]。
