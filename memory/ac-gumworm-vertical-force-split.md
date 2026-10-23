---
name: ac-gumworm-vertical-force-split
description: AC 沙虫的垂直力全在 tick() 的 isRidingMode() 块里、travel() 一点重力都没有;我们地面行走版一度用 0.08 重力削弧线又导致落地飘;已换成骑乘 −0.8/非骑乘 −0.5;自主扑击水平行程 17~52 格远超 servantGuardingRange=16,靠 isGuardingArea() 关掉
metadata: 
  node_type: memory
  type: reference
  originSessionId: e6112b54-7831-4204-8858-4e13afce37db
  modified: 2026-10-03T01:52:18.664Z
---

AC `GumWormEntity`（`.scratch/ac_entity_full/com/github/alexmodguy/alexscaves/server/entity/living/GumWormEntity.java`）的垂直力**全在 `tick()` 里，`travel()`（`:415-425`）一点重力都没有**。分两块：

- **`if (this.isRidingMode())` 块（`:342-364`）—— 这是骑乘专用，不是钻地**（当初移植时归错类了）：
  - 脚下实心 / 水平碰撞 / 骑手在头顶 4 格以上，且非跳跃 → `scale(0.9).add(0, +0.8|+0.4|-0.1, 0)` 把虫顶出地表
  - 悬空且非跳跃 → `scale(0.9).add(0, -0.8, 0)`，等效重力 **0.8**（原版属性的 10 倍）
  - **`isLeaping()` 时以上全部跳过** → 跳跃弧线是纯 `sin(f1·1.5π)·2.0`，一点垂直干扰都没有
  - ⚠ 块尾 `this.noPhysics = flag1` **不能搬**：骑乘时 `flag1 = !horizontalCollision`，会在没撞墙的瞬间关掉碰撞，虫直接穿墙。只搬垂直冲量。
- 非骑乘只有两处：卡在不能钻的方块下方 → 向上弹 0.8；露出地面 >10 tick → −0.5。

`:371` 的 `setNoGravity(...)` 在 AC 那边**没有任何机械作用**（它自己的 travel 不读重力，只进存档字段）——我们没抄它不是漏项。

我们的 `GumWormServant.travel():227` 是无条件 `−ENTITY_GRAVITY`（0.08，`setCustomAttributes` 也没设这个属性 → Forge 默认），所以**每一 tick 都在削跳跃**。实测骑乘跳跃弧线（`sin(f1·1.5π)·2.0` 逐 tick 累加）：

| N（蓄力 tick） | 原版峰值/净位移 | 我们峰值/净位移 |
|---|---|---|
| 10 | 8.44 / 5.17 | 7.88 / 4.37（−6.6% / −15.5%） |
| 30 | 25.41 / 13.71 | 23.81 / 11.31（−6.3% / −17.5%） |

2026-10-03 已完成：重力整块换成 AC 的两支，且只在 `!isNoGravity() && !onGround() && !isLeaping()` 时生效——
- 骑乘：`!horizontalCollision && 脚下非实心` → `delta.scale(0.9).add(0,-0.8,0)`，终端 **−2.29 格/tick ≈ 45.7 格/秒**
- 非骑乘：`delta.add(0,-0.5,0)`，终端 **−2.0 格/tick ≈ 40 格/秒**

（这就是 ② 的垂直力块，`noPhysics = flag1` 按既定原则**没搬**。AC 非骑乘原有的「卡在不可钻方块下 +0.8 上弹」与 `outOfGroundTime > 10` 门控没有对应物，没搬。）旧实现是无条件 `−ForgeMod.ENTITY_GRAVITY`（0.08）**再被 travel 尾部的 `scale(0.8)` 阻尼压**，终端只有 **−0.32 格/tick ≈ 6.4 格/秒**——比 AC 慢 7 倍，表现为「落地缓缓飘落」。`ForgeMod` import 随之下掉。

**未做**的后续：③ `canContinueToUse()` 还缺 AC 的 `leapFor > 0 || !isInWall()` 那段俯冲尾巴（我们的虫不钻地，对应物是 `|| !onGround()`，落地才收招）。**`canUse()` 不要动**——AC 写的是 `if (!isInWall() || ...) return false`，即**要求虫埋在窒息方块里**（沙虫破土而出）；我们换成 `!onGround()`（站在地面上）正是非钻地版的对应物。曾把 `!onGround()` 误读成"要求悬空"、断言扑击被关掉，实际正相反（`GumWormServantAttackGoal:52/59` 用 `isInWall() || onGround()` 也印证了这个替代）。

**自主扑击的水平行程远超警戒半径（2026-10-03 查实）**：扑击的冲量是**每 tick 叠加**的（`setDeltaMovement(getDeltaMovement().add(leapDelta))`，`travel()` 里 `scale(0.8)`），水平速度收敛到 `5 × leapRange` 并保持；加上落地后没有地面摩擦、残余速度再滑 `5 × v_end`。实测水平净前进 ≈ `leapRange × (5·maxLeapTime + 5)`：N=15/r=0.3 → 16.7 格，N=20/r=0.5 → 40.1 格，N=25/r=0.5 → 52.5 格。**`findLeapFromPosition()` 挑的落点在半径内也没用**，行程本身就会冲出去。Goety 的 `servantGuardingRange` 默认 **16**（实例配置 `config/goety/goety-mobs.toml:296`）→ 每次扑击必出圈，然后被守位/游荡目标走回来，表现为「警戒时冲出去好远再回来」。**已修**：`GumWormServantLeapRandomlyGoal.canUse():33` 加 `|| this.entity.isGuardingArea()`，警戒时（除战斗外）不扑。注意警戒时的**索敌本身已经是钳住的**——`Summoned.setTarget()` 在 `isGuardingArea()` 且非 `isPrioritizing()` 时拒掉距 `boundPos` 超过 `GUARDING_RANGE` 的目标，所以追敌不是元凶；但战斗扑击（`GumWormServantAttackGoal` 的 `dist > 13` 突进）同样有叠加冲量的越界问题，用户明确要保留。

相关：[[ac-impulse-movecontrol-speed]]、[[goetyominous-deadcode-cleanup-preference]]、[[ac-goety-port-probability-gates]]
