# AGENTS.md — warm-flow-core 模块规则

> 本文件只写 `warm-flow-core` 的差异化规则。通用工程与编码规范以仓库根 [`../AGENTS.md`](../AGENTS.md) 为准；规则优先级见根 `AGENTS.md`「规则优先级」。
>
> 最高约束：core 是整个引擎的地基，被所有 orm / plugin 模块与下游使用方依赖。**任何公共行为改动按 L2 高风险处理**。

## 模块职责

框架无关、ORM 无关、JSON 库无关的流程引擎核心，包根 `org.dromara.warm.flow.core`：

- `FlowEngine`：静态门面，持有各 `XxxService`、实体 `Supplier`、handler / listener / `jsonConvert`。
- `config`：`WarmFlow` 引擎配置与 `init()` 装配。
- `invoker/FrameInvoker`：框架桥接（`setBeanFunction` / `setCfgFunction`），core 不依赖容器的关键。
- `entity`：`Definition`/`Node`/`Skip`/`Instance`/`Task`/`HisTask`/`User`/`Form` 等接口。
- `service` + `service.impl`：`DefService`/`NodeService`/`SkipService`/`InsService`/`TaskService`/`HisTaskService`/`UserService`/`FormService`/`ChartService`。
- `orm`：抽象 `dao/WarmDao`、`agent/WarmQuery`、`service/WarmServiceImpl`（ORM 接缝，不含具体实现）。
- `handler`：`DataFillHandler`/`TenantHandler`/`PermissionHandler`。
- `listener`：`Listener`/`GlobalListener`/`ListenerVariable`。
- `strategy`：策略接口——`ConditionStrategy`（条件）、`HandlerStrategy`（办理人）、`ListenerStrategy`（监听器）、`VoteSignStrategy`（票签）、`ExpressionStrategy`（表达式基类）；具体 SpEL/SnEL 实现在 `plugin-modes`。
- `condition`：条件比较运算的具体实现（`AbstractConditionStrategy` + `Eq`/`Ne`/`Gt`/`Ge`/`Lt`/`Le`/`Like`/`NotLike`）。
- `keygen`：`SnowFlakeId14/15` 等 ID 生成。
- `json`：`JsonConvert` SPI 接口（实现在 plugin-json）。
- `utils`：引擎自带工具（`StringUtils`/`ObjectUtil`/`CollUtil`/`MapUtil`/`AssertUtil` 等）。

## 改动前必读

- 根 [`../AGENTS.md`](../AGENTS.md)「架构与扩展机制」「兼容性红线」。
- 改服务 / 状态机前，先读对应 `service.impl` + 相关 `strategy` / `handler` / `listener` + 状态枚举（`FlowStatus`/`NodeType`/`SkipType`/`CooperateType` 等）。
- `../.qoder/repowiki/zh/content/核心引擎架构/` 有服务层、实体模型、数据流的详细文档（本地参考）。

## 高风险点（一律 L2）

- **零框架依赖红线**：core **禁止**出现 `org.springframework.*`、`org.noear.solon.*`、`com.baomidou.*` 等具体框架 / ORM import（当前已是零依赖，必须保持）。需要容器能力时走 `FrameInvoker`，需要可替换实现走 SPI。
- **JDK 1.8 语法**：禁止 Java 9+ 语法 / API（`var`、`record`、switch 表达式、文本块、`List.of`、`Optional.isEmpty`、`Stream.toList`、`String.isBlank` 等）；用 `utils.*` 替代。
- **门面与契约**：`FlowEngine` 方法、`WarmDao` 抽象、实体接口、`WarmFlow` 配置项、枚举常量（code / 顺序 / 名称）都是对外契约，改动评估下游破坏，优先「加法」，废弃用 `@Deprecated` 留过渡期。
- **状态机语义**：通过 / 退回 / 跳转 / 转办 / 加减签 / 终止 / 撤回 / 票签 / 网关有副作用，先确认现有流转再改，不要凭文件名猜。
- **实体字段**：新增 / 改字段要同步各 ORM 实体实现、JSON 序列化与 `sql/` 四套表结构。

## 聚焦验证

```bash
mvn -pl warm-flow-core -am -DskipTests compile
```

core 改动后至少再编译一个下游模块（如某 orm-core 或 plugin），确认接缝未破。
