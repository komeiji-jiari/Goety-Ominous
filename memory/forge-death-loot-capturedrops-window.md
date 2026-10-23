---
name: forge-death-loot-capturedrops-window
description: 1.20.1 dropAllDeathLoot 里 dropEquipment() 在 captureDrops 窗口内、且在 doMobLoot 门控外 —— 覆写它做背包掉落会自动汇进 LivingDropsEvent
metadata: 
  node_type: memory
  type: reference
  originSessionId: 0d1ac7eb-dd47-4947-be68-37a12dabd63d
  modified: 2026-09-23T16:41:32.920Z
---

`LivingEntity.dropAllDeathLoot`（`.scratch/forge_src2/.../LivingEntity.java:1360`）的真实顺序：

```java
this.captureDrops(new java.util.ArrayList<>());      // ← 窗口开启
if (this.shouldDropLoot() && level().getGameRules().getBoolean(RULE_DOMOBLOOT)) {
    this.dropFromLootTable(...);                      // 战利品表（受 doMobLoot 门控）
    this.dropCustomDeathLoot(...);
}
this.dropEquipment();                                 // ← 无条件调用，且在窗口内
this.dropExperience();
Collection<ItemEntity> drops = captureDrops(null);    // ← 窗口关闭，取出
if (!ForgeHooks.onLivingDrops(this, source, drops, i, ...))
    drops.forEach(e -> level().addFreshEntity(e));
```

两个可利用点：

1. **`dropEquipment()` 不受 `shouldDropLoot()` / `RULE_DOMOBLOOT` 门控**，是"这个实体死了就要执行"的位置。
2. 此时 `captureDrops != null`，所以 `this.spawnAtLocation(stack)` 不会直接进世界，而是被收进 `drops`，**自动汇入 `LivingDropsEvent`**（其它 mod 的事件处理器能看到、也能清掉）——不用自己去构造 `ItemEntity`。

`GraveGolem` / `AbstractIllagerServant`（Goety）就是覆写 `dropEquipment()` → `dropAllInventory()` 来让仆从死亡时吐背包。注意 Goety 那版漏了 `setItem(i, EMPTY)`，`spawnAtLocation` 不复制 ItemStack，容器和 ItemEntity 会共用同一个栈。

**反向要点**：从 A 实体的 `LivingDropsEvent` 处理器里调 `B.spawnAtLocation(...)`，读的是 **B 自己的** `captureDrops`（当时为 null），所以直接落世界，不会被受害者那轮 capture 吞掉。碎船兽装不下的溢出掉落走的就是这条路，见 `ModEventHandler.onLivingDrops`。

**但 `dropEquipment()` 不是万能钩子**：它在 `die()` 里跑，早于 `tickDeath`，所以做不了"死亡动画期间分批吐"的表现。碎船兽最终没用它 —— 物品留在 `SimpleContainer` 里（NBT 已存），由 `tickDeath` 按动画 tick 分批吐，`remove(KILLED|DISCARDED)` 兜底吐剩下的。**`remove()` 里必须排除 `UNLOADED_TO_CHUNK` / `CHANGED_DIMENSION`**，否则每次区块卸载都会把背包倒在地上。

AC 原版碎船兽（`.scratch/ac_entity_full/.../HullbreakerEntity.java:232,248,269`）就是这套：`dropFromLootTable` 空覆写、`populateDeathLootForHullbreaker()` 把战利品表滚进 `deathItems`、`tickDeath` 里 `getAnimationTick() > 10 && % 7 == 0` 每次随机吐一个、`spawnAtLocation` 被覆写成从**嘴部**（headPart 位置 − 0.5×前向 + 0.5y）带前向初速（0.8~1.1）飞出并 `setGlowingTag(true)`。本仓库只移植了其中的胚胎部分，战利品表（奥秘引擎/海玻璃碎片/铜锭/废金属，见 `data/alexscaves/loot_tables/entities/hullbreaker.json`）没搬。

**吐出的"时刻"固定按 AC 原版**（用户明确要求，别自作主张把间隔改成自适应）：

```java
// tickDeath 内
if (!this.level().isClientSide && this.getAnimation() == ANIMATION_DIE
        && this.getAnimationTick() > 10 && this.getAnimationTick() % 7 == 0) {
    int beatsLeft = (42 - this.getAnimationTick()) / 7 + 1;
    int batch = (this.lootCount() + beatsLeft - 1) / beatsLeft;
    for (int i = 0; i < batch; ++i) {
        this.spitRandomLoot();
    }
}
```

吐出点是 animationTick **14 / 21 / 28 / 35 / 42 共 5 次**，`ANIMATION_DIE` 50 tick、46 才 `remove()`，5 次正好铺满死亡动画。AC 战利品表也是固定 5 池，所以原版永远刚好吐完 —— 这个"刚好"是巧合，不是设计余量。

**每拍吐几个按剩余量均摊**（用户要求"平均一下，每次吐出来差不多的数量"）：本仓库背包 36 格，一拍一个装不下。`beatsLeft` 由最后一拍 42 倒推，`ceil(存货/剩余拍数)` 让每拍数量接近且必然在 5 拍内吐完（36 → 8/7/7/7/7，20 → 4/4/4/4/4，5 → 每个一拍，**退化回原版**）。装不满时前面几拍会稍微多一点、后面自然只剩 1 个。存货为 0 时 batch 算出 0，循环空转，不用额外判空。

曾试过"改间隔来平摊"（`% 7` → `% 4` 或按存货算 `spitInterval`），两次都被要求改回**时刻不动**。要改只能改**每拍数量**，不能改时刻。

**吐东西必须自己驱动动画**：`spawnAtLocation` 只管物体，嘴不动。碎船兽的 `ModelHullbreakerServant` 里 `jaw` 骨骼静止时只有 `walk(jaw, idleSpeed, idleDegree * 0.15F, ...)` 的微幅摆动 —— 等于闭嘴。`ANIMATION_DIE` 恰好把 jaw 从 0 开到 80° 再收 40°，覆盖 animTick 10~45，正好是死亡吐出窗口，所以死的时候嘴是张的；而**主动催吐是自设行为、没有现成动画**，当初漏了 `setAnimation` 所以闭着嘴吐。已加 `ANIMATION_VOMIT`（jaw 5 tick 开到 80° → 保持 75° 15 tick → 5 tick 收），吐出时机从 `mobInteract` 挪到 `tick()` 里判 `getAnimationTick() >= 5`，跟 MeleeGoal 判 BITE 伤害同一个套路。

该模型角度约定（照 BITE/BASH/DIE 反推）：**jaw +80° = 最大限度张嘴**；head xRot 负 = 抬头、正 = 低头（BITE 抬头张嘴用 -40°，BASH 低头砸用 +50°）。

拾取侧（`InventoryCarrier` + `SimpleContainer` + 配置门控）样板：本仓库 `AbstractPiglinServant`、`HullbreakerServant`；触发侧对照 Goety `ServantEvents.DropEvents`。
