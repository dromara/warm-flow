<!-- 连接 + 添加按钮  -->
<template>
  <div class="flow-node-line" v-if="!readonly">
    <el-icon color="#2a53fe" class="flow-node-line-add flow-add-icon" ref="buttonRef" size="30">
      <CirclePlusFilled />
    </el-icon>
  </div>
  <div v-else class="flow-node-line-2" style="min-height: 30px;"></div>

  <el-popover ref="popoverRef" placement="right" :width="240" trigger="hover" :virtual-ref="buttonRef">
    <el-row :gutter="20" style="gap: 10px;">
      <el-col :span="11">
        <el-button class="flow-menu-btn" :icon="Search" plain type="primary"
          @click="$attrs.onAddNodes({ id: id, type: 'between' })">审批人</el-button>
      </el-col>
      <el-col :span="11">
        <el-button class="flow-menu-btn" :icon="Search" plain type="primary"
          @click="$attrs.onAddNodes({ id: id, type: 'serial' })">条件分支</el-button>
      </el-col>
      <el-col :span="11">
        <el-button class="flow-menu-btn" :icon="Search" plain type="primary"
          @click="$attrs.onAddNodes({ id: id, type: 'parallel' })">并行分支</el-button>
      </el-col>
      <!-- <el-col :span="11">
                <el-button class="flow-menu-btn" :icon="Search" plain type="primary">抄送人</el-button>
            </el-col> -->

    </el-row>
  </el-popover>
</template>

<script setup>
import {
  CirclePlusFilled
} from '@element-plus/icons-vue'
import { defineProps, toRefs, ref } from 'vue';
import { ClickOutside as vClickOutside } from 'element-plus'
const buttonRef = ref()
const popoverRef = ref()
const onClickOutside = () => {
  unref(popoverRef).popperRef?.delayHide?.()
}
const props = defineProps({
  id: {
    type: String,
    required: true
  },
  readonly: {
    type: Boolean,
    default: false,
  }
})


</script>

<style scoped>
.ep-row {
  padding-top: 10px;
}

.ep-row>.ep-col {
  margin-bottom: 10px;

}

.flow-menu-btn {
  width: 100%;
}

.flow-node-line-add:hover {
  transition: transform .2s;
  transform: scale(1.2);
}

.flow-node-line {
  width: var(--global-flow-width);
  display: flex;
  align-content: center;
  justify-content: center;
  position: relative;
  z-index: 1;
  padding: 15px 0 10px;
  /* align-items: flex-start; */

}

/* .flow-add-icon{
        background-color: var(--global-flow-background-color);
    } */

.flow-node-line::after {
  content: '';
  left: 50%;
  position: absolute;
  height: 17px;
  transform: translate(-50%, -15px);
  width: 1px;
  background-color: var(--global-flow-line-color);
}

.flow-node-line::before {
  content: '';
  left: 50%;
  position: absolute;
  height: 12px;
  bottom: 0;
  transform: translate(-50%, 0px);
  width: 1px;
  background-color: var(--global-flow-line-color);
}

.flow-node-line-2::before {
  content: '';
  left: 50%;
  position: absolute;
  min-height: 30px;
  height: 30px;
  transform: translate(-50%, 0px);
  width: 1px;
  background-color: var(--global-flow-line-color);
}
</style>