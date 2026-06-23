# CLAUDE.md

本仓库（`warm-flow`，Dromara 开源轻量级工作流引擎 / SDK）的编码与协作规则，**以 [AGENTS.md](./AGENTS.md) 为唯一来源**。开始任何任务前先读根 `AGENTS.md`；若改动所在顶层模块下还有模块级 `AGENTS.md`，再读模块级。本文件只是核心红线摘要，**与 `AGENTS.md` 冲突时以 `AGENTS.md` 为准**。

## 规则优先级

1. 用户当前明确指令。
2. 模块级 `AGENTS.md`（若存在）。
3. 根 `AGENTS.md`。
4. 本 `CLAUDE.md` 与 `.cursor/rules`（与 `AGENTS.md` 冲突以 `AGENTS.md` 为准）。
5. `.qoder/repowiki` 等领域 / 架构参考。

## 项目性质（决定一切约束）

- 这是**被他人依赖的开源类库 / SDK**（发布到 Maven 中央仓库，`groupId=org.dromara.warm`），不是业务应用。
- 三大最高约束：**JDK 1.8 源码级兼容**、**对外契约向后兼容**、**核心与框架 / ORM / JSON 解耦**。

## 技术基线

- JDK 1.8 源码级（兼容 Java 8/17/21）；多框架 Spring Boot 2.7/3.0/4.0 + Solon 3.10；多 ORM MyBatis / MyBatis-Plus / Easy-Query；多 JSON snack3/snack4/jackson/jackson3/fastjson2/gson；多数据库 MySQL/Oracle/PostgreSQL/SQL Server。
- Lombok + `slf4j-api`；依赖版本统一在父 `pom.xml`，子模块不私改。

## 模块速览

- `warm-flow-core`：框架 / ORM / JSON 无关的引擎核心（实体、服务、抽象 `WarmDao`、`FlowEngine`、`FrameInvoker`、SPI、表达式 / 监听器策略）。
- `warm-flow-orm`：ORM 适配，按「ORM × (sb/sb3/sb4/solon)」矩阵实现 `WarmDao`。
- `warm-flow-plugin`：可插拔扩展（`modes` 表达式、`json` 序列化、`ui` 设计器后端）。
- `warm-flow-ui`：Vue3 前端设计器，独立于 Maven 反应堆。
- `sql/`：mysql/oracle/postgresql/sqlserver 四套脚本 + MySQL `v1-upgrade/`。

## 核心红线（详见 AGENTS.md）

1. **JDK 8 语法**：主代码禁止 `var`/`record`/switch 表达式/文本块/`List.of`/`Optional.isEmpty`/`Stream.toList`/`String.isBlank` 等 Java 9+ API；优先用 core 的 `utils.*`。
2. **解耦**：`warm-flow-core` 禁止 import `org.springframework.*` / `org.noear.solon.*` / `com.baomidou.*`；框架差异走 `FrameInvoker` + SPI + 各 starter，ORM 差异走 `WarmDao` 实现。
3. **门面**：统一通过 `FlowEngine.xxxService()` / `FlowEngine.newXxx()` 取服务和建实体，不绕过门面直接 new 实现类。
4. **向后兼容**：公共方法签名、`WarmDao` 接口、实体字段、枚举常量（code/顺序）、`WarmFlow` 配置项是已发布契约，不随意删 / 改签名 / 重排；废弃用 `@Deprecated` 留过渡期。
5. **多生态对齐**：改某 starter 能力时，确认 `sb`/`sb3`/`sb4`/`solon` 与对应 ORM 模块是否需同步。
6. **SQL 四套同步**：表结构改动同步 mysql/oracle/postgresql/sqlserver 全量脚本 + 新增 `v1-upgrade/warm-flow_x.x.x.sql`，写明各库方言兼容与回滚。
7. **多租户 / 逻辑删除**：引擎自带实现，相关字段 / 行为改动兼顾「引擎自带」与「ORM 框架」两条路径。
8. **扩展机制**：新增 JSON 走 `JsonConvert` + `META-INF/services`；新增 ORM / 框架走五件套矩阵 + 对应自动装配（`spring.factories` / `AutoConfiguration.imports` / Solon properties）。
9. **状态机**：通过 / 退回 / 跳转 / 转办 / 加减签 / 终止 / 撤回 / 票签 / 网关有副作用，改动前读对应 `service.impl` / `strategy` / `listener` 与状态枚举。
10. **品牌与协议**：保留每个 Java 文件 Apache 2.0 license header、`@author warm`、`org.dromara.warm` 包名、banner、README 链接与 `LICENSE`，不弱化「永久开源免费」声明。

## 高风险（按 L2 处理，先读调用链）

core 公共 API / 实体 / 抽象 DAO、扩展机制（`FlowEngine` / `FrameInvoker` / SPI / 自动装配）、跨生态行为、跨 ORM 行为、多数据库表结构 / 升级脚本、版本发布、流程状态机语义。

## 验证与安全边界

- 本仓库无 `src/test`（测试在独立仓库 `warm-flow-test`），用**分模块编译**验证：`mvn -pl <module> -am -DskipTests compile` 或 `mvn clean install -DskipTests`；core 改动至少编译一个下游 orm/plugin 模块。无法验证时如实报告命令、现象、风险与下一步。
- 未经用户明确批准：不 `git commit` / `git push`、不 `mvn deploy` / 改版本推送、不改写历史、不执行 `DROP` / 批量 `DELETE`、不降 JDK 基线 / 删编译插件、不输出真实发布凭证。

完整规则见 **[AGENTS.md](./AGENTS.md)**。
