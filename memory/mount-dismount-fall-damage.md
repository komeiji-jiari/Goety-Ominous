---
name: mount-dismount-fall-damage
description: 高个坐骑下马摔伤的成因与固定解法——覆写 getDismountLocationForPassenger 返回 bb.minY
metadata: 
  node_type: memory
  type: reference
  originSessionId: a9f70623-c2a7-4c43-acb3-6a864f57af83
  modified: 2026-10-05T19:45:14.552Z
---

下马时的落点是 `Entity.getDismountLocationForPassenger(LivingEntity)`，默认 **`new Vec3(getX(), getBoundingBox().maxY, getZ())`**——把乘客扔到坐骑头顶。调用链：`LivingEntity.stopRiding()` →（仅服务端）`dismountVehicle(vehicle)` → `vehicle.getDismountLocationForPassenger(this)` → `dismountTo()` → `teleportTo()`。

骑乘期间 `LivingEntity.rideTick()` 每 tick `resetFallDistance()`，所以摔伤完全由这次传送的高度决定：坐骑 bbHeight 超过 3 就必掉血（`ceil(fallDistance - 3.0)`）。原版坐骑最高（Ravager 2.5 / Camel 2.375）都摸不到 3.0，所以原版没这毛病；本项目的高个坐骑（灾厄大象 3.75 等）会中招。

**How to apply:** 加这一个覆写即可，本仓库 10 个高个坐骑统一用这版：

```java
@Override
public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
    return new Vec3(this.getX(), this.getBoundingBox().minY, this.getZ());
}
```

落点用坐骑自脚下 → 乘客零距离落地；XZ 取坐骑中心是有意的（坐骑站着的地方一定不是实心方块，往侧面挪反而可能塞进墙里）。参照 `CandicornServant` / `TremorzillaServant` / `AtlatitanServant` 等。注意别和 [[goety-mount-immobile-anim-root]] / [[forge-ridden-mob-water-speed]] 那套骑乘逻辑混着改。

**Why:** 这是原版留的正规钩子，比用 `LivingFallEvent` 兜（如 [[mob-tickheadturn-bodyrot-snap]] 提到的 Warg 那种）更干净——Warg 那套只在「还在骑」时生效，乘客落地时 `getVehicle()` 已是 null，拦不到下马这一摔。
