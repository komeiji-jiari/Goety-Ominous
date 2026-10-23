---
name: goety-multipart-part-as-servant
description: "多部件肢体(AM/AC 那条继承链)若 extends Summoned,会白跑整套仆从逻辑并可能崩;状态要横推不能委派父实体"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 3e26bb4f-f1d2-4d77-abef-64a4680b6785
  modified: 2026-10-08T17:43:42.058Z
---

把 AM/AC 的多部件肢体（body/tail 分段）改成 `extends Summoned` 而不是 `extends Mob` 时（洞穴蜈蚣就是），有三个独立坑，2026-10-08 全部实测/反编译确认：

**1. 委派 `isCommanded()` 但不委派 `getCommandPos()` = 服务端必崩。**
`Summoned.isCommanded()` = `commandPos != null`，`getCommandPos()` 返回自己的字段。肢体重写 `isCommanded()` 去问头实体、又没重写 `getCommandPos()`（因为想让 setCommandPos 空转）→ `IServant.commandMode()` 里 `owned.getNavigation().isStableDestination(this.getCommandPos())` 收到 null，`PathNavigation.isStableDestination` 第一行 `p.below()` → NPE（实体 tick 异常 = 崩端）。触发极容易：**拿命令之角对着方块右键**（`CommandHorn.useOn` 无模式门槛），头被设上 commandPos 的下一 tick 就炸。
反过来也不行：补 `getCommandPos()` 委派会让每节身体在 commandMode 末尾 `moveTo(commandPos)`，整条链子瞬移散架。正解是 `isCommanded()` 直接 `return false`（肢体永远不可能被命令到某点）。

**2. 肢体跑 `servantTick()`/`ownedTick()` 全是白烧。**
`Summoned.tick()` = `super.tick() + servantTick()`，`Owned.tick()` = `super.tick() + ownedTick()`。8 节身体每 tick = 8 次 `targetingTick()` 里 `getEntitiesOfClass(Mob, bbox.inflate(FOLLOW_RANGE))` 大范围扫描 + 8 个没人用的 EntityReference + 8 段 chunkLoad + stayingMode 的速度修饰符（肢体靠 moveTo 位移，修饰符无效）。`healServant`/`burnServant` 倒是恒空转（身体血量永不掉：`hurt()` 把伤害转发给头后自身不受伤；身体 fireImmune）。修法：肢体覆写这两个 default 方法为空。

**3. 客户端拿不到父实体 → 状态委派退化成 super → 双端不一致。**
`getParent()` 沿用 AM 写法 `!isClientSide ? getEntity(uuid) : null`，客户端恒 null → 委派 getter 掉进 `super`（自己的 synced flag，永远 false）。修法不是给 getParent 加客户端分支（UUID 在客户端查不到实体），而是**让头在 `setWandering`/`setStaying`/`setCommandPos` 里把「有效值」横推给所有分段**——这套状态是 synced entity data，推下去双端自然一致。要点：
- 只覆写 `setX(boolean)` 两个就够，因为 Goety 所有模式切换（`setFollowing()/setGuarding()/setWandering()/setStaying()/copyStance/updateMoveMode/CommandHorn`）都收敛到 `setWandering(boolean)`+`setStaying(boolean)`。
- 必须额外覆写 `setCommandPos`，否则「待命 + 被命令到某点」时头的 `isStaying()` 因 `!isCommanded()` 变 false，而分段的 flag 还停在 true（分段的 isCommanded 恒 false，表达不了这个叠加）。
- 分段原有的 `setWandering/setStaying` **空实现必须删掉**，否则推不进去。删掉后 CommandHorn 会直接给每个分段写同名值，正好一致。
- 创建分段时补一次、重新连线（`shouldReplaceParts` 分支，即读档后）补一次；synced data 值相同不产生网络包，所以每次模式切换都推也无所谓。
- 残留差异：命令期间的推挤（`Summoned.push/doPush` 的 `!isStaying()` 门）会随 flag 走，纯表现，可忽略。

相关:[[goety-multipart-part-projectile-crash]]（同一条链上另一个坑：肢体被第三方投射物命中）、[[goety-servant-follow-standby]]、[[goety-command-mode-conflict]]、[[ac-servant-parity-keep-original]]
