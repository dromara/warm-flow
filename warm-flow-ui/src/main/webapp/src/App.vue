<template>
  <router-view v-slot="{ Component, route }">
    <transition name="fade-transform" mode="out-in">
      <keep-alive :include="['Design']">
        <component v-if="!route.meta.link" :is="Component" :key="route.path"/>
      </keep-alive>
    </transition>
  </router-view>
</template>

<script setup>
</script>
