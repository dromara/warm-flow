# AGENTS.md — warm-flow-plugin 模块规则

> 本文件只写 `warm-flow-plugin` 的差异化规则。通用工程与编码规范以仓库根 [`../AGENTS.md`](../AGENTS.md) 为准；规则优先级见根 `AGENTS.md`「规则优先级」。

## 模块职责

引擎的可插拔扩展实现，按扩展点分三类：

- `warm-flow-plugin-modes`：框架模式与表达式实现。`*-sb`（Spring，SpEL：`ConditionStrategySpel`、`VoteSignStrategySpel`、`HandlerStrategySpel` 等，含 `SpelHelper` / 安全方法解析）；`*-solon`（Solon，SnEL：`*SnEl`）。包根 `org.dromara.warm.plugin.modes.sb` / `.solon`。
- `warm-flow-plugin-json`：`JsonConvert` 的 SPI 实现。`*-json-v1`（`JsonConvertSnack`/`Snack4`/`Jackson`/`FastJson`/`Gson`）；`*-json-jackson3`。包根 `org.dromara.warm.plugin.json`。
- `warm-flow-plugin-ui`：设计器 / 流程图后端。`*-ui-core`（`service` / `dto` / `vo`）、`*-ui-sb-web`（Spring controller）、`*-ui-solon-web`（Solon controller）、`*-vue3-ui`（打包前端资源）。包根 `org.dromara.warm.flow.ui`。

## 改动前必读

- 根 [`../AGENTS.md`](../AGENTS.md)「架构与扩展机制」「兼容性红线」「扩展开发指引」。
- core 的 `condition` / `strategy` / `listener` / `json.JsonConvert` 接口；改表达式策略前先读 core 默认实现与同类 sb/solon 实现。

## 高风险点（按 L2）

- **SPI 注册一致**：新增 / 改 JSON 实现必须同步 `META-INF/services/org.dromara.warm.flow.core.json.JsonConvert`；`FlowEngine` 通过 `ServiceLoaderUtil.loadFirst` 取首个实现，注意实现优先级与 classpath 唯一性。
- **表达式安全**：SpEL / SnEL 涉及脚本执行，沿用现有 `SafeMethodResolver` / `SafeTypeLocator` 等安全约束，**不要放开任意方法 / 类型调用**，避免表达式注入风险。
- **双模式 UI**：经典模式与仿钉钉模式都要兼顾；controller 在 sb-web 与 solon-web 行为对齐，前端资源放 `*-vue3-ui`。
- **多生态对齐**：modes / ui 的 sb 与 solon 实现保持能力一致，不要只改一侧。
- **JSON 兼容**：不同 JSON 库对日期 / null / 泛型 / 多态序列化差异要与引擎实体约定一致，避免下游切换 JSON 库后行为变化。

## 聚焦验证

```bash
# 按被改子模块替换路径
mvn -f warm-flow-plugin/warm-flow-plugin-json/warm-flow-plugin-json-v1/pom.xml -DskipTests compile
mvn -f warm-flow-plugin/warm-flow-plugin-modes/warm-flow-plugin-modes-sb/pom.xml -DskipTests compile
```

涉及 SPI / 表达式 / UI 契约时，确认注册文件与对应 sb/solon 实现同步。
