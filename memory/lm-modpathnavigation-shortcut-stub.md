---
name: lm-modpathnavigation-shortcut-stub
description: "LM 的 ModPathNavigation.followThePath 每次把路径索引跳到同层最远节点(swep 是半成品空壳),移植成仆从后表现为游荡时斜着走/来回拐"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 995316c9-270b-43a6-a469-3c60550318ea
  modified: 2026-09-25T17:54:51.231Z
---

LM 的 `ModPathNavigation.followThePath()` 覆写了原版逻辑：先算出同层(与 mob 当前 y 层相同的)最后一个节点 `pathLength`，再 `tryShortcut` 从 `pathLength-1` 往回找第一个"视线通畅"的节点并把 `nextNodeIndex` 跳过去。而它调用的 `sweep()`（体素 DDA）**是半成品**：`tr/ldi/tri/step/tDelta/tNext/normed` 全部算完就 `return true`，从不判定地形，等于"永远通畅"。

后果：只要同层还剩 ≥2 个节点，`nextNodeIndex` 每 tick 都被跳到同层最远点，`PathNavigation.tick()` 随后把 MoveControl 指向那个远点 → mob 直线斜插，把绕障碍的路径整条无视；撞墙后 `doStuckDetection` 重算路径再直插 → "路线是斜的/来回拐"。

**Why:** LM 原版傀儡是 boss，行为就是朝玩家直冲，没人会发现；移植成会游荡的 Goety 仆从后缺陷才暴露。这属于依赖内部行为，`grep 仓库` 看不出来。

**How to apply:** 判定手法（不用反编译）：`unzip -p <LM jar> net/miauczel/legendary_monsters/entity/ai/navigation/ModPathNavigation.class | grep -c BlockState` → 0 就说明它不做方块查询，sweep 必是空壳；编译依赖 jar 和运行中的 2.2.2 都为 0。修法是仓库侧写 `ServantPathNavigation extends ModPathNavigation` 只覆写 `followThePath` 回原版逐节点推进（`maxDistanceToWaypoint` / `path.getNextNodePos()` / `path.advance()` / `doStuckDetection` 都是 protected 可访问；原版 OR 分支里的 `canMoveDirectly` 是 private，无法调用，略去即可），并在 `IAnimatedMonsterServant.createNavigation` 换掉实例。保留 LM 的 `ModPathfinder`：它把 `getEntityPosAtNode` 改成节点中心（原版是 `(int)(bbWidth+1)*0.5` 的角偏移），换掉反而会变。

相关：[[forge-followthepath-bbwidth-ulp-freeze]]、[[goety-summon-idle-freeze]]、[[lm-mechanic-presence-version-check]]、[[goety-ac-dinosaur-spirit-port]]
