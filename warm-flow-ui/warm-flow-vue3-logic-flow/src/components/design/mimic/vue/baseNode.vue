<template>
  <div id="bbb" class="base-node" ref="baseNodeDiv">
    <!-- Top Section -->
    <div class="top-section">
      <span v-if="showSpan" @click="editNodeName">{{ nodeName }}</span>
      <!-- Edit Process Name Dialog -->
      <input
          v-if="editingNodeName"
          ref="nodeNameInput"
          v-model="nodeName"
          @blur="saveNodeName"/>
    </div>

    <!-- Bottom Section -->
    <div class="bottom-section">{{ handler }}</div>

  </div>
</template>

<script setup name="BaseInfo">
import {ref, onMounted, computed} from 'vue';

const props = defineProps({
  text: {
    type: String,
    default () {
      return ''
    }
  },
});

const baseNode = computed(() => {
  return {
    width: "100%",
    height: "80px",
    border: "1px solid #ccc",
    backgroundColor: "#fff",
    borderRadius: "6px", /* 添加圆角 */
  };
});

const showSpan = ref(true);
const baseNodeDiv = ref(null);


const emit = defineEmits(['btnClick']);


const nodeName = ref('发起人');
const handler = ref('所有人');

const editingNodeName = ref(false);

watch(
    () => props.text,
    (newVal) => {
      console.log('text:', newVal);
      if (newVal) {
        nodeName.value = newVal;
      }
    },
    { immediate: true }
);

const editNodeName = () => {
  showSpan.value = false;
  editingNodeName.value = true;
  setTimeout(() => {
    nodeNameInput.value.focus();
  }, 0);
};



const saveNodeName = () => {
  editingNodeName.value = false;
  showSpan.value = true;
  emit('btnClick', nodeName.value);
};

const nodeNameInput = ref(null);
const assigneeInput = ref(null);

onMounted(() => {
  handler.value = '所有人';
});
</script>

<style scoped>
.base-node {
  width: 100%;
  height: 80px;
  border: 1px solid #ccc;
  border-radius: 5px; /* 添加圆角 */
  background-color: #fff; /* 设置背景色 */
}

.top-section {
  background-color: #ccc;
  padding: 10px;
  height: 25px;
  display: flex;
  align-items: center;
  border-top-left-radius: 5px; /* 与 .base-node 的 border-radius 一致 */
  border-top-right-radius: 5px; /* 与 .base-node 的 border-radius 一致 */
}

.bottom-section {
  padding: 10px;
  height: calc(100%);
}

</style>
