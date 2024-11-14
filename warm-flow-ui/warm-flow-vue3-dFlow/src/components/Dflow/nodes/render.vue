<template>
    <StartNode v-bind="$attrs" v-if="$attrs.nodeType === 'start'" ></StartNode>
    <BetweenBox v-bind="$attrs" v-else-if="$attrs.nodeType === 'between'" ></BetweenBox>
    <SerialBox v-bind="$attrs" v-else-if="$attrs.nodeType === 'serial'" ></SerialBox>
    <SerialNode v-bind="$attrs" v-else-if="$attrs.nodeType === 'serial-node'" ></SerialNode>
    <ParallelBox v-bind="$attrs" v-else-if="$attrs.nodeType === 'parallel'" ></ParallelBox>
    <EndFlow v-else-if="$attrs.nodeType === 'end'"></EndFlow>
    <div v-else>不存在的类别{{ $attrs.nodeType }}</div>
</template>

<script setup>
    import StartNode from './startNode.vue'
    import EndFlow from './endNode.vue'
    import SerialBox from './serial/serialBox.vue'
    import SerialNode from './serial/serialNode.vue';
    import BetweenBox from './between/betweenBox.vue';
    import ParallelBox from './parallel/parallelBox.vue'
</script>

<style lang="scss" scoped>

</style>