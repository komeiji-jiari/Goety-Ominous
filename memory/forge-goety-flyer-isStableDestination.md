---
name: forge-goety-flyer-isstabledestination
description: Goety 飞行仆从吃不到 CommandFocus/CommandHorn 方块命令 — FlyingPathNavigation.isStableDestination 会把命令格判为不可达
metadata: 
  node_type: memory
  type: project
  originSessionId: 9e0e2e2d-e4ee-4e2f-9e3d-a9614bbab85d
  modified: 2026-09-06T13:21:23.203Z
---

飞行仆从(RodlingServant 等,导航是 `FlyingPathNavigation`)无法执行 Goety 的"移动到方块"命令(CommandFocus/OrderFocus/CommandHorn 右键方块),即使已成功绑定。

**根因链**:Goety 下达方块命令时 `IServant.setCommandPos(点击格子上方的非实心空气格)`;每 tick `IServant.commandMode()` 用 `owned.getNavigation().isStableDestination(commandPos)` 当入口闸门。地面导航继承基类 `PathNavigation.isStableDestination`(检查 `pos.below()` 是否实心)→ 命令格下方是实心地面 → 通过;但 `FlyingPathNavigation` 重写为检查**命令格本身** `entityCanStandOn` → 空气格恒 false → `commandMode()` 走 else 分支同 tick 清掉命令 → 仆从毫无反应。

**How to apply**:凡 `createNavigation` 返回裸 `new FlyingPathNavigation(...)` 的 Goety 飞行仆从,都要像 Goety 自家 Haunt/AbstractReaper(以及本 repo 的 TeletorServant/DeepOneMageServant)那样装匿名子类重写:
```java
new FlyingPathNavigation(this, level) {
    @Override public boolean isStableDestination(BlockPos pos) {
        return !this.level.getBlockState(pos.below()).isAir();
    }
};
```
只修了 RodlingServant;FarseerServant/MurmurServantHead(裸 FlyingPathNavigation)大概率同病,未改。关联 [[ac-compat-porting-notes]] [[goety-summon-idle-freeze]]。
