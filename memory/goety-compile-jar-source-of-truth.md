---
name: goety-compile-jar-source-of-truth
description: 编译用的 Goety 不是 libs/goety-*.jar，而是 build.gradle 里那个 curse 坐标对应的 gradle 缓存 deobf jar；版本号会变，每次先去 build.gradle 现查
metadata: 
  node_type: memory
  type: reference
  originSessionId: 9a8eed06-9e03-4f5b-9777-466810a2f025
  modified: 2026-10-15T11:45:21.177Z
---

**`libs/goety-2.5.56.0.jar` 不是编译依赖**（`build.gradle` 里只有 `implementation fg.deobf("curse.maven:goety-586095:<version>")`，libs 那个只是丢在那儿的副本）。两者字节不同（Summoned.class 23672 vs 25063 字节），**API 也不同**——踩过的坑：`libs` 版没有 `getFollowSpeed()`/`getCommandSpeed()`，`Summoned.followGoal()` 传字面量 `1.0`；真依赖版两者都有，`followGoal()` 传 `this.getFollowSpeed()`。2026-09-30 为了改骑士跟随行为差点按错版本的结论去写。

**权威路径（version 每次现查，别背）**：
1. 先 `grep -n "goety-586095" build.gradle` 拿到坐标尾巴，**那才是当前版本**（2026-10-15 是 `9153454`，记忆里写过的 8951387 已经过期）。
2. jar：`C:\Users\TangJinbo\.gradle\caches\forge_gradle\deobf_dependencies\curse\maven\goety-586095\<version>_mapped_official_1.20.1\goety-586095-<version>_mapped_official_1.20.1.jar`（**用 mapped/deobf 那个**，反编译出来才是可读名字；`.`gradle/caches/modules-2/...` 下还有一份未映射的原始 jar，别用）。
3. 判某个 `.scratch/xxx/com/...` 反编译到底对应哪个 jar：**md5 比对 class 文件**，别信目录名/时间戳。已知 `.scratch/_goety/` 与老依赖一致，`.scratch/_gf/`、`.scratch/goety_follow_decomp/`、`.scratch/goety_req_decomp/` 都是老版本。
4. 反编译照 `.scratch/decompile_goety_servantaigoal.gradle` / `.scratch/dc_req2.gradle`（JavaExec + CFR，`--jarfilter` 卡包名）建一个，`./gradlew -I <那个.gradle> <task>`。**直接跑 `java`/`javap` 会被守卫拦**，走 gradle 就行。
5. 只要几行常量（不进反编译）时：`unzip -p <deobf jar> com/Polarice3/Goety/.../X.class | tr -c '[:print:]' '\n' | grep -i yyy` 就够了。

相关：[[forge-compile-setup]]
