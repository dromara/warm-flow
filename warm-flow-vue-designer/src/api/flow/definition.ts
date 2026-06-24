import { getDataProvider } from '@/data/provider'

// 说明：以下函数保持原有导出名与签名不变，内部统一委托给「当前数据源」(dataProvider)。
// 默认实现为内置 axios（见 src/data/httpProvider.ts），故所有调用方无需改动；
// 业务方可通过 setDataProvider 注入自定义后端 / mock，实现数据层与具体后端解耦。

// 保存json流程定义
export function saveJson(data: any, onlyNodeSkip?: boolean): Promise<any> {
  return getDataProvider().saveJson(data, onlyNodeSkip)
}

// 获取流程定义
export function queryDef(id?: string | number): Promise<any> {
  return getDataProvider().queryDef(id)
}

// 获取流程图
export function queryFlowChart(id: string | number): Promise<any> {
  return getDataProvider().queryFlowChart(id)
}

// 办理人权限设置列表tabs页签
export function handlerType(): Promise<any> {
  return getDataProvider().handlerType()
}

// 办理人权限设置列表结果
export function handlerResult(query?: Record<string, any>): Promise<any> {
  return getDataProvider().handlerResult(query)
}

// 办理人权限名称回显
export function handlerFeedback(query?: Record<string, any>): Promise<any> {
  return getDataProvider().handlerFeedback(query)
}

// 办理人选择项
export function handlerDict(): Promise<any> {
  return getDataProvider().handlerDict()
}

// 查询已发布表单定义列表
export function publishedList(): Promise<any> {
  return getDataProvider().publishedList()
}

// 节点扩展属性
export function nodeExt(): Promise<any> {
  return getDataProvider().nodeExt()
}

// 获取监听器列表
export function listenerList(): Promise<any> {
  return getDataProvider().listenerList()
}
