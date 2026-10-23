---
name: mc-hardcoded-entitymodel-retexture
description: "模组实体几何写死在 Java 里时,资源包只能改贴图;含 ModelPart.Cube 的 UV 布局公式与读几何的工具"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 013128df-21c2-4fff-92e1-a2fda5b72ea8
  modified: 2026-10-02T17:37:49.762Z
---

给模组实体换皮时,先分清几何是 **JSON 模型**(资源包能整体替换)还是 **Java 里 CubeListBuilder 写死的**(资源包只能改贴图)。

**读死几何**:`.scratch/_disasm.mjs <class> [方法名]` —— 自写的 JVM 字节码扫描器,直接输出 createBodyLayer 里的 texOffs/addBox/PartPose 调用序列和 ldc 常量的**实际值**(int/float 常量池都解了)。比反编译快,也能拿到 `pokeBallModel.createBodyLayer` 这种只有一屏的类。注意 `fconst 1.0`/`iconst 0` 是隐式常量,不进常量池。

**ModelPart.Cube 的 UV 布局**(1.20.1,`_mcsrc/.../geom/ModelPart.java` 已核对,与人人熟知的皮肤表一致),盒子 (dx,dy,dz) 在 texOffs(u,v);占位宽 `2dx+2dz`、高 `dz+dy`:

| 面 | x 范围 | y 范围 | 尺寸 |
|---|---|---|---|
| down | u+dz | v | dx × dz |
| up | u+dz+dx | v(**V 翻转**) | dx × dz |
| west | u | v+dz | dz × dy |
| north | u+dz | v+dz | dx × dy |
| east | u+dz+dx | v+dz | dz × dy |
| south | u+dz+dx+dz | v+dz | dx × dy |

按这个公式铺 18 个盒子 × 6 面实测 **重叠 0** —— 拿它当自检:算完统计覆盖像素数与重叠数,重叠不为 0 就是公式或盒子表抄错了。
实体模型用 `EntityModel.renderType()` = `entityCutoutNoCull`,**透明度是二值的**(alpha<0.1 直接丢弃),别指望半透明。

**工具**(都在 `.scratch/`,纯 node、无需三方库):`_png.mjs` 读写 PNG(RGBA8)、`_genpbatlas.mjs` 按盒子表生成整张图集、`cropzoom.ps1` 放大看像素、`zip_pack.ps1` 打包(显式把 `\` 换成 `/`)。像素图的 ASCII dump 很好用,但**别用同一个字符既表示透明又表示某个颜色**——踩过,读起来自相矛盾。

活例子:精灵球模组 → [[pokeball-tesseract-pack]]。打包/覆盖边界见 [[mc-resourcepack-texture-revert]]。
