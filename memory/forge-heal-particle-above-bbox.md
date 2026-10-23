---
name: forge-heal-particle-above-bbox
description: "巨型实体喂食/治疗粒子要用 getY()+getBbHeight()+0.3F 而不是 getRandomY(),后者沿身高散布会被自己的模型深度剔除"
metadata: 
  node_type: memory
  type: reference
  originSessionId: 0d1ac7eb-dd47-4947-be68-37a12dabd63d
  modified: 2026-09-24T09:53:57.472Z
---

`getRandomY()` = `position.y + bbHeight * random`，也就是**脚底到头顶**的随机高度 —— 正好是身体占据的那一段。巨型实体用 `getRandomX(1.0), getRandomY() + 0.5, getRandomZ(1.0)` 撒爱心/治疗粒子，纵向整条会落在模型内部。

**为什么会看不见**（不是碰撞箱的锅，碰撞箱根本不渲染）：

1. `LevelRenderer.renderLevel` 里实体在 `popPush("entities")`，粒子在 `popPush("particles")` —— 粒子永远后画
2. `ParticleRenderType.TERRAIN_SHEET.begin()` 里有 `RenderSystem.depthMask(true)`，粒子写深度、深度测试正常生效
3. fabulous 模式更直白：`particlesTarget.copyDepthFrom(this.minecraft.getMainRenderTarget())`，粒子缓冲区直接拷贝实体渲染后的深度

所以粒子被模型表面深度剔除。遮挡表现跟粒子类型无关 —— 但**别因此以为 `ModParticleTypes.HEAL_EFFECT` 和 `ParticleTypes.HEART` 长一样**：两者共用 `HeartParticle` 这个行为类，spriteSet 却不同，`goety:heal` 是绿色十字。喂食一律用 `ParticleTypes.HEART`，见 [[goetyominous-feed-particle-heart]]。

**附带坑**：`getRandomX(1.0)` 是 `position.x + bbWidth * (2r-1)`，范围 `x ± bbWidth`，是碰撞箱的 **2 倍宽**（碰撞箱只有 `x ± bbWidth/2`）。参数不是"碰撞箱比例"，别按比例去理解。横向能溢出到体外，所以症状是"一半可见一半被吞"，不是全黑。

**仓库惯例**（撼地斯拉/擎天龙/遗迹恐手龙早就是这样，写新仆从照着抄）：

```java
this.getRandomX(1.0F), this.getY() + this.getBbHeight() + 0.3F, this.getRandomZ(1.0F)
```

2026-09-23 已把 `HullbreakerServant`(4.65×4.5) 和 `LuxtructosaurusServant`(6.0×8.5) 从 `getRandomY() + 0.5D` 改过来。其余用该写法的最大实体是 `OvergrownColossusServant`(3.0×5.0)，身高 3 格以内的不用管。

判断标准：`bbHeight` 明显大于模型宽度时才需要提到头顶。同类的"从模板抄喂食逻辑"的坑见 [[forge-livingentity-eat-double-consume]]。
