<template>
  <div>
    <div class="top-text">流程名称: {{value.flowName}}</div>

    <el-header :style="headerStyle">
      <div class="log-text">Warm-Flow</div>
      <div style="padding: 5px 0; text-align: right;">
        <div>
          <el-button size="small" icon="ZoomOut" @click="zoomViewport(false)">缩小</el-button>
          <el-button size="small" icon="ZoomIn" @click="zoomViewport(true)">放大</el-button>
          <el-button size="small" icon="Rank" @click="zoomViewport(1)">自适应屏幕</el-button>
          <el-button size="small" icon="DArrowLeft" @click="undoOrRedo(true)">上一步</el-button>
          <el-button size="small" icon="DArrowRight" @click="undoOrRedo(false)">下一步</el-button>
          <el-button size="small" icon="Delete" @click="clear()">清空</el-button>
          <el-button size="small" icon="DocumentAdd" @click="saveJsonModel">保存</el-button>
          <el-button size="small" icon="Download" @click="downLoad">下载流程图</el-button>
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

<script setup name="Design">
import LogicFlow from "@logicflow/core";
import "@logicflow/core/lib/style/index.css";
import {DndPanel, Menu, SelectionSelect, Snapshot, InsertNodeInPolyline } from '@logicflow/extension';
import '@logicflow/extension/lib/style/index.css'
import { ElLoading } from 'element-plus'
import Start from "@/components/WarmFlow/js/start";
import Between from "@/components/WarmFlow/js/between";
import Serial from "@/components/WarmFlow/js/serial";
import Parallel from "@/components/WarmFlow/js/parallel";
import End from "@/components/WarmFlow/js/end";
import Skip from "@/components/WarmFlow/js/skip";
import PropertySetting from '@/components/WarmFlow/PropertySetting/index.vue'
import { queryDef, saveJson } from "@/api/flow/definition";
import {
  json2LogicFlowJson,
  logicFlowJsonToWarmFlow,
  addBetweenNode,
  addGatewayNode, gatewayAddNode
} from "@/components/WarmFlow/js/tool";
import useAppStore from "@/store/app";
import {computed, onMounted, onUnmounted, ref, watch} from "vue";
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
  initDndPanel();
  lf.value.setTheme({
    snapline: {
      stroke: '#1E90FF', // 对齐线颜色
      strokeWidth: 2, // 对齐线宽度
    },
  })
  // initMenu();
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

/**
 * 初始化拖拽面板
 */
function initDndPanel() {
  lf.value.extension.dndPanel.setPatternItems([
    {
      type: 'start',
      text: '开始',
      label: '开始节点',
      icon: 'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB0PSIxNzQ4MTc1OTQ3Mzg4IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjIwODA5IiB3aWR0aD0iMzYiIGhlaWdodD0iMzYiPjxwYXRoIGQ9Ik01MTIgMTAyNEMyMjkuMjMwNDMxIDEwMjQgMCA3OTQuNzY5NTY5IDAgNTEyUzIyOS4yMzA0MzEgMCA1MTIgMHM1MTIgMjI5LjIzMDQzMSA1MTIgNTEyLTIyOS4yMzA0MzEgNTEyLTUxMiA1MTJ6IG0wLTk1MC4zNzkwODVDMjY5Ljg4OTI1NSA3My42MjA5MTUgNzMuNjIwOTE1IDI2OS44ODkyNTUgNzMuNjIwOTE1IDUxMnMxOTYuMjY4MzQgNDM4LjM3OTA4NSA0MzguMzc5MDg1IDQzOC4zNzkwODUgNDM4LjM3OTA4NS0xOTYuMjY4MzQgNDM4LjM3OTA4NS00MzguMzc5MDg1Uzc1NC4xMTA3NDUgNzMuNjIwOTE1IDUxMiA3My42MjA5MTV6IiBmaWxsPSIjMDAwMDAwIiBwLWlkPSIyMDgxMCI+PC9wYXRoPjwvc3ZnPg==',
    },
    {
      type: 'between',
      text: '中间节点',
      label: '中间节点',
      icon: 'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB0PSIxNzQ4MTc1Mzc1ODI3IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjgzMTkiIHdpZHRoPSIzNiIgaGVpZ2h0PSIzNiI+PHBhdGggZD0iTTMzNS43NTE4MDQgMjQ0Ljc4MzE0N0MyODMuNjA3MDI5IDI0NC43ODMxNDcgMjQ2LjMwMzg5OSAyODQuODY5MDYgMjQ2LjE5OTI3IDMzMC41Mjk2NDJsMCAwLjAxMzIwNyAwIDAuMDEyNjQ4YzAuMDAzMjk0IDEzLjgwODQ2NSAzLjczOTc0MyAyOC4zODE3NDcgOS41Nzc0MzEgNDEuNTI2MzQyIDQuMjE1MTMyIDkuNDkxMTE1IDkuNDU1Nzk4IDE4LjIxNjY3NiAxNS44NDE3NDQgMjUuMjA2NjE0QzIzMy42NjU1ODggNDEwLjI3Mjg1MyAxODkuMjAxOTQ5IDQzMS42NDI4ODMgMTY2LjcyOCA0NzMuNzgxNTNMMTY1LjUxNjk2IDQ3Ni4wNTI1MTRsMCAxMzYuNDA4NDgyIDM0MC40Njk2ODkgMCAwLTEzNi40MDg0ODItMS4yMTEwNDMtMi4yNzA5ODRjLTIyLjE1MDcwOC00MS41MzI1NjYtNjUuNjUyMjA0LTYyLjg3Mjg0OC0xMDMuMjM4MjA0LTc1LjkxMTExNiAxOC4zNDg0MTktMTguNjU4MjE4IDIzLjc2MTA0OS00Mi43NDA1NzMgMjMuNzY2OTMyLTY3LjMxNDkybDAtMC4wMTI2NDggMC0wLjAxMzIwN0M0MjUuMTk5NzA3IDI4NC44NjkwNjMgMzg3Ljg5NjU4MyAyNDQuNzgzMTQ3IDMzNS43NTE4MDQgMjQ0Ljc4MzE0N1pNMzAwLjE0ODUyMyAyOTMuNDA0NTIxYzIuNDEwMzI4IDAuMDA2MDU5IDUuMDU2NjUgMC4wODY1NzIgNy45NzQwMTUgMC4yNTg1MjIgMjMuMjQ0MDI5IDEuMzcwMDI5IDMxLjA2Njk5NiA1LjU1NDA1OSAzNy4wODAzMjMgOS41MjIyODMgNi4wMTMyOTggMy45NjgyMjMgMTAuMjUyNDE3IDcuNzQ1Njk5IDI2LjE0NDE4OSA4LjIwODk4MmwwLjAwNDcwNiAwIDAuMDA1Mjk1IDBjMTIuMzgzODktMC40NjMyMTUgMTguMzM5NTA2LTIuNjcxMTM1IDIyLjYxMDQ1Mi01LjE3MjE5MiAxLjczMDY0NS0xLjAxMzQ1MyAzLjE4MzgyNS0yLjA2NzAwMyA0LjY3Mjk1LTMuMDcyOTg0IDMuOTM2MDM2IDguNDM2NjY4IDYuMDQ5NjU0IDE3Ljc2MjkzOCA2LjA3MzU5NyAyNy40MTQ5ODEtMC4wMDgyMzYgMjcuNDg0NjUyLTQuNzMzMzA4IDQ2LjczMjMwMi0yOS45MzQxNTQgNjIuNDgyODNsMi40NjUxNzcgMTguNTgwOTQ0YzUuMjQ1MzUxIDEuNTkyODg5IDEwLjY2NzMwNSAzLjM0MDY3MSAxNi4xNzAzNTQgNS4yNTcyMiAwLjc2ODUzNSAzLjIwNjE4MyAxLjY1NjQ5MiA3LjQxMTMwNiAyLjI1Mzc0OCAxMS44ODE3MzkgMC42MjU2NyA0LjY4MzQ1NCAwLjg3MTc0OSA5LjU1NjMzMyAwLjQ4NjAxMSAxMy4yMTUxMzktMC4zODU3MzggMy42NTg4MDYtMS41MjE3MTYgNS42MzM5NzItMS43MjExNzQgNS44MzM0NTktMTIuODA4OTg0IDEyLjgwODk1NS0zNS41NDYwMzYgMjAuMjc5MTM5LTU4LjYwODQzOCAyMC4yNzkxMzktMjMuMDYyMzkzIDAtNDUuNzk5NDQ0LTcuNDcwMTg0LTU4LjYwODQyMy0yMC4yNzkxMzktMC4xOTk0NjEtMC4xOTk0ODctMS4zMzU0NTYtMi4xNzQ2NTMtMS43MjExOTQtNS44MzM0NTktMC4zODU3MzUtMy42NTg4MDYtMC4xMzk2NjItOC41MzE2ODUgMC40ODYwMjYtMTMuMjE1MTM5IDAuNjAwNTI3LTQuNDk1MTE1IDEuNDk1NTUyLTguNzI1MDI4IDIuMjY2OTYzLTExLjkzNzQ2NyA1LjQ0ODA4Ni0xLjg5NDYwNiAxMC44MTU1NDctMy42MjQwNDIgMTYuMDEwMDczLTUuMjAxNDkxbDEuNDY5NTYxLTE5LjkwOTc1NmMtMS4xOTY1NzEtMS41MzQ1NjQtMi40MTU3ODItMi41NTExNjctMy44NzA5NTktMy42NDI4ODQtNS42MjQzNzctNC4yMTk1NjUtMTIuNDQ1MTQyLTEzLjUwMjI0NS0xNy4yNjMwNDktMjQuMzUwNjEzLTQuODE2MTg5LTEwLjg0NDQ5OS03LjgwMDAzOC0yMy4yNDAwMjMtNy44MDQ1MzYtMzMuMTYyODE4IDAuMDI5OTI2LTExLjg5NjM4MiAzLjIzMTM3OS0yMy4yOTkzMDQgOS4xMTE1MTYtMzMuMTQ5MDMyIDEuMDUyMTE1LTAuMzkxNjE4IDIuMTYxNTY2LTAuODA1NTE3IDMuNDA4NDg4LTEuMjE1NjM0IDQuMzg1MDE3LTEuNDQyMjUgMTAuMzkzOTczLTIuODE4ODg5IDIwLjgzODcxNi0yLjc5MjYyOHpNMjU1LjYzMDc4IDQyNS42MzgxMzZjLTAuMDE4NTAyIDAuMTM0OTYxLTAuMDM5MzUzIDAuMjY2NjgxLTAuMDU3NDQ5IDAuNDAyMTQ4LTAuNzYwOTE0IDUuNjk1NjM2LTEuMjA4MDMxIDExLjg5NTI3My0wLjU1MzgxNyAxOC4xMDA2NzUgMC42NTQyMTQgNi4yMDU0MDIgMi4yOTE1NjggMTIuODg2NDMyIDcuNjM4NTA3IDE4LjIzMzM1IDE4LjI1MDg2MSAxOC4yNTA4ODEgNDUuODcxNzU5IDI2LjMxMDIzMyA3My4xNjczMTggMjYuMzEwMjMzIDI3LjI5NTU1MSAwIDU0LjkxNjQ1Mi04LjA1OTM1MSA3My4xNjczMDQtMjYuMzEwMjMzIDUuMzQ2OTQ4LTUuMzQ2OTE4IDYuOTg0MzItMTIuMDI3OTQ4IDcuNjM4NTIyLTE4LjIzMzM1IDAuNjU0MjAyLTYuMjA1NDMxIDAuMjA3MTA2LTEyLjQwNTAzOS0wLjU1MzgxMS0xOC4xMDA2NzUtMC4wMTUwMDEtMC4xMTIyNDgtMC4wMzI0MTQtMC4yMjEzMDctMC4wNDc2OC0wLjMzMzIxIDI3Ljc0NzUzIDEyLjE2ODM2IDU0LjU2Nzc0NiAyOS41OTUyNjEgNjkuMzY3MDE1IDU1LjYxNDczNGwwIDExMC41NDkyMjgtNDkuMjY4ODMyIDAgMC03Ny45NDc3MDQtMjAuNTg5OTYgMCAwIDc3Ljk0NzcwNC0xNjAuMDEzNCAwIDAtNzcuOTQ3NzA0LTIwLjU4OTk2IDAgMCA3Ny45NDc3MDQtNDguODI3NjE4IDAgMC0xMTAuNTQ5MjI4YzE0LjgyNzE1Ni0yNi4wNjg1MzYgNDEuNzIwNTQ3LTQzLjUxMjIzMiA2OS41MjM4Ni01NS42ODM2NzJ6TTIxOS45ODEgMTA3LjUxOTU3NWMtMTA5LjkzNDgyNCAwLTE5OS41MDEgODkuMTg4MzQ1LTE5OS41MDEgMTk4LjkxMTk5OWwwIDQxMS4xMzU5ODljMCAxMDkuNzIzNjU5IDg5LjU2NjE3NiAxOTguOTEyMDExIDE5OS41MDEgMTk4LjkxMjAxMWw1ODQuMDM3OTg5IDBjMTA5LjkzNDg1MyAwIDE5OS41MDEwMDEtODkuMTg4MzUxIDE5OS41MDEwMDEtMTk4LjkxMjAxMWwwLTQxMS4xMzU5ODljMC0xMDkuNzIzNjUzLTg5LjU2NjE0OC0xOTguOTExOTk5LTE5OS41MDEwMDEtMTk4LjkxMTk5OWwtNTg0LjAzNzk4OSAwem0wIDYxLjQ0MDAwMWw1ODQuMDM3OTg5IDBjNzcuMDc0OTU1IDAgMTM4LjA2MTAwMyA2MC44Mzg5MTUgMTM4LjA2MTAwMyAxMzcuNDcxOTk4bDAgNDExLjEzNTk4OWMwIDc2LjYzMzA5NC02MC45ODYwNDggMTM3LjQ3MjAxMi0xMzguMDYxMDAzIDEzNy40NzIwMTJsLTU4NC4wMzc5ODkgMGMtNzcuMDc0OTYgMC0xMzguMDYxLTYwLjgzODkxOC0xMzguMDYxLTEzNy40NzIwMTJsMC00MTEuMTM1OTg5YzAtNzYuNjMzMDgyIDYwLjk4NjA0LTEzNy40NzE5OTggMTM4LjA2MS0xMzcuNDcxOTk4eiIgcC1pZD0iODMyMCI+PC9wYXRoPjwvc3ZnPg==',
      className: 'important-node',
      properties: {collaborativeWay: '1'},
    },
    {
      type: 'serial',
      text: '',
      label: '互斥网关',
      properties: {},
      icon: 'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB0PSIxNzQ4MTc1NTgyNzMwIiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjkzNjciIHdpZHRoPSIzNiIgaGVpZ2h0PSIzNiI+PHBhdGggZD0iTTAgMGgxMDI0djEwMjRIMHoiIGZpbGw9IiNGRkZGRkYiIGZpbGwtb3BhY2l0eT0iMCIgcC1pZD0iOTM2OCI+PC9wYXRoPjxwYXRoIGQ9Ik00NjUuMjI3Mjk0IDE0Mi4yNDU2NDdMMTQyLjI0NTY0NyA0NjUuMjI3Mjk0YTYxLjUwMDIzNSA2MS41MDAyMzUgMCAwIDAgMCA4Ni45Nzk3NjVsMzIyLjk4MTY0NyAzMjIuOTgxNjQ3YzI0LjAzMzg4MiAyNC4wMzM4ODIgNjIuOTQ1ODgyIDI0LjAzMzg4MiA4Ni45Nzk3NjUgMGwzMjIuOTgxNjQ3LTMyMi45ODE2NDdhNjEuNTAwMjM1IDYxLjUwMDIzNSAwIDAgMCAwLTg2Ljk3OTc2NUw1NTIuMjA3MDU5IDE0Mi4yNDU2NDdhNjEuNTAwMjM1IDYxLjUwMDIzNSAwIDAgMC04Ni45Nzk3NjUgMHogbTQ5LjY5NDExOCAzNy4yNTU1MjlsMzIzLjAxMTc2NCAzMjMuMDExNzY1YTguNzk0MzUzIDguNzk0MzUzIDAgMCAxIDAgMTIuNDA4NDcxTDUxNC45MjE0MTIgODM3LjkzMzE3NmE4Ljc5NDM1MyA4Ljc5NDM1MyAwIDAgMS0xMi40MDg0NzEgMEwxNzkuNTAxMTc2IDUxNC45MjE0MTJhOC43OTQzNTMgOC43OTQzNTMgMCAwIDEgMC0xMi40MDg0NzFMNTAyLjUxMjk0MSAxNzkuNTAxMTc2YTguNzk0MzUzIDguNzk0MzUzIDAgMCAxIDEyLjQwODQ3MSAweiIgZmlsbD0iIzMzMzMzMyIgcC1pZD0iOTM2OSI+PC9wYXRoPjxwYXRoIGQ9Ik01OTIuNTM0NTg4IDM4NS4wODQyMzVhMjYuMzUyOTQxIDI2LjM1Mjk0MSAwIDAgMSAzOS41NzQ1ODggMzQuNjM1Mjk0bC0yLjM3OTI5NCAyLjcxMDU4OS0yMTAuODIzNTI5IDIxMC4xMzA4MjNhMjYuMzUyOTQxIDI2LjM1Mjk0MSAwIDAgMS0zOS41NDQ0NzEtMzQuNjM1Mjk0bDIuMzQ5MTc3LTIuNzEwNTg4IDIxMC44MjM1MjktMjEwLjEzMDgyNHoiIGZpbGw9IiMzMzMzMzMiIHAtaWQ9IjkzNzAiPjwvcGF0aD48cGF0aCBkPSJNMzgxLjg2MTY0NyAzODQuOTMzNjQ3YTI2LjM1Mjk0MSAyNi4zNTI5NDEgMCAwIDEgMzQuNTQ0OTQxLTIuMzQ5MTc2bDIuNzEwNTg4IDIuMzQ5MTc2IDIxMC40OTIyMzYgMjEwLjUyMjM1M2EyNi4zNTI5NDEgMjYuMzUyOTQxIDAgMCAxLTM0LjU3NTA1OSAzOS42MDQ3MDZsLTIuNzEwNTg4LTIuMzQ5MTc3LTIxMC40NjIxMTgtMjEwLjUyMjM1M2EyNi4zNTI5NDEgMjYuMzUyOTQxIDAgMCAxIDAtMzcuMjU1NTI5eiIgZmlsbD0iIzMzMzMzMyIgcC1pZD0iOTM3MSI+PC9wYXRoPjwvc3ZnPg==',
    },
    {
      type: 'parallel',
      text: '',
      label: '并行网关',
      properties: {},
      icon: 'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB0PSIxNzQ4MTc1NjIwMDAyIiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjEwNDQxIiB3aWR0aD0iMzYiIGhlaWdodD0iMzYiPjxwYXRoIGQ9Ik0wIDBoMTAyNHYxMDI0SDB6IiBmaWxsPSIjRkZGRkZGIiBmaWxsLW9wYWNpdHk9IjAiIHAtaWQ9IjEwNDQyIj48L3BhdGg+PHBhdGggZD0iTTQ2NS4yMjcyOTQgMTQyLjI0NTY0N0wxNDIuMjQ1NjQ3IDQ2NS4yMjcyOTRhNjEuNTAwMjM1IDYxLjUwMDIzNSAwIDAgMCAwIDg2Ljk3OTc2NWwzMjIuOTgxNjQ3IDMyMi45ODE2NDdjMjQuMDMzODgyIDI0LjAzMzg4MiA2Mi45NDU4ODIgMjQuMDMzODgyIDg2Ljk3OTc2NSAwbDMyMi45ODE2NDctMzIyLjk4MTY0N2E2MS41MDAyMzUgNjEuNTAwMjM1IDAgMCAwIDAtODYuOTc5NzY1TDU1Mi4yMDcwNTkgMTQyLjI0NTY0N2E2MS41MDAyMzUgNjEuNTAwMjM1IDAgMCAwLTg2Ljk3OTc2NSAweiBtNDkuNjk0MTE4IDM3LjI1NTUyOWwzMjMuMDExNzY0IDMyMy4wMTE3NjVhOC43OTQzNTMgOC43OTQzNTMgMCAwIDEgMCAxMi40MDg0NzFMNTE0LjkyMTQxMiA4MzcuOTMzMTc2YTguNzk0MzUzIDguNzk0MzUzIDAgMCAxLTEyLjQwODQ3MSAwTDE3OS41MDExNzYgNTE0LjkyMTQxMmE4Ljc5NDM1MyA4Ljc5NDM1MyAwIDAgMSAwLTEyLjQwODQ3MUw1MDIuNTEyOTQxIDE3OS41MDExNzZhOC43OTQzNTMgOC43OTQzNTMgMCAwIDEgMTIuNDA4NDcxIDB6IiBmaWxsPSIjMzMzMzMzIiBwLWlkPSIxMDQ0MyI+PC9wYXRoPjxwYXRoIGQ9Ik01MDUuNzM1NTI5IDMzMy42NDMyOTRjMTMuNDMyNDcxIDAgMjQuNTE1NzY1IDEwLjA1OTI5NCAyNi4xNDIxMTggMjMuMDRsMC4yMTA4MjQgMy4zMTI5NDF2Mjk3LjY1MjcwNmEyNi4zNTI5NDEgMjYuMzUyOTQxIDAgMCAxLTUyLjQ5NTA1OSAzLjMxMjk0MWwtMC4yMTA4MjQtMy4zMTI5NDF2LTI5Ny42NTI3MDZjMC0xNC41NzY5NDEgMTEuNzc2LTI2LjM1Mjk0MSAyNi4zNTI5NDEtMjYuMzUyOTQxeiIgZmlsbD0iIzMzMzMzMyIgcC1pZD0iMTA0NDQiPjwvcGF0aD48cGF0aCBkPSJNNjU0LjU3Njk0MSA0ODIuNDg0NzA2YTI2LjM1Mjk0MSAyNi4zNTI5NDEgMCAwIDEgMy4zMTI5NDEgNTIuNDk1MDU5bC0zLjMxMjk0MSAwLjIxMDgyM0gzNTYuODk0MTE4YTI2LjM1Mjk0MSAyNi4zNTI5NDEgMCAwIDEtMy4zMTI5NDItNTIuNTI1MTc2bDMuMzEyOTQyLTAuMTgwNzA2aDI5Ny42ODI4MjN6IiBmaWxsPSIjMzMzMzMzIiBwLWlkPSIxMDQ0NSI+PC9wYXRoPjwvc3ZnPg==',
    },
    {
      type: 'end',
      text: '结束',
      label: '结束节点',
      icon: 'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB0PSIxNzQ4MTc1ODQ2ODU1IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjE5NDMyIiB3aWR0aD0iMzYiIGhlaWdodD0iMzYiPjxwYXRoIGQ9Ik01MTIgMGE1MTIgNTEyIDAgMSAxIDAgMTAyNEE1MTIgNTEyIDAgMCAxIDUxMiAweiBtMCA2NGE0NDggNDQ4IDAgMSAwIDAgODk2QTQ0OCA0NDggMCAwIDAgNTEyIDY0eiIgZmlsbD0iIzAwMDAwMCIgcC1pZD0iMTk0MzMiPjwvcGF0aD48cGF0aCBkPSJNNTEyIDEyOGEzODQgMzg0IDAgMSAxIDAgNzY4QTM4NCAzODQgMCAwIDEgNTEyIDEyOHogbTAgNjRhMzIwIDMyMCAwIDEgMCAwIDY0MEEzMjAgMzIwIDAgMCAwIDUxMiAxOTJ6IiBmaWxsPSIjMDAwMDAwIiBwLWlkPSIxOTQzNCI+PC9wYXRoPjwvc3ZnPg==',
    },
  ]);
}
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
          gatewayAddNode(lf.value, node, "between");
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
  LogicFlow.use(DndPanel);
  LogicFlow.use(SelectionSelect);
  LogicFlow.use(Menu);
  LogicFlow.use(Snapshot);
  LogicFlow.use(InsertNodeInPolyline);
}
function initEvent() {
  const { eventCenter } = lf.value.graphModel
  eventCenter.on('node:dbclick', (args) => {
    nodeClick.value = args.data
    let graphData = lf.value.getGraphData()
    nodes.value = graphData['nodes']
    skips.value = graphData['edges']
    proxy.$nextTick(() => {
      propertySettingRef.value.show()
    })
  })

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
