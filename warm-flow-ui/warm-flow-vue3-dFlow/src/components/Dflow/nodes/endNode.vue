<!-- 一个节点  包含 面板 + 连线 -->
<template>
    <div class="flow-end-node">
        <svg t="1719541267771" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="7706" width="16" height="16"><path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64z m271.5 719.5c-35.3 35.3-76.4 63-122.1 82.3-47.3 20-97.6 30.2-149.4 30.2-51.9 0-102.2-10.1-149.5-30.2-45.7-19.3-86.8-47-122.1-82.3s-63-76.4-82.3-122.1c-20-47.3-30.1-97.6-30.1-149.4 0-51.9 10.1-102.2 30.1-149.5 19.3-45.7 47-86.8 82.3-122.1s76.4-63 122.1-82.3c47.3-20 97.6-30.1 149.5-30.1 51.9 0 102.1 10.1 149.4 30.1 45.7 19.3 86.8 47 122.1 82.3s63 76.4 82.3 122.1c20 47.3 30.2 97.6 30.2 149.5 0 51.9-10.1 102.1-30.2 149.4-19.3 45.7-47 86.8-82.3 122.1z" fill="#727272" p-id="7707"></path><path d="M384 384h256v256H384z" fill="#727272" p-id="7708"></path></svg>
        <span>结束</span>
    </div>
</template>

<script setup>
import Panel from './panel.vue'
import Line from  './line.vue'
import {defineProps} from  'vue'
defineProps({
    bodyClass:{
        type: String,
        default: ''
    }
})

</script>

<style  scoped>
   .flow-end-node{
        display: grid;
        justify-content: center;
        justify-items: center;
        align-content: baseline;
        margin-bottom: 25px;
   }
</style>