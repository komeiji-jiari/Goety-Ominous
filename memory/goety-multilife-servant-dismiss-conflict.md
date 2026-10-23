---
name: goety-multilife-servant-dismiss-conflict
description: "带多命/虚拟血量复活的上游 Boss 移植成仆从后,会吃掉 Goety 的解散与寿命两条非战斗死亡通道——die() 白名单必须放行 DISMISSED + STARVE"
metadata: 
  node_type: memory
  type: project
  originSessionId: c9861133-b6bd-4efe-ad0a-eeefa263a153
  modified: 2026-10-15T17:12:38.408Z
---

MM `MutantJungleZombie`(4 条命)、LM `IAnimatedBoss` 那类"死不透"的怪,上游 `die()` 的真死白名单通常只认 `摔出世界 / GENERIC_KILL`。而 Goety 有两条非战斗处死通道都不在里头:

- 解散:`Summoned.tryKill(player)` 与 `IOwned.dismiss()` 是同一条 → `hurt(ModDamageSource.getDamageSource(level, ModDamageSource.DISMISSED), Float.MAX_VALUE)`
- 寿命到期:`IOwned.lifeSpanDamage()` → `hurt(damageSources().starve(), 1.0f)`,每 tick 1 点

后果:解散 → 躺一下满血爬起来,散不掉;寿命 → 每 tick 1 点永远追不上复活回的血。更坑的是躺下期 health=0,`LivingEntity.hurt()` 里 `else if (this.isDeadOrDying()) return false;` 会把后续 DISMISSED 伤害整个吃掉——玩家再解散一次也无效,只能 `/kill` 或推下虚空。

**Why:** Goety 自己就是靠 `source.is(ModDamageSource.DISMISSED)` 分辨"被解散"和"被打死"的(`AbsorberServant.java:279`、`MagispellerServant.java:1620`、`TremblerServant.java:299`)。

**How to apply:** `die()` 判真死时补 `source.is(ModDamageSource.DISMISSED) || source.is(DamageTypes.STARVE)`,并覆写 `tryKill(player)`(或 `hurt()` 顶部)特判躺下期被解散。2026-10-15 用户对突变丛林僵尸定案「两条都放行」。主人死亡/换维度走 `discard()`,不经 `die()`,不受影响。相关 [[goety-servant-trykill-confirm]]、[[lm-ianimatedboss-virtual-health]]、[[mm-jungle-zombie-servant-port-decisions]]。
