---
name: no-comments-in-code
description: 不要给我写进工程的代码加注释（用户明确要求：连解释性 javadoc、行内注释都不要）
metadata: 
  node_type: memory
  type: feedback
  originSessionId: 3cdcb961-240f-4035-939e-38af37edc283
---

**不要加注释**：写进工程的代码一律裸代码 —— 不写解释性 javadoc、不写行内注释、不写"为什么这么写"的说明块（中英文都不写）。

**Why:** 用户 2026-10-09 明确说「交给你edit，**不要加注释**」。更早的证据：我加在 `JigsawStructureMixin` 里的 3 行中文说明被直接删掉；随后我把 `PoolAliasContext.run` 上自己加的那句也主动删了。

**How to apply:**
- 贴代码、落盘代码都保持裸代码；需要解释"为什么"就写在**对话里**，或写进记忆目录（本项目里机制性坑都记在 memory 里，别塞回 .java）。
- 只对我新写的内容生效：项目里**已有**的注释不动、不"顺手清理"。
- 这条与 [[no-edit-without-consent]] 独立：即使给了 edit 授权，也不要加注释。

相关：[[no-edit-without-consent]] [[trial-chamber-port-plan]]
