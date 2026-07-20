<!-- 由代码质量审查任务生成（2026-07-20，分支 feature-ui-npm）。
     纯只读静态审查产物；Top 级发现已由主流程抽查核实（FlowTaskMapper.xml:257 缺 flow_status=、
     :62 node_type 绑定 nodeCode、UISort "asc".equals("ASC") 反向、MP @TableLogic(0/1) 硬编码、
     根 pom java17.path Windows 硬路径、TenantDeleteUtil 仅存在于 mybatis-core——全部属实）。 -->

# warm-flow-orm 代码质量审查报告

审查范围：`warm-flow-orm/` 全部 21 个 git 跟踪的 pom + 3 套 ORM 的全部 Java 源码与 mapper XML，并对照 `warm-flow-core` 的 `WarmDao`/`Page`/`WarmQuery`/`WarmFlow` 契约与 `sql/` 四库 DDL、`plugin-modes` 的 `BeanConfig` 装配链。方法：逐文件阅读 + ripgrep 全量扫描（Java9+ API、日志坏味道、license header、注册文件一致性、XML 语句清单比对）。未修改任何文件。

---

## 一、矩阵一致性（最重要）

### 1.1 租户 + 逻辑删除：引擎级能力实际上只有 mybatis 实现，MP/EQ 静默失效

【严重度 P0】【warm-flow-orm/warm-flow-mybatis/warm-flow-mybatis-core/.../utils/TenantDeleteUtil.java:39-50、warm-flow-mybatis-core/.../dao/WarmDaoImpl.java:50-129 vs warm-flow-mybatis-plus-core/.../dao/WarmDaoImpl.java:49-143、warm-flow-easy-query-core/.../dao/WarmDaoImpl.java:117-209】

mybatis 版每个 DAO 操作都会走 `TenantDeleteUtil.getEntity(entity)`，把 `FlowEngine.tenantHandler().getTenantId()` 与 `logicNotDeleteValue` 注入查询/写入条件：

```39:50:warm-flow-orm/warm-flow-mybatis/warm-flow-mybatis-core/src/main/java/org/dromara/warm/flow/orm/utils/TenantDeleteUtil.java
 public static <T extends RootEntity> T getEntity(T entity) {
 WarmFlow flowConfig = FlowEngine.getFlowConfig();
 if (flowConfig.isLogicDelete()) {
 entity.setDelFlag(flowConfig.getLogicNotDeleteValue());
 }

 TenantHandler tenantHandler = FlowEngine.tenantHandler();
 if (ObjectUtil.isNotNull(tenantHandler)) {
 entity.setTenantId(tenantHandler.getTenantId());
 }
 return entity;
 }
```

而 MP 版 `WarmDaoImpl`（如 `selectById` 直接 `getMapper().selectById(id)`，`save` 直接 `insert(entity)`）与 EQ 版（`whereById(id)`、`insertable(entity)`）**全程不接触 `FlowEngine.tenantHandler()`，也没有等价的 utils 类**；core 的 `DataFillHandler.insertFill`（warm-flow-core/.../handler/DataFillHandler.java:61-68）也只填 id/时间/办理人，不填 tenantId。结论：配置了 `warm-flow.tenant_handler_path` 的用户切到 MP/EQ 后，插入不落 tenant_id、查询不按租户过滤——**跨租户数据可见**，除非用户自行配置 MP 的 TenantLineInnerInterceptor（仓库内 0 处注册，见 1.9 同一 grep）。逻辑删除同理：EQ 的 `deleteById/deleteByIds/delete` 全是物理删除（多个 DaoImpl 内注释自认「没有启用逻辑删除，执行物理删除」，如 FlowHisTaskDaoImpl.java:81）。

建议修法：把 `TenantDeleteUtil` 语义下沉为 core 内跨 ORM 的强制切面（或在 MP/EQ 的 WarmDaoImpl 每个方法显式补条件）；EQ 侧用 easy-query 拦截器实现 tenant + logic delete；无法实现前，在 README/文档明确「引擎自带租户/逻辑删除仅 mybatis 生效」，并在 MP/EQ starter 启动时对 `tenantHandlerPath`/`logicDelete` 配置打 WARN。

### 1.2 逻辑删除值：MP `@TableLogic(value="0", delval="1")` 硬编码，与 WarmFlow 可配置值（默认删除值 "2"）冲突

【严重度 P1】【warm-flow-orm/warm-flow-mybatis-plus/warm-flow-mybatis-plus-core/.../entity/FlowDefinition.java:76-77（8 个实体同样写法）vs warm-flow-core/.../config/WarmFlow.java:61-71】

```76:77:warm-flow-orm/warm-flow-mybatis-plus/warm-flow-mybatis-plus-core/src/main/java/org/dromara/warm/flow/orm/entity/FlowDefinition.java
 @TableLogic(value = "0", delval = "1")
 private String delFlag;
```

而 core 默认 `logicDeleteValue = "2"`、`logicNotDeleteValue = "0"`（WarmFlow.java:66/71）。三套行为：mybatis 按配置（默认关，开了写 "2"）；MP **无视 `warm-flow.logic_delete` 开关永远逻辑删除**（@TableLogic 是实体级无条件生效），删除值恒 "1"；EQ 永远物理删除。同一引擎三种删除语义、两种删除标记值，数据层面互不兼容（换 ORM 后 del_flag=1 的行 mybatis 认为「未删除」除非配置匹配）。

建议修法：统一约定 del_flag 值（建议 0/1）写进 core 默认值与四库 DDL 注释；MP 侧改为读取全局配置（`GlobalConfig.DbConfig.logicDeleteValue`）或移除注解改走引擎自带路径，保证「开关 + 值」三 ORM 一致。

### 1.3 排序方向：EQ 把核心默认 "ASC" 全部当成降序执行

【严重度 P1】【warm-flow-orm/warm-flow-easy-query/warm-flow-easy-query-core/.../utils/UISort.java:36-38、50 vs warm-flow-core/.../utils/page/Page.java:65、WarmQuery.java:45/137】

```36:39:warm-flow-orm/warm-flow-easy-query/warm-flow-easy-query-core/src/main/java/org/dromara/warm/flow/orm/utils/UISort.java
 String isAsc = page.getIsAsc();
 Map<String, Boolean> queryMap = new HashMap<>();
 queryMap.put(orderBy, "asc".equals(isAsc));
 return new UISort(queryMap);
```

core 的规范值是大写：`Page.isAsc = "ASC"`、`WarmQuery.orderByAsc()` 设 `"ASC"`。`"asc".equals("ASC")` 为 false → EQ 对所有走 core API 的升序请求实际执行 **DESC**。MP 侧对应写法 `page.getIsAsc().equals(SqlKeyword.ASC.getSqlSegment())`（mp WarmDaoImpl.java:71-72）用大写比较，规范值正确但小写 "asc" 会反向；mybatis 直接字符串拼接大小写皆可。三 ORM 对同一 `isAsc` 值给出两种排序结果。

建议修法：统一 `equalsIgnoreCase("asc")`（EQ、MP 都改），或在 core `Page/WarmQuery` setter 里规范化大小写。

### 1.4 按实体查询的「有效条件字段集」三 ORM 漂移（部分条件被静默丢弃）

【严重度 P1】【多处，代表性证据如下】

- EQ `FlowDefinitionDaoImpl.buildWhereCondition`（easy-query-core/.../FlowDefinitionDaoImpl.java:64-79）缺 `category`、`activityStatus`、`listenerType`、`listenerPath`、`ext`（实体有这些字段，FlowDefinition.java:84-105）→ EQ 下按分类/激活状态筛选流程定义的条件被静默忽略，返回全部。
- EQ `FlowNodeDaoImpl.buildWhereCondition`（FlowNodeDaoImpl.java:71-92）缺 `anyNodeSkip`；EQ `FlowInstanceDaoImpl.buildWhereCondition`（FlowInstanceDaoImpl.java:54-70）缺 `activityStatus`。
- mybatis `FlowInstanceMapper.xml` 的 `select_parameter`（FlowInstanceMapper.xml:61-81）缺 `node_type` 条件（MP/EQ 都有）。
- MP 用 `new QueryWrapper<>(entity)` 全字段生效，是三者中唯一「全量」的。

同一 `service.list(entity)` 调用在三个 ORM 下 where 子句不同。

建议修法：以实体字段全集为基准补齐 EQ 的 `buildWhereCondition` 与 mybatis 的 `select_parameter`，并建立「实体字段 ↔ 条件模板」核对清单（新增字段时三处同步）。

### 1.5 easy-query sb3/sb4 starter 与 sb-starter 同 FQCN 类重复打包

【严重度 P1】【warm-flow-orm/warm-flow-easy-query/warm-flow-easy-query-sb3-starter/src/main/java/org/dromara/warm/flow/spring/boot/config/FlowAutoConfig.java:34（sb4 同）+ sb3-starter/pom.xml:17-21】

eq-sb3/sb4 各自带一份与 sb-starter **逐字节相同**的 `org.dromara.warm.flow.spring.boot.config.FlowAutoConfig`（三份文件内容一致），同时 pom 又依赖 `warm-flow-easy-query-sb-starter`（sb3-starter/pom.xml:17-21，未 exclude）→ 运行时 classpath 上同一 FQCN 出现在两个 jar 里（类加载顺序不确定、将来一旦其中一份修改即行为分叉）。对比 mybatis/mp 的 sb3/sb4 是「无源码、纯 pom 重打包」方案（warm-flow-mybatis-sb3-starter 无 src 目录），矩阵内两种互相矛盾的做法。

建议修法：删除 eq-sb3/sb4 中的重复类与重复注册文件，向 mybatis/mp 的纯 pom 方案对齐。

### 1.6 Solon 侧「after 钩子」三种私搭写法，存在装配顺序与重复 Bean 风险

【严重度 P2】【三个 solon FlowAutoConfig + plugin-modes-solon/.../BeanConfig.java:140-156】

SB 版 `BeanConfig.initFlow()` 内置 `after(warmFlow)` 钩子（plugin-modes-sb/.../BeanConfig.java:141-153）；Solon 版 `BeanConfig` **没有 after 钩子**，于是三个 ORM 各自发明：

- mybatis：`@Bean WarmFlow initFlow(@Db Configuration db1Cfg, WarmFlow flowConfig)`（mybatis-solon-plugin/.../FlowAutoConfig.java:36-40）——再注册一个 WarmFlow 类型 Bean；
- mybatis-plus：`@Bean WarmFlow after()` 里 `return FlowEngine.getFlowConfig()`（mp-solon-plugin/.../FlowAutoConfig.java:36-41）——**无任何注入参数保证 `BeanConfig.initFlow` 先执行**，若先跑则 `getFlowConfig()` 为 null（副作用 `IdUtils.setInstanceNative` 虽仍执行，但注册出 null Bean/时序不定）；
- easy-query：`@Bean WarmFlow after(@Db EasyEntityQuery entityQuery, WarmFlow flowConfig)`（eq-solon-plugin/.../FlowAutoConfig.java:40-50），且**覆盖**了 BeanConfig 里设置的 `FrameInvoker.setBeanFunction`，判断式 `clazz.isAssignableFrom(EasyEntityQuery.class)` 方向可疑（对 `getBean(Object.class)` 也会返回 entityQuery）。

另外 Solon 下 `plugin-modes-solon` 插件已 `beanMake(BeanConfig.class)`，各 ORM 插件又 `beanMake(FlowAutoConfig.class)`（其为 BeanConfig 子类）——继承的 `@Bean` 方法是否被二次注册取决于 Solon 的方法扫描实现，需实测确认（本次静态审查无法定论，标记验证项）。

建议修法：在 solon 版 `BeanConfig` 补一个与 SB 对齐的 `after(WarmFlow)` 模板方法，三个 ORM 统一 override，废弃三种私搭 `@Bean WarmFlow`。

### 1.7 mybatis：SB 设置 `setJdbcTypeForNull(JdbcType.NULL)`，Solon 不设置

【严重度 P2】【warm-flow-mybatis-sb-starter/.../FlowAutoConfig.java:60 vs warm-flow-mybatis-solon-plugin/.../WarmFlowDaoSolonPlugin.java:39-59】

SB 版 `loadXml` 里 `configuration.setJdbcTypeForNull(JdbcType.NULL)`；Solon 版只解析 XML 不设置该项。动态 insert/update 传 null 参数时（引擎 XML 大量 `<if>` 但仍有 null 经过，如批量 insert `#{item.xxx}` 不带 `<if>`），Oracle 驱动会抛「无效的列类型: 1111」——同一 ORM 在 SB 正常、Solon 报错。顺带：该行**改的是宿主应用全局 mybatis Configuration**，对宿主有副作用，建议文档声明。

建议修法：Solon 事件回调中补 `e.setJdbcTypeForNull(JdbcType.NULL)`，或两侧都改为仅对 warm-flow 语句生效的方案。

### 1.8 MP 分页依赖宿主注册 PaginationInnerInterceptor，warm-flow 未注册、未校验

【严重度 P1】【warm-flow-mybatis-plus-core/.../WarmDaoImpl.java:66-83；全仓 `MybatisPlusInterceptor|PaginationInnerInterceptor` 0 命中】

MP 版 `selectPage` 直接 `getMapper().selectPage(pagePlus, queryWrapper)`。仓库内没有任何地方注册 MP 分页拦截器，宿主应用若未自行配置，MP 的 selectPage **不截断 SQL、全量拉取**（MyBatis-Plus 已知行为），UI 的任务/定义分页列表在大表下变全表扫描且「看起来正常」。mybatis 版自带方言分页、EQ 自带 `toPageResult`，只有 MP 有此隐性前置条件。

建议修法：starter 里 `@ConditionalOnMissingBean` 注册带 PaginationInnerInterceptor 的 MybatisPlusInterceptor，或启动时检测缺失并 WARN + 文档写明。

### 1.9 注册文件矩阵本身核对结果

【严重度 P3（记录）】SB2 `spring.factories` 与 SB3/4 `AutoConfiguration.imports` 在 mybatis/mp/eq 的 sb-starter 内均成对存在且指向同一 `FlowAutoConfig`（内容一致）；solon 三个插件 properties 分别指向 `WarmFlowDaoSolonPlugin`（mybatis、mp）与 `XPluginImpl`（eq）——**插件类命名不统一**（P3，建议 eq 更名对齐）；mybatis/mp 的 sb3/sb4 无自身注册文件、复用传递进来的 sb-starter 注册文件（可行，但与 eq 方案冲突，见 1.5）。

### 1.10 其它对齐差异（合并列出）

【严重度 P2】【warm-flow-mybatis-core/.../WarmDaoImpl.java:65-75 vs mp WarmDaoImpl.java:66-83、eq WarmDaoImpl.java:132-144】mybatis 版 `selectPage` 返回 `new Page<>(list, total)` **不回填 pageNum/pageSize**（返回体里恒为默认 1/10），MP/EQ 均显式回填；且 mybatis 版先 `page.setPageNum((pageNum-1)*pageSize)`（oracle 再改 pageSize）**破坏性修改调用方传入的 Page 对象**，复用该对象二次查询会错页。建议：不改入参、返回时回填原始分页参数。

【严重度 P2】批量能力：mybatis `saveBatch` 单条多值 INSERT（分 mysql/oracle 方言）、MP `saveBatch` 循环单条 insert（N 条 SQL，N+1 风味）、EQ 走真 JDBC batch；`updateBatch` 三家都是循环单条。性能特征差异大，且循环中途失败无事务兜底（事务归宿主）。建议 MP 侧改用 `Db.saveBatch` 或 SqlSession batch；文档声明事务边界。

【严重度 P3】EQ `FlowHisTask` 多出 `@ColumnIgnore private String createBy`（easy-query-core/.../FlowHisTask.java:123-125），mybatis/MP 版没有该字段；EQ `FlowDefinitionDaoImpl.queryByCodeList` 独有 `.useInterceptor()`（FlowDefinitionDaoImpl.java:42，全 orm 仅此一处），语义来源不明。建议对齐或注释说明。

---

## 二、重复代码

【严重度 P2】【eq 三个 starter 的 FlowAutoConfig.java（sb/sb3/sb4 逐字节相同，各 45 行）】纯复制，见 1.5，应只保留 sb-starter 一份。

【严重度 P2】【warm-flow-mybatis-sb-starter/.../FlowAutoConfig.java:55-70 与 warm-flow-mybatis-solon-plugin/.../WarmFlowDaoSolonPlugin.java:39-59】同一份 8 个 XML 的 mapperList + XMLMapperBuilder 加载逻辑复制两份（mp-solon 又一份，mp-solon-plugin/.../WarmFlowDaoSolonPlugin.java:39-59）。可下沉到 mybatis-core（或 mp-core）的一个静态工具 `WarmFlowMapperLoader.load(Configuration)`，三处调用。

【严重度 P2】【warm-flow-mybatis-core/src/main/resources/warm/flow/*.xml】8 份 XML 中 `paging_end`、`order_by`、oracle ROWNUM 分页骨架、`deleteById/updateByIdLogic/deleteByIds/updateByIdsLogic` 模板完全重复 8 次（例如 FlowDefinitionMapper.xml:51-59 与 FlowUserMapper.xml:34-42 全同）。这正是 1.4/三章多处单点笔误（#{id}、#{entity.skip}、缺 `flow_status =`）的温床。建议引入代码生成或至少建立模板 + diff 校验脚本。

【严重度 P3】【warm-flow-mybatis-plus-core/src/main/resources/warm/flow/*.xml（8 个文件共 62 行）】MP 的 8 份 mapper XML 全是**空壳**（只有 namespace，无任何语句），却仍被 mp-solon 插件逐个解析加载。属于从 mybatis 模块复制后掏空的遗留物。建议删除文件并同步删除 solon 插件的加载列表。

【严重度 P3】【warm-flow-easy-query-core/.../FlowUserDaoImpl.java:88 与 92】`buildWhereCondition` 中 `o.createBy().eq(...)` 重复两次。删一处。

【严重度 P3】【三 ORM 的 8 个实体 ×3 份复制】字段注释已开始漂移：EQ `FlowHisTask.formPath` 注释写成「审批表单是否自定义（Y是 2否）」（easy-query-core/.../FlowHisTask.java:131-132），mybatis 版 `FlowHisTask.formPath` 注释也复制错为「审批表单是否自定义」（mybatis-core/.../FlowHisTask.java:160-163）。实体无法下沉（ORM 注解不同），但注释/字段清单应以 core 接口为基准统一校对。

【严重度 P3】【5 份 sb3/sb4 pom 的 maven-compiler/jar 插件配置重复】（mybatis-sb3/sb4、mp-sb3、eq-sb3/sb4）内容相同的 fork-JDK17 配置复制五份且与 mp-sb4 不一致，建议抽成父 pom 的 pluginManagement + profile。

---

## 三、正确性缺陷

### 3.1 flow_task updateById：flowStatus 分支缺 `flow_status =` 前缀，生成非法 SQL

【严重度 P1】【warm-flow-orm/warm-flow-mybatis/warm-flow-mybatis-core/src/main/resources/warm/flow/FlowTaskMapper.xml:257】

```255:258:warm-flow-orm/warm-flow-mybatis/warm-flow-mybatis-core/src/main/resources/warm/flow/FlowTaskMapper.xml
 <if test="nodeName != null and nodeName != ''">node_name = #{nodeName},</if>
 <if test="nodeType != null">node_type = #{nodeType},</if>
 <if test="flowStatus != null and flowStatus != ''">#{flowStatus},</if>
 <if test="definitionId != null">definition_id = #{definitionId},</if>
```

带 flowStatus 的任务更新会生成 `SET node_code=?, ?, definition_id=?` → SQLSyntaxErrorException。`IWarmService.updateById` 是公开 API，下游可直接触发。修法：改为 `flow_status = #{flowStatus},`。

### 3.2 flow_task 条件拼错参数：node_type 绑定了 nodeCode

【严重度 P1】【同文件:62】

```62:62:warm-flow-orm/warm-flow-mybatis/warm-flow-mybatis-core/src/main/resources/warm/flow/FlowTaskMapper.xml
 <if test="entity.nodeType != null">and t.node_type = #{entity.nodeCode}</if>
```

按 nodeType 查任务时用 nodeCode 值比较 node_type，查询结果恒错（一般为空）。修法：`#{entity.nodeType}`。

### 3.3 flow_his_task 条件参数漏 `entity.` 前缀 → BindingException

【严重度 P1】【FlowHisTaskMapper.xml:74】`<if test="entity.id != null ">and t.id = #{id}</if>`——多 @Param 语句中无顶层 `id` 参数，`selectList/selectCount` 带 id 条件即抛 `Parameter 'id' not found`。修法：`#{entity.id}`。

### 3.4 flow_skip 两处参数错写 → 运行时异常

【严重度 P1】【FlowSkipMapper.xml:66、70-72】`and t.skip_name = #{entity.skip}`（FlowSkip 无 `skip` 属性，OGNL ReflectionException）与 `and t.skip_condition =#{skipCondition}`（无顶层参数，BindingException）。修法：`#{entity.skipName}` / `#{entity.skipCondition}`。

### 3.5 flow_user 两处参数错写 → BindingException

【严重度 P1】【FlowUserMapper.xml:204、330】`listByProcessedBys` 单办理人分支 `and t.processed_by = #{processedBy}`（应为 `#{entity.processedBy}`；DAO 恰好在 `processedBys.size()==1` 时走此分支，FlowUserDaoImpl.java:71-74，属可达路径）；`updateLogic` 中 `and type = #{type}`（应为 `#{entity.type}`）。

### 3.6 flow_user.type 列的方言引用错乱：Oracle/SQL Server 直接报错

【严重度 P1】【FlowUserMapper.xml:178-190、212-224 + sql/oracle/oracle-wram-flow-all.sql:283】

`listByAssociatedAndTypes` 只对 postgresql 用 `t."type"`，其余（含 oracle、sqlserver）用反引号 `` t.`type` `` → Oracle ORA-00911、SQL Server 语法错误；`listByProcessedBys` 把 oracle 归入双引号分支，但 Oracle DDL 建列是**未加引号的 `TYPE`**（存储为大写），`t."type"`（小写带引号）→ ORA-00904 不存在的列。即：涉及 flow_user 按 type 过滤的路径在 Oracle 两种写法都错、SQL Server 反引号也错。修法：`type` 在四库都不是必须引用的保留字，统一写裸列名 `t.type`（PostgreSQL 建表用了 `"type"` 小写，裸写亦匹配）。

### 3.7 SQL Server 分页完全缺失 + dataSourceType 探测值对不上

【严重度 P1】【8 份 XML 的 paging_end（如 FlowDefinitionMapper.xml:51-55、180-197）+ CommonUtil.java:34-59】

分页只有两个分支：oracle ROWNUM 与 `LIMIT #{pageSize} offset #{pageNum}`；SQL Server 不支持 LIMIT/OFFSET 语法（需 `OFFSET..FETCH`），而 `sql/sqlserver/sqlserver.sql` 又是官方交付的四库之一 → mybatis ORM 在 SQL Server 上分页必报错。且 `CommonUtil.setDataSourceType` 自动探测取 `getDatabaseProductName().toLowerCase()`，SQL Server 返回 "microsoft sql server"，与任何分支都不匹配、兜底 mysql。修法：XML 增加 sqlserver 分支（OFFSET..FETCH），探测值做规范化映射（contains("sql server") → "sqlserver"）。

### 3.8 flow_form updateById 硬编码 `now()`，Oracle/SQL Server 方言不兼容

【严重度 P2】【FlowFormMapper.xml:250-267】`<trim prefix="SET" ...>...</trim>, update_time=now()`：`now()` 在 Oracle（SYSDATE）/SQL Server（GETDATE()）非法；且当 trim 内容全空时生成 `update flow_form , update_time=now()` 非法 SQL；还与其它 7 张表「update_time 用 `#{updateTime}`」的写法不一致（form 的 updateTime 实体值被忽略）。修法：与其它 mapper 对齐，用 `<if test="updateTime != null">update_time = #{updateTime},</if>` 放入 trim。

### 3.9 flow_node insert 的 permission_flag 误嵌套在 nodeName 条件内

【严重度 P2】【FlowNodeMapper.xml:272-274、296-298】

```272:274:warm-flow-orm/warm-flow-mybatis/warm-flow-mybatis-core/src/main/resources/warm/flow/FlowNodeMapper.xml
 <if test="nodeName != null and nodeName != ''">node_name,
 <if test="permissionFlag != null and permissionFlag != ''">permission_flag,</if>
 </if>
```

nodeName 为空时 permission_flag 被静默丢弃（列/值两侧同构，不炸 SQL 但丢数据）。修法：解开嵌套，两个 `<if>` 平级。

### 3.10 `${order}` 排序拼接是 SQL 注入通道（mybatis 与 MP 双双存在）

【严重度 P1】【8 份 XML 的 `order_by`（如 FlowUserMapper.xml:40-42）+ mybatis WarmDaoImpl.java:71/84 + mp WarmDaoImpl.java:70-72】

mybatis：`order by ${order}`，order = `page.getOrderBy() + " " + page.getIsAsc()` 原样拼接；MP：`queryWrapper.orderBy(..., page.getOrderBy())` 列名同样不转义（MP orderBy 列名注入是官方明示风险）。core 的 `IWarmService.orderBy(String)`/`WarmQuery.orderBy(String)` 是公开 API，下游把 HTTP 参数透传即形成注入。EQ 因走属性名→列名映射（UISort camelCase + EQ 校验）是唯一安全实现。修法：core 层对 orderBy 做标识符白名单校验（`[a-zA-Z0-9_.]+`）+ isAsc 枚举化，mybatis/MP 共用校验；文档警示。

### 3.11 空集合 in() 生成非法 SQL

【严重度 P2】【FlowTaskMapper.xml:202-211（无 size>0 守卫）、mp FlowTaskDaoImpl.java:56-61（`.in(FlowTask::getNodeCode, nodeCodes)` 无条件）】`TaskServiceImpl.java:151-152` 用 `suffixNodeCodes`（可能为空列表）调用 `getByInsIdAndNodeCodes` → mybatis 生成 `in ()` 语法错误、MP 生成 `IN ()` 同错；EQ 的 `in(空)` 由框架降级为 false 条件，行为又不一致。修法：XML 加 `<if test="nodeCodes != null and nodeCodes.size() > 0">`（同文件 getByInsAndNodeCodes 的写法），MP 加 `CollUtil.isNotEmpty` 条件。

### 3.12 EQ listByTaskIdAndCooperateTypes 条件表达式写错

【严重度 P2】【warm-flow-easy-query-core/.../FlowHisTaskDaoImpl.java:68】`proxy.taskId().eq(Objects.nonNull(proxy.taskId()), taskId)`——`proxy.taskId()` 永非 null，条件恒真；taskId 为 null 时生成 `task_id = NULL` 恒假。应为 `Objects.nonNull(taskId)`。

### 3.13 flow_user 物理删除路径缺租户过滤

【严重度 P2】【FlowUserMapper.xml:341-346 vs 348-358】`deleteByTaskIds`（物理删）没有 tenant_id 条件，而同语义的 `updateByTaskIdsLogic`（逻辑删）有 `entity.tenantId` 条件；DAO 两个分支传的是同一个 `TenantDeleteUtil.getEntity(...)`（FlowUserDaoImpl.java:48-55）。修法：物理删分支补 `<if test="entity.tenantId...">and tenant_id = #{entity.tenantId}</if>`（其余表的 deleteByIds 均有，此处独漏）。

### 3.14 OGNL 数值字段与 '' 比较的隐患

【严重度 P3】【FlowHisTaskMapper.xml:289】`<if test="entity.taskId != null and entity.taskId != ''">`——taskId 是 Long，OGNL 中数值与 '' 比较按 0 处理，taskId=0 时条件被跳过；同文件其它数值字段均只判 null。统一去掉 `!= ''`。

事务边界、时间/ID 类型：orm 层无 `@Transactional`（事务在 plugin-ui controller 与宿主应用），`DefServiceImpl.removeDef` 等跨 DAO 多写操作依赖宿主事务——作为 SDK 属可接受设计，但建议在 README 明示；时间统一 `java.util.Date`、ID 统一 Long（雪花/MP IdWorker），未发现类型处理缺陷。

---

## 四、MyBatis XML 与实体一致性、四库方言

除三章已列的 3.1–3.9、3.11、3.13 外：

【严重度 P3】【FlowHisTaskMapper.xml:29/31、FlowTaskMapper.xml:18-19】resultMap 映射 `business_id`、`flow_name` 列，但 `flow_his_task`/`flow_task` 表（sql/mysql/warm-flow-all.sql 建表语句）没有这两列，`selectVo` 也不查——死映射，实体的 businessId/flowName 实际由 service 层组装。建议删映射或注释说明来源。

【严重度 P3】【FlowDefinitionMapper.xml:309-317、FlowFormMapper.xml:269-277 + FlowDefinitionMapper.java:34】`closeFlowByCodeList` 两处 XML 语句 + 一处接口方法，core 的 `FlowDefinitionDao`/`DefService` 均未声明调用（FlowFormMapper 接口甚至没有该方法）——死代码，且硬编码 `is_publish = 9`（魔法数，对应 PublishStatus.EXPIRED）。建议删除或补全调用链。

【严重度 P3】【FlowHisTaskMapper.xml:356-382 vs 440-465 vs 467-503】同文件内部不一致：`updateById` 的 SET 不含 approver；`delete` 条件含 collaborator 不含 approver；`updateLogic` 条件两者都含。建议按实体字段全集统一。

【严重度 P2（评估性结论）】四库方言总体：批量 insert 仅分 mysql（多值 VALUES，PG/SQLServer 兼容）与 oracle（`union all from dual`）两支，可用；分页缺 sqlserver（见 3.7）；`flow_user.type` 引用错乱（见 3.6）；`now()`（见 3.8）。除上述外未发现其他方言问题。

---

## 五、JDK8 兼容

**未发现。** 全目录扫描 `var`/`record`/文本块/switch 表达式/instanceof 模式/`List.of`/`Map.of`/`Optional.isEmpty`/`String.isBlank`/`strip`/`Files.readString`/Java9+ import 均 0 命中。说明：EQ DAO 中的 `.toList()`（如 easy-query-core/.../WarmDaoImpl.java:128）是 easy-query `EntityQueryable` 自身 API，非 `Stream.toList()`，不构成违规。sb3/sb4 starter 以 JDK17 编译属刻意为之（对应 SB3/4 基线）。

---

## 六、依赖与打包

【严重度 P1】【根 pom.xml:73 + warm-flow-mybatis-sb3-starter/pom.xml:63-66（另 mybatis-sb4:68-71、mp-sb3:74-77、eq-sb3:42-45、eq-sb4:58-61）】

```73:73:pom.xml
 <java17.path>D:\software\jdk\jdk-17.0.1\bin\</java17.path>
```

5 个 sb3/sb4 starter 用 `<fork>true</fork><executable>${java17.path}\javac</executable>` 编译——硬编码 Windows 盘符路径 + 反斜杠，macOS/Linux 或任何非该路径机器上 `mvn install` 直接失败（不可移植的发布构建）。且 mp-sb4（pom.xml:79-88）没有 fork 配置，同类模块两种编译方式。修法：改用 maven-toolchains-plugin 或 `<release>17</release>`，删除机器路径。

【严重度 P2】【warm-flow-mybatis-plus-sb4-starter/pom.xml:17-19 vs 根 pom.xml:96】mp-sb4 私设 `mybatis-plus.version=3.5.15`（父管理为 3.5.12，mp-sb3 又本地重复声明 3.5.12），mybatis-sb3/sb4 也各自私设 `mybatis-spring-boot3=3.0.3`/`mybatis-spring-boot4=4.0.1`——违反「版本统一在父 pom」约定，同一 ORM 各 starter 实际带出的 MP 版本不一致（3.5.12 vs 3.5.15）。修法：全部上收父 pom properties（如 `mybatis-plus4.version`）。

【严重度 P2】【warm-flow-easy-query-core/pom.xml:33-37】`com.easy-query:sql-processor`（APT 注解处理器）按默认 compile scope 声明，会**传递泄漏到所有下游使用方的运行时 classpath**。修法：加 `<scope>provided</scope>` 或 `<optional>true</optional>`（proxy 类已随 jar 发布，消费者无需 APT）。

【严重度 P3】【warm-flow-mybatis-plus-sb4-starter/target/classes/org/dromara/warm/flow/orm/jackson3/JsonConvertJackson3.class + META-INF/services/...JsonConvert】target 中残留**源码树已删除**的旧 JsonConvert 实现与 SPI 注册文件（现改为依赖 `warm-flow-plugin-json-jackson3`）。若发布前不 `mvn clean` 会把幽灵 SPI 打进 jar 造成 JsonConvert 双实现。修法：发布流程强制 clean；确认 1.8.9 正式产物无此文件。

其余核对：lombok 均 provided ✓；各模块版本均走 `${warm-flow}`/父管理 ✓；sb-starter 同时带 spring.factories + AutoConfiguration.imports 属兼容性冗余，可接受；solon 插件对 `org.noear:solon` compile scope 符合 Solon 生态惯例。

---

## 七、日志 / 异常 / 坏味道

【严重度 P2】【warm-flow-mybatis-core/.../utils/CommonUtil.java:40-51】探测数据库类型时 **两层 catch 全静默吞掉**（连 debug 日志都没有，throw 被注释），失败后兜底 mysql——Oracle 用户若取连接失败会静默用 LIMIT 分页然后在深处报错，难排查。修法：catch 中至少 `log.warn("探测数据库类型失败，dataSourceType 回退为 mysql", e)`。

【严重度 P3】【warm-flow-mybatis-sb-starter/.../FlowAutoConfig.java:67-69、mybatis-solon .../WarmFlowDaoSolonPlugin.java:56-58、mp-solon 同】XML 加载失败 `throw new RuntimeException(e)` 裸包装、无上下文信息（哪个 mapper 失败）。建议带 mapper 路径信息的专用异常/日志。

【严重度 P2】【warm-flow-easy-query-core/.../utils/UISort.java:1、FlowFormDaoImpl.java:1】两个文件**缺 Apache 2.0 license header**（仓库红线要求每个 Java 文件保留）。补齐 header 与 `@author`。

【严重度 P3】【warm-flow-mybatis-plus-core/.../keygen/MybatisPlusIdGen.java:37-43】`synchronized nextId()` 且每次调用 `FrameInvoker.getBean(IdentifierGenerator.class)`——高频取号路径上「全局锁 + 容器查找」，IdWorker/IdentifierGenerator 本身线程安全，锁多余。建议缓存 bean、去掉 synchronized。

【严重度 P3】【warm-flow-easy-query-core/.../WarmDaoImpl.java:61-63】用实例初始化块 `{ entityClass = getGenericClass(); }` + 反射取泛型，风格晦涩（可放构造器）；`TenantDeleteUtil` 类注释写「mybatis-plus 租户和逻辑删除工具类」但位于 mybatis 模块（TenantDeleteUtil.java:24-25，注释复制错误）。

【严重度 P3】【warm-flow-easy-query-core/.../FlowFormDaoImpl.java:12-17】`@className/@description` 空模板注释，与仓库类注释规范不符。

---

## Top 10 优先整改清单

| # | 项 | 严重度 | 位置 |
|---|---|---|---|
| 1 | 租户/逻辑删除仅 mybatis 生效，MP/EQ 静默失去租户隔离与逻辑删除 | P0 | 1.1（MP/EQ WarmDaoImpl 全部方法） |
| 2 | MP `@TableLogic(0/1)` 硬编码且无视开关，与引擎默认值 2/0、EQ 物理删三方分裂 | P1 | 1.2（MP 8 个实体） |
| 3 | `${order}`/MP orderBy 列名不校验的 SQL 注入通道（公开 API 可达） | P1 | 3.10（8 份 XML + 两个 WarmDaoImpl） |
| 4 | flow_task `updateById` 缺 `flow_status =`、`node_type = #{entity.nodeCode}` 两处必错 SQL | P1 | 3.1/3.2（FlowTaskMapper.xml:257、62） |
| 5 | 五处参数绑定错写（#{id}、#{entity.skip}、#{skipCondition}、#{processedBy}、#{type}）运行时异常 | P1 | 3.3–3.5 |
| 6 | flow_user.type 方言引用在 Oracle/SQL Server 全错 + SQL Server 分页缺失 + 探测值不匹配 | P1 | 3.6/3.7（FlowUserMapper.xml、CommonUtil） |
| 7 | EQ 排序方向反转（core 默认 "ASC" 被当 DESC）；EQ 条件字段缺失致过滤条件静默丢弃 | P1 | 1.3/1.4（UISort.java:38、各 buildWhereCondition） |
| 8 | MP 分页依赖宿主拦截器未注册即全表返回 | P1 | 1.8（mp WarmDaoImpl.selectPage） |
| 9 | `java17.path` Windows 硬路径 fork 编译，非 Windows 构建必失败；子 pom 私设版本漂移（MP 3.5.12/3.5.15） | P1/P2 | 6.x（根 pom.xml:73 + 5 个 starter pom） |
| 10 | eq sb3/sb4 与 sb-starter 同 FQCN 重复类打包；MP 空 mapper XML、closeFlowByCodeList 等死代码清理 | P1/P3 | 1.5/2.x/4.x |

补充说明（不确定性如实报告）：本次为纯只读静态审查，未执行编译与运行验证；「Solon 下 `BeanConfig` 与其子类 `FlowAutoConfig` 双 beanMake 是否导致继承 @Bean 方法重复注册」（1.6）依赖 Solon 版本实现细节，建议用 warm-flow-test 仓库在 Solon 3.10 下实测后再定级。
