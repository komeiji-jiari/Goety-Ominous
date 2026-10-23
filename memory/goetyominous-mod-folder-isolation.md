---
name: goetyominous-mod-folder-isolation
description: 联动内容一律进 <modid>/ 子包隔离，附 8 个缩写目录名到实际 mod 包的对照表
metadata: 
  node_type: memory
  type: reference
  originSessionId: 76783ee9-783b-4555-addf-bb22a53ee522
  modified: 2026-10-19T21:40:26.505Z
---

仓库对每个联动 mod 都做目录隔离，实体/渲染/模型三处同名子包，与 `compat/<modid>/` 一一对应：

| 目录 | 实际 mod 包 |
|---|---|
| `ac` | `com.github.alexmodguy`（Alex's Caves）|
| `am` | `com.github.alexthe666`（Alex's Mobs）|
| `lm` | `net.miauczel.legendary_monsters` |
| `of` | `com.unusualmodding.opposing_force` |
| `sar` | `com.teamabnormals.savage_and_ravage` |
| `ua` | `com.teamabnormals.upgrade_aquatic` |
| `mm` | `com.alexander.mutantmore` |
| `ias` | `com.yellowbrossproductions.illageandspillage`（Illage and Spillage）|

对应路径三处：
- `common/entities/ally/<modid>/`
- `client/render/<modid>/`
- `client/render/model/<modid>/`
- （layer 也在 `client/render/layer/<modid>/`）

**Why:** 2026-09-25 把 IAS 和 mm 补齐到这套约定（IAS 原本散在通用桶 `ally/illager/` `ally/mobs/` 和 `render/` `model/` `layer/` 根目录，mm 的 renderer 也留在根目录）。注意 `ally/mobs/` 是**自研实体的通用桶**，不是"怪物 mod"目录——往里放第三方实体就是漏隔离的信号。

**How to apply:** 新增联动实体时直接建 `<modid>/` 三处子包。`ally/illager/train/GoetyOminousType.java` 是核心训练仪式类型，只处理 Goety/IAS 的灾厄村民。训练仪式用的是 Forge `IExtensibleEnum`（`IllagerType.create` 可追加常量），所以每个联动 jar 自己注册一个 `ITrainIllager` 类型即可：SAR 的是 `compat/sar/SarOminousType.java`，在 `SarCompatManager.init` 里 `IllagerType.create("SarOminous", ...)`。不要再加回跨 mod 的扩展表/静态注册（2026-10-19 已删）。

迁移手法（已验证可行）：`git mv` → `sed` 批量替换**全限定名**（`OLD_FQN.CLASS` → `NEW_FQN.CLASS`）→ 单独改 `package` 行（它的行尾是 `;` 不含类名，通配 sed 抓不到）→ 补/删通配 import → `compileJava` 迭代。漏改任何一处引用都会编译报错，所以编译绿即代表引用全部到位。相关：[[intellij-red-font-troubleshooting]]、[[forge-compile-setup]]。
