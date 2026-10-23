---
name: goetyominous-test-instance-and-logs
description: 用户实测用 PCL 实例路径与日志位置——日志我可以直接读，不用让用户贴
metadata:
  node_type: memory
  type: reference
  originSessionId: 73989e8f-c7ca-4dcd-b54d-bb715dfd196a
  modified: 2026-10-10T14:41:34.662Z
---

用户用 PCL 启动实测。**实例会换，别照旧路径找**。

**判"现在在用哪个实例"要看进程，不要看日志**——latest.log 的 mtime 会骗人（2026-09-30 实测：日志最新的是 `1.20.1-Forge 测试`，但真正在跑的是 `渊海`）。正确姿势是 `powershell -NoProfile -Command "Get-CimInstance Win32_Process -Filter \"Name='java.exe'\" | Select-Object -ExpandProperty CommandLine"`，从命令行里找 `--gameDir "D:\beta\...\versions\<实例>"`。这条命令同时告诉你**游戏在不在跑**（决定能不能换 jar）。注意 bash 里输出是 GBK 乱码，中文实例名会显示成 `????`，靠 `D:\beta\Cat\` 还是 `D:\beta\.minecraft\versions\` 前缀区分。

已知实例：`D:\beta\.minecraft\versions\` 下有 `1.20.1-Forge 测试`，`D:\beta\Cat\` 下有 `渊海`/`巫法`/`亚猫`/`围墙`/`方匣`。`1.20.1-Forge 测试` 曾在 2026-09-28 前后消失过、后又出现，所以**别信"某目录已不存在"这种结论，每次实查**；同一实例的 jar 也可能比源码旧（用户拷 jar 的时点早于我最后一次 build），拷之前先比一下 `build/libs` 的字节数/md5。另外**先看 `mods\` 里有没有同 modid 的旧包**（2026-10-03 实测：`测试` 里新旧两份 goetyominous 并存，FML 按 jar 内版本号挑了旧的那份，新包静默不加载，导致"改了半天毫无变化"）——见 [[forge-duplicate-modid-jar-silent-pick]]。

`logs\latest.log` 只用来读现场，读之前还是先按进程确认是哪个实例——读错实例等于白读。

**拷 jar 别手打中文路径**:bash 传中文参给 `cp`/`md5sum` 会被 shell 中途弄坏(2026-09-30 实测出现 `'D:/beta/.minecraft/versions/1.20.1-Forge �'\213''�'\225''/mods/...'` → No such file or directory)。用**通配让文件系统自己出字节**:`cp D:/ark/java/build/libs/goetyominous-3.0.0-1.20.1.jar D:/beta/.minecraft/versions/*/mods/`,再 `md5sum D:/beta/.minecraft/versions/*/mods/goetyominous-3.0.0-1.20.1.jar` 核对。副作用是 glob 会**一次暴露所有匹配到的实例**——2026-09-30 就是这么发现 `versions/1.20.1-Forge 测试` 已被替换成 `versions/1.20.1-Forge 渊海`(mtime 14:34),而且 `渊海` 在 `D:\beta\.minecraft\versions\` 下有一份(之前记录里 `渊海` 只在 `D:\beta\Cat\`)。所以两个根目录都要 glob。

**换 jar 与启动的竞态（2026-10-04 实测，我闯的祸）**：`cp` 覆盖 jar 和玩家点启动撞在同一秒 → FML 扫描读到的 jar 是半截的 → `[BackgroundScanHandler/SCAN] An error occurred scanning file Mod File: ...\mods\goetyominous-3.0.0.fix-1.20.1.jar` + `cpw.mods.niofs.union.UnionFileSystem$NoSuchFileException: resourcepacks` → 紧接着 `Mixin prepare failed ... 'com.qiuyue.goetyominous.common.mixin.IllagerSpawnerMixin' was not found` → FATAL 崩在 early window，**根本没进主菜单**。这不是代码/打包问题：崩完 `unzip -t <实例里的jar>` 全是 OK、mixclass 也提得出来（`resourcepacks/` 是本包合法的顶层目录，别被那个报错名误导）。**判据**：报了 `UnionFileSystem$NoSuchFileException` + mixin not found ⇒ 先怀疑覆盖竞态，别去查 mixins.json。**How to apply**：build 完 **临 cp 之前**再查一次 `java.exe`（只在 build 前查不够——玩家可能正好在这 30 秒里点启动）；崩了就让玩家直接重启，罐子本身是好的。

**运行中热换 jar（2026-10-10 实测，又是我闯的祸）**：游戏 15:36 启动、我 15:38 覆盖 jar、15:40 崩。症状**不是**启动期 scan 报错，而是**跑着跑着某个类第一次被加载时** `java.lang.NoClassDefFoundError: com/qiuyue/goetyominous/common/effects/ModEffectParticles` → `Caused by: ClassNotFoundException` → 栈顶是 `cpw.mods.cl.ModuleClassLoader.loadClass` + 上面几帧 `cpw.mods.niofs.union.UnionFileSystem.byteChannel`，`Suspected Mods` 同时列出本包和另一个 mixin 方（这次是 RevelationFix 的 FrostLevelMixin，**无关，别顺着它查**）。**判据**：那个类 `unzip -l <实例jar>` 明明在（securejarhandler 按启动时读到的 jar 索引/摘要解析条目，文件被换掉后就再也找不到该类，且只在惰性加载时才炸）⇒ 直接让玩家重启，代码和罐子都没问题。**How to apply**：cp 前的检查要能区分游戏和 Gradle 守护进程（`./gradlew` 也会留下 java.exe）——`Get-CimInstance Win32_Process -Filter "Name like 'java%'"` 取 CommandLine，含 `MojangTricks...minecraft.exe.heapdump` / `--gameDir` 的才是游戏，含 `GradleDaemon` 的忽略；**数到游戏在跑就停手，别 cp**（我 2026-10-10 就是查到了 1 个进程还是照 cp）。

**How to apply:** 需要现场数据时，`./gradlew build` 后把 `D:\ark\java\build\libs\goetyominous-3.0.0-1.20.1.jar` 拷进该实例的 `mods\`（先确认没在跑：`powershell -NoProfile -Command "Get-CimInstance Win32_Process -Filter \"Name='java.exe'\""` 看命令行是不是 PCL/minecraft——运行中热换 jar 会 CNFE），然后**我自己用 Read/grep 读该实例的 `logs\latest.log`**——不要叫用户复制粘贴日志。注意该 log 是 GBK+中文日期前缀，grep 关键词用英文标签（例如自己埋的 `[ShakeDbg]`）。仓库里 `run\logs\latest.log` 是旧 dev 日志，不是实测日志。相关：[[forge-compile-setup]]
