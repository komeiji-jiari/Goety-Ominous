---
name: cuotiben-tooling
description: 徒弟错题本·工具与流程篇：别信工具显示（Grep 的 //、Edit 的缩进）、中文 Windows javac 是 GBK、移植前先和原版同名类对比、伤害类型 tag、SRG 混淆名
metadata: 
  node_type: memory
  type: project
  originSessionId: 304245bf-c7a1-4c6f-8c54-9cc55d2f31af
---

来源：徒弟的「错题本.md」第四节。取法见 [[cuotiben-build-and-merge]]。

## 别信工具"显示"出来的东西

- **第 13 条**：Grep 带上下文时会把 `//` 渲染成 `\ `、行尾显示 `<\b>`，看起来像语法错误。**怀疑某行是语法错误就直接用 Read 读那一行确认**，别凭 Grep 下结论。⚠️ 这个坑**踩了两次**。
- **第 17 条**：Edit 报 `String to replace not found in file`，反复核对都觉得一模一样 → 真实缩进是 **5 个空格**，抄成了 1 个；javadoc 行（` * ...`）在 Read 输出里和真实空格数对不上，**肉眼数不出来**。**连续失败两次就别猜了**，dump 字符码：
  ```powershell
  $lines = [System.IO.File]::ReadAllLines($p)
  $lines[128].ToCharArray() | ForEach-Object { '{0:X4}' -f [int]$_ }
  ```
  `0020 0020 0020 0020 0020 002A` = 5 空格 + `*`。
- **第 26 条**：**中文 Windows 上 javac 输出 GBK**，日志按 UTF-8 读 → 「错误:」变乱码，搜 `error` 一条都搜不到（看着像"没错误但构建失败"）。而且 javac 的关键字本来就是**中文的「错误」**。读日志一律加 `-Encoding Default`，抓 `"符号:|位置:"`。⚠️「找不到符号」是**三行**，**光看第一行没用**（只知道哪行出错），「符号:」那行才告诉你编译器在找什么。
  > 注：本仓库是 **GBK 环境**，我在 Bash 里 `grep "error:"` 同样会漏 —— 用 `grep -a` 或直接看非 UTF-8 字节更稳。

## 动手前先花两分钟查

- **第 27 条：移植「别人自建的基类」之前，先和原版同名类逐字对比。** 很多 mod 的「自建基类」其实是**原版类复制粘贴改名**。差点白抄 200 行 `AbstractFlyingProjectile`，对比后发现和原版 `AbstractHurtingProjectile` **逐字一致**，只改了类名 + 4 处（`shouldBurn`→false、删拖尾烟、删入水气泡、删无用的 `getTrailParticle`）。
  - 差异**用手指头数得完**且集中在独立方法 → 直接继承原版 + 覆写那几个方法
  - 差异藏在**大方法中间**（如夹在 `tick()` 里） → 才需要抄整个类（这次正是这种）
- **第 28 条：自定义伤害类型有「看不见的另一半」。** 伤害类型 json 只管「死亡消息怎么写、难度怎么缩放」；**「会不会被护甲挡住」写在 tag 里**：`data/minecraft/tags/damage_type/bypasses_armor.json`。只搬 json 不搬 tag → 伤害**悄悄变低，什么都不报**。搬伤害类型时**顺手去对方 jar 里搜 `*tags/damage_type*`**，该进的 tag 一个都不能漏。
- **第 29 条：直接反编译 mod 的发布 jar，方法名是 SRG 混淆名**（`m_8119_()` / `m_7678_()`），抄不了。要抄代码就去解 **ForgeGradle 的官方映射 jar**：
  ```
  <gradle caches>\forge_gradle\minecraft_user_repo\net\minecraftforge\forge\
    1.20.1-47.3.22_mapped_official_1.20.1\
    forge-1.20.1-47.3.22_mapped_official_1.20.1-sources.jar
  ```
  ⚠️ 它是**反编译产物，方法上没有 `@Override`** —— 别以为「没写 @Override 就不是覆写」。⚠️ 同目录下还有 `47.4.10`、`parchment` 等版本，**认准 `mapped_official`**。这也是第 24 条（凭记忆写 API）的正解。见 [[srg-axis-mapping]]。

- **第 14 条**：**别再提议「倒放某段原版动画当过渡」这个套路。** 倒放拼出来的过渡，起点是原版末帧、终点强行对齐另一段动画第 0 帧，而原版这两帧本来就差 7.5 度（右腿），接缝骗不过眼睛。

- **第 30 条：同一个方块别挂两个 `BlockItem`——`Block.asItem()` 会被打乱。** `Item.BY_BLOCK` 是 Block→Item 的**一对一**映射，而创造页插入原版页签、从自定义页签剔除，走的都是 `asItem()` → 两个物品只有一个能被"认领"，另一个必然错位（实测现象：一个物品赖在自定义页签没被剔除、另一个进不了原版页签、搜索也搜不到）。要做变体物品就走**单物品**（由放置上下文决定属性），或者干脆不做——成熟先例 irons（vault/trial_spawner）就是**一方块一物品**，动手前先看它怎么做。
- **第 31 条：1.20.1 的方块渲染层写在模型 JSON 里（`"render_type": "minecraft:cutout"`），不是 `setRenderLayer`。** 有透明贴图的方块（宝库那种镂空笼子）漏了这行 → 透明像素按不透明渲染 → **整块全黑**、BER 的内部物品也被挡住看不见。佐证：irons 全仓库零 `setRenderLayer`、模板 JSON 里写 `render_type` → 照抄即修（写在共享 parent 模板上，子模型一次全生效）。

## 第 32 条：mixin 的 `@Shadow` 目标，`gradlew compileJava` 不校验

2026-10-08 实锤（试炼刷怪笼的 `SpawnEggItemMixin`）：我写了 `@Shadow public abstract EntityType<?> getType(ItemStack stack);`，**`gradlew compileJava` 一路 BUILD SUCCESSFUL**，但 IntelliJ 的 mixin 插件报 `Cannot find target for @Shadow method` —— 因为 1.20.1 的 `SpawnEggItem.getType` 收的是 **`CompoundTag`**（1.21 才改成 `ItemStack`）。注解处理器默认不校验 @Shadow 目标，**运行时才抛 "Shadow method was not found" 直接崩**。

- **教训**：凡是我写/改 mixin，`@Shadow`/`@Inject` 的目标**必须先用 javap 对着实际类核签名**（`javap -cp <mapped forge jar> <目标类>`），别拿"编译过了"当"mixin 没问题"。
- 同类坑还有：`@Inject(method="xxx")` 的**方法名**在 1.20.1 与 1.21 不同（改名/改参数都会让注入落空，`require=1` 才在加载时报错，`require=0` 是静默失效）。
- 查法：`javap -cp "$J" <类> | grep -n <方法名>`；字节码层面确认行为用 `javap -c -p`。

## 第 33 条：跨版本移植，同名 API 必须看 1.20.1 的「本体实现」，不能只看签名

2026-10-09 一天之内踩了四次，全是同一个病根：**把 1.21 的调用形状直接套到 1.20.1 的同名 API 上**。1.21 里的"同名"经常是巧合。

- **`ArrowItem.createArrow(Level, ItemStack, LivingEntity)`**：1.21 的 `asProjectile` 走**坐标构造**（owner 可传 null）；1.20.1 同名方法内部是 `new Arrow(level, shooter)` → `AbstractArrow` 里 `shooter.getX()` → **传 null 直接 NPE 崩服**。1.20.1 的等价写法是 `new Arrow(level, x, y, z)` + `arrow.setEffectsFromItem(stack)`（`ArrowItem` 本体也是这么干的）。
- **`Arrow.setEffectsFromItem(stack)`**：1.20.1 里有 `if (stack.is(Items.TIPPED_ARROW))` 这道门（字节码实证）→ 物品名不是 `tipped_arrow` 时药水**静默丢弃**；1.21 靠数据组件不看物品类型。所以战利品表里"普通箭 + `set_potion`"在 1.20.1 必须改成 `minecraft:tipped_arrow`。
- **`EntityType.getBaseClass()`**：1.20.1 里**写死返回 `Entity.class`**（`javap -c`：`ldc_w class net/minecraft/world/entity/Entity; areturn`）→ 判"是不是某类实体"只能用 `getCategory()`。
- **`Monster.checkMonsterSpawnRules`**：1.21 有 `spawnType.ignoresLightRequirements()`（仅 `TRIAL_SPAWNER`）的**光照旁路**，1.20.1 没有 → 而试炼刷怪笼激活态自身发光 8 级 → 落点永远"不够暗" → **原版光照判定 100% 失败，一只怪也刷不出来**（irons 的移植同样中招）。1.20.1 的等价件：`MobCategory.MONSTER` 时只判 `difficulty != PEACEFUL`。

**做法**：
1. 先 `javap -c -p -cp "<mapped forge jar>" <类>` 看**真实实现**（不是只看方法表）；必要时把 1.20.1 的类抽出来用 CFR 反编译，读本体。
2. 混淆名一律**按官方签名反查映射表**（如 `grep "atBottomCenterOf" mappings`），别凭上下文猜字母——`Vec3` 是 `a=atLowerCornerOf / b=atCenterOf / c=atBottomCenterOf`（`c` 不是"角"！），`Mth` 是 `lerp=i/d`、`rotLerp=j/e`（两个渲染器里都用到了，抄错就转反/倒退一格）。
3. 崩溃栈/日志能定位"哪一行"，但**根因常常是同名不同语义**——定位到自己的行之后，回头查那个原版调用的 1.20.1 实现，而不是再猜一次。

相关：[[cuotiben-build-and-merge]]、[[cuotiben-lm-port]]、[[goety2-src-decompile]]、[[trial-spawner-port-plan]]
