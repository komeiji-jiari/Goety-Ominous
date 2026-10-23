---
name: mm-mutant-husk-servant-port-notes
description: MM Mutant Husk 移植成 Goety 仆从的定案(不做小海龟索敌/不做 HuskHammer;roar 改召唤自己人、SandBoulder 整套搬)+ 两处 MM 代码写死 instanceof MutantHusk 的暗坑
metadata:
  node_type: memory
  type: project
  originSessionId: bce39cd2-57a2-4ba9-85aa-23e8f11f9523
  modified: 2026-10-15T18:15:47.501Z
---

2026-10-15 用户对 `mutantmore:mutant_husk` 移植成 Goety 仆从的定案。**移植已于同日完成**(代码 + 注册 + 客户端渲染 + 中英文案全部落地,尚未构建实测)。

**用户拍板的四项:**

- **不做小海龟索敌** — 照 MJZ 的先例砍掉那段 `AllDirectionsTarget<Turtle>` + `Turtle.BABY_ON_LAND_SELECTOR`
- **不做 HuskHammer 玩家道具** — 同上,MM 仆从一律不配玩家侧配套装备(见 [[mm-mutant-jungle-zombie-arm-port]])
- **roar 召唤改造成"召唤你自己的仆从"** — 不照抄 MM 那样刷敌对原版僵尸;`ZombieResurrectionServant` 生成出来的实体一律 `setTrueOwner(this.owner)` + `setLimitedLife(getSummonLifespan)`。owner 传的是 **husk 仆从自己**(不是主人的 `getTrueOwner()`),靠 `Owned.remove()` 自动把子实体重新挂给 husk 的真主人,先例是 `DiscipleServant`
- **SandBoulder 整套移植进来** — 和 `AreaDamage` 同款做法,MM 类型锁死自己类的件就复制进本仓库。沙球对友军的判定另加了开头一道 `MobUtil.areAllies(owner, target)` 守卫

**两处 MM 代码写死 `instanceof MutantHusk`,已一起改掉:**

1. `common/entities/ally/mobs/mm/AreaDamage.java` 约 248 行 → `MutantHuskServant`。不改的话 `geyser_attack_griefing` 那段破坏方块**静默永不触发**。
2. `ZombieResurrectionServant` 里 `((MutantHusk)this.owner).resurrectedMobs.add(entity)` → `instanceof MutantHuskServant husk`。不改的话我们仆从的 `resurrectedMobs` **永远是空表**,而 roar goal 恰好要求该表为空才放行 → roar 冷却判定失效。

**已确认的限制(不是本次引入的):** `MobUtil.areAllies(仆从A, 仆从B)` 恒为 false——1.20.1 的 `isAlliedTo` 只看队伍,`SEHelper` 只桥接 玩家↔仆从。所以沙球/`AreaDamage` 挡得住主人玩家,挡不住同伴仆从。修这个要另开一路判定,当时没做。

**可复用面备注:** `MutantHuskModel` 形参写死 `<T extends MutantHusk>`、`MutantHuskKeyframeAnimations` 里 `MUTANT_HUSK_DOWN/GEYSER/NOVELTY(entity)` 也写死 MM 实体类型,和 MJZ 一样在自己包里重写;**不含实体类型的静态 `AnimationDefinition`(如 `ZombieResurrectionAnimations.ZOMBIE_RESURRECTION`)直接 import MM,不复制**。`MutantHuskAdvancedAnimations`(330KB)在 jar 里零引用,是死类,别搬。贴图按 `mutant_husk + entity.getSandType() + ".png"` 拼,`sandType` 由 `MiscUtils.sandStringForPos` 按脚下方块是否在 `RED_SAND` tag 里给 `""` 或 `"_red_sand"`——直接引 MM 资源包贴图,不复制。

相关:[[mm-jungle-zombie-servant-port-decisions]]、[[goety-multilife-servant-dismiss-conflict]]、[[servant-spawn-egg-item-model]]
