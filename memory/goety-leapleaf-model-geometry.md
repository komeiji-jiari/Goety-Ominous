---
name: goety-leapleaf-model-geometry
description: "Goety LeapleafModel 静止姿势几何(body/main/chest 烘焙角、腿挂 root、坐标符号),算动画角度前必读"
metadata: 
  node_type: memory
  type: reference
  originSessionId: fbf5c252-d6e9-419a-bc0b-63ff4b170a9a
  modified: 2026-09-10T14:04:04.075Z
---
Goety `LeapleafModel`（`libs/goety-2.5.56.0.jar`，SRG 名）的 `createBodyLayer` 实测数值：
- `body` = `PartPose.offset(0.4975, 15.0, 19.6288)`，`main` = `offsetAndRotation(0,0,0, 0.8727f,0,0)`，`chest` = `offsetAndRotation(0,-12,0, 0.3927f,0,0)`，`head` = xRot `0.2618f`。
- 即 **xRot：main +50°、chest +22.5°、head +15°**（弧度→度：0.8727→50、0.3927→22.5、0.2618→15）。
- `body` 与 `main` 都是空 `CubeListBuilder` 的**纯变换节点**，几何从 `chest` 开始；`body` 的枢轴 (y=15, z=19.6288) 与腿的 (y=16, z=19.6288) 重合，等于**胯部**，转 `body` 就是整条上身绕胯折。
- **`right_leg`/`left_leg` 挂在 `root`**（`createBodyLayer` 里是 `aload_1`），不在 `body` 链上，转 `body` 带不动腿，要单独给角度。
- `chest` 盒 `addBox(-10,-18,-7, 19,18,15)`，质量在枢轴**上方**；`right_arm` 静止 xRot `-0.2182f` = **-12.5°**、zRot `+0.1745f` = +10°。
符号：模型空间 **-Z = 朝向、+Y = 向下**，`+xRot` 对枢轴**上方**质量是前倾、对**下垂肢体**是后摆（锚点 `ArmPose.BOW_AND_ARROW` 的 `rightArm.xRot=-PI/2` 是前伸）。所以静止姿势的胸部其实已经前倾 **72.5°**（50+22.5）。
**IDLE 动画的通道值几乎全是 0**（`body`/`main` 全 0，`chest` 只 +7.5°）——那个驼背静止姿势**就是这生物的"站立"造型**，不是缺动画。定水中姿态时以"胸=90° 为水平"为基准反推 `body` 增量（约 +35，还要算上 SWIM 循环自带的 `chest -20`）。
相关：[[goety-model-reuse-private-fields]]