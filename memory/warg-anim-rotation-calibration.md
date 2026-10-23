---
name: warg-anim-rotation-calibration
description: "Warg/Cerberus 模型动画的旋转方向定标法与骨骼语义(正=低头/张嘴/前倾,负=抬头),附反推验证步骤"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 8422e26a-7053-43c5-b20c-10c60df0512a
  modified: 2026-09-19T13:01:06.270Z
---
给 Warg / Cerberus 这类自制 `HierarchicalModel` 写新动画时,别靠"看起来应该"猜方向——
它们的中间骨骼带着 ±90° 的 X 旋转(`mane` +1.5708 / `head` -1.5708 互相抵消),肉眼推不出来。
**符号约定(已实测标定):正 = 低头 / 张嘴 / 躯干前倾;负 = 抬头 / 闭嘴 / 躯干上扬。**
和原版 `headPitch` 一致(正=低头),因为 `LivingEntityRenderer` 那层 `scale(-1,-1,1)` 会把
模型空间的 X 轴旋转手性翻过来——所以照着"右手定则"在模型空间硬算是会算反的。
**定标方法(可复用):** 找同一文件里语义已知的动画,读它的关键帧反推。这次用了两个锚点:
- `landing` 里 `bone`(躯干)在触地帧给 **+5.0**、`jumping` 里给 **-7.5**
  → 落地前倾 / 腾空上扬 ⇒ 负=上
- `bone2`(下颚)在 `grounded`(放松坐姿,嘴应最闭)给 **-22.5**、`biting` 在 -30~+25 摆动
  → ⇒ 正=张嘴
**Warg 骨骼语义**(`WargModel.createBodyLayer`,层级 root→bone→mane/body):
- `mane` = 前半身(含前腿 leg3/leg4),**动它会带着前腿一起动,别拿它当脖子用**
- `head` = 头(mane 的子级),抬头低头只动这个
- `bone2` = 下颚 + 牙齿,rest 本身 +0.6109(≈35°,即静止就是微张的龇牙姿)
- `bone4` = 头顶那片刻板(3×8×0 零厚度),幅度别超过 ±16(参考 running 的 4~16)
- `bone` POSITION 的 ±Y 是上下移动(不是翻转的):`grounded` -9 = 坐下的下沉
- 三个模型共用 `WargAnimations`,新增动作要**同步绑到 `WargModel` / `WargArmorModel` /
  `WargSaddleModel`**,否则护甲头盔会跟头脱开。`WargSaddleModel` 只有 bone/mane/group,
  没有 head —— 按名字找不到的骨骼 `KeyframeAnimations` 会静默跳过
- `CerberusModel` 用自己那套 `CerberusAnimations`,不绑 WargAnimations
相关:[[forge-model-space-to-world]]