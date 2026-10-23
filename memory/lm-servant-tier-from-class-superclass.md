---
name: lm-servant-tier-from-class-superclass
description: "移植 LM 实体时选哪个仆从基类,要看原版 .class 的真 super_class,不能靠常量池 grep/凭印象;附解析脚本与已核对的层级表"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 6a57ba39-21f0-4b79-99c1-e80b3d1212e3
  modified: 2026-09-26T18:16:22.057Z
---

移植 LM（传奇怪物）实体成 goetyominous 仆从时，**基类必须照抄 LM 原版的层级**：LM 实体 `extends IAnimatedBoss` ⇒ 仆从 `extends IAnimatedBossServant`；`extends IAnimatedMiniBoss` ⇒ `IAnimatedMiniBossServant`；`extends IAnimatedMob` ⇒ `IAnimatedMobServant`；`extends IAnimatedMonster` ⇒ `IAnimatedMonsterServant`。

**两条判定方法上的坑（都栽过）：**

1. **对 `.class` 做 `grep -a` 看常量池字符串判断父类是错的。** 常量池里出现 `IAnimatedBoss` 只说明这个类**引用**了它（字段类型/参数/catch），不代表继承它。实际反例：`FlamebornGuardEntity` 常量池里有 `IAnimatedBoss`，真父类是 `AbstractFlameborn`。
2. **别凭仓库现状反推"哪层是正统"。** 我曾在 `src/` 里没搜到 `IAnimatedBossServant` 就断言"仓库没有这层、新建等于凭空发明"——文件其实一直在（`common/entities/ally/lm/IAnimatedBossServant.java`，2026-09-25 为圣骑建的）。搜索没搜到 ≠ 不存在，下结论前先 `git log`/全仓再确认一遍。

**正确做法：解 `.class` 的 `super_class`。** 脚本 `.scratch/_superclass.mjs`：

```
node _superclass.mjs <装着 .class 的目录> [要剥掉的文件名前缀]
```

核心是读 magic 后跳过常量池（tag 5/6 是 long/double 占两个槽、tag 1 的 Utf8 要读 length），再过 `access_flags`、`this_class` 拿到 `super_class` 索引；**注意 `super_class` 指向 CONSTANT_Class，它的 `name_index` 才指向 Utf8，要两步解**。另有一个坑：直接读 `super_class` 前只 `+=2` 会落在 `this_class` 上，必须 `+=2`（access_flags）再 `+=2`（this_class）。

用例：`unzip -o -p _jars/lm.jar "<类路径>" > x.class` 拿到单个类，或整目录解出来跑脚本。

**已核对的 LM 层级表（2026-09-26）：**

| LM 实体 | 真父类 | 仓库对应基类 |
|---|---|---|
| `TheObliteratorEntity` | `IAnimatedBoss` | `IAnimatedBossServant` |
| `Cloud_GolemEntity` | `IAnimatedBoss` | `IAnimatedBossServant` ✓（2026-09-26 才从 `IAnimatedMiniBossServant` 改过来；此前是历史遗留，比 `IAnimatedBossServant` 早一天提交。改层的代价：错层期间少了连击适应减伤、少了溺水/冰冻免疫） |
| `PossessedPaladinEntity` | `IAnimatedBoss` | `IAnimatedBossServant` ✓ |
| `AnnihilationPursuerEntity` | `IAnimatedMiniBoss` | `IAnimatedMiniBossServant` ✓ |
| `Overgrown_colossusEntity` | `IAnimatedMiniBoss` | ✓ |
| `Shulker_MimicEntity` | `IAnimatedMiniBoss` | ✓ |
| `HoveringHurricaneEntity` | `IAnimatedMonster` | `IAnimatedMonsterServant` ✓ |
| `AbstractFlameborn`（Guard/Warrior/Drifter/WanderingEye 都继承它） | `IAnimatedMob` | `IAnimatedMobServant` ✓ |

仓库侧的继承链：`IAnimatedMiniBossServant` → `IAnimatedMobServant` → `IAnimatedMonsterServant` → `Summoned`；而 `IAnimatedBossServant` → `IAnimatedMonsterServant`（**跳过 IAnimatedMob**）。也就是说 **`IAnimatedBoss` 和 `IAnimatedMiniBoss` 在 LM 里是兄弟分支不是祖孙**，仓库里也保持了这个形状。

**Why:** 层级选错，`damageCap()` / 动态减伤 / 免疫 / 动画派发这些挂在中间层的东西会整批丢失或整批白拿，而且要等编译或实测才发现。
**How to apply:** 每次移植新 LM 实体，先跑一遍 `_superclass.mjs` 定层，再动手写类头。相关 [[the-obliterator-servant-port-plan]]、[[lm-servant-boss-mechanic-stripping]]、[[goetyominous-deadcode-analysis-scripts]]。
