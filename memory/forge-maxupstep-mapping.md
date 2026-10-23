---
name: forge-maxupstep-mapping
description: "Forge 1.20.1 上台阶该覆写 IForgeEntity#getStepHeight();maxUpStep() 已废弃且只是它的默认回退"
metadata:
  node_type: memory
  type: reference
  originSessionId: 350a396b-8c00-4851-935c-2825c92667b7
  modified: 2026-10-03T19:16:54.515Z
---

Forge 1.20.1 里"能爬多高台阶"的**正确覆写点是 `getStepHeight()`**(来自 `IForgeEntity`,Entity 已 implements)。`Entity.maxUpStep()` 带 `@Deprecated /** Forge: Use IForgeEntity#getStepHeight() */`,但**不是** `getMaxUpStep()` —— 用那个名字覆盖会报"方法不会覆盖或实现超类型的方法"。

证据(2026-10-03 实测,`build/fg_cache/.../forge-1.20.1-47.3.22_mapped_official_1.20.1-sources.jar`):
- `Entity.java:896` 真正的上台阶逻辑读的是 `float stepHeight = getStepHeight();`,判定 `vec32.y < (double)stepHeight`。
- `IForgeEntity.getStepHeight()` 默认实现 = `maxUpStep()` + `forge:step_height`(ForgeMod.STEP_HEIGHT_ADDITION)属性值,再 `Math.max(0, …)`。

两种写法都能生效:
- 覆写 `maxUpStep()` —— 走默认 `getStepHeight()` 间接读到,但拿不到属性的便宜。
- 覆写 `getStepHeight()` —— 更正规,**但会短路掉 `forge:step_height` 属性加成**;想两全就 `Math.max(自己的值, super.getStepHeight())`。

判据仍是 **> 1.0F 才能爬整格方块**(严格小于,1.0F 恰好不够,常用 1.25F)。

**实例(2026-10-03)**:Goety `Leapleaf.getStepHeight()` 返回**恰好 `1.0f`** → 潮涌之叶(bbWidth 1.9)在海底/陆地都卡 1 格台阶(见 [[goety-leapkelp-land-water-leap]] 第 16 条)。已覆写 `Math.max(1.25F, super.getStepHeight())`。顺带:`AmphibiousNodeEvaluator`/`WalkNodeEvaluator` 生成邻居用的是 `Mth.floor(Math.max(1.0F, stepHeight))`,所以 1.0→1.25 **不改变寻路**(仍是 1 格),只修移动侧那个边界。

**Why:** 移植生物时改爬坡高度极易用错方法名,而编译报错信息会误导成"名字写错了"。
**How to apply:** 新代码优先 `getStepHeight()`;看到老代码覆写 `maxUpStep()` 不要当 bug 改,但要意识到属性加成被绕过。相关:[[forge-entity-sized-vs-getdimensions]]。
