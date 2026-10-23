---
name: mc-resourcepack-texture-revert
description: 用材质包把模组贴图回退到旧版本:diff jar → 像素比对 → 模型必须一起换 → 正斜杠打包
metadata: 
  node_type: memory
  type: project
  originSessionId: 3bfa72f4-d8ad-4102-8299-11b083468532
  modified: 2026-10-02T16:18:58.035Z
---

给某个模组做"旧版材质包"的流程（已在 GoetyRevelation 2.3.3fix → 2.3.4 上走通）：

1. **两个 jar 都解包**（`unzip -o -q <jar> -d <dir>`；注意 Git Bash 里带 `assets/*` 的过滤模式不可靠，直接全解再 grep）。
2. **按路径比 md5** 分三类：同名但不同、只旧有新无、只新有旧无。只有"同名不同"才是要还原的对象。
3. **像素级复核**，剔除"只是重新编码、画面没变"的假阳性。用 PowerShell + `System.Drawing` 解码后比 `LockBits` 出来的 SHA256（本机没有 python/PIL，python 是 Microsoft Store 的存根，静默无输出）。实测 62 张全是真重绘，一张都没白放 —— 但这步不能省。
4. **`.mcmeta` 别无条件跟着还原 —— 先算帧数**。这一条踩过坑：`decisive-moment_disc` 新旧版贴图都是 16x16（**1 帧**），旧版 `.mcmeta` 的 `frames` 却列到索引 12 —— 越界，日志刷 `Invalid frame index on sprite goety_revelation:item/disc/decisive-moment_disc frame N: M`。**2.3.4 删掉这个 mcmeta 正是在修这个 bug**，当成"只旧有新无"照搬回去等于复原作者已修的 bug。判据：`帧数 = 高/宽`，逐个比对 `frames` 里的最大索引；越界的一律丢弃。
   反过来，同名 `.mcmeta` 在新旧版都存在的也要核对：`quietus_star` 在 2.3.4 里贴图被改成 16x16 而 `frames:[0..5]` 没改 → 2.3.4 本身就在报这个错。
   **但真正的坑在这条 ⬇️，第一版包就是死在这里的：**
   **`.mcmeta` 必须由包里自己提供，不存在"资源包给 PNG、jar 给 mcmeta"的拼接。** MC 只从**提供该 PNG 的那个包**里读同名 `.mcmeta`；包里没带时，整条 16x96 被当成一张静态图贴进 16x16 图标 = **6 帧叠在一起**（用户原话"很多张叠一起"）。
   **要命的是这种坏法日志里一条都不报**（没有动画就没有帧校验），所以"日志干净"绝不能当作"动画正确"的验收依据 —— 就被这个骗过一轮，还反过来把报错消失解释成"修好了"。
   正确规则：**包里每一个"高>宽"的 PNG 都必须自带同名 `.mcmeta`**；等价说法是"jar 里对该路径有 mcmeta 的，包里就自带一份"（跳过 2.3.4 主动删掉的那些，那是在修 bug）。验收用 `unzip -l` 数 mcmeta，别只看日志。
5. **模型（`models/**/*.json`）必须和贴图成对还原** —— 这条最容易漏。改成"只还原贴图"会让手持模型花掉：作者重绘贴图时会按新贴图重排 UV（实测 UV 值域从 0–16 变成 0–12.3 且出现 6.6/6.7/12.3 这类重画值）。判据：根模型（`forge:separate_transforms` 的 `<item>.json`）两版是否一致 —— 一致就说明 base/gui 的指向没变，覆盖 `_normal.json` 能生效。
6. **打包**：`pack_format: 15`（1.20.1）。本机没有 `zip` 命令，`Compress-Archive` 不可信，用 `.NET ZipArchive` 手工 `CreateEntry` 显式把 `\` 换成 `/`（条目名带反斜杠的包会被 MC 静默忽略）。打完必须复验 `backslash entries: 0`。

**资源包的覆盖边界**（决定了哪些能还原、哪些不能）：
- 能覆盖同路径的 `textures/**` 和 `models/**`。
- **覆盖不了"引用被改掉"的情况**：新版把某模型的 `layer0` 从 `goety:item/unholy_robe` 改成自己的新贴图（Apollyon 长袍就是），旧贴图没有对应路径可放，只能连模型 json 一起塞进去。
- 旧版没有的新内容（新生物/新物品/新粒子）**无从还原**，如实说，别硬凑。

坑：中文路径走 PowerShell 会写飞，见 [[powershell-cli-mangles-nonascii-args]]。本机解包工作目录在 `.scratch/`（已 gitignore），参照 [[goetyominous-deadcode-analysis-scripts]] 的放法。
