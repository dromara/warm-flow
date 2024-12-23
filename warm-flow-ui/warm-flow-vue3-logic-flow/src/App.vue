<template>
  <component v-bind:is="component"></component>
</template>


<script setup>
import useAppStore from "@/store/app";
const appStore = useAppStore();
const appParams = computed(() => useAppStore().appParams);
const component = ref("");
onMounted(async () => {
  if (!appParams.value) await appStore.fetchTokenName();
  let pathObj = {
    form: `./views/form-design/index.vue`,
    formCreate: `./views/form-design/formCreate.vue`
  };
  let path = pathObj[appParams.value.type] || `./views/flow-design/index.vue`;
  import(/* @vite-ignore */path).then((module) => {
    component.value = module.default;
  });
});
</script>
