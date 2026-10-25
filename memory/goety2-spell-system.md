---
name: goety2-spell-system
description: Goety 2.5.58.2 法术基座 ISpell/Spell/SummonSpell 的契约、生命周期、模板与注册方式——写新法术照着这个来
metadata: 
  node_type: memory
  type: reference
  originSessionId: aa83982d-2283-4121-a0ca-337b4239c3a9
---

源码见 [[goety2-src-decompile]]（`E:\java\.disasm\goety_src\com\Polarice3\Goety\`）。版本 **Goety 2.5.58.2 / MC 1.20.1**。

## 1. 契约：谁是抽象方法

```
ISpell (api\magic\ISpell.java)          ← 绝大多数逻辑都是 default 方法
 └ Spell (common\magic\Spell.java)      ← 只把 3 个方法重新声明为 abstract
    ├ SummonSpell implements ISummonSpell   （+ 抽象 SummonDownDuration()）
    ├ ChargingSpell implements IChargingSpell（+ 抽象 Cooldown()）
    │  └ EverChargeSpell  └ BreathingSpell implements IBreathingSpell
    ├ TouchSpell implements ITouchSpell
    └ BlockSpell implements IBlockSpell
外挂：`Spell implements ITouchSpell`（`RecallSpell`）、`BlockSpell implements ITouchSpell`（`IgniteSpell`）都可以。
```

**`ISpell` 的 5 个抽象方法：** `defaultSoulCost()`、`defaultCastDuration()`、`defaultSpellCooldown()`、`getSpellType()`、`acceptedEnchantments()`。
**`Spell` 只保留 3 个抽象：** `defaultSoulCost()` / `defaultCastDuration()` / `defaultSpellCooldown()`；`getSpellType()` 在 `Spell` 里是**具体方法返回 `SpellType.NONE`**，`acceptedEnchantments()` 返回空 `ArrayList`。

⚠️ 忘了覆写 `getSpellType()` → 静默变 `SpellType.NONE`，`typeStaff` / `rightStaff` / 灵魂折扣表 / 法杖粒子全废。
⚠️ `acceptedEnchantments()` 返回空列表 = **该 focus 无法附任何魔**（`MagicFocus.canApplyAtEnchantingTable` 按这个白名单拒绝）。

## 2. `Spell` 提供的具体实现（大概率要用的）

```java
public void mobSpellResult(LivingEntity caster, ItemStack staff)          // -> 带 WandUtil.getStats
public void mobSpellResult(LivingEntity caster, ItemStack staff, SpellStat)
public void serverCheckSpellResult(Level, LivingEntity, ItemStack, SpellStat)  // level instanceof ServerLevel 才转
public boolean conditionsMet(Level, LivingEntity)   // ★ 客户端直接 return false
public LivingEntity getTarget(LivingEntity caster)                 // 用 defaultStats().getRange()
public LivingEntity getTarget(LivingEntity caster, int range)      // ⚠️ 想按 SpellStat 缩放射程必须用这个重载
public boolean rightStaff(ItemStack staff)          // 法杖"对口"本法术类型 → 通常给增强
public boolean typeStaff(ItemStack staff, SpellType) // 法杖是否属于某类型 → 用来选变体
public boolean GeoPower(LivingEntity)               // = CuriosFinder.hasCurio(caster, AMETHYST_NECKLACE)
public boolean isShifting(LivingEntity)             // 潜行 + 手持魔杖
protected HitResult rayTraceCollide(Level, LivingEntity, int range, double radius)
protected EntityHitResult entityCollideResult(Level, LivingEntity, int range, double radius)  // 要求视线
```

## 3. 生命周期（谁在什么时候调）

**玩家路径（`DarkWand` 驱动）：**

```
右键魔杖
├─ getSpell == null / cannotCast（= 冷却 || !spell.conditionsMet(serverLevel, caster)）→ 不施法
├─ castDuration(player, staff) > 0 → startUsingItem 进入蓄力
└─ 否则瞬时法术 → SpellResult

蓄力中每 tick（DarkWand.onUseTick）：
├─ CastTime == 1 : startSpell(serverLevel, caster, staff, WandUtil.getStats(caster, spell))   ← 仅服务端
├─ 每 tick      : useSpell(serverLevel, caster, staff, CastTime, stats)                        ← 仅服务端
├─ 粒子         : spell.useParticle(worldIn, caster, staff)                                    ← 双端！
└─ IChargingSpell 且 CastTime >= castUp(...) → 每 Cooldown(...) tick 走一次 SpellResult

松手 / 蓄力结束（DarkWand.releaseUsing）：
└─ stopSpell(serverLevel, caster, staff, focus, CastTime, stats)   ← 仅服务端
```

**生物路径：** 生物 AI 直接调 `spell.mobSpellResult(caster, staff[, stat])` —— 它内部已经做了 `serverCheckSpellResult` 的服务端判定。⚠️ 生物**不要**直接调 `SpellResult`（客户端会崩）。

**纯服务端的 hook：** `startSpell`、`useSpell`、`SpellResult`、`stopSpell`、`conditionsMet(ServerLevel,...)`。
**双端：** `useParticle`（⚠️ 必须自己 `if (worldIn instanceof ServerLevel ...)` 守卫）、`soulCost`、`castDuration`、`spellCooldown`、`defaultStats`。
**纯客户端：** `getPose`（`@OnlyIn(Dist.CLIENT)`）、`IBreathingSpell.showWandBreath`、`loopSound`。

⚠️ `Spell.conditionsMet(Level, LivingEntity)` 在客户端**恒为 false**，别在客户端调它然后期待 true。
⚠️ `conditionsMet` 会**每 tick**被 `IWand.cannotCast` 调一次；`SummonSpell` 的覆写里有 `worldIn.getAllEntities()` 全量扫描，自己覆写时别再加 O(n) 扫描。

## 4. `SpellStat`

`common\magic\SpellStat.java` —— 可变结构体（不是 record），setter 是流式的（改自己 + 返回 `this`）。

| 字段 | 类型 | 含义 |
|---|---|---|
| `potency` | int | 威力：伤害加成 / 效果等级 / 数量缩放 |
| `duration` | int | 时长，单位由法术自定（效果常按分钟，召唤按倍数） |
| `range` | int | 格，用于 rayTrace / 吐息索敌 / AoE |
| `radius` | double | 格，AoE / 爆炸 / 多格 |
| `burning` | int | 着火秒数倍数 |
| `velocity` | float | 投射物初速 |

- 默认：`ISpell.defaultStats() = new SpellStat(0, 0, 16, 0.0D, 0, 0.0F)`；`SummonSpell` 覆写为 `super.defaultStats().setDuration(1).setBurning(0)`。
- ⚠️⚠️ **`defaultStats()` 必须每次返回新实例**。`WandUtil.getStats(caster, spell)` 会在返回值上 `.increasePotency(...)` 等一串 `+=`，缓存/复用同一个实例会让属性加成每 tick 累加。

```java
public static SpellStat getStats(LivingEntity livingEntity, ISpell spell) {
   return spell.defaultStats()
      .increasePotency (ModAttributes.getPotency (livingEntity, spell))
      .increaseDuration(ModAttributes.getDuration(livingEntity))
      .increaseRange   (ModAttributes.getRange   (livingEntity))
      .increaseRadius  (ModAttributes.getRadius  (livingEntity))
      .increaseBurning (ModAttributes.getBurning (livingEntity))
      .increaseVelocity(ModAttributes.getVelocity(livingEntity));
}
```

- **附魔加成是法术自己在 `SpellResult` 里手动再加一遍的**（`getStats` 只加了属性，没加附魔）。这是几乎所有法术里重复出现的那段：

```java
int potency = spellStat.getPotency();
int duration = spellStat.getDuration();
if (WandUtil.enchantedFocus(caster)) {
   potency  += WandUtil.getPotencyLevel(caster);                                  // POTENCY
   duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
   range    += WandUtil.getRangeLevel(caster);                                    // RANGE，每级 +2
   radius   += WandUtil.getLevels(ModEnchantments.RADIUS.get(), caster);
   burning  += WandUtil.getLevels(ModEnchantments.BURNING.get(), caster);
   velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster) / 3.0F;
}
```

法术通过覆写 `defaultStats()` 定制基线，例：`IceSpikeSpell` `.setVelocity(1.6F)`、`FireBreathSpell` `.setRange(8).setBurning(1)`、`IlluminateSpell` `.setRadius(16.0D)`、`TelekinesisSpell` `.setPotency(1)`。

## 5. 最小法术模板

```java
public class MyBoltSpell extends Spell {

   @Override public SpellStat defaultStats()   { return super.defaultStats().setVelocity(1.6F); }  // ★ 必须新实例
   @Override public int defaultSoulCost()      { return 20; }
   @Override public int defaultCastDuration()  { return 20; }   // 0 = 瞬时，右键立刻 SpellResult
   @Override public int defaultSpellCooldown() { return 40; }
   @Override public SpellType getSpellType()   { return SpellType.FROST; }
   @Override public SoundEvent CastingSound()  { return ModSounds.MY_CAST.get(); }
   @Override public List<Enchantment> acceptedEnchantments() {
      return List.of(ModEnchantments.POTENCY.get(), ModEnchantments.VELOCITY.get());
   }

   @Override
   public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
      int potency = spellStat.getPotency();
      float velocity = spellStat.getVelocity();
      if (WandUtil.enchantedFocus(caster)) {
         potency  += WandUtil.getPotencyLevel(caster);
         velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster) / 3.0F;
      }
      if (this.rightStaff(staff)) velocity *= 1.5F;
      // ... spawn projectile ...
   }
}
```

## 6. 召唤法术模板（`SummonSpell`）

多一个抽象方法 `SummonDownDuration()`（每一次额外召唤叠加多少 tick 的 Summon Down 负面效果）。

```java
public class MyServantSpell extends SummonSpell {
   @Override public int defaultSoulCost()      { return 80; }
   @Override public int defaultCastDuration()  { return 30; }
   @Override public int defaultSpellCooldown() { return 60; }
   @Override public int SummonDownDuration()   { return 300; }
   @Override public SpellType getSpellType()   { return SpellType.NECROMANCY; }
   @Override public Predicate<LivingEntity> summonPredicate() { return l -> l instanceof MyServant; }
   @Override public int summonLimit() { return 16; }

   @Override
   public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
      this.commonResult(worldIn, caster);                 // ★ 潜行时 = 把已有仆从传送过来 + 特效
      int potency = spellStat.getPotency();
      int duration = spellStat.getDuration();
      if (WandUtil.enchantedFocus(caster)) {
         potency  += WandUtil.getPotencyLevel(caster);
         duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
      }
      if (this.isShifting(caster)) return;                // ★ 潜行只集合，不召唤
      int count = this.rightStaff(staff) ? 2 + worldIn.random.nextInt(4) : 1;

      for (int i = 0; i < count; ++i) {
         Summoned servant = new MyServant(ModEntityType.MY_SERVANT.get(), worldIn);
         BlockPos pos = BlockFinder.SummonRadius(caster.blockPosition(), servant, worldIn);
         if (caster.isUnderWater()) pos = BlockFinder.SummonWaterRadius(caster, worldIn);

         servant.setTrueOwner(caster);                       // 写 Owner UUID + OwnerClient id
         servant.moveTo(pos, 0.0F, 0.0F);
         MobUtil.moveDownToGround(servant);
         servant.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);   // 600~2380 tick × duration
         servant.setPersistenceRequired();
         servant.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(caster.blockPosition()),
                               MobSpawnType.MOB_SUMMONED, null, null);
         this.buffSummon(caster, servant, potency);          // potency>0 且无 SummonDown 时给永久 BUFF
         this.SummonSap(caster, servant);                    // 有 SummonDown 时削弱新仆从
         this.setTarget(caster, servant);
         if (worldIn.addFreshEntity(servant)) this.uponSummon(worldIn, caster, staff, servant);
         this.summonAdvancement(caster, servant);
      }
      this.SummonDown(caster);                               // 叠加 SummonDown（上限 amplifier 4）
   }
}
```

- `MobUtil.getSummonLifespan(Level)` = `20 * (30 + random.nextInt(90))` → 600~2380 tick。
- 变体选择用 `this.typeStaff(staff, SpellType.FROST)` 之类（见 `ZombieSpell`：寒霜法杖→`FrozenZombieServant`、风暴→`FrayedServant`、荒野→`JungleZombieServant`、下界→`ZPiglinServant`、深渊→`DrownedServant`）；也可以走 `entity.getVariant(player, level, pos).create(level)` 的统一变体接口。
- 数量：`NAMELESS_STAFF` → 7；`rightStaff(staff)` → `2 + rand(4)`；`specialStaffs(staff)` → 2；否则 1。
- `summonLimit()`（默认 64）+ `summonPredicate()`（默认 `l -> l instanceof IOwned`）在 `SummonSpell.conditionsMet` 里被消费：**达到上限就直接拒绝施法**（并给玩家显示 `info.goety.summon.limit`）——**除非潜行**，潜行时绕过上限检查只做集合。
- `SummonDown` 每次施法叠加（amplifier +1，钳到 4）；`ISpell.SoulCostUp(caster)` 在 SummonDown 生效时把灵魂消耗乘 `amplifier + 2`。

## 7. 注册方式 —— **没有法术注册表**

`common\magic\ModSpells.java` 只是个普通持有类，里面是 `public static final ISpell XXX = new XxxSpell();`。

法术进游戏靠**做成 focus 物品**再用 `DeferredRegister<Item>` 注册：

```java
public static final RegistryObject<Item> VEXING_FOCUS =
    ITEMS.register("vexing_focus", () -> new MagicFocus(ModSpells.VEXING));
```

`MagicFocus`（`common\items\magic\MagicFocus.java`）构造器 `MagicFocus(ISpell spell)`，把 spell 存成 **public 字段**，实现 `IFocus extends ISpellHolder`（`ISpell getSpell()`）。

**第三方模组就是要：**
1. 写自己的 `Spell` 子类；
2. 自己在持有类里留**唯一一个共享实例** `public static final ISpell MY_SPELL = new MySpell();`
3. 注册自己的物品：`DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "自己modid")`，`ITEMS.register("my_focus", () -> new MagicFocus(MySpells.MY_SPELL))`。
   —— `MagicFocus` 是 public 的、构造器 public，**不需要任何 Goety 侧注册**。
4. 可选：`new DarkWand(properties, SpellType.XXX)` 加自己的法杖（`DarkWand` 有多个 public 构造器）。

⚠️ **`SpellItemCache` 是按 identity 建索引的**（`IdentityHashMap<ISpell, List<Item>>`，懒扫描 `ForgeRegistries.ITEMS` 里的 `ISpellHolder`）。所以**同一个法术只能有一个实例**，focus 物品、配置、持有类字段都必须引用同一个对象；`new MySpell()` 两次会让 `SEHelper.addSpellCooldown` 对其中一个静默失效。

自定义 `SpellType`：走 Forge 的 `IExtensibleEnum`，`SpellType.create("mymagic", "My Magic")`，并自己补 lang key `spell.goety.mymagic`。⚠️ 但 `ISpell.SoulCalculation` 和 `ModAttributes` **硬编码了原生 SpellType 常量**，自定义类型拿不到灵魂折扣/加成分支，除非往 Goety 自己的 `init\ModAttributes.java` 的 `ATTRIBUTES` 里注册 `SpellAttribute.potency/discount(spellType, ...)`。

## 8. 各专用基类

```java
public abstract class ChargingSpell extends Spell implements IChargingSpell {
   public abstract int Cooldown();                    // 每发之间的间隔 tick
   public int defaultCastDuration()  { return 72000; }// 几乎永久蓄力
   public int defaultSpellCooldown() { return 0; }
}
public abstract class EverChargeSpell extends ChargingSpell { public int Cooldown(){return 0;} public boolean everCharge(){return true;} }
public abstract class BreathingSpell extends EverChargeSpell implements IBreathingSpell { }   // 不额外加东西
public abstract class TouchSpell extends Spell implements ITouchSpell { public int defaultCastDuration(){return 0;} }
public abstract class BlockSpell extends Spell implements IBlockSpell { public int defaultCastDuration(){return 0;} }
```

- `IChargingSpell` 关键：`Cooldown(caster, staff, shots)`（默认转 `Cooldown()`）、`everCharge()`、`defaultCastUp()` / `castUp(...)`（开始连发前的起手 tick）、`shotsNumber()`。
- `ITouchSpell`：`targetConditions(ServerLevel, caster, target, staff, stat)`（门）、`touchResult(ServerLevel, caster, target, staff, stat)`。
- `IBlockSpell`：**只用** `rightBlock(ServerLevel, caster, BlockPos, Direction, SpellStat)` 和 `blockResult(ServerLevel, caster, staff, BlockPos, Direction, SpellStat)`，其余重载都 `@Deprecated`。
- `IBreathingSpell`：`everCharge()` → true、**`useParticle` 覆写为空**、`getBreathTarget(...)`、`showWandBreath(caster, staff, stat)`（客户端）、`breathAttack(...)` / `dragonBreathAttack(...)` 辅助。

## 9. 坑（写新法术最容易踩的）

1. **`defaultStats()` 必须每次新实例**（见 §4）。
2. **灵魂/时长/冷却不是每帧算的** —— `DarkWand.setSpellConditions(...)` 在 `inventoryTick` 里把它们缓存进魔杖 NBT（`"Soul Cost"` / `"Duration"` / `"Cooldown"` / `"Soul Use"`）。
3. **`useParticle` 双端都会调**，必须自己守卫服务端。`Spell.useParticle` 已经按 `SpellType` 出了法杖粒子；想加东西就覆写并考虑先调 `super`。
4. **`DarkWand.MagicResults` 只处理 `caster instanceof Player`**，生物走不到那里；且 `DarkWand.canCastTouch` 开头是**无条件 `(Player) caster` 强转**，非玩家施放 touch/block 法术会 `ClassCastException`。想让生物用你的法术，`SpellResult` / `touchResult` 要写成与玩家无关。
5. **冷却：自己管就返回 `hasCustomCooldown(...) = true`**，并在 `SpellResult`/`stopSpell` 里用 `SEHelper.addSpellCooldown(player, this, ticks)` + `SEHelper.sendSEUpdatePacket(player)`。否则魔杖会自动加 `spellCooldown(caster)`，你再加一次就**翻倍**。`self.spellCooldown(caster)` 已经含 `ModAttributes.getCooldownDiscount`。
6. **`everCharge()` 的语义**：`true`（EverCharge/Breathing）时 `MagicResults` 每 20 次调用才扣一次灵魂（吐息型）；`false`（ChargingSpell）每一发都扣（机枪型）。
7. **`IChargingSpell` 的魔杖冷却来自 `Cooldown(...)`（NBT `"Cooldown"`）**，不是 `defaultSpellCooldown()`；后者只在 `releaseUsing` 里作为松手惩罚。
8. **瞬时法术** = `defaultCastDuration()` 返回 0，魔杖在 `use()` 里 `swing` 完直接进 `SpellResult`。
9. **`stopSpell` 收到的 focus 可能是 `ItemStack.EMPTY`**，加冷却前先 `!focus.isEmpty()`。
10. **`rightStaff` vs `typeStaff` 不是一回事**：`rightStaff(staff)` = 法杖与本法术类型**对口**（增强，如冰锥→冰矛 ×1.5 初速）；`typeStaff(staff, X)` = 法杖**是否属于 X 类型**（选变体，如寒霜法杖→`FrozenZombieServant`）。两者都查 `IWand.getSpellTypes()`，一根法杖可以有多个类型。
11. **`GeoPower` / `NecroPower` / `FrostPower` / `WildPower` / `NetherPower`**：`Spell.GeoPower` = 紫水晶项链；`SummonSpell` 把 `GeoPower` 改成"有地脉法袍"并新增 Necro/Frost/Wild/Nether（各种法袍）。覆写时想要基座行为记得调 `super`。`ModAttributes.getPotency(caster, spell)` 按 `getSpellTypes()` 找第一个匹配的类型属性，所以多类型法术覆写 `getSpellTypes()` 有意义。
12. **`conditionsMet` 每 tick 调**，别在里面做重活。
13. **`getTarget(caster)` 忽略你的 `SpellStat`** —— 它用 `defaultStats().getRange()`。要按 stat 缩放就用 `getTarget(caster, spellStat.getRange())`。
14. **原生法术的 cost/duration/cooldown 都来自 Forge 配置**（`config\SpellConfig.java`，`ConfigValue<Integer>` + `.get()`），第三方法术直接返回字面量或自己开配置都行。

相关：[[goety2-servant-system]]、[[goety2-src-decompile]]
