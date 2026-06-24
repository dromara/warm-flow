import { getDataProvider } from '@/data/provider'

// 说明：保持原有导出名与签名不变，内部委托给「当前数据源」(dataProvider)。
// 默认实现为内置 axios（见 src/data/httpProvider.ts），调用方无需改动。

// 查询表单定义内容
export function getFormContent(id: string | number): Promise<any> {
  return getDataProvider().getFormContent(id)
}

// 保存表单设计
export function saveFormContent(data: any): Promise<any> {
  return getDataProvider().saveFormContent(data)
}

// 获取表单设计详情及数据
export function executeLoad(id: string | number): Promise<any> {
  return getDataProvider().executeLoad(id)
}

// 办理OA 申请
export function executeHandle(data: any, taskId: string | number, skipType: string, message?: string): Promise<any> {
  return getDataProvider().executeHandle(data, taskId, skipType, message)
}

// 查看已办表单详情
export function hisLoad(taskId: string | number): Promise<any> {
  return getDataProvider().hisLoad(taskId)
}
