---
name: goety-servant-trykill-confirm
description: "本仓库大型仆从统一覆写 tryKill 做\"二次点击才解散\"确认，样板就是那段 killChance/warnKill"
metadata: 
  node_type: memory
  type: project
  originSessionId: 4192c732-5f86-4d2b-987c-dd136bc9e155
  modified: 2026-09-29T23:45:32.895Z
---

仓库里**约 21 个大型/要员仆从**都写了同一段 `tryKill` 覆写（AC: Atlatitan/CaramelCube/Forsaken/Hullbreaker/Luxtructosaurus/Relicheirus/Tremorsaurus/Tremorzilla，AM: IllagerElephant/WarpedMosco，LM: OvergrownColossus/CloudGolem/AnnihilationPursuer/PossessedPaladin/ShulkerMimic/TheObliterator/BeheadedKnight/ResurrectedKnight，mobs: Cerberus/Heresiarch/MutantBlaze/MutantHoglin/MutantShulker/MutantWitherSkeleton…）。新增同级别仆从照抄：

```java
@Override
public void tryKill(Player player) {
    if (this.killChance <= 0) {
        this.warnKill(player);
    } else {
        super.tryKill(player);
    }
}
```

机制（Goety `Summoned`，逐行核实过）：

- **触发点**：潜行 + 右键自己的仆从（走 `DarkWand`，`SpellConfig.OwnerHitKill` 决定是"打一下"还是"右键"两条路），DarkWand 调 `IServant.tryKill(player)`。
- `killChance` 是 public 字段；`warnKill(player)` = 置 `killChance = 60` + 给玩家发 `info.goety.servant.tryKill` 消息。`60` 由 `IServant.servantTick()`（`:424-425`）每 tick 减一，而 `Summoned.tick()` 会调 `servantTick()` —— 所以**确认窗口是 60 tick / 3 秒**。
- `Summoned.tryKill(player)`（`entities/ally/Summoned.java:611`）= `this.hurt(ModDamageSource.DISMISSED, Float.MAX_VALUE)` —— **解散是走伤害的**。所以第一次点击只警告、第二次才真杀。

## ⚠️ 解散会不会被 `hurt()` 吃掉，只看这一条

`goety:dismissed` 在 Goety jar 的 `data/minecraft/tags/damage_type/` 里**只**塞进了 `minecraft:bypasses_invulnerability`（该文件里就它一个条目）和 `bypasses_armor`。

但这只在**门真的去看了伤害源**时才有用：

```java
if (gate && !source.is(BYPASSES_INVULNERABILITY)) return false;   // ✅ 解散穿得过
if (gate) return false;                                           // ❌ 解散被静默吃掉
```

**不看伤害源的无条件早退一律会吃掉解散。** 这是唯一要查的东西。

> 更正（2026-09-30）：本记忆旧版写着"云傀儡的 `isSleep()`、`attackState == 21` 都被 `!source.is(BYPASSES_INVULNERABILITY)` 包着，所以拦不住解散"——**推理是错的**。云傀儡 `hurt()` 里那行 `if (this.getAttackState() == 21) return false;` 是无条件的；而它的 `hurt()` 里压根没有 `isSleep()` 判断（`isSleep()` 是状态 1/2，只在 `addEffect` 等处用）。它结论上能正常解散纯属巧合，不是靠豁免。

## 已知踩过的坑

- **BeheadedKnight / ResurrectedKnight**（2026-09-30 修）：`hurt()` 顶部 `if (this.isSleep()) return false;` 无条件早退，而待命睡眠 = 状态 8 ∈ `isSleep()` → **待命时完全无法处死**。修法即上面第 ✅ 行，各加一个 `&& !pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)`。同一批还给这两个补上了缺失的 `tryKill` 覆写（此前醒着时按一次就直接没，没有二次确认）。
- **云傀儡 / FlamebornGuard**（2026-09-30 修）：两处 `hurt()` 顶部各加同一行豁免。云傀儡原来是 `GolemInvulnerabilityTime > 0`（每次受击成功都置 10，被围殴时是持续刷新 → 挨打期间处死反复失效，不是"0.5 秒窗口"）+ `getAttackState() == 21`；FlamebornGuard 原来是 `getAttackState() == 4 && attackTicks < 15` + `canBlock()`。
  - FlamebornGuard 的 `canBlock()` = `attackState == 0 && block_cooldown <= 0 && 有目标`——**处死会被它当成一次攻击格挡掉**（放格挡动画 + BLOCK 音效 + `setAttackState(4)`），且它**没有 `tryKill` 覆写**（火焰裔全家都没有），所以连 `warnKill` 提示都没有，玩家只看到"点了没反应"。不是死锁：那次格挡把 `block_cooldown` 置 60 且进入状态 4，等 ~0.75 秒再点一次就能处死。
  - 这两道门在 LM 原版 `FlamebornGuardEntity.hurt` 里逐字相同（已反编译核实），但 LM 原版没有主人/处死概念，属于"实现层冲突 → Goety 优先"。
  - **豁免集实测**（`bypasses_invulnerability` 是合并不是替换，Goety 那份 JSON 没写 `replace`）：`generic_kill` + `out_of_world` + `goety:dismissed` + `twilightforest:expired` + `the_bumblezone:architects/spikes`。**玩家拿任何武器都带不上这个 tag**，所以加豁免对正常战斗 0 影响。
- ⚠️ **不要靠"某只仆从能正常解散"反推它豁免了**：云傀儡当年看着正常纯属巧合（见下方更正）。
- 只免疫负面效果的 `addEffect` 门、以及 `IAnimatedBossServant.hurt` 那一串早退，都**不**影响解散——前者不是 `hurt` 路径，后者第一行就 `if (source.is(BYPASSES_INVULNERABILITY)) return super.hurt(...)`。**这一行就是本仓库的正解样板**，写在 `hurt()` 最顶上，让所有自定义免伤/格挡/无敌路径一起豁免。

## `/kill` 是同一根线上的第二个受害者

`/kill` 走 `LivingEntity.kill()` → `hurt(damageSources().genericKill(), MAX)`，而 `minecraft:generic_kill` 也在 `bypasses_invulnerability` 里。**所以上面那条 ✅/❌ 判据同时管两个**：看着伤害源的门，`/kill` 和普通伤害一样能过；不看伤害源的无条件早退，`/kill` 会被静默吃掉（而且 `amount = Float.MAX_VALUE > 1.0F`，会**触发格挡/弹反**，表现出来就是"怪物把 `/kill` 弹开了"）。

- **堕落圣骑**（2026-09-30 修，用户报"可以格挡主人的处死和 kill"）：它是全仓库唯一一处**四个门全都不看伤害源**的——`attackState == 26`、`BossInvulnerabilityTime > 0`、`isBlockin()`、`canParry()`。当年只给解散打了补丁（`boolean executedByOwner`，`tryKill` 里置真、`hurt` 顶部短路），所以**处死能过、`/kill` 过不去**。已改成顶部 `if (source.is(BYPASSES_INVULNERABILITY)) return super.hurt(...)`，`executedByOwner` 整块删掉（补丁被新门完全覆盖，`tryKill` 收回成标准样板）。

相关：[[goety-servant-goal-priority-flags]]、[[lm-servant-boss-mechanic-stripping]]
