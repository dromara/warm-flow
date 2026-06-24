<template>
  <Icon :icon="iconName" :class="svgClass" :style="color ? { color } : undefined" aria-hidden="true" />
</template>

<script>
import { Icon } from '@iconify/vue'

// 说明：组件名与 icon-class 接口保持不变（向后兼容历史用法），内部改用 iconify 渲染。
// 无前缀的 icon-class 默认归到 Warm-Flow 自定义集（wf:），也支持传入完整 iconify 名（如 ep:zoom-out）。
export default defineComponent({
  components: { Icon },
  props: {
    iconClass: {
      type: String,
      required: true
    },
    className: {
      type: String,
      default: ''
    },
    color: {
      type: String,
      default: ''
    }
  },
  setup(props) {
    return {
      iconName: computed(() => (props.iconClass.includes(':') ? props.iconClass : `wf:${props.iconClass}`)),
      svgClass: computed(() => {
        if (props.className) {
          return `svg-icon ${props.className}`
        }
        return 'svg-icon'
      })
    }
  }
})
</script>

<style scope lang="scss">
.sub-el-icon,
.nav-icon {
  display: inline-block;
  font-size: 15px;
  margin-right: 12px;
  position: relative;
}

.svg-icon {
  width: 1em;
  height: 1em;
  position: relative;
  fill: currentColor;
  vertical-align: -2px;
}
</style>
