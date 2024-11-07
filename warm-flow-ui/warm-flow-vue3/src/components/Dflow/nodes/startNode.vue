<!-- 一个节点  包含 面板 + 连线 -->
<template>
    <div class="flow-start-node">
       
        <Node v-bind="$attrs"  :backgroundImage="backgroundImage" :enableDestory="false" > 
               
        </Node>
    </div>
</template>

<script setup>
import {defineProps,ref, useAttrs} from  'vue'
import Node from './node.vue';
const attrs = useAttrs();
defineProps({
    bodyClass:{
        type: String,
        default: ''
    },
   
})

const backgroundImage = ref("linear-gradient(to right, #606c88 0%, #3f4c6b  51%, #606c88  100%)")
</script>

<style  scoped>
   .flow-start-node{
        margin-top: 35px;
        justify-content: center;
        display: grid;
   }
</style>