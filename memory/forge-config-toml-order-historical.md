---
name: forge-config-toml-order-historical
description: Forge 配置 TOML 的键序是历史插入序，改源码顺序不会重排；删除重建才按 spec 定义顺序；重复 define 同一 path 会静默后者覆盖
metadata: 
  node_type: memory
  type: project
  originSessionId: c6e12552-53c3-420f-9575-5bd05d7000b8
  modified: 2026-10-19T11:59:46.105Z
---

Forge 1.20.1 生成/维护配置文件的顺序规则：

- `ForgeConfigSpec.Builder` 内部是 `LinkedHashMap`（`ForgeConfigSpec.java` 注释 "Use LinkedHashMap for consistent ordering"），spec 本身按 `define` 调用顺序排列
- 但 `ConfigFileTypeHandler.reader` 用 NightConfig 的 `preserveInsertionOrder()` 读文件，`ForgeConfigSpec.correct()` 对已存在的键只改值、不动位置，只有缺失的键才 `put` 到所在 section 末尾
- 所以现有 TOML 的顺序 = 每个键第一次被加进配置的年代顺序；在源码里移动 `define` 不会让旧文件重排

**Why:** 用户看 `run/config` 里排序乱，会误以为源码定义顺序乱；实际源码改不动旧文件，只有删掉文件让 `setConfig → correct → save` 从空表重建才按 spec 顺序写出。

**How to apply:** 要让已有配置重排，备份后把 TOML 移出目录，下次启动自动按定义顺序重建（自定义值会丢，先经用户同意）。审计配置时还要查重复键：同一 `push` 路径下两次 `define` 同一键名不会报错，后者覆盖前者的默认值与注释——本项目 `AttributesConfig` 就曾把 `dropBearServantArmor` 的键名误写成 `dropBearServantKnockbackResistance`，把 0.7 的击退抗性默认值覆盖成 0。相关：[[config-file-split-rule]]、[[mobsconfig-unwired-keys-are-not-dead]]。
