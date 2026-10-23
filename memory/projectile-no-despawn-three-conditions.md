---
name: projectile-no-despawn-three-conditions
description: "判\"弹射物会不会永远飘\"看三要素(无阻力/无重力/无寿命);本工程只有风弹三者齐缺,已限 40 格"
metadata: 
  node_type: memory
  type: project
  originSessionId: cce8e1f4-3808-498d-a62a-0431192b4cb8
  modified: 2026-10-04T20:53:07.004Z
---

判一个弹射物会不会变成"永远不消失的幽灵弹"，必须看三个条件是否**同时**成立：无阻力（`getInertia() == 1.0F`）、无重力、无寿命/距离上限。只数"有没有 `discard()`"会误判 —— 原版 `AbstractHurtingProjectile.tick()` 根本不施加重力，而 Goety `SpellThrowableProjectile.tick()` 有 0.03 重力 + 0.99 阻力，后者即使没有上限也会画抛物线落地（约十几到二十几格）。

2026-10-04 全工程审计（`common/entities/` 下全部弹射物）结论：**只有 `AbstractWindCharge` 三者齐缺**（`getInertia()` 返回 1.0F、无重力、原本只有 `getBlockY() > maxBuildHeight + 30` 一条兜底），已在基类加 `MAX_DISTANCE = 40.0D` / `MAX_LIFE = 60` + `traveled` 字段（存 NBT），三个使用者一起生效：`BreezeWindCharge`、`ServantWindCharge`、`WindChargeItem`（玩家风弹也是 `ServantWindCharge`，初速 1.5 格/tick，是 AI 0.7 的两倍多）。

LM 的 `AnnihilationBomb`/`ThrownKnight`/`EntityThrown`/`CloudEntity`/`SmallAnnihilationBomb` 没有寿命上限但有重力，判为不构成威胁、未动（它们 `tick()` 里的 `tickCount = 0` 在 `handleEntityEvent`，不是每 tick 清零，别当成"计数器不可用"）。`BigShulkerBullet`/`ThrownPhantomDagger` 无阻力无重力但有上限，安全。剩余边界项：`MutantShulkerServantBullet`（`noPhysics = true` 能穿方块 + 无寿命检查）。

**Why:** 复查"哪些弹射物会飘"时不必重跑全量审计，也不会再把"有重力"的误报成幽灵弹。
**How to apply:** 新增弹射物按三要素自查；`HurricanePunch`（`MAX_DISTANCE = 12.0D` / `MAX_LIFE = 40`，超限 `burst(); discard();`）是仓库现成的限距模板，含 `Traveled` 存 NBT 的写法。相关：[[goety-projectile-base-choice]]
