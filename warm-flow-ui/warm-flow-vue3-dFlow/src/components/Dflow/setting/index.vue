<template>
  <el-drawer :key="form?.nodeCode" v-model="drawer" @close="closeHandler" :direction="direction"
    destroy-on-close :force-render="false">
    <template #header>
      <el-text v-if="!drawerEdit" @click="changeTitle" tag="ins">{{ form?.nodeName }}</el-text>
      <el-input v-else ref="drawerEditInput" v-model="form.nodeName" autofocus @blur="drawerEditBlur" />
    </template>
    <template #default>
      <component ref="settingRef" :key="form.nodeCode" v-model="form" :is="currComponent" :showWays="true" :backNodeList="backNodeList">
      </component>
    </template>
  </el-drawer>
</template>

<script setup>

import { ref, defineExpose, nextTick } from 'vue';
import Start from '&/components/start.vue'
import Between from '&/components/between.vue'
import Skip from '&/components/skip.vue'
// import UserConfig from './userConfig.vue'
// import StartConfig from './startConfig.vue'
// import SerialConfig from './serialConfig.vue'
const emit = defineEmits(["formUpdata"]);
const drawer = ref(false);
const direction = ref('rtl')
const form = ref();
const backNodeList = ref([]);
const currComponent = ref();
const settingRef = ref();

// 初始化 动态组件
const initComponent = () => {
  switch (form.value?.nodeType) {
    case 'start': currComponent.value = Start; break;
    case 'bewteen': currComponent.value = Between; break;
    case 'skip': currComponent.value = Skip; break;
    default: currComponent.value = Between; break;
  }

}

// 打开抽屉, 传入值
const openHandler = (data, backNodes) => {
  form.value = data;
  backNodeList.value = backNodes
  // 加载对应组件
  initComponent();
  drawer.value = true;
}
// 关闭抽屉
const closeHandler = () => {
  emit("formUpdata", form.value);
  drawer.value = false;
}

const drawerEdit = ref(false);
const drawerEditInput = ref(null);
// 修改title
const changeTitle = () => {
  drawerEdit.value = true;
  nextTick(() => {
    drawerEditInput.value.focus();
  })
}
const drawerEditBlur = () => {
  drawerEdit.value = false;
  drawerEditInput.value.blur();
}

defineExpose({
  openHandler, closeHandler
})

</script>

<style scoped>
.flow-setting-container-aa {
  display: inline-block;
  box-sizing: border-box;
  /* border: 1px solid tomato; */
  padding: 10px;
}
</style>