import {
  ElMessage, ElMessageBox, ElNotification, ElLoading, ClickOutside,
  ElButton, ElInput, ElInputNumber, ElSelect, ElOption, ElForm, ElFormItem,
  ElTable, ElTableColumn, ElCol, ElRow, ElHeader, ElCheckbox, ElCheckboxGroup,
  ElTreeSelect, ElTree, ElTag, ElRadio, ElRadioGroup, ElDialog, ElDrawer,
  ElDatePicker, ElTimePicker, ElSwitch, ElPagination, ElDivider, ElTooltip, ElIcon
} from 'element-plus'
import type { UiAdapter, UiFeedbackType, UiFeedbackOptions, UiLoadingHandle } from './uiAdapter'

const DEFAULT_TITLE = '系统提示'

function toOptions(content: string | UiFeedbackOptions): UiFeedbackOptions {
  return typeof content === 'string' ? { message: content } : { ...content }
}

/**
 * Element Plus 实现的 UI 适配器（设计器默认 UI 库）。
 *
 * 把统一的 uiAdapter 契约映射到 EP 的 ElMessage / ElNotification / ElMessageBox / ElLoading
 * 与 ClickOutside 指令；行为对齐迁移前 plugins/modal.js 与 utils/request.js。
 *
 * @author warm
 */
export const elementPlusAdapter: UiAdapter = {
  name: 'element-plus',

  message(type: UiFeedbackType, content) {
    ElMessage({ ...toOptions(content), type })
  },

  notify(type: UiFeedbackType, content) {
    ElNotification({ ...toOptions(content), type })
  },

  alert(content, options = {}) {
    const { title = DEFAULT_TITLE, ...rest } = options
    return ElMessageBox.alert(content, title, rest)
  },

  confirm(content, options = {}) {
    const { title = DEFAULT_TITLE, ...rest } = options
    return ElMessageBox.confirm(content, title, {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      ...rest
    })
  },

  prompt(content, options = {}) {
    const { title = DEFAULT_TITLE, ...rest } = options
    return ElMessageBox.prompt(content, title, {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      ...rest
    }) as Promise<{ value: string }>
  },

  loading(options = {}): UiLoadingHandle {
    const instance = ElLoading.service({ lock: true, ...options })
    return { close: () => instance.close() }
  },

  clickOutside: ClickOutside,

  // v-loading 指令直接复用 EP 内置实现（区域遮罩），与 antd 自实现遮罩对齐
  loadingDirective: ElLoading.directive,

  // 中性组件语义名 -> Element Plus 实现（供 Wf* 透传渲染）
  components: {
    'button': ElButton,
    'input': ElInput,
    'input-number': ElInputNumber,
    'select': ElSelect,
    'option': ElOption,
    'form': ElForm,
    'form-item': ElFormItem,
    'table': ElTable,
    'table-column': ElTableColumn,
    'col': ElCol,
    'row': ElRow,
    'header': ElHeader,
    'checkbox': ElCheckbox,
    'checkbox-group': ElCheckboxGroup,
    'tree-select': ElTreeSelect,
    'tree': ElTree,
    'tag': ElTag,
    'radio': ElRadio,
    'radio-group': ElRadioGroup,
    'dialog': ElDialog,
    'drawer': ElDrawer,
    'date-picker': ElDatePicker,
    'time-picker': ElTimePicker,
    'switch': ElSwitch,
    'pagination': ElPagination,
    'divider': ElDivider,
    'tooltip': ElTooltip,
    'icon': ElIcon
  }
}

export default elementPlusAdapter
