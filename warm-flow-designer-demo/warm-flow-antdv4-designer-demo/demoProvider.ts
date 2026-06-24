import { createMockProvider } from '@dromara/warm-flow-designer'

/**
 * Playground 专用「带持久化的 demo 数据源」。
 *
 * 在库内置内存 mock（createMockProvider，提供办理人 / 监听器 / 表单 / 配置等脱后端数据）基础上，
 * 仅覆盖 saveJson / queryDef 两个方法，用浏览器 localStorage 持久化「已保存的流程定义」，
 * 从而让 demo 能完整演示「新建 → 保存 → 列表 → 再次打开修改 → 只读预览 → 导出」闭环。
 *
 * 注意：这是 playground（demo 宿主）自己的数据源实现，演示「业务方如何用 setDataProvider 注入后端」，
 * 不属于发布库；库内置的 mockProvider 保持纯内存语义不变，npm 产物不受影响。
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
    // FlowDesigner 传入的是 logicFlowJsonToWarmFlow 的结果（warm-flow 定义 JSON 字符串）
    saveJson(data: any) {
      let def: any
      try {
        def = typeof data === 'string' ? JSON.parse(data) : (data || {})
      } catch (e) {
        def = {}
      }
      // 模拟后端「保存时规范化」：补全前端画布可能缺省的字段，
      // 避免再次 queryDef 还原时 json2LogicFlowJson 读取 undefined.toString() 崩溃（如 nodeRatio）。
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
      // 返回新 id 作为 data，宿主据此实现「保存→修改」闭环
      return ok(id, '保存成功(demo·已持久化到 localStorage)')
    },
    // 修改 / 预览态：返回保存的定义对象（含 nodeList），可被 json2LogicFlowJson 还原画布；
    // 新建态（无 id）或未找到：仅返回元数据，设计器走本地 initData。
    // 元数据（流程类别 / 表单标识等下拉项）取自库内置 mock（base.queryDef），与持久化定义合并，
    // 使「流程类别 / 表单唯一标识」等下拉在 demo 里也有可选数据。
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
