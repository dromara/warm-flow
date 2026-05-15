<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :sm="24" :lg="12" style="padding-left: 20px">
        <h2>Warm-Flow国产工作流引擎🎉</h2>
        <p>
          <h4>> Dromara Warm-Flow，国产的工作流引擎，以其简洁轻量、五脏俱全、灵活扩展性强的特点，成为了众多开发者的首选。它不仅可以通过jar包快速集成设计器，同时原生支持经典和仿钉钉双模式，还具备以下显著优势：</h4>
          <ol>
            <li>简洁易用‌：仅包含7张表，代码量少，上手和集成速度快。</li>
            <li>审批功能全面‌：支持通过、退回、撤销、拿回、任意跳转、终止、转办、票签、委派和加减签、互斥、并行、自动审批、远程访问和脚本执行服务等多种审批操作，以及条件表达式、办理人表达和监听器等高级功能。</li>
            <li>流程设计器‌：通过jar包形式快速集成到项目，支持节点属性扩展，原生支持经典和仿钉钉双模式</li>
            <li>流程图‌：自带流程图查看，通过jar包快速集，功能扩展，原生支持经典和仿钉钉双模式。</li>
            <li>条件表达式‌：内置常见的和spel条件表达式，支持自定义扩展。</li>
            <li>办理人变量表达式‌：内置${handler}和spel格式的表达式，满足不同场景需求，灵活可扩展。</li>
            <li>监听器‌：提供四种监听器，支持不同作用范围和spel表达式，参数传递灵活，支持动态权限。</li>
            <li>流程变量‌：在整个流程办理过程起到重要的角色，如办理人表达式中，传入变量进行动态指定办理人。</li>
            <li>ORM框架支持‌：当前项目已整体重构为 Jimmer ORM + PostgreSQL，后续按需扩展其他 ORM 适配。</li>
            <li>数据库支持‌：支持MySQL、Oracle、PostgreSQL和SQL Server，其他数据库只需要转换表结构即可支持。</li>
            <li>多租户与软删除‌：流程引擎自身维护多租户和软删除实现，也可使用对应ORM框架的实现方式。</li>
            <li>兼容性‌：同时支持Spring和Solon，兼容Java8、Java17、Java21。</li>
            <li>实战项目‌：官方提供基于Ruoyi-Vue封装的实战项目，极具参考价值。</li>
          </ol>
        </p>
        <p>
          <el-tag type="danger">&yen;免费开源</el-tag>
        </p>
        <p>
          <el-button
            type="primary"
            size="mini"
            icon="el-icon-cloudy"
            plain
            @click="goTarget('https://gitee.com/dromara/warm-flow')"
            >访问码云</el-button
          >
          <el-button
            size="mini"
            icon="el-icon-s-home"
            plain
            @click="goTarget('https://www.warm-flow.com/')"
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
              width="100%"
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

<script>
export default {
  name: "Index",
  data() {
    return {
      // 版本号
      version: "3.8.8"
    };
  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank");
    }
  }
};
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

