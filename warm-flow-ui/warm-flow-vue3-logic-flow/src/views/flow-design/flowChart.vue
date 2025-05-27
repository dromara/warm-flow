<template>
  <div style="width: 100%; height: 100%">
    <el-header style="border-bottom: 1px solid rgb(218 218 218); height: auto">
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
        </div>
        <div>
          <el-button size="small" style="border: 1px solid #000">未完成</el-button>
          <el-button size="small" style="background-color: #fff8dc; border: 1px solid #ffcd17">进行中</el-button>
          <el-button size="small" style="background-color: #f0ffd9; border: 1px solid #9dff00">已完成</el-button>
        </div>
      </div>
    </el-header>
    <div class="containerView" ref="containerRef"></div>
  </div>
</template>

<script setup lang="ts">
import { ref ,onMounted } from 'vue';
import LogicFlow from '@logicflow/core';
import '@logicflow/core/lib/style/index.css';
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
    queryFlowChart(definitionId.value).then(res => {
      defJson.value = res.data;
      if (defJson.value) {
        const data = json2LogicFlowJson(defJson.value);
        lf.value = new LogicFlow({
          container: containerRef.value,
          grid: false,
          isSilentMode: true,
          textEdit: false
        });
        register();
        lf.value.render(data);
        lf.value.translateCenter();
      }
    }).catch(() => {
      lf.value.render({});
    });
  }

});
</script>

<style scoped>
/* 样式部分保持不变 */
.containerView {
  width: 100%;
  height: 100%;
}
</style>
