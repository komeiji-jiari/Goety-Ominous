---
name: forge-bypass-intermediate-tick-basetick
description: "子类想跳过中间父类的 tick() 覆写又不想重写实体基础逻辑时,用 this.baseTick()——Entity.tick() 就是 baseTick() 的壳"
metadata: 
  node_type: memory
  type: reference
  originSessionId: f9629217-d45e-4fb1-9e1f-138a63788b4a
  modified: 2026-09-13T15:00:18.922Z
---

`Entity.tick()` 在 1.20.1 的全部实现就是 `{ this.baseTick(); }`（`Entity.java:421`），而 `baseTick()` 是 **public** 的（`Entity.java:425`，`LivingEntity`/`Mob` 的覆写也是 public）。所以当你想继承 A，但 A 的 `tick()` 里有你不想要的行为时，Java 没有 `super.super.tick()`，正确解法是：

```java
@Override
public void tick() {
    if (需要自定义) {
        this.baseTick();
        自定义逻辑();
    } else {
        super.tick();
    }
}
```

`baseTick()` 负责 tickCount++、火焰/传送门状态、载具簿记等实体基础维护。

实例：`DinosaurSpiritServant extends DinosaurSpiritEntity`（AC 的恐龙幽灵）。AC 的 `tickGrottoceratops` 会在主人没举灭绝长矛时把幽灵置为 fading——直接 `super.tick()` 会让护卫盾瞬灭，但整段重写又会丢掉另外两种幽灵的行为。用 `this.baseTick()` 分流后，只有角龙走自写逻辑，翼龙/霸王龙原样走 `super.tick()`。

判据：先确认中间父类**没有**覆写 `baseTick()`，否则 `baseTick()` 也会带上你不想要的行为。AC 的 `DinosaurSpiritEntity` 只覆写 `tick()`，没碰 `baseTick()`。

相关：[[goety-ac-dinosaur-spirit-port]]
