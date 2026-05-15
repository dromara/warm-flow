<template>
  <div :style="'height:' + height" class="warm-flow-iframe-wrapper">
<!--    <el-header style="border-bottom: 1px solid rgb(218 218 218); height: auto">-->
<!--      <div style="padding: 10px 0px; text-align: right;">-->
<!--        <div>-->
<!--          <el-tooltip effect="dark" content="暗模式" placement="bottom">-->
<!--            <el-button size="small" icon="Rank" @click="handleTheme('theme-dark')">暗模式</el-button>-->
<!--          </el-tooltip>-->
<!--          <el-tooltip effect="dark" content="明模式" placement="bottom">-->
<!--            <el-button size="small" icon="Rank" @click="handleTheme('theme-light')">明模式</el-button>-->
<!--          </el-tooltip>-->
<!--        </div>-->
<!--      </div>-->
<!--    </el-header>-->
    <iframe id="warmChart" :src="url" style="width: 100%; height: 100%"/>
  </div>
</template>
<script>
import {getToken} from "@/utils/auth";

export default {
  name: "WarmFlow",
  data() {
    return {
      height: document.documentElement.clientHeight - 94.5 + "px;",
      url: ""
    };
  },
  mounted() {
    // 流程定义id
    const id = this.$route.params.id
    // 是否只显示设计器, true:只显示设计器 false:显示基础信息和设计器
    const onlyDesignShow = this.$route.query.onlyDesignShow
    // 是否可编辑 , true:不可编辑  false:可编辑  (本身warm-flow工作流内部会通过发布状态自行判断是否可以编辑，但是如果是需要查看的场景可以单独可控制)
    const disabled = this.$route.query.disabled
    // const themeParam = '&theme=theme-dark&darkColors=111827'
    const themeParam = ''
    const baseUrl = `${process.env.VUE_APP_FLOW_API}/warm-flow-ui/index.html?id=${id}&onlyDesignShow=${onlyDesignShow}&disabled=${disabled}${themeParam}`;
    this.url = baseUrl + `&Authorization=Bearer ` + getToken();
    // this.url = baseUrl + `&showGrid=true&Authorization=Bearer ` + getToken();
    window.addEventListener("message", this.handleMessage);
  },
  beforeDestroy() {
    window.removeEventListener("message", this.handleMessage);
  },
  methods: {
    handleMessage(event) {
      switch (event.data.method) {
        case "close":
          this.close();
          break;
      }
    },
    close() {
      // 路由参数传递时间戳 来触发页面刷新
      const obj = { path: "/flow/definition", query: { t: Date.now(), pageNum: this.$route.query.pageNum } };
      this.$tab.closeOpenPage(obj);
    },
    handleTheme(theme) {
      // 获取目标窗口对象
      const targetWindow = document.getElementById('warmChart').contentWindow;
      targetWindow.postMessage({type: theme}, "*");
    },
  }
};
</script>

<style scoped>
.warm-flow-iframe-wrapper {
  /* 真机兼容：使用 flex 布局确保 iframe 容器正确撑满可用空间 */
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.warm-flow-iframe-wrapper iframe {
  /* 确保 iframe 在移动端不出现滚动条问题 */
  border: none;
  flex: 1;
}
</style>
