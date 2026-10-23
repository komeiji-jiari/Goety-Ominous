---
name: goetyominous-deadcode-cleanup-preference
description: "移植进来的死代码用户要求全删,即使会拉大与上游 diff —— parity 只管行为,不管死账"
metadata: 
  node_type: memory
  type: feedback
  originSessionId: 637df696-01ad-4b07-a897-ce5989e56df2
  modified: 2026-09-24T20:28:01.712Z
---

从 LM/AC 整体搬进来的实体里,永不进入的攻击状态、永不启动的 `AnimationState`、零引用字段/常量、只写不读的字段,用户选择**全删**,在我提示"删了会和上游 diff 拉大、以后再同步更痛"之后仍然确认删。

**Why:** 这些死账是移植前上游就有的(可在 `.scratch/lm_cloudgolem/` 反编译源码里逐条对上),不是移植回归;留着会让每次读文件都要重新怀疑一遍"这是不是漏了什么"。

**How to apply:** 和 [[ac-servant-parity-keep-original]] 不冲突——**行为**要严格对齐原版(触发条件、硬编码数值、默认空闲行为别自作主张),**死代码**则清干净。清理时连带处理:switch case、`stopAllAnimationStates()`、`getAnimationState()` 映射、以及**模型文件里绑同一个 `AnimationState` 的 `animate(...)` 行**(漏了模型会编不过或永远不播)。清完复跑 `.scratch/_deadcode.mjs` + `.scratch/_deadfields.mjs` 验收,两个脚本的用法和判据见 [[goetyominous-deadcode-analysis-scripts]]。
