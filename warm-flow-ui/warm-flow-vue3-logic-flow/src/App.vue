<template>
  <component v-bind:is="component"></component>
</template>


<script setup>
import Design from './views/flow-design/index.vue';
import FlowChart from './views/flow-design/flowChart.vue';
import Form from './views/form-design/index.vue';
import FormCreate from './views/form-design/formCreate.vue';
import useAppStore from "@/store/app";
import mimicDing from "@/views/flow-design/mimicDing.vue";
const appStore = useAppStore();
const appParams = computed(() => appStore.appParams);
const component = ref(null);
onMounted(async () => {
  if (!appParams.value) await appStore.fetchTokenName();
  let pathObj = {
    mimicDing: mimicDing,
    form: Form,
    FlowChart: FlowChart,
    formCreate: FormCreate
  };
  component.value = pathObj[appParams.value.type] || Design;
});
</script>
