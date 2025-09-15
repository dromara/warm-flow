<p align="center">
	<img alt="logo" src="https://foruda.gitee.com/images/1726820610127990120/c8c5f3a4_2218307.png" width="100">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">Warm-Flow工作流</h1>
<p align="center">
    <a href="https://gitee.com/dromara/warm-flow.git"><img src="https://gitee.com/dromara/warm-flow/badge/star.svg?theme=dark"></a>
    <a href='https://gitee.com/dromara/warm-flow/members'><img src='https://gitee.com/dromara/warm-flow/badge/fork.svg?theme=dark' alt='fork'></a>
    <a href='https://github.com/dromara/warm-flow.git'><img src='https://img.shields.io/github/stars/dromara/warm-flow.svg' alt='fork'></a>
    <a href='https://github.com/dromara/warm-flow.git'><img src='https://img.shields.io/github/forks/dromara/warm-flow.svg' alt='fork'></a>
    <a href='https://gitcode.com/dromara/warm-flow'><img src='https://gitcode.com/dromara/warm-flow/star/badge.svg' alt='fork'></a>
    <a href='https://gitee.com/dromara/warm-flow/blob/master/LICENSE'><img src='https://img.shields.io/github/license/dromara/warm-flow' alt='fork'></a>
</p>


**项目代码、文档 均开源免费可商用 遵循开源协议即可**

**过去、现在和未来都不会有商业版！！！**


**开发完成请务必登记使用项目列表，[登记地址](https://gitee.com/dromara/warm-flow/issues/I7Y57D)**

## 介绍

> Dromara Warm-Flow，国产的工作流引擎，以其简洁轻量、五脏俱全、灵活扩展性强的特点，成为了众多开发者的首选。它不仅可以通过**jar包快速集成设计器**，同时原生支持**经典和仿钉钉双模式**，还具备以下显著优势：

- **简洁易用**‌：仅包含7张表，代码量少，上手和集成速度快。
- **审批功能全面**‌：支持通过、退回、驳回到上一个任务、撤销、拿回、任意跳转、转办、终止、会签、票签、委派、加减签、互斥和并行网关等多种审批操作，以及条件表达式、办理人表达和监听器等高级功能。
- **流程设计器**‌：通过jar包形式快速集成到项目，支持节点属性扩展，原生支持经典和仿钉钉双模式。
- **流程图**‌：自带流程图，通过jar包快速集，功能扩展，原生支持经典和仿钉钉双模式。
- **条件表达式**‌：内置常见的和spel条件表达式，支持自定义扩展。
- **办理人变量表达式**‌：内置${handler}和spel格式的表达式，满足不同场景需求，灵活可扩展。
- **监听器**‌：提供四种监听器，支持不同作用范围和spel表达式，参数传递灵活，支持动态权限。
- **流程变量**‌：在整个流程办理过程起到重要的角色，如办理人表达式中，传入变量进行动态指定办理人。
- **ORM框架支持**‌：支持MyBatis、Mybatis-Plus、Mybatis-Flex、Jpa、Easy-Query和BeetlSql，后续将扩展支持其他框架
- **数据库支持**‌：支持MySQL、Oracle、PostgreSQL和SQL Server，其他数据库只需要转换表结构即可支持。
- **多租户与软删除**‌：流程引擎自身维护多租户和软删除实现，也可使用对应ORM框架的实现方式。
- **兼容性**‌：同时支持Spring和Solon，兼容Java8、Java17、Java21。
- **实战项目**‌：官方提供基于Ruoyi-Vue封装的实战项目，极具参考价值。

```
希望一键三连，你的⭐️ Star ⭐️是我持续开发的动力，项目也活的更长
```

>   **[github地址](https://github.com/dromara/warm-flow.git)** | **[gitee地址](https://gitee.com/dromara/warm-flow.git)** | **[gitCode地址](https://gitcode.com/dromara/warm-flow)**

<img src="https://foruda.gitee.com/images/1749458482882123468/1ce24e01_2218307.png"/>
<img src="https://foruda.gitee.com/images/1754530281717340950/b531c256_2218307.png"/>
<img src="https://foruda.gitee.com/images/1754530582498275502/be3acb55_2218307.png"/>



## 演示地址

- admin/admin123

演示地址：http://www.warm-flow.cn

## 使用文档与联系方式

https://warm-flow.dromara.org/

> 部分地区访问不了，可本地部署
https://gitee.com/warm_4/warm-flow-doc.git

## 组件所需脚本

- 首次导入，先创建数据库，找到对应数据库的全量脚本[warm-flow-all.sql](https://gitee.com/dromara/warm-flow/tree/master/sql/mysql)，执行
- 如果版本更新，找到对应数据库的更新版本，比如xx-upgrade，[warm-flow_x.x.x.sql](https://gitee.com/dromara/warm-flow/tree/master/sql/mysql/v1-upgrade)，执行

## 官网流程定义案例json

[官网流程定义案例json](https://gitee.com/dromara/warm-flow-test/tree/master/warm-flow-core-test/src/main/resources)  

<span style="color: red;padding: 8px;">有典型的流程案例，可以发给我json文件</span>

## 测试代码

> 测试代码[warm-flow-test](https://gitee.com/dromara/warm-flow-test)项目中，warm-flow-xxx-test模块的测类

## 与Activiti、Flowable对比

| **工作流**     | **Activiti**                  | **Flowable**                         | **Warm-Flow**                                     |
|-------------|-------------------------------|--------------------------------------|---------------------------------------------------|
| **项目背景**    | Apache 基金会。                   | 由 Activiti 原团队创建，功能更优化。              | 国产工作流引擎（[Dromara 社区](https://dromara.org/)）       |
| **社区活跃度**   | 社区规模大，但近年活跃度下降。               | 社区活跃，迭代快                             | 文档和Ruoyi-Vue实战案例**较完善，社区活跃，更新快**。                 |
| **数据库表结构**  | 约 25 张表，分类简单。                 | 约 40 张表（部分版本达 79 张），分类更细。            | **仅 7 张表**，结构极简，维护成本低。                            |
| **功能与扩展性**  | 基础 BPMN 支持，插件机制有限。            | 支持动态流程修改、REST API、多实例任务优化。           | **审批功能全面**，基于json定义，支持办理人表达式、监听器、变量表达式表达式、动态权限。   |
| **流程设计器**   | 需独立部署或集成第三方工具，通常只有经典模式设计器。    | 需额外配置或扩展，通常只有经典模式设计器。                | **通过 Jar 包快速集成**，支持节点属性扩展，原生支持**经典和仿钉钉双模式**。      |
| **流程图**     | 生成静态 BPMN 流程图，颜色和样式固定。        | 需结合bpmn.js，集成难度高，扩展困难                | 自带流程图，通过jar包快速集，设置节点状态颜色，功能扩展等，原生支持**经典和仿钉钉双模式**  |
| **数据驱动**    | 内部是通过mybatis进行增删改查，对其他orm不支持。 | 同左。                                  | 支持**多 ORM 框架**。                                   |
| **多租户与软删除** | 需自行实现或依赖外部框架。                 | 原生支持多租户和软删除。                         | **原生支持多租户和软删除**，也可复用 ORM 框架实现。                    |
| **数据库支持**   | 主流数据库（MySQL、Oracle 等）。        | 同左。                                  | 支持 MySQL、Oracle、PostgreSQL、SQL Server，和**国产数据库**。 |
| **条件表达式**   | 基础条件支持。                       | 支持 SpEL 表达式。                         | **内置 SpEL 和自定义表达式**，支持动态权限和参数传递。                  |
| **办理人表达式**  | 基于 UEL实现，支持简单变量和固定角色分配。       | 支持UEL、SpEL 表达式，可通过动态变量、角色、部门等灵活分配任务。 | **默认表达式和支持 SpEL ，支持自定义规则**。                       |
| **适用场景**    | 简单流程或旧系统兼容。                   | 复杂流程、高扩展性需求。                         | **国产化、轻量级项目**，快速审批场景，灵活扩展和低代码集成。                  |


## 应用场景

Dromara Warm-Flow作为一个国产的工作流引擎，其设计简洁轻量但功能全面，适用于多种应用场景，尤其是针对中小型项目。以下是一些典型的应用场景：

1. 企业内部流程管理：用于管理企业的日常业务流程，如请假、报销、采购审批等。
2. 项目管理：在项目管理中，Dromara Warm-Flow可以用来跟踪项目任务的状态，管理项目流程，确保项目按计划进行。
3. 客户服务流程：用于管理客户服务请求，如客户咨询、投诉处理、售后服务等。
4. 人力资源管理：在人力资源管理中，Warm-Flow可用于员工招聘、培训、绩效评估等流程的管理。
5. 财务和会计流程：管理财务审批流程，如发票审核、预算审批等。
6. IT服务管理：用于IT服务请求的处理，如IT支持请求、系统变更管理等。
7. 合规性和风险管理：帮助企业在遵守法规和标准的同时，管理风险和合规流程。


## 支持数据库类型
> 目前支持MySQL 、Oracle、PostgreSQL和SQL Server，其他数据库只需要转换表结构，使用Mybatis-Plus、Mybatis-Flex和Easy-Query即可兼容

* [x] MySQL
* [x] Oracle
* [x] PostgreSQL
* [x] SQL Server
* [ ] ......


## 支持orm框架类型
* [x] mybatis
* [x] mybatis-plus
* [x] jpa
* [x] easy-query
* [x] mybatis-flex
* [ ] ......



## 工作流集成服务
如果有需要集成工作流，或者你有项目想要外包，可以微信联系【warm-houhou】。

> **有想扩展其他orm框架和数据库的可加qq群联系群主**


## 推荐

大家在使用本项目时，推荐结合贺波老师的书
[《深入Flowable流程引擎：核心原理与高阶实战》](https://item.jd.com/14804836.html)学习。这本书得到了Flowable创始人Tijs Rademakers亲笔作序推荐，对系统学习和深入掌握Flowable的用法非常有帮助。

<img src="https://gitee.com/cai_xiao_feng/lowflow-design/raw/main/public%2Fflowable.jpg" width="500px"/>

## 你可以请作者喝杯咖啡表示鼓励

![输入图片说明](https://foruda.gitee.com/images/1697770422557390406/7efa04d6_2218307.png "屏幕截图")

## 特别感谢JetBrains对开源项目支持


<a href="https://jb.gg/OpenSourceSupport">
  <img src="https://user-images.githubusercontent.com/8643542/160519107-199319dc-e1cf-4079-94b7-01b6b8d23aa6.png" align="left" height="100" width="100"  alt="JetBrains">
</a>
<br>
<br>
<br>


## git提交规范

    [init] 初始化  
    [feat] 增加新功能  
    [fix] 修复问题/BUG  
    [perf] 优化/性能提升  
    [refactor] 重构  
    [revert] 撤销修改  
    [style] 代码风格相关无影响运行结果的  
    [update] 其他修改  
    [upgrade] 升级版本  
    
