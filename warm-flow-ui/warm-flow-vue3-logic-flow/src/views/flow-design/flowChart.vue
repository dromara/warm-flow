<template>
  <div style="width: 100%; height: 100%">
    <el-header :style="headerStyle">
      <div style="display: flex; padding: 10px 0px; justify-content: space-between">
        <div>
          <el-tooltip effect="dark" content="自适应屏幕" placement="bottom">
            <el-button size="small" icon="Rank" @click="zoomViewport(1)">自适应屏幕</el-button>
          </el-tooltip>
          <el-tooltip effect="dark" content="放大" placement="bottom">
            <el-button size="small" icon="ZoomIn" @click="zoomViewport(true)">放大</el-button>
          </el-tooltip>
          <el-tooltip effect="dark" content="缩小" placement="bottom">
            <el-button size="small" icon="ZoomOut" @click="zoomViewport(false)">缩小</el-button>
          </el-tooltip>
          <el-tooltip effect="dark" content="下载流程图" placement="bottom">
            <el-button size="small" icon="Download" @click="downLoad">下载流程图</el-button>
          </el-tooltip>
        </div>
        <div>
          <el-button size="small" :style="`border: 1px solid rgb(${statusColors.notDone})`">未完成</el-button>
          <el-button size="small" :style="`background-color: rgb(${statusColors.todo}, 0.15); border: 1px solid rgb(${statusColors.todo})`">进行中</el-button>
          <el-button size="small" :style="`background-color: rgb(${statusColors.done}, 0.15); border: 1px solid rgb(${statusColors.done})`">已完成</el-button
          >
        </div>
      </div>
    </el-header>
    <div class="containerView" ref="containerRef"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, computed } from "vue";
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
import { queryFlowChart } from "&/api/flow/definition";

const appStore = useAppStore();
const appParams = computed(() => useAppStore().appParams);
const definitionId = ref(null);
const defJson = ref({});
const containerRef = ref(null);
const statusColors = ref({
  done: "",
  todo: "",
  notDone: "",
});
const isDark = ref(false);
const headerStyle = computed(() => {
  return {
    borderBottom: "1px solid rgb(218 218 218)",
    height: "auto",
    backgroundColor: isDark.value ? "#333" : "#fff",
  };
});

const lf = ref(null);
const register = () => {
  lf.value.register(Start);
  lf.value.register(Between);
  lf.value.register(Serial);
  lf.value.register(Parallel);
  lf.value.register(End);
  lf.value.register(Skip);
};

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
 * 添加扩展
 */
function use() {
  LogicFlow.use(Snapshot);
}

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
</style>
