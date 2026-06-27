import { ref } from 'vue'
import type { Ref } from 'vue'

/**
 * Warm-Flow 设计器轻量 i18n（零依赖，与 setUiAdapter/setDataProvider 等 set* 插件风格一致）。
 *
 * 设计目标：
 * - **向后兼容**：默认语言 zh，不调用任何 i18n API 时行为与历史一致（仍显示中文）。
 * - **零依赖**：内置响应式 locale + 点分 key catalog + `{name}` 占位插值，不引 vue-i18n。
 * - **可扩展**：业务方可经 setMessages 覆盖 / 扩展任意语言包。
 *
 * 用法：
 *   import { setLocale, useI18n } from '@dromara/warm-flow-designer'
 *   setLocale('en')                       // 切换语言（全局响应式，模板内 t() 自动更新）
 *   const { t, locale } = useI18n()
 *   t('common.save')                      // -> 'Save'
 *   t('common.deleteConfirm', { name })   // 占位插值
 *
 * 说明：组件文案正在分批从硬编码中文迁移到本 catalog；未迁移的文案暂仍为中文，迁移完成后即随 setLocale 生效。
 *
 * @author warm
 */

/** 支持的语言。默认 zh。 */
export type WfLocale = 'zh' | 'en'

/** 文案树：值为字符串或下一层子树（点分 key 访问）。 */
export interface WfMessageTree {
  [key: string]: string | WfMessageTree
}

// 启动 catalog（骨架）：先收敛跨组件通用文案，组件级文案分批迁移补全。
const zh: WfMessageTree = {
  common: {
    save: '保存',
    cancel: '取消',
    confirm: '确定',
    delete: '删除',
    create: '新建',
    edit: '编辑',
    preview: '预览',
    export: '导出',
    reset: '重置',
    search: '查询',
    add: '新增',
    remove: '移除',
    loading: '加载中…',
    empty: '暂无数据',
    tip: '提示',
    success: '操作成功',
    failed: '操作失败',
    required: '必填项'
  }
}

const en: WfMessageTree = {
  common: {
    save: 'Save',
    cancel: 'Cancel',
    confirm: 'OK',
    delete: 'Delete',
    create: 'New',
    edit: 'Edit',
    preview: 'Preview',
    export: 'Export',
    reset: 'Reset',
    search: 'Search',
    add: 'Add',
    remove: 'Remove',
    loading: 'Loading…',
    empty: 'No data',
    tip: 'Tip',
    success: 'Success',
    failed: 'Failed',
    required: 'Required'
  }
}

const messages: Record<WfLocale, WfMessageTree> = { zh, en }

// 全局响应式当前语言：模板 / computed 中调用 translate 会建立依赖，setLocale 后自动更新。
const currentLocale: Ref<WfLocale> = ref('zh')

/** 切换当前语言（未注册的语言忽略）。 */
export function setLocale(locale: WfLocale): void {
  if (messages[locale]) {
    currentLocale.value = locale
  }
}

/** 获取当前语言。 */
export function getLocale(): WfLocale {
  return currentLocale.value
}

function deepMerge(base: WfMessageTree, patch: WfMessageTree): WfMessageTree {
  const out: WfMessageTree = { ...base }
  for (const key of Object.keys(patch)) {
    const patchVal = patch[key]
    const baseVal = out[key]
    out[key] =
      typeof patchVal === 'object' && typeof baseVal === 'object'
        ? deepMerge(baseVal, patchVal)
        : patchVal
  }
  return out
}

/**
 * 覆盖 / 扩展指定语言的文案（深合并）。业务方可补全英文、新增语言或改写默认文案。
 */
export function setMessages(locale: WfLocale, patch: WfMessageTree): void {
  messages[locale] = deepMerge(messages[locale] ?? {}, patch)
}

function resolve(tree: WfMessageTree | undefined, path: string): string | undefined {
  if (!tree) {
    return undefined
  }
  let cursor: string | WfMessageTree | undefined = tree
  for (const seg of path.split('.')) {
    if (cursor && typeof cursor === 'object' && seg in cursor) {
      cursor = (cursor as WfMessageTree)[seg]
    } else {
      return undefined
    }
  }
  return typeof cursor === 'string' ? cursor : undefined
}

/**
 * 翻译：按当前语言解析点分 path，缺失回落中文、再回落 path 原样；支持 `{name}` 占位插值。
 *
 * 读取响应式 currentLocale，因此在模板 / computed 中使用时，setLocale 会触发刷新。
 */
export function translate(path: string, params?: Record<string, unknown>): string {
  const text = resolve(messages[currentLocale.value], path) ?? resolve(messages.zh, path) ?? path
  if (!params) {
    return text
  }
  return text.replace(/\{(\w+)\}/g, (_, key: string) => (params[key] != null ? String(params[key]) : ''))
}

/** useI18n 返回值。 */
export interface UseI18nReturn {
  /** 响应式当前语言 */
  locale: Ref<WfLocale>
  /** 翻译函数（点分 key + 可选占位插值） */
  t: (path: string, params?: Record<string, unknown>) => string
  /** 切换语言 */
  setLocale: (locale: WfLocale) => void
  /** 获取当前语言 */
  getLocale: () => WfLocale
}

/**
 * 组合式 API：返回响应式 locale、翻译函数 t、切换 setLocale。
 */
export function useI18n(): UseI18nReturn {
  return { locale: currentLocale, t: translate, setLocale, getLocale }
}
