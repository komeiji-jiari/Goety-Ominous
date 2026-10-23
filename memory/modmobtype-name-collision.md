---
name: modmobtype-name-collision
description: 两个 ModMobType 同名不同包——Goety 的(NATURAL/NETHER/FROST) vs 本仓库的(只有 FEL)；MobType 是引用相等，常量绝不能跨 jar 复制
metadata: 
  node_type: memory
  type: reference
  originSessionId: d32db0f4-b36c-414f-bc45-874cd71b4a4c
  modified: 2026-10-07T09:40:44.221Z
---

`ModMobType` 在本工程有两个，**同名不同包**：

| 全限定名 | 内容 | 谁在用 |
|---|---|---|
| `com.Polarice3.Goety.init.ModMobType` | Goety 自带：NATURAL / NETHER / FROST | ac 的恐龙类（Atlatitan/Corrodent/Grottoceratops/Relicheirus/Tremorsaurus/Tremorzilla/Vallumraptor/Vesper）、am 等 |
| `com.qiuyue.goetyominous.utils.ModMobType` | **只有一个 `FEL = new MobType()`** | ac 的 7 个糖果怪（Candicorn/Caniac/CaramelCube/Gumbeeper/GummyBear/GumWorm/Licowitch）+ `CroneRobeEvents:88` + `HealTypeMixin:19` + `SummonedFelCrownMixin` |

本仓库那个存在的唯一目的，就是给 Goety 的 MobType 家族补一个它没有的 `FEL`。**AC 目录里两种 import 并存**（同目录下 7 个用 ominous 的、8 个用 Goety 的）。

**Why:** 拆分/搬运用 `sed` 批量替换 `ModMobType.` 或按类名匹配会误伤另一半，把用 Goety NATURAL 的恐龙类一起改坏。跟 [[annihilation-bomb-ac-lm-name-collision]] 是同一类坑。

**How to apply:** 替换必须带**完整包名 + `\b` 限定**，只动 `com.qiuyue.goetyominous.utils.ModMobType`。另外 `utils/ModMobType` 是**全局契约**，拆分后必须留 core、**绝不能复制进各 jar**——`MobType` 没覆写 `equals`，判定走**引用相等**（`HealTypeMixin:19`、`CroneRobeEvents:88` 都是 `==`）。两个 jar 各 `new MobType()` 得到不同对象，`==` 恒假 → FEL 仆从的治疗与巫婆长袍判定**静默失效**（不报错，只是"感觉没生效"）。真要模块脱离主 mod，只能抽 `ominous-api` jar 而非复制。相关：[[goetyominous-split-into-modules]]。
