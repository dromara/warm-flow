<!-- 分支选择盒子 （由分支选择组组成）-->  
<template>
    <!-- 最外层框 -->
    <div  class="flow-serial-box" :style="{'margin-top': $attrs.readonly? '0': '10px'}">
        <SerialGroup  v-bind="$attrs"></SerialGroup>
        <div class="flow-serial-box-footer">
            <Line  v-bind="$attrs"></Line>
        </div>
    </div>
</template>

<script setup>
    import { defineProps, toRefs, useAttrs} from 'vue';
    import Line from '../line.vue'
    import SerialGroup from './serialGroup.vue'

    const attrs = useAttrs();
   
</script>

<style  scoped>
    .flow-serial-box{
        width: 100%;
        /* margin-top: 10px; */
        display: flex;
        flex-direction: column;
        min-width: 0;
        flex-shrink: 0;
        /* align-content: center; */
        /* flex-wrap: nowrap; */
        /* align-items: center; */
        position: relative;
    }
    .flow-serial-box-wrap{
        display: flex;
        flex-direction: row;
        flex-wrap: nowrap;
        align-content: center;
        justify-content: center;
    }
    .flow-serial-box-footer{
        display: flex;
        width: 100%;
        flex-wrap: nowrap;
        justify-content: center;
        background-color: var(--global-flow-background-color);

    }
</style>