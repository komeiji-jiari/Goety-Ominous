---
name: ac-screen-shake-duplicate-handler
description: AC 的 ClientEvents.computeCameraAngles 已覆盖所有 ShakesScreen（含本仓库仆从），TremorzillaRenderEvents 那份是逐行重复实现
metadata: 
  node_type: memory
  type: project
  originSessionId: 4696e330-a840-4e83-b330-b43265b7c76a
  modified: 2026-09-30T14:19:18.355Z
---

Alex's Caves 自带的 `com.github.alexmodguy.alexscaves.client.event.ClientEvents#computeCameraAngles` 会扫 64 格内**所有** `ShakesScreen` 生物，对相机 `move(每 tick 重掷的随机偏移)`；本仓库的 Tremorzilla/Tremorsaurus/Forsaken/Atlatitan/Luxtructosaurus Servant 都实现了该接口，所以 AC 那颗本来就会震到它们。AC 同一方法里还给 Submarine/Tremorsaurus/**Atlatitan**/Tremorzilla/GumWorm 做 `getMaxZoom` 后推——但本仓库的 `*Servant` **不继承** AC 实体（`AtlatitanServant extends AnimalSummon`、`LuxtructosaurusServant extends Summoned`），所以后推那几支不会重复，**只有相机震动那一支重复**。

- 重复点：`TremorzillaRenderEvents.computeCameraAngles`（用自己那对 `randomTremorOffsets`/`lastTremorTick`）——同一帧两颗各生成一套随机数、各 move 一次 = 振幅翻倍、两次偏移互不相同。
- 2026-09-19 已给本仓库那份加门控 `!Boolean.TRUE.equals(AlexsCaves.CLIENT_CONFIG.screenShaking.get())`：AC 开着只震一次，AC 关着本仓库补上。
- 骑手判定：`canFeelShake` 只排除「自己是那个 shaker 的乘客」，所以**自己坐骑**不震自己；但旁边另一只仆从震时骑手吃满（骑乘时玩家 `onGround` 是挂载前的陈旧 true），且 `getShakeDistance()` 恒为 64.0，很远也生效。

**Why:** 排查「骑乘时镜头抖」时容易只盯后推；两颗 handler 叠加随机偏移能放大到 0.4~1.0 格/帧，看着就像疯狂抖动。

**How to apply:** 见到 `ShakesScreen` 相关镜头抖动，先确认 AC 与本仓库两颗是否同时生效；给仆从加震动前先查 AC 是否已经代劳。
