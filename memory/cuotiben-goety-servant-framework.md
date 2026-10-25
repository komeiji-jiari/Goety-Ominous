---
name: cuotiben-goety-servant-framework
description: 徒弟错题本·Goety 仆从框架篇：注册表隔离、goal 优先级双向陷阱（太低被抢 / 太高挤光近战）、导航器抄死、toml 覆盖默认值
metadata: 
  node_type: memory
  type: project
  originSessionId: 304245bf-c7a1-4c6f-8c54-9cc55d2f31af
---

来源：徒弟的「错题本.md」第二节。取法见 [[cuotiben-build-and-merge]]。

## 注册表

- **第 4 条：借别人的注册表 → 人家一改就崩。** 原先往 `com.Polarice3.Goety.init.ModAttributes.ATTRIBUTES` 塞自己的属性，Goety 2.5.57.3 把它从 public 改 private 就崩 `IllegalAccessError`。**自己建 `DeferredRegister`，只用对方公开 API。**（与 [[cuotiben-lm-port]] 第 30 条是**方向相反**的两种错：那条是"别人有公开 API 就别再抄一份"。分界线=**对方的公开 API 够不够用**。）
- **第 5 条：可选联动 mod 的内容注册进主注册表 → 别人启动就崩。** 联动内容必须注册进**各自的** `XxItems`，和主注册表隔离。见 [[goetyominous-compat-architecture]]、[[goetyominous-soft-dep-reflection]]。

## goal 优先级 —— 两个方向都要防

- **第 7 条（太低）**：Goety 的 `FollowOwnerGoal` 优先级是 **5**，同优先级按注册顺序轮询，先返回 `true` 的会锁住 MOVE/LOOK/JUMP 三个 Flag。**攻击 goal 优先级必须 > 5。**
- **第 18 条（太高）**：给圣骑补的「后跃拉开距离」goal 为了压过所有攻击 goal 注册在**优先级 0**，又没加掷骰 → 每 6 秒冷却一到**必触发**，一次占 28 tick 后空翻，跃开还必然接远程招。近战只剩一两秒。**招式一个没删，近战却几乎看不见了。**
  - 根因：**优先级决定的是「谁先被问」，不只是「谁抢得过谁」。**
  - 以后：加「行为」goal（改变节奏的那种，不是新招式）前先算 —— `一次占用多久 × 触发频率 → 剩下的时间还够不够主力招式用？` 拿不准先放优先级 1（和攻击同级）。
  - 第 7 / 18 是**一体两面**：一个太低打不过，一个太高把别人全挤没。

## 其他

- **第 6 条：`FollowOwnerGoal` 构造时把导航器抄死了。** 构造里抓当时的导航器存成字段，而 Goety 仆从之后会替换导航器 → 存的是被丢弃的旧对象，表现为**不跟随、移速慢得离谱**。涉及导航器的 goal 用 `entity.getNavigation()` **现取**。
- **第 8 条：配置默认值改不动。** `run/config` 下的 toml 已存在时，**已存在的键会覆盖代码默认值**。改已存在的键要**代码和 toml 两边都改**，或让玩家删 toml 重新生成。

相关：[[cuotiben-lm-port]]、[[goety2-servant-system]]
