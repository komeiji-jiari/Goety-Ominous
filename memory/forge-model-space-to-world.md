---
name: forge-model-space-to-world
description: "手算 Forge 1.20.1 实体模型部件的世界坐标公式/旋转链规则,用于定位坐骑座位、挂点、穿模"
metadata: 
  node_type: memory
  type: reference
  originSessionId: d42522fc-d06d-4c70-a4c4-fec5c852d8b3
  modified: 2026-09-30T00:53:12.727Z
---

`LivingEntityRenderer.render` 的变换链是 `T(实体) · R_y(180-yBodyRot) · S(-1,-1,1) · T(0,-1.501,0) · 模型`。
`ModelPart.translateAndRotate` 先 `translate(x/16,y/16,z/16)` 再 `mulPose(Rz·Ry·Rx)`;`Cube` 顶点也 /16。
所以单块 = 16 模型单位,**世界 y = 实体 y + 1.501 - y_model/16**(模型 y 越大越低,脚底在 y_model=24 → 世界 +0.001)。

**RenderLayer 里最容易踩的坑**:`translateAndRotate` 自己做了 /16,但你紧接着写的裸 `poseStack.translate(x,y,z)`
**是方块不是模型单位**。要接一截模型单位偏移,必须 `/16.0F`,否则 24.0F 就是 24 格。而且这个局部系里
**+y 是世界向下**(由 `S(-1,-1,1)` 决定,和 `PartPose.offset` 的符号习惯一致:y 越大越低)。
所以「往上抬 N 模型单位」写 `translate(0, -N/16, 0)`。样板:本仓库 `PossessedPaladinServantGrabLayer`
接在 `translateModel` 后面用的是 `translate(-1.0,-1.0,1.25)` —— 量级 1 就是方块量级。

旋转链规则:
- 子部件的 offset 在**父部件已旋转的局部系**里,不是根系。求点用 `M·p = o_bone + o_mane + R_mane·o_head + R_head·p` 这样的
  从左往右累乘(矩阵右乘、点从右往左作用),**不能**把 offset 直接按根系相加。
- 逐段对 AABB 取包围盒只在 90° 整数倍旋转下才精确;写脚本比手算可靠。
- 校验手法:把脚(后腿 leg 的 box)代进去,结果应该 ≈ 世界 y=0。对不上就是符号/次序错了。

`Vec3.directionFromRotation(0, yRot)` 在 yRot=0 给 (0,0,1) = 世界前方,而模型局部 **-z 才是前方**
(局部 -z → 世界 +z)。所以 `positionRider` 里 `facing.scale(-0.68)` 的世界 -0.68 对应模型局部 **z=+0.68**,
即坐骑的**后半身**。算坐骑座位高度时别忘了这一步换算。

座位高度 = 目标面世界 y − 乘客跨部高度。玩家模型腿 12px=0.75,所以跨部在自身脚上方 0.75;
`positionRider` 摆的是乘客的**脚**。样板:原版矿车 offset 使跨部刚好过车斗沿。

Warg 实测(见 [[goety-ac-servant-keydown-channels]] 同批):鞍座顶 world y=1.148、背顶 1.126、胸口 1.313,
骑手座位应是 `rideHeight + 0.4`,写 0.75 会悬空 0.35。

**相机余量**:骑手眼高 = 座位 + 1.62,座狼身上最高的几何是 `fur_2` 那片毛(1.751),所以第一人称相机只在
毛尖上方 **0.32**。任何只平滑**骑手**(相机跟着动、坐骑模型不动)的方案,只要下沉超过 0.32 就会把相机送进毛里
→ 上台阶瞬间被挡。要给坐骑台阶做缓动,必须连**坐骑模型**一起滞后(同一 `rideLag` 反向 translate),
让相机与模型相对位置恒定;此时骑手渲染就**不要**再补 `+rideLag`(否则骑手会浮在坐骑上方)。
