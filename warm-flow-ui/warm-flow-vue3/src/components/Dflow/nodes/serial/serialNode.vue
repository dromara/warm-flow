<!-- 分支节点 -->
<template>
        <div class="flow-serial-node-wrap">
            <Node :panelClass="warnClass" v-bind="$attrs" defaultValue="请设置条件" :backgroundImage="backgroundImage" >
            
            </Node>
         
        </div>
</template>

<script setup>
import {ref, provide,defineProps,  toRefs, watch} from 'vue'
    import Node from '../node.vue'
    const props =  defineProps({
       
      
    })

    const  backgroundImage = ref('linear-gradient(to right, #67B26F 0%, #4ca2cd  51%, #67B26F  100%)') 
   



    provide('body', () => this.slotContent);
</script>

<style  scoped>
   .flow-warn {
        border: 2px solid #f25643;
    }
    .flow-status-completed{
        border: 2px solid #67B26F;
    }

    .flow-serial-node-wrap{
        display: flex;
        overflow: visible;
        /* min-height: 180px; */
        height: auto;
        position: relative;
        padding-top: 15px;
        justify-content: center;
        background-color: var(--global-flow-background-color);
        min-width: 0;
    }
    .flow-serial-node-wrap::before{
        content: '';
        position: absolute;
        top: 0px;
        left: 50%;
        height: 14px;
        transform: translate(-50%, 0px);
        width: 1px;
        background-color: var(--global-flow-line-color);
        /* transform: translate(-50%, 0px); */
    }
 

    .flow-serial-node-last{
        text-align: center;
    }
 
</style>