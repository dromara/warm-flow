import { ref } from 'vue'
import type { Ref } from 'vue'

/**
 * 设计器全局 UI 选项（与具体 UI 库解耦的「风格」配置中心）。
 *
 * 目前承载「组件尺寸」全局设置：消费方一次性指定 small / default / large，
 * 设计器内所有中性组件 wf-*（按当前 UI 适配器渲染为 EP / antd 组件）默认即采用该尺寸，
 * 局部仍可用组件自身的 size 覆盖。后续可在此扩展更多全局风格 / 行为开关。
 *
 * @author warm
 */

/** 组件尺寸：对齐 Element Plus 取值（large / default / small），antd 适配器内部按需换算。 */
export type ComponentSize = 'large' | 'default' | 'small'

// 用 ref 持有，便于中性组件在 render 中响应式读取：运行期改尺寸，已挂载组件自动重渲染。
const componentSize: Ref<ComponentSize> = ref<ComponentSize>('default')

/**
 * 设置设计器全局组件尺寸。须在渲染设计器前（或运行期任意时刻）调用。
 * 也可通过 `app.use(WarmFlowDesigner, { size: 'small' })` 在安装时指定。
 */
export function setComponentSize(size: ComponentSize): void {
  componentSize.value = size
}

/** 获取当前设计器全局组件尺寸。 */
export function getComponentSize(): ComponentSize {
  return componentSize.value
}

/** 返回尺寸的响应式引用，供中性组件在 render 中读取以保持响应式。 */
export function useComponentSize(): Ref<ComponentSize> {
  return componentSize
}
