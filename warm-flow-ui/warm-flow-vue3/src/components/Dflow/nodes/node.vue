
<template>
    <div  class="flw-node-single">
        <Panel :class="panelClass + ' ' + statusClass" v-bind="$attrs" >
          
        </Panel>
      
        <Line v-bind="$attrs" ></Line>
    </div>
</template>

<script setup>
import Panel from './panel.vue'
import Line from  './line.vue'
import {defineProps,  watch, ref, useAttrs} from  'vue'
    const props =  defineProps({
        
        panelClass:{
             type: String,
            default: ''
        },
        //节点状态
        nodeStatus:{
            type: String,
        },
       
       
       
    })
    const statusClass = ref("");


    watch(()=>props.nodeStatus,(newv,oldv)=>{
        if(newv && newv.includes(2)){
            statusClass.value = "flow-status-completed";
        }
    },{immediate:true, deep:true})
</script>

<style  scoped>
    .flow-warn {
        border: 2px solid #f25643;
    }
    .flow-status-completed{
        border: 2px solid #67B26F;
    }
    .flw-node-single{
        padding-left: var(--golbal-flow-node-padding);
        padding-right: var(--golbal-flow-node-padding);
        z-index: 1;
    }
    
   
    .flw-node-left-top{
        position: absolute;
        width: 50%;
        height: 1px;
    }
</style>