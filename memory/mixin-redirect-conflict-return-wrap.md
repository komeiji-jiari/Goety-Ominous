---
name: mixin-redirect-conflict-return-wrap
description: 第三方 mod 的 @Redirect 抢同一调用点时，自己的 @Redirect 会被跳过并硬崩；改用被调方法的 RETURN 注入包返回值
metadata: 
  node_type: memory
  type: reference
  originSessionId: 3398e615-6e40-4eb1-80f5-bf888dcbe299
  modified: 2026-10-26T15:28:37.126Z
---

2026-10-26 崩溃实况（Lithostitched 1.4.11 + goetyominous 3.1.0，Forge 47.4.28）：`trial.JigsawStructureMixin` 的 `@Redirect JigsawPlacement.addPieces` 被 `lithostitched.mixins.json:common.JigsawStructureMixin` 抢走同一条 INVOKE。两者优先级都是 1000，日志只留一句 “@Redirect conflict. Skipping …”，被跳过的 redirector 随后在注入检查里 0/1 → `MixinTransformerError`，游戏卡在 Bootstrap（StructureType 静态初始化）直接崩。

**规则：**
- 一条 INVOKE 只能有一个 @Redirect 生效；被跳过 ≠ 注入成功，`require=1` 会当场崩启动。
- 可能被别人抢的调用点（原版结构/世界生成这类热门目标）别用 @Redirect / @ModifyArg，改成在被调方法自己的 `@At("RETURN")` 注入包返回值：`@Inject(cancellable=true)` + `cir.setReturnValue(...)`，参数照样拿得到，与任何 redirect 共存。这次就是把包装逻辑搬进 `JigsawPlacement.addPieces(8 参)` 的 RETURN。
- 抬高自己优先级压过对方是坏解法：对方也可能是 `require=1`，只是把崩溃换给对方。

**验证：** `build/tmp/compileJava/goetyominous.mixins.refmap.json` 里能看到该目标被解析成 SRG 名（这次 `m_227238_`），说明描述符对上了；`compileJava` 通过不代表运行时注入成功。

见 [[trial-effect-port-1-21-1]]（同类教训：「同一调用点不能再加第二个 @Redirect」）、[[goety-ac-no-mixin]]。
