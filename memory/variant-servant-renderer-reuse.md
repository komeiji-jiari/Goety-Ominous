---
name: variant-servant-renderer-reuse
description: "做\"放大版/换皮版\"仆从时不复制模型和渲染器——靠 EntityType<? extends T> 注册签名 + 渲染器子类参数化"
metadata: 
  node_type: memory
  type: project
  originSessionId: 4c9d4684-d3af-4bca-9856-70ab807d125b
  modified: 2026-10-03T13:24:40.198Z
---

要给已有仆从做"远古版/放大版/换皮版"（如 AncientMineGuardianServant extends MineGuardianServant），**不要**复制模型或渲染器。

**Why:** `MobRenderer<T extends LivingEntity, M extends EntityModel<T>>` 的泛型不变，`ModelX extends AdvancedEntityModel<父实体>` 不是 `EntityModel<子实体>`，所以子类实体**无法**拥有自己类型参数的渲染器（同 [[goety-ac-dinosaur-spirit-port]] 那个坑）。但 `EntityRenderersEvent.registerEntityRenderer(EntityType<? extends T>, EntityRendererProvider<T>)` 接受父类型的 provider —— 注册用的 provider 保持 T=父实体即可，渲染器子类本身**不改类型参数**，只是覆写方法，因此合法。

**How to apply:**
1. 实体：`class AncientXServant extends XServant`，构造函数转发，其余全继承（锚链/爆炸友军/召唤上限/保护 handler 全是 `instanceof 父类`，必须继承才不失配）。
2. 渲染器：把父渲染器的贴图字段改 `protected static final`，加一个 protected 构造 `(ctx, texture, sleepingTexture, glowTexture)`，参数存成 final 字段；`LayerGlow` 之类的嵌套层把写死的贴图挪成构造参数。
3. 缩放：父类里提 `protected float getModelScale()`，`scale()` 和任何"写在 `super.render()` 之后、已在 pose 栈外"的附加几何都要用它；附加几何用 `poseStack.scale(getModelScale()/DEFAULT_MODEL_SCALE, ...)` 包一层，父类行为才能逐位不变。
4. 子渲染器只覆写 `getTextureLocation`（无需 instanceof 分支，它只注册给子类型）、`getModelScale()`、`this.shadowRadius`；模型 `new ModelXServant()` 各自一份实例，不共享。
5. `EntityType.sized()` 按放大比例同步（服务端受击盒只认它，见 [[forge-entity-sized-vs-getdimensions]]）。

相关：[[goety-ac-servant-keydown-channels]] 同属 AC 仆从基建；[[servant-spawn-egg-item-model]] 刷怪蛋 item model 必补。
