## 介绍

🎉国产自研工作流，其特点简洁(只有6张表)但又不简陋，五脏俱全，组件独立，可扩展，可满足中小项目的组件。

1. 支持简单的流程流转，比如跳转、回退、审批
2. 业务项目可不依赖流程设计器，组件会生成流程图片
3. 支持角色、部门和用户等权限配置
4. 支持增加监听器，参数传递，动态权限
5. 支持多租户
6. 支持互斥网关，并行网关（或签）
7. 可跳转任意节点
8. 支持条件表达式，可扩展
9. 支持不同orm框架和数据库扩展
10. 同时支持spring和solon
11. 兼容java8和java17,理论11也可以
12. 官方提供简单流程封装demo项目，很实用

>  **希望一键三连，你的⭐️ Star ⭐️是我持续开发的动力，项目也活的更长**  
>   **可二开、商用，但请注明出处，保留代码注释中的作者名，[但是使用前请先登记](https://gitee.com/warm_4/warm-flow/issues/I7Y57D)**  
>
>   **[gitee地址](https://gitee.com/warm_4/warm-flow.git  )** |**[github地址](https://github.com/minliuhua/warm-flow.git)**


## 使用文档与联系方式
http://warm_4.gitee.io/warm-flow-doc


**demo项目**：  

springboot：[RuoYi-Vue-Warm-Flow](https://gitee.com/min290/hh-vue) ｜[演示地址](http://www.hhzai.top:81)  
solon：[warm-sun](https://gitee.com/min290/warm-sun.git) ｜[演示地址](http://www.warm-sun.vip)



## 快速开始

在开始之前，我们假定您已经：

* 熟悉 Java 环境配置及其开发
* 熟悉 关系型 数据库，比如 MySQL
* 熟悉 Spring Boot或者Solon 及相关框架
* 熟悉 Java 构建工具，比如 Maven

### 导入sql，按需求执行增量脚本
>   **如果第一次导入，请先创建数据库，并导入：https://gitee.com/warm_4/warm-flow/blob/master/sql/warm-flow.sql**  
>   **如果需要增量更新，请按需导入：https://gitee.com/warm_4/warm-flow/blob/master/sql/warm-flow_xxx.sql**


### maven依赖

**springboot项目**

```maven
<dependency>
      <groupId>io.github.minliuhua</groupId>
      <artifactId>warm-flow-mybatis-sb-starter</artifactId>
      <version>最新版本</version>
</dependency>
```

**solon项目**

```maven
<dependency>
      <groupId>io.github.minliuhua</groupId>
      <artifactId>warm-flow-mybatis-solon-plugin</artifactId>
      <version>最新版本</version>
</dependency>
```

‍

### 支持数据库类型

* [x] mysql
* [ ] oracle
* [ ] sqlserver
* [ ] ......


### 支持orm框架类型

* [x] mybatis及其增强组件
* [ ] jpa
* [ ] easy-query
* [ ] wood
* [ ] sqltoy
* [ ] beetlsql
* [ ] ......




> **有想扩展其他orm框架和数据库的可加qq群联系群主**

### 代码示例

https://gitee.com/min290/hh-vue/blob/master/ruoyi-admin/src/test/java/com/ruoyi/system/service/impl/FlowTest.java



#### 部署流程

```java
public void deployFlow() throws Exception {
        String path = "/Users/minliuhua/Desktop/mdata/file/IdeaProjects/min/hh-vue/hh-admin/src/main/resources/leaveFlow-serial.xml";
        System.out.println("已部署流程的id：" + defService.importXml(new FileInputStream(path)).getId());
    }
```

#### 发布流程

```java
public void publish() throws Exception {
        defService.publish(1212437969554771968L);
    }
```

#### 开启流程

```java
public void startFlow() {
        System.out.println("已开启的流程实例id：" + insService.start("1", getUser()).getId());
    }
```

#### 流程流转

```java
public void skipFlow() throws Exception {
        // 通过实例id流转
        Instance instance = insService.skipByInsId(1219286332141080576L, getUser().skipType(SkipType.PASS.getKey())
                .permissionFlag(Arrays.asList("role:1", "role:2")));
        System.out.println("流转后流程实例：" + instance.toString());

//        // 通过任务id流转
//        Instance instance = insService.skip(1219286332145274880L, getUser().skipType(SkipType.PASS.getKey())
//                .permissionFlag(Arrays.asList("role:1", "role:2")));
//        System.out.println("流转后流程实例：" + instance.toString());
    }

 public void skipAnyNode() throws Exception {
        // 跳转到指定节点
        Instance instance = insService.skip(1219286332145274880L, getUser().skipType(SkipType.PASS.getKey())
                .permissionFlag(Arrays.asList("role:1", "role:2")).nodeCode("4"));
        System.out.println("流转后流程实例：" + instance.toString());
    }
```


#### 参数传递

```java
// 流程变量
Map<String, Object> variable = new HashMap<>();
variable.put("testLeave", testLeave);
flowParams.variable(variable);
Instance instance = insService.skip(taskId, flowParams);
```

#### 监听器
实现Listener接口，然后在设计器中配置好监听器
```java
public class FinishListener implements Listener {

    @Resource
    private TestLeaveMapper testLeaveMapper;

    private static final Logger log = LoggerFactory.getLogger(StartListener.class);

    @Override
    public void notify(ListenerVariable variable) {
        log.info("完成监听器:{}", variable);
        Instance instance = variable.getInstance();
        Map<String, Object> testLeaveMap = variable.getVariable();
        TestLeave testLeave = (TestLeave) testLeaveMap.get("testLeave");
        /** 如果{@link com.ruoyi.system.service.impl.TestLeaveServiceImpl}中更新了，这里就不用更新了*/
//        testLeave.setNodeCode(instance.getNodeCode());
//        testLeave.setNodeName(instance.getNodeName());
//        testLeave.setFlowStatus(instance.getFlowStatus());
//        testLeave.setUpdateTime(DateUtils.getNowDate());
//        testLeaveMapper.updateTestLeave(testLeave);
        log.info("完成监听器结束;{}", "任务完成");
    }
}
```


## 你可以请作者喝杯咖啡表示鼓励

![输入图片说明](https://foruda.gitee.com/images/1697770422557390406/7efa04d6_2218307.png "屏幕截图")


### 特别感谢JetBrains对开源项目支持

<a href="https://jb.gg/OpenSourceSupport">
  <img src="https://user-images.githubusercontent.com/8643542/160519107-199319dc-e1cf-4079-94b7-01b6b8d23aa6.png" align="left" height="100" width="100"  alt="JetBrains">
</a>

