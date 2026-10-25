---
name: srg-axis-mapping
description: com.mojang.math.Axis 的 SRG 名→官方名对照表，反编译 goety 1.20.1 渲染代码时必查（极易搞错，症状是模型转向反了）
metadata: 
  node_type: memory
  type: reference
  originSessionId: 22c2255f-6322-409f-bcfb-326046b39603
---

# `com.mojang.math.Axis` SRG → 官方映射

反编译 Goety 1.20.1（`libs/goety-2.5.56.5.jar`）拿到的是 SRG 名，还原时**不能凭直觉猜**。按声明顺序对齐（javap 两个 jar 同名列位）：

| 顺序 | SRG | 官方 |
|---|---|---|
| 1 | `f_252495_` | `XN` |
| 2 | `f_252529_` | `XP` |
| 3 | `f_252392_` | **`YN`** |
| 4 | `f_252436_` | **`YP`** |
| 5 | `f_252393_` | `ZN` |
| 6 | `f_252403_` | `ZP` |

⚠️ 最易错的是 `f_252392_`——它是 **YN 不是 YP**，两者差一个符号，`YN.rotationDegrees(-45)` ≡ 绕 +Y 转 +45°，模型会整体转向反侧。

## 自己复验的办法（两条命令对齐即可）

```bash
SRG=build/fg_cache/net/minecraftforge/forge/1.20.1-47.3.22/forge-1.20.1-47.3.22-srg.jar
MAP=build/fg_cache/net/minecraftforge/forge/1.20.1-47.3.22_mapped_official_1.20.1/forge-1.20.1-47.3.22_mapped_official_1.20.1.jar
# 各自 javap com/mojang/math/Axis，按输出行序一一对应
```

同一个坑适用于**任何** SRG 名还原：凡是靠"名字像不像"猜的，都要用 srg.jar ↔ mapped_official jar 对齐验一遍。

## 交叉验证的捷径

`E:\java\Goety-3-main`（1.21.1 NeoForge，**官方映射**）里常能找到同一段逻辑的可读版本，直接对照最快。例：`client/render/block/ModISTER.java` 的 plushie 分支就是 `Axis.XP.rotationDegrees(30)` + `Axis.YN.rotationDegrees(-45)`。

相关：[[dreden-port-plan]]（同为逐文件移植 SRG 代码的场景）
