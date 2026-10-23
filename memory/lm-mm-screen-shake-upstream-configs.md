---
name: lm-mm-screen-shake-upstream-configs
description: 屏幕震动开关最终是「各自一条仓库自有键」——LM 2026-10-16 加 LmServantScreenShake（默认 false，绕开 LM 的 allowCameraShake），MM 是 MutantMoreServantScreenShake；上游把判定埋在公共入口方法体内的事实仍然成立
metadata:
  node_type: memory
  type: project
  originSessionId: e440cfb1-40a1-4c83-b20f-201e147f2f8c
  modified: 2026-10-15T23:39:39.402Z
---

**最终形态（2026-10-16 定案，与 2026-10-11 的「都不加」相反，别再引用旧结论）：**

- **MM**：`compat/mm/MmScreenShake.shake(Level, int duration, float amount, BlockPos, int range)` —— 先判 `MobsConfig.MutantMoreServantScreenShake.get()`（**默认 false**）再转 `ShakeCameraEvent.shake`，我们的调用点全部走它。
- **LM**：2026-10-16 用户令「lm 仆从的屏幕震动也加一个配置，不再继承传奇生物的配置」→ 新增 `compat/lm/LmScreenShake.shake(Level, Vec3, float, float, int, int)` —— 判 `MobsConfig.LmServantScreenShake.get()`（**默认 false**，组 `"Legendary Monsters Servants"`，键 `lmServantScreenShake`，紧挨 MM 那条），然后自己 `new CameraShakeEntity(level, pos, radius, strength, duration, fadeDuration)` + `addFreshEntity`。**14 个文件 121 处** `CameraShakeEntity.cameraShake(...)` 已全量改过去（Paladin 45 / Obliterator 31 / BeheadedKnight 14 最多；含 `projectile/` 下 4 个与 `obliterator/TheObliteratorCloneArmed`）。一次性替换脚本留在 `.scratch/_lm_shake_codemod.mjs`。
- 因此 **LM 的 `allowCameraShake` 现在只管 LM 本体 boss，不再管我们的 LM 仆从**（用户要的正是这个解耦：以前关 LM 的键会连带关掉我们的仆从）。

## 为什么「自己 new 实体」能绕开（关键判据，仍成立）

- LM 的开关 `ModConfig.MOB_CONFIG.allowCameraShake`（`config/legendary_monsters-common.toml` → `["Mob Settings"."General Settings"]` → `"Allow Camera Shake" = true`，默认 true）**只写在 `CameraShakeEntity.cameraShake` 方法体里**：`if (!world.isClientSide && allowCameraShake.get()) { addFreshEntity(new CameraShakeEntity(...)) }`。
- 用 `_pal/CameraShakeEntity.java` 反编译 + 全 jar 常量池核对：**除这个静态方法与配置类外没有任何类读 `allowCameraShake`**（客户端渲染器不读，震动靠相机 handler 扫最近的 `CameraShakeEntity` 实体取 `getShakeAmount()`）。所以绕开那个静态方法、自己构造实体 = 完全绕开 LM 的配置，且渲染照常。
- `CameraShakeEntity(Level, Vec3, float, float, int, int)` 在编译依赖 `8902349` 里是 **public**（javap 核过），可以直接 new。

## MM 侧的上游细节（保留，用于理解 `MmScreenShake`）

- 上游开关 `MutantMoreMiscClientConfig.screen_shake_amount`（`config/mutantmore/misc-client.toml`，Float 默认 1.0，**CLIENT 类型**）是唯一应用点，乘在客户端 `ShakeCameraEvent.shakeCamera` 的振幅上。
- `ShakeCameraEvent.shake(Level, int, float, BlockPos, int)` 双端各判一次：`level == null || isClientSide` → 加本地 list；否则发包。所以 `tickDeath()` 里传 `null` 的调用点（`MutantHoglinServant`、`MutantBlazeServant`）走的是客户端那条腿。

**How to apply:** 再遇到「想给某个联动的某类效果加开关」时，先反编译上游那个入口方法看它内部有没有自己的配置判定——**有判定≠不能解耦**：只要判定只在这一个公共静态方法里（不在渲染/客户端侧），自己 new 实体/自己发包就能绕开，我们的键才是真开关。同类前例见 [[ac-screen-shake-duplicate-handler]]；查 jar 版本差异见 [[lm-mechanic-presence-version-check]]。
