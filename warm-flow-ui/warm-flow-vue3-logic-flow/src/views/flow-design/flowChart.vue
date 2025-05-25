<template>
  <div>
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
import { json2LogicFlowJson } from "@/components/WarmFlow/js/tool";

const defJson = ref({ 
    "id": null, 
    "flowCode": "leave1", 
    "flowName": "请假申请-普通", 
    "category": "100", 
    "version": "1", 
    "formCustom": "N", 
    "formPath": "/workflow/leaveEdit/index", 
    "listenerType": null, 
    "listenerPath": null, 
    "ext": null, 
    "extMap": null, 
    "nodeList": [ 
        { 
            "nodeType": 0, 
            "nodeCode": "d5ee3ddf-3968-4379-a86f-9ceabde5faac", 
            "nodeName": "开始", 
            "permissionFlag": null, 
            "nodeRatio": "0.000", 
            "coordinate": "200,200|200,200", 
            "anyNodeSkip": null, 
            "listenerType": null, 
            "listenerPath": null, 
            "handlerType": null, 
            "handlerPath": null, 
            "formCustom": "N", 
            "formPath": null, 
            "ext": "[]", 
            "status": 2, 
            "extMap": {}, 
            "skipList": [ 
                { 
                    "nowNodeCode": "d5ee3ddf-3968-4379-a86f-9ceabde5faac", 
                    "nextNodeCode": "dd515cdd-59f6-446f-94ca-25ca062afb42", 
                    "skipName": null, 
                    "skipType": "PASS", 
                    "skipCondition": null, 
                    "coordinate": "220,200;310,200", 
                    "status": 2, 
                    "extMap": null 
                } 
            ] 
        }, 
        { 
            "nodeType": 1, 
            "nodeCode": "dd515cdd-59f6-446f-94ca-25ca062afb42", 
            "nodeName": "申请人", 
            "permissionFlag": null, 
            "nodeRatio": "0.000", 
            "coordinate": "360,200|360,200", 
            "anyNodeSkip": null, 
            "listenerType": null, 
            "listenerPath": null, 
            "handlerType": null, 
            "handlerPath": null, 
            "formCustom": "N", 
            "formPath": null, 
            "ext": "[]", 
            "status": 2, 
            "extMap": {}, 
            "skipList": [ 
                { 
                    "nowNodeCode": "dd515cdd-59f6-446f-94ca-25ca062afb42", 
                    "nextNodeCode": "78fa8e5b-e809-44ed-978a-41092409ebcf", 
                    "skipName": null, 
                    "skipType": "PASS", 
                    "skipCondition": null, 
                    "coordinate": "410,200;490,200", 
                    "status": 2, 
                    "extMap": null 
                } 
            ] 
        }, 
        { 
            "nodeType": 1, 
            "nodeCode": "78fa8e5b-e809-44ed-978a-41092409ebcf", 
            "nodeName": "组长", 
            "permissionFlag": "role:1", 
            "nodeRatio": "0.000", 
            "coordinate": "540,200|540,200", 
            "anyNodeSkip": null, 
            "listenerType": null, 
            "listenerPath": null, 
            "handlerType": null, 
            "handlerPath": null, 
            "formCustom": "N", 
            "formPath": null, 
            "ext": "[{\"code\":\"ButtonPermissionEnum\",\"value\":\"back,termination\"}]", 
            "status": 1, 
            "extMap": {}, 
            "skipList": [ 
                { 
                    "nowNodeCode": "78fa8e5b-e809-44ed-978a-41092409ebcf", 
                    "nextNodeCode": "a8abf15f-b83e-428a-86cc-033555ea9bbe", 
                    "skipName": null, 
                    "skipType": "PASS", 
                    "skipCondition": null, 
                    "coordinate": "590,200;670,200", 
                    "status": 0, 
                    "extMap": null 
                } 
            ] 
        }, 
        { 
            "nodeType": 1, 
            "nodeCode": "a8abf15f-b83e-428a-86cc-033555ea9bbe", 
            "nodeName": "部门主管", 
            "permissionFlag": "role:3@@role:4", 
            "nodeRatio": "0.000", 
            "coordinate": "720,200|720,200", 
            "anyNodeSkip": null, 
            "listenerType": null, 
            "listenerPath": null, 
            "handlerType": null, 
            "handlerPath": null, 
            "formCustom": "N", 
            "formPath": null, 
            "ext": "[{\"code\":\"ButtonPermissionEnum\",\"value\":\"back,termination\"}]", 
            "status": 0, 
            "extMap": {}, 
            "skipList": [ 
                { 
                    "nowNodeCode": "a8abf15f-b83e-428a-86cc-033555ea9bbe", 
                    "nextNodeCode": "8b82b7d7-8660-455e-b880-d6d22ea3eb6d", 
                    "skipName": null, 
                    "skipType": "PASS", 
                    "skipCondition": null, 
                    "coordinate": "770,200;880,200", 
                    "status": 0, 
                    "extMap": null 
                } 
            ] 
        }, 
        { 
            "nodeType": 2, 
            "nodeCode": "8b82b7d7-8660-455e-b880-d6d22ea3eb6d", 
            "nodeName": "结束", 
            "permissionFlag": null, 
            "nodeRatio": "0.000", 
            "coordinate": "900,200|900,200", 
            "anyNodeSkip": null, 
            "listenerType": null, 
            "listenerPath": null, 
            "handlerType": null, 
            "handlerPath": null, 
            "formCustom": "N", 
            "formPath": null, 
            "ext": "[]", 
            "status": 0, 
            "extMap": {}, 
            "skipList": [] 
        } 
    ] 
}); 
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
  console.log(defJson.value)
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
});
</script>

<style scoped>
/* 样式部分保持不变 */
.containerView {
  width: 100%;
  height: 500px;
}
</style>
