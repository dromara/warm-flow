# AGENTS.md — warm-flow-orm 模块规则

> 本文件只写 `warm-flow-orm` 的差异化规则。通用工程与编码规范以仓库根 [`../AGENTS.md`](../AGENTS.md) 为准；规则优先级见根 `AGENTS.md`「规则优先级」。

## 模块职责

ORM 适配层，实现 `warm-flow-core` 的 `WarmDao` 抽象与各实体。按「**ORM × 框架**」矩阵组织，每种 ORM 一套五件套：

- `warm-flow-mybatis`：`*-core`（实体 / Mapper / `WarmDaoImpl`）+ `*-sb-starter`(SB2) + `*-sb3-starter`(SB3) + `*-sb4-starter`(SB4) + `*-solon-plugin`(Solon)。
- `warm-flow-mybatis-plus`：同上五件套（含 `MybatisPlusIdGen` 等适配）。
- `warm-flow-easy-query`：同上五件套。

包根：ORM 实现 `org.dromara.warm.flow.orm`（`entity`/`mapper`/`dao`/`keygen`/`utils`）；Spring 适配 `org.dromara.warm.flow.spring.boot`；Solon 适配 `org.dromara.warm.flow.solon`。

## 改动前必读

- 根 [`../AGENTS.md`](../AGENTS.md)「架构与扩展机制」「兼容性红线」「扩展开发指引」。
- core 的 `WarmDao` / `WarmQuery` / `WarmServiceImpl` 与对应实体接口；改某 ORM 的 `WarmDaoImpl` 前，先看同 ORM 的 `*-core` 实现与 starter 装配。

## 高风险点（按 L2）

- **多生态对齐**：一个能力改动通常要在同 ORM 的 `sb` / `sb3` / `sb4` / `solon` 同步；不要只改一套 starter 导致生态不一致。
- **自动装配注册一致**：SB2 用 `META-INF/spring.factories`，SB3/4 用 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`，Solon 用 `META-INF/solon/*.properties`。新增 / 改 `FlowAutoConfig` 时同步对应注册文件，并确保把实现接入 `FlowEngine` / 注入 `FrameInvoker`。
- **WarmDao 契约**：各 ORM 实现要满足 core `WarmDao<T>` 的语义（分页、批量、软删除、租户），不要让不同 ORM 行为漂移。
- **ID 生成 / 分页 / 方言**：`keyType`、`dataSourceType` 影响 ID 与分页 SQL；跨数据库时确认方言差异。
- **多租户与逻辑删除**：区分「引擎自带实现」与「复用 ORM 框架实现」两条路径，改动时两者都要自洽。

## SQL 同步

表结构相关改动按根 `AGENTS.md`「SQL 与数据库」同步 `sql/` 下 mysql / oracle / postgresql / sqlserver 四套脚本与升级脚本。

## 聚焦验证

```bash
# 编译被改的某个 ORM 模块（按实际路径替换）
mvn -f warm-flow-orm/warm-flow-mybatis-plus/warm-flow-mybatis-plus-core/pom.xml -DskipTests compile
```

改 core 接缝相关内容时，至少各编译一种受影响 ORM 的 `*-core` 与一个 starter。
