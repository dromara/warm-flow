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
    <a href='https://gitee.com/dromara/warm-flow/blob/master/LICENSE'><img src='https://img.shields.io/badge/License-Apache2.0-blue.svg' alt='fork'></a>
</p>


**项目代码、文档 均开源免费可商用 遵循开源协议即可**  
**开发完成请务必登记使用项目列表，[登记地址](https://gitee.com/dromara/warm-flow/issues/I7Y57D)**

## 介绍

Warm-Flow国产工作流引擎🎉，其特点简洁轻量，五脏俱全，可扩展，是一个可通过jar引入设计器的工作流。

1. 简洁易用：只有7张表，代码量少，可快速上手和集成
2. 审批功能：支持通过、退回、任意跳转、转办、终止、会签、票签、委派和加减签、互斥和并行网关
3. 监听器与流程变量：支持四种监听器，可应对不同场景，灵活可扩展，参数传递，动态权限
4. 流程图：流程引擎自带流程图，可在不集成流程设计器情况下使用
5. 流程设计器：可通过jar包形式快速集成到项目，减少繁琐代码搬运和适配
6. 条件表达式：内置常见的和spel条件表达式，并且支持自定义扩展
7. 办理人变量表达式：内置${handler}和spel格式的表达式，可满足不同场景，灵活可扩展
8. orm框架扩展：目前支持MyBatis、Mybatis-Plus、Mybatis-Flex和Jpa，后续会由社区提供其他支持，扩展方便
9. 数据库支持：目前支持MySQL 、Oracle 和PostgreSQL，后续会继续支持其他数据库或者国产数据库
10. 多租户与软删除：流程引擎自身维护多租户和软删除实现，也可使用对应orm框架的实现方式
11. 同时支持spring和solon
12. 兼容java8和java17,理论11也可以
13. 官方提供基于ruoyi-vue封装实战项目，很实用

```shell
希望一键三连，你的⭐️ Star ⭐️是我持续开发的动力，项目也活的更长
```

>   **[github地址](https://github.com/dromara/warm-flow.git)** | **[gitee地址](https://gitee.com/dromara/warm-flow.git)** | **[gitCode地址](https://gitcode.com/dromara/warm-flow)**

## 演示地址

- admin/admin123

演示地址：http://www.hhzai.top

## 使用文档与联系方式

https://warm-flow.dromara.org/

## 组件所需脚本

- 首次导入，先创建数据库，找到对应数据库的全量脚本[warm-flow-all.sql](https://gitee.com/dromara/warm-flow/tree/master/sql/mysql)，执行
- 如果版本更新，找到对应数据库的更新版本，比如xx-upgrade，[warm-flow_x.x.x.sql](https://gitee.com/dromara/warm-flow/tree/master/sql/mysql/v1-upgrade)，执行

## 官网流程定义案例xml

[官网流程定义案例xml](https://gitee.com/dromara/warm-flow-test/tree/master/warm-flow-core-test/src/main/resources)

## 测试代码

> 测试代码[warm-flow-test](https://gitee.com/dromara/warm-flow-test)项目中，warm-flow-xxx-test模块的测类

## 支持数据库类型

* [x] mysql
* [x] oracle
* [x] postgresql
* [ ] 达梦
* [ ] 人大金仓
* [ ] GaussDB
* [ ] oceanbase
* [ ] sqlserver
* [ ] ......

## orm扩展包

* [x] mybatis
* [x] mybatis-plus
* [x] jpa
* [x] mybatis-flex
* [x] easy-query
* [ ] ......




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
    
