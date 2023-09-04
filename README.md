## warm-freemarker

此项目是极其简单的工作流，没有太多设计，代码量少，并且只有6张表，一个小时就可以看完整个设计。使用起来方便

1. 支持简单的流程流转，比如跳转、回退、审批
2. 支持角色、部门和用户等权限配置
3. 官方提供简单流程封装很实用
4. 支持多租户，感谢【luoheyu】PR
5. 支持代办任务和已办任务，通过权限标识过滤数据

**具体demo项目**
[hh-vue](https://gitee.com/min290/hh-vue)

**更新记录** ：https://gitee.com/min290/warm-freemarker/wikis/Home

## 集成注意（务必注意）：

1、（感谢【luoheyu】提供测试意见）先查看自己项目mapper.xml是否在mapper/下面，此项目mapper.xml所在路径可能和自己项目加载的路径不一致，这时候需要把
mybatis的配置加上这段后面的 ",classpath*:mapper/**/*Mapper.xml"
把flow中的mpper.xml也加载进去，获取其他方式加载进去

![输入图片说明](https://foruda.gitee.com/images/1692858175635657150/b6eba956_2218307.png "屏幕截图")

2、（感谢【luoheyu】提供测试意见）此项目目前使用的是雪花算法生成id，可能导致前端页面获取丢失精度
按照这个把long序列化成字符串，前端页面就不会丢失精度了，获取查看hh-vue项目如何处理
http://doc.ruoyi.vip/ruoyi/other/faq.html#%E5%A6%82%E4%BD%95%E5%A4%84%E7%90%86long%E7%B1%BB%E5%9E%8B%E7%B2%BE%E5%BA%A6%E4%B8%A2%E5%A4%B1%E9%97%AE%E9%A2%98

## qq群：778470567

![输入图片说明](https://foruda.gitee.com/images/1685245176850079274/44f8f0c1_2218307.png "屏幕截图")
![输入图片说明](https://foruda.gitee.com/images/1685245214449807999/5434fac5_2218307.png "屏幕截图")
![输入图片说明](https://foruda.gitee.com/images/1681544763299393774/a25d33ab_2218307.png "屏幕截图")
![输入图片说明](https://foruda.gitee.com/images/1685245304110972083/31a9105d_2218307.png "屏幕截图")
![输入图片说明](https://foruda.gitee.com/images/1685418370349267839/f70e5589_2218307.png "屏幕截图")
![输入图片说明](https://foruda.gitee.com/images/1685418217810487859/02883c8c_2218307.png "屏幕截图")
![输入图片说明](https://foruda.gitee.com/images/1685418386450207624/90327020_2218307.png "屏幕截图")

git提交规范

    init 初始化  
    feat 增加新功能  
    fix 修复问题/BUG  
    style 代码风格相关无影响运行结果的  
    perf 优化/性能提升  
    refactor 重构  
    revert 撤销修改  
    test 测试相关  
    docs 文档/注释  
    chore 依赖更新/脚手架配置修改等  
    workflow 工作流改进  
    ci 持续集成  
    types 类型定义文件更改  
    wip 开发中