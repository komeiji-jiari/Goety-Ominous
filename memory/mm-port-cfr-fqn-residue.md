---
name: mm-port-cfr-fqn-residue
description: 照抄 CFR 反编译 MM 源码时，MM 自己类的全限定名残留会编译失败或串到 MM 的类，必须改回本包同名类
metadata: 
  node_type: memory
  type: project
  originSessionId: bce39cd2-57a2-4ba9-85aa-23e8f11f9523
  modified: 2026-10-15T18:48:40.702Z
---

从 MM 反编译源码抄进本仓库时，CFR 为了消歧会把**同名的 MM 类写成全限定名** `com.alexander.mutantmore.entities.X`。照抄的话，这个 FQN 指向的是 MM 自己的类，不是我们复制过来的 `common/entities/ally/mobs/mm/X`。

出现位置（2026-10-15 在 `AreaDamage.java` 里一次抓到 14 处）：
- 字段类型：`public List<com.alexander.mutantmore.entities.AreaDamage> connectedAreaDamages` → 编译错误「推论变量 E 具有不兼容的上限」，并**连带**所有给它赋值的 goal（`MutantHuskGeyserAttackGoal` / `MutantHuskMeleeAttackGoal` 的 `Lists.newArrayList(this.areaDamages.iterator())`）一起报错
- 局部变量声明与强转：`com.alexander.mutantmore.entities.AreaDamage areaDamage;` / `(com.alexander.mutantmore.entities.AreaDamage)var3.next()`
- **`defineId(X.class, ...)` 的类字面量**：`SynchedEntityData.defineId(com.alexander.mutantmore.entities.AreaDamage.class, ...)`（9 处）。这条**不报编译错**，但 id 会从 MM 那个类的计数器里分配，属于潜伏问题，同样要改回本类
- 同理会出现在 lambda 参数、`instanceof` 之后的模式匹配等

注意不是所有 `com.alexander.mutantmore.*` 都要改：`init.TagInit` / `init.EffectInit` / `init.MMDamageTypes` / `util.MiscUtils` / `config.*` 这些是**真要用 MM 的**，保留。

另一个易撞的：简名冲突。`import com.alexander.mutantmore.init.TagInit.Blocks;` 和 `import net.minecraft.world.level.block.Blocks;` 同时存在会直接编译失败（「single-type-import 已定义具有相同简名的类型」+ 一串「对 Blocks 的引用不明确」）。MM 原文是 `TagInit.Blocks.MUTANT_FROZEN_ZOMBIE_WALKS_THROUGH` 全限定，照抄 MM 才对；同仓库其它仆从只 import 了 `TagInit.Blocks` 走裸 `Blocks.X`，只有冰尸同时要原版 `Blocks.SNOW`。

**Why:** MM 是可选依赖但被我们直接 import，FQN 残留又不会立刻报错（defineId 那类），只有真的跑一次编译/运行才暴露。

**How to apply:** 抄完 MM 实体后，`grep -rn "com.alexander.mutantmore.entities" src/main/java/` 清零（`init`/`config`/`util`/`interfaces` 等包名除外）。另外**移植完必须跑一次 `./gradlew compileJava`**——2026-10-15 这次 build 第一次跑出 13 个错，说明 husk goal / SandBoulder 那几批移植当时从没编译过。另见 [[forge-compile-setup]]、[[mm-mutant-husk-servant-port-notes]]。
