---
name: dungeonsdelight-fastutil-dispenser-race
description: "错误报告里的 fastutil rehash AIOOBE 是 dungeonsdelight 的并发崩溃,不是本仓的锅;甩锅前先看这条"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 37effd27-4657-48c3-a4ef-142ed2de678a
  modified: 2026-10-06T18:30:00.469Z
---

**症状**(2026-10-06 19:28,实例 `D:\beta\.minecraft\versions\1.20.1-Forge 测试`):

```
java.lang.ArrayIndexOutOfBoundsException: Index 2048 out of bounds for length 1025
  at it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap.rehash(Object2ObjectOpenHashMap.java:1275)
  ... .insert(:245) ... .put(:253)
  at net.minecraft.world.level.block.DispenserBlock.m_52672_(DispenserBlock.java:48)
  at net.yirmiri.dungeonsdelight.core.event.DDCommonEvents.registerDispenserBehaviors(DDCommonEvents.java:142)
  at ...DDCommonEvents.commonSetup(DDCommonEvents.java:54)
```

崩在 `FMLCommonSetupEvent dispatch for modid dungeonsdelight`,报告里 `Suspected Mods: NONE`(误导),fastutil 那一版是 Forge bootstrap 的 8.5.9。

**判定:不是 goetyominous,是 dungeonsdelight 1.3.1 的并发写崩,而且是偶发的。** 证据链:

1. 本仓 `grep -rn "registerBehavior\|DispenserBlock" src/main/java/` **零命中** —— 我们不参与那张表,不可能改它的容量。
2. 同一天 18:46 / 18:53 / 19:22 三次启动**同样是这套 110 个 mod,mod 加载全过**(`Mod Loading has failed` 计数 0),其中 19:22 那次用的还是带龙修复的前一版 jar。⇒ 不是"某 mod 版本必然崩",是非确定性。
3. `java -jar cfr DDCommonEvents.class` / 直接 grep 常量池:`DDCommonEvents` **没有 `enqueueWork`**,`commonSetup` 直接在事件处理器里调 `m_52672_`。而 Forge 的 `FMLCommonSetupEvent` 是 `ParallelDispatchEvent`,每个 mod 的处理器跑在各自的 ForkJoinPool 线程上(栈里能看到 `ModContainer.lambda$buildTransitionHandler$5` → `CompletableFuture$AsyncRun` → `ForkJoinPool`)。**Forge 明确要求这类注册必须 `event.enqueueWork(...)` 挪回主线程**。
4. 为什么是"长度 1025 却写 2048":vanilla `DispenserBlock.DISPENSER_REGISTRY = Util.make(new Object2ObjectOpenHashMap<>(), m -> m.defaultReturnValue(...))`,`n=1024` 时数组长度正好 1025(多一格留给 null key);索引 2048 说明 `rehash(2048)` 已经把 `n/mask` 换了、而 `key/value` 还是旧数组 —— 这是**两个线程同时 put** 的典型撕裂,不是容量计数问题(计数固定,纯 bug 的话四次启动会全崩)。

**How to apply:** 用户再丢"错误报告"且带这个栈,直接回:是 `dungeonsdelight` 的偶发并发崩溃,重启大概率就过;反复崩就删/换 `[地下城乐事] forge-dungeonsdelight-*.jar`(两个实例都有)。别去查 mixins.json、别去动自己的 tag/blockstate。相关:[[goetyominous-test-instance-and-logs]] [[forge-duplicate-modid-jar-silent-pick]]

顺带(与本崩溃无关,别混进来):该日志里 `goetyominous:blockstates/piglin_merchant_spawner.json` missing model for variant、`Missing sound for event: goetyominous:archgeomancer` 是我们在**这个**实例上真实存在的两条小警告。
