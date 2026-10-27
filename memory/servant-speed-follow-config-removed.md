---
name: servant-speed-follow-config-removed
description: 仆从 MOVEMENT_SPEED/FOLLOW_RANGE/FLYING_SPEED/KNOCKBACK_RESISTANCE/ATTACK_KNOCKBACK 配置已按用户要求全部删除，属性硬编码为原默认值
metadata: 
  node_type: memory
  type: feedback
  originSessionId: df7594ad-41f6-4424-9529-697887305b47
  modified: 2026-10-25T23:57:07.773Z
---

2026-10-25 用户要求「删除所有调整仆从移动速度和跟随范围的配置，不需要这个」，随后又要求「删除所有调整击退抗性的配置」，再要求「攻击击退的配置也删了」，最后明确「敌对怪配置也删了这些吧」。已删除 9 个配置类里全部 191 个 `*MovementSpeed` / `*FollowRange` / `*ServantFlyingSpeed` 键（含 `AttributesConfig` 里敌对/仆从共用与纯敌对的 Mired、Dreden、Sunken/Storm Necromancer、Returned、ArchGeomancer 等）、70 个 `*KnockbackResistance` 键、26 个 `*AttackKnockback` 键，引用处一律替换成原 `.defineInRange` 默认值。审计中曾恢复过 `ReturnedFollowRange` / `ArchGeomancerMovementSpeed`，用户随后要求一并删除，已再次移除。

**Why:** 用户不想要这类可调开关；这些值调了容易和 Goety 的修正值通道打架（见 [[goety-move-speed-two-channels]]）。

**How to apply:** 新移植任何生物（仆从、敌对、中立都算）时**不要再加**移速 / 跟随范围 / 飞行速度 / 击退抗性 / 攻击击退配置项，直接在 `bakeAttributes()` / `setCustomAttributes()` 里写数值常量（取上游原值）；已有的这类键也照此删。仍保留速度类配置只有武器攻速与两个招式速度（`hullbreakerServantGlowChaseSpeed`、`deepOneKnightServantOrtholanceDashSpeed`），别顺手一起删。配置分节仍按 [[config-goety-style-naming]]。
