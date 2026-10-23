---
name: mm-shulker-levitation-injection-points
description: 突变潜影贝子弹浮空的四处注入点（本体射击两分支/陷阱变弹/玩家法术）+ 子弹字段与 NBT + 「已浮空不刷新」门 + 最大生命阈值键 MobsConfig.MutantShulkerBulletLevitationMaxHealth（2026-10-16 删过又加回，现值 100）
metadata: 
  node_type: memory
  type: project
  originSessionId: 51f63f6c-bee0-455c-b643-9f301c3041ff
  modified: 2026-10-16T01:59:57.896Z
---

**现状（2026-10-16 起）：浮空是开着的，四处注入点齐活。** 同一天用户先分三批下令删除（①陷阱弹不施加 ②本体射击也要断 ③玩家法术不用浮空 + 删阈值键），随后一句「把突变潜影贝的浮空加回来 不删了」整批回退，确认范围时选了「三处全恢复 + 阈值键」。**别再看到"全删了"之类的旧印象又动手删。**

**四处注入点（写法一律是两行 `xxx.levitationLength/Level = ...levitation_length/_level.get()`）：**
- `MutantShulkerServant.shootMutantShulkerProjectile()` —— **追踪/定点两个 if-else 分支各一份**，本体所有射击 goal（壳内/壳外、追踪/散射）最后都汇进这里，只改一支等于没改。
- `MutantShulkerServantTrap.baseTick()` 造弹处（`vanishAnimationTick == 1`）。
- `ShulkerBulletSpell`（玩家侧法术，读的是 MM 的 `MutantShulkerCommonConfig`）。

**真正的施加逻辑在 `MutantShulkerServantBullet.onHitEntity()`**：造成伤害且 `flag == true` 才 `addEffect(LEVITATION, levitationLength, levitationLevel)`，条件只剩 `!living.hasEffect(LEVITATION)` —— **2026-10-16 用户令「导弹击中不刷新浮空时长」**：原版 `MobEffectInstance.update()` 在增幅相同、新实例时长更长时会把 `duration` 重置回满，连发子弹就等于永久浮空；判定 `hasEffect` 直接跳过已浮空目标，比在 `update` 语义上做文章简单。**注意副作用：目标只会在落地后挨下一发时重新浮起（周期性飘一下），这是预期表现**。

**阈值键活着，现值 100（2026-10-16 用户令「阈值加回来 设为100」）**：`MobsConfig.MutantShulkerBulletLevitationMaxHealth`（本仓库自加，非上游）卡"目标最大生命 ≤ N 才给浮空"，落在 `config/goetyominous/goetyominous-mobs.toml` 的 `["Mutant Shulker Enhancements"]` 段。演变：默认 200 → 150 → 当天「删除阈值机制」连字段/builder/import 一起删净 → 同一天又要回来设 100。**删那一刻 Forge 会把该键从 toml 里抹掉，所以加回 100 后不会残留旧值**（实测 02:37 的 toml 里已无此键，下次启动按新默认写入）；反过来若只是改默认值而键还在 toml 里，Forge 不会覆盖既有值，改默认值等于不生效。

子弹另有 `levitationLength/levitationLevel` 字段与 NBT `LevitationLength`/`LevitationLevel`。

**键的归属：**`mutant_shulker_bullet_levitation_length/_level`（`com.alexander.mutantmore.config.MutantShulkerCommonConfig`）和 `trap_shulker_bullet_levitation_length/_level`（同包 `MutantShulkerRewardsCommonConfig`）**都在上游 MM jar 里，不在本仓库**，不要去"清理配置"。MM 自己那只敌对 `MutantShulker` 用 MM 的 `MutantShulkerBullet`，读同样这些键——**我们的改动管不到它**，本仓库这四处只影响仆从、陷阱仆从和玩家的聚晶。

相关：[[mm-mutant-shulker-anim-gate]]、[[mm-mutant-shulker-dyeing]]、[[mm-shulker-flight-removed]]
