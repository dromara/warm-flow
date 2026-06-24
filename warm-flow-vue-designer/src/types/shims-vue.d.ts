/**
 * 类型声明补充：让 TypeScript 识别 .vue 单文件组件与 Vite 虚拟模块。
 *
 * .vue 统一声明为通用组件（库出口层不强制深入 .vue 内部类型，避免巨型页面组件拖累类型生成）。
 *
 * @author warm
 */
declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<Record<string, unknown>, Record<string, unknown>, any>
  export default component
}