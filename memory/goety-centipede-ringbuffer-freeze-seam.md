---
name: goety-centipede-ringbuffer-freeze-seam
description: "蜈蚣体节抖动的根因 = ring buffer 的采样断层(索引只在移动时推进、槽却每 tick 覆盖)；挪写入位置无效,唯一解(无条件推进)被用户回退,现状保留 AM 原样"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 7243ed09-ddbf-45ce-8d7c-6ee99bf5eaf9
  modified: 2026-10-10T16:49:48.192Z
---

`ServantCentipedeHead` 的 64 格 ring buffer 存头每 tick 的 `getYRot()`，节段 i 读 `getYawForPart(i) = getRingBuffer(4 + 4i, 1.0F)` —— `partialTicks = 1 - 1 = 0` → 直接取裸槽 `ringBuffer[(idx - 4 - 4i) & 63]`。这个值喂给 `tickMultipartPosition(..., ourYRot)`（`ServantCentipedeHead:279`）再进 `calcOffsetVec`（`ServantCentipedeBody:275-277`），所以**朝向和位置是一起动的**。

AM 原版写法（我们逐字照搬）：

```java
if (this.updateRingBuffer() || this.ringBufferIndex < 0) {   // lengthSqr() >= 0.005
    ++this.ringBufferIndex;
}
if (this.ringBufferIndex == this.ringBuffer.length) { this.ringBufferIndex = 0; }
this.ringBuffer[this.ringBufferIndex] = this.getYRot();      // ← 在 if 外：每 tick 都覆盖
```

**症状**：静止时头还在转（跟着主人走、到了跟前停下、主人擦身而过导致原地转向）→ 恢复移动后约 4 tick，节段朝向/位置猛跳一次、下一 tick 跳回；此后每 4 tick 往尾节传一节，像波浪。抖幅 ≈ 静止期间头转过的总角度。纯静止不转头、或一直在走，都不触发 —— 所以是「有时候」。

**根因不是「某个槽被写脏」，是采样断层**：buffer 本质是「写入时刻的头朝向序列」。停顿期间索引冻住、同一个槽被反复覆盖 30 次 → 末值 = 静止结束时的朝向；恢复后第一个写入槽又是新朝向。缝夹在「暂停前最后一个写入的槽」与「恢复后第一个写入的槽」之间，两槽相差整个原地转角。**把 `R[idx] = getYRot()` 挪进 if 里只把缝往后挪一格、幅度不变**（已按 3°/tick、静止 30 tick 推演：现状槽8=24°/槽9=117°/槽10=120°；挪进去变成 24°/27°/120°，跳变从节段0 挪到节段1）。**只要停顿期间索引不推进，缝必然存在。**

**唯一解**：索引无条件每 tick 推进 + 每 tick 写 → buffer 成为连续时间序列，节段恒读 `yRot(t - 4 - 4i)`，不存在任何一格突变。代价：原地转身时身体会跟着头绕（丢掉「爬痕」语义 —— 不再「没走路就不长新足迹」）。2026-10-10 试过这个改法（`updateRingBuffer()` 随之成死方法删掉、编译构建部署都做了），**用户当场要求回退** —— 这个代价比抖动更不可接受。**现状 = AM 原样，缝仍在**；别自作主张再提这个改法，要动先问。

**Why:** 用户报「蜈蚣体节为什么有时候会突然抖动」。AM 的野生蜈蚣是游荡怪、几乎不停，我们的蜈蚣是仆从、一直在停-走-停，把这个缝引爆了。**不是移植抄漏** —— diff 过 AM 的 `EntityCentipedeHead`，ring buffer 那段逐字一致（`limitAngle` 也排除过：它是 AM `IHurtableMultipart` 的 default 方法，平滑 slew 不是跳变）。

**How to apply:** 见到「环状缓冲 + 有门控的索引推进 + 无条件写槽」这种结构，先算「暂停期间这个槽被覆盖了几次」—— 缝一定落在时间断层处，且**改写入位置没用，只有让采样在时间上连续才有效**。相关：[[mc-1201-client-interp-sync-order]]、[[forge-bare-entity-update-interval-smoothness]]。
