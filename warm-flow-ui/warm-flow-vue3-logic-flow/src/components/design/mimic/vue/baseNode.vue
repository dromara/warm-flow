<template>
  <div class="start-node">
    <!-- Top Section -->
    <div class="top-section">

      <span class="icon-user">sf<el-icon :size="10"><UserFilled/></el-icon></span>
      <span class="text">发起人</span>
      <span class="icon-edit" @click="editProcessName"></span>
      <!-- Edit Process Name Dialog -->
      <input
          v-if="editingProcessName"
          ref="processNameInput"
          v-model="processName"
          @blur="saveProcessName"
          @keyup.enter="saveProcessName"
      />
    </div>

    <!-- Bottom Section -->
    <div class="bottom-section">{{ assignee }}</div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import {UserFilled} from "@element-plus/icons-vue";

// Define props
const props = defineProps({
  model: Object,
  graphModel: Object,
  disabled: Boolean,
  isSelected: Boolean,
  isHovered: Boolean,
  properties: Object,
});

// Define emits
const emit = defineEmits(['update:model']);

// State
const processName = ref('发起人');
const assignee = ref('所有人');
const editingProcessName = ref(false);

// Methods
const editProcessName = () => {
  editingProcessName.value = true;
  setTimeout(() => {
    processNameInput.value.focus();
  }, 0);
};

const saveProcessName = () => {
  editingProcessName.value = false;
  // Update model or do something with the new process name
};

// References
const processNameInput = ref(null);
const assigneeInput = ref(null);

// Lifecycle hooks
onMounted(() => {
  // Initialize values from props if needed
  processName.value = '发起人';
  assignee.value = '所有人';
});
</script>

<style scoped>
.start-node {
  width: 100%;
  height: 100%;
  border: 1px solid #ccc;
  border-radius: 10px; /* 添加圆角 */
}

.top-section {
  background-color: #ccc;
  padding: 10px;
  height: 30%;
  display: flex;
  align-items: center;
}

.bottom-section {
  padding: 10px;
  height: calc(100% - 40px);
  box-sizing: border-box;
  cursor: pointer;
}

.icon-user {
  padding: 10px;
}

.icon-edit {
  margin-left: 5px;
  cursor: pointer;
}

.text {
}


</style>
