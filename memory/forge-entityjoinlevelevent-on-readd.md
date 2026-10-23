---
name: forge-entityjoinlevelevent-on-readd
description: "对已入场的实体调 addFreshEntity/addDuringTeleport 会照发 EntityJoinLevelEvent(仅 UUID 检查失败报警告,实体本身零效果)——在 goal tick 里做这件事会被第三方「join 时加 goal」的 mod 打成 GoalSelector CME"
metadata: 
  node_type: memory
  type: project
  originSessionId: d796dd1a-3838-4673-abda-f9f57993ada2
  modified: 2026-09-25T23:51:23.592Z
---

`ServerLevel.m_8872_(addFreshEntity) / m_143334_(addDuringTeleport) / m_7967_ / m_8847_` 全是同一个方法,走 `PersistentEntitySectionManager.m_157533_ → m_157538_`,而 **`EntityJoinLevelEvent` 是在 UUID 去重检查之前 post 的**:

```java
private boolean m_157538_(T e, boolean worldGen) {
   if (e instanceof Entity entity && MinecraftForge.EVENT_BUS.post(new EntityJoinLevelEvent(...))) return false;
   return addEntityWithoutEvent(e, worldGen);
}
// addEntityWithoutEvent → m_157557_: UUID 已在 f_157491_ → warn("UUID of added entity already exists") + return false
```

所以对一个**已经在场**的实体调 `addFreshEntity`/`addDuringTeleport`,除了 (1) 白跑一遍 EntityJoinLevelEvent、(2) 刷一条 `UUID of added entity already exists` 警告之外**什么都不做**——`onAddedToWorld()` 也不会再调。LM 的 `EntityUtil.applyServerTeleport` 就是这个 cargo-cult,`teleportTo` 后面接它纯属多余。

致命处在于 (1):`[灾厄侵扰] illageandspillagerespillaged` 的 `ForgeEventBusSubscriber.stopMobs(EntityJoinLevelEvent)` 无任何门控地对**每个 Mob** `f_21345_.m_25352_(0, new LoseAIGoal(...))`。若这次多余的 re-add 发生在该实体自己的 `goalSelector.tick()` 里(即某个 running goal 的 `tick()` 调了传送),`tickRunningGoals` 正在 for-each `availableGoals`(LinkedHashSet)→ **ConcurrentModificationException**,崩在 `GoalSelector.m_186081_`。查到的实例:LM 仆从(FlamebornGuard/Warrior/AnnihilationPursuer/FlameDrifter/ShulkerMimic)传送后崩在 `Ticking entity`。

同类「join 时改 goal」的 mod(排查时可直接全量筛):mutantmore `ModifyAIEvent.makeWanderingTradersScaredOf`(WanderingTrader)、savage_and_ravage、upgrade_aquatic、mowziesmobs、eeeabsmobs、twilightforest、FarmersDelight、piglinproliferation、goetydelight(BiscatItem,Creeper)、Goety `ModEvents`(按 tag)、goetyominous 自己的 RaycatAmuletEvents。

**Why:** 只有 running goal 的 `tick()` 里结构性改动**同一个** GoalSelector 才会 CME;`EntityJoinLevelEvent` 是唯一能把「外来 mod 的 goal 写入」塞进那一段执行窗的通道。

**How to apply:** 移植 LM/AC 传送代码时删掉 `teleportTo` 后的 `EntityUtil.applyServerTeleport(this)`(LM 的 FlamebornGuard/Warrior 本来就没有,AnnihilationPursuer/FlameDrifter/ShulkerMimic 才有,全是空操作);任何「移动实体」的代码一律不要用 addFreshEntity/addDuringTeleport,要发位置包就直接发 `ClientboundTeleportEntityPacket`。看到 `UUID of added entity already exists` 警告就要顺藤摸到是谁在 re-add。[[lm-mechanic-presence-version-check]] [[goety-servant-goal-priority-flags]]
