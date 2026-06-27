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
import { useI18n } from '@/i18n';

defineOptions({ name: 'PropertySetting' });

const { proxy } = getCurrentInstance()!;
const { t } = useI18n();

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
    return t('property.titleEdge')
  } else if (props.node && props.node.type === 'serial') {
    return t('property.titleSerial')
  } else if (props.node && props.node.type === 'parallel') {
    return t('property.titleParallel')
  } else if (props.node && props.node.type === 'inclusive') {
      return t('property.titleInclusive')
  }  else if (props.node && props.node.type === 'start') {
    return t('property.titleStart')
  } else if (props.node && props.node.type === 'end') {
    return t('property.titleEnd')
  }
  return t('property.titleBetween')
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
@import './propertySetting.scoped.scss';
</style>

<!-- 全局样式：抽屉 append-to-body 后需要全局穿透（scoped 无法命中 teleport 到 body 的元素） -->
<style lang="scss">
@import './propertySetting.global.scss';
</style>
