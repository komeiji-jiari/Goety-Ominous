---
name: mm-servants-no-terrain-placement
description: "仆从版突变霜冻僵尸仍不铺雪;突变尸壳的四处 quicksand 放置点 2026-10-16 已全部加回(用户当初删是因为不知道流沙会自然消失)——雪与流沙现在是两个不同的结论,别互相推"
metadata: 
  node_type: memory
  type: project
  modified: 2026-10-15T23:21:09.320Z
  originSessionId: 2de83047-6979-4f19-b5d1-4021d6d8c3a8
---

**沿革:** 2026-10-15 用户令「仆从版突变霜冻僵尸和突变尸壳不要在地上生成雪和 quicksand 方块」,五处放置点全删(直接删,不加自己的配置开关)。**2026-10-16 用户反馈:当初删流沙是因为不知道 quicksand 会自然消失,要求把突变尸壳技能里的流沙加回来** —— 于是四处**流沙**全部恢复,**铺雪仍然是删的**。

**已恢复(2026-10-16,逐行照抄删除前的版本 —— 删除前的代码还在 git index 里,`git show :<path>` 就能取):**

1. `MutantHuskServant.aiStep()` — roar 期间按 `roar_quicksand_range` 撒流沙,条件 `!isClientSide && roarAnimationTick <= roarAnimationActionPoint && roarAnimationTick > roarAnimationLength - 72`,`roar_attack_griefing` && `!quicksand_griefing_off`,逐格 `random.nextInt(200) == 0`
2. `goals/MutantHuskServant/MutantHuskPunchAttackGoal.tick()` — 重拳落点按 `slamAttackBoundingBox` 撒流沙(无概率,空气且 `survivable` 就放),门控 `punch_attack_griefing` && `!quicksand_griefing_off`
3. `AreaDamage.tick()` 的 `getActiveType() == 2`(间歇泉)分支 — 盒子 inflate(2.0),`random.nextInt(40) == 0`,门控 `geyser_attack_griefing` && `!quicksand_griefing_off`
4. `SandBoulderServant` — `public boolean griefing = true;` 字段 + `Griefing` NBT 读写 + `tick()` 里 `if (this.griefing)` 的滚动撒沙(`random.nextBoolean()` 半概率,盒子 inflate(0.2));投放端 `MutantHuskThrowSandAttackGoal` 里 `boulder.griefing = sand_boulder_griefing && !quicksand_griefing_off` 也一并恢复

**唯一有意偏离删除前代码的一处:** AreaDamage 里原本写的是 `this.owner instanceof MutantHusk`(MM 的实体),而我们的间歇泉传的 owner 是 `this.mob`(`MutantHuskServant`)→ 原样贴回来**永远不成立、等于死码**,所以改成了 `instanceof MutantHuskServant`。同理 [[mm-mutant-husk-servant-port-notes]] 记的沙球 canHarm/canHit 那两处也是这么改的。

**仍然删着 / 没跟着回来:**
- **霜冻僵尸的铺雪**(雪暴期间铺 `Blocks.SNOW` / 叠 `SnowLayerBlock.LAYERS`)—— 用户这次只交流沙,别顺手加回。雪暴机制本身、`canSnowSurvive` 缺失、雪暴期换跟随移速那套都照旧。
- `MutantHuskServant.cantBreakWithHurtGriefing()`(返回 `BlockInit.QUICKSAND` 的"不破坏"白名单)—— 删除前它在 index 里,但 2026-10-16 复查时**全仓库零引用**(本仓库 MM 仆从没有任何 walk/hurt griefing 破坏方块的代码),恢复流沙时没一起加回。要加是另一件事,先问。
- 各攻击的伤害/击退/沙子粒子、另一套方块破坏(walk_griefing/hurt_griefing)照旧不动。

**验证手段:** `grep -rn "QuicksandBlock.survivable" src/main/java/.../mm/` 应有 4 处(husk aiStep / PunchGoal / AreaDamage / SandBoulder);`grep -rn "quicksand_griefing_off"` 应有 4 处(同上);`grep -rn "setBlockAndUpdate" .../mm/` 现在应只剩 MutantBlazeRod/RodlingFireball 的放火 2 处 + 这 4 处。

相关:[[mm-mutant-husk-servant-port-notes]]、[[goetyominous-deadcode-cleanup-preference]]、[[mm-zombie-servant-death-drop-hook]]
