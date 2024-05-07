## 介绍

🎉国产自研工作流，其特点简洁(只有6张表)但又不简单，五脏俱全，组件独立，可扩展，可满足中小项目的组件。

1. 支持常规的流程流转，比如跳转、回退、审批和任意跳转
2. 支持转办、终止，任务最终回到发起人
3. 支持或签（会签和票签开发中）
4. 业务项目可不依赖流程设计器，组件会生成流程图片
5. 支持角色、部门和用户等权限配置
6. 支持监听器，参数传递，动态权限 
7. 支持多租户
8. 支持互斥网关，并行网关
9. 支持条件表达式，可扩展
10. 支持不同orm框架和数据库扩展
11. 同时支持spring和solon
12. 兼容java8和java17,理论11也可以
13. 官方提供简单流程封装demo项目，很实用

>  **希望一键三连，你的⭐️ Star ⭐️是我持续开发的动力，项目也活的更长**  
>   **可二开、商用，但请注明出处，保留代码注释中的作者名，[但是使用前请先登记](https://gitee.com/warm_4/warm-flow/issues/I7Y57D)**  
>
>   **[gitee地址](https://gitee.com/dromara/warm-flow.git  )** |**[github地址](https://github.com/dromara/warm-flow.git)**

## 演示地址

- admin/admin123

演示地址：http://www.hhzai.top:81

## 使用文档与联系方式
http://warm-flow.cn


## 集成项目示例 

| 版本           | 项目名称           | 源码地址              | 
|--------------|--------------|-----------------------------------------|
| springboot2+vue2  |RuoYi-Vue-Warm-Flow| https://gitee.com/min290/hh-vue        |
| springboot3+React |quick-boot         | https://github.com/csx-bill/quick-boot |
| solon+vue3        |warm-sun           | https://gitee.com/min290/warm-sun.git  |



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

### 加入warm-flow团队
完成一个功能开发，即可申请加入

## 你可以请作者喝杯咖啡表示鼓励

![输入图片说明](https://foruda.gitee.com/images/1697770422557390406/7efa04d6_2218307.png "屏幕截图")


### 特别感谢JetBrains对开源项目支持

<a href="https://jb.gg/OpenSourceSupport">
  <img src="https://user-images.githubusercontent.com/8643542/160519107-199319dc-e1cf-4079-94b7-01b6b8d23aa6.png" align="left" height="100" width="100"  alt="JetBrains">
</a>