---
name: goety-ac-servant-keydown-channels
description: AC 坐骑类仆从(移植 AlexsCaves 实体为 Goety Summoned)骑乘能力键的三条通道:AC 能力键(撼地斯拉同款)、repo 自注册键 K、MC 空格跳跃
metadata: 
  node_type: memory
  type: reference
  originSessionId: fa1cabc6-fecf-4ce4-bdbf-94e8f3690638
  modified: 2026-09-06T08:37:28.472Z
---

移植 Alex's Caves 可骑乘实体为 Goety `Summoned` 仆从时,"玩家骑乘触发能力"有三条现成通道(2026-09 实勘于 D:\ark\java):

1. **AC 原生能力键**:`AlexsCaves.PROXY.isKeyDown(2)` → `AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(id, playerId, 2))` → 实体实现 `KeybindUsingMount.onKeyPacket(keyPresser, type)`,服务端校验 `keyPresser.isPassengerOfSameVehicle(this)`。TremorzillaServant(AnimalSummon,非 Summoned)原样用这条(`TremorzillaServant.java:457-464, 1343-1364`)。
2. **repo 自注册键**:`client/ModKeyBindings.java` 的 `RIDER_CHARGE_KEY`(默认 K)→ `client/RiderChargeInputHandler.java` `onKeyInput` 里 `consumeClick()` 后按 `vehicle.getClass().getName()` 精确字符串匹配目标类 → 发 `RiderChargePacket`。这是给 MutantMore 的突变疣猪兽用的。
3. **MC 原生跳跃**:空格走 `PlayerRideableJumping`(蓄力跳)或自实现输入感测+packet(ForsakenServant 蓄力跳)。

CandicornServant 决定用通道 1(和 Tremorzilla/撼地斯拉同键,type=2 式)做骑乘冲刺 + 通道 3(空格=普通跳跃)。

**关键约束**:
- `KeybindUsingMount`/`MountedEntityKeyMessage`/`RiderChargePacket` 均在 AC 存在时才能引用;AcCompatManager 整体被 `AlexCavesCompat.isAlexCavesLoaded()` 门控(GoetyOminous.java:133)。
- `TremorzillaServant` 覆写了 updateControlFlags 禁 MOVE/JUMP/LOOK + 骑行时 setTarget(null),是"大体积 AC 坐骑 + Goety AnimalSummon"的样板;ForsakenServant 是"Summoned + 自写 rider 蓄力跳"的样板。
- AC `CandicornEntity` 服务端实体与 AI 源码、客户端模型/渲染/粒子/骑手层都已拉在 `.scratch/` 可作移植素材;纹理在 AC jar 需运行时引用。

相关:[[goety-servant-follow-standby]] [[ac-compat-porting-notes]]
