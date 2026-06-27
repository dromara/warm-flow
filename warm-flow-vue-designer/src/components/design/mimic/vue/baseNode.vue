<template>
  <div :style="baseNodeColor" class="base-node" ref="baseNodeDiv">
    <div :style="topSectionColor" class="top-section" v-click-outside="handleLeave">
      <span v-show="showSpan" @click="editNodeName">{{ nodeName }}
        <svg t="1753861236923" class="edit-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1784"
                     width="16" height="16" v-show="canEdit">
            <path d="M469.333333 128a42.666667 42.666667 0 0 1 0 85.333333H213.333333v597.333334h597.333334v-256l0.298666-4.992A42.666667 42.666667 0 0 1 896 554.666667v256a85.333333 85.333333 0 0 1-85.333333 85.333333H213.333333a85.333333 85.333333 0 0 1-85.333333-85.333333V213.333333a85.333333 85.333333 0 0 1 85.333333-85.333333z m414.72 12.501333a42.666667 42.666667 0 0 1 0 60.330667L491.861333 593.066667a42.666667 42.666667 0 0 1-60.330666-60.330667l392.192-392.192a42.666667 42.666667 0 0 1 60.330666 0z"
                  fill="#000000" p-id="1785"></path>
        </svg>
      </span>
      <input
          v-show="editingNodeName"
          ref="nodeNameInput"
          v-model="nodeName"
          @blur="saveNodeName"/>
      <span v-show="props.type === 'between' && canEdit" class="delete-btn" @click.stop="deleteNode">✕</span>
    </div>
    <div class="bottom-section" @click="editNode" :title="handler">{{ handler }}</div>
  </div>
</template>

<script setup lang="ts">
import {computed, ref, watch} from 'vue';
import {handlerFeedback} from "@/api/flow/definition";
import { useI18n } from '@/i18n';

defineOptions({ name: 'BaseInfo' });

const { t } = useI18n();

interface BaseNodeProps {
  /** 节点文本（节点名） */
  text?: string;
  /** 办理人权限标识（@@ 分隔） */
  permissionFlag?: string;
  /** 实例进度图状态色（有值=运行态） */
  chartStatusColor?: any[];
  /** 节点状态 */
  status?: number | null;
  /** 节点类型 */
  type?: string;
  /** 填充色 */
  fill?: string;
  /** 描边色 */
  stroke?: string;
}
const props = withDefaults(defineProps<BaseNodeProps>(), {
  text: '',
  permissionFlag: '',
  chartStatusColor: () => [],
  status: null,
  type: '',
  fill: '',
  stroke: '',
});

const showSpan = ref(true);
const baseNodeDiv = ref<any>(null);
const nodeName = ref(t('baseNode.initiator'));
const handler = ref(t('baseNode.everyone'));
const nodeNameInput = ref<any>(null);
const editingNodeName = ref(false);
const emit = defineEmits<{
  (e: 'updateNodeName', nodeName: string): void;
  (e: 'deleteNode'): void;
  (e: 'editNode'): void;
}>(); // 添加 deleteNode 事件

// 运行态（流程实例查看，chartStatusColor 有三原色）保留状态语义色；设计态走钉钉蓝卡片风格
const isRuntime = computed(() => props.chartStatusColor && props.chartStatusColor.length > 0);

// 设计器只读态（预览 / 已发布）：由 FlowDesigner 渲染前置位的全局标志判定，
// 与运行态一并隐藏编辑图标 / 删除按钮 / 改名输入，避免预览时仍能改节点。
const isReadonly = computed(() => typeof window !== 'undefined' && !!window.__WF_DESIGNER_DISABLED__);
// 是否允许编辑节点（非运行态且非只读态）
const canEdit = computed(() => !isRuntime.value && !isReadonly.value);

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
const DESIGN_HEADER_GRADIENT: Record<string, string> = {
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
        handler.value = t('baseNode.everyone');
      }
    },
    { immediate: true }
);

const editNodeName = () => {
  if (!canEdit.value) {
    return
  }
  editingNodeName.value = true;
  showSpan.value = false;
};

const saveNodeName = () => {
  if (!canEdit.value) {
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
  background: var(--wf-bg-white);
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
  color: var(--wf-text-primary);
}

.edit-icon {
  vertical-align: middle;
  margin: 3px;
}

</style>
