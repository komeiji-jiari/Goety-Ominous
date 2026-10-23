---
name: vanilla-goal-cancontinue-default
description: 1.20.1 Goal.canContinueToUse() 默认 return canUse() —— 运行中的 goal 每 tick 重跑 canUse；覆写成恒 true 会永久占着 Flag
metadata: 
  node_type: memory
  type: reference
  originSessionId: 3cef97c8-4947-456b-ae5a-ded0755aeeea
  modified: 2026-10-08T18:39:08.779Z
---

1.20.1 原版 `net.minecraft.world.entity.ai.goal.Goal`：

```java
public boolean canContinueToUse() { return this.canUse(); }
```

不是恒 `true`。所以**运行中的 goal 每 tick 会经 canContinueToUse 重跑一遍 canUse**：canUse 变为 false（冷却中、目标消失）时它会自己停。推论：

- 只用 canUse 做门控的 goal（例如 AM `AITackleMelee`：`shouldSwoop()` = 有目标且 tackleCooldown==0）**不需要**补 canContinueToUse，攻击后靠 cooldown 自然退出，不会永久锁死 MOVE/LOOK。
- 反过来，覆写 canContinueToUse 且恒返回 true 的 goal 会一直跑下去，持续占着它的 `Goal.Flag`，压死所有同 flag 的 goal。
- 只有 canContinueToUse 与 canUse 语义**不同**时才需要覆写（AM `AIFlyRandom` 就是这种）。

查证方式（别猜）：CFR 反编译 forge mapped jar

```bash
bash .scratch/cfrrun.sh "build/fg_cache/net/minecraftforge/forge/1.20.1-47.3.22_mapped_official_1.20.1/forge-1.20.1-47.3.22_mapped_official_1.20.1.jar" \
  --outputdir .scratch/vanilladec net.minecraft.world.entity.ai.goal.Goal
```

相关：[[srg-to-mojmap-tsrg-lookup]]、[[goety-servant-goal-priority-flags]]
