## 介绍

此项目是极其简单的工作流，没有太多设计，代码量少，并且只有6张表，个把小时就可以看完整个设计。使用起来方便

1. 支持简单的流程流转，比如跳转、回退、审批
2. 支持角色、部门和用户等权限配置
3. 官方提供简单流程封装demo项目，很实用
4. 支持多租户
5. 支持代办任务和已办任务，通过权限标识过滤数据
6. 支持互斥网关，并行网关（会签、或签）
7. 可退回任意节点
8. 支持条件表达式，可扩展
9. 同时支持spring和solon
10. 兼容java8和java17,理论11也可以



**联系方式**

qq群：778470567  

![输入图片说明](https://foruda.gitee.com/images/1703695662316836735/8a72a5bf_2218307.png "屏幕截图")

**git地址**：https://gitee.com/warm_4/warm-flow.git



**demo项目**：  

springboot：[hh-vue](https://gitee.com/min290/hh-vue) ｜[演示地址](http://www.hhzai.top:81)  
solon：[warm-sun](https://gitee.com/min290/warm-sun.git) ｜[演示地址](http://www.warm-sun.vip)



## 快速开始

在开始之前，我们假定您已经：

* 熟悉 Java 环境配置及其开发
* 熟悉 关系型 数据库，比如 MySQL
* 熟悉 Spring Boot或者Solon 及相关框架
* 熟悉 Java 构建工具，比如 Maven

### 导入sql

导入组件目录下文件/warm-flow/sql/warm-flow.sql

### 表结构

| **#** | **数据表**      | **名称**       | **备注说明** |
| ----- | --------------- | -------------- | ------------ |
| 1     | flow_definition | 流程定义表     |              |
| 2     | flow_his_task   | 历史任务记录表 |              |
| 3     | flow_instance   | 流程实例表     |              |
| 4     | flow_node       | 流程结点表     |              |
| 5     | flow_skip       | 结点跳转关联表 |              |
| 6     | flow_task       | 待办任务表     |              |



#### **flow_definition [**流程定义表**]**

| **#** | **字段**    | **名称**                          | **数据类型**    | **主键** | **非空** | **默认值** | **备注说明** |
| ----- | ----------- | --------------------------------- | --------------- | -------- | -------- | ---------- | ------------ |
| 1     | id          | 主键id                            | BIGINT UNSIGNED | √        | √        |            |              |
| 2     | flow_code   | 流程编码                          | VARCHAR(40)     |          | √        |            |              |
| 3     | flow_name   | 流程名称                          | VARCHAR(100)    |          | √        |            |              |
| 4     | version     | 流程版本                          | VARCHAR(20)     |          | √        |            |              |
| 5     | is_publish  | 是否发布（0未发布 1已发布 9失效） | BIT(1)          |          | √        | 0          |              |
| 6     | from_custom | 审批表单是否自定义（Y是 N否）     | CHAR(1)         |          |          | 'N'        |              |
| 7     | from_path   | 审批表单路径                      | VARCHAR(100)    |          |          |            |              |
| 8     | create_time | 创建时间                          | DATETIME        |          |          |            |              |
| 9     | update_time | 更新时间                          | DATETIME        |          |          |            |              |



#### **flow_his_task [**历史任务记录表**]**

| **#** | **字段**         | **名称**                                                     | **数据类型**    | **主键** | **非空** | **默认值** | **备注说明** |
| ----- | ---------------- | ------------------------------------------------------------ | --------------- | -------- | -------- | ---------- | ------------ |
| 1     | id               | 主键id                                                       | BIGINT UNSIGNED | √        | √        |            |              |
| 2     | definition_id    | 对应flow_definition表的id                                    | BIGINT          |          | √        |            |              |
| 3     | instance_id      | 对应flow_instance表的id                                      | BIGINT          |          | √        |            |              |
| 4     | tenant_id        | 租户id                                                       | VARCHAR(40)     |          |          |            |              |
| 5     | node_code        | 开始节点编码                                                 | VARCHAR(100)    |          |          |            |              |
| 6     | node_name        | 开始节点名称                                                 | VARCHAR(100)    |          |          |            |              |
| 7     | node_type        | 开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） | BIT(1)          |          | √        |            |              |
| 8     | target_node_code | 目标节点编码                                                 | VARCHAR(100)    |          |          |            |              |
| 9     | target_node_name | 结束节点名称                                                 | VARCHAR(100)    |          |          |            |              |
| 10    | approver         | 审批者                                                       | VARCHAR(40)     |          |          |            |              |
| 11    | permission_flag  | 权限标识（权限类型:权限标识，可以多个，如role:1,role:2)      | VARCHAR(200)    |          |          |            |              |
| 12    | flow_status      | 流程状态（0待提交 1审批中 2 审批通过 8已完成 9已驳回 10失效） | BIT(1)          |          | √        |            |              |
| 13    | gateway_node     | 所属并行网关节点编码                                         | VARCHAR(40)     |          |          |            |              |
| 14    | message          | 审批意见                                                     | VARCHAR(500)    |          |          |            |              |
| 15    | create_time      | 创建时间                                                     | DATETIME        |          |          |            |              |
| 16    | update_time      | 更新时间                                                     | DATETIME        |          |          |            |              |

#### **flow_instance [**流程实例表**]**

| **#** | **字段**      | **名称**                                                     | **数据类型** | **主键** | **非空** | **默认值** | **备注说明** |
| ----- | ------------- | ------------------------------------------------------------ | ------------ | -------- | -------- | ---------- | ------------ |
| 1     | id            | 主键id                                                       | BIGINT       | √        | √        |            |              |
| 2     | definition_id | 对应flow_definition表的id                                    | BIGINT       |          | √        |            |              |
| 3     | business_id   | 业务id                                                       | VARCHAR(40)  |          | √        |            |              |
| 4     | node_type     | 结点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） | BIT(1)       |          | √        |            |              |
| 5     | node_code     | 流程节点编码                                                 | VARCHAR(40)  |          | √        |            |              |
| 6     | node_name     | 流程节点名称                                                 | VARCHAR(100) |          |          |            |              |
| 7     | flow_status   | 流程状态（0待提交 1审批中 2 审批通过 8已完成 9已驳回 10失效） | BIT(1)       |          | √        |            |              |
| 8     | create_by     | 创建者                                                       | VARCHAR(64)  |          |          |            |              |
| 9     | create_time   | 创建时间                                                     | DATETIME     |          |          |            |              |
| 10    | update_time   | 更新时间                                                     | DATETIME     |          |          |            |              |
| 11    | ext           | 扩展字段                                                     | VARCHAR(500) |          |          |            |              |

####  **flow_node [**流程结点表**]**

| **#** | **字段**        | **名称**                                                     | **数据类型**    | **主键** | **非空** | **默认值** | **备注说明** |
| ----- | --------------- | ------------------------------------------------------------ | --------------- | -------- | -------- | ---------- | ------------ |
| 1     | id              | 主键id                                                       | BIGINT UNSIGNED | √        | √        |            |              |
| 2     | node_type       | 节点类型（0开始节点 1中间节点 2结束结点 3互斥网关 4并行网关） | BIT(1)          |          | √        |            |              |
| 3     | definition_id   | 流程定义id                                                   | BIGINT          |          | √        |            |              |
| 4     | node_code       | 流程节点编码                                                 | VARCHAR(100)    |          | √        |            |              |
| 5     | node_name       | 流程节点名称                                                 | VARCHAR(100)    |          |          |            |              |
| 6     | permission_flag | 权限标识（权限类型:权限标识，可以多个，如role:1,role:2)      | VARCHAR(200)    |          |          |            |              |
| 7     | coordinate      | 坐标                                                         | VARCHAR(100)    |          |          |            |              |
| 8     | skip_any_node   | 是否可以退回任意节点（Y是 N否）                              | VARCHAR(100)    |          |          | 'N'        |              |
| 9     | version         | 版本                                                         | VARCHAR(20)     |          | √        |            |              |
| 10    | create_time     | 创建时间                                                     | DATETIME        |          |          |            |              |
| 11    | update_time     | 更新时间                                                     | DATETIME        |          |          |            |              |

#### **flow_skip [**结点跳转关联表**]**

| **#** | **字段**       | **名称**                                                     | **数据类型**    | **主键** | **非空** | **默认值** | **备注说明** |
| ----- | -------------- | ------------------------------------------------------------ | --------------- | -------- | -------- | ---------- | ------------ |
| 1     | id             | 主键id                                                       | BIGINT UNSIGNED | √        | √        |            |              |
| 2     | definition_id  | 流程定义id                                                   | BIGINT          |          | √        |            |              |
| 3     | node_id        | 当前节点id                                                   | BIGINT          |          | √        |            |              |
| 4     | now_node_code  | 当前流程节点的编码                                           | VARCHAR(100)    |          | √        |            |              |
| 5     | now_node_type  | 当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） | BIT(1)          |          |          |            |              |
| 6     | next_node_code | 下一个流程节点的编码                                         | VARCHAR(100)    |          | √        |            |              |
| 7     | next_node_type | 下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） | BIT(1)          |          |          |            |              |
| 8     | skip_name      | 跳转名称                                                     | VARCHAR(100)    |          |          |            |              |
| 9     | skip_type      | 跳转类型（PASS审批通过 REJECT驳回）                          | VARCHAR(40)     |          |          |            |              |
| 10    | skip_condition | 跳转条件                                                     | VARCHAR(200)    |          |          |            |              |
| 11    | coordinate     | 坐标                                                         | VARCHAR(100)    |          |          |            |              |
| 12    | create_time    | 创建时间                                                     | DATETIME        |          |          |            |              |
| 13    | update_time    | 更新时间                                                     | DATETIME        |          |          |            |              |

####  **flow_task [**待办任务表**]**

| **#** | **字段**        | **名称**                                                     | **数据类型** | **主键** | **非空** | **默认值** | **备注说明** |
| ----- | --------------- | ------------------------------------------------------------ | ------------ | -------- | -------- | ---------- | ------------ |
| 1     | id              | 主键id                                                       | BIGINT       | √        | √        |            |              |
| 2     | definition_id   | 对应flow_definition表的id                                    | BIGINT       |          | √        |            |              |
| 3     | instance_id     | 对应flow_instance表的id                                      | BIGINT       |          | √        |            |              |
| 4     | tenant_id       | 租户id                                                       | VARCHAR(40)  |          |          |            |              |
| 5     | node_code       | 节点编码                                                     | VARCHAR(100) |          | √        |            |              |
| 6     | node_name       | 节点名称                                                     | VARCHAR(100) |          |          |            |              |
| 7     | node_type       | 节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关） | BIT(1)       |          | √        |            |              |
| 8     | permission_flag | 权限标识（权限类型:权限标识，可以多个，如role:1,role:2)      | VARCHAR(200) |          |          |            |              |
| 9     | flow_status     | 流程状态（0待提交 1审批中 2 审批通过 8已完成 9已驳回 10失效） | BIT(1)       |          | √        |            |              |
| 10    | approver        | 审批者                                                       | VARCHAR(40)  |          |          |            |              |
| 11    | assignee        | 转办人                                                       | VARCHAR(40)  |          |          |            |              |
| 12    | gateway_node    | 所属并行网关节点编码                                         | VARCHAR(40)  |          |          |            |              |
| 13    | create_time     | 创建时间                                                     | DATETIME     |          |          |            |              |
| 14    | update_time     | 更新时间                                                     | DATETIME     |          |          |            |              |





### maven依赖

**springboot项目**

```maven
<dependency>
      <groupId>io.github.minliuhua</groupId>
      <artifactId>warm-flow-spring-boot-starter</artifactId>
      <version>最新版本</version>
</dependency>
```

**solon项目**

```maven
<dependency>
      <groupId>io.github.minliuhua</groupId>
      <artifactId>warm-flow-solon-plugin</artifactId>
      <version>最新版本</version>
</dependency>
```

**<u>注意：solon版本需要自己加载warm-flow流的xml文件，mybatis的配置加上这段后面的 &quot;classpath</u>** ***<u>:mapper/**/</u>*****<u>Mapper.xml&quot;</u>**

h![输入图片说明](https://foruda.gitee.com/images/1692858175635657150/b6eba956_2218307.png "屏幕截图")

‍

### 支持数据库类型

* [x] mysql

### 支持orm框架类型

* [x] mybatis及其增强组件





## 流程设计

### 演示图

<table>
    <tr>
        <td><img src="https://foruda.gitee.com/images/1697704379975758657/558474f6_2218307.png"/></td>
        <td><img src="https://foruda.gitee.com/images/1703576997421577844/a1dc2737_2218307.png"/></td>
    </tr>
    <tr>
        <td><img src="https://foruda.gitee.com/images/1703577051212751284/203a05b0_2218307.png"/></td>
        <td><img src="https://foruda.gitee.com/images/1703577120823449150/ba952a84_2218307.png"/></td>
    </tr>
    <tr>
        <td><img src="https://foruda.gitee.com/images/1703577416508497463/863d8da1_2218307.png"/></td>
        <td><img src="https://foruda.gitee.com/images/1703641952765512992/dc187080_2218307.png"/></td>
    </tr>
    <tr>
        <td><img src="https://foruda.gitee.com/images/1703639870569018221/453a0e0e_2218307.png"/></td>
        <td><img src="https://foruda.gitee.com/images/1703639949778635820/34a6c14e_2218307.png"/></td>
    </tr>
    <tr>
        <td><img src="https://foruda.gitee.com/images/1703640045465410604/c14affda_2218307.png"/></td>
        <td><img src="https://foruda.gitee.com/images/1703641581976369452/e4629da5_2218307.png"/></td>
    </tr>
    <tr>
        <td><img src="https://foruda.gitee.com/images/1703640080823852176/bdf9a360_2218307.png"/></td>
        <td><img src="https://foruda.gitee.com/images/1703640099939146504/b19b2b85_2218307.png"/></td>
    </tr>
    <tr>
        <td><img src="https://foruda.gitee.com/images/1703641659022331552/cc4e0af2_2218307.png"/></td>
        <td><img src="https://foruda.gitee.com/images/1703641675840058630/3430da37_2218307.png"/></td>
    </tr>
    <tr>
        <td><img src="https://foruda.gitee.com/images/1703641687716655707/62a8b20c_2218307.png"/></td>
        <td><img src="https://foruda.gitee.com/images/1703641702939748288/6da6c4f6_2218307.png"/></td>
    </tr>
</table>


### 新增定义

流程编码和流程版本：确定唯一

审批表单路径：记录代表任务需要显示的代表信息页面

![输入图片说明](https://foruda.gitee.com/images/1703667450784737720/940b2bab_2218307.png "屏幕截图")



### 流程绘制

前端通过logic-flow画图，得到的json转成流程组件所需的xml格式

后台解析xml保存流程表flow_definition、flow_node、flow_skip

![输入图片说明](https://foruda.gitee.com/images/1703668217542373017/a168e1e0_2218307.png "屏幕截图")
![输入图片说明](https://foruda.gitee.com/images/1703668142615887253/95a1485a_2218307.png "屏幕截图")

   

## 流程流转

### 开启流程实例

demo项目已经准备了五套流程，以及开启流程代码，开启流程会直接执行到开始节点后一个节点

![输入图片说明](https://foruda.gitee.com/images/1703668403710988300/77dd7ef4_2218307.png "屏幕截图")

![输入图片说明](https://foruda.gitee.com/images/1703668613165514018/981e60e4_2218307.png "屏幕截图")









### 提交流程

提交流程后，流程流转到代表任务，由流程设计中的对应权限人去办理

![输入图片说明](https://foruda.gitee.com/images/1703668493778770778/d77716b5_2218307.png "屏幕截图")

![输入图片说明](https://foruda.gitee.com/images/1703668693940367665/c7206c53_2218307.png "屏幕截图")



### 办理流程

如果是互斥网关则会判断是否满足条件

![输入图片说明](https://foruda.gitee.com/images/1703668882786849328/0b9554ec_2218307.png "屏幕截图")
![输入图片说明](https://foruda.gitee.com/images/1703668896500858952/c9dc78e1_2218307.png "屏幕截图")

![输入图片说明](https://foruda.gitee.com/images/1703669015017157051/5c881c49_2218307.png "屏幕截图")

### 驳回流程

![输入图片说明](https://foruda.gitee.com/images/1703669345903195445/4ba131bc_2218307.png "屏幕截图")

## 流程图

流程图根据前端返回的节点坐标，通过后端Graphics2D进行绘制，最终返回图片给前端展示

![输入图片说明](https://foruda.gitee.com/images/1703669461653266881/c3ddafb1_2218307.png "屏幕截图")

![输入图片说明](https://foruda.gitee.com/images/1703669506555479009/bd1b51cf_2218307.png "屏幕截图")

## 条件表达式

目前内置了大于、大于等、等于、不等于、小于、小于等于、包含、不包含，并且支持扩展

扩展需要实现ExpressionStrategy.java或者继承ExpressionStrategyAbstract.java

并且通过这个方法进行注册ExpressionUtil.setExpression

![输入图片说明](https://foruda.gitee.com/images/1703669588889979582/cbe952be_2218307.png "屏幕截图")

![输入图片说明](https://foruda.gitee.com/images/1703669685489610156/a8e6be49_2218307.png "屏幕截图")



## 流程规则

 [流程规则 - Wiki - Gitee.com](https://gitee.com/warm_4/warm-flow/wikis/流程规则)



## 常见问题

[常见问题 - Wiki - Gitee.com](https://gitee.com/warm_4/warm-flow/wikis/常见问题)



## **更新记录和未来计划** 

[更新记录和未来计划 - Wiki - Gitee.com](https://gitee.com/warm_4/warm-flow/wikis/更新记录和未来计划?sort_id=8390375)



## 你可以请作者喝杯咖啡表示鼓励

![输入图片说明](https://foruda.gitee.com/images/1697770422557390406/7efa04d6_2218307.png "屏幕截图")