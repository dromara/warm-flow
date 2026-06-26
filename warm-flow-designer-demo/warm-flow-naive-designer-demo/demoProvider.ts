import { createMockProvider } from '@dromara/warm-flow-designer'

/**
 * Playground 专用「带持久化的 demo 数据源」（与 ep / antd demo 同款，UI 库无关）。
 *
 * 在库内置内存 mock（createMockProvider）基础上，仅覆盖 saveJson / queryDef 两个方法，
 * 用浏览器 localStorage 持久化「已保存的流程定义」，演示「新建 → 保存 → 列表 → 修改 → 预览 → 导出」闭环。
 *
 * @author warm
 */

const STORAGE_KEY = 'wf-designer-demo:flows'

export interface DemoFlowRecord {
  id: string
  flowName: string
  modelValue: string
  updateTime: string
  def: any
}

function readStore(): Record<string, DemoFlowRecord> {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : {}
  } catch (e) {
    return {}
  }
}

function writeStore(store: Record<string, DemoFlowRecord>): void {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(store))
}

function nowText(): string {
  const d = new Date()
  const p = (n: number) => (n < 10 ? '0' + n : '' + n)
  return (
    d.getFullYear() +
    '-' + p(d.getMonth() + 1) +
    '-' + p(d.getDate()) +
    ' ' + p(d.getHours()) +
    ':' + p(d.getMinutes()) +
    ':' + p(d.getSeconds())
  )
}

function ok(data?: any, msg?: string): Promise<any> {
  return Promise.resolve({ code: 200, msg: msg || 'ok(demo)', data: data })
}

/** 列出已保存流程（按更新时间倒序），供 demo 列表渲染。 */
export function listFlows(): DemoFlowRecord[] {
  const store = readStore()
  return Object.keys(store)
    .map((k) => store[k])
    .sort((a, b) => (a.updateTime < b.updateTime ? 1 : -1))
}

/** 取某个流程的 warm-flow 定义 JSON 字符串（格式化，供导出 / 查看）。 */
export function getFlowJsonString(id: string): string {
  const store = readStore()
  const rec = store[id]
  return rec ? JSON.stringify(rec.def, null, 2) : ''
}

/** 删除某个流程。 */
export function removeFlow(id: string): void {
  const store = readStore()
  delete store[id]
  writeStore(store)
}

/** 清空全部 demo 流程。 */
export function clearFlows(): void {
  writeStore({})
}

/**
 * 创建带 localStorage 持久化的 demo 数据源（仅覆盖 saveJson / queryDef，其余继承库内置 mock）。
 */
export function createDemoProvider(): any {
  const base = createMockProvider()
  return {
    ...base,
    saveJson(data: any) {
      let def: any
      try {
        def = typeof data === 'string' ? JSON.parse(data) : (data || {})
      } catch (e) {
        def = {}
      }
      if (def && Array.isArray(def.nodeList)) {
        def.nodeList.forEach((n: any) => {
          if (n.nodeRatio == null || n.nodeRatio === '') {
            n.nodeRatio = '0'
          }
        })
      }
      const store = readStore()
      const id = def.id || 'demo-' + Date.now()
      def.id = id
      store[id] = {
        id: id,
        flowName: def.flowName || '未命名流程',
        modelValue: def.modelValue || '',
        updateTime: nowText(),
        def: def
      }
      writeStore(store)
      return ok(id, '保存成功(demo·已持久化到 localStorage)')
    },
    async queryDef(id?: string | number) {
      const metaRes = await base.queryDef(id)
      const meta = (metaRes && metaRes.data) || {}
      if (!id) {
        return ok(meta)
      }
      const store = readStore()
      const rec = store[String(id)]
      return rec ? ok({ ...meta, ...rec.def }) : ok(meta)
    }
  }
}
