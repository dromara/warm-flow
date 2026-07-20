<!-- 由代码质量审查任务生成（2026-07-20，分支 feature-ui-npm，HEAD 37b17a89）。
     纯只读静态审查产物；Top 级发现已由主流程抽查核实（getNextByCheckGateway 返回 null、
     FlowEngine 恒 null 服务字段、AddCopyrightHeader 混入 src/main 等）。 -->

# warm-flow-core 只读代码质量审查报告

审查范围：`warm-flow-core/src/main`（119 个 Java 文件，无 `src/test`），未修改任何文件。所有发现均基于当前工作区代码（HEAD `37b17a89`，工作树干净）。下文路径省略前缀 `warm-flow-core/src/main/java/org/dromara/warm/flow/core/`。

---

## 一、正确性缺陷

### 1.1【P1】`getNextByCheckGateway` 返回 null 引发 NPE 链

【文件:行号】`service/impl/NodeServiceImpl.java:242-244`、`service/impl/InsServiceImpl.java:78-82`、`service/impl/TaskServiceImpl.java:199-204`、`NodeServiceImpl.java:276-279`

网关节点无出线时返回 `null` 而非空集合/业务异常：

```239:244:warm-flow-core/src/main/java/org/dromara/warm/flow/core/service/impl/NodeServiceImpl.java
        if (NodeType.isGateWay(nextNode.getNodeType())) {
            List<Skip> skipsGateway = StreamUtils.filter(flowCombine.getAllSkips()
                , skip -> nextNode.getNodeCode().equals(skip.getNowNodeCode()));
            if (CollUtil.isEmpty(skipsGateway)) {
                return null;
            }
```

三处调用方未判空：`InsServiceImpl.start` L82 `setStartInstance(nextNodes.get(0), ...)` 直接 NPE；`TaskServiceImpl.skip` L204 `pathWayData.getTargetNodes().addAll(nextNodes)` NPE；`NodeServiceImpl` 自身递归 L277-278 `newNextNodes.addAll(nodeList)` NPE。流程图配置缺陷导致的是无上下文 NPE 而非 `FlowException`。

【建议】返回 `Collections.emptyList()` 或直接 `AssertUtil.isEmpty(skipsGateway, NULL_DEST_NODE)` 抛业务异常；调用方对返回值统一 `CollUtil.isNotEmpty` 防御。签名不变，向后兼容。

### 1.2【P1】断言前解引用，空指针先于业务异常

【文件:行号】`service/impl/TaskServiceImpl.java:245-247`

```245:247:warm-flow-core/src/main/java/org/dromara/warm/flow/core/service/impl/TaskServiceImpl.java
        Instance instance = FlowEngine.insService().getById(instanceId);
        flowParams.variable(MapUtil.mergeAll(instance.getVariableMap(), flowParams.getVariable()));
        AssertUtil.isNull(instance, ExceptionCons.NOT_FOUNT_INSTANCE);
```

实例不存在时 L246 先抛 NPE，L247 的 `NOT_FOUNT_INSTANCE` 永远到不了。且 L246 与 L251 **重复执行**同一 merge。同型问题：`service/impl/DefServiceImpl.java:266-268` `getById(id).copy()` 在 L268 `AssertUtil.isNull(definition, ...)` 之前解引用（copy 后对象也永不为 null，断言彻底失效）。

【建议】把 `AssertUtil.isNull` 挪到首次解引用之前；删除重复 merge。纯内部顺序调整，无兼容风险。

### 1.3【P2】`ListenerUtil.execute` 下标错位与提前 return

【文件:行号】`utils/ListenerUtil.java:98-113`

```98:104:warm-flow-core/src/main/java/org/dromara/warm/flow/core/utils/ListenerUtil.java
            String[] listenerTypeArr = listenerTypes.split(",");
            for (int i = 0; i < listenerTypeArr.length; i++) {
                String listenerType = listenerTypeArr[i].trim();
                if (listenerType.equals(type)) {
                    if (StringUtils.isNotEmpty(listenerPaths)) {
                        String[] listenerPathArr = listenerPaths.split(FlowCons.SPLIT_AT);
                        String listenerPath = listenerPathArr[i].trim();
```

`listenerTypeArr[i]` 与 `listenerPathArr[i]` 共用下标，设计器数据中 types 数量多于 paths 时抛 `ArrayIndexOutOfBoundsException`，无兜底。另 L111-113：表达式监听器命中后 `return` 退出**整个方法**，同一节点配置的后续同类型监听器全部被跳过（语义上应为 `continue`）。

【建议】循环前校验两数组长度并抛带 nodeCode 上下文的 `FlowException`；`return` 改 `continue`（需确认「优先执行表达式监听器」的预期语义后修正）。

### 1.4【P2】条件表达式解析裸奔

【文件:行号】`condition/AbstractConditionStrategy.java:56-61`、`condition/ConditionStrategyEq.java:34-39`

```56:61:warm-flow-core/src/main/java/org/dromara/warm/flow/core/condition/AbstractConditionStrategy.java
    public Boolean eval(String expression, Map<String, Object> variable) {
        String[] split = expression.split(FlowCons.SPLIT_VERTICAL);
        preEval(split[0].trim(), variable);
        String variableValue = String.valueOf(variable.get(split[0].trim()));
        return afterEval(split[1].trim(), variableValue);
    }
```

表达式缺 `|`（如 `eq@@flag`）时 `split[1]` 抛 AIOOBE；`ConditionStrategyEq` L36：value 是数字而变量值不是数字时 `MathUtil.determineSize` 内 `new BigDecimal(variableValue)` 抛裸 `NumberFormatException`，均无表达式上下文。

【建议】`eval` 中校验 `split.length == 2` 否则抛 `FlowException("非法条件表达式: " + expression)`；`determineSize` 调用点捕获转译。

### 1.5【P2】静态可变共享状态（并发面）

- `strategy/ConditionStrategy.java:33`、`HandlerStrategy.java:34`、`ListenerStrategy.java:31`、`VoteSignStrategy.java:33`：接口常量 `EXPRESSION_STRATEGY_LIST = new ArrayList<>()` 是公共静态可变、非线程安全集合；`ExpressionUtil.setExpression`（`utils/ExpressionUtil.java:59-61`）追加无去重——Spring 容器多次刷新（devtools/测试多 context）会重复注册，且 `getValue` L159 倒序匹配使行为随注册次数漂移。
- `invoker/FrameInvoker.java:27` `public static FrameInvoker frameInvoker = new FrameInvoker<>();` 原始类型+公共可变静态字段；`beanFunction`/`cfgFunction` 非 volatile，启动期跨线程可见性无保证。
- `utils/IdUtils.java:36-77`：`instance` 有 volatile+DCL，但 `instanceNative`（L41）非 volatile 且由 `setInstanceNative` 异线程写入；`nextId(workerId, datacenterId)` 的参数仅首次初始化生效，后续调用传不同值被静默忽略。
- `enums/ChartStatus.java:51-53` `CUSTOM_COLOR` 等三个静态 HashMap 由 `init` 写、运行期读，同属启动期竞态窗口（实际风险低）。

【建议】策略列表改 `CopyOnWriteArrayList` + 注册时按 `getType()` 去重；`FrameInvoker` 字段降为 private + volatile；`instanceNative` 加 volatile。均为实现细节，不破坏契约。

### 1.6【P2】`FlowEngine` 服务字段恒为 null，缓存失效

【文件:行号】`FlowEngine.java:41-49, 231-237`

```41:43:warm-flow-core/src/main/java/org/dromara/warm/flow/core/FlowEngine.java
    private static final DefService defService = null;
    private static final NodeService nodeService = null;
    private static final SkipService skipService = null;
```

9 个 `static final … = null` 字段永远为 null，`getObj(t, tClass)`（L231-237）的第一分支永假，**每次** `FlowEngine.xxxService()` 都走 `FrameInvoker.getBean` 查容器；bean 缺失时返回 null，调用点直接 NPE 且无提示。这组字段是死代码兼性能损耗。

【建议】保持方法签名不变，把字段改为非 final 可写缓存（首次 getBean 后缓存），或至少在 getBean 返回 null 时抛出「未接入 starter/未注册 XxxService」的明确异常。

### 1.7【P2】流程图元数据 `map.get` 后未判空

【文件:行号】`service/impl/ChartServiceImpl.java:59-63, 78-93`。`nodeMap.get(node.getNodeCode()).setStatus(...)`（L59）、`skipMap.get(getSkipKey(skip)).setStatus(...)`（L60、L87）——实例 defJson 与最新路径数据不一致（定义被改）时 NPE。同型：`service/impl/NodeServiceImpl.java:149-150` `nodeMap.get(nodeCode)` 后直接 `node.getNodeType()`。

【建议】get 后判空跳过或抛带 nodeCode 的 `FlowException`。

### 1.8【P2】`importDef` 路径 `activityStatus` 未初始化即被解引用

【文件:行号】`service/impl/InsServiceImpl.java:67`、`utils/FlowConfigUtil.java:43-79`、`dto/DefJson.java:215-229`

`InsServiceImpl.start` L67 `definition.getActivityStatus().equals(ActivityStatus.SUSPENDED.getKey())` 直接拆箱比较；而 `FlowConfigUtil.structureFlow` 与 `DefJson.copyDef(DefJson)` 都不设置 `activityStatus`，ORM 实体（如 mybatis `FlowDefinition.java:116`）亦无 Java 默认值——导入的流程定义若 insert 显式写 NULL（全列 insert 覆盖 DB default 的常见情形），启动流程即 NPE。

【建议】L67 改用 `ActivityStatus.isSuspended(...)`（自带判空），或 `structureFlow`/`copyDef` 补默认 `ACTIVITY`。

### 1.9【P3】零散空指针/判空口径

- `service/impl/TaskServiceImpl.java:390-392` 等四处 `AssertUtil.isNull(flowParams.getHandler(), HANDLER_NOT_EMPTY)`：只挡 null 不挡空串，空字符串 handler 可穿透（应使用 `AssertUtil.isEmpty`）。
- `service/impl/TaskServiceImpl.java:262` `instance.getCreateBy().equals(...)`：createBy 为 null 时 NPE。
- `service/impl/FormServiceImpl.java:56-57` `getById(id)` 结果未判空即 `form.getIsPublish().equals(...)`，双重 NPE 面。
- `utils/page/Page.java:103-105` `pageOf(Integer, Integer)` 传 null 拆箱 NPE。

### 1.10 资源关闭与 equals/hashCode

**未发现问题**。`DefServiceImpl.importIs`（L66）、`ClassUtil.findClassesInJar`（L152）均 try-with-resources；core 实体全为接口，比较均走 `Objects.equals` 或常量在前的 `equals`，未见 equals/hashCode 定义缺陷。

---

## 二、JDK8 兼容红线

**未发现违规**。逐包正则扫描 `List.of/Map.of/Set.of/String.isBlank/strip/Stream.toList()/Optional.isEmpty/Files.readString/var/record/switch 表达式/文本块`：全部零命中；所有流式收集均为 `Collectors.toList()`，且 `utils/StreamUtils.java:47` 有明确防回归注释：`// 注意此处不要使用 .toList() 新语法 因为返回的是不可变List 会导致序列化问题`。

两条前瞻性备注（非违规，P3 记录）：

- 【P3】`utils/ServiceLoaderUtil.java:122-147` 使用 `System.getSecurityManager()`/`AccessController.doPrivileged`——JDK8 合法，但 JDK17+ 标记 deprecated for removal，未来高版本 JDK 有告警/移除风险。建议后续版本降级为直接取 ContextClassLoader。
- 【P3】`enums/ChartStatus.java:24` 与 `service/impl/ChartServiceImpl.java:34` `import java.awt.*`（`java.awt.Color`）——把桌面模块类型引入引擎核心，jlink 裁剪运行时需带 `java.desktop`。建议长期用自定义 RGB 值对象替代（需过渡期，`Color` 已出现在公共方法签名 `ChartStatus.getColorByKey` 中）。

---

## 三、API 设计与一致性

### 3.1【P2】`MapUtil.mergeAll` 双重载语义冲突

【文件:行号】`utils/MapUtil.java:66-92`。`mergeAll(Map<K,V>... maps)`（合并多个 map）与 `mergeAll(Object... values)`（把参数当 k1,v1,k2,v2 组装 map）同名共存，重载决议取决于实参静态类型，语义完全不同，极易误用；后者全仓库零调用。【建议】`Object...` 版本 `@Deprecated` 并更名（如 `ofPairs`），过渡期保留。

### 3.2【P2】DTO getter 带副作用

【文件:行号】`dto/FlowParams.java:266-290`。`getHandler()`/`getPermissionFlag()` 在 getter 内惰性调用 `PermissionHandler` 并**写回字段**、吞异常——序列化/日志打印 DTO 都会触发外部调用，行为不可预测。【建议】保留行为但抽成显式方法（如 `resolveHandler()`），getter 过渡期委托并在 javadoc 声明副作用；吞掉的异常至少 debug 日志。

### 3.3【P2】排序字段直通 SQL 的注入面与物理列名泄漏

【文件:行号】`orm/agent/WarmQuery.java:94-117, 135-161`。`orderBy = "id"/"create_time"/"update_time"` 把 snake_case 物理列名硬编码进 core 抽象；`orderByAsc(String)`/`orderBy(String)` 接受任意字符串最终拼进各 ORM 的 order by——下游若把用户输入透传即 SQL 注入。`WarmDao` 接口本身干净（无 ORM 概念泄漏），泄漏点集中在 WarmQuery 字符串列名。【建议】文档明确「orderByField 必须是白名单列」，或在 core 侧加列名合法性校验（`[a-zA-Z0-9_.]+`）。

### 3.4【P2】`FlowEngine.jsonConvert` 公共可变静态字段

【文件:行号】`FlowEngine.java:70` `public static JsonConvert jsonConvert;`——门面其余能力都有 getter/init 封装，唯独 JSON 转换器裸暴露，任何代码可随时改写。【建议】加 `jsonConvert()` 访问器与受控 setter，字段过渡期保留 public（兼容）但标注废弃。

### 3.5【P3】utils 重复能力与命名漂移（引擎自带工具矩阵混乱）

同语义多处实现（均有真实代码，行号见文件）：

| 能力 | 重复实现 |
|---|---|
| 集合含任一字符串 | `StringUtils.containsAny(Collection,String...)` L124 与 `CollUtil.containsAny(Collection,String...)` L86（完全同签名同语义） |
| 拼接 | `StringUtils.join` L414-450、`StreamUtils.join` L77-94、`CollUtil.strListToStr` L217（后者 `deleteCharAt` 只删 1 字符，多字符分隔符结果错误——自身还是 bug） |
| null 默认值 | `StringUtils.nvl` L47 与 `ObjectUtil.defaultNull` L66 |
| 字符串切分 | `StringUtils.str2List` L218、`CollUtil.strToColl` L137、`ArrayUtil.strToArrAy` L73（方法名拼写错误 "ArrAy"） |

命名漂移：`UserService`（impl L72-120）同为「查列表」混用 `getPermission`/`listByAssociatedAndTypes`/`getByAssociateds`/`listByProcessedBys`/`getByProcessedBys`（且 `getPermission` 实际返回的是 processedBy 集合，名实不符）；`IWarmService.java:93` `selectCount`（DAO 术语）与 `save/remove/getById`（MP service 术语）混搭；`CooperateType.isVoteSignRejectSpel` L169 名带 Reject 实为通用 spel 前缀判断。

【建议】保留旧方法（已发布契约），在 javadoc 互相 `@see` 指向唯一推荐实现；拼写错误方法新增正确名并 `@Deprecated` 旧名；后续大版本收敛。

### 3.6【P3】`AssertUtil` 断言语义反直觉且不对称

【文件:行号】`utils/AssertUtil.java:34-97`。方法名描述的是**抛异常的条件**（`isNull` = 为 null 就抛、`isTrue` = 为 true 就抛），与 Spring `Assert`（断言期望成立）恰好相反，对新贡献者是长期陷阱；且 `isNotEmpty(null)` 静默放过（L70-71）而 `isEmpty(null)` 抛错（L85-87），不对称。【建议】不改现有签名（大量调用+下游使用），补类级 javadoc 醒目说明语义约定，长期可新增 `throwIfNull` 风格别名。

### 3.7【P3】其他

- `handler/DefaultHandlerStrategy.java:27`：办理人策略实现放在 `handler` 包而其余策略在 `strategy`/`condition` 包，包归属漂移。
- `utils/StreamUtils.java:176` `groupByKeyFilter` 声明未使用的泛型参数 `<T>`。
- `orm/agent/WarmQuery.java:124` `desc()` javadoc 写「设置正序排列」（实为倒序），且无对称 `asc()`。
- `FlowEngine.java:199-208` `permissionHandler()` 的 javadoc 是「获取填充类」（复制自 dataFillHandler）。

---

## 四、状态机与流程语义

### 4.1【P1】一票否决/收尾清理：注释承诺「转历史任务」但实现只删除

【文件:行号】`service/impl/TaskServiceImpl.java:921-943, 946-958, 665-668`

```928:942:warm-flow-core/src/main/java/org/dromara/warm/flow/core/service/impl/TaskServiceImpl.java
    private void oneVoteVeto(Task task, String nextNodeCode, FlowCombine flowCombine) {
        // 一票否决（谨慎使用），如果退回，退回指向节点后还存在其他正在执行的待办任务，转历史任务，状态失效,重走流程。
        List<Task> tasks = list(FlowEngine.newTask().setInstanceId(task.getInstanceId()));
        // ... 省略筛选后置未完成任务 ...
        if (CollUtil.isNotEmpty(noDoneTasks)) {
            removeAndUser(noDoneTasks);
        }
    }
```

`removeAndUser`（L665-668）只做 `removeByIds` + `deleteByTaskIds`，**不生成任何 HisTask**。方法注释与 `FlowStatus.INVALID`("失效") 枚举都承诺「转历史，状态失效」，实际被否决的并行分支任务无审计痕迹。`handUndoneTask`（L946-958）注释「转历史任务，状态完成」同样只删除。这是注释与实现不符 + 历史链路缺口的双重问题。

【建议】删除前按 `hisTaskService().setSkipHisList` 生成 INVALID/FINISHED 状态历史再删；若确认现行为是设计意图，必须同步修正注释与文档（影响下游对审计数据的依赖预期）。

### 4.2【P2】`revoke` 与 `updateFlowInfo` 复制粘贴流水

【文件:行号】`service/impl/TaskServiceImpl.java:293-307` vs `969-986`。两段几乎相同的「存历史→删待办与权限人→生成新任务权限人→setInsFinishInfo→saveBatch→updateById→saveBatch(users)」序列，且两处顺序有微妙差异（revoke 先存历史再删待办，updateFlowInfo 相反），后续维护极易语义漂移。【建议】抽私有方法统一（内部重构，不动公共 API）。

### 4.3【P2】`HisTaskServiceImpl` 五处 set*His 大段重复 + 双重 setMessage

【文件:行号】`service/impl/HisTaskServiceImpl.java:100-126, 129-153, 156-183, 186-212, 219-244`。五个方法重复 15+ 行相同 setter 链；其中 `setSignHisTask` L198 与 L205 `setMessage(flowParams.getMessage())` 调用两次。【建议】抽 `baseHisTask(task, flowParams)` 私有构造器；删冗余 setMessage。

### 4.4【P2】`updateHandler` 监听器变量不完整（与其他动作不一致）

【文件:行号】`service/impl/TaskServiceImpl.java:445, 488`。`ListenerUtil.executeStart(new ListenerVariable(r.definition, r.instance, r.nowNode, null, r.task))`——variable 传 null 且未 `.setFlowParams(flowParams)`；同方法 L488 的 executeFinish 也缺 flowParams。skip/revoke/termination/pending 全部都带。转办/加减签场景监听器拿不到流程参数，属跨动作行为不一致。【建议】补齐 `flowParams.getVariable()` 与 `.setFlowParams(flowParams)`。

### 4.5【P2】重复变量合并

【文件:行号】`service/impl/TaskServiceImpl.java:170 与 213`、`246 与 251`。同一请求内 `MapUtil.mergeAll(r.instance.getVariableMap(), flowParams.getVariable())` 执行两次（`getVariableMap` 每次都反序列化 JSON），纯浪费且中间语义易混淆。【建议】合并一次后复用。

### 4.6【P3】超长方法与深嵌套

**未发现 >150 行方法**（实测最长：`TaskServiceImpl.cooperate` L737-837 约 100 行、`skip` L166-235 约 70 行、`revoke` L238-313 约 76 行）。但：`cooperate` 集中了或签/会签/票签 4 种策略 + 表达式分支，三元嵌套（L807-822）复杂度高；`ListenerUtil.execute` L96-137 六层嵌套。`getNewVersion` 在 `DefServiceImpl.java:311-343` 与 `FormServiceImpl.java:121-145` 两份相似实现（Def 有非数字版本 fallback `"_1"`，Form 没有），属复制漂移。【建议】cooperate 拆「或签/会签/票签」策略私有方法；版本号生成收敛为一个工具。

### 4.7【P3】文档与默认值矛盾

`service/TaskService.java:423, 438, 453, 468` javadoc 写「ignore 转办忽略权限校验,**默认忽略**」，而 `FlowParams.ignore` 是 boolean 默认 false（不忽略），同接口 L365（transfer）又写「默认不忽略」——三处自相矛盾。另 `TaskServiceImpl.java:173` 校验 skipType 却用 `NULL_CONDITION_VALUE`("跳转条件不能为空!") 文案。【建议】统一 javadoc 为「默认不忽略」；新增 `NULL_SKIP_TYPE_PARAM` 类文案。

---

## 五、日志与异常规范

### 5.1【P2】catch 后完全静默的吞异常清单

`printStackTrace`/`System.out` 业务代码残留：**未发现**（`ExceptionUtil.java:33` 的 `e.printStackTrace(new PrintWriter(sw))` 是写入 StringWriter 的合法用法；`WarmFlow.printBanner` L161 的 System.out 属 banner 例外；`AddCopyrightHeader` 见第六类）。但静默吞异常成体系：

| 位置 | 场景 | 后果 |
|---|---|---|
| `FlowEngine.java:258` | initBean 反射失败 `catch (Exception ignored)` | 用户配错 handler 路径静默回退，租户/权限/监听器不生效且无迹可查 |
| `invoker/FrameInvoker.java:46-51, 63-68` | getBean/getCfg catch-all 返回 null | 容器异常被吞 |
| `dto/FlowParams.java:270-273, 283-286` | PermissionHandler 调用失败 | 权限上下文静默缺失 |
| `handler/DataFillHandler.java:73-76, 97-100` | 同上 | createBy/updateBy 静默不填 |
| `utils/ExpressionUtil.java:96-99` | convertPermissions 失败 | 办理人转换静默跳过 |
| `utils/ClassUtil.java:48, 88-89` | getClazz/clone 失败返回 null | 上层拿 null 继续走 |

【建议】统一至少 `log.warn`（带类路径/key 上下文）；`ServiceLoaderUtil.java:41,63` 吞 `ServiceConfigurationError` 属可选依赖的有意设计，可保留但建议 debug 日志。

### 5.2【P2】异常丢失 cause 与上下文

【文件:行号】`utils/ReflectionUtil.java:50-51, 75-76, 107-108`。三处 `catch (Exception e) { throw new FlowException("扫描异常"); }`——`FlowException(String, Throwable)` 构造器（`exception/FlowException.java:55-58`）存在却不用，cause 丢失、消息无 interfacePath/implementPath。另：断言消息普遍无实体 id 上下文（如 `NOT_FOUNT_TASK` 不含 taskId），对比 `DefServiceImpl.checkFlowLegal` L348 拼接 flowName 的好实践。【建议】补 cause 与关键参数。

### 5.3【P2】自定义异常体系不统一

- `keygen/SnowFlakeId19.java:137`、`SnowFlakeId14.java:101`、`SnowFlakeId15.java:69` 时钟回拨抛裸 `RuntimeException`，而引擎其余全用 `FlowException`。
- `exception/FlowException.java:32, 43`：`code`/`detailMessage` 字段在整个 core 无任何赋值调用（`FlowException(String, Integer)` 零使用）——半成品异常协议。

【建议】雪花类改抛 `FlowException`（消息保留）；code 体系要么落地要么在大版本收敛。

### 5.4【P3】错误文案复制粘贴错位

- `service/impl/FormServiceImpl.java:95` 表单查不到报 `NOT_FOUNT_TASK`("未找到待办任务!")；L83 表单复制失败报 `NOT_FOUNT_DEF`("流程定义不存在!")——误导排查。
- `constant/ExceptionCons.java:58` `NULL_LISTENER_STRATEGY = "办理人表达式策略不能为空!"`（应为「监听器表达式策略」，与 L56 重复文案）。
- `handler/DataFillHandler.java:90` updateFill 日志写 "Insert operation failed"。
- `config/WarmFlow.java:168` banner 版本 `getImplementationVersion()` 非 jar 运行（IDE/单测）时为 null，打印 `v null`。

---

## 六、代码坏味道

### 6.1【P2】开发脚本混入发布 jar 且违反自家 license 红线

【文件:行号】`utils/AddCopyrightHeader.java:1-8`

```1:8:warm-flow-core/src/main/java/org/dromara/warm/flow/core/utils/AddCopyrightHeader.java
package org.dromara.warm.flow.core.utils;

import java.io.*;

public class AddCopyrightHeader {
    public static void main(String[] args) throws Exception {
        //项目的绝对路径，也就是想修改的文件路径
        String filePath = "D:\\IdeaProjects\\min\\RuoYi-Vue-Warm-Flow\\warm-flow";
```

带 `main()` 的一次性版权头插入脚本被打进中央仓库 jar：硬编码作者本机 Windows 路径、`System.out.println`（L82），且**它自己没有 Apache 2.0 header**——是全模块唯一违反「每个 Java 文件保留 license header」红线的文件。全仓库零引用。【建议】移出 `src/main`（挪 `docs/`/脚本目录或删除，git 可追溯）；对已发布版本无 API 兼容影响（无人会依赖它）。

### 6.2【P2】零引用的 public 死代码（发布契约内，需谨慎处置）

以下在**本仓库所有模块 `src/main` 零调用**（已逐一 grep 验证；因是已发布 SDK，外部可能引用，不能直接删）：

- `utils/ReflectionUtil.java` 全类（3 个 public 方法）+ 仅被它使用的 `ClassUtil.findClasses` 系（L107-163）。
- `utils/Base64.java` 全类（274 行手写 Base64，JDK8 自带 `java.util.Base64`——与 JDK 既有能力重复且零引用）。
- `utils/HttpStatus.java`：core 内零引用（仅 `warm-flow-plugin-ui-core` 的 `HandlerSelectService` 用到 `SUCCESS` 一个常量）——HTTP 状态码放引擎核心属位置错误。
- `enums/CooperateType.java:74, 179-194` 顺签特性：`SEQUENCE` 常量、`isSequenceSign`、`removeSequence` 定义后无任何调用点——未完成特性的存根。
- 其他零引用工具方法：`MapUtil.mergeAll(Object...)`、`CollUtil.strToColl/listAddToNew/listAddListsToNew/strListToStr`、`StreamUtils.toIdentityMap/groupBy2Key/group2Map`、`StringUtils.nvl/toUnderScoreCase/convertToCamelCase/inStringIgnoreCase/padLeft`、`ObjectUtil.isStrTrue`、`ServiceLoaderUtil.loadList`、`ChartStatus.getColorByKey(Integer)`。

【建议】统一加 `@Deprecated` + javadoc 说明替代（如 Base64 → `java.util.Base64`），大版本再移除；HttpStatus 建议下沉到 ui 插件（core 保留 @Deprecated 转发过渡）。

### 6.3【P3】未使用变量/误用流做副作用

- `service/impl/UserServiceImpl.java:152` `Date now = new Date();` 声明后未使用。
- `service/impl/UserServiceImpl.java:52` `StreamUtils.toList(addTasks, task -> taskUserList.addAll(taskAddUser(task)))`——用 map+collect 做副作用遍历，返回值丢弃（应 `forEach`）。
- `condition/ConditionStrategyGe.java:36`、`ConditionStrategyLe.java:36` `determineSize(...) > 0 || determineSize(...) == 0`：同一比较计算两次（应 `>= 0`）。

### 6.4【P3】魔法值未用常量/枚举

- `dto/FlowParams.java:331` `variable.put("formData", formData)`——`FlowCons.FORM_DATA` 已存在（`TaskServiceImpl` L1013 用的就是常量）。
- `dto/DefJson.java:153` `modelValue = "CLASSICS"`（应 `ModelEnum.CLASSICS.name()`）；L240 `"0"`（应 `StringUtils.ZERO`，`DefServiceImpl` L140 用的是常量）。
- `service/impl/TaskServiceImpl.java:783-790` 票签表达式变量名 `"skipType"/"passNum"/"rejectNum"/"todoNum"/"allNum"/...` 字面量入 map——这是对外表达式契约，应常量化防止改名破坏。
- `utils/ListenerUtil.java:109` `"listenerVariable"` 字面量（同为表达式契约）。
- `handler/DefaultHandlerStrategy.java:31-36` `"$"`、`"${"`、`"}"` 字面量。

### 6.5【P3】@Deprecated 无替代说明

【文件:行号】`entity/Node.java:126-136`。`getVersion/setVersion` 仅注 `@deprecated 下个版本废弃`，无迁移指引；且 `Node.copy()` L153、`FlowConfigUtil` L147、`DefServiceImpl` L94 内部仍在调用——「下个版本废弃」不可能兑现。【建议】javadoc 补「版本随 Definition 维护，使用 `Definition#getVersion`」类说明，并先清内部调用。

### 6.6【P3】注释与实现不符（除 4.1 外）

- `keygen/SnowFlakeId14.java:35-60`：注释整套从 19 位版复制——「最大机器id 结果是31」实为 7（3 bits）、「工作机器ID(0~1024)」实为 0~7、「掩码 4095」实为 31（5 bits）、「时间截向左移22位(10+12)」实为 8 位。
- `keygen/SnowFlakeId15.java:88-89`：「保证生成的ID为15位」但 `id % 10_000_000_000_000_000L` 是对 10^16 取模（16 位上限），且远期回绕理论上产生重复 id。
- `enums/CooperateType.java:183-188` `removeSequence` 的 javadoc 是 `isSequenceSign` 的原样复制（"判断是否是顺签"）。
- `json/JsonConvert.java:46` 已发布 SPI 接口方法挂着 `TODO 未测试`。
- `service/impl/TaskServiceImpl.java:167, 507` 两处 `TODO min 后续考虑并发问题...加锁`——与 `mergeVariable`（L591-598）读改写窗口一起构成**已知未处理并发缺口**，长期挂账应立项。
- `utils/StringUtils.java:29` 空字符串常量命名 `NULLPTR`，名实相悖。

---

## 七、SPI/扩展点

### 7.1【P2】`JsonConvert` SPI 加载失败静默为 null，NPE 遍地开花

【文件:行号】`config/WarmFlow.java:154-157`、`FlowEngine.java:70`、`entity/Instance.java:125-127`

```154:157:warm-flow-core/src/main/java/org/dromara/warm/flow/core/config/WarmFlow.java
    public void spiLoad() {
        // 通过SPI机制加载json转换策略实现类
        FlowEngine.jsonConvert = ServiceLoaderUtil.loadFirst(JsonConvert.class);
    }
```

未引入任何 `warm-flow-plugin-json-*` 时 `loadFirst` 返回 null 且无任何提示，之后 `Instance.getVariableMap()`（默认方法直接 `FlowEngine.jsonConvert.strToMap`）、`FlowParams.getVariableStr`、`DefServiceImpl.importJson` 等全部 NPE，错误离根因极远。【建议】`spiLoad` 后判空 `log.warn`/fail-fast：「未检测到 JsonConvert 实现，请引入 warm-flow-plugin-json 模块」。

### 7.2 注册文件与实现类一致性：核对通过

- `warm-flow-plugin-json-v1/.../META-INF/services/org.dromara.warm.flow.core.json.JsonConvert` 注册 5 个实现（Snack/Snack4/Jackson/FastJson/Gson），源码目录 5 个类**一一对应存在**；`json-jackson3` 注册 1 个、类存在。`ServiceLoaderUtil.loadFirst` 吞 `ServiceConfigurationError` 以兼容 optional 依赖缺失，机制成立。
- 【P3】隐式顺序风险：一个 services 文件 5 个实现，classpath 同时存在多个 JSON 库时选中哪个取决于文件行序（Snack 优先），用户不可配置、文档未声明。建议在文档/启动日志打印实际选中的实现类。
- 【P3】遗留构建产物：`warm-flow-orm/warm-flow-mybatis-plus/warm-flow-mybatis-plus-sb4-starter/` 仅剩 `pom.xml` + `target/`（无 `src`），target 内残留旧版 `JsonConvert` services 注册（`org.dromara.warm.flow.orm.jackson3.JsonConvertJackson3`）——本地脏产物，建议 clean，避免本地调试时被旧 SPI 干扰。

### 7.3【P2】ExpressionStrategy 注册机制：静态块 + 各框架命令式注册，无去重

【文件:行号】`utils/ExpressionUtil.java:38-51`（core 静态块注册 8 个条件策略 + DefaultHandlerStrategy）、`warm-flow-plugin-modes-sb/.../BeanConfig.java:155-161` 与 `modes-solon/.../BeanConfig.java:158-162`（各自追加 SpEL/SnEL 策略）。结合 1.5 的可变静态列表问题：容器刷新两次即重复注册，`getValue` 倒序优先规则下用户自定义策略可能被后来的重复系统策略遮蔽。**空实现处理**：`getValue` L161-163 对 list 中 null 元素抛 `NULL_CONDITION_STRATEGY`，但列表为空/无匹配时静默返回 null——`evalCondition` 于是返回 false，条件不满足与策略缺失不可区分。【建议】注册按 `getType()` 幂等去重；无匹配策略时抛「未找到表达式策略: 前缀」异常。

### 7.4 Listener/Handler 扩展点

`ListenerUtil.executeListener` L86-93 对 GlobalListener 判空后调用、`WarmServiceImpl.insertFill/updateFill` L181-196 对 DataFillHandler 判空——空实现处理正确。问题集中在 initBean 静默回退（见 5.1）与监听器路径解析健壮性（见 1.3）。

---

## Top 10 优先整改清单

| # | 严重度 | 事项 | 关键位置 |
|---|---|---|---|
| 1 | P1 | `getNextByCheckGateway` 返回 null 的 NPE 链（开流程/审批/递归三处引爆点），改空集合或业务异常 | `NodeServiceImpl:243` → `InsServiceImpl:82`、`TaskServiceImpl:204`、`NodeServiceImpl:278` |
| 2 | P1 | 断言前解引用：`revoke` 实例判空滞后（含重复 merge）、`copyDef` 判空失效 | `TaskServiceImpl:245-251`、`DefServiceImpl:266-268` |
| 3 | P1 | 一票否决/流程收尾「转历史」承诺未实现，审计链缺口（或修实现或修语义文档） | `TaskServiceImpl:921-958, 665-668` |
| 4 | P2 | `JsonConvert` SPI 缺失静默 null → 全局远端 NPE；补 fail-fast 提示并封装 `FlowEngine.jsonConvert` | `WarmFlow:154-157`、`FlowEngine:70` |
| 5 | P2 | `ListenerUtil.execute` types/paths 下标错位 AIOOBE + 表达式命中 `return` 吞掉后续监听器 | `ListenerUtil:98-113` |
| 6 | P2 | 体系性吞异常整改（initBean/FrameInvoker/FlowParams/DataFillHandler/ExpressionUtil/ClassUtil 统一 warn 日志） | `FlowEngine:258` 等 10 处 |
| 7 | P2 | 表达式策略静态可变 ArrayList：线程安全 + 注册去重 + 无匹配策略显式报错 | `ConditionStrategy:33` 等 4 处、`ExpressionUtil:59, 155-173` |
| 8 | P2 | `FlowEngine` 九个恒 null 服务字段：每次调用查容器、bean 缺失无提示；补缓存与明确异常 | `FlowEngine:41-49, 231-237` |
| 9 | P2 | 条件表达式解析健壮性（`split[1]` AIOOBE、非数字变量裸 NumberFormatException，异常带表达式上下文） | `AbstractConditionStrategy:56-61`、`ConditionStrategyEq:36` |
| 10 | P2 | `AddCopyrightHeader` 开发脚本移出 src/main（无 license header + 硬编码本机路径）；死代码工具方法批量 `@Deprecated` 规划清理 | `utils/AddCopyrightHeader`、6.2 清单 |

补充说明：第 3、4、7 项涉及对外可感知行为，按仓库规则属 L2 改动，实施前建议先在 `warm-flow-test` 仓库补对应回归用例；其余项均可在不改公共签名的前提下完成。JDK8 兼容红线本次扫描零违规，资源关闭与 equals/hashCode 未发现问题。
