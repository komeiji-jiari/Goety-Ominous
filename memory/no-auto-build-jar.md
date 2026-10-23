---
name: no-auto-build-jar
description: 改完不要自动跑 gradlew build，等用户明确说构建才构建
metadata: 
  node_type: memory
  type: feedback
  originSessionId: 1c1f2d5c-15a3-48c5-aa73-e34ae2e4aa2c
  modified: 2026-10-15T12:56:30.234Z
---

改完文件就停手汇报，**不要**顺手 `./gradlew build`。只有用户明说「build」「构建 jar」「打包」时才构建。

**Why:** 构建时机由用户控制——一次 build 二三十秒，而且产物要自己拷进测试实例、拷之前还得确认游戏没在跑（见 [[goetyominous-test-instance-and-logs]]）。2026-10-15 用户明确要求「以后修改完不用 build jar，我让你做你再做」。

**How to apply:** 编辑完直接说改了哪些文件、结果如何，停在「改完了」。要验证就只做静态检查（grep / Node 脚本 / JSON 解析 / 复刻 Patchouli 排序逻辑），不触发构建。顺带：这也意味着别为了"验证"而构建。

相关：[[no-unrequested-recipes-patchouli]]、[[goetyominous-test-instance-and-logs]]
