---
name: sauropod-part-oldpos-teleport-neck
description: "AC 蜥脚类(擎天龙/瞑煌龙)仆从瞬移后脖子乱扭的根因=部件摆位与客户端位置补间不同拍:tickMultipart 必须放在 lSteps 补间块之后"
metadata:
  node_type: memory
  type: project
  originSessionId: 29b1d364-9553-4e51-aafb-56b0863d7d49
  modified: 2026-09-13T13:48:19.277Z
---

AC 蜥脚类(SauropodBaseEntity 系)的脖子/尾巴姿态在客户端由"部件实体与身体连接点的实际世界向量"反推(`SauropodPartEntity.calculateAnimationAngle` → `SauropodBaseModel.positionNeckAndTail`),它读双方的 `getPosition(partialTicks)` 插值位置。**真正的坑是两者的 (old, cur) 不同拍**:

- 客户端所有移动/传送包都走 `Entity.lerpTo(..., 3, ...)`(`ClientPacketListener.handleTeleportEntity:539`、`handleMoveEntity:565`),AC 覆写 `lerpTo` 只存 `lx/ly/lz/lSteps` 不立刻 setPos,位置在 `tick()` 末尾的 `lSteps` 块每 tick 只走 1/3。
- 部件不在实体 tick 列表,`xo` 由 `tickMultipart()` 末尾从"本 tick 开头的部件位置"抄入 ⇒ 部件的"当前位"= f(身体**移动前**的坐标),而身体的 `x` 已是移动后的坐标 ⇒ 部件比身体**晚一拍**,空间差 = 身体本 tick 位移。走路时 ~0.1 格(看不见),Goety 跟随瞬移(`moveTo`,24~48 格)时 = 十几格 → 向量方向乱 → 脖子被摆成野角度,持续 ~4 tick(补间 3 tick + 收尾 1 tick)。

**How to apply(2026-09-13 已改):** 把 `tick()` 里的 `tickMultipart()/tickWalking()` 从 yBodyRot 计算之后、`if (level().isClientSide){ lSteps 补间 }` **之前**,挪到该块**之后** —— 部件永远按身体当前位置摆位,两边 (old,cur) 对拍一致,任意 partialTicks 几何自洽(身体滑、脖子跟着滑)。服务端没有该块,顺序等价,行为不变(只注意别把 `tickWalking()` 挪到 STOMP screenShake 判定之后,会压低震屏)。

**已回滚的错误修法:** 曾判定为"瞬移后部件 old pos 停在旧位置",加 `prevMultipartCenter` 阈值判定传送 + 传送 tick 直接 `xo=x`。这是错的:客户端根本不存在原始跳变(lerpTo 已被 AC 改成补间),强行 xo=x 反而杀掉插值、并把部件按"移动前坐标"渲染,所以用户反馈"还是存在"。模型侧 `if (!straighten && !entity.isFakeEntity())`(firstTick 首帧部件在 0,0,0)是真需要的,保留。

相关: [[ac-goety-port-probability-gates]] [[goety-servant-riding-mechanism]] [[sauropod-part-oldpos-teleport-neck]]
