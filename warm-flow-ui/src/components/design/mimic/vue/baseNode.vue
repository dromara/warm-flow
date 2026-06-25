<template>
  <div :style="baseNodeColor" class="base-node" ref="baseNodeDiv">
    <div :style="topSectionColor" class="top-section" v-click-outside="handleLeave">
      <span v-show="showSpan" @click="editNodeName">{{ nodeName }}
        <svg t="1753861236923" class="edit-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1784"
                     width="16" height="16" v-show="!chartStatusColor || chartStatusColor.length === 0">
            <path d="M469.333333 128a42.666667 42.666667 0 0 1 0 85.333333H213.333333v597.333334h597.333334v-256l0.298666-4.992A42.666667 42.666667 0 0 1 896 554.666667v256a85.333333 85.333333 0 0 1-85.333333 85.333333H213.333333a85.333333 85.333333 0 0 1-85.333333-85.333333V213.333333a85.333333 85.333333 0 0 1 85.333333-85.333333z m414.72 12.501333a42.666667 42.666667 0 0 1 0 60.330667L491.861333 593.066667a42.666667 42.666667 0 0 1-60.330666-60.330667l392.192-392.192a42.666667 42.666667 0 0 1 60.330666 0z"
                  fill="#000000" p-id="1785"></path>
        </svg>
      </span>
      <input
          v-show="editingNodeName"
          ref="nodeNameInput"
          v-model="nodeName"
          @blur="saveNodeName"/>
      <span v-show="props.type === 'between' && (!chartStatusColor || chartStatusColor.length === 0)" class="delete-btn" @click.stop="deleteNode">✕</span>
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
  status: {
    type: Number,
    default () {
      return null
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

// 运行态（流程实例查看，chartStatusColor 有三原色）保留状态语义色；设计态走钉钉蓝卡片风格
const isRuntime = computed(() => props.chartStatusColor && props.chartStatusColor.length > 0);

const baseNodeColor = computed(() => {
  if (isRuntime.value) {
    return {
      border: (props.status === 1 ? "2px dashed " : "1px solid ") + (props.stroke || "rgb(166,178,189)"),
    };
  }
  // 设计态：无边框卡片，仅靠柔和阴影区分层次；背景跟随主题变量（暗黑模式自动变深）
  return {
    background: "var(--wf-bg-white, #fff)",
  };
});

// 设计态头部按节点类型区分（开始=绿 / 结束=红 / 其余审批=蓝），不再全蓝
const DESIGN_HEADER_GRADIENT = {
  start: "linear-gradient(135deg, #67c23a 0%, #5daf34 100%)",
  end: "linear-gradient(135deg, #f56c6c 0%, #e85c5c 100%)",
};

const topSectionColor = computed(() => {
  if (isRuntime.value) {
    return { backgroundColor: props.stroke || "rgb(166,178,189)" };
  }
  // 设计态：按节点类型着色头部，默认审批蓝
  return { background: DESIGN_HEADER_GRADIENT[props.type] || "linear-gradient(135deg, #409eff 0%, #2b7de9 100%)" };
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
  if (props.chartStatusColor && props.chartStatusColor.length > 0) {
    return
  }
  editingNodeName.value = true;
  showSpan.value = false;
};

const saveNodeName = () => {
  if (props.chartStatusColor && props.chartStatusColor.length > 0) {
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
  box-sizing: border-box;
  border-radius: 10px;
  background: var(--wf-bg-white, #fff);
  /* 去边框，仅用多层柔和阴影区分层次（现代卡片质感） */
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.10), 0 1px 4px rgba(0, 0, 0, 0.05);
  overflow: hidden; /* 让头部跟随卡片圆角 */
}

.top-section {
  position: relative; /* 用于绝对定位子元素 */
  font-size: 13px;
  padding: 10px;
  height: 25px;
  display: flex;
  align-items: center;
  color: #fff; /* 彩色头部，文字/图标统一白色 */
}

/* 头部为彩色底，编辑图标改白色（原 svg 写死黑色） */
.top-section .edit-icon path {
  fill: #fff;
}

.delete-btn {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  display: block;
  color: #fff;
}

.bottom-section {
  padding: 10px;
  height: calc(100%);
  font-size: 14px;
  color: var(--wf-text-primary, #303133);
}

.edit-icon {
  vertical-align: middle;
  margin: 3px;
}

</style>
