<template>
  <component v-bind:is="component"></component>
</template>


<script setup>
import Design from './views/flow-design/index.vue';
import Form from './views/form-design/index.vue';
import FormCreate from './views/form-design/formCreate.vue';
import useAppStore from "@/store/app";
const appStore = useAppStore();
const appParams = computed(() => useAppStore().appParams);
const component = ref(null);
onMounted(async () => {
  if (!appParams.value) await appStore.fetchTokenName();
  let pathObj = {
    form: Form,
    formCreate: FormCreate
  };
  component.value = pathObj[appParams.value.type] || Design;
});
</script>
