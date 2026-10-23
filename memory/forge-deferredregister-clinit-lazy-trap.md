---
name: forge-deferredregister-clinit-lazy-trap
description: "静态字段里调 DeferredRegister.register 的类,被方块 supplier 惰性拖到 RegisterEvent 里才初始化就会崩;必须在 mod 构造器里先触碰一次"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 5850c515-ab9a-4d80-b4d6-9483a063d382
  modified: 2026-10-16T16:18:52.817Z
---

若一个类把 `DeferredRegister.register(name, supplier)` 写在**静态字段初始化**里（如 `TrialSounds.create(...)`、`LmSounds.LM_SOUNDS.register(...)`），它的 `<clinit>` 一定是**惰性**的：谁第一次触碰它，就在谁那里跑。如果第一次触碰发生在**另一个注册表的 RegisterEvent 派发过程中**（典型：方块的 `DeferredRegister.register("x", () -> new Block(props()))` 这个 supplier 在 BLOCK 的 `addEntries` 里才执行，而 `props()` 里读了那个类的静态字段），就会拿到：

```
ExceptionInInitializerError
  at <SoundTypes>.<clinit>
  at <Blocks>.<someProps>
  at <Blocks>.lambda$static$N
  at DeferredRegister.lambda$addEntries$1
Caused by: IllegalStateException: Cannot register new entries to DeferredRegister after RegisterEvent has been fired.
  at DeferredRegister.register
  at <Sounds>.create
  at <Sounds>.<clinit>
```

`DeferredRegister` 用 `seenRegisterEvent` 标志位挡「事件后再 register」，而 `addEntries` 是**先置位再遍历**。

**Why:** 症状看着像资源/贴图错，实际是纯类初始化时序问题，而且只在**装了新 jar 首次启动**时暴露（老 jar 里这个类早因别的原因初始化过）。反直觉的是 `RegisterEvent` 期间**读取**别人的 `RegistryObject.get()` 是安全的 —— Forge 按注册表顺序派发事件，`SOUND_EVENT` 早于 `BLOCK` 完成填充；同一 mod 里 `HeavyCoreBlock.HEAVY_CORE_SOUND` 就在 BLOCK 的 supplier 里 `.get()` 且一直正常。

**2026-10-16 反例,别把上面那句当普遍规律:** 在 **ITEM** 的 register supplier 里读 `MmEntityRegistry.MUTANT_*_SERVANT.get()`(跨注册表:物品→实体类型),启动直接崩 `NullPointerException: Registry Object not present: goetyominous:mutant_jungle_zombie_servant`(`MmItems.lambda$static$N`,堆栈落在 `RegisterEvent` dispatch)。**ITEM 的 RegisterEvent 早于 ENTITY_TYPE**,所以跨注册表的 `.get()` 不是安全的 —— sound→block 那种"早注册表先填好"是顺序恰好,不是保证。**物品/方块里要拿实体类型就存 `RegistryObject`（或 `Supplier<? extends EntityType<...>>`),到运行时(如 `interactLivingEntity`)才 `.get()`** —— spawn egg 一直这么写,所以没踩过。

**How to apply:** 注册型静态类一律显式给一个 `public static void register(IEventBus modEventBus)`，在里面 `自己的 DeferredRegister.register(modEventBus)`，并在 **mod 构造器**里调用它（`ModSounds.init()` / `LmSounds.register(bus)` / `TrialBlocks.register(bus)` 那一串）。构造器一定早于任何 RegisterEvent，调用即完成 `<clinit>`。多个 DeferredRegister 指向同一注册表是允许的（`ModSounds.SOUNDS` 与 `LmSounds.LM_SOUNDS` 长期共存）。**别**反过来在构造器里强行初始化「读 `.get()` 拼 SoundType」的那一层 —— 那时对应注册表还没填，`.get()` 会抛 Registry Object not present。参见 [[mc-weathering-block-per-tier-props]]。
