---
name: forge-duplicate-modid-jar-silent-pick
description: "mods 里两份同 modid 的 jar → FML 按 mods.toml 里的版本号挑一份,另一份静默不加载;改代码没效果先查这个"
metadata: 
  node_type: memory
  type: project
  originSessionId: 0672aaf0-77b6-46d7-81bf-27f28135f10d
  modified: 2026-10-03T17:08:09.620Z
---

一个实例的 `mods\` 里放**两个同 modid、不同文件名的 jar** 时,Forge/FML 不会报错也不会加载两份:它按**jar 内部 `META-INF/mods.toml` 的 `version` 字段**(不是文件名)排序去重,**留下一份、静默丢弃其余**。文件名里带 `-1.20.1` 之类的后缀完全不参与比较。

**2026-10-03 实测踩到**:`goetyominous-3.0.0.fix-1.20.1.jar`(版本串 `3.0.0.fix-1.20.1`)与 `goetyominous-3.0.0-1.20.1.jar`(版本串 `3.0.0-1.20.1`)并存 → FML 选了 `.fix` 那个,新包连 mod 列表都没进。表现是"改了半天代码进游戏毫无变化",排查方向全跑偏(以为是 goal 没删干净/是 MoveControl 漏进原版)。

**判据(不用猜、看日志)**:`logs\latest.log` 里 Forge 启动时打印的 mod 列表行 `\t- <modid> <version>` 就是最终生效的那个版本;资源加载报错也会带来源 jar 名,例如 `(from goetyominous-3.0.0.fix-1.20.1.jar)`。两者一比就知道加载的是哪份。**"我 build 出 jar 了"和"游戏加载的是我 build 的 jar"是两件事,必须分别验证。**

**Why:** 版本号只存在于 jar 内,光看目录列表看不出谁赢;而且丢弃是静默的,日志里连 "duplicate" 字样都不一定有。

**How to apply:** 换 jar 时把**旧的移出 `mods\`**而不是原地覆盖另一份(`mv` 到实例根目录改名 `.disabled`,FML 只扫 `*.jar`,且随时可挪回)。挪出前先确认该实例还有另一份可用的新包——若旧包是该实例**唯一**的 goetyominous jar,挪走等于整个 mod 消失。核对新包是否真的生效:`md5sum` 比 `build\libs` 与 `mods\` 里那份,再看日志 mod 列表的版本串。相关:[[goetyominous-test-instance-and-logs]]、[[forge-compile-setup]]
