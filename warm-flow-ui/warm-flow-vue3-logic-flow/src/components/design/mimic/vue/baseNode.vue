<template>
  <div class="base-node" ref="baseNodeDiv">
    <div class="top-section">
      <span v-if="showSpan" @click="editNodeName">{{ nodeName }} 📝</span>
      <input
          v-if="editingNodeName"
          ref="nodeNameInput"
          v-model="nodeName"
          @blur="saveNodeName"/>
      <span v-if="props.type === 'between'" class="delete-btn" @click.stop="deleteNode">✕</span>
    </div>

    <div class="bottom-section">{{ handler }}</div>

  </div>
</template>

<script setup name="BaseInfo">
import {ref} from 'vue';
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
  type: {
    type: String,
    default () {
      return ''
    }
  },
});

const showSpan = ref(true);
const baseNodeDiv = ref(null);


const emit = defineEmits(['updateNodeName', 'deleteNode']); // 添加 deleteNode 事件

const deleteNode = () => {
  emit('deleteNode'); // 触发删除事件，由父组件处理
};


const nodeName = ref('发起人');
const handler = ref('所有人');

const editingNodeName = ref(false);

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
  showSpan.value = false;
  editingNodeName.value = true;
  setTimeout(() => {
    nodeNameInput.value.focus();
  }, 0);
};



const saveNodeName = () => {
  editingNodeName.value = false;
  showSpan.value = true;
  emit('updateNodeName', nodeName.value);
};

const nodeNameInput = ref(null);

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
  position: relative; /* 用于绝对定位子元素 */
  background-color: #ccc;
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
  font-size: 16px;
  color: #999;
}

/* 悬停时显示删除按钮 */
.base-node:hover .delete-btn {
  display: block;
}

.bottom-section {
  padding: 10px;
  height: calc(100%);
}

</style>
