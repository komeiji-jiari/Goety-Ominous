---
name: goety-servant-limit-two-channels
description: 仆从召唤上限有两条互不相干的通道：实体 getSummonLimit（仪式/蛋方块）与法术 summonLimit()（法术自己数）；接线必须两边都查
metadata: 
  node_type: memory
  type: reference
  originSessionId: 09280b4a-dac6-4917-8543-dcf7e90fabc7
  modified: 2026-10-05T11:34:52.030Z
---

Goety 的"召唤上限"不是一个方法，是**两条独立通道**，各自数各自的：

1. **实体侧** `IOwned.getSummonLimit(LivingEntity owner)` —— 默认实现 `return 64;`（在 `IOwned.java`）。
   唯一消费点是 `RitualRequirements.canSummon(Level, Player, EntityType)`：遍历所有维度数你名下 `owned.summonPredicate().test(entity) && getTrueOwner() == player` 的实体，`count >= limit` 就弹 `info.goety.summon.limit` 并拒。
   上游调用者：`SummonRitual` / `ConvertRitual` / `DarkAltarBlockEntity`（即黑祭坛召唤与转化仪式）、`TransferScroll`、`SoulJar` 那批复活道具、以及本工程的 `*ServantEggBlock`（放蛋方块）。

2. **法术侧** `SummonSpell.summonLimit()` + `summonPredicate()` —— 默认 `64` / `instanceof IOwned`。
   在 `SummonSpell.conditionsMet(ServerLevel, LivingEntity)` 里自己遍历 `worldIn.getEntities()` 数一遍，非潜行时超限就拒。
   注意签名：`summonLimit()` **无参**，拿不到法杖；要在里面做依赖法杖的判断，得覆写 `conditionsMet` 存个字段，再让 `summonLimit()` 读它（`conditionsMet` 先跑、且 `summonLimit()` 只被它调用，所以不会读到陈旧值）。从 caster 反查法杖用 `WandUtil.findWand(caster)`（主手→副手，`instanceof IWand` 才算），配 `this.rightStaff(...)` 与 `SpellResult` 里拿到的 `staff` 参数等价。

**`summonPredicate()` 有两个消费点，不是一个**：`SummonSpell.conditionsMet`（数名额）和 `SummonSpell.commonResult`（潜行右键召回时遍历该谓词 `teleportServants` 拉人）。所以想让"同一只怪的不同变体各自数名额"（如长尾鲨 3 大 / 10 普通）而把谓词按种类切开时，**必须同时覆写 `commonResult(ServerLevel, LivingEntity)`** 把召回改回合并谓词，否则带上晋升装备后潜行召回只拉走一种、另一批留在原地。另注意 `GreatThrasherServant extends ThrasherServant`，普通那支谓词要写成 `instanceof ThrasherServant && !(instanceof GreatThrasherServant)`。

**所以接线一个 `*ServantLimit` 配置键时，要先查这只怪走哪条路**：有召唤法术的（`common/magic/spells/**`，`extends SummonSpell`）通常要在法术里覆写 `summonLimit()`；能被黑祭坛仪式/转化仪式召唤的要在实体里覆写 `getSummonLimit`；两条都活的就都接。本工程 `DropBearServant` + `DropBearSpell` 是两边都接的样板。

只从刷怪蛋（`ServantSpawnEggItem`）获得的怪**两条都不走**——刷怪蛋直接 spawn，不过任何 limit 检查。所以给它接线是"接上了也够不着"，只能等以后补仪式/法术。

相关：[[mobsconfig-unwired-keys-are-not-dead]]、[[goety-summonspawn-owner-order]]
