# AGENTS.md

本文件是 Codex 以及其他 coding agent 在本仓库工作的项目级操作指南，也是本仓库 **AI 规则的唯一主来源**。

它综合了：

- 当前 `warm-flow` 仓库的真实模块结构、技术基线与扩展机制。
- dromara 开源协作约定与 Apache 2.0 开源协议要求。
- 工作流引擎作为「公共依赖库（SDK）」对外契约稳定与多生态兼容的长期规则。
- Karpathy 风格 coding-agent 规则：澄清不确定性、保持简单、避免顺手改无关代码、用可验证结果证明行为。

## 项目概览

`warm-flow` 是 [Dromara](https://dromara.org/) 社区的国产轻量级工作流引擎，`groupId=org.dromara.warm`，发布到 Maven 中央仓库供他人依赖。它**是一个被集成的类库 / SDK，不是业务应用**：简洁轻量（核心仅 7 张表）、五脏俱全、扩展性强，可通过 jar 包快速集成流程设计器，原生支持经典与仿钉钉双模式。

核心定位决定了最高优先约束：**对外公共 API、实体字段、数据库表结构、配置项都是契约，向后兼容是红线**；**核心引擎与具体框架 / ORM / JSON 库解耦**；**JDK 1.8 源码级兼容**。

顶层结构（Maven 反应堆模块 + 前端 + 脚本）：

- `warm-flow-core`：流程引擎核心，**框架无关、ORM 无关、JSON 库无关**。流程定义 / 节点 / 跳转 / 实例 / 任务 / 历史任务 / 用户 / 表单的实体、服务、抽象 DAO、处理器、监听器、条件与办理人表达式策略、ID 生成、SPI 入口。
- `warm-flow-orm`：ORM 适配层，实现 core 的 `WarmDao` 与实体。按「ORM × 框架」矩阵展开：
  - `warm-flow-mybatis`：`*-core` + `*-sb-starter`(SpringBoot2) + `*-sb3-starter`(SpringBoot3) + `*-sb4-starter`(SpringBoot4) + `*-solon-plugin`(Solon)。
  - `warm-flow-mybatis-plus`：同上五件套。
  - `warm-flow-easy-query`：同上五件套。
- `warm-flow-plugin`：可插拔扩展。
  - `warm-flow-plugin-modes`：框架模式与表达式实现（`*-sb` SpEL、`*-solon` SnEL）。
  - `warm-flow-plugin-json`：JSON 序列化实现（`*-json-v1`：snack/snack4/jackson/fastjson2/gson；`*-json-jackson3`）。
  - `warm-flow-plugin-ui`：设计器 / 流程图后端（`*-ui-core`、`*-ui-sb-web`、`*-ui-solon-web`、`*-vue3-ui`）。
- `warm-flow-ui`：流程设计器**前端工程（Vue3 + yarn）**，独立于 Maven 反应堆，不被父 `pom.xml` 的 `<modules>` 纳入。
- `sql/`：建表与升级脚本，按数据库分目录：`mysql/`、`oracle/`、`postgresql/`、`sqlserver/`；每库一份全量 `*-all.sql`，MySQL 另有 `v1-upgrade/` 版本升级脚本。
- 测试不在本仓库：官方测试在独立仓库 `warm-flow-test`（gitee），本仓库无 `src/test`。

## 技术基线

- **JDK 1.8 源码级**：父 `pom.xml` 锁定 `maven.compiler.source/target = 8`，需同时兼容 Java 8 / 17 / 21。主代码**禁止使用 Java 9+ 的语法与 API**（详见「兼容性红线」）。
- **多框架生态**：Spring Boot 2.7.18 / 3.0.1 / 4.0.2 与 Solon 3.10.0 并行支持，对应 `sb` / `sb3` / `sb4` / `solon` 后缀的 starter。
- **多 ORM**：MyBatis 3.5.15（mybatis-spring-boot 2.3.2）、MyBatis-Plus 3.5.12、Easy-Query 3.1.79；README 另提到 JPA / BeetlSql 等生态由社区扩展。
- **多 JSON**：snack3 3.2.139、snack4 4.0.8、jackson 2.13.5、jackson3 3.0.4、fastjson2 2.0.43、gson 2.9.0。
- **多数据库**：MySQL、Oracle、PostgreSQL、SQL Server（其它库转换表结构即可）。
- **基础依赖**：Lombok、`slf4j-api`（仅 API，不绑定日志实现）、JUnit 4（测试在独立仓库）。
- **依赖版本统一在父 `pom.xml` 的 `dependencyManagement` 与 `properties` 管理**，子模块不私自写死或改版本号；版本属性 `${warm-flow}` 跟随 `${project.version}`。

## 架构与扩展机制（warm-flow 的灵魂，改动前必须理解）

引擎通过几个解耦点做到「核心与框架无关、可多生态适配」，**这是本仓库最重要的设计约束，破坏它等于破坏整个工程**：

- `FlowEngine`：静态门面，持有各 `XxxService`、实体 `Supplier`、`handler` / `listener`、`jsonConvert`。业务方与内部统一通过 `FlowEngine.xxxService()` / `FlowEngine.newXxx()` 取服务和新建实体，不要绕过它直接 new 实现类。
- `FrameInvoker`：框架桥接点，通过 `setBeanFunction` / `setCfgFunction` 注入「取 Bean」「取配置」的能力。**core 因此不依赖任何容器**；Spring / Solon 适配模块在启动时注入这两个 Function。
- `WarmFlow`（config）：引擎配置载体（`enabled`、`framework`、`banner`、`keyType`、`logicDelete`、租户 / 数据填充 / 权限 / 全局监听器的类路径、`dataSourceType`、`ui`、`tokenName`、流程图三原色等），`init()` 负责装配 handler、打印 banner、SPI 加载。
- **Java SPI**：`ServiceLoaderUtil.loadFirst(...)` + `META-INF/services/`（如 `org.dromara.warm.flow.core.json.JsonConvert`）用于可替换实现（典型：JSON 转换策略）。
- **自动装配注册**随框架不同而不同：
  - Spring Boot 2：`META-INF/spring.factories`。
  - Spring Boot 3 / 4：`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`。
  - Solon：`META-INF/solon/*.properties`。
- **ORM 抽象**：core 定义 `WarmDao<T>`、`WarmQuery`、`WarmServiceImpl` 与各实体接口；每个 ORM 模块提供实体实现（如 `FlowDefinition`）、Mapper / Dao 实现（如 `WarmDaoImpl`）、ID 生成器适配，并通过 starter 把实现接入 `FlowEngine`。

新增能力时**必须沿用上述矩阵与扩展点**，不要在 core 里直接耦合某个框架 / ORM / JSON 实现。

## 核心工作规则

- 先思考，再编辑。非平凡任务开始前，先识别目标文件、期望行为、验证方式与风险。
- 保持简单。优先选择与现有模式一致的最小正确改动。
- 外科手术式改动。不要格式化、重命名、重排或重构无关代码。
- 保护用户已有工作。较大改动前先看 `git status --short`，除非用户明确要求，绝不回退不是你做的改动。
- 管理不确定性。如果流程语义、状态流转、跨生态行为不清晰且无法从代码安全推断，先问一个简洁问题，或明确写出假设。
- 用证据验证。没有新鲜命令输出或直接检查结果时，不要声称构建、测试、修复已经成功。
- 暴露真实失败。不要用假默认值、吞异常、模拟成功数据或静默降级掩盖真实问题。
- 凡涉及**公共 API / 实体字段 / 表结构 / 配置项 / SPI 契约 / 跨生态行为 / 多数据库 SQL** 的改动，一律按高风险处理。

## 规则源维护

- `AGENTS.md` 是本仓库 agent 规则主来源。
- `.qoder/repowiki` 是更细的引擎架构与领域参考（**本地文档，已 gitignore、未纳入仓库**；作者本地可选参考，团队成员默认没有，不要当作必读入口或长期依赖）。
- 新增、删除或调整长期项目规则时，先更新根 `AGENTS.md`，再按需要同步模块级 `AGENTS.md`、根 `CLAUDE.md` 与 `.cursor/rules`。
- 如果本文件、其他 agent 规则与用户当前指令冲突，优先遵循用户当前指令，同时说明冲突点、取舍原因与影响。
- 不要把一次性任务背景写成永久规则。只有能长期约束本仓库开发质量的约定才加入本文件。

## 规则优先级

冲突时从高到低：

1. 用户当前明确指令。
2. 模块级 `AGENTS.md`（所在顶层模块下若存在）。
3. 仓库根 `AGENTS.md`（本文件）。
4. 根 `CLAUDE.md` 与 `.cursor/rules/*`（与 `AGENTS.md` 冲突一律以 `AGENTS.md` 为准）。
5. （可选）本地 `.qoder/repowiki` 等领域 / 架构参考（未纳入仓库，可能不存在）。

## 模块级 AGENTS.md 与 CLAUDE.md

- 每个顶层模块（`warm-flow-core`、`warm-flow-orm`、`warm-flow-plugin`、`warm-flow-ui`）下都有一份模块级 `AGENTS.md`，**只写本模块差异**（职责、改动前必读、高风险点、聚焦验证），通用规范一律指向根 `AGENTS.md`。
- 根 `CLAUDE.md` 是「以 `AGENTS.md` 为唯一来源」的红线摘要，不承载独有规则。
- 同一条规则只维护一处：通用规则进根 `AGENTS.md`，模块差异进模块级 `AGENTS.md`，避免多处漂移。开始任务前先读根 `AGENTS.md`，再读所在模块级 `AGENTS.md`。

## 品牌与版权保护

- 不要移除、替换或弱化 `warm-flow`、`Warm-Flow`、`dromara`、`org.dromara.warm` 包名 / groupId、模块名、启动 banner、作者信息（`warm` / `290631660@qq.com` 等 `developers`）、README 中的 Star/赞助商/文档/演示链接，除非用户明确要求。
- **每个 Java 文件保留 Apache 2.0 license header**（`Copyright 2024-2025, Warm-Flow (290631660@qq.com).` 开头的注释块）。新增 Java 文件必须照抄现有 header、`package`、Lombok 与注释风格。
- 保留现有中文 README、中文注释、类注释中的 `@author warm` 与 `@since`，不要批量改成英文或通用模板。
- 不改动 `LICENSE`，不弱化「永久开源免费、无商业版」的项目声明。

## 任务分级

使用与风险匹配的最轻流程。

- **L0**：单文件小修、注释、文档、单库 SQL 注释类改动。读相关代码，改，跑聚焦编译。
- **L1**：模块内多文件改动、服务逻辑、工具类、单个 starter 适配、单库 SQL 增改。收集上下文，简短规划，小步编辑，编译受影响模块。
- **L2**：core 公共 API / 实体 / 抽象 DAO 改动、扩展机制（`FlowEngine` / `FrameInvoker` / SPI / 自动装配）改动、跨生态（SB2/3/4/Solon）行为、跨 ORM 行为、多数据库表结构 / 升级脚本、版本发布、流程状态机语义。必要时在 `.codex/` 或 `docs/` 记录关键决策与验证细节。

## 兼容性红线（warm-flow 最关键的约束）

### 1. JDK 1.8 源码级兼容

主代码（`src/main`）只能用 Java 8 语法与 API。**禁止**：

- `var` 局部变量类型推断、`record`、`sealed`、switch 表达式 / `yield`、文本块 `"""`、增强 `instanceof` 模式匹配。
- Java 9+ 集合工厂 `List.of` / `Map.of` / `Set.of`（用 `Arrays.asList` / `Collections` / 手动构造）。
- `Optional.isEmpty()`（用 `!opt.isPresent()`）、`Stream.toList()`（用 `collect(Collectors.toList())`）、`String.isBlank()` / `String.strip()`、`Files.readString` 等 Java 9+ 新方法。
- 任何只在高版本 JDK 存在的 API。优先复用项目已有的 `org.dromara.warm.flow.core.utils.*`（`StringUtils`、`ObjectUtil`、`CollUtil`、`MapUtil`、`StreamUtils`、`ArrayUtil`、`AssertUtil` 等），引擎已自带 JDK8 下的流式 / 集合 / 字符串替代工具。

### 2. 对外契约向后兼容

- 公共类、方法签名、`FlowEngine` 门面、`WarmDao` 接口、实体字段、枚举常量（code / 顺序 / 名称）、`WarmFlow` 配置项都是已发布契约，**改动前评估对下游依赖方的破坏**；不要随意删除 / 重命名 / 改签名 / 重排枚举。
- 确需废弃时用 `@Deprecated` 并保留过渡期，新增能力优先「加法」而非「改签名」。

### 3. 框架与 ORM 解耦

- `warm-flow-core` **禁止**出现 `org.springframework.*`、`org.noear.solon.*`、`com.baomidou.*` 等具体框架 / ORM 依赖与 import（当前 core 已做到零此类依赖，必须保持）。
- 框架差异通过 `FrameInvoker`、SPI、各 starter 解决；ORM 差异通过 `WarmDao` 实现与各 orm 模块解决。

### 4. 多生态对齐

- 改某个 starter 的能力时，确认是否需要在 `sb` / `sb3` / `sb4` / `solon` 以及对应 ORM 模块同步；不要只改一处导致生态不一致。

## Java 与编码风格

- **格式基线以 `.editorconfig` 为准**：4 空格缩进、UTF-8、LF 换行、去行尾空格、文件末尾留空行（`json`/`yml`/`js` 为 2 空格）。仓库**未启用 checkstyle / spotless**，没有自动格式化兜底，提交前自觉对齐既有风格。
- 遵循引擎现有「实体 / 服务接口 + `service.impl` / 抽象 DAO + orm 实现」分层，不引入新的架构风格或 DDD 分层。
- Lombok：实体 / DTO 用 `@Getter`/`@Setter`/`@Data`，组件用构造器注入；**不要手写可由注解生成的 getter/setter**。
- 日志使用 `slf4j` + 占位符；不要 `printStackTrace()` / `System.out.println`；不打印敏感信息。
- 工具优先复用 `org.dromara.warm.flow.core.utils.*`，不为一次性需求新增通用工具类、不重复造轮子、不在 core 引入重型三方工具库。
- 注释解释意图、流程语义、边界与非显然取舍，不复述下一行代码；新增类保持标准类注释与 `@author`。
- 表达式（条件 / 办理人 / 票签）与监听器是引擎对外扩展点，新增策略沿用 `condition` / `strategy` / `listener` 既有接口与 `plugin-modes` 的 SpEL/SnEL 实现模式。

## 实体、DAO、服务与状态机

- 实体（`Definition`/`Node`/`Skip`/`Instance`/`Task`/`HisTask`/`User`/`Form`）通过 `FlowEngine.newXxx()` 创建；新增实体字段要同时考虑各 ORM 实体实现、JSON 序列化、SQL 表结构与升级脚本。
- 单表 CRUD 走 `WarmDao` 抽象与各 ORM 实现，不在 core 写死某 ORM 的查询 API。
- 流程状态、跳转、转办、加签 / 减签、终止、撤回、票签 / 会签、互斥 / 并行网关都有业务副作用与状态机约束，改动前先读对应 `service.impl`、`strategy`、`handler`、`listener` 与状态枚举，确认现有流转后再改，不要凭文件名猜行为。

## SQL 与数据库

- 表结构改动必须**同步四套脚本**：`sql/mysql`、`sql/oracle`、`sql/postgresql`、`sql/sqlserver` 的全量脚本，并在 `sql/mysql/v1-upgrade/` 新增对应版本升级脚本（命名沿用 `warm-flow_x.x.x.sql`）。
- 注意各库方言差异（类型、自增 / 序列、分页、大小写、关键字）；破坏性或迁移脚本必须写明用途、影响范围、回滚方式与各库兼容性，执行需用户明确批准。
- 引擎自身维护**多租户与逻辑删除**（见 `WarmFlow.logicDelete` 等）；改动相关字段 / 行为要兼顾「引擎自带实现」与「复用 ORM 框架实现」两条路径。

## 扩展开发指引（新增生态 / ORM / JSON / DB）

- **新增 JSON 实现**：实现 core 的 `JsonConvert`，放到 `warm-flow-plugin-json`，并加 `META-INF/services/org.dromara.warm.flow.core.json.JsonConvert`。
- **新增 ORM 支持**：在 `warm-flow-orm` 下按现有五件套（`core` + `sb`/`sb3`/`sb4` starter + `solon-plugin`）建模块，实现 `WarmDao`、实体与 ID 生成适配。
- **新增框架适配**：提供注入 `FrameInvoker` 的启动逻辑与对应自动装配文件（`spring.factories` / `AutoConfiguration.imports` / Solon properties）。
- **新增数据库**：补四套之外的脚本目录与全量脚本，并验证 `dataSourceType` / 分页 / 方言。
- 以上均为 L2，先评估必要性与对现有矩阵的影响，再实施。

## 验证规则

本仓库**无 `src/test`**（测试在独立仓库 `warm-flow-test`），验证以**分模块编译**为主，按风险选择范围：

```bash
# 全量安装到本地仓库（最稳，先装核心再装下游）
mvn clean install -DskipTests

# 聚焦编译单个模块（按改动模块替换 -pl 路径或 -f 指定 pom）
mvn -pl warm-flow-core -am -DskipTests compile
mvn -f warm-flow-orm/warm-flow-mybatis/warm-flow-mybatis-core/pom.xml -DskipTests compile
```

- 文档 / 注释改动：`git diff --check` 即可。
- core 改动：编译 core，并至少编译一个依赖它的 orm / plugin 模块。
- 某 starter / 适配改动：编译该模块，必要时编译同一 ORM 的其它 starter 确认生态一致。
- SPI / 自动装配改动：确认注册文件与实现类一致，编译相关模块。
- SQL / 表结构改动：核对四套脚本与升级脚本一致性，说明各库兼容性。

无法完成验证时必须报告：尝试的命令、失败现象、可能原因、对当前改动的风险、建议下一步。**不要为了让构建“通过”而删插件、降基线或改 JDK 版本。**

## 文档与 AI 产物归档

- 面向用户的正式文档以 README 与官网（warm-flow.com）为准；引擎细节可参考本地 `.qoder/repowiki`（未纳入仓库，仅本地可选）。
- AI / agent 的一次性调研、决策记录、临时计划放 `.codex/` 或 `docs/`，不要塞进源码包，也不要当作长期必读入口。
- 临时 / 备份代码、注释掉的旧实现、压测入口不留在源码树，确认无引用后删除（git 历史可追溯）。

## git 提交规范

提交信息前缀沿用 README 约定（中文项目）：

```
init: 初始化
feat: 增加新功能
fix: 修复问题/BUG
perf: 优化/性能提升
refactor: 重构
revert: 撤销修改
style: 代码风格相关无影响运行结果的
update: 其他修改
upgrade: 升级版本
```

## 构建与发布命令

```bash
mvn clean package -DskipTests          # 打包
mvn clean install -DskipTests          # 安装到本地仓库
mvn clean deploy -P release            # 发布中央仓库（需 release profile 与凭证）
mvn versions:set -DnewVersion=1.8.9    # 统一修改版本号
mvn versions:commit                    # 确认版本修改（versions:revert 回滚）
```

- 发布到中央仓库依赖 `release` profile（javadoc、gpg 签名、central-publishing），需本地 `settings.xml` 配置 `ossrh` 凭证与 GPG。
- `.github/workflows/release.yml` 当前是**全注释的模板（CI 未启用）**，发布是**手动**执行，不要假设 CI 会自动发版。`.gitee/` 下有中文 issue / PR 模板，提 PR 时遵循。

## 安全边界

没有用户明确批准，不要执行：

- `git commit`、`git push`、删除分支、强推或改写历史。
- `mvn deploy` / 发布到中央仓库、改版本号后推送、轮换发布凭证。
- 任务范围外的破坏性文件系统操作。
- `DROP`、批量 `DELETE`、表结构重写等破坏性数据库脚本的执行。
- 降低 JDK 基线、删除编译 / 打包插件、放宽开源协议或弱化品牌信息。

默认允许在相关任务中执行：读取文件、搜索代码、运行本地构建 / 编译、编辑项目文件、使用 `git status` / `git diff`。

## Agent 回复风格

- 简洁但具体，优先说明改了什么、验证了什么。
- 有帮助时带上文件路径。
- 如实说明不确定性、跨生态 / 兼容性残余风险。
- 不要掩盖构建或编译失败。
