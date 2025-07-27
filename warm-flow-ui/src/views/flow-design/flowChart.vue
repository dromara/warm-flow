<template>
  <div style="width: 100%; height: 100%">
    <div class="top-text" v-if="defJson.topTextShow">{{defJson.topText}}</div>
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
          <el-button size="small" icon="ZoomIn" @click="zoomViewport(true)">放大</el-button>
          <el-button size="small" icon="Rank" @click="zoomViewport(1)">自适应</el-button>
          <el-button size="small" icon="ZoomOut" @click="zoomViewport(false)">缩小</el-button>
          <el-button size="small" icon="Download" @click="downLoad">下载流程图</el-button>
        </div>
      </div>
    </el-header>
    <div class="containerView" ref="containerRef"></div>

    <div
        v-if="visible"
        :style="{ left: tooltipPosition.x + 'px', top: tooltipPosition.y + 'px' }"
        ref="tooltipContainerRef">
    </div>

    <div class="log-text">Warm-Flow</div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed, render, h, nextTick } from "vue";
import LogicFlow from "@logicflow/core";
import { Snapshot } from "@logicflow/extension";
import "@logicflow/core/lib/style/index.css";
import StartC from "@/components/design/classics/js/start";
import BetweenC from "@/components/design/classics/js/between";
import SerialC from "@/components/design/classics/js/serial";
import ParallelC from "@/components/design/classics/js/parallel";
import EndC from "@/components/design/classics/js/end";
import SkipC from "@/components/design/classics/js/skip";
import StartM from "@/components/design/mimic/js/start";
import BetweenM from "@/components/design/mimic/js/between";
import SerialM from "@/components/design/mimic/js/serial";
import ParallelM from "@/components/design/mimic/js/parallel";
import EndM from "@/components/design/mimic/js/end";
import SkipM from "@/components/design/mimic/js/skip";
import useAppStore from "@/store/app";
import {isClassics, json2LogicFlowJson} from "@/components/design/common/js/tool";
import { queryFlowChart } from "@/api/flow/definition";

const appStore = useAppStore();
const appParams = computed(() => useAppStore().appParams);
const definitionId = ref(null);
const defJson = ref({});
const containerRef = ref(null);
const tooltipPosition = ref({ x: 0, y: 0 });
const tooltipContainerRef = ref(null);
const visible = ref(false);

const promptContent = ref({
  dialogStyle: {},
  info: []
});

const statusColors = ref({
  done: "",
  todo: "",
  notDone: ""
});

const isDark = ref(false);
const headerStyle = computed(() => {
  return {
    top: "5px",
    right: "50px",
    zIndex: "2",
    height: "auto",
    backgroundColor: isDark.value ? "#333" : "#fff"
  };
});

const lf = ref(null);

const use = () => {
  LogicFlow.use(Snapshot);
};

const register = () => {
  if (isClassics(defJson.value.modelValue)) {
    lf.value.register(StartC);
    lf.value.register(BetweenC);
    lf.value.register(SerialC);
    lf.value.register(ParallelC);
    lf.value.register(EndC);
    lf.value.register(SkipC);
  } else {
    lf.value.register(StartM);
    lf.value.register(BetweenM);
    lf.value.register(SerialM);
    lf.value.register(ParallelM);
    lf.value.register(EndM);
    lf.value.register(SkipM);
  }
};

const initEvent = () => {
  const { eventCenter } = lf.value.graphModel;
  eventCenter.on('node:click', (data) => {
    const promptArr = data.data.properties.promptContent;
    if (promptArr) {
      visible.value = true;

      nextTick(() => {
        if (tooltipContainerRef.value) {
          // // 构建 HTML 内容
          promptContent.value = data.data.properties.promptContent
          // promptContent.value = {
          //   dialogStyle: { /* 弹框样式 */
          //     position: 'absolute', /* 绝对定位，基于最近的定位祖先元素（如 container） */
          //     backgroundColor: "#fff", /* 背景色为白色 */
          //     maxHeight: "300px",
          //     overflowY: "auto",
          //     border: "1px solid #ccc", /* 灰色边框 */
          //     borderRadius: "4px", /* 添加圆角 */
          //     boxShadow: "0 2px 8px rgba(0, 0, 0, 0.15)", /* 阴影效果（轻微立体感） */
          //     padding: "8px 12px", /* 内边距（内容与边框的间距） */
          //     fontSize: "14px", /* 字体大小 */
          //     zIndex: 1000, /* 层级高于其他元素，确保提示框可见 */
          //     maxWidth: "500px", /* 最大宽度限制，防止内容过长 */
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
  });

  eventCenter.on('blank:click', () => {
    visible.value = false;
  });
};

// 监听 promptContent 变化并动态渲染
watch(
    () => promptContent.value,
    (contentData) => {
      if (!tooltipContainerRef.value) return;
      if (!contentData) return;

      // 更新 tooltipContainerRef 的样式
      Object.entries(contentData.dialogStyle || {}).forEach(([key, value]) => {
        tooltipContainerRef.value.style[key] = value;
      });

      // 生成 <p> 元素数组
      const children = contentData.info.map((item) =>
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

      // 调用 render 方法
      const wrapper = h("div", children);
      render(wrapper, tooltipContainerRef.value);
    },
    { deep: true, immediate: true }
);

const zoomViewport = async (zoom) => {
  lf.value.zoom(zoom);
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
            document.body.style.overflow = 'hidden';
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
            initEvent();
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
  if (!lf.value) return;
  lf.value.graphModel.background = {
    background: v ? "#333" : "#fff"
  };
});

/**
 * 下载流程图
 */
function downLoad() {
  lf.value.getSnapshot(defJson.value.flowName, {
    fileType: 'png',
    backgroundColor: '#f5f5f5',
    padding: 30,
    partial: false,
    quality: 0.92
  });
}

/**
 * 监听消息
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
.containerView {
  width: 100%;
  height: 100%;
}

.top-text {
  position: absolute;
  font-weight: bold;
  left: 500px;
  top: 2px;
  border: 1px solid #d1e9ff;
  background-color: #e8f4ff;
  padding: 4px 8px;
  border-radius: 4px;
  max-width: 300px;
  font-size: 15px;
  color: #333;
  z-index: 1;
}

.log-text {
  position: absolute;
  font-weight: bold;
  right: 10px;
  bottom: 10px;
  font-size: 15px;
  color: #333;
  z-index: 1;
}
</style>
