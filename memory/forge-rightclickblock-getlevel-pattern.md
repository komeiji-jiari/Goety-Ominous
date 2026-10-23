---
name: forge-rightclickblock-getlevel-pattern
description: "Forge 1.20.1 RightClickBlock 里 event.getLevel() instanceof Level 模式匹配报编译错误,改用 player.level()"
metadata: 
  node_type: memory
  type: project
  originSessionId: 8da63f92-8a37-457a-8267-36433333c8f0
  modified: 2026-08-24T14:18:24.801Z
---

Forge 1.20.1 的 `PlayerInteractEvent.RightClickBlock` 事件里写 `if (event.getLevel() instanceof Level level)` 会报 javac 错误「模式类型 Level 是表达式类型 Level 的子类型」——`getLevel()` 的返回类型与模式类型的子类型关系让 javac 不满。

**How to apply:**
- 直接从 `Player player = event.getEntity();` 拿 `player.level()`(返回 `Level`)来用,别用 `event.getLevel() instanceof` 模式匹配。
- 服务端事件里 `player.level()` 就是 `ServerLevel`,后续可再 `instanceof ServerLevel` 判断。
- 编译错误输出是 GBK 编码,终端/日志乱码,用 `grep ... | iconv -f GBK -t UTF-8` 转码才可读。

**Why:** 项目里首个 `RightClickBlock` 处理器(NucleeperSummonHandler)踩了这个坑;GBK 转码排查法对所有编译报错排查通用。相关:[[forge-compile-setup]]