---
name: ac-radiation-ally-immunity
description: "AC 核爆/伽马蟑螂雾对友军照发 IRRADIATED;Forge Applicable 事件无 source、Added 在 put 之前,只能用区域+云主判定"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 620f35fe-071e-4c00-8478-a36f47716524
  modified: 2026-09-27T00:07:42.742Z
---

AC 的 `NuclearExplosionEntity`(核能苦力怕自爆 + 湮灭核弹都走它)对半径内**每个** LivingEntity 无条件 `addEffect(IRRADIATED, 48000t, amp 1~2)`,只跳过 `isIntentionalGameDesign()` —— 没有 owner/友军判定;辐照伤害/击飞另有保护但 buff 照给。伽马蟑螂仆从的 `AreaEffectCloud` 同理。

拦法(不用 mixin,走 Forge 事件):`MobEffectEvent.Applicable` 里 `event.setResult(Event.Result.DENY)`(仓库先例 `RaycatAmuletEvents.onEffectApplicable`)。两个坑:

- `Applicable` **不带 source 实体**(`Added` 才有 `getEffectSource()`),所以"谁放的"只能靠位置/登记推断;
- `Added` 在 `LivingEntity.addEffect` 里是在 `activeEffects.put()` **之前** post 的(1.20.1 源码 929 行 → 931 行 put),所以想在 `Added` 里 `removeEffect` 抵消是无效的(那时新实例还没进去)。

最终判据两条:①`NucleeperNukeProtectionHandler` 已登记的核爆保护 zone(`coveringZone`/`isProtected`,含 owner/`IOwned`/`SEHelper.isAlly` 善意判定)——加个 `isRadiationProtected()` 复用;②`AreaEffectCloud.getOwner()` 必须自己 `setOwner(servant)`(AC 原版不设),再按云半径扫附近云比对 `owner.isAlliedTo(entity)`。服务端判定即可(客户端只是收包镜像)。

相关:[[ac-nuke-immunity-two-halves]]、[[ac-goety-port-probability-gates]]
