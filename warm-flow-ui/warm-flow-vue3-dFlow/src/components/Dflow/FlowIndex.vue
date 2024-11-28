<template>
  <div style="flex: 1; position: relative; overflow: auto; background-color: var(--global-flow-background-color)">
    <Maxmin :percent="percent" @updatePercent="updatePercent"></Maxmin>
    <el-button class="saveBtn" @click="submitForm">保存</el-button>

    <div class="flow-container" id="flow-container" :style="{ 'transform': 'scale(' + (percent / 100) + ')' }"
      @mousemove="mousemove">
      <template v-for="(item, index) in flowData" :key="item.id">
        <FlowRender v-model="flowData[index]" v-bind="item" @removeNodes="removeNodes" @addNodes="addNodes"
          @nodeClick="nodeClick" :readonly="readonly"></FlowRender>
      </template>
    </div>
  </div>

  <setting ref="settingRef" @formUpdata="formUpdata"></setting>
</template>

<script setup>
import { ref, defineProps, toRefs, watch } from 'vue';
import FlowRender from './nodes/render.vue'
import setting from './setting/index.vue';
import Maxmin from './maxmin.vue'
import { addNode, removeNode, backNodeList } from './flow';
const props = defineProps({
  flowData: [],
  readonly: {
    type: Boolean,
    default: false
  },
})
const { readonly } = toRefs(props)
const currid = ref(null);
const currNode = ref();
const { flowData } = toRefs(props)

const backNodes = ref([])
watch(() => flowData.value, (newV) => {
  backNodes.value = backNodeList(newV);
}, {
  immediate: true,
  deep: true
})

// 菜单 显示 隐藏
const percent = ref(100)

const settingRef = ref();

const updatePercent = (num) => {
  const value = percent.value + num;
  if (value <= 200 && value >= 10) {
    percent.value = value;
  }
}

// 移除节点
const removeNodes = (id) => {
  removeNode(flowData.value, { "id": id })
}
// 增加节点
const addNodes = (params) => {
  addNode(flowData.value, params)
}

// 节点点击事件，数据处理反显
const nodeClick = (n) => {
  currid.value = n.id;
  currNode.value = n; // getNode(flowData.value, id);
  if (n.type !== "skip") {
    if (!n.properties.collaborativeWay) {
      let nodeRatio = n.properties.nodeRatio || "";
      n.properties.collaborativeWay = nodeRatio === "0.000" ? "1" : nodeRatio === "100.000" ? "3" : n.properties.nodeRatio ? "2" : "1";
    }
    if (n.properties.collaborativeWay === "2" && !n.properties.nodeRatio) n.properties.nodeRatio = "50";
    n.properties.formCustom = JSON.stringify(n.properties) === "{}" ? "N" : (n.properties.formCustom || "");
    let listenerTypes = n.properties.listenerType ? n.properties.listenerType.split(",") : [];
    let listenerPaths = n.properties.listenerPath ? n.properties.listenerPath.split("@@") : [];
    n.properties.listenerRows = listenerTypes.map((type, index) => ({
      listenerType: type,
      listenerPath: listenerPaths[index]
    }));
  }
  let form = {
    nodeType: n.type,
    nodeCode: n.id,
    ...n.properties,
    nodeName: n.text instanceof Object ? n.text.value : n.text
  }
  //打开弹窗
  settingRef.value.openHandler(form, backNodes.value);
}

// 数据更新
const formUpdata = (data, form = flowData.value) => {
  for (const i in form) {
    if (form[i].id === data.nodeCode) {
      if (data.nodeType !== "skip") {
        form[i].text.value = data.nodeName;
        data.nodeRatio = data.collaborativeWay === "1" ? "0.000" : data.collaborativeWay === "3" ? "100.000" : data.nodeRatio || "50";
        data.permissionFlag = Array.isArray(data.permissionFlag) ? data.permissionFlag.filter(e => e).join(',') : data.permissionFlag
        // 监听器数据处理，类型路径其一为空则不保存
        data.listenerRows = data.listenerRows.filter(e => e.listenerType && e.listenerPath);
        data.listenerType = data.listenerRows.map(e => e.listenerType).join(",");
        data.listenerPath = data.listenerRows.map(e => e.listenerPath).join("@@");
      } else {
        // 条件节点
        form[i].text.value = data.skipName;
      }
      delete data.nodeName;
      delete data.nodeCode;
      delete data.nodeType;
      form[i].properties = data;
      console.log(form[i])
      break;
    } else if (form[i].childNodes) {
      for (let m in form[i].childNodes) {
        formUpdata(data, form[i].childNodes[m]);
      }
    }
  }
}

const submitForm = () => {
  console.log(flowData.value);
}

</script>

<style scoped>
span {
  font-size: 12px;
}

div {
  font-size: 12px;
}

.saveBtn {
  position: fixed;
  right: 50px;
  top: 25px;
  z-index: 100;
}

.my-flow {
  display: flex;
  width: 100%;
  height: 100%;
  flex-direction: column;
  flex-wrap: nowrap;
  background-color: var(--global-flow-background-color);
}

#my-flow {
  height: 100%;
}

/* .my-flow{
        width: 100%;
        height: 100%;
        overflow: auto;
        position: relative;
       
        background-color: var(--global-flow-background-color);
    } */
.flow-container {
  position: relative;
  width: 100%;
  /* height: 100%; */
  padding-top: 10px;
  align-items: flex-start;
  justify-content: center;
  flex-wrap: wrap;
  min-width: -moz-min-content;
  min-width: min-content;
  transform-origin: 50% 0px 0px;
  background-color: var(--global-flow-background-color);
}

/* 滚动条整体样式 */
.my-flow::-webkit-scrollbar {
  width: 12px;
  /* 适用于垂直滚动条 */
  height: 12px;
  /* 适用于水平滚动条 */
}

/* 滚动条的滑块部分 */
.my-flow::-webkit-scrollbar-thumb {
  background-color: #c2c2c2;
  /* 滑块颜色 */
  border-radius: 6px;
  /* 滑块圆角 */
  border: 2px solid transparent;
  /* 滑块边框，可以设置为透明或颜色 */
}

/* 滚动条的轨道部分 */
.my-flow::-webkit-scrollbar-track {
  background-color: #f0f0f0;
  /* 轨道颜色 */
  border-radius: 6px;
  /* 轨道圆角 */
}

/* 鼠标悬停在滚动条上时的样式 */
.my-flow::-webkit-scrollbar-thumb:hover {
  background-color: #a6a6a6;
  /* 鼠标悬停时滑块颜色加深 */
}

/* 滚动条滑块被滚动时的样式 */
.my-flow::-webkit-scrollbar-thumb:active {
  background-color: #8c8c8c;
  /* 滚动时滑块颜色更深 */
}
</style>