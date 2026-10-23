---
name: forge-flying-stroll-deadzone
description: 1.20.1 RandomStrollGoal 陆地游荡目标搜索的死区:飞行实体悬停离地>7格时 getPosition 永远为 null
metadata: 
  node_type: memory
  type: reference
  originSessionId: 34e4deda-e0aa-4397-977d-35e959a7ca33
  modified: 2026-08-29T02:21:03.437Z
---

1.20.1 飞行实体(RandomStrollGoal / FlyingPathNavigation)陆地游荡的目标搜索机制:

- `RandomStrollGoal.getPosition()` = `DefaultRandomPos.getPos(mob, 10, 7)`,x/z 半径 10、垂直 dy 仅 ±7。
- 候选必须同时通过:`GoalUtils.isNotStable`(即 `navigation.isStableDestination(candidate)` = `!level.getBlockState(pos.below()).isAir()` —— **候选格下方必须是实心/非空气**)、`hasMalus`(该格 getBlockPathTypeStatic 的 pathfindingMalus == 0,空气块为 OPEN=0)。
- 因此陆地候选实际只可能是"恰好位于地表面一层、下方是实心块的空气格"。dy 需从自身高度精确落到地表 → 平坦地形上仅 1/15 的 dy 值可用(约 49% 成功)。
- **死区:实体悬停离地 >7 格时,±7 垂直范围内永远找不到"下方实心"的候选 → getPosition() 恒 null → 游荡 goal 永不触发,原地悬浮。** 无重力 + 无水下锚定的飞行召唤物(如 DeepOneMageServant)容易卡进这个死区。

AC 的 DeepOneWanderGoal 反例:其 canUse 有 `mob.isInWater()` 门控(水上才激活),靠把法师钉在水面附近避开死区;陆地游荡仍只有 RandomStrollGoal。

**守卫模式同样缺约束**:Goety 自家仆从走 `Summoned$WanderGoal.randomBoundPos()`(游荡目标限在守卫点 ±GUARDING_RANGE/2 内,staying 时停游荡);换成原版 RandomStrollGoal 的实体守卫时会漂浮着越飘越远、不回岗哨。解法:覆写 stroll.getPosition(),`isGuardingArea()` 时以 `getBoundPos()`(IServant.getBoundPos)为中心取随机偏移、沿列下探到地表返回 ground+2——一石二鸟:既把游荡约束回守卫范围,又让无重力飞行实体俯冲贴地巡逻(守卫点通常在悬浮高度设置,直接 3D 球心判攻击范围会偏高)。DeepOneMageServant 已按此实现。

相关:[[forge-compile-setup]]、[[ac-compat-porting-notes]]
