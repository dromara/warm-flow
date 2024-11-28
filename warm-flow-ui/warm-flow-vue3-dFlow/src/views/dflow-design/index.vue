<template>
  <FlowIndex :flowData="flowData" :readonly="readonly"></FlowIndex>
</template>

<script setup>
import { ref } from 'vue'
import FlowIndex from '@/components/Dflow/FlowIndex.vue'
import { getXmlString } from "&/api/flow/definition.js";
import { uuid } from '@/utils/tool';
import { xml2LogicFlowJson } from "@/components/Dflow/tool";
import useAppStore from "@/store/app";
const appStore = useAppStore();
const appParams = computed(() => useAppStore().appParams);
const definitionId = ref(null);
const readonly = ref(false)
const flowData = ref(
  [
    {
      "text": {
        "value": "开始",
        "x": 100,
        "y": 200
      },
      "properties": {
        "nodeRatio": "0.000",
        "listenerType": "assignment",
        "listenerPath": "/2"
      },
      "type": "start",
      "id": "start",
      "x": 100,
      "y": 200
    },
    {
      "text": {
        "value": "中间节点-或签1",
        "x": 220,
        "y": 200
      },
      "properties": {
        "nodeRatio": "0.000",
        "listenerType": "start,create,assignment",
        "listenerPath": "/5@@/1@@/2"
      },
      "type": "between",
      "id": "submit",
      "x": 220,
      "y": 200
    },
    {
      "text": {},
      "properties": {
        "nodeRatio": "0.000"
      },
      "type": "parallel",
      "id": "e01a7001-e8e3-4ce6-8e75-21737541b818",
      "x": 600,
      "y": 200,
      childNodes: [
        [
          {
            "text": {
              "value": "中间节点-或签2",
              "x": 720,
              "y": 120
            },
            "properties": {
              "nodeRatio": "0.000"
            },
            "type": "between",
            "id": "approval",
            "x": 720,
            "y": 120
          }
        ],
        [
          {
            "text": {
              "value": "中间节点-会签",
              "x": 900,
              "y": 200
            },
            "properties": {
              "nodeRatio": "0.000"
            },
            "type": "between",
            "id": "24ac4505-08e2-4c98-a1ed-ff4d18eee6fa",
            "x": 900,
            "y": 200
          },
        ]
      ]
    },
    {
      "text": {},
      "properties": {
        "nodeRatio": "0.000"
      },
      "type": "serial",
      "id": "264f4ccf-4cc2-420f-8908-e18c073deedc",
      "x": 340,
      "y": 200,
      childNodes: [
        [
          {
            "text": {
              "value": "通过",
              "x": 340,
              "y": 147
            },
            "properties": {
              "skipCondition": "@@like@@|aa@@like@@a",
              "skipName": "通过",
              "skipType": "PASS"
            },
            // "id": null,
            id: "skip-1",
            "type": "skip",
            "sourceNodeId": "264f4ccf-4cc2-420f-8908-e18c073deedc",
            "targetNodeId": "d61533d8-ebe3-4c3d-b06b-5350a7dc6bec",
            "pointsList": [
              {
                "x": 340,
                "y": 175
              },
              {
                "x": 340,
                "y": 119
              },
              {
                "x": 410,
                "y": 119
              }
            ],
            "startPoint": {
              "x": 340,
              "y": 175
            },
            "endPoint": {
              "x": 410,
              "y": 119
            }
          }
        ],
        [
          {
            "text": {
              "value": "不通过",
              "x": 340,
              "y": 262
            },
            "properties": {
              "skipCondition": "@@gt@@|bb@@gt@@b",
              "skipName": "不通过",
              "skipType": "PASS"
            },
            // "id": null,
            id: "skip-2",
            "type": "skip",
            "sourceNodeId": "264f4ccf-4cc2-420f-8908-e18c073deedc",
            "targetNodeId": "598ad0cd-8e4d-4355-8011-5bb067972b2d",
            "pointsList": [
              {
                "x": 340,
                "y": 225
              },
              {
                "x": 340,
                "y": 300
              },
              {
                "x": 410,
                "y": 300
              }
            ],
            "startPoint": {
              "x": 340,
              "y": 225
            },
            "endPoint": {
              "x": 410,
              "y": 300
            }
          },
        ]
      ]
    },
    {
      "text": {
        "value": "结束",
        "x": 1040,
        "y": 200
      },
      "properties": {
        "nodeRatio": "0.000"
      },
      "type": "end",
      "id": "end",
      "x": 1040,
      "y": 200
    },
  ]
)
onMounted(async () => {
  if (!appParams.value) await appStore.fetchTokenName();
  definitionId.value = appParams.value.id;
  if (appParams.value.disabled === 'true') {
    disabled.value = true
  }
  if (definitionId.value) {
    getXmlString(definitionId.value).then(res => {
      if (res.data) {
        let nodesData = xml2LogicFlowJson(res.data)
        console.log(JSON.stringify(nodesData));
        // flowData.value = nodesData;
        // nodesData.map(e => {
        //     flowData.value.push(e);
        //     if (["serial", "parallel"].includes(e.type)) {

        //     }
        // });
      }
    });
  }
})
</script>

<style lang="scss" scoped></style>
