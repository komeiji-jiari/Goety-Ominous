---
name: mm-hurt-inline-event-die-order
description: "移植准则:把 MM 的 LivingDamageEvent 处理器内联进 hurt() 时,必须放在 super.hurt() 之后但要自己补 !isDeadOrDying() 门控 —— 上游事件在 actuallyHurt 里、die() 之前触发,内联后 die() 会先跑"
metadata: 
  node_type: memory
  type: project
  modified: 2026-10-15T20:55:09.130Z
  originSessionId: 2de83047-6979-4f19-b5d1-4021d6d8c3a8
---

**Why:** MM 的 `MutantWitherSkeleton` 把"格挡计数"逻辑写在 `LivingDamageEvent` 里。Forge 的伤害流程是 `hurt()` → `actuallyHurt()` → **事件** → `die()`;我们移植时内联进了 `hurt()`(本仓库不许用 mixin,事件也懒得注册),只能放在 `super.hurt()` 之后 → **致命一击时 `die()` 先执行**,随后格挡分支再跑:`stopAndLockAllAnimations(4, [...])` 会停掉刚起的 `death`(`AbstractAdvancedAnimation.stop()` 把 `onFinish` 置 null = **回调被销毁,永不执行**),接着 `block.start(...)` 因 block 刚被 lock 而静默失败 → 全部动画锁死、无解锁 → 剩 ~20 tick 立正,**死亡动画/残骸部件/parts 掉落表全丢**。触发条件是"致命击恰好又是第 N 次格挡计数"(`block_hit_requirement` 默认 5),约 1/5 的死亡/解散。

**How to apply:** 内联这类事件逻辑时,在条件里补 `&& !this.isDeadOrDying()`(1.20.1 `LivingEntity.isDeadOrDying()` 就是 `getHealth() <= 0.0F`,反编译确认过)恢复"事件在 die 之前"的语义;或者老实注册 `LivingDamageEvent`。判据:凡是内联块里会 **start/stop/lock 动画**的,都要问一句"这一击会不会同时也是致命击"。

同类坑(已查):另 4 个 MM 仆从 `hurt()` 里没有内联事件,没这问题;`die()` 里**不要**简单补 `super.die(source)` 而不删手动那次 `this.dropAllDeathLoot(source)` —— `LivingEntity.die` 自己会调 `dropAllDeathLoot`,会**双倍掉落**。凋零骷髅 2026-10-15 的改法是 `this.dropAllDeathLoot(source)` → `super.die(source)`(顺带拿回 Goety `Summoned.die` 的"主人收到死亡消息"、`dead` 标志、`ENTITY_DIE` 游戏事件、byte 3、`Pose.DYING`、`ForgeHooks.onLivingDeath` 可取消)。见 [[mm-zombie-sinewave-walk-jitter-fix]]。
