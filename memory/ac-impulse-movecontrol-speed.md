---
name: ac-impulse-movecontrol-speed
description: AC 部分实体(沙虫/腐蚀者)速度来自自定义 MoveControl 的硬编码冲量 + 自定义 travel，MOVEMENT_SPEED 属性是装饰；移植时丢了这两样速度只剩 1/8
metadata: 
  node_type: memory
  type: reference
  originSessionId: e6112b54-7831-4204-8858-4e13afce37db
  modified: 2026-10-03T01:08:47.429Z
---

Alex's Caves 的**沙虫 `GumWormEntity`** 速度**完全不来自 `MOVEMENT_SPEED` 属性**：

- `GumWormEntity.MoveController extends MoveControl`（内部类，硬编码 `float digSpeed = 0.25f`）：每 tick 直接
  `setDeltaMovement(getDeltaMovement().add(offset.normalize().scale(speedModifier * 0.25)).scale(0.9F))`
  —— 冲量大小 = `speedModifier × 0.25` 格/tick，与目标距离无关；转向是 `setYRot(approachDegrees(..., 25.0F))`。
- `GumWormEntity.travel()` 覆写：`moveRelative(getSpeed(), vec)` + `move(MoverType.SELF, delta)` + `setDeltaMovement(delta.scale(0.8))` —— **没有摩擦、没有重力**（`getSpeed()` 恒 0，所以 moveRelative 那段实际不产生位移）。
- 稳态速度 = `1/(1-0.9×0.8) × 0.72 × 冲量` ≈ **2.571 × 冲量**：游荡/跟随(sm=1.0) ≈ 0.643 格/tick ≈ **12.9 格/秒**；骑乘(`setWantedPosition(..., 3.0)`) ≈ 1.93 格/tick ≈ 38.6 格/秒。
- 属性里的 0.25 / 重力 0.15 在 AC 里基本是摆设。

**踩过的坑（2026-10-03）**：沙虫移植时因为砍掉钻地，把这两个内部类一起丢了，改用 `GroundPathNavigatorNoSpin` + 默认 `MoveControl`。默认 MoveControl 走 `setSpeed(sm × 属性)`，而**乘积 <1 时冲量被平方**（见 [[goety-move-speed-two-channels]]）：0.25×1.0 → 实际 0.0625 格/tick ≈ 1.6 格/秒，**慢了 8 倍**；骑乘 3.0×0.25=0.75 → 反而有 ~14 格/秒，所以只有"走路慢"能被看出来。

**修法**：把 `MoveController` + `travel` 一起搬回来（去掉挖矿判定 `isSafeDig`），冲量用 `speedModifier × getAttributeValue(MOVEMENT_SPEED)` 代替硬编码 0.25 —— 默认值相同所以数值与原版完全一致，但 config 仍然可调、Goety 的 staying 惩罚（`IOwned.SPEED_MODIFIER` = ADDITION -1.0，把属性压到 0）也能自然让虫停下。地面行走版要额外补重力（原版靠挖地/冲量的 y 分量，没有重力）。

**判据**：看移植主体的反编译源码里有没有 `f_21342_ = new ...MoveController()` 和 `m_7023_(travel)` 覆写；AC 的 `CorrodentEntity`、`GumWormEntity` 都是这一挂。仓库里的正解样板是 `CorrodentServant.DiggingMoveControl`（保留了挖地版）、`HullbreakerServant.travel`（保留了水中版）。

相关：[[ac-servant-parity-keep-original]]、[[goety-move-speed-two-channels]]、[[goety-ac-dinosaur-spirit-port]]
