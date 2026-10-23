---
name: am-cachalot-whale-squid-capture-api
description: AM 抹香鲸抓乌贼可纯 API 复刻(不 mixin)：锁定靠 tag、抓取后全类型通用、渲染层不查类型
metadata: 
  node_type: memory
  type: reference
  originSessionId: 7243ed09-ddbf-45ce-8d7c-6ee99bf5eaf9
  modified: 2026-10-10T16:25:31.288Z
---

`EntityCachalotWhale` 里抓乌贼只有**两处**按类型拦人：`tickCaptured` 调用(约 673 行)与"冲锋撞击改抓取"(约 748 行 `if (target instanceof EntityGiantSquid && !isBaby())`，否则只给一次平伤)。另外两条关键事实：

- **锁定是 tag 驱动的**：`EntityAINearestTarget3D(this, LivingEntity.class, 30, false, true, AMEntityRegistry.buildPredicateFromTag(CACHALOT_WHALE_TARGETS))`，predicate 就是 `e.isAlive() && e.getType().is(tag)`（AMEntityRegistry:505）。往 `data/alexsmobs/tags/entity_types/cachalot_whale_targets.json` 里 `replace:false` 追加自己的实体就能让鲸鱼锁定它。
- **`isGrabbing()` 之后全类型通用**：摆进嘴里、`setCaughtSquidId`、`grabTime>30` 起每 20 tick 4+rand(4)、`grabTime>300` 松口丢弃，都对任意 target 生效；渲染层 `LayerCachalotWhaleCapturedSquid` 走 `dispatcher.getRenderer(squid)` + `instanceof LivingEntityRenderer`，**不查类型** —— 自家渲染器会被免费画进嘴里（因此本体要在 captured 时 `shouldRender=false`，否则画两遍）。
- 鲸鱼相关 API 全 public：`isGrabbing/setGrabbing/isCharging/setCharging/setHoldingSquidLeft/isBeached/isSleeping/chargeProgress/getCaughtSquid`；`Entity.getDismountLocationForPassenger` 在 Forge 里也是 public。

**Why:** 2026-10-10 移植大王乌贼仆从时，用户选了纯 API 方案 A 而非 mixin —— AM 在本工程是 `compileOnly` + `mods.toml mandatory=false`，给可选依赖打 mixin 要引 `IMixinConfigPlugin` 门控，与 [[goety-ac-no-mixin]] 同一条原则。代价可接受：AM 以后改这段代码只会退化成"只挨一次平伤"，不崩。

**注意：大王乌贼仆从当天就被用户要求整个删掉了**（"删除乌贼仆从 这玩意不要了"），连同 `data/alexsmobs/tags/`（我们建的那个目录树）一起清空，所以**不要再主动重提这个移植**；上面的机制知识是留给"以后又要接 AM 的吞/抓机制"时复用的。

**How to apply:** 需要"第三方怪主动吞/抓我方生物"这类机制时，先查它的**目标选择是不是 tag 驱动**、**抓取后的状态机是否按类分型**，两者都是纯 API 就能接上的钩子；只把"触发点"用 Forge 事件补上即可（当年落点是 `LivingAttackEvent` 里 cancel 平伤并翻 `setGrabbing(true)` —— Forge 的 onLivingAttack 在原版 `hurt` 最前面，早于无敌判定）。相关：[[goety-ac-no-mixin]]、[[goetyominous-mod-folder-isolation]]。
