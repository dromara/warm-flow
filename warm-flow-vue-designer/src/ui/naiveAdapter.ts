import { defineComponent, h, ref, Fragment, createApp, watch } from 'vue'
import type { Component, Directive, VNode } from 'vue'
import {
  createDiscreteApi,
  NButton, NInput, NInputNumber, NSelect, NSwitch,
  NRadio, NRadioGroup, NCheckbox, NCheckboxGroup,
  NTag, NTooltip, NDivider, NTreeSelect, NTree,
  NForm, NFormItem, NDataTable, NModal, NDrawer, NDrawerContent,
  NDatePicker, NTimePicker, NPagination, NSpin
} from 'naive-ui'
import type { UiAdapter, UiFeedbackType, UiFeedbackOptions, UiLoadingHandle } from './uiAdapter'

/**
 * Naive UI 的 UI 适配器（库子入口 @dromara/warm-flow-designer/naive，与 element-plus / antdv 子入口对称）。
 *
 * 关键差异（相对 EP / antd）：
 * - Naive 的 message / dialog / notification 是「上下文（provider）」式 API（useMessage 等需在 provider 内 setup 调用），
 *   而本适配器的命令式方法要在任意位置可调用。故用 `createDiscreteApi` 取「脱上下文」实例（自带 provider，挂在 body），
 *   懒初始化（首次调用才挂载，避免 import 即产生 DOM）。
 * - 组件：把设计器既有的 el-* 风格 props / v-model / 插槽翻译为 naive n-* 的 API（值键多为 value）。
 * - clickOutside / 区域 loading：naive 无内置等价，自实现（DOM 监听 / NSpin 遮罩），与 antd 适配器同思路。
 *
 * 已知局限：propertySetting 等抽屉 / 弹窗的精细样式是按 `.el-*` 类名写的 scoped 全局样式，naive DOM 结构不同、
 * 这些选择器不命中（与 antd 下同属「样式 token 双主题对齐」范畴），属功能可用、观感best-effort。
 *
 * @author warm
 */

function toContent(content: string | UiFeedbackOptions): string {
  if (typeof content === 'string') return content
  return (content.message ?? content.title ?? '') as string
}

// 懒初始化脱上下文反馈 API：首次使用时才 createDiscreteApi（避免模块 import 即挂载 provider DOM）
type DiscreteApi = ReturnType<typeof createDiscreteApi>
let _discrete: DiscreteApi | null = null
function discrete(): DiscreteApi {
  if (!_discrete) {
    _discrete = createDiscreteApi(['message', 'notification', 'dialog'])
  }
  return _discrete
}

// 轻量 clickOutside 指令（naive 无内置，自实现）
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

// 区域 loading 遮罩指令（naive 无内置 v-loading，自实现：在宿主元素上覆盖 NSpin 遮罩）
function applyLoading(el: any, value: unknown): void {
  if (value) {
    if (el.__wfLoadingApp__) return
    if (getComputedStyle(el).position === 'static') {
      el.__wfLoadingPrevPosition__ = el.style.position
      el.style.position = 'relative'
    }
    const mask = document.createElement('div')
    mask.style.cssText = 'position:absolute;inset:0;z-index:10;display:flex;align-items:center;justify-content:center;background:rgba(255,255,255,0.6)'
    el.appendChild(mask)
    const app = createApp({ render: () => h(NSpin, { size: 'large' }) })
    app.mount(mask)
    el.__wfLoadingApp__ = app
    el.__wfLoadingEl__ = mask
  } else {
    removeLoading(el)
  }
}
function removeLoading(el: any): void {
  if (el.__wfLoadingApp__) {
    el.__wfLoadingApp__.unmount()
    el.__wfLoadingApp__ = null
  }
  if (el.__wfLoadingEl__) {
    el.__wfLoadingEl__.remove()
    el.__wfLoadingEl__ = null
  }
  if (el.__wfLoadingPrevPosition__ !== undefined) {
    el.style.position = el.__wfLoadingPrevPosition__
    el.__wfLoadingPrevPosition__ = undefined
  }
}
const loadingDirective: Directive = {
  mounted(el: any, binding) { applyLoading(el, binding.value) },
  updated(el: any, binding) {
    if (binding.value === binding.oldValue) return
    applyLoading(el, binding.value)
  },
  unmounted(el: any) { removeLoading(el) }
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

// 为每个行对象分配稳定 key
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

// EP 日期/时间 value-format（YYYY-MM-DD HH:mm:ss）-> naive(date-fns) token（yyyy-MM-dd HH:mm:ss）
function toNaiveDateFormat(fmt: string | undefined, fallback: string): string {
  if (!fmt) return fallback
  return String(fmt).replace(/Y/g, 'y').replace(/D/g, 'd')
}

// 通用 v-model 翻译工厂：modelValue <-> valueKey（naive 多为 value），其余 attrs 透传、插槽转发
function vModelComp(
  name: string,
  naiveComp: Component,
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
        return h(naiveComp, props, slots)
      }
    }
  })
}

// el-button -> n-button（type/size/link/plain 映射）
const NaiveButton: Component = defineComponent({
  name: 'WfNaiveButton',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    const typeMap: Record<string, string> = { primary: 'primary', success: 'success', warning: 'warning', danger: 'error', info: 'info' }
    const sizeMap: Record<string, string> = { large: 'large', default: 'medium', small: 'small' }
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const elType = a.type as string | undefined
      const link = a.link
      const plain = a.plain
      const elSize = a.size as string | undefined
      ;['type', 'link', 'plain', 'size'].forEach((k) => delete a[k])
      const props: Record<string, unknown> = { ...a }
      if (elType === 'text' || link) props.text = true
      else if (elType && typeMap[elType]) props.type = typeMap[elType]
      else props.type = 'default'
      if (plain) props.ghost = true
      if (elSize && sizeMap[elSize]) props.size = sizeMap[elSize]
      return h(NButton, props, slots)
    }
  }
})

// el-input -> n-input（v-model:value；textarea；clearable）
const NaiveInput: Component = defineComponent({
  name: 'WfNaiveInput',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const modelValue = a.modelValue
      const onUpd = a['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
      const elType = a.type as string | undefined
      const rows = a.rows
      ;['modelValue', 'onUpdate:modelValue', 'type', 'rows'].forEach((k) => delete a[k])
      if (a.maxlength != null) a.maxlength = Number(a.maxlength)
      const props: Record<string, unknown> = {
        ...a,
        value: modelValue ?? null,
        'onUpdate:value': (v: unknown) => onUpd?.(v)
      }
      if (elType === 'textarea') {
        props.type = 'textarea'
        if (rows != null) props.rows = Number(rows)
      } else if (elType) {
        props.type = elType
      }
      return h(NInput, props, slots)
    }
  }
})

const NaiveInputNumber = vModelComp('WfNaiveInputNumber', NInputNumber, 'value', (props) => {
  if (props.value === '' || props.value === undefined) props.value = null
  props.style = { width: '100%', ...((props.style as Record<string, unknown>) || {}) }
  return props
})

// el-select -> n-select（options 化；v-model:value；clearable/filterable）
const NaiveSelect: Component = defineComponent({
  name: 'WfNaiveSelect',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const modelValue = a.modelValue
      const onUpd = a['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
      const clearable = a.clearable
      const filterable = a.filterable
      const multiple = a.multiple
      ;['modelValue', 'onUpdate:modelValue', 'clearable', 'filterable', 'multiple'].forEach((k) => delete a[k])
      const optVNodes = collectByName(slots.default ? slots.default() : [], 'WfNaiveOption')
      const options = optVNodes.map((v) => {
        const p = (v.props || {}) as Record<string, unknown>
        return { label: p.label, value: p.value }
      })
      return h(NSelect, {
        ...a,
        value: modelValue ?? null,
        clearable: !!clearable,
        filterable: !!filterable,
        multiple: !!multiple,
        options,
        'onUpdate:value': (v: unknown) => onUpd?.(v)
      })
    }
  }
})

// el-option 占位：由 NaiveSelect 收集 label/value
const NaiveOption: Component = defineComponent({
  name: 'WfNaiveOption',
  setup() { return () => null }
})

// el-switch -> n-switch（含 el 的非法值归一，见 antd 适配器同款修复）
const NaiveSwitch: Component = defineComponent({
  name: 'WfNaiveSwitch',
  inheritAttrs: false,
  setup(_props, { attrs }) {
    const at = attrs as Record<string, unknown>
    watch(
      () => at.modelValue,
      (mv) => {
        const onUpd = at['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
        if (!onUpd) return
        const rawChecked = at['active-value'] ?? at.activeValue
        const rawUnchecked = at['inactive-value'] ?? at.inactiveValue
        const checkedValue = rawChecked !== undefined ? rawChecked : true
        const unCheckedValue = rawUnchecked !== undefined ? rawUnchecked : false
        if (mv !== checkedValue && mv !== unCheckedValue) onUpd(unCheckedValue)
      },
      { immediate: true }
    )
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const modelValue = a.modelValue
      const onUpd = a['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
      const checkedValue = a['active-value'] ?? a.activeValue
      const unCheckedValue = a['inactive-value'] ?? a.inactiveValue
      const activeText = a['active-text'] ?? a.activeText
      const inactiveText = a['inactive-text'] ?? a.inactiveText
      ;['modelValue', 'onUpdate:modelValue', 'active-value', 'activeValue', 'inactive-value',
        'inactiveValue', 'active-text', 'activeText', 'inactive-text', 'inactiveText'].forEach((k) => delete a[k])
      const slots: Record<string, () => unknown> = {}
      if (activeText) slots.checked = () => activeText
      if (inactiveText) slots.unchecked = () => inactiveText
      return h(NSwitch, {
        ...a,
        value: modelValue,
        checkedValue: checkedValue !== undefined ? checkedValue : true,
        uncheckedValue: unCheckedValue !== undefined ? unCheckedValue : false,
        'onUpdate:value': (v: unknown) => onUpd?.(v)
      }, slots)
    }
  }
})

// el-radio-group -> n-radio-group（v-model:value）
const NaiveRadioGroup = vModelComp('WfNaiveRadioGroup', NRadioGroup, 'value')
// el-radio -> n-radio（el 的 label 即值 -> naive 的 value）
const NaiveRadio: Component = defineComponent({
  name: 'WfNaiveRadio',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const value = a.label ?? a.value
      delete a.label
      return h(NRadio, { ...a, value }, slots)
    }
  }
})

const NaiveCheckboxGroup = vModelComp('WfNaiveCheckboxGroup', NCheckboxGroup, 'value')
// el-checkbox -> n-checkbox（group 内 value 取 el 的 label；单用 v-model:checked）
const NaiveCheckbox: Component = defineComponent({
  name: 'WfNaiveCheckbox',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const mv = a.modelValue
      const onUpd = a['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
      const value = a.label ?? a.value
      ;['modelValue', 'onUpdate:modelValue', 'label', 'value'].forEach((k) => delete a[k])
      const props: Record<string, unknown> = { ...a, value }
      // 单独使用（非 group）时透传 checked 双向
      if (mv !== undefined || onUpd) {
        props.checked = mv
        props['onUpdate:checked'] = (v: unknown) => onUpd?.(v)
      }
      return h(NCheckbox, props, slots)
    }
  }
})

const NaiveTreeSelect = vModelComp('WfNaiveTreeSelect', NTreeSelect, 'value', (props, raw) => {
  if (raw.data != null) { props.options = raw.data; delete props.data }
  const ep = (raw.props || {}) as Record<string, unknown>
  props.keyField = (ep.value as string) || (raw['node-key'] as string) || 'id'
  props.labelField = (ep.label as string) || 'label'
  props.childrenField = (ep.children as string) || 'children'
  delete props.props
  delete props['node-key']
  delete props['value-key']
  delete props['check-strictly']
  if (props.value === '' || props.value == null) props.value = null
  props.style = { width: '100%', ...((raw.style as Record<string, unknown>) || {}) }
  return props
})

// el-tag -> n-tag（type -> naive type；effect/round 透传）
const NaiveTag: Component = defineComponent({
  name: 'WfNaiveTag',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    const typeMap: Record<string, string> = { success: 'success', info: 'info', warning: 'warning', danger: 'error', primary: 'primary' }
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const t = a.type as string | undefined
      ;['type', 'effect'].forEach((k) => delete a[k])
      return h(NTag, { ...a, type: t ? (typeMap[t] || 'default') : 'default' }, slots)
    }
  }
})

// el-tooltip -> n-tooltip（EP：content 属性 + 默认插槽=触发器；naive：#trigger=触发器 + 默认插槽=内容）
const NaiveTooltip: Component = defineComponent({
  name: 'WfNaiveTooltip',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const content = a.content
      const showAfter = a['show-after'] ?? a.showAfter
      ;['content', 'show-after', 'showAfter'].forEach((k) => delete a[k])
      if (showAfter != null) a.delay = Number(showAfter)
      return h(NTooltip, a, {
        trigger: () => (slots.default ? slots.default() : []),
        default: () => (content != null ? content : (slots.content ? slots.content() : []))
      })
    }
  }
})

// el-form -> n-form（label-width/label-placement；暴露 el 风格 validate(cb)）
const NaiveForm: Component = defineComponent({
  name: 'WfNaiveForm',
  inheritAttrs: false,
  setup(_props, { attrs, slots, expose }) {
    const inner = ref<any>(null)
    expose({
      validate(cb?: (valid: boolean, fields?: unknown) => void) {
        const inst = inner.value
        const p = inst?.validate ? inst.validate() : null
        if (!p || typeof p.then !== 'function') { cb?.(true); return Promise.resolve(true) }
        return p.then(() => { cb?.(true); return true }).catch((errs: unknown) => {
          cb?.(false, errs)
          if (cb) return false
          return Promise.reject(errs)
        })
      },
      // naive 无逐字段 clearValidate(field)，统一 restoreValidation 复位
      clearValidate() { return inner.value?.restoreValidation?.() },
      restoreValidation() { return inner.value?.restoreValidation?.() },
      resetFields() { return inner.value?.restoreValidation?.() }
    })
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const labelWidth = a['label-width'] ?? a.labelWidth
      const inline = a.inline === true || a.inline === '' || a.inline === 'true'
      ;['label-width', 'labelWidth', 'inline'].forEach((k) => delete a[k])
      const props: Record<string, unknown> = {
        ...a,
        ref: inner,
        labelPlacement: 'left',
        labelAlign: 'right',
        inline
      }
      if (labelWidth != null) {
        props.labelWidth = /^\d+$/.test(String(labelWidth)) ? Number(labelWidth) : labelWidth
      }
      return h(NForm, props, slots)
    }
  }
})

// el-form-item -> n-form-item（prop -> path；rules -> rule）
const NaiveFormItem: Component = defineComponent({
  name: 'WfNaiveFormItem',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const prop = a.prop
      const rules = a.rules
      ;['prop', 'rules'].forEach((k) => delete a[k])
      const props: Record<string, unknown> = { ...a }
      if (prop != null) props.path = prop
      if (rules != null) props.rule = rules
      return h(NFormItem, props, slots)
    }
  }
})

// el-table（列子组件）-> n-data-table（columns + render 单元格）
const NaiveTable: Component = defineComponent({
  name: 'WfNaiveTable',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const data = a.data
      const onRowClick = a.onRowClick as ((row: unknown) => void) | undefined
      ;['data', 'empty-text', 'emptyText', 'onRowClick', 'stripe', 'border'].forEach((k) => delete a[k])
      const colVNodes = collectByName(slots.default ? slots.default() : [], 'WfNaiveTableColumn')
      const columns = colVNodes.map((v, idx) => {
        const p = (v.props || {}) as Record<string, unknown>
        const col: Record<string, unknown> = {
          title: p.label,
          key: (p.prop as string) ?? idx,
          width: p.width,
          align: p.align
        }
        const ch = v.children
        const defSlot = ch && typeof ch === 'object' && !Array.isArray(ch)
          ? (ch as Record<string, unknown>).default
          : null
        if (typeof defSlot === 'function') {
          col.render = (rowData: unknown, rowIndex: number) =>
            (defSlot as (s: unknown) => unknown)({ row: rowData, $index: rowIndex, column: col })
        }
        return col
      })
      return h(NDataTable, {
        ...a,
        data: Array.isArray(data) ? [...data] : [],
        columns,
        rowKey: rowKeyOf,
        size: 'small',
        ...(onRowClick ? { rowProps: (row: unknown) => ({ style: 'cursor:pointer', onClick: () => onRowClick(row) }) } : {})
      })
    }
  }
})
const NaiveTableColumn: Component = defineComponent({
  name: 'WfNaiveTableColumn',
  setup() { return () => null }
})

// el-dialog -> n-modal（preset card；v-model open）
const NaiveDialog: Component = defineComponent({
  name: 'WfNaiveDialog',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const mv = a.modelValue
      const onUpd = a['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
      const title = a.title
      const width = a.width
      ;['modelValue', 'onUpdate:modelValue', 'append-to-body', 'destroy-on-close', 'before-close', 'title', 'width'].forEach((k) => delete a[k])
      return h(NModal, {
        ...a,
        show: mv,
        preset: 'card',
        title,
        style: { width: width ? (/^\d+$/.test(String(width)) ? `${width}px` : String(width)) : '600px' },
        'onUpdate:show': (v: unknown) => onUpd?.(v)
      }, { default: slots.default, footer: slots.footer })
    }
  }
})

// el-drawer -> n-drawer + n-drawer-content（v-model open；direction->placement；size->width/height）
const NaiveDrawer: Component = defineComponent({
  name: 'WfNaiveDrawer',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    const placementMap: Record<string, string> = { rtl: 'right', ltr: 'left', ttb: 'top', btt: 'bottom' }
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const mv = a.modelValue
      const onUpd = a['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
      const dir = a.direction as string | undefined
      const size = a.size
      const title = a.title
      ;['modelValue', 'onUpdate:modelValue', 'direction', 'size', 'append-to-body', 'before-close', 'destroy-on-close', 'title'].forEach((k) => delete a[k])
      const placement = (dir && placementMap[dir]) || 'right'
      const sizeKey = placement === 'top' || placement === 'bottom' ? 'height' : 'width'
      const sizeVal = size != null ? (/^\d+$/.test(String(size)) ? Number(size) : size) : 380
      return h(NDrawer, {
        ...a,
        show: mv,
        placement,
        [sizeKey]: sizeVal,
        'onUpdate:show': (v: unknown) => onUpd?.(v)
      }, {
        default: () => h(NDrawerContent, { title, closable: true }, slots)
      })
    }
  }
})

// el-date-picker -> n-date-picker（type 直传；EP value-format/string 值 -> naive formatted-value + value-format）
const NaiveDatePicker: Component = defineComponent({
  name: 'WfNaiveDatePicker',
  inheritAttrs: false,
  setup(_props, { attrs }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const type = (a.type as string) || 'date'
      const isRange = type.endsWith('range')
      const mv = a.modelValue
      const onUpd = a['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
      const valueFormat = (a['value-format'] ?? a.valueFormat) as string | undefined
      const showTime = type === 'datetime' || type === 'datetimerange'
      ;['modelValue', 'onUpdate:modelValue', 'value-format', 'valueFormat',
        'range-separator', 'start-placeholder', 'end-placeholder'].forEach((k) => delete a[k])
      let value: unknown
      if (isRange) value = Array.isArray(mv) && mv.length === 2 && mv[0] && mv[1] ? mv : null
      else value = mv === '' || mv == null ? null : mv
      return h(NDatePicker, {
        ...a,
        type,
        clearable: true,
        formattedValue: value,
        valueFormat: toNaiveDateFormat(valueFormat, showTime ? 'yyyy-MM-dd HH:mm:ss' : 'yyyy-MM-dd'),
        'onUpdate:formattedValue': (v: unknown) => onUpd?.(v)
      })
    }
  }
})

// el-time-picker -> n-time-picker（formatted-value + value-format）
const NaiveTimePicker: Component = defineComponent({
  name: 'WfNaiveTimePicker',
  inheritAttrs: false,
  setup(_props, { attrs }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const mv = a.modelValue
      const onUpd = a['onUpdate:modelValue'] as ((v: unknown) => void) | undefined
      const valueFormat = (a['value-format'] ?? a.valueFormat) as string | undefined
      ;['modelValue', 'onUpdate:modelValue', 'value-format', 'valueFormat'].forEach((k) => delete a[k])
      return h(NTimePicker, {
        ...a,
        clearable: true,
        formattedValue: mv === '' || mv == null ? null : mv,
        valueFormat: toNaiveDateFormat(valueFormat, 'HH:mm:ss'),
        'onUpdate:formattedValue': (v: unknown) => onUpd?.(v)
      })
    }
  }
})

// el-pagination -> n-pagination（current-page->page；page-size->page-size；total->item-count）
const NaivePagination: Component = defineComponent({
  name: 'WfNaivePagination',
  inheritAttrs: false,
  setup(_props, { attrs }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const current = a['current-page'] ?? a.currentPage ?? a.modelValue
      const onCur = (a['onUpdate:current-page'] ?? a['onUpdate:currentPage']) as ((v: unknown) => void) | undefined
      const pageSize = a['page-size'] ?? a.pageSize
      const total = a.total
      ;['current-page', 'currentPage', 'modelValue', 'onUpdate:current-page', 'onUpdate:currentPage', 'page-size', 'pageSize', 'total', 'layout'].forEach((k) => delete a[k])
      return h(NPagination, {
        ...a,
        page: current,
        pageSize,
        itemCount: total,
        'onUpdate:page': (v: unknown) => onCur?.(v)
      })
    }
  }
})

// el-tree -> n-tree（data/props/node-key 映射；node-click 走 node-props onClick）
const NaiveTreeComp: Component = defineComponent({
  name: 'WfNaiveTree',
  inheritAttrs: false,
  setup(_props, { attrs }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const data = a.data
      const ep = (a.props || {}) as Record<string, unknown>
      const onNodeClick = a.onNodeClick as ((data: unknown) => void) | undefined
      ;['data', 'props', 'node-key', 'nodeKey', 'onNodeClick', 'highlight-current', 'default-expand-all', 'expand-on-click-node'].forEach((k) => delete a[k])
      return h(NTree, {
        ...a,
        data: Array.isArray(data) ? data : [],
        keyField: (ep.value as string) || (attrs['node-key'] as string) || 'id',
        labelField: (ep.label as string) || 'label',
        childrenField: (ep.children as string) || 'children',
        blockLine: true,
        ...(onNodeClick ? {
          nodeProps: (info: { option: Record<string, unknown> }) => ({ onClick: () => onNodeClick(info.option) })
        } : {})
      })
    }
  }
})

// el-col / el-row -> 轻量 flex 实现（避免 naive grid 与 EP 栅格语义差异；span 基于 24 栅格）
const NaiveRow: Component = defineComponent({
  name: 'WfNaiveRow',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const gutter = Number(a.gutter ?? 0)
      delete a.gutter
      const style = { display: 'flex', flexWrap: 'wrap', ...(gutter ? { gap: `${gutter}px` } : {}), ...((a.style as Record<string, unknown>) || {}) }
      return h('div', { ...a, style }, slots.default ? slots.default() : [])
    }
  }
})
const NaiveCol: Component = defineComponent({
  name: 'WfNaiveCol',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => {
      const a: Record<string, unknown> = { ...attrs }
      const span = Number(a.span ?? 24)
      ;['span', 'xs', 'sm', 'md', 'lg', 'xl'].forEach((k) => delete a[k])
      const pct = `${(Math.min(Math.max(span, 0), 24) / 24) * 100}%`
      const style = { flex: `0 0 calc(${pct} - 0px)`, maxWidth: pct, boxSizing: 'border-box', ...((a.style as Record<string, unknown>) || {}) }
      return h('div', { ...a, style }, slots.default ? slots.default() : [])
    }
  }
})

// el-header -> 普通 header 元素
const NaiveHeader: Component = defineComponent({
  name: 'WfNaiveHeader',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => h('header', { ...attrs }, slots.default ? slots.default() : [])
  }
})

// el-icon -> 普通 span 容器（设计器图标多走全局 svg-icon）
const NaiveIcon: Component = defineComponent({
  name: 'WfNaiveIcon',
  inheritAttrs: false,
  setup(_props, { attrs, slots }) {
    return () => h('span', { ...attrs, class: ['wf-icon', attrs.class] }, slots.default ? slots.default() : [])
  }
})

export const naiveAdapter: UiAdapter = {
  name: 'naive-ui',

  message(type: UiFeedbackType, content) {
    discrete().message[type](toContent(content))
  },

  notify(type: UiFeedbackType, content) {
    const o = typeof content === 'string' ? { message: content } : content
    discrete().notification[type]({ title: o.title ?? '提示', content: o.message })
  },

  alert(content, options = {}) {
    return new Promise((resolve) => {
      discrete().dialog.info({
        title: options.title ?? '系统提示',
        content,
        positiveText: '确定',
        onPositiveClick: () => resolve(true),
        onClose: () => resolve(true)
      })
    })
  },

  confirm(content, options = {}) {
    return new Promise((resolve, reject) => {
      discrete().dialog.warning({
        title: options.title ?? '系统提示',
        content,
        positiveText: '确定',
        negativeText: '取消',
        onPositiveClick: () => resolve(true),
        onNegativeClick: () => reject(new Error('cancel')),
        onClose: () => reject(new Error('cancel'))
      })
    })
  },

  // naive 无内置 prompt：用 dialog + n-input 实现
  prompt(content, options = {}) {
    return new Promise((resolve, reject) => {
      let value = ''
      discrete().dialog.info({
        title: options.title ?? '系统提示',
        content: () => h('div', [
          typeof content === 'string' && content ? h('div', { style: 'margin-bottom:8px' }, content) : null,
          h(NInput, { 'onUpdate:value': (v: string) => { value = v } })
        ]),
        positiveText: '确定',
        negativeText: '取消',
        onPositiveClick: () => resolve({ value }),
        onNegativeClick: () => reject(new Error('cancel')),
        onClose: () => reject(new Error('cancel'))
      })
    })
  },

  // 全屏遮罩 loading（naive 无命令式 Spin service，自建覆盖层）
  loading(options = {}): UiLoadingHandle {
    const el = document.createElement('div')
    el.style.cssText = 'position:fixed;inset:0;z-index:9999;display:flex;align-items:center;justify-content:center;background:rgba(0,0,0,0.45)'
    document.body.appendChild(el)
    const app = createApp({
      render: () => h('div', { style: 'text-align:center' }, [
        h(NSpin, { size: 'large' }),
        h('div', { style: 'margin-top:12px;color:#fff' }, options.text ?? '加载中...')
      ])
    })
    app.mount(el)
    return { close: () => { app.unmount(); el.remove() } }
  },

  clickOutside,

  loadingDirective,

  components: {
    button: NaiveButton,
    input: NaiveInput,
    'input-number': NaiveInputNumber,
    select: NaiveSelect,
    option: NaiveOption,
    switch: NaiveSwitch,
    radio: NaiveRadio,
    'radio-group': NaiveRadioGroup,
    checkbox: NaiveCheckbox,
    'checkbox-group': NaiveCheckboxGroup,
    'tree-select': NaiveTreeSelect,
    tree: NaiveTreeComp,
    tag: NaiveTag,
    tooltip: NaiveTooltip,
    divider: NDivider,
    col: NaiveCol,
    row: NaiveRow,
    form: NaiveForm,
    'form-item': NaiveFormItem,
    table: NaiveTable,
    'table-column': NaiveTableColumn,
    header: NaiveHeader,
    dialog: NaiveDialog,
    drawer: NaiveDrawer,
    'date-picker': NaiveDatePicker,
    'time-picker': NaiveTimePicker,
    pagination: NaivePagination,
    icon: NaiveIcon
  }
}

export default naiveAdapter
