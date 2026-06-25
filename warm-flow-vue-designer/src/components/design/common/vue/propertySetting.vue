<template>
  <div>
    <wf-drawer
      ref="drawerRef"
      :title="title"
      destroy-on-close
      v-model="drawer"
      direction="rtl"
      :size="drawerSize"
      :append-to-body="true"
      :before-close="handleClose"
      class="property-drawer-modern">
      <component :ref="componentType.name" :is="componentType" v-model="form" :disabled="disabled" :skipConditionShow="skipConditionShow"
                 :nodes="nodes" :skips="skips" :form-path-list="formPathList">
        <template v-slot:[key]="data" v-for="(item, key) in $slots">
          <slot :name="key" v-bind="data || {}"></slot>
        </template>
      </component>
    </wf-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, getCurrentInstance, ref, watch } from 'vue'
import start from '@/components/design/common/vue/start.vue'
import between from '@/components/design/common/vue/between.vue'
import serial from '@/components/design/common/vue/gateway.vue'
import parallel from '@/components/design/common/vue/gateway.vue'
import inclusive from '@/components/design/common/vue/gateway.vue'
import end from '@/components/design/common/vue/end.vue'
import skip from '@/components/design/common/vue/skip.vue'
import BaseInfo from "@/components/design/common/vue/baseInfo.vue";

defineOptions({ name: 'PropertySetting' });

const { proxy } = getCurrentInstance()!;

// 响应式窗口宽度：监听 resize 变化
const windowWidth = ref(typeof window !== 'undefined' ? window.innerWidth : 1024);
if (typeof window !== 'undefined') {
  window.addEventListener('resize', () => { windowWidth.value = window.innerWidth; });
}

// 抽屉宽度：手机端全屏，桌面端 37%
const drawerSize = computed(() => {
  return windowWidth.value <= 768 ? '100%' : '37%';
});

// 节点点击打开抽屉时的防抖标记：防止 blank:click 立即关闭
let lastOpenTime = 0;

const COMPONENT_LIST = {
  start,
  between,
  serial,
  parallel,
  inclusive,
  end,
  skip
}

interface PropertySettingProps {
  /** 表单值（保留字段） */
  value?: Record<string, any>;
  /** 当前选中的节点 / 边 model */
  node?: Record<string, any>;
  /** LogicFlow 实例 */
  lf?: any;
  /** 是否只读 */
  disabled?: boolean;
  /** 是否显示跳转条件 */
  skipConditionShow?: boolean;
  /** 画布节点列表 */
  nodes?: any[];
  /** 画布边列表 */
  skips?: any[];
  /** 自定义表单路径树 */
  formPathList?: any[];
}
const props = withDefaults(defineProps<PropertySettingProps>(), {
  value: () => ({}),
  node: () => ({}),
  lf: null,
  disabled: false,
  skipConditionShow: true,
  nodes: () => [],
  skips: () => [],
  formPathList: () => [],
});

const drawer = ref(false);
const form = ref({});
const objId = ref(undefined);
const nodeCode = ref(null);
const title = computed(() => {
  if (props.node && props.node.type === 'skip') {
    return '设置边属性'
  } else if (props.node && props.node.type === 'serial') {
    return '设置串行网关属性'
  } else if (props.node && props.node.type === 'parallel') {
    return '设置并行网关属性'
  } else if (props.node && props.node.type === 'inclusive') {
      return '设置包含网关属性'
  }  else if (props.node && props.node.type === 'start') {
    return '设置开始属性'
  } else if (props.node && props.node.type === 'end') {
    return '设置结束属性'
  }
  return '设置中间属性'
});

// 组件类型
const componentType = computed(() => {
  if (!props.node || !props.node.type) return
  return COMPONENT_LIST[props.node.type]
})


watch(() => props.node, n => {
  if (n) {
    objId.value = n.id
    if (n.type === 'skip') {
      let skipCondition = n.properties.skipCondition
      let condition, conditionType, conditionValue = ''
      if (skipCondition) {
        let conditionSpl = skipCondition.split('@@')
        if (skipCondition && (/^spel/.test(skipCondition) || /^default/.test(skipCondition)) || /^snel/.test(skipCondition)) {
          conditionType = conditionSpl && conditionSpl.length > 0 ? conditionSpl[0] : ''
          conditionValue = conditionSpl && conditionSpl.length > 1 ? conditionSpl[1] : ''
        } else if (skipCondition) {
          conditionType = conditionSpl && conditionSpl.length > 0 ? conditionSpl[0] : ''
          let conditionOneSpl = conditionSpl[1].split("|")
          condition = conditionOneSpl && conditionOneSpl.length > 0 ? conditionOneSpl[0] : ''
          conditionValue = conditionOneSpl && conditionOneSpl.length > 1 ? conditionOneSpl[1] : ''
        }
      }

      form.value = {
        nodeType: n.type,
        skipType: n.properties.skipType,
        skipName: n.text instanceof Object ? n.text.value : n.text,
        skipCondition: skipCondition,
        condition: condition,
        conditionType: conditionType,
        conditionValue: conditionValue
      }
    } else {
      let nodeRatio = n.properties.nodeRatio || "0";
      if (!n.properties.collaborativeWay) {
        n.properties.collaborativeWay = parseFloat(nodeRatio) === 0 ? "1" : parseFloat(nodeRatio) === 100 ?
            "3" : nodeRatio ? "2" : "1";
      }
      if (n.properties.collaborativeWay === "2" && !n.properties.nodeRatio) n.properties.nodeRatio = "50";

      let nodeRatioType = 'passRatio', nodeRatioValue = ''
      if (nodeRatio) {
          if (/^passCount|rejectCount/.test(nodeRatio)) {
              const [type, value] = nodeRatio.split('=');
              nodeRatioType = type;
              nodeRatioValue = value;
          } else if (/^spel|default|snel/.test(nodeRatio)) {
              const [type, value] = nodeRatio.split('@@');
              nodeRatioType = type;
              nodeRatioValue = value;
          } else {
              // 默认情况：直接使用整个字符串
              nodeRatioValue = nodeRatio;
          }
      }

      n.properties.formCustom = JSON.stringify(n.properties) === "{}" ? "N" : (n.properties.formCustom ?
          n.properties.formCustom : props.formPathList && props.formPathList.length > 0 ? "Y" :"N");
      let listenerTypes = n.properties.listenerType ? n.properties.listenerType.split(",") : [];
      let listenerPaths = n.properties.listenerPath ? n.properties.listenerPath.split("@@") : [];
      n.properties.listenerRows = listenerTypes && listenerTypes.length > 0 ? listenerTypes.map((type, index) => ({
        listenerType: type,
        listenerPath: listenerPaths[index]
      })) : [{}];
      form.value = {
        nodeType: n.type,
        nodeCode: n.id,
        ext: n.properties.ext ? n.properties.ext : {},
        ...n.properties,
        nodeName: n.text instanceof Object ? n.text.value : n.text,
        nodeRatioType: nodeRatioType,
        nodeRatioValue: nodeRatioValue,
      }
    }
  }
});

watch(() => form.value.nodeCode, (n) => {
  nodeCode.value = n;
});

watch(() => form.value.skipType, (n) => {
  // 监听跳转属性变化并更新
  props.lf.setProperties(objId.value, {
    skipType: n
  })

});

watch(() => form.value.nodeName, (n) => {
  // 更新流程节点上的文本内容
  props.lf.updateText(objId.value, n)
  // 监听节点名称变化并更新
  props.lf.setProperties(objId.value, {
    nodeName: n
  })
});

watch(() => form.value.collaborativeWay, (n) => {
  // 监听节点属性变化并更新
  props.lf.setProperties(objId.value, {
    nodeRatio: n === "1" ? "0" : n === "3" ? "100" : "50"
  })
});

watch(() => form.value.nodeRatio, (n) => {
  // 监听节点属性变化并更新
  props.lf.setProperties(objId.value, {
    nodeRatio: n
  })
});

watch(() => form.value.permissionFlag, (n) => {
  // 监听节点属性变化并更新
  props.lf.setProperties(objId.value, {
    permissionFlag: Array.isArray(n) ? n.filter(e => e).join('@@') : n
  })
}, { deep: true });

watch(() => form.value.anyNodeSkip, (n) => {
  // 监听跳转属性变化并更新
  props.lf.setProperties(objId.value, {
    anyNodeSkip: n
  })
});

// 监听：监听器路类型数组
watch(() => form.value.listenerRows?.map(e => e.listenerType), (n) => {
  // 监听监听器类型变化并更新
  props.lf.setProperties(objId.value, {
    listenerType: Array.isArray(n) ? n.join(",") : n
  })
}, { deep: true });

// 监听：监听器路径数组
watch(() => form.value.listenerRows?.map(e => e.listenerPath), (n) => {
  // 监听监听器类型变化并更新
  props.lf.setProperties(objId.value, {
    listenerPath: Array.isArray(n) ? n.join("@@") : n
  })
}, { deep: true });

watch(() => form.value.formCustom, (n) => {
  props.lf.setProperties(objId.value, {
    formCustom: n || ""
  })
});

watch(() => form.value.formPath, (n) => {
  props.lf.setProperties(objId.value, {
    formPath: n
  })
});

watch(() => form.value.skipName, (n) => {
  if (['skip'].includes(props.node.type)) {
    // 监听跳转名称变化并更新
    props.lf.updateText(objId.value, n)
    // 监听跳转属性变化并更新
    props.lf.setProperties(objId.value, {
      skipName: n
    })
  }
});

watch(() => form.value.skipCondition, (n) => {
  // 监听跳转属性变化并更新
  props.lf.setProperties(objId.value, {
    skipCondition: n
  })

});

watch(() => form.value.ext, (n) => {
  // 监听节点属性变化并更新
  props.lf.setProperties(objId.value, {
    ext: n
  })
}, { deep: true });

function show () {
  lastOpenTime = Date.now();
  if (window._markDrawerOpen) window._markDrawerOpen();
  drawer.value = true
}

async function handleClose () {
  // 防抖：节点打开后 200ms 内的 blank:click 不执行关闭
  if (Date.now() - lastOpenTime < 200) return;
  // 如果抽屉未打开，直接返回
  if (!drawer.value) return;
  if (!props.disabled && typeof proxy.$refs[componentType.value?.name]?.validate === "function") {
    // 校验表单必填项
    await proxy.$refs[componentType.value.name].validate().then(() => {
      handleDrawer();
    }).catch(err => {
      return;
    });
  } else handleDrawer();
}

function handleDrawer () {
  if (nodeCode.value && objId.value) {
    if (['skip'].includes(props.node?.type)) {
      if (!props.lf.getEdgeModelById(nodeCode.value)) {
        props.lf.changeEdgeId(objId.value, nodeCode.value)
      }
    } else {
      if (!props.lf.getNodeModelById(nodeCode.value)) {
        props.lf.changeNodeId(objId.value, nodeCode.value)
      }
    }
  }
  drawer.value = false
  // 延迟解除标志位（由 index.vue 的 _markDrawerClosed + scheduleMobileResize
  // 统一管理抽屉关闭后的画布适配，避免多处 fitView 时序冲突导致节点跳动）
  if (window._markDrawerClosed) window._markDrawerClosed();
}


defineExpose({
  show,
  handleClose,
})
</script>

<style scoped lang="scss">
/* 抽屉容器隐藏滚动条（组件内部生效） */
.el-drawer__container ::-webkit-scrollbar {
  display: none;
}
</style>

<!-- 全局样式：抽屉 append-to-body 后需要全局穿透（scoped 无法命中 teleport 到 body 的元素） -->
<style lang="scss">
/* ========== 现代化属性面板抽屉 ========== */
.property-drawer-modern {
  /* 与基础信息页统一：系统字体栈 + 字形平滑 */
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "PingFang SC", "Helvetica Neue", "Microsoft YaHei", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;

  /* 表单标签：与基础信息页统一（加粗；右对齐由 EP 默认 label-position 提供） */
  .el-form-item__label {
    font-weight: 600;
    color: var(--wf-text-primary, #303133);
  }

  /* 抽屉头部：收紧与正文间距（EP 默认 margin 32px 太空）+ 标题加深加粗（默认偏浅灰、不清晰） */
  .el-drawer__header {
    background: transparent !important;
    margin-bottom: 0 !important;
    padding-bottom: 14px !important;
    border-bottom: 1px solid var(--wf-border-lighter, #ebeef5);
    > span {
      color: var(--wf-text-primary, #303133);
      font-weight: 600;
      font-size: 16px;
    }
  }

  /* 正文上内边距收紧，让 tab 紧跟头部 */
  .el-drawer__body {
    padding-top: 12px !important;
  }
  /* 含 Tab 的面板（开始/中间节点）：tab 栏紧贴抽屉标题，去掉上方间距，对齐更自然 */
  .el-drawer__body:has(.modern-tabs-wrapper) {
    padding-top: 0 !important;
  }

  .el-drawer__close-btn {
    color: #909399 !important;
    transition: all .25s ease !important;
    &:hover { color: #409eff !important; transform: rotate(90deg); }
  }

  /* 白底描边输入框：与基础信息表单统一（白底 + 浅边框 + 柔和 focus 光环） */
  .el-input__wrapper,
  .el-textarea__inner {
    border-radius: 10px;
    background-color: var(--wf-bg-white, #fff);
    box-shadow: none;
    border: 1px solid var(--wf-border-light, #dcdfe6);
    transition: background-color .2s ease, box-shadow .2s ease, border-color .2s ease;
    &:hover { border-color: var(--wf-primary, #409eff); }
    &.is-focus,
    &:focus {
      background-color: var(--wf-bg-white, #fff);
      border-color: var(--wf-primary, #409eff);
      box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.12);
    }
  }

  /* 校验失败态：红边 + 红光环，压过 focus 蓝色（与基础信息一致） */
  .el-form-item.is-error .el-input__wrapper,
  .el-form-item.is-error .el-input__wrapper.is-focus {
    border-color: var(--wf-danger, #f56c6c);
    box-shadow: 0 0 0 4px rgba(245, 108, 108, 0.12);
  }

  /* 表格：与基础信息页统一（圆角边框 + 中性表头 + hover 蓝条 + 删除按钮红渐变） */
  .el-table {
    border-radius: var(--wf-radius-lg, 12px);
    overflow: hidden;
    border: 1px solid var(--wf-border-lighter, #ebeef5);

    .el-table__header-wrapper th {
      background: var(--wf-bg-white, #fff) !important;
      color: var(--wf-text-regular, #606266);
      font-weight: 600;
      font-size: 13px;
    }
    .el-table__row {
      transition: background-color 0.2s ease;
      & > td:first-child { position: relative; }
      & > td:first-child::before {
        content: '';
        position: absolute; left: 0; top: 50%; transform: translateY(-50%);
        width: 3px; height: 0;
        background: var(--wf-primary, #409eff);
        border-radius: 0 2px 2px 0;
        transition: height 0.25s ease;
      }
      &:hover > td:first-child::before { height: 60%; }
      &:hover > td { background-color: var(--wf-primary-light, #ecf5ff) !important; }
    }
    /* 删除按钮：无实底，仅红色图标，hover 浅红圆底 */
    .el-button--danger.is-link {
      color: var(--wf-danger, #f56c6c);
      padding: 5px;
      height: auto;
      border: none;
      transition: all 0.2s ease;
      &:hover {
        color: #e04848;
        background: rgba(245, 108, 108, 0.12) !important;
        border-radius: 8px;
      }
    }

    /* 表格内控件默认透明：整行读作干净白底，hover 轻填充、focus 才显白底+蓝环。
       避免「全是下拉/输入的行」被填充底染灰、与「含纯文本列的行」深浅不一致 */
    .el-input__wrapper,
    .el-select .el-input__wrapper {
      background-color: transparent;
      &:hover { background-color: rgba(118, 128, 150, 0.07); }
      &.is-focus { background-color: var(--wf-bg-white, #fff); }
    }
  }
}

/* ========== 暗黑模式：el-drawer 抽屉完整适配（全局生效） ========== */
html.dark {
  .property-drawer-modern {
    /* 整体抽屉：覆盖 Element Plus 默认白色变量 + 直接设色双重保险 */
    .el-drawer {
      --el-drawer-bg-color: var(--wf-bg-color, #141414) !important;
      --el-bg-color: var(--wf-bg-color, #141414) !important;
      background-color: var(--wf-bg-color, #141414) !important;

      /* 消除 header 和 body 之间的白色缝隙 */
      &__header {
        margin-bottom: 0 !important;
        padding-bottom: 12px !important;
        background: var(--wf-bg-color, #141414) !important;
        border-bottom: 1px solid var(--wf-border-color, #2a2a2a) !important;

        > span {
          color: var(--wf-text-primary, #e0e0e0) !important;
          font-weight: 600;
        }
      }

      &__body {
        background-color: var(--wf-bg-color, #141414) !important;
        margin-top: 0 !important;
      }

      &__close-btn {
        color: var(--wf-text-secondary, #888888) !important;
        &:hover { color: #409eff !important; }
      }
    }

    /* 抽屉容器本身也要深色（防止 append-to-body 后外层残留白色） */
    &.el-drawer,
    &.el-drawer__wrapper,
    .v-modal + .el-overlay .el-drawer {
      --el-drawer-bg-color: var(--wf-bg-color, #141414) !important;
      background-color: var(--wf-bg-color, #141414) !important;
    }

    /* iOS 填充式输入框：暗黑覆盖 */
    .el-input__wrapper,
    .el-textarea__inner {
      background-color: rgba(255, 255, 255, 0.06) !important;
      box-shadow: none !important;
      border: 1px solid transparent !important;
    }
    .el-input__wrapper.is-focus {
      border-color: var(--wf-primary, #409eff) !important;
      box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.18) !important;
    }

    /* 表格暗黑：中性表头 + 蓝调 hover */
    .el-table {
      border-color: var(--wf-border-color, #333333);
      .el-table__header-wrapper th {
        background: rgba(255, 255, 255, 0.04) !important;
        color: var(--wf-text-regular, #b0b0b0);
      }
      .el-table__row:hover > td {
        background-color: rgba(64, 158, 255, 0.12) !important;
      }
    }
  }

  /* el-dialog 对话框暗黑适配（selectUser 等弹窗） */
  .el-dialog {
    --el-dialog-bg-color: var(--wf-bg-white, #1f1f1f);

    &__header {
      background: linear-gradient(135deg, rgba(64,158,255,.08), rgba(43,125,233,.04));
      border-bottom: 1px solid var(--wf-border-color, #333333);
    }

    &__title { color: var(--wf-text-primary, #e0e0e0); }

    &__body { color: var(--wf-text-regular, #b0b0b0); }

    &__headerbtn {
      color: var(--wf-text-secondary, #888888);
      &:hover { color: var(--wf-text-primary, #e0e0e0); }
    }
  }
}
</style>
