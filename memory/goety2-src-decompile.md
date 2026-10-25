---
name: goety2-src-decompile
description: 已反编译的 Goety 2.5.58.2 源码位置与重生成方法——读前置模组 API 直接看这里，不要再用 javap 猜
metadata: 
  node_type: memory
  type: reference
  originSessionId: aa83982d-2283-4121-a0ca-337b4239c3a9
---

本工程依赖 **Goety 2.5.59.0**（`build.gradle`: `curse.maven:goety-586095:9152683`，2026-10-13 用户升级；此前 8951387 → 8891954 即 2.5.58.2），MC 1.20.1 / Forge 47.3.22。

## 已反编译源码（首选，直接 Read）

`E:\java\.disasm\goety_src\com\Polarice3\Goety\` —— 2166 个 `.java`，ForgeFlower 产物，`.disasm/` 已在 `.gitignore` 里。
⚠️ **这份反编译对应的是 2.5.58.2（file 8891954），已落后两个版本**（现依赖 2.5.59.0）⇒ 读它的结论对"新版本才加/改的东西"可能过时，必要时按下面命令重生成（或先对着新版 jar 复核关键签名）。

关键路径：
- 法术基类：`common\magic\Spell.java`、`SummonSpell.java`、`SpellStat.java`、`ChargingSpell.java`、`TouchSpell.java`、`BlockSpell.java`、`BreathingSpell.java`、`EverChargeSpell.java`
- 法术 API：`api\magic\ISpell.java`、`ISummonSpell.java`、`SpellType.java`
- 仆从基类：`common\entities\ally\Summoned.java`；父类 `common\entities\neutral\Owned.java`
- 仆从 API：`api\entities\IOwned.java`、`api\entities\ally\IServant.java`
- AI goal：`common\entities\ai\*.java`（顶层）、`common\entities\ai\servant\*.java`
- 具体法术样例：`common\magic\spells\<派系>\`（abyss/frost/geomancy/necromancy/nether/storm/utility/void_spells/wild/wind，共 125 个）

## 重新生成（若 `.disasm` 被清掉）

1. 全量解 jar（本机 `unzip` 的 `'*.class'` 通配不生效，直接全解）：
   `unzip -o -q "<goety.jar>" -d E:/java/.disasm/goety_cls2`
2. ForgeFlower：
   `java -Xmx6G -jar "<forgeflower.jar>" -dgs=1 -rsy=1 -e="<goety.jar>;<forge-mapped.jar>" E:/java/.disasm/goety_cls2 E:/java/.disasm/goety_src`
   约 3–5 分钟。

jar 路径：
- goety **2.5.59.0（现用）**：`C:\Users\MECHREVO\.gradle\caches\forge_gradle\deobf_dependencies\curse\maven\goety-586095\9152683_mapped_official_1.20.1\goety-586095-9152683_mapped_official_1.20.1.jar`
- goety 2.5.58.2（`.disasm/goety_src` 反编译对应的旧版）：`C:\Users\MECHREVO\.gradle\caches\forge_gradle\deobf_dependencies\curse\maven\goety-586095\8891954_mapped_official_1.20.1\goety-586095-8891954_mapped_official_1.20.1.jar`
- forgeflower：`C:\Users\MECHREVO\.gradle\caches\forge_gradle\maven_downloader\net\minecraftforge\forgeflower\2.0.629.0\forgeflower-2.0.629.0.jar`
- forge mapped（做库解析用）：`C:\Users\MECHREVO\.gradle\caches\forge_gradle\minecraft_user_repo\net\minecraftforge\forge\1.20.1-47.3.22_mapped_official_1.20.1\forge-1.20.1-47.3.22_mapped_official_1.20.1.jar`

## 坑

- 仓库根目录的 `Goety-3-main/`、`Goety-3-main_new/` 是 **Goety 3 / MC 1.21.1**，`Goety-master/` 是 **Goety 1（1.37.0）** —— 三个都**不能**当作本工程 1.20.1 的参考，API 差异很大。只有 `.disasm/goety_src/` 与上面那个 2.5.58.2 jar 是本工程的真实基座。

相关：[[goety2-servant-system]]、[[goety2-spell-system]]
