<template>
  <div style="position: relative;">
    <el-button v-if="!$attrs.readonly" dark round size="small" class="flow-serial-group-add-btn"
      @click="flowNodeSwitchAdd">添加并行</el-button>
    <div class="flow-serial-group">

      <div class="flow-serial-group-line" v-for="(item) in childNodes" :key="item.id">
        <div class="flow-serial-group-wrap">

          <FlowRender v-bind="{ ...$attrs, ...subItem }" v-model="item[index]" v-for="(subItem, index) in item"
            :key="subItem.id" :backgroundImage="backgroundImage"></FlowRender>
          <!-- <FlowRender :nodeData="subItem"  v-for="subItem in item"  :key="subItem.id" :readonly="readonly" ></FlowRender> -->
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, toRefs, useAttrs } from 'vue';
const attrs = useAttrs();
const props = defineProps({
  childNodes: [],
})

import { uuid } from '@/utils/tool';

import FlowRender from '../render.vue';
const backgroundImage = ref("linear-gradient(to right, #7474BF 0%, #348AC7  51%, #7474BF  100%)")

const flowNodeSwitchAdd = () => {
  const childNodes = props.childNodes;
  childNodes.splice(childNodes.length, 0, [{
    text: { value: `中间节点${childNodes.length + 1}` },
    id: uuid(),
    type: 'between',
    properties: {}
  }])
}

</script>

<style scoped>
.flow-serial-group {
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  align-content: center;
  justify-content: center;
  position: relative;
  background-color: var(--global-flow-background-color);
  min-width: 0;
  width: 100%;

}

.flow-serial-group-line {
  position: relative;
  background-color: var(--global-flow-background-color);
}

.flow-serial-group-add-btn {
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translate(-50%);
  background-color: var(--el-bg-color);
  z-index: 2;
}

.flow-serial-group-add-btn:hover {
  transform-origin: center;
  transition: transform .1s;
  transform: scale(1.05) translate(-48%);
  background-color: var(--el-bg-color);

}

.flow-node-swtich-group-add-btn-content {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
  font-size: 12px;
  width: 10px;
}

.flow-serial-group-wrap {
  display: flex;
  flex-wrap: nowrap;
  white-space: nowrap;
  flex-direction: column;
  justify-content: flex-start;
  position: relative;
  z-index: 0;
  height: 100%;
  padding-top: 20px;
  background-color: var(--global-flow-background-color);

}

.low-node-switch-group-wrap>* {
  flex-shrink: 0;
}

.flow-serial-group-wrap::before {
  content: '';
  position: absolute;
  top: 0;
  left: 50%;
  height: 100%;
  width: 1px;
  transform: translate(-50%, 0px);
  background-color: var(--global-flow-line-color);
  z-index: 0;
}

.flow-serial-group-line:first-child::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  height: 1px;
  width: 50%;
  background-color: var(--global-flow-line-color);
  z-index: 1;
}

.flow-serial-group-line:not(:first-child):not(:last-child)::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  height: 1px;
  width: 100%;
  background-color: var(--global-flow-line-color);
  z-index: 1;
}

.flow-serial-group-line:last-child::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  height: 1px;
  width: 50%;
  background-color: var(--global-flow-line-color);
  z-index: 1;
}

.flow-serial-group-line:first-child::after {
  content: '';
  position: absolute;
  bottom: 0;
  right: 0;
  height: 1px;
  width: 50%;
  background-color: var(--global-flow-line-color);
  z-index: 1;
}

.flow-serial-group-line:not(:first-child):not(:last-child)::after {
  content: '';
  position: absolute;
  bottom: 0;
  right: 0;
  height: 1px;
  width: 100%;
  background-color: var(--global-flow-line-color);
  z-index: 1;
}

.flow-serial-group-line:last-child::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  height: 1px;
  width: 50%;
  background-color: var(--global-flow-line-color);
  z-index: 1;
}



.flow-node-group-box {
  position: relative;
  display: inline-block;
  min-width: fit-content;
}

.low-node-switch:last-child {
  padding-right: 0
}


.flow-node-switch-last {
  text-align: center;
}



.flow-node-group-add-btn {
  position: absolute;
  left: 50%;
  width: 45px;
  height: 20px;
  transform: translate(-50%, -50%);
  background-color: blue;
  z-index: 1;
}

.flow-serial-node-wrap {
  padding-top: 25px;
  display: flex;
  overflow: visible;
  /* min-height: 180px; */
  height: auto;
  position: relative;
  padding-top: 15px;
  justify-content: center;
  background-color: var(--global-flow-background-color);
  min-width: 0;
}

.flow-serial-node-wrap::before {
  content: '';
  position: absolute;
  top: 0px;
  left: 50%;
  height: 14px;
  transform: translate(-50%, 0px);
  width: 1px;
  background-color: var(--global-flow-line-color);
  /* transform: translate(-50%, 0px); */
}


.flow-serial-node-last {
  text-align: center;
}
</style>