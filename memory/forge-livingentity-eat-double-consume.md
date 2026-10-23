---
name: forge-livingentity-eat-double-consume
description: "1.20.1 LivingEntity.eat() 自己会 shrink(1);喂食逻辑里手动 shrink + eat() 会一次扣 2 个物品,Goety 全套喂食模板都带这个 bug"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 0d1ac7eb-dd47-4947-be68-37a12dabd63d
  modified: 2026-09-23T15:51:48.884Z
---

1.20.1 的 `LivingEntity.eat(Level, ItemStack)` 内部自己扣物品：

```java
if (!(this instanceof Player) || !((Player)this).getAbilities().instabuild) {
   p_21068_.shrink(1);
}
```

对 Mob 来说 `this instanceof Player` 恒为 false，条件短路成 true → **无条件再扣 1 个**。所以「手动 `itemstack.shrink(1)` + `this.eat(...)`」的写法一次喂食扣 2 个。

原版喂生物的正确惯例是 `Animal.usePlayerItem(player, hand, stack)`（就是 `if (!instabuild) shrink(1)`），**从不调 eat()**。`eat()` 是给玩家吃东西用的（`Player.eat` 覆写里才有 `getFoodData().eat(...)`）。对 Mob 调 eat() 几乎没有收益：音效走 `ItemStack.getEatingSound()` → `Item.getEatingSound()` 默认返回 null，播不出声；只剩 `addEatEffect` 应用食物自带药水效果。

**Goety 上游自己就有两派写法**，同一份模板：

- 写错（shrink + eat，双扣）：`Gnasher`、`GuardianServant`、`BearServant`、`BlackWolf`、`HoglinServant`
- 写对（只 shrink）：`Whisperer` / `Wavewhisperer` / `Leapleaf`（骨粉，`heal(5.0f)`）

两派的主人判定、`shrink`、7 颗 `HEAL_EFFECT` 粒子、`swing`、返回 `CONSUME` 全都一样，**唯一差别就是多不多那一句 `this.eat(...)`** —— 同一个作者复制粘贴，一遍写对一遍写错。

本仓库 2026-09-23 已修 6 处（删掉 `this.eat(...)`，回血量不变）：`HullbreakerServant`、`TerrorServant`、`DeepOneServant`、`DeepOneKnightServant`、`DeepOneMageServant`、`DropBearServant`、`EmuServant`。写对的样板见 `ThrasherServant.java:239`（`heal(2.0F)` + 手动 shrink，无 eat）。

Goety 上游那 5 处改不了（依赖 mod 的 jar）。以后从 Goety 抄 mobInteract 喂食分支时，对照 `ThrasherServant` 而不是 `Gnasher`。同类「照抄上游时多/少一行」的坑见 [[ac-goety-port-probability-gates]]；上游行为该不该照搬的判断见 [[ac-servant-parity-keep-original]]。
