---
name: mm-servant-aoe-ally-guards
description: MM 突变仆从的 canHarm 不排除主人/友军，范围伤害与击退要另加 MobUtil.areAllies
metadata: 
  node_type: memory
  type: project
  originSessionId: e336502b-123a-4b4b-b3e5-ce357f0ab660
  modified: 2026-10-23T20:31:22.611Z
---

从 Mutant More 移植的敌对突变怪，其 `MiscUtils.canHarmBasedOnTeamAndTag` 只查 entity type tag 和计分板队伍，**不排除主人**；原版当敌对怪时无所谓，做成仆从后 AoE 会打飞主人和友军。

**Why:** `MutantShulkerServant`/`MutantWitherSkeletonServant` 移植时在 `canHarm`/`isHostileTarget` 里加了 `isAlliedTo`/`MobUtil.areAllies`，其余（Husk/Blaze/Hoglin/FrozenZombie/JungleZombie）漏了，2026-10-23 修。

**How to apply:** 移植 MM 范围攻击时，`canHarm` 开头加 `if (MobUtil.areAllies(this, target)) return false;`；另外击退（`strongKnockback`/`entity.push`）常写成自成一段、不受 `canHarm` 门控，必须在循环里单独 `continue` 跳过友军。参见 [[mm-mutant-husk-servant-port-notes]]。
