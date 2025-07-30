<template>
  <div :style="baseNodeColor" class="base-node" ref="baseNodeDiv">
    <div :style="topSectionColor" class="top-section" v-click-outside="handleLeave">
      <span v-show="showSpan" @click="editNodeName">{{ nodeName }} 📝</span>
      <input
          v-show="editingNodeName"
          ref="nodeNameInput"
          v-model="nodeName"
          @blur="saveNodeName"/>
      <span v-show="props.type === 'between' && !chartStatusColor" class="delete-btn" @click.stop="deleteNode">✕</span>
    </div>

    <div class="bottom-section" @click="editNode" :title="handler">{{ handler }}</div>

  </div>
</template>

<script setup name="BaseInfo">
import {computed, ref} from 'vue';
import {handlerFeedback} from "@/api/flow/definition.js";

const props = defineProps({
  text: {
    type: String,
    default () {
      return ''
    }
  },
  permissionFlag: {
    type: String,
    default () {
      return ''
    }
  },
  chartStatusColor: {
    type: Array,
    default () {
      return []
    }
  },
  type: {
    type: String,
    default () {
      return ''
    }
  },
  fill: {
    type: String,
    default () {
      return ''
    }
  },
  stroke: {
    type: String,
    default () {
      return ''
    }
  },
});

const showSpan = ref(true);
const baseNodeDiv = ref(null);
const nodeName = ref('发起人');
const handler = ref('所有人');
const nodeNameInput = ref(null);
const editingNodeName = ref(false);
const emit = defineEmits(['updateNodeName', 'deleteNode', 'editNode']); // 添加 deleteNode 事件

const baseNodeColor = computed(() => {
  return {
    backgroundColor: props.fill ? props.fill : "#fff",
    border: props.stroke ? "1px solid " + props.stroke : "#ccc",
  };
});

const topSectionColor = computed(() => {
  return {
    backgroundColor: props.stroke ? props.stroke : "#ccc",
  };
});

const deleteNode = () => {
  emit('deleteNode'); // 触发删除事件，由父组件处理
};

watch(
    () => props.text,
    (newVal) => {
      if (newVal) {
        nodeName.value = newVal;
      }
    },
    { immediate: true }
);

watch(
    () => props.permissionFlag,
    (newVal) => {
      if (newVal) {
        handlerFeedback({storageIds: newVal.split("@@")}).then(response => {
          if (response.code === 200 && response.data) {
            // 遍历response.data数组，数组中每个元素都是对象，获取每个对象中handlerName的值，并且用、拼接
            handler.value = response.data.map(item => item.handlerName).join('、');
          }
        });
      } else {
        handler.value = '所有人';
      }
    },
    { immediate: true }
);

const editNodeName = () => {
  if (props.chartStatusColor) {
    return
  }
  editingNodeName.value = true;
  showSpan.value = false;
};

const saveNodeName = () => {
  if (props.chartStatusColor) {
    return
  }
  editingNodeName.value = false;
  showSpan.value = true;
  emit('updateNodeName', nodeName.value);
};

const editNode = () => {
  emit('editNode');
};

function handleLeave() {
  editingNodeName.value = false;
  showSpan.value = true;
}

</script>

<style scoped>
.base-node {
  width: 100%;
  height: 80px;
  border-radius: 5px; /* 添加圆角 */
}

.top-section {
  position: relative; /* 用于绝对定位子元素 */
  font-size: 13px;
  padding: 10px;
  height: 25px;
  display: flex;
  align-items: center;
  border-top-left-radius: 5px;
  border-top-right-radius: 5px;
}

.delete-btn {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  display: none;
  color: #999;
}

/* 悬停时显示删除按钮 */
.base-node:hover .delete-btn {
  display: block;
}

.bottom-section {
  padding: 10px;
  height: calc(100%);
  font-size: 14px;
}

</style>
