---
name: tremorzilla-breath-sound-ownership
description: "吐息音效分工——BEAM_START/END 归 handler,蓄力音归 spell;别重复加"
metadata: 
  node_type: memory
  type: project
  originSessionId: b2486410-d1ce-4d77-bbd0-ba8847d6c9f7
  modified: 2026-09-30T12:25:16.485Z
---

`TremorzillaBreathHandler.startBeam()` 自带 `TREMORZILLA_BEAM_START`(起手音),`tickBeam()` 收尾自带 `TREMORZILLA_BEAM_END`。`startBeam` 由 `TremorzillaBreathSpell.SpellResult` 触发,而 `SpellResult` 走 `DarkWand.finishUsingItem`,**每次施法只调一次**。

所以 `TremorzillaBreathSpell` 的职责边界是:只管蓄力阶段的 `TREMORZILLA_CHARGE_NORMAL` / `TREMORZILLA_CHARGE_COMPLETE`,**不要再往 `useSpell` 里塞 BEAM_START**。

**Why:** 曾经给 spell 补了一发 BEAM_START(t≈190),和 handler 那发(t=200)错开 10 tick 叠放——同一个 3.9s 样本几乎完全重合,加上 COMPLETE 那发 3.25s 的尾巴,释放瞬间三发 Tremorzilla 声音糊在一起,听起来是"叫了好多次"的断续重影。

**How to apply:** 判断某个 AC 音效"有没有被引用"时,别用 `grep -rn <符号> | head -N` —— 前面的无关命中(那次 head 里全是 `TEXTURE_BEAM_END` 贴图行)会把真正的那条截掉,据此下"没被引用"的结论就会出错。要么精确 grep 符号全名,要么去掉 head。

相关:[[ac-servant-parity-keep-original]]、[[ac-goety-port-probability-gates]]
