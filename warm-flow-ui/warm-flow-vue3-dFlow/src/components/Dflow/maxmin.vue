<template>

  <div class="flow-zoom-wrap">

    <div class="flow-max-min">

      <el-button style="padding:0 8px 0 8px;  border-radius:0;  " @click="maxmin(-10)" icon="Minus"></el-button>
      <el-button type="plain" class="flow-max-min-percent">{{ percent }}%</el-button>
      <el-button style="padding:0 8px 0 8px; border-radius:0; margin: 0;" @click="maxmin(10)" icon="Plus"></el-button>

    </div>
  </div>
</template>

<script setup>
import { defineProps, toRefs, defineEmits } from 'vue';

const props = defineProps({
  percent: Number
})
const { percent } = toRefs(props)
const emits = defineEmits(['updatePercent'])
const maxmin = (num) => {
  emits('updatePercent', num)
}

</script>

<style scoped>
.ep-button+.ep-button {
  margin-left: unset;
}

.flow-zoom-wrap {
  position: absolute;
  right: 250px;
  top: 25px;
}

.flow-max-min {
  display: flex;
  position: fixed;
  grid-auto-flow: column;
  z-index: 3;
  box-sizing: border-box;
  box-shadow: var(--el-box-shadow);
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  flex-direction: row;
  align-items: center;
}

.flow-max-min-percent {
  width: 60px;
  text-align: center;
  padding: 0 20px 0 20px;
  border-radius: 0;
  background-color: var(--el-fill-color-dark);
  cursor: default;
  margin: 0;
}

</style>