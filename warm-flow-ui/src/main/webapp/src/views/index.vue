<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :sm="24" :lg="12" style="padding-left: 20px">
        <h2>Warm-Flow国产工作流引擎🎉</h2>
        <p>
          <h4>其特点简洁轻量但又不简单，五脏俱全，组件独立，可扩展，可满足中小项目的组件。</h4>
          <ul>
              <li>1. 简洁易用：只有7张表，代码量少，可快速上手和集成</li>
              <li>2. 审批功能：支持通过、退回、任意跳转、转办、终止、会签、票签、委派和加减签、互斥和并行网关</li>
              <li>3. 监听器与流程变量：支持五种监听器，可应对不同场景，灵活可扩展，参数传递，动态权限</li>
              <li>4. 流程图：流程引擎自带流程图，可在不集成流程设计器情况下使用</li>
              <li>5. 条件表达式：内置常见的条件表达式，并且支持自定义扩展</li>
              <li>6. orm框架扩展：目前支持MyBatis、Mybatis-Plus、Mybatis-Flex和Jpa，后续会由社区提供其他支持，扩展方便</li>
              <li>7. 数据库支持：目前支持MySQL 、Oracle 和PostgreSQL，后续会继续支持其他数据库或者国产数据库</li>
              <li>8. 多租户与软删除：流程引擎自身维护多租户和软删除实现，也可使用对应orm框架的实现方式</li>
              <li>9. 支持角色、部门和用户等权限配置</li>
              <li>10. 同时支持spring和solon</li>
              <li>11. 兼容java8和java17,理论11也可以</li>
              <li>12. 官方提供基于ruoyi-vue封装实战项目，很实用</li>
          </ul>
        </p>
        <p>
          <el-tag type="danger">&yen;免费开源</el-tag>
        </p>
        <p>
          <el-button
            type="primary"
            icon="Cloudy"
            plain
            @click="goTarget('https://gitee.com/dromara/warm-flow')"
            >访问码云</el-button
          >
          <el-button
            icon="HomeFilled"
            plain
            @click="goTarget('http://www.warm-flow.cn/')"
            >访问主页</el-button
          >
        </p>
      </el-col>

      <el-col :sm="24" :lg="12" style="padding-left: 50px">
        <el-card class="update-log">
          <div slot="header" class="clearfix">
            <span>捐赠支持</span>
          </div>
          <div class="body">
            <img
              src="https://foruda.gitee.com/images/1697770422557390406/7efa04d6_2218307.png"
              alt="donate"
              style="width:100%"
            />
            <span style="display: inline-block; height: 30px; line-height: 30px"
            >你可以请作者喝杯咖啡表示鼓励</span
            >
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-divider />
  </div>
</template>

<script setup name="Index">
const version = ref('3.8.7')

function goTarget(url) {
  window.open(url, '__blank')
}
</script>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>

