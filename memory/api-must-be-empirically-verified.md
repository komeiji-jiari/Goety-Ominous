---
name: api-must-be-empirically-verified
description: ⚠️最高优先级：任何 API（方法名/字段/常量映射/mixin target/codec/行为）不得凭名字猜，必须查 1.20.1 映射、javap、反编译源码、refmap 或实测
metadata: 
  node_type: memory
  type: feedback
  originSessionId: e4255ebd-0052-4003-9006-bce94115609a
---

**任何 API 不得猜想，必须实证。** 包括但不限于：Minecraft/Forge 方法名、字段名、SRG→official 常量映射、mixin 的 `@Shadow`/`@Inject`/`@ModifyArg` 目标、Codec 重载、`BooleanOp` 等枚举常量、以及“名字看起来对”的行为语义。

**Why:** 2026-10-27 移植 MTI 试炼密室时，把 MTI 的 `BooleanOp.f_82689_` 按名字猜成 `ONLY_FIRST`；查 1.20.1 映射后实际是 `AND`。结果 `dimension_padding` 把 `free` 可用空间裁反，只生成起始房 + 内嵌宝库，向外接的走廊/房间全部被碰撞检测拒绝。另外 `StringRepresentable.fromEnum(MobCategory.values())` 也猜错过，1.20.1 只有 `fromEnum(Supplier<E[]>)`，正确是 `StringRepresentable.keys(MobCategory.values())`。

**How to apply:**
- SRG 映射：查 `C:\Users\MECHREVO\.gradle\caches\forge_gradle\minecraft_repo\versions\1.20.1\mcp_mappings.tsrg` + `client_mappings.txt`；先按类定位，再按 obf 成员名交叉映射，不要只看 SRG 名猜。
- 原版 API：优先用 `build/fg_cache/net/minecraftforge/forge/1.20.1-47.3.22_mapped_official_1.20.1/forge-1.20.1-47.3.22_mapped_official_1.20.1.jar` 做 `javap`/CFR，确认签名、字段、常量。
- Mixin target：编译后看 `build/tmp/compileJava/compileJava-refmap.json` 的实际映射；启动后查 `debug.log` 的 `Mixing ... into ...`；字段/方法必须在目标类源码或 `javap` 里存在，不能只靠“编译通过”。
- Codec/DFU：确认运行时 `E:\release\.minecraft\libraries\com\mojang\datafixerupper\6.0.8\datafixerupper-6.0.8.jar` 的重载，而不是只信编译期；`DataResult.error` 用 Supplier 已在 6.0.8 实证。
- 行为语义：`BooleanOp`、`Shapes.join`、优先级队列、排序方向等，必须对照 MTI/1.21 源码或写独立 harness 实测；队列已用 jshell 验证 `high mid low`。
- 查不到实证时停下来问/继续查，不许“先这样写试试”。

相关：[[trial-chamber-port-plan]] [[cuotiben-tooling]] [[srg-axis-mapping]]
