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
  let formPath = appParams.value.type === "form" ? "form" : "flow";
  import(/* @vite-ignore */`./views/${formPath}-design/index.vue`).then((module) => {
    component.value = module.default;
  });
});
</script>
