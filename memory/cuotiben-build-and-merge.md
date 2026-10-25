---
name: cuotiben-build-and-merge
description: 徒弟错题本·编译与构建篇：增量编译假成功、clean 砸 fg_cache、git 自动合并的两个静默坑（两边都加 / 一边删一边没动）
metadata: 
  node_type: memory
  type: project
  originSessionId: 304245bf-c7a1-4c6f-8c54-9cc55d2f31af
---

来源：徒弟的「错题本.md」（第 74e2f8ab/86fa49ee 版，758 行；该文件已被 `66998d1f` 从仓库移除，需要时用 `git show 86fa49ee:错题本.md` 取）。这是**编译与构建**那一节的浓缩。

## 合并后必须全量编译 —— 两个"假成功"陷阱

**第 19 条：两边各自加了同一个符号 → 文本不冲突，语义冲突。**
两个分支**各自独立**往同一个类的不同位置追加同名成员（都在自己那份加了 `VOLT_FOCUS`）。git 按文本块比，两处离得远就认为互不干扰，**两份都留下**。编译才报「已定义」。
- 查符号所有位置：`[System.IO.File]::ReadAllLines($路径)` 逐行 `-match '符号名'`
- ⚠️ **只删声明不够**：两边若各写了一整套赋值/注册（如两个 `BUILDER.push("Volt")`），必须**成套成对删**，否则配置里冒出两个同名段落。确认哪套在用，**顺着调用方反查**（谁 `.get()` 了它、谁 new 了这个类）。

**第 22 条：一边删了、一边没动 → 静默删掉你依赖的方法。** 是 19 的镜像，更阴：19 报「已定义」，22 报「找不到符号」，而**报错文件根本不在冲突列表里**，`git diff --stat HEAD -- <该文件>` 是**空的**。
- 实例：`PossessedPaladinServant extends IAnimatedBossServant extends IAnimatedMonsterServant`，协作者在**爷爷类**里把 4 个 LM 工具方法当死代码删了（434→353 行），他那边没圣骑、在他眼里没人调用；我们没碰过那文件，git 直接采用删掉那版，**一个冲突都不报**。
- 反查顺序：① 确认报错文件本身没被合并动过 ② 看它 `extends` 谁 ③ 父类三方逐个 grep 缺失符号（`:1`=共同祖先 `:2`=我们 `:3`=对方）④ `git log -S '方法名' --oneline -- <路径>`。**口诀：哪一方的行数变了、符号没了，就是那一方删的。**
- **修的时候放进自己的文件**（把 4 个方法搬进 `IAnimatedBossServant`——协作者没这文件），放回共享文件等于跟协作者对着干，下次合并**同样静默地**再删一次。
- 搬的时候**方法体逐字照抄，别顺手优化**。合并修复阶段只搬运不重构，否则出问题分不清是谁的锅。

## 编译环境

- **第 1 条**：动过 `build.gradle` 依赖版本就**别信增量编译的 BUILD SUCCESSFUL**，某个源文件没被编辑过就沿用旧 class，不拿新依赖重新校验。依赖方 public→private 编译期一声不吭，运行时才炸 `IllegalAccessError`。
- **第 20 条**：`gradlew clean` 会删 `build/fg_cache`（ForgeGradle 打的补丁 Forge 核心 jar）→ 所有 Forge/MC 类找不到 → **2762 个假错误**。**认特征：错误数突然上千且大量是 `警告: 未知的枚举常量 Dist.CLIENT` 这类 Forge 自己的东西找不到**，别去读那些错误。**真正的根因在日志第 14 行**（`Failed to delete existing output file`），别只看结尾。要全量重编时**别用 clean**，手工删 `build\classes` + `build\tmp\compileJava`（先 `gradlew --stop` 停守护进程）。
- **第 2 条**：编译日志别写进 `build/`，会锁死 `clean`（自己锁自己要删的文件）。写 `$env:TEMP`。
- **第 3 条**：别把发布版 mod jar 丢进 `run/mods`，userdev 和 production 是两种东西，要用 `fg.deobf(...)` 声明依赖。
- **第 24 条**：**别凭记忆写原版 API**。1.20.1 的 `PlayerAdvancements` **只有** `getOrStartProgress(Advancement)`，`getProgress` / `isDone(Advancement)` 都是后来版本才加的。冒出「这方法应该存在啊」的念头就**别猜第三次**，直接翻官方映射源码 jar（见 [[cuotiben-tooling]] 第 29 条的路径）。⚠️ 那个 jar 里**缩进是 3 个空格**，用 `^    public` 搜会一个都匹配不到，误以为文件是空的。

- **第 26 条（2026-10-13 实测）：改 `build.gradle` 的依赖版本后"刷新一下环境就炸"**（症状与第 20 条一样：IDE 一片 `Cannot resolve symbol 'minecraft'`）。根因：新版本**不在缓存里** ⇒ gradle 必须**联网下载 + deobf**（Goety 那包 93 MB，实测要 1 分半）⇒ 若 IDE 那次 sync 超时、或 IntelliJ 开着 **Gradle Offline Mode**（IDE 级设置，项目文件里看不到）⇒ 模型构建失败 ⇒ 全红。**修法：命令行跑一次不带 `--offline` 的 `./gradlew compileJava`**（把新产物下进 `…\deobf_dependencies\curse\maven\goety-586095\<fileid>_mapped_official_1.20.1\`），之后 IDE 里 Reload Gradle Project 就秒过（缓存已就绪）。⚠️ 顺带：**`.disasm/goety_src` 不会跟着升级**，读前置 API 前先看 [[goety2-src-decompile]] 里的版本提示。
- **第 25 条（2026-10 实测）：产物 jar 莫名缩水 = 残缺的增量编译，不是丢了源码。** 症状：新 build 出来的 jar 比上一版小了 1MB+。排查法：`unzip -l 新旧jar` 对比条目 →「只在旧 jar」的类清一色某几个包（这次是 client 渲染/粒子 + 联动实体 347 个）→ `find build -name "<缺失类>.class"` **查无此物**，但源码在、git 干净、build.gradle 无排除 → **compileJava 被判定 up-to-date/只编了一部分，build/classes 本就是残缺的，jar 只是忠实打包了残缺产物**。修复：删 `build/classes/java/main` 后全量重编（**别 clean**，同第 20 条；执行后 13→127 个类立刻回归）。**量化印证**：class 未压缩体量 ≈ jar 差值的 **2.2 倍**（未压缩 2.59MB ↔ 压缩后 1.19MB）——以后 jar 缩水时 ×2.2 反查像不像丢了一批 class。另：`build/libs/*.jar` 与 `build/reobfJar/output.jar` 内容一致（发布用后者，已重混淆）。

相关：[[cuotiben-goety-servant-framework]]、[[cuotiben-lm-port]]、[[cuotiben-tooling]]、[[stash-audit-2026-09-26]]
