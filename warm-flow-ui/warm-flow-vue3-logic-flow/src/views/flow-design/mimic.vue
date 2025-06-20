<template>
  <div>
    <div class="top-text">流程名称: {{value.flowName}}</div>

    <el-header :style="headerStyle">
      <div class="log-text">Warm-Flow</div>
      <div style="padding: 5px 0; text-align: right;">
        <div>
          <el-button size="small" icon="ZoomOut" @click="zoomViewport(false)">缩小</el-button>
          <el-button size="small" icon="Rank" @click="zoomViewport(1)">自适应</el-button>
          <el-button size="small" icon="ZoomIn" @click="zoomViewport(true)">放大</el-button>
          <el-button size="small" icon="DArrowLeft" @click="undoOrRedo(true)">上一步</el-button>
          <el-button size="small" icon="DArrowRight" @click="undoOrRedo(false)">下一步</el-button>
          <el-button size="small" icon="Delete" @click="clear()">清空</el-button>
          <el-button size="small" icon="DocumentAdd" @click="saveJsonModel">保存</el-button>
          <el-button size="small" icon="Download" @click="downLoad">下载流程图</el-button>
          <el-button size="small" icon="Download" @click="downJson">下载json</el-button>
        </div>
      </div>
    </el-header>
    <div class="container" ref="container">
      <PropertySetting ref="propertySettingRef" :node="nodeClick" v-model="processForm" :lf="lf" :disabled="disabled"
                       :skipConditionShow="skipConditionShow" :nodes="nodes" :skips="skips">
        <template v-slot:[key]="data" v-for="(item, key) in $slots">
          <slot :name="key" v-bind="data || {}"></slot>
        </template>
      </PropertySetting>
    </div>
    <div class="log-text">Warm-Flow</div>
  </div>
</template>

<script setup name="mimic">
import LogicFlow from "@logicflow/core";
import "@logicflow/core/lib/style/index.css";
import { Menu, Snapshot } from '@logicflow/extension';
import '@logicflow/extension/lib/style/index.css'
import { ElLoading } from 'element-plus'
import Start from "@/components/design/mimic/js/start";
import Between from "@/components/design/mimic/js/between";
import Serial from "@/components/design/mimic/js/serial";
import Parallel from "@/components/design/mimic/js/parallel";
import End from "@/components/design/mimic/js/end";
import Skip from "@/components/design/mimic/js/skip";
import PropertySetting from '@/components/design/mimic/PropertySetting/index.vue'
import { queryDef, saveJson } from "@/api/flow/definition";
import { json2LogicFlowJson, logicFlowJsonToWarmFlow } from "@/components/design/common/js/tool";
import { addBetweenNode, addGatewayNode, gatewayAddNode } from "@/components/design/mimic/js/mimic";
import useAppStore from "@/store/app";
import {computed, onMounted, onUnmounted, ref, watch} from "vue";
import request from "@/utils/request.js";
const urlPrefix = import.meta.env.VITE_URL_PREFIX
const appStore = useAppStore();
const appParams = computed(() => useAppStore().appParams);

const { proxy } = getCurrentInstance();

const lf = ref(null);
const definitionId = ref(null);
const nodeClick = ref(null);
const disabled = ref(false);
const processForm = ref({});
const propertySettingRef = ref({});
const value = ref({});
const jsonString = ref('');
const skipConditionShow = ref(true);
const nodes = ref([]);
const skips = ref([]);
const isDark = ref(false);

const headerStyle = computed(() => {
  return {
    top: "5px",
    right: "50px",
    zIndex: "2",
    height: "auto",
    backgroundColor: isDark.value ? "#333" : "#fff",
  };
});


onMounted(async () => {
  if (!appParams.value) await appStore.fetchTokenName();
  definitionId.value = appParams.value.id;
  if (appParams.value.disabled === 'true') {
    disabled.value = true
  }
  use();
  lf.value = new LogicFlow({
    container: proxy.$refs.container,
    snapline: true,
    grid: {
      size: 20,
      visible: 'true' === appParams.value.showGrid,
      type: 'dot',
      config: {
        color: '#ccc',
        thickness: 1,
      },
      background: {
        backgroundColor: "#fff",
      },
    },
    keyboard: {
      enabled: true,
      shortcuts: [
        {
          keys: ["delete"],
          callback: () => {
            const elements = lf.value.getSelectElements(true);
            lf.value.clearSelectElements();
            elements.edges.forEach((edge) => lf.value.deleteEdge(edge.id));
            elements.nodes.forEach((node) => lf.value.deleteNode(node.id));
          },
        },
      ],
    },
  });
  register();
  lf.value.setTheme({
    snapline: {
      stroke: '#1E90FF', // 对齐线颜色
      strokeWidth: 2, // 对齐线宽度
    },
  })
  initMenu();
  initEvent();
  // 隐藏滚动条
  document.body.style.overflow = 'hidden';
  if (definitionId.value) {
    queryDef(definitionId.value).then(res => {
      jsonString.value = res.data;
      if (jsonString.value) {
        value.value = json2LogicFlowJson(jsonString.value);
        lf.value.render(value.value);
        // 将内容平移至画布中心
        lf.value.translateCenter()
      }
    }).catch(() => {
      lf.value.render({});
    });
  }
  if (!definitionId.value) {
    proxy.$modal.notifyError("流程id不能为空！");
    lf.value.render({});
  }
})

watch(isDark, (v) => {
  if (!lf.value) {
    return;
  }
  lf.value.graphModel.background = {
    background: v ? "#333" : "#fff",
  };
});

/**
 * data为 {type: string, data?: any}
 * @param e
 */
function listeningMessage(e) {
  const { data } = e;
  switch (data.type) {
    case "theme-dark": {
      isDark.value = true;
      return;
    }
    case "theme-light": {
      isDark.value = false;
      return;
    }
  }
}

onMounted(() => {
  window.addEventListener("message", listeningMessage);
});
onUnmounted(() => {
  window.removeEventListener("message", listeningMessage);
});

function saveJsonModel() {
  const loadingInstance = ElLoading.service(({ fullscreen: true , text: "保存中，请稍等"}))
  let graphData = lf.value.getGraphData()
  value.value['nodes'] = graphData['nodes']
  value.value['edges'] = graphData['edges']
  value.value['id'] = definitionId.value
  let jsonString = logicFlowJsonToWarmFlow(value.value);
  saveJson(jsonString).then(response => {

    if (response.code === 200) {
      proxy.$modal.msgSuccess("保存成功");
      close();
    }
  }).finally(() => {
    nextTick(() => {
      loadingInstance.close();
    });
  });
}

/**
 * 初始化菜单
 */
function initMenu() {
  // 指定类型元素配置菜单
  lf.value.extension.menu.setMenuByType({
    type: "serial",
    menu: [
      {
        text: "添加中间节点",
        callback(node) {
          gatewayAddNode(lf.value, node);
        },
      },
    ],
  });

  lf.value.extension.menu.setMenuByType({
    type: "parallel",
    menu: [
      {
        text: "添加中间节点",
        callback(node) {
          gatewayAddNode(lf.value, node);
        },
      },
    ],
  });

  // 为菜单追加选项（必须在 lf.render() 之前设置）
  lf.value.extension.menu.addMenuConfig({
    edgeMenu: [
      {
        text: "添加中间节点",
        callback(edge) {
          addBetweenNode(lf.value, edge, "between");
        },
      },
      {
        text: "添加互斥网关",
        callback(edge) {
          addGatewayNode(lf.value, edge, "serial");
        },
      },
      {
        text: "添加并行网关",
        callback(edge) {
          addGatewayNode(lf.value, edge, "parallel");
        },
      },
    ],
  });
}


/**
 * 注册自定义节点和边
 */
function register() {
  lf.value.register(Start);
  lf.value.register(Between);
  lf.value.register(Serial);
  lf.value.register(Parallel);
  lf.value.register(End);
  lf.value.register(Skip);
}

/**
 * 添加扩展
 */
function use() {
  LogicFlow.use(Menu);
  LogicFlow.use(Snapshot);
}
function initEvent() {
  const { eventCenter } = lf.value.graphModel
  eventCenter.on('node:mouseenter', (args) => {
    if (['serial', 'parallel'].includes(args.data.type)) {
    }
  });

  // 中间节点双击事件
  eventCenter.on('node:dbclick', (args) => {
    if ('between' === args.data.type) {
      nodeClick.value = args.data
      let graphData = lf.value.getGraphData()
      nodes.value = graphData['nodes']
      skips.value = graphData['edges']
      proxy.$nextTick(() => {
        propertySettingRef.value.show()
      })
    }
  })

  // 边双击事件
  eventCenter.on('edge:dbclick  ', (args) => {
    nodeClick.value = args.data
    const nodeModel = lf.value.getNodeModelById(nodeClick.value.sourceNodeId);
    skipConditionShow.value = nodeModel['type'] === 'serial'
    let graphData = lf.value.getGraphData()
    nodes.value = graphData['nodes']
    skips.value = graphData['edges']
    proxy.$nextTick(() => {
      propertySettingRef.value.show(nodeModel['nodeType'] === 'serial')
    })
  })

  eventCenter.on('edge:add', (args) => {
    lf.value.changeEdgeType(args.data.id, 'skip')
    // 修改边类型
    lf.value.setProperties(args.data.id, {
      skipType: 'PASS'
    })
  })

  eventCenter.on('blank:click', () => {
    nodeClick.value = null
    proxy.$nextTick(() => {
      propertySettingRef.value.handleClose()
    })
  })
}
/** 关闭按钮 */
function close() {
  window.parent.postMessage({ method: "close" }, "*");
}
const zoomViewport = async (zoom) => {
  lf.value.zoom(zoom);
  // 将内容平移至画布中心
  lf.value.translateCenter();
};
const undoOrRedo = async (undo) => {
  if(undo){
    lf.value.undo(undo)
  }else{
    lf.value.redo(undo)
  }
};
//清空
const clear = async () => {
  lf.value.clearData()
}

/**
 * 下载流程图
 */
function downLoad() {
  lf.value.getSnapshot(value.value.flowName, {
    fileType: 'png',        // 可选：'png'、'webp'、'jpeg'、'svg'
    backgroundColor: '#f5f5f5',
    padding: 30,           // 内边距，单位为像素
    partial: false,        // false: 导出所有元素，true: 只导出可见区域
    quality: 0.92          // 对jpeg和webp格式有效，取值范围0-1
  })
}

async function downJson() {
  const url = urlPrefix + `warm-flow/down-json/${definitionId.value}`;
  const filename = `${value.value.flowName}_${value.value.version}.json`;

  try {
    // 确保请求返回 Blob 数据
    const response = await request({
      url: url,
      method: 'get'
    });

    // 创建 Blob 并触发下载
    const blob = new Blob([response.data]);
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(downloadUrl);
  } catch (error) {
    console.error('下载失败:', error);
  }
}

</script>

<style>

.container {
  flex: 1;
  width: 100%;
  height: 800px;
}

.top-text {
  position: absolute;
  font-weight: bold;
  left: 500px;
  top: 2px;
  border: 1px solid #d1e9ff;
  background-color: #e8f4ff;
  padding: 4px 8px;
  border-radius: 4px;
  max-width: 300px;
  font-size: 15px; /* 可以根据需要调整字体大小 */
  color: #333; /* 可以根据需要调整颜色 */
  z-index: 1; /* 确保文本在其他内容之上显示 */
}

.log-text {
  position: absolute;
  font-weight: bold;
  right: 10px;
  bottom: 10px;
  font-size: 15px; /* 可以根据需要调整字体大小 */
  color: #333; /* 可以根据需要调整颜色 */
  z-index: 1; /* 确保文本在其他内容之上显示 */
}
</style>
