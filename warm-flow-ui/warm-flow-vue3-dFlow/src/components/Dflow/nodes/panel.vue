<!-- 最小面板 -->
<template>
  <div :class="'flow-node-panel ' + warnClass" @click.stop="$attrs.onNodeClick($attrs.modelValue)"
    @mouseenter="mouseenter" @mouseleave="mouseleave">
    <div class="flow-node-panel-header" :style="{ 'background-image': backgroundImage }">
      <span class="flow-node-panel-header-content">
        <slot name="head">
          {{ text?.value ? text?.value : "节点" }}
        </slot>

        <svg v-show="closeBtnShow" @click.stop="$attrs.onRemoveNodes(id)" t="1719894776474" class="icon"
          viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5303" width="14" height="14">
          <path
            d="M512 466.944l233.472-233.472a31.744 31.744 0 0 1 45.056 45.056L557.056 512l233.472 233.472a31.744 31.744 0 0 1-45.056 45.056L512 557.056l-233.472 233.472a31.744 31.744 0 0 1-45.056-45.056L466.944 512 233.472 278.528a31.744 31.744 0 0 1 45.056-45.056z"
            fill="#ffffff" p-id="5304"></path>
        </svg>
      </span>
      <span class="serial-level" :v-if="levelValue != null">
        {{ levelValue }}
      </span>
    </div>
    <div class="flow-node-panel-body">
      <slot name="body">
        <!-- {{ value&&value.length > 0 ? value: defaultValue }} -->
        {{ text?.value ? text?.value : "节点" }}
      </slot>
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref, watch, useAttrs } from 'vue'
const attrs = useAttrs();
const props = defineProps({
  id: {
    type: String,
    required: true
  },
  // 头部背景图 
  backgroundImage: {
    type: String,
    default: 'linear-gradient(to right, #67B26F 0%, #4ca2cd  51%, #67B26F  100%)'
  },
  // 节点名称
  text: {
    type: String,
  },
  // 节点名称
  nodeId: {
    type: String,
  },
  // 默认提示信息
  defaultValue: {
    type: String,
  },
  // 优先级信息
  levelValue: {
    type: String
  },
  // 只读模式
  readonly: {
    type: Boolean,
    default: false
  },
  //是否能销毁当前节点 (实现removeNode)
  enableDestory: {
    type: Boolean,
    default: true
  }
})

const closeBtnShow = ref(false)
const mouseenter = () => {
  if (props.readonly == true) {
    closeBtnShow.value = false;
  } else {
    closeBtnShow.value = props.enableDestory;
  }

}

const test = () => {
  console.log("孙组件的方法", attrs);
}

const mouseleave = () => {
  closeBtnShow.value = false;
}
// const removeNode = () =>{
//     emit('removeNode', data.value);
// }

const warnClass = ref('')

watch(() => props.value, (newV) => {
  if (newV == null || newV == undefined || newV.length == 0) {
    warnClass.value = "flow-warn"
  }
  else {
    warnClass.value = "";
  }
}, {
  immediate: true,
  deep: true
})


</script>

<style scoped>
.serial-level {
  position: absolute;
  right: 20px;
}

.icon {
  color: #ffffff;
  float: right;
  font-weight: 500;
  margin-top: 3px;
  margin-right: 6px;
  text-align: center;
  align-items: center;
  filter: grayscale(1) contrast(999) invert(1);
}

.icon:hover {
  transform: scale(1.2);
  cursor: pointer;
}

.flow-node-panel {
  min-height: var(--global-flow-panel-height);
  width: var(--global-flow-width);
  border: 1px solid gray;
  z-index: 1;
  box-sizing: border-box;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);

}

.flow-node-panel:hover {
  transition: transform .1s;
  transform: scale(1.05);
}

.flow-node-panel-header {
  position: relative;
  height: 20px;
  line-height: 20px;
  align-items: center;
  border-bottom: 1px solid #2e2e2e;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.5);
  /* background-color: var(--global-flow-panel-head-background-color); */


}

.flow-node-panel-header-content {
  padding-left: 8px;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
  /* color: var(--global-flow-panel-head-background-color); */
  filter: grayscale(1) contrast(999) invert(1);

}

.flow-node-panel-body {
  display: grid;
  text-align: left;
  padding: 10px 20px;
  line-height: 1.5;
  /* align-items: revert; */
  white-space: pre-wrap;
  min-height: calc(var(--global-flow-panel-height) - 20px);
  background-color: var(--global-flow-panel-body-background-color);
  align-content: center;
  justify-content: center;
  flex-wrap: wrap;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
}

.flow-warn {
  border: 2px solid #f25643;
}
</style>