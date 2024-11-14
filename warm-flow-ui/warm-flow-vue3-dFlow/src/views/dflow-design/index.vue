<template>
        <FlowIndex :flowData="flowData" :readonly="readonly" ></FlowIndex>

</template>

<script setup>
    import { ref } from 'vue'
    import FlowIndex from '@/components/Dflow/FlowIndex.vue'
    import { uuid } from '@/utils/tool';
    const readonly = ref(false)
const flowData = ref(
    [
                    {
                    nodeId: uuid(),
                    nodeType: "start",
                    type: 'start',
                    nodeName:"开始节点",
                    properties:{},
                    value: "所有人",
                    },
                    {

                        nodeId: uuid(),
                        nodeType: 'between',
                        type: 'between',
                        nodeName:"审批人",
                        properties:{},
                        value: ''
                    },
                    {
                        nodeId: uuid(),
                        nodeType: 'parallel',
                        nodeName:"并行分支",
                        properties:{},
                        childNodes:[
                            [
                                {
                                    nodeId: uuid(),
                                    nodeName:'审批人1',
                                    nodeType: 'between',
                                    type: 'between',
                                    properties:{},
                                    value: '',
                                },

                            ],
                            [
                                {
                                    nodeId: uuid(),
                                    nodeName:'审批人2',
                                    nodeType: 'between',
                                    type: 'between',
                                    properties:{},
                                    value: '',
                                },

                            ]
                        ]

                    },
                    {
                        nodeId: uuid(),
                        nodeType: 'serial',
                        nodeName:"分支选择",
                        childNodes:[
                              [
                                {
                                  nodeId: uuid(),
                                  nodeType: 'serial-node',
                                  type: 'serial-node',
                                  nodeName:"条件",
                                  sortNum: 0,
                                  value: '',
                                  properties:{},
                                },

                              ],
                              [
                                {
                                  nodeId:  uuid(),
                                  nodeType: 'serial-node',
                                  type: 'serial-node',
                                  nodeName:"条件",
                                  value: '其他条件走此流程',
                                  sortNum: 1,
                                  default: true,
                                  properties:{},
                                },

                              ]
                        ]

                },

                    {
                        nodeId: uuid(),
                        type: 'end',
                        nodeName:"结束",
                        nodeType: "end",
                        properties:{},
                    }
                ]
)



</script>

<style lang="scss" scoped>

</style>
