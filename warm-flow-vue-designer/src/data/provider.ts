import { createHttpProvider } from './httpProvider'

/**
 * 设计器与后端交互的数据源契约（DataProvider）。
 *
 * 流程设计器内部所有后端调用都经由「当前 dataProvider」，默认实现为内置 axios（createHttpProvider），
 * 与原有 iframe / webjars 行为完全一致；通过 setDataProvider 注入自定义实现（业务方后端、mock 等），
 * 即可把数据层与具体后端解耦。这是「业务方注入数据源」与组件库 / npm 包形态的统一入口。
 *
 * 返回值统一为 Promise（对齐后端 {code, msg, data} 结构）。为保证向后兼容与跨后端适配，
 * 入参 / 返回的业务数据保持宽松类型（any），不强行约束后端响应结构。
 *
 * @author warm
 */
export interface DataProvider {
  // ===== 流程定义 =====
  saveJson(data: any, onlyNodeSkip?: boolean): Promise<any>
  queryDef(id?: string | number): Promise<any>
  queryFlowChart(id: string | number): Promise<any>
  handlerType(): Promise<any>
  handlerResult(query?: Record<string, any>): Promise<any>
  handlerFeedback(query?: Record<string, any>): Promise<any>
  handlerDict(): Promise<any>
  publishedList(): Promise<any>
  nodeExt(): Promise<any>
  listenerList(): Promise<any>

  // ===== 配置 =====
  config(): Promise<any>
}

let currentProvider: DataProvider = createHttpProvider()

/**
 * 注入自定义数据源。
 *
 * 传入对象会与默认 http 实现「合并」，因此业务方只需覆盖关心的方法，其余自动回退到内置 axios；
 * 传 null / 不传则恢复为纯默认 http 实现。
 *
 * @param provider 自定义数据源（部分或全部方法）
 */
export function setDataProvider(provider?: Partial<DataProvider> | null): void {
  currentProvider = Object.assign(createHttpProvider(), provider || {})
}

/**
 * 获取当前生效的数据源。api/* 内部统一通过它委托调用，调用方无需感知具体实现。
 */
export function getDataProvider(): DataProvider {
  return currentProvider
}
