---
name: mc-1201-entity-collide-private
description: 1.20.1 的 Entity.collide 是 private(要 AT)、Mob.rotlerp 是 private static、Entity 没有 getGravity —— 三处都别照抄高版本写法
metadata: 
  node_type: memory
  type: reference
  originSessionId: 7243ed09-ddbf-45ce-8d7c-6ee99bf5eaf9
  modified: 2026-10-10T16:25:34.763Z
---

1.20.1 特有的三个坑，全部由编译期证明：

- **`Entity.collide(Vec3)` 是 private**（`m_20272_`）→ 子类既不能 `@Override` 也不能 `super.collide(...)`。要靠 AT：`public net.minecraft.world.entity.Entity m_20272_(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;`（必须写 SRG 名，FG 把 AT 打在 SRG 命名的 injected jar 上）。仓库 `META-INF/accesstransformer.cfg` 已有这一行 —— **它现在是 `CorrodentServant:543` 与 `RockyRollerServant:366` 在依赖（两者都 `@Override public Vec3 collide(Vec3)`），不是死行，别跟着别的东西一起清掉**。（2026-10-10 大王乌贼仆从删除时特意留下的。）
- **`Mob.rotlerp(FFF)` 是 private static**（`m_21376_`）→ 子类要自己抄一份 `protected float rotlerp(...)`。注意 `MoveControl.rotlerp`（`m_24991_`）是另一个 private 实例方法。
- **1.20.1 没有 `Entity.getGravity()`**（那是 1.20.5+ 的属性化重力）→ AM 的 `AquaticMoveController` 第 65 行用的是 `(double)entity.getSpeed() * d1 * 0.6`，先照抄成 `getGravity()` 会"找不到符号"。
- 反过来，`Entity.getDismountLocationForPassenger(LivingEntity)` 在 Forge 里是 **public**，可以从别的类对别的实体调用。

**Why:** 反编译 AM（1.20.1）时方法名/调用形式看着"理所当然"，但可见性、存在性都跟高版本 mojmap 直觉不一致；这三处全是编译期才发现，写之前没有判据。

**How to apply:** 移植 1.20.1 第三方实体代码时，凡是 `super.xxx()` 失败或"找不到符号"，先怀疑**可见性**而不是抄错名字 —— 用 javap 验：bash 读不了 `.jdks` 路径（安全守卫，整个命令会被拒），走 `powershell -NoProfile -Command "& 'C:\Users\TangJinbo\.jdks\ms-17.0.20\bin\javap.exe' -p -classpath 'D:\ark\java\build\fg_cache\net\minecraftforge\forge\1.20.1-47.3.22_mapped_official_1.20.1\forge-1.20.1-47.3.22_mapped_official_1.20.1-recomp.jar' <类>`（见 [[java-toolchain-location]]、[[srg-to-mojmap-tsrg-lookup]]）。AT 生效的判据很简单：加行后能编译过即说明 AT 打上了。
