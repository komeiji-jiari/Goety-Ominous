---
name: mob-tickheadturn-bodyrot-snap
description: "Mob.tickHeadTurn 覆写丢掉了 LivingEntity 的 yBodyRot 阻尼,移动时 yBodyRot 硬等于 yRot;模型转圈/抽搐类 bug 在这里加阻尼"
metadata: 
  node_type: memory
  type: project
  originSessionId: 656237a7-572d-4350-8ed1-a88a4c64ce9d
  modified: 2026-09-25T20:21:12.588Z
---

`Mob.tickHeadTurn()` 整个覆写掉 `LivingEntity.tickHeadTurn()`：只调 `bodyRotationControl.clientTick()` 然后 `return p_21539_`，把 `yBodyRot += wrapDegrees(target - yBodyRot) * 0.3F` 和 `if (|diff| > 50°) yBodyRot += diff - sign*50` 那套阻尼全丢了。而 `BodyRotationControl.clientTick()` 在 `isMoving()` 时是 `this.mob.yBodyRot = this.mob.getYRot();` —— **等号硬赋值**。

后果：所有 Mob 移动时身体朝向 1:1 跟 `yRot` 的台阶走（模型朝向 = `yBodyRot`，`LivingEntityRenderer` 拿 `rotLerp(yBodyRotO, yBodyRot)` 插值），而 `MoveControl.rotlerp(getYRot(), f9, 90)` 每 tick 最多掰 90°。凡是"模型走着走着转一圈/抽搐/扭身"的 bug，先怀疑这里，而不是渲染器或动画（渲染器基本都不碰 Y 轴）。

MM 仆从那次：巨型 `bbWidth` 把寻路转向台阶放大（`Path.getEntityPosAtNode` 用 `node + (int)(bbWidth+1)*0.5` 的固定斜向偏移当转向点、`followThePath` 到达半径用 `bbWidth/2`，疣猪/潜影 1.375~1.45 已 > 1 格节点间距 → 节点索引跑到实体前面，转向目标每 tick 换方位）。修法：自定义 `createBodyControl()`，移动分支用回 `LivingEntity` 那套 0.3 + 50° 钳制（`MutantBodyRotationControl`），非移动分支的领头/身链保持不变。MM 自己给烈焰人挂 `ImprovedMoveControl(mob, 90.0F, 3)`（朝向只在 `tickCount % rotateInterval == 0` 时更新）是同一思路的 1/3 版。

注意 `yBodyRot` 不只是外观：MM 的冲锋/踢击/吐息/弹射物生成都用它当朝向（`PositionUtils.getOffsetPos(..., yBodyRot)`），所以阻尼会让"急转瞬间"的攻击朝向滞后最多 50°（0.3 系数约 3 tick 收敛）。

2026-09-25 实测确认修好（凋灵骷髅/疣猪兽/潜影贝三个走 `AbstractMutantServant.createBodyControl()`）。竿灵被用户要求回退——它是 `FlyingMoveControl(this, 20, true)`，转向步长本来就只有地面那三个的 2/9，不在症状范围内。烈焰人显式 `return new BodyRotationControl(this)` 钉回原版，因为它的 `MutantBlazeServantFlyStrafeMovementGoal` 直接拿 `yBodyRot` 算 `deltaMovement`，阻尼会改飞行手感。教训：按症状范围最小化施加，别顺手"统一"。`AbstractMutantServant` 并非所有 MM 仆从的父类（竿灵 `extends Summoned`），改基类前先确认谁真的继承了它。

相关：[[sauropod-client-ybodyrot-local]]、[[vanilla-lookcontrol-head-forward]]、[[goety-servant-follow-standby]]
