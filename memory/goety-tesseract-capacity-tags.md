---
name: goety-tesseract-capacity-tags
description: Goety EsotericTesseract 的 tag→容量映射是错位的，tesseract_large=占满方匣(+16)，占4格要用 tesseract_medium
metadata: 
  node_type: memory
  type: project
  originSessionId: cac4e3f0-da9d-44eb-a70e-df633a5afa2c
  modified: 2026-08-30T05:41:32.566Z
---

Goety 奥术方匣（EsotericTesseract）的容量 tag 语义与直觉相反，容易配错：

- **`goety:tesseract_large` = 占满整个方匣**（保存 isLarge→存 HUGE 标志→读取 `+= TesseractCapacity`(默认16)）。原版只放 `redstone_monstrosity` 这类巨型 boss。
- **`goety:tesseract_medium` = 占 4 格**（isMedium→存 LARGE→读取 `+= 4`）。原版放 necromancer servant / redstone_golem / grave_golem。
- **`goety:tesseract_small` = 占 2 格**（isSmall→存 BIG→读取 `+= 2`）。

根因在 `EsotericTesseract.putServantIntoTesseract`：`isLarge→HUGE / isMedium→LARGE / isSmall→BIG`，与读取端标志名错位。

**Why:** 本模组 TesseractTagHandler 用 `Holder.Reference.bindTags` 在 `TagsUpdatedEvent` 运行时把仆从实体绑定到 tag；曾把三个 mm 仆从放进 `tesseract_large`，导致一只就占满方匣（16）而非预期的 4。

**How to apply:** 给实体指定"占 4 格"时必须 bind `goety:tesseract_medium`（TesseractTagHandler.TESSERACT_MEDIUM）。大中小仆从归类参考：hullbreaker/tremorzilla 巨型放 LARGE；mm 三仆从、AC 恐龙类放 MEDIUM；小型仆从放 SMALL。bindTags 是追加合并，不会清掉实体已有 tag。相关：[[ac-compat-porting-notes]]
