---
name: mm-shulker-trap-texture-blue-outline
description: "潜影贝陷阱贴图上那圈\"蓝线\"的来历（我们这批贴图 = MM 形状重上色 + 多画 2px 浅蓝描边），2026-10-16 已按用户令剥掉；附 GDI+ 改 PNG 会预乘往返的坑与纯 Node 改图脚本"
metadata: 
  node_type: memory
  type: project
  originSessionId: 0d6ebe98-227e-4b82-9991-057c0f3871ec
  modified: 2026-10-16T00:26:52.462Z
---

**现象：** 潜影贝陷阱仆从身上有一圈浅蓝的"模型线框"。**排查结论：不是渲染出来的**——`MutantShulkerServantTrapRenderer` 与 MM 上游 `MutantShulkerTrapRenderer` 反编译对比后结构完全一致（都是 `MobRenderer` + 按颜色选贴图 + `getFlipDegrees` 返回 0，没有 layer、没有画线）；线在**贴图**里。

**成因：** 我们这批 `assets/goetyominous/textures/entity/mutant_shulker_trap_servant*.png`（本体 + 16 染色）是 MM 那张的重上色版，且**在面板外面多描了 2 像素浅蓝边**。因为实体默认走 cutout 渲染类型（只丢弃 alpha < 0.1），描边的 alpha 100/45 也被当实心渲染 → 每条棱边一条实心蓝线。上游 `mutantmore:textures/entities/mutant_shulker_trap.png` 蓝色像素数为 **0**，且我们的紫色也比上游亮一档（我们 `#C08AC0/#AD7EAD/#9A719A`，上游 `#976997/#875F87/#775477`）——同 [[mm-mutant-shulker-dyeing]] 那批"外推近似品"。

**2026-10-16 用户令"这圈蓝边去掉"→ 已剥离。** 判据（很硬）：把蓝边像素排除后，我们不透明像素与 MM **逐像素完全重合**（重合 1073、单方 0），说明紫面板形状本来就等于上游，蓝边纯粹是画在外面的多余 2px。剥离后 opaque 1860 → **1073 = 上游的值**，剪影与上游一致。**只做了去描边，底色紫没动**（用户只要去掉蓝边）。

**工具链坑（下次改贴图先看这条）：**

- **GDI+ 读 PNG 会预乘往返**：磁盘上实际存的是 `#77D6EA`(a=100) / `#76D7E8`(a=45)，但 `Bitmap::FromFile` + `GetPixel` 会读成 `#78D7EB`——**照 GetPixel 的值去精确匹配会一个像素都匹配不到**（第一版脚本 `stripped=0` 就是这个原因）。
- **`$bmp.Save($path)` 是无条件写**：干跑版脚本即使没改任何像素也把 17 张全部重编码了（`git status` 立刻变 17 个 M）→ 先 `git checkout --` 还原再改。**贴图改动前先确认 `git status` 干净，它就是免备份的回滚点。**
- **正解**：`.scratch/_strip_trap_outline.mjs` —— 纯 Node 解 PNG（8 位 RGBA / colorType 6 / 非交错，本仓库贴图都是这个格式），自己做 unfilter / deflate / CRC32，不经过任何色彩空间转换；`node 脚本 report|apply|verify`，verify 会拿 `git show HEAD:<path>` 对拍并报「非目标像素被改」与 opaque 计数。要改别的贴图可以直接抄这个脚本。

相关：[[mm-mutant-shulker-dyeing]]、[[resource-gen-script-no-rmdir]]
