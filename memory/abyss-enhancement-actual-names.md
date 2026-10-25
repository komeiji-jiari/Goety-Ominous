---
name: abyss-enhancement-actual-names
description: abyss 加强清单里 14 个法术的实际类名/包名核实结果，以及 key_of_rlyeh 无法用 rightStaff 区分的关键结论
metadata: 
  node_type: memory
  type: reference
  originSessionId: 636e9543-5e18-4936-a377-d96b89787536
---

# abyss 加强清单：实际类名核实（2026-09-26）

用户给的是简化名，这里是**在 `.disasm/goety_src/` 里逐一核实过**的实际类名。**动工前不要再猜。**

| 用户写法 | 实际类名 | 包 | 备注 |
|---|---|---|---|
| rottenspell | **`ZombieSpell`** | `necromancy/` | ⚠️ 名不符。Goety 里**没有** `RottenSpell`。`ZombieSpell` 是唯一召唤 `DrownedServant` 的，ABYSS 杖走溺尸变体 |
| skeletonspell | `SkeletonSpell` | `necromancy/` | ✓ |
| huntingspell | `HuntingSpell` | `wild/` | ✓ |
| maulingspell | `MaulingSpell` | `wild/` | ✓ |
| slimyspell | `SlimySpell` | `wild/` | ✓ |
| guardianspell | `GuardianSpell` | `abyss/` | ✓ |
| biominespell | `BioMineSpell` | `abyss/` | ✓ |
| prismabeamspell | `PrismaBeamSpell` | `abyss/` | ✓ |
| steamingspell | **`SteamSpell`** | `abyss/` | ⚠️ 没有 `SteamingSpell` |
| tidalspell | `TidalSpell` | `abyss/` | ✓ |
| bouncybubblespell | `BouncyBubbleSpell` | `abyss/` | ✓ |
| tridentstormspell | `TridentStormSpell` | `abyss/` | ✓ |
| waterwhipspell | **`GulfTentacleSpell`** | `abyss/` | ⚠️ 没有 `WaterWhipSpell`。配置键叫 `WaterWhipCost/Duration/CoolDown`，focus 叫 `water_whip_focus` |
| waterjetspell | `WaterJetSpell` | `abyss/` | ✓ |

## 关键结论：`rightStaff()` 无法区分 key_of_rlyeh 与原版 abyss_staff

所有相关法术的 `rightStaff(staff)` 最终都是查 `IWand.getSpellTypes().contains(SpellType.ABYSS)`。
而 `key_of_rlyeh` **就是一把 ABYSS 类型的 DarkStaff** → `rightStaff()` 对它返回 **true**。

**后果**：拿 key_of_rlyeh 施法时，原版 abyssstaff 的加强分支**已经生效**（数量 2、+4 伤害等）。
- 验证方法：改 `rightStaff()` 为恒 false，看 key_of_rlyeh 是否还触发加强（若仍触发，说明确实走了 super 的 ABYSS 判定）
- 因此加强要么**替换**该分支内的常量，要么**额外加一层** `staff.is(ModItems.KEY_OF_RLYEH)`

## 原版两种加强模式（照抄用）

**① 数量类（TidalSpell:85）**
```java
int h = this.rightStaff(staff) ? 1 : 0;
for (int i = -h; i <= h; ++i) { ... }   // h=1 → 3 道，h=0 → 1 道
```

**② 追加类（BouncyBubbleSpell:68）**
```java
worldIn.addFreshEntity(bouncyBubble);       // 先发 1 个
if (this.rightStaff(staff)) {
    for (int i = 0; i < 2; ++i) { ... }     // 再补 2 个 → 共 3 个
}
```

## 相关

- [[abyss-staff-enhancement-plan]] —— 14 条需求的数值清单
- [[key-of-rlyeh-staff]] —— 这把杖本身

---

# 加强的写法（用户 2026-09-26 亲自确认）

**核心原则：key_of_rlyeh 的加强是「在 rightStaff 已有效果之上再追加一层」，不是替换。**
`rightStaff()` 不用改（它对 key_of_rlyeh 本来就返回 true，原版加成已生效）。

举例（用户原话）：

- 召唤类：rightStaff 给 2 个 → key_of_rlyeh **再 +1** = 3 个
- `BouncyBubbleSpell`：rightStaff 发 2 个 → key_of_rlyeh **再多发 1 个**
- `TridentStormSpell`：rightStaff 已给范围扩展 → key_of_rlyeh **只追加伤害**，数量不变

## 具体改法（已验证的模板）

**① TidalSpell 型（改 h 值）** —— `.disasm/.../abyss/TidalSpell.java` 第 85 行：

```java
int h = this.rightStaff(staff) ? 1 : 0;   // 原版：1 → 3 道
// 改成 key_of_rlyeh 时 h = 2 → 5 道（-20/-10/0/+10/+20），for 循环不用动
```

**② BouncyBubbleSpell 型（追加循环）** —— 第 68 行的 `if (this.rightStaff(staff)) { for (int i = 0; i < 2; ++i) {...} }`
之后再加一层 `if (staff.is(ModItems.KEY_OF_RLYEH.get())) { for (int i = 0; i < 1; ++i) {...} }`

## 待确认

`BouncyBubbleSpell` 的"3 个"到底是**总计 3** 还是**在 rightStaff 的 3 个之上再 +1（共 4）**？
用户原话"原模组的 rightstaff 为 2 个，我们这里再加一个"——原版是"先发 1 + rightStaff 补 2 = 3 个"，那"+1"应理解为**共 4 个**，但用户写的是"改为召唤 3 个"。需当面确认。

