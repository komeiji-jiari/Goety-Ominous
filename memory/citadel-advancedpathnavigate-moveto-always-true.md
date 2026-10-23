---
name: citadel-advancedpathnavigate-moveto-always-true
description: "Citadel 的 AdvancedPathNavigate.moveTo(Entity,double) 无条件返回 true,判断\"能否走到目标\"必须用 moveToLivingEntity 拿 PathResult"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 70da2640-52af-49b9-9c72-c53f6c8705ed
  modified: 2026-09-13T12:13:14.903Z
---

Alex's Caves 的 `AdvancedPathNavigate`(Citadel raycoms 寻路)里,`moveTo(Entity, double)` 最终走 `tryMoveToBlockPos(...)`,反编译确认该私有方法 **无条件 `return true`**(ac 2.0.2 / citadel 2.6.3 行 315-318)。所以用 `getNavigation().moveTo(target, speed)` 的返回值当"可达性判定"永远是 true。

要判断可达,正确写法是走返回 `PathResult<?>` 的重载:

```java
if (!(this.mob.getNavigation() instanceof AdvancedPathNavigate navigate)) return true;
PathResult<?> result = navigate.moveToLivingEntity(target, 1.0D);
if (result == null || result.getPath() == null) return false;
Node node = result.getPath().getEndNode();
if (node == null) return false;
int i = node.x - target.getBlockX(), j = node.y - target.getBlockY(), k = node.z - target.getBlockZ();
return (double)(i * i + j * j + k * k) <= 3.0D;
```

`Node` 在 `net.minecraft.world.level.pathfinder.Node`,`x/y/z` 是 public 字段。样板见 `LuxtructosaurusServantMeleeGoal.canReach`(AC 原版 LuxtructosaurusMeleeGoal 的跳劈触发条件就靠这个,照搬 moveTo 的话跳跃动画永远不触发)。

相关:[[ac-goety-port-probability-gates]](同样是"逐行照搬反编译源码"才发现的问题)。
