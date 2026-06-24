import type { App, Component } from 'vue'
import { createNeutralComponent } from './createNeutralComponent'

/**
 * 中性组件语义名清单（对应 `el-*` 后缀）。注册名为 `wf-<语义名>`，
 * 设计器迁移即把 `<el-x>` 改为 `<wf-x>`；底层由当前 UI 适配器的 components 映射决定。
 */
const NEUTRAL_KEYS = [
  'button', 'input', 'input-number', 'select', 'option', 'form', 'form-item',
  'table', 'table-column', 'col', 'row', 'header', 'checkbox', 'checkbox-group',
  'tree-select', 'tree', 'tag', 'radio', 'radio-group', 'dialog', 'drawer',
  'date-picker', 'time-picker', 'switch', 'pagination', 'divider', 'tooltip', 'icon'
] as const

function toPascalCase(kebab: string): string {
  return kebab
    .split('-')
    .map((s) => s.charAt(0).toUpperCase() + s.slice(1))
    .join('')
}

/** 注册名（wf-xxx）-> 中性透传组件 */
export const wfComponents: Record<string, Component> = NEUTRAL_KEYS.reduce((acc, key) => {
  acc[`wf-${key}`] = createNeutralComponent(`Wf${toPascalCase(key)}`, key)
  return acc
}, {} as Record<string, Component>)

/**
 * 全局注册全部 Wf* 中性组件（迁移期与 el-* 并存，互不影响）。
 * 由 main.js（应用）与 designer/index.ts 的 install（库）调用。
 */
export function registerWfComponents(app: App): void {
  Object.keys(wfComponents).forEach((name) => {
    if (!app.component(name)) {
      app.component(name, wfComponents[name])
    }
  })
}
