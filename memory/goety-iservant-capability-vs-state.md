---
name: goety-iservant-capability-vs-state
description: "Goety IServant 的 canStay/canWander/canGuardArea/canFollow 是\"能力位\"不是状态；拿去当状态判定会编译报错"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 3cef97c8-4947-456b-ae5a-ded0755aeeea
  modified: 2026-10-08T19:10:56.012Z
---

`IServant` 里这几个 `default public boolean` 全是**能力位**（这个物种允不允许有这种模式），永不是"当前处于该状态"：

```java
default public boolean canStay()      { return true; }
default public boolean canWander()    { ... }
default public boolean canGuardArea() { return true; }
default public boolean canFollow()    { return true; }
```

**当前状态**的判定在 `Summoned` / `IServant` 上，名字是 `is` 系：

```java
isWandering()    = getFlag(1)
isStaying()      = getFlag(2) && !isCommanded() && getControllingPassenger() == null
isGuardingArea() = getBoundPos() != null
isCommanded()    = getCommandPos() != null
isFollowing()    = !isWandering() && !isStaying() && !isGuardingArea() && canFollow()
```

坑：在仆从实体里写 `private boolean canStay() { return this.isStaying() && ...; }` 会直接编译失败 —— 接口 default 方法可见性 public，私有同签名方法**既不构成覆写也不够可见**，报「canStay() 无法实现 IServant 中的 canStay()」。状态谓词要换名（本项目灵魂鹫用的 `canStandby()`）。

已踩：2026-10-08 灵魂鹫待命落地。

相关：[[goety-servant-follow-standby]]、[[soul-vulture-servant-port]]
