<template>
  <div style="width: 100%; height: 100%">
    <div class="top-text">{{defJson.topText}}</div>
    <el-header :style="headerStyle">
      <div style="padding: 5px 0; display: flex; align-items: center;">
        <!-- 左侧按钮组 -->
        <div style="display: flex; align-items: center; margin-left: 50px;">
          <el-button size="small" :style="`border: 1px solid rgb(${statusColors.notDone})`">未完成</el-button>
          <el-button size="small" :style="`background-color: rgb(${statusColors.todo}, 0.15); border: 1px solid rgb(${statusColors.todo})`">进行中</el-button>
          <el-button size="small" :style="`background-color: rgb(${statusColors.done}, 0.15); border: 1px solid rgb(${statusColors.done})`">已完成</el-button>
        </div>

        <!-- 右侧状态按钮组 -->
        <div style="margin-left: auto; display: flex; align-items: center;">
          <el-button size="small" icon="Rank" @click="zoomViewport(1)">自适应屏幕</el-button>
          <el-button size="small" icon="ZoomIn" @click="zoomViewport(true)">放大</el-button>
          <el-button size="small" icon="ZoomOut" @click="zoomViewport(false)">缩小</el-button>
          <el-button size="small" icon="Download" @click="downLoad">下载流程图</el-button>
        </div>
      </div>
    </el-header>
    <div class="containerView" ref="containerRef"></div>

    <div
        v-if="visible"
        class="custom-tooltip"
        :style="{ left: tooltipPosition.x + 'px', top: tooltipPosition.y + 'px' }"
        ref="tooltipContainerRef">
    </div>

    <div class="log-text">Warm-Flow</div>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, onUnmounted, watch, computed, render, h, nextTick} from "vue";
import LogicFlow from "@logicflow/core";
import { Snapshot } from "@logicflow/extension";
import "@logicflow/core/lib/style/index.css";
import Start from "@/components/WarmFlow/js/start";
import Between from "@/components/WarmFlow/js/between";
import Serial from "@/components/WarmFlow/js/serial";
import Parallel from "@/components/WarmFlow/js/parallel";
import End from "@/components/WarmFlow/js/end";
import Skip from "@/components/WarmFlow/js/skip";
import useAppStore from "@/store/app";
import { json2LogicFlowJson } from "@/components/WarmFlow/js/tool";
import { queryFlowChart } from "@/api/flow/definition";

const appStore = useAppStore();
const appParams = computed(() => useAppStore().appParams);
const definitionId = ref(null);
const defJson = ref({});
const containerRef = ref(null);
const tooltipPosition = ref({ x: 0, y: 0 }); // 弹框位置
const tooltipContainerRef = ref<HTMLDivElement | null>(null);
const visible = ref(false)
interface TooltipItem {
  prefix: string;
  prefixStyle?: Record<string, string | number>;
  content: string;
  contentStyle?: Record<string, string | number>;
  rowStyle?: Record<string, string | number>;
}

interface TooltipData {
  dialogStyle: Record<string, string | number>;
  info: TooltipItem[];
}

const promptContent = ref<TooltipData>({
  dialogStyle: {},
  info: []
});
const statusColors = ref({
  done: "",
  todo: "",
  notDone: "",
});
const isDark = ref(false);
const headerStyle = computed(() => {
  return {
    top: "5px",
    right: "50px",
    zIndex: "2",
    marginTop: "10px",
    height: "auto",
    backgroundColor: isDark.value ? "#333" : "#fff",
  };
});

const lf = ref(null);

const use = () => {
  LogicFlow.use(Snapshot);
};

const register = () => {
  lf.value.register(Start);
  lf.value.register(Between);
  lf.value.register(Serial);
  lf.value.register(Parallel);
  lf.value.register(End);
  lf.value.register(Skip);
};

const initEvent = () => {
  const { eventCenter } = lf.value.graphModel
  eventCenter.on('node:mouseenter', (data) => {
    const promptArr = data.data.properties.promptContent
    if (promptArr) {
      visible.value = true;

      // 确保 tooltipContainerRef 已渲染
      nextTick(() => {
        if (tooltipContainerRef.value) {
          // // 构建 HTML 内容
          promptContent.value = data.data.properties.promptContent
          // promptContent.value = {
          //   dialogStyle: { /* 弹框样式 */
          //     position: 'absolute', /* 绝对定位，基于最近的定位祖先元素（如 container） */
          //     backgroundColor: "#fff", /* 背景色为白色 */
          //     border: "1px solid #ccc", /* 灰色边框 */
          //     borderRadius: "4px", /* 添加圆角 */
          //     boxShadow: "0 2px 8px rgba(0, 0, 0, 0.15)", /* 阴影效果（轻微立体感） */
          //     padding: "8px 12px", /* 内边距（内容与边框的间距） */
          //     fontSize: "14px", /* 字体大小 */
          //     zIndex: 1000, /* 层级高于其他元素，确保提示框可见 */
          //     maxWidth: "500px", /* 最大宽度限制，防止内容过长 */
          //     pointerEvents: 'none', /* ❗️关键点：提示框不响应任何鼠标事件 */
          //     color: "#333" /* 深色文字 */
          //   },
          //   info: [
          //     {
          //       prefix: "任务名称: ",
          //       prefixStyle: {},
          //       content: "组长审批",
          //       contentStyle: {
          //         border: '1px solid #d1e9ff',
          //         backgroundColor: "#e8f4ff",
          //         padding: "4px 8px",
          //         borderRadius: "4px"
          //       },
          //       rowStyle: {
          //         fontWeight: "bold",
          //         margin: "0 0 6px 0",
          //         padding: "0 0 8px 0",
          //         borderBottom: "1px solid #ccc"
          //       }
          //     },
          //     {
          //       prefix: "负责人: ",
          //       prefixStyle: { fontWeight: "bold" },
          //       content: "李四",
          //       contentStyle: {},
          //       rowStyle: {}
          //     },
          //     {
          //       prefix: "状态: ",
          //       prefixStyle: { fontWeight: "bold" },
          //       content: "进行中",
          //       contentStyle: {},
          //       rowStyle: {}
          //     }
          //   ]
          // };
          // 获取节点位置
          tooltipPosition.value = { x: data.e.clientX, y: data.e.clientY - 80 };
        }
      });
    }
  })
  eventCenter.on('node:mouseleave', () => {
    visible.value = false
  })
}

// 监听 promptContent 变化并动态渲染
watch(
    () => promptContent.value,
    (contentData) => {
      if (!tooltipContainerRef.value) return;

      if (!contentData) {
        return;
      }

      // 更新 tooltipContainerRef 的样式
      Object.entries(contentData.dialogStyle || {}).forEach(([key, value]) => {
        tooltipContainerRef.value.style[key] = value;
      });

      // 生成 <p> 元素数组
      const children = contentData.info.map((item, index) =>
          h("p", {
            style: item.rowStyle || {},
          }, [
            h("span", {
              style: item.prefixStyle || {}
            }, item.prefix),
            h("span", {
              style: item.contentStyle || {}
            }, item.content)
          ])
      );

      // 直接将 <p> 元素渲染到 tooltipContainerRef
      const wrapper = h("div", children);

      // 调用 render 方法
      render(wrapper, tooltipContainerRef.value);
    },
    { deep: true, immediate: true }
);

const zoomViewport = async (zoom) => {
  lf.value.zoom(zoom);
  // 将内容平移至画布中心
  lf.value.translateCenter();
};

onMounted(async () => {
  if (!appParams.value) await appStore.fetchTokenName();
  definitionId.value = appParams.value.id;

  if (definitionId.value) {
    queryFlowChart(definitionId.value)
        .then((res) => {
          defJson.value = res.data;
          if (defJson.value) {
            [
              statusColors.value.done,
              statusColors.value.todo,
              statusColors.value.notDone,
            ] = res.data.chartStatusColor || ["157,255,0", "255,205,23", "0,0,0"];

            const data = json2LogicFlowJson(defJson.value);
            use();
            lf.value = new LogicFlow({
              container: containerRef.value,
              plugins: [Snapshot],
              isSilentMode: true,
              textEdit: false,
              grid: {
                size: 20,
                visible: 'true' === appParams.value.showGrid,
                type: 'dot',
                config: {
                  color: "#ccc",
                  thickness: 1,
                },
              },
              background: {
                backgroundColor: "#fff",
              },
            });
            register();
            initEvent()
            lf.value.render(data);
            lf.value.translateCenter();
          }
        })
        .catch(() => {
          lf.value.render({});
        });
  }
});

watch(isDark, (v) => {
  if (!lf.value) {
    return;
  }
  lf.value.graphModel.background = {
    background: v ? "#333" : "#fff",
  };
});

/**
 * 下载流程图
 */
function downLoad() {
  lf.value.getSnapshot(defJson.value.flowName, {
    fileType: 'png',        // 可选：'png'、'webp'、'jpeg'、'svg'
    backgroundColor: '#f5f5f5',
    padding: 30,           // 内边距，单位为像素
    partial: false,        // false: 导出所有元素，true: 只导出可见区域
    quality: 0.92          // 对jpeg和webp格式有效，取值范围0-1
  })
}

/**
 * data为 {type: string, data?: any}
 * @param e
 */
function listeningMessage(e) {
  const { data } = e;
  switch (data.type) {
    case "theme-dark": {
      isDark.value = true;
      return;
    }
    case "theme-light": {
      isDark.value = false;
      return;
    }
  }
}

onMounted(() => {
  window.addEventListener("message", listeningMessage);
});
onUnmounted(() => {
  window.removeEventListener("message", listeningMessage);
});
</script>

<style scoped>
/* 样式部分保持不变 */
.containerView {
  width: 100%;
  height: 100%;
}

.top-text {
  position: absolute;
  font-weight: bold;
  left: 500px;
  top: 10px;
  border: 1px solid #d1e9ff;
  background-color: #e8f4ff;
  padding: 4px 8px;
  border-radius: 4px;
  max-width: 300px;
  font-size: 15px; /* 可以根据需要调整字体大小 */
  color: #333; /* 可以根据需要调整颜色 */
  z-index: 1; /* 确保文本在其他内容之上显示 */
}

.log-text {
  position: absolute;
  font-weight: bold;
  right: 10px;
  bottom: 10px;
  font-size: 15px; /* 可以根据需要调整字体大小 */
  color: #333; /* 可以根据需要调整颜色 */
  z-index: 1; /* 确保文本在其他内容之上显示 */
}
</style>
