---
name: intellij-red-font-troubleshooting
description: 文件在 IntelliJ 里整篇红字但 gradle 能编译时的排查方法与结论
metadata: 
  node_type: memory
  type: reference
  originSessionId: dfecafc6-f89b-466c-86cb-07691ffe12e4
  modified: 2026-09-29T20:58:22.393Z
---

**先分清是"树节点红"还是"内容红"**(2026-09-01 查实)。文件名字变红 ≠ 文件内容红字:

- **目录树节点红 + 内容正常 + gradle 能编译** = VCS 状态着色,该文件是 `git status` 里的 `??`(untracked)。已 add 的是绿(`A`),改过的是蓝(`M`)。悬停无提示,Invalidate Caches 当然无效(曾在 [[goetyominous-mod-folder-isolation]] 新增文件时被误判成下面的桩缓存问题,白查一轮)。修法:`git add <file>`,或关 Settings → Version Control 的 "Highlight VCS file status"。判别:同一批新建文件里只有 `??` 的那个红。
- **内容整篇红**才走下面的流程。

排查「IntelliJ 文件红字但 gradle compileJava 通过」的流程(2026-09-01 用于 CorrodentServantTailEntity.java):

**先别假设是 SRG/反混淆 jar 错配**。IntelliJ 当前项目模型可以从磁盘缓存直接读:
- `%LOCALAPPDATA%/JetBrains/IntelliJIdea<版本>/projects/<projectHash>/project-model-cache/cache.data` —— 用 `grep -ao` 搜 `alexs-caves`/`mapped_official` 能确认模块实际解析到哪个 jar。本项目(Gradle external storage)此处显示的就是 `deobf_dependencies/curse/maven/alexs-caves-924854/5848216_mapped_official_1.20.1/...jar`(mojmap 反混淆),**没有** modules-2 里的 raw SRG jar。
- `caches/names.dat`(全局 VFS 索引,二进制)用 `grep -ao` 搜 `jar://` 能看到 IntelliJ 把所有相关 jar 都索引了。

**结论**:类路径/模型正确、javac 通过、无编译日志错误时,红字是 IntelliJ 的陈旧分析/桩缓存(尤其对 deobf 的 mod jar)。同步、重开文件、重启 IDE(普通)都不清;唯一可靠修复是 **File → Invalidate Caches and Restart**。或对单个文件做一次真实内容改动强制该文件立即重分析。

**但先排查「依赖版本掉队」**(2026-09-24,用户报「基本都报错 比如无法解析 MobType」时就是这一类)。三条命令定位:
1. 模型里解析到的 jar 版本 —— `grep -ao "[a-zA-Z0-9_.:+-]\{3,80\}_mapped_official_1\.20\.1\.jar" "<cache.data>" | sort -u`,路径见上。**逐条和 build.gradle 的 `curse.maven:` 版本号对**(本项目 9/24 那次:模型 goety=8736784/goetyawaken=8724855,build.gradle 已是 8951387/8880533,差 3 周)。
2. 上次同步时间 —— `%APPDATA%/JetBrains/IntelliJIdea<版本>/options/actionSummary.xml` 里 `ExternalSystem.RefreshAllProjects` / `InvalidateCaches` 的 `l=` 是 epoch ms(9/24 那次最后一次同步停在 9/1)。`.idea/gradle.xml` 的 mtime 是更粗的旁证。
3. 差异是否打到代码 —— 比两个版本 jar 的类表:`comm -13 <(unzip -l 旧jar|awk '{print $4}'|grep '\.class$'|sort -u) <(新jar 同)`;再把仓库 import 转成 `a/b/C.class` 取交集(grad 过而 IDE 红的就是这几个)。

**注意 workspace.xml 里 `autoReloadType=SELECTIVE`**,即改 build.gradle 不会自动重载 → 每次改依赖版本后必须手动 Reload Gradle Project(9/24 那次的红字就是这么来的,先 Reload,不行再 Invalidate)。

另:`.idea/misc.xml` 的 `project-jdk-name` 可能指向已删除的 SDK(本项目写着 `temurin-17`,而 `options/jdk.table.xml` 里只有 `ms-17`/`21`)。

反混淆 jar 里类可用 `javap_run.sh`(`D:/ark/java/.disasm/tmp/javap_run.sh`,内含 `/d/ark/tools/jdk17/bin/javap.exe`)验证是否完整 mojmap:ACMultipartEntity 应含 `getAddEntityPacket()` 且实现 PartEntity 全部抽象方法,无 `m_5654_` 残留。相关:[[ac-compat-porting-notes]]
