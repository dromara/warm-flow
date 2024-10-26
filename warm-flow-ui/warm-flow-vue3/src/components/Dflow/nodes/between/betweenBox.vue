<!-- 一个节点  包含 面板 + 连线 -->
<template>
    <div class="flow-serial-node-wrap">
            <userNode  v-bind="$attrs" :backgroundImage="backgroundImage"  >
            </userNode>   
            
    </div>
</template>

<script setup>

import {defineProps, ref, toRefs, watch} from  'vue'
import userNode from './userNode.vue';



const backgroundImage = ref("linear-gradient(to right, #7474BF 0%, #348AC7  51%, #7474BF  100%)")
</script>

<style  scoped>
    
   .flow-single-node{
        padding-left: var(--golbal-flow-node-padding);
        padding-right: var(--golbal-flow-node-padding);
        position: relative;
        justify-content: center;
        margin-top: 15px;
        display: grid;
        z-index: 1;
        background-color: var(--global-flow-background-color);

   }
   .flow-single-node::before{
        content: "";
        position: absolute;
        top: -16px;
        left: 50%;
        -webkit-transform: translateX(-50%);
        transform: translate(-50%);
        width: 0;
        height: 4px;
        border-style: solid;
        border-width: 12px 6px 0px;
        border-color: var(--global-flow-line-color) transparent transparent;
        background: var(--global-flow-background-color)

   }
   .flow-serial-node-wrap{
        display: flex;
        overflow: visible;
        /* min-height: 180px; */
        height: auto;
        position: relative;
        justify-content: center;
        background-color: var(--global-flow-background-color);
        min-width: 0;
    }
 

    .flow-serial-node-last{
        text-align: center;
    }
</style>