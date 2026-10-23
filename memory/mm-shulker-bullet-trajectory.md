---
name: mm-shulker-bullet-trajectory
description: 突变潜影贝导弹弹道的两处根因——上游那套 60 tick 直线+20 tick 陈旧瞄准让弹飞 60 格再绕回来；客户端也跑同一套制导但 moveDelay/trackSpeed 不同步，导致客户端往目标拐、服务端直飞，视觉一卡一卡
metadata: 
  node_type: memory
  type: project
  originSessionId: 51f63f6c-bee0-455c-b643-9f301c3041ff
  modified: 2026-10-16T01:59:55.987Z
---

**症状（用户原话）**：「潜影导弹飞离太远再飞回来，看起来一卡一卡的」。两件事其实是两个独立的病，2026-10-16 一起修了。

**病一：上游那套制导参数本身就是"远距离抛物+大回环"。** `MutantShulkerServantBullet.tick()` 与 MM 上游逐行相同：
- `moveDelay = 60`（发射时在 `MutantShulkerServant.shootMutantShulkerProjectile` 两分支 / `ShulkerBulletSpell` 设置）是"解锁倒计时"，**但只在 `distanceTo(target) >= 7.5` 时才递减** —— 近距离发射（目标就在 7.5 格内）时倒计时永远不走，`targetMovement` 一直是 null，**这发弹永远不会制导**，直线飞走。
- 解锁后每 20 tick 才重算一次 `targetMovement`，也就是说瞄准方向最多陈旧 20 tick；速度用 `Mth.lerp(0.1D, …)` 追这个陈旧方向 → 时间常数 10 tick、**转弯半径 ≈ 1.25/0.1 ≈ 12 格**，于是冲过头再画大弧绕回来。
- 竖直方向撞地会 +0.1 y（贴地爬升），保留未动。

**病二：客户端也在跑同一套制导，但它没有服务端那份状态。** 本仓库的弹继承 Goety `SpellThrowableProjectile` → `SpellTargetProjectile`，后者的 `getTarget()` 走 `TARGET_CLIENT_ID` **同步数据**，所以**客户端拿到的是真目标**，`tick()` 里那段制导客户端也在跑。而 `moveDelay`(默认 0)、`trackSpeed`(默认 1.25)、`targetMovement` 都是**只在服务端赋值的普通字段**：服务端 moveDelay=60 直飞，客户端 moveDelay=0 从第 1 tick 就开始追人 → 客户端把弹拽向目标、位置包又把它拽回服务端的直线，每 3 tick 一次。非生物实体的 `Entity.lerpTo` 是**瞬时 setPos（只有 LivingEntity/Boat 这类覆写者才做 steps 插值）**，所以纠正是硬跳不是平滑 → 一卡一卡。

**修法（服务端权威 + 客户端不预测）：**
- `tick()` 拆成 `tickServerMovement()`（全部制导+owner 推开+撞地爬升+move）与 `tickClientMovement()`：客户端**不再跑制导**，只 `move(SELF, getDeltaMovement())` 用同步来的速度滑行，再按固定增益 0.5 缓动到位置包给的坐标（覆写 `lerpTo` 存 `syncedPosition`；`teleport=true` 时才 `super` 瞬移）。粒子块照旧。
- `MmEntityRegistry` 里给该 EntityType 加 `.updateInterval(1)`：`ServerEntity.sendChanges` 的门是 `tickCount % updateInterval == 0 || hasImpulse || entityData.isDirty()`，默认 3 → 位置/速度包每 3 tick 才来一次；改成 1 之后滑行速度每 tick 都是新的。见 [[forge-bare-entity-update-interval-smoothness]]。
- 制导侧改：解锁倒计时**无条件每 tick 递减**（去掉 7.5 格门）、`moveDelay <= 0` 时**每 tick 重算**方向、追速系数 `0.1 → 0.35`（转弯半径 ≈ 3.6 格）、发射解锁 `60 → 8`（三处：本体制导/定点两分支 + 玩家聚晶）、`hurt()` 里被打偏的 `60 → 20`（打掉制导那几下的直线段缩短）。`distance` 除以 0 会出 NaN，加了 `Math.max(…, 0.001D)` 兜底。

**没动的**：`trackSpeed` 的数值与来源（本体 1.25、聚晶 1.25+velocity*0.2、陷阱读配置）、`hurt()` 里 `shootFromRotation` 的打偏机制、贴地 +0.1 爬升、浮空/伤害/炸裂逻辑。

相关：[[mm-shulker-levitation-injection-points]]、[[mm-mutant-shulker-anim-gate]]、[[mm-shulker-flight-removed]]
