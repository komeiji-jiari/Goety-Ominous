---
name: mc-1211-asset-extraction
description: 1.21.1 原版音频/素材提取法与坑:本体 jar 无 ogg;sounds 在启动器 assets 对象库按 SHA1 存;1.21.2 起部分音效被重编码;附带 mappings 解混淆对照表
metadata: 
  node_type: memory
  type: reference
  originSessionId: 3de0d865-d387-45cb-8c49-d09049e8bc59
---

- **权威取音频路线（2026-10 实测补充）**：镜像（jsdelivr）对**大 ogg 会超时截断**（precipice.ogg 两次只下到 0.6MB/1.1MB），且**第三方模组里的音频可能是转码版**（Trials-Final 的 precipice 2.9MB vs 官方 9.8MB，CDN 哈希 404 实证）。正确姿势：`https://piston-meta.mojang.com/mc/game/version_manifest_v2.json` → 1.21.1 的 version json → `assetIndex.url`（⚠️ 1.21.x 共用索引 **17.json**）→ 索引里 `objects["minecraft/sounds/records/precipice.ogg"].hash` → `https://resources.download.minecraft.net/<xx>/<hash>` 直取（内容寻址=按哈希即权威，sha1 自校验）。小文件（<300KB）镜像仍可用 ✓。
- 提取流程:先从镜像 `https://cdn.jsdelivr.net/gh/InventivetalentDev/minecraft-assets@<版本>/assets/minecraft/...` 拿同名文件(得内容与 SHA1)→ 到本机 store 按 SHA1 拷贝;或直接从镜像下载。权威验证:官方 CDN `https://resources.download.minecraft.net/<xx>/<sha1>`(按哈希拉,可逐字节比对)。
- ⚠️ **跨版本重编码坑**:1.21.2 起 Mojang 把部分音效重编成低码率(heavy_core 240kbps→86kbps,文件减半),同名文件跨版本 md5 不同 —— 移植取素材必须按基准版本(1.21.1)。
- 解混淆工具链:`tools/mappings-1.21.1.txt` + `tools/cfr-0.152.jar`(反编译单个 .class;`tools/mc-classes` 是混淆类)。常用映射:**Blocks=dga、HeavyCoreBlock=djr、SoundType=dnm、BlockBehaviour$Properties=dtb$d、PushReaction=epj(NORMAL=a/BLOCK=c)、BlockStateProperties=dts(WATERLOGGED=C)、MapColor=epi(METAL=g)、NoteBlockInstrument=dud(SNARE=c)**;映射文件里 `-> xx:` 查类、段落内 `-> c` 查成员。
- 结论备忘:**原版 heavy_core 徒手可挖**(无 requiresCorrectToolForDrops、不在任何 needs_* 标签),且**可含水**(SimpleWaterloggedBlock + WATERLOGGED);其模型 JSON 里贴图引用写裸 `"all"`(官方特例)——1.20.1 用 `#all` 才对。
- 相关:[[windy-update-port-plan]] [[port-data-fidelity-rule]] [[cuotiben-tooling]]
