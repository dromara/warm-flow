import { defineComponent, h, ref, Fragment, createApp } from 'vue'
import type { Component, Directive, VNode } from 'vue'
import {
  message, notification, Modal,
  Button as AButton, Input as AInput, Switch as ASwitch,
  RadioGroup as ARadioGroup, Radio as ARadio, Select as ASelect,
  Tag as ATag, Tooltip as ATooltip, Divider as ADivider, InputNumber as AInputNumber,
  Checkbox as ACheckbox, CheckboxGroup as ACheckboxGroup, Col as ACol, Row as ARow,
  TreeSelect as ATreeSelect, Form as AForm, FormItem as AFormItem, Table as ATable,
  Drawer as ADrawer, DatePicker as ADatePicker, TimePicker as ATimePicker,
  Tree as ATree, Pagination as APagination, Spin as ASpin
} from 'ant-design-vue'
import type { UiAdapter, UiFeedbackType, UiFeedbackOptions, UiLoadingHandle } from './uiAdapter'

// 全部组件已翻译为 antd，不再依赖 Element Plus（真 antd 单栈）

/**
 * Ant Design Vue 4 的 UI 适配器（demo-local 原型）。
 *
 * - 反馈：message / notification / Modal / message.loading + 自实现 clickOutside 指令。
 * - 组件：已翻译的 button / input 走 antd（把 el-* 风格 props/v-model 翻译为 antd），
 *   其余组件过渡期回退 Element Plus；逐组件翻译完成后移除对应 EP 回退。
 *
 * 验证通过后将提升为库子入口 @dromara/warm-flow-designer/antdv。
 *
 * @author warm
 */

function toContent(content: string | UiFeedbackOptions): string {
  if (typeof content === 'string') return content
  return (content.message ?? content.title ?? '') as string
}

// 轻量 clickOutside 指令（antd 无内置，自实现）
const clickOutside: Directive = {
  mounted(el: any, binding) {
    el.__wfClickOutside__ = (e: Event) => {
      if (el !== e.target && !el.contains(e.target as Node)) binding.value?.(e)
    }
    document.addEventListener('click', el.__wfClickOutside__, true)
  },
  unmounted(el: any) {
    document.removeEventListener('click', el.__wfClickOutside__, true)
    el.__wfClickOutside__ = null
  }
}

// el-button 风格 -> a-button 翻译
const AntButton: Component = defineComponent({
  name: 'WfAntButton',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const elType = a.type as string | undefined
      delete a.type
      delete a.link
      delete a.plain
      const props: Record<string, unknown> = { ...a }
      // 类型映射：el(primary/success/info/warning/danger/text) -> antd(type/danger)
      if ((attrs as any).link) props.type = 'link'
      else if (elType === 'primary') props.type = 'primary'
      else if (elType === 'text') props.type = 'text'
      else if (elType === 'danger') { props.danger = true }
      else if (elType === 'warning' || elType === 'success' || elType === 'info') props.type = 'default'
      else props.type = props.type || 'default'
      // 尺寸映射：el(large/default/small) -> antd(large/middle/small)
      const elSize = (attrs as any).size
      if (elSize === 'default') props.size = 'middle'
      return h(AButton, props, slots)
    }
  }
})

// el-input 风格 -> a-input 翻译（v-model -> value/onUpdate:value）
const AntInput: Component = defineComponent({
  name: 'WfAntInput',
  inheritAttrs: false,
  setup(_props, { attrs }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const modelValue = a.modelValue
      const onUpd = a['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
      const clearable = a.clearable
      delete a.modelValue
      delete a['onUpdate:modelValue']
      delete a.clearable
      // antd 的 maxlength 要求 Number，而 el-input 常传字符串
      if (a.maxlength != null) a.maxlength = Number(a.maxlength)
      return h(AInput, {
        ...a,
        value: modelValue,
        allowClear: clearable,
        'onUpdate:value': (v: unknown) => onUpd?.(v)
      })
    }
  }
})

// el-switch 风格 -> a-switch（v-model + active/inactive-value/text）
const AntSwitch: Component = defineComponent({
  name: 'WfAntSwitch',
  inheritAttrs: false,
  setup(_props, { attrs }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const modelValue = a.modelValue
      const onUpd = a['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
      const checkedValue = a['active-value'] ?? a.activeValue
      const unCheckedValue = a['inactive-value'] ?? a.inactiveValue
      const checkedChildren = a['active-text'] ?? a.activeText
      const unCheckedChildren = a['inactive-text'] ?? a.inactiveText
      ;['modelValue', 'onUpdate:modelValue', 'active-value', 'activeValue', 'inactive-value',
        'inactiveValue', 'active-text', 'activeText', 'inactive-text', 'inactiveText'].forEach((k) => delete a[k])
      if (a.size === 'large' || a.size === 'default') delete a.size // antd 仅 default/small
      return h(ASwitch, {
        ...a, checked: modelValue, checkedValue, unCheckedValue,
        checkedChildren, unCheckedChildren,
        'onUpdate:checked': (v: unknown) => onUpd?.(v)
      })
    }
  }
})

// el-radio-group 风格 -> a-radio-group（v-model:value）
const AntRadioGroup: Component = defineComponent({
  name: 'WfAntRadioGroup',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const modelValue = a.modelValue
      const onUpd = a['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
      delete a.modelValue
      delete a['onUpdate:modelValue']
      return h(ARadioGroup, { ...a, value: modelValue, 'onUpdate:value': (v: unknown) => onUpd?.(v) }, slots)
    }
  }
})

// el-radio 风格 -> a-radio（el 的 label 即值 -> antd 的 value）
const AntRadio: Component = defineComponent({
  name: 'WfAntRadio',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const value = a.label ?? a.value
      delete a.label
      return h(ARadio, { ...a, value }, slots)
    }
  }
})

// el-select 风格 -> a-select（v-model:value、clearable->allowClear、filterable/allow-create->showSearch/tags）
const AntSelect: Component = defineComponent({
  name: 'WfAntSelect',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const modelValue = a.modelValue
      const onUpd = a['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
      const clearable = a.clearable
      const filterable = a.filterable
      const allowCreate = a['allow-create'] ?? a.allowCreate
      ;['modelValue', 'onUpdate:modelValue', 'clearable', 'filterable', 'allow-create', 'allowCreate'].forEach((k) => delete a[k])
      // 收集 wf-option 子节点 -> antd options 属性（a-select 严格校验子组件类型，不能直接塞 WfOption）
      const optVNodes = collectByName(slots.default ? slots.default() : [], 'WfOption')
      const options = optVNodes.map((v) => {
        const p = (v.props || {}) as Record<string, unknown>
        return { label: p.label, value: p.value }
      })
      return h(ASelect, {
        ...a,
        value: modelValue,
        allowClear: clearable,
        showSearch: !!(filterable || allowCreate),
        mode: allowCreate ? 'tags' : undefined,
        options,
        style: { width: '100%', ...((a.style as Record<string, unknown>) || {}) },
        'onUpdate:value': (v: unknown) => onUpd?.(v)
      })
    }
  }
})

// el-option 占位：本身不渲染，由 AntSelect 收集其 label/value 为 options 属性
const AntOption: Component = defineComponent({
  name: 'WfAntOption',
  setup() {
    return () => null
  }
})

// 通用 v-model 翻译工厂：modelValue <-> valueKey，其余 attrs 透传、插槽转发，可选 transform 进一步映射
function vModelComp(
  name: string,
  antdComp: Component,
  valueKey: string,
  transform?: (props: Record<string, unknown>, raw: Record<string, unknown>) => Record<string, unknown>
): Component {
  return defineComponent({
    name,
    inheritAttrs: false,
    setup(_props, { attrs, slots }) {
      return () => {
        const a: Record<string, unknown> = { ...attrs }
        const mv = a.modelValue
        const onUpd = a['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
        delete a.modelValue
        delete a['onUpdate:modelValue']
        let props: Record<string, unknown> = { ...a, [valueKey]: mv, [`onUpdate:${valueKey}`]: (v: unknown) => onUpd?.(v) }
        if (transform) props = transform(props, a)
        return h(antdComp, props, slots)
      }
    }
  })
}

const AntInputNumber = vModelComp('WfAntInputNumber', AInputNumber, 'value')
const AntCheckboxGroup = vModelComp('WfAntCheckboxGroup', ACheckboxGroup, 'value')
const AntCheckbox = vModelComp('WfAntCheckbox', ACheckbox, 'checked', (props, raw) => {
  if (raw.label != null) { props.value = raw.label; delete props.label }
  return props
})
const AntTreeSelect = vModelComp('WfAntTreeSelect', ATreeSelect, 'value', (props, raw) => {
  if (raw.data != null) { props.treeData = raw.data; delete props.data }
  if (raw.props != null) { props.fieldNames = raw.props; delete props.props }
  if (raw['check-strictly'] != null) { props.treeCheckStrictly = raw['check-strictly']; delete props['check-strictly'] }
  delete props['value-key']
  // antd TreeSelect 不接受空串作为 value，空值统一为 undefined
  if (props.value === '' || props.value == null) props.value = undefined
  // 默认占满容器宽度（对齐 el-tree-select 观感）
  props.style = { width: '100%', ...((raw.style as Record<string, unknown>) || {}) }
  return props
})

// el-tag 风格 -> a-tag（type -> color）
const AntTag: Component = defineComponent({
  name: 'WfAntTag',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    const colorMap: Record<string, string> = { success: 'success', info: 'default', warning: 'warning', danger: 'error', primary: 'processing' }
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const t = a.type as string | undefined
      delete a.type
      return h(ATag, { ...a, color: t ? (colorMap[t] || t) : undefined }, slots)
    }
  }
})

// el-tooltip 风格 -> a-tooltip（content -> title，默认插槽为触发元素）
const AntTooltip: Component = defineComponent({
  name: 'WfAntTooltip',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const content = a.content
      delete a.content
      return h(ATooltip, { ...a, title: content ?? slots.content?.() }, { default: slots.default })
    }
  }
})

// el-form 风格 -> a-form：label-width -> labelCol；暴露 el 风格 validate(cb)/clearValidate 转发到 a-form
const AntForm: Component = defineComponent({
  name: 'WfAntForm',
  inheritAttrs: false,
  setup(_props, { attrs, slots, expose }) {
    const inner = ref<any>(null)
    expose({
      // el 的 validate(cb) -> a-form 的 validate()/validateFields() Promise
      validate(cb?: (valid: boolean) => void) {
        const inst = inner.value
        const p = inst?.validate ? inst.validate() : inst?.validateFields?.()
        if (!p || typeof p.then !== 'function') { cb?.(true); return Promise.resolve(true) }
        return p.then(() => { cb?.(true); return true }).catch((e: unknown) => {
          cb?.(false)
          // 回调式调用（el 风格）时不再 reject，避免未处理的 Promise 拒绝；Promise 式调用才 reject
          if (cb) return false
          return Promise.reject(e)
        })
      },
      validateField(...args: unknown[]) { return inner.value?.validateFields?.(...args) },
      clearValidate(fields?: unknown) { return inner.value?.clearValidate?.(fields) },
      resetFields(...args: unknown[]) { return inner.value?.resetFields?.(...args) },
      scrollToField(...args: unknown[]) { return inner.value?.scrollToField?.(...args) }
    })
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const labelWidth = a['label-width'] ?? a.labelWidth
      delete a['label-width']
      delete a.labelWidth
      const props: Record<string, unknown> = { ...a, ref: inner, layout: 'horizontal' }
      if (labelWidth != null) {
        const w = /^\d+$/.test(String(labelWidth)) ? `${labelWidth}px` : String(labelWidth)
        props.labelCol = { style: { width: w, textAlign: 'right' } }
      }
      return h(AForm, props, slots)
    }
  }
})

// el-form-item 风格 -> a-form-item：prop -> name（点路径转数组，支持表格内嵌套校验）
const AntFormItem: Component = defineComponent({
  name: 'WfAntFormItem',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const prop = a.prop
      delete a.prop
      let name: unknown = prop
      if (typeof prop === 'string' && prop.includes('.')) {
        name = prop.split('.').map((s) => (/^\d+$/.test(s) ? Number(s) : s))
      }
      return h(AFormItem, { ...a, name }, slots)
    }
  }
})

// 为每个行对象分配稳定 key（避免 antd rowKey 用 index 的弃用警告）
const rowKeyMap = new WeakMap<object, number>()
let rowKeySeq = 0
function rowKeyOf(record: unknown): string | number {
  if (record && typeof record === 'object') {
    let k = rowKeyMap.get(record as object)
    if (k == null) { k = ++rowKeySeq; rowKeyMap.set(record as object, k) }
    return k
  }
  return String(record)
}

// 从默认插槽 vnode 中按组件名收集子节点（含 v-if/v-for 产生的 Fragment）
function collectByName(nodes: VNode[] | undefined, compName: string): VNode[] {
  const out: VNode[] = []
  const walk = (arr: unknown) => {
    if (!Array.isArray(arr)) return
    for (const v of arr as VNode[]) {
      if (!v || typeof v !== 'object') continue
      if (v.type === Fragment) { walk(v.children as VNode[]); continue }
      const name = (v.type as { name?: string; __name?: string })?.name || (v.type as { __name?: string })?.__name || ''
      if (name === compName) out.push(v)
    }
  }
  walk(nodes)
  return out
}

// el-table（列子组件） -> a-table（columns 属性 + customRender 单元格）
const AntTable: Component = defineComponent({
  name: 'WfAntTable',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const data = a.data
      const emptyText = a['empty-text'] ?? a.emptyText
      ;['data', 'empty-text', 'emptyText'].forEach((k) => delete a[k])
      const colVNodes = collectByName(slots.default ? slots.default() : [], 'WfTableColumn')
      const columns = colVNodes.map((v, idx) => {
        const p = (v.props || {}) as Record<string, unknown>
        const col: Record<string, unknown> = {
          title: p.label,
          dataIndex: p.prop,
          key: p.prop ?? idx,
          width: p.width,
          align: p.align
        }
        const ch = v.children
        const defSlot = ch && typeof ch === 'object' && !Array.isArray(ch)
          ? (ch as Record<string, unknown>).default
          : null
        if (typeof defSlot === 'function') {
          col.customRender = (info: { record: unknown; index: number }) =>
            (defSlot as (s: unknown) => unknown)({ row: info.record, $index: info.index, column: col, ...info })
        }
        return col
      })
      return h(ATable, {
        ...a,
        // 浅拷贝：既追踪数组响应（迭代）又给 a-table 新引用，确保 push/删除后及时更新
        dataSource: Array.isArray(data) ? [...data] : [],
        columns,
        rowKey: rowKeyOf,
        pagination: false,
        size: 'small',
        locale: emptyText ? { emptyText } : undefined
      })
    }
  }
})

// table-column 本身不渲染（由 AntTable 收集为 columns 配置）
const AntTableColumn: Component = defineComponent({
  name: 'WfAntTableColumn',
  setup() {
    return () => null
  }
})

// el-dialog -> a-modal（v-model open；无 #footer 时不显示默认按钮，对齐 el）
const AntDialog: Component = defineComponent({
  name: 'WfAntDialog',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const mv = a.modelValue
      const onUpd = a['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
      ;['modelValue', 'onUpdate:modelValue', 'append-to-body', 'destroy-on-close', 'before-close'].forEach((k) => delete a[k])
      return h(Modal as unknown as Component, {
        ...a,
        open: mv,
        destroyOnClose: true,
        footer: slots.footer ? undefined : null,
        'onUpdate:open': (v: unknown) => onUpd?.(v)
      }, { default: slots.default, footer: slots.footer, title: slots.title })
    }
  }
})

// el-drawer -> a-drawer（v-model open；direction->placement；size->width/height；before-close->onClose）
const AntDrawer: Component = defineComponent({
  name: 'WfAntDrawer',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    const placementMap: Record<string, string> = { rtl: 'right', ltr: 'left', ttb: 'top', btt: 'bottom' }
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const mv = a.modelValue
      const onUpd = a['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
      const dir = a.direction as string | undefined
      const size = a.size
      const beforeClose = a['before-close'] as ((done?: () => void) => void) | undefined
      ;['modelValue', 'onUpdate:modelValue', 'direction', 'size', 'append-to-body', 'before-close', 'destroy-on-close'].forEach((k) => delete a[k])
      const placement = (dir && placementMap[dir]) || 'right'
      const sizeKey = placement === 'top' || placement === 'bottom' ? 'height' : 'width'
      return h(ADrawer, {
        ...a,
        open: mv,
        placement,
        [sizeKey]: size,
        destroyOnClose: true,
        'onUpdate:open': (v: unknown) => onUpd?.(v),
        onClose: () => { beforeClose ? beforeClose() : onUpd?.(false) }
      }, slots)
    }
  }
})

const AntDatePicker = vModelComp('WfAntDatePicker', ADatePicker, 'value')
const AntTimePicker = vModelComp('WfAntTimePicker', ATimePicker, 'value')

// el-tree -> a-tree（data->treeData；props->fieldNames；node-key->无需）
const AntTree = vModelComp('WfAntTree', ATree, 'checkedKeys', (props, raw) => {
  if (raw.data != null) { props.treeData = raw.data; delete props.data }
  if (raw.props != null) { props.fieldNames = raw.props; delete props.props }
  delete props['node-key']
  return props
})

// el-pagination -> a-pagination（current-page->current；page-size->pageSize）
const AntPagination: Component = defineComponent({
  name: 'WfAntPagination',
  inheritAttrs: false,
  setup(_props, { attrs }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const current = a['current-page'] ?? a.currentPage ?? a.modelValue
      const onCur = (a['onUpdate:current-page'] ?? a['onUpdate:currentPage']) as ((v: unknown) => void) | undefined
      const pageSize = a['page-size'] ?? a.pageSize
      ;['current-page', 'currentPage', 'modelValue', 'onUpdate:current-page', 'onUpdate:currentPage', 'page-size', 'pageSize', 'layout'].forEach((k) => delete a[k])
      return h(APagination, {
        ...a,
        current,
        pageSize,
        'onUpdate:current': (v: unknown) => onCur?.(v)
      })
    }
  }
})

// el-header -> 普通 header 元素（保留定位/样式，避免 antd Layout.Header 默认样式干扰）
const AntHeader: Component = defineComponent({
  name: 'WfAntHeader',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => h('header', { ...attrs }, slots.default ? slots.default() : [])
  }
})

// el-icon -> 普通 span 容器（antd 图标为组件，无 el-icon 等价；设计器图标多走全局 svg-icon）
const AntIcon: Component = defineComponent({
  name: 'WfAntIcon',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => h('span', { ...attrs, class: ['wf-icon', attrs.class] }, slots.default ? slots.default() : [])
  }
})

export const antdvAdapter: UiAdapter = {
  name: 'ant-design-vue',

  message(type: UiFeedbackType, content) {
    message[type](toContent(content))
  },

  notify(type: UiFeedbackType, content) {
    const o = typeof content === 'string' ? { message: content } : content
    notification[type]({ message: o.title ?? '提示', description: o.message })
  },

  alert(content, options = {}) {
    return new Promise((resolve) => {
      Modal.info({ title: options.title ?? '系统提示', content, onOk: () => resolve(true) })
    })
  },

  confirm(content, options = {}) {
    return new Promise((resolve, reject) => {
      Modal.confirm({
        title: options.title ?? '系统提示',
        content,
        okText: '确定',
        cancelText: '取消',
        onOk: () => resolve(true),
        onCancel: () => reject(new Error('cancel'))
      })
    })
  },

  // antd 无内置 prompt：用 Modal.confirm + a-input 实现
  prompt(content, options = {}) {
    return new Promise((resolve, reject) => {
      let value = ''
      Modal.confirm({
        title: options.title ?? '系统提示',
        content: h('div', [
          typeof content === 'string' && content ? h('div', { style: 'margin-bottom:8px' }, content) : null,
          h(AInput, { 'onUpdate:value': (v: string) => { value = v } })
        ]),
        okText: '确定',
        cancelText: '取消',
        onOk: () => resolve({ value }),
        onCancel: () => reject(new Error('cancel'))
      })
    })
  },

  // 全屏遮罩 loading（antd 无命令式 Spin service，自建覆盖层）
  loading(options = {}): UiLoadingHandle {
    const el = document.createElement('div')
    el.style.cssText = 'position:fixed;inset:0;z-index:9999;display:flex;align-items:center;justify-content:center;background:rgba(0,0,0,0.45)'
    document.body.appendChild(el)
    const app = createApp({
      render: () => h('div', { style: 'text-align:center' }, [
        h(ASpin, { size: 'large' }),
        h('div', { style: 'margin-top:12px;color:#fff' }, options.text ?? '加载中...')
      ])
    })
    app.mount(el)
    return { close: () => { app.unmount(); el.remove() } }
  },

  clickOutside,

  components: {
    // 已翻译为 antd
    button: AntButton,
    input: AntInput,
    switch: AntSwitch,
    radio: AntRadio,
    'radio-group': AntRadioGroup,
    select: AntSelect,
    option: AntOption,
    'input-number': AntInputNumber,
    checkbox: AntCheckbox,
    'checkbox-group': AntCheckboxGroup,
    'tree-select': AntTreeSelect,
    tag: AntTag,
    tooltip: AntTooltip,
    col: ACol,
    row: ARow,
    divider: ADivider,
    form: AntForm,
    'form-item': AntFormItem,
    table: AntTable,
    'table-column': AntTableColumn,
    header: AntHeader,
    tree: AntTree,
    dialog: AntDialog,
    drawer: AntDrawer,
    'date-picker': AntDatePicker,
    'time-picker': AntTimePicker,
    pagination: AntPagination,
    icon: AntIcon
  }
}

export default antdvAdapter
