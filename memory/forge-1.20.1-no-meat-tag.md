---
name: forge-1-20-1-no-meat-tag
description: "1.20.1 没有肉 tag(只有 minecraft:fishes),写 \"吃肉\" 判定只能 isEdible()+FoodProperties.isMeat();且 Summoned 无 isFood 不能加 @Override"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 387ef423-e4f2-4814-b644-3a15178ef504
  modified: 2026-09-24T09:58:54.647Z
---

**1.20.1 不存在肉 tag**。查证过:`data/minecraft/tags/items/` 里只有 `fishes.json`,没有 `meat.json`;Forge 47.2.0 的 `net/minecraftforge/common/Tags$Items.class` 常量池里既没有 `FOODS_*` 字段也没有 `foods/*` 或 `*_meat` 标签名(对比通用标签 `armors/boots`、`crops/wheat` 是有的)。所以"照 tag 写喂肉"在这版做不到,通通走 FoodProperties。

**"吃肉"判定的唯一写法**(Goety `BlackBeast` 也就是"暗兽"就是这么写的,`build/cfrgoety9/.../ally/BlackBeast.java:889`):

```java
public boolean isFood(ItemStack stack) {
    if (!stack.getItem().isEdible()) {
        return false;
    }
    FoodProperties foodProperties = stack.getFoodProperties(this);
    return foodProperties != null && foodProperties.isMeat();
}
```

仓库里已有同款:`ForsakenServant:426`(null-safe 版,照抄这个)、`DropBearServant:249`(裸链 `getFoodProperties(this).isMeat()`,有 NPE 面)。

**坑**:`Summoned`(以及 `AnimalSummon`)本身**没有** `isFood(ItemStack)`,给它下面加 `isFood` 不能写 `@Override`,否则编译报"方法不会覆盖或实现超类型的方法"。只有 `Warg extends BlackWolf`(Goety 的,Horse/Animal 那条链)才能带 `@Override`。判断口径:`DropBearServant`/`ForsakenServant`/`SkyvernServant` 都是裸方法。

**别抄暗兽喂食的另一半**:它 `itemstack.shrink(1)` 之后还调了 `this.eat(level, itemstack)`,一次喂食扣 2 个,见 [[forge-livingentity-eat-double-consume]]。

2026-09-24 给 `SkyvernServant` 按此加过喂食(肉 + 固定回 5 血,粒子用 HEART,见 [[goetyominous-feed-particle-heart]])。
