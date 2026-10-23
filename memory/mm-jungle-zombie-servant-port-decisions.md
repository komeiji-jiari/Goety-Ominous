---
name: mm-jungle-zombie-servant-port-decisions
description: MM 突变丛林僵尸移植成仆从的五项定案(多命保留/跳跃破坏去掉/手臂道具做过又删/小海龟目标不做/DISMISSED+STARVE 放行)+ 三处不能直引的客户端定义
metadata: 
  node_type: memory
  type: project
  originSessionId: c9861133-b6bd-4efe-ad0a-eeefa263a153
  modified: 2026-10-15T17:57:48.512Z
---

2026-10-15 用户对 `mutantmore:mutant_jungle_zombie` 移植成 Goety 仆从的定案:

- **多命保留** — 照抄 `get_up_times`(默认 4)+ `REMAINING_LIVES`/`LIVES_LOST` 同步字段 + 改写的 `die()`/`tickDeath()`(躺下 160 tick、真死 35 tick 后手动 `dropAllTickDeathLoot()`、`dropAllDeathLoot` 整个清空)
- **跳跃破坏不保留** — `aiStep()` 里 jump_griefing 那段"砸碎 `mutantmore:unbreakable` 以外任何方块 + 砸碎即停跳"不要
- **玩家侧手臂道具:做过又删** — 原本做了 `MutantJungleZombieArmItem`(带 BEWLR 的投掷手臂)+ 抓取包 + 客户端 tick 事件 + choke 里那段成就判定,2026-10-15 当晚用户改主意,整套已删(含 choke 里那段判定)。备份见 [[mm-mutant-jungle-zombie-arm-port]],别再主动加回
- **小海龟目标不做** — `attacks_baby_turtles` 那段 AllDirectionsTargetGoal 去掉(对齐 MutantHoglinServant)
- **DISMISSED + STARVE 都放行** — 见 [[goety-multilife-servant-dismiss-conflict]]

**How to apply:** 客户端三处不能直引——`MUTANT_JUNGLE_ZOMBIE_CHOKE(MutantJungleZombie)` / `MUTANT_JUNGLE_ZOMBIE_DOWN(MutantJungleZombie)` 形参写死 MM 实体类型,`MutantJungleZombieSineWaveAnimations.*(MutantJungleZombieModel, ...)` 写死 MM 模型类型;要像 `MutantHoglinSineWaveAnimations` 那样在自己包里重写(读 `getGrabArmXRot()`/livesLost),其余 keyframe 定义可直引。模板 = `MutantHoglinServant` + `AbstractMutantServant`。

其他易漏:攻击动作走老式 AnimationState + tick 计数 + `handleEntityEvent` byte 4..16(**不是** AdvancedAnimatable 框架),客户端模型直读这些字段;抓人的"按住"是 `hurtMarked=true` + `setPos` + 零速(玩家另加 `teleportTo`),命中判定是手搓线段 AABB 扫 `PositionUtils.getOffsetPos`,要连 `PositionUtils` 一起搬;`failedAttacks >= 3 + livesLost` → `restTick=60` 定身;`AbstractMutantServant` 只覆盖了 walk/hurt 两种破坏。仓库里已有的 `data/mutantmore/mutation_type/jungle_zombie_servant_to_mutant.json` 是 dungeonsmobs 那条,别重复加。
