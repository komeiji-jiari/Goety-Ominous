---
name: cuotiben-custom-mob-skill-animation
description: 徒弟错题本·自建生物技能篇：法术 goal 双端幽灵施法、共享冷却、warmup 减半吞弹、动画时长/LOOP/END 对齐、homing 投射物同步、多部件旧位置、Blockbench 杂 channel
metadata: 
  node_type: memory
  type: project
  originSessionId: 6cf5bda6-4c83-4a13-99b5-52eebf72879b
---

来源：2026-11-06 火术师（PyrologerServant / PyromancerFireball / FireWallEntity）实战踩坑。下一只新生物直接照这份清单检查。相关：[[cuotiben-goety-servant-framework]]、[[goety2-spell-system]]、[[goety2-servant-system]]。

## 1. 法术 goal 必须只在服务端跑

- Minecraft 的 goalSelector **双端都跑**。自定义施法 goal 若不在 `canUse()` 里挡客户端，会出现：
  - 客户端 `spellCool` 是普通字段、永远 0（服务端设置/递减不同步）；
  - 服务端 FireBreath 还在喷（服务端 `spellCastingTickCount > 0`），客户端却因同步的施法 ID 被清而以为没在施法 → 客户端自己起手 FireWall/Fireball，切客户端动画、生成**幽灵火墙/火球**，服务端喷火照旧。
  - 自然结束后客户端也会抢先起手下一招，把 END 动画覆盖掉，看起来像"跳过 END"。
- **所有自定义 spell goal 的 `canUse()` 开头加 `if (this.level().isClientSide) return false;`**，客户端只根据服务端同步的 `ANIM_STATE` 播动画。
- `SpellcasterIllagerServant.spellCastingTickCount` 只在服务端 `customServerAiStep` 每 tick 递减；客户端的 `isCastingSpell()` 读同步的 `DATA_SPELL_CASTING_ID`。基座 `CastingSpellGoal` 没有 `canContinueToUse`，被更高优先级 goal 的 flag 冲突中途 stop 时会把这个同步 ID 清成 NONE——**给它补 `canContinueToUse() { return getSpellCastingTime() > 0; }`，并让 `stop()` 只在 `getSpellCastingTime() <= 0` 时调 `super.stop()`**，防止施法中途清同步状态。
- 每个 spell goal 的 `stop()` 里**无条件** `spellCastingTickCount = 0; setIsCastingSpell(NONE);`。自然结束时计数已归零，条件式（`if > 0`）清理会漏掉，同步施法 ID 卡住。

## 2. 每个 goal 自带冷却 ≠ 招数间隔

- 基座 `SpellcasterUseSpellGoal` 每个 goal 各自维护 `nextAttackTickCount`，彼此独立；A 技能 stop 后 B 的冷却可能早就好了，下一 tick 就能起手，没有任何间隔。
- 在实体上做共享冷却：`spellCool` 字段，服务端 tick 递减；**每个 spell goal 的 `canUse` 检查 `<= 0`，每个 `stop()` 设 40（2 秒）**。设在后停而不是 start，语义才是"上一次施法结束后至少 2 秒"。
- 注意 goalSelector 的 tick 顺序是 **cleanup（stop）→ goalUpdate（start）→ tickRunning**，所以 stop 里设的冷却在同 tick 的 goalUpdate 已经生效，一般没有 1 tick 空窗；但 stop 之后 `spellCastingTickCount` 归零的那一 tick 仍要注意其它 goal 的 `isCastingSpell` 判断。

## 3. `adjustedTickDelay` 会把前摇减半

- `SpellcasterUseSpellGoal.start()` 用 `adjustedTickDelay(getCastWarmupTime())`。`Goal.requiresUpdateEveryTick()` 默认 **false**，此时 `adjustedTickDelay` 走 `reducedTickDelay` = `ceil(i / 2)`。
- 案例：火球 `getCastWarmupTime() = 30` 实际变成 15，goal 在第 16 tick 就停止；而 `launchFireball()` 在第 20 tick，stop 时 `!launched` → 手里的火球 `discard()`，表现为"火球出现后直接消失、没飞出去"。
- 需要精确前摇的 goal 覆写 `requiresUpdateEveryTick()` 返回 true（项目里 DiscipleServant 已有先例）。FireWall/Support 的施法时点也受同一坑影响。

## 4. 动画时长应决定技能节奏

- Pyrologer 其他技能本来就是按动画长度填的：`FIRE_WALL` 1.625s → 33t，`FIREBALL` 1.5s → 30t，`FIERY_HIDE` 1.6667s → 34t。新生物照做，别从别的怪硬抄数字。
- `FIRE_BREATH_LOOP` 是 `.looping()`，**动画本身没有总时长**；必须额外决定循环次数/配置上限。默认 `fireBreathTime = 100` tick = 5 个 20t 循环；技能结束点必须落在**完整循环边界**，否则 END 起手帧与 LOOP 0 帧姿态对不上会跳变。
- END 动画的重置倒计时不要与动画等长裸奔（END 0.5s = 10t，倒计时也 10t，客户端晚一 tick END 就被掐掉）：**动画时长 + 2t 余量**（如 12t），并在 `setAnimationState()` 里加 guard：
  ```java
  if (state == ANIM_NONE && getCurrentAnimation() == ANIM_END && animResetTicks > 0) return;
  ```
  只有倒计时自己减到 0 时才能切 NONE，防止其它 goal 的 `stop()` 中途覆盖 END。
- 动画状态是 synced int；`entityData.set` 相同值不会重新触发 `onSyncedDataUpdated`，不会重启动画。

## 5. Homing 投射物的客户端同步

- 只改服务端 `xPower/yPower/zPower` 不够：这些字段不同步，客户端实体没有速度也不会自行移动，只靠 position packet 一跳一跳地"传送"，轨迹/朝向/拖尾都会卡；有加速过程时特别明显。
- 用 `SynchedEntityData` 同步 **held + 三个 power**：
  - 服务端 `setHeld()` / `launch()` / `handleHoming()` 统一通过 `setPowers(direction)` 写普通字段和同步数据；
  - 客户端 `tick()` 先 `xPower = entityData.get(...)` 再 `super.tick()`，用同一套惯性/加速复现；
  - `held` 也要同步，否则服务端握持期间客户端已经在跑飞行物理。
- 原版恶魂火球物理：`power = 归一化方向 × 0.1`，初速度 0，`AbstractHurlingProjectile` 每 tick `(delta + power) × 0.95` → 稳态 **1.9 格/tick（约 38 格/s）**。不要每 tick 强制 `setDeltaMovement(dir * 固定速度)`，那样既没有加速也不是原版速度。

## 6. 多部件实体（PartEntity）

- `PartEntity` 不进入 level 实体列表、**没有任何代码会 tick 它**。只 `setPos` 不更新 `xo/yo/zo`，客户端渲染用 `lerp(旧值, 新值)`、旧值永远停在 0，表现为碰撞箱上下抽搐/"沉入地里"；只有主实体（正常 tick，`customServerAiStep`/`baseTick` 会更新旧位置）是稳定的。
- 每 tick 对每个 part：`part.setPos(...); part.setOldPosAndRot();`（后者一次写 `xo/yo/zo` + `xOld/yOld/zOld` + 旧旋转）。Iron 的 `WallOfFireEntity` 就是手动同步这些字段。
- 火墙挡弹射物：`PartEntity.canBeHitByProjectile()` 必须为 true（`isPickable()`）才能拦。自家/友方投射物穿过应交给 `ProjectileImpactEvent`：`wall.isOwnerSide(projectile.getOwner())` 时 `event.setCanceled(true)`，不要为了"别让自家火球撞墙"把 `canBeHitByProjectile` 全关——那会让敌人投射物也穿过去。

## 7. 火墙的视觉高度 ≠ WALL_HEIGHT

- `WALL_HEIGHT` 只控制 PartEntity 碰撞/伤害高度和 `getWallArea()`；视觉高度由 `ambientParticles()` 决定。粒子只在地面 `y+0.1 ~ y+0.35` 时，无论碰撞箱多高都看着矮。
- 要真高：粒子 y 分布到 `0.1 ~ WALL_HEIGHT`，每段数量 2~3，沿墙段均匀分布；否则只加碰撞箱没有视觉效果。

## 8. Blockbench 动画里的杂 channel

- 导出的 `WALK` 动画可能带目标骨骼（如背后旗帜 banner/drop）的单帧 `SCALE` / `POSITION` channel：走路时旗帜缩到 0.65、抬高 5px（=0.3125 格），切 idle 时 `resetPose` 恢复，看起来像 bug。
- 检查每个动画里非目标骨骼的 channel；不需要就从该动画删掉，让骨骼回到模型基础姿势。删 SCALE 只修大小，删 POSITION 才修上下位移。
