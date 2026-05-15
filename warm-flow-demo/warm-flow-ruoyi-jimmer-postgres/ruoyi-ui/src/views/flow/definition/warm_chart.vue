<template>
  <div :style="'height:' + height" class="warm-flow-iframe-wrapper">
<!--        <el-header style="border-bottom: 1px solid rgb(218 218 218); height: auto">-->
<!--          <div style="padding: 10px 0px; text-align: right;">-->
<!--            <div>-->
<!--              <el-tooltip effect="dark" content="暗模式" placement="bottom">-->
<!--                <el-button size="small" icon="Rank" @click="handleTheme('theme-dark')">暗模式</el-button>-->
<!--              </el-tooltip>-->
<!--              <el-tooltip effect="dark" content="明模式" placement="bottom">-->
<!--                <el-button size="small" icon="Rank" @click="handleTheme('theme-light')">明模式</el-button>-->
<!--              </el-tooltip>-->
<!--            </div>-->
<!--          </div>-->
<!--        </el-header>-->
    <iframe id="warmChart" :src="url" style="width: 100%; height: 100%"/>
  </div>
</template>
<script>
import {getToken} from "@/utils/auth";

export default {
  name: "WarmChart",
  props: {
    // 组件调用时传入的流程实例ID
    insId: { type: [String, Number], default: null }
  },
  data() {
    return {
      // 移动端减少偏移量，使弹框底部贴近屏幕底部
      height: this.calcHeight(),
      url: "",
    };
  },
  watch: {
    insId: {
      immediate: true,
      handler(newVal) {
        // const themeParam = '&theme=theme-dark&darkColors=111827'
        const themeParam = ''
        const baseUrl = `${process.env.VUE_APP_FLOW_API}/warm-flow-ui/index.html?id=${newVal}&type=FlowChart${themeParam}`;
        this.url = baseUrl + `&Authorization=Bearer ${getToken()}`;
        // this.url = baseUrl + `&showGrid=true&Authorization=Bearer ${getToken()}`;
      }
    }
  },

  methods: {
    /** 计算iframe高度，移动端减少偏移使弹框贴近屏幕底部 */
    calcHeight() {
      const clientH = document.documentElement.clientHeight;
      const isMobile = window.innerWidth <= 768;
      // 移动端只减去标题栏+少量间距（约90px），PC端减200px
      const offset = isMobile ? 90 : 200;
      return (clientH - offset) + "px";
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
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.warm-flow-iframe-wrapper iframe {
  border: none;
  flex: 1;
}
</style>

<!-- 全局样式：移动端弹窗自适应宽度 -->
<style>
@media (max-width: 768px) {
  .el-dialog__wrapper .el-dialog {
    width: 96% !important;
    margin-top: 2vh !important;
    margin-bottom: 2vh !important;
  }

  /* 流程图弹框高度自适应 */
  .el-dialog__body {
    padding: 0 !important;
    max-height: calc(100vh - 60px) !important;
    overflow: hidden !important;
  }
}
</style>
