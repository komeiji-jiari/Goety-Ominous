---
name: forge-positionrider-two-overloads
description: 覆写 positionRider 必须调双参 m_19956_;调单参 super.positionRider(p) 会虚分派回自己→StackOverflowError(服务端 tick 循环崩)
metadata: 
  node_type: memory
  type: reference
  originSessionId: 32403d76-827f-4982-aecf-dbf281e3ede4
  modified: 2026-10-04T16:29:05.120Z
---

1.20.1 的 `Entity` 有**两个** `positionRider`,而且低的那个会掉头回高的那个:

| SRG | 签名 | 实现 |
|---|---|---|
| `m_7332_` | `positionRider(Entity)` — 单参、**public** | `this.positionRider(pPassenger, Entity::setPos)` |
| `m_19956_` | `positionRider(Entity, MoveFunction)` — 双参、**protected** | `pCallback.accept(pPassenger, getX(), getY()+getPassengersRidingOffset()+pPassenger.getMyRidingOffset(), getZ())` |

**覆写的是双参那个。** 所以 `super.positionRider(passenger)`(单参)不是"走父类默认实现",而是**虚分派回到自己的覆写** → 每 tick 两个栈帧 → `StackOverflowError: Exception in server tick loop`,崩溃日志里会看到 `Entity.m_7332_(Entity.java:1874)` 与 `YourMob.m_19956_` 交替刷屏。

**正确写法**:永远把回调透传下去。
```java
@Override
public void positionRider(Entity passenger, Entity.MoveFunction function) {
    if (passenger instanceof Player) {
        super.positionRider(passenger, function);   // 双参,带上 function
        return;
    }
    function.accept(passenger, x, y, z);            // 自己摆位置也是用 function.accept
}
```
这个坑特别毒的地方:**老代码里那个 `else` 分支往往就写着单参 `super.positionRider(passenger)`**——它一直没炸,只因为那条分支现实中走不到(比如非 LivingEntity 乘客)。等你把玩家/新乘客类型接到那条路上,潜伏的递归立刻炸响,于是看起来像"新功能引入了崩溃",其实是旧地雷。

顺带两条:
- `startRiding(vehicle, pForce=true)` **绕过 `canAddPassenger`**(门是 `if (!pForce && (...!canAddPassenger(this)))`)。想靠 `canAddPassenger` 拦住"谁能被硬塞成乘客"是拦不住的,`GrabGoal` 这类强上马的地方一律 force=true。
- 验证覆写有没有真的生效(reobf 后):mod 自己定义的方法名保持字面量,覆写原版的方法名变成 SRG。`grep` 编译产物,若覆写的方法名还是字面量(如 `setTarget`)= reobf 没映射上 = 运行时根本没覆写。例:`setTarget` 覆写后应 grep 到 `m_6710_` 而 grep 不到 `setTarget`。

相关:[[forge-ridden-mob-water-speed]]、[[thrasher-servant-player-riding]]
